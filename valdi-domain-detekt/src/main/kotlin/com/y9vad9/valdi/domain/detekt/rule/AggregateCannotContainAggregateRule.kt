package com.y9vad9.valdi.domain.detekt.rule

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * A Detekt rule that prevents aggregate roots from containing other aggregate roots as properties.
 *
 * ## Rule Overview
 * In Domain-Driven Design (DDD), an `@AggregateRoot` defines a consistency boundary.
 * Aggregates should not reference other aggregates directly as properties, as this leads to tightly coupled domain structures.
 *
 * ## Checks Performed
 * - Classes annotated with `@AggregateRoot` must not have any property whose type is also annotated with `@AggregateRoot`.
 *
 * ## Why This Matters
 * Nesting aggregates violates aggregate boundaries and may lead to domain model corruption, inconsistent state transitions,
 * and increased coupling between aggregates.
 *
 * ## Example of a violation:
 * ```kotlin
 * @AggregateRoot
 * class Order(val payment: Payment) // if Payment is also annotated with @AggregateRoot → ❌
 * ```
 *
 * ## Example of valid usage:
 * ```kotlin
 * @AggregateRoot
 * class Order(val payment: Payment) // if Payment is a @DomainEntity or @ValueObject → ✅
 * ```
 */
@RequiresTypeResolution
public class AggregateCannotContainAggregateRule(config: Config) : Rule(config) {
    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Defect,
        description = """
            Prevents aggregate roots from directly containing other aggregate roots as properties.
            
            Each aggregate defines a transactional boundary and must be managed independently.
            Nesting aggregates introduces coupling that contradicts Domain-Driven Design principles.
        """.trimIndent(),
        debt = Debt.FIVE_MINS,
    )

    override fun visitClass(klass: KtClass) {
        if (!klass.hasAnnotation("AggregateRoot")) return

        klass.getBody()?.properties?.forEach { property ->
            val typeRef = property.typeReference ?: return@forEach
            val type = bindingContext[BindingContext.TYPE, typeRef] ?: return@forEach
            val classDescriptor = type.constructor.declarationDescriptor as? ClassDescriptor ?: return@forEach

            // Check annotations on the resolved type
            val isAggregate = classDescriptor.annotations.any {
                it.fqName?.asString()?.endsWith("AggregateRoot") == true
            }

            if (isAggregate) {
                report(
                    CodeSmell(
                        issue = issue,
                        entity = Entity.atName(property),
                        message = "@AggregateRoot '${klass.name}' must not contain another @AggregateRoot '${classDescriptor.name}' as a property.",
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
