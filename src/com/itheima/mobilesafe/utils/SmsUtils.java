package com.itheima.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

public class SmsUtils {

	private static final String TAG = "SmsUtils";

	/**
	 * ���ű���
	 * 
	 * @param context
	 * @throws Exception
	 */
	public static void backup(Context context, ProgressCallBack progress)
			throws Exception {
		ContentResolver resolver = context.getContentResolver();
		File file = new File(Environment.getExternalStorageDirectory(),
				"smsBackup.xml");
		FileOutputStream fos = new FileOutputStream(file);
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(fos, "utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[] { "body", "address",
				"type", "date" }, null, null, null);
		int max = cursor.getCount();
		progress.setMax(max);
		serializer.attribute(null, "max", String.valueOf(max));
		int progressValue = 0;
		while (cursor.moveToNext()) {
			Thread.sleep(500);
			serializer.startTag(null, "sms");
			serializer.startTag(null, "body");
			serializer.text(cursor.getString(0));
			serializer.endTag(null, "body");
			serializer.startTag(null, "address");
			serializer.text(cursor.getString(1));
			serializer.endTag(null, "address");
			serializer.startTag(null, "type");
			serializer.text(cursor.getString(2));
			serializer.endTag(null, "type");
			serializer.startTag(null, "date");
			serializer.text(cursor.getString(3));
			serializer.endTag(null, "date");
			serializer.endTag(null, "sms");
			progressValue++;
			LogUtil.d(TAG, progressValue + "");
			progress.setProgress(progressValue);
		}
		cursor.close();
		serializer.endTag(null, "smss");
		serializer.endDocument();
		fos.close();
	}

	public interface ProgressCallBack {
		/**
		 * ���ý����������ֵ
		 * 
		 * @param max
		 *            ���ֵ
		 */
		void setMax(int max);

		/**
		 * ���ý������Ľ���
		 * 
		 * @param progress
		 */
		void setProgress(int progress);

	}

	/**
	 * ��ԭ����
	 * 
	 * @param context
	 * @param flag
	 *            �Ƿ�ɾ��ԭ���Ķ���
	 * @param progress
	 *            �������ӿ�
	 * @throws Exception
	 */
	public static void restore(Context context, boolean flag,
			ProgressCallBack progress) throws Exception {
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms/");
		// 1.��ȡsd���ϵ�xml�ļ�
		File file = new File(Environment.getExternalStorageDirectory(),
				"smsBackup.xml");
		FileInputStream fis = new FileInputStream(file);
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(fis, "utf-8");
		// ɾ���ɶ���
		if (flag) {
			resolver.delete(uri, null, null);
		}
		int eventType = parser.getEventType();
		String body = null;
		String address = null;
		String type = null;
		String date = null;
		int progressValue = 0;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = parser.getName();
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if (name.equals("smss")) {
					// 2.��ȡmax
					String max = parser.getAttributeValue(null, "max");
					// ���ý��������ֵ
					progress.setMax(Integer.valueOf(max));
					// 3.��ȡÿһ����Ϣ��body,address,type,date
				} else if (name.equals("body")) {
					body = parser.nextText();
					LogUtil.d(TAG, "body:" + body);
				} else if (name.equals("address")) {
					address = parser.nextText();
					LogUtil.d(TAG, "address:" + address);
				} else if (name.equals("type")) {
					type = parser.nextText();
					LogUtil.d(TAG, "type:" + type);
				} else if (name.equals("date")) {
					date = parser.nextText();
					LogUtil.d(TAG, "date:" + date);
				}
				break;
			case XmlPullParser.END_TAG:
				// 4.�Ѷ��Ų��뵽ϵͳ����Ӧ����
				if (name.equals("sms")) {
					ContentValues values = new ContentValues();
					values.put("body", body);
					values.put("address", address);
					values.put("type", type);
					values.put("date", date);
					resolver.insert(uri, values);
					// 5.���ý���������
					progressValue++;
					progress.setProgress(progressValue);
				}
			default:
				break;
			}
			eventType = parser.next();
		}

	}
}