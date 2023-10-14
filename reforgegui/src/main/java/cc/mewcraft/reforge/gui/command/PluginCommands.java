package cc.mewcraft.reforge.gui.command;

import cc.mewcraft.reforge.gui.ReforgePlugin;
import cc.mewcraft.reforge.gui.menu.ReforgeMenu;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import com.google.inject.Injector;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PluginCommands {
    private final Injector injector;
    private final ReforgePlugin plugin;
    private final CommandRegistry registry;

    @Inject
    public PluginCommands(
            final Injector injector,
            final ReforgePlugin plugin,
            final CommandRegistry registry
    ) {
        this.injector = injector;
        this.plugin = plugin;
        this.registry = registry;
    }

    public void registerCommands() {
        // Prepare commands
        registry.prepareCommand(registry
                .commandBuilder("reforgegui")
                .literal("open")
                .argument(PlayerArgument.optional("target"))
                .permission("reforgegui.command.open")
                .handler(ctx -> {
                    ReforgeMenu menu = injector.getInstance(ReforgeMenu.class);
                    if (ctx.contains("target")) {
                        Player target = ctx.get("target");
                        menu.open(target);
                    } else if (ctx.getSender() instanceof Player player) {
                        menu.open(player);
                    }
                }).build());
        registry.prepareCommand(registry
                .commandBuilder("reforgegui")
                .literal("reload")
                .permission("reforgegui.command.reload")
                .handler(ctx -> {
                    plugin.onDisable();
                    plugin.onEnable();
                }).build());

        // Register commands
        registry.registerCommands();
    }
}
