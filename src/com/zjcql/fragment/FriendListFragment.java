package com.zjcql.fragment;

import java.util.Map;

import com.zjcql.activity.NewFriendActivity;
import com.zjcql.adapter.FriendListAdapter;
import com.zjcql.base.BaseFragemntv4;
import com.zjcql.base.MyApplication;
import com.zjcql.campusappv2.R;
import com.zjcql.util.CollectionUtils;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.push.a.in;

/** 
* 好友列表Fragment
* @author phlofy
* @date 2015年12月5日 下午4:58:29 
*/
public class FriendListFragment extends BaseFragemntv4 implements OnClickListener{
	
	View mRootView;
	ListView mListView;
	ImageView hasNew;
	FriendListAdapter mAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		mRootView = inflater.inflate(R.layout.fragment_friend_list, null);
		findView();
		queryMyfriends();
		return mRootView;
	}

	private void findView() {
		mRootView.findViewById(R.id.lay_bottom).setOnClickListener(this);
		mListView = (ListView) mRootView.findViewById(R.id.listview);
		hasNew = (ImageView) mRootView.findViewById(R.id.has_new);
	}

	/** 获取好友列表
	  * queryMyfriends
	  * @return void
	  * @throws
	  */
	private void queryMyfriends() {
		//是否有新的好友请求
		if(BmobDB.create(getActivity()).hasNewInvite()){
			hasNew.setVisibility(View.VISIBLE);
		}else{
			hasNew.setVisibility(View.GONE);
		}
		//在这里再做一次本地的好友数据库的检查，是为了本地好友数据库中已经添加了对方，但是界面却没有显示出来的问题
		// 重新设置下内存中保存的好友列表
		MyApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(getActivity()).getContactList()));
	
		Map<String,BmobChatUser> users = MyApplication.getInstance().getContactList();

		if(mAdapter == null ||mAdapter != null){
			mAdapter = new FriendListAdapter(getActivity(), CollectionUtils.map2list(users));
			mListView.setAdapter(mAdapter);
		}else{
			mAdapter.notifyDataSetChanged();
		}

	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.lay_bottom:{
			Intent in = new Intent(getActivity(),NewFriendActivity.class);
			startActivity(in);
			break;
		}
		}
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		queryMyfriends();
		System.out.println("res");
		super.onResume();
	}
	
}
