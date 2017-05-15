package com.zjcql.base;

import java.util.List;

import com.zjcql.util.CollectionUtils;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.FindListener;

/** 
* 类似BaseActivity
* @author phlofy
* @date 2015年12月5日 下午4:51:58 
*/
public class BaseFragmentActivity extends FragmentActivity{
	protected BmobUserManager userManager;
	protected BmobChatManager manager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//可设置调试模式，当为true的时候，会在logcat的BmobChat下输出一些日志，包括推送服务是否正常运行，如果服务端返回错误，也会一并打印出来。方便开发者调试，正式发布应注释此句。
        BmobChat.DEBUG_MODE = true;
        //BmobIM SDK初始化--只需要这一段代码即可完成初始化
        BmobChat.getInstance(this).init(Config.applicationId);
        userManager = BmobUserManager.getInstance(this);
		manager = BmobChatManager.getInstance(this);
	}
	public void toast(String msg){
		try{
			Toast.makeText(BaseFragmentActivity.this, msg, Toast.LENGTH_SHORT).show();
		}
		catch(Exception e){
			System.out.println("toast空指针异常。");
		}
	}
	
	/** 用于登陆或者自动登陆情况下的用户资料及好友资料的检测更新
	  * @Title: updateUserInfos
	  * @Description: TODO
	  * @param  
	  * @return void
	  * @throws
	  */
	public void updateUserInfos(){
		//查询该用户的好友列表(这个好友列表是去除黑名单用户的哦),目前支持的查询好友个数为100，如需修改请在调用这个方法前设置BmobConfig.LIMIT_CONTACTS即可。
		//这里默认采取的是登陆成功之后即将好于列表存储到数据库中，并更新到当前内存中,
		userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						if(arg0==BmobConfig.CODE_COMMON_NONE){
							System.out.println(arg1);
						}else{
							System.out.println("查询好友列表失败："+arg1);
						}
					}

					@Override
					public void onSuccess(List<BmobChatUser> arg0) {
						// TODO Auto-generated method stub
						// 保存到application中方便比较
						MyApplication.getInstance().setContactList(CollectionUtils.list2map(arg0));
					}
				});
	}
}
