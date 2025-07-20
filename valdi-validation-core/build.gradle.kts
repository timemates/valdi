plugins {
    id(conventions.multiplatform.library)
}

group = "com.y9vad9.valdi"

mavenPublishing {
    coordinates(
        groupId = "com.y9vad9.valdi",
        artifactId = "valdi-validation-core",
        version = System.getenv("LIB_VERSION") ?: return@mavenPublishing,
    )

    pom {
        name.set("Valdi Validation Core")
        description.set("Core module of Valdi Validation")
    }
}
