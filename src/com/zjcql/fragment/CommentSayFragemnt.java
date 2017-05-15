package com.zjcql.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zjcql.Interface.GeneralListener;
import com.zjcql.Interface.OnLoad;
import com.zjcql.Interface.OnRefresh;
import com.zjcql.activity.UserActivity;
import com.zjcql.adapter.CommentSayAdapter;
import com.zjcql.adapter.UserGoodsAdapter;
import com.zjcql.apparatus.Poper;
import com.zjcql.apparatus.Puller;
import com.zjcql.base.BaseFragment;
import com.zjcql.bmobbean.Comment;
import com.zjcql.bmobbean.Goods;
import com.zjcql.bmobbean.Says;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/** 
* 评论吐槽
* @author phlofy
* @date 2016年1月14日 上午10:33:03 
*/
public class CommentSayFragemnt extends BaseFragment{
	
		/**
		 * 根View
		 */
	View mRootView;
		/**
		 * 刷新布局view
		 */
	SwipeRefreshLayout mSwipeLayout;
		/**
		 * 适配器的数据
		 */
	List<Comment> dataList;
	ListView mListView;
	CommentSayAdapter mAdapter;
	Puller mPuller;
		/**
		 * 是否完成OnCreateView方法
		 */
	boolean isCompleteOnCreateViewMethod = false;
	boolean isRequestFirstRefresh = false;
	
	Button send;
	EditText content;
	
	Says say;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent in = getActivity().getIntent();
		Bundle be = in.getExtras();
		say = (Says) be.getSerializable("says");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		mRootView = inflater.inflate(R.layout.fragment_comment_say, null);
		// 获取控件
		findView();
	 
	    mPuller = new Puller(getActivity(), mSwipeLayout, mListView);
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
	    
	    dataList = new ArrayList<Comment>();
		
	    // 创建适配器
	    mAdapter = new CommentSayAdapter(getActivity(), dataList);
	    mListView.setAdapter(mAdapter);
	    
	    isCompleteOnCreateViewMethod = true;
	    notifyIamCompleted();
	    // 第一次刷新
	    firstRefresh();
	    
		return mRootView;
	}
	
	private void findView(){
		// 获取listview实例
	    mListView = (ListView) mRootView.findViewById(R.id.listview);
	    // 获取刷新布局View实例
	    mSwipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipe_layout);
	    content = (EditText) mRootView.findViewById(R.id.edit);
	    send = (Button) mRootView.findViewById(R.id.send);
	    send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(content.getText().toString().length() > 0){
					User user = BmobUser.getCurrentUser(getActivity(), User.class);
					if(user != null){
						final Comment comment = new Comment();
						comment.setContent(content.getText().toString());
						comment.setSay(say);
						comment.setUser(user);
						comment.save(getActivity(), new SaveListener() {
							
							@Override
							public void onSuccess() {
								dataList.add(0,comment);
								mAdapter.notifyDataSetChanged();
								content.setText("");
								Poper.getInstance().increme(true,getActivity(), say.getFromUser(), 1, new GeneralListener() {
									
									@Override
									public void onReturn(boolean arg0, int arg1, String arg2) {
									}
								});
							}
							
							@Override
							public void onFailure(int arg0, String arg1) {
								toast("评论失败："+arg1);
							}
						});
					}
					else{
						toast("还未登录，不能评论");
					}
				}
				else{
					toast("内容不能为空");
				}
			}
		});
	}
	
	public void firstRefresh(){
		if(isCompleteOnCreateViewMethod){   
			// 完成onCreateView方法，则运行下面两行代码
			dataList.clear();
			mPuller.refresh();
		}
		else{    
			// onCreateView方法还未执行完毕，则设置isRequestFirstRefresh字段请求在onCreateView方法完成以后执行firstRefresh方法
			isRequestFirstRefresh = true;
		}
	}
	
	private void notifyIamCompleted(){
		if(isRequestFirstRefresh){
			firstRefresh();
			isRequestFirstRefresh = false;
		}
	}
	
	/**
	 * 查找下拉全部
	 */
	private void fetchDataForRefresh(){
		// 防止第一条重复
		BmobQuery<Comment> query2 = new BmobQuery<Comment>();
		if(dataList.size() >0 ){
			query2.addWhereNotEqualTo("objectId", dataList.get(0).getObjectId());
		}
		
		// 防止前n条重复
		BmobQuery<Comment> query3 = new BmobQuery<Comment>();
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
		
		// 整合以上条件
		List<BmobQuery<Comment>> andQuery = new ArrayList<BmobQuery<Comment>>();
		andQuery.add(query2);
		andQuery.add(query3);

		
		// 最后查询
		BmobQuery<Comment> query = new BmobQuery<Comment>();
		query.addWhereEqualTo("say", new BmobPointer(say));
		query.and(andQuery);
		query.order("-createdAt");
		query.setLimit(10);
		query.include("user");
		query.findObjects(getActivity(), new FindListener<Comment>() {
			
			@Override
			public void onSuccess(List<Comment> arg0) {
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
	
	/**
	 * 查找上拉全部
	 */
	private void fetchDataForLoad(){

		// 防止最后一条重复
		BmobQuery<Comment> query2 = new BmobQuery<Comment>();
		if(dataList.size() >0 ){
			query2.addWhereNotEqualTo("objectId", dataList.get(dataList.size()-1).getObjectId());
		}
		
		// 防止前n条重复
		BmobQuery<Comment> query3 = new BmobQuery<Comment>();
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
		
		
		
		// 整合以上条件
		List<BmobQuery<Comment>> andQuery = new ArrayList<BmobQuery<Comment>>();
		andQuery.add(query2);
		andQuery.add(query3);

		
		// 最后查询
		BmobQuery<Comment> query = new BmobQuery<Comment>();
		query.addWhereEqualTo("say", new BmobPointer(say));
		query.and(andQuery);
		query.order("-createdAt");
		query.setLimit(10);
		query.include("user");
		query.findObjects(getActivity(), new FindListener<Comment>() {
			
			@Override
			public void onSuccess(List<Comment> arg0) {
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
				mSwipeLayout.setRefreshing(false);
				mPuller.complete();
			}
			
		});
	}
}
