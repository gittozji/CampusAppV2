package com.zjcql.service;

import java.util.Iterator;

import com.zjcql.Interface.GeneralListener;
import com.zjcql.apparatus.UploadTaskQueue;
import com.zjcql.base.MyApplication;
import com.zjcql.others.UploadTask;

import android.app.IntentService;
import android.content.Intent;

/** 
* 获取MyApplication的上传队列并开启网络上传工作
* @author phlofy
* @date 2015年9月13日 上午10:24:29 
*/
public class UploadTaskService extends IntentService{
	UploadTaskQueue queue;
	UploadTask task;
	public UploadTaskService() {
		super("UploadTaskService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		queue = MyApplication.getInstance().getUploadTaskQueue();
		task = queue.getTask();
		while(task != null){
			task.setOnGeneralListener(new GeneralListener() {
				
				@Override
				public void onReturn(boolean arg0, int arg1, String arg2) {
					if("任务成功完成".equals(arg2)){
						// 任务成功
						queue.finishTask(task);
						// 发送成功的广播
						Intent in = new Intent();
						in.putExtra("result", true);
						in.setAction("com.zjcql.Broadcast.action.UPLOAD_RECEIVER");
						sendBroadcast(in);
					}
					else{
						// 任务失败
						queue.failureTask(task);
						// 发送失败的广播
						Intent in = new Intent();
						in.putExtra("result", false);
						in.setAction("com.zjcql.Broadcast.action.UPLOAD_RECEIVER");
						sendBroadcast(in);
					}
				}
			});
			// 执行任务
			task.startWork();
			// 获取下一个任务
			task = queue.getTask();
		}
		
	}
	
}
