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
		// �����Ƿ��Զ�����
		siv_update = (SettingItemView) findViewById(R.id.siv_update);

		boolean update = sp.getBoolean("update", false);
		if (update) {
			// �Զ������Ѿ�����
			siv_update.setChecked(true);
		} else {
			// �Զ������Ѿ��ر�
			siv_update.setChecked(false);
		}
		siv_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				// �ж��Ƿ���ѡ��
				// �Ѿ����Զ�������
				if (siv_update.isChecked()) {
					siv_update.setChecked(false);
					editor.putBoolean("update", false);
				} else {
					// û�д��Զ�����
					siv_update.setChecked(true);
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
		// ���ú����������ʾ
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
		//���Ĺ����صı������
		scv_changebg = (SettingClickView)findViewById(R.id.scv_changebg);
		final String [] items = {"��͸��","������","��ʿ��","������","ƻ����"};
		scv_changebg.setTitle("��������ʾ����");
		scv_changebg.setDesc(items[sp.getInt("which", 0)]);
		scv_changebg.setOnClickListener(new OnClickListener() {
			/**
			 * ���������ر������ѡ��Ի���
			 */
			@Override
			public void onClick(View v) {
				//�����Ի���
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("��������ʾ����");
				int choose = sp.getInt("which", 0);
				builder.setSingleChoiceItems(items, choose, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Editor editor = sp.edit();
						editor.putInt("which", which);
						editor.commit();
						scv_changebg.setDesc(items[which]);
						//ȡ���Ի���
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("ȡ��", null);
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
