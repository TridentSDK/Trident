package net.tridentsdk.server.entity;

import net.tridentsdk.Position;
import net.tridentsdk.entity.DroppedItem;
import net.tridentsdk.entity.types.EntityType;

import java.util.UUID;

/**
 * Represents an item that is dropped on the ground
 *
 * @author The TridentSDK Team
 */
public class TridentDroppedItem extends TridentEntity implements DroppedItem {
    public TridentDroppedItem(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
    }

    @Override
    public int age() {
        return 0;
    }

    @Override
    public void setAge(int age) {

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
}
