import net.minecraftforge.gradle.common.util.RunConfig
import net.minecraftforge.gradle.userdev.UserDevExtension

buildscript {
    repositories {
        maven("https://files.minecraftforge.net/maven")
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("net.minecraftforge.gradle:ForgeGradle:3.+")
    }
}

apply {
    plugin("net.minecraftforge.gradle")
}

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    id("org.jlleitschuh.gradle.ktlint")
}

val mod_version: String by project
val mod_author: String by project
val mod_name: String by project
val mod_id: String by project
val mod_group: String by project

val minecraft_version: String by project
val forge_version: String by project
val mappings_version: String by project

version = mod_version
group = mod_group
base.archivesBaseName = mod_name

configure<UserDevExtension> {
    mappings("snapshot", mappings_version)

    fun createRunConfig(name: String, config: RunConfig.() -> Unit) {
        runs.create(name) {
            workingDirectory(project.file("run"))
            properties["forge.logging.markers"] = "SCAN,REGISTRIES,REGISTRYDUMP"
            properties["forge.logging.console.level"] = "debug"
            mods {
                create(mod_id) {
                    source(sourceSets.main.get())
                }
            }

            apply(config)
        }
    }

    createRunConfig("client") {}
    createRunConfig("server") {}
    createRunConfig("data") {
        args("--mod", mod_id, "--all", "--output", file("src/generated/resources/"), "--existing", file("src/main/resources/"))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    "minecraft"("net.minecraftforge:forge:$minecraft_version-$forge_version")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xinline-classes")
            destinationDir = file("$buildDir/classes/java/main")
        }
    }

    jar {
        manifest {
            attributes(
                "Specification-Title" to mod_name,
                "Specification-Vendor" to mod_author,
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to mod_version,
                "Implementation-Vendor" to mod_author
//                    "Implementation-Timestamp" to org.gradle.internal.impldep.org.joda.time.LocalDateTime.now()
            )
        }
    }

    shadowJar {
        minimize()
        archiveClassifier.set("")
        dependencies {
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7"))
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8"))
            include(dependency("org.jetbrains.kotlin:kotlin-reflect"))
            include(dependency("org.jetbrains:annotations"))
        }
    }
}

sourceSets.main {
    resources {
        srcDir("src/generated/resources")
        exclude {
            it.file == file("src/generated/resources/.cache")
        }
    }
}
