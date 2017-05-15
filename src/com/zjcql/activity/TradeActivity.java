package com.zjcql.activity;

import com.zjcql.Interface.HasReturn;
import com.zjcql.Interface.NotifyParent;
import com.zjcql.base.BaseActivity;
import com.zjcql.base.BaseFragment;
import com.zjcql.campusappv2.R;
import com.zjcql.fragment.TradeBuyBrowseFragment;
import com.zjcql.fragment.TradeCartFragment;
import com.zjcql.fragment.TradeGoodsBrowseFragment;
import com.zjcql.fragment.TradeGoodsIssueFragment;
import com.zjcql.fragment.TradeGoodsSeekFragment;
import com.zjcql.fragment.TradeMyStoreFragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 交易模块的Activity，是控制层，控制各个Fragment。
 * @author phlofy
 *
 */
public class TradeActivity extends BaseActivity implements OnClickListener,NotifyParent{
	DrawerLayout mDrawerLayout;
		/**
		 * 我的微店Fragment
		 */
	TradeMyStoreFragment mTradeMyStoreFragment;
		/**
		 * 商品发布Fragment
		 */
	TradeGoodsIssueFragment mTradeGoodsIssueFragment;
		/**
		 * 购物车Fragment
		 */
	TradeCartFragment mTradeCartFragment;
		/**
		 * 商品浏览Fragment
		 */
	TradeGoodsBrowseFragment mTradeGoodsBrowseFragment;
		/**
		 * 求购信息Fragment
		 */
	TradeBuyBrowseFragment mTradeBuyBrowseFragment;
		/**
		 * 商品查找Fragment
		 */
	TradeGoodsSeekFragment mTradeGoodsSeekFragment;
		/**
		 * 导航标题
		 */
	TextView mTitle;
	
//	LinearLayout mLl;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trade);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		
		// 菜单栏宽度为屏幕宽度的0.618(黄金分割点)
//		mLl = (LinearLayout)this.findViewById(R.id.layout_menu);
//		android.view.ViewGroup.LayoutParams lp =mLl.getLayoutParams();
//		lp.width=(int) (0.618f*ScreenUtils.getScreenWidth(TradeActivity.this));
		
		initEvents();
		
		findView();
		
		
		mTradeGoodsBrowseFragment = new TradeGoodsBrowseFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame, mTradeGoodsBrowseFragment).commit();
	}
	
	private void findView(){
		findViewById(R.id.menu).setOnClickListener(this);
		mTitle = (TextView) findViewById(R.id.title);
		findViewById(R.id.mine).setOnClickListener(this);
		findViewById(R.id.carts).setOnClickListener(this);
		findViewById(R.id.browse).setOnClickListener(this);
		findViewById(R.id.seek).setOnClickListener(this);
		findViewById(R.id.send).setOnClickListener(this);
		findViewById(R.id.carts).setOnClickListener(this);
		findViewById(R.id.browse2).setOnClickListener(this);
	}
	
	//处理Drawer事件
	private void initEvents()
	{
		mDrawerLayout.setDrawerListener(new DrawerListener()
		{
			@Override
			public void onDrawerStateChanged(int newState) {
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
			}

			@Override
			public void onDrawerOpened(View drawerView) {
			}

			@Override
			public void onDrawerClosed(View drawerView) {
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Fragment中无法调用onActivityResult方法，所以将该方法的数据回调给frgment
		if(mTradeGoodsIssueFragment.toString().equals("TradeGoodsIssueFragment")){
			mTradeGoodsIssueFragment.fakeOnActivityResult(requestCode, resultCode, data);
		}
	}
	
	private void getTradeMyStoreFragment (FragmentTransaction tran){
		if(mTradeMyStoreFragment != null){
			tran.show(mTradeMyStoreFragment);
		}
		else{
			mTradeMyStoreFragment = new TradeMyStoreFragment();
			tran.add(R.id.frame, mTradeMyStoreFragment);
		}
	}
	
	private void getTradeGoodsIssueFragment(FragmentTransaction tran){
		if(mTradeGoodsIssueFragment != null){
			tran.show(mTradeGoodsIssueFragment);
		}
		else{
			mTradeGoodsIssueFragment = new TradeGoodsIssueFragment();
			mTradeGoodsIssueFragment.setOnNotifyParentListener(this);
			tran.add(R.id.frame, mTradeGoodsIssueFragment);
		}
	}
	
	private void getTradeCartFragment(FragmentTransaction tran){
		if(mTradeCartFragment != null){
			tran.show(mTradeCartFragment);
		}
		else{
			mTradeCartFragment = new TradeCartFragment();
			tran.add(R.id.frame, mTradeCartFragment);
		}
	}
	private void getTradeGoodsBrowseFragment(FragmentTransaction tran){
		if(mTradeGoodsBrowseFragment != null){
			tran.show(mTradeGoodsBrowseFragment);
			if(isChange(TradeGoodsSeekFragment.KEY_WORD_STRING, TradeGoodsSeekFragment.CLASSIFY_STRING, mTradeGoodsBrowseFragment.key_word_string, mTradeGoodsBrowseFragment.classify_string)){
				mTradeGoodsBrowseFragment.firstRefresh();
			}
		}
		else{
			mTradeGoodsBrowseFragment = new TradeGoodsBrowseFragment();
			tran.add(R.id.frame, mTradeGoodsBrowseFragment);
		}
	}
	
	private void getTradeBuyBrowseFragment(FragmentTransaction tran){
		if(mTradeBuyBrowseFragment != null){
			tran.show(mTradeBuyBrowseFragment);
			if(isChange(TradeGoodsSeekFragment.KEY_WORD_STRING, TradeGoodsSeekFragment.CLASSIFY_STRING, mTradeBuyBrowseFragment.key_word_string, mTradeBuyBrowseFragment.classify_string)){
				mTradeBuyBrowseFragment.firstRefresh();
			}
		}
		else{
			mTradeBuyBrowseFragment = new TradeBuyBrowseFragment();
			tran.add(R.id.frame, mTradeBuyBrowseFragment);
		}
	}
	
	private void getTradeGoodsSeekFragment(FragmentTransaction tran){
		if(mTradeGoodsSeekFragment != null){
			tran.show(mTradeGoodsSeekFragment);
		}
		else{
			mTradeGoodsSeekFragment = new TradeGoodsSeekFragment();
			mTradeGoodsSeekFragment.setOnNotifyParentListener(this);
			tran.add(R.id.frame, mTradeGoodsSeekFragment);
		}
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		case R.id.menu:{
			mDrawerLayout.openDrawer(Gravity.LEFT);
			break;
		}
		case R.id.mine:{
			FragmentTransaction tran = getFragmentManager().beginTransaction();
			hideFragment(tran);
			getTradeMyStoreFragment(tran);
			mTitle.setText("我的微店");
			mDrawerLayout.closeDrawer(Gravity.LEFT);
			tran.commit();
			break;
		}
		case R.id.send:{
			FragmentTransaction tran = getFragmentManager().beginTransaction();
			hideFragment(tran);
			getTradeGoodsIssueFragment(tran);
			mTitle.setText("商品发布");
			mDrawerLayout.closeDrawer(Gravity.LEFT);
			tran.commit();
			break;
		}
		case R.id.carts:{
			FragmentTransaction tran = getFragmentManager().beginTransaction();
			hideFragment(tran);
			getTradeCartFragment(tran);
			mTitle.setText("购物车");
			mDrawerLayout.closeDrawer(Gravity.LEFT);
			tran.commit();
			break;
		}
		case R.id.browse:{
			FragmentTransaction tran = getFragmentManager().beginTransaction();
			hideFragment(tran);
			getTradeGoodsBrowseFragment(tran);
			mTitle.setText("商品浏览");
			mDrawerLayout.closeDrawer(Gravity.LEFT);
			tran.commit();
			break;
		}
		case R.id.browse2:{
			FragmentTransaction tran = getFragmentManager().beginTransaction();
			hideFragment(tran);
			getTradeBuyBrowseFragment(tran);
			mTitle.setText("求购信息浏览");
			mDrawerLayout.closeDrawer(Gravity.LEFT);
			tran.commit();
			break;
		}
		case R.id.seek:{
			FragmentTransaction tran = getFragmentManager().beginTransaction();
			hideFragment(tran);
			getTradeGoodsSeekFragment(tran);
			mTitle.setText("条件搜索");
			mDrawerLayout.closeDrawer(Gravity.LEFT);
			tran.commit();
			break;
		}
		default:break;
		}
	}
	
	void hideFragment(FragmentTransaction transaction){
		if(mTradeMyStoreFragment != null)
			transaction.hide(mTradeMyStoreFragment);
		if(mTradeGoodsIssueFragment != null)
			transaction.hide(mTradeGoodsIssueFragment);
		if(mTradeCartFragment != null)
			transaction.hide(mTradeCartFragment);
		if(mTradeGoodsBrowseFragment != null)
			transaction.hide(mTradeGoodsBrowseFragment);
		if(mTradeBuyBrowseFragment != null)
			transaction.hide(mTradeBuyBrowseFragment);
		if(mTradeGoodsSeekFragment != null)
			transaction.hide(mTradeGoodsSeekFragment);
	}

	@Override
	public void returnCode(String string) {
		if("切换到浏览界面".equals(string)){
			mDrawerLayout.openDrawer(Gravity.LEFT);
			FragmentTransaction tran = getFragmentManager().beginTransaction();
			hideFragment(tran);
			getTradeGoodsBrowseFragment(tran);
			mTitle.setText("商品浏览");
			mDrawerLayout.closeDrawer(Gravity.LEFT);
			tran.commit();
		}
		
		if("条件搜索商品".equals(string) || "搜索全部商品".equals(string)){
			mDrawerLayout.closeDrawer(Gravity.LEFT);
			FragmentTransaction tran = getFragmentManager().beginTransaction();
			hideFragment(tran);
			getTradeGoodsBrowseFragment(tran);
			tran.commit();
			mTradeGoodsBrowseFragment.firstRefresh();
			mTitle.setText("商品浏览");
		}
		
		if("条件搜索求购信息".equals(string) || "搜索全部求购信息".equals(string)){
			mDrawerLayout.closeDrawer(Gravity.LEFT);
			FragmentTransaction tran = getFragmentManager().beginTransaction();
			hideFragment(tran);
			getTradeBuyBrowseFragment(tran);
			tran.commit();
			new Thread(){
				public void run(){
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}mTradeBuyBrowseFragment.firstRefresh();
				}
			}.start();
			
			mTitle.setText("求购信息浏览");
		}
	}
	
	/**
	 * 判断s1<->c1,s2<->c2是否没有改变
	 * @param s1
	 * @param s2
	 * @param c1
	 * @param c2
	 * @return 改变返回true，否则返回false
	 */
	private boolean isChange(String s1,String s2,String c1,String c2){
		if(((s1 != null && c1 !=null) && s1.equals(c1)) || (s1 == null && c1 == null)){
			if(((s2 != null && c2 !=null)&&s2.equals(c2)) || (s2 == null && c2 == null)){
				return false;
			}
		}
		return true;
	}
}
