package cc.mewcraft.mythiclibext.listener;

import cc.mewcraft.mythiclibext.MythicLibExt;
import cc.mewcraft.mythiclibext.object.ItemsAdderStatus;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jetbrains.annotations.NotNull;

@Singleton
public class ItemsAdderListener implements Listener {

    private final @NotNull MythicLibExt plugin;

    @Inject
    public ItemsAdderListener(final @NotNull MythicLibExt plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemsAdderLoad(ItemsAdderLoadDataEvent event) {
        ItemsAdderStatus.markAsComplete();

        // In 99% of times of reloading ItemsAdder,
        // we don't add/remove items from ItemsAdder.

        // So we only auto reload MMOItems for the first event.
        // (Usually the first event is fired at server startup)

        if (ItemsAdderStatus.completeCount() == 1) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mmoitems reload all");
        }
    }

}
