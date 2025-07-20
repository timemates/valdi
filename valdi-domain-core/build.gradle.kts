plugins {
    id(conventions.multiplatform.library)
}

group = "com.y9vad9.valdi"

mavenPublishing {
    coordinates(
        groupId = "com.y9vad9.valdi",
        artifactId = "valdi-domain-core",
        version = System.getenv("LIB_VERSION") ?: return@mavenPublishing,
    )

    pom {
        name.set("Valdi Domain Core")
        description.set("Valdi Annotations for Domain to be validated by Detekt.")
    }
}
