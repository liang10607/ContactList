package com.liang.phonecontactlist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.liang.Model.CallLogModel;
import com.liang.Model.ContactMen;
import com.liang.MyUtil.OneCallLogAdapter;
import com.liang.controlHelper.ContactHelper;
import com.liang.customUI.RoundImageView;
import com.liang.phonenum.utils.DatabaseHelper;

public class CustomDialog extends Dialog {

	private static final String TAG = "CustomDialog";

	private ContactMen men;

	RoundImageView riv_headphoto;

	ListView lv_MenCalllog;

	TextView tv_nuber, tv_nuberInfo, tv_sendMSG, tv_nocallLog;

	TextView tv_shareList, tv_OwnMen, tv_editMen, tv_menName,
			tv_menDetailDelete;
	LinearLayout ll_scrollContainer;
	RelativeLayout rl_telNuberContainer;
	ScrollView scrollview_center;

	ImageView img_showCallLog;

	RelativeLayout ll_centerInfo;

	Button bt_transparentSet;

	Context mContext;

	String snumber, new_number;

	private String phone_adress; // 号码归属地

	private int phone_operator; // 手机号所属运营商

	String opra_string;

	DatabaseHelper dataHelper;

	ContactHelper contactHelper;

	int lastX;
	int lastY;

	int curX;
	int curY;

	boolean isStranger = false;

	boolean isShowCallLog;

	public CustomDialog(Context context, ContactMen iMen) {
		super(context);

	}

	public CustomDialog(Context context) {
		super(context);
	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;

	}

	public void setMenListView(ContactMen iMen) {
		List<CallLogModel> onecallLogList = new ArrayList<CallLogModel>();
		if (!isStranger) {
			onecallLogList = dataHelper.dao.getOneCallLogResult(iMen.getName());
		} else {
			onecallLogList = dataHelper.dao.getOCallLogByNum(iMen.getNumber());
		}

		if (onecallLogList == null) {
			return;
		}
		if (onecallLogList == null || onecallLogList.size() == 0) {
			tv_nocallLog.setVisibility(View.VISIBLE);
		}
		OneCallLogAdapter adapter = new OneCallLogAdapter(mContext,
				R.layout.one_callhis_item, onecallLogList);

		lv_MenCalllog.setAdapter(adapter);
	}

	public void setBaseDate(ContactMen iMen) {
		this.men = iMen;
		dataHelper = DatabaseHelper.getDatabaseHelper(getContext());
		contactHelper = ContactHelper.getContactHelper(getContext());
		if (iMen.getContactId() == null || iMen.getContactId().equals("")) {
			isStranger = true;
			initWigdt(isStranger);
			initStrangerDate(iMen);
		} else {
			isStranger = false;
			initWigdt(isStranger);
			initData(iMen);
		}

		setScrollPara();
		setMenListView(iMen);

	}

	public void setScrollPara() {
		bt_transparentSet.setVisibility(View.GONE);
	}

	private void getFirstNumber(ContactMen iMen) {
		JSONArray phoneArr;
		try {
			phoneArr = contactHelper.getPhone(iMen.getContactId());
			JSONObject jo = phoneArr.getJSONObject(0);
			String number = jo.getString("phone");
			number = number.replace("-", "").replace(" ", "");
			iMen.setNumber(number);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initStrangerDate(ContactMen iMen) {
		tv_menName.setText(iMen.getNumber());
		tv_nuber.setText(iMen.getNumber());
		String numInfo = null;

		if (iMen.getPhone_adress() == null) {
			numInfo = dataHelper.dao.getNumberInfo(iMen.getNumber());
		}
		tv_nuberInfo.setText(numInfo);
	}

	public void initData(ContactMen iMen) {

		if (iMen.getImg_id() == 0) {
			if (riv_headphoto == null) {

			}

			riv_headphoto.setImageResource(R.drawable.headnew);
		} else {
			try {

				riv_headphoto.setImageBitmap(contactHelper.getPhoto(
						iMen.getContactId(), iMen.getImg_id()));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		getFirstNumber(iMen);
		tv_menName.setText(iMen.getName());
		tv_nuber.setText(iMen.getNumber());
		String numInfo = null;

		if (iMen.getPhone_adress() == null) {
			numInfo = dataHelper.dao.getNumberInfo(iMen.getNumber());
		}
		tv_nuberInfo.setText(numInfo);

		addNewNumItem(iMen);

		addAddessItem(iMen);

		addEmailItem(iMen);

		addRingItem(iMen);

	}

	private void addEmailItem(ContactMen iMen) {
		try {
			JSONArray emailArr = contactHelper.getEmail(iMen.getContactId());
			String strEmail = "";
			if (emailArr.length() > 0) {
				for (int i = 0; i < emailArr.length(); i++) {
					if (emailArr.length() > 1 && i == 1) {
						i = 2;
					}
					JSONObject jo1 = emailArr.getJSONObject(i);
					String email1 = jo1.getString("email");

					strEmail = strEmail + i + ":" + email1;
					men.seteMail(strEmail);

					String emailType1 = jo1.getString("type");
					View view = LayoutInflater.from(getContext()).inflate(
							R.xml.contact_other_item, null);
					view.setEnabled(true);
					view.setVisibility(View.VISIBLE);
					view.setClickable(true);

					ll_scrollContainer.addView(view, new LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));
					TextView tv_name = (TextView) view
							.findViewById(R.id.tv_otherName);
					tv_name.setText(email1);

					TextView tv_nameinfo = (TextView) view
							.findViewById(R.id.tv_otherinfo);
					tv_nameinfo.setText(emailType1);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addAddessItem(ContactMen iMen) {
		try {
			JSONArray addrArr = contactHelper.getAddress(iMen.getContactId());
			String allSdr = "";
			if (addrArr.length() > 0) {
				for (int i = 0; i < addrArr.length(); i++) {
					if (addrArr.length() > 1 && i == 1) {
						i = 1;
					}
					JSONObject jo1 = addrArr.getJSONObject(i);
					String addr1 = jo1.getString("address");

					String defaultAddr = addrArr.getJSONObject(i).getString(
							"address");
					defaultAddr = defaultAddr.substring(0, addr1.length() / 2);

					addr1 = addr1.substring(0, addr1.length() / 2);
					allSdr = allSdr + i + ":" + addr1;
					men.setAddress(allSdr);

					String addrType1 = jo1.getString("addressType");
					View view = LayoutInflater.from(getContext()).inflate(
							R.xml.contact_other_item, null);
					view.setEnabled(true);
					view.setVisibility(View.VISIBLE);
					view.setClickable(true);

					ll_scrollContainer.addView(view, new LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));
					TextView tv_name = (TextView) view
							.findViewById(R.id.tv_otherName);
					tv_name.setText(addr1);

					TextView tv_nameinfo = (TextView) view
							.findViewById(R.id.tv_otherinfo);
					tv_nameinfo.setText(addrType1);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addRingItem(ContactMen iMen) {
		try {
			View view = LayoutInflater.from(getContext()).inflate(
					R.xml.contact_other_item, null);

			view.setClickable(true);
			ll_scrollContainer.addView(view, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			TextView tv_name = (TextView) view.findViewById(R.id.tv_otherName);
			tv_name.setText("手机铃声");

			TextView tv_nameinfo = (TextView) view
					.findViewById(R.id.tv_otherinfo);
			tv_nameinfo.setText("默认");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addNewNumItem(ContactMen iMen) {
		try {
			JSONArray phoneArr = contactHelper.getPhone(iMen.getContactId());

			if (phoneArr.length() > 1) {
				for (int i = 1; i < phoneArr.length(); i++) {
					JSONObject jo = phoneArr.getJSONObject(i);
					new_number = jo.getString("phone");
					new_number = new_number.replace("-", "").replace(" ", "");
					String numInfo = dataHelper.dao.getNumberInfo(new_number);

					View view = LayoutInflater.from(getContext()).inflate(
							R.xml.numdetail_item, null);
					view.setEnabled(true);
					view.setVisibility(View.VISIBLE);
					view.setClickable(true);

					ll_scrollContainer.addView(view, new LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT));
					TextView tv_name = (TextView) view
							.findViewById(R.id.tv_Numberishow);
					tv_name.setText(new_number);

					TextView tv_nameinfo = (TextView) view
							.findViewById(R.id.tv_numberdetailInfo);
					tv_nameinfo.setText(numInfo);
					TextView tv_new_sendmsg = (TextView) view
							.findViewById(R.id.tv_sendsmsCus);
					tv_new_sendmsg
							.setOnClickListener(new android.view.View.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									String numer = new_number.replace(" ", "");
									numer = numer.replace("-", "");
									numer = numer.replace("+86", "");
									Intent intent = new Intent(mContext,
											SmsDetailActivity.class);
									intent.putExtra("smsNumer", numer);
									mContext.startActivity(intent);
								}
							});

				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();

	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		super.cancel();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();

	}

	public void setCallShowState(boolean iShow) {
		int top = 0;
		int botom = 0;

		if (iShow) {
			top = ll_centerInfo.getHeight() - rl_telNuberContainer.getHeight();
			botom = top + scrollview_center.getHeight();
			isShowCallLog = true;
		} else {
			top = 0;
			botom = top + scrollview_center.getHeight();
			isShowCallLog = false;
		}
		scrollview_center.layout(scrollview_center.getLeft(), top,
				scrollview_center.getRight(), botom);
		setChooseLogShowImg(iShow);
	}

	public void setChooseLogShowImg(boolean iShow) {
		if (iShow) {
			img_showCallLog.setImageResource(R.drawable.choose_up);
		} else {
			img_showCallLog.setImageResource(R.drawable.choose_down);
		}

	}

	public void initWigdt(boolean mIsStranger) {
		riv_headphoto = (RoundImageView) findViewById(R.id.img_headphoto);

		tv_menName = (TextView) this.getWindow().findViewById(
				R.id.dialog_TVname);

		lv_MenCalllog = (ListView) findViewById(R.id.lv_singlecallLog);
		// lv_MenCalllog.setVisibility(View.GONE);
		tv_nuber = (TextView) findViewById(R.id.tv_telNumber);
		tv_nuberInfo = (TextView) findViewById(R.id.tv_telInfo);
		tv_sendMSG = (TextView) findViewById(R.id.tv_sendsms);

		tv_shareList = (TextView) findViewById(R.id.tv_shareList);
		tv_OwnMen = (TextView) findViewById(R.id.TV_addCollect);
		tv_editMen = (TextView) findViewById(R.id.TV_editList);
		tv_menDetailDelete = (TextView) findViewById(R.id.tv_menDetailDelete);

		ll_scrollContainer = (LinearLayout) findViewById(R.id.scroll_container);
		ll_centerInfo = (RelativeLayout) findViewById(R.id.ll_centerInfo);
		scrollview_center = (ScrollView) findViewById(R.id.scroll_center);
		scrollview_center.setTop(0);
		rl_telNuberContainer = (RelativeLayout) findViewById(R.id.rl_telNuberContainer);
		bt_transparentSet = (Button) findViewById(R.id.bt_transparetSet);
		tv_nocallLog = (TextView) findViewById(R.id.tv_nocallLog);
		img_showCallLog = (ImageView) findViewById(R.id.choose_DiaFun);

		img_showCallLog
				.setOnClickListener(new android.view.View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						setCallShowState(!isShowCallLog);
					}
				});

		if (mIsStranger) {
			tv_shareList.setVisibility(View.GONE);
			tv_OwnMen.setVisibility(View.GONE);
			Drawable drawable1 = mContext.getResources().getDrawable(
					R.drawable.add);
			drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),
					drawable1.getMinimumHeight());
			Drawable drawable2 = mContext.getResources().getDrawable(
					R.drawable.men_detail);
			drawable2.setBounds(0, 0, drawable2.getMinimumWidth(),
					drawable2.getMinimumHeight());
			tv_shareList.setCompoundDrawables(null, drawable1, null, null);
			tv_OwnMen.setCompoundDrawables(null, drawable2, null, null);
		}

		tv_sendMSG.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String numer = men.getNumber().replace(" ", "");
				numer = numer.replace("-", "");
				numer = numer.replace("+86", "");
				Intent intent = new Intent(mContext, SmsDetailActivity.class);
				intent.putExtra("smsNumer", numer);
				mContext.startActivity(intent);
			}
		});

		scrollview_center.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					curX = (int) event.getRawX();
					curY = (int) event.getRawY();
					int dx = curX - lastX;
					int dy = curY - lastY;
					int top = scrollview_center.getTop();
					int botom = scrollview_center.getBottom();

					if (scrollview_center.getScrollY() == 0) {
						if (curY > lastY) {
							top = scrollview_center.getTop() + dy;
							botom = scrollview_center.getBottom() + dy;

						}
					}
					if (scrollview_center.getScrollY() < ll_centerInfo
							.getHeight() - rl_telNuberContainer.getHeight

					()) {
						if (curY > lastY) {
							if (dy > 50) {
								top = ll_centerInfo.getHeight() -

								rl_telNuberContainer.getHeight();
								botom = top + scrollview_center.getHeight

								();
							} else {
								top = scrollview_center.getTop() + dy;
								botom = scrollview_center.getBottom() + dy;
							}
						} else {
							if (scrollview_center.getTop() + dy >= 0) {
								if (dy < -50) {
									top = 0;
									botom = scrollview_center.getHeight();
								} else {
									top = scrollview_center.getTop() + dy;
									botom = scrollview_center.getBottom() + dy;
								}
							}

						}
					}

					if (top > ll_centerInfo.getHeight()
							- rl_telNuberContainer.getHeight()) {
						isShowCallLog = true; // 显示完全通话记录
						top = ll_centerInfo.getHeight()
								- rl_telNuberContainer.getHeight();
						botom = top + scrollview_center.getHeight();
						isShowCallLog = true;// 隐藏了通话记
						setChooseLogShowImg(isShowCallLog);
					}
					if (top < 0) {
						top = 0;

					}
					if (top == 0) {
						isShowCallLog = false;// 隐藏了通话记录
						setChooseLogShowImg(isShowCallLog);
					}
					scrollview_center.layout(scrollview_center.getLeft(), top,
							scrollview_center.getRight(), botom);
					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:

					if (scrollview_center.getTop() >= ll_centerInfo.getHeight()
							- rl_telNuberContainer.getHeight()) {
						scrollview_center.setTop(ll_centerInfo.getHeight()
								- rl_telNuberContainer.getHeight());
						isShowCallLog = true;// 显示完全通话记录
						setChooseLogShowImg(isShowCallLog);
					}
					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
					break;

				case MotionEvent.ACTION_SCROLL:

					// Log.e(TAG, "ACTION_SCROLL");
					break;

				default:
					break;
				}
				return false;

			}
		});

		tv_menDetailDelete
				.setOnClickListener(new android.view.View.OnClickListener() {

					@Override
					public void onClick(View v) {
						contactHelper.DeleteMen(men);
						String strActName = mContext.getClass().toString();
						strActName = strActName.replace(
								"class com.liang.phonecontactlist.", "");

						if (strActName.equals("MainActivity")) {
							MainActivity mainAct = (MainActivity) mContext;

							mainAct.refreshMenLog();
						}
						dismiss();
					}
				});

		tv_editMen.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				closeDialog();
				Intent intent = new Intent("android.intent.action.ADDNEWPerson");

				intent.putExtra("NewMen", (Serializable) men);
				getContext().startActivity(intent);

			}

		});

		tv_OwnMen.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				contactHelper.setMenFavorite(men);
			}
		});

		tv_shareList
				.setOnClickListener(new android.view.View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Log.e(TAG, "分享:" + men.shareString());
						Intent shareIntent = new Intent();
						shareIntent.setAction(Intent.ACTION_SEND);

						shareIntent.putExtra(Intent.EXTRA_TEXT,
								men.shareString());
						shareIntent.setType("text/plain");

						// 设置分享列表的标题，并且每次都显示分享列表
						mContext.startActivity(Intent.createChooser(
								shareIntent, "分享联系人到"));
					}
				});

	}

	private void closeDialog() {
		if (this.isShowing()) {
			this.cancel();

		}
	}

	public static class Builder {
		private Context context;

		private View contentView;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		public CustomDialog create(ContactMen iMen) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			CustomDialog dialog = null;
			dialog = new CustomDialog(context, R.style.CustomDialog);
			View layout = inflater.inflate(R.layout.dialog_normal_layout, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

			dialog.setBaseDate(iMen);
			dialog.setCancelable(true);

			return dialog;
		}
	}
}