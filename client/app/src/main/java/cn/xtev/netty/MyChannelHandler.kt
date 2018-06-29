package cn.xtev.netty

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.handler.codec.DelimiterBasedFrameDecoder
import io.netty.handler.codec.Delimiters
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder

class NettyClientThread : Thread() {
    companion object {
        val HOST = "192.168.1.29"
        var PORT = 9999
    }

    override fun run() {
        super.run()
        val group = NioEventLoopGroup()
        try {
            val bootstrap = Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel::class.java)
                    .handler(SimpleChatClientInitializer())
            val channel = bootstrap.connect(HOST, PORT).sync().channel()
            channel.writeAndFlush("hello server->" + System.currentTimeMillis() + "\r\n")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
//            关闭链接
//            group.shutdownGracefully()
        }
    }
}

class SimpleChatClientInitializer : ChannelInitializer<SocketChannel>() {
    @Throws(Exception::class)
    public override fun initChannel(ch: SocketChannel) {
        val pipeline = ch.pipeline()

        pipeline.addLast("framer", DelimiterBasedFrameDecoder(8192, *Delimiters.lineDelimiter()))
        pipeline.addLast("decoder", StringDecoder())
        pipeline.addLast("encoder", StringEncoder())
        pipeline.addLast("handler", SimpleChatClientHandler())
    }
}

class SimpleChatClientHandler : SimpleChannelInboundHandler<String>() {

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, s: String) {
        println("---channelRead0 have received : $s  ")
    }
}
