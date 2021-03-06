package Netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class BaseNettyServer {

    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public BaseNettyServer(ChannelHandler ... handlers) {
        EventLoopGroup auth = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        AuthService.connection();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(auth, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(handlers);
                        }
                    });
            ChannelFuture future = bootstrap.bind(8189).sync();
            // server started!
            future.channel().closeFuture().sync(); // block
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            AuthService.disconnect();
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
