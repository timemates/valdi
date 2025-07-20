package com.y9vad9.valdi.domain.detekt.rule.test

import com.y9vad9.valdi.domain.AggregateRoot
import com.y9vad9.valdi.domain.detekt.rule.AggregateCannotContainAggregateRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.config.JvmClasspathRoot

//class AggregateCannotContainAggregateRuleTest {
//
//    private lateinit var env: KotlinCoreEnvironment
//    private val rule = AggregateCannotContainAggregateRule(Config.empty)
//
//    @BeforeTest
//    fun setupClasspath() {
//        val annotationJar = File(AggregateRoot::class.java.protectionDomain.codeSource.location.toURI())
//        env.configuration.add(CLIConfigurationKeys.CONTENT_ROOTS, JvmClasspathRoot(annotationJar))
//    }
//
//    @Test
//    fun `reports when aggregate root contains another aggregate root`() {
//        val code = """
//            @AggregateRoot
//            class Payment(val amount: Int)
//
//            @AggregateRoot
//            class Order(val payment: Payment)
//        """.trimIndent()
//
//        val findings = rule.compileAndLintWithContext(env, code)
//
//        println(code)
//
//        assertEquals(
//            expected = 1,
//            actual = findings.size,
//        )
//        assertEquals(
//            expected = "Order must not contain another @AggregateRoot 'Payment' as a property",
//            actual = findings.single().message.substringBefore('.'),
//        )
//    }
//
//    @Test
//    fun `does not report when aggregate root contains value object`() {
//        val code = """
//            @ValueObject
//            data class Money(val amount: Int)
//
//            @AggregateRoot
//            class Order(val total: Money)
//        """.trimIndent()
//
//        val findings = rule.compileAndLintWithContext(env, code)
//        assertEquals(0, findings.size)
//    }
//}
