package cc.mewcraft.reforge.gui;

import cc.mewcraft.reforge.gui.config.ReforgeConfigDirectory;
import cc.mewcraft.reforge.gui.menu.MenuConfig;
import cc.mewcraft.spatula.message.Translations;
import com.google.inject.AbstractModule;
import org.bukkit.configuration.file.FileConfiguration;
import org.slf4j.Logger;

import java.nio.file.Path;

import javax.inject.Singleton;

public class BasicBindings extends AbstractModule {
    private final ReforgePlugin plugin;

    public BasicBindings(final ReforgePlugin plugin) {
        this.plugin = plugin;
    }

    @Override protected void configure() {
        bind(ReforgePlugin.class).toInstance(plugin);
        bind(Logger.class).toInstance(plugin.getSLF4JLogger());
        bind(Translations.class).toProvider(() ->
                new Translations(plugin, "lang/message")
        ).in(Singleton.class);
        bind(Path.class).annotatedWith(ReforgeConfigDirectory.class).toProvider(() ->
                plugin.getDataFolder().toPath().resolve("item")
        ).in(Singleton.class);
        bind(FileConfiguration.class).annotatedWith(MenuConfig.class).toProvider(
                plugin::getConfig
        );
    }
}
