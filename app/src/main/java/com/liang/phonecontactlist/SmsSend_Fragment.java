package com.liang.phonecontactlist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.liang.Model.ContactMen;
import com.liang.Model.SMSModel;
import com.liang.MyUtil.ContactTimeUtils;
import com.liang.controlHelper.SMSHelper;
import com.liang.phonecontactlist.R.id;
import com.liang.phonenum.utils.DatabaseHelper;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SmsSend_Fragment extends Fragment {

	private final static String TAG = "SmsSend_Fragment";

	ImageView img_addSms, img_sendMsg;

	EditText et_addSmsBody;

	String smsNumber = "";

	String smsBody = "";

	SmsDetailActivity smsDetialAct;

	NewSMSActivity newSmsActivity;

	DatabaseHelper dataHelper;

	int currentapiVersion;

	List<ContactMen> addedMenlist = new ArrayList<ContactMen>();

	SMSHelper smsHelper;

	public enum SendType {
		single_detail, multi_detail, multi_newsms
	};

	private SendType sendType;

	public SmsSend_Fragment(SendType sendType) {

		this.sendType = sendType;
		smsNumber = "";

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
		
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_sms_send, container,
				false);
		currentapiVersion = android.os.Build.VERSION.SDK_INT;
		initWight(view);
		if (!isDefaultSms()) {
			setDefaultSmsPack();
		}
		et_addSmsBody.setEnabled(true);
		if (currentapiVersion > 18) {
			if (!isDefaultSms()) {
				et_addSmsBody.setEnabled(false);
			} 
		}
		 
		Bundle bundle = getArguments();// 从activity传过来的Bundle
		dataHelper = DatabaseHelper.getDatabaseHelper(getActivity());
		smsHelper = SMSHelper.getSMSHelper(getActivity());

		addedMenlist = new ArrayList<ContactMen>();
		smsNumber = bundle.getString("smsNumber");
		switch (sendType) {
		case single_detail:
			smsDetialAct = (SmsDetailActivity) getActivity();
			break;
		case multi_detail:
			smsDetialAct = (SmsDetailActivity) getActivity();
			if (bundle.getSerializable("smsDetailMenList") != null) {

				// 群发对应的所有号码 -数组
				String[] multiN = (String[]) bundle
						.getSerializable("smsDetailMenList");
				// 把联系人号码数组,转换成List集合保存
				for (int i = 0; i < multiN.length; i++) {
					ContactMen con = new ContactMen();
					con.setNumber(multiN[i]);
					con.setName(dataHelper.dao.getnumName(multiN[i]));
					addedMenlist.add(con);
				}
			}
			break;
		case multi_newsms:
			newSmsActivity = (NewSMSActivity) getActivity();
			break;

		default:
			break;
		}

		return view;
	}

	private void initWight(View view) {
		et_addSmsBody = (EditText) view.findViewById(R.id.et_smsAddBody);
		img_addSms = (ImageView) view.findViewById(R.id.img_smsAdd);
		img_sendMsg = (ImageView) view.findViewById(R.id.img_sendMsg);

		img_sendMsg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				switch (sendType) {
				case single_detail:
					if (!et_addSmsBody.getText().toString().equals("")) {
						// Log.e(TAG,
						// "号码:"+smsNumber+"内容"+et_addSmsBody.getText().toString());
						sendSMS(smsNumber, et_addSmsBody.getText().toString());
						smsDetialAct = (SmsDetailActivity) getActivity();
						smsDetialAct.refresMenSmsLog();
						et_addSmsBody.setText("");
					}
					break;
				case multi_detail:
					if (!et_addSmsBody.getText().toString().equals("")) {
						sendGroupSMS(addedMenlist, et_addSmsBody.getText()
								.toString());
						SMSModel smsModel = new SMSModel();
						smsModel.setAddress(smsNumber);
						smsModel.setId("g"
								+ ContactTimeUtils.CurTimeToString(3));
						smsModel.setBody(et_addSmsBody.getText().toString());
						smsModel.setDate(ContactTimeUtils.CurTimeToString(4));
						smsModel.setPerson(addedMenlist.get(0).getName() + "等"
								+ addedMenlist.size() + "个人");
						smsModel.setType("8");
						smsModel.setSms_count(1);
						dataHelper.dao.insertGroupSmsToTable(smsModel,
								addedMenlist, smsNumber);
						et_addSmsBody.setText("");
						smsDetialAct.refreshGroupSmsLog();

					}
					break;
				case multi_newsms:
					Intent newsmsintent = new Intent(getActivity(),
							SmsDetailActivity.class);
					addedMenlist = newSmsActivity.getAddedSmsMen();
					if (!et_addSmsBody.getText().toString().equals("")) {

						if (addedMenlist.size() == 1) {
							smsNumber = addedMenlist.get(0).getNumber();
							sendSMS(smsNumber, et_addSmsBody.getText()
									.toString());

							smsHelper.getLatestSms(smsNumber);
							newsmsintent.putExtra("smsNumer", smsNumber);
							newsmsintent.putExtra("isMultiSend", false);
							et_addSmsBody.setText("");
							startActivity(newsmsintent);
							getActivity().finish();

						} else {
							sendGroupSMS(addedMenlist, et_addSmsBody.getText()
									.toString());
							SMSModel smsModel = new SMSModel();
							smsNumber = ContactTimeUtils.CurTimeToString(2);
							smsModel.setAddress(smsNumber);
							smsModel.setId("g"
									+ ContactTimeUtils.CurTimeToString(3));
							smsModel.setBody(et_addSmsBody.getText().toString());
							smsModel.setDate(ContactTimeUtils
									.CurTimeToString(4));
							smsModel.setPerson(addedMenlist.get(0).getName()
									+ "等" + addedMenlist.size() + "个人");
							smsModel.setType("8");
							smsModel.setSms_count(1);

							dataHelper.dao.insertGroupSmsToTable(smsModel,
									addedMenlist, smsNumber);
							et_addSmsBody.setText("");

							newsmsintent.putExtra("smsNumer",
									smsModel.getAddress());
							newsmsintent.putExtra("isMultiSend", true);
							startActivity(newsmsintent);
							getActivity().finish();
						}

					}
					break;

				default:
					break;
				}

			}
		});

		et_addSmsBody.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				smsBody = et_addSmsBody.getText().toString().trim();

				switch (sendType) {
				case multi_detail:
					if (!isNotEmpty(smsBody)) {
						img_sendMsg.setVisibility(View.GONE);
					} else {
						img_sendMsg.setVisibility(View.VISIBLE);
					}
					break;
				case multi_newsms:
					addedMenlist = newSmsActivity.getAddedSmsMen();
					if (addedMenlist.size() > 0 && isNotEmpty(smsBody)) {
						img_sendMsg.setVisibility(View.VISIBLE);
					} else {
						img_sendMsg.setVisibility(View.GONE);
					}
					break;
				case single_detail:
					if (!isNotEmpty(smsBody)) {
						img_sendMsg.setVisibility(View.GONE);
					} else {
						img_sendMsg.setVisibility(View.VISIBLE);
					}
					break;
				default:
					break;
				}
			}

		});
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	private boolean isNotEmpty(String in) {
		return !in.equals("");
	}

	private void sendGroupSMS(List<ContactMen> revMenList, String smsBody) {
		for (int i = 0; i < revMenList.size(); i++) {
			sendSMS(revMenList.get(i).getNumber(), smsBody);
		}
	}

	private void sendSMS(String number, String message) {
		String SENT = "sms_sent";
		String DELIVERED = "sms_delivered";

		PendingIntent sentPI = PendingIntent.getActivity(getActivity(), 0,
				new Intent(SENT), 0);
		PendingIntent deliveredPI = PendingIntent.getActivity(getActivity(), 0,
				new Intent(DELIVERED), 0);

		SMSModel smsModel = new SMSModel();
		smsModel.setAddress(number);
		smsModel.setBody(message);
		smsModel.setType(String.valueOf(2));
		smsModel.setRead(1);
		if (isDefaultSms()) {
			smsHelper.insertToSmsContent(smsModel);
		}
		SmsManager smsm = SmsManager.getDefault();
		Log.e(TAG, number + ":" + message);
		smsm.sendTextMessage(number, null, message, sentPI, deliveredPI);
	}

	private boolean isDefaultSms() {
		String mPackageName = getActivity().getPackageName();
		return Telephony.Sms.getDefaultSmsPackage(getActivity()).equals(
				mPackageName);
	}

	private boolean setDefaultSmsPack() {

		boolean result = false;
		final String myPackageName = getActivity().getPackageName();
		if (!isDefaultSms()) {

			Intent intent = new Intent(
					Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
			intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
					myPackageName);
			startActivity(intent);
		}
		result = isDefaultSms();
		return result;
	}

}
