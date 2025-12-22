import groovy.lang.Closure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    alias(catalog.plugins.kotlin.jvm)
    alias(catalog.plugins.kotlin.plugin.serialization)

    alias(catalog.plugins.git.version)

    alias(catalog.plugins.unmined)
}

val archive_name: String by rootProject.properties
val id: String by rootProject.properties
val name: String by rootProject.properties
val author: String by rootProject.properties
val description: String by rootProject.properties
val source: String by rootProject.properties

group = "yarden_zamir.column_quick_swap"

val gitVersion: Closure<String> by extra
version = gitVersion()

base {
    archivesName = archive_name
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withSourcesJar()
}

repositories {
    mavenCentral()

    maven("https://thedarkcolour.github.io/KotlinForForge/") {
        content { includeGroup("thedarkcolour") }
    }
}

unimined.minecraft {
    version(catalog.versions.minecraft.get())

    mappings {
        searge()
        mojmap()
        parchment(mcVersion = "1.20.1", version = "2023.09.03")

        devFallbackNamespace("searge")
    }

    minecraftForge {
        loader(catalog.versions.forge.get())
    }
}

val minecraftLibraries by configurations

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    compileOnly(catalog.mixin)

    implementation(catalog.kotlin.forge)

    // macOS ARM native bridge for dev environment
    if (System.getProperty("os.name").contains("Mac")) {
        minecraftLibraries("ca.weblite:java-objc-bridge:1.1")
    }
}

kotlin {
    jvmToolchain(17)
}

tasks {
    withType<ProcessResources> {
        val properties = mapOf(
            "id" to id,
            "version" to rootProject.version,
            "group" to rootProject.group,
            "name" to rootProject.name,
            "description" to rootProject.property("description").toString(),
            "author" to rootProject.property("author").toString(),
            "source" to rootProject.property("source").toString()
        )
        from(rootProject.sourceSets.main.get().resources)
        inputs.properties(properties)

        filesMatching(
            listOf(
                "META-INF/mods.toml",
                "META-INF/MANIFEST.MF"
            )
        ) {
            expand(properties)
        }
    }

    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}
