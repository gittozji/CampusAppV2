package com.zjcql.popup;

import com.zjcql.campusappv2.R;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 该类是数字键盘。
 * 允许的整数位无限，
 * 允许的小数位位2位。
 * @author phlofy
 *
 */
public class KeyBoardPopup implements OnClickListener{
	/**
	 * 上下文Activity
	 */
	Activity mActivity;
	/**
	 * PopupWindow所依附的根View
	 */
	View mRootView;
	/**
	 * PopupWindow对象，默认大小是覆盖整个屏幕
	 */
	PopupWindow mPopupWindow;
	/**
	 * PopupWindow的界面布局View
	 */
	View mPopupLayout;
	/**
	 * TextView，用于显示价格
	 */
	TextView mPrice,mTextPrice;
	/**
	 * 价格的字符串
	 */
	StringBuffer mStringBuffer = null;
	
	/**
	 * 构造方法
	 * @param activity:上下文
	 * @param rootView:PopupWindow依附的根View
	 * @param price:接收数字键盘的输出结果
	 */
	public KeyBoardPopup(Activity activity,View rootView,TextView mPrice) {
		// TODO Auto-generated constructor stub
		this.mActivity = activity;
		this.mRootView = rootView;
		this.mPrice = mPrice;
		
		mPopupLayout = mActivity.getLayoutInflater().inflate(R.layout.popup_layout_keyboard, null);
		mPopupWindow = new PopupWindow(mPopupLayout,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
		mPopupWindow.setAnimationStyle(R.style.KeyBoardInOutAnimation);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		initParams();
	}
	
	private void initParams(){
		mTextPrice = (TextView) mPopupLayout.findViewById(R.id.price);
		mPopupLayout.findViewById(R.id.zero).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.one).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.two).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.three).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.four).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.five).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.six).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.seven).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.eight).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.night).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.dot).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.yes).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.remove).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.cancel).setOnClickListener(this);
	}
	
	/**
	 * 显示PopupWindow
	 */
	public void show(){
		// 将mPrice的值赋给mTextPrice和mStringBuffer
		String temp = mPrice.getText().toString();
		mTextPrice.setText(temp);
		// 除去最后一个"元"字符
		temp = temp.substring(0,temp.length()-1);
		mStringBuffer = new StringBuffer();
		mStringBuffer.append(temp);
		mPopupWindow.showAtLocation(mRootView,Gravity.BOTTOM, 0, 0);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.zero:
			doStringBuffer("0");
			break;
		case R.id.one:
			doStringBuffer("1");
			break;
		case R.id.two:
			doStringBuffer("2");
			break;
		case R.id.three:
			doStringBuffer("3");
			break;
		case R.id.four:
			doStringBuffer("4");
			break;
		case R.id.five:
			doStringBuffer("5");
			break;
		case R.id.six:
			doStringBuffer("6");
			break;
		case R.id.seven:
			doStringBuffer("7");
			break;
		case R.id.eight:
			doStringBuffer("8");
			break;
		case R.id.night:
			doStringBuffer("9");
			break;
		case R.id.dot:
			if(mStringBuffer.indexOf(".")>0){
				break;
			}
			if(mStringBuffer.length() == 0){
				mStringBuffer.append("0.");
			}
			else{
				mStringBuffer.append(".");
			}
			mTextPrice.setText(mStringBuffer+"元");
			break;
		case R.id.remove:
			mPopupWindow.dismiss();
			break;
		case R.id.cancel:
			if(mStringBuffer.length() == 0)
				break;
			mStringBuffer.deleteCharAt(mStringBuffer.length()-1);
			mTextPrice.setText(mStringBuffer+"元");
			break;
		case R.id.yes:
			if(mTextPrice.getText().toString().equals("元")){
				// mTextPrice只显示"元"，则认为是"0元"
				mPrice.setText("0元");
			}
			else{
				// mTextPrice显示"n元"
				if(mStringBuffer.indexOf(".") == mStringBuffer.length()-1){
					// mTextPrice显示"xxxxxx.元"，则认为是删去"."
					mStringBuffer.deleteCharAt(mStringBuffer.length()-1);
				}
				mPrice.setText(mStringBuffer+"元");
			}
			mPopupWindow.dismiss();
			break;
			
		}
	}
	
	/**
	 * 验证如何处理新加入的字符
	 * 0:正常加入到尾部
	 * 1:不加入
	 * 2:替换掉0位置的字符
	 * @return
	 */
	private int validate(){
		if(mStringBuffer.indexOf("0") == 0){
			// 第一个字符为"0"
			if(mStringBuffer.indexOf(".") == 1){
				// 第二个字符为"."
				if(mStringBuffer.length() == 4){
					// 小数点后已经有两位数字存在
					return 1;
				}
				else{
					// 小数点后数字还不到两位
					return 0;
				}
			}
			else{
				// 第二个字符不为"."
				return 2;
			}
		}
		else{
			if(mStringBuffer.indexOf(".") != -1 && mStringBuffer.indexOf(".")+3 == mStringBuffer.length()){
				// 存在小数点，且小数点后已经有两位数字
				return 1;
			}
			else{
				// 不存在"."或小数点后数字不到两位
				return 0;
			}
		}
	}
	
	/**
	 * 统一处理StringBuffer
	 * @param string
	 */
	private void doStringBuffer(String string){
		switch(validate()){
		case 0:
			mStringBuffer.append(string);
			break;
		case 1:
			break;
		case 2:
			mStringBuffer.replace(0, 1, string);
			break;
		}
		mTextPrice.setText(mStringBuffer+"元");
	}
}
