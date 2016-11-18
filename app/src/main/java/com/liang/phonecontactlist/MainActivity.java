package com.liang.phonecontactlist;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;

import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.liang.Model.CallLogModel;
import com.liang.Model.ContactMen;
import com.liang.Model.SMSModel;
import com.liang.MyUtil.KeyBoardListener;
import com.liang.controlHelper.SMSHelper;
import com.liang.listInterface.MenListInterface;
import com.liang.phonenum.utils.DataInitalBack;
import com.liang.phonenum.utils.DatabaseHelper;

public class MainActivity extends Activity implements DataInitalBack,
		MenListInterface {

	List<CallLogModel> callLogList = new ArrayList<CallLogModel>();
	EditText et_numberInput;
	TextView tv_SMS;
	TextView tv_MenLog;
	TextView tv_CallLog;
	LinearLayout ll_mainMenu;

	KeyBoardDialog keyBoard;

	Drawable nav_up;

	private final static String TAG = "MainActivity";

	FragmentManager fragmentManager;
	FragmentTransaction transation;
	CallHis_Fragment callhis_fragment;
	MenLog_Fragment menLog_fragment;
	Sms_Fragment sms_fragment;

	DatabaseHelper dataHelper;

	private int input_count;

	private String input_Number = "";

	boolean sms_init = false;

	boolean callLog_init = false;

	boolean men_init = false;

	ProgressDialog progresDialog;

	@SuppressLint("CommitTransaction")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		Intent intents = getIntent();
		String sr = intents.getStringExtra("phnone");
		boolean isDelete = intents.getBooleanExtra("deleteMen", false);
		if (isDelete) {
			refreshMenLog();
		}

		et_numberInput = (EditText) findViewById(R.id.et_number_input);

		et_numberInput.addTextChangedListener(new TextWatcher() {

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
				input_Number = et_numberInput.getText().toString();
				input_count = input_Number.length();
			}
		});

		et_numberInput.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(et_numberInput.getWindowToken(), 0);
				showNumberKeyBoard();
				return false;
			}
		});

		ll_mainMenu = (LinearLayout) findViewById(R.id.ll_mainMenu);
		tv_SMS = (TextView) findViewById(R.id.tv_SMSrecord);
		tv_MenLog = (TextView) findViewById(R.id.tv_phoneMen);
		tv_CallLog = (TextView) findViewById(R.id.tv_Callhistory);
		nav_up = getResources().getDrawable(R.drawable.dot);
		nav_up.setBounds(0, 0, nav_up.getMinimumWidth(),
				nav_up.getMinimumHeight());

		fragmentManager = getFragmentManager();
		transation = fragmentManager.beginTransaction();

		if (menLog_fragment == null) {
			menLog_fragment = new MenLog_Fragment();
		}

		dataHelper = DatabaseHelper.getDatabaseHelper(getApplicationContext());

		int startTimes = getAPPStartData();
		if (startTimes == 0) {
			showProgressDialog();
			dataHelper.setInitDB(this);
		} else {
			if (progresDialog != null) {
				progresDialog.dismiss();
			}
			transation.replace(R.id.content_frameLayout, menLog_fragment);
			transation.commit();

		}
		startTimes++;
		setAPPStartData(startTimes);

	}

	private void setAPPStartData(int startTime) {
		SharedPreferences.Editor editor = getSharedPreferences("start_date",
				MODE_PRIVATE).edit();
		editor.putInt("starttime", startTime);
		editor.commit();
	}

	private void showProgressDialog() {
		progresDialog = new ProgressDialog(this);
		progresDialog.setMessage("正在加载本地数据...");
		progresDialog.setIndeterminate(true);
		progresDialog.setCancelable(true);
		progresDialog.setCanceledOnTouchOutside(false);
		progresDialog.show();
	}

	private int getAPPStartData() {
		int result = 0;
		SharedPreferences pre = getSharedPreferences("start_date", MODE_PRIVATE);
		result = pre.getInt("starttime", 0);
		return result;
	}

	public void callLogShowDialog(String mNumber) {
		ContactMen mMen = dataHelper.dao.getSimgleMen(mNumber);

		Log.e(TAG, "得到的mMen详细:" + mMen.toString());
		if (mMen.getNumber() == null || mMen.getNumber().equals("")) {
			mMen = new ContactMen();
			mMen.setNumber(mNumber);
			mMen.setName(mNumber);
			showAlertDialog(mMen);
		} else {
			showAlertDialog(mMen);
		}

	}

	public void showAlertDialog(ContactMen iMen) {

		CustomDialog.Builder builder = new CustomDialog.Builder(
				MainActivity.this);

		CustomDialog menDitailDialog = builder.create(iMen);
		setDialogPara(menDitailDialog);

		menDitailDialog.show();

	}

	KeyBoardListener keyListener = new KeyBoardListener() {

		@Override
		public void keyUp(KEY_NAME key) {
			// TODO Auto-generated method stub
			if (input_count == 0 && key != KEY_NAME.KEY_DELETE) {
				input_count = 1;
				setNumInput(true);

			}
			switch (key) {
			case KEY0:
				insertText("0");
				break;
			case KEY1:
				insertText("1");
				break;
			case KEY2:
				insertText("2");
				break;
			case KEY3:
				insertText("3");
				break;
			case KEY4:
				insertText("4");
				break;
			case KEY5:
				insertText("5");
				break;
			case KEY6:
				insertText("6");
				break;
			case KEY7:
				insertText("7");
				break;
			case KEY8:
				insertText("8");
				break;
			case KEY9:
				insertText("9");
				break;
			case KEY_DELETE:
				deleteText();
				if (input_count == 0) {
					setNumInput(false);
				}
				break;
			case KEY_DIAL:
				call(input_Number);
				break;
			case KEY_JING:
				insertText("#");
				break;
			case KEY_XING:
				insertText("*");
				break;
			case KEY_SHOWBOARD:

			default:

				break;
			}
		}
	};

	private void setNumInput(boolean isInput) {
		if (isInput) {
			et_numberInput.setVisibility(View.VISIBLE);
			et_numberInput.setFocusable(true);
			keyBoard.setMenuDeleteKey(true);
			InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(et_numberInput.getWindowToken(), 0);
			ll_mainMenu.setVisibility(View.GONE);

		} else {
			et_numberInput.setVisibility(View.GONE);
			et_numberInput.setFocusable(false);
			keyBoard.setMenuDeleteKey(false);
			InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(et_numberInput.getWindowToken(), 0);
			ll_mainMenu.setVisibility(View.VISIBLE);
		}
	}

	public void showNumberKeyBoard() {

		KeyBoardDialog.Builder builder = new KeyBoardDialog.Builder(
				MainActivity.this);

		keyBoard = builder.create(keyListener);
		setKeyBoardPara(keyBoard);

		keyBoard.show();
		if (input_count > 0) {
			keyBoard.setMenuDeleteKey(true);
		} else {
			keyBoard.setMenuDeleteKey(false);
		}

	}

	private void setKeyBoardPara(KeyBoardDialog keyDialg) {
		if (keyDialg == null) {
			return;
		}

		Window dialogWindow = keyDialg.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.TOP);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int widthPixels = dm.widthPixels;
		int heightPixels = dm.heightPixels;

		lp.width = widthPixels; // 宽度
		lp.height = 400;
		lp.x = 0; // 新位置X坐标
		lp.y = heightPixels - lp.height; // 新位置Y坐标
		// // 高度
		lp.alpha = 1.0f; //
		lp.dimAmount = 0.0f;

		dialogWindow.setAttributes(lp);
	}

	private void setDialogPara(CustomDialog dialog) {
		if (dialog == null) {
			return;
		}

		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.alpha = 1.0f;

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int widthPixels = dm.widthPixels;
		int heightPixels = dm.heightPixels;
		float density = dm.density;
		int screenWidth = (int) (widthPixels);
		int screenHeight = (int) (heightPixels);
		Log.d(TAG, "screenWidth:" + screenWidth + " screenHeight:"
				+ screenHeight);
		params.width = (int) (screenWidth * 0.7);
		params.height = (int) (screenHeight * 0.7);

		Log.d(TAG, "params.width :" + params.width + " params.height:"
				+ params.height);
		Window dialogWindow = dialog.getWindow();

		dialogWindow.setGravity(Gravity.CENTER);

		dialog.getWindow().setAttributes(params);

	}

	public void showCallLog(View view) {
		fragmentManager = getFragmentManager();
		transation = fragmentManager.beginTransaction();
		tv_MenLog.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
				null);
		tv_SMS.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		tv_CallLog.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
				nav_up);
		if (callhis_fragment == null) {
			callhis_fragment = new CallHis_Fragment();
		}

		transation.replace(R.id.content_frameLayout, callhis_fragment);
		transation.commit();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshMenLog();

	}

	public void refreshMenLog() {
		if (menLog_fragment != null && menLog_fragment.isVisible()) {

			menLog_fragment.refresMen();
		}
	}

	public void showMenLog(View view) {
		fragmentManager = getFragmentManager();
		transation = fragmentManager.beginTransaction();
		tv_CallLog.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
				null);
		tv_SMS.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		tv_MenLog.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
				nav_up);

		if (menLog_fragment == null) {
			menLog_fragment = new MenLog_Fragment();
		}

		transation.replace(R.id.content_frameLayout, menLog_fragment);
		transation.commit();
	}

	public void onSendSMS(View view) {

	}

	public void deleteMen() {

	}

	public void showSMS(View view) {
		fragmentManager = getFragmentManager();
		transation = fragmentManager.beginTransaction();
		tv_CallLog.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
				null);
		tv_MenLog.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
				null);
		tv_SMS.setCompoundDrawablesWithIntrinsicBounds(null, null, null, nav_up);

		if (sms_fragment == null) {
			sms_fragment = new Sms_Fragment();
		}

		transation.replace(R.id.content_frameLayout, sms_fragment);
		transation.commit();

	}

	public void call(String phone) {

		if (phone != null && phone.trim().length() > 0) {
			Intent intent = new Intent("android.intent.action.CALL",
					Uri.parse("tel:" + phone.trim()));
			startActivity(intent);
		} else {
			Toast.makeText(this, "电话号码不能为空", Toast.LENGTH_LONG).show();
		}
	}

	private int getEditTextCursorIndex(EditText mEditText) {
		return mEditText.getSelectionStart();
	}

	/** 向EditText指定光标位置插入字符串 */
	private void insertText(String mText) {
		et_numberInput.getText().insert(getEditTextCursorIndex(et_numberInput),
				mText);
	}

	/** 向EditText指定光标位置删除字符串 */
	private void deleteText() {
		if (input_count > 0 && et_numberInput.getText().toString().length() > 0) {
			et_numberInput.getText().delete(
					getEditTextCursorIndex(et_numberInput) - 1,
					getEditTextCursorIndex(et_numberInput));
		}
	}

	public void addMen(View view) {

		Intent intent = new Intent("android.intent.action.ADDNEWPerson");
		startActivity(intent);

	}

	public void SearchMen(View view) {

	}

	public void multiDelete(View view) {

	}

	@Override
	public void backInitalState(initType type) {
		// TODO Auto-generated method stub
		if (type == initType.callLogType) {
			callLog_init = true;
		}
		if (type == initType.menType) {
			men_init = true;
		}
		if (type == initType.smsType) {
			sms_init = true;
		}
		if (callLog_init && men_init && sms_init) {
			progresDialog.dismiss();
			transation.replace(R.id.content_frameLayout, menLog_fragment);
			transation.commit();

		}
	}

	@Override
	public void refreshActMenList() {
		// TODO Auto-generated method stub
		refreshMenLog();
	}

}
