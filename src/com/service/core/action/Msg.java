package com.service.core.action;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.model.Data;
import com.model.JSONtype;
import com.model.SysCode;
import com.model.Type;
import com.model.UserServerPojo;
import com.service.core.DbService;
import com.tools.ServerLog;

/**
 * 消息发送
 * 
 * @author Allen
 * @date 2016年12月12日
 *
 */
public final class Msg {
	private TextWebSocketFrame msg;// webSocket消息体
	private ChannelHandlerContext ctx;
	@SuppressWarnings("unused")
	private ChannelGroup group;

	public Msg(ChannelGroup group, TextWebSocketFrame msg, ChannelHandlerContext ctx) {
		this.msg = msg;
		this.ctx = ctx;
		this.group = group;
	}

	/**
	 * 全体广播
	 * 
	 * @author Allen
	 * @date 2016年12月12日
	 */
	public void sendAll() {

	}

	/**
	 * 目标广播
	 * 
	 * @author Allen
	 * @date 2016年12月12日
	 */
	public void sendTo() {
		JSONObject json = new JSONObject();
		try {
			JSONObject msgJson = JSONObject.fromObject(msg.text());
			String info = msgJson.getString("info");
			// info信息验证
			if (info == null || info.trim().equals(""))
				json.put("result", SysCode.msg_isNull);
			else if (info.length() > Integer.parseInt(Data.sysConfig.get("maxMsg").toString()))
				json.put("result", SysCode.msg_LengthError);
			else
				msgCore(json, info, msgJson);
		} catch (JSONException e) {
			json.put("result", ServerLog.print(Type.ERROR, e, SysCode.json_FormatError));
		} catch (Exception e) {
			json.put("result", ServerLog.print(Type.ERROR, e, SysCode.sys_unknownException));
		}
		/**
		 * 返回给发送者
		 */
		json.put("type", JSONtype.MSGTO);
		ctx.channel().writeAndFlush(new TextWebSocketFrame(json.toString()));

	}

	/**
	 * 消息核心转发
	 * 
	 * @param json
	 * @param info
	 * @author Allen
	 * @date 2016年12月14日
	 */
	private void msgCore(JSONObject json, String info, JSONObject msgJson) {
		// 当前登录用户对象
		UserServerPojo usPojo = null;
		String channelId = ctx.channel().id().toString();

		if ((usPojo = Data.onlineUser.get(channelId)) == null && (usPojo = Data.onlineCustomer.get(channelId)) == null)
			// 未登录用户
			json.put("result", SysCode.user_NotLogin);
		else {
			// 客服判断to
			String fromName = null;
			Channel toChannel = null;
			if (usPojo.getUserRole() == 1) {
				int size = usPojo.getCustomerThread().size();
				for (int i = 0; i < size; i++) {
					if (usPojo.getCustomerThread().get(i).id().toString().equals(msgJson.getString("to"))) {
						fromName = usPojo.getUserName();
						toChannel = Data.onlineUser.get(msgJson.getString("to")).getUserChannel();
						break;
					}
				}

				if (fromName == null) {
					json.put("result", SysCode.customer_userNull);
					return;
				}
			} else if (usPojo.getUserRole() == 2) {
				// 用户直接取
				if (usPojo.getCustomerChannel() == null) {
					// 还未分配客服无法通讯
					json.put("result", SysCode.user_customerNull);
					return;
				} else {
					fromName = usPojo.getUserName();
					toChannel = usPojo.getCustomerChannel();
				}
			}
			DbService.saveMsg(info);
			info = keyWords(info);
			if (ctx.channel().id().toString().equals(msgJson.getString("to")))
				json.put("result", SysCode.msg_targetIsNotMe);
			else {
				JSONObject msgFrom = new JSONObject();
				msgFrom.put("type", JSONtype.MSGFROM);
				msgFrom.put("info", info);
				msgFrom.put("from", fromName);
				toChannel.writeAndFlush(new TextWebSocketFrame(msgFrom.toString()));
				json.put("result", SysCode.success);
			}
		}

	}

	/**
	 * 敏感字符过滤
	 * 
	 * @param msg
	 * @author Allen
	 * @date 2016年12月12日
	 */
	private String keyWords(String msg) {
		return msg;
	}
}
