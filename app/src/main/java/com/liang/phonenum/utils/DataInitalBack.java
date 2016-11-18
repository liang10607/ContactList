package com.liang.phonenum.utils;

public interface DataInitalBack {
	public static enum initType {
		smsType, menType, callLogType
	}

	public void backInitalState(initType type);
}
