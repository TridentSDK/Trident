/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
import net.tridentsdk.server.net.NetData;
import net.tridentsdk.server.packet.PacketOut;
import net.tridentsdk.ui.bossbar.BossBar;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
public abstract class PlayOutBossBar extends PacketOut {

    protected BossBar bossBar;
    private int action;

    public PlayOutBossBar(BossBar bossBar, int action) {
        super(PlayOutBossBar.class);
        this.bossBar = bossBar;
        this.action = action;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeLong(bossBar.getUniqueId().getMostSignificantBits());
        buf.writeLong(bossBar.getUniqueId().getLeastSignificantBits());
        NetData.wvint(buf, action);
    }

    public static class Add extends PlayOutBossBar {

        public Add(BossBar bossBar) {
            super(bossBar, 0);
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            NetData.wstr(buf, bossBar.getTitle().toString());
            buf.writeFloat(bossBar.getHealth());
            NetData.wvint(buf, bossBar.getColor().getId());
            NetData.wvint(buf, bossBar.getDivision().getId());

            int flags = 0;
            if (bossBar.isDarkenSky())
                flags |= 0x1;
            if (bossBar.isDragonBar())
                flags |= 0x2;
            buf.writeByte(flags);
        }

    }

    public static class Remove extends PlayOutBossBar {

        public Remove(BossBar bossBar) {
            super(bossBar, 1);
        }

    }

    public static class UpdateHealth extends PlayOutBossBar {

        public UpdateHealth(BossBar bossBar) {
            super(bossBar, 2);
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            buf.writeFloat(bossBar.getHealth());
        }

    }

    public static class UpdateTitle extends PlayOutBossBar {

        public UpdateTitle(BossBar bossBar) {
            super(bossBar, 3);
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            NetData.wstr(buf, bossBar.getTitle().toString());
        }

    }

    public static class UpdateStyle extends PlayOutBossBar {

        public UpdateStyle(BossBar bossBar) {
            super(bossBar, 4);
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            NetData.wvint(buf, bossBar.getColor().getId());
            NetData.wvint(buf, bossBar.getDivision().getId());
        }

    }

    public static class UpdateFlags extends PlayOutBossBar {

        public UpdateFlags(BossBar bossBar) {
            super(bossBar, 5);
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            int flags = 0;
            if (bossBar.isDarkenSky())
                flags |= 0x1;
            if (bossBar.isDragonBar())
                flags |= 0x2;
            buf.writeByte(flags);
        }

    }

}
