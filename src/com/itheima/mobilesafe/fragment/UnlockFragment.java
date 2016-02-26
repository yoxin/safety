package com.itheima.mobilesafe.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.dao.AppLockDao;
import com.itheima.mobilesafe.domain.AppInfo;
import com.itheima.mobilesafe.engine.AppInfoProvider;

public class UnlockFragment extends Fragment {

	private TextView tv_size;
	private ListView lv_unlock;
	private Context context;
	private List<AppInfo> appInfos;
	private List<AppInfo> lockAppInfos;
	private UnLockAdapter adapter;
	private AppLockDao dao;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.item_unlock_fragment, null);
		context = getActivity();
		tv_size = (TextView) view.findViewById(R.id.tv_size);
		lv_unlock = (ListView) view.findViewById(R.id.lv_unlock);
		return view;
	}

	@Override
	public void onStart() {
		appInfos = AppInfoProvider.getAppInfos(context);
		lockAppInfos = new ArrayList<AppInfo>();
		for (AppInfo info : appInfos) {
			String packageName = info.getPackageName();
			dao = new AppLockDao(context);
			if (dao.find(packageName)) {
				lockAppInfos.add(info);
			}
		}
		appInfos.removeAll(lockAppInfos);
		adapter = new UnLockAdapter();
		lv_unlock.setAdapter(adapter);
		super.onStart();
	}

	private class UnLockAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			tv_size.setText("未加锁的软件数目" + appInfos.size());
			return appInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return appInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.item_unlock_app, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
				holder.bt_unlock = (Button) view.findViewById(R.id.bt_unlock);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			final AppInfo info = (AppInfo) getItem(position);
			holder.iv_icon.setBackgroundDrawable(info.getIcon());
			holder.tv_name.setText(info.getName());
			/**
			 * 点击事件，将未加锁软件设置为加锁
			 */
			holder.bt_unlock.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 更新数据和UI
					appInfos.remove(position);
					dao.insert(info.getPackageName());
					adapter.notifyDataSetChanged();
				}
			});
			return view;
		}
	}

	private class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		Button bt_unlock;
	}
}
