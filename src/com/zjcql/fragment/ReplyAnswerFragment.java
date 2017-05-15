package com.zjcql.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.baidu.location.an;
import com.zjcql.Interface.GeneralListener;
import com.zjcql.Interface.OnLoad;
import com.zjcql.Interface.OnRefresh;
import com.zjcql.activity.UserActivity;
import com.zjcql.adapter.CommentSayAdapter;
import com.zjcql.adapter.GoodsAdapter;
import com.zjcql.adapter.ReplyAnswerAdapter;
import com.zjcql.apparatus.Poper;
import com.zjcql.apparatus.Puller;
import com.zjcql.base.BaseFragment;
import com.zjcql.bmobbean.Answer;
import com.zjcql.bmobbean.ReplyAnswer;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;
import com.zjcql.popup.PictureShowerPopup;
import com.zjcql.rebuildview.CircleImageView;
import com.zjcql.rebuildview.GridViewPro;
import com.zjcql.util.MyBitmapUtil;
import com.zjcql.util.MyUtil;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/** 
* 问答fragment
* @author phlofy
* @date 2016年1月27日 下午12:02:50 
*/
public class ReplyAnswerFragment extends BaseFragment{
	
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
	List<ReplyAnswer> dataList;
	ListView mListView;
	ReplyAnswerAdapter mAdapter;
	Puller mPuller;
		/**
		 * 是否完成OnCreateView方法
		 */
	boolean isCompleteOnCreateViewMethod = false;
	boolean isRequestFirstRefresh = false;
	
	Button send;
	EditText content;
	
	Answer answer;
	CircleImageView icon;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent in = getActivity().getIntent();
		Bundle be = in.getExtras();
		answer = (Answer) be.getSerializable("answer");
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
	    
	    dataList = new ArrayList<ReplyAnswer>();
		
	    // 创建适配器
	    mAdapter = new ReplyAnswerAdapter(getActivity(), dataList);
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
	    addHeaderViewToListView();
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
						final ReplyAnswer replyAnswer = new ReplyAnswer();
						replyAnswer.setContent(content.getText().toString());
						replyAnswer.setAnswer(answer);
						replyAnswer.setUser(user);
						replyAnswer.setPop(0);
						replyAnswer.save(getActivity(), new SaveListener() {
							
							@Override
							public void onSuccess() {
								dataList.add(0,replyAnswer);
								mAdapter.notifyDataSetChanged();
								content.setText("");
								Poper.getInstance().increme(true,getActivity(), answer.getFromUser(), 1, new GeneralListener() {
									
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
	public void setIcon(CircleImageView icon){
		this.icon = icon;
		icon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in = new Intent(getActivity(),UserActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("user", answer.getFromUser());
				in.putExtras(bundle);
				getActivity().startActivity(in);
			}
		});
	}
	private void addHeaderViewToListView() {
		/********************获取view************************/
		View headerView;
		headerView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_reply_answer_header, null,false);
		TextView title = (TextView) headerView.findViewById(R.id.title);
		TextView content = (TextView) headerView.findViewById(R.id.content);
		final GridViewPro grid = (GridViewPro) headerView.findViewById(R.id.grid);
		/********************转载数据************************/
//		content.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		MyBitmapUtil.displayIcon(getActivity(), answer.getFromUser().getAvatar(), icon);
		title.setText(answer.getTitle());
		content.setText(answer.getContent());
		BaseAdapter mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// 获取屏幕的宽度，用于动态控制三个image的大小
				int mScreenWidth = MyUtil.getScreenWidth(getActivity()) - MyUtil.dip2px(getActivity(),26);
				AbsListView.LayoutParams mLayoutParams =  new AbsListView.LayoutParams(mScreenWidth/3, mScreenWidth/3);
				ImageView mImageView = new ImageView(getActivity());
				mImageView.setScaleType(ScaleType.CENTER_CROP);
				mImageView.setLayoutParams(mLayoutParams);
				MyBitmapUtil.displayImage(getActivity(), answer.getFiles()[position], mImageView);
				return mImageView;
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				return null;
			}
			
			@Override
			public int getCount() {
				if(answer.getFiles() == null)
					return 0;
				return answer.getFiles().length;
			}
		};
		grid.setAdapter(mAdapter);
		
		/********************设置监听************************/
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PictureShowerPopup psp = new PictureShowerPopup(getActivity(), grid, answer.getFiles()[position]);
				psp.show();
			}
		});
		
		mListView.addHeaderView(headerView);
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
		
		// 最后查询
		BmobQuery<ReplyAnswer> query = new BmobQuery<ReplyAnswer>();
		query.addWhereEqualTo("answer", new BmobPointer(answer));
		query.order("-pop,-createdAt");
		query.setLimit(10);
		query.include("user");
		query.findObjects(getActivity(), new FindListener<ReplyAnswer>() {
			
			@Override
			public void onSuccess(List<ReplyAnswer> arg0) {
				dataList.clear();
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
		
		// 最后查询
		BmobQuery<ReplyAnswer> query = new BmobQuery<ReplyAnswer>();
		query.addWhereEqualTo("answer", new BmobPointer(answer));
		query.order("-pop,-createdAt");
		query.setLimit(10);
		query.setSkip(dataList.size());
		query.include("user");
		query.findObjects(getActivity(), new FindListener<ReplyAnswer>() {
			
			@Override
			public void onSuccess(List<ReplyAnswer> arg0) {
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
