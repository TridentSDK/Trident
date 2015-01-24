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

package net.tridentsdk.server.window;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import net.tridentsdk.docs.Volatile;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.server.data.Slot;
import net.tridentsdk.server.entity.TridentEntity;
import net.tridentsdk.server.entity.TridentEntityBuilder;
import net.tridentsdk.server.packets.play.in.PacketPlayInPlayerCloseWindow;
import net.tridentsdk.server.packets.play.out.PacketPlayOutOpenWindow;
import net.tridentsdk.server.packets.play.out.PacketPlayOutSetSlot;
import net.tridentsdk.server.player.PlayerConnection;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.window.Window;
import net.tridentsdk.window.inventory.InventoryType;
import net.tridentsdk.window.inventory.Item;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An inventory window, wherever and whatever is holding it or having it open
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class TridentWindow implements Window {
    /**
     * Counter for window ids, initial value is 2 to avoid confusion with a window and a player inventory
     */
    private static final AtomicInteger counter = new AtomicInteger(2);

    private final int id;
    private final String name;
    private final int length;
    private final InventoryType type;
    private final Set<Player> users = Factories.collect().createSet();
    @Volatile(policy = "Do not write individual elements", reason = "Thread safe array", fix = "See Line 110")
    private volatile Item[] contents;

    /**
     * Builds a new inventory window
     *
     * @param name   the title of the inventory
     * @param length the amount of slots in the inventory (should be multiple of 9)
     */
    public TridentWindow(String name, int length, InventoryType type) {
        this.name = name;
        this.length = length;
        this.id = counter.addAndGet(1);
        this.contents = new Item[length];
        this.type = type;
    }

    /**
     * Builds a new inventory window
     *
     * @param length the amount of slots in the inventory (should be multiple of 9)
     */
    public TridentWindow(int length) {
        this("", length, InventoryType.CHEST);
    }

    @Override
    public int windowId() {
        return this.id;
    }

    @Override
    public Item[] items() {
        return this.contents;
    }

    @Override
    public int length() {
        return this.length;
    }

    //@Override
    public int getItemLength() {
        int counter = 0;
        for (Item item : items()) {
            if (item != null)
                counter++;
        }

        return counter;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public void setSlot(int index, Item value) {
        contents[index] = value;
        this.contents = this.contents; // Flush caches, make entire array visible

        PacketPlayOutSetSlot setSlot = new PacketPlayOutSetSlot();
        setSlot.set("windowId", windowId()).set("slot", (short) index).set("item", new Slot(value));

        for (Player player : users) {
            ((TridentPlayer) player).getConnection().sendPacket(setSlot);
        }
    }

    @Override
    public void putItem(Item item) {
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] == null) {
                setSlot(i, item);
                return;
            }
        }

        for (Player user : users) {
            // TODO implement
            TridentEntity dropped = TridentEntityBuilder.create()
                    .spawnLocation(user.location())
                    .build(TridentEntity.class);
            // TODO set dropped type
        }
    }

    public void sendTo(TridentPlayer player) {
        PacketPlayOutOpenWindow window = new PacketPlayOutOpenWindow();
        window.set("windowId", windowId())
                .set("inventoryType", type)
                .set("windowTitle", name())
                .set("slots", length())
                .set("entityId", -1);
        player.getConnection().sendPacket(window);

        for (int i = 0; i < length(); i++) {
            PacketPlayOutSetSlot setSlot = new PacketPlayOutSetSlot();
            setSlot.set("windowId", windowId()).set("slot", (short) i).set("item", new Slot(items()[i]));
            player.getConnection().sendPacket(window);
        }

        addClosedListener(player);
        users.add(player);
    }

    @Volatile(policy = "DO NOT INVOKE OUTSIDE OF THIS CLASS",
            reason = "Extremely unsafe and causes unspecified behavior without proper handling",
            fix = "Do not use reflection on this method")
    private void addClosedListener(Player player) {
        final PlayerConnection connection = ((TridentPlayer) player).getConnection();
        connection.getChannel().pipeline().addLast(new ChannelHandlerAdapter() {
            @Override
            // Occurs after the message should be decoded
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof PacketPlayInPlayerCloseWindow) {
                    PacketPlayInPlayerCloseWindow windowClose = (PacketPlayInPlayerCloseWindow) msg;
                    if (windowClose.getWindowId() == windowId())
                        for (Player player1 : users) {
                            if (connection.getChannel().equals(ctx.channel())) {
                                users.remove(player1);
                                ctx.pipeline().remove(this);
                            }
                        }
                }

                // Pass to the next channel handler
                super.channelRead(ctx, msg);
            }
        });
    }
}
