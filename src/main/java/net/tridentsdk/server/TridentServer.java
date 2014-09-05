/*
 * Copyright (C) 2014 The TridentSDK Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tridentsdk.server;

import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.ProfileRepository;
import net.tridentsdk.api.Server;
import net.tridentsdk.api.Trident;
import net.tridentsdk.server.netty.protocol.Protocol;
import net.tridentsdk.server.threads.ThreadsManager;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The access base to internal workings of the server
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public final class TridentServer implements Server, Runnable {
    private final AtomicReference<Thread> SERVER_THREAD = new AtomicReference<>();
    private final ProfileRepository PROFILE_REPOSITORY = new HttpProfileRepository("minecraft");

    private final TridentConfig config;
    private final Protocol protocol;
    private final Queue<Runnable> threadTasks = new ConcurrentLinkedQueue<>();

    private volatile boolean stopped;

    private TridentServer(TridentConfig config) {
        this.config = config;
        this.protocol = new Protocol();
    }

    /**
     * Creates the server access base, distributing information to the fields available
     *
     * @param config the configuration to use for option lookup
     */
    public static TridentServer createServer(TridentConfig config) {
        TridentServer server = new TridentServer(config);
        Trident.setServer(server);

        server.SERVER_THREAD.set(new Thread(server, "TridentServer Main Thread"));
        server.SERVER_THREAD.get().start();

        return server;
        // We CANNOT let the "this" instance escape during creation, else we lose thread-safety
    }

    /**
     * Get the protocol base of the server
     *
     * @return the access to server protocol
     */
    public Protocol getProtocol() {
        return this.protocol;
    }

    public ProfileRepository getProfileRepository() {
        return this.PROFILE_REPOSITORY;
    }

    /**
     * Gets the port the server currently runs on
     *
     * @return the port occupied by the server
     */
    @Override
    public int getPort() {
        return (int) this.config.getPort();
    }

    /**
     * Puts a task into the execution queue
     */
    public void addTask(Runnable task) {
        this.threadTasks.add(task);
    }

    @Override
    public void run() {
        //TODO: Set some server stuff up

        //TODO: Main server Loop
        while (!this.stopped) {
            Runnable task = this.threadTasks.poll();
            if (task != null)
                task.run();
            ThreadsManager.park();
        }
    }

    /**
     * Performs the shutdown procedure on the server, ending with the exit of the JVM
     */
    @Override
    public void shutdown() {
        //TODO: Cleanup stuff...
        this.SERVER_THREAD.get().interrupt();
        this.stopped = true;
    }
}
