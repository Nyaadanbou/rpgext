package cc.mewcraft.reforge.hook;

import cc.mewcraft.reforge.api.Reforge;

enum ProviderEnum {
    MMOITEMS("MMOItems", MMOItemsReforge.class),
    ITEMSADDER("ItemsAdder", ItemsAdderReforge.class),
    NOVA("Nova", NovaReforge.class),
    MOCK("Mock", MockReforge.class);

    public final String plugin;
    public final Class<? extends Reforge> clazz;

    ProviderEnum(final String plugin, final Class<? extends Reforge> clazz) {
        this.plugin = plugin;
        this.clazz = clazz;
    }

    public static ProviderEnum match(final String provider) {
        return switch (provider.toLowerCase()) {
            case "mmoitems" -> MMOITEMS;
            case "itemsadder" -> ITEMSADDER;
            case "nova" -> NOVA;
            case "mock" -> MOCK;
            default -> throw new IllegalStateException("Unexpected value: " + provider);
        };
    }
}
