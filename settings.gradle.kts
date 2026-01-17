dependencyResolutionManagement {
    pluginManagement {
        repositories {
            maven("https://maven.wagyourtail.xyz/releases")
            mavenCentral()
            gradlePluginPortal()
        }
    }
}

dependencyResolutionManagement.versionCatalogs.create("catalog") {
    // https://github.com/palantir/gradle-git-version
    plugin("git-version", "com.palantir.git-version").version("3.+")

    plugin("unmined", "xyz.wagyourtail.unimined").version("1.+")

    val minecraft = "1.20.1"
    version("minecraft", minecraft)

    val kotlin = "2.1.21"
    version("kotlin", kotlin)
    plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm").version(kotlin)
    plugin("kotlin-plugin-serialization", "org.jetbrains.kotlin.plugin.serialization").version(kotlin)

    val kotlinxSerialization = "1.7.3"
    library("kotlinx-serialization-core", "org.jetbrains.kotlinx", "kotlinx-serialization-core").version(
        kotlinxSerialization
    )
    library("kotlinx-serialization-json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version(
        kotlinxSerialization
    )

    // https://linkie.shedaniel.dev/dependencies?loader=forge
    version("forge", "47.4.10")
    library("kotlin-forge", "thedarkcolour", "kotlinforforge").version("4.12.0")

    library("mixin", "org.spongepowered", "mixin").version("0.8.7")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

val name: String by settings

rootProject.name = name
