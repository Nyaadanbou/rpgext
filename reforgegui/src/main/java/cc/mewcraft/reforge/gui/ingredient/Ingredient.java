package cc.mewcraft.reforge.gui.ingredient;

import cc.mewcraft.spatula.message.Translations;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;

import java.util.LinkedList;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Proof of concept.
 * <p>
 * Represents a single ingredient that will be checked and consumed upon reforging.
 * <p>
 * By "a single ingredient", we mean a single entry of the ingredient list in the config file.
 * For example, the following ingredient list has <i>three</i> ingredient entries in total:
 *
 * <ul>
 *     <li>Entry 1: {@code minecraft:diamond/2}</li>
 *     <li>Entry 2: {@code itemsadder:iasurvival:ruby}</li>
 *     <li>Entry 3: {@code $150R}</li>
 * </ul>
 * <p>
 * The entry 1 is an ItemStack of vanilla;
 * The entry 2 is an ItemStack of ItemsAdder;
 * The entry 3 is 150 currencies of R.
 */
public abstract class Ingredient {
    /**
     * The translations instance is used to send feedback to the user.
     */
    protected final @NotNull Translations translations;

    /**
     * Last ingredient on the chain of responsibility.
     */
    private @Nullable Ingredient last;
    /**
     * Next ingredient on the chain of responsibility.
     */
    private @Nullable Ingredient next;

    public static Ingredient chain(@NotNull Ingredient first, @NotNull Ingredient... chain) {
        Ingredient head = first;
        for (final Ingredient nextInChain : chain) {
            head.next = nextInChain;
            nextInChain.last = head;
            head = nextInChain;
        }
        return head;
    }

    public static Ingredient chain(@NotNull LinkedList<Ingredient> list) {
        Ingredient head = list.removeFirst();
        for (final Ingredient nextInChain : list) {
            head.next = nextInChain;
            nextInChain.last = head;
            head = nextInChain;
        }
        return head;
    }

    Ingredient(final @NotNull Translations translations) {
        this.translations = translations;
    }

    public abstract @NotNull Result check(@NotNull IngredientParams params);

    public abstract @NotNull Result consume(@NotNull IngredientParams params);

    protected @NotNull Result checkNext(@NotNull IngredientParams params) {
        if (next == null) {
            return ReforgeResult.SILENT_SUCCESS;
        }
        return next.check(params);
    }

    protected @NotNull Result consumeNext(@NotNull IngredientParams params) {
        if (next == null) {
            return ReforgeResult.SILENT_SUCCESS;
        }
        return next.consume(params);
    }

    /**
     * Finds all the {@link Ingredient} instances, which are the same type
     * as the runtime type of this instance, along the whole chain.
     *
     * @return a set of {@link Ingredient} instances along the chain
     */
    protected @NotNull Set<Ingredient> findSameType() {
        Set<Ingredient> collected = ReferenceArraySet.of(this); // initialized with `this` included

        Ingredient ptr;

        // Find towards head
        ptr = this;
        while ((ptr = ptr.last) != null) {
            if (getClass().isInstance(ptr))
                collected.add(ptr);
        }

        // Find towards tail
        ptr = this;
        while ((ptr = ptr.next) != null) {
            if (getClass().isInstance(ptr))
                collected.add(ptr);
        }

        return collected;
    }
}
