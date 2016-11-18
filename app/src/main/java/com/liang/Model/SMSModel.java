package com.liang.Model;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

import com.liang.MyUtil.ContactTimeUtils;

public class SMSModel {

	public static final int SMS_TYPE_RECEIVER = 1;

	public static final int SMS_TYPE_SEND = 2;

	private String id;
	private String number;
	private String person;
	private String body;
	private String date;
	private String type;

	private int sms_count;

	private int read;

	public SMSModel() {

	}

	public SMSModel(String id, String number, String person, String body,
			String date, String type) {
		this.number = number;
		this.id = id;
		this.person = person;
		this.body = body;
		this.date = date;
		this.type = type;

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "id:" + id + " number:" + number + " person:" + person
				+ " body:" + body + " date" + date + " type" + type;
	}

	public String getId() {
		return id;
	}

	public String getAddress() {
		return number;
	}

	public String getPerson() {
		return person;
	}

	public String getSmsName() {
		if (person == null || person.equals("")) {
			return number;
		} else {
			return person;
		}
	}

	public String getBody() {
		return body;
	}

	public String getDate() {
		return date;
	}

	// 需要改正
	public String getShortDate() {
		String result;

		if (ContactTimeUtils.isTotoday(date)) {
			result = ContactTimeUtils.longTimeToString(date, 4);

		} else {
			result = ContactTimeUtils.longTimeToString(date, 3);
		}
		return result;

	}

	// 需要改正
	public String getWholeDate() {
		String result;

		// Log.e("Time", ContactTimeUtils.longTimeToString(date, 3));
		result = ContactTimeUtils.longTimeToString(date, 3);
		result = result + "\n" + ContactTimeUtils.longTimeToString(date, 4);

		return result;

	}

	public String getType() {
		return type;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setAddress(String number) {
		this.number = number;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSms_count() {
		return sms_count;
	}

	public void setSms_count(int sms_count) {
		this.sms_count = sms_count;
	}

	public void addSms_count() {
		this.sms_count = sms_count + 1;
	}

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}
}
