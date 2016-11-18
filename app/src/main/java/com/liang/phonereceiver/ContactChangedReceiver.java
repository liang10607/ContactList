package com.liang.phonereceiver;

import com.liang.phonenum.utils.DatabaseHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ContactChangedReceiver extends BroadcastReceiver {

	DatabaseHelper dataHelper;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.e("ContactChangedReceiver", "更新数据库");
		dataHelper = DatabaseHelper.getDatabaseHelper(context);
		dataHelper.initDB();
	}

}
