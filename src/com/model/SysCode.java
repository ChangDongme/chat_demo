package com.model;

/**
 * 错误代码
 * 
 * @author Allen
 * @date 2016年12月9日
 *
 */
public final class SysCode {

	/**
	 * 服务已启动
	 */
	public final static int serviceRun_success = 1000;
	/**
	 * 端口参数错误,服务器无法启动
	 */
	public final static int serviceRun_postError = 1001;
	/**
	 * 未知异常
	 */
	public final static int sys_unknownException = 1002;
	/**
	 * JSON错误
	 */
	public final static int json_FormatError = 1003;
	/**
	 * JSON操作类型不匹配
	 */
	public final static int json_TypeError = 1004;
	/**
	 * 用户不存在
	 */
	public final static int user_NotFound = 1005;
	/**
	 * 操作成功
	 */
	public final static int success = 1006;
	/**
	 * 消息类型错误
	 */
	public final static int msg_typeError = 1007;
	/**
	 * 消息为空
	 */
	public final static int msg_isNull = 1008;
	/**
	 * 消息过长
	 */
	public final static int msg_LengthError = 1009;
	/**
	 * 消息目标不存在
	 */
	public final static int msg_targetIsNotFound = 1010;
	/**
	 * 无法与自己通讯
	 */
	public final static int msg_targetIsNotMe = 1011;
	/**
	 * 系统结束
	 */
	public final static int sys_isOver = 1012;
	/**
	 * 错误的remove
	 */
	public final static int remove_Error = 1013;
	/**
	 * 没有在线的客服
	 */
	public final static int admin_Offline  = 1014;
	/**
	 * 队列已满
	 */
	public final static int queue_Max  = 1015;
	/**
	 * 开始排队
	 */
	public final static int queue_Start  = 1016;
	/**
	 * 未登录
	 */
	public final static int user_NotLogin  = 1017;
	/**
	 * 还未分配客服
	 */
	public final static int user_customerNull  = 1018;
	/**
	 * 用户不存在无法通讯
	 */
	public final static int customer_userNull  = 1019;
}
