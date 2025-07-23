enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "valdi"

includeBuild("build-conventions")

include(
    ":valdi-validation-core",
    ":valdi-domain-core",
    ":valdi-domain-detekt",
)
