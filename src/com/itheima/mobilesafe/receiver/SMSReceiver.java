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
		// 接收短信的代码
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		sp = context.getSharedPreferences("config", context.MODE_PRIVATE);

		for (Object obj : objs) {
			// 具体的某一条短信
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
			// 发送者
			String sender = sms.getOriginatingAddress();
			String safeNumber = sp.getString("safeNumber", "");
			// 短信内容
			String body = sms.getMessageBody();
			if (sender.contains(safeNumber)) {
				if ("#*location*#".equals(body)) {
					// 获得手机位置地址
					LogUtil.e(TAG, "获得手机位置地址");
					// 截断广播
					abortBroadcast();
				} else if ("#*alarm*#".equals(body)) {
					// 手机报警
					LogUtil.e(TAG, "手机报警");
					MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
					player.setLooping(false);
					player.setVolume(1.0f, 1.0f);
					player.start();
					abortBroadcast();
				} else if ("#*lockscreen*#".equals(body)) {
					// 远程锁屏
					LogUtil.e(TAG, "远程锁屏");
					abortBroadcast();
				} else if ("#*wipedata*#".equals(body)) {
					// 清除手机数据
					LogUtil.e(TAG, "清楚数据");
					abortBroadcast();
				}
			}
		}
	}

}
