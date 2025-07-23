package com.y9vad9.valdi.domain

/**
 * Marks a class as an **Aggregate Root** within a domain model.
 *
 * An Aggregate Root is the entry point to an aggregate: a cluster of domain objects that
 * are treated as a single unit. It is responsible for enforcing consistency and invariants
 * within the aggregate boundary.
 *
 * Classes annotated with `@AggregateRoot` should contain only members whose types are also
 * semantically annotated — typically with [ValueObject] or [AggregateRoot]. This ensures
 * clarity and strictness in expressing domain boundaries.
 *
 * ### Usage
 * Apply this annotation to a class representing the root of an aggregate in your domain layer.
 *
 * ```
 * @AggregateRoot
 * class Order(
 *     val orderId: OrderId,
 *     val items: List<OrderItem>, // <- forbidden
 *     val customer: CustomerId
 * )
 * ```
 *
 * In this example, `OrderId`, `OrderItem`, and `CustomerId` are annotated with
 * `@ValueObject` or `@DomainEntity`.
 *
 * `@AggregateRoot` on member's classes is not allowed.
 */
public annotation class AggregateRoot
