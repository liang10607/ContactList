package com.liang.phonereceiver;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import com.liang.Model.ContactMen;
import com.liang.Model.SMSModel;
import com.liang.controlHelper.ContactHelper;
import com.liang.controlHelper.SMSHelper;
import com.liang.phonecontactlist.R;
import com.liang.phonecontactlist.SmsDetailActivity;

import org.json.JSONException;

import java.util.List;

public class SmsReceiver extends BroadcastReceiver {

	private static final String TAG = "SmsReceiver";

	SMSHelper smsHelper;

	ContactHelper contactHelper;

	Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Bundle bundle = intent.getExtras();

		mContext = context;

		contactHelper = ContactHelper.getContactHelper(mContext);
		String mPackageName = context.getPackageName();

		boolean b = Telephony.Sms.getDefaultSmsPackage(context).equals(
				mPackageName);

		smsHelper = SMSHelper.getSMSHelper(context);

		Object[] pdus = (Object[]) bundle.get("pdus"); // 提取短信消息

		SmsMessage[] messages = new SmsMessage[pdus.length];

		Log.e(TAG, "长度为:" + messages.length);

		for (int i = 0; i < messages.length; i++) {

			messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
			Log.e(TAG, "messages" + i + ":" + messages[i].toString());
		}

		String address = messages[0].getOriginatingAddress(); // 获取发

		String fullMessage = "";

		for (SmsMessage message : messages) {

			fullMessage += message.getMessageBody(); // 获取短信内容

		}

		SMSModel smsModel = new SMSModel();
		ContactMen con = smsHelper.showMenDetail(address);
		smsModel.setAddress(address);
		smsModel.setPerson(con.getName());
		smsModel.setType("1");
		smsModel.setBody(fullMessage);
		smsModel.setRead(0);
		if (b) {
			smsHelper.insertToSmsContent(smsModel);
		}
		isTopActivy(address);
		Log.e(TAG, "number:" + address + " 内容为:" + fullMessage);
		send(context, smsModel);

	}

	public void send(Context context, SMSModel mSmsMode) {

		// 1.得到NotificationManager
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// 2.实例化一个通知，指定图标、概要、时间




		// 3.指定通知的标题、内容和intent
		Intent intent = new Intent(context, SmsDetailActivity.class);
		intent.putExtra("smsNumer", mSmsMode.getAddress());


		PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
		Notification n = new Notification.Builder(context)
				.setAutoCancel(true)
				.setContentTitle( mSmsMode.getSmsName())
				.setContentText(mSmsMode.getBody())
				.setContentIntent(pi)
				.setSmallIcon(R.drawable.ic_launcher)
				.setWhen(System.currentTimeMillis())
				.build();

		// 指定声音
		n.defaults = Notification.DEFAULT_SOUND;
		n.flags = Notification.FLAG_AUTO_CANCEL;
		n.ledARGB = 1000;
		ContactMen con = contactHelper.getSingelMenbyNum(mSmsMode.getAddress());
		Bitmap photo = null;
		if (con.getContactId() == null || con.getContactId().equals("")) {
			photo = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.headnew);
		}
		if (con!=null||con.getContactId() != null || !con.getContactId().equals("")) {
			try {
				photo = contactHelper.getPhoto(con.getContactId(),
						con.getImg_id());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (photo == null) {
			photo = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.headnew);
		}
		n.largeIcon = photo;
		// 4.发送通知
		nm.notify(1, n);

	}

	public void isTopActivy(String msmsNumber) {
		ActivityManager manager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
		String cmpNameTemp = null;

		if (null != runningTaskInfos) {
			cmpNameTemp = (runningTaskInfos.get(0).topActivity).toString();
			Log.e("cmpname", "cmpname1:" + cmpNameTemp);
		}
		// cmpname:ComponentInfo{com.liang.phonecontactlist/com.liang.phonecontactlist.SmsDetailActivity}
		cmpNameTemp = cmpNameTemp
				.replace(
						"ComponentInfo{com.liang.phonecontactlist/com.liang.phonecontactlist.",
						"");
		cmpNameTemp = cmpNameTemp.replace("}", "").trim();
		Log.e("cmpname", "cmpname2:" + cmpNameTemp);
		if (null == cmpNameTemp)
			return;

		if (cmpNameTemp.equals("SmsDetailActivity")
				&& SmsDetailActivity.curActivity.smsNumer.equals(msmsNumber)) {
			SmsDetailActivity.curActivity.refresMenSmsLog();
		}

	}

}
