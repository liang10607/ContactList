package com.liang.phonecontactlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.liang.Model.CallLogModel;
import com.liang.Model.ContactMen;
import com.liang.MyUtil.LatestCallLogAdapter;
import com.liang.MyUtil.MenSearchAdapter;
import com.liang.controlHelper.CallLogHelper;
import com.liang.controlHelper.ContactHelper;
import com.liang.controlHelper.MenSearch;
import com.liang.customUI.FlowLayout;
import com.liang.phonecontactlist.SmsSend_Fragment.SendType;

public class NewSMSActivity extends Activity implements OnClickListener {

	private final static String TAG = "NewSMSActivity";

	FragmentManager fragmentManager;
	FragmentTransaction transation;
	SmsSend_Fragment smsSend_fragment;

	FlowLayout flowLayout;
	EditText et_inputMen;

	TextView tv_men;

	ImageView img_addLoacalMen;

	ListView lv_searchedMen;

	CallLogHelper callLogHelper;

	ScrollView scroll_view;

	Map<String, ContactMen> smsSendMen;

	List<CallLogModel> listLatestMen;

	LatestCallLogAdapter latestMenAdapter;

	LatestCallLogAdapter localSeachMenAdapter;

	List<ContactMen> menList = new ArrayList<ContactMen>();

	List<ContactMen> menSearchedList = new ArrayList<ContactMen>();

	List<CallLogModel> callmenSearchedList = new ArrayList<CallLogModel>();

	Map<String, TextView> menMap = new LinkedHashMap<String, TextView>();

	int positon = 0;

	boolean isLastAdapter = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_new_sms);
		getContact();
		showFrame();
		smsSendMen = new LinkedHashMap<String, ContactMen>();
		initWidgt();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				List<ContactMen> cSendMenlist = (List<ContactMen>) data
						.getSerializableExtra("chooseSMSMen");
				addNumChildView(cSendMenlist);
				// smsSendMenlist.addAll(cSendMenlist);

			}
			break;

		default:
			break;
		}
	}

	private void initWidgt() {
		flowLayout = (FlowLayout) findViewById(R.id.flow_menAdd);
		et_inputMen = (EditText) findViewById(R.id.et_inputMen);

		scroll_view = (ScrollView) findViewById(R.id.src_inputScroll);

		callLogHelper = new CallLogHelper(this);

		img_addLoacalMen = (ImageView) findViewById(R.id.img_addLocalMen);
		img_addLoacalMen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(NewSMSActivity.this,
						SmsMenActivity.class);
				startActivityForResult(intent, 1);

			}
		});
		lv_searchedMen = (ListView) findViewById(R.id.lv_sms_searchedmen);
		listLatestMen = callLogHelper.getSixCallList();
		latestMenAdapter = new LatestCallLogAdapter(this,
				R.layout.latest_callhis_item, listLatestMen);

		lv_searchedMen.setAdapter(latestMenAdapter);

		lv_searchedMen.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (isLastAdapter) {
					CallLogModel men = listLatestMen.get(position);
					addNumChildView(men.getName(), men.getNumber());
				} else {
					CallLogModel men = callmenSearchedList.get(position);
					addNumChildView(men.getName(), men.getNumber());
				}
				et_inputMen.setText("");
			}
		});

		et_inputMen.addTextChangedListener(new TextWatcher() {

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

				String input = et_inputMen.getText().toString().trim();
				if (input.equals("")) {
					lv_searchedMen.setVisibility(View.VISIBLE);
					lv_searchedMen.setAdapter(latestMenAdapter);
					isLastAdapter = true;
					return;
				}
				String lastIn = input.substring(input.length() - 1,
						input.length());
				if (!input.equals("")) {
					if (lastIn.equals(",") || lastIn.equals("\n")) {

						if (checkNumberLegal(input.replace(lastIn, ""))) {
							Toast.makeText(NewSMSActivity.this, "你输入的号码不合法",
									Toast.LENGTH_SHORT);

						} else {
							input = input.replace(",", "").trim();
							addNumChildView(input, input);
						}

						et_inputMen.setText("");
					} else {
						isLastAdapter = false;
						callmenSearchedList.clear();
						menSearchedList = MenSearch.search(input, menList);

						for (int i = 0; i < menSearchedList.size(); i++) {
							CallLogModel callMen = new CallLogModel();
							callMen.setName(menSearchedList.get(i).getName());
							callMen.setNumber(menSearchedList.get(i)
									.getNumber());
							callmenSearchedList.add(callMen);
						}

						if (menSearchedList.size() > 0) {

							localSeachMenAdapter = new LatestCallLogAdapter(
									NewSMSActivity.this,

									R.layout.latest_callhis_item,
									callmenSearchedList);
							lv_searchedMen.setAdapter(localSeachMenAdapter);
						}

						localSeachMenAdapter.notifyDataSetChanged();
					}
				}
			}
		});
	}

	public List<ContactMen> getAddedSmsMen() {
		List<ContactMen> addedMenlist = new ArrayList<ContactMen>();
		if (smsSendMen != null && smsSendMen.size() > 0) {
			for (String key : smsSendMen.keySet()) {

				ContactMen value = smsSendMen.get(key);
				addedMenlist.add(value);

			}
		}
		return addedMenlist;
	}

	private void getContact() {
		try {

			ContactHelper menHelper = ContactHelper.getContactHelper(this);
			menList = menHelper.getDataList();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void addNumChildView(List<ContactMen> addSmsMenlist) {
		for (int i = 0; i < addSmsMenlist.size(); i++) {
			addNumChildView(addSmsMenlist.get(i).getName(), addSmsMenlist
					.get(i).getNumber());
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public void addNumChildView(String aName, String aNumber) {
		positon++;
		tv_men = new TextView(NewSMSActivity.this);
		tv_men.setText(aName.trim());
		LayoutParams tvLay = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		tvLay.setMargins(5, 5, 5, 5);
		tv_men.setPadding(3, 3, 3, 3);
		tv_men.setLayoutParams(tvLay);
		tv_men.setBackgroundColor(Color.BLUE);
		tv_men.setTextColor(Color.GRAY);
		tv_men.setTextSize(12);
		tv_men.setTextAppearance(NewSMSActivity.this, R.style.textview_number);
		tv_men.setId(positon);
		tv_men.setTag(Boolean.valueOf(false));
		tv_men.setOnClickListener(NewSMSActivity.this);

		menMap.put(String.valueOf(tv_men.getId()), tv_men);
		flowLayout.addView(tv_men, flowLayout.getChildCount() - 1);
		ContactMen contactMen = new ContactMen();
		contactMen.setName(aName.trim());
		contactMen.setNumber(aNumber.trim());
		smsSendMen.put(String.valueOf(tv_men.getId()), contactMen);
	}

	private void showFrame() {
		fragmentManager = getFragmentManager();
		transation = fragmentManager.beginTransaction();
		Bundle bundle = new Bundle();

		if (smsSend_fragment == null) {
			smsSend_fragment = new SmsSend_Fragment(SendType.multi_newsms);
		}
		smsSend_fragment.setArguments(bundle);
		transation.replace(R.id.frame_newSmsAddBar, smsSend_fragment);
		transation.commit();

	}

	private boolean checkNumberLegal(String mNumber) {
		String regEx = "[\u4e00-\u9fa5]";
		Pattern pat = Pattern.compile(regEx);
		Matcher matcher = pat.matcher(mNumber);
		boolean flg = false;
		if (matcher.find()) {
			flg = true;
		}
		return flg;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		boolean b = (Boolean) v.getTag();
		if (!b) {
			Drawable drawable1 = getResources().getDrawable(
					R.drawable.del_newsmsmen);
			drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),
					drawable1.getMinimumHeight());
			((TextView) v).setCompoundDrawables(null, null, drawable1, null);
			drawable1 = null;
			v.setTag(Boolean.valueOf(true));
			for (String key : menMap.keySet()) {

				TextView value = menMap.get(key);

				if (key != String.valueOf(v.getId())) {
					value.setCompoundDrawables(null, null, null, null);
					value.setTag(Boolean.valueOf(false));
				}

			}
		} else {
			flowLayout.removeView(menMap.get(String.valueOf(v.getId())));
			menMap.remove(String.valueOf(v.getId()));
			smsSendMen.remove(String.valueOf(v.getId()));
		}

	}
}
