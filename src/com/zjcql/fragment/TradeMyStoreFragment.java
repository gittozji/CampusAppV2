package com.zjcql.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zjcql.Interface.OnLoad;
import com.zjcql.Interface.OnRefresh;
import com.zjcql.adapter.GoodsAdapter;
import com.zjcql.adapter.UserGoodsAdapter;
import com.zjcql.apparatus.Puller;
import com.zjcql.base.BaseFragment;
import com.zjcql.bmobbean.Goods;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;
import com.zjcql.util.MyUtil;

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
* 我的商店Fragment
* @author phlofy
* @date 2015年10月2日 下午3:20:22 
*/
public class TradeMyStoreFragment extends BaseFragment{
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
	List<Goods> dataList;
	ListView mListView;
	GoodsAdapter mAdapter;
	Puller mPuller;
		/**
		 * 当前用户
		 */
	User mUser = null;
		/**
		 * false为非自己账号
		 * true为是自己账号
		 */
	boolean isCurrent = false;
	
		/**
		 * 是否完成OnCreateView方法
		 */
	boolean isCompleteOnCreateViewMethod = false;
	boolean isRequestFirstRefresh = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null){
			mUser = (User) getArguments().get("user");
			isCurrent = false;
		}
		else{
			mUser = BmobUser.getCurrentUser(getActivity(), User.class);
			isCurrent = true;
		}
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
	    
	    dataList = new ArrayList<Goods>();
		
	    // 创建适配器
	    mAdapter = new GoodsAdapter(getActivity(), dataList);
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
		//未登录用户时无需访问网络
		if(isCurrent && mUser == null){
			mPuller.complete();
			return;
		}
		// 防止第一条重复
		BmobQuery<Goods> query2 = new BmobQuery<Goods>();
		if(dataList.size() >0 ){
			query2.addWhereNotEqualTo("objectId", dataList.get(0).getObjectId());
		}
		
		// 防止前n条重复
		BmobQuery<Goods> query3 = new BmobQuery<Goods>();
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
		
		// 查找user发布的信息
		BmobQuery<Goods> query6 = new BmobQuery<Goods>();
		query6.addWhereEqualTo("user", mUser);
		if(mUser == null)
			System.out.println("确实为空");
		// 整合以上条件
		List<BmobQuery<Goods>> andQuery = new ArrayList<BmobQuery<Goods>>();
		andQuery.add(query2);
		andQuery.add(query3);
		andQuery.add(query6);
		
		// 最后查询
		BmobQuery<Goods> query = new BmobQuery<Goods>();
		query.and(andQuery);
		query.order("-createdAt");
		query.setLimit(10);
		query.findObjects(getActivity(), new FindListener<Goods>() {
			
			@Override
			public void onSuccess(List<Goods> arg0) {
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
		//未登录用户时无需访问网络
		if(isCurrent && mUser == null){
			mPuller.complete();
			return;
		}
		// 防止最后一条重复
		BmobQuery<Goods> query2 = new BmobQuery<Goods>();
		if(dataList.size() >0 ){
			query2.addWhereNotEqualTo("objectId", dataList.get(dataList.size()-1).getObjectId());
		}
		
		// 防止前n条重复
		BmobQuery<Goods> query3 = new BmobQuery<Goods>();
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
		
		// 查找user发布的信息
		BmobQuery<Goods> query6 = new BmobQuery<Goods>();
		query6.addWhereEqualTo("user", mUser);
		
		// 整合以上条件
		List<BmobQuery<Goods>> andQuery = new ArrayList<BmobQuery<Goods>>();
		andQuery.add(query2);
		andQuery.add(query3);
		andQuery.add(query6);
		
		// 最后查询
		BmobQuery<Goods> query = new BmobQuery<Goods>();
		query.and(andQuery);
		query.order("-createdAt");
		query.setLimit(10);
		query.findObjects(getActivity(), new FindListener<Goods>() {
			
			@Override
			public void onSuccess(List<Goods> arg0) {
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
	
	@Override
	public String toString() {
		return "TradeMyStoreFragment";
	}
}

