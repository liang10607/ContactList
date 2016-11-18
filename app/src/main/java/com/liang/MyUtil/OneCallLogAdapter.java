package com.liang.MyUtil;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liang.Model.CallLogModel;
import com.liang.Model.ContactMen;
import com.liang.phonecontactlist.R;

public class OneCallLogAdapter extends ArrayAdapter<CallLogModel> {

	private int resourceId;

	String opra_adr;
	String opra_string;

	public OneCallLogAdapter(Context context, int resource,
			List<CallLogModel> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		resourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		CallLogModel callLogModel = getItem(position);
		View view;

		ViewHolder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_dataString = (TextView) view
					.findViewById(R.id.tv_ocallTime);
			viewHolder.tv_number_name = (TextView) view
					.findViewById(R.id.tv_calhis_onumber);
			viewHolder.tv_numberAdr = (TextView) view
					.findViewById(R.id.tv_calhis_onumAdr);
			viewHolder.img_menDetail = (TextView) view
					.findViewById(R.id.tv_omenDetail);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.tv_dataString.setText(callLogModel.getShowCallTime());
		viewHolder.tv_number_name.setText(callLogModel.getName());

		opra_adr = callLogModel.getPhone_adress();
		opra_string = callLogModel.getPhone_operator();
		switch (callLogModel.getCallType()) {
		case CallLogModel.INCOMING_CALL:
			Drawable drawable = getContext().getResources().getDrawable(
					R.drawable.incoming);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());

			viewHolder.tv_numberAdr.setCompoundDrawables(null, null, drawable,
					null);
			viewHolder.img_menDetail.setText("来电"
					+ ContactTimeUtils.getHMS(callLogModel.getCall_dur()));
			break;
		case CallLogModel.MISSED_CALL:
			viewHolder.tv_number_name.setTextColor(Color.RED);
			viewHolder.img_menDetail.setText("未接来电");
			viewHolder.img_menDetail.setTextColor(Color.RED);
			break;

		case CallLogModel.OUTCOMING_CALL:
			Drawable drawable1 = getContext().getResources().getDrawable(
					R.drawable.outcoming);
			drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),
					drawable1.getMinimumHeight());
			viewHolder.tv_numberAdr.setCompoundDrawables(null, null, drawable1,
					null);
			viewHolder.img_menDetail.setText("去电"
					+ ContactTimeUtils.getHMS(callLogModel.getCall_dur()));
			break;
		default:
			break;
		}
		viewHolder.tv_numberAdr.setText(" " + opra_adr + " " + opra_string);
		if (callLogModel.getCallType() == 3) {
			viewHolder.tv_number_name.setTextColor(Color.RED);

		} else {
			viewHolder.tv_number_name.setTextColor(Color.BLACK);
		}
		return view;
	}

	class ViewHolder {
		TextView tv_dataString;
		TextView tv_number_name;
		TextView tv_numberAdr;
		TextView img_menDetail;
	}

}
