package com.liang.phonecontactlist;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.liang.Model.ContactMen;
import com.liang.MyUtil.MenLogAdapter;
import com.liang.MyUtil.MenSearchAdapter;

import com.liang.controlHelper.ContactHelper;
import com.liang.controlHelper.MenSearch;

public class MenLog_Fragment extends Fragment {

	private final static String TAG = "MenLog_Fragment";

	List<ContactMen> menList = new ArrayList<ContactMen>();

	List<ContactMen> favMenList = new ArrayList<ContactMen>();
	// PullMenParser menParser;
	View gView;

	ListView listview;

	MenLogAdapter adapter;
	MenSearchAdapter searchAdapter;
	List<ContactMen> menSearchedList = new ArrayList<ContactMen>();
	TextView et_nosearch;
	TextView mutv_searchMen;
	EditText et_searchMen;

	public void getLocalContact() {
		try {
			menList.clear();
			favMenList.clear();
			ContactHelper menHelper = ContactHelper
					.getContactHelper(getActivity());
			menList.addAll(menHelper.getDataList());
			favMenList.addAll(menHelper.getFavDataList());

			Collections.sort(menList, new CompratorByFileName());
			// refresMenList();
		} catch (Exception e) {

			Log.d("其他错误", " 错误");
			e.printStackTrace();
		}

	}

	Handler uiHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 61:
				refresMenList();
				break;

			default:
				break;
			}

		};
	};

	public void refresMen() {
		getLocalContact();
		refresMenList();

	}

	public void refresMenList() {
		if (gView == null) {
			return;
		}

		listview.setVisibility(View.VISIBLE);
		et_nosearch.setVisibility(View.GONE);
		adapter = new MenLogAdapter(gView.getContext(), R.layout.menlist,
				menList);

		if (gView != null) {

			listview.setAdapter(adapter);
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		refresMen();
		// refresMenList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.phonemen_fragment, container,
				false);
		gView = view;
		getLocalContact();
		listview = (ListView) gView.findViewById(R.id.LV_MenList);
		et_nosearch = (TextView) view.findViewById(R.id.search_noresult);

		TextView multidelete = (TextView) view
				.findViewById(R.id.tv_multidelete);
		if (multidelete == null) {
			Log.e(TAG, "multidelete不存在");
		}

		multidelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(
						"android.intent.action.MultiDeleteMenLog");
				intent.putExtra("contact", (Serializable) menList);
				startActivity(intent);
			}
		});

		et_searchMen = (EditText) view.findViewById(R.id.et_MenSearch);

		mutv_searchMen = (TextView) view.findViewById(R.id.menfrag_seach);

		mutv_searchMen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				et_searchMen.setFocusable(true);
				et_searchMen.setText("");
			}
		});

		et_searchMen.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

				if (s.toString().equals("")) {
					listview.setVisibility(View.VISIBLE);
					listview.setAdapter(adapter);
					et_nosearch.setVisibility(View.GONE);
				} else {
					menSearchedList = MenSearch.search(s.toString(), menList);

					searchAdapter = new MenSearchAdapter(gView.getContext(),
							R.layout.men_search_list, menSearchedList);

					if (menSearchedList.size() > 0) {
						et_nosearch.setVisibility(View.GONE);
						listview.setVisibility(View.VISIBLE);
						listview.setAdapter(searchAdapter);
					} else {
						listview.setVisibility(View.GONE);
						et_nosearch.setVisibility(View.VISIBLE);
					}
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				ContactMen men = new ContactMen();
				if (listview.getAdapter() == adapter) {
					men = menList.get(position);
				} else if (listview.getAdapter() == searchAdapter) {
					men = menSearchedList.get(position);
				}

				MainActivity parentActivity = (MainActivity) getActivity();
				parentActivity.showAlertDialog(men);

			}

		});

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

	}

	@SuppressWarnings("unused")
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

}
