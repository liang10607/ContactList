package com.liang.controlHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.provider.Contacts.ContactMethods;
import android.text.TextUtils;

import com.liang.Model.ContactMen;

public class MenSearch {
	/**
	 * 按号码-拼音搜索联系人
	 * 
	 * @param str
	 */
	public static List<ContactMen> search(String str,
			List<ContactMen> allContacts) {
		List<ContactMen> contactList = new ArrayList<ContactMen>();
		contactList.clear();

		// 如果搜索条件以0 1 +开头则按号码搜索
		if (str.startsWith("0") || str.startsWith("1") || str.startsWith("+")) {
			for (ContactMen contact1 : allContacts) {
				if (contact1.getNumber() != null && contact1.getName() != null) {
					if (contact1.getNumber().contains(str)
							|| contact1.getName().contains(str)) {

						contactList.add(contact1);
					}
				}
			}
			return contactList;
		}
		ChineseSpelling finder = ChineseSpelling.getInstance();

		String result = "";
		for (ContactMen contact1 : allContacts) {
			// 先将输入的字符串转换为拼音
			finder.setResource(str);
			result = finder.getSpelling();
			if (contains(contact1, result)) {
				contactList.add(contact1);
			} else if (contact1.getNumber().contains(str)) {

				contactList.add(contact1);
			}
		}
		return contactList;
	}

	/**
	 * 根据拼音搜索
	 * 
	 * @param str
	 *            正则表达式
	 * @param pyName
	 *            拼音
	 * @param isIncludsive
	 *            搜索条件是否大于6个字符
	 * @return
	 */
	public static boolean contains(ContactMen contact, String search) {
		if (TextUtils.isEmpty(contact.getName())) {
			return false;
		}

		boolean flag = false;

		// 简拼匹配,如果输入在字符串长度大于6就不按首字母匹配了
		if (search.length() < 6) {
			String firstLetters = FirstLetterUtil.getFirstLetter(contact
					.getName());
			// 不区分大小写
			Pattern firstLetterMatcher = Pattern.compile(search,
					Pattern.CASE_INSENSITIVE);
			flag = firstLetterMatcher.matcher(firstLetters).find();
		}

		if (!flag) { // 如果简拼已经找到了，就不使用全拼了
			// 全拼匹配
			ChineseSpelling finder = ChineseSpelling.getInstance();
			finder.setResource(contact.getName());
			// 不区分大小写
			Pattern pattern2 = Pattern
					.compile(search, Pattern.CASE_INSENSITIVE);
			Matcher matcher2 = pattern2.matcher(finder.getSpelling());
			flag = matcher2.find();
		}

		return flag;
	}
}
