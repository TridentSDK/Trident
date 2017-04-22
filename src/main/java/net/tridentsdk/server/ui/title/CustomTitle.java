package net.tridentsdk.server.ui.title;

import lombok.Getter;
import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.ui.title.Title;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * The representation of a title that is sent to the player.
 */
@NotThreadSafe
@Getter
public class CustomTitle implements Title {
    private ChatComponent header;
    private ChatComponent subtitle;
    private int fadeIn = Title.DEFAULT_FADE_IN;
    private int stay = Title.DEFAULT_STAY;
    private int fadeOut = Title.DEFAULT_FADE_OUT;

    @Override
    public Title setHeader(ChatComponent title) {
        this.header = title;
        return this;
    }

    @Override
    public Title setSubtitle(ChatComponent subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    @Override
    public Title setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }

    @Override
    public Title setStay(int stay) {
        this.stay = stay;
        return this;
    }

    @Override
    public Title setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }

    @Override
    public boolean isDefaultFadeTimes() {
        return this.fadeIn == Title.DEFAULT_FADE_IN &&
                this.stay == Title.DEFAULT_STAY &&
                this.fadeOut == Title.DEFAULT_FADE_OUT;
    }
}