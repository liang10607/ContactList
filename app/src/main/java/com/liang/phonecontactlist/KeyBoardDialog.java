package com.liang.phonecontactlist;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.liang.Model.ContactMen;
import com.liang.MyUtil.KeyBoardListener;

public class KeyBoardDialog extends Dialog implements
		android.view.View.OnClickListener {

	private static final String TAG = "KeyBoardDialog";

	Context mContext;

	RelativeLayout key0, key1, key2, key3, key4, key5, key6, key7, key8, key9,
			key_X, key_J;

	TextView tv_key_dial;

	ImageView iv_keyShow, iv_keyMenu;

	private KeyBoardListener keyListener;

	public KeyBoardDialog(Context context, ContactMen iMen) {
		super(context);

	}

	public KeyBoardDialog(Context context) {
		super(context);
	}

	public KeyBoardDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
	}

	public void setBaseDate(KeyBoardListener iKeyListener) {
		initWigdt();
		this.keyListener = iKeyListener;
	}

	public void setMenuDeleteKey(boolean keyFlag) {
		if (keyFlag) {
			iv_keyMenu.setEnabled(true);
			tv_key_dial.setEnabled(true);

		} else {
			iv_keyMenu.setEnabled(false);
			tv_key_dial.setEnabled(false);
		}
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();

	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		super.cancel();
	}

	public void goMenDetail() {

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();

	}

	public void initWigdt() {

		key0 = (RelativeLayout) findViewById(R.id.key0);
		key1 = (RelativeLayout) findViewById(R.id.key1);
		key2 = (RelativeLayout) findViewById(R.id.key2);
		key3 = (RelativeLayout) findViewById(R.id.key3);
		key4 = (RelativeLayout) findViewById(R.id.key4);
		key5 = (RelativeLayout) findViewById(R.id.key5);
		key6 = (RelativeLayout) findViewById(R.id.key6);
		key7 = (RelativeLayout) findViewById(R.id.key7);
		key8 = (RelativeLayout) findViewById(R.id.key8);
		key9 = (RelativeLayout) findViewById(R.id.key9);
		key_J = (RelativeLayout) findViewById(R.id.key_jing);
		key_X = (RelativeLayout) findViewById(R.id.key_Xing);

		tv_key_dial = (TextView) findViewById(R.id.key_dial);
		tv_key_dial.setEnabled(false);

		iv_keyShow = (ImageView) findViewById(R.id.key_showbtn);
		iv_keyMenu = (ImageView) findViewById(R.id.key_menu);

		key0.setOnClickListener(this);
		key1.setOnClickListener(this);
		key2.setOnClickListener(this);
		key3.setOnClickListener(this);
		key4.setOnClickListener(this);
		key5.setOnClickListener(this);
		key6.setOnClickListener(this);
		key7.setOnClickListener(this);
		key8.setOnClickListener(this);
		key9.setOnClickListener(this);
		tv_key_dial.setOnClickListener(this);
		iv_keyShow.setOnClickListener(this);
		iv_keyMenu.setOnClickListener(this);
		key_J.setOnClickListener(this);
		key_X.setOnClickListener(this);

	}

	public static class Builder {
		private Context context;

		private View contentView;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		public KeyBoardDialog create(KeyBoardListener iKeyListener) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			KeyBoardDialog dialog = null;
			dialog = new KeyBoardDialog(context, R.style.CustomDialog);
			View layout = inflater.inflate(R.layout.number_keyboard, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

			dialog.setBaseDate(iKeyListener);
			return dialog;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.key0:
			keyListener.keyUp(KeyBoardListener.KEY_NAME.KEY0);
			break;
		case R.id.key1:
			keyListener.keyUp(KeyBoardListener.KEY_NAME.KEY1);
			break;
		case R.id.key2:
			keyListener.keyUp(KeyBoardListener.KEY_NAME.KEY2);
			break;
		case R.id.key3:
			keyListener.keyUp(KeyBoardListener.KEY_NAME.KEY3);
			break;
		case R.id.key4:
			keyListener.keyUp(KeyBoardListener.KEY_NAME.KEY4);
			break;
		case R.id.key5:
			keyListener.keyUp(KeyBoardListener.KEY_NAME.KEY5);
			break;
		case R.id.key6:
			keyListener.keyUp(KeyBoardListener.KEY_NAME.KEY6);
			break;
		case R.id.key7:
			keyListener.keyUp(KeyBoardListener.KEY_NAME.KEY7);
			break;
		case R.id.key8:
			keyListener.keyUp(KeyBoardListener.KEY_NAME.KEY8);
			break;
		case R.id.key9:
			keyListener.keyUp(KeyBoardListener.KEY_NAME.KEY9);
			break;
		case R.id.key_dial:
			keyListener.keyUp(KeyBoardListener.KEY_NAME.KEY_DIAL);
			break;
		case R.id.key_jing:
			keyListener.keyUp(KeyBoardListener.KEY_NAME.KEY_JING);
			break;
		case R.id.key_Xing:
			keyListener.keyUp(KeyBoardListener.KEY_NAME.KEY_XING);
			break;
		case R.id.key_menu:
			keyListener.keyUp(KeyBoardListener.KEY_NAME.KEY_DELETE);
			break;
		case R.id.key_showbtn:
			// keyListener.keyUp(KeyBoardListener.KEY_NAME.KEY_SHOWBOARD);
			this.dismiss();
			break;

		default:
			break;
		}
	}
}