package com.itheima.mobilesafe;

import java.io.FileNotFoundException;

import com.itheima.mobilesafe.utils.LogUtil;
import com.itheima.mobilesafe.utils.SmsUtils;
import com.itheima.mobilesafe.utils.SmsUtils.ProgressCallBack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AtoolsActivity extends Activity {

	protected static final String TAG = "AtoolsActivity";
	private ProgressDialog pd_Backup;// ���ű��ݽ�����
	private ProgressDialog pd_Restore;// ���Ż�ԭ������
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		context = this;
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
		pd_Backup = new ProgressDialog(context);
		pd_Backup.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd_Backup.setMessage("���ű���");
		pd_Backup.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					SmsUtils.backup(context, new ProgressCallBack() {

						@Override
						public void setProgress(int progress) {
							pd_Backup.setProgress(progress);
						}

						@Override
						public void setMax(int max) {
							pd_Backup.setMax(max);
						}
					});
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(context, "���ݳɹ�", Toast.LENGTH_LONG)
									.show();
						}
					});
				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(context, "����ʧ��", Toast.LENGTH_LONG)
									.show();
						}
					});
					e.printStackTrace();
				} finally {
					LogUtil.d(TAG, "dismiss");
					pd_Backup.dismiss();
				}
			}
		}).start();

	}

	/**
	 * ����¼����������Ż�ԭ����
	 * 
	 * @param view
	 */
	public void smsRestore(View view) {
		pd_Restore = new ProgressDialog(context);
		pd_Restore.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd_Restore.setMessage("���Ż�ԭ");
		pd_Restore.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					SmsUtils.restore(context, true, new ProgressCallBack() {

						@Override
						public void setProgress(int progress) {
							pd_Restore.setProgress(progress);
						}

						@Override
						public void setMax(int max) {
							pd_Restore.setMax(max);
						}
					});
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(context, "��ԭ�ɹ�", Toast.LENGTH_LONG)
									.show();
						}
					});
				} catch (FileNotFoundException fnfe) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(context, "�Ҳ������ű��ݣ����ȱ���",
									Toast.LENGTH_LONG).show();
						}
					});
					fnfe.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					pd_Restore.dismiss();
				}

			}
		}).start();

	}

	/**
	 * ����¼���������
	 * @param view
	 */
	public void appLock(View view) {
		Intent intent = new Intent(AtoolsActivity.this, AppLockActivity.class);
		startActivity(intent);
	}
}
