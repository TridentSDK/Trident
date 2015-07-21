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

package net.tridentsdk.server.entity.ai;


import net.tridentsdk.entity.living.ai.AiHandler;
import net.tridentsdk.entity.living.ai.AiModule;
import net.tridentsdk.entity.types.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of interface
 *
 * @author The TridentSDK Team
 */
public class TridentAiHandler implements AiHandler {
    private final Map<EntityType, AiModule> modules = new ConcurrentHashMap<>();
    private final Map<EntityType, AiModule> nativeModules = new HashMap<>();

    public TridentAiHandler() {
        // TODO add default AIs
        nativeModules.put(EntityType.CREEPER, new CreeperAiModule());
    }

    @Override
    public AiModule defaultAiFor(EntityType type) {
        if (modules.get(type) == null) {
            return nativeModules.get(type);
        } else {
            return modules.get(type);
        }
    }

    @Override
    public void setDefaultAiFor(EntityType type, AiModule module) {
        modules.put(type, module);
    }

    @Override
    public AiModule nativeAIFor(EntityType type) {
        return nativeModules.get(type);
    }
}
