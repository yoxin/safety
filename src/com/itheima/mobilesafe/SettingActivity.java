package com.itheima.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.itheima.mobilesafe.service.AddressService;
import com.itheima.mobilesafe.ui.SettingClickView;
import com.itheima.mobilesafe.ui.SettingItemView;
import com.itheima.mobilesafe.utils.ServiceUtils;

public class SettingActivity extends Activity {
	private SettingItemView siv_update;
	private SharedPreferences sp;
	private SettingItemView siv_show_address;
	private SettingClickView scv_changebg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 设置是否自动更新
		siv_update = (SettingItemView) findViewById(R.id.siv_update);

		boolean update = sp.getBoolean("update", false);
		if (update) {
			// 自动升级已经开启
			siv_update.setChecked(true);
		} else {
			// 自动升级已经关闭
			siv_update.setChecked(false);
		}
		siv_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				// 判断是否有选中
				// 已经打开自动升级了
				if (siv_update.isChecked()) {
					siv_update.setChecked(false);
					editor.putBoolean("update", false);
				} else {
					// 没有打开自动升级
					siv_update.setChecked(true);
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
		// 设置号码归属地显示
		siv_show_address = (SettingItemView) findViewById(R.id.siv_show_address);
		final Intent intent = new Intent(this, AddressService.class);
		siv_show_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if (siv_show_address.isChecked()) {
					siv_show_address.setChecked(false);
					stopService(intent);
					editor.putBoolean("showAddress", false);
				} else {
					siv_show_address.setChecked(true);
					startService(intent);
					editor.putBoolean("showAddress", true);
				}
				editor.commit();
			}
		});
		//更改归属地的背景风格
		scv_changebg = (SettingClickView)findViewById(R.id.scv_changebg);
		final String [] items = {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
		scv_changebg.setTitle("归属地提示框风格");
		scv_changebg.setDesc(items[sp.getInt("which", 0)]);
		scv_changebg.setOnClickListener(new OnClickListener() {
			/**
			 * 弹出归属地背景风格选择对话框
			 */
			@Override
			public void onClick(View v) {
				//创建对话框
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("归属地提示框风格");
				int choose = sp.getInt("which", 0);
				builder.setSingleChoiceItems(items, choose, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Editor editor = sp.edit();
						editor.putInt("which", which);
						editor.commit();
						scv_changebg.setDesc(items[which]);
						//取消对话框
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("取消", null);
				builder.show();
			}
		});
	}

	@Override
	protected void onResume() {
		if (ServiceUtils.isServiceRunning(this,
				"com.itheima.mobilesafe.service.AddressService")) {
			siv_show_address.setChecked(true);
		} else {
			siv_show_address.setChecked(false);
		}
		super.onResume();
	}

}
