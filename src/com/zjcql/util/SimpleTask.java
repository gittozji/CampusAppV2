package com.zjcql.util;
/** 
* 简单的上传工具。其运行机制是开启一条线程运行传入的代码
* @author phlofy
* @date 2015年9月5日 下午8:46:55 
*/
public abstract class SimpleTask {
	int statue = FREE;
	public final static int FREE = 0;
	public final static int UPLOADING = 1;
	public final static int SUCCEED = 2;
	public final static int FAILURE = 3;
	public SimpleTask(){
		
	}
	public void start(){
		new Thread(){
			public void run(){
				setStatue(UPLOADING);
				doInNewThread();
			}
		}.start();
	}
	public int getStatue(){
		return statue;
	}
	public void setStatue(int statue){
		this.statue = statue;
	}
	public abstract void doInNewThread();
}
