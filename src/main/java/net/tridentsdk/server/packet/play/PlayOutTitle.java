package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.server.packet.PacketOut;
import net.tridentsdk.ui.title.Title;

import static net.tridentsdk.server.net.NetData.wstr;
import static net.tridentsdk.server.net.NetData.wvint;

public abstract class PlayOutTitle extends PacketOut {

    private final PlayOutTitleType action;

    private PlayOutTitle(PlayOutTitleType action) {
        super(PlayOutTitle.class);
        this.action = action;
    }

    @Override
    public void write(ByteBuf buf) {
        wvint(buf, this.action.ordinal());
    }

    public static class SetTitle extends PlayOutTitle {
        private ChatComponent chat;

        public SetTitle(Title title) {
            this(title.getTitle());
        }

        public SetTitle(ChatComponent chat) {
            super(PlayOutTitleType.SET_TITLE);
            this.chat = chat;
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);
            wstr(buf, chat.toString());
        }
    }

    public static class SetSubtitle extends PlayOutTitle {
        private ChatComponent chat;

        public SetSubtitle(Title title) {
            this(title.getSubtitle());
        }

        public SetSubtitle(ChatComponent chat) {
            super(PlayOutTitleType.SET_SUBTITLE);
            this.chat = chat;
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);
            wstr(buf, chat.toString());
        }
    }

    public static class SetActionBar extends PlayOutTitle {
        private ChatComponent chat;

        public SetActionBar(ChatComponent chat) {
            super(PlayOutTitleType.SET_ACTION_BAR);
            this.chat = chat;
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);
            wstr(buf, chat.toString());
        }
    }

    public static class SetTiming extends PlayOutTitle {
        private int fadeIn;
        private int stay;
        private int fadeOut;

        public SetTiming(Title title) {
            this(title.getFadeIn(), title.getStay(), title.getFadeOut());
        }

        public SetTiming(int fadeIn, int stay, int fadeOut) {
            super(PlayOutTitleType.SET_TIMES_AND_DISPLAY);
            this.fadeIn = fadeIn;
            this.stay = stay;
            this.fadeOut = fadeOut;
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);
            buf.writeInt(fadeIn);
            buf.writeInt(stay);
            buf.writeInt(fadeOut);
        }
    }

    public static class Hide extends PlayOutTitle {
        public Hide() {
            super(PlayOutTitleType.HIDE);
        }
    }

    public static class Reset extends PlayOutTitle {
        public Reset() {
            super(PlayOutTitleType.RESET);
        }
    }

    public enum PlayOutTitleType {
        SET_TITLE, SET_SUBTITLE, SET_ACTION_BAR, SET_TIMES_AND_DISPLAY, HIDE, RESET
    }
}
