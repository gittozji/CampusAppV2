package com.zjcql.util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.widget.ImageView;
import cn.bmob.v3.datatype.BmobFile;

/** 
* 该类提供各种图片压缩方法，还提供了bitmap对象保存至sdcard的方法
* @author phlofy
* @date 2015年8月23日 下午10:28:15 
*/
public class MyBitmapUtil {
	
	/** 
     * 压缩图片 
     * @param bitmap 源图片 
     * @param width 想要的宽度 
     * @param height 想要的高度 
     * @param isAdjust 是否自动调整尺寸, true图片就不会拉伸，false严格按照你的尺寸压缩 
     * @return Bitmap 
     */  
    public static Bitmap reduce(Bitmap bitmap, int width, int height, boolean isAdjust) {  
        // 如果想要的宽度和高度都比源图片小，就不压缩了，直接返回原图  
        if (bitmap.getWidth() < width && bitmap.getHeight() < height) {return bitmap;}  
        // 根据想要的尺寸精确计算压缩比例, 方法详解：public BigDecimal divide(BigDecimal divisor, int scale, int roundingMode);  
        // scale表示要保留的小数位, roundingMode表示如何处理多余的小数位，BigDecimal.ROUND_DOWN表示自动舍弃  
        float sx = new BigDecimal(width).divide(new BigDecimal(bitmap.getWidth()), 4, BigDecimal.ROUND_DOWN).floatValue();  
        float sy = new BigDecimal(height).divide(new BigDecimal(bitmap.getHeight()), 4, BigDecimal.ROUND_DOWN).floatValue();  
        if (isAdjust) {// 如果想自动调整比例，不至于图片会拉伸  
            sx = (sx < sy ? sx : sy);sy = sx;// 哪个比例小一点，就用哪个比例  
        }  
        Matrix matrix = new Matrix();  
        matrix.postScale(sx, sy);// 调用api中的方法进行压缩，就大功告成了  
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
    } 
     
    /**
     * 获取图片格式
     * @param uri
     * @return
     */
    private synchronized static String getImageType(String uri) {
        String[] array = uri.split("\\.");
        return "." + array[array.length-1].toLowerCase();
    }
	/** 
	 * 创建一个指定大小的缩略图 
	 * @param bitmap 源文件(Bitmap类型) 
	 * @param width  压缩成的宽度 
	 * @param height 压缩成的高度 
	 */ 
	public static Bitmap extractThumbnail(Bitmap bitmap, int width, int height){
		return ThumbnailUtils.extractThumbnail(bitmap, width, height);
	}
	
	
	/**
	 * 创建一个指定大小居中的缩略图
	 * 
	 * @param bitmap 源文件(Bitmap类型)
	 * @param width  输出缩略图的宽度
	 * @param height 输出缩略图的高度
	 * @param options 如果options定义为OPTIONS_RECYCLE_INPUT,则回收@param source这个资源文件
	 * (除非缩略图等于@param bitmap)
	 * 
	 */ 
	public static Bitmap extractThumbnail(Bitmap bitmap, int width, int height, int options){
		return ThumbnailUtils.extractThumbnail(bitmap, width, height, options);
	}
	 

	/**
	 * 图片质量压缩
	 * @param bitmap 源文件
	 * @param maxSize 最大允许大小(单位KB)
	 * @return
	 */
	public static Bitmap compressImage(Bitmap bitmap ,int maxSize) {  
		   
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100;  
        System.out.println("压缩率为100时："+baos.toByteArray().length);
        while ( baos.toByteArray().length / 1024 > maxSize) {  //循环判断如果压缩后图片是否大于maxSizeKB,大于继续压缩   
        	options -= 10;//每次都减少10  
            baos.reset();//重置baos即清空baos  
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            System.out.println("压缩率为"+options+"："+baos.toByteArray().length);
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        return BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片    
    }
	
	// 图片压缩,用于getSmallBitmap()
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth) {  
	    // 源图片的宽度  
	    final int width = options.outWidth;  
	    int inSampleSize = 1;  
	    if (width > reqWidth) {  
	        // 计算出实际宽度和目标宽度的比率  
	        final int widthRatio = Math.round((float) width / (float) reqWidth);  
	        inSampleSize = widthRatio;  
	    }  
	    return inSampleSize;  
	}  
	/**
	 * 按比例压缩
	 * @param pathName 图片路径
	 * @param reqWidth 压缩后宽度
	 * @return
	 */
	public static Bitmap getSmallBitmap(String pathName,int reqWidth){
		if(BitmapFactory.decodeFile(pathName).getWidth() < reqWidth)
			return BitmapFactory.decodeFile(pathName);
		BitmapFactory.Options options = new BitmapFactory.Options();  
		options.inJustDecodeBounds = true;  
		BitmapFactory.decodeFile(pathName, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth);  
        /** 
         * 第二轮解析，负责具体压缩 
         */  
        // 使用获取到的inSampleSize值再次解析图片  
        options.inJustDecodeBounds = false;  
        return BitmapFactory.decodeFile(pathName, options); 
	}
	
	/**
	 * 保存bitmap对象于sd卡中
	 * @param filePath 保存的绝对路径
	 * @param mBitmap 
	 */
	public static boolean saveBitmap(String filePath,Bitmap mBitmap){
		File f = new File(filePath);
		try{
			f.createNewFile(); 
			FileOutputStream fOut = null;
			fOut = new FileOutputStream(f);
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
		}
		catch(Exception e){
			return false;
		}
		return true;
	}
	
	/**
	 * 展示图片
	 * @param context
	 * @param file
	 * @param view
	 */
	public static void displayImage(Context context,BmobFile file,ImageView view){
		if(file != null){
			ImageLoader.getInstance().displayImage(file.getFileUrl(context),view,getOptions(R.drawable.no_image,R.drawable.no_image,R.drawable.no_image));
	
		}
		else{
			view.setImageBitmap(null);
		}
		
	}
	/**
	 * 展示图片
	 * @param context
	 * @param url
	 * @param view
	 */
	public static void displayImage(Context context ,String url,ImageView view){
		ImageLoader.getInstance().displayImage(url,view,getOptions(R.drawable.no_image,R.drawable.no_image,R.drawable.no_image));
	}
	/**
	 * 展示头像
	 * @param context
	 * @param file
	 * @param view
	 */
	public static void displayIcon(Context context,User user,ImageView view){
		if(user != null && user.getAvatar() != null){
			ImageLoader.getInstance().displayImage(user.getAvatar(),view,getOptions(R.drawable.icon,R.drawable.icon,R.drawable.icon));
		}
		else{
			view.setImageResource(R.drawable.icon);
		}
		
	}
	/**
	 * 展示头像
	 * @param context
	 * @param url
	 * @param view
	 */
	public static void displayIcon(Context context ,String url,ImageView view){
		ImageLoader.getInstance().displayImage(url,view,getOptions(R.drawable.icon,R.drawable.icon,R.drawable.icon));
	}
	
	// imageLoader相关的方法，现在还未理解。
	public static DisplayImageOptions getOptions(int imageOnLoading,int imageForEmptyUri,int imageOnFail){
		return new DisplayImageOptions.Builder()
		.showImageOnLoading(imageOnLoading)
		.showImageForEmptyUri(imageForEmptyUri)
		.showImageOnFail(imageOnFail)
		.resetViewBeforeLoading(true)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}
}
