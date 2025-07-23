package com.y9vad9.valdi

/**
 * Marker interface for representing validation failures.
 *
 * All errors returned by validation constraints must implement this interface
 *  to be processed by the validation engine.
 *
 * Implementing this interface allows a domain-specific error type to participate
 * in validation flows while preserving strong typing and semantic meaning.
 */
public interface ValidationFailure
