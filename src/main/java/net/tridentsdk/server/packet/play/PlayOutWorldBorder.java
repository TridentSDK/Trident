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
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

import static net.tridentsdk.server.net.NetData.wvint;
import static net.tridentsdk.server.net.NetData.wvlong;

/**
 * Sent by the server whenever the world border should be
 * updated.
 */
@Immutable
public abstract class PlayOutWorldBorder extends PacketOut {
    public PlayOutWorldBorder() {
        super(PlayOutWorldBorder.class);
    }

    @Immutable
    public static class SetSize extends PlayOutWorldBorder {
        private final double diameter;

        public SetSize(double diameter) {
            this.diameter = diameter;
        }

        @Override
        public void write(ByteBuf buf) {
            wvint(buf, 0);
            buf.writeDouble(this.diameter);
        }
    }

    @Immutable
    public static class LerpSize extends PlayOutWorldBorder {
        private final double old;
        private final double newDiameter;
        private final long millis;

        public LerpSize(double old, double newDiameter, long millis) {
            this.old = old;
            this.newDiameter = newDiameter;
            this.millis = millis;
        }

        @Override
        public void write(ByteBuf buf) {
            wvint(buf, 1);
            buf.writeDouble(this.old);
            buf.writeDouble(this.newDiameter);
            wvlong(buf, this.millis);
        }
    }

    @Immutable
    public static class SetCenter extends PlayOutWorldBorder {
        private final double x;
        private final double z;

        public SetCenter(double x, double z) {
            this.x = x;
            this.z = z;
        }

        @Override
        public void write(ByteBuf buf) {
            wvint(buf, 2);
            buf.writeDouble(this.x);
            buf.writeDouble(this.z);
        }
    }

    @Immutable
    public static class Init extends PlayOutWorldBorder {
        private final double x;
        private final double z;
        private final double oldDiameter;
        private final double newDiameter;
        private final long millis;
        private final int warnTime;
        private final int warnBlocks;

        public Init(double x, double z, double oldDiameter, double newDiameter,
                    long growthMillis, int warnTime, int warnBlocks) {
            this.x = x;
            this.z = z;
            this.oldDiameter = oldDiameter;
            this.newDiameter = newDiameter;
            this.millis = growthMillis;
            this.warnTime = warnTime;
            this.warnBlocks = warnBlocks;
        }

        @Override
        public void write(ByteBuf buf) {
            wvint(buf, 3);
            buf.writeDouble(this.x);
            buf.writeDouble(this.z);
            buf.writeDouble(this.oldDiameter);
            buf.writeDouble(this.newDiameter);
            wvlong(buf, this.millis);
            wvint(buf, 29999984);
            wvint(buf, this.warnTime);
            wvint(buf, this.warnBlocks);
        }
    }

    @Immutable
    public static class SetWarnTime extends PlayOutWorldBorder {
        private final int seconds;

        public SetWarnTime(int seconds) {
            this.seconds = seconds;
        }

        @Override
        public void write(ByteBuf buf) {
            wvint(buf, 4);
            wvint(buf, this.seconds);
        }
    }

    @Immutable
    public static class SetWarnBlocks extends PlayOutWorldBorder {
        private final int blocks;

        public SetWarnBlocks(int blocks) {
            this.blocks = blocks;
        }

        @Override
        public void write(ByteBuf buf) {
            wvint(buf, 5);
            wvint(buf, this.blocks);
        }
    }
}