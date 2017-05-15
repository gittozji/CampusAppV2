package com.zjcql.base;

import com.zjcql.Interface.NotifyParent;

import android.support.v4.app.Fragment;
import android.widget.Toast;

/** 
*
* @author phlofy
* @date 2015年12月5日 下午4:53:45 
*/
public class BaseFragemntv4 extends Fragment{
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
