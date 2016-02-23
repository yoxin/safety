package com.itheima.mobilesafe.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itheima.mobilesafe.domain.Virus;

/**
 * �������ݿ��ѯ����
 * 
 * @author YOXIN
 * 
 */
public class AntivirusQueryUtils {
	// �������ݿⱾ�����ݿ�Ŀ¼
	private static String path = "data/data/com.itheima.mobilesafe/files/antivirus.db";

	/**
	 * ��鵱ǰ����������MD5�Ƿ������ݿ�����
	 * 
	 * @param md5
	 * @return
	 */
	public static boolean isVirus(String md5) {
		// ���ؽ��
		boolean isVirus = false;

		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);

		Cursor cursor = database.rawQuery("select * from datable where md5=?",
				new String[] { md5 });

		if (cursor.moveToNext()) {
			isVirus = true;
		} else {
			isVirus = false;
		}
		cursor.close();
		database.close();

		return isVirus;
	}

	public static boolean addVirus(Virus virus) {
		boolean result = false;
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		if (!isVirus(virus.getMd5())) {
			ContentValues values = new ContentValues();
			values.put("md5", virus.getMd5());
			values.put("type", 6);
			values.put("name", "defaultName");
			values.put("desc", virus.getDesc());
			database.insert("datable", null, values);
			result = true;
		} else {
			result = false;
		}
		database.close();
		return result;
	}
	
}
