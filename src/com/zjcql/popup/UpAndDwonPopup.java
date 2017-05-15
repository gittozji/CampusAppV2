package com.zjcql.popup;

import com.zjcql.base.BasePopupWindow;
import com.zjcql.campusappv2.R;
import com.zjcql.util.MyUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;

/** 
* 提供两个选项的popup
* @author phlofy
* @date 2015年10月1日 下午1:43:00 
*/
public class UpAndDwonPopup extends BasePopupWindow implements OnClickListener{
	String string1,string2;
	Button button1,button2;
	public UpAndDwonPopup(Context mContext, View mRootViwe ,String string1,String string2) {
		super(mContext, mRootViwe);
		this.string1 = string1;
		this.string2 = string2;
		doWork();
	}

	@Override
	public void doWork() {
		mPopupLayout = ((Activity)mContext).getLayoutInflater().inflate(R.layout.popup_layout_up_and_down, null);
		button1 = (Button) mPopupLayout.findViewById(R.id.button1);
		button2 = (Button) mPopupLayout.findViewById(R.id.button2);
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button1.setText(string1);
		button2.setText(string2);
		mPopupWindow = new PopupWindow(mPopupLayout,MyUtil.dip2px(mContext, 160),MyUtil.dip2px(mContext, 40),true);
			// 点击外面区域自动退出Popup
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button1:{
			gl.onReturn(true,1, null);
			mPopupWindow.dismiss();
			break;
		}
		case R.id.button2:{
			gl.onReturn(true, 2, null);
			mPopupWindow.dismiss();
			break;
		}
		default:break;
		}
	}

	@Override
	public void show() {
		mPopupWindow.showAsDropDown(mRootView);
	}

	
}
