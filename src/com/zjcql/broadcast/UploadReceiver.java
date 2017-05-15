package com.zjcql.broadcast;

import com.zjcql.base.MyApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**  
* 上传任务的广播接听者
* @author phlofy
* @date 2015年9月13日 下午3:31:43 
*/
public class UploadReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getBooleanExtra("result", false)){
			Toast.makeText(MyApplication.getInstance(), "一任务成功上传", Toast.LENGTH_LONG).show();
		}
		else{
			Toast.makeText(MyApplication.getInstance(), "一任务上传失败", Toast.LENGTH_LONG).show();
		}
	}

}
