package com.itheima.mobilesafe.receiver;

import com.itheima.mobilesafe.service.UpdateWidgetService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;

public class MyWidget extends AppWidgetProvider {

	/**
	 * widget有任何动作都会调用该方法
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, UpdateWidgetService.class);
		context.startService(i);
		super.onReceive(context, intent);
	}

	/**
	 * xml中配置文件设置的更新时间到了就调用
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	/**
	 * 为桌面添加第一个widget时，调用
	 */
	@Override
	public void onEnabled(Context context) {
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
		super.onEnabled(context);
	}

	/**
	 * 删除最后一个该widget时，调用
	 */
	@Override
	public void onDisabled(Context context) {
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.stopService(intent);
		super.onDisabled(context);
	}

}
