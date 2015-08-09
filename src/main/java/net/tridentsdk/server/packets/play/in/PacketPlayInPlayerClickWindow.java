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

package net.tridentsdk.server.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.base.Substance;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.event.player.PlayerClickItemEvent;
import net.tridentsdk.inventory.Inventory;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.registry.Registered;
import net.tridentsdk.server.data.Slot;
import net.tridentsdk.server.event.EventProcessor;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.player.PlayerConnection;
import net.tridentsdk.util.TridentLogger;

/**
 * Packet sent by the player when it clicks on a slot in a inventory.
 */
public class PacketPlayInPlayerClickWindow extends InPacket {

    /**
     * The id of the inventory which was clicked. 0 for player inventory.
     */
    protected int windowId;
    /**
     * The button used in the click, dependent on action number  TODO reference to wiki
     */
    protected int clickedButton;

    /**
     * The clicked slot, -999 if not applicable
     */
    protected short clickedSlot;
    /**
     * A unique number for the action, used for transaction handling
     */
    protected short actionNumber;
    /**
     * Inventory operation mode
     */
    protected ClickAction mode;
    /**
     * Item clicked
     */
    protected Slot clickedItem;

    @Override
    public int id() {
        return 0x0E;
    }

    public int windowId() {
        return this.windowId;
    }

    public int clickedButton() {
        return this.clickedButton;
    }

    public short clickedSlot() {
        return this.clickedSlot;
    }

    public short actionNumber() {
        return this.actionNumber;
    }

    public ClickAction mode() {
        return this.mode;
    }

    public Slot clickedItem() {
        return this.clickedItem;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.windowId = (int) buf.readByte();
        this.clickedSlot = buf.readShort();
        this.clickedButton = (int) buf.readByte();

        this.actionNumber = buf.readShort();

        short mode = buf.readShort();

        this.mode = ClickAction.getAction(mode, clickedButton, clickedSlot);
        this.clickedItem = new Slot(buf);

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        if(mode == null) {
            return;
        }

        Player player = ((PlayerConnection) connection).player();
        Inventory window = Registered.inventories().fromId(this.windowId);

        if(clickedSlot >= window.length()) {
            clickedSlot += (9 - window.length());
            window = player.window();
        }

        PlayerClickItemEvent clickEvent = EventProcessor
                .fire(new PlayerClickItemEvent(window, this.clickedSlot, (int) this.actionNumber));

        if (clickEvent.isIgnored()) {
            return;
        }

        // TODO Implement all
        switch(mode) {
            case LEFT_CLICK:
            case RIGHT_CLICK:
                if(player.pickedItem() == null) {
                    if(window.itemAt(clickedSlot) != null && window.itemAt(clickedSlot).type() != Substance.AIR) {
                        if(window.itemAt(clickedSlot).isSimilar(clickedItem.item())) {
                            if(mode == ClickAction.LEFT_CLICK) {
                                player.setPickedItem(clickedItem.item());
                                window.setSlot(clickedSlot, null);
                            } else {
                                Item cursor = clickedItem.item().clone();
                                cursor.setQuantity((short) (cursor.quantity() / 2));
                                player.setPickedItem(cursor);
                                window.itemAt(clickedSlot).setQuantity((short) (window.itemAt(clickedSlot).quantity() - cursor.quantity()));
                                window.setSlot(clickedSlot, window.itemAt(clickedSlot));
                            }
                        } else {
                            TridentLogger.get().warn(player.name() + " tried to cheat items!");
                        }
                    }
                } else {
                    Item temp = window.itemAt(clickedSlot);
                    window.setSlot(clickedSlot, player.pickedItem());

                    if(temp != null && temp.type() != Substance.AIR) {
                        player.setPickedItem(temp);
                    } else {
                        player.setPickedItem(null);
                    }
                }
                break;
            case SHIFT_LEFT_CLICK:
                break;
            case SHIFT_RIGHT_CLICK:
                break;
            case NUMBER_KEY:
                break;
            case MIDDLE_CLICK:
                break;
            case DROP_KEY_ONE:
                break;
            case DROP_KEY_STACK:
                break;
            case LEFT_CLICK_OUTSIDE:
                break;
            case RIGHT_CLICK_OUTSIDE:
                break;
            case START_LEFT_CLICK_DRAG:
                break;
            case START_RIGHT_CLICK_DRAG:
                break;
            case ADD_SLOT_LEFT_CLICK_DRAG:
                break;
            case ADD_SLOT_RIGHT_CLICK_DRAG:
                break;
            case END_LEFT_CLICK_DRAG:
                break;
            case END_RIGHT_CLICK_DRAG:
                break;
            case DOUBLE_CLICK:
                break;
        }
    }

    public enum ClickAction {

        LEFT_CLICK,
        RIGHT_CLICK,

        SHIFT_LEFT_CLICK,
        SHIFT_RIGHT_CLICK,

        NUMBER_KEY,

        MIDDLE_CLICK,

        DROP_KEY_ONE,
        DROP_KEY_STACK,
        LEFT_CLICK_OUTSIDE,
        RIGHT_CLICK_OUTSIDE,

        START_LEFT_CLICK_DRAG,
        START_RIGHT_CLICK_DRAG,
        ADD_SLOT_LEFT_CLICK_DRAG,
        ADD_SLOT_RIGHT_CLICK_DRAG,
        END_LEFT_CLICK_DRAG,
        END_RIGHT_CLICK_DRAG,

        DOUBLE_CLICK;

        public static ClickAction getAction(short mode, int button, int slot){
            switch(mode){
                case 0:
                case 255:
                    switch(button) {
                        case 0:
                            return LEFT_CLICK;
                        case 1:
                            return RIGHT_CLICK;
                    }
                    break;
                case 1:
                    switch(button) {
                        case 0:
                            return SHIFT_LEFT_CLICK;
                        case 1:
                            return SHIFT_RIGHT_CLICK;
                    }
                    break;
                case 2:
                    return NUMBER_KEY;
                case 3:
                    return MIDDLE_CLICK;
                case 4:
                    if(slot == -999) {
                        switch(button) {
                            case 0:
                                return LEFT_CLICK_OUTSIDE;
                            case 1:
                                return RIGHT_CLICK_OUTSIDE;
                        }
                    } else {
                        switch(button) {
                            case 0:
                                return DROP_KEY_ONE;
                            case 1:
                                return DROP_KEY_STACK;
                        }
                    }
                    break;
                case 5:
                    switch(button) {
                        case 0:
                            return START_LEFT_CLICK_DRAG;
                        case 4:
                            return START_RIGHT_CLICK_DRAG;
                        case 1:
                            return ADD_SLOT_LEFT_CLICK_DRAG;
                        case 5:
                            return ADD_SLOT_RIGHT_CLICK_DRAG;
                        case 2:
                            return END_LEFT_CLICK_DRAG;
                        case 6:
                            return END_RIGHT_CLICK_DRAG;
                    }
                    break;
                case 6:
                    return DOUBLE_CLICK;
            }

            return null;
        }

    }

}
