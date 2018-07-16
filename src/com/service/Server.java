package com.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

import com.model.Data;
import com.model.SysCode;
import com.model.Type;
import com.tools.ServerLog;

/**
 * netty服务主入口
 * 
 * @author Allen
 * @date 2016年12月9日
 *
 */
public final class Server {
	private final ChannelGroup group = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
	private final EventLoopGroup workerGroup = new NioEventLoopGroup();
	private Channel channel;

	private final ChannelFuture core(InetSocketAddress address) {
		ServerBootstrap boot = new ServerBootstrap();
		boot.group(workerGroup).channel(NioServerSocketChannel.class);
		boot.childHandler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				// 编码模块
				channel.pipeline().addLast(new HttpServerCodec());
				// 大数据内容写入模块
				channel.pipeline().addLast(new ChunkedWriteHandler());
				// 组装Http头信息保证完整性
				channel.pipeline().addLast(new HttpObjectAggregator(64 * 1024));
				// 上下文判断
				channel.pipeline().addLast(new WebSocketHandler(Data.sysConfig.get("context").toString()));
				// 对websocket握手的支持 io.netty 提供
				channel.pipeline().addLast(new WebSocketServerProtocolHandler(Data.sysConfig.get("context").toString()));
				// 处理文本消息
				channel.pipeline().addLast(new MsgHandler(group));
			}
		});
		ChannelFuture f = boot.bind(address).syncUninterruptibly();
		channel = f.channel();
		return f;
	}

	/**
	 * channel Group回收
	 * 
	 * @author Allen
	 * @date 2016年12月13日
	 */
	private void recovery() {

		ServerLog.print(Type.INFO, "NettyServer 资源回收");
		if (channel != null)
			channel.close();
		group.close();
		workerGroup.shutdownGracefully();
		ServerLog.print(Type.INFO, "NettyServer 资源回收完毕");
	}

	/**
	 * 启动
	 * 
	 * @author Allen
	 * @date 2016年12月13日
	 */
	public final void run() {
		ChannelFuture f = core(new InetSocketAddress(Integer.parseInt(Data.sysConfig.get("post").toString())));
		ServerLog.print(Type.INFO, "Netty端口: [", Data.sysConfig.get("post"), "] 启动状态:", SysCode.serviceRun_success);
		debug();
		// 系统异常则进行回收
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				recovery();
				ServerLog.print(Type.ERROR, SysCode.sys_isOver);
			}
		});
		f.channel().closeFuture().syncUninterruptibly();

	}

	/**
	 * 启动测试
	 */
	private final void debug() {
		if (Boolean.valueOf(Data.sysConfig.get("debug").toString())) {
			/**
			 * 开发测试使用
			 */
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						ServerLog.print(Type.INFO, "当前总人数:[", group.size(), "] 用户:[", Data.onlineUser.size(), "] 客服: [", Data.onlineCustomer.size(), "] 总:[", Data.idMapping.size(), "]");
					}
				}
			}).start();
		}
	}

}