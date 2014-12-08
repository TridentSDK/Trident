package net.tridentsdk.server.entity.decorate;

import net.tridentsdk.entity.LivingEntity;
import net.tridentsdk.entity.decorate.Equippable;
import net.tridentsdk.entity.decorate.LivingDecorationAdapter;
import net.tridentsdk.server.packets.play.out.PacketPlayOutEntityEquipment;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.window.inventory.ItemStack;

public class DecoratedEquippable extends LivingDecorationAdapter implements Equippable {
    private ItemStack[] equipment = new ItemStack[4];

    protected DecoratedEquippable(LivingEntity entity) {
        super(entity);
    }

    @Override
    public ItemStack[] getEquipment() {
        return equipment;
    }

    @Override
    public void setEquipment(ItemStack[] stack) {
        this.equipment = stack;
    }

    public void applyArmorUpdate() {
        for (int i = 0; i < equipment.length; i++) {
            ItemStack stack = equipment[i];
            PacketPlayOutEntityEquipment entityEquipment = new PacketPlayOutEntityEquipment();
            entityEquipment
                    .set("entityId", original().getId())
                    .set("slot", (short) i + 5)
                    .set(String.valueOf(i + 5),
                            Long.decode(Integer.toHexString(stack.getId()) + "010000ffff").longValue());
            TridentPlayer.sendAll(entityEquipment);
        }
    }
}
