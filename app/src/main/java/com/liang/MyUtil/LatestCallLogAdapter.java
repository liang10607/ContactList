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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liang.Model.CallLogModel;
import com.liang.Model.ContactMen;
import com.liang.phonecontactlist.MainActivity;
import com.liang.phonecontactlist.R;

public class LatestCallLogAdapter extends ArrayAdapter<CallLogModel> {

	private final static String TAG = "LatestCallLogAdapter";

	private int resourceId;

	CallLogModel callLogModel;

	Context mContext;

	public LatestCallLogAdapter(Context context, int resource,
			List<CallLogModel> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		resourceId = resource;

		mContext = context;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		callLogModel = getItem(position);

		View view;

		ViewHolder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();

			viewHolder.tv_number_name = (TextView) view
					.findViewById(R.id.tv_latest_men);
			viewHolder.tv_numberAdr = (TextView) view
					.findViewById(R.id.tv_latest_menNumber);

			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.tv_number_name.setText(callLogModel.getName());
		viewHolder.tv_numberAdr.setText(callLogModel.getNumber());

		return view;
	}

	class ViewHolder {

		TextView tv_number_name;
		TextView tv_numberAdr;

	}

}
