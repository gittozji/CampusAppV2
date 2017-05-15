package com.zjcql.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zjcql.Interface.OnLoad;
import com.zjcql.Interface.OnRefresh;
import com.zjcql.adapter.AddFriendAdapter;
import com.zjcql.adapter.NoticesAdapter;
import com.zjcql.apparatus.Puller;
import com.zjcql.base.BaseActivity;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;

/** 
* 添加好友Activity
* @author phlofy
* @date 2015年12月5日 下午5:12:46 
*/
public class AddFriendActivity extends BaseActivity implements OnClickListener{
	EditText mEditText;
	/**
	 * 刷新布局view
	 */
	SwipeRefreshLayout mSwipeLayout;
	AddFriendAdapter mAdapter;
	/**
	 * 适配器的数据
	 */
	List<BmobChatUser> dataList;
	ListView mListView;
	Puller mPuller;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend);
		findView();
		mPuller = new Puller(AddFriendActivity.this, mSwipeLayout, mListView);
		mPuller.setDividerHeight(0);
        mPuller.setOnRefreshListener(new OnRefresh() {
			
			@Override
			public void onRefresh() {
				fetchDataForRefresh();
			}
		});
        
        mPuller.setOnLoadListener(new OnLoad() {
			
			@Override
			public void onLoad() {
				fetchDataForLoad();
			}
		});
        dataList = new ArrayList<BmobChatUser>();
        mAdapter = new AddFriendAdapter(AddFriendActivity.this, dataList);
        mListView.setAdapter(mAdapter);
	}
	private void findView(){
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.seek).setOnClickListener(this);
		mEditText = (EditText) findViewById(R.id.text);
		mSwipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
		mListView = (ListView)findViewById(R.id.listview);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.back:{
			finish();
			break;
		}
		case R.id.seek:{
			dataList.clear();
			mPuller.refresh();
			break;
		}
		}
	}
	private String getText(){
		return mEditText.getText().toString();
	}
	protected void fetchDataForRefresh() {
		if(getText() == null || "".equals(getText())){
			toast("请输入用户名或ID");
			mPuller.complete();
			return;
		}
		// 自己除外
		BmobQuery<User> query1 = new BmobQuery<User>();
		query1.addWhereNotEqualTo("objectId", userManager.getCurrentUser().getObjectId());
		
		// 防止第一条重复
		BmobQuery<User> query2 = new BmobQuery<User>();
		if(dataList.size() >0 ){
			query2.addWhereNotEqualTo("objectId", dataList.get(0).getObjectId());
		}
		
		// 防止前n条重复
		BmobQuery<User> query3 = new BmobQuery<User>();
		if(dataList.size() >0 ){
			String dateString = dataList.get(0).getCreatedAt();  
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			Date date  = null;
			try {
			    date = sdf.parse(dateString);
			} 
			catch (ParseException e) {
			}
			query3.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(date));
		}
		
		// objectId匹配
		BmobQuery<User> query4 = new BmobQuery<User>();
		query4.addWhereEqualTo("objectId", getText());
		
		// nick模糊匹配
		BmobQuery<User> query5 = new BmobQuery<User>();
		query5.addWhereContains("nick", getText());
		
		// 整合以上两个匹配条件
		List<BmobQuery<User>> orQuery = new ArrayList<BmobQuery<User>>();
		orQuery.add(query4);
		orQuery.add(query5);
		BmobQuery<User> mainQuery = new BmobQuery<User>();
		BmobQuery<User> or = mainQuery.or(orQuery);
		
		// 整合以上条件
		List<BmobQuery<User>> andQuery = new ArrayList<BmobQuery<User>>();
		andQuery.add(query1);
		andQuery.add(query2);
		andQuery.add(query3);
		andQuery.add(or);
		
		// 最后查询
		BmobQuery<User> query = new BmobQuery<User>();
		query.and(andQuery);
		query.order("-createdAt");
    	query.setLimit(10);
    	query.findObjects(AddFriendActivity.this, new FindListener<User>() {
			
			@Override
			public void onSuccess(List<User> arg0) {
				dataList.addAll(0,arg0);
				mAdapter.notifyDataSetChanged();
				mPuller.complete();
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				toast("失败："+arg1);
				mPuller.complete();
			}
		});
	}
	
	protected void fetchDataForLoad() {
		if(getText() == null || "".equals(getText())){
			toast("请输入用户名或ID");
			mPuller.complete();
			return;
		}
		// 自己除外
		BmobQuery<User> query1 = new BmobQuery<User>();
		query1.addWhereNotEqualTo("objectId", userManager.getCurrentUser().getObjectId());
		System.out.println(userManager.getCurrentUser().getObjectId()+"now");
		
		// 防止最后一条重复
		BmobQuery<User> query2 = new BmobQuery<User>();
		if(dataList.size() >0 ){
			query2.addWhereNotEqualTo("objectId", dataList.get(dataList.size()-1).getObjectId());
		}
		
		// 防止前n条重复
		BmobQuery<User> query3 = new BmobQuery<User>();
		if(dataList.size() >0 ){
			String dateString = dataList.get(dataList.size()-1).getCreatedAt();  
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			Date date  = null;
			try {
			    date = sdf.parse(dateString);
			} 
			catch (ParseException e) {
			}
			query3.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));
		}
		
		// objectId匹配
		BmobQuery<User> query4 = new BmobQuery<User>();
		query4.addWhereEqualTo("objectId", getText());
		
		// nick模糊匹配
		BmobQuery<User> query5 = new BmobQuery<User>();
		query5.addWhereContains("nick", getText());
		
		// 整合以上两个匹配条件
		List<BmobQuery<User>> orQuery = new ArrayList<BmobQuery<User>>();
		orQuery.add(query4);
		orQuery.add(query5);
		BmobQuery<User> mainQuery = new BmobQuery<User>();
		BmobQuery<User> or = mainQuery.or(orQuery);
		
		// 整合以上条件
		List<BmobQuery<User>> andQuery = new ArrayList<BmobQuery<User>>();
		andQuery.add(query1);
		andQuery.add(query2);
		andQuery.add(query3);
		andQuery.add(or);
		
		// 最后查询
		BmobQuery<User> query = new BmobQuery<User>();
		query.and(andQuery);
		query.order("-createdAt");
    	query.setLimit(10);
    	query.findObjects(AddFriendActivity.this, new FindListener<User>() {
			
			@Override
			public void onSuccess(List<User> arg0) {

				if(arg0.size() > 0){
					dataList.addAll(arg0);
					mAdapter.notifyDataSetChanged();
					mPuller.complete();
				}
				else{
					mPuller.complete();
					mPuller.noMoreData();
				}
				
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				toast("失败："+arg1);
				mPuller.complete();
			}
			
		});
	}
}
