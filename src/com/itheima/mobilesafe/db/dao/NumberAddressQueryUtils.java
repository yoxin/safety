package com.itheima.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberAddressQueryUtils {

	// 号码归属地本地数据库目录
	private static String path = "data/data/com.itheima.mobilesafe/files/address.db";

	/**
	 * 查询查询号码归属地
	 * 
	 * @return 地址
	 */
	public static String queryNumber(String phone) {
		String address = "查询不到";
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		if (phone.matches("^1[3458]\\d{9}$")) {
			Cursor cursor = database
					.rawQuery(
							"select location from data2 where id = (select outkey from data1 where id = ?)",
							new String[] { phone.substring(0, 7) });
			while (cursor.moveToNext()) {
				address = cursor.getString(0);
			}
			cursor.close();
		} else {
			switch (phone.length()) {
			case 3:
				address = "特殊号码";
				break;
			case 4:// 5556
				address = "模拟器";
				break;
			case 5:
				address = "客服电话";
				break;
			case 7:
				address = "本地电话";
				break;

			case 8:
				address = "本地电话";
				break;
			default:
				if (phone.length() > 10 && phone.startsWith("0")) {
					// 010 12345678
					Cursor cursor = database.rawQuery(
							"select location from data2 where area = ?",
							new String[] { phone.substring(1, 3) });
					if (cursor.moveToNext()) {
						String location = cursor.getString(0);
						address = location.substring(0, location.length() - 2);
					}
					cursor.close();

					// 0855 12345678
					cursor = database.rawQuery(
							"select location from data2 where area = ?",
							new String[] { phone.substring(1, 4) });
					if (cursor.moveToNext()) {
						String location = cursor.getString(0);
						address = location.substring(0, location.length() - 2);
					}
					cursor.close();
				}

			}
		}

		// 记得关闭资源
		database.close();

		return address;
	}
}
