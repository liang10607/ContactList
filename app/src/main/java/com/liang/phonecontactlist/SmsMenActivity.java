package com.liang.phonecontactlist;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.liang.Model.ContactMen;
import com.liang.MyUtil.MenLogAdapter;
import com.liang.controlHelper.ContactHelper;
import com.liang.phonecontactlist.MultiDelActivity.MenDelLogAdapter.ViewHolder;

public class SmsMenActivity extends Activity {

	private final static String TAG = "SmsMenActivity";

	ImageView img_cancelChoose, img_saveChoose;

	TextView tv_smsMenAllCheck;

	EditText et_smsMenSearch;

	ListView lv_smsMenList;

	MenMultiLogAdapter menLogAdapter;

	private int checkCount = 0;

	private boolean isAllCheck = false;

	List<ContactMen> smsMenList = new ArrayList<ContactMen>();

	List<ContactMen> smsChooseMenList = new ArrayList<ContactMen>();

	Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sms_men);
		getContact();
		initWigdt();

	}

	private void initWigdt() {
		lv_smsMenList = (ListView) findViewById(R.id.LV_smsMenList);
		et_smsMenSearch = (EditText) findViewById(R.id.et_smsMenSearch);
		tv_smsMenAllCheck = (TextView) findViewById(R.id.tv_smsMenAllCheck);
		tv_smsMenAllCheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!isAllCheck) {
					setAllCheck(true);
				} else {

					setAllCheck(false);
				}

			}
		});

		menLogAdapter = new MenMultiLogAdapter(this, R.layout.menlist,
				smsMenList);
		lv_smsMenList.setAdapter(menLogAdapter);
		for (int i = 0; i < smsMenList.size(); i++) {
			isCheckMap.put(i, false);
		}

		lv_smsMenList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				menLogAdapter.notifyDataSetChanged();
				// TODO Auto-generated method stub
				Log.e(TAG, "position:" + position + " id" + id);

				CheckBox checkBox = (CheckBox) view
						.findViewById(R.id.checked_men);

				if (!checkBox.isChecked()) {
					checkBox.setChecked(true);
					isCheckMap.put(position, true);
					checkCount++;
					Log.d(TAG, "选中个数:" + checkCount);
				} else {
					checkBox.setChecked(false);
					isCheckMap.put(position, false);
					checkCount--;
					Log.d(TAG, "取消选中后的个数:" + checkCount);
				}

				if (checkCount > 0) {
					if (checkCount == smsMenList.size()) {
						setAllCheck(true);
						Log.d(TAG, "已经被全部选中:" + smsMenList.size());
					} else {

						Drawable drawable1 = getResources().getDrawable(
								R.drawable.allcheck_enable);
						drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),
								drawable1.getMinimumHeight());

						tv_smsMenAllCheck.setCompoundDrawables(null, drawable1,
								null, null);//

						tv_smsMenAllCheck.setText("全选");
						tv_smsMenAllCheck.setTextColor(Color.BLACK);
						isAllCheck = false;
						Log.d(TAG, "已经被取消全部选中:" + smsMenList.size() + " "
								+ checkCount);
					}
				}
			}
		});
	}

	public void saveChoose(View view) {

		for (int i = 0; i < isCheckMap.size(); i++) {
			if (isCheckMap.get(i)) {
				ContactMen men = smsMenList.get(i);
				smsChooseMenList.add(men);
			}
		}

		Intent intent = new Intent();
		intent.putExtra("chooseSMSMen", (Serializable) smsChooseMenList);
		setResult(RESULT_OK, intent);
		finish();
	}

	public void cancelChoose(View view) {
		Intent intent = new Intent();
		intent.putExtra("chooseSMSMen", (Serializable) smsChooseMenList);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.putExtra("chooseSMSMen", (Serializable) smsChooseMenList);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void getContact() {
		try {

			ContactHelper menHelper = ContactHelper.getContactHelper(this);
			smsMenList = menHelper.getDataList();

			Collections.sort(smsMenList, new CompratorByFileName());
			// refresMenList();
		} catch (Exception e) {

			Log.d("其他错误", " 错误");
			e.printStackTrace();
		}

	}

	private void setAllCheck(boolean checked) {

		if (checked) {

			checkCount = smsMenList.size();
			for (int i = 0; i < smsMenList.size(); i++) {

				View view1 = menLogAdapter.getView(i, null, null);

				CheckBox chek = (CheckBox) view1.findViewById(R.id.checked_men);
				chek.setChecked(true);
				isCheckMap.put(i, true);
			}

			Drawable drawable1 = getResources().getDrawable(
					R.drawable.allcheck_disable);
			drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),
					drawable1.getMinimumHeight());

			tv_smsMenAllCheck.setCompoundDrawables(null, drawable1, null, null);//

			tv_smsMenAllCheck.setText("取消全选");
			tv_smsMenAllCheck.setTextColor(Color.WHITE);

			isAllCheck = true;
		} else {
			checkCount = 0;

			for (int i = 0; i < smsMenList.size(); i++) {
				View view1 = menLogAdapter.getView(i, null, null);
				CheckBox chek = (CheckBox) view1.findViewById(R.id.checked_men);
				chek.setChecked(false);
				isCheckMap.put(i, false);
			}

			Drawable drawable1 = getResources().getDrawable(
					R.drawable.allcheck_enable);
			drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),
					drawable1.getMinimumHeight());

			tv_smsMenAllCheck.setCompoundDrawables(null, drawable1, null, null);//
			tv_smsMenAllCheck.setText("全选");
			tv_smsMenAllCheck.setTextColor(Color.BLACK);
			isAllCheck = false;
		}
		// listview.refreshDrawableState();
		menLogAdapter.notifyDataSetChanged();
	}

	private static class CompratorByFileName implements Comparator<ContactMen> {

		@Override
		public int compare(ContactMen lhs, ContactMen rhs) {
			Comparator<Object> cmp = Collator
					.getInstance(java.util.Locale.CHINA);
			return cmp.compare(lhs.getName(), rhs.getName());
		}

		@Override
		public boolean equals(Object o) {
			return true;
		}

	}

	private class MenMultiLogAdapter extends ArrayAdapter<ContactMen> {

		private int resourceId;

		private List<ContactMen> listData;

		public MenMultiLogAdapter(Context context, int resource,
				List<ContactMen> objects) {
			super(context, resource, objects);
			// TODO Auto-generated constructor stub
			resourceId = resource;
			listData = objects;
		}

		@Override
		public int getCount() {
			return listData.size();
		}

		@Override
		public ContactMen getItem(int position) {
			return listData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ContactMen contactMen = getItem(position);
			View view;

			ViewHolder viewHolder = null;
			if (convertView == null) {
				view = LayoutInflater.from(getContext()).inflate(resourceId,
						null);
				viewHolder = new ViewHolder();
				viewHolder.headImage = (ImageView) view
						.findViewById(R.id.menlist_itePhoto);
				viewHolder.menName = (TextView) view
						.findViewById(R.id.menlist_itemName);
				viewHolder.checkBox = (CheckBox) view
						.findViewById(R.id.checked_men);
				viewHolder.checkBox.setVisibility(View.VISIBLE);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}

			try {
				ContactHelper contactHelper = ContactHelper
						.getContactHelper(getContext());
				if (contactMen.getImg_id() != 0) {
					viewHolder.headImage.setImageBitmap(contactHelper.getPhoto(
							contactMen.getContactId(), contactMen.getImg_id()));
				} else {
					viewHolder.headImage.setImageResource(R.drawable.headnew);
				}

				if (isCheckMap.get(position)) {
					viewHolder.checkBox.setChecked(true);
				} else {
					viewHolder.checkBox.setChecked(false);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			viewHolder.menName.setText(contactMen.getName());

			return view;
		}

		class ViewHolder {
			ImageView headImage;

			TextView menName;

			CheckBox checkBox;
		}

	}

}
