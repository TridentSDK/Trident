package net.tridentsdk.server.player;

import net.tridentsdk.server.entity.TridentLivingEntityMeta;
import net.tridentsdk.server.net.EntityMetadata;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
public class TridentPlayerMeta extends TridentLivingEntityMeta {

    public TridentPlayerMeta(EntityMetadata metadata) {
        super(metadata);
        metadata.add(11, EntityMetadata.EntityMetadataType.FLOAT, 0f);
        metadata.add(12, EntityMetadata.EntityMetadataType.VARINT, 0);
        metadata.add(13, EntityMetadata.EntityMetadataType.BYTE, -1);
        metadata.add(14, EntityMetadata.EntityMetadataType.BYTE, 0);
    }

    public float getAdditionalHearts() {
        return getMetadata().get(11).asFloat();
    }

    public void setAdditionalHearts(float hearts) {
        getMetadata().get(11).set(hearts);
    }

    public int getScore() {
        return getMetadata().get(12).asInt();
    }

    public void setScore(int score) {
        getMetadata().get(12).set(score);
    }

    public boolean isCapeEnabled() {
        return getMetadata().get(13).asBit(0);
    }

    public void setCapeEnabled(boolean enabled) {
        getMetadata().get(13).setBit(0, enabled);
    }

    public boolean isJacketEnabled() {
        return getMetadata().get(13).asBit(1);
    }

    public void setJacketEnabled(boolean enabled) {
        getMetadata().get(13).setBit(1, enabled);
    }

    public boolean isLeftSleeveEnabled() {
        return getMetadata().get(13).asBit(2);
    }

    public void setLeftSleeveEnabled(boolean enabled) {
        getMetadata().get(13).setBit(2, enabled);
    }

    public boolean isRightSleeveEnabled() {
        return getMetadata().get(13).asBit(3);
    }

    public void setRightSleeveEnabled(boolean enabled) {
        getMetadata().get(13).setBit(3, enabled);
    }

    public boolean isLeftLegPantsEnabled() {
        return getMetadata().get(13).asBit(4);
    }

    public void setLeftLegPantsEnabled(boolean enabled) {
        getMetadata().get(13).setBit(4, enabled);
    }

    public boolean isRightLegPantsEnabled() {
        return getMetadata().get(13).asBit(5);
    }

    public void setRightLegPantsEnabled(boolean enabled) {
        getMetadata().get(13).setBit(5, enabled);
    }

    public boolean isHatEnabled() {
        return getMetadata().get(13).asBit(6);
    }

    public void setHatEnabled(boolean enabled) {
        getMetadata().get(13).setBit(6, enabled);
    }

    public boolean isLeftHandMain() {
        return getMetadata().get(14).asByte() == 0;
    }

    public void setLeftHandMain(boolean main) {
        getMetadata().get(14).set(main ? 0 : 1);
    }

}
