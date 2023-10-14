import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("cc.mewcraft.deploy-conventions")
    alias(libs.plugins.pluginyml.paper)
}

project.ext.set("name", "MythicLibExt")

group = "cc.mewcraft.rpgext.mythiclib"
version = "1.0.0"
description = "An extension of MythicLib plugin"

dependencies {
    // server
    compileOnly(libs.server.paper)

    // helper
    compileOnly(libs.helper)

    // standalone plugins
    compileOnly(libs.mythiclib)
    compileOnly(libs.itemsadder)

    // internal
    implementation(project(":rpgext:common"))
    implementation(project(":spatula:guice"))
}

paper {
    main = "cc.mewcraft.mythiclibext.MythicLibExt"
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
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.AFTER // MMOItems depends on this plugin
        }
        register("ItemsAdder") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
    }
}
