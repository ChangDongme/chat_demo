package com.model;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.Channel;

/**
 * 用户服务器通讯端数据
 * 
 * @author Allen
 * @date 2016年12月9日
 *
 */
public final class UserServerPojo implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8391821418568055499L;

	public Channel userChannel;// netty Channel
	public String userName;// 用户昵称
	public String userId;// 用户id
	public String userHead;// 用户头像
	public int userRole;// 用户身份 2用户 1客服
	public List<Channel> customerThread;// 客户队列
	public Channel customerChannel;// 服务我的客服

	public UserServerPojo() {
		// TODO Auto-generated constructor stub
	}

	public void setAll(Channel userChannel, String userName, String userId, String userHead, int userRole) {
		this.userChannel = userChannel;
		this.userName = userName;
		this.userId = userId;
		this.userHead = userHead;
		this.userRole = userRole;
	}

	/**
	 * 按配置初始化创建客服队列
	 * 
	 * @author Allen
	 * @date 2016年12月13日
	 */
	public void ini() {
		this.customerThread = new ArrayList<Channel>(Integer.parseInt(Data.sysConfig.get("customerQueue").toString()));
	}

	public Channel getCustomerChannel() {
		return customerChannel;
	}

	/**
	 * sync
	 * @param customerChannel
	 * @author Allen
	 * @date 2016年12月14日
	 */
	public synchronized void setCustomerChannel(Channel customerChannel) {
		this.customerChannel = customerChannel;
	}

	public List<Channel> getCustomerThread() {
		return customerThread;
	}

	public void setCustomerThread(List<Channel> customerThread) {
		this.customerThread = customerThread;
	}

	public Channel getUserChannel() {
		return userChannel;
	}

	public void setUserChannel(Channel userChannel) {
		this.userChannel = userChannel;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserHead() {
		return userHead;
	}

	public void setUserHead(String userHead) {
		this.userHead = userHead;
	}

	public int getUserRole() {
		return userRole;
	}

	public void setUserRole(int userRole) {
		this.userRole = userRole;
	}

}
