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
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

/**
 * Ensures that an `@AggregateRoot` class only contains properties that are either:
 * - Annotated with `@ValueObject` or `@DomainEntity`
 * - Or ignored because:
 *    - They have a custom getter
 *    - They are delegated (e.g., `by lazy`)
 *    - They are private
 *
 * This rule enforces strict encapsulation of aggregate roots in DDD by limiting
 * their members to proper domain types.
 */
@RequiresTypeResolution
public class AggregateMustOnlyContainDomainObjectsRule(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Defect,
        description = """
            Properties inside an @AggregateRoot must have types annotated with @ValueObject or @DomainEntity.
            Private, delegated, or properties with custom getters are ignored.
        """.trimIndent(),
        debt = Debt.FIVE_MINS,
    )

    override fun visitClass(klass: KtClass) {
        if (!klass.hasAnnotation("AggregateRoot")) return

        val bindingContext = this.bindingContext
        if (bindingContext == BindingContext.EMPTY) {
            // Type resolution not enabled; cannot analyze properly
            return
        }

        klass.getBody()?.properties?.forEach { property ->
            if (property.isPrivate()) return@forEach
            if (property.getter != null) return@forEach
            if (property.hasDelegate()) return@forEach

            val typeReference = property.typeReference ?: return@forEach

            val type = bindingContext[BindingContext.TYPE, typeReference] ?: return@forEach
            val classDescriptor = type.constructor.declarationDescriptor as? ClassDescriptor ?: return@forEach

            if (!hasDomainAnnotation(classDescriptor)) {
                report(
                    CodeSmell(
                        issue,
                        Entity.atName(property),
                        message = "Property '${property.name}' inside @AggregateRoot must be a type annotated with @ValueObject or @DomainEntity. " +
                            "Type '${classDescriptor.fqNameSafe}' is missing required annotation."
                    )
                )
            }
        }

        super.visitClass(klass)
    }

    private fun hasDomainAnnotation(classDescriptor: ClassDescriptor): Boolean {
        return classDescriptor.annotations.hasAnnotation(FqName("com.y9vad9.valdi.domain.ValueObject")) ||
            classDescriptor.annotations.hasAnnotation(FqName("com.y9vad9.valdi.domain.DomainEntity"))
    }

    private fun KtModifierListOwner.hasAnnotation(name: String): Boolean {
        return annotationEntries.any { it.shortName?.asString() == name }
    }
}
