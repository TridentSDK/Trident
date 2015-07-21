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
package net.tridentsdk.server.service;

import com.google.common.collect.ForwardingCollection;
import net.tridentsdk.Trident;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.plugin.Plugin;
import net.tridentsdk.service.ChatFormatter;
import net.tridentsdk.service.ChatIdentityFormatter;
import net.tridentsdk.util.TridentLogger;

import java.util.Collection;
import java.util.Collections;

/**
 * Handles the chat formatting
 *
 * @author The TridentSDK Team
 */
public class ChatHandler
        extends ForwardingCollection<ChatIdentityFormatter>
        implements ChatFormatter {
    private volatile ChatIdentityFormatter provider = new ChatIdentityFormatter() {
        @Override
        public String format(String message, Player sender) {
            return "%n%d";
        }

        @Override
        public void overriden(ChatIdentityFormatter other, Plugin overrider) {
            TridentLogger.warn("Trident default chat overriden by " + overrider);
        }
    };

    public ChatHandler() {
        if (!Trident.isTrident())
            throw new RuntimeException(new IllegalAccessException("This class should only be instantiated by Trident"));
    }

    @Override
    public void setFormatter(ChatIdentityFormatter provider, Plugin plugin) {
        this.provider.overriden(provider, plugin);
        this.provider = provider;
    }

    @Override
    public String format(String message, Player player) {
        return provider.format(message, player);
    }

    @Override
    protected Collection<ChatIdentityFormatter> delegate() {
        return Collections.singleton(provider);
    }
}