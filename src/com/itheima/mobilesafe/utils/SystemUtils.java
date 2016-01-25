package com.itheima.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.itheima.mobilesafe.domain.TaskInfo;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageManager;

public class SystemUtils {
	/**
	 * 获取手机进程数
	 * @param context 上下文
	 * @return
	 */
	public static int getProcessCount(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		return list.size();
	}
	
	/**
	 * 获取手机可用内存
	 * @param context
	 * @return
	 */
	public static long getAvailMem(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}
	
	/**
	 * 获取手机总内存
	 * @param context
	 * @return
	 */
	public static long getTotalMem(Context context) {
		try {
			File file = new File("/proc/meminfo");
			FileInputStream fis;
			fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = br.readLine();	//MemTotal:        2070852 kB
			StringBuilder sb = new StringBuilder();
			for (char c : line.toCharArray()) {
				if (c <= '9' && c >= '0') {
					sb.append(c);
				}
			}
			return Long.parseLong(sb.toString())*1024;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
}
