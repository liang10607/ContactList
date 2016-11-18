package com.liang.phonenum.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.liang.phonecontactlist.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

// http://blog.csdn.net/trbbadboy/article/details/8001157

/**
 * <p>
 * This is a Assets Database Manager, you can use a assets database file in you
 * application. It will copy the database file to <em>
 * "/data/data/[your application package name]/database"</em> when you first
 * time you use it Then you can get a SQLiteDatabase object by the assets
 * database file.
 * </p>
 * How to use:
 * <ul>
 * <li>Initialize AssetsDatabaseManager.
 * <li>Get AssetsDatabaseManager.
 * <li>Get a SQLiteDatabase object through database file.
 * <li>Use this database object.
 * </ul>
 * Using example:<code>
 *      <li>AssetsDatabaseManager.initManager(getApplication()); 
 *      <li>AssetsDatabaseManager mg =AssetsDatabaseManager.getAssetsDatabaseManager(); 
 *      <li> SQLiteDatabase db1 = mg.getDatabase("db1.db"); </code>
 */
public class AssetsDatabaseManager {
	private static final String TAG = "AssetsDatabaseManager";
	private static String DB_PATH = "/data/data/%s/databases";// %s is
																// packageName.
	private Map<String, SQLiteDatabase> databases = new HashMap<String, SQLiteDatabase>();
	private Context mContext = null;
	private static AssetsDatabaseManager mInstance = null;// singleton pattern.

	private AssetsDatabaseManager(Context context) {
		this.mContext = context;
	}

	/** Initialize AssetDatabaseManager. */
	public static void initManager(Context context) {
		if (null == mInstance)
			mInstance = new AssetsDatabaseManager(context);
	}

	/** Get a AssetsDatabaseManager object. */
	public static AssetsDatabaseManager getAssetsDatabaseManager() {
		return mInstance;
	}

	/**
	 * Get a assets database, if this database is opened,this method is only
	 * return a copy of the opened database.
	 * 
	 * @param dbName
	 *            , the assets file which will be opened for a database.
	 * @return, if success it return a SQLiteDatabase object else return null.
	 */
	public SQLiteDatabase getDatabase(String dbName) {
		dbName = getPrefix(dbName) + ".db";// number_location.db
		if (databases.get(dbName) != null) {
			Log.i(TAG, String.format("Return a database copy of %s.", dbName));
			return databases.get(dbName);
		}
		if (null == mContext)
			return null;
		Log.i(TAG, String.format("Create database %s.", dbName));
		String dbPath = getDatabaseFilePath();
		String dbFile = getDatabaseFile(dbName);

		File file = new File(dbFile);// "/data/data/packageName/databases/dbName"
		SharedPreferences dbsp = mContext.getSharedPreferences(
				AssetsDatabaseManager.class.toString(), Context.MODE_PRIVATE);
		// Get Database file flag,if true means this database file was copied
		// and valid.
		boolean flag = dbsp.getBoolean(dbName, false);
		if (!flag || !file.exists()) {// if flag is false or file is not exist.
			file = new File(dbPath);
			if (!file.exists() && !file.mkdirs()) {
				Log.i(TAG, "Create \"" + dbPath + "\" failed!");
				return null;
			}
			if (!copyAssetsToFilesystem(dbName, dbFile)) {
				Log.i(TAG,
						String.format("Copy %s to %s failed!", dbName, dbFile));
				return null;
			}
			dbsp.edit().putBoolean(dbName, true).commit();
		}
		SQLiteDatabase db = SQLiteDatabase.openDatabase(dbFile, null,
				SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		if (null != db)
			databases.put(dbName, db);
		return db;

	}

	/** get file's prefix in front of the point. */
	private String getPrefix(String str) {
		return str.substring(0, str.lastIndexOf("."));
	}

	private String getDatabaseFilePath() {
		return String
				.format(DB_PATH, mContext.getApplicationInfo().packageName);
	}

	private String getDatabaseFile(String dbName) {
		return getDatabaseFilePath() + "/" + dbName;
	}

	/** Copy the database file to file system. */
	private boolean copyAssetsToFilesystem(String dbSrc, String dbDes) {
		Log.i(TAG, "Copy " + dbSrc + " to " + dbDes);
		InputStream inStream = null;
		ZipInputStream zin = null;
		OutputStream outStream = null;

		try {

			Log.e(TAG, getPrefix(dbSrc) + ".zip");
			inStream = mContext.getResources().openRawResource(
					R.raw.number_location);

			zin = new ZipInputStream(new BufferedInputStream(inStream));
			ZipEntry ze;
			byte[] buffer = new byte[1024];
			int count;

			while ((ze = zin.getNextEntry()) != null) {
				Log.i(TAG, "Unzipping " + ze.getName());
				outStream = new FileOutputStream(dbDes);
				while ((count = zin.read(buffer)) != -1) {
					outStream.write(buffer, 0, count);
				}
				outStream.close();
				zin.closeEntry();
			}
			zin.close();

		} catch (IOException e) {
			e.printStackTrace();
			try {
				if (zin != null)
					zin.close();
				if (outStream != null)
					outStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return false;
		}
		return true;
	}

	/** close assets database. */
	public boolean closeDatabase(String dbfile) {
		if (databases.get(dbfile) != null) {
			SQLiteDatabase db = databases.get(dbfile);
			db.close();
			databases.remove(dbfile);
			return true;
		}
		return false;
	}

	/** Close all assets databases. */
	public static void closeAllDatabase() {
		Log.i(TAG, "closeAllDatabases.");
		if (mInstance != null) {
			for (int i = 0; i < mInstance.databases.size(); i++) {
				if (mInstance.databases.get(i) != null)
					mInstance.databases.get(i).close();
			}
			mInstance.databases.clear();
		}
	}
}
