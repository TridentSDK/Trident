package net.tridentsdk.server.player;

import net.tridentsdk.entity.meta.living.PlayerMeta;
import net.tridentsdk.server.entity.meta.TridentLivingEntityMeta;
import net.tridentsdk.server.net.EntityMetadata;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
public class TridentPlayerMeta extends TridentLivingEntityMeta implements PlayerMeta {

    public TridentPlayerMeta(EntityMetadata metadata) {
        super(metadata);
        metadata.add(11, EntityMetadata.EntityMetadataType.FLOAT, 0f);
        metadata.add(12, EntityMetadata.EntityMetadataType.VARINT, 0);
        metadata.add(13, EntityMetadata.EntityMetadataType.BYTE, -1);
        metadata.add(14, EntityMetadata.EntityMetadataType.BYTE, 1);
    }

    @Override
    public float getAdditionalHearts() {
        return getMetadata().get(11).asFloat();
    }

    @Override
    public void setAdditionalHearts(float hearts) {
        getMetadata().get(11).set(hearts);
    }

    @Override
    public int getScore() {
        return getMetadata().get(12).asInt();
    }

    @Override
    public void setScore(int score) {
        getMetadata().get(12).set(score);
    }

    @Override
    public byte getSkinFlags() {
        return getMetadata().get(13).asByte();
    }

    @Override
    public void setSkinFlags(byte skinFlags) {
        getMetadata().get(13).set(skinFlags);
    }

    @Override
    public boolean isCapeEnabled() {
        return getMetadata().get(13).asBit(0);
    }

    @Override
    public void setCapeEnabled(boolean enabled) {
        getMetadata().get(13).setBit(0, enabled);
    }

    @Override
    public boolean isJacketEnabled() {
        return getMetadata().get(13).asBit(1);
    }

    @Override
    public void setJacketEnabled(boolean enabled) {
        getMetadata().get(13).setBit(1, enabled);
    }

    @Override
    public boolean isLeftSleeveEnabled() {
        return getMetadata().get(13).asBit(2);
    }

    @Override
    public void setLeftSleeveEnabled(boolean enabled) {
        getMetadata().get(13).setBit(2, enabled);
    }

    @Override
    public boolean isRightSleeveEnabled() {
        return getMetadata().get(13).asBit(3);
    }

    @Override
    public void setRightSleeveEnabled(boolean enabled) {
        getMetadata().get(13).setBit(3, enabled);
    }

    @Override
    public boolean isLeftLegPantsEnabled() {
        return getMetadata().get(13).asBit(4);
    }

    @Override
    public void setLeftLegPantsEnabled(boolean enabled) {
        getMetadata().get(13).setBit(4, enabled);
    }

    @Override
    public boolean isRightLegPantsEnabled() {
        return getMetadata().get(13).asBit(5);
    }

    @Override
    public void setRightLegPantsEnabled(boolean enabled) {
        getMetadata().get(13).setBit(5, enabled);
    }

    @Override
    public boolean isHatEnabled() {
        return getMetadata().get(13).asBit(6);
    }

    @Override
    public void setHatEnabled(boolean enabled) {
        getMetadata().get(13).setBit(6, enabled);
    }

    @Override
    public boolean isLeftHandMain() {
        return getMetadata().get(14).asByte() == 0;
    }

    @Override
    public void setLeftHandMain(boolean main) {
        getMetadata().get(14).set(main ? 0 : 1);
    }

}
