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
package net.tridentsdk.server.entity;

import net.tridentsdk.base.Position;
import net.tridentsdk.entity.DroppedItem;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.server.data.MetadataType;
import net.tridentsdk.server.data.ProtocolMetadata;
import net.tridentsdk.server.data.Slot;
import net.tridentsdk.server.packets.play.out.PacketPlayOutEntityMetadata;
import net.tridentsdk.server.packets.play.out.PacketPlayOutSpawnObject;
import net.tridentsdk.server.player.TridentPlayer;

import java.util.UUID;

/**
 * Represents an item that is dropped on the ground
 *
 * @author The TridentSDK Team
 */
public class TridentDroppedItem extends TridentEntity implements DroppedItem {

    private int age = 0;
    private Item item;

    public TridentDroppedItem(Position spawnPosition, Item item) {
        super(UUID.randomUUID(), spawnPosition);
        this.item = item;
        setSize(0.25f, 0.25f);
    }

    @Override
    public int age() {
        return age;
    }

    @Override
    public void setAge(int age) {
        this.age = age;
    }

    @Override
    protected void doTick(){
        age++;
    }

    @Override
    public short health() {
        return 0;
    }

    @Override
    public void setHealth(short health) {

    }

    @Override
    public String owner() {
        return null;
    }

    @Override
    public void setOwner(String owner) {

    }

    @Override
    public String dropper() {
        return null;
    }

    @Override
    public void setDropper(String dropper) {

    }

    @Override
    public EntityType type() {
        return EntityType.ITEM;
    }

    @Override
    public TridentEntity spawn(){
        super.spawn();

        ProtocolMetadata metadata = new ProtocolMetadata();
        super.encodeMetadata(metadata);
        metadata.setMeta(10, MetadataType.SLOT, new Slot(item));

        PacketPlayOutSpawnObject object = new PacketPlayOutSpawnObject();
        object.set("entityId", entityId());
        object.set("entity", this);

        PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata();
        meta.set("entityId", entityId());
        meta.set("metadata", metadata);

        TridentPlayer.sendAll(object);
        TridentPlayer.sendAll(meta);
        return this;
    }

    public Item item(){
        return item;
    }

    public boolean canPickupItem(){
        return age() > 40; // TODO Find out actual value
    }
}
