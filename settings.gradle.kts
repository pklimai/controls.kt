rootProject.name = "controls-kt"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {

    val toolsVersion: String by extra

    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        maven("https://repo.kotlin.link")
    }

    plugins {
        id("ru.mipt.npm.gradle.project") version toolsVersion
        id("ru.mipt.npm.gradle.mpp") version toolsVersion
        id("ru.mipt.npm.gradle.jvm") version toolsVersion
        id("ru.mipt.npm.gradle.js") version toolsVersion
    }
}

dependencyResolutionManagement {

    val toolsVersion: String by extra

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.kotlin.link")
    }

    versionCatalogs {
        create("npmlibs") {
            from("ru.mipt.npm:version-catalog:$toolsVersion")
        }
    }
}

include(
    ":controls-core",
    ":controls-tcp",
    ":controls-serial",
    ":controls-server",
    ":controls-opcua",
//    ":controls-mongo",
    ":controls-storage",
    ":controls-storage:controls-xodus",
    ":magix",
    ":magix:magix-api",
    ":magix:magix-server",
    ":magix:magix-rsocket",
    ":magix:magix-java-client",
    ":magix:magix-zmq",
    ":magix:magix-rabbit",

//    ":magix:magix-storage",
    ":magix:magix-storage:magix-storage-xodus",
    ":controls-magix-client",
    ":demo:all-things",
    ":demo:magix-demo",
    ":demo:car",
    ":demo:motors",
    ":demo:echo"
)
