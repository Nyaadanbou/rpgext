import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("cc.mewcraft.deploy-conventions")
    alias(libs.plugins.pluginyml.paper)
}

project.ext.set("name", "ReforgeGui")

group = "cc.mewcraft.reforge.gui"
version = "1.0.1"
description = "Provides GUIs for the item reforge mechanism"

dependencies {
    // server
    compileOnly(libs.server.paper)

    // helper
    compileOnly(libs.helper)

    // standalone plugins
    compileOnly(project(":economy:api"))
    compileOnly(project(":rpgext:reforge"))

    // internal
    implementation(project(":rpgext:common"))
    implementation(project(":spatula:guice"))
    implementation(project(":spatula:bukkit:gui"))
    implementation(project(":spatula:bukkit:command"))
    implementation(project(":spatula:bukkit:message"))
    implementation(project(":spatula:bukkit:item:api"))
}

paper {
    main = "cc.mewcraft.reforge.gui.ReforgePlugin"
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
        register("Reforge") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
        register("GemsEconomy") {
            required = true // TODO make it optional
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
    }
}
