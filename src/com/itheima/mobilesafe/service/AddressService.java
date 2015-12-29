package com.itheima.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.itheima.mobilesafe.db.dao.NumberAddressQueryUtils;

public class AddressService extends Service {

	private TelephonyManager tm;
	MyPhoneStateListener listener;
	private OutCallReceriver outCallReceriver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * 广播接收者：接受打电话的广播Toast拨出电话的地址归属地
	 * @author YOXIN
	 *
	 */
	class OutCallReceriver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String phone = getResultData();
			String address = NumberAddressQueryUtils.queryNumber(phone);
			Toast.makeText(context, address, Toast.LENGTH_LONG).show();
		}
		
	}
	
	class MyPhoneStateListener extends PhoneStateListener {
		/**
		 * 当呼叫状态发生改变的时候回调该方法
		 */
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 当电话响起时，即来电时
				String address = NumberAddressQueryUtils
						.queryNumber(incomingNumber);
				Toast.makeText(getApplicationContext(), address,
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}

	}

	@Override
	public void onCreate() {
		super.onCreate();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		//代码注册广播接收者
		outCallReceriver = new OutCallReceriver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(outCallReceriver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		//代码取消注册广播接收着
		unregisterReceiver(outCallReceriver);
		outCallReceriver = null;
	}
}
