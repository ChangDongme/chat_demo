package com.tools;

import io.netty.channel.Channel;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import com.model.Data;
import com.model.Type;

/**
 * 初始化配置文件到com.model.Data
 * 
 * @author Allen
 * @date 2016年12月9日
 *
 */
public final class IniConf {

	public final void iniConf() {
		Properties prop = getProperties("SysConfig.properties");
		Iterator<Entry<Object, Object>> it = prop.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Object, Object> entry = it.next();
			Data.sysConfig.put(entry.getKey(), entry.getValue());
		}
		Data.serverQueue = new ArrayList<Channel>(Integer.parseInt(Data.sysConfig.get("serverQueue").toString()));
		ServerLog.print(Type.INFO, "初始化成功");
	}

	/**
	 * 从classLoader中获取文件
	 * 
	 * @param propertiesPath
	 * @return
	 * @author Allen
	 * @date 2016年12月9日
	 */
	private final static Properties getProperties(String propertiesPath) {
		Properties prop = new Properties();
		try {
			prop.load(new InputStreamReader(IniConf.class.getClassLoader().getResourceAsStream(propertiesPath), "UTF-8"));
			return prop;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
