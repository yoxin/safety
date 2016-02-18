package com.itheima.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.receiver.MyWidget;
import com.itheima.mobilesafe.utils.SystemUtils;

public class UpdateWidgetService extends Service {

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
				views.setTextViewText(R.id.process_count, "�������еĽ��̣�"
						+ SystemUtils.getProcessCount(getApplicationContext())
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
				PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				awm.updateAppWidget(provider, views);
			}
		};
		// ִ��task�Ķ�������һ���ӳ�0�������������3000��������һ��
		timer.schedule(task, 0, 3000);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		timer.cancel();
		task.cancel();
		timer = null;
		task = null;
		super.onDestroy();
	}

}
