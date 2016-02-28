package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AppLockInterPwdActivity extends Activity implements
		OnClickListener {

	private EditText et_pwd;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initUI();
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		context = this;
		setContentView(R.layout.activity_app_lock_inter_pwd);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		et_pwd.setInputType(InputType.TYPE_NULL);
		Button bt_0 = (Button) findViewById(R.id.bt_0);
		Button bt_1 = (Button) findViewById(R.id.bt_1);
		Button bt_2 = (Button) findViewById(R.id.bt_2);
		Button bt_3 = (Button) findViewById(R.id.bt_3);
		Button bt_4 = (Button) findViewById(R.id.bt_4);
		Button bt_5 = (Button) findViewById(R.id.bt_5);
		Button bt_6 = (Button) findViewById(R.id.bt_6);
		Button bt_7 = (Button) findViewById(R.id.bt_7);
		Button bt_8 = (Button) findViewById(R.id.bt_8);
		Button bt_9 = (Button) findViewById(R.id.bt_9);
		Button bt_clean = (Button) findViewById(R.id.bt_clean);
		Button bt_delete = (Button) findViewById(R.id.bt_delete);
		Button bt_ok = (Button) findViewById(R.id.bt_ok);
		bt_0.setOnClickListener(this);
		bt_1.setOnClickListener(this);
		bt_2.setOnClickListener(this);
		bt_3.setOnClickListener(this);
		bt_4.setOnClickListener(this);
		bt_5.setOnClickListener(this);
		bt_6.setOnClickListener(this);
		bt_7.setOnClickListener(this);
		bt_8.setOnClickListener(this);
		bt_9.setOnClickListener(this);
		bt_clean.setOnClickListener(this);
		bt_delete.setOnClickListener(this);
		bt_ok.setOnClickListener(this);
	}

	/**
	 * 点击事件
	 * 
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		String pwd = et_pwd.getText().toString();
		switch (v.getId()) {
		case R.id.bt_clean:
			et_pwd.setText("");
			break;
		case R.id.bt_delete:
			if (pwd.isEmpty()) {
				return;
			}
			et_pwd.setText(pwd.substring(0, pwd.length() - 1));
			break;
		case R.id.bt_ok:
			if ("123".equals(pwd)) {
				Intent intent = getIntent();
				String packageName = intent.getStringExtra("packageName");
				intent = new Intent();
				intent.putExtra("packageName", packageName);
				intent.setAction("com.itheima.mobileguard.stopprotect");
				sendBroadcast(intent);
				finish();
			} else {
				Toast.makeText(context, "密码错误", Toast.LENGTH_LONG).show();
			}
			break;

		default:
			Button bt_v = (Button) v;
			et_pwd.setText(pwd + bt_v.getText().toString());
			break;
		}
	}

}
