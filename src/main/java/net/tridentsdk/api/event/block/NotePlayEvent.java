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

import net.tridentsdk.api.Block;
import net.tridentsdk.api.Instrument;
import net.tridentsdk.api.Note;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.Cancellable;

import java.util.List;

/**
 * Called when a note is played, has a list of players that will hear this note
 */
public class NotePlayEvent extends BlockEvent implements Cancellable {
    private final List<Player> players;
    private Note note;
    private Instrument instrument;
    private boolean cancel;

    /**
     * @param block      Block playing the Note
     * @param players    List of Players who can hear the Note
     * @param note       Note representing the sound being played
     * @param instrument Instrument of the Note
     */
    public NotePlayEvent(Block block, List<Player> players, Note note, Instrument instrument) {
        super(block);
        this.players = players;
        this.note = note;
        this.instrument = instrument;
    }

    /**
     * Returns the Note being played
     *
     * @return Note representing the sound that is being played
     */
    public Note getNote() {
        return this.note;
    }

    /**
     * Set the Note that is being played
     *
     * @param note Note that is being played
     */
    public void setNote(Note note) {
        this.note = note;
    }

    /**
     * Get the Instrument being used to play the Note
     *
     * @return Instrument being used to play the Note
     */
    public Instrument getInstrument() {
        return this.instrument;
    }

    /**
     * Set the Instrument to play the Note
     *
     * @param instrument Instrument to play the note
     */
    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    /**
     * Returns a list of players that will hear the Note being played
     *
     * @return List of Players who can hear the Note
     */
    public List<Player> getPlayers() {
        return this.players;
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
}
