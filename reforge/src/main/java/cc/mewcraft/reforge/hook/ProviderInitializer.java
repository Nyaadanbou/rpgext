package cc.mewcraft.reforge.hook;

import cc.mewcraft.reforge.api.ReforgeProvider;
import com.google.inject.Injector;
import me.lucko.helper.plugin.HelperPlugin;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ProviderInitializer {

    private final Logger logger;
    private final HelperPlugin plugin;
    private final Injector injector;

    @Inject
    public ProviderInitializer(
            final Logger logger,
            final HelperPlugin plugin,
            final Injector injector
    ) {
        this.logger = logger;
        this.plugin = plugin;
        this.injector = injector;
    }

    /**
     * Initializes {@link cc.mewcraft.reforge.api.ReforgeProvider} by the specific provider key.
     * <p>
     * If the specific provider cannot be initialized, it will fall back to {@link MockReforge}.
     *
     * @param provider the provider key
     */
    public void initialize(String provider) {
        ProviderEnum match = ProviderEnum.match(provider);

        if (plugin.isPluginPresent(match.plugin)) {
            ReforgeProvider.register(injector.getInstance(match.clazz));
            logger.info("Registered reforge provider: {}", match.clazz.getSimpleName());
        } else {
            logger.warn("Specific reforge provider cannot be registered: {}", provider);
            ReforgeProvider.register(injector.getInstance(MockReforge.class));
            logger.warn("Fall back to default provider: {}", MockReforge.class.getSimpleName());
        }
    }
}
