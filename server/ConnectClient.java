package net.sirun.connect.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import net.sirun.connect.bean.BasicMessage;
import net.sirun.connect.service.AVNLogin;
import net.sirun.connect.service.Bss;
import net.sirun.connect.service.Vehicle;
import net.sirun.connect.service.Weather;

public class ConnectClient{
	private static Channel channel;
//	static File certificate = new File("/Users/pj/git/xtev/connect/certificates/TBOX000012.crt");
//	static File privateKey = new File("/Users/pj/git/xtev/connect/certificates/TBOX000012.pkcs8.pem");
//	static File caCert = new File("/Users/pj/git/xtev/connect/certificates/ca.cer");

	static File certificate = new File("D:/sirun_jason/workspace/ideaworkspace/sirun.v3/connect/certificates/TBOX000012.crt");
	static File privateKey = new File("D:/sirun_jason/workspace/ideaworkspace/sirun.v3/connect//certificates/TBOX000012.pkcs8.pem");
	static File caCert = new File("D:/sirun_jason/workspace/ideaworkspace/sirun.v3/connect/certificates/ca.cer");

	public static void main(String[] args) throws Exception{
		 String host ="prod.sirun.cloud";
		//String host = "127.0.0.1";
		int port = 7780;
		final SslContext sslCtx;
		// Iterable<String> it =
		// Lists.newArrayList("TLS_RSA_WITH_AES_128_CBC_SHA"); // 强制指定协议版本

		sslCtx = SslContextBuilder.forClient().sslProvider(SslProvider.OPENSSL).trustManager(caCert).keyManager(certificate, privateKey)
				/* .ciphers(it) */.clientAuth(ClientAuth.REQUIRE).build();
		// 获取键盘输入
		new Thread(() -> {
			try{
				Thread.sleep(2000);
				readConsole();
			} catch(IOException | InterruptedException e){
				e.printStackTrace();
			}
		}).start();

		// Configure the client.
		EventLoopGroup group = new NioEventLoopGroup();
		try{
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).handler(new TcpsSnoopClientInitializer(sslCtx));
			channel = b.connect(host, port).sync().channel();
			channel.closeFuture().sync();
		} finally{
			group.shutdownGracefully();
		}
	}

	private static void readConsole() throws IOException{
		String welcome = "客户端控制台输入:" +
				"\n [5] 车机自动登录  " +
				"\n [6] 车机账户密码登录  " +
				"\n [7] 车机获取二维码 " +
				"\n [8] 车机二维码登录  " +
				"\n [9] 车机登出  " +
				"\n [x] 断开连接";
		System.out.println(welcome);
		while(true){
			if(!channel.isOpen()) System.out.println("\n\n\n  *********** \n通道已经被关闭! \n\n\n");

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String str = null;
			str = br.readLine();
			switch(str){
			
				case "5":
					channel.writeAndFlush(AVNLogin.loginRequest("client_credentials"));
                    continue;
				case "6":
					channel.writeAndFlush(AVNLogin.loginRequest("password"));
                    continue;
				case "7":
					channel.writeAndFlush(AVNLogin.qrCodeRequest());
                    continue;
				case "8":
					channel.writeAndFlush(AVNLogin.loginRequest("authorization_code"));
                    continue;
				case "9":
					channel.writeAndFlush(AVNLogin.loginOutRequest());
                    continue;
				default:
					break;
			}
			if(str.startsWith("a")){
				BasicMessage unread = TcpsSnoopClientHandler.getUnreadMessage(str.substring(1));
				if(unread != null){
					channel.writeAndFlush(unread);
				}
			} else{
				System.out.println("未知命令:[" + str+"]");
				System.out.println(welcome);
			}

		}
	}
}
