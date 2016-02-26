package com.itheima.mobilesafe.text;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.test.AndroidTestCase;

import com.itheima.mobilesafe.db.BlackNumberOpenHeaper;
import com.itheima.mobilesafe.db.dao.BlackNumberDao;
import com.itheima.mobilesafe.domain.BlackNumberInfo;
import com.itheima.mobilesafe.utils.LogUtil;

public class BlackNumberDBTest extends AndroidTestCase {
	private static final String TAG = "BlackNumberDBTest";
	public void textCreatTable() throws Exception {
		BlackNumberOpenHeaper heaper = new BlackNumberOpenHeaper(
				getContext());
		heaper.getWritableDatabase();
	}
	public void textFind() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		boolean actual = dao.find("100");
		assertEquals(true, actual);
	}
	public void textFindAll() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		List<BlackNumberInfo> list = dao.findAll();
		Iterator<BlackNumberInfo> iterator = list.iterator();
		while (iterator.hasNext()) {
			BlackNumberInfo blackNumberInfo = (BlackNumberInfo) iterator.next();
			LogUtil.d(TAG, blackNumberInfo.getNumber());
		}
	}
	public void textAdd() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.add("100", "1");
	}
	public void textDelete() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.delete("100");
	}
	public void textUpdate() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.update("100", "2");
	}
	public void textAddAll() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		long number = 18819451000l;
		Random random = new Random(47);
		for (int i = 0; i < 100; i++) {
			dao.add(String.valueOf(number+i), String.valueOf(random.nextInt(3)+1));
		}
	}
	public void textDeleteAll() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.deleteAll();
	}
}
