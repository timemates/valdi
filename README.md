# Valdi
[![CodeQL Analysis](https://github.com/y9vad9/valdi/actions/workflows/analyse.codeql.yml/badge.svg)](https://github.com/y9vad9/valdi/actions/workflows/analyse.codeql.yml) [![Detekt Checks](https://github.com/y9vad9/valdi/actions/workflows/check.detekt.yml/badge.svg)](https://github.com/y9vad9/valdi/actions/workflows/check.detekt.yml) [![Test Suite](https://github.com/y9vad9/valdi/actions/workflows/check.tests.yml/badge.svg)](https://github.com/y9vad9/valdi/actions/workflows/check.tests.yml) [![Coverage](https://codecov.io/gh/y9vad9/valdi/branch/main/graph/badge.svg)](https://codecov.io/gh/y9vad9/valdi) [![Published to Maven Central](https://github.com/y9vad9/valdi/actions/workflows/publish.yml/badge.svg)](https://github.com/y9vad9/valdi/actions/workflows/publish.yml)

Valdi is a modular Kotlin library designed for validation and domain-driven design (DDD) enforcement in your Kotlin projects. It provides a powerful validation DSL, domain modeling annotations, and static analysis tooling to help you build robust, maintainable applications aligned with DDD principles.

## Validation

`valdi-validation-core` provides a Kotlin-first validation framework centered around the concept of a Factory.
A factory validates input using a set of constraints and either produces a valid output or returns validation errors.

Constraints are defined as predicates coupled with error results implementing a marker interface for validation
failures.
Validation stops at the first failure, providing clear and precise error feedback.

The API exposes a simple and expressive DSL to build constraints inline, supporting both constant and lazily created
errors.

### Basic example

```kotlin
val factory = factory<UserInput, User, UserError> {
    constraints {
        gives(UserError.EmptyName) on { it.name.isBlank() }
        gives { UserError.InvalidEmail(it.email) } unless { it.email.isValidEmail() }
    }
    constructor { input -> User(input.name, input.email) }
}

sealed interface UserError : ValidationError { /* ... */ }

val result: ValidationResult<User, UserError> = factory.create(input)
val unsafe: User = factory.createOrThrow(input)
val nullable: User? = factory.createOrNull(input)
```

### Domain Integration & Static Analysis

To support Domain-Driven Design practices, valdi provides optional modules that integrate with your domain model and
help enforce consistency:

The `valdi-domain-core` module introduces marker annotations like @ValueObject, @AggregateRoot, and @DomainEntity.
These are used to declare the role of a class within your domain model.
They do not affect behavior but serve as structural metadata.

The `valdi-domain-detekt` module provides Detekt rules that analyze your codebase for violations of common DDD patterns,
based on the annotations defined in valdi-domain-core.
This enables early feedback during development and helps maintain architectural boundaries.

These modules are optional and can be used independently of the validation system.

## Implementation

valdi is published as a set of modular libraries. You can include only what you need.
<details>
<summary><code>valdi-validation-core</code></summary>
<pre>
<code lang="Kotlin">
implementation("com.y9vad9.valdi:valdi-validation-core:$version")
</code>
</pre>
</details>
<details>
<summary><code>valdi-domain-core</code></summary>
<pre>
<code lang="Kotlin">
implementation("com.y9vad9.valdi:valdi-validation-core:$version")
</code>
</pre>
</details>

<details>
<summary><code>valdi-domain-detekt</code> (Detekt plugin)</summary>
<pre>
<code lang="Kotlin">
detektPlugins("com.y9vad9.valdi:valdi-domain-detekt:$version")
</code>
</pre>
</details>

### Supported Platforms

valdi is a Kotlin Multiplatform library with support for the following targets:

- JVM (with toolchain targeting Java 11)
- JavaScript (both Browser and Node.js)
- iOS (x64, arm64, simulator arm64)
- WebAssembly:
- wasmJs (for both Browser and Node.js)
- wasmWasi (for Node.js)

## License

This project is licensed under the MIT License. You are free to use, modify, and distribute it with attribution.
