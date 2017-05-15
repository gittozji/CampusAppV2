package com.zjcql.others;

import java.util.HashMap;

import com.zjcql.Interface.GeneralListener;

import android.os.Bundle;

/** 
* 上传任务。通过Bundle打包需上传的数据，然后重写startWork（）方法完成具体上传操作
* @author phlofy
* @date 2015年9月12日 下午11:07:33 
*/
public abstract class UploadTask{
		/**
		 * 任务需要处理的数据
		 */
	Bundle bundle;
		/**
		 * 任务状态
		 */
	int state;
		/**
		 * 标签，用于指明该任务的来源
		 */
	String tag;
		/**
		 * 回调接口
		 */
	public GeneralListener generalListener;

	public static final int RUNNING = 0;
	public static final int NEW = 1;
	public static final int FINISHED = 2;
	public static final int FAILURE = 3;
	
	public UploadTask(Bundle bundle){
		this.bundle = bundle;
		this.state = NEW;
	}
	
	/**
	 * 开启工作
	 * @return true为任务成功，false为任务失败 
	 */
	public abstract void  startWork();
	
	public Bundle getBundle() {
		return bundle;
	}
	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}
	public void setState(int state){
		this.state = state;
	}
	public int getState() {
		return state;
	}
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	/**
	 * 
	 * @param gl 规则是。。。
	 */
	public void setOnGeneralListener(GeneralListener gl){
		this.generalListener = gl;
	}
}
