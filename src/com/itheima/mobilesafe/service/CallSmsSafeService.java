package com.itheima.mobilesafe.service;

import com.itheima.mobilesafe.db.dao.BlackNumberDao;
import com.itheima.mobilesafe.utils.LogUtil;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

public class CallSmsSafeService extends Service {

	public static final String TAG = "callSmsSafeService";
	InnerSmsReceiver smsReceiver;
	BlackNumberDao dao;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class InnerSmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.d(TAG, "内部广播接收者，短信来了");
			//检查短信发送人是否黑名单联系人，模式是否为短信拦截或者全部拦截
			dao = new BlackNumberDao(context);
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			
			for (Object obj : objs) {
				SmsMessage message = SmsMessage.createFromPdu((byte[])obj);
				//获取发送人号码
				String sender = message.getOriginatingAddress();
				String mode = dao.findMode(sender);
				if ("2".equals(mode) || "3".equals(mode)) {
					LogUtil.d(TAG, "黑名单联系人，短信拦截");
					abortBroadcast();
				}
			}
		}
		
	}

	@Override
	public void onCreate() {
		//注册短信拦截广播接收器
		smsReceiver = new InnerSmsReceiver();
		registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		//注销短信拦截广播接收器
		unregisterReceiver(smsReceiver);
		smsReceiver = null;
		super.onDestroy();
	}
	
	
}
