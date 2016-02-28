package com.itheima.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.itheima.mobilesafe.db.AppLockOpenHelper;

public class AppLockDao {
	AppLockOpenHelper heaper;
	private Context context;
	private static String TABLE = "applock";

	public AppLockDao(Context context) {
		this.context = context;
		if (heaper == null) {
			heaper = new AppLockOpenHelper(context);
		}
	}

	/**
	 * 查询黑名单号码是否存在
	 * 
	 * @param number
	 *            手机号码
	 * @return
	 */
	public boolean find(String packagename) {
		SQLiteDatabase db = heaper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from applock where packagename = ?",
				new String[] { packagename });
		boolean result = false;
		if (cursor.moveToNext()) {
			result = true;
		}
		db.close();
		cursor.close();
		return result;
	}

	/**
	 * 查询所有的包名
	 * 
	 * @return
	 */
	public List<String> findAll() {
		List<String> result = new ArrayList<String>();
		SQLiteDatabase db = heaper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from applock", new String[] {});
		while (cursor.moveToNext()) {
			String packageName = cursor.getString(cursor
					.getColumnIndex("packagename"));
			result.add(packageName);
		}
		db.close();
		cursor.close();
		return result;
	}

	/**
	 * 插入数据
	 * 
	 * @param packagename
	 * @return
	 */
	public long insert(String packagename) {
		long result;
		SQLiteDatabase db = heaper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packagename", packagename);
		result = db.insert(TABLE, null, values);
		db.close();
		// 自定义内容提供者, 通知数据更新
		context.getContentResolver().notifyChange(
				Uri.parse("com.itheima.mobilesafe.applock.change"), null);
		return result;
	}

	/**
	 * 删除数据
	 * 
	 * @param packagename
	 * @return
	 */
	public void delete(String packagename) {
		SQLiteDatabase db = heaper.getWritableDatabase();
		db.delete(TABLE, "packagename = ?", new String[] { packagename });
		db.close();
		// 自定义内容提供者, 通知数据更新
		context.getContentResolver().notifyChange(
				Uri.parse("com.itheima.mobilesafe.applock.change"), null);
	}
}
