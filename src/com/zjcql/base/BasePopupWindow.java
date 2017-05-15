package com.zjcql.base;


import com.zjcql.Interface.GeneralListener;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

/**
 * 为自定义popupWindow建立基础类的接口声明
 * @author phlofy
 *
 */
public abstract class BasePopupWindow extends PopupWindow {
		/**
		 * 上下文
		 */
	protected Context mContext;
		/**
		 * popup所依附的view
		 */
	protected View mRootView;
		/**
		 * PopupWindow对象
		 */
	protected PopupWindow mPopupWindow;
		/**
		 * PopupWindow的界面布局View
		 */
	protected View mPopupLayout;
		/**
		 * 回调接口
		 */
	protected GeneralListener gl;
	/**
	 * 构造方法
	 * @param mContext：上下文
	 * @param mRootViwe：PopupWindow所依附的View
	 */
	public BasePopupWindow(Context mContext,View mRootViwe) {
		this.mContext = mContext;
		this.mRootView = mRootViwe;
	}
	public BasePopupWindow(Context mContext,View mRootViwe,String[] mItemString_second) {
		this.mContext = mContext;
		this.mRootView = mRootViwe;
	}
	public BasePopupWindow(Context mContext,View mRootViwe,String[] mItemString_stair, String[][] mItemString_second) {
		this.mContext = mContext;
		this.mRootView = mRootViwe;
	}
	/**
	 * 其他代码事务
	 */
	public abstract void doWork();
	/**
	 * 显示PopupWindow
	 * 默认从底部对齐
	 */
	public void show(){
		mPopupWindow.showAtLocation(mRootView,Gravity.BOTTOM, 0, 0);
	}
	/**
	 * 设置监听器
	 * @param gl 规则具体环境具体使用
	 */
	public void setGeneralListener(GeneralListener gl) {  
        this.gl = gl;  
    }  
	
}
