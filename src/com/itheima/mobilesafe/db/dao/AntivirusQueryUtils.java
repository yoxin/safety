package com.itheima.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
}
