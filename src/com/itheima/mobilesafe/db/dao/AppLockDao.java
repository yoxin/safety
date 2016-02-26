package com.itheima.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.itheima.mobilesafe.db.AppLockOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AppLockDao {
	AppLockOpenHelper heaper;
	private static String TABLE = "applock";

	public AppLockDao(Context context) {
		if (heaper == null) {
			heaper = new AppLockOpenHelper(context);
		}
	}

	/**
	 * ��ѯ�����������Ƿ����
	 * 
	 * @param number
	 *            �ֻ�����
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
	 * ��������
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
		return result;
	}

	/**
	 * ɾ������
	 * 
	 * @param packagename
	 * @return
	 */
	public void delete(String packagename) {
		SQLiteDatabase db = heaper.getWritableDatabase();
		db.delete(TABLE, "packagename = ?", new String[] { packagename });
		db.close();
	}
}
