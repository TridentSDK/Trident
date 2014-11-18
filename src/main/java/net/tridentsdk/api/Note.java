/*
 *     TridentSDK - A Minecraft Server API
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.api;

/**
 * Immutable value representing the pitch of a note played
 *
 * @author The TridentSDK Team
 */
public class Note {
    private final short id;

    Note(int id) {
        if (id > 24) {
            throw new IllegalArgumentException("Note is too high!");
        } else if (id < 0) {
            throw new IllegalArgumentException("Note is too low!");
        }

        this.id = (short) id;
    }

    /**
     * Returns a note one step sharper than this
     */
    public Note sharpen() {
        if ((int) this.id + 1 > 24) {
            throw new IllegalArgumentException("Cannot sharpen this note, it is already the max");
        }
        return new Note((int) this.id + 1);
    }

    /**
     * Returns a note flatter than this
     */
    public Note flatten() {
        if ((int) this.id - 1 < 0)
            throw new IllegalArgumentException("Cannot flatten this note, it is already the max");
        return new Note((int) this.id - 1);
    }

    // TODO: make this more notable & remove the horrible puns
}
