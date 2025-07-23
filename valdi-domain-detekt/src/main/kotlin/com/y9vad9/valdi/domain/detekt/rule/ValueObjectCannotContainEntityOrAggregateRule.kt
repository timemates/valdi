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
 * A Detekt rule that ensures classes annotated with @ValueObject
 * do not contain properties typed as @DomainEntity or @AggregateRoot.
 *
 * Value objects should be self-contained and free from identity and lifecycle concerns,
 * which are the responsibility of entities and aggregates.
 *
 * This rule requires type resolution.
 */
@RequiresTypeResolution
public class ValueObjectCannotContainEntityOrAggregateRule(config: Config) : Rule(config) {
    override val issue: Issue = Issue(
        id = javaClass.name,
        severity = Severity.CodeSmell,
        description = """
            Value objects must not contain properties that are domain entities or aggregate roots.
            
            Entities and aggregates carry identity and lifecycle,
            which value objects should not depend on.
        """.trimIndent(),
        debt = Debt.TEN_MINS,
    )

    override fun visitClass(klass: KtClass) {
        if (!klass.hasAnnotation("ValueObject")) return

        klass.getBody()?.properties?.forEach { property ->
            val typeRef = property.typeReference ?: return@forEach
            val type = bindingContext[BindingContext.TYPE, typeRef] ?: return@forEach
            val classDescriptor = type.constructor.declarationDescriptor as? ClassDescriptor ?: return@forEach

            val annotationsFqNames = classDescriptor.annotations.mapNotNull { it.fqName?.asString() }

            val isEntity = annotationsFqNames.any { it.endsWith("DomainEntity") }
            val isAggregate = annotationsFqNames.any { it.endsWith("AggregateRoot") }

            if (isEntity || isAggregate) {
                report(
                    CodeSmell(
                        issue = issue,
                        entity = Entity.atName(property),
                        message = "@ValueObject '${klass.name}' must not contain property '${property.name}' of" +
                            " type '${classDescriptor.name}', which is annotated as " +
                            if (isEntity) "@DomainEntity" else "@AggregateRoot" + ".",
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
