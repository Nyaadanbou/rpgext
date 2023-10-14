package cc.mewcraft.reforge.gui.menu;

import cc.mewcraft.reforge.api.ReforgeProvider;
import cc.mewcraft.reforge.gui.config.ReforgeManager;
import cc.mewcraft.reforge.gui.ingredient.Ingredient;
import cc.mewcraft.reforge.gui.ingredient.IngredientParams;
import cc.mewcraft.reforge.gui.ingredient.Result;
import cc.mewcraft.spatula.message.Translations;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.intellij.lang.annotations.Subst;
import org.slf4j.Logger;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static cc.mewcraft.reforge.gui.util.AdventureUtils.translatable;

public class ReforgeMenu {
    private final Logger logger;
    private final FileConfiguration config;
    private final Translations translations;
    private final ReforgeManager manager;

    private final Window.Builder.Normal.Single window;

    private final VirtualInventory transformInventory; // Items in this inventory will be transformed
    private final VirtualInventory ingredientInventory; // Items in this inventory will be consumed for the transformation
    private final VirtualInventory outputInventory; // Transformed items will be put in this inventory

    @Inject
    public ReforgeMenu(
            final Logger logger,
            @MenuConfig final FileConfiguration config,
            final Translations translations,
            final ReforgeManager manager
    ) {
        this.logger = logger;
        this.config = config;
        this.translations = translations;
        this.manager = manager;

        // Initialize inventories
        this.transformInventory = new VirtualInventory(1);
        this.ingredientInventory = new VirtualInventory(10);
        this.outputInventory = new VirtualInventory(1);

        // Set GUI priorities so that items go to transformInventory first when shift-click moving items
        transformInventory.setGuiPriority(2);
        ingredientInventory.setGuiPriority(1);

        // Stop viewer adding items to outputInventory
        outputInventory.setPreUpdateHandler(event -> {
            if (!event.isRemove()) event.setCancelled(true);
        });

        // Stop viewer adding items to transformInventory if outputInventory is not empty
        transformInventory.setPreUpdateHandler(event -> {
            if (!outputInventory.isEmpty()) event.setCancelled(true);
        });

        String[] layout = config.getStringList("gui.layout").toArray(String[]::new);
        SimpleItem background = new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(translatable("menu.reforge.background.name")));

        // Backed GUI
        Gui gui = Gui.normal()
                .setStructure(layout)
                .addIngredient('#', background)
                .addIngredient('i', transformInventory)
                .addIngredient('u', ingredientInventory)
                .addIngredient('o', outputInventory)
                .addIngredient('c', new ReforgeItem())
                .build();

        this.window = Window.single()
                .setTitle(translatable("menu.reforge.title"))
                .setGui(gui);
    }

    public void open(Player viewer) {
        window.addOpenHandler(() -> {
            // Play sound upon opening
            @Subst("minecraft:entity.villager.trade")
            String string = Objects.requireNonNull(config.getString("reforge_sound.start"));
            viewer.playSound(Sound.sound(Key.key(string), Sound.Source.MASTER, 1f, 1f));
        });

        window.addCloseHandler(() -> {
            // Return all items to player inventory if window is closed
            PlayerInventory playerInventory = viewer.getInventory();
            returnItems(playerInventory, transformInventory.getItems());
            returnItems(playerInventory, ingredientInventory.getItems());
            returnItems(playerInventory, outputInventory.getItems());
        });

        window.open(viewer);
    }

    private void returnItems(@NotNull PlayerInventory playerInventory, @Nullable ItemStack @NotNull ... items) {
        for (final ItemStack item : items)
            if (item != null) {
                playerInventory.addItem(item);
            }
    }

    private class ReforgeItem extends AbstractItem {
        @Override public ItemProvider getItemProvider() {
            return new ItemBuilder(Material.ANVIL).setDisplayName(translatable("menu.reforge.start"));
        }

        @Override public void handleClick(final @NotNull ClickType clickType, final @NotNull Player player, final @NotNull InventoryClickEvent event) {
            // TODO add click anti-spam

            if (clickType.isLeftClick()) {
                // Check if player has put the input item
                ItemStack item = transformInventory.getItem(0);
                if (item == null) {
                    translations.of("msg_empty_transform_slot").send(player);
                    return;
                }

                // Check if the input item is reforgeable
                if (!manager.isReforgeable(item)) {
                    translations.of("msg_none_reforge_recipe")
                            .resolver(Placeholder.component("item", item.displayName()))
                            .send(player);
                    return;
                }

                // Check required ingredients
                Ingredient ingredient = manager.getIngredient(item);
                IngredientParams params = new IngredientParams(player, ingredient, ingredientInventory);
                Result checkResult = ingredient.check(params);
                if (checkResult.state() == Result.State.FAILURE) {
                    checkResult.sendResult(player);
                    return; // Check failed - return immediately
                }

                // Consume required ingredients
                Result consumeResult = ingredient.consume(params);
                if (consumeResult.state() == Result.State.FAILURE) {
                    checkResult.sendResult(player);
                    return; // Consume failed - usually it indicates an error!
                }

                // Ingredients are consumed - refresh the ingredient inventory
                ingredientInventory.notifyWindows();

                // Required ingredients are checked and consumed - let's reforge the input item
                String option = Objects.requireNonNull(config.getString("reforge_option"));
                Optional<ItemStack> optional = ReforgeProvider.get().transform(item, option);
                if (optional.isEmpty()) {
                    player.sendRichMessage("<red>Reforge failed due to an internal error");
                    logger.error("An internal error occurred on reforge");
                    return;
                }

                // Set contents of transform/output inventories
                transformInventory.setItemSilently(0, null);
                outputInventory.setItemSilently(0, optional.get());

                // Play sound when done
                @Subst("minecraft:entity.villager.trade")
                String string = Objects.requireNonNull(config.getString("reforge_sound.done"));
                player.playSound(Sound.sound(Key.key(string), Sound.Source.MASTER, 1f, 1f));
            }
        }
    }
}
