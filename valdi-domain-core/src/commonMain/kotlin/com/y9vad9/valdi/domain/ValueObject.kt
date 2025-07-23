package com.y9vad9.valdi.domain

/**
 * Marks a class as a **Value Object** within the domain model.
 *
 * Value objects are defined **entirely by their values**. They have:
 *
 * - **No identity**: two value objects with the same data are considered equal.
 * - **Immutability**: once created, their state must never change.
 * - **No lifecycle tracking**: they are owned and managed by entities or aggregate roots.
 *
 * ### Design Rules:
 * - Must be a `data class` or `value class` with **only `val` properties**.
 * - Must not contain other entities or aggregate roots.
 * - Can contain other value objects.
 * - Can be freely copied, shared, or replaced.
 *
 * ### Example:
 * ```
 * @ValueObject
 * data class Email(val value: String)
 * ```
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
public annotation class ValueObject
