package net.tridentsdk.server.ui.title;

import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.ui.title.Title;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CustomTitle implements Title {
    private AtomicReference<ChatComponent> title = new AtomicReference<>();
    private AtomicReference<ChatComponent> subtitle = new AtomicReference<>();
    private AtomicInteger fadeIn = new AtomicInteger(10);
    private AtomicInteger stay = new AtomicInteger(70);
    private AtomicInteger fadeOut = new AtomicInteger(20);
    private AtomicBoolean defaultTimings = new AtomicBoolean(true);

    @Override
    public ChatComponent getTitle() {
        return this.title.get();
    }

    @Override
    public Title setTitle(ChatComponent title) {
        ChatComponent old;

        while(true) {
            old = this.title.get();
            if (title != null && !title.equals(old)) {
                if (this.title.compareAndSet(old, title)) {
                    break;
                }
            } else {
                break;
            }
        }

        return this;
    }

    @Override
    public ChatComponent getSubtitle() {
        return this.subtitle.get();
    }

    @Override
    public Title setSubtitle(ChatComponent subtitle) {
        ChatComponent old;

        while(true) {
            old = this.subtitle.get();
            if (subtitle != null && !subtitle.equals(old)) {
                if (this.subtitle.compareAndSet(old, subtitle)) {
                    break;
                }
            } else {
                break;
            }
        }

        return this;
    }

    @Override
    public int getFadeIn() {
        return this.fadeIn.get();
    }

    @Override
    public Title setFadeIn(int fadeIn) {
        int old;

        while(true) {
            old = this.fadeIn.get();
            if (fadeIn != old) {
                if (this.fadeIn.compareAndSet(old, fadeIn)) {
                    this.defaultTimings.set(false);
                    break;
                }
            } else {
                break;
            }
        }

        return this;
    }

    @Override
    public int getStay() {
        return this.stay.get();
    }

    @Override
    public Title setStay(int stay) {
        int old;

        while(true) {
            old = this.stay.get();
            if (stay != old) {
                if (this.stay.compareAndSet(old, stay)) {
                    this.defaultTimings.set(false);
                    break;
                }
            } else {
                break;
            }
        }

        return this;
    }

    @Override
    public int getFadeOut() {
        return this.fadeOut.get();
    }

    @Override
    public Title setFadeOut(int fadeOut) {
        int old;

        while(true) {
            old = this.fadeOut.get();
            if (fadeOut != old) {
                if (this.fadeOut.compareAndSet(old, fadeOut)) {
                    this.defaultTimings.set(false);
                    break;
                }
            } else {
                break;
            }
        }

        return this;
    }

    @Override
    public boolean isDefaultTimings() {
        return this.defaultTimings.get();
    }
}
