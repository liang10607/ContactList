package com.liang.MyUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog.Calls;
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

public class CallLogAdapter extends ArrayAdapter<CallLogModel> {

    private final static String TAG = "CallLogAdapter";
    String opra_adr;
    String opra_string;
    CallLogModel callLogModel;
    Context mContext;
    MainActivity mainActivity;
    private int resourceId;

    public CallLogAdapter(Context context, int resource,
                          List<CallLogModel> objects) {
        super(context, resource, objects);
        // TODO Auto-generated constructor stub
        resourceId = resource;
        mainActivity = (MainActivity) context;
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
            viewHolder.tv_dataString = (TextView) view
                    .findViewById(R.id.tv_callTime);
            viewHolder.tv_number_name = (TextView) view
                    .findViewById(R.id.tv_calhis_number);
            viewHolder.tv_numberAdr = (TextView) view
                    .findViewById(R.id.tv_calhis_numAdr);
            viewHolder.img_menDetail = (ImageView) view
                    .findViewById(R.id.img_menDetail);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.img_menDetail.setTag(position);
        viewHolder.tv_dataString.setText(callLogModel.getShowCallTime());
        viewHolder.tv_number_name.setText(callLogModel.getName());
        viewHolder.img_menDetail.setOnClickListener(new MyListener(position));

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

                break;
            case CallLogModel.MISSED_CALL:
                viewHolder.tv_number_name.setTextColor(Color.RED);
                break;

            case CallLogModel.OUTCOMING_CALL:
                Drawable drawable1 = getContext().getResources().getDrawable(
                        R.drawable.outcoming);
                drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),
                        drawable1.getMinimumHeight());
                viewHolder.tv_numberAdr.setCompoundDrawables(null, null, drawable1,
                        null);
                break;
            default:
                break;
        }

        if (callLogModel.getCallType() == 3) {
            viewHolder.tv_number_name.setTextColor(Color.RED);
        } else {
            viewHolder.tv_number_name.setTextColor(Color.BLACK);
        }

        viewHolder.tv_numberAdr.setText(" " + opra_adr + " " + opra_string);

        return view;
    }

    class ViewHolder {
        TextView tv_dataString;
        TextView tv_number_name;
        TextView tv_numberAdr;
        ImageView img_menDetail;
    }

    private class MyListener implements OnClickListener {
        int mPosition;

        public MyListener(int inPosition) {
            mPosition = inPosition;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            callLogModel = getItem(mPosition);
            Log.d(TAG, "查看的通话记录详情对应的人:" + callLogModel.getNumber());
            mainActivity.callLogShowDialog(callLogModel.getNumber());
            int ss = 50;


        }

    }

}
