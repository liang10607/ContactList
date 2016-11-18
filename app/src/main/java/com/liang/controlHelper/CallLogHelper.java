package com.liang.controlHelper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

import com.liang.Model.CallLogModel;

import com.liang.phonecontactlist.R;
import com.liang.phonenum.utils.DatabaseHelper;

public class CallLogHelper {

	private Context context;

	DatabaseHelper dataHelper;

	public static Map<String, CallLogModel> call_Map = new HashMap<String, CallLogModel>();

	private final static String TAG = "CallLogHelper";

	private String number;

	private String name;

	private int call_type;

	private String call_date;

	private String call_Dur;

	public CallLogHelper(Context context) {
		this.context = context;
		if (dataHelper == null) {
			dataHelper = DatabaseHelper.getDatabaseHelper(context
					.getApplicationContext());
		}

	}

	public void getlogcalData() {
		String phoneAdr = null;
		String phoneOpe = null;

		final Cursor cursor = context.getContentResolver().query(
				CallLog.Calls.CONTENT_URI,
				new String[] { CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME,
						CallLog.Calls.TYPE, CallLog.Calls.DATE,
						CallLog.Calls.DURATION }, null, null,
				CallLog.Calls.DEFAULT_SORT_ORDER);

		for (int i = 0; i < cursor.getCount(); i++) {
			try {
				cursor.moveToPosition(i);
				number = cursor.getString(0);
				name = cursor.getString(1);
				call_type = cursor.getInt(2);
				call_date = cursor.getString(3);
				call_Dur = cursor.getString(4);
				int img_id = R.drawable.headnew;
				Map<String, String> map = new HashMap<String, String>();
				map = dataHelper.dao.getNumberLocation(number);
				if (dataHelper.dao == null) {
					Log.e(TAG, "dataHelper.dao==null" + map.toString());
				}
				// Log.e(TAG, "map:"+map.toString());

				if (dataHelper.dao.isZeroStarted(number)) {
					phoneAdr = map.get("city");
					phoneOpe = "";
				} else {
					if (map != null && map.get("province") != null
							&& map.get("city") != null) {
						phoneAdr = map.get("province") + map.get("city");

						phoneOpe = map.get("telecom").replace("中国", "");
					} else {
						phoneAdr = "";
					}

				}

				CallLogModel callLogMen = new CallLogModel(call_date,
						call_type, img_id, name, number, phoneAdr, phoneOpe,
						call_Dur);
				call_Map.put(call_date, callLogMen);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}

	}

	public boolean insertLastCall() {
		boolean b;
		String phoneAdr = null;
		String phoneOpe = null;
		CallLogModel callLogMen = null;

		Cursor cursor = context.getContentResolver().query(
				CallLog.Calls.CONTENT_URI,
				new String[] { CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME,
						CallLog.Calls.TYPE, CallLog.Calls.DATE,
						CallLog.Calls.DURATION }, null, null,
				CallLog.Calls.DEFAULT_SORT_ORDER+" limit 0,1");// + 

		try {
			if (cursor.moveToFirst()) {

				number = cursor.getString(0);
				name = cursor.getString(1);
				call_type = cursor.getInt(2);
				call_date = cursor.getString(3);
				call_Dur = cursor.getString(4);
				int img_id = R.drawable.headnew;
				Map<String, String> map = new HashMap<String, String>();
				map = dataHelper.dao.getNumberLocation(number);
				if (dataHelper.dao == null) {
					Log.e(TAG, "dataHelper.dao==null" + map.toString());
				}

				if (dataHelper.dao.isZeroStarted(number)) {
					phoneAdr = map.get("city");
					phoneOpe = "";
				} else {
					if (map != null && map.get("province") != null
							&& map.get("city") != null) {
						phoneAdr = map.get("province") + map.get("city");

						phoneOpe = map.get("telecom").replace("中国", "");
					} else {
						phoneAdr = "";
					}

				}

				callLogMen = new CallLogModel(call_date, call_type, img_id,
						name, number, phoneAdr, phoneOpe, call_Dur);
			 
			}
			b = dataHelper.dao.insertSingleCallLog(callLogMen);
			b = true;
		} catch (NullPointerException e) {
			e.printStackTrace();
			b = false;
		}

		return b;
	}

	public List<CallLogModel> getCallList() {
		List<CallLogModel> callLogList = new ArrayList<CallLogModel>();
		getlogcalData();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Iterator<String> it = call_Map.keySet().iterator();
		while (it.hasNext()) {

			String key = it.next().toString();

			callLogList.add(call_Map.get(key));
		}
		Collections.sort(callLogList, new CompratorByFileName());

		return callLogList;
	}

	public List<CallLogModel> getSixCallList() {
		// List<CallLogModel> result = new ArrayList<CallLogModel>();
		List<CallLogModel> callLogList = new ArrayList<CallLogModel>();
		callLogList = dataHelper.dao.getSixCallLogResult();

		return callLogList;
	}

	public List<CallLogModel> getDataCallList() {
		List<CallLogModel> callLogList = new ArrayList<CallLogModel>();
		callLogList = dataHelper.dao.getCallLogResult();
		return callLogList;
	}

	private static class CompratorByFileName implements
			Comparator<CallLogModel> {

		@Override
		public int compare(CallLogModel lhs, CallLogModel rhs) {
			Comparator<Object> cmp = Collator
					.getInstance(java.util.Locale.CHINA);
			return cmp.compare(rhs.getCallTime(), lhs.getCallTime());
		}

		@Override
		public boolean equals(Object o) {
			return true;
		}

	}

}
