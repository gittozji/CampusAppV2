package com.zjcql.base;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zjcql.util.CollectionUtils;
import com.zjcql.util.SharePreferenceUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.zjcql.apparatus.UploadTaskQueue;
import com.zjcql.others.UploadTask;

import android.app.Application;
import android.graphics.Bitmap;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;

/** 
* 这是一个系统Application类。它存在于整个程序的生命周期。因此它的变量都是全局变量。运用这一点
* 我们可以在使用他来做“中介”。如Activity与Service之间的数据传递。还有一个重要的是，我会在这
* 保存一些网络操作的List集合方便启动service获取任务队列进行指定任务
* @author phlofy
* @date 2015年9月2日 下午2:06:03 
*/
public class MyApplication extends Application{
	
	private static MyApplication myApplication = null;
	
	private static UploadTaskQueue queue = null;
	
	private Map<String, BmobChatUser> contactList = new HashMap<String, BmobChatUser>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		//由于Application类本身已经单例，所以直接按以下处理即可。
		myApplication = this;
		initImageLoader();
		init();
	}
	private void init(){
		// 若用户登陆过，则先从好友数据库中取出好友list存入内存中
		if (BmobUserManager.getInstance(getApplicationContext()).getCurrentUser() != null) {
			// 获取本地好友user list到内存,方便以后获取好友list
			contactList = CollectionUtils.list2map(BmobDB.create(getApplicationContext()).getContactList());
		}
	}
	/**
	 * 获取Application实例
	 * @return
	 */
	public static MyApplication getInstance(){
		return myApplication;
	}
	/**
	 * 获取网络操作队列
	 * @return
	 */
	public static UploadTaskQueue getUploadTaskQueue() {
		if(queue == null) {
			queue = new UploadTaskQueue();
		}
		return queue;
	}	
	/**
	 * 获取内存中好友user list
	 * 
	 * @return
	 */
	public Map<String, BmobChatUser> getContactList() {
		return contactList;
	}
	/**
	 * 设置好友user list到内存中
	 * @param contactList
	 */
	public void setContactList(Map<String, BmobChatUser> contactList) {
		if (this.contactList != null) {
			this.contactList.clear();
		}
		this.contactList = contactList;
	}
	/**
	 * 初始化imageLoader
	 */
	public void initImageLoader(){
		File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
										.memoryCache(new LruMemoryCache(5*1024*1024))
										.memoryCacheSize(10*1024*1024)
										.discCache(new UnlimitedDiscCache(cacheDir))
										.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
										.build();
		ImageLoader.getInstance().init(config);
	}
	// 单例模式，才能及时返回数据
	SharePreferenceUtil mSpUtil;
	public static final String PREFERENCE_NAME = "_sharedinfo";

	public synchronized SharePreferenceUtil getSpUtil() {
		if (mSpUtil == null) {
			String currentId = BmobUserManager.getInstance(
					getApplicationContext()).getCurrentUserObjectId();
			String sharedName = currentId + PREFERENCE_NAME;
			mSpUtil = new SharePreferenceUtil(this, sharedName);
		}
		return mSpUtil;
	}
	/**
	 * 退出登录,清空缓存数据
	 */
	public void logout() {
		BmobUserManager.getInstance(getApplicationContext()).logout();
		setContactList(null);
	}
}
