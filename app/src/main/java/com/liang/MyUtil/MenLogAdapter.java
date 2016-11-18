package com.liang.MyUtil;

import java.io.File;
import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.liang.Model.ContactMen;
import com.liang.controlHelper.ContactHelper;
import com.liang.phonecontactlist.R;

public class MenLogAdapter extends ArrayAdapter<ContactMen> {

	private int resourceId;

	private List<ContactMen> listData;

	Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();

	public MenLogAdapter(Context context, int resource, List<ContactMen> objects) {
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
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.headImage = (ImageView) view
					.findViewById(R.id.menlist_itePhoto);
			viewHolder.menName = (TextView) view
					.findViewById(R.id.menlist_itemName);
			viewHolder.checkBox = (CheckBox) view
					.findViewById(R.id.checked_men);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		if (contactMen.getImg_id() == 0) {
			viewHolder.headImage.setImageResource(R.drawable.headnew);
		} else {
			ContactHelper contactHelper = ContactHelper
					.getContactHelper(getContext());
			try {
				viewHolder.headImage.setImageBitmap(contactHelper.getPhoto(
						contactMen.getContactId(), contactMen.getImg_id()));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		viewHolder.menName.setText(contactMen.getName());
		viewHolder.checkBox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						int radiaoId = Integer.parseInt(buttonView.getTag()
								.toString());
						if (isChecked) {
							// ��ѡ�еķ���hashmap��
							isCheckMap.put(radiaoId, isChecked);
						} else {
							// ȡ��ѡ�е����޳�
							isCheckMap.remove(radiaoId);
						}
					}
				});

		return view;
	}

	class ViewHolder {
		ImageView headImage;

		TextView menName;

		CheckBox checkBox;
	}

}
