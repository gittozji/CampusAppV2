package com.zjcql.fragment;

import java.util.List;

import com.zjcql.Interface.GeneralListener;
import com.zjcql.activity.EditUserActivity;
import com.zjcql.apparatus.ImageSelector;
import com.zjcql.base.BaseFragment;
import com.zjcql.base.MyApplication;
import com.zjcql.bmobbean.Answer;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;
import com.zjcql.dialog.EditDialog;
import com.zjcql.dialog.TwoSelectDialog;
import com.zjcql.others.UploadTask;
import com.zjcql.service.UploadTaskService;
import com.zjcql.util.MyBitmapUtil;
import com.zjcql.util.MyUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

/** 
*  问答发送Fragment
* @author phlofy
* @date 2016年1月25日 下午8:35:59 
*/
public class AnswerSendFragment extends BaseFragment implements OnClickListener,GeneralListener{

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
		 * 标题,内容
		 */
	EditText mTitle,mContent;
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
		mRootView = inflater.inflate(R.layout.fragment_answer_send, null);
		findView();
		
		/*************************GridView部分*****************************/
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
	
	private void findView() {
		mTitle = (EditText) mRootView.findViewById(R.id.title);
		mContent = (EditText) mRootView.findViewById(R.id.content);
		mGridView = (GridView) mRootView.findViewById(R.id.grid);
		mRootView.findViewById(R.id.floatbutton).setOnClickListener(this);
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
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.floatbutton:{
			if(BmobUser.getCurrentUser(getActivity()) != null)
				sendATask();
			else
				toast("还未登录");
			break;
		}
		}
	}
	
	
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
				Answer answer = new Answer();
				answer.setFromUser(user);
				answer.setTitle(getBundle().getString("title"));
				answer.setContent(getBundle().getString("content"));
				if(arg0 != null){
					// 有图片
					answer.setFiles(arg1.toArray(new String[arg1.size()]));
					String[] fileName = new String[arg0.size()];
					int i=0;
					for(BmobFile file : arg0){
						fileName[i++] = file.getUrl();
					}
					answer.setFilesName(fileName);
				}
				answer.save(MyApplication.getInstance(), new SaveListener() {
					
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
