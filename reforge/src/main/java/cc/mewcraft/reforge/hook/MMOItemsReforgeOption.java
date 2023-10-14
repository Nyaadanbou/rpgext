package cc.mewcraft.reforge.hook;

import cc.mewcraft.reforge.api.ReforgeOption;
import net.Indyuce.mmoitems.api.ReforgeOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jetbrains.annotations.Nullable;

@Singleton
public class MMOItemsReforgeOption implements ReforgeOption {
    private final Map<String, ReforgeOptions> reforgeOptions;

    @Inject
    public MMOItemsReforgeOption(final FileConfiguration fileConfig) {
        this.reforgeOptions = new HashMap<>();

        // Load options from config files
        ConfigurationSection section = Objects.requireNonNull(fileConfig.getConfigurationSection("reforge_options.mmoitems"));
        for (final String key : section.getKeys(false)) {
            String optionData = Objects.requireNonNull(section.getString(key));
            boolean[] mask = new boolean[12];
            for (final String n : optionData.split(",")) {
                int set = Integer.parseInt(String.valueOf(n));
                mask[set - 1] = true;
            }
            reforgeOptions.put(key, new ReforgeOptions(mask));
        }
    }

    public @Nullable ReforgeOptions parse(String optionKey) {
        return reforgeOptions.get(optionKey);
    }
}
