package com.zjcql.activity;

import com.zjcql.Interface.NotifyParent;
import com.zjcql.base.BaseActivity;
import com.zjcql.campusappv2.R;
import com.zjcql.dialog.EditDialog;
import com.zjcql.fragment.MySayFragment;
import com.zjcql.fragment.NoticeBrowseFragment;
import com.zjcql.fragment.SayBrowseFragment;
import com.zjcql.fragment.SaySendFragment;
import com.zjcql.util.SimpleTask;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/** 
* 吐槽Activity
* @author phlofy
* @date 2016年1月2日 下午8:03:06 
*/
public class SayActivity extends BaseActivity implements OnClickListener,NotifyParent{
	TextView mTitle;
	SayBrowseFragment mSayBrowseFragment;
	SaySendFragment mSaySendFragment;
	MySayFragment mMySayFragment;
	RelativeLayout my,add;
	boolean isSend = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_say);
		findView();
		mSayBrowseFragment = new SayBrowseFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame, mSayBrowseFragment).commit();
	}


	private void findView() {
		mTitle = (TextView) findViewById(R.id.bar_title);
		add = (RelativeLayout) findViewById(R.id.add);
		add.setOnClickListener(this);
		my = (RelativeLayout) findViewById(R.id.my);
		my.setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
	}
	
	private void getSayBrowseFragment(FragmentTransaction tran){
		mTitle.setText("吐槽墙");
		if(mSayBrowseFragment != null){
			tran.show(mSayBrowseFragment);
		}
		else{
			mSayBrowseFragment = new SayBrowseFragment();
			tran.add(R.id.frame,mSayBrowseFragment);
		}
		my.setVisibility(View.VISIBLE);
		add.setVisibility(View.VISIBLE);
		isSend = false;
	}
	
	private void getSaySendFragment(FragmentTransaction tran){
		mTitle.setText("发布");
		if(mSaySendFragment != null){
			tran.show(mSaySendFragment);
		}
		else{
			mSaySendFragment = new SaySendFragment();
			mSaySendFragment.setOnNotifyParentListener(this);
			tran.add(R.id.frame, mSaySendFragment);
		}
		add.setVisibility(View.GONE);
		my.setVisibility(View.GONE);
		isSend = true;
	}
	
	private void getMySayFragment(FragmentTransaction tran){
		mTitle.setText("MySay");
		if(mMySayFragment != null){
			tran.show(mMySayFragment);
		}
		else{
			mMySayFragment = new MySayFragment();
			tran.add(R.id.frame,mMySayFragment);
		}
		my.setVisibility(View.GONE);
		add.setVisibility(View.GONE);
		isSend = true;
	}
	
	private void hideFragment(FragmentTransaction tran){
		if(mSayBrowseFragment != null)
			tran.hide(mSayBrowseFragment);
		if(mSaySendFragment != null)
			tran.hide(mSaySendFragment);
		if(mMySayFragment != null)
			tran.hide(mMySayFragment);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.back:{
			if(isSend){
				FragmentTransaction tran = getFragmentManager().beginTransaction();
				hideFragment(tran);
				getSayBrowseFragment(tran);
				tran.commit();
			}
			else{
				finish();
			}
			break;
		}
		case R.id.add:{
			FragmentTransaction tran = getFragmentManager().beginTransaction();
			hideFragment(tran);
			getSaySendFragment(tran);
			tran.commit();
			break;
		}
		case R.id.my:{
			FragmentTransaction tran = getFragmentManager().beginTransaction();
			hideFragment(tran);
			getMySayFragment(tran);
			tran.commit();
			break;
		}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Fragment中无法调用onActivityResult方法，所以将该方法的数据回调给frgment
		if(isSend){
			mSaySendFragment.fakeOnActivityResult(requestCode, resultCode, data);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event) {
		if(isSend && keyCode == 4){
			FragmentTransaction tran = getFragmentManager().beginTransaction();
			hideFragment(tran);
			getSayBrowseFragment(tran);
			tran.commit();
		}
		else
			super.onKeyDown(keyCode, event);
		return true;
	}


	@Override
	public void returnCode(String string) {
		if("切换到浏览界面".equals(string)){
			FragmentTransaction tran = getFragmentManager().beginTransaction();
			hideFragment(tran);
			getSayBrowseFragment(tran);
			tran.commit();
		}
	}
	
}
