package com.liang.phonecontactlist;

import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.liang.Model.ContactMen;

import com.liang.controlHelper.ContactHelper;

import com.liang.phonenum.utils.AssetsDatabaseManager;
import com.liang.phonenum.utils.DatabaseDAO;

public class MultiDelActivity extends Activity {

	private final static String TAG = "MultiDelActivity";

	List<ContactMen> menList = new ArrayList<ContactMen>();

	@SuppressLint("UseSparseArrays")
	Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();

	// PullMenParser menParser;

	ListView listview;

	private int checkCount = 0;

	MenDelLogAdapter adapter;
	TextView tv_multidelete;
	TextView tv_checkAllMen, tv_deleteInfo;

	ContactHelper contactHelper = ContactHelper.getContactHelper(this);

	private boolean isAllCheck = false;

	public void refresMen() {

		listview.setVisibility(View.VISIBLE);
		Collections.sort(menList, new CompratorByFileName());

		adapter = new MenDelLogAdapter(MultiDelActivity.this, R.layout.menlist,
				menList);

		listview.setAdapter(adapter);
		for (int i = 0; i < menList.size(); i++) {
			isCheckMap.put(i, false);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_multi_del);
		menList = (List<ContactMen>) getIntent()
				.getSerializableExtra("contact");
		Log.e(TAG, "长度:" + menList.size());

		listview = (ListView) findViewById(R.id.lv_delMenList);
		TextView tv_cancelDel = (TextView) findViewById(R.id.tv_canelDelete);

		refresMen();

		tv_multidelete = (TextView) findViewById(R.id.tv_deletebtn);
		tv_multidelete.setEnabled(false);
		tv_multidelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				for (int i = 0; i < isCheckMap.size(); i++) {
					if (isCheckMap.get(i)) {
						ContactMen men = menList.get(i);
						contactHelper.DeleteMen(men);
						// contactHelper.deleteContact(men);
						Drawable drawable = getResources().getDrawable(
								R.drawable.garbage_enable);
						drawable.setBounds(0, 0, drawable.getMinimumWidth(),
								drawable.getMinimumHeight());

						tv_multidelete.setCompoundDrawables(drawable, null,
								null, null);//

					}
				}
				Intent intent = new Intent(MultiDelActivity.this,
						MainActivity.class);
				intent.putExtra("deleteMen", true);
				startActivity(intent);
			}
		});
		tv_checkAllMen = (TextView) findViewById(R.id.tv_deleteChooseAll);
		tv_checkAllMen.setOnClickListener(new OnClickListener() {

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
		tv_deleteInfo = (TextView) findViewById(R.id.tv_deleteInfo);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.notifyDataSetChanged();
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
					if (checkCount == menList.size()) {
						setAllCheck(true);
						Log.d(TAG, "已经被全部选中:" + menList.size());
					} else {

						Drawable drawable1 = getResources().getDrawable(
								R.drawable.allcheck_enable);
						drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),
								drawable1.getMinimumHeight());

						tv_checkAllMen.setCompoundDrawables(null, drawable1,
								null, null);//

						tv_checkAllMen.setText("全选");
						tv_checkAllMen.setTextColor(Color.BLACK);
						isAllCheck = false;
						Log.d(TAG, "已经被取消全部选中:" + menList.size() + " "
								+ checkCount);
					}
					setGarbageCheck(true);
					tv_deleteInfo.setText("删除联系人" + checkCount);
				} else {
					tv_deleteInfo.setText("删除联系人");
					setGarbageCheck(false);
				}

			}
		});

		tv_cancelDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void deleteMen(ContactMen imen) {
		// String where = ContactsContract.Data._ID + "=?";
		String[] whereparams = new String[] { imen.getContactId() };

		Log.e(TAG, "删除联系人:" + imen.toString());
		//
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		getContentResolver().delete(uri, "_id=?", whereparams);

		// getContentResolver().delete(uri, "display_name=?", new
		// String[]{imen.getName()});
		// uri = Uri.parse("content://com.android.contacts/data");
		// getContentResolver().delete(uri, "raw_contact_id=?", new
		// String[]{imen.getNumber()});

		// int s=
		// getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI,
		// where, whereparams);
		// Log.d(TAG,
		// "姓名:"+imen.getName()+" id"+imen.getContactId()+" 删除结果:"+s);
	}

	public void deleteContact(long rawContactId) {
		getContentResolver().delete(
				ContentUris.withAppendedId(RawContacts.CONTENT_URI,
						rawContactId), null, null);
	}

	private void setGarbageCheck(boolean checked) {
		if (checked) {
			tv_multidelete.setEnabled(true);

			Drawable drawable = getResources().getDrawable(
					R.drawable.garbage_disable);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());

			tv_multidelete.setCompoundDrawables(null, drawable, null, null);
			tv_multidelete.setTextColor(Color.WHITE);
		} else {
			Drawable drawable = getResources().getDrawable(
					R.drawable.garbage_enable);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());

			tv_multidelete.setCompoundDrawables(null, drawable, null, null);
			tv_multidelete.setTextColor(Color.BLACK);
		}
	}

	private void setAllCheck(boolean checked) {
		ListAdapter listAdapter = listview.getAdapter();
		if (checked) {

			checkCount = menList.size();
			for (int i = 0; i < menList.size(); i++) {

				View view1 = adapter.getView(i, null, null);

				CheckBox chek = (CheckBox) view1.findViewById(R.id.checked_men);
				chek.setChecked(true);
				isCheckMap.put(i, true);
			}

			Drawable drawable1 = getResources().getDrawable(
					R.drawable.allcheck_disable);
			drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),
					drawable1.getMinimumHeight());

			tv_checkAllMen.setCompoundDrawables(null, drawable1, null, null);//

			tv_deleteInfo.setText("删除联系人" + checkCount);
			tv_checkAllMen.setText("取消全选");
			tv_checkAllMen.setTextColor(Color.WHITE);
			setGarbageCheck(true);
			isAllCheck = true;
		} else {
			checkCount = 0;
			tv_multidelete.setEnabled(false);

			for (int i = 0; i < menList.size(); i++) {
				View view1 = adapter.getView(i, null, null);
				CheckBox chek = (CheckBox) view1.findViewById(R.id.checked_men);
				chek.setChecked(false);
				isCheckMap.put(i, false);
			}
			setGarbageCheck(false);
			Drawable drawable1 = getResources().getDrawable(
					R.drawable.allcheck_enable);
			drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),
					drawable1.getMinimumHeight());

			tv_checkAllMen.setCompoundDrawables(null, drawable1, null, null);//

			tv_deleteInfo.setText("删除联系人");
			tv_checkAllMen.setText("全选");
			tv_checkAllMen.setTextColor(Color.BLACK);
			isAllCheck = false;
		}
		// listview.refreshDrawableState();
		adapter.notifyDataSetChanged();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.multi_del, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class MenDelLogAdapter extends ArrayAdapter<ContactMen> {

		private int resourceId;

		private List<ContactMen> listData;

		public MenDelLogAdapter(Context context, int resource,
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
