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
			LogUtil.d(TAG, "�ڲ��㲥�����ߣ���������");
			//�����ŷ������Ƿ��������ϵ�ˣ�ģʽ�Ƿ�Ϊ�������ػ���ȫ������
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
		dao = new BlackNumberDao(getBaseContext());
		//ע��������ع㲥������
		smsReceiver = new InnerSmsReceiver();
		registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
		//�����绰��������
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		//ע���������ع㲥������
		unregisterReceiver(smsReceiver);
		smsReceiver = null;
		//ȡ���绰��������
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		super.onDestroy();
	}
	
	private class MyPhoneStateListener extends PhoneStateListener {

		/**
		 * �����绰״̬�ı�
		 */
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING://�绰����
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
	 * �Ҷϵ绰
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
