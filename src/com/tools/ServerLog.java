package com.tools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.model.Type;
/**
 * 日志打印
 * @author Allen 
 * @date 2016年12月9日
 *
 */
public final class ServerLog {
	private static Log logger = LogFactory.getLog(ServerLog.class);

	/**
	 * 输出str
	 * @param t
	 * @param ob
	 * @author Allen
	 * @date 2016年12月9日
	 */
	public final static void print(Type t, Object... ob) {
		StringBuffer sbf = new StringBuffer(t.getVal());
		for (Object temp : ob)
			sbf.append(temp);
		logger.info(sbf.toString());
	}
	/**
	 * 输出str 且返回
	 * @param t
	 * @param ob
	 * @author Allen
	 * @date 2016年12月9日
	 */
	public  final static int print(Type t, int val) {
		StringBuffer sbf = new StringBuffer(t.getVal());
		sbf.append(String.valueOf(val));
		logger.info(sbf.toString());
		return val;
	}
	/**
	 * 输出堆栈
	 * @param t
	 * @param ob
	 * @author Allen
	 * @date 2016年12月9日
	 */
	public  final static void print(Type t, Throwable e, Object... ob) {
		StringBuffer sbf = new StringBuffer(t.getVal());
		for (Object temp : ob)
			sbf.append(temp);
		logger.info(sbf.toString(), e);
	}
	/**
	 * 输出堆栈 且返回
	 * @param t
	 * @param ob
	 * @author Allen
	 * @date 2016年12月9日
	 */
	public  final static int print(Type t, Throwable e, int val) {
		StringBuffer sbf = new StringBuffer(t.getVal());
		sbf.append(String.valueOf(val));
		logger.info(sbf.toString(), e);
		return val;
	}
}
