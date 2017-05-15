package com.zjcql.dialog;

import com.zjcql.campusappv2.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/** 
* 用于简单的编辑
* @author phlofy
* @date 2015年9月5日 下午4:51:30 
*/
public abstract class EditDialog implements OnClickListener{
	EditText mText;
	Button mButton1,mButton2;
	Dialog mDialog;
	
	public EditDialog(Context context, String text){
		mDialog = new Dialog(context, R.style.DialogTheme);
		mDialog.setContentView(R.layout.dialog_layout_edit);
		mText = (EditText) mDialog.findViewById(R.id.text);
		mText.setText(text);
		mDialog.findViewById(R.id.button1).setOnClickListener(this);
		mDialog.findViewById(R.id.button2).setOnClickListener(this);
	}
	public EditDialog(Context context,String text,boolean isEnable){
		mDialog = new Dialog(context, R.style.DialogTheme);
		mDialog.setContentView(R.layout.dialog_layout_edit);
		mText = (EditText) mDialog.findViewById(R.id.text);
		mText.setText(text);
		mText.setEnabled(isEnable);
		mDialog.findViewById(R.id.button1).setOnClickListener(this);
		mDialog.findViewById(R.id.button2).setOnClickListener(this);
		
	}
	
	public void show(){
		mDialog.show();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button1:{
			onClickEventForButton1();
			mDialog.dismiss();
			break;
		}
		case R.id.button2:{
			onClickEventForButton2(mText.getText().toString());
			mDialog.dismiss();
			break;
		}
		default:{
			mDialog.dismiss();
			break;
		}
		}
		
	}
	
	public abstract void onClickEventForButton1();
	public abstract void onClickEventForButton2(String text);
}
