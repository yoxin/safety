package com.itheima.mobilesafe.receiver;

import com.itheima.mobilesafe.service.UpdateWidgetService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;

public class MyWidget extends AppWidgetProvider {

	/**
	 * widget���κζ���������ø÷���
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, UpdateWidgetService.class);
		context.startService(i);
		super.onReceive(context, intent);
	}

	/**
	 * xml�������ļ����õĸ���ʱ�䵽�˾͵���
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	/**
	 * Ϊ������ӵ�һ��widgetʱ������
	 */
	@Override
	public void onEnabled(Context context) {
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
		super.onEnabled(context);
	}

	/**
	 * ɾ�����һ����widgetʱ������
	 */
	@Override
	public void onDisabled(Context context) {
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.stopService(intent);
		super.onDisabled(context);
	}

}
