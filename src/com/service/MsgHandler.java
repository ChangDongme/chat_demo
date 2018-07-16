package com.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import com.service.core.ContainerChange;
import com.service.core.action.Create;

/**
 * 
 * @author Allen
 * @date 2016年12月9日
 *
 */

public final class MsgHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	private ChannelGroup group;

	public MsgHandler(ChannelGroup group) {
		super();
		this.group = group;
	}

	/**
	 * 客户端解注册
	 */
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		ContainerChange.unReg(group, ctx);
		super.channelUnregistered(ctx);
	}

	/**
	 * 链接事件触发
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object msg) throws Exception {
		// 消息类型满足websocketWebSocket
		if (msg == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
			// 数据已经满足webSokcet消息头匹配 则清空这个配置
			ctx.pipeline().remove(WebSocketHandler.class);
			ctx.channel().writeAndFlush(new TextWebSocketFrame(Create.execute().toString()));
		} else {
			super.userEventTriggered(ctx, msg);
		}
	}

	/**
	 * 收到消息
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		new MsgExecute(group, msg, ctx).execute();
	}

	/**
	 * 异常关闭链接且抛出
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		cause.printStackTrace();
	}
}