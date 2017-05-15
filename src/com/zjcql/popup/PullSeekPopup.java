package com.zjcql.popup;

import com.zjcql.activity.AnswerActivity;
import com.zjcql.base.BasePopupWindow;
import com.zjcql.campusappv2.R;
import com.zjcql.rebuildview.TouchImageView;
import com.zjcql.util.MyBitmapUtil;
import com.zjcql.util.MyUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.PopupWindow;

/** 
* 模糊查询的下拉popup
* @author phlofy
* @date 2016年1月26日 下午9:27:21 
*/
public class PullSeekPopup extends BasePopupWindow{
	EditText edit;
	public PullSeekPopup(Context mContext, View mRootViwe) {
		super(mContext, mRootViwe);
		doWork();
	}
	@Override
	public void doWork() {
		mPopupLayout = ((Activity)mContext).getLayoutInflater().inflate(R.layout.popup_seek, null);
		mPopupLayout.findViewById(R.id.seek).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				gl.onReturn(true, 0, edit.getText().toString());
				AnswerActivity.SEEK_FIELD = edit.getText().toString();
				mPopupWindow.dismiss();
			}
		});
		mPopupLayout.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AnswerActivity.SEEK_FIELD = "";
				edit.setText("");
			}
		});
		edit = (EditText) mPopupLayout.findViewById(R.id.edit);
		edit.setText(AnswerActivity.SEEK_FIELD);
		mPopupWindow = new PopupWindow(mPopupLayout,ViewGroup.LayoutParams.MATCH_PARENT,MyUtil.dip2px(mContext, 75),true);
		mPopupWindow.setAnimationStyle(R.style.PullSeekInOutAnimation);
		// 点击外面区域自动退出Popup
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	}
	@Override
	public void show() {
		mPopupWindow.showAtLocation(mRootView, Gravity.TOP, 0, 0);
	}
	
}
