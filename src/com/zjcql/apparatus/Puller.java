package com.zjcql.apparatus;

import com.zjcql.Interface.OnLoad;
import com.zjcql.Interface.OnRefresh;
import com.zjcql.campusappv2.R;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

/** 
* 将listview的下拉刷新和上拉加载抽象出来
* 该类需要传递SwipeRefreshLayout和Listview两个对象
* 提供OnRefresh和OnLoad两个接口
* @author phlofy
* @date 2015年9月26日 下午4:23:53 
*/
public class Puller {
	Context mContext;
	SwipeRefreshLayout mSwipeRefreshLayout;
	ListView mListView;
	View mListViewFooter;
	OnRefresh mOnRefresh;
	OnLoad mOnLoad;
	boolean isWorking = false;
	/**
	 * 上拉加载是否可行？
	 */
	boolean loadCanWork = true;
	public Puller(Context arg0,SwipeRefreshLayout arg1,ListView arg2){
		this.mContext = arg0;
		this.mSwipeRefreshLayout = arg1;
		this.mListView = arg2;
		doWork();
	}
	private void doWork(){
		// 设置下拉刷新时的颜色值,颜色值需要定义在xml中
        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,android.R.color.holo_orange_light, android.R.color.holo_red_light);
        
		// 加入footer View
        mListViewFooter = LayoutInflater.from(mContext).inflate(R.layout.listview_footer, null,false);
        mListView.addFooterView(mListViewFooter);
        mListViewFooter.setVisibility(View.GONE);
        
        // 下拉刷新
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
            	if(!isWorking){
            		isWorking = true;
            		mOnRefresh.onRefresh();
            	}
            	else{
            		mSwipeRefreshLayout.setRefreshing(false);
            	}
            }
        });
		
		// 上拉加载
		mListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(!loadCanWork)
					return;
				if(!isWorking && mListView.getLastVisiblePosition() == mListView.getAdapter().getCount()-1){
					removeNoMoreData();
					isWorking = true;
	                mSwipeRefreshLayout.postDelayed(new Runnable() {

	                    @Override
	                    public void run() {
	                    	mOnLoad.onLoad();
	                    }
	                }, 0);
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});
	}
	/**
	 * 设置listview的divider高度
	 * @param h
	 */
	public void setDividerHeight(int h){
		this.mListView.setDividerHeight(h);
	}
	public void setLoadCanWork(boolean b){
		loadCanWork = b;
	}
	/**
	 * 下拉刷新对外方法调用接口
	 */
	public void refresh(){
		mSwipeRefreshLayout.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(!isWorking){
					isWorking = true;
					mSwipeRefreshLayout.setRefreshing(true);
					mOnRefresh.onRefresh();
				}
				else{
					mSwipeRefreshLayout.setRefreshing(false);
				}
			}
		}, 0);
	}
	/**
	 * 完成刷新或加载
	 */
	public void complete(){
		isWorking = false;
		mSwipeRefreshLayout.setRefreshing(false);
		mListViewFooter.setVisibility(View.INVISIBLE);
	}
	/**
	 * 设置没有更多数据
	 */
	public void noMoreData(){
		View view1 = mListViewFooter.findViewById(R.id.load_progress);
		View view2 = mListViewFooter.findViewById(R.id.text);
		view1.setVisibility(View.INVISIBLE);
		view2.setVisibility(View.VISIBLE);
		mListViewFooter.setVisibility(View.VISIBLE);
	}
	/**
	 * 没有文字提示的Footer
	 */
	private void removeNoMoreData(){
		View view1 = mListViewFooter.findViewById(R.id.load_progress);
		View view2 = mListViewFooter.findViewById(R.id.text);
		view1.setVisibility(View.VISIBLE);
		view2.setVisibility(View.INVISIBLE);
		mListViewFooter.setVisibility(View.VISIBLE);
	}
	
	public void setOnRefreshListener(OnRefresh arg0){
		this.mOnRefresh = arg0;
	}
	public void setOnLoadListener(OnLoad arg0){
		this.mOnLoad = arg0;
	}
}
