/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.server.packet.PacketOut;
import net.tridentsdk.ui.title.Title;

import static net.tridentsdk.server.net.NetData.wstr;
import static net.tridentsdk.server.net.NetData.wvint;

/**
 * Packet used to display and handle text to be displayed
 * in the center of the player's screen.
 */
public abstract class PlayOutTitle extends PacketOut {
    /**
     * Cached instance of an empty title
     */
    private static final ChatComponent EMPTY_TITLE = ChatComponent.empty();

    private final PlayOutTitle.PlayOutTitleType action;

    private PlayOutTitle(PlayOutTitle.PlayOutTitleType action) {
        super(PlayOutTitle.class);
        this.action = action;
    }

    @Override
    public void write(ByteBuf buf) {
        wvint(buf, this.action.ordinal());
    }

    public static class SetTitle extends PlayOutTitle {
        private final ChatComponent chat;

        public SetTitle(Title title) {
            this(title.getHeader());
        }

        public SetTitle(ChatComponent chat) {
            super(PlayOutTitle.PlayOutTitleType.SET_TITLE);
            this.chat = chat == null ? PlayOutTitle.EMPTY_TITLE : chat;
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);
            wstr(buf, this.chat.toString());
        }
    }

    public static class SetSubtitle extends PlayOutTitle {
        private final ChatComponent chat;

        public SetSubtitle(Title title) {
            this(title.getSubtitle());
        }

        public SetSubtitle(ChatComponent chat) {
            super(PlayOutTitle.PlayOutTitleType.SET_SUBTITLE);
            this.chat = chat == null ? PlayOutTitle.EMPTY_TITLE : chat;
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);
            wstr(buf, this.chat.toString());
        }
    }

    public static class SetActionBar extends PlayOutTitle {
        private final ChatComponent chat;

        public SetActionBar(ChatComponent chat) {
            super(PlayOutTitle.PlayOutTitleType.SET_ACTION_BAR);
            this.chat = chat;
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);
            wstr(buf, this.chat.toString());
        }
    }

    public static class SetTiming extends PlayOutTitle {
        private final int fadeIn;
        private final int stay;
        private final int fadeOut;

        public SetTiming() {
            this(Title.DEFAULT_FADE_IN, Title.DEFAULT_STAY, Title.DEFAULT_FADE_OUT);
        }

        public SetTiming(Title title) {
            this(title.getFadeIn(), title.getStay(), title.getFadeOut());
        }

        public SetTiming(int fadeIn, int stay, int fadeOut) {
            super(PlayOutTitle.PlayOutTitleType.SET_TIMES_AND_DISPLAY);
            this.fadeIn = fadeIn;
            this.stay = stay;
            this.fadeOut = fadeOut;
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);
            buf.writeInt(this.fadeIn);
            buf.writeInt(this.stay);
            buf.writeInt(this.fadeOut);
        }
    }

    public static class Hide extends PlayOutTitle {
        public Hide() {
            super(PlayOutTitle.PlayOutTitleType.HIDE);
        }
    }

    public static class Reset extends PlayOutTitle {
        public Reset() {
            super(PlayOutTitle.PlayOutTitleType.RESET);
        }
    }

    public enum PlayOutTitleType {
        SET_TITLE, SET_SUBTITLE, SET_ACTION_BAR, SET_TIMES_AND_DISPLAY, HIDE, RESET
    }
}
