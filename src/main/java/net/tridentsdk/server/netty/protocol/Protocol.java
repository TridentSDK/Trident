/*
 * Copyright (C) 2014 The TridentSDK Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tridentsdk.server.netty.protocol;

import javax.annotation.concurrent.ThreadSafe;

/**
 * The base implementation for the protocol handling internals
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class Protocol {

    private Play play = new Play();
    private Status status = new Status();
    private Login login = new Login();
    private Handshake handshake = new Handshake();

    public Play getPlay() {
        return play;
    }

    public Status getStatus() {
        return status;
    }

    public Login getLogin() {
        return login;
    }

    public Handshake getHandshake() {
        return handshake;
    }

}
