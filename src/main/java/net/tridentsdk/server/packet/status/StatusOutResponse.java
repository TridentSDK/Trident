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
package net.tridentsdk.server.packet.status;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.command.logger.Logger;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.config.ServerConfig;
import net.tridentsdk.server.packet.PacketOut;
import net.tridentsdk.server.player.TridentPlayer;

import javax.annotation.concurrent.Immutable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

import static java.nio.file.StandardWatchEventKinds.*;
import static net.tridentsdk.server.net.NetData.wstr;

/**
 * The response to a client ping request.
 */
@Immutable
public final class StatusOutResponse extends PacketOut {
    /**
     * The current Minecraft version implemented by the
     * server
     */
    public static final String MC_VERSION = "1.11.2";
    /**
     * The protocol version associated with the Minecraft
     * version
     */
    private static final int PROTOCOL_VERSION = 316;

    private static final Logger logger;
    private static final Path iconPath;
    private static final AtomicReference<String> b64icon = new AtomicReference<>();
    static {
        String userDir = System.getProperty("user.dir");
        logger = Logger.get("Server Icon File Watcher");
        iconPath = Paths.get("server-icon.png");
        try {
            loadIcon();
        } catch (IOException ex) {
            logger.log("No server-icon.png!");
        }
        Thread watcherThread = new Thread(() -> {
            try {
                Path dir = Paths.get(userDir);
                WatchService service = dir.getFileSystem().newWatchService();
                WatchKey watchKey = dir.register(service, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
                while (true) {
                    try {
                        WatchKey eventKey = service.take();
                        logger.log("Got server icon watcher key!");
                        if (eventKey != watchKey) {
                            logger.warn(String.format("unexpected watch key: %s. expected %s\n", eventKey, watchKey));
                            break;
                        }
                        eventKey.pollEvents().forEach(e -> {
                            if (!e.context().equals(iconPath))
                                return;
                            logger.log("server-icon.png fired an event: " + e.kind().toString());
                            if (e.kind() == ENTRY_CREATE || e.kind() == ENTRY_MODIFY) {
                                try {
                                    loadIcon();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            } else if (e.kind() == ENTRY_DELETE) {
                                b64icon.set(null);
                            }
                        });

                        if (!eventKey.reset()) {
                            logger.log("Server icon watch key no longer valid!");
                            break;
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "TRD - Icon Watcher");
        watcherThread.setDaemon(true);
        watcherThread.start();
    }

    private static void loadIcon() throws IOException {
        logger.log("Loading server-icon.png");
        BufferedImage image = ImageIO.read(iconPath.toFile());

        if (image.getWidth() != 64 || image.getHeight() != 64){ // resize to 64x64 as required
            BufferedImage resizedImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(image, 0, 0, 64, 64, null);
            g.dispose();
            g.setComposite(AlphaComposite.Src);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            image = resizedImage;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] data = baos.toByteArray();
        String b64 = Base64.getEncoder().encodeToString(data);
        b64icon.set("data:image/png;base64," + b64);
        logger.log("Loaded server icon data: data:image/png;base64," + b64);
    }

    public StatusOutResponse() {
        super(StatusOutResponse.class);
    }

    @Override
    public void write(ByteBuf buf) {
        ServerConfig cfg = TridentServer.cfg();
        JsonObject resp = new JsonObject();

        JsonObject version = new JsonObject();
        version.addProperty("name", MC_VERSION);
        version.addProperty("protocol", PROTOCOL_VERSION);
        resp.add("version", version);

        JsonObject players = new JsonObject();
        players.addProperty("max", cfg.maxPlayers());
        players.addProperty("online", TridentPlayer.getPlayers().size());
        JsonArray sample = new JsonArray();
        TridentPlayer.getPlayers().forEach((u, p) -> {
            JsonObject o = new JsonObject();
            o.addProperty("name", p.getName());
            o.addProperty("id", p.getUuid().toString());
            sample.add(o);
        });
        players.add("sample", sample);
        resp.add("players", players);

        resp.add("description", ChatComponent.text(cfg.motd()).asJson());

        String icon = b64icon.get();
        if (icon != null) {
            resp.addProperty("favicon", icon);
        }
        wstr(buf, resp.toString());
    }
}
