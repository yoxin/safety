package com.itheima.mobilesafe.receiver;

import com.itheima.mobilesafe.utils.LogUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

	private static final String TAG = "SMSReceiver";
	private SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		//���ն��ŵĴ���
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		
		for (Object obj : objs) {
			//�����ĳһ������
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
			//������
			String sender = sms.getOriginatingAddress();
			//��������
			String body = sms.getMessageBody();
			if("#*location*#".equals(body)){
				//����ֻ�λ�õ�ַ
				LogUtil.e(TAG, "����ֻ�λ�õ�ַ");
				//�ضϹ㲥
				abortBroadcast();
			}else if("#*alarm*#".equals(body)){
				//�ֻ�����
				LogUtil.e(TAG, "�ֻ�����");
				abortBroadcast();
			}else if("#*lockscreen*#".equals(body)){
				//Զ������
				LogUtil.e(TAG, "Զ������");
				abortBroadcast();
			}else if("#*wipedata*#".equals(body)){
				//����ֻ�����
				LogUtil.e(TAG, "�������");
				abortBroadcast();
			}
		}
	}

}
