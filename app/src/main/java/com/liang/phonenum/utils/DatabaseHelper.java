package com.liang.phonenum.utils;

import java.util.List;

import com.liang.Model.CallLogModel;
import com.liang.Model.ContactMen;
import com.liang.Model.SMSModel;
import com.liang.controlHelper.CallLogHelper;
import com.liang.controlHelper.ContactHelper;
import com.liang.controlHelper.SMSHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseHelper {

	private static DatabaseHelper mInstance;

	private static Context mContext = null;

	private static SQLiteDatabase sqliteDB;

	public static DatabaseDAO dao;

	private static DataInitalBack dataInitBack;

	private DatabaseHelper(Context mContex) {
		this.mContext = mContex;
	}

	public static DatabaseHelper getDatabaseHelper(Context mContex) {

		if (mInstance == null) {
			mInstance = new DatabaseHelper(mContex);
			initDao();
		}
		return mInstance;
	}

	public void setInitDB(DataInitalBack initBack) {
		dataInitBack = initBack;
		initDB();
	}

	private static void initDao() {
		AssetsDatabaseManager.initManager(mContext);
		AssetsDatabaseManager mg = AssetsDatabaseManager
				.getAssetsDatabaseManager();
		sqliteDB = mg.getDatabase("number_location.zip");
		dao = new DatabaseDAO(sqliteDB);
	}

	public static void initDB() {
		dao.truncateAllTable();

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				CallLogHelper callLogHelper = new CallLogHelper(mContext);
				List<CallLogModel> logList = callLogHelper.getCallList();
				dao.copyCallHisTotable(logList);
				dataInitBack
						.backInitalState(DataInitalBack.initType.callLogType);

				ContactHelper contactHelper = ContactHelper
						.getContactHelper(mContext);
				List<ContactMen> menList = contactHelper.parserJSONArray();
				dao.copyMenTotable(menList);
				dataInitBack.backInitalState(DataInitalBack.initType.menType);

				SMSHelper smsHelper = SMSHelper.getSMSHelper(mContext);
				List<SMSModel> smsList = smsHelper.getSmsInPhone();

				dao.copySmsTotable(smsList);
				dataInitBack.backInitalState(DataInitalBack.initType.smsType);
			}

		}).start();
		//
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		//
		// }
		// }).start();
		//
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		//
		//
		// }
		// }).start();
	}

	public static void closeDataBase() {
		AssetsDatabaseManager.closeAllDatabase();
	}

	public void setDataInitBack(DataInitalBack dataInitBack) {
		this.dataInitBack = dataInitBack;
	}

}
