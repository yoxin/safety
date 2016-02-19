package com.itheima.mobilesafe.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.receiver.MyWidget;
import com.itheima.mobilesafe.utils.LogUtil;
import com.itheima.mobilesafe.utils.SystemUtils;

public class UpdateWidgetService extends Service {

	public static final String TAG = "UpdateWidgetService";
	private ScreenOnReceiver onReceiver;
	private ScreenOffReceiver offReceiver;

	/**
	 * ��ʱ��
	 */
	private Timer timer;
	private TimerTask task;
	// widget������
	private AppWidgetManager awm;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		awm = AppWidgetManager.getInstance(this);
		// ע��㲥������
		onReceiver = new ScreenOnReceiver();
		offReceiver = new ScreenOffReceiver();
		IntentFilter onIntent = new IntentFilter(Intent.ACTION_SCREEN_ON);
		IntentFilter offIntent = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(onReceiver, onIntent);
		registerReceiver(offReceiver, offIntent);
		startTimer(); // ������ʱ��
		super.onCreate();
	}

	/**
	 * ������ʱ��
	 */
	private void startTimer() {
		if (timer == null || task == null) {
			timer = new Timer();
			task = new TimerTask() {

				@Override
				public void run() {
					// ����Widget�� ԭ���ǲ��÷���API
					// ���ø��µ����
					ComponentName provider = new ComponentName(
							UpdateWidgetService.this, MyWidget.class);
					RemoteViews views = new RemoteViews(getPackageName(),
							R.layout.process_widget);
					views.setTextViewText(
							R.id.process_count,
							"�������еĽ��̣�"
									+ SystemUtils
											.getProcessCount(getApplicationContext())
									+ "��");
					long availMem = SystemUtils
							.getAvailMem(getApplicationContext());
					views.setTextViewText(
							R.id.process_memory,
							"�����ڴ棺"
									+ Formatter.formatFileSize(
											getApplicationContext(), availMem));
					// Ϊ��ť�ĵ���¼�����һ������������һ���㲥��֪ͨӦ�ó��������ڴ�
					Intent intent = new Intent();
					intent.setAction("com.itheima.mobilesafe.killall");
					PendingIntent pendingIntent = PendingIntent.getBroadcast(
							getApplicationContext(), 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
					awm.updateAppWidget(provider, views);
				}
			};
			// ִ��task�Ķ�������һ���ӳ�0�������������3000��������һ��
			timer.schedule(task, 0, 3000);
		}
	}

	@Override
	public void onDestroy() {
		if (offReceiver != null) {
			unregisterReceiver(offReceiver);
		}
		if (onReceiver != null) {
			unregisterReceiver(onReceiver);
		}
		offReceiver = null;
		onReceiver = null;
		stopTimer();// ��ͣ��ʱ��
		super.onDestroy();
	}

	/**
	 * ��ͣ��ʱ��
	 */
	private void stopTimer() {
		if (timer != null) {
			timer.cancel();
		}
		if (task != null) {
			task.cancel();
		}
		timer = null;
		task = null;
	}

	/**
	 * ��Ļ�����㲥������
	 * 
	 * @author YOXIN
	 * 
	 */
	class ScreenOffReceiver extends BroadcastReceiver {

		/**
		 * �رշ���
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.d(TAG, "����");
			stopTimer(); // �رռ�ʱ��
		}
	}

	/**
	 * ��Ļ������Ļ�㲥������
	 * 
	 * @author YOXIN
	 * 
	 */
	class ScreenOnReceiver extends BroadcastReceiver {

		/**
		 * ��������
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.d(TAG, "������Ļ");
			startTimer(); // ������ʱ��
		}
	}

}
