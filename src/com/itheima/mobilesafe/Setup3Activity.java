package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends BaseSetupActivity {
	private EditText et_setup3_phone;
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		context = this;
		et_setup3_phone = (EditText)findViewById(R.id.et_setup3_phone);
		et_setup3_phone.setText(sp.getString("safeNumber", ""));
		
	}
	
	@Override
	public void showNext() {
		//保存安全号码
		String phone = et_setup3_phone.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(context, "没有设置安全号码", Toast.LENGTH_LONG).show();
			return;
		}
		Editor editor = sp.edit();
		editor.putString("safeNumber", phone);
		editor.commit();
		Intent intent = new Intent(Setup3Activity.this, Setup4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(Setup3Activity.this, Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	
	/**
	 * 选择联系人的点击事件
	 * @param view
	 */
	public void selectContact(View view) {
		Intent intent = new Intent(this, SelectContactActivity.class);
		startActivityForResult(intent, 0);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null) {
			return;
		}
		String phone = data.getStringExtra("phone").replace("-", "");
		et_setup3_phone.setText(phone);
	}
}
