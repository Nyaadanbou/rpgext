package cc.mewcraft.reforge.gui.ingredient;

import cc.mewcraft.spatula.item.PluginItem;
import cc.mewcraft.spatula.message.Translations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.inventory.Inventory;

import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

public class ItemStackIngredient extends Ingredient {
    final PluginItem<?> item; // Backed by PluginItem
    final int amount; // Required amount (maximum can be greater than 64)

    public ItemStackIngredient(
            final Translations translations,
            final PluginItem<?> item,
            final int amount
    ) {
        super(translations);
        this.item = item;
        this.amount = amount;
    }

    public ItemStackIngredient(
            final Translations translations,
            final PluginItem<?> item
    ) {
        this(translations, item, 1);
    }

    @Override public @NotNull Result check(@NotNull final IngredientParams params) {
        int leftOver = amount; // Remaining amount to check
        for (final ItemStack i : params.ingredientInventory().getUnsafeItems()) {
            if (leftOver <= 0) {
                return checkNext(params); // OK, pass to next
            }
            if (i != null && item.matches(i)) {
                leftOver -= i.getAmount();
            }
        }

        // Check failed
        Player player = params.player();
        Set<Ingredient> ingredients = findSameType();
        return new ReforgeResult(
                Result.State.FAILURE,
                translations.of("msg_insufficient_items_ingredient")
                        .resolver(
                                Placeholder.component(
                                        // Pass in tag name
                                        "item",
                                        // Pass in tag content
                                        Component.join(
                                                // Pass in separator
                                                JoinConfiguration.separator(translations.of("msg_item_list_separator").locale(player).component()),
                                                // Pass in ingredient list
                                                ingredients.stream().map(ItemStackIngredient.class::cast).map(item -> {
                                                    ItemStack itemStack = Objects.requireNonNull(item.item.createItemStack());
                                                    return translations.of("msg_item_display_format").resolver(
                                                            Placeholder.component("item", itemStack.displayName()),
                                                            Placeholder.component("amount", Component.text(item.amount))
                                                    ).locale(player).component();
                                                }).toList()
                                        )
                                )
                        ).component()
        );
    }

    @Override public @NotNull Result consume(@NotNull final IngredientParams params) {
        Inventory inventory = params.ingredientInventory();
        int leftOver = amount; // Remaining amount to consume
        ItemStack[] items = inventory.getUnsafeItems();
        for (int i = 0; i < items.length; i++) {
            if (leftOver == 0) {
                return consumeNext(params); // OK, pass to next
            }
            ItemStack ui = items[i];
            if (ui != null && item.matches(ui)) {
                int subtract = Math.min(leftOver, ui.getAmount());
                if (ui.subtract(subtract).getAmount() == 0) {
                    inventory.setItemSilently(i, null);
                }
                leftOver -= subtract;
            }
        }

        // Consume failed
        return ReforgeResult.FATAL_FAILURE;
    }
}
