package com.itheima.mobilesafe;

import com.itheima.mobilesafe.ui.SettingItemView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class Setup2Activity extends BaseSetupActivity {

	private SettingItemView siv_setup2_sim;
	private TelephonyManager telephonyManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		telephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		siv_setup2_sim = (SettingItemView) findViewById(R.id.siv_setup2_sim);
		String sim = sp.getString("sim", null);
		siv_setup2_sim.setChecked(!TextUtils.isEmpty(sim));
		siv_setup2_sim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if (siv_setup2_sim.isChecked()) {
					siv_setup2_sim.setChecked(false);
					editor.putString("sim", null);
				} else {
					siv_setup2_sim.setChecked(true);
					// 保存SIM卡的序列号
					String sim = telephonyManager.getSimSerialNumber();
					editor.putString("sim", sim);
				}
				editor.commit();
			}
		});
	}

	@Override
	public void showNext() {
		//取出SIM卡序列号
		String sim = sp.getString("sim", null);
		if (TextUtils.isEmpty(sim)) {
			Toast.makeText(this, "没有绑定SIM卡", Toast.LENGTH_LONG).show();
			return;
		}
		Intent intent = new Intent(Setup2Activity.this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showPre() { 
		Intent intent = new Intent(Setup2Activity.this, Setup1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
}
