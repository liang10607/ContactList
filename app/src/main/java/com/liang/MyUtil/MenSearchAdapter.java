package com.liang.MyUtil;

import java.io.File;
import java.text.Collator;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liang.Model.ContactMen;
import com.liang.controlHelper.ContactHelper;
import com.liang.phonecontactlist.R;

public class MenSearchAdapter extends ArrayAdapter<ContactMen> {

	private int resourceId;

	public MenSearchAdapter(Context context, int resource,
			List<ContactMen> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		resourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ContactMen contactMen = getItem(position);
		View view;

		ViewHolder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.headImage = (ImageView) view
					.findViewById(R.id.search_itemPhoto);
			viewHolder.menName = (TextView) view
					.findViewById(R.id.search_menName);
			viewHolder.seachContent = (TextView) view
					.findViewById(R.id.search_content);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		if (contactMen.getImg_id() == 0) {
			viewHolder.headImage.setImageResource(R.drawable.headnew);
		} else {
			try {
				ContactHelper contactHelper = ContactHelper
						.getContactHelper(getContext());
				viewHolder.headImage.setImageBitmap(contactHelper.getPhoto(
						contactMen.getContactId(), contactMen.getImg_id()));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		viewHolder.menName.setText(contactMen.getName());
		viewHolder.seachContent.setText(contactMen.getNumber());
		return view;
	}

	class ViewHolder {
		ImageView headImage;

		TextView menName;

		TextView seachContent;
	}

}
