package org.projectblueshift.server.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;

public class BlueServerChannel extends ChannelInitializer {

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.config().setOption(ChannelOption.IP_TOS, 24);
        channel.config().setOption(ChannelOption.TCP_NODELAY, false);
    }
}
