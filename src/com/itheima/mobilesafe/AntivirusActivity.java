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
					//��ȡ��ǰ�ֻ��ϵ�app����
					String appName = info.applicationInfo.loadLabel(packageManager).toString();
					//��ȡÿ�������Ŀ¼
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
