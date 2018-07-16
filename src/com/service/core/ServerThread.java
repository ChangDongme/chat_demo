package com.service.core;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Iterator;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import com.model.Data;
import com.model.JSONtype;
import com.model.SysCode;
import com.model.Type;
import com.model.UserServerPojo;
import com.tools.ServerLog;

/**
 * 服务器队列
 * 
 * @author Allen
 * @date 2016年12月13日
 *
 */
public class ServerThread implements Runnable {
	JSONObject json;

	public ServerThread(JSONObject json) {
		this.json = json;
	}

	public ServerThread() {
		this.json = new JSONObject();
	}

	/**
	 * 服务器队列
	 * 
	 * @param json
	 * @author Allen
	 * @date 2016年12月13日
	 */
	public void execute(JSONObject json, Channel channel) {
		if (Data.onlineCustomer.size() == 0) {
			// 没有在线的客服
			json.put("result", SysCode.admin_Offline);
			json.put("note", Data.sysConfig.get("offlineNote"));
			channel.writeAndFlush(new TextWebSocketFrame(json.toString()));
		}
		if (Data.serverQueue.size() > 0)
			Data.serverQueue.add(channel);
		else
			regThread(channel, true);
	}

	/**
	 * 注册到队列
	 * 
	 * @param flag
	 *            = true 则考虑是否插入到服务器队列</br> flag = false 则直接发送最新队列状态index
	 * 
	 * @author Allen
	 * @date 2016年12月15日
	 */
	public synchronized void regThread(Channel channel, Boolean flag) {
		String channelId = findCustomer();
		// 没有空闲的客服
		if (channelId == null) {
			int serverSize = Integer.parseInt(Data.sysConfig.get("serverQueue").toString());
			if (flag && Data.serverQueue.size() < serverSize) {
				json = new JSONObject();
				// 有队可以排
				Data.serverQueue.add(channel);
				json.put("result", SysCode.queue_Start);
				json.put("position", Data.serverQueue.size());
				channel.writeAndFlush(new TextWebSocketFrame(json.toString()));
			} else if (!flag) {
				sendServerThreadPosition();
			} else {
				json.put("result", SysCode.queue_Max);
				channel.writeAndFlush(new TextWebSocketFrame(json.toString()));
				// 这里可以选择清空该用户，也可以选择不清空
				// 清空则踢下线，不清空则等待前端再次尝试连接等
			}
		} else {
			send(channel, channelId);
			if (!flag) {
				Data.serverQueue.remove(channel);
				ServerThread.sendServerThreadPosition();
			}
		}
	}

	/**
	 * 将消息发送到用户与客服 并绑定双方数据关系
	 * 
	 * @param channel
	 * @param channelId
	 * @author Allen
	 * @date 2016年12月15日
	 */
	private void send(Channel channel, String channelId) {
		// 客服信息
		UserServerPojo customerPojo = Data.onlineCustomer.get(channelId);
		// 用户信息
		UserServerPojo userPojo = Data.onlineUser.get(channel.id().toString());
		// 将用户channel绑定到到客服
		// 通知客服新用户进入
		Data.onlineCustomer.get(channelId).getCustomerThread().add(channel);
		JSONObject customerJson = new JSONObject();
		customerJson.put("type", JSONtype.USERCOMING);
		customerJson.put("user", bindPojo(userPojo));
		customerPojo.getUserChannel().writeAndFlush(new TextWebSocketFrame(customerJson.toString()));
		// 将客服channel绑定到用户
		// 将客服信息通知给用户
		Data.onlineUser.get(channel.id().toString()).setCustomerChannel(customerPojo.getUserChannel());
		this.json.put("customer", bindPojo(customerPojo));
		channel.writeAndFlush(new TextWebSocketFrame(this.json.toString()));
	}

	/**
	 * 找到服务人数最少的客服
	 */
	private String findCustomer() {
		String channelId = null;
		int size = Integer.parseInt(Data.sysConfig.get("customerQueue").toString());
		Iterator<Entry<String, UserServerPojo>> it = Data.onlineCustomer.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, UserServerPojo> entry = it.next();
			if (entry.getValue().getCustomerThread().size() == 0) {
				channelId = entry.getKey();
				break;
			} else if (entry.getValue().getCustomerThread().size() < size) {
				size = entry.getValue().getCustomerThread().size();
				channelId = entry.getKey();
			}
		}
		return channelId;
	}

	/**
	 * 组装pojo
	 * 
	 * @param userPojo
	 * @return
	 * @author Allen
	 * @date 2016年12月15日
	 */
	private JSONObject bindPojo(UserServerPojo userPojo) {
		JSONObject userJson = new JSONObject();
		userJson.put("userName", userPojo.getUserName());
		userJson.put("userHead", userPojo.getUserHead());
		userJson.put("userId", userPojo.getUserId());
		userJson.put("cId", userPojo.getUserChannel().id().toString());
		return userJson;
	}

	/**
	 * 发送队列新位置消息
	 * 
	 * @see sync
	 * @author Allen
	 * @date 2016年12月15日
	 */
	public static void sendServerThreadPosition() {
		for (int i = 0; i < Data.serverQueue.size(); i++) {
			JSONObject queueJson = new JSONObject();
			queueJson.put("type", JSONtype.QUEUESTATUS);
			queueJson.put("position", i + 1);
			Data.serverQueue.get(i).writeAndFlush(new TextWebSocketFrame(queueJson.toString()));
		}
	}

	/**
	 * 队列入会
	 * 
	 * @author Allen
	 * @date 2016年12月15日
	 */
	private void threadJoinMeeting(int i) {
		// 排队异常准备
		JSONObject successJSON = new JSONObject();
		successJSON.put("type", JSONtype.QUEUESUCCESS);
		if (Data.serverQueue.size() == 0)
			return;
		if (Data.onlineCustomer.size() == 0) {
			// 没有在线的客服
			JSONObject json = new JSONObject();
			json.put("type", JSONtype.QUEUEERROR);
			json.put("result", SysCode.admin_Offline);
			json.put("note", Data.sysConfig.get("offlineNote"));
			Data.serverQueue.get(i).writeAndFlush(new TextWebSocketFrame(json.toString()));
		} else {
			regThread(Data.serverQueue.get(i), false);
		}
	}

	@Override
	public void run() {
		// 队列监听系统状态有位置则进入
		long t = Long.valueOf(Data.sysConfig.get("serverQueueTime").toString());
		while (true) {
			try {
				threadJoinMeeting(0);
				Thread.sleep(t);
			} catch (Exception e) {
				ServerLog.print(Type.ERROR, e, SysCode.sys_unknownException);
			}
		}
	}

}
