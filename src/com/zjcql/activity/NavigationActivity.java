package com.zjcql.activity;

import java.util.List;
import java.util.Map;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zjcql.Interface.GeneralListener;
import com.zjcql.apparatus.Poper;
import com.zjcql.base.BaseActivity;
import com.zjcql.base.MyApplication;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;
import com.zjcql.rebuildview.CircleImageView;
import com.zjcql.util.MyBitmapUtil;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;

/**
 * 主界面(模块导航界面)的Activity
 * 负责开启各个模块Activity
 * @author phlofy
 *
 */
public class NavigationActivity extends BaseActivity implements OnClickListener{
	CircleImageView mIcon_image;
	TextView mIcon_top,mIcon_bottom;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation);
		findView();
		doIcon();
	}

	private void findView(){
		findViewById(R.id.trade).setOnClickListener(this);
		findViewById(R.id.icon).setOnClickListener(this);
		findViewById(R.id.answer).setOnClickListener(this);
		findViewById(R.id.said).setOnClickListener(this);
		findViewById(R.id.notice).setOnClickListener(this);
		findViewById(R.id.contact).setOnClickListener(this);
		mIcon_image = (CircleImageView) findViewById(R.id.icon_image);
		mIcon_top = (TextView) findViewById(R.id.icon_top);
		mIcon_bottom = (TextView) findViewById(R.id.icon_bottom);
	}
	
	private void doIcon() {
		User user = BmobUser.getCurrentUser(this,User.class);
		// 头像
		MyBitmapUtil.displayIcon(NavigationActivity.this,user,mIcon_image);
		if(user != null && user.getNick() != null)
			mIcon_top.setText(user.getNick());
		else
			mIcon_top.setText("路人");
		if(user != null && user.getSignature() != null)
			mIcon_bottom.setText(user.getSignature());
		else
			mIcon_bottom.setText("点击我，不再当路人啦");
	}

	// 由于设置OnClickListener的控件数量比较多，因此将监听处理事件放在一起，统一管理
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch(v.getId()){
		case R.id.trade:{
			intent.setClass(NavigationActivity.this, TradeActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.icon:{
			if(BmobUser.getCurrentUser(this,User.class) != null){
				intent.setClass(NavigationActivity.this, UserActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("user", BmobUser.getCurrentUser(this,User.class));
				intent.putExtras(bundle);
			}
			else{
				intent.setClass(NavigationActivity.this, LogActivity.class);
			}
			startActivity(intent);
			break;
		}
		case R.id.answer:{
			intent.setClass(NavigationActivity.this, AnswerActivity.class);
			startActivity(intent);
			break;		
		}
		case R.id.said:{
			intent.setClass(NavigationActivity.this, SayActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.contact:{
			if(BmobUser.getCurrentUser(NavigationActivity.this) == null){	
				toast("还未登录");
				break;
			}
			intent.setClass(NavigationActivity.this, ContactActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.notice:{
			intent.setClass(NavigationActivity.this, NoticeActivity.class);
			startActivity(intent);
			break;		
		}
		default:break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		doIcon();
	}
	
}

