package com.itheima.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.db.dao.BlackNumberDao;
import com.itheima.mobilesafe.domain.BlackNumberInfo;
import com.itheima.mobilesafe.utils.LogUtil;

/**
 * 黑名单列表界面
 * 
 * @author YOXIN
 * 
 */
public class BlackNumberActivity extends Activity {

	private static final String TAG = "BlackNumberActivity";
	private static final int LIMIT = 20;// ListView每次查询的数据总数

	ListView list_black_number;
	List<BlackNumberInfo> data;
	BlackNumberDao dao;
	BlackNumberAdapter adapter;
	private Context context;
	private LinearLayout ll_loading;
	private int offset;// 查询的起始下标
	private int lastPosition;// ListView最后的子项的标号

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_number);
		context = this;
		list_black_number = (ListView) findViewById(R.id.list_black_number);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		dao = new BlackNumberDao(this);
		ll_loading.setVisibility(View.VISIBLE);
		findData();
		list_black_number.setOnScrollListener(new OnScrollListener() {

			/**
			 * 监听滑动状态
			 */
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case SCROLL_STATE_IDLE: // 空闲时
					if (view.getLastVisiblePosition() == offset - 1) {
						ll_loading.setVisibility(View.VISIBLE);
						findData();
					}
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}

	private void findData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				final List<BlackNumberInfo> result = dao.findAll(20, offset);// 查询前20条记录
				offset += LIMIT;
				runOnUiThread(new Runnable() {
					public void run() {
						ll_loading.setVisibility(View.INVISIBLE);
						if (data == null) {
							data = result;
							adapter = new BlackNumberAdapter(context,
									R.layout.blacknumber_item_view, data);
							list_black_number.setAdapter(adapter);
						} else {
							data.addAll(result);
							adapter.notifyDataSetChanged();
						}
					}
				});
			}
		}).start();
	}

	/**
	 * 自定义ListView设配器
	 * 
	 * @author YOXIN
	 * 
	 */
	class BlackNumberAdapter extends ArrayAdapter<BlackNumberInfo> {

		private int resourceId;

		public BlackNumberAdapter(Context context, int textViewResourceId,
				List<BlackNumberInfo> objects) {
			super(context, textViewResourceId, objects);
			resourceId = textViewResourceId;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			BlackNumberInfo blackNumberInfo = getItem(position);
			final String number = blackNumberInfo.getNumber();
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
				viewHolder.delete = (ImageView) view
						.findViewById(R.id.iv_black_delete);
				viewHolder.layout = (LinearLayout) view
						.findViewById(R.id.item_black);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}
			viewHolder.number.setText(number);
			final String mode = blackNumberInfo.getMode();
			if ("1".equals(mode)) {
				viewHolder.mode.setText("电话拦截");
			} else if ("2".equals(mode)) {
				viewHolder.mode.setText("短信拦截");
			} else if ("3".equals(mode)) {
				viewHolder.mode.setText("全部拦截");
			}
			/**
			 * 点击事件，删除黑名单
			 */
			viewHolder.delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 创建警告窗口，确认是否删除黑名单
					AlertDialog.Builder builder = new AlertDialog.Builder(
							BlackNumberActivity.this);
					builder.setTitle("删除黑名单联系人")
							.setMessage("是否删除黑名单联系人")
							.setPositiveButton("确定",
									new AlertDialog.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// 在数据库里删除数据
											dao.delete(number);
											// 在listview数据中删除数据
											data.remove(position);
											// 通知adapter更改数据
											adapter.notifyDataSetChanged();
										}
									}).setNegativeButton("取消", null).create()
							.show();
				}
			});
			/**
			 * listView子项布局长按点击事件：修改黑名单联系人
			 */
			view.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							BlackNumberActivity.this);
					final AlertDialog dialog = builder.create();
					View contentView = View.inflate(BlackNumberActivity.this,
							R.layout.dialog_add_black_number, null);
					tv_title = (TextView) contentView
							.findViewById(R.id.tv_title);
					et_black_phone = (EditText) contentView
							.findViewById(R.id.et_black_phone);
					cb_phone = (CheckBox) contentView
							.findViewById(R.id.cb_phone);
					cb_sms = (CheckBox) contentView.findViewById(R.id.cb_sms);
					ok = (Button) contentView.findViewById(R.id.ok);
					cancel = (Button) contentView.findViewById(R.id.cancel);
					tv_title.setText("修改黑名单");
					et_black_phone.setText(number);
					if ("1".equals(mode)) {
						cb_phone.setChecked(true);
					} else if ("2".equals(mode)) {
						cb_sms.setChecked(true);
					} else if ("3".equals(mode)) {
						cb_phone.setChecked(true);
						cb_sms.setChecked(true);
					}
					dialog.setView(contentView, 0, 0, 0, 0);
					dialog.show();
					ok.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							String NewNumber = et_black_phone.getText()
									.toString().trim();
							if (number.isEmpty()) {
								Toast.makeText(BlackNumberActivity.this,
										"请输入号码", Toast.LENGTH_LONG).show();
								return;
							}
							String NewMode = null;
							if (cb_phone.isChecked() && cb_sms.isChecked()) {// 全部拦截
								NewMode = "3";
							} else if (cb_phone.isChecked()) {// 手机拦截
								NewMode = "1";
							} else if (cb_sms.isChecked()) {// 短信拦截
								NewMode = "2";
							} else {
								Toast.makeText(BlackNumberActivity.this,
										"请选择模式", Toast.LENGTH_LONG).show();
								return;
							}
							// 在数据库修改数据
							Integer id = data.get(position).getId();
							LogUtil.d(TAG, "" + id);
							dao.updateById(id, NewNumber, NewMode);
							// 在listView修改数据
							data.get(position).setMode(NewMode);
							data.get(position).setNumber(NewNumber);
							// 通知Adapter数据更新
							adapter.notifyDataSetChanged();
							// 关闭窗口
							dialog.dismiss();
						}
					});
					cancel.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					return true;
				}
			});
			return view;
		}

		class ViewHolder {
			LinearLayout layout;
			TextView number;
			TextView mode;
			ImageView delete;
		}
	}

	private EditText et_black_phone;
	private CheckBox cb_phone;
	private CheckBox cb_sms;
	private Button ok;
	private Button cancel;
	private TextView tv_title;

	/**
	 * 点击事件，增加黑名单号码
	 * 
	 * @param view
	 */
	public void addBlackNumber(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View contentView = View.inflate(this, R.layout.dialog_add_black_number,
				null);
		et_black_phone = (EditText) contentView
				.findViewById(R.id.et_black_phone);
		cb_phone = (CheckBox) contentView.findViewById(R.id.cb_phone);
		cb_sms = (CheckBox) contentView.findViewById(R.id.cb_sms);
		ok = (Button) contentView.findViewById(R.id.ok);
		cancel = (Button) contentView.findViewById(R.id.cancel);
		dialog.setView(contentView, 0, 0, 0, 0);
		dialog.show();
		ok.setOnClickListener(new OnClickListener() {

			/**
			 * 添加黑名单
			 */
			@Override
			public void onClick(View v) {
				String number = et_black_phone.getText().toString().trim();
				if (number.isEmpty()) {
					Toast.makeText(BlackNumberActivity.this, "请输入号码",
							Toast.LENGTH_LONG).show();
					return;
				}
				String mode = null;
				if (cb_phone.isChecked() && cb_sms.isChecked()) {// 全部拦截
					mode = "3";
				} else if (cb_phone.isChecked()) {// 手机拦截
					mode = "1";
				} else if (cb_sms.isChecked()) {// 短信拦截
					mode = "2";
				} else {
					Toast.makeText(BlackNumberActivity.this, "请选择模式",
							Toast.LENGTH_LONG).show();
					return;
				}
				// 在数据库插入数据
				dao.add(number, mode);
				// 在listView插入数据
				BlackNumberInfo info = new BlackNumberInfo(number, mode);
				data.add(0, info);
				// 通知Adapter数据更新
				adapter.notifyDataSetChanged();
				// 关闭窗口
				dialog.dismiss();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
}
