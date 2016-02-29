package com.itheima.mobilesafe;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.utils.LogUtil;

public class CleanCacheActivity extends Activity {
	private static final String TAG = "CleanCacheActivity";
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();

	}

	// 缓存数据
	private List<CacheInfo> cacheInfos;
	private PackageManager pm;
	private ListView lv_cache;
	private TextView tv_count;

	// @hide
	// public abstract void getPackageSizeInfo(String packageName,
	// IPackageStatsObserver observer);
	// 利用到aidl
	private void fillData() {
		pm = getPackageManager();
		final List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
		cacheInfos = new ArrayList<CacheInfo>();

		new Thread(new Runnable() {

			@Override
			public void run() {
				for (PackageInfo info : packageInfos) {
					// 获取缓存数据
					getCacheInfo(info);
				}
				handler.sendEmptyMessage(0);
			}
		}).start();

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (adapter == null) {
				adapter = new CacheAdapter();
				lv_cache.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}

		}
	};
	private CacheAdapter adapter;

	private void getCacheInfo(PackageInfo packageInfo) {
		try {
			/**
			 * 反射API
			 */
			Method method = PackageManager.class.getDeclaredMethod(
					"getPackageSizeInfo", String.class,
					IPackageStatsObserver.class);
			method.invoke(pm, packageInfo.applicationInfo.packageName,
					new MyIPackageStatsObserver(packageInfo));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {

		private PackageInfo packageInfo;

		public MyIPackageStatsObserver(PackageInfo packageInfo) {
			this.packageInfo = packageInfo;
		}

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			long cacheSize = pStats.cacheSize;
			if (cacheSize > 0) {
				CacheInfo cacheInfo = new CacheInfo();
				String appName = packageInfo.applicationInfo.loadLabel(pm)
						.toString();
				if (appName.isEmpty()) {
					cacheInfo.appName = packageInfo.packageName;
				} else {
					cacheInfo.appName = appName;
				}
				Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
				if (icon == null) {
					cacheInfo.icon = getResources().getDrawable(
							R.drawable.ic_launcher);
				} else {
					cacheInfo.icon = icon;
				}
				cacheInfo.cacheSize = cacheSize;
				cacheInfos.add(cacheInfo);
			}
		}

	}

	private class CacheInfo {
		String appName;
		Drawable icon;
		long cacheSize;

		@Override
		public String toString() {
			return "CacheInfo [appName=" + appName + ", cacheSize=" + cacheSize
					+ "]";
		}
	}

	private void initUI() {
		context = this;
		setContentView(R.layout.activity_clean_cache);
		tv_count = (TextView) findViewById(R.id.tv_count);
		lv_cache = (ListView) findViewById(R.id.lv_cache);
		fillData();
	}

	/**
	 * 缓存清理适配器
	 * 
	 * @author YOXIN
	 * 
	 */
	private class CacheAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			tv_count.setText("需要清理缓存的软件有" + cacheInfos.size() + "个");
			return cacheInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return cacheInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder holder;
			CacheInfo cacheInfo = cacheInfos.get(position);
			if (convertView == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.item_cache, null);
				holder = new ViewHolder();
				holder.bt_clean = (Button) view.findViewById(R.id.bt_clean);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
				holder.tv_size = (TextView) view.findViewById(R.id.tv_size);
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			holder.iv_icon.setBackgroundDrawable(cacheInfo.icon);
			holder.tv_name.setText(cacheInfo.appName);
			holder.tv_size.setText("缓存："
					+ Formatter.formatFileSize(context, cacheInfo.cacheSize));
			return view;
		}

	}

	private class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_size;
		Button bt_clean;
	}

	/**
	 * 点击事件，一键清理缓存
	 */
	public void cleanAll(View view) {
		try {
			Method method = PackageManager.class.getDeclaredMethod(
					"freeStorageAndNotify", long.class,
					IPackageDataObserver.class);
			// 计算要释放的空间大小
			long localLong = Long.valueOf(getEnviromenSize() - 1L);
			// method.invoke(pm, localLong, new MyIPackageDataObserver());
			// 直接给一个Long.MAX_VALUE也行
			method.invoke(pm, Long.MAX_VALUE, new MyIPackageDataObserver());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Toast.makeText(context, "全部清除完成", Toast.LENGTH_LONG).show();
		fillData();
	}

	/**
	 * 计算要释放的空间大小
	 * 
	 * @return
	 */
	private long getEnviromenSize() {
		File localFile = Environment.getDataDirectory();
		long l1;
		if (localFile == null) {
			l1 = 0L;
		} else {
			String str = localFile.getPath();
			StatFs localStatFs = new StatFs(str);
			long l2 = localStatFs.getBlockSize();
			l1 = localStatFs.getBlockCount() * l2;
		}
		return l1;
	}

	private class MyIPackageDataObserver extends IPackageDataObserver.Stub {

		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {
			LogUtil.d(TAG, "packageName:" + packageName + "succeeded:"
					+ succeeded);

		}
	}

	@Override
	protected void onRestart() {
		fillData();
		super.onRestart();
	}
}
