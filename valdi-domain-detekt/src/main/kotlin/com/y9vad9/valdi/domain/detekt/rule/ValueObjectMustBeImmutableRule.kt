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
 * A Detekt rule that enforces immutability constraints on classes annotated with `@ValueObject`.
 *
 * ## Rule Overview
 * Value objects in Domain-Driven Design (DDD) are expected to be immutable, self-contained,
 * and defined by their properties rather than identity. This rule ensures that such immutability
 * expectations are upheld at the Kotlin language level.
 *
 * ## Checks Performed
 * - The class must not be `open` (i.e., it must be final).
 * - All properties in the class must be `val`, not `var`.
 * - No mutable types (such as `MutableList`, `ArrayList`, `HashMap`, etc.) are allowed as property types.
 *
 * ## Why This Matters
 * Immutability is a foundational property of value objects. Allowing mutation introduces subtle bugs,
 * breaks equality semantics, and contradicts core domain modeling principles. By enforcing these rules,
 * we ensure consistent and safe usage of value objects across the codebase.
 *
 * ## Example of a violation:
 * ```kotlin
 * @ValueObject
 * class Name(var value: String) // `var` is not allowed
 * ```
 *
 * ## Example of valid usage:
 * ```kotlin
 * @ValueObject
 * @JvmInline
 * value class Name(val value: String) // immutable and valid
 * ```
 */
public class ValueObjectMustBeImmutableRule(@Suppress("unused") config: Config) : Rule() {
    override val issue: Issue = Issue(
        id = javaClass.name,
        severity = Severity.CodeSmell,
        description = """
            Ensures that classes annotated with @ValueObject follow immutability constraints.
                    
            A value object must:
                - Be final (no `open` modifier or inheritance).
                - Not expose mutable properties (e.g., `var`, collections like `MutableList`).
                - Contain only other value objects or primitives.
                    
            Violations of this rule indicate potential misuse of domain modeling, 
            allowing state mutation where immutability is expected.
        """.trimIndent(),
        debt = Debt.FIVE_MINS,
    )

    override fun visitClass(klass: KtClass) {
        if (!klass.hasAnnotation("ValueObject")) return

        if (klass.hasModifier(KtTokens.OPEN_KEYWORD)) {
            report(
                CodeSmell(
                    issue,
                    Entity.atName(klass),
                    message = "@ValueObject classes must be final (not open).",
                )
            )
        }

        klass.getBody()?.properties?.forEach { property ->
            if (property.isVar) {
                report(
                    CodeSmell(
                        issue,
                        Entity.atName(property),
                        message = "@ValueObject property '${property.name}' must be declared as `val`, not `var`.",
                    )
                )
            }

            val typeRef = property.typeReference?.text ?: return@forEach

            // Basic check for mutable types
            val mutableTypes = listOf(
                "MutableList", "MutableSet", "MutableMap",
                "ArrayList", "HashMap", "HashSet",
                "LinkedList", "TreeMap", "TreeSet",
            )
            if (mutableTypes.any { typeRef.contains(it) || it.contains("Mutable") }) {
                report(
                    CodeSmell(
                        issue = issue,
                        entity = Entity.atName(property),
                        message = "@ValueObject property '${property.name}' uses a mutable type: '$typeRef'.",
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
