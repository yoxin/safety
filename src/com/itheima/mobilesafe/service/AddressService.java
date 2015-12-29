package com.itheima.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.dao.NumberAddressQueryUtils;

public class AddressService extends Service {

	private TelephonyManager tm;
	MyPhoneStateListener listener;
	private OutCallReceriver outCallReceriver;
	//���ڹ�����
	private WindowManager wm;
	private View view;
	private SharedPreferences sp;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * �㲥�����ߣ����ܴ�绰�Ĺ㲥Toast�����绰�ĵ�ַ������
	 * @author YOXIN
	 *
	 */
	class OutCallReceriver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String phone = getResultData();
			String address = NumberAddressQueryUtils.queryNumber(phone);
			myToast(address);
		}
		
	}
	
	class MyPhoneStateListener extends PhoneStateListener {
		/**
		 * ������״̬�����ı��ʱ��ص��÷���
		 */
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// ���绰����ʱ��������ʱ
				String address = NumberAddressQueryUtils
						.queryNumber(incomingNumber);
				myToast(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE: // �绰����ʱ�����ҵ��绰ʱ
				if (view != null) {
					wm.removeView(view);
					view = null;
				}
			default:
				break;
			}
		}

	}

	@Override
	public void onCreate() {
		super.onCreate();
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		//����ע��㲥������
		outCallReceriver = new OutCallReceriver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(outCallReceriver, filter);
	}

	public void myToast(String address) {
		view = View.inflate(this, R.layout.toast_show_address, null);
		//������˾�ı������
		//"��͸��","������","��ʿ��","������","ƻ����"
		int[] color = {R.drawable.call_locate_white,
				R.drawable.call_locate_orange,
				R.drawable.call_locate_blue,
				R.drawable.call_locate_gray,
				R.drawable.call_locate_green};
		int which = sp.getInt("which", 0);
		view.setBackgroundResource(color[which]);
		TextView tv_address = (TextView)view.findViewById(R.id.tv_address);
		tv_address.setText(address);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        wm.addView(view, params);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		//����ȡ��ע��㲥������
		unregisterReceiver(outCallReceriver);
		outCallReceriver = null;
	}
}
