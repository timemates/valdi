package com.y9vad9.valdi

/**
 * Exception indicating that a validation contract was violated.
 *
 * This is typically thrown when a [ValidationFailure] is encountered
 * and the failure is considered unrecoverable or exceptional in the flow.
 *
 * @param failure The validation failure that caused this exception.
 */
public class ValidationException(
    public val failure: ValidationFailure,
) : Exception() {
    override val message: String =
        "Validation failed due to contract violation: ${failure::class.simpleName} – $failure"
}
