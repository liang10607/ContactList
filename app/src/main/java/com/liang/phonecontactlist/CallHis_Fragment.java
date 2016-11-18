package com.liang.phonecontactlist;

import java.util.ArrayList;
import java.util.List;

import com.liang.Model.CallLogModel;
import com.liang.MyUtil.CallLogAdapter;

import com.liang.controlHelper.CallLogHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;
import android.provider.CallLog.Calls;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CallHis_Fragment extends Fragment implements OnClickListener {

	private final static String TAG = "CallHis_Fragment";

	final Context contexta = getActivity();

	EditText et_numberInput;

	TextView tv_dialButton, tv_dialMenu;
	MainActivity mainActity;
	CallLogAdapter adapter;

	List<CallLogModel> callLogList = new ArrayList<CallLogModel>();

	List<CallLogModel> all_CallList = new ArrayList<CallLogModel>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.call_history_fragment, container,
				false);

		tv_dialButton = (TextView) view.findViewById(R.id.tv_dialButton);
		tv_dialMenu = (TextView) view.findViewById(R.id.tv_dialMenu);
		tv_dialMenu.setOnClickListener(this);
		tv_dialButton.setOnClickListener(this);

		mainActity = (MainActivity) getActivity();

		et_numberInput = mainActity.et_numberInput;

		CallLogHelper callLogHelper = new CallLogHelper(getActivity());

		try {
			callLogList = callLogHelper.getDataCallList();
			all_CallList.addAll(callLogList);

		} catch (Exception e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
		adapter = new CallLogAdapter(getActivity(), R.layout.callhis_item,
				callLogList);

		ListView callHisListv = (ListView) view.findViewById(R.id.LV_callHis);

		callHisListv.setAdapter(adapter);
		mainActity.showNumberKeyBoard();
		callHisListv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				CallLogModel menHisLog = callLogList.get(position);
				mainActity.call(menHisLog.getNumber());
			}

		});
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

	}

	public void setShowedCallList(int iType) {
		switch (iType) {
		case 0:
			callLogList.clear();

			callLogList.addAll(all_CallList);

			adapter.notifyDataSetChanged();
			break;
		case 1:
			callLogList.clear();
			for (int i = 0; i < all_CallList.size(); i++) {
				if (all_CallList.get(i).getCallType() == 1) {
					callLogList.add(all_CallList.get(i));
				}
			}
			adapter.notifyDataSetChanged();
			break;
		case 2:
			callLogList.clear();
			for (int i = 0; i < all_CallList.size(); i++) {
				if (all_CallList.get(i).getCallType() == 2) {
					callLogList.add(all_CallList.get(i));
				}
							}
			adapter.notifyDataSetChanged();
			break;
		case 3:
			callLogList.clear();
			for (int i = 0; i < all_CallList.size(); i++) {
				if (all_CallList.get(i).getCallType() == 3) {
					callLogList.add(all_CallList.get(i));

				}
			}
			adapter.notifyDataSetChanged();
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_dialButton:
			et_numberInput.setVisibility(View.GONE);
			mainActity.showNumberKeyBoard();
			break;

		case R.id.tv_dialMenu:
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			// builder.setIcon(R.drawable.);
			builder.setTitle("选择通话类型");
			// 指定下拉列表的显示数据
			final String[] cities = { "全部通话", "已接电话", "已拨电话", "未接来电" };
			// 设置一个下拉的列表选择项
			builder.setItems(cities, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					switch (which) {
					case 0:
						setShowedCallList(0);
						break;
					case Calls.INCOMING_TYPE:
						setShowedCallList(1);
						break;
					case Calls.OUTGOING_TYPE:
						setShowedCallList(2);
						break;
					case Calls.MISSED_TYPE:
						setShowedCallList(3);
						break;

					default:
						break;
					}
					adapter.notifyDataSetChanged();
				}
			});
			builder.show();
			break;
		default:
			break;
		}
	}
}
