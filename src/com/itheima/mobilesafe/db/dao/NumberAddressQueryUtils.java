package com.itheima.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberAddressQueryUtils {

	// ��������ر������ݿ�Ŀ¼
	private static String path = "data/data/com.itheima.mobilesafe/files/address.db";

	/**
	 * ��ѯ��ѯ���������
	 * 
	 * @return ��ַ
	 */
	public static String queryNumber(String phone) {
		String address = "��ѯ����";
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
				address = "�������";
				break;
			case 4:// 5556
				address = "ģ����";
				break;
			case 5:
				address = "�ͷ��绰";
				break;
			case 7:
				address = "���ص绰";
				break;

			case 8:
				address = "���ص绰";
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

		// �ǵùر���Դ
		database.close();

		return address;
	}
}
