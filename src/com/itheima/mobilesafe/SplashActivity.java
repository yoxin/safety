package com.itheima.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.itheima.mobilesafe.db.dao.AntivirusQueryUtils;
import com.itheima.mobilesafe.domain.Virus;
import com.itheima.mobilesafe.service.AddressService;
import com.itheima.mobilesafe.service.CallSmsSafeService;
import com.itheima.mobilesafe.utils.LogUtil;
import com.itheima.mobilesafe.utils.StreamTools;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class SplashActivity extends Activity {

	protected static final String TAG = "SplashActivity";
	protected static final int SHOW_UPDATE_DIALOG = 0;
	protected static final int ENTER_HOME = 1;
	protected static final int URL_ERROR = 2;
	protected static final int NETWORK_ERROR = 3;
	protected static final int JSON_ERROR = 4;
	private TextView tv_splash_version;
	private String description;
	private TextView tv_update_info;
	/**
	 * 新版本的下载地址
	 */
	private String apkurl;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("版本号" + getVersionName());
		tv_update_info = (TextView) findViewById(R.id.tv_update_info);
		// 创建快捷图标
		installShortcut();
		// 将assets目录下的数据库拷贝到data/data/com.itheima.mobilesafe/files目录下
		copyDB("address.db");
		copyDB("antivirus.db");
		udataVirus(); //更新病毒数据库
		
		// 开启地址归属地显示服务
		boolean showAddress = sp.getBoolean("showAddress", false);
		if (showAddress) {
			Intent intent = new Intent(this, AddressService.class);
			startService(intent);
		}
		// 开启黑名单拦截服务
		boolean isCallSmsSafe = sp.getBoolean("callSmsSafe", false);
		if (isCallSmsSafe) {
			Intent intent = new Intent(this, CallSmsSafeService.class);
			startService(intent);
		}
		boolean update = sp.getBoolean("update", false);
		if (update) {
			// 检查升级
			checkUpdate();
		} else {
			// 自动升级已经关闭
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// 进入主页面
					enterHome();

				}
			}, 2000);

		}

		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(500);
		findViewById(R.id.rl_root_splash).startAnimation(aa);
	}

	/**
	 * 更新病毒数据库
	 */
	private void udataVirus() {
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.GET, getString(R.string.url_updata_virus), new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg) {
				LogUtil.d(TAG, arg.result.toString());
				List<Virus> VirusInfos = JSON.parseArray(arg.result.toString(), Virus.class);
				for (Virus virus : VirusInfos) {
					LogUtil.d(TAG, virus.toString());
					if (AntivirusQueryUtils.addVirus(virus)) {
						LogUtil.d(TAG, "成功插入病毒数据");
					} else {
						LogUtil.d(TAG, "失败插入病毒数据");
					}
				}
				
			}

			
		});
	}

	/**
	 * 创建桌面图标
	 * 注意添加权限
	 */
	private void installShortcut() {
		/*
		 * <receiver
		 * android:name="com.android.launcher2.InstallShortcutReceiver"
		 * android:permission
		 * ="com.android.launcher.permission.INSTALL_SHORTCUT"> <intent-filter>
		 * <action android:name="com.android.launcher.action.INSTALL_SHORTCUT"
		 * /> </intent-filter> </receiver>
		 */
		if (sp.getBoolean("shortCut", false)) {// 防止多次创建
			return;
		}
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory
				.decodeResource(getResources(), R.drawable.ic_launcher));
		Intent shortCutIntent = new Intent();
		shortCutIntent.setAction(Intent.ACTION_MAIN);
		shortCutIntent.addCategory(Intent.CATEGORY_DEFAULT);
		shortCutIntent.setClassName(getPackageName(),
				"com.itheima.mobilesafe.SplashActivity");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
		sendBroadcast(intent);
		Editor editor = sp.edit();
		editor.putBoolean("shortCut", true);
		editor.commit();
	}

	/**
	 * 将assets目录下的数据库拷贝到data/data/com.itheima.mobilesafe/files目录下
	 */
	private void copyDB(String name) {
		try {
			File file = new File(getFilesDir(), name);
			if (file.exists() && file.length() > 0) {
				Log.e(TAG, "数据库文件"+name+"只需要拷贝一下，如果拷贝了，不需要重新拷贝了");
			} else {
				// 数据库文件只需要拷贝一下，如果拷贝了，不需要重新拷贝了。
				AssetManager am = getAssets();
				InputStream is = am.open(name);
				// 创建一个文件/data/data/包名/files/address.db
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SHOW_UPDATE_DIALOG:// 显示升级的对话框
				Log.i(TAG, "显示升级的对话框");
				showUpdateDialog();
				break;
			case ENTER_HOME:// 进入主页面
				enterHome();
				break;

			case URL_ERROR:// URL错误
				enterHome();
				Toast.makeText(getApplicationContext(), "URL错误", 0).show();

				break;

			case NETWORK_ERROR:// 网络异常
				enterHome();
				Toast.makeText(SplashActivity.this, "网络异常", 0).show();
				break;

			case JSON_ERROR:// JSON解析出错
				enterHome();
				Toast.makeText(SplashActivity.this, "JSON解析出错", 0).show();
				break;

			default:
				break;
			}
		}

	};

	/**
	 * 检查是否有新版本，如果有就升级
	 */
	private void checkUpdate() {

		new Thread() {
			public void run() {
				// URLhttp://192.168.1.254:8080/updateinfo.html

				Message mes = Message.obtain();
				long startTime = System.currentTimeMillis();
				try {

					URL url = new URL(getString(R.string.serverurl));
					// 联网
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(4000);
					int code = conn.getResponseCode();
					if (code == 200) {
						// 联网成功
						InputStream is = conn.getInputStream();
						// 把流转成String
						String result = StreamTools.readFromStream(is);
						Log.i(TAG, "联网成功了" + result);
						// json解析
						JSONObject obj = new JSONObject(result);
						// 得到服务器的版本信息
						String version = (String) obj.get("version");

						description = (String) obj.get("description");
						apkurl = (String) obj.get("apkurl");

						// 校验是否有新版本
						if (getVersionName().equals(version)) {
							// 版本一致，没有新版本，进入主页面
							mes.what = ENTER_HOME;
						} else {
							// 有新版本，弹出一升级对话框
							mes.what = SHOW_UPDATE_DIALOG;

						}

					}

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					mes.what = URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					mes.what = NETWORK_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mes.what = JSON_ERROR;
				} finally {

					long endTime = System.currentTimeMillis();
					// 我们花了多少时间
					long dTime = endTime - startTime;
					// 2000
					if (dTime < 2000) {
						try {
							Thread.sleep(2000 - dTime);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					handler.sendMessage(mes);
				}

			};
		}.start();

	}

	/**
	 * 弹出升级对话框
	 */
	protected void showUpdateDialog() {
		// this = Activity.this
		AlertDialog.Builder builder = new Builder(SplashActivity.this);
		builder.setTitle("提示升级");
		// builder.setCancelable(false);//强制升级
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				// 进入主页面
				enterHome();
				dialog.dismiss();

			}
		});
		builder.setMessage(description);
		builder.setPositiveButton("立刻升级", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 下载APK，并且替换安装
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// sdcard存在
					// afnal
					FinalHttp finalhttp = new FinalHttp();
					finalhttp.download(apkurl, Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/mobilesafe2.0.apk", new AjaxCallBack<File>() {

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							t.printStackTrace();
							Toast.makeText(getApplicationContext(), "下载失败", 1)
									.show();
							super.onFailure(t, errorNo, strMsg);
						}

						@Override
						public void onLoading(long count, long current) {
							// TODO Auto-generated method stub
							super.onLoading(count, current);
							tv_update_info.setVisibility(View.VISIBLE);
							// 当前下载百分比
							int progress = (int) (current * 100 / count);
							tv_update_info.setText("下载进度：" + progress + "%");
						}

						@Override
						public void onSuccess(File t) {
							// TODO Auto-generated method stub
							super.onSuccess(t);
							installAPK(t);
						}

						/**
						 * 安装APK
						 * 
						 * @param t
						 */
						private void installAPK(File t) {
							Intent intent = new Intent();
							intent.setAction("android.intent.action.VIEW");
							intent.addCategory("android.intent.category.DEFAULT");
							intent.setDataAndType(Uri.fromFile(t),
									"application/vnd.android.package-archive");

							startActivity(intent);

						}

					});
				} else {
					Toast.makeText(getApplicationContext(), "没有sdcard，请安装上在试",
							0).show();
					return;
				}

			}
		});
		builder.setNegativeButton("下次再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				enterHome();// 进入主页面
			}
		});
		builder.show();

	}

	protected void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		// 关闭当前页面
		finish();

	}

	/**
	 * 得到应用程序的版本名称
	 */

	private String getVersionName() {
		// 用来管理手机的APK
		PackageManager pm = getPackageManager();

		try {
			// 得到知道APK的功能清单文件
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

	}

}
