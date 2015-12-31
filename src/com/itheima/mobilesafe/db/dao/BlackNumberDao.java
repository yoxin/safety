package com.itheima.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itheima.mobilesafe.db.BlackNumberDBOpenHeaper;
import com.itheima.mobilesafe.domain.BlackNumberInfo;

public class BlackNumberDao {
	BlackNumberDBOpenHeaper heaper;
	private static String TABLE = "blacknumber";
	public BlackNumberDao(Context context) {
		if (heaper == null) {
			heaper = new BlackNumberDBOpenHeaper(context);
		}
	}
	
	/**
	 * ��ѯ�����������Ƿ����
	 * @param number �ֻ�����
	 * @return
	 */
	public boolean find(String number) {
		SQLiteDatabase db = heaper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from blacknumber where number = ?", new String[]{number});
		if (cursor.moveToNext()) {
			return true;
		}
		db.close();
		cursor.close();
		return false;
	}
	
	/**
	 * ���Ҳ�����ȫ������
	 * @return
	 */
	public List<BlackNumberInfo> findAll() {
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = heaper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from blacknumber", null);
		while (cursor.moveToNext()) {
			Integer id = cursor.getInt(cursor.getColumnIndex("_id"));
			String number = cursor.getString(cursor.getColumnIndex("number"));
			String mode = cursor.getString(cursor.getColumnIndex("mode"));
			BlackNumberInfo info = new BlackNumberInfo(number, mode, id);
			list.add(info);
		}
		db.close();
		cursor.close();
		return list;
	}
	
	/**
	 * ɾ�������������Ӧ����
	 * @param number �ֻ�����
	 */
	public void delete(String number) {
		SQLiteDatabase db = heaper.getWritableDatabase();
		db.delete(TABLE, "number = ?", new String[]{number});
		db.close();
	}
	
	/**
	 * ɾ��ȫ������
	 */
	public void deleteAll() {
		SQLiteDatabase db = heaper.getWritableDatabase();
		db.delete(TABLE, null, null);
		db.close();
	}
	
	/**
	 * ��������
	 * @param number �ֻ�����
	 * @param mode ģʽ
	 */
	public void add(String number, String mode) {
		SQLiteDatabase db = heaper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		db.insert(TABLE, null, values);
		db.close();
	}
	
	/**
	 * ��������
	 * @param number �ֻ�����
	 * @param NewMode ģʽ
	 */
	public void update(String number, String NewMode) {
		SQLiteDatabase db = heaper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", NewMode);
		db.update(TABLE, values, "number = ?", new String[]{number});
		db.close();
	}
	
	/**
	 * ͨ��Id��������
	 * @param _id
	 * @param NewNumber
	 * @param NewMode
	 */
	public void updateById(Integer _id, String NewNumber, String NewMode) {
		SQLiteDatabase db = heaper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", NewMode);
		values.put("number", NewNumber);
		db.update(TABLE, values, "_id = ?", new String[]{String.valueOf(_id)});
		db.close();
	}
}
