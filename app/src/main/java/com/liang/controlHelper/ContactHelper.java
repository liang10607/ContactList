package com.liang.controlHelper;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts.Data;
import android.util.Log;

import com.liang.Model.ContactMen;
import com.liang.phonenum.utils.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ContactHelper {

	private static final String TAG = ContactHelper.class.getSimpleName();

	private static final String KEY_BIRTH = "birthday";
	private static final String KEY_ADDR = "address";
	private static final String KEY_NICKNAME = "nickname";
	private static final String KEY_ORG = "org";
	private static final String KEY_IM = "IM";
	private static final String KEY_NOTE = "note";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_PHONE = "phone";
	private static final String KEY_WEBSITE = "website";
	private static final String KEY_PHOTO = "photo";

	private Context context;

	Cursor cursor;

	static DatabaseHelper dataHelper;

	private static ContactHelper mInstance;

	public static ContactHelper getContactHelper(Context context) {
		if (mInstance == null) {
			mInstance = new ContactHelper(context);
			if (dataHelper == null) {
				dataHelper = DatabaseHelper.getDatabaseHelper(context
						.getApplicationContext());
			}

		}
		return mInstance;
	}

	private ContactHelper(Context context) {
		this.context = context;
	}

	public List<ContactMen> getDataList() {
		List<ContactMen> localMenList = new ArrayList<ContactMen>();
		localMenList = dataHelper.dao.getMenResult();
		return localMenList;
	}

	public List<ContactMen> getFavDataList() {
		List<ContactMen> localMenList = new ArrayList<ContactMen>();
		localMenList = dataHelper.dao.getFavMenResult();
		return localMenList;
	}

	public boolean setMenFavorite(ContactMen iMen) {
		return dataHelper.dao.updateFavorite(iMen);
	}

	public ContactMen getMenInResolver(String mName) {
		Cursor cursor1 = null;
		ContactMen contactMen = null;
		try {
			cursor1 = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					new String[] { ContactsContract.Contacts._ID,
							ContactsContract.Contacts.DISPLAY_NAME,
							ContactsContract.Contacts.PHOTO_ID },
					ContactsContract.Contacts.DISPLAY_NAME + "=?",
					new String[] { mName }, null);

			if (cursor1.moveToFirst()) {
				String contactName = null;

				String contactId = cursor1.getString(cursor1
						.getColumnIndex(ContactsContract.Contacts._ID));

				contactName = cursor1
						.getString(cursor1
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

				long photoId = cursor1.getLong(cursor1
						.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)); // phone

				contactMen = new ContactMen(contactId, photoId, contactName);
				contactMen.setNumber(getFirstNumber(contactId));
				Log.e(TAG, mName + "getMenInResolver" + contactMen.toString());

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor1 != null) {
				cursor1.close();
			}
		}

		return contactMen;
	}

	public ContactMen getSingelMenbyNum(String mNumber) {
		return dataHelper.dao.getSimgleMen(mNumber);
	}

	public List<ContactMen> parserJSONArray() {
		List<ContactMen> localMenList = new ArrayList<ContactMen>();
		Cursor cursor1 = null;
		try {
			cursor1 = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI, null, null, null,
					null);

			while (cursor1.moveToNext()) {
				String contactName = null;

				String contactId = cursor1.getString(cursor1
						.getColumnIndex(ContactsContract.Contacts._ID));

				contactName = cursor1
						.getString(cursor1
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

				long photoId = cursor1.getLong(cursor1
						.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)); // phone

				ContactMen contactMen = new ContactMen(contactId, photoId,
						contactName);
				contactMen.setNumber(getFirstNumber(contactId));
				localMenList.add(contactMen);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor1 != null) {
				cursor1.close();
			}
		}

		return localMenList;
	}

	public String getFirstNumber(String contactId) {
		JSONArray phoneArr;
		String number = "";
		try {
			phoneArr = getPhone(contactId);
			JSONObject jo = phoneArr.getJSONObject(0);
			number = jo.getString("phone");
			number = number.replace("-", "").replace(" ", "");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return number;
	}

	public void initCursor(String contactId) {
		if (cursor == null) {
			cursor = context.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
					new String[] { contactId + "" }, null);
		}
	}

	public JSONArray getPhone(String contactId) throws JSONException {
		initCursor(contactId);
		JSONArray phoneList = new JSONArray();
		try {

			while (cursor.moveToNext()) {
				int type = cursor
						.getInt(cursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
				String phoneType = ContactsContract.CommonDataKinds.Phone
						.getTypeLabel(context.getResources(), type, "")
						.toString();
				String phoneNumber = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

				JSONObject item = new JSONObject();
				item.put("phone", phoneNumber);
				item.put("type", phoneType);

				phoneList.put(item);
			}

		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return phoneList;
	}

	public void editContact(ContactMen imen) {

		try {
			AddContact(imen);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void DeleteMen(ContactMen imen) {
		Log.e(TAG, "删除联系人:" + imen.toString());
		ContentResolver resolver = context.getContentResolver();

		Uri uri = Uri.parse("content://com.android.contacts/data");

		resolver.delete(uri, "raw_contact_id=?",
				new String[] { imen.getContactId() });

		uri = Uri.parse("content://com.android.contacts/raw_contacts");

		resolver.delete(uri, "display_name=?", new String[] { imen.getName() });

		String[] whereparams = new String[] { imen.getContactId() };

		uri = ContactsContract.Contacts.CONTENT_URI;
		resolver.delete(uri, "_id=?", whereparams);

		dataHelper.dao.deleteContactData(imen);
	}

	public void AddContact(ContactMen iMen) throws Exception {
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		long contactid = ContentUris.parseId(resolver.insert(uri, values));

		uri = Uri.parse("content://com.android.contacts/data");

		// 添加姓名
		values.put("raw_contact_id", contactid);
		values.put(Data.MIMETYPE, "vnd.android.cursor.item/name");
		values.put("data1", iMen.getName());
		resolver.insert(uri, values);
		values.clear();

		// 添加电话
		values.put("raw_contact_id", contactid);
		values.put(Data.MIMETYPE, "vnd.android.cursor.item/phone_v2");
		values.put("data1", iMen.getNumber());
		resolver.insert(uri, values);
		values.clear();

		// 添加Email
		values.put("raw_contact_id", contactid);
		values.put(Data.MIMETYPE, "vnd.android.cursor.item/email_v2");
		values.put("data1", iMen.getEmail());
		resolver.insert(uri, values);

		// 添加地址
		values.put("raw_contact_id", contactid);
		values.put(Data.MIMETYPE, "vnd.android.cursor.item/postal-address_v2");
		values.put("data1", iMen.getAddress());
		resolver.insert(uri, values);

		Log.e(TAG, "添加记录");
		// 添加生日
		values.put("raw_contact_id", contactid);
		values.put(Data.MIMETYPE, "vnd.android.cursor.item/birthday_v2");
		values.put("data1", iMen.getBirthday());
		resolver.insert(uri, values);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ContactMen con = getMenInResolver(iMen.getName());

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		dataHelper.dao.insertSingleMen(con);

	}

	public ContactMen getOneMen(String contactId) {
		ContactMen mMen = new ContactMen();
		try {

			mMen.setContactId(contactId);
			mMen.setAddress(getAddessItem(contactId));
			mMen.setBirthday(getBirthday(contactId));
			mMen.setNumber(getFirstNumber(contactId));
			mMen.seteMail(geteMailItem(contactId));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mMen;
	}

	private String geteMailItem(String contactId) {
		String result = "";
		try {
			JSONArray addrArr = getAddress(contactId);

			if (addrArr.length() > 0) {

				JSONObject jo1 = addrArr.getJSONObject(0);
				String email1 = jo1.getString("email");

				String emailType1 = "";
				if (jo1.getString("type") != null) {
					emailType1 = jo1.getString("type");
				}

				result = email1 + "" + emailType1;

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private String getAddessItem(String contactId) {
		String result = "";
		try {
			JSONArray addrArr = getAddress(contactId);

			if (addrArr.length() > 0) {

				JSONObject jo1 = addrArr.getJSONObject(0);
				String addr1 = jo1.getString("address");

				addr1 = addr1.substring(0, addr1.length() / 2);

				String addrType1 = "";
				if (jo1.getString("addressType") != null) {
					addrType1 = jo1.getString("addressType");
				}

				result = addrType1 + "" + addr1;

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public JSONArray getEmail(String contactId) throws JSONException {
		Cursor emailCur = null;
		JSONArray emailList = new JSONArray();
		try {
			emailCur = context.getContentResolver().query(
					ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
					new String[] { contactId }, null);

			while (emailCur.moveToNext()) {
				String email = emailCur
						.getString(emailCur
								.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
				int type = emailCur
						.getInt(emailCur
								.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
				String emailType = ContactsContract.CommonDataKinds.Email
						.getTypeLabel(context.getResources(), type, "")
						.toString();

				JSONObject item = new JSONObject();
				item.put("email", email);
				item.put("type", emailType);

				emailList.put(item);
			}

		} finally {
			if (emailCur != null) {
				emailCur.close();
			}
		}
		return emailList;
	}

	public String getBirthday(String contactId) throws JSONException {
		Cursor bCur = null;
		JSONObject data = new JSONObject();
		String birthday = null;
		try {
			bCur = context
					.getContentResolver()
					.query(ContactsContract.Data.CONTENT_URI,
							new String[] { ContactsContract.CommonDataKinds.Event.DATA },
							ContactsContract.Data.CONTACT_ID
									+ " = "
									+ contactId
									+ " AND "
									+ ContactsContract.Data.MIMETYPE
									+ " = '"
									+ ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
									+ "' AND "
									+ ContactsContract.CommonDataKinds.Event.TYPE
									+ " = "
									+ ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY,
							null, null);
			while (bCur.moveToNext()) {
				birthday = bCur.getString(0);
				data.put(KEY_BIRTH, birthday);

			}
		} finally {
			if (bCur != null) {
				bCur.close();
			}
		}
		return birthday;
	}

	/**
	 * Get address infomation of given contact.
	 * 
	 * @param contactId
	 * @param data
	 * @throws JSONException
	 */
	public JSONArray getAddress(String contactId) throws JSONException {
		JSONArray addrList = new JSONArray();
		Cursor postals = null;
		try {
			// address
			postals = context
					.getContentResolver()
					.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID
									+ " = " + contactId, null, null);

			int postFormattedNdx = postals
					.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS);
			int postTypeNdx = postals
					.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE);
			int postStreetNdx = postals
					.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET);

			while (postals.moveToNext()) {
				String addressType = ContactsContract.CommonDataKinds.StructuredPostal
						.getTypeLabel(context.getResources(), postTypeNdx, "")
						.toString();
				String str1 = postals.getString(postFormattedNdx);
				String str2 = postals.getString(postStreetNdx);

				JSONObject item = new JSONObject();
				item.put("addressType", addressType);
				item.put("address", str1 + str2);

				addrList.put(item);
			}

		} finally {
			if (postals != null) {
				postals.close();
			}
		}
		return addrList;
	}

	/**
	 * Get the photo of given contact.
	 * 
	 * @param cr
	 * @param id
	 * @param photo_id
	 * @return
	 */
	public Bitmap getPhoto(String contactId, long photoId) throws JSONException {

		Bitmap photo = null;
		Uri uri = ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI,
				Long.parseLong(contactId));

		InputStream input = ContactsContract.Contacts
				.openContactPhotoInputStream(context.getContentResolver(), uri);
		if (input != null) {

			photo = BitmapFactory.decodeStream(input);

		} else {
			Log.d(TAG, "First try failed to load photo!");
		}

		byte[] photoBytes = null;
		Uri photoUri = ContentUris.withAppendedId(
				ContactsContract.Data.CONTENT_URI, photoId);
		Cursor c = context.getContentResolver().query(photoUri,
				new String[] { ContactsContract.CommonDataKinds.Photo.PHOTO },
				null, null, null);
		try {
			if (c.moveToFirst()) {
				photoBytes = c.getBlob(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}

		if (photoBytes != null) {

			photo = BitmapFactory.decodeByteArray(photoBytes, 0,
					photoBytes.length);

		} else {
			Log.d(TAG, "Second try also failed!");
		}
		return photo;
	}

}
