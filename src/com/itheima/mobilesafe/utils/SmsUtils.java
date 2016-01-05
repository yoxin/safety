package com.itheima.mobilesafe.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

public class SmsUtils {

	private static final String TAG = "SmsUtils";

	/**
	 * 短信备份
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
		 * 设置进度条的最大值
		 * 
		 * @param max
		 *            最大值
		 */
		void setMax(int max);

		/**
		 * 设置进度条的进度
		 * 
		 * @param progress
		 */
		void setProgress(int progress);

	}

}