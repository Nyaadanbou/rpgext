package cc.mewcraft.reforge.hook;

import cc.mewcraft.reforge.api.Reforge;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public class NovaReforge implements Reforge {
    @Override public Optional<ItemStack> transform(@NotNull final ItemStack item, @NotNull final String optionKey) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
