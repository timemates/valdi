package com.y9vad9.valdi

/**
 * Represents a generic factory that validates [TIn] against a set of [InputConstraint]s
 * and produces a value of type [TOut] or returns a validation error of type [TErr].
 *
 * This abstraction is ideal for domain model creation, where business rules or invariants must be enforced.
 *
 * @param TIn The raw input type.
 * @param TOut The successfully constructed output type.
 * @param TErr The type of error returned when validation fails.
 */
public interface Factory<TIn : Any, TOut : Any, TErr : ValidationFailure> {
    /** Constraints to be validated before creation. */
    public val constraints: List<InputConstraint<TIn, TErr>>

    /**
     * Checks the given [input] against this constraint and returns a list of
     * validation errors encountered during the check.
     *
     * If the input satisfies the constraint, the returned list will be empty.
     *
     * @param input The value to be validated.
     * @return A list of errors of type [TErr] if validation fails, or an empty list if validation passes.
     */
    public fun check(input: TIn): List<TErr>

    /**
     * Attempts to create a new [TOut] instance from the provided [input].
     *
     * All [constraints] are applied, and the first one that fails returns an [ValidationResult.Failure].
     * If all constraints pass, the object is created and returned as [ValidationResult.Success].
     *
     * @param input The input to validate and transform.
     * @return A [ValidationResult] representing either a successful creation or the failure reason.
     * If there is multiple – first one take precedence.
     */
    public fun create(input: TIn): ValidationResult<TOut, TErr>
}

/**
 * Attempts to create a new [TOut] instance from the provided [input].
 *
 * If validation fails, throws the exception created by [getOrThrow].
 *
 * @param getOrThrow Function to transform the error [TErr] into a [Throwable] to throw.
 * @return The successfully created [TOut] instance.
 * @throws ValidationException when validation fails.
 */
public fun <TIn : Any, TOut : Any, TErr : ValidationFailure> Factory<TIn, TOut, TErr>.createOrThrow(
    input: TIn,
): TOut = create(input).getOrThrow()

/**
 * Attempts to create a new [TOut] instance from the provided [input].
 *
 * If validation fails, returns the result of the [orElse] fallback function.
 *
 * @param orElse Function to provide a fallback [TOut] when validation fails.
 * @return The successfully created [TOut] or the fallback result.
 */
public inline fun <TIn : Any, TOut : Any, TErr : ValidationFailure> Factory<TIn, TOut, TErr>.createOrElse(
    input: TIn,
    orElse: (TErr) -> TOut,
): TOut = create(input).getOrElse(orElse)