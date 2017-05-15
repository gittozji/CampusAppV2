package com.zjcql.fragment;

import java.io.File;
import java.util.zip.Inflater;

import com.zjcql.Interface.GeneralListener;
import com.zjcql.activity.EditUserActivity;
import com.zjcql.apparatus.ImageSelector;
import com.zjcql.base.BaseFragment;
import com.zjcql.base.MyApplication;
import com.zjcql.bmobbean.Notices;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;
import com.zjcql.dialog.EditDialog;
import com.zjcql.dialog.SelectColorDialog;
import com.zjcql.dialog.TwoSelectDialog;
import com.zjcql.others.UploadTask;
import com.zjcql.service.UploadTaskService;
import com.zjcql.util.MyBitmapUtil;
import com.zjcql.util.MyUtil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/** 
* 发布通知的Fragment
* @author phlofy
* @date 2015年10月17日 下午7:24:30 
*/
public class NoticeSendFragment extends BaseFragment implements OnClickListener,GeneralListener{
		/**
		 * 根View
		 */
	View mRootView;
	EditText mTitle;
	EditText mContent;
	ImageView mImage;
	ImageSelector mImageSelector;
	int colorIndex = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageSelector = new ImageSelector(getActivity(), 1);
		mImageSelector.setGeneralListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_notice_send, null);
		findView();
		return mRootView;
	}

	private void findView() {
		mTitle = (EditText) mRootView.findViewById(R.id.title);
		mContent = (EditText) mRootView.findViewById(R.id.content);
		mImage = (ImageView) mRootView.findViewById(R.id.image);
		mImage.setOnClickListener(this);
		mRootView.findViewById(R.id.send).setOnClickListener(this);
		mRootView.findViewById(R.id.select).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.image:{
			new TwoSelectDialog(getActivity(), "拍照", "图册"){

				@Override
				public void onClickEventForButton1() {
					mImageSelector.getImageFromCamera(0);
				}

				@Override
				public void onClickEventForButton2() {
					mImageSelector.getImageFromGallery(0);
				}
				
			}.show();
			break;
		}
		case R.id.select:{
			new SelectColorDialog(getActivity(),"选择颜色") {
				
				@Override
				public void onClick(int index,String color) {
					mTitle.setTextColor(Color.parseColor(color));
					colorIndex = index;
				}
			}.show();
			break;
		}
		case R.id.send:{
			if(BmobUser.getCurrentUser(getActivity()) == null){
				toast("还未登录");
				return;
			}
			if(mTitle.getText().toString().length() < 1 || mTitle.getText().toString().length() > 30){
				toast("标题长度应在1～30字符之间");
				return;
			}
			mNotifyParent.returnCode("切换到浏览界面");
			Bundle bundle = new Bundle();
			bundle.putString("title", mTitle.getText().toString());
			bundle.putInt("index", colorIndex);
			bundle.putString("content", mContent.getText().toString());
			bundle.putInt("countOfPath", mImageSelector.getImageListSize());
			for(int i=0;i<mImageSelector.getImageListSize();i++) {
				bundle.putString("path"+i, mImageSelector.getImageByIndex(i));
			}
			UploadTask task = new UploadTask(bundle) {
				User user;
				@Override
				public void startWork() {
					user = BmobUser.getCurrentUser(MyApplication.getInstance(), User.class);
					if(user == null){
						generalListener.onReturn(true, 0, "没有用户");
						return;
					}
					if(getBundle().getInt("countOfPath") > 0){
						// 有图片上传任务
						final BmobFile bmobFile = new BmobFile(new File(getBundle().getString("path0")));
						bmobFile.uploadblock(getActivity(), new UploadFileListener() {
							
							
							@Override
							public void onSuccess() {
								sendForText(bmobFile.getFileUrl(getActivity()), bmobFile.getUrl());
							}
							
							@Override
							public void onFailure(int arg0, String arg1) {
								generalListener.onReturn(true, 0, "上传图片失败："+arg1);
							}
						});
					}
					else{
						// 无图片上传任务
						sendForText(null,null);
					}
				}
				private void sendForText(String arg0, String arg1){
					Notices notices = new Notices();
					notices.setUser(user);
					if(arg0 != null){
						notices.setFile(arg0);
						notices.setFileName(arg1);
					}
					notices.setTitle(getBundle().getString("title"));
					notices.setIndex(getBundle().getInt("index"));
					notices.setContent(getBundle().getString("content"));
					notices.save(MyApplication.getInstance(), new SaveListener() {
						
						@Override
						public void onSuccess() {
							generalListener.onReturn(true, 0, "任务成功完成");
						}
						
						@Override
						public void onFailure(int arg0, String arg1) {
							generalListener.onReturn(true, 0, "任务未能完成："+arg1);
						}
					});
				}
			};
			
			MyApplication.getInstance().getUploadTaskQueue().addTask(task);
			Intent intent = new Intent(getActivity(), UploadTaskService.class);
			MyApplication.getInstance().startService(intent);
			toast("正在上传...");
			
			break;
		}
		default:break;
		}
	}

	@Override
	public void onReturn(boolean arg0, int arg1, String arg2) {
		switch (arg1) {
		case ImageSelector.NORMAL:
			MyBitmapUtil.displayImage(getActivity(), "file://"+mImageSelector.getImageByIndex(0), mImage);
			break;
		case ImageSelector.NO_SDCARD:
			new EditDialog(getActivity(),"没有sdcard，尝试从图库中获取图片",false) {
				
				@Override
				public void onClickEventForButton2(String text) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onClickEventForButton1() {
					// TODO Auto-generated method stub
					
				}
			}.show();
			break;
		case ImageSelector.UNKNOW_ERROR:
			new EditDialog(getActivity(),"系统图片选择器已经关闭",false) {
				
				@Override
				public void onClickEventForButton2(String text) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onClickEventForButton1() {
					// TODO Auto-generated method stub
					
				}
			}.show();
			break;
		default:
			break;
		}	
	}
	
	/**
	 * Activity通过该方法传递onActivityResult方法返回的数据
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void fakeOnActivityResult(int requestCode, int resultCode, Intent data){
		System.out.println("fakeOnActivityResult");
		mImageSelector.doOnActivityResult(requestCode, resultCode, data);
	}
}	
