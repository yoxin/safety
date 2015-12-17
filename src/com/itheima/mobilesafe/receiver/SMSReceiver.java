package com.itheima.mobilesafe.receiver;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.LogUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

	private static final String TAG = "SMSReceiver";
	private SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		// ���ն��ŵĴ���
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		sp = context.getSharedPreferences("config", context.MODE_PRIVATE);

		for (Object obj : objs) {
			// �����ĳһ������
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
			// ������
			String sender = sms.getOriginatingAddress();
			String safeNumber = sp.getString("safeNumber", "");
			// ��������
			String body = sms.getMessageBody();
			if (sender.contains(safeNumber)) {
				if ("#*location*#".equals(body)) {
					// ����ֻ�λ�õ�ַ
					LogUtil.e(TAG, "����ֻ�λ�õ�ַ");
					// �ضϹ㲥
					abortBroadcast();
				} else if ("#*alarm*#".equals(body)) {
					// �ֻ�����
					LogUtil.e(TAG, "�ֻ�����");
					MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
					player.setLooping(false);
					player.setVolume(1.0f, 1.0f);
					player.start();
					abortBroadcast();
				} else if ("#*lockscreen*#".equals(body)) {
					// Զ������
					LogUtil.e(TAG, "Զ������");
					abortBroadcast();
				} else if ("#*wipedata*#".equals(body)) {
					// ����ֻ�����
					LogUtil.e(TAG, "�������");
					abortBroadcast();
				}
			}
		}
	}

}
