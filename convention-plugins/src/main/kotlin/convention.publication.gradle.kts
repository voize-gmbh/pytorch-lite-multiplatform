import java.util.*

plugins {
    `maven-publish`
    signing
    id("org.jetbrains.dokka")
    id("io.github.gradle-nexus.publish-plugin")
}

val signingKey: String? = System.getenv("SIGNING_SIGNING_KEY")
val signingPassword: String? = System.getenv("SIGNING_PASSWORD")

// Stub secrets to let the project sync and build without the publication values set up
ext["ossrhUsername"] = null
ext["ossrhPassword"] = null

// Grabbing secrets from local.properties file or from environment variables, which could be used on CI
val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    secretPropsFile.reader().use {
        Properties().apply {
            load(it)
        }
    }.onEach { (name, value) ->
        ext[name.toString()] = value
    }
} else {
    ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
}

fun getExtraString(name: String) = ext[name]?.toString()

tasks {
    val dokkaOutputDir = "$buildDir/dokka"

    dokkaHtml {
        outputDirectory.set(file(dokkaOutputDir))
    }

    val deleteDokkaOutputDir by registering(Delete::class) {
        delete(dokkaOutputDir)
    }

    register<Jar>("javadocJar") {
        dependsOn(deleteDokkaOutputDir, dokkaHtml)
        archiveClassifier.set("javadoc")
        from(dokkaOutputDir)
    }
}

publishing {
    // Configure all publications
    publications.withType<MavenPublication> {

        artifact(tasks.named<Jar>("javadocJar").get())

        // Provide artifacts information requited by Maven Central
        pom {
            name.set("pytorch-lite-multiplatform")
            description.set("Kotlin Multiplatform wrapper for PyTorch Lite")
            url.set("https://github.com/voize-gmbh/pytorch-lite-multiplatform")

            licenses {
                license {
                    name.set("Apache-2.0")
                    url.set("https://opensource.org/licenses/Apache-2.0")
                }
            }
            developers {
                developer {
                    id.set("LeonKiefer")
                    name.set("Leon Kiefer")
                    email.set("leon@voize.de")
                }
                developer {
                    id.set("ErikZiegler")
                    name.set("Erik Ziegler")
                    email.set("erik@voize.de")
                }
            }
            scm {
                url.set("https://github.com/voize-gmbh/pytorch-lite-multiplatform")
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(getExtraString("ossrhUsername"))
            password.set(getExtraString("ossrhPassword"))
        }
    }
}

if (signingPassword != null) {
    signing {
        if (signingKey != null) {
            useInMemoryPgpKeys(signingKey, signingPassword)
        }
        sign(publishing.publications)
    }
}
