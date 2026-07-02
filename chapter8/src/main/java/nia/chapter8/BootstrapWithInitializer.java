package nia.chapter8;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
// Netty 4.1: import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

import java.net.InetSocketAddress;

/**
 * 代码清单 8-6 引导和使用 ChannelInitializer
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class BootstrapWithInitializer {

    /**
     * 代码清单 8-6 引导和使用 ChannelInitializer
     * */
    public void bootstrap() throws InterruptedException {
        //创建 ServerBootstrap 以创建和绑定新的 Channel
        ServerBootstrap bootstrap = new ServerBootstrap();
        //设置 EventLoopGroup，其将提供用以处理 Channel 事件的 EventLoop
        // Netty 4.1: bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
        // Netty 4.2: 使用 MultiThreadIoEventLoopGroup + NioIoHandler 替代已弃用的 NioEventLoopGroup
        bootstrap.group(new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory()),
                new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory()))
            //指定 Channel 的实现
            .channel(NioServerSocketChannel.class)
            //注册一个 ChannelInitializerImpl 的实例来设置 ChannelPipeline
            .childHandler(new ChannelInitializerImpl());
        //绑定到地址
        ChannelFuture future = bootstrap.bind(new InetSocketAddress(8080));
        future.sync();
    }

    //用以设置 ChannelPipeline 的自定义 ChannelInitializerImpl 实现
    final class ChannelInitializerImpl extends ChannelInitializer<Channel> {
        @Override
        //将所需的 ChannelHandler 添加到 ChannelPipeline
        protected void initChannel(Channel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new HttpClientCodec());
            pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));

        }
    }
}
