import org.jetbrains.kotlin.gradle.dsl.*

plugins {
    id("multiplatform-convention")
    id("publish-convention")
}

kotlin {
    explicitApi = ExplicitApiMode.Strict
}
