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

	// ��ʱֹͣ�����İ���
	private String tempStopProtectPackageName;
	private WatchDogReceiver watchDogReceiver;

	// ��������Ӧ�ð�������
	private List<String> packageNameInfos;

	private class WatchDogReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction()
					.equals("com.itheima.mobileguard.stopprotect")) {
				// ��ȡ��ֹͣ�����Ķ���

				tempStopProtectPackageName = intent
						.getStringExtra("packageName");
			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				tempStopProtectPackageName = null;
				// �ù���Ϣ
				flag = false;
			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				// �ù������ɻ�
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

		// ע�������ṩ��
		getContentResolver().registerContentObserver(
				Uri.parse("content://com.itheima.mobilesafe.applock.change"),
				true, new AppLockContentObserver(new Handler()) {
				});

		// �������
		dao = new AppLockDao(context);
		packageNameInfos = dao.findAll();

		// ע��㲥������
		watchDogReceiver = new WatchDogReceiver();
		IntentFilter filter = new IntentFilter();
		// ֹͣ����
		filter.addAction("com.itheima.mobileguard.stopprotect");

		// ע��һ�������Ĺ㲥
		/**
		 * ����Ļ��ס��ʱ�򡣹�����Ϣ ��Ļ������ʱ���ù������
		 */
		filter.addAction(Intent.ACTION_SCREEN_OFF);

		filter.addAction(Intent.ACTION_SCREEN_ON);

		registerReceiver(watchDogReceiver, filter);

		// ��ȡ���̹�����
		activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		// �������Ź�
		startWatchDog();
	}

	private void startWatchDog() {
		// ���ڿ��Ź�һֱ���У�Ϊ�˱�����������������߳�
		new Thread(new Runnable() {

			@Override
			public void run() {
				flag = true;
				while (flag) {
					// ��ȡ��ǰ�̵߳�����ջ
					List<RunningTaskInfo> tasks = activityManager
							.getRunningTasks(1);
					// ��ȡջ��������Ϣ
					RunningTaskInfo info = tasks.get(0);
					// ��ȡ����
					String packageName = info.topActivity.getPackageName();
					// ���߳���Ϣһ��
					SystemClock.sleep(50);
					LogUtil.d(TAG, "packageName:" + packageName);
					// �����ݿ��в�����û������
					// if (dao.find(packageName)) {
					// �Ż��ɴ��ڴ��в���
					if (packageNameInfos.contains(packageName)) {
						// �����İ���������ʱ�����İ���ʱ������������
						if (packageName.equals(tempStopProtectPackageName)) {
							// do nothing
						} else {
							Intent intent = new Intent(context,
									AppLockInterPwdActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("packageName", packageName);
							startActivity(intent);
							LogUtil.d(TAG, "�ó�������");
						}
					} else {
						LogUtil.d(TAG, "�ó���δ��");
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
