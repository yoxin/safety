package com.itheima.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Debug.MemoryInfo;

import com.itheima.mobilesafe.domain.TaskInfo;

public class TaskInfoProvider {
	public static List<TaskInfo> getTaskprovider(Context context) {
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		for (RunningAppProcessInfo processInfo : processInfos) {
			TaskInfo taskInfo = new TaskInfo();
			String packageName = processInfo.processName;
			taskInfo.setPackname(packageName);
			MemoryInfo[] mi = am.getProcessMemoryInfo(new int[]{processInfo.pid});
			taskInfo.setMemSize(mi[0].getTotalPrivateDirty()*1024);
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
				taskInfo.setName(applicationInfo.loadLabel(pm).toString());
				taskInfo.setIcon(applicationInfo.loadIcon(pm));
				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					taskInfo.setUserTack(true);
				} else {
					taskInfo.setUserTack(false);
				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			taskInfos.add(taskInfo);
		}
		return taskInfos;	
	}
}
