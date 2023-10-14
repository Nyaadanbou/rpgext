package cc.mewcraft.reforge.gui.ingredient;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ReforgeResult implements Result {
    public static final Result SILENT_SUCCESS = new ReforgeResult(State.SUCCESS, null);
    public static final Result FATAL_FAILURE = new ReforgeResult(State.FAILURE, Component.text("An internal error occurred, please report this issue to Mewcraft forum").color(NamedTextColor.RED));

    private final @NotNull State state;
    private final @Nullable Component message;

    public ReforgeResult(
            final @NotNull State state,
            final @Nullable Component message
    ) {
        this.state = state;
        this.message = message;
    }

    @Override public void sendResult(final @NotNull Audience audience) {
        if (message != null) {
            audience.sendMessage(message);
        }
    }

    @Override public @NotNull State state() {
        return state;
    }
}
