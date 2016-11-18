package com.liang.phonenum.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.liang.Model.CallLogModel;
import com.liang.Model.ContactMen;
import com.liang.Model.SMSModel;
import com.liang.MyUtil.ContactTimeUtils;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseDAO {

	private final static String TAG = "DatabaseDAO";
	private SQLiteDatabase db;

	private static String callLog_table = "call_history";

	private static String men_table = "contact_men";

	private static String sms_table = "sms_local";

	private static String smsGroup_table = "smsgroup_local";

	public DatabaseDAO(SQLiteDatabase db) {
		this.db = db;
	}

	private Map<String, String> queryAeraCode(String number) {
		return queryNumber("0", number);
	}

	public String getNumberInfo(String phoneNumber) {
		String result = null;
		String prefix, num;
		Map<String, String> map = null;

		if (phoneNumber.indexOf("+86") != -1) {
			phoneNumber = phoneNumber.replace("+86", "");

		}

		if (isZeroStarted(phoneNumber) && getNumLength(phoneNumber) > 2) {
			num = getAreaCodePrefix(phoneNumber);
			map = queryAeraCode(num);
			result = map.get("city") + " 固话  ";

		} else if (!isZeroStarted(phoneNumber) && getNumLength(phoneNumber) > 6) {
			prefix = getMobilePrefix(phoneNumber);
			num = getMobileNumber(phoneNumber);
			map = queryNumber(prefix, num);
			try {
				result = map.get("province") + map.get("city") + " "
						+ map.get("telecom").replace("中国", "");
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		if (result == null) {
			result = "";
		}
		return result;
	}

	public String getnumName(String number) {
		String result = "";
		if (isTableExists(men_table)) {
			String sql = "select contact_name from " + men_table
					+ " where contact_number= '" + number + "'";

			result = getCursorResult(sql).get("contact_name");
		}
		return result;
	}

	public Map<String, String> getNumberLocation(String phoneNumber) {

		String prefix, num;
		Map<String, String> map = null;

		if (isZeroStarted(phoneNumber) && getNumLength(phoneNumber) > 2) {
			num = getAreaCodePrefix(phoneNumber);
			map = queryAeraCode(num);

		} else if (!isZeroStarted(phoneNumber) && getNumLength(phoneNumber) > 6) {
			prefix = getMobilePrefix(phoneNumber);
			num = getMobileNumber(phoneNumber);
			map = queryNumber(prefix, num);
		}
		return map;
	}

	private String getAreaCodePrefix(String number) {
		if (number.charAt(1) == '1' || number.charAt(1) == '2')
			return number.substring(1, 3);
		return number.substring(1, 4);
	}

	private String getMobilePrefix(String number) {
		return number.substring(0, 3);
	}

	private String getMobileNumber(String number) {
		return number.substring(0, 7);
	}

	public boolean isZeroStarted(String number) {
		if (number == null || number.isEmpty()) {
			return false;
		}
		return number.charAt(0) == '0';
	}

	private int getNumLength(String number) {
		if (number == null || number.isEmpty())
			return 0;
		return number.length();
	}

	public boolean insertSimgleMen(ContactMen iMen) {
		boolean result = true;

		String sql = "insert into " + men_table + " values ('"
				+ iMen.getContactId() + "'" + ",'" + iMen.getName() + "'"
				+ ",'" + iMen.getONumber() + "'" + ",'"
				+ String.valueOf(iMen.getImg_id()) + "','0" + "')";
		try {
			excuteSQL(sql);
		} catch (SQLException e) {
			// TODO: handle exception
			result = false;
		}

		return result;
	}

	public SMSModel getLastGroupLog(String number) {
		SMSModel smsModel = new SMSModel();
		String sql = "select * from " + sms_table + " where number='" + number
				+ "' order by date desc limit 0,1";
		Cursor cursor = getCursor(sql);
		if (cursor.moveToFirst()) {
			String id = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(0)));
			String name = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(2)));
			String body = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(3)));
			String date = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(4)));
			String type = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(5)));

			smsModel = new SMSModel(id, number, name, body, date, type);

		}

		return smsModel;
	}

	public ContactMen getSimgleMen(String number) {
		ContactMen calllogm = new ContactMen();
		String sql = "select * from " + men_table + " where contact_number= "
				+ number;
		Cursor cursor = getCursor(sql);
		if (cursor.moveToFirst()) {

			String men_id = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(0)));
			String men_name = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(1)));
			String men_number = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(2)));
			String men_photoId = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(3)));

			calllogm = new ContactMen(men_id, men_number, men_name,
					Integer.parseInt(men_photoId));

		}
		return calllogm;
	}

	public boolean insertSingelSmsTotable(SMSModel mSMSModel) {
		boolean result = true;

		String sql = "insert into " + sms_table + " values ('"
				+ mSMSModel.getId() + "'" + ",'" + mSMSModel.getAddress() + "'"
				+ ",'" + mSMSModel.getPerson() + "'" + ",'"
				+ mSMSModel.getBody().replace("'", "''") + "'" + ",'"
				+ mSMSModel.getDate() + "'" + ",'" + mSMSModel.getType() + "'"
				+ ",'" + String.valueOf(mSMSModel.getSms_count()) + "'" + ")";

		try {
			excuteSQL(sql);
		} catch (SQLException e) {
			// TODO: handle exception
			result = false;
			Log.e(TAG, "出现SQLException");
		}

		return result;
	}

	public String isSmsGoupExist(List<ContactMen> groupNumMen) {
		String result = "";
		String sql = "select * from " + smsGroup_table;
		Cursor cursor = getCursor(sql);
		while (cursor.moveToNext()) {
			String strNumber = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(1)));
			String temp[] = strNumber.split(",&");
			if (isNumberMatching(groupNumMen, temp))
				result = cursor.getString(cursor.getColumnIndex(cursor
						.getColumnName(0)));
			;
		}
		return result;
	}

	private boolean isNumberMatching(List<ContactMen> groupNumMen, String[] temp) {
		boolean result = false;
		List<String> multiNumber = new ArrayList<String>();
		if (groupNumMen.size() == temp.length) {
			for (int i = 0; i < groupNumMen.size(); i++) {
				multiNumber.add(groupNumMen.get(i).getNumber());
			}
			for (int j = 0; j < temp.length; j++) {
				if (multiNumber.contains(temp[j])) {
					result = true;
				} else {
					result = false;
				}
			}
		}
		return result;
	}

	public String[] getGroupSmsNumber(String createTime) {
		String sql = "select all_number from " + smsGroup_table
				+ " where create_date='" + createTime + "'";
		Cursor cursor = getCursor(sql);
		String temp[] = null;
		while (cursor.moveToNext()) {
			String strNumber = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(0)));
			temp = strNumber.split(",&");
		}
		return temp;
	}

	public String getSmsGroupName(String groupNumber) {
		String sql = "select person from " + sms_table + " where number='"
				+ groupNumber + "'";
		Cursor cursor = getCursor(sql);
		String temp = null;
		if (cursor.moveToFirst()) {
			temp = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(0)));

		}
		return temp;
	}

	public boolean insertGroupSmsToTable(SMSModel smsModel,
			List<ContactMen> groupNumMen, String curTime) {
		boolean result = true;
		createGroupSmsTable(smsGroup_table);
		if (isSmsGoupExist(groupNumMen).equals("")) {
			String numBody = "";
			for (int i = 0; i < groupNumMen.size(); i++) {
				numBody = numBody + groupNumMen.get(i).getNumber() + ",&";
			}

			String sql1 = "insert into " + smsGroup_table + " values ('"
					+ curTime + "','" + numBody + "')";
			excuteSQL(sql1);
			smsModel.setAddress(curTime);
			Log.e(TAG, "该群发组不存在");
		} else {
			Log.e(TAG, "该群发组已经存在");
			smsModel.setAddress(isSmsGoupExist(groupNumMen));
		}
		String sql = "insert into " + sms_table + " values ('"
				+ smsModel.getId() + "'" + ",'" + smsModel.getAddress() + "'"
				+ ",'" + smsModel.getPerson() + "'" + ",'"
				+ smsModel.getBody().replace("'", "''") + "'" + ",'"
				+ smsModel.getDate() + "'" + ",'" + smsModel.getType() + "'"
				+ ",'" + String.valueOf(smsModel.getSms_count()) + "'" + ")";

		try {
			excuteSQL(sql);
		} catch (SQLException e) {
			// TODO: handle exception
			result = false;
			Log.e(TAG, "出现SQLException");
		}

		return result;
	}

	public boolean copySmsTotable(List<SMSModel> smsList) {
		boolean result = true;
		if (!isTableExists(sms_table)) {
			createSmsTable(sms_table);
		}

		if (smsList == null || smsList.size() < 1) {
			return false;
		}
		for (int i = 0; i < smsList.size(); i++) {
			String sql = "insert into " + sms_table + " values ('"
					+ smsList.get(i).getId() + "'" + ",'"
					+ smsList.get(i).getAddress() + "'" + ",'"
					+ smsList.get(i).getPerson() + "'" + ",'"
					+ smsList.get(i).getBody().replace("'", "''") + "'" + ",'"
					+ smsList.get(i).getDate() + "'" + ",'"
					+ smsList.get(i).getType() + "'" + ",'"
					+ String.valueOf(smsList.get(i).getSms_count()) + "'" + ")";

			try {
				excuteSQL(sql);
			} catch (SQLException e) {
				// TODO: handle exception
				result = false;
				Log.e(TAG, "出现SQLException");
			}

		}
		return result;
	}

	public boolean insertSingleMen(ContactMen iMen) {
		boolean b = false;
		if (iMen == null) {
			return false;
		}

		Log.e(TAG, "再次查询出来的" + iMen.toString());
		String sql1 = "insert into " + men_table + " values ('"
				+ iMen.getContactId() + "'" + ",'" + iMen.getName() + "'"
				+ ",'" + iMen.getONumber() + "'" + ",'"
				+ String.valueOf(iMen.getImg_id()) + "','0" + "')";
		try {
			excuteSQL(sql1);
		} catch (SQLException e) {
			// TODO: handle exception
			b = false;
		}

		return b;
	}

	public boolean copyMenTotable(List<ContactMen> menList) {
		boolean result = true;

		if (!isTableExists(men_table)) {
			createMenTable(men_table);
		}

		if (menList == null || menList.size() < 1) {
			return false;
		}

		for (int i = 0; i < menList.size(); i++) {
			String sql = "insert into " + men_table + " values ('"
					+ menList.get(i).getContactId() + "'" + ",'"
					+ menList.get(i).getName() + "'" + ",'"
					+ menList.get(i).getONumber() + "'" + ",'"
					+ String.valueOf(menList.get(i).getImg_id()) + "','"
					+ "0')";
			try {
				excuteSQL(sql);
			} catch (SQLException e) {
				// TODO: handle exception
				result = false;
			}

		}
		return result;
	}

	public boolean deleteContactData(ContactMen iMen) {
		boolean result = false;
		String sql = "delete from " + men_table + " where contact_id='"
				+ iMen.getContactId() + "'";
		try {
			excuteSQL(sql);
			result = true;
		} catch (SQLException e) {
			// TODO: handle exception
			result = false;
			e.printStackTrace();
		}
		return result;
	}

	public boolean addContactData(ContactMen iMen) {
		boolean result = false;
		String sql = "insert into " + men_table + " values ('"
				+ iMen.getContactId() + "'" + ",'" + iMen.getName() + "'"
				+ ",'" + iMen.getNumber() + "'" + ",'"
				+ String.valueOf(iMen.getImg_id()) + "','0" + "')";
		try {
			excuteSQL(sql);
			result = true;
		} catch (SQLException e) {
			// TODO: handle exception
			result = false;
			// e.printStackTrace();
		}
		return result;
	}

	public boolean truncateAllTable() {
		boolean result = false;
		String sql1 = "delete from " + men_table + " where 1=1";
		String sql2 = "delete from " + callLog_table + " where 1=1";
		String sql3 = "delete from " + sms_table + " where 1=1";
		try {
			if (isTableExists(men_table)) {
				excuteSQL(sql1);
			}
			if (isTableExists(callLog_table)) {
				excuteSQL(sql2);
			}
			if (isTableExists(sms_table)) {
				excuteSQL(sql1);
			}

			result = true;
		} catch (SQLException e) {
			// TODO: handle exception
			result = false;
			e.printStackTrace();
		}
		return result;
	}

	public boolean insertSingleCallLog(CallLogModel callLogModel) {
		boolean result = true;

		if (callLogModel == null) {
			return false;
		}

		String sql = "";
		if (callLogModel.getLocalName() == null) {
			sql = "insert into " + callLog_table + " values ('"
					+ callLogModel.getCallTime() + "'" + ","
					+ callLogModel.getLocalName() + ",'"
					+ callLogModel.getNumber() + "'" + ",'"
					+ callLogModel.getPhone_adress() + "'" + ",'"
					+ callLogModel.getPhone_operator() + "'" + ",'"
					+ String.valueOf(callLogModel.getCallType()) + "'" + ",'"
					+ String.valueOf(callLogModel.getCall_dur()) + "'" + ")";
		} else {
			sql = "insert into " + callLog_table + " values ('"
					+ callLogModel.getCallTime() + "'" + ",'"
					+ callLogModel.getLocalName() + "'" + ",'"
					+ callLogModel.getNumber() + "'" + ",'"
					+ callLogModel.getPhone_adress() + "'" + ",'"
					+ callLogModel.getPhone_operator() + "'" + ",'"
					+ String.valueOf(callLogModel.getCallType()) + "'" + ",'"
					+ String.valueOf(callLogModel.getCall_dur()) + "'" + ")";
		}

		try {
			excuteSQL(sql);
		} catch (SQLException e) {
			// TODO: handle exception
			result = false;
		}

		return result;
	}

	public boolean copyCallHisTotable(List<CallLogModel> logList) {
		boolean result = false;
		if (!isTableExists(callLog_table)) {
			createCallLogTable(callLog_table);

		}
		if (logList == null || logList.size() < 1) {
			return false;
		}

		Log.e(TAG, "长度" + logList.size());

		for (int i = 0; i < logList.size(); i++) {
			String sql = "";
			if (logList.get(i).getLocalName() == null) {
				sql = "insert into " + callLog_table + " values ('"
						+ logList.get(i).getCallTime() + "'" + ","
						+ logList.get(i).getLocalName() + ",'"
						+ logList.get(i).getNumber() + "'" + ",'"
						+ logList.get(i).getPhone_adress() + "'" + ",'"
						+ logList.get(i).getPhone_operator() + "'" + ",'"
						+ String.valueOf(logList.get(i).getCallType()) + "'"
						+ ",'" + String.valueOf(logList.get(i).getCall_dur())
						+ "'" + ")";
			} else {
				sql = "insert into " + callLog_table + " values ('"
						+ logList.get(i).getCallTime() + "'" + ",'"
						+ logList.get(i).getLocalName() + "'" + ",'"
						+ logList.get(i).getNumber() + "'" + ",'"
						+ logList.get(i).getPhone_adress() + "'" + ",'"
						+ logList.get(i).getPhone_operator() + "'" + ",'"
						+ String.valueOf(logList.get(i).getCallType()) + "'"
						+ ",'" + String.valueOf(logList.get(i).getCall_dur())
						+ "'" + ")";
			}

			try {
				excuteSQL(sql);
			} catch (SQLException e) {
				// TODO: handle exception
			}

		}

		return result;
	}

	public boolean updateFavorite(ContactMen iMen) {
		boolean b = false;
		if (!isTableExists(men_table)) {
			return false;
		}
		String sql = "update " + men_table
				+ " set favorite='1' where contact_id='" + iMen.getContactId()
				+ "'";
		try {
			excuteSQL(sql);
		} catch (SQLException e) {
			// TODO: handle exception
			b = false;
		}
		return b;
	}

	private boolean createMenTable(String tableName) {
		boolean result = false;
		// callLog_table = tableName;
		if (!isTableExists(tableName)) {
			String sql = "create table "
					+ tableName
					+ "(contact_id varchar(20) primary key,"
					+ "contact_name varchar(20),contact_number varchar(16),photoId varchar(20),favorite char(1) )";
			result = true;
			try {
				excuteSQL(sql);
			} catch (SQLException e) {
				// TODO: handle exception
			}

		}
		if (isTableExists(tableName)) {

			result = true;
		} else {

			result = false;
		}

		return result;
	}

	private boolean createGroupSmsTable(String tableName) {
		boolean result = false;

		if (!isTableExists(tableName)) {
			String sql = "create table "
					+ tableName
					+ "(create_date varchar(16) primary key,all_number varchar(8000))";
			result = true;
			try {
				excuteSQL(sql);
			} catch (SQLException e) {
				// TODO: handle exception
			}

		}
		if (isTableExists(tableName)) {

			result = true;
		} else {

			result = false;
		}

		return result;
	}

	private boolean createSmsTable(String tableName) {

		boolean result = false;

		if (!isTableExists(tableName)) {
			String sql = "create table "
					+ tableName
					+ "(id varchar(10) primary key,"
					+ "number varchar(16),person varchar(16),body varchar(8000),date varchar(16),type char(1),sms_count varchar(4))";
			result = true;
			try {
				excuteSQL(sql);
			} catch (SQLException e) {
				// TODO: handle exception
			}

		}
		if (isTableExists(tableName)) {

			result = true;
		} else {

			result = false;
		}

		return result;
	}

	private boolean createCallLogTable(String tableName) {
		boolean result = false;
		// callLog_table = tableName;
		if (!isTableExists(tableName)) {
			String sql = "create table "
					+ callLog_table
					+ "(call_date varchar(20) primary key,"
					+ "name varchar(20),number varchar(16),address varchar(20),"
					+ "operator varchar(16),call_kind char(1),call_dur varchar(20))";
			try {
				excuteSQL(sql);
			} catch (SQLException e) {
				// TODO: handle exception
			}

			result = true;
		}
		if (isTableExists(tableName)) {

		} else {

		}
		result = true;
		return result;
	}

	private Map<String, String> queryNumber(String prefix, String number) {

		if (!isTableExists("number_" + prefix)) {
			return null;
		}
		String sql = "";
		if (prefix == "0") {
			sql = "select city from number_" + prefix + " where city_id="
					+ number;
		} else {
			sql = "select province,city,telecom from number_" + prefix
					+ " where number=" + number;
		}

		return getCursorResult(sql);
	}

	public List<CallLogModel> getOCallLogByNum(String mNumber) {
		List<CallLogModel> callList = new ArrayList<CallLogModel>();
		String sql = "select * from " + callLog_table + " where number='"
				+ mNumber + "' order by call_date desc";
		Cursor cursor = getCursor(sql);
		if (cursor == null) {
			return null;
		}
		while (cursor.moveToNext()) {
			String calltime = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(0)));
			String callname = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(1)));
			String callnumber = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(2)));
			String calladdress = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(3)));
			String calloperator = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(4)));
			int calltype = Integer.parseInt(cursor.getString(cursor
					.getColumnIndex(cursor.getColumnName(5))));
			String call_dur = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(6)));
			CallLogModel calllogm = new CallLogModel(calltime, calltype, 0,
					callname, callnumber, calladdress, calloperator, call_dur);
			callList.add(calllogm);
		}
		return callList;
	}

	public List<CallLogModel> getOneCallLogResult(String menName) {
		List<CallLogModel> callList = new ArrayList<CallLogModel>();
		String sql = "select * from " + callLog_table + " where name='"
				+ menName + "' order by call_date desc";
		Cursor cursor = getCursor(sql);
		if (cursor == null) {
			return null;
		}
		while (cursor.moveToNext()) {
			String calltime = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(0)));
			String callname = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(1)));
			String callnumber = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(2)));
			String calladdress = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(3)));
			String calloperator = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(4)));
			int calltype = Integer.parseInt(cursor.getString(cursor
					.getColumnIndex(cursor.getColumnName(5))));
			String call_dur = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(6)));
			CallLogModel calllogm = new CallLogModel(calltime, calltype, 0,
					callname, callnumber, calladdress, calloperator, call_dur);
			callList.add(calllogm);
		}
		return callList;
	}

	public List<SMSModel> getGroupSMSResult() {
		List<SMSModel> smsList = new ArrayList<SMSModel>();
		String sql = "select * from " + sms_table
				+ " where type='8' order by date desc";

		Cursor cursor = getCursor(sql);
		while (cursor.moveToNext()) {
			String id = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(0)));
			String number = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(1)));
			String name = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(2)));
			String body = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(3)));
			String date = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(4)));
			String type = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(5)));
			// int sms_count = Integer.parseInt(
			// cursor.getString(cursor.getColumnIndex(cursor
			// .getColumnName(6))));
			SMSModel smsModel = new SMSModel(id, number, name, body, date, type);
			smsList.add(smsModel);
		}
		return smsList;
	}

	public List<SMSModel> getSMSResult() {
		List<SMSModel> smsList = new ArrayList<SMSModel>();
		String sql = "select * from " + sms_table + " order by date desc";

		String sql1 = "select sms.[id],sms.[number],men.[contact_name],sms.[body],sms.[date],sms.[type],"
				+ "sms.[sms_count] from sms_local sms left join contact_men men on sms.[number] like '&'+men.[contact_number]  order by sms.[date] desc";
		Cursor cursor = getCursor(sql1);
		while (cursor.moveToNext()) {
			String id = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(0)));
			String number = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(1)));
			String name = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(2)));
			String body = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(3)));
			String date = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(4)));
			String type = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(5)));
			// int sms_count = Integer.parseInt(
			// cursor.getString(cursor.getColumnIndex(cursor
			// .getColumnName(6))));
			SMSModel smsModel = new SMSModel(id, number, name, body, date, type);
			smsList.add(smsModel);
		}

		return smsList;
	}

	public List<SMSModel> getSMSByNumber(String mSmsNumber) {
		List<SMSModel> smsList = new ArrayList<SMSModel>();
		List<SMSModel> result = new ArrayList<SMSModel>();
		smsList = getSMSResult();
		for (SMSModel smsModel : smsList) {
			if (smsModel.getAddress().equals(mSmsNumber)) {
				result.add(smsModel);
			}
		}
		return result;
	}

	public List<CallLogModel> getCallLogResult() {
		List<CallLogModel> callList = new ArrayList<CallLogModel>();
		String sql = "select * from " + callLog_table
				+ " order by call_date desc";
		Cursor cursor = getCursor(sql);
		while (cursor.moveToNext()) {
			String calltime = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(0)));
			String callname = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(1)));
			String callnumber = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(2)));
			String calladdress = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(3)));
			String calloperator = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(4)));
			int calltype = Integer.parseInt(cursor.getString(cursor
					.getColumnIndex(cursor.getColumnName(5))));
			String call_dur = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(6)));
			CallLogModel calllogm = new CallLogModel(calltime, calltype, 0,
					callname, callnumber, calladdress, calloperator, call_dur);
			callList.add(calllogm);
		}
		return callList;
	}

	public List<CallLogModel> getSixCallLogResult() {
		List<CallLogModel> callList = new ArrayList<CallLogModel>();
		String sql = "select distinct name,number from " + callLog_table
				+ " where name<>'null' order by call_date desc limit 0,6";
		// select distinct name,number from call_history where name<>'null'
		// order by call_date limit 0,5
		Cursor cursor = getCursor(sql);
		while (cursor.moveToNext()) {

			String callname = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(0)));
			String callnumber = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(1)));

			CallLogModel calllogm = new CallLogModel(null, 1, 0, callname,
					callnumber, null, null, null);
			callList.add(calllogm);
		}
		return callList;
	}

	public List<ContactMen> getMenResult() {
		List<ContactMen> dmenList = new ArrayList<ContactMen>();
		String sql = "select * from " + men_table
				+ " order by contact_name desc";
		Cursor cursor = getCursor(sql);
		while (cursor.moveToNext()) {
			String men_id = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(0)));
			String men_name = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(1)));
			String men_number = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(2)));
			String men_photoId = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(3)));
			String isFav = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(4)));

			ContactMen calllogm = new ContactMen(men_id, men_number, men_name,
					Integer.parseInt(men_photoId));
			if (isFav != null && isFav.equals("1")) {
				calllogm.setFavorite(true);
			}
			dmenList.add(calllogm);
		}
		return dmenList;
	}

	public List<ContactMen> getFavMenResult() {
		List<ContactMen> dmenList = new ArrayList<ContactMen>();
		String sql = "select * from " + men_table
				+ " where favorite='1' order by contact_name desc";
		Cursor cursor = getCursor(sql);
		while (cursor.moveToNext()) {
			String men_id = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(0)));
			String men_name = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(1)));
			String men_number = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(2)));
			String men_photoId = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(3)));
			String isFav = cursor.getString(cursor.getColumnIndex(cursor
					.getColumnName(4)));

			ContactMen calllogm = new ContactMen(men_id, men_number, men_name,
					Integer.parseInt(men_photoId));
			if (isFav != null && isFav.equals("1")) {
				calllogm.setFavorite(true);
			}
			dmenList.add(calllogm);
		}
		return dmenList;
	}

	private Map<String, String> getCursorResult(String sql) {
		Cursor cursor = getCursor(sql);
		int col_len = cursor.getColumnCount();
		Map<String, String> map = new HashMap<String, String>();

		while (cursor.moveToNext()) {
			for (int i = 0; i < col_len; i++) {
				String columnName = cursor.getColumnName(i);
				String columnValue = cursor.getString(cursor
						.getColumnIndex(columnName));
				if (columnValue == null)
					columnValue = "";
				// Log.e(TAG, "列名:"+columnName+" �?"+columnValue);
				map.put(columnName.trim(), columnValue.trim());
			}
		}
		return map;
	}

	private void excuteSQL(String sql) throws SQLException {
		db.execSQL(sql);

	}

	private Cursor getCursor(String sql) {
		return db.rawQuery(sql, null);
	}

	private boolean isTableExists(String tableName) {
		boolean result = false;
		if (tableName == null)
			return false;
		Cursor cursor = null;
		try {
			String sql = "select count(*) as c from sqlite_master where type='table' and "
					+ "name = '" + tableName.trim() + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0)
					result = true;
			}
		} catch (Exception e) {

		}
		return result;
	}

	public void closeDB() {
		if (db != null) {
			db = null;
			db.close();
		}
	}
}