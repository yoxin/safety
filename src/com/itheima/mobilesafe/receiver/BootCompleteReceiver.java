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
		// ��鵱ǰ���ֻ�sim,��ȡԭ���󶨵�sim�������������������һ����˵���ֻ����ܱ�������Ҫ͵͵�ķ����ű�����
		sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		tm = (TelephonyManager) context
				.getSystemService(context.TELEPHONY_SERVICE);
		String saveSim = sp.getString("sim", "") + "a";
		String realSim = tm.getSimSerialNumber();
		if (saveSim.equals(realSim)) {
			// simû�б��
		} else {
			// sim�Ѿ����
			LogUtil.d(TAG, "sim �Ѿ����");
			Toast.makeText(context, "sim �Ѿ����", 1).show();
			SmsManager.getDefault().sendTextMessage(sp.getString("safenumber", ""), null, "sim changing....", null, null);
		}
	}

}
