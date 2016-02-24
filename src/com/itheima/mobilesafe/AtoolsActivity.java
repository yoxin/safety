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
	private ProgressDialog pd_Backup;// 短信备份进度条
	private ProgressDialog pd_Restore;// 短信还原进度条
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		context = this;
	}

	/**
	 * 点击事件，打开号码归属地查询功能
	 * 
	 * @param view
	 */
	public void numberQuery(View view) {
		Intent intent = new Intent(this, NumberAddressActivity.class);
		startActivity(intent);
	}

	/**
	 * 点击事件，启动短信备份功能
	 * 
	 * @param view
	 */
	public void smsBackup(View view) {
		pd_Backup = new ProgressDialog(context);
		pd_Backup.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd_Backup.setMessage("短信备份");
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
							Toast.makeText(context, "备份成功", Toast.LENGTH_LONG)
									.show();
						}
					});
				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(context, "备份失败", Toast.LENGTH_LONG)
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
	 * 点击事件，启动短信还原功能
	 * 
	 * @param view
	 */
	public void smsRestore(View view) {
		pd_Restore = new ProgressDialog(context);
		pd_Restore.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd_Restore.setMessage("短信还原");
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
							Toast.makeText(context, "还原成功", Toast.LENGTH_LONG)
									.show();
						}
					});
				} catch (FileNotFoundException fnfe) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(context, "找不到短信备份，请先备份",
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
	 * 点击事件，程序锁
	 * @param view
	 */
	public void appLock(View view) {
		Intent intent = new Intent(AtoolsActivity.this, AppLockActivity.class);
		startActivity(intent);
	}
}
