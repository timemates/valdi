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
 * Ensures that classes annotated with @DomainEntity have only private constructors.
 *
 * Domain Entities are typically created only through Aggregate Roots or factories.
 * Enforcing private constructors ensures that entity instantiation is controlled
 * and domain invariants are preserved.
 */
public class EntityConstructorMustBePrivateRule(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.CodeSmell,
        description = """
            @DomainEntity classes must declare their constructors as private.
            This enforces encapsulation of business rules and ensures entities are only
            created through aggregates or designated factory methods.
        """.trimIndent(),
        debt = Debt.FIVE_MINS,
    )

    override fun visitClass(klass: KtClass) {
        if (!klass.hasAnnotation("DomainEntity")) return

        val constructors = klass.primaryConstructor?.let { listOf(it) }.orEmpty() +
            klass.secondaryConstructors

        constructors.forEach { ctor ->
            val visibility = ctor.visibilityModifierType()
            if (visibility != KtTokens.PRIVATE_KEYWORD) {
                report(
                    CodeSmell(
                        issue,
                        Entity.atName(ctor),
                        message = "@DomainEntity constructor must be private."
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
