package com.itheima.mobilesafe;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.LogUtil;

import android.app.Activity;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

public class CleanCacheActivity extends Activity {
	private static final String TAG = "CleanCacheActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
		fillData();
	}

	private List<CacheInfo> cacheInfos;
	private PackageManager pm;

	// @hide
	// public abstract void getPackageSizeInfo(String packageName,
	// IPackageStatsObserver observer);
	// 利用到aidl
	private void fillData() {
		pm = getPackageManager();
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
		cacheInfos = new ArrayList<CacheInfo>();
		for (PackageInfo info : packageInfos) {
			// 获取缓存数据
			getCacheInfo(info);
		}
		for (CacheInfo info : cacheInfos) {
			LogUtil.d(TAG, info.toString());
		}
	}

	private void getCacheInfo(PackageInfo packageInfo) {
		try {
			/**
			 * 反射API
			 */
			Method method = PackageManager.class.getDeclaredMethod(
					"getPackageSizeInfo", String.class,
					IPackageStatsObserver.class);
			method.invoke(pm, packageInfo.applicationInfo.packageName,
					new MyIPackageStatsObserver(packageInfo));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {

		private PackageInfo packageInfo;

		public MyIPackageStatsObserver(PackageInfo packageInfo) {
			this.packageInfo = packageInfo;
		}

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			long cacheSize = pStats.cacheSize;
			if (cacheSize > 0) {
				CacheInfo cacheInfo = new CacheInfo();
				String appName = packageInfo.applicationInfo.loadLabel(pm)
						.toString();
				if (appName.isEmpty()) {
					appName = packageInfo.packageName;
				}
				cacheInfo.appName = appName;
				Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
				cacheInfo.icon = icon;
				cacheInfo.cacheSize = cacheSize;
				cacheInfos.add(cacheInfo);
			}
		}

	}

	private class CacheInfo {
		String appName;
		Drawable icon;
		long cacheSize;

		@Override
		public String toString() {
			return "CacheInfo [appName=" + appName + ", cacheSize=" + cacheSize
					+ "]";
		}
	}

	private void initUI() {
		setContentView(R.layout.activity_clean_cache);
	}
}
