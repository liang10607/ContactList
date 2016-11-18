package com.liang.phonereceiver;

import com.liang.phonecontactlist.MainActivity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallLogReceiver extends BroadcastReceiver {

    private static final String TAG = "CallLogReceiver";

    private static boolean mIncomingFlag = false;
    private static String mIncomingNumber = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            mIncomingFlag = false;
            Log.e(TAG, "拨号中...");

        } else {
            TelephonyManager tManager = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            switch (tManager.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    mIncomingNumber = intent.getStringExtra("incoming_number");
                    Log.i(TAG, "RINGING :来电响铃中" + mIncomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i(TAG, "incoming ACCEPT :" + mIncomingNumber);
                    Log.e(TAG, "通话中...");
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i(TAG, "incoming IDLE待机中");
                    Intent logIntent = new Intent(
                            "com.liang.service.CALL_LOG_CHANGED");
                    context.startService(logIntent);
                    break;


            }
        }
    }

}
