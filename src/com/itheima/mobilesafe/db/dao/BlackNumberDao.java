package com.itheima.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itheima.mobilesafe.db.BlackNumberOpenHeaper;
import com.itheima.mobilesafe.domain.BlackNumberInfo;

public class BlackNumberDao {
	BlackNumberOpenHeaper heaper;
	private static String TABLE = "blacknumber";

	public BlackNumberDao(Context context) {
		if (heaper == null) {
			heaper = new BlackNumberOpenHeaper(context);
		}
	}

	/**
	 * ��ѯ�����������Ƿ����
	 * 
	 * @param number
	 *            �ֻ�����
	 * @return
	 */
	public boolean find(String number) {
		SQLiteDatabase db = heaper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from blacknumber where number = ?",
				new String[] { number });
		boolean result = false;
		if (cursor.moveToNext()) {
			result = true;
		}
		db.close();
		cursor.close();
		return false;
	}

	/**
	 * ���Ҳ�����ȫ������
	 * 
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
	 * ��offsetָ����λ�ÿ�ʼ��ѯlimit����Ϣ
	 * @param limit ��ѯ����Ϣ����Ŀ
	 * @param offset ��ѯ��ʼ��λ��
	 * @return ��ѯ��������
	 */
	public List<BlackNumberInfo> findAll(int limit, int offset) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = heaper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from blacknumber limit ? offset ?",
				new String[] {String.valueOf(limit), String.valueOf(offset)});
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
	 * 
	 * @param number
	 *            �ֻ�����
	 */
	public void delete(String number) {
		SQLiteDatabase db = heaper.getWritableDatabase();
		db.delete(TABLE, "number = ?", new String[] { number });
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
	 * 
	 * @param number
	 *            �ֻ�����
	 * @param mode
	 *            ģʽ
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
	 * 
	 * @param number
	 *            �ֻ�����
	 * @param NewMode
	 *            ģʽ
	 */
	public void update(String number, String NewMode) {
		SQLiteDatabase db = heaper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", NewMode);
		db.update(TABLE, values, "number = ?", new String[] { number });
		db.close();
	}

	/**
	 * ͨ��Id��������
	 * 
	 * @param _id
	 * @param NewNumber
	 * @param NewMode
	 */
	public void updateById(Integer _id, String NewNumber, String NewMode) {
		SQLiteDatabase db = heaper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", NewMode);
		values.put("number", NewNumber);
		db.update(TABLE, values, "_id = ?",
				new String[] { String.valueOf(_id) });
		db.close();
	}

	/**
	 * ��ѯmode
	 * 
	 * @param number
	 *            �绰����
	 * @return
	 */
	public String findMode(String number) {
		SQLiteDatabase db = heaper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select mode from blacknumber where number = ?",
				new String[] { number });
		String result = null;
		if (cursor.moveToNext()) {
			result = cursor.getString(0);
		}
		db.close();
		cursor.close();
		return result;
	}
	
	
}
