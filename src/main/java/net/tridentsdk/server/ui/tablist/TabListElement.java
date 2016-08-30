package net.tridentsdk.server.ui.tablist;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.world.opt.GameMode;

import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class TabListElement {

    private final UUID uuid;
    private String name = "";
    private GameMode gameMode = GameMode.SURVIVAL;
    private int ping = 0;
    private ChatComponent displayName;
    private boolean blank;
    private List<PlayerProperty> properties;

    @Data
    @RequiredArgsConstructor
    public static class PlayerProperty {

        private final String name;
        @NonNull
        private String value;
        private String signature;

    }

}
