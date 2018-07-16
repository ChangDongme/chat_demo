package com.service.core.action;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Random;

import net.sf.json.JSONObject;

import com.model.Data;
import com.model.JSONtype;
import com.model.SysCode;
import com.model.Type;
import com.model.UserServerPojo;
import com.service.core.ContainerChange;
import com.service.core.DbService;
import com.service.core.ServerThread;
import com.tools.ServerLog;

/**
 * 进入聊天室
 * 
 * @author Allen
 * @date 2016年12月9日
 *
 */
public final class Join {
	private TextWebSocketFrame msg;// webSocket消息体
	private ChannelHandlerContext ctx;
	private ChannelGroup group;

	public Join(ChannelGroup group, TextWebSocketFrame msg, ChannelHandlerContext ctx) {
		this.msg = msg;
		this.ctx = ctx;
		this.group = group;
	}

	/**
	 * 加入聊天室
	 * 
	 * @param json
	 * @param type
	 * @author Allen
	 * @date 2016年12月12日
	 */
	public void execute(JSONObject json, String type) {
		// 新用户链接消息加入
		try {
			new ContainerChange().localLoginCheck(group, ctx);
			UserServerPojo usPojo = new UserServerPojo();
			int flag;
			if (type.equals(JSONtype.JOIN))
				flag = user(usPojo, ctx.channel(), msg.text());
			else
				flag = guest(usPojo, ctx.channel());
			// 数据正确
			if (flag == SysCode.success) {
				JSONObject userJson = bindUserInfo(usPojo);
				json.put("type", type);
				json.put("result", flag);
				json.put("info", userJson);
				
				// 用户登录
				if (usPojo.getUserRole() == 2) {
					Data.onlineUser.put(ctx.channel().id().toString(), usPojo);
					new ServerThread().execute(json, ctx.channel());
				} else if (usPojo.getUserRole() == 1) {
					//客服登录
					Data.onlineCustomer.put(ctx.channel().id().toString(), usPojo);
					ctx.channel().writeAndFlush(new TextWebSocketFrame(json.toString()));
				}
				Data.idMapping.put(usPojo.getUserId(), ctx.channel());
				// 新用户添加到channelGroup
				group.add(ctx.channel());
			} else {
				json.put("type", type);
				json.put("result", flag);
				ctx.channel().writeAndFlush(new TextWebSocketFrame(json.toString()));
			}
		} catch (NullPointerException e) {
			ServerLog.print(Type.ERROR, e, SysCode.remove_Error);
		} catch (Exception e) {
			ServerLog.print(Type.ERROR, e, SysCode.sys_unknownException);
		}
	}

	/**
	 * 聊天室（用户）
	 * 
	 * @param channel
	 * @param msg
	 *            type:join str:msg 注册用户带注册信息链接并{@link DbService.checkUser}认证
	 * @return
	 * @author Allen
	 * @date 2016年12月9日
	 */
	private int user(UserServerPojo usPojo, Channel channel, String msg) {
		try {
			JSONObject json = JSONObject.fromObject(msg);
			// 注册用户
			JSONObject userJson = null;
			//json.getString(JSONtype.INFO)
			if ((userJson = DbService.checkUser(msg)) != null) {
				new ContainerChange().elesWhereLoginCheck(group, userJson.getString("userId"));
				usPojo.setAll(channel, userJson.getString("userName"), userJson.getString("userId"), userJson.getString("userHead"), Integer.parseInt(userJson.getString("userRole")));
				if (Integer.parseInt(userJson.getString("userRole")) == 1) {
					// 客服登录初始化客服容器
					usPojo.ini();
				}
			} else {
				return ServerLog.print(Type.ERROR, SysCode.user_NotFound);
			}
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			return ServerLog.print(Type.ERROR, e, SysCode.json_FormatError);
		} catch (Exception e) {
			return ServerLog.print(Type.ERROR, e, SysCode.sys_unknownException);
		}
		return SysCode.success;
	}

	/**
	 * 聊天室（访客）
	 * 
	 * @param channel
	 * @param msg
	 *            type:join str:msg
	 * @return
	 * @author Allen
	 * @date 2016年12月9日
	 */
	private int guest(UserServerPojo usPojo, Channel channel) {
		try {
			// 访客随机id,需求确定后做英文大小写数字组合
			String temp = String.valueOf(new Random().nextInt(89999999) + 10000000);
			usPojo.setAll(channel, new StringBuffer("访客").append(temp).toString(), String.valueOf(temp), Data.sysConfig.get("defaultHead").toString(), 2);
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			return ServerLog.print(Type.ERROR, e, SysCode.json_FormatError);
		} catch (Exception e) {
			return ServerLog.print(Type.ERROR, e, SysCode.sys_unknownException);
		}
		return SysCode.success;
	}

	/**
	 * 组装用户信息到json
	 * 
	 * @param usPojo
	 * @return
	 * @author Allen
	 * @date 2016年12月13日
	 */
	private JSONObject bindUserInfo(UserServerPojo usPojo) {
		JSONObject userJson = new JSONObject();
		userJson.put("userName", usPojo.getUserName());
		userJson.put("userHead", usPojo.getUserHead());
		userJson.put("userId", usPojo.getUserId());
		userJson.put("userRole", usPojo.getUserRole());
		userJson.put("cId", ctx.channel().id().toString());
		return userJson;
	}
}
