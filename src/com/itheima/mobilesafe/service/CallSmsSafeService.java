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
			LogUtil.d(TAG, "�ڲ��㲥�����ߣ���������");
			//�����ŷ������Ƿ��������ϵ�ˣ�ģʽ�Ƿ�Ϊ�������ػ���ȫ������
			dao = new BlackNumberDao(context);
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			
			for (Object obj : objs) {
				SmsMessage message = SmsMessage.createFromPdu((byte[])obj);
				//��ȡ�����˺���
				String sender = message.getOriginatingAddress();
				String mode = dao.findMode(sender);
				if ("2".equals(mode) || "3".equals(mode)) {
					LogUtil.d(TAG, "��������ϵ�ˣ���������");
					abortBroadcast();
				}
			}
		}
		
	}

	@Override
	public void onCreate() {
		//ע��������ع㲥������
		smsReceiver = new InnerSmsReceiver();
		registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		//ע���������ع㲥������
		unregisterReceiver(smsReceiver);
		smsReceiver = null;
		super.onDestroy();
	}
	
	
}
