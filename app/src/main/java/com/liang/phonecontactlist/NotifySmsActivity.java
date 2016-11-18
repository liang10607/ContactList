package com.liang.phonecontactlist;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

public class NotifySmsActivity extends Activity {

	FragmentManager fragmentManager;
	FragmentTransaction transation;
	Sms_Fragment sms_fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_notify_sms);
		showFrame();
	}

	public void showFrame() {
		fragmentManager = getFragmentManager();
		transation = fragmentManager.beginTransaction();

		if (sms_fragment == null) {
			sms_fragment = new Sms_Fragment();
		}
		transation.replace(R.id.notigy_frameLayout, sms_fragment);
		transation.commit();

	}

}
