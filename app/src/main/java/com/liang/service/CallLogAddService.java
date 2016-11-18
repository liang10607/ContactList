package com.liang.service;

import com.liang.controlHelper.CallLogHelper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CallLogAddService extends Service {

	private static final String TAG = "CallLogAddService";

	private int runTime = 0;

	CallLogHelper callLogHelper;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		runTime = 0;
		Log.e(TAG, "CallLogAddService 创建create");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.e(TAG, "CallLogAddService 启动onStartCommand");
		runTime = 0;
		if (callLogHelper == null) {
			callLogHelper = new CallLogHelper(getApplicationContext());
		}
		insertLastCallLog();

		return super.onStartCommand(intent, flags, startId);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();
		Log.e(TAG, "CallLogAddService 销毁onDestroy");
	}

	public void insertLastCallLog() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
					Thread.sleep(1000);
					callLogHelper.insertLastCall();

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				stopSelf();
			}
		}).start();
	}

}
