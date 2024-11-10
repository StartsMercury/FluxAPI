buildscript {
    repositories {
        maven {
            name = "JitPack"
            url = uri("https://jitpack.io")
        }
        mavenCentral()
    }

    dependencies {
        classpath(
            group = "org.codeberg.CRModders",
            name = "cosmic-loom",
            version = "PR7-SNAPSHOT",
        )
    }
}

rootProject.name = "Flux API"
