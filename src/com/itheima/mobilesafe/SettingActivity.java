package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.itheima.mobilesafe.service.AddressService;
import com.itheima.mobilesafe.ui.SettingItemView;
import com.itheima.mobilesafe.utils.ServiceUtils;

public class SettingActivity extends Activity {
	private SettingItemView siv_update;
	private SharedPreferences sp;
	private SettingItemView siv_show_address;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//设置是否自动更新
		siv_update = (SettingItemView) findViewById(R.id.siv_update);

		boolean update = sp.getBoolean("update", false);
		if(update){
			//自动升级已经开启
			siv_update.setChecked(true);
		}else{
			//自动升级已经关闭
			siv_update.setChecked(false);
		}
		siv_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				//判断是否有选中
				//已经打开自动升级了
				if(siv_update.isChecked()){
					siv_update.setChecked(false);
					editor.putBoolean("update", false);
				}else{
					//没有打开自动升级
					siv_update.setChecked(true);
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
		//设置号码归属地显示
		siv_show_address = (SettingItemView)findViewById(R.id.siv_show_address);
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
		
	}
	
	@Override
	protected void onResume() {
		if (ServiceUtils.isServiceRunning(this, "com.itheima.mobilesafe.service.AddressService")) {
			siv_show_address.setChecked(true);
		} else {
			siv_show_address.setChecked(false);
		}
		super.onResume();
	}

}
