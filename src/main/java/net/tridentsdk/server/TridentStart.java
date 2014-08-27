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

import com.google.common.collect.Lists;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import joptsimple.*;
import net.tridentsdk.server.netty.TridentChannelInitializer;
import net.tridentsdk.server.threads.ThreadsManager;

import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.util.Collection;

/**
 * Server class that starts the connection listener. <p/> <p>Despite the fact that this class is under protected access,
 * it is documented anyways because of its significance in the server</p>
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
final class TridentStart {
    private static final EventLoopGroup bossGroup   = new NioEventLoopGroup();
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private TridentStart() {} // Do not initialize

    /**
     * Shutdown hook
     */
    static void shutdown() {
        TridentStart.close();
    }
    // TODO why do we need this? Put it in close instead - AgentTroll

    /**
     * Starts the server up when the jarfile is run
     *
     * @param args the command line arguments
     */
    public static void main(String... args) {
        /*TODO:
         check some args here, using an interpreter
         parse the configuration file
         create the server from the args/config values
         */

        OptionParser parser = new OptionParser();
        parser.acceptsAll(TridentStart.asList("h", "help"), "Show this help dialog.").forHelp();
        OptionSpec<Boolean> append =
                parser.acceptsAll(TridentStart.asList("log-append"), "Whether to append to the log file")
                      .withRequiredArg()
                      .ofType(Boolean.class)
                      .defaultsTo(true)
                      .describedAs("Log append");
        OptionSpec<File> properties =
                parser.acceptsAll(TridentStart.asList("properties"), "The location for the properties file")
                      .withRequiredArg()
                      .ofType(File.class)
                      .defaultsTo(new File("server.yml"))
                      .describedAs("Properties file");

        OptionSet options;
        try {
            options = parser.parse(args);
        } catch (OptionException ex) {
            ex.printStackTrace();
            return;
        }

        TridentStart.init(new TridentConfig(options.valueOf(properties)));
    }

    private static Collection<String> asList(String... params) {
        return Lists.newArrayList(params);
    }

    /**
     * Initializes the server with the configuration file
     *
     * @param config the configuration to use for option lookup
     */
    private static void init(TridentConfig config) {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(TridentStart.bossGroup, TridentStart.workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new TridentChannelInitializer())
             .option(ChannelOption.TCP_NODELAY, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind((int) config.getPort()).sync();

            //Runs the server on a separate thread
            //Server should read all settings from the loaded config
            TridentServer.createServer(config);

            // Wait until the server socket is closed, to gracefully shut down your server.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            //Exception is caught if server is closed.
        } finally {
            TridentStart.close();
        }
    }

    /**
     * Shuts down the server by closing the backed event loops
     */
    private static void close() {
        //Correct way to close the socket and shut down the server
        TridentStart.workerGroup.shutdownGracefully().awaitUninterruptibly();
        TridentStart.bossGroup.shutdownGracefully().awaitUninterruptibly();
        ThreadsManager.stopAll();
    }
}
