package com.liang.phonecontactlist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.liang.Model.SMSModel;
import com.liang.MyUtil.SMSAdapter;
import com.liang.controlHelper.SMSHelper;

public class Sms_Fragment extends Fragment {

	private final static String TAG = "Sms_Fragment";

	View mView;

	List<SMSModel> smsList = new ArrayList<SMSModel>();

	TextView addNewSMS, smsMenu, tv_topNotify;

	SMSAdapter smsAdapter;

	SMSAdapter smsNotifyAdapter;

	SMSHelper smshelper;

	ListView smsListView;

	boolean isNotify;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sms_fragment, container, false);
		mView = view;
		smsListView = (ListView) view.findViewById(R.id.lv_sms);
		initWight(view);
		smshelper = SMSHelper.getSMSHelper(getActivity());

		if (isNotify) {
			smsList = smshelper.groupNotifySMSList();
		} else {
			smsList = smshelper.groupSMSList();
		}

		smsAdapter = new SMSAdapter(view.getContext(), R.layout.sms_list_item,
				smsList);

		smsListView.setAdapter(smsAdapter);

		smsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				SMSModel smsModel = smsList.get(position);
				if (smsModel.getPerson() != null
						&& smsModel.getPerson().equals("通知消息")) {
					Intent intent = new Intent(getActivity(),
							NotifySmsActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(getActivity(),
							SmsDetailActivity.class);
					intent.putExtra("smsNumer", smsModel.getAddress());
					if (smsModel.getType().equals("8")) {
						Log.e(TAG, "是一个群发聊天窗口");
						intent.putExtra("isMultiSend", true);
					}
					startActivity(intent);
				}

			}
		});

		smsListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});

		return view;
	}

	public void refreshListView() {
		smsList.clear();
		if (isNotify) {

			smsList.addAll(smshelper.groupNotifySMSList());
		} else {

			smsList.addAll(smshelper.groupSMSList());

		}
		smsAdapter.notifyDataSetChanged();

	}

	private void initWight(View view) {
		addNewSMS = (TextView) view.findViewById(R.id.tv_addNewMsg);
		smsMenu = (TextView) view.findViewById(R.id.tv_smsMenu);
		tv_topNotify = (TextView) view.findViewById(R.id.tv_topNotify);

		addNewSMS.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), NewSMSActivity.class);
				getActivity().startActivity(intent);
			}
		});
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		if (activity instanceof NotifySmsActivity) {
			isNotify = true;
		} else {
			isNotify = false;
		}
		super.onAttach(activity);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshListView();

	}

}
