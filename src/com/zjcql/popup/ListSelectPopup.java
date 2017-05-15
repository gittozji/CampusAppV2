package com.zjcql.popup;

import com.zjcql.base.BasePopupWindow;
import com.zjcql.campusappv2.R;
import com.zjcql.util.MyUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 这是一个借助PopupWindow使用listView列表的选择器。
 * 支持一级、二级选择。
 * 使用方法，只需new出该类的对象后调用show()方法即可
 * @author phlofy
 *
 */
public class ListSelectPopup extends BasePopupWindow{
		/**
		 * 一级listView,二级listView
		 */
	ListView mPopupList_stair,mPopupList_second;
		/**
		 * 一级菜单的String数组
		 */
	String[] mItemString_stair;
		/**
		 * 二级菜单的String数组
		 */
	String[][] mItemString_second;

	ArrayAdapter<String> mArrayAdapter;
	BaseAdapter mBaseAdapter;
		/**
		 * 第二级使用简单的ArrayAdapter做适配
		 */
	ArrayAdapter<String>[] mArrayAdapterSecond;
	
	int mLastPosition = -1 ;
	
	String mTitle;
	/**
	 * 一级选择器构造方法
	 * @param mContext 上下文
	 * @param mRootViwe 依附的view
	 * @param mTitle bar标题
	 * @param mItemString_second 传入选择器的数据
	 */
	public ListSelectPopup(Context mContext, View mRootViwe,String mTitle, String[] mItemString_stair) {
		super(mContext, mRootViwe);
		this.mItemString_stair = mItemString_stair;
		this.mTitle = mTitle;
		doWork();
	}
	/**
	 * 二级选择器构造方法
	 * @param mContext 上下文
	 * @param mRootViwe 依附的view
	 * @param mTitle bar标题
	 * @param mItemString_stair 传入选择器的一级数据
	 * @param mItemString_second 传入选择器的二级数据
	 */
	public ListSelectPopup(Context mContext,View mRootViwe,String mTitle,String[] mItemString_stair, String[][] mItemString_second) {
		super(mContext,mRootViwe);
		this.mItemString_stair = mItemString_stair;
		this.mItemString_second = mItemString_second;
		this.mTitle = mTitle;
		doWork();
	}
	
	@Override
	public void doWork() {
		if(mItemString_second == null)
			workOneList();
		else
			workTwoList();
	}
	/**
	 * 一级选择器构造
	 */
	private void workOneList(){
		mPopupLayout = ((Activity)mContext).getLayoutInflater().inflate(R.layout.popup_layout_list_one, null);
		((TextView)mPopupLayout.findViewById(R.id.title)).setText(mTitle);
		mPopupList_second = (ListView) mPopupLayout.findViewById(R.id.listview);
		mArrayAdapter = new ArrayAdapter<String>(mContext,R.layout.popup_layout_item, mItemString_stair);
		mPopupList_second.setAdapter(mArrayAdapter);
		mPopupList_second.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				gl.onReturn(true, 0, mItemString_stair[position]);
				mPopupWindow.dismiss();
			}
		});
		mPopupLayout.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {			
				mPopupWindow.dismiss();
			}
		});
			// 设置popup占据整个屏幕
		mPopupWindow = new PopupWindow(mPopupLayout,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);
			//设置动画
		mPopupWindow.setAnimationStyle(R.style.ListSelectInOutAnimation);
			// 点击外面区域自动退出Popup
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	}
	/**
	 * 二级选择器构造
	 */
	public void workTwoList(){
		/**
		 * 为二级选项准备适配器
		 */
		mArrayAdapterSecond = new ArrayAdapter[mItemString_stair.length];
		for(int i = 0;i < mItemString_stair.length;i++){
			mArrayAdapterSecond[i] = new ArrayAdapter<String>(mContext,R.layout.popup_layout_item,mItemString_second[i]);
		}
		
		mPopupLayout = ((Activity)mContext).getLayoutInflater().inflate(R.layout.popup_layout_list_two, null);
		((TextView)mPopupLayout.findViewById(R.id.title)).setText(mTitle);
		mPopupList_stair = (ListView) mPopupLayout.findViewById(R.id.list_left);
		mPopupList_second = (ListView) mPopupLayout.findViewById(R.id.list_right);
		mBaseAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView mText = new TextView(mContext);
				mText.setHeight(MyUtil.dip2px(mContext, 50));
				mText.setGravity(Gravity.CENTER_VERTICAL);
				mText.setText(mItemString_stair[position]+"");
				mText.setTextSize(16);
				mText.setPadding(MyUtil.dip2px(mContext, 30), 0, 0, 0);
				if(position == mLastPosition){
					mText.setBackgroundColor(Color.parseColor("#ffffff"));
				}
				else{
					mText.setBackgroundColor(Color.parseColor("#F5F5F5"));
				}
				return mText;
			}
			
			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mItemString_stair.length;
			}
		};
		
		mPopupList_stair.setAdapter(mBaseAdapter);
		mPopupList_stair.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				
				view.setBackgroundColor(Color.parseColor("#ffffff"));
				if(mLastPosition != -1 && mLastPosition != position){
					getViewByPosition(mLastPosition,mPopupList_stair).setBackgroundColor(Color.parseColor("#F5F5F5"));
					
				}
				mLastPosition = position;
				mPopupList_second.setAdapter(mArrayAdapterSecond[position]);
				
			}
		});
		
		mPopupList_second.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				gl.onReturn(true, 0, mItemString_second[mLastPosition][position]);
				mPopupWindow.dismiss();
			}
		});

		mPopupLayout.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {			
				mPopupWindow.dismiss();
			}
		});
			// 设置popup占据整个屏幕
		mPopupWindow = new PopupWindow(mPopupLayout,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);
		mPopupWindow.setAnimationStyle(R.style.ListSelectInOutAnimation);
			// 点击外面区域自动退出Popup
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	}
	
	/**
	 * 通过位置获取ItemView
	 * @param pos 位置
	 * @param listView 操作的ListView
	 * @return
	 */
	private View getViewByPosition(int pos, ListView listView) {
		final int firstListItemPosition = listView.getFirstVisiblePosition();
		final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

		if (pos < firstListItemPosition || pos > lastListItemPosition ) {
		    return listView.getAdapter().getView(pos, null, listView);
		} 
		else {
		    final int childIndex = pos - firstListItemPosition;
		    return listView.getChildAt(childIndex);
		}
	}

}
