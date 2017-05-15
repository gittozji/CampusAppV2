package com.zjcql.activity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.zjcql.base.BaseFragemntv4;
import com.zjcql.base.BaseFragmentActivity;
import com.zjcql.campusappv2.R;
import com.zjcql.fragment.FriendListFragment;
import com.zjcql.fragment.ReplyFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
/**
 * 社交圈Activity
 * @author phlofy
 *
 */
@SuppressLint("NewApi")
public class ContactActivity extends BaseFragmentActivity implements OnClickListener{
	ViewPager pager;
	ArrayList<BaseFragemntv4> fragmentList;
	ArrayList<String> titleList;
	ReplyFragment replyFragment;
	FriendListFragment friendListFragment;
	PagerTabStrip strip;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		findView();
		strip.setTabIndicatorColorResource(R.color.white);
		replyFragment = new ReplyFragment();
		friendListFragment = new FriendListFragment();
		fragmentList = new ArrayList<BaseFragemntv4>();
		fragmentList.add(replyFragment);
		fragmentList.add(friendListFragment);
		titleList = new ArrayList<String>();
		titleList.add("会话");
		titleList.add("好友");
		pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
			
			@Override
			public int getCount() {
				return titleList.size();
			}
			
			@Override
			public Fragment getItem(int arg0) {
				return fragmentList.get(arg0);
			}
			@Override
			public CharSequence getPageTitle(int position) {
				SpannableStringBuilder ssb = new SpannableStringBuilder(" "+titleList.get(position)+" ");
				ForegroundColorSpan fcs = new ForegroundColorSpan(Color.WHITE);
				ssb.setSpan(fcs, 1, ssb.length(),  Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				return ssb;
			}
		});
	}
	private void findView(){
		pager = (ViewPager) findViewById(R.id.viewpager);
		strip = (PagerTabStrip) findViewById(R.id.pagertab);
		findViewById(R.id.add).setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.add:{
			Intent in = new Intent(ContactActivity.this, AddFriendActivity.class);
			startActivity(in);
			break;
		}
		case R.id.back:{
			finish();
		}
		}
	}
}
