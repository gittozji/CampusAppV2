package com.zjcql.base;

import com.zjcql.Interface.NotifyParent;

import android.app.Fragment;
import android.widget.Toast;

/**
 * 对Fragment的通用初始化
 * @author phlofy
 *
 */
public class BaseFragment extends Fragment{
	/**
	 * 回调接口
	 */
	public NotifyParent mNotifyParent;
	
	public void toast(String msg){
		try{
			Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
		}
		catch(Exception e){
			System.out.println("toast空指针异常。");
		}
	}
	
	public void setOnNotifyParentListener(NotifyParent notifyParent) {
		this.mNotifyParent = notifyParent;
	}
}
