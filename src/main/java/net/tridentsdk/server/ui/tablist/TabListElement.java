package net.tridentsdk.server.ui.tablist;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.world.opt.GameMode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A tab list element.
 */
@Data
public class TabListElement {
    /**
     * The UUID of the player at this tab list element
     */
    private final UUID uuid;
    /**
     * The game mode
     */
    private volatile GameMode gameMode;
    /**
     * The name of the player
     */
    private volatile String name;
    /**
     * The player's ping
     */
    private volatile int ping = 0;
    /**
     * The value that is displayed on the scoreboard
     */
    private volatile ChatComponent displayName;
    /**
     * Whether or not this element is blank/empty
     */
    private volatile boolean blank;
    /**
     * The tab list properties (such as skin textures)
     */
    private volatile List<PlayerProperty> properties;

    /**
     * Creates a new tab list element which can be filled
     * with custom fields.
     *
     * @param uuid the uuid for the element
     */
    public TabListElement(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Creates a new tab list element which is filled with
     * data from the player's properties.
     *
     * @param player the player to fill the element
     */
    public TabListElement(TridentPlayer player) {
        this.uuid = player.uuid();
        this.name = player.name();
        this.gameMode = player.getGameMode();

        String textures = player.getTextures();
        if (textures != null) {
            this.properties = new ArrayList<>();
            this.properties.add(new TabListElement.PlayerProperty("textures", textures));
        }
    }

    @Data
    @RequiredArgsConstructor
    public static class PlayerProperty {
        private final String name;
        @NonNull
        private String value;
        private String signature;
    }
}