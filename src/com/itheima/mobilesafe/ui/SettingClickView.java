package com.itheima.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe.R;

/**
 * 我们自定义的组合控件，它里面有两个TextView ，还有一个CheckBox,还有一个View
 * 
 * @author Administrator
 * 
 */
public class SettingClickView extends RelativeLayout {

	private TextView tv_desc;
	private TextView tv_title;
	private String title;

	/**
	 * 初始化布局文件
	 * 
	 * @param context
	 */
	private void iniView(Context context) {

		// 把一个布局文件---》View 并且加载在SettingItemView
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
	 * 设置 组合控件的描述信息
	 */

	public void setDesc(String text) {
		tv_desc.setText(text);
	}

	/**
	 * 设置 组合控件的标题信息
	 * @param text
	 */
	public void setTitle(String text) {
		tv_title.setText(text);
	}
}
