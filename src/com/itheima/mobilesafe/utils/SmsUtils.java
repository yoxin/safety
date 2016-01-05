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
	
	/**
	 * ¶ÌÐÅ±¸·Ý
	 * @param context
	 * @throws Exception 
	 */
	public static void  backup(Context context) throws Exception {
		ContentResolver resolver = context.getContentResolver();
		File file = new File(Environment.getExternalStorageDirectory(), "smsBackup.xml"); 	
		FileOutputStream fos = new FileOutputStream(file);
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(fos, "utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[]{"body", "address", "type", "date"}, null, null, null);
		while (cursor.moveToNext()) {
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
		}
		cursor.close();
		serializer.endTag(null, "smss");
		serializer.endDocument();
		fos.close();
	}
}
