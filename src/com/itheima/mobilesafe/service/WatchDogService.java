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
		// ��ȡ���̹�����
		activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		dao = new AppLockDao(context);
		// �������Ź�
		startWatchDog();
	}

	private void startWatchDog() {
		// ���ڿ��Ź�һֱ���У�Ϊ�˱�����������������߳�
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					// ��ȡ��ǰ�̵߳�����ջ
					List<RunningTaskInfo> tasks = activityManager
							.getRunningTasks(1);
					// ��ȡջ��������Ϣ
					RunningTaskInfo info = tasks.get(0);
					// ��ȡ����
					String packageName = info.topActivity.getPackageName();

					LogUtil.d(TAG, "packageName:" + packageName);
					if (dao.find(packageName)) {
						LogUtil.d(TAG, "�ó�������");
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
	}
}
