package com.model;
/**
 * 类型
 * @author Allen 
 * @date 2016年12月9日
 *
 */
public final class Type {
	private String val;

	public String getVal() {
		return val;
	}

	private Type() {
	}

	private Type(String val) {
		this.val = val;

	} 
	/**
	 * 日志类型
	 */
	public final static Type ERROR=new Type("[ERROR]:");
	public final static Type INFO=new Type("[INFO]:");  
	
}
