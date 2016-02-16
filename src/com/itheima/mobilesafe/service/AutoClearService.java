package com.itheima.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class AutoClearService extends Service {
	
	ActivityManager am;
	ScreenOffReceiver receiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		receiver = new ScreenOffReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		if (receiver!=null) {
			unregisterReceiver(receiver);
		}
		receiver = null;
		super.onDestroy();
	}
	
	/**
	 * 屏幕锁屏广播接收者
	 * @author YOXIN
	 *
	 */
	class ScreenOffReceiver extends BroadcastReceiver {

		/**
		 * 清理进程
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
			for (RunningAppProcessInfo info : infos) {
				am.killBackgroundProcesses(info.processName);
			}
		}
	}
}
