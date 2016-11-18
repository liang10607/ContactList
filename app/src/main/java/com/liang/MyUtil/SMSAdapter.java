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
import com.liang.Model.SMSModel;
import com.liang.phonecontactlist.R;
import com.liang.phonenum.utils.DatabaseHelper;

public class SMSAdapter extends ArrayAdapter<SMSModel> {

	private int resourceId;

	String sms_bodyExt;
	int sms_type;

	DatabaseHelper dataHelper;

	public SMSAdapter(Context context, int resource, List<SMSModel> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		resourceId = resource;
		dataHelper = DatabaseHelper.getDatabaseHelper(context);
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
					.findViewById(R.id.tv_smsTime);
			viewHolder.tv_number_name = (TextView) view
					.findViewById(R.id.tv_sms_number);
			viewHolder.tv_sms_body = (TextView) view
					.findViewById(R.id.tv_sms_bodyExt);
			viewHolder.img_sms_notify = (ImageView) view
					.findViewById(R.id.img_sms_notify);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.tv_dataString.setText(smsModel.getShortDate());

		if (smsModel.getPerson() == null || smsModel.getPerson().equals("")) {
			if (smsModel.getSms_count() > 1) {
				viewHolder.tv_number_name.setText(smsModel.getAddress() + "("
						+ smsModel.getSms_count() + ")");
			} else {
				viewHolder.tv_number_name.setText(smsModel.getAddress());
			}

		}

		if ((smsModel.getPerson() == null || smsModel.getPerson().equals(""))
				&& smsModel.getType().equals("8")) {
			String groupName = dataHelper.dao.getSmsGroupName(smsModel
					.getAddress());
			if (smsModel.getSms_count() > 1) {
				viewHolder.tv_number_name.setText(groupName + "("
						+ smsModel.getSms_count() + ")");
			} else {
				viewHolder.tv_number_name.setText(groupName);
			}

		}

		if (smsModel.getPerson() != null && smsModel.getPerson().equals("通知消息")) {
			viewHolder.tv_number_name.setText(smsModel.getPerson());
			viewHolder.img_sms_notify.setVisibility(View.VISIBLE);
		}

		if (smsModel.getPerson() != null && !smsModel.getPerson().equals("")
				&& !smsModel.getPerson().equals("通知消息")) {

			if (smsModel.getSms_count() > 1) {
				viewHolder.tv_number_name.setText(smsModel.getPerson() + "("
						+ smsModel.getSms_count() + ")");
			} else {
				viewHolder.tv_number_name.setText(smsModel.getPerson());
			}

		}

		sms_bodyExt = smsModel.getBody();
		sms_type = Integer.parseInt(smsModel.getType());

		viewHolder.tv_sms_body.setText(smsModel.getBody());

		return view;
	}

	class ViewHolder {
		TextView tv_dataString;
		TextView tv_number_name;
		TextView tv_sms_body;
		ImageView img_sms_notify;
	}

}
