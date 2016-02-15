package com.itheima.mobilesafe;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
	private TextView tv_status;
	private int process_count;
	private long availMem;
	private long totalMem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		context = this;
		tv_status = (TextView) findViewById(R.id.tv_status);
		ll_task_loading = (LinearLayout) findViewById(R.id.ll_task_loading);
		lv_task = (ListView) findViewById(R.id.lv_task);
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_mem_info = (TextView) findViewById(R.id.tv_mem_info);
		fillData();
		/**
		 * ����listView����¼����ı�ͼ�¼checkbox״̬
		 */
		lv_task.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TaskInfo info;
				if (position == 0) {
					return;
				} else if (position == userTaskInfos.size() + 1) {
					return;
				} else if (position <= userTaskInfos.size()) {
					info = userTaskInfos.get(position - 1);
				} else {
					info = systemTaskInfos.get(position - userTaskInfos.size()
							- 2);
				}
				// �������Ŀ�İ�������Ӧ�ó���İ��������ӵ��
				if (info.getPackname().equals(getPackageName())) {
					return;
				}
				// �ı�checkbox��״̬
				ViewHolder holder = (ViewHolder) view.getTag();
				holder.cb_status.setChecked(!info.isChecked());
				info.setChecked(!info.isChecked());
			}
		});
		/**
		 * ����listView�Ļ����¼�������С��Ŀ���ƶ�
		 */
		lv_task.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (userTaskInfos != null && systemTaskInfos != null) {
					if (firstVisibleItem <= userTaskInfos.size()) {
						tv_status.setText("�û�����:" + userTaskInfos.size());
					} else {
						tv_status.setText("ϵͳ����:" + systemTaskInfos.size());
					}
				}
			}
		});
	}

	private void initTitle() {
		process_count = SystemUtils.getProcessCount(context);
		tv_process_count.setText("��������" + process_count);
		availMem = SystemUtils.getAvailMem(context);
		totalMem = SystemUtils.getTotalMem(context);
		tv_mem_info.setText("ʣ��/���ڴ棺"
				+ Formatter.formatFileSize(context, availMem) + "/"
				+ Formatter.formatFileSize(context, totalMem));
	}

	private void fillData() {
		ll_task_loading.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// ��ȡȫ���Ľ�������
				allTaskInfos = TaskInfoProvider.getTaskprovider(context);
				// ����û��������ݺ�ϵͳ��������
				userTaskInfos = new ArrayList<TaskInfo>();
				systemTaskInfos = new ArrayList<TaskInfo>();
				for (TaskInfo info : allTaskInfos) {
					if (info.isUserTack()) {
						userTaskInfos.add(info);
					} else {
						systemTaskInfos.add(info);
					}
				}
				// ����UI
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						ll_task_loading.setVisibility(View.INVISIBLE);
						if (adapter == null) {
							adapter = new TaskManagerAdapter();
							lv_task.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						initTitle();
					}
				});
			}
		}).start();
	}

	class TaskManagerAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// ���н�������+����TextViewС��Ŀ
			return allTaskInfos.size() + 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TaskInfo info;
			if (position == 0) {
				TextView tv = new TextView(context);
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("�û�����:" + userTaskInfos.size());
				return tv;
			} else if (position == userTaskInfos.size() + 1) {
				TextView tv = new TextView(context);
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("ϵͳ����:" + systemTaskInfos.size());
				return tv;
			} else if (position <= userTaskInfos.size()) {
				info = userTaskInfos.get(position - 1);
			} else {
				info = systemTaskInfos.get(position - userTaskInfos.size() - 2);
			}
			LogUtil.d(TAG, "position:" + position);
			View view = null;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(context, R.layout.list_item_taskinfo, null);
				holder = new ViewHolder();
				holder.iv_task_icon = (ImageView) view
						.findViewById(R.id.iv_task_icon);
				holder.tv_task_name = (TextView) view
						.findViewById(R.id.tv_task_name);
				holder.tv_task_isrom = (TextView) view
						.findViewById(R.id.tv_task_isrom);
				holder.cb_status = (CheckBox) view.findViewById(R.id.cb_status);
				view.setTag(holder);
			}
			holder.iv_task_icon.setImageDrawable(info.getIcon());
			holder.tv_task_name.setText(info.getName());
			holder.tv_task_isrom.setText("�ڴ��С��"
					+ Formatter.formatFileSize(context, info.getMemSize()));
			holder.cb_status.setChecked(info.isChecked());
			/**
			 * �������Ŀ�İ�������Ӧ�ó���İ������Ͳ���ʾcheckbox else����������Ϊholder���õ�ԭ����Ҫ�������û�ȥ
			 */
			if (info.getPackname().equals(getPackageName())) {
				holder.cb_status.setVisibility(View.INVISIBLE);
			} else {
				holder.cb_status.setVisibility(View.VISIBLE);
			}
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

	/**
	 * ��ť����£�ȫѡ��ѡ��ȫ��checkbox
	 * 
	 * @param view
	 */
	public void selectAll(View view) {
		for (TaskInfo info : allTaskInfos) {
			// �������Ŀ�İ�������Ӧ�ó���İ���������
			if (info.getPackname().equals(getPackageName())) {
				continue;
			}
			info.setChecked(true);
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * ��ť����¼�����ѡ����ѡȫ��checkbox
	 * 
	 * @param view
	 */
	public void selectRe(View view) {
		for (TaskInfo info : allTaskInfos) {
			// �������Ŀ�İ�������Ӧ�ó���İ���������
			if (info.getPackname().equals(getPackageName())) {
				continue;
			}
			info.setChecked(!info.isChecked());
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * ��ť����¼�����������ȫ����ѡ�еĽ���
	 * 
	 * @param view
	 */
	public void clearProcess(View view) {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<TaskInfo> killedTaskInfos = new ArrayList<TaskInfo>();
		int count = 0;// ɱ���Ľ�����Ŀ
		int clearMem = 0;// ������ڴ�
		for (TaskInfo info : allTaskInfos) {
			if (info.isChecked()) {
				// ɱ������
				am.killBackgroundProcesses(info.getPackname());
				killedTaskInfos.add(info);
				// ������ݣ�֪ͨui����
				if (info.isUserTack()) {
					userTaskInfos.remove(info);
				} else {
					systemTaskInfos.remove(info);
				}
				count++;// ɱ���Ľ�����Ŀ�ۼ�
				clearMem += info.getMemSize();// ������ڴ��ۼ�
			}
		}
		// ����UI
		allTaskInfos.removeAll(killedTaskInfos);
		process_count -= count;
		tv_process_count.setText("��������" + process_count);
		availMem += clearMem;
		tv_mem_info.setText("ʣ��/���ڴ棺"
				+ Formatter.formatFileSize(context, availMem) + "/"
				+ Formatter.formatFileSize(context, totalMem));
		adapter.notifyDataSetChanged();
		// toast�������ʾ
		Toast.makeText(
				context,
				"������" + count + "������"
						+ Formatter.formatFileSize(context, clearMem) + "�ڴ�",
				Toast.LENGTH_LONG).show();
	}

	/**
	 * ��ť����¼�������
	 * 
	 * @param view
	 */
	public void interSetting(View view) {

	}
}
