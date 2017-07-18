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
package net.tridentsdk.server.packet.login;

import net.tridentsdk.server.concurrent.PoolSpec;
import net.tridentsdk.server.concurrent.ServerThreadPool;
import net.tridentsdk.server.net.NetData;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.hjson.Stringify;

import javax.annotation.concurrent.ThreadSafe;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Helper class for initiating requests to Mojang servers.
 *
 * @param <T> the response type
 */
@ThreadSafe
public final class Mojang<T> {
    /**
     * A cached instance of the scheduler thread pool
     */
    private static final ServerThreadPool SCHEDULER = ServerThreadPool.forSpec(PoolSpec.SCHEDULER);
    /**
     * The connection to the Mojang server
     */
    private final HttpsURLConnection c;
    /**
     * The async callback
     */
    private volatile Function<JsonValue, T> callback;
    /**
     * Callback for exceptional requests
     */
    private volatile Function<String, T> exception;

    // Use static factory
    private Mojang(HttpsURLConnection connection) {
        this.c = connection;
    }

    /**
     * Opens a connection to the mojang URL following the
     * given format and fillers.
     *
     * @param <T> the response type
     * @param format the full URL
     * @param fill the variables
     * @return the mojang request
     */
    public static <T> Mojang<T> req(String format, String... fill) {
        try {
            URL url = new URL(String.format(format, (Object[]) fill));
            URLConnection connection = url.openConnection();
            return new Mojang<>((HttpsURLConnection) connection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Performs an HTTP(s) GET to the server.
     *
     * @return the response future
     */
    public Future<T> get() {
        Callable<T> get = () -> {
            try {
                this.c.setRequestMethod("GET");
                this.c.setRequestProperty("User-Agent", "Mozilla/5.0");
                this.c.setRequestProperty("Content-Type", "application/json");
                this.c.setDoOutput(true);
                this.c.setDoInput(true);

                int code = this.c.getResponseCode();
                if (code != 200) {
                    return this.exception.apply(String.valueOf(code));
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.c.getInputStream()))) {
                    return this.callback.apply(JsonValue.readJSON(reader));
                }
            } catch (IOException e) {
                return this.exception.apply(e.getMessage());
            }
        };
        return SCHEDULER.submit(get);
    }

    /**
     * Performs an HTTP(s) POST to the mojang server.
     *
     * @param element the JSON object to POST
     * @return the response future
     */
    public Future<T> post(JsonObject element) {
        Callable<T> post = () -> {
            try {
                this.c.setRequestMethod("POST");
                this.c.setRequestProperty("User-Agent", "Mozilla/5.0");
                this.c.setRequestProperty("Content-Type", "application/json");
                this.c.setDoOutput(true);
                this.c.setDoInput(true);

                try (OutputStream out = this.c.getOutputStream()) {
                    out.write(element.asObject().toString(Stringify.PLAIN).getBytes(NetData.NET_CHARSET));
                }

                int code = this.c.getResponseCode();
                if (code != 200) {
                    return this.exception.apply(String.valueOf(code));
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.c.getInputStream()))) {
                    return this.callback.apply(JsonValue.readJSON(reader));
                }
            } catch (IOException e) {
                return this.exception.apply(e.getMessage());
            }
        };
        return SCHEDULER.submit(post);
    }

    /**
     * Sets the callback before the request is performed.
     *
     * @param consumer the callback
     * @return the current instance of this mojang request
     */
    public Mojang<T> callback(Consumer<JsonValue> consumer) {
        return this.callback(resp -> {
            consumer.accept(resp);
            return null;
        });
    }

    /**
     * Sets the callback before the request is performed.
     *
     * @param func the callback
     * @return the current instance of this mojang request
     */
    public Mojang<T> callback(Function<JsonValue, T> func) {
        this.callback = func;
        return this;
    }

    public Mojang<T> onException(Function<String, T> func) {
        this.exception = func;
        return this;
    }
}
