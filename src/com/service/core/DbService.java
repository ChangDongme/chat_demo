package com.service.core;

import java.util.Random;

import net.sf.json.JSONObject;

/**
 * DB操作
 *  
 * @author Allen
 * @date 2016年12月9日
 *
 */
public final class DbService {

	/**
	 * 保存消息
	 * 
	 * @return
	 * @author Allen
	 * @date 2016年12月9日
	 */
	public static void saveMsg(String info) {
	}

	/**
	 * 验证用户信息
	 * @see 待DB接入
	 * @param msg
	 * @return
	 * @author Allen
	 * @date 2016年12月9日
	 */
	@SuppressWarnings("unused")
	public static JSONObject checkUser(String msg) throws NullPointerException, IndexOutOfBoundsException {
		// 用户账号#密码MD5-64bit
		JSONObject userJson = JSONObject.fromObject(msg);
		String account = userJson.getString("account");
		String passwordMd5 = userJson.getString("password");
		// 调用check验证用户身份 账号密码匹配数据库
		userJson.clear();
		if (account.equals("admin")) {
			userJson.put("userName", "客服大人");
			userJson.put("userHead", "tx");
			userJson.put("userId", new Random().nextInt(899)+100);
			userJson.put("userRole", "1"); // 1客服 2用户
			
		} else {
			userJson.put("userName", "U用户");
			userJson.put("userHead", "tx");
			userJson.put("userId", new Random().nextInt(89999)+10000);
			userJson.put("userRole", "2");  
		}
		return userJson;
	}

	/**
	 * 离开聊天室
	 * 
	 * @return
	 * @author Allen
	 * @date 2016年12月9日
	 */
	public static String leave() {
		return null;
	}
}
