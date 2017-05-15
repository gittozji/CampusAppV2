package com.zjcql.activity;

import java.util.ArrayList;

import com.zjcql.base.BaseActivity;
import com.zjcql.base.BaseFragemntv4;
import com.zjcql.base.BaseFragmentActivity;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;
import com.zjcql.fragment.FriendListFragment;
import com.zjcql.fragment.ReplyAnswerFragment;
import com.zjcql.fragment.ReplyFragment;
import com.zjcql.fragment.TradeMyStoreFragment;
import com.zjcql.rebuildview.CircleImageView;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

/** 
* 回答问题控制层
* @author phlofy
* @date 2016年1月27日 上午11:41:35 
*/
public class ReplyAnswerActiviy extends BaseActivity{
	/**
	 * 我的微店Fragment
	 */
	ReplyAnswerFragment mReplyAnswerFragment;
	TextView title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reply_answer);
		mReplyAnswerFragment = new ReplyAnswerFragment();
		Intent in = getIntent();
		Bundle bundle = in.getExtras();
		mReplyAnswerFragment.setArguments(bundle);
		mReplyAnswerFragment.setIcon((CircleImageView)(findViewById(R.id.icon)));
		getFragmentManager().beginTransaction().replace(R.id.frame, mReplyAnswerFragment).commit();
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
}