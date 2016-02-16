package com.itheima.mobilesafe;

import com.itheima.mobilesafe.service.AutoClearService;
import com.itheima.mobilesafe.utils.ServiceUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TaskSettingActivity extends Activity {
	private CheckBox cb_show_system_process;
	private CheckBox cb_screen_off_clear;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_setting);
		cb_show_system_process = (CheckBox) findViewById(R.id.cb_show_system_process);
		cb_screen_off_clear = (CheckBox) findViewById(R.id.cb_screen_off_clear);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 显示系统进程checkbox点击事件
		cb_show_system_process
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Editor editor = sp.edit();
						editor.putBoolean("showSystem", isChecked);
						editor.commit();
					}
				});

		// 锁屏清理进程checkbox点击事件
		cb_screen_off_clear
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Intent service = new Intent(TaskSettingActivity.this,
								AutoClearService.class);
						if (isChecked) {
							startService(service);
						} else {
							stopService(service);
						}
					}
				});
	}

	@Override
	protected void onStart() {
		cb_show_system_process.setChecked(sp.getBoolean("showSystem", false));
		Boolean isServiceRunning = ServiceUtils.isServiceRunning(
				TaskSettingActivity.this,
				"com.itheima.mobilesafe.service.AutoClearService");
		cb_screen_off_clear.setChecked(isServiceRunning);
		super.onStart();
	}
}
