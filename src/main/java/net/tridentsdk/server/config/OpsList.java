/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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
package net.tridentsdk.server.config;

import lombok.Getter;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.util.Misc;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * List of server operators
 */
@ThreadSafe
public class OpsList extends TridentConfig {
    /**
     * Path to the ops file
     */
    public static final Path PATH = Misc.HOME_PATH.resolve("ops.hjson");
    /**
     * The key to the ops collection in the config file
     */
    private static final String OPS_KEY = "ops";

    /**
     * The list of server ops
     */
    @Getter
    private final Set<UUID> ops = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * Ops list initializer method
     */
    private OpsList() {
        super(PATH);
    }

    /**
     * Initializes the ops list to the config file contents.
     *
     * @param needsInit whether to initialize empty file
     * @return the new ops list
     *
     * @throws IOException pass down exception
     */
    public static OpsList init(boolean needsInit) throws IOException {
        OpsList list = new OpsList();
        if (needsInit) {
            Files.write(list.getPath(), "{}".getBytes());
        }
        list.load();
        return list;
    }

    /**
     * Adds the given UUID as a server operator.
     *
     * @param uuid the UUID to give operator status
     */
    public void addOp(UUID uuid) {
        this.ops.add(uuid);
        this.set(OPS_KEY, this.ops.stream().map(UUID::toString).collect(Collectors.toList()));
        try {
            this.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TridentServer.getInstance().getLogger().log(TridentPlayer.getPlayers().get(uuid).getName() + " [" + uuid + "] has been opped");
    }

    /**
     * Removes the given UUID from the operators list.
     *
     * @param uuid the UUID to deop
     */
    public void removeOp(UUID uuid) {
        this.ops.remove(uuid);
        this.set(OPS_KEY, this.ops);
        try {
            this.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TridentServer.getInstance().getLogger().log(TridentPlayer.getPlayers().get(uuid).getName() +
                " [" + uuid + "] has been deopped");
    }

    @Override
    public void load() throws IOException {
        super.load();
        if (this.hasKey(OPS_KEY)) {
            this.getCollection(OPS_KEY, new AbstractSet<String>() {
                @Override
                public boolean add(String c) {
                    OpsList.this.ops.add(UUID.fromString(c));
                    return true;
                }

                @Override
                public Iterator<String> iterator() {
                    return null;
                }

                @Override
                public int size() {
                    return 0;
                }
            });
        } else {
            this.set(OPS_KEY, this.ops);
            this.save();
        }
    }
}