package com.y9vad9.valdi.builder

import com.y9vad9.valdi.Factory
import com.y9vad9.valdi.InputConstraint
import com.y9vad9.valdi.ValidationFailure
import com.y9vad9.valdi.ValidationResult
import kotlin.js.JsName

/**
 * Creates a [Factory] instance using a builder-style DSL.
 *
 * The [block] configures constraints and a constructor function that
 * transforms input of type [TIn] into output of type [TOut].
 *
 * Validation errors are represented by [TErr], which must extend [ValidationFailure].
 *
 * Example usage:
 * ```
 * val userFactory = factory<UserInput, User, UserError> {
 *     constraints {
 *         gives(UserError.NameEmpty) on { it.name.isBlank() }
 *     }
 *     constructor { input -> User(input.name) }
 * }
 * ```
 *
 * @param TIn The input type to validate and transform.
 * @param TOut The output type produced by the factory.
 * @param TErr The validation failure type.
 * @param block DSL block to configure the factory.
 * @return A [Factory] that validates input and constructs output or returns errors.
 */
@FactoryDsl
public fun <TIn : Any, TOut : Any, TErr : ValidationFailure> factory(
    block: FactoryBuilder<TIn, TOut, TErr>.() -> Unit,
): Factory<TIn, TOut, TErr> = FactoryBuilder<TIn, TOut, TErr>().apply(block).build()

/**
 * Creates a [Factory] instance from a predefined list of [InputConstraint]s and a constructor function.
 *
 * This function is a convenience overload for when you already have constraints defined,
 * allowing quick creation of a factory without the builder DSL.
 *
 * @param TIn The input type to validate and transform.
 * @param TOut The output type produced by the factory.
 * @param TErr The validation failure type.
 * @param constraints List of input constraints to apply during validation.
 * @param constructor Function that produces [TOut] from valid [TIn].
 * @return A [Factory] that validates input against given constraints and constructs output or returns errors.
 */
public fun <TIn : Any, TOut : Any, TErr : ValidationFailure> factory(
    constraints: List<InputConstraint<TIn, TErr>>,
    constructor: (TIn) -> TOut,
): Factory<TIn, TOut, TErr> {
    return factory {
        constraints {
            constraints.forEach(::add)
        }

        constructor {
            constructor(it)
        }
    }
}

/**
 * Builder DSL for creating a [Factory] that validates inputs and constructs outputs.
 *
 * Use [constraints] to define validation rules and [constructor] to specify
 * how valid input is transformed into output.
 *
 * @param TIn The type of input to validate.
 * @param TOut The type of output to construct.
 * @param TErr The type of validation failure.
 */
@FactoryDsl
public class FactoryBuilder<TIn : Any, TOut : Any, TErr : ValidationFailure> {
    private var constructor: ((TIn) -> TOut)? = null
    private val constraintBuilder = ConstraintBuilderScope<TIn, TErr>()

    /**
     * Configure input validation constraints within this block.
     *
     * @param block DSL block to add constraints.
     */
    public fun constraints(block: ConstraintBuilderScope<TIn, TErr>.() -> Unit) {
        constraintBuilder.apply(block)
    }

    /**
     * Define the constructor function that transforms valid input into output.
     *
     * This must be set before building the factory.
     *
     * @param block Function taking input [TIn] and producing output [TOut].
     */
    @JsName("factoryConstructor")
    public fun constructor(block: (TIn) -> TOut) {
        constructor = block
    }

    /**
     * Builds the [Factory] instance.
     *
     * @throws IllegalStateException if constructor function is not defined.
     * @return The configured [Factory].
     */
    internal fun build(): Factory<TIn, TOut, TErr> {
        val constructorFn = requireNotNull(constructor) { "Constructor must be defined." }
        val constraints = constraintBuilder.build()

        return object : Factory<TIn, TOut, TErr> {
            override val constraints: List<InputConstraint<TIn, TErr>> = constraints

            override fun check(input: TIn): List<TErr> =
                constraints.mapNotNull { it.check(input) }

            override fun create(input: TIn): ValidationResult<TOut, TErr> {
                val errors = check(input)
                return if (errors.isEmpty()) {
                    ValidationResult.Success(constructorFn(input))
                } else {
                    ValidationResult.Failure(errors.first())
                }
            }
        }
    }
}

