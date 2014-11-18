/*
 *     Trident - A Multithreaded Server Alternative
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
package net.tridentsdk.api.event.block;

import com.google.common.base.Preconditions;
import net.tridentsdk.api.Block;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.Cancellable;

/**
 * Called when a player edits a sign, or when the sign is first created
 */
public class SignChangeEvent extends BlockEvent implements Cancellable {

    private final Player editor;
    private String[] contents;
    private boolean cancel;

    public SignChangeEvent(Block block, Player editor, String... contents) {
        super(block);
        this.editor = editor;
        this.contents = contents;
    }

    /**
     * Return if the event is cancelled
     *
     * @return true if cancelled
     */
    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    /**
     * Set if the event is cancelled
     *
     * @param cancel Boolean cancellation state of event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Returns the contents of the Sign
     *
     * @return String[] contents of the Sign
     */
    public String[] getContents() {
        return this.contents;
    }

    /**
     * Sets the contents of the Sign
     *
     * @param contents String[] contents of the Sign
     */
    public void setContents(String... contents) {
        this.contents = contents;
    }

    /**
     * Returns the text of the specified line
     *
     * @param i line of the Sign
     * @return String text of the specified line
     */
    public String getLine(int i) {
        Preconditions.checkArgument(i >= 0, "Sign line is below 0");
        Preconditions.checkArgument(i <= 3, "Sign line is above 3");
        return this.contents[i];
    }

    /**
     * Sets the value of a line
     *
     * @param i    line of the Sign
     * @param text String text to set the line as
     * @return String previous text on the specified line
     */
    public String setLine(int i, String text) {
        Preconditions.checkArgument(!text.isEmpty(), "Sign line length is below 0 characters");
        Preconditions.checkArgument(text.length() <= 16, "Sign line length is above 16 characters");

        String previous = this.contents[i];
        this.contents[i] = text;
        return previous;
    }

    /**
     * Returns the Player who edited the Sign
     *
     * @return Player editor of the sign, null if no player
     */
    public Player getEditor() {
        return this.editor;
    }
}
