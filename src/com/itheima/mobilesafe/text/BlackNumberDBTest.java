package com.itheima.mobilesafe.text;

import android.test.AndroidTestCase;

import com.itheima.mobilesafe.db.BlackNumberDBOpenHeaper;

public class BlackNumberDBTest extends AndroidTestCase {
	public void textCreatTable() throws Exception {
		BlackNumberDBOpenHeaper heaper = new BlackNumberDBOpenHeaper(
				getContext());
		heaper.getWritableDatabase();
	}
}
