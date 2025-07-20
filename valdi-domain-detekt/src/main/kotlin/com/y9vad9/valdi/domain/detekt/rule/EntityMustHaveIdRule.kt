package com.y9vad9.valdi.domain.detekt.rule

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*

/**
 * Ensures that classes annotated with `@DomainEntity` declare an identity.
 *
 * A DomainEntity must have a unique identity to distinguish one instance from another.
 * This rule enforces that by requiring:
 * - A property named `id`, or
 * - A property annotated with `@DomainEntity.Id`
 *
 * Failing to declare an identity leads to incorrect modeling, where equality and lifecycle
 * cannot be properly managed.
 */
public class EntityMustHaveIdRule(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Defect,
        description = """
            DomainEntity classes must declare a unique identity.
            
            Each class annotated with @DomainEntity must have either:
            - A property named `id`, or
            - A property annotated with @DomainEntity.Id
            
            This ensures the entity can be tracked and managed correctly.
        """.trimIndent(),
        debt = Debt.FIVE_MINS,
    )

    override fun visitClass(klass: KtClass) {
        if (!klass.hasAnnotation("DomainEntity")) return

        val properties = klass.getProperties()

        val hasIdByName = properties.any { it.name == "id" }
        val hasIdAnnotation = properties.any { it.hasAnnotation("DomainEntity.Id") }

        if (!hasIdByName && !hasIdAnnotation) {
            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.atName(klass),
                    message = "Class annotated with @DomainEntity must have an `id` property or a property annotated with @DomainEntity.Id"
                )
            )
        }

        super.visitClass(klass)
    }

    private fun KtModifierListOwner.hasAnnotation(name: String): Boolean {
        return annotationEntries.any { it.shortName?.asString() == name }
    }
}