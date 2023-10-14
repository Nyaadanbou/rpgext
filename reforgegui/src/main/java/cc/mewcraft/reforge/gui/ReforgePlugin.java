package cc.mewcraft.reforge.gui;

import cc.mewcraft.reforge.gui.command.PluginCommands;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;
import xyz.xenondevs.inventoryaccess.component.i18n.AdventureComponentLocalizer;
import xyz.xenondevs.inventoryaccess.component.i18n.Languages;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ReforgePlugin extends ExtendedJavaPlugin {
    @Override protected void enable() {
        saveResourceRecursively("lang");
        saveResourceRecursively("item");
        saveDefaultConfig();
        reloadConfig();

        // Configure dependency injector
        Injector injector = Guice.createInjector(new BasicBindings(this));

        // Initialize translations for InvUI
        try {
            Languages.getInstance().loadLanguage(
                    "zh_cn",
                    getDataFolder().toPath().resolve("lang").resolve("modding").resolve("zh_cn.json").toFile(),
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            logger().error("Failed to load language files", e);
        }

        // Add support of MiniMessage in InvUI localization config
        AdventureComponentLocalizer.getInstance().setComponentCreator(MiniMessage.miniMessage()::deserialize);

        // Register commands (this is the entry point of this plugin)
        try {
            injector.getInstance(PluginCommands.class).registerCommands();
        } catch (ConfigurationException | ProvisionException e) {
            logger().error("Failed to register commands", e);
        }
    }

    private Logger logger() { // Convenience function to get slf4j logger
        return getSLF4JLogger();
    }
}
