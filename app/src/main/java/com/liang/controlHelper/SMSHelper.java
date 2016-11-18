package com.liang.controlHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Log;

import com.liang.Model.CallLogModel;
import com.liang.Model.ContactMen;
import com.liang.Model.SMSModel;
import com.liang.phonenum.utils.DatabaseHelper;

public class SMSHelper {

	private static final String TAG = SMSHelper.class.getSimpleName();

	private static SMSHelper smsHelper;

	static Context mContext;

	static DatabaseHelper dataHelper;

	private SMSHelper() {

	}

	public static SMSHelper getSMSHelper(Context context) {
		if (smsHelper == null) {
			smsHelper = new SMSHelper();
			dataHelper = DatabaseHelper.getDatabaseHelper(context
					.getApplicationContext());

		}
		mContext = context;
		return smsHelper;
	}

	public List<SMSModel> getSMSDataList() {
		List<SMSModel> smsList = new ArrayList<SMSModel>();
		smsList = dataHelper.dao.getSMSResult();
		return smsList;
	}

	public List<SMSModel> getSmsInPhone() {
		List<SMSModel> smsList = new ArrayList<SMSModel>();
		final String SMS_URI_ALL = "content://sms/";
		final String SMS_URI_INBOX = "content://sms/inbox";
		final String SMS_URI_SEND = "content://sms/sent";
		final String SMS_URI_DRAFT = "content://sms/draft";
		final String SMS_URI_OUTBOX = "content://sms/outbox";
		final String SMS_URI_FAILED = "content://sms/failed";
		final String SMS_URI_QUEUED = "content://sms/queued";

		try {
			Uri uri = Uri.parse(SMS_URI_ALL);
			String[] projection = new String[] { "_id", "address", "person",
					"body", "date", "type" };
			Cursor cur = mContext.getContentResolver().query(uri, projection,
					null, null, "date desc"); // 获取手机内部短信

			if (cur.moveToFirst()) {
				String id;
				String name;
				String phoneNumber;
				String smsbody;
				String date;
				String type;
				int idColunm = cur.getColumnIndex("_id");
				int nameColumn = cur.getColumnIndex("person");
				int phoneNumberColumn = cur.getColumnIndex("address");
				int smsbodyColumn = cur.getColumnIndex("body");
				int dateColumn = cur.getColumnIndex("date");
				int typeColumn = cur.getColumnIndex("type");
				do {

					id = cur.getInt(idColunm) + "";

					name = cur.getString(nameColumn);

					phoneNumber = cur.getString(phoneNumberColumn).replace(" ",
							"");
					phoneNumber = phoneNumber.replace("+86", "");
					phoneNumber = phoneNumber.replace(" ", "");
					phoneNumber = phoneNumber.replace("-", "");
					smsbody = cur.getString(smsbodyColumn);

					date = cur.getString(dateColumn);

					int typeId = cur.getInt(typeColumn);
					if (typeId == 1) {
						type = "" + 1;// 接收
					} else if (typeId == 2) {
						type = "" + 2;// 发送
					} else {
						type = "";
					}

					SMSModel smsModel = new SMSModel(id, phoneNumber, name,
							smsbody, date, type);
					smsList.add(smsModel);
				} while (cur.moveToNext());

				if (!cur.isClosed()) {
					cur.close();
					cur = null;
				}
			} else {
				return null;
			} // end if

		} catch (SQLiteException ex) {
			Log.d("SQLiteException in getSmsInPhone", ex.getMessage());
		}

		return smsList;
	}

	public List<SMSModel> getSMSLogByNumer(String mNumber) {
		List<SMSModel> mSmsNmberList = new ArrayList<SMSModel>();
		mSmsNmberList = dataHelper.dao.getSMSByNumber(mNumber);
		return mSmsNmberList;
	}

	public String getNumberLocation(String mNumber) {
		return dataHelper.dao.getNumberInfo(mNumber);
	}

	public List<SMSModel> groupNotifySMSList() {
		List<SMSModel> mSMSList = getSMSDataList();
		List<SMSModel> smsList = new ArrayList<SMSModel>();
		Map<String, SMSModel> smsMap = new LinkedHashMap<String, SMSModel>();

		for (SMSModel smsModel : mSMSList) {
			if (isNotifyFlag(smsModel.getAddress())) {

				if (!smsMap.containsKey(smsModel.getAddress())) {
					smsModel.setSms_count(1);
					smsMap.put(smsModel.getAddress(), smsModel);
				} else {
					smsMap.get(smsModel.getAddress()).addSms_count();
				}
			}
		}

		for (String key : smsMap.keySet()) {
			smsList.add(smsMap.get(key));
		}

		return smsList;
	}

	public ContactMen showMenDetail(String number) {
		return dataHelper.dao.getSimgleMen(number);
	}

	public SMSModel getLocalLastGroupSms(String number) {
		return dataHelper.dao.getLastGroupLog(number);

	}

	public boolean isNotifyFlag(String number) {
		boolean b = true;
		String flag = number.substring(0, 2);
		if (flag.equals("13") || flag.equals("14") || flag.equals("15")
				|| flag.equals("17") || flag.equals("19")) {
			b = false;
		}
		if (flag.subSequence(0, 1).equals("0")) {
			b = false;
		}
		return b;
	}

	public void setSmsReaded(String mNumber) {
		ContentResolver resolver = mContext.getContentResolver();
		Uri uri = Uri.parse("content://sms/");

		ContentValues values = new ContentValues();
		values.put("read", "1");

		resolver.update(uri, values, "address=?", new String[] { mNumber });

	}

	public boolean insertToSmsContent(SMSModel smsModel) {
		boolean b = false;
		ContentResolver resolver = mContext.getContentResolver();
		Uri uri = Uri.parse("content://sms/");
		ContentValues values = new ContentValues();
		values.put("address", smsModel.getAddress());
		values.put("type", smsModel.getType());
		values.put("body", smsModel.getBody());
		values.put("read", smsModel.getRead());

		// values.put("date",System.currentTimeMillis());
		resolver.insert(uri, values);
		getLatestSms(smsModel.getAddress());
		return b;
	}

	public SMSModel getLatestSms(String number) {
		SMSModel smsModel = new SMSModel();
		final String SMS_URI_ALL = "content://sms/";
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Uri uri = Uri.parse(SMS_URI_ALL);
			String[] projection = new String[] { "_id", "address", "person",
					"body", "date", "type", "read" };
			if (mContext == null) {
				Log.e(TAG, "mContext==null");
			}
			Cursor cur = mContext.getContentResolver()
					.query(uri, projection, "address=?",
							new String[] { number }, "date desc limit 0,1"); // 获取手机内部短信

			if (cur.moveToFirst()) {
				String id;
				String name;
				String phoneNumber;
				String smsbody;
				String date;
				String type;
				int idColunm = cur.getColumnIndex("_id");
				int nameColumn = cur.getColumnIndex("person");
				int phoneNumberColumn = cur.getColumnIndex("address");
				int smsbodyColumn = cur.getColumnIndex("body");
				int dateColumn = cur.getColumnIndex("date");
				int typeColumn = cur.getColumnIndex("type");
				int readColumn = cur.getColumnIndex("read");

				id = cur.getInt(idColunm) + "";

				name = cur.getString(nameColumn);

				phoneNumber = cur.getString(phoneNumberColumn).replace(" ", "");
				phoneNumber = phoneNumber.replace("+86", "");
				phoneNumber = phoneNumber.replace(" ", "");
				phoneNumber = phoneNumber.replace("-", "");
				smsbody = cur.getString(smsbodyColumn);

				date = cur.getString(dateColumn);

				int typeId = cur.getInt(typeColumn);
				int read = cur.getInt(readColumn);
				if (typeId == 1) {
					type = "" + 1;// 接收
				} else if (typeId == 2) {
					type = "" + 2;// 发送
				} else {
					type = "";
				}

				smsModel = new SMSModel(id, phoneNumber, name, smsbody, date,
						type);
				smsModel.setRead(read);
				dataHelper.dao.insertSingelSmsTotable(smsModel);

				if (!cur.isClosed()) {
					cur.close();
					cur = null;
				}
			} else {
				return null;
			} // end if

		} catch (SQLiteException ex) {
			Log.d("SQLiteException in getSmsInPhone", ex.getMessage());
		}

		return smsModel;
	}

	public List<SMSModel> groupSMSList() {
		List<SMSModel> mSMSList = getSMSDataList();
		List<SMSModel> smsList = new ArrayList<SMSModel>();
		Map<String, SMSModel> smsMap = new LinkedHashMap<String, SMSModel>();

		for (SMSModel smsModel : mSMSList) {
			if (!smsMap.containsKey(smsModel.getAddress())) {
				smsModel.setSms_count(1);
				smsMap.put(smsModel.getAddress(), smsModel);
			} else {
				smsMap.get(smsModel.getAddress()).addSms_count();
			}

		}
		int notify_count = 0;
		for (String key : smsMap.keySet()) {
			if (isNotifyFlag(smsMap.get(key).getAddress())) {
				notify_count++;
				if (notify_count == 1) {
					smsMap.get(key).setPerson("通知消息");
					smsList.add(smsMap.get(key));
				}

			} else {
				smsList.add(smsMap.get(key));
			}

		}

		return smsList;
	}
}
