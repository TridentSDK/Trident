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
package net.tridentsdk.server.packet.login;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.chat.Chat;
import net.tridentsdk.server.packet.PacketOut;

import static net.tridentsdk.server.net.NetData.wstr;

/**
 * This packet is sent to the client in states LOGIN, PLAY
 * to indicate that the player will be disconnected from
 * the server.
 */
public final class LoginOutDisconnect extends PacketOut {
    /**
     * The message displayed on the screen once the player
     * is disconnected
     */
    private final Chat reason;

    public LoginOutDisconnect(Chat reason) {
        super(LoginOutDisconnect.class);
        this.reason = reason;
    }

    @Override
    public void write(ByteBuf buf) {
        wstr(buf, this.reason.asJson());
    }
}