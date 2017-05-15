package com.zjcql.activity;

import java.io.File;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zjcql.Interface.GeneralListener;
import com.zjcql.Interface.HasReturn;
import com.zjcql.apparatus.ImageSelector;
import com.zjcql.base.BaseActivity;
import com.zjcql.base.MyApplication;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;
import com.zjcql.dialog.EditDialog;
import com.zjcql.dialog.TwoSelectDialog;
import com.zjcql.popup.LoadingPopup;
import com.zjcql.popup.PictureShowerPopup;
import com.zjcql.rebuildview.CircleImageView;
import com.zjcql.util.MyBitmapUtil;
import com.zjcql.util.SimpleTask;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/** 
* 编辑用户信息
* @author phlofy
* @date 2015年9月5日 上午12:50:10 
*/
public class EditUserActivity extends BaseActivity implements OnClickListener,GeneralListener{

	User mUser;
	CircleImageView mIcon;
	TextView mName,mSex,mCollege,mGrade,mSignature,mCurious,mMail,mTel;
	SimpleTask mSimpleTask;
	LoadingPopup mLoadingPopup;
	String willBeDelete;
	/**
	 * 图片选择器
	 */
	ImageSelector mImageSelector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_user);
		mImageSelector = new ImageSelector(EditUserActivity.this, 1);
		mImageSelector.setGeneralListener(this);
		findView();
		loadLocalData();
	}

	private void findView() {
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.yes).setOnClickListener(this);
		findViewById(R.id.one).setOnClickListener(this);
		findViewById(R.id.two).setOnClickListener(this);
		findViewById(R.id.three).setOnClickListener(this);
		findViewById(R.id.four).setOnClickListener(this);
		findViewById(R.id.five).setOnClickListener(this);
		findViewById(R.id.six).setOnClickListener(this);
		findViewById(R.id.seven).setOnClickListener(this);
		findViewById(R.id.eight).setOnClickListener(this);
		findViewById(R.id.night).setOnClickListener(this);
		mIcon = (CircleImageView) findViewById(R.id.icon);
		mIcon.setOnClickListener(this);
		mName = (TextView) findViewById(R.id.name);
		mSex = (TextView) findViewById(R.id.sex);
		mCollege = (TextView) findViewById(R.id.college);
		mGrade = (TextView) findViewById(R.id.grade);
		mSignature = (TextView) findViewById(R.id.signature);
		mCurious = (TextView) findViewById(R.id.curious);
		mMail = (TextView) findViewById(R.id.mail);
		mTel = (TextView) findViewById(R.id.tel);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		// 返回按钮
		case R.id.back:{
			finish();
			break;
		}
		// 图片点击事件
		case R.id.icon:{
			if(mImageSelector.getImageListSize() == 0)
				new PictureShowerPopup(EditUserActivity.this,mIcon,mUser.getAvatar()).show();
			else
				new PictureShowerPopup(EditUserActivity.this,mIcon,"file://"+mImageSelector.getImageByIndex(0)).show();
			break;
		}
		// 保存按钮
		case R.id.yes:{
			// 开启提示正在保存界面
			mLoadingPopup = new LoadingPopup(EditUserActivity.this, findViewById(R.id.yes));
			mLoadingPopup.show();
			// 开启一个新线程进行网络操作
			mSimpleTask = new SimpleTask() {
				@Override
				public void doInNewThread() {
					if(mImageSelector.getImageListSize() > 0){
						uploadInCludeIcon();
					}
					else{
						upload();
					}
				}
			};
			mSimpleTask.start();
			break;
		}
		case R.id.one:{
			new TwoSelectDialog(EditUserActivity.this, "拍照", "图册"){
				@Override
				public void onClickEventForButton1() {
					mImageSelector.getImageFromCameraAndCut(0);
				}
				@Override
				public void onClickEventForButton2() {
					mImageSelector.getImageFromGalleryAndCut(0);
				}
				
			}.show();
			break;
		}
		case R.id.two:{
			// 开启简单的编辑窗口
			new EditDialog(EditUserActivity.this,mName.getText().toString()){

				@Override
				public void onClickEventForButton1() {
				}
				@Override
				public void onClickEventForButton2(String text) {
					mName.setText(text);
				}
			}.show();
			break;
		}
		case R.id.three:{
			new TwoSelectDialog(EditUserActivity.this,"男生","女生") {
				@Override
				public void onClickEventForButton1() {
					mSex.setText("男");
				}
				@Override
				public void onClickEventForButton2() {
					mSex.setText("女");
				}
			}.show();
			break;
		}
		case R.id.four:{
			// 这是暂时的。
			new EditDialog(EditUserActivity.this,mCollege.getText().toString()){

				@Override
				public void onClickEventForButton1() {
					
				}

				@Override
				public void onClickEventForButton2(String text) {
					mCollege.setText(text);
				}
				
			}.show();
			break;
		}
		case R.id.five:{
			new EditDialog(EditUserActivity.this,mGrade.getText().toString()){

				@Override
				public void onClickEventForButton1() {
					
				}

				@Override
				public void onClickEventForButton2(String text) {
					mGrade.setText(text);
				}
				
			}.show();
			break;
		}
		case R.id.six:{
			new EditDialog(EditUserActivity.this,mSignature.getText().toString()){

				@Override
				public void onClickEventForButton1() {
					
				}

				@Override
				public void onClickEventForButton2(String text) {
					mSignature.setText(text);
				}
				
			}.show();
			break;
		}
		case R.id.seven:{
			new EditDialog(EditUserActivity.this,mCurious.getText().toString()){

				@Override
				public void onClickEventForButton1() {
					
				}

				@Override
				public void onClickEventForButton2(String text) {
					mCurious.setText(text);
				}
				
			}.show();
			break;
		}
		case R.id.eight:{
			new EditDialog(EditUserActivity.this,mMail.getText().toString()){

				@Override
				public void onClickEventForButton1() {
					
				}

				@Override
				public void onClickEventForButton2(String text) {
					mMail.setText(text);
				}
				
			}.show();
			break;
		}
		case R.id.night:{
			new EditDialog(EditUserActivity.this,mTel.getText().toString()){

				@Override
				public void onClickEventForButton1() {
					
				}

				@Override
				public void onClickEventForButton2(String text) {
					mTel.setText(text);
				}
				
			}.show();
			break;
		}
		default:break;
		}
	}
	/**
	 * 加载界面数据
	 */
	private void loadLocalData(){
		mUser = BmobUser.getCurrentUser(this,User.class);
		// 头像
		MyBitmapUtil.displayIcon(EditUserActivity.this, mUser, mIcon);
		// 性别
		if(mUser.getSex() != null){
			if(mUser.getSex() == true){
				mSex.setText("男");
			}
			else{
				mSex.setText("女");
			}
		}
		else{// 不做处理
		}
		
		// 昵称
		mName.setText(mUser.getNick());
		
		// 校区
		mCollege.setText(mUser.getCollege());
		
		// 年级
		mGrade.setText(mUser.getGrade());
		
		// 签名
		mSignature.setText(mUser.getSignature());
		
		// 爱好
		mCurious.setText(mUser.getCurious());
		
		// 邮箱
		mMail.setText(mUser.getMail());
		
		// 手机
		mTel.setText(mUser.getTel());
		
	}

	@Override
	public void onReturn(boolean arg0, int arg1, String arg2) {
		switch (arg1) {
		case ImageSelector.NORMAL:
			MyBitmapUtil.displayIcon(EditUserActivity.this, "file://"+mImageSelector.getImageByIndex(0), mIcon);
			break;
		case ImageSelector.NO_SDCARD:
			new EditDialog(EditUserActivity.this,"没有sdcard，尝试从图库中获取图片",false) {
				
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
			new EditDialog(EditUserActivity.this,"系统图片选择器已经关闭",false) {
				
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
	// 有图片的网络操作
	private void uploadInCludeIcon(){
		final BmobFile bmobFile = new BmobFile(new File(mImageSelector.getImageByIndex(0)));
		bmobFile.uploadblock(EditUserActivity.this, new UploadFileListener() {
			
			@Override
			public void onSuccess() {
				// 得到之前头像的文件名
				willBeDelete = mUser.getFileName();
				
				User user = new User();
				// Avatar为头像url
				user.setAvatar(bmobFile.getFileUrl(EditUserActivity.this));
				user.setFileName(bmobFile.getUrl());
				user.setNick(mName.getText().toString());
				if("男".equals(mSex.getText().toString())){
					user.setSex(true);
				}
				else{
					user.setSex(false);
				}
				user.setCollege(mCollege.getText().toString());
				user.setGrade(mGrade.getText().toString());
				user.setSignature(mSignature.getText().toString());
				user.setCurious(mCurious.getText().toString());
				user.setMail(mMail.getText().toString());
				user.setTel(mTel.getText().toString());
				
				user.update(EditUserActivity.this, mUser.getObjectId(), new UpdateListener() {
					@Override
					public void onSuccess() {
						mSimpleTask.setStatue(SimpleTask.SUCCEED);
						toast("保存成功");
						System.out.println("保存成功");
						
						// 为了减少服务器的占用空间，及时删除之前文件
						BmobFile bmobFile = new BmobFile();
						bmobFile.setUrl(mUser.getFileName());
						bmobFile.delete(EditUserActivity.this, new DeleteListener() {
							
							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
								System.out.println("EditUserActivity 图片删除成功");
							}
							
							@Override
							public void onFailure(int arg0, String arg1) {
								// TODO Auto-generated method stub
								
							}
						});
						finish();
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						mSimpleTask.setStatue(SimpleTask.FAILURE);
						toast("失败"+arg1);
						System.out.println("失败"+arg1);
						mLoadingPopup.dismiss();
					}
				});
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				mSimpleTask.setStatue(SimpleTask.FAILURE);
				toast("上传失败"+arg1);
				System.out.println("上传失败:"+arg1);
				mLoadingPopup.dismiss();
			}
		});
	}
	// 没有图片的网络操作
	private void upload(){
		User user = new User();
		user.setNick(mName.getText().toString());
		if("男".equals(mSex.getText().toString())){
			user.setSex(true);
		}
		else{
			user.setSex(false);
		}
		user.setCollege(mCollege.getText().toString());
		user.setGrade(mGrade.getText().toString());
		user.setSignature(mSignature.getText().toString());
		user.setCurious(mCurious.getText().toString());
		user.setMail(mMail.getText().toString());
		user.setTel(mTel.getText().toString());
		
		user.update(EditUserActivity.this, mUser.getObjectId(), new UpdateListener() {
			@Override
			public void onSuccess() {
				mSimpleTask.setStatue(SimpleTask.SUCCEED);
				toast("保存成功");
				System.out.println("保存成功");
				finish();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				mSimpleTask.setStatue(SimpleTask.FAILURE);
				toast("失败"+arg1);
				System.out.println("失败"+arg1);
				try{
					mLoadingPopup.dismiss();
				}catch(Exception e){}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
			mImageSelector.doOnActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event) {
		System.out.println(keyCode);
		if(mSimpleTask != null && mSimpleTask.getStatue() == SimpleTask.UPLOADING && keyCode == 4){
			new EditDialog(EditUserActivity.this,"还未保存，是否仍要退出？",false) {
				@Override
				public void onClickEventForButton1() {
					return;
				}
				@Override
				public void onClickEventForButton2(String text) {
					finish();
				}
			}.show();
		}
		else
			super.onKeyDown(keyCode, event);
		return true;
	}
}
