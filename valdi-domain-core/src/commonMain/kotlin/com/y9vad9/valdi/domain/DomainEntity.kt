package com.y9vad9.valdi.domain

/**
 * Marks a class as a **Domain Entity** within the domain model.
 *
 * Entities are distinguished by a **stable identity**, not just their attributes.
 * They may contain mutable state — but in Kotlin, it's strongly recommended to
 * model changes through immutable copies (`data class + copy(...)`).
 *
 * A domain entity can be:
 * - A **standalone root** (if annotated with [AggregateRoot]), or
 * - A **member of an aggregate**, encapsulated and modified through the root only.
 *
 * ### Design Rules:
 * - Must contain an identity property (e.g., `id: EntityId`).
 * - Should not contain aggregate roots or reference them directly (only by ID).
 * - Should encapsulate business behavior and enforce local invariants.
 * - Must not be accessed or persisted independently unless it's an aggregate root.
 *
 * ### Example:
 * ```
 * @DomainEntity
 * data class OrderItem(
 *     val id: OrderItemId,
 *     val productId: ProductId,
 *     val quantity: Quantity
 * )
 * ```
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
public annotation class DomainEntity {
    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
    public annotation class Id
}