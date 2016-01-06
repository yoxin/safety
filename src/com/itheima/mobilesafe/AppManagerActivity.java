package com.itheima.mobilesafe;

import java.util.List;

import com.itheima.mobilesafe.BlackNumberActivity.BlackNumberAdapter.ViewHolder;
import com.itheima.mobilesafe.domain.AppInfo;
import com.itheima.mobilesafe.engine.AppInfoProvider;
import com.itheima.mobilesafe.utils.LogUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AppManagerActivity extends Activity {
	
	private static final String TAG = "AppManagerActivity";
	private TextView tv_avail_rom;
	private TextView tv_avail_sd;
	private Context context;
	private LinearLayout ll_app_loading;
	private List<AppInfo> data;
	private ListView lv_app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.activity_app_manager);
		tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
		tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);
		ll_app_loading =(LinearLayout)findViewById(R.id.ll_app_loading);
		lv_app = (ListView) findViewById(R.id.lv_app);
		String availSD = Formatter.formatFileSize(context, getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath()));
		String availRom = Formatter.formatFileSize(context, getAvailSpace(Environment.getDataDirectory().getAbsolutePath()));
		tv_avail_sd.setText("SD内存："+availSD);
		tv_avail_rom.setText("手机内存："+availRom);
		ll_app_loading.setVisibility(View.VISIBLE);//设置加载UI可见
		data = AppInfoProvider.getAppInfos(context);//获取数据
		ll_app_loading.setVisibility(View.INVISIBLE);//设置加载UI不可见
		lv_app.setAdapter(new AppAdapter(context, R.layout.list_item_appinfo, data));
	}
	
	/**
	 * 自定义设配器
	 * @author YOXIN
	 *
	 */
	private class AppAdapter extends ArrayAdapter<AppInfo> {

		private int resourceId;
		
		public AppAdapter(Context context, int textViewResourceId,
				List<AppInfo> objects) {
			super(context, textViewResourceId, objects);
			resourceId = textViewResourceId;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder viewHolder;
			if (convertView == null) {
				view = LayoutInflater.from(getContext()).inflate(resourceId, null);
				viewHolder = new ViewHolder();
				viewHolder.icon = (ImageView) view.findViewById(R.id.iv_app_icon);
				viewHolder.name = (TextView) view.findViewById(R.id.tv_app_name);
				viewHolder.isrom = (TextView) view.findViewById(R.id.tv_app_isrom);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}
			AppInfo appInfo = getItem(position);
			viewHolder.icon.setImageDrawable(appInfo.getIcon());
			String name = appInfo.getName();
			viewHolder.name.setText(name);
			return view;
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
		return availableBlocks*blockSize;
	}
}
