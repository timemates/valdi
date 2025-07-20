package com.y9vad9.valdi

/**
 * Represents a constraint that validates input of type [TIn] and optionally produces an error of type [TErr].
 *
 * This interface is used to define business or domain-level validation rules that can be applied
 * to user-provided or external input values. If the input does not satisfy the constraint,
 * an appropriate [TErr] should be returned to describe the failure.
 *
 * @param TIn The type of the input to be validated.
 * @param TErr The type of the error returned if validation fails. `null` indicates that the input is valid.
 */
public fun interface InputConstraint<TIn, TErr> {
    /**
     * Performs validation on the given [input].
     *
     * @param input The value to validate.
     * @return An error of type [TErr] if the input violates the constraint, or `null` if it passes.
     */
    public fun check(input: TIn): TErr?
}