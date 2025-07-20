package com.y9vad9.valdi.builder

import com.y9vad9.valdi.Factory
import com.y9vad9.valdi.InputConstraint
import com.y9vad9.valdi.ValidationFailure
import com.y9vad9.valdi.ValidationResult
import kotlin.js.JsName

public fun <TIn : Any, TOut : Any, TErr : ValidationFailure> factory(
    block: FactoryBuilder<TIn, TOut, TErr>.() -> Unit,
): Factory<TIn, TOut, TErr> = FactoryBuilder<TIn, TOut, TErr>().apply(block).build()

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

@FactoryDsl
public class FactoryBuilder<TIn : Any, TOut : Any, TErr : ValidationFailure> {
    private var constructor: ((TIn) -> TOut)? = null
    private val constraintBuilder = ConstraintBuilderScope<TIn, TErr>()

    public fun constraints(block: ConstraintBuilderScope<TIn, TErr>.() -> Unit) {
        constraintBuilder.apply(block)
    }

    @JsName("factoryConstructor")
    public fun constructor(block: (TIn) -> TOut) {
        constructor = block
    }

    internal fun build(): Factory<TIn, TOut, TErr> {
        val constructorFn = requireNotNull(constructor) { "Constructor must be defined." }
        val constraints = constraintBuilder.build()

        return object : Factory<TIn, TOut, TErr> {
            override val constraints: List<InputConstraint<TIn, TErr>> = constraints

            override fun check(input: TIn): List<TErr> {
                return constraints.mapNotNull { constraint ->
                    constraint.check(input)
                }
            }

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