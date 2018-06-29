package cn.xtev.netty

import io.netty.handler.ssl.SslContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.channel.ChannelHandlerContext
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.SimpleChannelInboundHandler


/**
 *
 * @author liuhe
 * @date 18-6-29
 */
internal class TcpsSnoopClientInitializer(sslCtx: SslContext) : ChannelInitializer<SocketChannel>() {

    @Throws(Exception::class)
    override fun initChannel(ch: SocketChannel) {
        ch.pipeline().addLast(EchoClientHandler())
    }
}

internal class EchoClientHandler : SimpleChannelInboundHandler<ByteBuf>() {
    // 客户端连接服务器后被调用
    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        println("客户端连接服务器，开始发送数据……")
        val req = "QUERY TIME ORDER".toByteArray()//消息
        val firstMessage = Unpooled.buffer(req.size)//发送类
        firstMessage.writeBytes(req)//发送
        ctx.writeAndFlush(firstMessage)//flush
    }

    // • 从服务器接收到数据后调用
    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) {
        println("client 读取server数据..")
        // 服务端返回消息后
        val req = ByteArray(msg.readableBytes())
        msg.readBytes(req)
        val body = String(req, "UTF-8")
        println("服务端数据为 :$body")
    }

    // • 发生异常时被调用
    @Throws(Exception::class)
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        println("client exceptionCaught..")
        // 释放资源
        ctx.close()
    }
}