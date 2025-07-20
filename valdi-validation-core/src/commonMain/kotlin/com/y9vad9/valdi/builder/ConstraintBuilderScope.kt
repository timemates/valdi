package com.y9vad9.valdi.builder

import com.y9vad9.valdi.InputConstraint

/**
 * DSL scope for building a list of [InputConstraint]s that validate input of type [TIn]
 * and produce errors of type [TErr] when validation fails.
 *
 * This builder provides convenient methods to add constraints with static or
 * lazily generated errors, as well as to add pre-existing [InputConstraint] instances.
 *
 * Example usage:
 * ```
 * constraints {
 *    add(MyError.SomeError) { foo.isValid() }
 *    add({ MyError.AnotherError() }) { hasX() }
 *    add(customConstraint)
 * }
 * ```
 *
 * @param TIn The type of the input to validate.
 * @param TErr The type of error produced when a constraint is violated.
 */
@FactoryDsl
public class ConstraintBuilderScope<TIn, TErr> {
    private val constraints = mutableListOf<InputConstraint<TIn, TErr>>()

    public fun gives(error: TErr, condition: (TIn) -> Boolean) {
        constraints += InputConstraint { input -> if (condition(input)) error else null }
    }

    public fun gives(factory: () -> TErr, condition: (TIn) -> Boolean) {
        constraints += InputConstraint { input -> if (condition(input)) factory() else null }
    }

    public fun add(constraint: InputConstraint<TIn, TErr>) {
        constraints += constraint
    }

    internal fun build(): List<InputConstraint<TIn, TErr>> = constraints
}