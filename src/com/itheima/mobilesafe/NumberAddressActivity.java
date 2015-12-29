package com.itheima.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.db.dao.NumberAddressQueryUtils;

public class NumberAddressActivity extends Activity {
	
	private EditText edt_phone;
	private TextView txt_address;
	private Button btn_query;
	/**
	 * 手机震动服务
	 */
	private Vibrator vibrator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_address);
		edt_phone = (EditText)findViewById(R.id.edt_phone);
		txt_address = (TextView)findViewById(R.id.txt_address);
		btn_query = (Button)findViewById(R.id.btn_qurey);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		btn_query.setOnClickListener(new QueryOnClickListener());
		edt_phone.addTextChangedListener(new TextWatcher() {
			//当文本发生变化的时候调
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length()>=3){
					String address = NumberAddressQueryUtils.queryNumber(s.toString());
					txt_address.setText(address);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	/**
	 * 查询号码归属地
	 * @author YOXIN
	 *
	 */
	class QueryOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			String phone = edt_phone.getText().toString().trim();
			if (TextUtils.isEmpty(phone)) {
				Animation shake = AnimationUtils.loadAnimation(NumberAddressActivity.this, R.anim.shake);
				edt_phone.startAnimation(shake);
				//震动两秒
				//vibrator.vibrate(2000);
				//震动规律
				//-1不重复，非-1为从pattern的指定下标开始重复
				long[] pattern = {200,200,1000,1000};
				vibrator.vibrate(pattern, -1);
				Toast.makeText(NumberAddressActivity.this, "手机号为空", Toast.LENGTH_LONG).show();
			} else {
				String address = NumberAddressQueryUtils.queryNumber(phone);
				txt_address.setText(address);
			}
		}
	}
	
}
