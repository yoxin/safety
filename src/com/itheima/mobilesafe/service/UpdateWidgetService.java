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
	 * 计时器
	 */
	private Timer timer;
	private TimerTask task;
	// widget管理器
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
				// 更新Widget， 原理是采用反射API
				// 设置更新的组件
				ComponentName provider = new ComponentName(
						UpdateWidgetService.this, MyWidget.class);
				RemoteViews views = new RemoteViews(getPackageName(),
						R.layout.process_widget);
				views.setTextViewText(R.id.process_count, "正在运行的进程："
						+ SystemUtils.getProcessCount(getApplicationContext())
						+ "个");
				long availMem = SystemUtils
						.getAvailMem(getApplicationContext());
				views.setTextViewText(
						R.id.process_memory,
						"可用内存："
								+ Formatter.formatFileSize(
										getApplicationContext(), availMem));
				// 为按钮的点击事件设置一个动作：发送一个广播，通知应用程序清理内存
				Intent intent = new Intent();
				intent.setAction("com.itheima.mobilesafe.killall");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				awm.updateAppWidget(provider, views);
			}
		};
		// 执行task的动作，第一次延迟0毫秒启动，间隔3000毫秒启动一次
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
