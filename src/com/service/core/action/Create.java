package com.service.core.action;

import net.sf.json.JSONObject;

import com.model.JSONtype;
import com.model.SysCode;

/**
 * 创建连接
 * 
 * @author Allen
 * @date 2016年12月12日
 *
 */
public final class Create {

	public static Object execute() {
		JSONObject json = new JSONObject();
		json.put("type", JSONtype.CREATE);
		json.put("result", SysCode.success);
		return json.toString();
	}
}
