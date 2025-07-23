plugins {
    id(conventions.jvm.library)
}

group = "com.y9vad9.valdi"

dependencies {
    implementation(libs.detekt.api)
    testImplementation(libs.detekt.test)
    testImplementation(libs.kotlin.test.junit5)

    implementation(projects.valdiDomainCore)
}

mavenPublishing {
    coordinates(
        groupId = "com.y9vad9.valdi",
        artifactId = "valdi-domain-detekt",
        version = System.getenv("LIB_VERSION") ?: return@mavenPublishing,
    )

    pom {
        name.set("Valdi Domain Detekt Ruleset")
        description.set("Valdi ruleset for Detekt for Domains")
    }
}
