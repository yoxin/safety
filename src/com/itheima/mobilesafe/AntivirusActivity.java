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
	// 扫描开始
	protected static final int VIRUS_SCAN_START = 1;
	// 扫描中
	protected static final int VIRUS_SCAN_ING = 2;
	// 扫描结束
	protected static final int VIRUS_SCAN_END = 3;
	private ImageView iv_act_scanning;
	private Context context;
	private ProgressBar pb_virus;
	private LinearLayout ll_scaninfo;

	/**
	 * 处理查询结果，更新UI信息
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
					desc = "病毒";
					textView.setTextColor(Color.RED);
					textView.setText(info.getAppName() + ":" + desc);
				} else {
					desc = "安全";
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

				// 扫描开始
				msg = Message.obtain();
				msg.what = VIRUS_SCAN_START;
				int size = installedPackages.size();
				msg.obj = size;
				handler.sendMessage(msg);
				pb_virus.setMax(size);
				// 进度条进度
				int progress = 0;
				// 扫描中
				for (PackageInfo packageinfo : installedPackages) {
					// 病毒查询结果
					ScanInfo scanInfo = new ScanInfo();

					// 获取当前手机上的app名字
					String appName = packageinfo.applicationInfo.loadLabel(
							packageManager).toString();
					scanInfo.setAppName(appName);
					// 获取当前手机上的app包名
					String packageName = packageinfo.packageName;
					scanInfo.setPackageName(packageName);
					// 获取每个程序的目录
					String sourceDir = packageinfo.applicationInfo.sourceDir;
					// 获取文件的MD5
					String md5 = MD5Utils.getFileMd5(sourceDir);
					// 判断文件是否存在于病毒数据库里面
					boolean isVirus = AntivirusQueryUtils.isVirus(md5);
					scanInfo.setVirus(isVirus);
					LogUtil.d(TAG, scanInfo.toString());
					msg = Message.obtain();
					msg.what = VIRUS_SCAN_ING;
					msg.obj = scanInfo;
					handler.sendMessage(msg);

					// 更新进度条进度
					pb_virus.setProgress(++progress);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// 扫描结束
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
		 * 设置旋转动画
		 */
		RotateAnimation animation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation.setDuration(5000); // 旋转的周期时间
		animation.setRepeatCount(Animation.INFINITE); // 设置选择的次数：无限
		// 为控件添加动画
		iv_act_scanning.setAnimation(animation);
	}
}
