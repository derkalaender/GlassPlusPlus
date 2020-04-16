val mod_name: String by settings
rootProject.name = mod_name

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }

    val kotlin_version: String by settings
    val shadow_version: String by settings
    val ktlint_version: String by settings
    plugins {
        kotlin("jvm") version kotlin_version
        id("com.github.johnrengelman.shadow") version shadow_version
        id("org.jlleitschuh.gradle.ktlint") version ktlint_version
    }
}
