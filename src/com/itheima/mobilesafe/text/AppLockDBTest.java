package com.itheima.mobilesafe.text;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.test.AndroidTestCase;

import com.itheima.mobilesafe.db.BlackNumberOpenHeaper;
import com.itheima.mobilesafe.db.dao.AppLockDao;
import com.itheima.mobilesafe.db.dao.BlackNumberDao;
import com.itheima.mobilesafe.domain.BlackNumberInfo;
import com.itheima.mobilesafe.utils.LogUtil;

public class AppLockDBTest extends AndroidTestCase {
	private static final String TAG = "AppLockDBTest";
	public void textCreatTable() throws Exception {
		BlackNumberOpenHeaper heaper = new BlackNumberOpenHeaper(
				getContext());
		heaper.getWritableDatabase();
	}
	public void textFind() throws Exception {
		AppLockDao dao = new AppLockDao(getContext());
		boolean actual = dao.find("100");
		assertEquals(true, actual);
	}
}
