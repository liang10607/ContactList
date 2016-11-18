package com.liang.MyUtil;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liang.Model.SMSModel;
import com.liang.phonecontactlist.R;

public class SMSDetailAdapter extends ArrayAdapter<SMSModel> {

	private int resourceId;

	String sms_bodyExt;
	int sms_type;

	public SMSDetailAdapter(Context context, int resource,
			List<SMSModel> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		resourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		SMSModel smsModel = getItem(position);
		View view;

		ViewHolder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_dataString = (TextView) view
					.findViewById(R.id.tv_smsDetailTime);

			viewHolder.tv_sms_body = (TextView) view
					.findViewById(R.id.tv_msgItemBody);
			viewHolder.layout_msgBack = (LinearLayout) view
					.findViewById(R.id.msgBack_layout);

			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}

		if (smsModel == null) {
			Log.e("SMSDetailAdapter", "smsModel为null");
		}
		sms_bodyExt = smsModel.getBody();

		sms_type = Integer.parseInt(smsModel.getType());
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);// 工具类哦

		if (sms_type == 1) {
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

			viewHolder.layout_msgBack
					.setBackgroundResource(R.drawable.sms_left);
		} else if (sms_type == 2 || sms_type == 8) {
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			viewHolder.layout_msgBack
					.setBackgroundResource(R.drawable.sms_right);
		}
		viewHolder.layout_msgBack.setLayoutParams(layoutParams);
		viewHolder.tv_dataString.setText(smsModel.getWholeDate());
		viewHolder.tv_sms_body.setText(smsModel.getBody());

		return view;
	}

	class ViewHolder {
		TextView tv_dataString;
		TextView tv_sms_body;
		LinearLayout layout_msgBack;

	}

}
