package com.itheima.mobilesafe;

import com.itheima.mobilesafe.utils.LogUtil;
import com.itheima.mobilesafe.utils.MD5Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	protected static final String TAG = "HomeActivity";
	private SharedPreferences sp;
	private GridView list_home;
	private MyAdapter adapter;
	private static String[] names = { "�ֻ�����", "ͨѶ��ʿ", "�������", "���̹���", "����ͳ��",
			"�ֻ�ɱ��", "��������", "�߼�����", "��������"

	};

	private static int[] ids = { R.drawable.safe, R.drawable.callmsgsafe,
			R.drawable.app, R.drawable.taskmanager, R.drawable.netmanager,
			R.drawable.trojan, R.drawable.sysoptimize, R.drawable.atools,
			R.drawable.settings

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		list_home = (GridView) findViewById(R.id.list_home);
		adapter = new MyAdapter();
		list_home.setAdapter(adapter);
		list_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent;
				switch (position) {
				case 0:
					showLostFindDialog();// �����ֻ������ĶԻ���
					break;
				case 1://����ͨѶ��ʿ
					intent = new Intent(HomeActivity.this,
							BlackNumberActivity.class);
					startActivity(intent);
					break;
				case 2://�����������
					intent = new Intent(HomeActivity.this,
							AppManagerActivity.class);
					startActivity(intent);
					break;
				case 3://������̹���
					intent = new Intent(HomeActivity.this,
							TaskManagerActivity.class);
					startActivity(intent);
					break;
				case 7:// ����߼�����
					intent = new Intent(HomeActivity.this,
							AtoolsActivity.class);
					startActivity(intent);
					break;
				case 8:// ������������
					intent = new Intent(HomeActivity.this,
							SettingActivity.class);
					startActivity(intent);
					break;
				}

			}

		});
	}

	/**
	 * �����ֻ������ĶԻ���
	 */
	private void showLostFindDialog() {
		if (isSetupPwd()) {
			showEnterDialog();// ��ʾ������������ֻ�����ҳ��ĶԻ���
		} else {
			showSetupPwdDialog();// ��ʾ���õ�����Ի���
		}
	}

	private EditText edt_setup_pwd;
	private EditText edt_setup_confirm;
	private Button btn_ok;
	private Button btn_cancel;
	AlertDialog alertDialog = null;

	/**
	 * ������������ֻ�����ҳ��Ի���
	 */
	private void showEnterDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		View view = View.inflate(HomeActivity.this,
				R.layout.dialog_inter_password, null);
		edt_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		btn_ok = (Button) view.findViewById(R.id.ok);
		btn_cancel = (Button) view.findViewById(R.id.cancel);
		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String pwd = edt_setup_pwd.getText().toString().trim();
				String savePassword = sp.getString("password", "");
				if (MD5Utils.md5Password(pwd).equals(savePassword)) {
					LogUtil.d(TAG, "������ȷ���������ҳ��");
					alertDialog.dismiss();
					Intent intent = new Intent(HomeActivity.this,
							LostFindActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(HomeActivity.this, "�������", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		builder.setView(view);
		alertDialog = builder.create();
		alertDialog.show();
	}

	/**
	 * ��������
	 */
	private void showSetupPwdDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		View view = View.inflate(HomeActivity.this,
				R.layout.dialog_setup_password, null);
		edt_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		edt_setup_confirm = (EditText) view
				.findViewById(R.id.et_setup_pwd_confirm);
		btn_ok = (Button) view.findViewById(R.id.ok);
		btn_cancel = (Button) view.findViewById(R.id.cancel);
		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String pwd = edt_setup_pwd.getText().toString().trim();
				String confirm = edt_setup_confirm.getText().toString().trim();
				if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(confirm)) {
					Toast.makeText(HomeActivity.this, "����Ϊ��", Toast.LENGTH_LONG)
							.show();
				} else if (!pwd.equals(confirm)) {
					Toast.makeText(HomeActivity.this, "���벻һ��",
							Toast.LENGTH_LONG).show();
				} else {
					Editor editor = sp.edit();
					editor.putString("password", MD5Utils.md5Password(pwd));
					editor.commit();
					alertDialog.dismiss();
					LogUtil.d(TAG, "����������ã�����������ҳ��");
					Intent intent = new Intent(HomeActivity.this,
							Setup1Activity.class);
					startActivity(intent);
				}
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		builder.setView(view);
		alertDialog = builder.create();
		alertDialog.show();
	}

	/**
	 * �Ƿ����ù�����
	 * 
	 * @return
	 */
	private boolean isSetupPwd() {
		String pwd = sp.getString("password", "");
		return !TextUtils.isEmpty(pwd);
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return names.length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = View.inflate(HomeActivity.this,
					R.layout.list_item_home, null);
			ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
			TextView tv_item = (TextView) view.findViewById(R.id.tv_item);

			tv_item.setText(names[position]);
			iv_item.setImageResource(ids[position]);
			return view;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

}
