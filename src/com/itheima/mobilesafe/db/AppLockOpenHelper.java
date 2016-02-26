package com.itheima.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AppLockOpenHelper extends SQLiteOpenHelper {

	public AppLockOpenHelper(Context context) {
		super(context, "applock.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE applock (" +
				"_id integer primary key AUTOINCREMENT," +
				"packagename varchar(20));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
