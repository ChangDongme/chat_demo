package com.service;

import com.model.Type;
import com.tools.ServerLog;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
/**
 * 监听消息格式模块
 * @author Allen 
 * @date 2016年12月9日
 *
 */
public final class WebSocketHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private String requestContext;

	public WebSocketHandler(String requestContext) {
		this.requestContext = requestContext;
	}
	

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		// 上下文匹配 ws://ip/{wsUri}
		if (requestContext.equalsIgnoreCase(msg.uri())) {
			//匹配交给数据层处理
			ctx.fireChannelRead(msg.retain());
		} else { 
			ServerLog.print(Type.ERROR, "webSocket数据格式匹配错误 ws://ip/{wsUri}"); 
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		cause.printStackTrace(System.err);
	}
}