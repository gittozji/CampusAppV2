package com.zjcql.fragment;

import java.util.zip.Inflater;

import com.zjcql.Interface.GeneralListener;
import com.zjcql.Interface.HasReturn;
import com.zjcql.Interface.NotifyParent;
import com.zjcql.base.BaseFragment;
import com.zjcql.campusappv2.R;
import com.zjcql.popup.ListSelectPopup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/** 
* 条件查找Fragment
* 设置两个static变量。用于保存分类、关键词搜索条件
* @author phlofy
* @date 2015年9月24日 下午8:54:09 
*/
public class TradeGoodsSeekFragment extends BaseFragment implements OnClickListener{
	public static String CLASSIFY_STRING = null;
	public static String KEY_WORD_STRING = null;
	EditText mEditText;
	TextView mTextView;
	View mRootView;
	NotifyParent mNotifyParent;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_trade_goods_seek, null);
		findView();
		mEditText.setText(KEY_WORD_STRING);
		mTextView.setText(CLASSIFY_STRING);
		return mRootView;
	}
	
	private void findView(){
		mEditText = (EditText) mRootView.findViewById(R.id.text);
		mTextView = (TextView) mRootView.findViewById(R.id.classify);
		mRootView.findViewById(R.id.lay_classify).setOnClickListener(this);;
		mRootView.findViewById(R.id.button1).setOnClickListener(this);
		mRootView.findViewById(R.id.button2).setOnClickListener(this);
		mRootView.findViewById(R.id.button3).setOnClickListener(this);
		mRootView.findViewById(R.id.button4).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button1:{
			KEY_WORD_STRING = mEditText.getText().toString();
			mNotifyParent.returnCode("条件搜索商品");
			break;
		}
		case R.id.button2:{
			KEY_WORD_STRING = mEditText.getText().toString();
			mNotifyParent.returnCode("条件搜索求购信息");
			break;
		}
		case R.id.button3:{
			CLASSIFY_STRING = null;
			KEY_WORD_STRING = null;
			mEditText.setText("");
			mTextView.setText("");
			mNotifyParent.returnCode("搜索全部商品");
			break;
		}
		case R.id.button4:{
			CLASSIFY_STRING = null;
			KEY_WORD_STRING = null;
			mEditText.setText("");
			mTextView.setText("");
			mNotifyParent.returnCode("搜索全部求购信息");
			break;
		}
		case R.id.lay_classify:{
			String[] mStringLeft = {"电子设备","学习资料","零食","床上用品","运动器材","衣物","小装饰品","护肤品","其他","学习资料","零食","床上用品","运动器材","衣物","小装饰品","护肤品","其他"};
			String[][] mStringRight = {{"手机","电脑","洗衣机","电风扇","路由器","其他电子设备"},
										{"课外书本","四六级资料","考研资料","实验报告","课内书本","其他学习资料","四六级资料","考研资料","实验报告","课内书本","其他学习资料","四六级资料","考研资料","实验报告","课内书本","其他学习资料","四六级资料","考研资料","实验报告","课内书本","其他学习资料"},
										{"饮料","泡面","其他零食"},
										{"被子","枕头","抱枕","其他床上用品"},
										{"球","球拍","其他运动器材"},
										{"裙子","裤子","上衣","其他衣物"},
										{"毛绒装饰品","塑料装饰品","其他装饰品"},
										{"女生专用","男士专用","其他护肤品"},
										{"其他"},
										{"课外书本","四六级资料","考研资料","实验报告","课内书本","其他学习资料","四六级资料","考研资料","实验报告","课内书本","其他学习资料","四六级资料","考研资料","实验报告","课内书本","其他学习资料","四六级资料","考研资料","实验报告","课内书本","其他学习资料"},
										{"饮料","泡面","其他零食"},
										{"被子","枕头","抱枕","其他床上用品"},
										{"球","球拍","其他运动器材"},
										{"裙子","裤子","上衣","其他衣物"},
										{"毛绒装饰品","塑料装饰品","其他装饰品"},
										{"女生专用","男士专用","其他护肤品"},
										{"其他"}
										};
			ListSelectPopup mListSelectPopup2 = new ListSelectPopup(getActivity(),mRootView, "选择分类", mStringLeft, mStringRight);
			mListSelectPopup2.setGeneralListener(new GeneralListener() {
				
				@Override
				public void onReturn(boolean arg0, int arg1, String arg2) {
					mTextView.setText(arg2);
					CLASSIFY_STRING = arg2;
				}
			});
			mListSelectPopup2.show();
			break;
		}
		default:break;
		}
	}
	
	public void setOnNotifyParentListener(NotifyParent arg0){
		this.mNotifyParent = arg0;
	}
	
}
