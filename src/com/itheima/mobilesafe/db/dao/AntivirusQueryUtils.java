package com.itheima.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 病毒数据库查询工具
 * 
 * @author YOXIN
 * 
 */
public class AntivirusQueryUtils {
	// 病毒数据库本地数据库目录
	private static String path = "data/data/com.itheima.mobilesafe/files/antivirus.db";

	/**
	 * 检查当前程序特征码MD5是否在数据库里面
	 * 
	 * @param md5
	 * @return
	 */
	public static boolean isVirus(String md5) {
		// 返回结果
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
