package cc.mewcraft.reforge.gui.ingredient;

import org.jetbrains.annotations.Nullable;

public interface IngredientSerializer {
    // TODO implement it
    @Nullable Ingredient deserialize(String value);
}
