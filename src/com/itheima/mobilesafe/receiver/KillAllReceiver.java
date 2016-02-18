package com.itheima.mobilesafe.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KillAllReceiver extends BroadcastReceiver {

	/**
	 * ���ܹ㲥��ɱ�����н���
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appInfos = am.getRunningAppProcesses();
		for (RunningAppProcessInfo info : appInfos) {
			am.killBackgroundProcesses(info.processName);
		}
	}

	
}
