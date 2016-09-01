package net.tridentsdk.server.entity.meta;

import net.tridentsdk.entity.meta.living.LivingEntityMeta;
import net.tridentsdk.server.net.EntityMetadata;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
public class TridentLivingEntityMeta extends TridentEntityMeta implements LivingEntityMeta {

    public TridentLivingEntityMeta(EntityMetadata metadata) {
        super(metadata);
        metadata.add(6, EntityMetadata.EntityMetadataType.BYTE, 0b10);
        metadata.add(7, EntityMetadata.EntityMetadataType.FLOAT, 20.0f);
        metadata.add(8, EntityMetadata.EntityMetadataType.VARINT, 0);
        metadata.add(9, EntityMetadata.EntityMetadataType.BOOLEAN, false);
        metadata.add(10, EntityMetadata.EntityMetadataType.VARINT, 0);
    }

    @Override
    public boolean isHandActive() {
        return getMetadata().get(6).asBit(0);
    }

    @Override
    public void setHandActive(boolean active) {
        getMetadata().get(6).setBit(0, active);
    }

    @Override
    public boolean isMainHandActive() {
        return !getMetadata().get(6).asBit(1);
    }

    @Override
    public void setMainHandActive(boolean mainHand) {
        getMetadata().get(6).setBit(1, !mainHand);
    }

    @Override
    public float getHealth() {
        return getMetadata().get(7).asFloat();
    }

    @Override
    public void setHealth(float health) {
        getMetadata().get(7).set(health);
    }

    @Override
    public int getPotionEffectColor() {
        return getMetadata().get(8).asInt();
    }

    @Override
    public void setPotionEffectColor(int potionEffectColor) {
        getMetadata().get(8).set(potionEffectColor);
    }

    @Override
    public boolean isPotionEffectAmbient() {
        return getMetadata().get(9).asBoolean();
    }

    @Override
    public void setPotionEffectAmbient(boolean ambient) {
        getMetadata().get(9).set(ambient);
    }

    @Override
    public int getNumberOfArrowsInEntity() {
        return getMetadata().get(10).asInt();
    }

    @Override
    public void setNumberOfArrowsInEntity(int arrows) {
        getMetadata().get(10).set(arrows);
    }

}
