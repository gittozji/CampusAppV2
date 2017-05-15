package com.zjcql.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zjcql.base.BaseActivity;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.bmob.v3.listener.SaveListener;

/** 
* 登陆Acitivity
* @author phlofy
* @date 2015年9月3日 下午2:40:57 
*/
public class LogActivity extends BaseActivity implements OnClickListener{
	Button mLog;
	EditText mEditPhone,mEditPass;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log);
		findView();
	}
	
	private void findView(){
		mEditPhone = (EditText) findViewById(R.id.one);
		mEditPass = (EditText) findViewById(R.id.two);
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		mLog = (Button) findViewById(R.id.button2);
		mLog.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch(v.getId()){
		// 去注册按钮
		case R.id.button1:{
			Intent intent = new Intent();
			intent.setClass(LogActivity.this, SignActivity.class);
			startActivity(intent);
			break;
		}
		// 登陆按钮
		case R.id.button2:{
			if(isMobile(mEditPhone.getText().toString())){
				mLog.setText("正在登陆...");
				User user = new User();
				user.setUsername(mEditPhone.getText().toString());
				user.setPassword(mEditPass.getText().toString());
				userManager.login(user, new SaveListener() {
					
					@Override
					public void onSuccess() {
						// 暂时如此操作
						updateUserInfos();
						finish();
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						mLog.setText("重新登陆");
						toast("登陆失败"+arg1);
						
					}
				});
			}
			break;
		}
		case R.id.back:{
			finish();
			break;
		}
		default: break;
		}
		
	}
	
	/**
	 * 手机号码验证
	 * @param str 手机号
	 * @return
	 */
	private boolean isMobile(String str) {   
        Pattern p = null;  
        Matcher m = null;  
        boolean b = false;   
        p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号  
        m = p.matcher(str);  
        b = m.matches();   
        if(!b)
        	toast("手机号码不合法");
        return b;  
    }  
	
}
