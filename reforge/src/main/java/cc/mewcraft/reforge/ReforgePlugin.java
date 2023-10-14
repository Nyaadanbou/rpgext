package cc.mewcraft.reforge;

import cc.mewcraft.reforge.api.ReforgeProvider;
import cc.mewcraft.reforge.command.PluginCommands;
import cc.mewcraft.reforge.hook.ProviderInitializer;
import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.HelperPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.slf4j.Logger;

public class ReforgePlugin extends ExtendedJavaPlugin {
    @Override protected void enable() {
        // Save default config and reload it
        saveDefaultConfig();
        reloadConfig();

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(HelperPlugin.class).toInstance(ReforgePlugin.this);
                bind(ReforgePlugin.class).toInstance(ReforgePlugin.this);
                bind(FileConfiguration.class).toInstance(getConfig());
                bind(Logger.class).toInstance(getSLF4JLogger());
            }
        });

        // Register specific reforge provider
        String specificProvider = getConfig().getString("reforge_provider");
        injector.getInstance(ProviderInitializer.class).initialize(specificProvider);

        // Register commands
        try {
            injector.getInstance(PluginCommands.class).registerCommands();
        } catch (ConfigurationException | ProvisionException e) {
            getSLF4JLogger().error("Failed to initialize command manager", e);
        }
    }

    @Override protected void disable() {
        ReforgeProvider.unregister();
        getSLF4JLogger().info("Unregistered reforge provider");
    }
}
