object Constants {
    const val GROUP = "dev.crmodders"
    const val MODID = "flux-api"
    const val VERSION = "0.8.0-alpha.1"

    const val TITLE = "Flux API"
    const val DESCRIPTION = "Community focused API for Cosmic Reach Quilt"

    const val VERSION_COSMIC_REACH = "0.3.6"
    const val VERSION_COSMIC_QUILT = "03cc947b041184bc656e170d164ced5bc1477b37"
}

base {
    group = Constants.GROUP
    archivesName = Constants.MODID
    version = Constants.VERSION
}

plugins {
    `java-library`
    `maven-publish`
    id("cosmicloom")
}

java {
    withSourcesJar()
//    withJavadocJar()

    // Sets the Java version
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

loom {
    accessWidenerPath = file("src/main/resources/flux-api.accesswidener")
}

repositories {
    flatDir {
        dirs("lib")
    }
}

dependencies {
    cosmicReach(loom.cosmicReachClient("pre-alpha", Constants.VERSION_COSMIC_REACH))
    modImplementation(loom.cosmicQuilt(Constants.VERSION_COSMIC_QUILT))
    runtimeOnly(":testmod:")

    compileOnly("com.badlogicgames.gdx:gdx:1.12.1")
}

tasks.withType<ProcessResources> {
    // Locations of where to inject the properties
    val resourceTargets = listOf(
        "quilt.mod.json"
    )

    // Left item is the name in the target, right is the variable name
    val replaceProperties = mapOf(
        "mod_group" to Constants.GROUP,
        "mod_id" to Constants.MODID,
        "mod_version" to Constants.VERSION,

        "mod_name" to Constants.TITLE,
        "mod_desc" to Constants.DESCRIPTION,

        "cosmic_reach_version" to Constants.VERSION_COSMIC_REACH,
    )

    inputs.properties(replaceProperties)

    filesMatching(resourceTargets) {
        expand(replaceProperties)
    }
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = Constants.GROUP
            artifactId = Constants.MODID

            from(components["java"])
        }
    }
}
