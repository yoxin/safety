package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AtoolsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}
	
	/**
	 * 点击事件，打开号码归属地查询功能
	 * @param view
	 */
	public void numberQuery(View view) {
		Intent intent = new Intent(this, NumberAddressActivity.class);
		startActivity(intent);
	}
}
