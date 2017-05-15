package com.zjcql.activity;

import com.zjcql.Interface.GeneralListener;
import com.zjcql.Interface.NotifyParent;
import com.zjcql.base.BaseActivity;
import com.zjcql.campusappv2.R;
import com.zjcql.fragment.AnswerBrowseFragment;
import com.zjcql.fragment.AnswerSendFragment;
import com.zjcql.fragment.MyAnswerFragment;
import com.zjcql.fragment.MySayFragment;
import com.zjcql.fragment.SayBrowseFragment;
import com.zjcql.fragment.SaySendFragment;
import com.zjcql.popup.PullSeekPopup;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

/** 
* 问答控制层
* @author phlofy
* @date 2016年1月25日 下午8:18:40 
*/
public class AnswerActivity extends BaseActivity implements OnClickListener,NotifyParent{
	
	public static String SEEK_FIELD = ""; 
	
	TextView mTitle;
	AnswerBrowseFragment mAnswerBrowseFragment;
	AnswerSendFragment mAnswerSendFragment;
	MyAnswerFragment mMyAnswerFragment;
	RelativeLayout my,add,seek;
	boolean isSend = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_answer);
		findView();
		mTitle.setText("问答");
		mAnswerBrowseFragment = new AnswerBrowseFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame, mAnswerBrowseFragment).commit();
	}


	private void findView() {
		mTitle = (TextView) findViewById(R.id.bar_title);
		add = (RelativeLayout) findViewById(R.id.add);
		add.setOnClickListener(this);
		seek = (RelativeLayout) findViewById(R.id.seek);
		seek.setOnClickListener(this);
		my = (RelativeLayout) findViewById(R.id.my);
		my.setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
	}
	
	private void getAnswerBrowseFragment(FragmentTransaction tran){
		mTitle.setText("问答");
		if(mAnswerBrowseFragment != null){
			tran.show(mAnswerBrowseFragment);
		}
		else{
			mAnswerBrowseFragment = new AnswerBrowseFragment();
			tran.add(R.id.frame,mAnswerBrowseFragment);
		}
		my.setVisibility(View.VISIBLE);
		add.setVisibility(View.VISIBLE);
		seek.setVisibility(View.VISIBLE);
		isSend = false;
	}
	
	private void getAnswerSendFragment(FragmentTransaction tran){
		mTitle.setText("发布");
		if(mAnswerSendFragment != null){
			tran.show(mAnswerSendFragment);
		}
		else{
			mAnswerSendFragment = new AnswerSendFragment();
			mAnswerSendFragment.setOnNotifyParentListener(this);
			tran.add(R.id.frame, mAnswerSendFragment);
		}
		add.setVisibility(View.GONE);
		my.setVisibility(View.GONE);
		seek.setVisibility(View.GONE);
		isSend = true;
	}
	
	private void getMyAnswerFragment(FragmentTransaction tran){
		mTitle.setText("My提问");
		if(mMyAnswerFragment != null){
			tran.show(mMyAnswerFragment);
		}
		else{
			mMyAnswerFragment = new MyAnswerFragment();
			tran.add(R.id.frame,mMyAnswerFragment);
		}
		my.setVisibility(View.GONE);
		add.setVisibility(View.GONE);
		seek.setVisibility(View.GONE);
		isSend = true;
	}
	
	private void hideFragment(FragmentTransaction tran){
		if(mAnswerBrowseFragment != null)
			tran.hide(mAnswerBrowseFragment);
		if(mAnswerSendFragment != null)
			tran.hide(mAnswerSendFragment);
		if(mMyAnswerFragment != null)
			tran.hide(mMyAnswerFragment);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.back:{
			if(isSend){
				FragmentTransaction tran = getFragmentManager().beginTransaction();
				hideFragment(tran);
				getAnswerBrowseFragment(tran);
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
			getAnswerSendFragment(tran);
			tran.commit();
			break;
		}
		case R.id.my:{
			FragmentTransaction tran = getFragmentManager().beginTransaction();
			hideFragment(tran);
			getMyAnswerFragment(tran);
			tran.commit();
			break;
		}
		case R.id.seek:{
			PullSeekPopup psk = new PullSeekPopup(AnswerActivity.this, seek);
			psk.setGeneralListener(new GeneralListener() {
				
				@Override
				public void onReturn(boolean arg0, int arg1, String arg2) {
					FragmentTransaction tran = getFragmentManager().beginTransaction();
					hideFragment(tran);
					getAnswerBrowseFragment(tran);
					tran.commit();
					mAnswerBrowseFragment.firstRefresh();
				}
			});
			psk.show();
			break;
		}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Fragment中无法调用onActivityResult方法，所以将该方法的数据回调给frgment
		if(isSend){
			mAnswerSendFragment.fakeOnActivityResult(requestCode, resultCode, data);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event) {
		if(isSend && keyCode == 4){
			FragmentTransaction tran = getFragmentManager().beginTransaction();
			hideFragment(tran);
			getAnswerBrowseFragment(tran);
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
			getAnswerBrowseFragment(tran);
			tran.commit();
		}
	}
}
