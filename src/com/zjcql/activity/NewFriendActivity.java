package com.zjcql.activity;

import com.zjcql.adapter.NewFriendAdapter;
import com.zjcql.base.BaseActivity;
import com.zjcql.campusappv2.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import cn.bmob.im.db.BmobDB;

/** 
* 新好友Activity
* @author phlofy
* @date 2015年12月12日 下午7:31:08 
*/
public class NewFriendActivity extends BaseActivity{
	ListView mList;
	NewFriendAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_friend);
		findView();
		mAdapter = new NewFriendAdapter(NewFriendActivity.this, BmobDB.create(this).queryBmobInviteList());
		mList.setAdapter(mAdapter);
	}
	private void findView(){
		mList = (ListView) findViewById(R.id.listview);
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
