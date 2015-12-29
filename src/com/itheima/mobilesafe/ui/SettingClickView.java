package com.itheima.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe.R;

/**
 * �����Զ������Ͽؼ���������������TextView ������һ��CheckBox,����һ��View
 * 
 * @author Administrator
 * 
 */
public class SettingClickView extends RelativeLayout {

	private TextView tv_desc;
	private TextView tv_title;
	private String title;

	/**
	 * ��ʼ�������ļ�
	 * 
	 * @param context
	 */
	private void iniView(Context context) {

		// ��һ�������ļ�---��View ���Ҽ�����SettingItemView
		View.inflate(context, R.layout.setting_click_view, this);
		tv_desc = (TextView) this.findViewById(R.id.tv_desc);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		tv_title.setText(title);
	}

	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		iniView(context);
	}

	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		iniView(context);
	}

	public SettingClickView(Context context) {
		super(context);
		iniView(context);
	}


	/**
	 * ���� ��Ͽؼ���������Ϣ
	 */

	public void setDesc(String text) {
		tv_desc.setText(text);
	}

	/**
	 * ���� ��Ͽؼ��ı�����Ϣ
	 * @param text
	 */
	public void setTitle(String text) {
		tv_title.setText(text);
	}
}
