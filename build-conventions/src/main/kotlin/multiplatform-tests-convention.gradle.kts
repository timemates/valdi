import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("multiplatform-convention")
}

val libs = the<LibrariesForLibs>()

dependencies {
    commonTestImplementation(libs.kotlin.test)
}

tasks.withType<Test> {
    useJUnitPlatform()
}