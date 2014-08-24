package net.tridentsdk.server;

import com.google.common.collect.Lists;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.tridentsdk.api.Trident;
import net.tridentsdk.server.netty.TridentChannelInitializer;

import java.io.File;
import java.util.List;

public class TridentStart {

    private static TridentStart instance;

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private TridentServer server;

    public TridentStart () {
        instance = this;
    }

    public void init(TridentConfig config) {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new TridentChannelInitializer())
                    .option(ChannelOption.TCP_NODELAY, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(config.getPort()).sync();

            //Runs the server on a seperate thread
            //Server should read all settings from the loaded config
            server = new TridentServer(config);
            Trident.setServer(server);
            new Thread(server).run();

            // Wait until the server socket is closed, to gracefully shut down your server.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            //Exception is caught if server is closed.
        } finally {
            close();
        }
    }

    public void close() {
        //Correct way to close the socket and shut down the server
        workerGroup.shutdownGracefully().awaitUninterruptibly();
        bossGroup.shutdownGracefully().awaitUninterruptibly();
    }

    protected static void shutdown() {
        instance.close();
    }

    public static void main(String[] args) throws Exception {
        /*TODO:
         check some args here, using an interpreter
         parse the configuration file
         create the server from the args/config values
         */

        OptionParser parser = new OptionParser();
        parser.acceptsAll(asList("h", "help"), "Show this help dialog.").forHelp();
        OptionSpec<Boolean> append = parser.acceptsAll(asList("log-append"), "Whether to append to the log file").withRequiredArg().ofType(Boolean.class).defaultsTo(true).describedAs("Log append");
        OptionSpec<File> properties = parser.acceptsAll(asList("properties"), "The location for the properties file").withRequiredArg().ofType(File.class).defaultsTo(new File("server.yml")).describedAs("Properties file");

        OptionSet options;
        try {
            options = parser.parse(args);
        } catch(OptionException ex) {
            ex.printStackTrace();
            return;
        }

        new TridentStart().init(new TridentConfig(options.valueOf(properties)));
    }

    private static List<String> asList(String... params) {
        return Lists.newArrayList(params);
    }

}
