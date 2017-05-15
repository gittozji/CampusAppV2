package com.zjcql.activity;

import com.zjcql.base.BaseActivity;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;
import com.zjcql.fragment.TradeGoodsBrowseFragment;
import com.zjcql.fragment.TradeMyStoreFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/** 
* TradeMyStore的Activity
* @author phlofy
* @date 2015年10月6日 下午2:20:34 
*/
public class TradeMyStoreActivity extends BaseActivity{
	/**
	 * 我的微店Fragment
	 */
	TradeMyStoreFragment mTradeMyStoreFragment;
	TextView title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mystore);
		mTradeMyStoreFragment = new TradeMyStoreFragment();
		Intent in = getIntent();
		Bundle bundle = in.getExtras();
		mTradeMyStoreFragment.setArguments(bundle);
		getFragmentManager().beginTransaction().replace(R.id.frame, mTradeMyStoreFragment).commit();
		title = (TextView) findViewById(R.id.title);
		title.setText(((User)bundle.getSerializable("user")).getNick()+"的商店");
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
}
