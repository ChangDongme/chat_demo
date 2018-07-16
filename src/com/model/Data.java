package com.model;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 全局对象
 * 
 * @author Allen
 * @date 2016年12月9日
 *
 */
public final class Data {

	/**
	 * 在线用户列表<channelId.toString(),UserServerPojo>
	 */
	public static HashMap<String, UserServerPojo> onlineUser = new HashMap<String, UserServerPojo>();
	/**
	 * 在线客服列表<channelId.toString(),UserServerPojo>
	 */
	public static HashMap<String, UserServerPojo> onlineCustomer = new HashMap<String, UserServerPojo>();
	/**
	 * UserId to Channel 映射
	 */
	public static HashMap<String, Channel> idMapping = new HashMap<String, Channel>();
	/**
	 * 服务器队列
	 */
	public static ArrayList<Channel> serverQueue = null;
	/**
	 * 系统配置
	 */
	public static HashMap<Object, Object> sysConfig = new HashMap<Object, Object>();
}
