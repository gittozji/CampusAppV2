package com.zjcql.fragment;

import com.zjcql.adapter.ReplyChatAdapter;
import com.zjcql.base.BaseFragemntv4;
import com.zjcql.campusappv2.R;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.im.db.BmobDB;

/** 
* 会话Fragemnt
* @author phlofy
* @date 2015年12月5日 下午4:56:31 
*/
public class ReplyFragment extends BaseFragemntv4{
	View mRootView;
	ListView listView;
	ReplyChatAdapter adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		mRootView = inflater.inflate(R.layout.fragment_reply_list, null);
		findView();
		queryReply();
		return mRootView;
	}

	private void findView() {
		listView = (ListView) mRootView.findViewById(R.id.listview);
	}
	private void queryReply() {
		adapter = new ReplyChatAdapter(getActivity(), BmobDB.create(getActivity()).queryRecents());
		listView.setAdapter(adapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		queryReply();
	}
	
}
