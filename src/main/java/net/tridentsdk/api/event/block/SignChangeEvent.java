/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
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
