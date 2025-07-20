package com.y9vad9.valdi

public class ValidationException(failure: ValidationFailure) : Exception() {
    override val message: String = "The contract violation is occurred: ${failure::class}"
}