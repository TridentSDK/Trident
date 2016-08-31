package net.tridentsdk.server.entity;

import net.tridentsdk.server.net.EntityMetadata;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
public class TridentLivingEntityMeta extends TridentEntityMeta {

    public TridentLivingEntityMeta(EntityMetadata metadata) {
        super(metadata);
        metadata.add(6, EntityMetadata.EntityMetadataType.BYTE, 0b10);
        metadata.add(7, EntityMetadata.EntityMetadataType.FLOAT, 20.0f);
        metadata.add(8, EntityMetadata.EntityMetadataType.VARINT, 0);
        metadata.add(9, EntityMetadata.EntityMetadataType.BOOLEAN, false);
        metadata.add(10, EntityMetadata.EntityMetadataType.VARINT, 0);
    }

    public boolean isHandActive() {
        return getMetadata().get(6).asBit(0);
    }

    public void setHandActive(boolean active) {
        getMetadata().get(6).setBit(0, active);
    }

    public boolean isMainHandActive() {
        return !getMetadata().get(6).asBit(1);
    }

    public void setMainHandActive(boolean mainHand) {
        getMetadata().get(6).setBit(1, !mainHand);
    }

    public float getHealth() {
        return getMetadata().get(7).asFloat();
    }

    public void setHealth(float health) {
        getMetadata().get(7).set(health);
    }

    public int getPotionEffectColor() {
        return getMetadata().get(8).asInt();
    }

    public void setPotionEffectColor(int potionEffectColor) {
        getMetadata().get(8).set(potionEffectColor);
    }

    public boolean isPotionEffectAmbient() {
        return getMetadata().get(9).asBoolean();
    }

    public void setPotionEffectAmbient(boolean ambient) {
        getMetadata().get(9).set(ambient);
    }

    public int getNumberOfArrowsInEntity() {
        return getMetadata().get(10).asInt();
    }

    public void setNumberOfArrowsInEntity(int arrows) {
        getMetadata().get(10).set(arrows);
    }

}
