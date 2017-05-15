package com.zjcql.activity;

import com.zjcql.base.BaseActivity;
import com.zjcql.bmobbean.Says;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;
import com.zjcql.fragment.CommentSayFragemnt;
import com.zjcql.fragment.TradeMyStoreFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/** 
* 评论吐槽
* @author phlofy
* @date 2016年1月14日 上午10:22:20 
*/
public class CommentSayActivity extends BaseActivity{
	CommentSayFragemnt mCommentSayFragemnt;
	TextView mTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment_say);
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("评论墙");
		mCommentSayFragemnt = new CommentSayFragemnt();
		Intent in = getIntent();
		Bundle bundle = in.getExtras();
		mCommentSayFragemnt.setArguments(bundle);
		getFragmentManager().beginTransaction().replace(R.id.frame, mCommentSayFragemnt).commit();
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	
	
}
