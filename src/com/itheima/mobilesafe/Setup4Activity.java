package com.itheima.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends BaseSetupActivity {
	private CheckBox cb_protect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		cb_protect = (CheckBox) findViewById(R.id.cb_protect);
		boolean protecting = sp.getBoolean("protecting", false);
		if (protecting) {
			// �ֻ������Ѿ�������
			cb_protect.setText("�ֻ������Ѿ�����");
			cb_protect.setChecked(true);
		} else {
			// �ֻ�����û�п���
			cb_protect.setText("�ֻ�����û�п���");
			cb_protect.setChecked(false);

		}

		cb_protect.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					cb_protect.setText("���Ѿ�������������");
				} else {
					cb_protect.setText("��û�п�����������");
				}
				// ����ѡ���״̬
				Editor editor = sp.edit();
				editor.putBoolean("protecting", isChecked);
				editor.commit();
			}
		});
	}

	@Override
	public void showNext() {
		Editor editor = sp.edit();
		editor.putBoolean("configed", true);
		editor.commit();
		Intent intent = new Intent(Setup4Activity.this, LostFindActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(Setup4Activity.this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
}
