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

/**
 * Ensures that classes annotated with `@AggregateRoot` have only private constructors.
 *
 * Domain-driven aggregates should not be constructed directly using public or internal constructors.
 * Instead, their instantiation must go through controlled factory methods (e.g., `create`, `from`) that
 * preserve invariants and encapsulate creation logic.
 *
 * This rule checks both primary and secondary constructors.
 */
public class AggregateConstructorMustBePrivateRule(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.CodeSmell,
        description = """
            Aggregate root classes must declare only private constructors to avoid uncontrolled instantiation.
            Use named factory methods to construct aggregates and enforce invariants.
        """.trimIndent(),
        debt = Debt.FIVE_MINS
    )

    override fun visitClass(klass: KtClass) {
        if (!klass.hasAnnotation("AggregateRoot")) return

        // Check primary constructor
        klass.primaryConstructor?.let { constructor ->
            if (!constructor.hasModifier(KtTokens.PRIVATE_KEYWORD)) {
                report(
                    CodeSmell(
                        issue,
                        Entity.atName(constructor),
                        message = "@AggregateRoot class '${klass.name}' must have a private primary constructor."
                    )
                )
            }
        }

        // Check secondary constructors
        klass.secondaryConstructors.forEach { constructor ->
            if (!constructor.hasModifier(KtTokens.PRIVATE_KEYWORD)) {
                report(
                    CodeSmell(
                        issue,
                        Entity.atName(constructor),
                        message = "@AggregateRoot class '${klass.name}' must have only private constructors."
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
