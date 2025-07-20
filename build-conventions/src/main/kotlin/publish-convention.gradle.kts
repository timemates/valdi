import gradle.kotlin.dsl.accessors._e1ed38c5edb9d2dfb0afff7b03a80918.mavenPublishing

plugins {
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    publishToMavenCentral()

    pom {
        url.set("https://github.com/y9vad9/valdi")
        inceptionYear.set("2024")

        licenses {
            license {
                name.set("The MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("y9vad9")
                name.set("Vadym Yaroshchuk")
                url.set("https://github.com/y9vad9/")
            }
        }

        scm {
            url.set("https://github.com/y9vad9/valdi")
            connection.set("scm:git:git://github.com/y9vad9/valdi.git")
            developerConnection.set("scm:git:ssh://git@github.com/y9vad9/valdi.git")
        }

        issueManagement {
            system.set("GitHub Issues")
            url.set("https://github.com/y9vad9/valdi/issues")
        }
    }

    //signAllPublications()
}
