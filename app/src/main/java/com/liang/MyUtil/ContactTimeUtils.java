package com.liang.MyUtil;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@SuppressLint("SimpleDateFormat")
public class ContactTimeUtils {

	public final static int SIMPLEDATE = 0;
	public final static int SIMPLEONLYTIME = 1;
	public final static int SIMPLEALL = 2;

	public static Date StringToDate(String strDate) {
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			date = (Date) sdf.parse(strDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	public static String getHMS(String ssS) {
		String result = ssS;
		int hh = 0;
		int mm = 0;
		int ss = 0;
		int iSS = Integer.parseInt(ssS);
		int ass = iSS;
		if (iSS >= 3600) {
			hh = iSS / 3600;
			iSS = iSS % 3600;
			if (iSS >= 60) {
				mm = iSS / 60;
				ss = iSS % 60;
			} else {
				mm = 0;
				ss = iSS;
			}
		} else {
			hh = 0;
			if (iSS >= 60) {
				mm = iSS / 60;
				ss = iSS % 60;
			} else {

				mm = 0;
				ss = iSS;
			}
		}
		if (ass == 0) {
			result = "未接";
		} else {
			if (hh == 0) {
				result = "" + mm + "分" + ss + "秒";

			} else {
				result = hh + "小时" + mm + "分" + ss + "秒";
			}
		}
		return result;
	}

	private static String getTodayTime() {
		String result = null;
		// 得到long类型当前时间
		long l = System.currentTimeMillis();
		Date date = new Date(l);
		SimpleDateFormat dateFormat0 = new SimpleDateFormat("yyyyMMdd");
		result = new String(dateFormat0.format(date));
		return result;
	}

	public static boolean isTotoday(String calltime) {
		boolean result = false;
		if (longTimeToString(calltime, 0).equals(getTodayTime())) {
			result = true;
		}
		return result;
	}

	@SuppressLint("SimpleDateFormat")
	public static String CurTimeToString(int timekind) {
		String result = null;
		// 得到long类型当前时间
		long l = System.currentTimeMillis();
		// new日期对象
		Date date = new Date(l);
		// 转换提日期输出格式
		SimpleDateFormat dateFormat0 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("HHmmss");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat dateFormat3 = new SimpleDateFormat("ddHHmmss");
		switch (timekind) {
		case 0:
			result = new String(dateFormat0.format(date));
			break;
		case 1:
			result = new String(dateFormat1.format(date));
			break;
		case 2:
			result = new String(dateFormat2.format(date));
			break;
		case 3:
			result = new String(dateFormat3.format(date));
			break;
		case 4:
			result = String.valueOf(l);
			break;
		default:
			break;
		}

		return result;

	}

	@SuppressLint("SimpleDateFormat")
	public static String longTimeToString(String longTime, int timekind) {
		String result = null;
		// 得到long类型当前时间
		long l = Long.parseLong(longTime);
		// new日期对象
		Date date = new Date(l);
		// 转换提日期输出格式
		SimpleDateFormat dateFormat0 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("HHmmss");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat dateFormat3 = new SimpleDateFormat("MM/dd");
		SimpleDateFormat dateFormat4 = new SimpleDateFormat("HH:mm");
		switch (timekind) {
		case 0:
			result = new String(dateFormat0.format(date));
			break;
		case 1:
			result = new String(dateFormat1.format(date));
			break;
		case 2:
			result = new String(dateFormat2.format(date));
			break;
		case 3:
			result = new String(dateFormat3.format(date));
			break;
		case 4:
			result = new String(dateFormat4.format(date));
			break;
		default:
			break;
		}

		return result;

	}

	public static Bitmap rotaingImageView(int paramInt, Bitmap paramBitmap) {
		Bitmap localBitmap = paramBitmap;
		int i = paramBitmap.getWidth();
		int j = paramBitmap.getHeight();
		if (i > j) {
			Matrix localMatrix = new Matrix();
			localMatrix.postRotate(paramInt);
			localBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, i, j,
					localMatrix, true);
		}
		return localBitmap;
	}

}
