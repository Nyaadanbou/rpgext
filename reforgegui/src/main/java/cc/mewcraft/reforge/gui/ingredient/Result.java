package cc.mewcraft.reforge.gui.ingredient;

import net.kyori.adventure.audience.Audience;

public interface Result {
    State state();
    void sendResult(Audience audience);

    enum State {
        SUCCESS,
        FAILURE
    }
}
