import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("jvm-convention")
}

val libs = the<LibrariesForLibs>()

dependencies {
    testImplementation(libs.kotlin.test)
}

tasks.withType<Test> {
    useJUnitPlatform()
}