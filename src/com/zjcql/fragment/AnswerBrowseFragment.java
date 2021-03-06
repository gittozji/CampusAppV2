package com.zjcql.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zjcql.Interface.OnLoad;
import com.zjcql.Interface.OnRefresh;
import com.zjcql.activity.AnswerActivity;
import com.zjcql.adapter.AnswerAdapter;
import com.zjcql.adapter.SayAdapter;
import com.zjcql.apparatus.Puller;
import com.zjcql.base.BaseFragment;
import com.zjcql.bmobbean.Answer;
import com.zjcql.bmobbean.Goods;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;

/** 
* 问答浏览Fragment
* @author phlofy
* @date 2016年1月25日 下午8:30:14 
*/
public class AnswerBrowseFragment extends BaseFragment{
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
	List<Answer> dataList;
	ListView mListView;
	AnswerAdapter mAdapter;
	Puller mPuller;
		/**
		 * 是否完成OnCreateView方法
		 */
	boolean isCompleteOnCreateViewMethod = false;
	boolean isRequestFirstRefresh = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		mRootView = inflater.inflate(R.layout.fragment_trade_goods_browse, null);
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
	    
	    dataList = new ArrayList<Answer>();
		
	    // 创建适配器
	    mAdapter = new AnswerAdapter(getActivity(), dataList);
	    mListView.setAdapter(mAdapter);
	    
	    isCompleteOnCreateViewMethod = true;
	    notifyIamCompleted();
	    // 第一次刷新
	    firstRefresh();
	    
		return mRootView;
	}
	
	public void firstRefresh() {
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
	
	private void notifyIamCompleted() {
		if(isRequestFirstRefresh){
			firstRefresh();
			isRequestFirstRefresh = false;
		}
	}
	/**
	 * 查找上拉全部
	 */
	protected void fetchDataForLoad() {
		// 防止最后一条重复
		BmobQuery<Answer> query2 = new BmobQuery<Answer>();
		if(dataList.size() >0 ){
			query2.addWhereNotEqualTo("objectId", dataList.get(dataList.size()-1).getObjectId());
		}
		
		// 防止前n条重复
		BmobQuery<Answer> query3 = new BmobQuery<Answer>();
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
		
		// 关键字
		BmobQuery<Answer> query4 = new BmobQuery<Answer>();
		if(AnswerActivity.SEEK_FIELD != null && !"".equals(AnswerActivity.SEEK_FIELD)){
			query4.addWhereContains("title", AnswerActivity.SEEK_FIELD);
		}

		// 整合以上条件
		List<BmobQuery<Answer>> andQuery = new ArrayList<BmobQuery<Answer>>();
		andQuery.add(query2);
		andQuery.add(query3);
		andQuery.add(query4);
	
		// 最后查询
		BmobQuery<Answer> query = new BmobQuery<Answer>();
		query.and(andQuery);
		// 如果有用户且填写了校园则过滤掉其他校园的信息
		User user = BmobUser.getCurrentUser(getActivity(),User.class);
		if(user != null && (user.getCollege() != null && user.getCollege().length() > 0)){
			BmobQuery<User> innerQuery = new BmobQuery<User>();
	    	innerQuery.addWhereEqualTo("college", user.getCollege());
	    	query.addWhereMatchesQuery("fromUser", "_User", innerQuery);
		}
		query.order("-createdAt");
		query.setLimit(10);
		query.include("fromUser");
		query.findObjects(getActivity(), new FindListener<Answer>() {
			
			@Override
			public void onSuccess(List<Answer> arg0) {
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
	/**
	 * 查找下拉全部
	 */
	protected void fetchDataForRefresh() {
		// 防止第一条重复
		BmobQuery<Answer> query2 = new BmobQuery<Answer>();
		if(dataList.size() >0 ){
			query2.addWhereNotEqualTo("objectId", dataList.get(0).getObjectId());
		}
		
		// 防止前n条重复
		BmobQuery<Answer> query3 = new BmobQuery<Answer>();
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
		
		// 关键字
		BmobQuery<Answer> query4 = new BmobQuery<Answer>();
		if(AnswerActivity.SEEK_FIELD != null && !"".equals(AnswerActivity.SEEK_FIELD)){
			query4.addWhereContains("title", AnswerActivity.SEEK_FIELD);
		}
		
		// 整合以上条件
		List<BmobQuery<Answer>> andQuery = new ArrayList<BmobQuery<Answer>>();
		andQuery.add(query2);
		andQuery.add(query3);
		andQuery.add(query4);
		
		// 最后查询
		BmobQuery<Answer> query = new BmobQuery<Answer>();
		query.and(andQuery);
		// 如果有用户且填写了校园则过滤掉其他校园的信息
		User user = BmobUser.getCurrentUser(getActivity(),User.class);
		if(user != null && (user.getCollege() != null && user.getCollege().length() > 0)){
			BmobQuery<User> innerQuery = new BmobQuery<User>();
	    	innerQuery.addWhereEqualTo("college", user.getCollege());
	    	query.addWhereMatchesQuery("fromUser", "_User", innerQuery);
		}
		query.order("-createdAt");
		query.setLimit(10);
		query.include("fromUser");
		query.findObjects(getActivity(), new FindListener<Answer>() {
			
			@Override
			public void onSuccess(List<Answer> arg0) {
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
	
	private void findView() {
		// 获取listview实例
	    mListView = (ListView) mRootView.findViewById(R.id.listview);
	    // 获取刷新布局View实例
	    mSwipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipe_layout);
	}
	
}
