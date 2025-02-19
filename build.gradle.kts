import space.kscience.gradle.isInDevelopment
import space.kscience.gradle.useApache2Licence
import space.kscience.gradle.useSPCTeam

plugins {
    id("space.kscience.gradle.project")
}

val dataforgeVersion: String by extra("0.6.1")
val ktorVersion: String by extra(space.kscience.gradle.KScienceVersions.ktorVersion)
val rsocketVersion by extra("0.15.4")
val xodusVersion by extra("2.0.1")

allprojects {
    group = "space.kscience"
    version = "0.2.0-dev-1"
    repositories{
        maven("https://maven.pkg.jetbrains.space/spc/p/sci/dev")
    }
}

ksciencePublish {
    pom("https://github.com/SciProgCentre/controls.kt") {
        useApache2Licence()
        useSPCTeam()
    }
    github("controls.kt", "SciProgCentre")
    space(
        if (isInDevelopment) {
            "https://maven.pkg.jetbrains.space/spc/p/sci/dev"
        } else {
            "https://maven.pkg.jetbrains.space/spc/p/sci/release"
        }
    )
    space("https://maven.pkg.jetbrains.space/spc/p/controls/maven")
}

readme.readmeTemplate = file("docs/templates/README-TEMPLATE.md")