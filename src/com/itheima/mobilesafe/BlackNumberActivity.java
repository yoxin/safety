package com.itheima.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe.db.dao.BlackNumberDao;
import com.itheima.mobilesafe.domain.BlackNumberInfo;

/**
 * 黑名单列表界面
 * 
 * @author YOXIN
 * 
 */
public class BlackNumberActivity extends Activity {
	ListView list_black_number;
	List<BlackNumberInfo> data;
	BlackNumberDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_number);
		list_black_number = (ListView) findViewById(R.id.list_black_number);
		dao = new BlackNumberDao(this);
		data = dao.findAll();
		list_black_number.setAdapter(new blackNumberAdapter(this,
				R.layout.blacknumber_item_view, data));
	}

	/**
	 * 自定义ListView设配器
	 * 
	 * @author YOXIN
	 * 
	 */
	class blackNumberAdapter extends ArrayAdapter<BlackNumberInfo> {

		private int resourceId;

		public blackNumberAdapter(Context context, int textViewResourceId,
				List<BlackNumberInfo> objects) {
			super(context, textViewResourceId, objects);
			resourceId = textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			BlackNumberInfo blackNumberInfo = getItem(position);
			View view;
			ViewHolder viewHolder;
			if (convertView == null) {
				view = LayoutInflater.from(getContext()).inflate(resourceId,
						null);
				viewHolder = new ViewHolder();
				viewHolder.number = (TextView) view
						.findViewById(R.id.tv_black_number);
				viewHolder.mode = (TextView) view
						.findViewById(R.id.tv_black_mode);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}
			viewHolder.number.setText(blackNumberInfo.getNumber());
			String mode = blackNumberInfo.getMode();
			if ("1".equals(mode)) {
				viewHolder.mode.setText("电话拦截");
			} else if ("2".equals(mode)) {
				viewHolder.mode.setText("短信拦截");
			} else if ("3".equals(mode)) {
				viewHolder.mode.setText("全部拦截");
			}
			return view;
		}

		class ViewHolder {

			TextView number;

			TextView mode;

		}
	}
}
