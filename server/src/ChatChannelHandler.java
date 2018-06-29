import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatChannelHandler extends SimpleChannelInboundHandler<String> { // (1)

    private static final String TAG = ChannelInboundHandler.class.getSimpleName();
    /**
     * A thread-safe Set  Using ChannelGroup, you can categorize Channels into a meaningful group.
     * A closed Channel is automatically removed from the collection,
     */
    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {  // (2)
        Channel incoming = ctx.channel();
        System.out.println(TAG + "---handler Added");
        channels.writeAndFlush("Server->" + incoming.remoteAddress() + " 加入\n");
        channels.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {  // (3)
        Channel incoming = ctx.channel();
        System.out.println(TAG + "---handler Removed");
        // Broadcast a message to multiple Channels
        channels.writeAndFlush("Server->" + incoming.remoteAddress() + " 离开\n");

        // A closed Channel is automatically removed from ChannelGroup,
        // so there is no need to do "channels.remove(ctx.channel());"
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws InterruptedException { // (4)
        System.out.println("---------------channelRead0 have received : " + s + "  ");
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            if (channel != incoming) {
                while (true) {
                    channel.writeAndFlush("hello client at first" + "\n");
                    Thread.sleep(1000);
                }
            } else {
                while (true) {
                    channel.writeAndFlush("hello client:" + s + "\n");
                    Thread.sleep(1000);
                }
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) { // (5)
        Channel incoming = ctx.channel();
        System.out.println(TAG + "---" + incoming.remoteAddress() + "---在线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) { // (6)
        Channel incoming = ctx.channel();
        System.out.println(TAG + "---" + incoming.remoteAddress() + "---掉线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel incoming = ctx.channel();
        System.out.println(TAG + "---" + incoming.remoteAddress() + "---异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}