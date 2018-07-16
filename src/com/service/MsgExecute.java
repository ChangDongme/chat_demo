package com.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.model.JSONtype;
import com.model.SysCode;
import com.model.Type;
import com.service.core.action.Join;
import com.service.core.action.Msg;
import com.tools.ServerLog;

/**
 * 不同消息类型处理及转发
 * 
 * @author Allen
 * @date 2016年12月12日
 *
 */
public final class MsgExecute {

	private TextWebSocketFrame msg;// webSocket消息体
	private ChannelHandlerContext ctx;
	private ChannelGroup group;

	public MsgExecute(ChannelGroup group, TextWebSocketFrame msg, ChannelHandlerContext ctx) {
		this.msg = msg;
		this.ctx = ctx;
		this.group = group;
	}

	public JSONObject execute() {
		JSONObject json = new JSONObject();
		if (msg == null || msg.text() == null) {
			json.put("result", ServerLog.print(Type.ERROR, SysCode.msg_typeError));
		} else {
			try {
				String textStr = msg.text();
				JSONObject jsonType = JSONObject.fromObject(textStr);
				String type = jsonType.getString("type");
				switch (type) {
				// 用户进入
				case JSONtype.JOIN:
					new Join(group, msg, ctx).execute(json, type);
					break;
				// 游客进入
				case JSONtype.JOINGUEST:
					new Join(group, msg, ctx).execute(json, type);
					break;
				// 点对点聊天
				case JSONtype.MSGTO:
					new Msg(group, msg, ctx).sendTo();
					break;
				}
			} catch (JSONException e) {
				json.put("result", ServerLog.print(Type.ERROR, e, SysCode.msg_typeError));
			} catch (Exception e) {
				json.put("result", ServerLog.print(Type.ERROR, e, SysCode.sys_unknownException));

			}

		}
		return json;
	}

}
