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
	// 窗口管理器
	private WindowManager wm;
	private View view;
	private SharedPreferences sp;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * 广播接收者：接受打电话的广播Toast拨出电话的地址归属地
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
		 * 当呼叫状态发生改变的时候回调该方法
		 */
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 当电话响起时，即来电时
				String address = NumberAddressQueryUtils
						.queryNumber(incomingNumber);
				myToast(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE: // 电话空闲时，即挂掉电话时
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
		// 代码注册广播接收者
		outCallReceriver = new OutCallReceriver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(outCallReceriver, filter);
	}

	WindowManager.LayoutParams params;

	public void myToast(String address) {
		view = View.inflate(this, R.layout.toast_show_address, null);
		// 设置土司的背景风格
		// "半透明","活力橙","卫士蓝","金属灰","苹果绿"
		int[] color = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		int which = sp.getInt("which", 0);
		view.setBackgroundResource(color[which]);
		/**
		 * 地址归属地双击事件
		 */
		final long[] mHit = new long[2];
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.arraycopy(mHit, 1, mHit, 0, mHit.length-1);
				mHit[1] = SystemClock.uptimeMillis();
				if (mHit[0] > mHit[1] - 500) {
					//地址归属地居中
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
		 * s地址归属地触摸事件
		 */
		view.setOnTouchListener(new OnTouchListener() {
			int startX = 0;
			int startY = 0;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN://触摸
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					LogUtil.d(TAG, "触摸时 startX:"+startX+"startY:"+startY);
					break;
				case MotionEvent.ACTION_MOVE://移动
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					LogUtil.d(TAG, "移动时 newX:"+newX+"newY:"+newY);
					int dx = newX - startX;
					int dy = newY - startY;
					LogUtil.d(TAG, "移动时 dx:"+dx+"dy:"+dy);
					params.x += dx;
					params.y += dy;
					//边界问题
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
					LogUtil.d(TAG, "移动时 startX:"+startX+"startY:"+startY);
					break;
				case MotionEvent.ACTION_UP://放开
					Editor editor = sp.edit();
					editor.putInt("lastX", params.x);
					editor.putInt("lastY", params.y);
					editor.commit();
					LogUtil.d(TAG, "放开时 startX:"+startX+"startY:"+startY);
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
		//电话级UI，可以点击触摸，需要添加权限"android.permission.SYSTEM_ALERT_WINDOW"
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		wm.addView(view, params);
		//view加载后调用，如果是第一次启动，在view加载完成后获取中间的位置
		if (params.x == -1 && params.y == -1) {
			view.post(new Runnable() {
				/**
				 * 地址归属地居中
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
		// 代码取消注册广播接收着
		unregisterReceiver(outCallReceriver);
		outCallReceriver = null;
	}
}
