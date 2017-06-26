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

import net.tridentsdk.entity.LivingEntity;
import net.tridentsdk.entity.living.ai.AiModule;

/**
 * The AI Module that provides the default implentation of AI for creepers on the server
 */
public class CreeperAiModule implements AiModule {
    // TODO make this think
    @Override
    public int think(LivingEntity entity) {
        return 0;
    }
}
