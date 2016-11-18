package com.liang.MyUtil;

public interface KeyBoardListener {

	public static enum KEY_NAME {
		KEY0, KEY1, KEY2, KEY3, KEY4, KEY5, KEY6, KEY7, KEY8, KEY9, KEY_JING, KEY_XING, KEY_DELETE, KEY_DIAL, KEY_SHOWBOARD
	}

	public void keyUp(KEY_NAME key);

}
