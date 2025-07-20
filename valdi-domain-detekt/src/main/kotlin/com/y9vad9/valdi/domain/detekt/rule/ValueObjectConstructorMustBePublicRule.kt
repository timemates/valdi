package com.y9vad9.valdi.domain.detekt.rule

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * Ensures that classes annotated with @ValueObject have a public constructor.
 *
 * Value Objects are meant to be freely creatable and immutable.
 * They typically represent simple concepts and shouldn't hide their constructors
 * unless there's a very strong reason (e.g., validation via factory).
 */
public class ValueObjectConstructorMustBePublicRule(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = """
            @ValueObject classes should have public constructors.
            Value Objects should be easy to construct and typically have no hidden state.
            Avoid restricting construction unless validation or invariants demand it.
        """.trimIndent(),
        debt = Debt.FIVE_MINS,
    )

    override fun visitClass(klass: KtClass) {
        if (!klass.hasAnnotation("ValueObject")) return

        val constructors = klass.primaryConstructor?.let { listOf(it) }.orEmpty() +
            klass.secondaryConstructors

        constructors.forEach { ctor ->
            val visibility = ctor.visibilityModifierType()
            if (visibility == KtTokens.PRIVATE_KEYWORD || visibility == KtTokens.INTERNAL_KEYWORD) {
                report(
                    CodeSmell(
                        issue = issue,
                        entity = Entity.atName(ctor),
                        message = "@ValueObject constructor should be public.",
                    )
                )
            }
        }

        super.visitClass(klass)
    }

    private fun KtModifierListOwner.hasAnnotation(name: String): Boolean {
        return annotationEntries.any { it.shortName?.asString() == name }
    }
}
