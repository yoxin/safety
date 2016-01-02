package com.itheima.mobilesafe.service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.itheima.mobilesafe.db.dao.BlackNumberDao;
import com.itheima.mobilesafe.utils.LogUtil;

public class CallSmsSafeService extends Service {

	public static final String TAG = "callSmsSafeService";
	InnerSmsReceiver smsReceiver;
	BlackNumberDao dao;
	TelephonyManager tm;
	MyPhoneStateListener listener;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class InnerSmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.d(TAG, "内部广播接收者，短信来了");
			//检查短信发送人是否黑名单联系人，模式是否为短信拦截或者全部拦截
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
		dao = new BlackNumberDao(getBaseContext());
		//注册短信拦截广播接收器
		smsReceiver = new InnerSmsReceiver();
		registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
		//开启电话监听服务
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		//注销短信拦截广播接收器
		unregisterReceiver(smsReceiver);
		smsReceiver = null;
		//取消电话监听服务
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		super.onDestroy();
	}
	
	private class MyPhoneStateListener extends PhoneStateListener {

		/**
		 * 监听电话状态改变
		 */
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING://电话响起
				String mode = dao.findMode(incomingNumber);
				if ("1".equals(mode) || "3".equals(mode)) {
					endCall();
				}
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
		
	}

	/**
	 * 挂断电话
	 */
	private void endCall() {
		try {
			Class clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			ITelephony.Stub.asInterface(iBinder).endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
