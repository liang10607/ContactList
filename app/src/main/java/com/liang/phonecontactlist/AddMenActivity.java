package com.liang.phonecontactlist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.liang.Model.ContactMen;

import com.liang.controlHelper.ContactHelper;
import com.liang.customUI.RoundImageView;
import com.liang.listInterface.MenListInterface;
import com.liang.phonenum.utils.DatabaseHelper;

public class AddMenActivity extends Activity {

	private final static String TAG = "AddMenActivity";

	RoundImageView img_menPhoto;

	EditText et_name;

	EditText et_number;

	EditText et_address;

	EditText et_email;

	EditText et_birthday;

	ContactMen contactMen;

	ContactMen OriMen;

	int phone_operator;

	String phone_adress;

	String opra_string;

	private boolean isEdit;

	List<ContactMen> iContactMens = new ArrayList<ContactMen>();

	ContactMen cm = null;

	String editMenID;

	ContactHelper contactHelper = ContactHelper.getContactHelper(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_men);

		img_menPhoto = (RoundImageView) findViewById(R.id.img_headphoto);

		et_name = (EditText) findViewById(R.id.et_MenName);

		et_number = (EditText) findViewById(R.id.et_numMen);

		et_address = (EditText) findViewById(R.id.et_adrMen);

		et_email = (EditText) findViewById(R.id.et_mailMen);

		et_birthday = (EditText) findViewById(R.id.et_birthMen);

		Intent intent = getIntent();

		OriMen = (ContactMen) intent.getSerializableExtra("NewMen");

		isEdit = (OriMen == null) ? false : true;

		String number = intent.getStringExtra("sms_number");
		if (number != null) {
			this.et_number.setText(number);
		}

		if (isEdit) {
			editMenID = OriMen.getContactId();
			if (OriMen.getImg_id() != 0) {
				try {
					this.img_menPhoto.setImageBitmap(contactHelper.getPhoto(
							OriMen.getContactId(), OriMen.getImg_id()));
					Log.e(TAG, "设置头像");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			this.et_name.setText(OriMen.getName());
			this.et_address.setText(OriMen.getAddress());
			this.et_birthday.setText(OriMen.getBirthday());
			this.et_email.setText(OriMen.geteMail());
			this.et_number.setText(OriMen.getNumber());
		}

	}

	public void cancle(View view) {
		finish();
	}

	public void saveMen(View view) {

		if ("".equals(et_number.getText().toString())) {

			Toast.makeText(AddMenActivity.this, "请输入号码", Toast.LENGTH_SHORT)
					.show();

		} else {
			DatabaseHelper dataHelper = DatabaseHelper
					.getDatabaseHelper(getApplicationContext());
			Map<String, String> map = dataHelper.dao
					.getNumberLocation(et_number.getText().toString());

			contactMen = new ContactMen(R.drawable.headnew,
					getEditTextStr(et_name), getEditTextStr(et_number),
					getEditTextStr(et_address), et_email.getText().toString(),
					getEditTextStr(et_birthday),
					(map != null) ? (map.get("province") + map.get("city"))
							: null, (map != null) ? map.get("telecom") : null);
			contactMen.setContactId(editMenID);
			if (isEdit) {
				contactHelper.DeleteMen(OriMen);
				contactHelper.editContact(contactMen);
			} else {
				try {
					contactHelper.AddContact(contactMen);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// Intent intent = new
			// Intent("com.liang.phonereceiver.CONTACTCHANGED");
			// sendBroadcast(intent);
			finish();

		}

	}

	private String getEditTextStr(EditText et) {
		if (et.getText() != null) {
			return et.getText().toString();
		} else {
			return null;
		}
	}
}
