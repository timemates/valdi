package com.y9vad9.valdi.builder

import com.y9vad9.valdi.ValidationFailure

/**
 * Defines a fluent step in the constraint DSL for associating a validation error
 * with a conditional predicate.
 *
 * This interface is returned after invoking `gives(...)` inside a [ConstraintBuilderScope],
 * and allows expressing conditions under which the error should be triggered.
 *
 * Example usage:
 * ```
 * gives(MyError.InvalidInput) on { it.isBlank() }
 * gives { MyError.Dynamic() } unless { it.isValid() }
 * ```
 *
 * @param TIn The type of input being validated.
 */
public sealed interface GivesBuilder<TIn, TErr : ValidationFailure> {
    /**
     * Adds a constraint that triggers the associated error
     * **if the given condition evaluates to true**.
     *
     * @param condition Predicate that determines whether the error should be triggered.
     */
    public infix fun on(condition: (TIn) -> Boolean)

    /**
     * Adds a constraint that triggers the associated error
     * **unless the given condition evaluates to true**.
     *
     * @param condition Predicate that determines whether the error should be *not* triggered.
     */
    public infix fun unless(condition: (TIn) -> Boolean)
}

internal class StaticGivesBuilder<TIn, TErr : ValidationFailure>(
    private val error: TErr,
    private val target: ConstraintBuilderScope<TIn, TErr>,
) : GivesBuilder<TIn, TErr> {
    override infix fun on(condition: (TIn) -> Boolean) {
        target.add { input -> if (condition(input)) error else null }
    }

    override infix fun unless(condition: (TIn) -> Boolean) {
        target.add { input -> if (!condition(input)) error else null }
    }
}

internal class LazyGivesBuilder<TIn, TErr : ValidationFailure>(
    private val errorFactory: (TIn) -> TErr,
    private val target: ConstraintBuilderScope<TIn, TErr>,
) : GivesBuilder<TIn, TErr> {
    override infix fun on(condition: (TIn) -> Boolean) {
        target.add { input -> if (condition(input)) errorFactory(input) else null }
    }

    override infix fun unless(condition: (TIn) -> Boolean) {
        target.add { input -> if (!condition(input)) errorFactory(input) else null }
    }
}
