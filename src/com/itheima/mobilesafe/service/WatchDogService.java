package com.itheima.mobilesafe.service;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import com.itheima.mobilesafe.AppLockInterPwdActivity;
import com.itheima.mobilesafe.db.dao.AppLockDao;
import com.itheima.mobilesafe.utils.LogUtil;

public class WatchDogService extends Service {

	protected static final String TAG = "WatchDogService";
	private ActivityManager activityManager;
	private Context context;
	private AppLockDao dao;
	private boolean flag;

	// 临时停止保护的包名
	private String tempStopProtectPackageName;
	private WatchDogReceiver watchDogReceiver;

	// 已上锁的应用包名集合
	private List<String> packageNameInfos;

	private class WatchDogReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction()
					.equals("com.itheima.mobileguard.stopprotect")) {
				// 获取到停止保护的对象

				tempStopProtectPackageName = intent
						.getStringExtra("packageName");
			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				tempStopProtectPackageName = null;
				// 让狗休息
				flag = false;
			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				// 让狗继续干活
				if (flag == false) {
					startWatchDog();
				}
			}

		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private class AppLockContentObserver extends ContentObserver {

		public AppLockContentObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			packageNameInfos = dao.findAll();
		}

	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;

		// 注册内容提供者
		getContentResolver().registerContentObserver(
				Uri.parse("content://com.itheima.mobilesafe.applock.change"),
				true, new AppLockContentObserver(new Handler()) {
				});

		// 填充数据
		dao = new AppLockDao(context);
		packageNameInfos = dao.findAll();

		// 注册广播接收者
		watchDogReceiver = new WatchDogReceiver();
		IntentFilter filter = new IntentFilter();
		// 停止保护
		filter.addAction("com.itheima.mobileguard.stopprotect");

		// 注册一个锁屏的广播
		/**
		 * 当屏幕锁住的时候。狗就休息 屏幕解锁的时候。让狗活过来
		 */
		filter.addAction(Intent.ACTION_SCREEN_OFF);

		filter.addAction(Intent.ACTION_SCREEN_ON);

		registerReceiver(watchDogReceiver, filter);

		// 获取进程管理器
		activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		// 开启看门狗
		startWatchDog();
	}

	private void startWatchDog() {
		// 由于看门狗一直运行，为了避免程序阻塞，开启线程
		new Thread(new Runnable() {

			@Override
			public void run() {
				flag = true;
				while (flag) {
					// 获取当前线程的任务栈
					List<RunningTaskInfo> tasks = activityManager
							.getRunningTasks(1);
					// 获取栈顶进程信息
					RunningTaskInfo info = tasks.get(0);
					// 获取包名
					String packageName = info.topActivity.getPackageName();
					// 让线程休息一下
					SystemClock.sleep(50);
					LogUtil.d(TAG, "packageName:" + packageName);
					// 从数据库中查找有没有上锁
					// if (dao.find(packageName)) {
					// 优化成从内存中查找
					if (packageNameInfos.contains(packageName)) {
						// 启动的包名等于临时保护的包名时，跳过程序锁
						if (packageName.equals(tempStopProtectPackageName)) {
							// do nothing
						} else {
							Intent intent = new Intent(context,
									AppLockInterPwdActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("packageName", packageName);
							startActivity(intent);
							LogUtil.d(TAG, "该程序已锁");
						}
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
		flag = false;
		if (watchDogReceiver != null) {
			unregisterReceiver(watchDogReceiver);
			watchDogReceiver = null;
		}
	}
}
