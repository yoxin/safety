package com.itheima.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources.Theme;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itheima.mobilesafe.db.dao.AntivirusQueryUtils;
import com.itheima.mobilesafe.domain.ScanInfo;
import com.itheima.mobilesafe.utils.LogUtil;
import com.itheima.mobilesafe.utils.MD5Utils;

public class AntivirusActivity extends Activity {

	protected static final String TAG = "AntivirusActivity";
	// ɨ�迪ʼ
	protected static final int VIRUS_SCAN_START = 1;
	// ɨ����
	protected static final int VIRUS_SCAN_ING = 2;
	// ɨ�����
	protected static final int VIRUS_SCAN_END = 3;
	private ImageView iv_act_scanning;
	private Context context;
	private ProgressBar pb_virus;
	private LinearLayout ll_scaninfo;

	/**
	 * �����ѯ���������UI��Ϣ
	 */
	Handler handler = new Handler() {

		private int size;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case VIRUS_SCAN_START:
				size = (Integer) msg.obj;
				break;
			case VIRUS_SCAN_ING:
				TextView textView = new TextView(context);
				ScanInfo info = (ScanInfo) msg.obj;
				String desc;
				if (info.isVirus()) {
					desc = "����";
					textView.setTextColor(Color.RED);
					textView.setText(info.getAppName() + ":" + desc);
				} else {
					desc = "��ȫ";
					textView.setTextColor(Color.BLUE);
					textView.setText(info.getAppName() + ":" + desc);
				}
				ll_scaninfo.addView(textView, 0);
				break;
			case VIRUS_SCAN_END:
				iv_act_scanning.clearAnimation();
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		initUI();
		fillData();
	}

	private void fillData() {

		new Thread(new Runnable() {

			private Message msg;

			@Override
			public void run() {
				PackageManager packageManager = getPackageManager();

				List<PackageInfo> installedPackages = packageManager
						.getInstalledPackages(0);

				// ɨ�迪ʼ
				msg = Message.obtain();
				msg.what = VIRUS_SCAN_START;
				int size = installedPackages.size();
				msg.obj = size;
				handler.sendMessage(msg);
				pb_virus.setMax(size);
				// ����������
				int progress = 0;
				// ɨ����
				for (PackageInfo packageinfo : installedPackages) {
					// ������ѯ���
					ScanInfo scanInfo = new ScanInfo();

					// ��ȡ��ǰ�ֻ��ϵ�app����
					String appName = packageinfo.applicationInfo.loadLabel(
							packageManager).toString();
					scanInfo.setAppName(appName);
					// ��ȡ��ǰ�ֻ��ϵ�app����
					String packageName = packageinfo.packageName;
					scanInfo.setPackageName(packageName);
					// ��ȡÿ�������Ŀ¼
					String sourceDir = packageinfo.applicationInfo.sourceDir;
					// ��ȡ�ļ���MD5
					String md5 = MD5Utils.getFileMd5(sourceDir);
					// �ж��ļ��Ƿ�����ڲ������ݿ�����
					boolean isVirus = AntivirusQueryUtils.isVirus(md5);
					scanInfo.setVirus(isVirus);
					LogUtil.d(TAG, scanInfo.toString());
					msg = Message.obtain();
					msg.what = VIRUS_SCAN_ING;
					msg.obj = scanInfo;
					handler.sendMessage(msg);

					// ���½���������
					pb_virus.setProgress(++progress);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// ɨ�����
				msg = Message.obtain();
				msg.what = VIRUS_SCAN_END;
				handler.sendMessage(msg);
			}
		}).start();
	}

	private void initUI() {
		setContentView(R.layout.activity_antivirus);
		iv_act_scanning = (ImageView) findViewById(R.id.iv_act_scanning);
		ll_scaninfo = (LinearLayout) findViewById(R.id.ll_scaninfo);
		pb_virus = (ProgressBar) findViewById(R.id.pb_virus);
		/**
		 * ������ת����
		 */
		RotateAnimation animation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation.setDuration(5000); // ��ת������ʱ��
		animation.setRepeatCount(Animation.INFINITE); // ����ѡ��Ĵ���������
		// Ϊ�ؼ���Ӷ���
		iv_act_scanning.setAnimation(animation);
	}
}
