package com.itheima.mobilesafe;

import com.itheima.mobilesafe.fragment.LockFragment;
import com.itheima.mobilesafe.fragment.UnlockFragment;
import com.itheima.mobilesafe.utils.LogUtil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

public class AppLockActivity extends FragmentActivity implements
		OnClickListener {
	private static final String TAG = "AppLockActivity";
	private TextView tv_unlock;
	private TextView tv_lock;
	private LockFragment lockFragment;
	private UnlockFragment unlockFragment;
	private FragmentManager fragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initUI();
	}

	private void initUI() {
		setContentView(R.layout.activity_app_lock);
		tv_unlock = (TextView) findViewById(R.id.tv_unlock);
		tv_lock = (TextView) findViewById(R.id.tv_lock);
		tv_unlock.setOnClickListener(this);
		tv_lock.setOnClickListener(this);
		fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		lockFragment = new LockFragment();
		unlockFragment = new UnlockFragment();
		// 替换界面
		transaction.replace(R.id.fl_content, unlockFragment).commit();
	}

	@Override
	public void onClick(View v) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		switch (v.getId()) {
		case R.id.tv_lock:
			// 更新UI
			tv_lock.setBackgroundResource(R.drawable.tab_right_pressed);
			tv_unlock.setBackgroundResource(R.drawable.tab_left_default);
			transaction.replace(R.id.fl_content, lockFragment).commit();
			LogUtil.d(TAG, "切换到已加锁界面");
			break;
		case R.id.tv_unlock:
			// 更新UI
			tv_lock.setBackgroundResource(R.drawable.tab_right_default);
			tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
			transaction.replace(R.id.fl_content, unlockFragment).commit();
			LogUtil.d(TAG, "切换到未加锁界面");
			break;
		default:
			break;
		}
	}
}
