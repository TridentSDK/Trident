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
package net.tridentsdk.server.data.item;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.tridentsdk.meta.item.SkullMeta;
import net.tridentsdk.meta.item.SkullTexture;
import net.tridentsdk.meta.nbt.NBTField;
import net.tridentsdk.meta.nbt.NBTSerializable;
import net.tridentsdk.meta.nbt.TagType;

public class SkullMetaImpl extends ItemMetaImpl implements SkullMeta {
    protected static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @NBTField(name = "SkullOwner", type = TagType.COMPOUND)
    protected SkullOwnerData data;

    @Override
    public String owner() {
        return data == null ? null : data.name;
    }

    @Override
    public void setOwner(String name) {
        if (data == null) {
            data = new SkullOwnerData();
        }
        data.name = name;
    }

    @Override
    public List<SkullTexture> textures() {
        List<SkullTexture> textures = Lists.newArrayListWithCapacity(data.properties.textures.size());
        data.properties.textures.forEach(t -> textures.add(t));
        return textures;
    }

    protected static class SkullOwnerData implements NBTSerializable {
        @NBTField(name = "Name", type = TagType.STRING)
        String name;

        @NBTField(name = "Id", type = TagType.STRING)
        String id;

        @NBTField(name = "Properties", type = TagType.COMPOUND)
        SkullOwnerDataProperties properties;
    }

    protected static class SkullOwnerDataProperties implements NBTSerializable {
        @NBTField(name = "textures", type = TagType.LIST)
        List<SkullOwnerDataTexture> textures;
    }

    protected static class SkullOwnerDataTexture implements SkullTexture, NBTSerializable {
        @NBTField(name = "Value", type = TagType.STRING, required = true)
        String value;

        @NBTField(name = "Signature", type = TagType.STRING)
        String signature;

        String jsonString;
        JsonObject json;

        long jsonTimestamp;
        boolean jsonIsPublic;
        String jsonProfileId;
        String jsonProfileName;
        String jsonSkinUrl;
        String jsonCapeUrl;

        @Override
        public void process() {
            this.jsonString = new String(Base64.getDecoder().decode(value), StandardCharsets.ISO_8859_1);

            this.json = gson.fromJson(jsonString, JsonObject.class);

            this.jsonTimestamp = json.get("timestamp").getAsLong();
            this.jsonProfileId = json.get("profileId").getAsString();
            this.jsonProfileName = json.get("profileName").getAsString();
            this.jsonIsPublic = json.get("isPublic").getAsBoolean();

            JsonObject texObj = json.getAsJsonObject("textures");
            JsonObject skinObj = texObj.getAsJsonObject("SKIN");
            JsonObject capeObj = texObj.getAsJsonObject("CAPE");

            this.jsonSkinUrl = skinObj == null ? null : skinObj.get("url").getAsString();
            this.jsonCapeUrl = capeObj == null ? null : capeObj.get("url").getAsString();
        }

        public JsonObject jsonObject() {
            return json;
        }

        @Override
        public long timestamp() {
            return jsonTimestamp;
        }

        @Override
        public String profileId() {
            return jsonProfileId;
        }

        @Override
        public String profileName() {
            return jsonProfileName;
        }

        @Override
        public boolean isPublic() {
            return jsonIsPublic;
        }

        @Override
        public String skinUrl() {
            return jsonSkinUrl;
        }

        @Override
        public String capeUrl() {
            return jsonCapeUrl;
        }
    }
}
