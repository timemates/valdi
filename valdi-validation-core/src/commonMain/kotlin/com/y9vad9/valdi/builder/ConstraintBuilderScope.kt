package com.y9vad9.valdi.builder

import com.y9vad9.valdi.InputConstraint
import com.y9vad9.valdi.ValidationFailure

/**
 * DSL scope for building a list of [InputConstraint]s that validate input of type [TIn]
 * and produce [ValidationFailure]s of type [TErr] when validation fails.
 *
 * This builder supports three types of constraints:
 * - **Predefined constraints** via [add]
 * - **Inline constant errors** via `gives(error) on/unless { condition }`
 * - **Lazy error generation** via `gives { error } on/unless { condition }`
 *
 * Example usage:
 * ```
 * constraints {
 *     gives(MyError.MissingValue) on { it.isBlank() }
 *     gives { MyError.ComputedError(it) } unless { it.isValid() }
 *     add(customConstraint)
 * }
 * ```
 *
 * @param TIn The type of the input being validated.
 * @param TErr The type of error, must extend [ValidationFailure].
 */
@FactoryDsl
public class ConstraintBuilderScope<TIn, TErr : ValidationFailure> {
    private val constraints = mutableListOf<InputConstraint<TIn, TErr>>()

    /**
     * Adds a constraint that triggers a constant error if the provided condition is true.
     *
     * @param error The error to associate with the condition.
     * @return A [GivesBuilder] allowing the error to be conditionally applied.
     */
    public infix fun gives(error: TErr): GivesBuilder<TIn, TErr> =
        StaticGivesBuilder(error, this)

    /**
     * Adds a constraint that triggers a lazily generated error if the provided condition is true.
     *
     * @param factory The error factory to produce the error when needed.
     * @return A [GivesBuilder] allowing the error to be conditionally applied.
     */
    public infix fun gives(factory: (TIn) -> TErr): GivesBuilder<TIn, TErr> =
        LazyGivesBuilder(factory, this)

    /**
     * Adds a raw [InputConstraint] to the constraint set.
     */
    public fun add(constraint: InputConstraint<TIn, TErr>) {
        constraints += constraint
    }

    /**
     * Internal method to retrieve the accumulated constraints.
     */
    internal fun build(): List<InputConstraint<TIn, TErr>> = constraints
}
