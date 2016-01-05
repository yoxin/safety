package com.itheima.mobilesafe;

import com.itheima.mobilesafe.utils.SmsUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AtoolsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}

	/**
	 * ����¼����򿪺�������ز�ѯ����
	 * 
	 * @param view
	 */
	public void numberQuery(View view) {
		Intent intent = new Intent(this, NumberAddressActivity.class);
		startActivity(intent);
	}
	
	/**
	 * ����¼����������ű��ݹ���
	 * 
	 * @param view
	 */
	public void smsBackup(View view) {
		try {
			SmsUtils.backup(this);
			Toast.makeText(this, "���ݳɹ�", Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(this, "����ʧ��", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
	
	/**
	 * ����¼����������Ż�ԭ����
	 * 
	 * @param view
	 */
	public void smsRestore(View view) {
	}
}
