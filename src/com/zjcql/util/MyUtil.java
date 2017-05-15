package com.zjcql.util;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zjcql.base.MyApplication;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.widget.ImageButton;
import android.widget.ImageView;
import cn.bmob.v3.datatype.BmobFile;

/** 
* 我能想到的工具集
* @author phlofy
* @date 2015年10月6日 下午2:10:30 
*/
public class MyUtil {
	/**
	 * 判断两个User对象id是否一样。特殊的：当两个都为空时返回false。
	 * @param u1
	 * @param u2
	 * @return true为一样，false为不一样
	 */
	public static boolean isEqual(User u1,User u2){
		if(u1 != null && u2 != null && u1.getObjectId().equals(u2.getObjectId())){
			return true;
		}
		return false;
	}
	
	 /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  

    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }
    
    /**
	 * 获得屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context)
	{
		//获取windowmanager
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * 获得屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context)
	{
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}
}
