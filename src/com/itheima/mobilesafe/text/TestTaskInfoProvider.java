package com.itheima.mobilesafe.text;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import com.itheima.mobilesafe.domain.TaskInfo;
import com.itheima.mobilesafe.engine.TaskInfoProvider;
import com.itheima.mobilesafe.utils.LogUtil;

public class TestTaskInfoProvider extends AndroidTestCase {
	private static final String TAG = "TestTaskInfoProvider";

	public void testGetTaskprovider() {
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		taskInfos = TaskInfoProvider.getTaskprovider(getContext());
		for (TaskInfo taskInfo : taskInfos) {
			LogUtil.d(TAG, taskInfo.toString());
		}
	}
}
