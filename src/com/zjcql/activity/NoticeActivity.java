package com.zjcql.activity;

import java.util.List;

import com.zjcql.Interface.GeneralListener;
import com.zjcql.Interface.NotifyParent;
import com.zjcql.base.BaseActivity;
import com.zjcql.bmobbean.Secret;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;
import com.zjcql.dialog.EditDialog;
import com.zjcql.fragment.NoticeBrowseFragment;
import com.zjcql.fragment.NoticeSendFragment;
import com.zjcql.fragment.TradeGoodsBrowseFragment;
import com.zjcql.fragment.TradeMyStoreFragment;
import com.zjcql.util.SimpleTask;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.FindListener;

/** 
* 通知栏的Activity
* @author phlofy
* @date 2015年10月17日 下午2:31:58 
*/
public class NoticeActivity extends BaseActivity implements OnClickListener,NotifyParent{
	TextView mTitle;
		/**
		 * 通知浏览Fragment
		 */
	NoticeBrowseFragment mNoticeBrowseFragment;
		/**
		 * 发布通知Fragment
		 */
	NoticeSendFragment mNoticeSendFragment;
	
	boolean mOticeSendFragmentFlag = false;
	
	ImageButton floatbutton;
	User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);
		findView();
		user = BmobUser.getCurrentUser(NoticeActivity.this,User.class);
		mNoticeBrowseFragment = new NoticeBrowseFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame, mNoticeBrowseFragment).commit();
		isWhiteUser(new GeneralListener() {
			
			@Override
			public void onReturn(boolean arg0, int arg1, String arg2) {
				if(arg0)
					floatbutton.setVisibility(View.VISIBLE);
				else
					floatbutton.setVisibility(View.GONE);
			}
		});
	}
	
	private void findView(){
		mTitle = (TextView) findViewById(R.id.title);
		findViewById(R.id.back).setOnClickListener(this);
		floatbutton = (ImageButton) findViewById(R.id.floatbutton);
		floatbutton.setOnClickListener(this);
		floatbutton.setVisibility(View.GONE);
	}
	
	private void getNoticeBrowseFragment (FragmentTransaction tran){
		mTitle.setText("公告栏");
		isWhiteUser(new GeneralListener() {
			
			@Override
			public void onReturn(boolean arg0, int arg1, String arg2) {
				if(arg0)
					floatbutton.setVisibility(View.VISIBLE);
				else
					floatbutton.setVisibility(View.GONE);
			}
		});
		if(mNoticeBrowseFragment != null){
			tran.show(mNoticeBrowseFragment);
		}
		else{
			mNoticeBrowseFragment = new NoticeBrowseFragment();
			tran.add(R.id.frame, mNoticeBrowseFragment);
		}
	}
	
	private void getNoticeSendFragment (FragmentTransaction tran){
		mTitle.setText("发布公告");
		floatbutton.setVisibility(View.GONE);
		if(mNoticeSendFragment != null){
			tran.show(mNoticeSendFragment);
		}
		else{
			mNoticeSendFragment = new NoticeSendFragment();
			mNoticeSendFragment.setOnNotifyParentListener(this);
			tran.add(R.id.frame, mNoticeSendFragment);
		}
		mOticeSendFragmentFlag = true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Fragment中无法调用onActivityResult方法，所以将该方法的数据回调给frgment
		if(mNoticeSendFragment != null){
			mNoticeSendFragment.fakeOnActivityResult(requestCode, resultCode, data);
		}
	}
	

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.back:{
			if(mOticeSendFragmentFlag){
				returnCode("切换到浏览界面");
			}
			else{
				finish();
			}
			break;
		}
		case R.id.floatbutton:{
			if(true){
				FragmentTransaction tran = getFragmentManager().beginTransaction();
				hideFragment(tran);
				getNoticeSendFragment(tran);
				tran.commit();
				
			}
		}
		default:break;
		}
		
	}

	private void hideFragment(FragmentTransaction tran) {
		if(mNoticeBrowseFragment != null)
			tran.hide(mNoticeBrowseFragment);
		if(mNoticeSendFragment != null)
			tran.hide(mNoticeSendFragment);
		mOticeSendFragmentFlag = false;
	}

	@Override
	public void returnCode(String string) {
		if("切换到浏览界面".equals(string)){
			FragmentTransaction tran = getFragmentManager().beginTransaction();
			hideFragment(tran);
			getNoticeBrowseFragment(tran);
			tran.commit();
			return;
		}
		if("关闭Activity".equals(string)){
			finish();
			return;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event) {
		if(mOticeSendFragmentFlag && keyCode == 4){
			returnCode("切换到浏览界面");
		}
		else
			super.onKeyDown(keyCode, event);
		return true;
	}
	/**
	 * 判断是否有权限发布公告
	 * @param gl 返回ture既有，false为无
	 */
	private void isWhiteUser(final GeneralListener gl){
		if(user == null){
			gl.onReturn(false, 0, null);
			return;
		}
		BmobQuery<Secret> query = new BmobQuery<Secret>();
		query.addWhereEqualTo("user", user);
		query.findObjects(NoticeActivity.this, new FindListener<Secret>() {
			
			@Override
			public void onSuccess(List<Secret> arg0) {
				if(arg0.size() > 0){
					gl.onReturn(true, 0, null);
				}
				else{
					gl.onReturn(false, 0, null);
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				gl.onReturn(false, 0, null);
			}
		});
	}
}
