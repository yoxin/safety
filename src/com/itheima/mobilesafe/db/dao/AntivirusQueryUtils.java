package com.itheima.mobilesafe.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itheima.mobilesafe.domain.Virus;

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
