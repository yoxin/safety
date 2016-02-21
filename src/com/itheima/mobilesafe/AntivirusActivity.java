package com.itheima.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.itheima.mobilesafe.domain.AppInfo;
import com.itheima.mobilesafe.engine.AppInfoProvider;
import com.itheima.mobilesafe.utils.MD5Utils;

public class AntivirusActivity extends Activity {

	private ImageView iv_act_scanning;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		initUI();
		fillData();
	}

	private void fillData() {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				PackageManager packageManager = getPackageManager();
				 
				List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
				
				for (PackageInfo info : installedPackages) {
					//获取当前手机上的app名字
					String appName = info.applicationInfo.loadLabel(packageManager).toString();
					//获取每个程序的目录
					String sourceDir = info.applicationInfo.sourceDir;
					MD5Utils.getFileMd5(sourceDir);
				}
			}
		}).start();
	}

	private void initUI() {
		setContentView(R.layout.activity_antivirus);
		iv_act_scanning = (ImageView) findViewById(R.id.iv_act_scanning);
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
