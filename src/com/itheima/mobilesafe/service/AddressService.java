package com.itheima.mobilesafe.service;

import com.itheima.mobilesafe.db.dao.NumberAddressQueryUtils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class AddressService extends Service {

	private TelephonyManager tm;
	MyPhoneStateListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
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
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}
}
