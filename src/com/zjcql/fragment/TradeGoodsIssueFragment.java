package com.zjcql.fragment;

import java.util.List;

import com.zjcql.Interface.GeneralListener;
import com.zjcql.Interface.HasReturn;
import com.zjcql.Interface.NotifyParent;
import com.zjcql.activity.EditUserActivity;
import com.zjcql.apparatus.ImageSelector;
import com.zjcql.base.BaseFragment;
import com.zjcql.base.MyApplication;
import com.zjcql.bmobbean.Goods;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;
import com.zjcql.dialog.EditDialog;
import com.zjcql.dialog.TwoSelectDialog;
import com.zjcql.others.UploadTask;
import com.zjcql.popup.KeyBoardPopup;
import com.zjcql.popup.ListSelectPopup;
import com.zjcql.service.UploadTaskService;
import com.zjcql.util.MyBitmapUtil;
import com.zjcql.util.MyUtil;

import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.mtp.MtpObjectInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 商品发布的Fragment
 * @author phlofy
 *
 */
public class TradeGoodsIssueFragment extends BaseFragment implements OnClickListener,GeneralListener{
		/**
		 * 根View
		 */
	View mRootView;
		/**
		 * 存放三个ImageView的GridView
		 */
	GridView mGridView;
		/**
		 * mGridView的适配器
		 */
	BaseAdapter mAdapter;
		/**
		 * 标题、内容、金额、成色、分类的TextView
		 */
	TextView mTitle,mContent,mPrice,mState,mClassify,mType;
		/**
		 * 图片选择器
		 */
	ImageSelector mImageSelector;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化图片选择器
		mImageSelector = new ImageSelector(getActivity(), 3);
		mImageSelector.setGeneralListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_trade_goods_issuse, null);
		findView();
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// 获取屏幕的宽度，用于动态控制三个image的大小
				int mScreenWidth = MyUtil.getScreenWidth(getActivity()) - MyUtil.dip2px(getActivity(),26);
				AbsListView.LayoutParams mLayoutParams =  new AbsListView.LayoutParams(mScreenWidth/3, mScreenWidth/3);
				ImageView mImageView = new ImageView(getActivity());
				mImageView.setScaleType(ScaleType.CENTER_CROP);
				mImageView.setLayoutParams(mLayoutParams);
				// 设置背景色为透明
				if(position < mImageSelector.getImageListSize()){
					MyBitmapUtil.displayImage(getActivity(), "file://"+mImageSelector.getImageByIndex(position), mImageView);
				}
				else{
					mImageView.setImageResource(R.drawable.border);
				}
				return mImageView;
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				return null;
			}
			
			@Override
			public int getCount() {
				// 没有图片时仍有一个"+"按钮图片
				int size = mImageSelector.getImageListSize();
				if(size == mImageSelector.getMaxCount())
					return size;
				else
					return mImageSelector.getImageListSize()+1;
			}
		};
		//处理GridView
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 点击的是"+"按钮图片
				if(position == mImageSelector.getImageListSize()){
					new TwoSelectDialog(getActivity(), "拍照", "图册"){

						@Override
						public void onClickEventForButton1() {
							
							mImageSelector.getImageFromCamera(-1);
							
						}

						@Override
						public void onClickEventForButton2() {
							
							mImageSelector.getImageFromGallery(-1);
							
						}
						
					}.show();
				}
			}
		});
		return mRootView;
	}
	/**
	 * 通过id获取View
	 */
	private void findView() {
		mTitle = (TextView) mRootView.findViewById(R.id.title);
		mContent = (TextView) mRootView.findViewById(R.id.content);
		mGridView = (GridView) mRootView.findViewById(R.id.grid);
		mPrice = (TextView) mRootView.findViewById(R.id.price);
		mState = (TextView) mRootView.findViewById(R.id.state);
		mType = (TextView) mRootView.findViewById(R.id.type);
		mClassify = (TextView) mRootView.findViewById(R.id.classify);
		mRootView.findViewById(R.id.lay_price).setOnClickListener(this);
		mRootView.findViewById(R.id.lay_state).setOnClickListener(this);
		mRootView.findViewById(R.id.lay_classify).setOnClickListener(this);
		mRootView.findViewById(R.id.floatbutton).setOnClickListener(this);
		mRootView.findViewById(R.id.select).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		// 成色按钮
		case R.id.lay_state:{
			String[] mItemString = {"全新","未拆封","九成新","八成新","二手"};
			ListSelectPopup mListSelectPopup = new ListSelectPopup(getActivity(), mRootView,"选择成色", mItemString);
			mListSelectPopup.setGeneralListener(new GeneralListener() {
				
				@Override
				public void onReturn(boolean arg0, int arg1, String arg2) {
					mState.setText(arg2);
				}
			});
			mListSelectPopup.show();
			break;
		}
		// 价格按钮
		case R.id.lay_price:{
			// 在这创建一个数字输入软盘
		    new	KeyBoardPopup(getActivity(),mRootView,mPrice).show();
			break;
		}
		// 分类按钮
		case R.id.lay_classify:{
			// 为SelectClassifyView对象传递进去的两个分类列表
			String[] mStringLeft = {"电子设备","学习资料","零食","床上用品","运动器材","衣物","小装饰品","护肤品","其他","学习资料","零食","床上用品","运动器材","衣物","小装饰品","护肤品","其他"};
			String[][] mStringRight = {{"手机","电脑","洗衣机","电风扇","路由器","其他电子设备"},
										{"课外书本","四六级资料","考研资料","实验报告","课内书本","其他学习资料","四六级资料","考研资料","实验报告","课内书本","其他学习资料","四六级资料","考研资料","实验报告","课内书本","其他学习资料","四六级资料","考研资料","实验报告","课内书本","其他学习资料"},
										{"饮料","泡面","其他零食"},
										{"被子","枕头","抱枕","其他床上用品"},
										{"球","球拍","其他运动器材"},
										{"裙子","裤子","上衣","其他衣物"},
										{"毛绒装饰品","塑料装饰品","其他装饰品"},
										{"女生专用","男士专用","其他护肤品"},
										{"其他"},
										{"课外书本","四六级资料","考研资料","实验报告","课内书本","其他学习资料","四六级资料","考研资料","实验报告","课内书本","其他学习资料","四六级资料","考研资料","实验报告","课内书本","其他学习资料","四六级资料","考研资料","实验报告","课内书本","其他学习资料"},
										{"饮料","泡面","其他零食"},
										{"被子","枕头","抱枕","其他床上用品"},
										{"球","球拍","其他运动器材"},
										{"裙子","裤子","上衣","其他衣物"},
										{"毛绒装饰品","塑料装饰品","其他装饰品"},
										{"女生专用","男士专用","其他护肤品"},
										{"其他"}
										};
			ListSelectPopup mListSelectPopup2 = new ListSelectPopup(getActivity(),mRootView, "选择分类", mStringLeft, mStringRight);
			mListSelectPopup2.setGeneralListener(new GeneralListener() {
				
				@Override
				public void onReturn(boolean arg0, int arg1, String arg2) {
					mClassify.setText(arg2);
				}
			});
			mListSelectPopup2.show();
			break;
		}
		case R.id.floatbutton:{
			if(BmobUser.getCurrentUser(getActivity()) != null)
				sendATask();
			else
				toast("还未登录");
			break;
		}
		case R.id.select:{
			TwoSelectDialog select = new TwoSelectDialog(getActivity(),"出售","求购") {
				@Override
				public void onClickEventForButton1() {
					mType.setText("出售");
				}
				@Override
				public void onClickEventForButton2() {
					mType.setText("求购");
				}
			};
			select.show();
			break;
		}
		default: break;
		}
	}
	
	/**
	 * 发布商品
	 */
	private void sendATask() {
		if(mTitle.getText().toString().length() < 1 || mTitle.getText().toString().length() > 30){
			toast("标题长度应在1～30字符之间");
			return;
		}
		mNotifyParent.returnCode("切换到浏览界面");
		Bundle bundle = new Bundle();
		bundle.putString("title", mTitle.getText().toString());
		bundle.putString("content", mContent.getText().toString());
		bundle.putInt("countOfPath", mImageSelector.getImageListSize());
		for(int i=0;i<mImageSelector.getImageListSize();i++) {
			bundle.putString("path"+i, mImageSelector.getImageByIndex(i));
		}
		bundle.putDouble("price", Double.parseDouble(mPrice.getText().toString().substring(0, mPrice.getText().toString().length()-1)));
		bundle.putString("state", mState.getText().toString());
		bundle.putString("classify", mClassify.getText().toString());
		
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
					final String[] paths = new String[getBundle().getInt("countOfPath")];
					for(int i=0;i<getBundle().getInt("countOfPath");i++){
						paths[i] = getBundle().getString("path"+i);
					}
					// 批量上传图片
					// 批量上传图片
					BmobFile.uploadBatch(getActivity(), paths, new UploadBatchListener() {
						
						@Override
						public void onSuccess(List<BmobFile> arg0, List<String> arg1) {
							if(arg1.size() == paths.length){
								sendForText(arg0,arg1);
							}
						}
						
						@Override
						public void onProgress(int arg0, int arg1, int arg2, int arg3) {
							System.out.println("Progress "+arg0+" "+arg1+" "+arg2+" "+arg3);
						}
						
						@Override
						public void onError(int arg0, String arg1) {
							generalListener.onReturn(true, 0, "上传图片失败："+arg1);
						}
					});
					
				}
				else{
					// 无图片上传任务
					sendForText(null,null);
				}
			}
			
			private void sendForText(List<BmobFile> arg0, List<String> arg1){
				Goods goods = new Goods();
				goods.setUser(user);
				goods.setTitle(getBundle().getString("title"));
				goods.setContent(getBundle().getString("content"));
				if(arg0 != null){
					// 有图片
					goods.setFiles(arg1.toArray(new String[arg1.size()]));
					String[] fileName = new String[arg0.size()];
					int i=0;
					for(BmobFile file : arg0){
						fileName[i++] = file.getUrl();
					}
					goods.setFilesName(fileName);
				}
				goods.setPrice(getBundle().getDouble("price"));
				goods.setState(getBundle().getString("state"));
				goods.setClassify(getBundle().getString("classify"));
				if("出售".equals(mType.getText().toString())){
					goods.setIsBuy(false);
				}
				else{
					goods.setIsBuy(true);
				}
				goods.save(MyApplication.getInstance(), new SaveListener() {
					
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
	}

	@Override
	public void onReturn(boolean arg0, int arg1, String arg2) {
		switch (arg1) {
		case ImageSelector.NORMAL:
			mAdapter.notifyDataSetChanged();
			System.out.println(mImageSelector.getImageByIndex(0));
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
	 * 为Activity提供当前是实例化哪个Fragment
	 */
	@Override
	public String toString() {
		return "TradeGoodsIssueFragment";
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
