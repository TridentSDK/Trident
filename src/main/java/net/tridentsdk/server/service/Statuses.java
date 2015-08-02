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

import com.google.common.collect.Sets;
import net.tridentsdk.Trident;
import net.tridentsdk.registry.PlayerStatus;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * Loads the Trident status files for bans, ops, and whitelists
 *
 * @author The TridentSDK Team
 */
public class Statuses implements PlayerStatus {
    @Override
    public boolean isBanned(UUID uuid) {
        return banList.contains(uuid.toString());
    }

    @Override
    public boolean isIpBanned(InetSocketAddress address) {
        return banList.contains("ip: " + address.getAddress().getHostAddress());
    }

    @Override
    public boolean isOpped(UUID uuid) {
        return opsList.contains(uuid.toString());
    }

    @Override
    public boolean isWhitelisted(UUID uuid) {
        return whiteList.contains(uuid.toString());
    }

    @Override
    public void ban(UUID uuid) {
        banList.add(uuid.toString());
    }

    @Override
    public void ipBan(InetSocketAddress address) {
        banList.add("ip: " + address.getAddress().getHostAddress());
    }

    @Override
    public void op(UUID uuid) {
        opsList.add(uuid.toString());
    }

    @Override
    public void whitelist(UUID uuid) {
        whiteList.add(uuid.toString());
    }

    private final Path bans = Trident.fileContainer().resolve("bans.txt");
    private final Path ops = Trident.fileContainer().resolve("ops.txt");
    private final Path whitelist = Trident.fileContainer().resolve("whitelist.txt");

    private final Set<String> banList = Sets.newConcurrentHashSet();
    private final Set<String> opsList = Sets.newConcurrentHashSet();
    private final Set<String> whiteList = Sets.newConcurrentHashSet();

    public void loadAll() {
        ensureExists(bans);
        ensureExists(ops);
        ensureExists(whitelist);

        for (Iterator<String> iterator = loadFromReader(readFile(bans)); iterator.hasNext(); ) {
            banList.add(iterator.next());
        }

        for (Iterator<String> iterator = loadFromReader(readFile(ops)); iterator.hasNext(); ) {
            opsList.add(iterator.next());
        }

        for (Iterator<String> iterator = loadFromReader(readFile(whitelist)); iterator.hasNext(); ) {
            whiteList.add(iterator.next());
        }
    }

    private void ensureExists(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private BufferedReader readFile(Path path) {
        try {
            return new BufferedReader(new FileReader(path.toFile()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private BufferedWriter writeFile(Path path) {
        try {
            return new BufferedWriter(new FileWriter(path.toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Iterator<String> loadFromReader(BufferedReader reader) {
        return new Iterator<String>() {
            String line;

            @Override
            public boolean hasNext() {
                try {
                    return (line = reader.readLine()) != null;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            public String next() {
                if (line == null) {
                    try {
                        return reader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    return line;
                }

                return null;
            }
        };
    }

    public void saveAll() throws IOException {
        ensureExists(bans);
        BufferedWriter banWriter = writeFile(bans);
        for (String aBanList : banList) {
            banWriter.write(aBanList);
            banWriter.newLine();
        }
        banWriter.close();

        ensureExists(ops);
        BufferedWriter opsWriter = writeFile(ops);
        for (String op : opsList) {
            opsWriter.write(op);
            opsWriter.newLine();
        }
        opsWriter.close();

        ensureExists(whitelist);
        BufferedWriter whitelistWriter = writeFile(whitelist);
        for (String list : whiteList) {
            whitelistWriter.write(list);
            whitelistWriter.newLine();
        }
        whitelistWriter.close();
    }
}
