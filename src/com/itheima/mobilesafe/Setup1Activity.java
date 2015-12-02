package com.itheima.mobilesafe;

import com.itheima.mobilesafe.utils.LogUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class Setup1Activity extends BaseSetupActivity {
	protected static final String TAG = "Setup1Activity";
	private GestureDetector gestureDetector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
		gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if ((e2.getRawX() - e2.getRawX() > 200)) {
					finish();
					return true;
				}
				if((e1.getRawX() - e2.getRawX()) > 200){
					showNext();
					return true;
				}

				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}


	@Override
	public void showNext() {
		Intent intent = new Intent(Setup1Activity.this, Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showPre() {
	}

}
