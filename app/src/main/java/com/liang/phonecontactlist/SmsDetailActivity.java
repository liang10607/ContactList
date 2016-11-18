package com.liang.phonecontactlist;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liang.Model.ContactMen;
import com.liang.Model.SMSModel;
import com.liang.MyUtil.SMSDetailAdapter;
import com.liang.controlHelper.SMSHelper;
import com.liang.customUI.RoundImageView;
import com.liang.phonecontactlist.SmsSend_Fragment.SendType;
import com.liang.phonenum.utils.DatabaseHelper;

public class SmsDetailActivity extends Activity {

	public static SmsDetailActivity curActivity;

	private final static String TAG = "SmsDetailActivity";

	public String smsNumer = "";
	SMSHelper smshelper;

	List<SMSModel> mMenSmsList;

	RoundImageView headPhoto;

	TextView tv_menName, tv_menNumber, tv_numberLoacation, tv_showMenDetail,
			tv_menToBlack, tv_dialMen;

	ImageView img_showMenMore;

	ListView lv_smsDetialShow;

	RelativeLayout rlay_hideBar;

	boolean isStranger;

	boolean isHideBarShow;

	FragmentManager fragmentManager;
	FragmentTransaction transation;
	SmsSend_Fragment smsSend_fragment;

	SMSDetailAdapter smsDetailAdapter;

	DatabaseHelper dataHelper;

	boolean isMutil;

	String[] multiSmsNumber = null;

	String mSmsNumber = "";

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		curActivity = this;
		setContentView(R.layout.activity_sms_detail);
		Intent intent = getIntent();
		dataHelper = DatabaseHelper.getDatabaseHelper(this);
		smshelper = SMSHelper.getSMSHelper(this);
		smsNumer = intent.getStringExtra("smsNumer");
		smshelper.setSmsReaded(smsNumer);
		mSmsNumber = smsNumer;
		isMutil = intent.getBooleanExtra("isMultiSend", false);
		mMenSmsList = smshelper.getSMSLogByNumer(smsNumer);
		if (isMutil) {
			multiSmsNumber = dataHelper.dao.getGroupSmsNumber(smsNumer);
		}
		showFrame();
		initWigdt();
		String menName = "";
		String menLoca = "";
		Drawable drawable1 = getResources().getDrawable(R.drawable.add);
		drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),
				drawable1.getMinimumHeight());
		Drawable drawable2 = getResources().getDrawable(R.drawable.men_detail);
		drawable2.setBounds(0, 0, drawable2.getMinimumWidth(),
				drawable2.getMinimumHeight());

		menName = smshelper.showMenDetail(smsNumer).getName();

		if (mMenSmsList.size() > 0 && mMenSmsList.get(0).getType().equals("8")) {
			isMutil = true;
		} else {
			isMutil = false;
		}

		if (!isMutil) {

			menLoca = smshelper.getNumberLocation(smsNumer);

			if (menName == null || menName.equals("")) {
				menName = smsNumer;
				isStranger = true;
				menName = smsNumer;
				tv_menNumber.setText("");
				tv_showMenDetail.setText("添加到联系人");
				tv_showMenDetail.setCompoundDrawables(null, drawable1, null,
						null);
			} else {
				isStranger = false;
				tv_showMenDetail.setText("查看联系人");
				tv_menNumber.setText(smsNumer);
				tv_showMenDetail.setCompoundDrawables(null, drawable2, null,
						null);
			}
			if (mMenSmsList.size() > 0) {
				Collections.reverse(mMenSmsList);
				smsDetailAdapter = new SMSDetailAdapter(this,
						R.layout.msg_item, mMenSmsList);
				lv_smsDetialShow.setAdapter(smsDetailAdapter);
				lv_smsDetialShow.setSelection(smsDetailAdapter.getCount() - 1);
			}
		} else {
			if (menName == null) {
				menName = dataHelper.dao.getSmsGroupName(smsNumer);
			}
			tv_menNumber.setMovementMethod(ScrollingMovementMethod
					.getInstance());
			String[] multiMen = dataHelper.dao.getGroupSmsNumber(smsNumer);
			menLoca = "";
			mSmsNumber = "";
			for (int i = 0; i < multiMen.length; i++) {
				mSmsNumber = mSmsNumber
						+ dataHelper.dao.getnumName(multiMen[i]) + "-"
						+ multiMen[i] + "-"
						+ smshelper.getNumberLocation(multiMen[i]) + "\n";
			}
			tv_menNumber.setText(mSmsNumber);
			tv_showMenDetail.setEnabled(false);
			tv_menToBlack.setEnabled(false);
			tv_dialMen.setEnabled(false);
			if (mMenSmsList.size() > 0) {
				Collections.reverse(mMenSmsList);
				smsDetailAdapter = new SMSDetailAdapter(this,
						R.layout.msg_item, mMenSmsList);
				lv_smsDetialShow.setAdapter(smsDetailAdapter);
				lv_smsDetialShow.setSelection(smsDetailAdapter.getCount() - 1);
			}

		}

		tv_menName.setText(menName);
		tv_numberLoacation.setText(menLoca);

		drawable2 = null;
		drawable1 = null;
	}

	private void insetNewSmsLogToLocal() {
		smshelper.getLatestSms(smsNumer);
	}

	public void refresMenSmsLog() {
		SMSModel smsModel = new SMSModel();

		smsModel = smshelper.getLatestSms(smsNumer);
		mMenSmsList.add(smsModel);
		if (mMenSmsList.size() == 1) {
			smsDetailAdapter = new SMSDetailAdapter(this, R.layout.msg_item,
					mMenSmsList);
			lv_smsDetialShow.setAdapter(smsDetailAdapter);
		}
		smsDetailAdapter.notifyDataSetChanged();
		lv_smsDetialShow.setSelection(smsDetailAdapter.getCount() - 1);
	}

	public void refreshGroupSmsLog() {

		mMenSmsList.add(smshelper.getLocalLastGroupSms(smsNumer));
		if (mMenSmsList.size() == 1) {
			smsDetailAdapter = new SMSDetailAdapter(this, R.layout.msg_item,
					mMenSmsList);
			lv_smsDetialShow.setAdapter(smsDetailAdapter);
		}
		smsDetailAdapter.notifyDataSetChanged();
		lv_smsDetialShow.setSelection(smsDetailAdapter.getCount() - 1);
	}

	private void showFrame() {
		fragmentManager = getFragmentManager();
		Bundle bundle = new Bundle();

		bundle.putString("smsNumber", smsNumer);

		if (isMutil) {

			bundle.putSerializable("smsDetailMenList", multiSmsNumber);
			smsSend_fragment = new SmsSend_Fragment(SendType.multi_detail);
		} else {
			smsSend_fragment = new SmsSend_Fragment(SendType.single_detail);
		}

		transation = fragmentManager.beginTransaction();

		smsSend_fragment.setArguments(bundle);
		transation.replace(R.id.sms_send_frameLayout, smsSend_fragment);
		transation.commit();

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		if (null != this.getCurrentFocus()) {

			InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			return mInputMethodManager.hideSoftInputFromWindow(this
					.getCurrentFocus().getWindowToken(), 0);
		}

		return super.onTouchEvent(event);
	}

	private void initWigdt() {
		tv_menName = (TextView) findViewById(R.id.tv_smsName);
		tv_menNumber = (TextView) findViewById(R.id.tv_smsNumer);
		tv_numberLoacation = (TextView) findViewById(R.id.tv_smsNumerLocation);
		tv_showMenDetail = (TextView) findViewById(R.id.tv_opensmsMenDetail);
		tv_menToBlack = (TextView) findViewById(R.id.tv_smsMentoBlack);
		tv_dialMen = (TextView) findViewById(R.id.tv_smsDial);

		img_showMenMore = (ImageView) findViewById(R.id.img_smsshowMenMore);

		rlay_hideBar = (RelativeLayout) findViewById(R.id.lay_hideOut);

		headPhoto = (RoundImageView) findViewById(R.id.img_smsheadphoto);

		lv_smsDetialShow = (ListView) findViewById(R.id.lv_smsShow);
		tv_dialMen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				call(smsNumer);
			}
		});

		tv_showMenDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!isStranger) {
					showAlertDialog(smshelper.showMenDetail(smsNumer));
				} else {
					Intent intent = new Intent(
							"android.intent.action.ADDNEWPerson");
					intent.putExtra("sms_number", smsNumer);
					startActivity(intent);
				}
			}
		});

		lv_smsDetialShow.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

				switch (scrollState) {

				case OnScrollListener.SCROLL_STATE_FLING:

					if (lv_smsDetialShow.getLastVisiblePosition() == (lv_smsDetialShow
							.getCount() - 1)) {
						if (rlay_hideBar.getVisibility() == View.VISIBLE) {
							rlay_hideBar.setVisibility(View.GONE);
						}
					}

					if (lv_smsDetialShow.getFirstVisiblePosition() == 0) {
						if (rlay_hideBar.getVisibility() == View.GONE) {
							rlay_hideBar.setVisibility(View.VISIBLE);
						}
					}

					break;
				case OnScrollListener.SCROLL_STATE_IDLE:

					if (lv_smsDetialShow.getLastVisiblePosition() == (lv_smsDetialShow
							.getCount() - 1)) {
						if (rlay_hideBar.getVisibility() == View.VISIBLE) {
							rlay_hideBar.setVisibility(View.GONE);
						}
					}

					if (lv_smsDetialShow.getFirstVisiblePosition() == 0) {
						if (rlay_hideBar.getVisibility() == View.GONE) {
							rlay_hideBar.setVisibility(View.VISIBLE);
						}
					}

					break;

				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}

		});

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

	public void showAlertDialog(ContactMen iMen) {

		CustomDialog.Builder builder = new CustomDialog.Builder(
				SmsDetailActivity.this);

		CustomDialog menDitailDialog = builder.create(iMen);
		setDialogPara(menDitailDialog);

		menDitailDialog.show();

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

		params.width = (int) (screenWidth * 0.7);
		params.height = (int) (screenHeight * 0.7);

		Window dialogWindow = dialog.getWindow();

		dialogWindow.setGravity(Gravity.CENTER);

		dialog.getWindow().setAttributes(params);

	}

	public void showSmsMenEdit(View view) {
		setHideBar();
	}

	public void addNewMsg(View view) {

	}

	public void sendMsg(View view) {

	}

	private void setHideBar() {
		if (rlay_hideBar.getVisibility() == View.GONE) {
			rlay_hideBar.setVisibility(View.VISIBLE);
			img_showMenMore.setImageResource(R.drawable.choose_up);

		} else {
			rlay_hideBar.setVisibility(View.GONE);
			img_showMenMore.setImageResource(R.drawable.choose_down);

		}
	}

}
