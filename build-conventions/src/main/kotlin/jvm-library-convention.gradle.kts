plugins {
    kotlin("jvm")
    id("detekt-convention")
    id("kover-convention")
    id("jvm-tests-convention")
    id("publish-convention")
}

kotlin {
    explicitApi()
}