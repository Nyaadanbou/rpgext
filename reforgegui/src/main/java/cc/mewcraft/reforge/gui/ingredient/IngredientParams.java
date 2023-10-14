package cc.mewcraft.reforge.gui.ingredient;

import org.bukkit.entity.Player;
import xyz.xenondevs.invui.inventory.Inventory;

public record IngredientParams(
        Player player,
        Ingredient ingredient,
        Inventory ingredientInventory
) {

}
