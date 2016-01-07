package com.itheima.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.domain.AppInfo;
import com.itheima.mobilesafe.engine.AppInfoProvider;
import com.itheima.mobilesafe.utils.LogUtil;

public class AppManagerActivity extends Activity {

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
	PopupWindow popupWindow;

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
		ll_app_loading.setVisibility(View.VISIBLE);// ���ü���UI�ɼ�
		new Thread(new Runnable() {

			@Override
			public void run() {
				data = AppInfoProvider.getAppInfos(context);// ��ȡ����
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						ll_app_loading.setVisibility(View.INVISIBLE);// ���ü���UI���ɼ�
						lv_app.setAdapter(new AppAdapter(context,
								R.layout.list_item_appinfo, data));
					}
				});
			}
		}).start();
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
				int userSize = userData.size();
				AppInfo appInfo;
				if (position == 0) {
					return;
				} else if (position == userSize + 1) {
					return;
				} else if (position <= userSize) {
					int newPosition = position - 1;
					appInfo = userData.get(newPosition);
				} else {
					int newPosition = position - userSize - 2;
					appInfo = systemData.get(newPosition);
				}
				dismissPopupWindow();
				TextView contentView = new TextView(context);
				contentView.setText(appInfo.getPackageName());
				contentView.setTextColor(Color.BLACK);
				contentView.setBackgroundColor(Color.RED);
				popupWindow = new PopupWindow(contentView,
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				int[] location = new int[2];
				view.getLocationInWindow(location);
				int x = location[0];
				int y = location[1];
				popupWindow.showAtLocation(parent, Gravity.TOP | Gravity.LEFT, x, y);
			}

			
		});
	}
	
	private void dismissPopupWindow() {
		//�������ݴ���
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
		List<AppInfo> data;
		int userSize;
		int systemSize;

		public AppAdapter(Context context, int resourceId, List<AppInfo> data) {
			this.context = context;
			this.resourceId = resourceId;
			this.data = data;
			userData = new ArrayList<AppInfo>();
			systemData = new ArrayList<AppInfo>();
			for (AppInfo appInfo : data) {
				if (appInfo.isUserApp()) {
					userData.add(appInfo);
				} else {
					systemData.add(appInfo);
				}
			}
			userSize = userData.size();
			systemSize = systemData.size();
		}

		@Override
		public int getCount() {
			return data.size() + 2;
		}

		@Override
		public AppInfo getItem(int position) {
			if (position == 0 || position == userSize + 1) {
				return null;
			} else if (position <= userSize) {
				return userData.get(position - 1);
			} else {
				return systemData.get(position - userSize - 2);
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
				tv.setText("�û�����(" + userSize + ")");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			} else if (position == userSize + 1) {
				TextView tv = new TextView(getContext());
				tv.setText("ϵͳ����(" + systemSize + ")");
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
}
