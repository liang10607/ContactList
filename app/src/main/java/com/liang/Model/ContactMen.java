package com.liang.Model;

import java.io.Serializable;
import java.text.Collator;

import android.os.Handler;
import android.util.Log;

public class ContactMen implements Serializable {

	private String contactId;

	private long img_id;

	private String name;

	private String number;

	private String address;

	private String email;

	private String birthday;

	private String phone_adress; // 号码归属地

	private String phone_operator; // 手机号所属运营商

	boolean isFavorite = false;

	public ContactMen() {
		// TODO Auto-generated constructor stub
	}

	public ContactMen(int imgPath, String name, String number, String address,
			String email, String birthday) {
		this.img_id = imgPath;
		this.name = name;
		this.number = number;
		this.address = address;
		this.email = email;
		this.birthday = birthday;
	}

	public ContactMen(String contactID, long imgPath, String name, String number) {
		this.contactId = contactID;
		this.img_id = imgPath;
		this.name = name;
		this.number = number;

	}

	public ContactMen(String contactID, long imgPath, String name) {
		this.contactId = contactID;
		this.img_id = imgPath;
		this.name = name;

	}

	public ContactMen(String contactID, String number, String name, long imgPath) {
		this.contactId = contactID;
		this.img_id = imgPath;
		this.name = name;
		this.number = number;

	}

	public ContactMen(int imgPath, String name, String number, String address,
			String email, String birthday, String phone_adress,
			String phone_operator) {
		// TODO Auto-generated constructor stub
		this.img_id = imgPath;
		this.name = name;
		this.number = number;
		this.address = address;
		this.email = email;
		this.birthday = birthday;
		this.phone_adress = phone_adress;
		this.phone_adress = phone_adress;
		this.phone_operator = phone_operator;

	}

	public int compareTo(Object o) {
		if (!(o instanceof ContactMen))
			throw new RuntimeException(
					"This is not a instance of Class \"Student\" ");
		Collator cmp = Collator.getInstance(java.util.Locale.CHINA);
		ContactMen cm = (ContactMen) o;

		if (cmp.compare(this.name, cm.name) > 0)
			return 1;
		else if (cmp.compare(this.name, cm.name) < 0)
			return -1;
		return 0;
	}

	public String getName() {
		return name;
	}

	public String getNumber() {
		return number;
	}

	public String getONumber() {

		String phoneNumber = number.replace("+86", "");
		phoneNumber = phoneNumber.replace(" ", "");
		return phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public String geteMail() {
		return email;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void seteMail(String eMail) {
		this.email = eMail;
	}

	public String getPhone_adress() {
		return phone_adress;
	}

	public String getPhone_operator() {
		return phone_operator;
	}

	public void setPhone_adress(String phone_adress) {
		this.phone_adress = phone_adress;
	}

	public void setPhone_operator(String phone_operator) {
		this.phone_operator = phone_operator;
	}

	public long getImg_id() {
		return img_id;
	}

	public void setImg_id(int img_id) {
		this.img_id = img_id;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "_id" + contactId + " 头像编号:" + String.valueOf(img_id) + " 名字:"
				+ name + " 号码" + number + " 住址:" + address + " 生日:" + birthday
				+ " email:" + email + " 归属地:" + phone_adress + " 运营商:"
				+ phone_operator;
	}

	public String shareString() {
		StringBuilder sb = new StringBuilder("");
		if (name != null && !name.equals("")) {
			sb.append("名字:" + name);
		}
		if (number != null && !number.equals("")) {
			sb.append("\n号码:" + number);
		}
		if (address != null && !address.equals("")) {
			sb.append("\n住址:" + address);
		}
		if (birthday != null && !birthday.equals("")) {
			sb.append("\n生日:" + birthday);
		}
		if (email != null && !email.equals("")) {
			sb.append("\nemail:" + email);
		}
		if (phone_adress != null && !phone_adress.equals("")) {
			sb.append("\n归属地:" + phone_adress);
		}
		if (phone_operator != null && !phone_operator.equals("")) {
			sb.append("\n运营商:" + phone_operator);
		}

		return sb.toString();

	}

	public String getContactId() {
		return contactId;
	}

	public String getEmail() {
		return email;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

}
