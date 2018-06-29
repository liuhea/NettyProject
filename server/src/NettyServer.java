import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class NettyServer {

    public static void run(int nPort) throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChatServerHandler())  //(4)
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            System.out.println("服务器已启动，等待客户端连接");

            // 绑定端口，开始接收进来的连接
            ChannelFuture f = b.bind(nPort).sync(); // (7)

            // 等待服务器  socket 关闭 。
            f.channel().closeFuture().sync();
        } finally {
            System.out.println("---------------Shut down");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) {
        int nPort = 9999;
        System.out.println("---------------Main start");
        try {
            run(nPort);
        } catch (Exception e) {
            System.out.println("---------------Main Error");
            e.printStackTrace();
        }
    }
}
