package cc.mewcraft.reforge.gui.config;

import cc.mewcraft.reforge.gui.ingredient.CurrencyIngredient;
import cc.mewcraft.reforge.gui.ingredient.Ingredient;
import cc.mewcraft.reforge.gui.ingredient.ItemStackIngredient;
import cc.mewcraft.spatula.item.PluginItem;
import cc.mewcraft.spatula.item.PluginItemRegistry;
import cc.mewcraft.spatula.message.Translations;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jetbrains.annotations.NotNull;

@Singleton
public class ReforgeManager {
    private static final Pattern CURRENCY_PATTERN = Pattern.compile("(\\$)(\\d*\\.?\\d*)(\\D+)");

    private final @NotNull Logger logger;
    private final @NotNull Translations translations;
    private final @NotNull Path reforgeConfigDirectory;
    private final @NotNull Map<String, Ingredient> ingredientMap;

    @Inject
    public ReforgeManager(
            final @NotNull Logger logger,
            final @NotNull Translations translations,
            @ReforgeConfigDirectory final @NotNull Path reforgeConfigDirectory
    ) {
        this.logger = logger;
        this.translations = translations;
        this.reforgeConfigDirectory = reforgeConfigDirectory;

        // Load all reforge config files
        this.ingredientMap = loadFiles();
    }

    /**
     * Checks if the specific item stack is reforgeable.
     *
     * @param test item to be checked
     * @return true if the specific item is reforgeable, otherwise false
     */
    public boolean isReforgeable(final @NotNull ItemStack test) {
        PluginItem<?> item = PluginItemRegistry.INSTANCE.byItemStackOrNull(test);
        return item != null && ingredientMap.containsKey(item.getReference());
    }

    /**
     * Gets the reforge {@link Ingredient} of the specific item stack.
     * <p>
     * If the item stack does not have an {@link Ingredient} defined in the config,
     * this method will throw a {@link IllegalStateException}. In all cases, you should first use
     * {@link #isReforgeable(ItemStack)} to check the item stack before calling this method.
     *
     * @param key an item
     * @return an {@link Ingredient} of the specific item
     */
    public @NotNull Ingredient getIngredient(final @NotNull ItemStack key) {
        PluginItem<?> item = PluginItemRegistry.INSTANCE.byItemStack(key);
        return ingredientMap.get(item.getReference());
    }

    private @NotNull Map<String, Ingredient> loadFiles() {
        Map<String, Ingredient> ingredientMap = new HashMap<>();

        Collection<File> files;
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + "*.yml");
        try (Stream<Path> stream = Files.walk(reforgeConfigDirectory)) {
            files = stream.filter(pathMatcher::matches).map(Path::toFile).toList();
        } catch (IOException e) {
            logger.error("Failed to load reforge ingredient config files. Nothing will be reforgeable", e);
            return Collections.emptyMap();
        }

        // Loop through each file in the directory and deserialize them all
        for (final File file : files) {
            YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(file);
            for (final String consumer /* key of reforgeable item */ : yamlConfig.getKeys(false)) {
                PluginItem<?> pi = PluginItemRegistry.INSTANCE.byReferenceOrNull(consumer);
                if (pi == null) {
                    logger.error("Unknown reforgeable item from key: {}, skipped", consumer);
                    continue;
                }
                List<String> rawValues = yamlConfig.getStringList(consumer);
                Ingredient ingredient = deserializeIngredient(rawValues);
                ingredientMap.put(consumer, ingredient);
            }
        }

        return ingredientMap;
    }

    // TODO better use a chain of responsibility for this
    private @NotNull Ingredient deserializeIngredient(
            final @NotNull List<String> rawValues
    ) {
        LinkedList<Ingredient> collected = new LinkedList<>();

        for (final String rawValue : rawValues) {
            if (rawValue.startsWith("$")) {
                // It's an economy currency ingredient

                Matcher matcher = CURRENCY_PATTERN.matcher(rawValue);
                if (!matcher.matches()) {
                    logger.warn("Illegal currency ingredient: {}, skipped", rawValue);
                    continue;
                }
                double amount = Double.parseDouble(matcher.group(2));
                String identifier = matcher.group(3);
                collected.add(new CurrencyIngredient(translations, identifier, amount));

            } else {
                // It's an item stack ingredient

                String[] parts = rawValue.split("/");
                if (parts.length == 2) {
                    PluginItem<?> item = PluginItemRegistry.INSTANCE.byReferenceOrNull(parts[0]);
                    int amount = Integer.parseInt(parts[1]);
                    collected.add(new ItemStackIngredient(translations, item, amount));
                } else if (parts.length == 1) {
                    PluginItem<?> item = PluginItemRegistry.INSTANCE.byReferenceOrNull(parts[0]);
                    collected.add(new ItemStackIngredient(translations, item));
                } else {
                    logger.warn("Illegal item stack ingredient: {}, skipped", rawValue);
                }

            }
        }

        return Ingredient.chain(collected);
    }
}
