package com.zjcql.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zjcql.base.BaseActivity;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

/** 
* 用户注册界面。本应用中我希望只有通过手机号注册，并且 手机号码是账号
* @author phlofy
* @date 2015年9月2日 下午3:57:15 
*/
public class SignActivity extends BaseActivity implements OnClickListener{
	MyCountTimer timer;
	Button mObtainCode;
	EditText mEditPhone,mEditVerify,mEditPass,mEditPassAgain;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign);
		findView();
	}
	
	private void findView(){
		mEditPhone = (EditText) findViewById(R.id.one);
		mEditVerify = (EditText) findViewById(R.id.four);
		mEditPass = (EditText) findViewById(R.id.two);
		mEditPassAgain = (EditText) findViewById(R.id.three);
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.button2).setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		mObtainCode = (Button) findViewById(R.id.button3);
		mObtainCode.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		// 去登陆按钮
		case R.id.button1:{
			finish();
			break;
		}
		// 获取验证码按钮
		case R.id.button3:{
			if(isMobile(mEditPhone.getText().toString())){
				// 手机号码合理，请求发送验证码
				BmobSMS.requestSMSCode(SignActivity.this, mEditPhone.getText().toString(), "用户注册",new RequestSMSCodeListener() {
				    @Override
				    public void done(Integer smsId,BmobException ex) {
				        if(ex==null){
				        	//验证码获取成功
				        	timer = new MyCountTimer(300000, 1000);   
							timer.start();
				        }
				        else{
				        	toast("验证码获取失败");
				        	System.out.println(ex.getLocalizedMessage());
				        }
				    }
				});
			}
			break;
		}
		// 注册按钮
		case R.id.button2:{
			// 如果密码验证通过、手机号码验证通过、验证码不为空
			if(isCheck(mEditPass.getText().toString(), mEditPassAgain.getText().toString()) && isMobile(mEditPhone.getText().toString()) && isVerify(mEditVerify.getText().toString())){
				// 从服务器验证手机号和验证码
	        	BmobSMS.verifySmsCode(this,mEditPhone.getText().toString(), mEditVerify.getText().toString(), new VerifySMSCodeListener() {

	        	    @Override
	        	    public void done(BmobException ex) {
	        	        // TODO Auto-generated method stub
	        	        if(ex==null){//短信验证码已验证成功
	        	        	final User user = new User();
	        				user.setMobilePhoneNumber(mEditPhone.getText().toString());
	        				user.setMobilePhoneNumberVerified(true);
	        	        	user.setUsername(mEditPhone.getText().toString());
	        	        	user.setPassword(mEditPass.getText().toString());
	        	        	
	        	        	// 聊天组件新增加的
	        	        	user.setDeviceType("android");
	        	        	user.setInstallId(BmobInstallation.getInstallationId(SignActivity.this));
	        	        	
	        	        	user.signUp(SignActivity.this, new SaveListener() {
								@Override
								public void onSuccess() {
									// 聊天组件新增加的
									userManager.bindInstallationForRegister(user.getUsername());
									
									toast("成功注册");
									finish();
									
								}
								
								@Override
								public void onFailure(int arg0, String arg1) {

									toast("注册失败"+arg1);
									
								}
							});
	        	            System.out.println("验证通过");
	        	        }else{
	        	            toast("验证码错误");
	        	        }
	        	    }
	        	});
	        	
	        	
	        	
			}
			break;
		}
		case R.id.back:{
			finish();
			break;
		}
		default:break;
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
	/**
	 * 匹配两次密码输入是否正确，并判断该密码长度是否在4到16个字符之间
	 * @param pass1
	 * @param pass2
	 * @return
	 */
	private boolean isCheck(String pass1, String pass2){
		if(pass1.equals(pass2)){
			if(pass1.length() >= 4 && pass1.length() <= 16){
				return true;
			}
			else{
				toast("密码长度应在4-16字符之间");
			}
		}
		else{
			toast("两次密码不匹配");
		}
		return false;
	}
	
	private boolean isVerify(String verify) {
		if(verify.length() > 0){
			return true;
		}
		toast("验证码还未填写");
		return false;
	}
	/**
	 * 倒计时
	 * @author Bmob
	 *
	 */
	class MyCountTimer extends CountDownTimer{  
		  
        public MyCountTimer(long millisInFuture, long countDownInterval) {  
            super(millisInFuture, countDownInterval);  
            mObtainCode.setClickable(false);
        }  
		@Override  
        public void onTick(long millisUntilFinished) {  
			mObtainCode.setText((millisUntilFinished / 1000) +"秒后重发");  
        }  
        @Override  
        public void onFinish() {  
        	mObtainCode.setText("重新获取验证码"); 
        	mObtainCode.setClickable(true);
        }  
    }
	
}	
