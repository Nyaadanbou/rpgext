import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("cc.mewcraft.deploy-conventions")
    alias(libs.plugins.pluginyml.paper)
}

project.ext.set("name", "Reforge")

group = "cc.mewcraft.reforge"
version = "1.0.0"
description = "Adds item reforge mechanism to various custom items that can be \"reforged\""

dependencies {
    // server
    compileOnly(libs.server.paper)

    // helper
    compileOnly(libs.helper)

    // standalone plugins
    compileOnly(project(":economy:api"))
    compileOnly(libs.mmoitems)
    compileOnly(libs.mythiclib)
    compileOnly(libs.mythicmobs)

    // internal
    implementation(project(":rpgext:common"))
    implementation(project(":spatula:guice"))
    implementation(project(":spatula:bukkit:command"))
}

paper {
    main = "cc.mewcraft.reforge.ReforgePlugin"
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
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
        register("MMOItems") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
        register("ItemsAdder") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
        register("Nova") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
    }
}
