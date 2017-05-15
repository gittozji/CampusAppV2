package com.zjcql.apparatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.zjcql.others.UploadTask;

/** 
* 上传任务队列管理
* @author phlofy
* @date 2015年9月13日 上午10:07:29 
*/
public class UploadTaskQueue {
	/**
	 * 上传任务队列
	 */
	private List<UploadTask> queue = null;
	
	public List<UploadTask> getQueue() {
		return queue;
	}

	public void setQueue(List<UploadTask> queue) {
		this.queue = queue;
	}

	public UploadTaskQueue() {
		queue = new LinkedList<UploadTask>();
	}
	
	/**
	 * 增加一项任务
	 * @param task
	 */
	public synchronized void addTask(UploadTask task) {   
        if (task != null) {   
            queue.add(task);   
        }   
    }   
	 	
	/**
	 * 完成任务后从任务队列中删除
	 * @param task
	 */
    public synchronized void finishTask(UploadTask task) {  
    	if (task != null) {   
    		task.setState(UploadTask.FINISHED);  
    		queue.remove(task);   
        }   
    }   
    
    /**
     * 任务执行失败，将任务移至任务队列末尾
     * @param task
     */
    public synchronized void failureTask(UploadTask task){
    	if(task != null) {
    		task.setState(UploadTask.FAILURE);
    		queue.remove(task);
    		queue.add(task);
    	}
    }
	/**
	 * 取得一项待完成的任务
	 * @return
	 */
    public synchronized UploadTask getTask() {   
        Iterator<UploadTask> it = queue.iterator();   
        UploadTask task;   
        while (it.hasNext()) {   
            task = it.next();   // 寻找一个新建的任务  
            // 只有是新任务或者是失败的任务
            if (UploadTask.NEW == task.getState()) {   
            	// 把任务状态置为运行中 
            	task.setState(UploadTask.RUNNING); 
                return task;   
            }   
        }   
        return null;    
    }   
}
