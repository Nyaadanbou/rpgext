plugins {
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.paper-plugins")
}

project.ext.set("name", "Reforge")

group = "cc.mewcraft.reforge"
version = "1.0.0"
description = "Adds item reforge mechanism to various custom items that can be \"reforged\""

dependencies {
    // dependent module
    compileOnly(project(":rpgext:common"))

    // core libs
    compileOnly(project(":mewcore"))

    // server api
    compileOnly(libs.server.paper)

    // libs that present as other plugins
    compileOnly(project(":economy:api"))
    compileOnly(libs.helper)
    compileOnly(libs.mmoitems)
    compileOnly(libs.mythiclib)
    compileOnly(libs.mythicmobs)
}
