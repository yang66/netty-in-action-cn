package nia.chapter4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
// Netty 4.2: OioEventLoopGroup 在 4.2 中已被弃用（@Deprecated），但仍可使用。
// 阻塞 I/O（OIO）传输在 Netty 4.2 中已不推荐使用，建议迁移到 NIO 传输。
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * 代码清单 4.3 使用 Netty 的阻塞网络处理
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class NettyOioServer {
    public void server(int port)
            throws Exception {
        final ByteBuf buf =
                Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi!\r\n", Charset.forName("UTF-8")));
        // Netty 4.2: OioEventLoopGroup 已被弃用，阻塞 I/O 传输不再推荐使用
        // 如需替代，建议使用 NIO 传输（参见 NettyNioServer）
        @SuppressWarnings("deprecation")
        EventLoopGroup group = new OioEventLoopGroup();
        try {
            //创建 ServerBootstrap
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    // Netty 4.1: 使用 OioEventLoopGroup 以允许阻塞模式（旧的I/O）
                    // Netty 4.2: OioEventLoopGroup 已弃用，OioServerSocketChannel 也随之弃用
                    .channel(OioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    //指定 ChannelInitializer，对于每个已接受的连接都调用它
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                                ch.pipeline().addLast(
                                    //添加一个 ChannelInboundHandlerAdapter以拦截和处理事件
                                    new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(
                                                ChannelHandlerContext ctx)
                                                throws Exception {
                                            ctx.writeAndFlush(buf.duplicate())
                                                    .addListener(
                                                            //将消息写到客户端，并添加 ChannelFutureListener，
                                                            //以便消息一被写完就关闭连接
                                                            ChannelFutureListener.CLOSE);
                                        }
                                    });
                        }
                    });
            //绑定服务器以接受连接
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            //释放所有的资源
            group.shutdownGracefully().sync();
        }
    }
}

