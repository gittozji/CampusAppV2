package com.zjcql.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zjcql.Interface.OnLoad;
import com.zjcql.Interface.OnRefresh;
import com.zjcql.adapter.NoticesAdapter;
import com.zjcql.apparatus.Puller;
import com.zjcql.base.BaseFragment;
import com.zjcql.bmobbean.Goods;
import com.zjcql.bmobbean.Notices;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;

/** 
* 通知浏览Fragment
* @author phlofy
* @date 2015年10月17日 下午2:33:19 
*/
public class NoticeBrowseFragment extends BaseFragment{
	
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
	List<Notices> dataList;
	ListView mListView;
	NoticesAdapter mAdapter;
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
        dataList = new ArrayList<Notices>();
        mAdapter = new NoticesAdapter(getActivity(), dataList);
        mListView.setAdapter(mAdapter);
        
        isCompleteOnCreateViewMethod = true;
        notifyIamCompleted();
        // 第一次刷新
        firstRefresh();
		return mRootView;
	}
	
	private void findView(){
		mSwipeLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_layout);
		mListView = (ListView) mRootView.findViewById(R.id.listview);
	}
	/**
	 * 通知我差不多完成onCreateView方法了
	 */
	private void notifyIamCompleted(){
		if(isRequestFirstRefresh){
			firstRefresh();
			isRequestFirstRefresh = false;
		}
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
	
	protected void fetchDataForRefresh() {
		
		// 防止第一条重复
		BmobQuery<Notices> query2 = new BmobQuery<Notices>();
		if(dataList.size() >0 ){
			query2.addWhereNotEqualTo("objectId", dataList.get(0).getObjectId());
		}
		
		// 防止前n条重复
		BmobQuery<Notices> query3 = new BmobQuery<Notices>();
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
		List<BmobQuery<Notices>> andQuery = new ArrayList<BmobQuery<Notices>>();
		andQuery.add(query2);
		andQuery.add(query3);
		
		// 最后查询
		BmobQuery<Notices> query = new BmobQuery<Notices>();
		query.and(andQuery);
		// 如果有用户且填写了校园则过滤掉其他校园的信息
		User user = BmobUser.getCurrentUser(getActivity(),User.class);
		if(user != null && (user.getCollege() != null && user.getCollege().length() > 0)){
			BmobQuery<User> innerQuery = new BmobQuery<User>();
	    	innerQuery.addWhereEqualTo("college", user.getCollege());
	    	query.addWhereMatchesQuery("user", "_User", innerQuery);
		}
    	query.order("-createdAt");
    	query.setLimit(10);
    	query.include("user");
    	query.findObjects(getActivity(), new FindListener<Notices>() {
			
			@Override
			public void onSuccess(List<Notices> arg0) {
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
		// 防止最后一条重复
		BmobQuery<Notices> query2 = new BmobQuery<Notices>();
		if(dataList.size() >0 ){
			query2.addWhereNotEqualTo("objectId", dataList.get(dataList.size()-1).getObjectId());
		}
		
		// 防止前n条重复
		BmobQuery<Notices> query3 = new BmobQuery<Notices>();
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
		List<BmobQuery<Notices>> andQuery = new ArrayList<BmobQuery<Notices>>();
		andQuery.add(query2);
		andQuery.add(query3);
		
		// 最后查询
		BmobQuery<Notices> query = new BmobQuery<Notices>();
		query.and(andQuery);
		// 如果有用户且填写了校园则过滤掉其他校园的信息
		User user = BmobUser.getCurrentUser(getActivity(),User.class);
		if(user != null && (user.getCollege() != null && user.getCollege().length() > 0)){
			BmobQuery<User> innerQuery = new BmobQuery<User>();
	    	innerQuery.addWhereEqualTo("college", user.getCollege());
	    	query.addWhereMatchesQuery("user", "_User", innerQuery);
		}
    	query.order("-createdAt");
    	query.setLimit(10);
    	query.include("user");
    	query.findObjects(getActivity(), new FindListener<Notices>() {
			
			@Override
			public void onSuccess(List<Notices> arg0) {
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
