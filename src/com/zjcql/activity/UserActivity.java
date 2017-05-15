package com.zjcql.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zjcql.Interface.GeneralListener;
import com.zjcql.apparatus.Poper;
import com.zjcql.base.BaseActivity;
import com.zjcql.base.MyApplication;
import com.zjcql.bmobbean.Goods;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;
import com.zjcql.dialog.EditDialog;
import com.zjcql.popup.LoadingPopup;
import com.zjcql.popup.PictureShowerPopup;
import com.zjcql.popup.UpAndDwonPopup;
import com.zjcql.rebuildview.CircleImageView;
import com.zjcql.util.MyBitmapUtil;
import com.zjcql.util.MyUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobACL;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/** 
* 用户中心
* @author phlofy
* @date 2015年8月26日 下午9:49:31 
*/
public class UserActivity extends BaseActivity implements OnClickListener{
		/**
		 * 当前用户
		 */
	User mUser;
		/**
		 * false为非自己账号
		 * true为是自己账号
		 */
	boolean isCurrent = false;
		/**
		 * 用户头像
		 */
	CircleImageView mIcon;

	TextView mName,mPop,mCollege,mGrade,mSignature,mCurious,mMail,mTel;;
		/**
		 * 性别
		 */
	ImageView mSex;
	
	LoadingPopup mLoadingPopup;
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0x11){
				try{
					loadLocalData();
					wantPop = false;
					mLoadingPopup.dismiss();
				}
				catch(Exception e){};
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);

		final Intent in = getIntent();
		if(in.getStringExtra("objectId") != null){
			showPopWindow();
			BmobQuery<User> query = new BmobQuery<User>();
			query.addWhereEqualTo("objectId", in.getStringExtra("objectId"));
			//判断是否有缓存，该方法必须放在查询条件（如果有的话）都设置完之后再来调用才有效，就像这里一样。
			boolean isCache = query.hasCachedResult(UserActivity.this,User.class);
			if(isCache){//此为举个例子，并不一定按这种方式来设置缓存策略
			    query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
			}
			else{
			    query.setCachePolicy(CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
			}
			System.out.println("is finding  "+isCache);
			query.findObjects(UserActivity.this, new FindListener<User>() {
				
				@Override
				public void onSuccess(List<User> arg0) {
					mUser = arg0.get(0);
					mHandler.sendEmptyMessage(0x11);
				}
				
				@Override
				public void onError(int arg0, String arg1) {
					System.out.println(in.getStringExtra("objectId")+"    good");
					finish();
				}
			});
		}
		else{
			mUser = (User)in.getSerializableExtra("user");
			loadLocalData();
		}
		
	}
	
	
	
	private void findView(){
		findViewById(R.id.back).setOnClickListener(this);
		mIcon = (CircleImageView) findViewById(R.id.icon);
		mIcon.setOnClickListener(this);
		mName = (TextView) findViewById(R.id.name);
		mSex = (ImageView) findViewById(R.id.sex);
		mCollege = (TextView) findViewById(R.id.college);
		mGrade = (TextView) findViewById(R.id.grade);
		mSignature = (TextView) findViewById(R.id.signature);
		mCurious = (TextView) findViewById(R.id.curious);
		mMail = (TextView) findViewById(R.id.mail);
		mTel = (TextView) findViewById(R.id.tel);
		mPop = (TextView) findViewById(R.id.pop);
		
		if(!isCurrent){ //非本地账号
			findViewById(R.id.text).setVisibility(View.GONE);
			findViewById(R.id.menu).setVisibility(View.GONE);
			mPop.setOnClickListener(this);
			findViewById(R.id.goods_show_layout).setOnClickListener(this);
			findViewById(R.id.contact).setOnClickListener(this);
			
		}
		else{
			findViewById(R.id.goods_show_layout).setVisibility(View.GONE);
			findViewById(R.id.contact).setVisibility(View.GONE);
			findViewById(R.id.text).setOnClickListener(this);
			findViewById(R.id.menu).setOnClickListener(this);
		}
		
	}
	
	private void loadLocalData(){
		if(MyUtil.isEqual(BmobUser.getCurrentUser(UserActivity.this,User.class), mUser)){
			isCurrent = true;
		}
		else{
			isCurrent = false;
		}
		findView();
		
		// 头像
		MyBitmapUtil.displayIcon(UserActivity.this, mUser, mIcon);
		// 性别
		if(mUser.getSex() != null && mUser.getSex() == true){
			mSex.setBackgroundResource(R.drawable.man);
		}
		else{
			mSex.setBackgroundResource(R.drawable.woman);
		}
		
		// pop值
		Poper.getInstance().getPop(UserActivity.this, mUser, new GeneralListener() {
			
			@Override
			public void onReturn(boolean arg0, int arg1, String arg2) {
				if(arg0)
					mPop.setText("Pop: "+arg2);
			}
		});
		
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
	public void onClick(View v) {
		switch(v.getId()){
		// 返回按钮
		case R.id.back:{
			finish();			
			break;
		}
		// 编辑按钮
		case R.id.text:{
			Intent intent = new Intent();
			intent.setClass(UserActivity.this, EditUserActivity.class);
			startActivity(intent);
			break;
		}
		// 注销账号
		case R.id.menu:{
			EditDialog ed = new EditDialog(UserActivity.this,"确认退出该用户吗？",false) {
				
				@Override
				public void onClickEventForButton2(String text) {
					User.logOut(UserActivity.this);
					finish();
				}
				
				@Override
				public void onClickEventForButton1() {
				}
			};
			ed.show();
			break;
		}
		case R.id.pop:{
			UpAndDwonPopup up= new UpAndDwonPopup(UserActivity.this, mPop,"赞","贬");
			up.setGeneralListener(new GeneralListener() {
				
				@Override
				public void onReturn(boolean arg0, int arg1, String arg2) {
					if(arg1 == 1){
						Poper.getInstance().increme(false,UserActivity.this, mUser, 10, new GeneralListener() {
							
							@Override
							public void onReturn(boolean arg0, int arg1, String arg2) {
								Poper.getInstance().getPop(UserActivity.this, mUser, new GeneralListener() {
									
									@Override
									public void onReturn(boolean arg0, int arg1, String arg2) {
										if(arg0)
											mPop.setText("Pop: "+arg2);
									}
								});
							}
						});
					}
					else{
						Poper.getInstance().increme(false,UserActivity.this, mUser, -10, new GeneralListener() {
							
							@Override
							public void onReturn(boolean arg0, int arg1, String arg2) {
								Poper.getInstance().getPop(UserActivity.this, mUser, new GeneralListener() {
									
									@Override
									public void onReturn(boolean arg0, int arg1, String arg2) {
										if(arg0)
											mPop.setText("Pop: "+arg2);
									}
								});
							}
						});
					}
				}
			});
			up.show();
			break;
		}
		case R.id.goods_show_layout:{
			Intent intent = new Intent();
			intent.setClass(UserActivity.this, TradeMyStoreActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("user", mUser);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
		case R.id.contact:{
			Intent intent = new Intent(this, ChatActivity.class);
			intent.putExtra("user", mUser);
			startActivity(intent);
			break;
		}
		case R.id.icon:{
			if(mUser.getAvatar() == null){
				new PictureShowerPopup(UserActivity.this,mIcon,"",true).show();
			}
			else{
				new PictureShowerPopup(UserActivity.this,mIcon,mUser.getAvatar(),true).show();
			}
			break;
		}
		default:break;
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(isCurrent){
			mUser = BmobUser.getCurrentUser(UserActivity.this, User.class);
			loadLocalData();
		}
	}
	/**
	 * 还想要开启pop吗
	 */
	boolean wantPop = false;
	/**
	 * 过一小段时间在显示pop
	 */
	private void showPopWindow(){
		wantPop = true;
		mLoadingPopup = new LoadingPopup(UserActivity.this, findViewById(R.id.back),"正在加载中...");
		/*****************以下代码用来循环检测activity是否初始化完毕***************/  
		Runnable r = new Runnable() {

			@Override
			public void run() {
				if(wantPop == true && null != ((Activity)UserActivity.this).getWindow().getDecorView().getWindowToken()) {
						mLoadingPopup.show();
						mHandler.removeCallbacks(this);
				}
				else {
					mHandler.postDelayed(this, 5);
				}
			}
		};
		mHandler.post(r);
	}
	
}