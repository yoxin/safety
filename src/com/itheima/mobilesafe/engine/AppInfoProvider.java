package com.itheima.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.itheima.mobilesafe.domain.AppInfo;

public class AppInfoProvider {
	public static List<AppInfo> getAppInfos(Context context) {
		List<AppInfo> data = new ArrayList<AppInfo>();
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
		for (PackageInfo packageInfo : packageInfos) {
			ApplicationInfo ai = packageInfo.applicationInfo;
			String packageName = packageInfo.packageName;
			String name = ai.loadLabel(pm).toString();
			Drawable icon = ai.loadIcon(pm);
			AppInfo info = new AppInfo();
			int flag = ai.flags;
			if ((flag&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0) {
				//�ڲ��洢
				info.setInRom(true);
			} else {
				//�ⲿ�洢
				info.setInRom(false);
			}
			if ((flag&ApplicationInfo.FLAG_SYSTEM)==0) {
				//�û�Ӧ��
				info.setUserApp(true);
			} else {
				//ϵͳӦ��
				info.setUserApp(false);
			}
			info.setIcon(icon);
			info.setName(name);
			info.setPackageName(packageName);
			data.add(info);
		}
		return data;
	}
}
