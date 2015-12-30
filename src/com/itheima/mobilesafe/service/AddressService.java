package com.itheima.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.dao.NumberAddressQueryUtils;
import com.itheima.mobilesafe.utils.LogUtil;

public class AddressService extends Service {

	protected static final String TAG = "AddressService";
	private TelephonyManager tm;
	MyPhoneStateListener listener;
	private OutCallReceriver outCallReceriver;
	// ���ڹ�����
	private WindowManager wm;
	private View view;
	private SharedPreferences sp;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * �㲥�����ߣ����ܴ�绰�Ĺ㲥Toast�����绰�ĵ�ַ������
	 * 
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
		// ����ע��㲥������
		outCallReceriver = new OutCallReceriver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(outCallReceriver, filter);
	}

	WindowManager.LayoutParams params;

	public void myToast(String address) {
		view = View.inflate(this, R.layout.toast_show_address, null);
		// ������˾�ı������
		// "��͸��","������","��ʿ��","������","ƻ����"
		int[] color = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		int which = sp.getInt("which", 0);
		view.setBackgroundResource(color[which]);
		/**
		 * ��ַ������˫���¼�
		 */
		final long[] mHit = new long[2];
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.arraycopy(mHit, 1, mHit, 0, mHit.length-1);
				mHit[1] = SystemClock.uptimeMillis();
				if (mHit[0] > mHit[1] - 500) {
					//��ַ�����ؾ���
					params.x = (wm.getDefaultDisplay().getWidth()-view.getWidth())/2;
					params.y = (wm.getDefaultDisplay().getHeight()-view.getHeight())/2;
					Editor editor = sp.edit();
					editor.putInt("lastX", params.x);
					editor.putInt("lastY", params.y);
					editor.commit();
					wm.updateViewLayout(view, params);
				}
			}
		});
		/**
		 * s��ַ�����ش����¼�
		 */
		view.setOnTouchListener(new OnTouchListener() {
			int startX = 0;
			int startY = 0;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN://����
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					LogUtil.d(TAG, "����ʱ startX:"+startX+"startY:"+startY);
					break;
				case MotionEvent.ACTION_MOVE://�ƶ�
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					LogUtil.d(TAG, "�ƶ�ʱ newX:"+newX+"newY:"+newY);
					int dx = newX - startX;
					int dy = newY - startY;
					LogUtil.d(TAG, "�ƶ�ʱ dx:"+dx+"dy:"+dy);
					params.x += dx;
					params.y += dy;
					//�߽�����
					if (params.x < 0) {
						params.x = 0;
					}
					if (params.y < 0) {
						params.y = 0;
					}
					if (params.x > wm.getDefaultDisplay().getWidth()-view.getWidth()) {
						params.x = wm.getDefaultDisplay().getWidth()-view.getWidth();
					}
					if (params.y > wm.getDefaultDisplay().getHeight()-view.getHeight()) {
						params.y = wm.getDefaultDisplay().getHeight()-view.getHeight();
					}
					wm.updateViewLayout(view, params);
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					LogUtil.d(TAG, "�ƶ�ʱ startX:"+startX+"startY:"+startY);
					break;
				case MotionEvent.ACTION_UP://�ſ�
					Editor editor = sp.edit();
					editor.putInt("lastX", params.x);
					editor.putInt("lastY", params.y);
					editor.commit();
					LogUtil.d(TAG, "�ſ�ʱ startX:"+startX+"startY:"+startY);
					break;
				default:
					break;
				}
				return false;
			}
		});
		TextView tv_address = (TextView) view.findViewById(R.id.tv_address);
		tv_address.setText(address);
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.gravity = Gravity.TOP+Gravity.LEFT;
		params.x = sp.getInt("lastX", -1);
		params.y = sp.getInt("lastY", -1);
		params.format = PixelFormat.TRANSLUCENT;
		//�绰��UI�����Ե����������Ҫ���Ȩ��"android.permission.SYSTEM_ALERT_WINDOW"
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		wm.addView(view, params);
		//view���غ���ã�����ǵ�һ����������view������ɺ��ȡ�м��λ��
		if (params.x == -1 && params.y == -1) {
			view.post(new Runnable() {
				/**
				 * ��ַ�����ؾ���
				 */
				@Override
				public void run() {
					params.x = (wm.getDefaultDisplay().getWidth()-view.getWidth())/2;
					params.y = (wm.getDefaultDisplay().getHeight()-view.getHeight())/2;
					Editor editor = sp.edit();
					editor.putInt("lastX", params.x);
					editor.putInt("lastY", params.y);
					editor.commit();
					wm.updateViewLayout(view, params);
				}
			});
		}
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		// ����ȡ��ע��㲥������
		unregisterReceiver(outCallReceriver);
		outCallReceriver = null;
	}
}
