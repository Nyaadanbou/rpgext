package cc.mewcraft.mythicmobsext;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jetbrains.annotations.NotNull;

@Singleton
public class MythicMobsConfig {

    private final @NotNull MythicMobsExt plugin;

    @Inject
    public MythicMobsConfig(final @NotNull MythicMobsExt plugin) {
        this.plugin = plugin;
    }

    public String getDamageFormat() {
        return plugin.getConfig().getString("damage.format");
    }

}
