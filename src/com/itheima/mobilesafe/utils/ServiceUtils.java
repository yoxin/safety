package com.itheima.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtils {
	/**
	 * ��ѯ�����Ƿ���
	 * 
	 * @param context
	 * @param serviceName
	 *            Ҫ��ѯ�ķ������ƣ������������
	 * @return
	 */
	public static Boolean isServiceRunning(Context context, String serviceName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = am.getRunningServices(100);
		for (RunningServiceInfo info : infos) {
			if (info.service.getClassName().equals(serviceName)) {
				return true;
			}
		}
		return false;
	}
}
