package com.y9vad9.valdi.domain.detekt.rule.test

object ClasspathFiles {
    val AggregateRoot = """
        public annotation class AggregateRoot
    """.trimIndent()

    val ValueObject = """
        public annotation class ValueObject
    """.trimIndent()

    val DomainEntity = """
        @Target(AnnotationTarget.CLASS)
        @Retention(AnnotationRetention.RUNTIME)
        public annotation class DomainEntity {
            @Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
            public annotation class Id
        }
    """.trimIndent()

    val Builtins = """
        package com.y9vad9.valdi.domain
        
        $AggregateRoot
        
        $ValueObject
        
        $DomainEntity
    """.trimIndent()
}