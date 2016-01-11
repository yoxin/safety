package com.itheima.mobilesafe;

import java.text.Format;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.TextView;

import com.itheima.mobilesafe.utils.SystemUtils;

public class TaskManagerActivity extends Activity {
	
	private TextView tv_process_count;
	private TextView tv_mem_info;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.activity_task_manager);
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_mem_info = (TextView) findViewById(R.id.tv_mem_info);
		int process_count = SystemUtils.getProcessCount(context);
		tv_process_count.setText("进程数："+process_count);
		String availMem = Formatter.formatFileSize(context, SystemUtils.getAvailMem(context));
		String totalMem = Formatter.formatFileSize(context, SystemUtils.getTotalMem(context));
		tv_mem_info.setText("剩余/总内存："+availMem+"/"+totalMem);
	}
}
