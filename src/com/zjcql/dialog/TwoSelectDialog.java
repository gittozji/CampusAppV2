package com.zjcql.dialog;

import com.zjcql.campusappv2.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/** 
* 只有两个按钮的Dialog
* @author phlofy
* @date 2015年8月24日 下午2:20:46 
*/
public abstract class TwoSelectDialog implements OnClickListener{
	Button mButton1,mButton2;
	Dialog mDialog;
	public TwoSelectDialog(Context context,String string1,String string2) {
		mDialog = new Dialog(context, R.style.DialogTheme);
		mDialog.setContentView(R.layout.dialog_layout_select_two);
		mButton1 = (Button) mDialog.findViewById(R.id.button1);
		mButton1.setOnClickListener(this);
		mButton1.setText(string1);
		mButton2 = (Button) mDialog.findViewById(R.id.button2);
		mButton2.setOnClickListener(this);
		mButton2.setText(string2);
	}
	
	public void show(){
		mDialog.show();
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.button1:
			onClickEventForButton1();
			mDialog.dismiss();
			break;

		case R.id.button2:
			onClickEventForButton2();
			mDialog.dismiss();
			break;
		}
		
	}
	public abstract void onClickEventForButton1();
	public abstract void onClickEventForButton2();
	
}
