package com.itheima.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.domain.AppInfo;
import com.itheima.mobilesafe.engine.AppInfoProvider;
import com.itheima.mobilesafe.utils.DensityUtil;
import com.itheima.mobilesafe.utils.LogUtil;

public class AppManagerActivity extends Activity implements OnClickListener {

	private static final String TAG = "AppManagerActivity";
	private TextView tv_avail_rom;
	private TextView tv_avail_sd;
	private Context context;
	private LinearLayout ll_app_loading;
	private List<AppInfo> data;
	private List<AppInfo> userData;
	private List<AppInfo> systemData;
	private ListView lv_app;
	private TextView tv_status;
	private PopupWindow popupWindow;
	private LinearLayout ll_app_start;
	private LinearLayout ll_app_delete;
	private LinearLayout ll_app_share;
	private AppInfo appInfo;//��ȡlistView�����item��Ϣ
	private AppAdapter appAdapter;//listView���Զ���������

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.activity_app_manager);
		tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
		tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);
		ll_app_loading = (LinearLayout) findViewById(R.id.ll_app_loading);
		lv_app = (ListView) findViewById(R.id.lv_app);
		tv_status = (TextView) findViewById(R.id.tv_status);
		String availSD = Formatter.formatFileSize(context,
				getAvailSpace(Environment.getExternalStorageDirectory()
						.getAbsolutePath()));
		String availRom = Formatter
				.formatFileSize(context, getAvailSpace(Environment
						.getDataDirectory().getAbsolutePath()));
		tv_avail_sd.setText("SD�ڴ棺" + availSD);
		tv_avail_rom.setText("�ֻ��ڴ棺" + availRom);
		fillData();
		/**
		 * ����������
		 */
		lv_app.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			/**
			 * ��Ļ����ʱ���ص��÷���
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (userData != null && systemData != null) {
					dismissPopupWindow();
					if (firstVisibleItem <= userData.size()) {
						tv_status.setText("�û�����" + userData.size() + ")");
					} else {
						tv_status.setText("�û�����" + systemData.size() + ")");
					}
				}

			}
		});
		lv_app.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					return;
				} else if (position == userData.size() + 1) {
					return;
				} else if (position <= userData.size()) {
					int newPosition = position - 1;
					appInfo = userData.get(newPosition);
				} else {
					int newPosition = position - userData.size() - 2;
					appInfo = systemData.get(newPosition);
				}
				dismissPopupWindow();
				/*
				 * TextView contentView = new TextView(context);
				 * contentView.setText(appInfo.getPackageName());
				 * contentView.setTextColor(Color.BLACK);
				 * contentView.setBackgroundColor(Color.RED);
				 */
				LinearLayout contentView = (LinearLayout) View.inflate(context,
						R.layout.popup_app_item, null);
				ll_app_start = (LinearLayout) contentView
						.findViewById(R.id.ll_app_start);
				ll_app_delete = (LinearLayout) contentView
						.findViewById(R.id.ll_app_delete);
				ll_app_share = (LinearLayout) contentView
						.findViewById(R.id.ll_app_share);
				ll_app_start.setOnClickListener(AppManagerActivity.this);
				ll_app_delete.setOnClickListener(AppManagerActivity.this);
				ll_app_share.setOnClickListener(AppManagerActivity.this);
				popupWindow = new PopupWindow(contentView,
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				// ���Ŷ����Ĵ������Ҫ�б�����ɫ������ָ��Ϊ͸����ɫ
				popupWindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));
				int[] location = new int[2];
				// ��ȡ�ؼ�����
				view.getLocationInWindow(location);
				int x = DensityUtil.dip2px(context, 30f);
				int y = location[1];
				popupWindow.showAtLocation(parent, Gravity.TOP | Gravity.LEFT,
						x, y);
				// �������Ŷ���
				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0.5f);
				// ���ö���ʱ��
				sa.setDuration(300);
				// ����͸�����䶯��
				AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
				// ���ö���ʱ��
				aa.setDuration(300);
				// ���ö�������
				AnimationSet set = new AnimationSet(false);
				set.addAnimation(aa);
				set.addAnimation(sa);
				contentView.startAnimation(set);
			}
		});
	}

	private void fillData() {
		ll_app_loading.setVisibility(View.VISIBLE);// ���ü���UI�ɼ�
		new Thread(new Runnable() {

			@Override
			public void run() {
				data = AppInfoProvider.getAppInfos(context);// ��ȡ����
				userData = new ArrayList<AppInfo>();
				systemData = new ArrayList<AppInfo>();
				for (AppInfo appInfo : data) {
					if (appInfo.isUserApp()) {
						userData.add(appInfo);
					} else {
						systemData.add(appInfo);
					}
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (appAdapter == null) {
							appAdapter = new AppAdapter(context,
									R.layout.list_item_appinfo);
							lv_app.setAdapter(appAdapter);
						} else {
							appAdapter.notifyDataSetChanged();
						}
						ll_app_loading.setVisibility(View.INVISIBLE);// ���ü���UI���ɼ�
						
					}
				});
			}
		}).start();
	}

	private void dismissPopupWindow() {
		// �������ݴ���
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	/**
	 * �Զ���������
	 * 
	 * @author YOXIN
	 * 
	 */
	private class AppAdapter extends BaseAdapter {

		Context context;
		int resourceId;

		public AppAdapter(Context context, int resourceId) {
			this.context = context;
			this.resourceId = resourceId;
		}

		@Override
		public int getCount() {
			return data.size() + 2;
		}

		@Override
		public AppInfo getItem(int position) {
			if (position == 0 || position == userData.size() + 1) {
				return null;
			} else if (position <= userData.size()) {
				return userData.get(position - 1);
			} else {
				return systemData.get(position - userData.size() - 2);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == 0) {
				TextView tv = new TextView(getContext());
				tv.setText("�û�����(" + userData.size() + ")");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			} else if (position == userData.size() + 1) {
				TextView tv = new TextView(getContext());
				tv.setText("ϵͳ����(" + systemData.size() + ")");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			} else {
				View view;
				ViewHolder viewHolder;
				if (convertView == null || convertView instanceof TextView) {
					view = LayoutInflater.from(getContext()).inflate(
							resourceId, null);
					viewHolder = new ViewHolder();
					viewHolder.icon = (ImageView) view
							.findViewById(R.id.iv_app_icon);
					viewHolder.name = (TextView) view
							.findViewById(R.id.tv_app_name);
					viewHolder.isrom = (TextView) view
							.findViewById(R.id.tv_app_isrom);
					view.setTag(viewHolder);
				} else {
					view = convertView;
					viewHolder = (ViewHolder) view.getTag();
				}
				//��ȡappInfo
				AppInfo appInfo = getItem(position);
				viewHolder.icon.setImageDrawable(appInfo.getIcon());
				String name = appInfo.getName();
				viewHolder.name.setText(name);
				if (appInfo.isInRom()) {
					viewHolder.isrom.setText("�ֻ��洢");
				} else {
					viewHolder.isrom.setText("�ⲿ�洢");
				}
				return view;
			}
		}

		private Context getContext() {
			return context;
		}

		private class ViewHolder {
			ImageView icon;
			TextView name;
			TextView isrom;
		}

	}

	private int getAvailSpace(String path) {
		StatFs fs = new StatFs(path);
		int availableBlocks = fs.getAvailableBlocks();
		int blockSize = fs.getBlockSize();
		return availableBlocks * blockSize;
	}

	@Override
	protected void onDestroy() {
		dismissPopupWindow();
		super.onDestroy();
	}

	/**
	 * ���ֶ�Ӧ�ĵ���¼�
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_app_start:
			LogUtil.d(TAG, "����app");
			startApp();//����APP
			break;
		case R.id.ll_app_delete:
			LogUtil.d(TAG, "ж��app");
			deleteApp();
			dismissPopupWindow();
			break;
		case R.id.ll_app_share:
			LogUtil.d(TAG, "����app");
			break;
		default:
			break;
		}
	}

	/**
	 * ж��APP
	 */
	private void deleteApp() {
		if (appInfo.isUserApp()) {
			/*<activity android:name=".UninstallerActivity"
	                android:configChanges="orientation|keyboardHidden"
	                android:theme="@style/TallTitleBarTheme">
	            <intent-filter>
	                <action android:name="android.intent.action.VIEW" />
	                <action android:name="android.intent.action.DELETE" />
	                <category android:name="android.intent.category.DEFAULT" />
	                <data android:scheme="package" />
	            </intent-filter>
	        </activity>*/
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.setAction(Intent.ACTION_DELETE);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			String uriStr = "package:"+appInfo.getPackageName();
			intent.setData(Uri.parse(uriStr));
			startActivityForResult(intent, 0);
		} else {
			Toast.makeText(context, "ж��Ӧ�ó�����ҪRootȨ��", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//ˢ�½���
		switch (requestCode) {
		case 0:
			fillData();
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 *  ����APP
	 */
	private void startApp() {
		PackageManager pm = getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackageName());
		if (intent != null) {
			startActivity(intent);
		} else {
			Toast.makeText(context, "�޷�����", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	protected void onResume() {
		fillData();
		super.onResume();
	}
}
