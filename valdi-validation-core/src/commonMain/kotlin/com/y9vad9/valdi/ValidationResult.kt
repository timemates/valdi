package com.y9vad9.valdi

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Represents the result of a validation operation.
 * Provides either a [Success] with a validated value or an [Failure] with a reason.
 *
 * @param TSuccess The type of successful result.
 * @param TFailure The type of error if validation fails.
 */
public sealed interface ValidationResult<out TSuccess, out TFailure : ValidationFailure> {

    /**
     * Represents a successful validation result containing the validated value.
     */
    public data class Success<TSuccess>(
        public val value: TSuccess,
    ) : ValidationResult<TSuccess, Nothing>

    /**
     * Represents a failed validation result containing the error reason.
     */
    public data class Failure<TFailure : ValidationFailure>(
        public val error: TFailure,
    ) : ValidationResult<Nothing, TFailure>
}

// --- Extensions ---

/**
 * Returns `true` if this result is a [ValidationResult.Success].
 */
@OptIn(ExperimentalContracts::class)
public fun ValidationResult<*, *>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is ValidationResult.Success<*>)
    }
    return this is ValidationResult.Success
}

/**
 * Returns `true` if this result is a [ValidationResult.Failure].
 */
@OptIn(ExperimentalContracts::class)
public fun ValidationResult<*, *>.isError(): Boolean {
    contract {
        returns(true) implies (this@isError is ValidationResult.Failure<*>)
    }
    return this is ValidationResult.Failure
}

/**
 * Returns the value if this is a [ValidationResult.Success], or `null` otherwise.
 */
public fun <TSuccess, TFailure : ValidationFailure> ValidationResult<TSuccess, TFailure>.getOrNull(): TSuccess? =
    when (this) {
        is ValidationResult.Success -> value
        is ValidationResult.Failure -> null
    }

/**
 * Returns the error if this is a [ValidationResult.Failure], or `null` otherwise.
 */
public fun <TSuccess, TFailure : ValidationFailure> ValidationResult<TSuccess, TFailure>.errorOrNull(): TFailure? =
    when (this) {
        is ValidationResult.Success -> null
        is ValidationResult.Failure -> error
    }

/**
 * Returns the value if this is a [ValidationResult.Success],
 * otherwise throws an [IllegalStateException].
 */
public fun <TSuccess, TFailure : ValidationFailure> ValidationResult<TSuccess, TFailure>.requireSuccess(): TSuccess =
    when (this) {
        is ValidationResult.Success -> value
        is ValidationResult.Failure -> error("Expected Success but found Error: $error")
    }

/**
 * Returns the value if this is a [ValidationResult.Success],
 * otherwise returns the result of the [fallback] function.
 */
public inline fun <TSuccess, TFailure : ValidationFailure> ValidationResult<TSuccess, TFailure>.getOrElse(
    fallback: (TFailure) -> TSuccess,
): TSuccess = when (this) {
    is ValidationResult.Success -> value
    is ValidationResult.Failure -> fallback(error)
}

/**
 * Returns the value if this is a [ValidationResult.Success], or throws the result of [or] if [Error].
 *
 * Use this to explicitly propagate or wrap error conditions.
 */
public fun <TSuccess, TFailure : ValidationFailure> ValidationResult<TSuccess, TFailure>.getOrThrow(): TSuccess =
    when (this) {
        is ValidationResult.Success -> value
        is ValidationResult.Failure -> throw ValidationException(error)
    }

/**
 * Transforms the successful value using [transform], or propagates the [ValidationResult.Failure].
 */
public inline fun <TSuccess, TFailure : ValidationFailure, R> ValidationResult<TSuccess, TFailure>.map(
    transform: (TSuccess) -> R,
): ValidationResult<R, TFailure> = when (this) {
    is ValidationResult.Success -> ValidationResult.Success(transform(value))
    is ValidationResult.Failure -> this
}

/**
 * Transforms the successful value into another [ValidationResult] using [transform], or propagates the [Error].
 */
public inline fun <TSuccess, TFailure : ValidationFailure, R> ValidationResult<TSuccess, TFailure>.flatMap(
    transform: (TSuccess) -> ValidationResult<R, TFailure>,
): ValidationResult<R, TFailure> = when (this) {
    is ValidationResult.Success -> transform(value)
    is ValidationResult.Failure -> this
}

/**
 * Transforms the error value using [transform], or propagates the [Success].
 */
public inline
fun <TSuccess, TFailure : ValidationFailure, F : ValidationFailure> ValidationResult<TSuccess, TFailure>.mapError(
    transform: (TFailure) -> F,
): ValidationResult<TSuccess, F> = when (this) {
    is ValidationResult.Success -> this
    is ValidationResult.Failure -> ValidationResult.Failure(transform(error))
}
