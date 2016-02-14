package com.itheima.mobilesafe;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe.domain.TaskInfo;
import com.itheima.mobilesafe.engine.TaskInfoProvider;
import com.itheima.mobilesafe.utils.LogUtil;
import com.itheima.mobilesafe.utils.SystemUtils;

public class TaskManagerActivity extends Activity {
	
	private static final String TAG = "TaskManagerActivity";
	private TextView tv_process_count;
	private TextView tv_mem_info;
	private Context context;
	private LinearLayout ll_task_loading;
	private List<TaskInfo> allTaskInfos;
	private List<TaskInfo> userTaskInfos;
	private List<TaskInfo> systemTaskInfos;
	private ListView lv_task;
	private TaskManagerAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		context = this;
		ll_task_loading = (LinearLayout) findViewById(R.id.ll_task_loading);
		lv_task = (ListView) findViewById(R.id.lv_task);
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_mem_info = (TextView) findViewById(R.id.tv_mem_info);
		int process_count = SystemUtils.getProcessCount(context);
		tv_process_count.setText("进程数："+process_count);
		String availMem = Formatter.formatFileSize(context, SystemUtils.getAvailMem(context));
		String totalMem = Formatter.formatFileSize(context, SystemUtils.getTotalMem(context));
		tv_mem_info.setText("剩余/总内存："+availMem+"/"+totalMem);
		fillData();
		//监听listView点击事件，改变checkbox状态
		lv_task.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TaskInfo info;
				if (position == 0) {
					return;
				} else if (position == userTaskInfos.size()+1) {
					return;
				} else if (position <= userTaskInfos.size()) {
					info = userTaskInfos.get(position-1);
				} else {
					info = systemTaskInfos.get(position-userTaskInfos.size()-2);
				}
				//改变checkbox的状态
				ViewHolder holder = (ViewHolder) view.getTag();
				holder.cb_status.setChecked(!info.isChecked());
				info.setChecked(!info.isChecked());
			}
		});
	}

	private void fillData() {
		LogUtil.d(TAG, "fillData");
		ll_task_loading.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//获取全部的进程数据
				allTaskInfos = TaskInfoProvider.getTaskprovider(context);
				//填充用户进程数据和系统进程数据
				userTaskInfos = new ArrayList<TaskInfo>();
				systemTaskInfos = new ArrayList<TaskInfo>();
				for(TaskInfo info : allTaskInfos) {
					if(info.isUserTack()) {
						userTaskInfos.add(info);
					} else {
						systemTaskInfos.add(info);
					}
				}
				//更新UI
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						ll_task_loading.setVisibility(View.INVISIBLE);
						adapter = new TaskManagerAdapter();
						lv_task.setAdapter(adapter);
					}
				});
			}
		}).start();
	}
	
	class TaskManagerAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			//所有进程数据+两条TextView小条目
			return allTaskInfos.size()+2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TaskInfo info;
			if (position == 0) {
				TextView tv = new TextView(context);
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("用户进程:"+userTaskInfos.size());
				return tv;
			} else if (position == userTaskInfos.size()+1) {
				TextView tv = new TextView(context);
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("系统进程:"+systemTaskInfos.size());
				return tv;
			} else if (position <= userTaskInfos.size()) {
				info = userTaskInfos.get(position-1);
			} else {
				info = systemTaskInfos.get(position-userTaskInfos.size()-2);
			}
			LogUtil.d(TAG, "position:"+position);
			View view = null;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(context, R.layout.list_item_taskinfo, null);
				holder = new ViewHolder();
				holder.iv_task_icon = (ImageView) view.findViewById(R.id.iv_task_icon);
				holder.tv_task_name = (TextView) view.findViewById(R.id.tv_task_name);
				holder.tv_task_isrom = (TextView) view.findViewById(R.id.tv_task_isrom);
				holder.cb_status = (CheckBox) view.findViewById(R.id.cb_status);
				view.setTag(holder);
			}
			holder.iv_task_icon.setImageDrawable(info.getIcon());
			holder.tv_task_name.setText(info.getName());
			holder.tv_task_isrom.setText("内存大小："+Formatter.formatFileSize(context, info.getMemSize()));
			holder.cb_status.setChecked(info.isChecked());
			return view;
		}
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}
	
	class ViewHolder {
		TextView tv_task_name;
		TextView tv_task_isrom;
		CheckBox cb_status;
		ImageView iv_task_icon;
	}
}
