package com.zjcql.apparatus;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zjcql.Interface.GeneralListener;
import com.zjcql.Interface.HasReturn;
import com.zjcql.base.MyApplication;
import com.zjcql.util.MyBitmapUtil;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

/** 
* 图片选择器。
* 使用方法：
* 创建该类实例，调用getImageFrom...()方法，设置onHasReturn监听
* 然后在onActivityResult中调用doOnActivityResult方法，
* 最后在onReturn中获取image对象
* @author phlofy
* @date 2015年8月23日 下午6:31:23 
*/
public class ImageSelector{
	Context mContext;
		/**
		 * 存放图片选择器已经选择了的图片容器
		 */
	List<String> mImageList;
		/**
		 * 允许选择的图片最大数量
		 */
	int mMaxCount;
		/**
		 * 图片的uri
		 */
	Uri mPhotoOnSDCardUri;
		/**
		 * 回调接口
		 */
	GeneralListener gl;
		/**
		 * 得到的image对象存放在mImageList中的位置
		 * 如果为负则存放在mImageList的有效最后一位
		 */
	int putInImageIndex = -1;
	
		/**
		 * 相册返回值
		 */
	public final static int GALLERY_RESULT=666;
		/**
		 * 拍照返回值
		 */
	public final static int CAMERA_RESULT=777;
		/**
		 * 拍照剪裁返回值
		 */
	public final static int CAMERA_RESULT_CUT=888;
		/**
		 * 图库剪裁返回值
		 */
	public final static int GRLLERY_RESULT_CUT=555;
		/**
		 * 剪裁完成返回值
		 */
	public final static int CAMERA_RESULT_CUT_OVER=999;
		/**
		 * 未知错误
		 */
	public final static int UNKNOW_ERROR = 1;
		/**
		 * 没有sdcard
		 */
	public final static int NO_SDCARD = 2;
		/**
		 * 一切正常
		 */
	public final static int NORMAL = 4;
	
		/**
		 * 构造器 ，需要传递路径
		 * @param context
		 * @param maxCount
		 * @param path
		 */
	public ImageSelector(Context context, int maxCount, String path){
		this.mContext = context;
		this.mMaxCount = maxCount;
		mImageList = new ArrayList<String>();
	}
		/**
		 * 构造器，不传递保存路径，使用系统默认路径
		 * @param context
		 * @param maxCount
		 */
	public ImageSelector(Context context ,int maxCount) {
		this.mContext = context;
		this.mMaxCount = maxCount;
		mImageList = new ArrayList<String>();
	}
	
	/**
	 * 从图库中获取图片
	 * @param index 新图片存放在mImageList的位置,如果为负数则使用默认的填充规则
	 */
	public void getImageFromGallery(int index){
		this.putInImageIndex = index;
		Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        ((Activity)mContext).startActivityForResult(intent, GALLERY_RESULT);
	}
	/**
	 * 从图库中获取图片进行剪裁
	 * @param index 新图片存放在mImageList的位置,如果为负数则使用默认的填充规则
	 */
	public void getImageFromGalleryAndCut(int index){
		this.putInImageIndex = index;
		Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        ((Activity)mContext).startActivityForResult(intent, GRLLERY_RESULT_CUT);
	}
	 /**
	  * 从照相机中获取图片
	  * @param index 新图片存放在mImageList的位置,如果为负数则使用默认的填充规则
	  */
	public void getImageFromCamera(int index){
		if(!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			gl.onReturn(false, NO_SDCARD, null);
			return ;
		}
		this.putInImageIndex = index;
		((Activity)mContext).startActivityForResult(getIntentForCamera(),CAMERA_RESULT);
	}
	/**
	 * 从照相机中获取图片进行剪裁
	 * @param index 新图片存放在mImageList的位置,如果为负数则使用默认的填充规则
	 */
	public void getImageFromCameraAndCut(int index){
		if(!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			gl.onReturn(false, NO_SDCARD, null);
			return ;
		}
		this.putInImageIndex = index;
		//拍照后先剪裁再获取图片
		((Activity)mContext).startActivityForResult(getIntentForCamera(),CAMERA_RESULT_CUT);
	}
	/**
	 * 获取当前已选图片数量
	 * @return
	 */
	public int getImageListSize(){
		return mImageList.size();
	}
	/**
	 * 获取图片允许最大数量
	 * @return
	 */
	public int getMaxCount(){
		return mMaxCount;
	}
	/**
	 * 获取第index位置的图片路径
	 * @param index 
	 * @return 
	 */
	public String getImageByIndex(int index){
		if(mImageList.size() <= index){
			return null;
		}
		return mImageList.get(index);
	}
	/**
	 * 为开启Camera提供统一intent
	 * @return
	 */
	private Intent getIntentForCamera(){
			//设置开启拍照的intent
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			//设置图片保存绝对路径
		String mPhotoPath = getAppCacheDir() + namedImageByTime();
			// 通过路径new出文件对象
		File mPhotoFile = new File(mPhotoPath);
			// 判断是否存在该文件，不存在则创建
		if (!mPhotoFile.exists()) {
			try {
				mPhotoFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			// 得到该图片文件的uri
		mPhotoOnSDCardUri = Uri.fromFile(mPhotoFile);
		System.out.println(mPhotoOnSDCardUri.getPath()+"路径");
			// 设置图片输出位置为mPhotoOnSDCardUri
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoOnSDCardUri);
			// 拍照后获取此图片
		return intent;
	}
	/**
	 * 开启图片剪裁activity
	 * @param uri
	 */
	private void startCutImage(Uri uri){
		Intent in = new Intent("com.android.camera.action.CROP");
			//需要裁减的图片格式
		in.setDataAndType(uri, "image/*");
			//允许裁减
		in.putExtra("crop", "true");
			//剪裁后ImageView显时图片的宽
		in.putExtra("outputX", 250);
			//剪裁后ImageView显时图片的高
		in.putExtra("outputY", 250);
			//设置剪裁框的宽高比例
		in.putExtra("aspectX", 1);
		in.putExtra("aspectY", 1);
		in.putExtra("return-data", true);
		in.putExtra("outputFormat", "PNG");
		((Activity)mContext).startActivityForResult(in, CAMERA_RESULT_CUT_OVER);
	}
	/**
	 * 在AcvityviResult中处理返回数据方法
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 * @return 反馈信息
	 */
	public int doOnActivityResult(int requestCode, int resultCode, Intent data){
		Bitmap bitmap = null;
		switch(requestCode){
		// 拍照
		case CAMERA_RESULT:
			try {System.out.println("准备获取");
				// 质量压缩图片
				bitmap = MyBitmapUtil.reduce(BitmapFactory.decodeFile(mPhotoOnSDCardUri.getPath()), 800, 480, true);
				String name = getAppCacheDir()+"cmprss_"+namedImageByTime();
				if(bitmap != null && MyBitmapUtil.saveBitmap(name, bitmap)){
					putInImageList(name);
				}
				else
					gl.onReturn(false, UNKNOW_ERROR, null);
			} 
			catch (Exception e) {
				gl.onReturn(false, UNKNOW_ERROR, null);
				return UNKNOW_ERROR;
			}	
			break;
		// 相册
		case GALLERY_RESULT:
			if(data != null){
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = mContext.getContentResolver().query(data.getData(),filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				
				// 得到图片并压缩
				bitmap = MyBitmapUtil.reduce(BitmapFactory.decodeFile(picturePath), 800, 480, true);
				String name = getAppCacheDir()+"cmprss_"+namedImageByTime();
				if(MyBitmapUtil.saveBitmap(name, bitmap)){
					putInImageList(name);
				}
				else{
					gl.onReturn(false, UNKNOW_ERROR, null);
				}
			}
			else
				gl.onReturn(false, UNKNOW_ERROR, null);
			break;
		// 拍照剪裁
		case CAMERA_RESULT_CUT:
			startCutImage(mPhotoOnSDCardUri);
			break;
		// 相册剪裁
		case GRLLERY_RESULT_CUT:
			if(data != null){
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = mContext.getContentResolver().query(data.getData(),filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				
				startCutImage(Uri.fromFile(new File(picturePath)));
			}
			break;
		// 剪裁之后
		case CAMERA_RESULT_CUT_OVER:
			try {
				// 得到图片并压缩
				bitmap = (Bitmap) data.getExtras().get("data");
				bitmap = MyBitmapUtil.reduce(bitmap, 800, 480, true);
				
				String name = getAppCacheDir()+"cut_"+namedImageByTime();
				if(MyBitmapUtil.saveBitmap(name, bitmap)){
					System.out.println("成功保存小图片");
				}
				else{
					System.out.println("保存小图片失败");
				}
				putInImageList(name);
			} 
			catch (Exception e) {
				gl.onReturn(false, UNKNOW_ERROR, null);
				return UNKNOW_ERROR;
			}	
			break;
		}
		if(bitmap != null)
			bitmap.recycle();
		return NORMAL;
	}
	
	private void putInImageList(String path){
		if(putInImageIndex >= 0){
			if(mImageList.size() > putInImageIndex){
				mImageList.set(putInImageIndex, path);
			}
			else{
				mImageList.add(path);
			}
		}
		else{
			mImageList.add(path);
		}
		gl.onReturn(true, NORMAL, null);
	}
	
	/**
	 * 根据创建时间为图片命名
	 * @return 返回文件名
	 */
	private String namedImageByTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'CampusAppV2'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".png";
	}
	/**
	 * 获取内部缓存路径
	 */
	public String getAppCacheDir() {
		String path;
		File cacheDir;
		if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			cacheDir = mContext.getExternalCacheDir();
		}
		else{
			cacheDir = mContext.getCacheDir();
		}
        path = cacheDir.getPath();
        path+="/";
        System.out.println(path+"     这是新路径");
        return path;
	}
	/**
	 * 规则是成功返回（true，NORMAL,null）,失败返回（false,UNKNOW_ERROR,null）
	 * @param gl
	 */
	public void setGeneralListener(GeneralListener gl){
		this.gl = gl;
	}
}
