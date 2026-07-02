package nia.chapter8;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
// Netty 4.1: import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * 代码清单 8-4 引导服务器
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 * @author <a href="mailto:mawolfthal@gmail.com">Marvin Wolfthal</a>
 */
public class BootstrapServer {

    /**
     * 代码清单 8-4 引导服务器
     * */
    public void bootstrap() {
        // Netty 4.1: NioEventLoopGroup group = new NioEventLoopGroup();
        // Netty 4.2: 使用 MultiThreadIoEventLoopGroup + NioIoHandler 替代已弃用的 NioEventLoopGroup
        MultiThreadIoEventLoopGroup group = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        //创建 Server Bootstrap
        ServerBootstrap bootstrap = new ServerBootstrap();
        //设置 EventLoopGroup，其提供了用于处理 Channel 事件的EventLoop
        bootstrap.group(group)
            //指定要使用的 Channel 实现
            .channel(NioServerSocketChannel.class)
            //设置用于处理已被接受的子 Channel 的I/O及数据的 ChannelInboundHandler
            .childHandler(new SimpleChannelInboundHandler<ByteBuf>() {
                @Override
                protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                    ByteBuf byteBuf) throws Exception {
                    System.out.println("Received data");
                }
            });
        //通过配置好的 ServerBootstrap 的实例绑定该 Channel
        ChannelFuture future = bootstrap.bind(new InetSocketAddress(8080));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture)
                throws Exception {
                if (channelFuture.isSuccess()) {
                    System.out.println("Server bound");
                } else {
                    System.err.println("Bind attempt failed");
                    channelFuture.cause().printStackTrace();
                }
            }
        });
    }
}
