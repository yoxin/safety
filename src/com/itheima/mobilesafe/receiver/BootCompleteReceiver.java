package com.itheima.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.itheima.mobilesafe.utils.LogUtil;

public class BootCompleteReceiver extends BroadcastReceiver {

	private static final String TAG = "BootCompleteReceiver";
	private SharedPreferences sp;
	private TelephonyManager tm;

	@Override
	public void onReceive(Context context, Intent intent) {
		// 检查当前的手机sim,读取原来绑定的sim卡，如果发现两个卡不一样，说明手机可能被盗。需要偷偷的发短信报警；
		sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		tm = (TelephonyManager) context
				.getSystemService(context.TELEPHONY_SERVICE);
		String saveSim = sp.getString("sim", "") + "a";
		String realSim = tm.getSimSerialNumber();
		if (saveSim.equals(realSim)) {
			// sim没有变更
		} else {
			// sim已经变更
			LogUtil.d(TAG, "sim 已经变更");
			Toast.makeText(context, "sim 已经变更", 1).show();
			SmsManager.getDefault().sendTextMessage(sp.getString("safenumber", ""), null, "sim changing....", null, null);
		}
	}

}
