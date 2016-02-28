package com.itheima.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.itheima.mobilesafe.db.dao.AppLockDao;
import com.itheima.mobilesafe.utils.LogUtil;

public class WatchDogService extends Service {

	protected static final String TAG = "WatchDogService";
	private ActivityManager activityManager;
	private Context context;
	private AppLockDao dao;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		// 获取进程管理器
		activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		dao = new AppLockDao(context);
		// 开启看门狗
		startWatchDog();
	}

	private void startWatchDog() {
		// 由于看门狗一直运行，为了避免程序阻塞，开启线程
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					// 获取当前线程的任务栈
					List<RunningTaskInfo> tasks = activityManager
							.getRunningTasks(1);
					// 获取栈顶进程信息
					RunningTaskInfo info = tasks.get(0);
					// 获取包名
					String packageName = info.topActivity.getPackageName();

					LogUtil.d(TAG, "packageName:" + packageName);
					if (dao.find(packageName)) {
						LogUtil.d(TAG, "该程序已锁");
					} else {
						LogUtil.d(TAG, "该程序未锁");
					}
				}
			}
		}).start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
