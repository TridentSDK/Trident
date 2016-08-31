package net.tridentsdk.server.entity;

import lombok.Getter;
import net.tridentsdk.server.net.EntityMetadata;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
public class TridentEntityMeta {

    @Getter
    private EntityMetadata metadata;

    public TridentEntityMeta(EntityMetadata metadata) {
        this.metadata = metadata;
        this.metadata.add(0, EntityMetadata.EntityMetadataType.BYTE, 0);
        this.metadata.add(1, EntityMetadata.EntityMetadataType.VARINT, 0);
        this.metadata.add(2, EntityMetadata.EntityMetadataType.STRING, "");
        this.metadata.add(3, EntityMetadata.EntityMetadataType.BOOLEAN, false);
        this.metadata.add(4, EntityMetadata.EntityMetadataType.BOOLEAN, false);
        this.metadata.add(5, EntityMetadata.EntityMetadataType.BOOLEAN, false);
    }

    public boolean isOnFire() {
        return metadata.get(0).asBit(0);
    }

    public void setOnFire(boolean onFire) {
        metadata.get(0).setBit(0, onFire);
    }

    public boolean isCrouched() {
        return metadata.get(0).asBit(1);
    }

    public void setCrouched(boolean crouched) {
        metadata.get(0).setBit(1, crouched);
    }

    public boolean isSprinting() {
        return metadata.get(0).asBit(3);
    }

    public void setSprinting(boolean sprinting) {
        metadata.get(0).setBit(3, sprinting);
    }

    public boolean isEating() {
        return metadata.get(0).asBit(4);
    }

    public void setEating(boolean eating) {
        metadata.get(0).setBit(4, eating);
    }

    public boolean isInvisible() {
        return metadata.get(0).asBit(5);
    }

    public void setInvisible(boolean invisible) {
        metadata.get(0).setBit(5, invisible);
    }

    public boolean isGlowing() {
        return metadata.get(0).asBit(6);
    }

    public void setGlowing(boolean glowing) {
        metadata.get(0).setBit(6, glowing);
    }

    public boolean isUsingElytra() {
        return metadata.get(0).asBit(7);
    }

    public void setUsingElytra(boolean usingElytra) {
        metadata.get(0).setBit(7, usingElytra);
    }

    public int getAir() {
        return metadata.get(1).asInt();
    }

    public void setAir(int air) {
        metadata.get(1).set(air);
    }

    public String getCustomName() {
        return metadata.get(2).asString();
    }

    public void setCustomName(String name) {
        metadata.get(2).set(name);
    }

    public boolean isCustomNameVisible() {
        return metadata.get(3).asBoolean();
    }

    public void setCustomNameVisible(boolean visible) {
        metadata.get(3).set(visible);
    }

    public boolean isSilent() {
        return metadata.get(4).asBoolean();
    }

    public void setSilent(boolean silent) {
        metadata.get(4).set(silent);
    }

    public boolean isNoGravity() {
        return metadata.get(5).asBoolean();
    }

    public void setNoGravity(boolean noGravity) {
        metadata.get(5).set(noGravity);
    }

}
