package cc.mewcraft.mythiclibext;

import cc.mewcraft.mythiclibext.filter.ItemsAdderFilter;
import cc.mewcraft.mythiclibext.listener.ItemsAdderListener;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.lumine.mythic.lib.api.crafting.uimanager.UIFilterManager;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

public class MythicLibExt extends ExtendedJavaPlugin {
    @Override protected void enable() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(MythicLibExt.class).toInstance(MythicLibExt.this);
            }
        });

        // Register listeners
        registerListener(injector.getInstance(ItemsAdderListener.class));

        // Register ItemsAdder filter
        UIFilterManager.registerUIFilter(injector.getInstance(ItemsAdderFilter.class));
    }
}
