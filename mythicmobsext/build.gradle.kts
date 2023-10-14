import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("cc.mewcraft.deploy-conventions")
    alias(libs.plugins.pluginyml.paper)
}

project.ext.set("name", "MythicMobsExt")

group = "cc.mewcraft.rpgext.mythicmobs"
version = "1.1.0"
description = "An extension of MythicMobs plugin"

dependencies {
    // server
    compileOnly(libs.server.paper)

    // helper
    compileOnly(libs.helper)

    // standalone plugins
    compileOnly(libs.mmoitems)
    compileOnly(libs.mythiclib)
    compileOnly(libs.mythicmobs)

    // internal
    implementation(project(":rpgext:common"))
    implementation(project(":spatula:guice"))
    implementation(project(":spatula:bukkit:command"))
    implementation(project(":spatula:bukkit:message"))
}

paper {
    main = "cc.mewcraft.mythicmobsext.MythicMobsExt"
    name = project.ext.get("name") as String
    version = "${project.version}"
    description = project.description
    apiVersion = "1.19"
    authors = listOf("Nailm")
    serverDependencies {
        register("helper") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("MythicLib") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
        register("MMOItems") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
        register("MythicMobs") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
    }
}
