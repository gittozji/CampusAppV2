package com.zjcql.activity;

import java.util.ArrayList;
import java.util.List;

import com.zjcql.Interface.OnRefresh;
import com.zjcql.adapter.ChatAdapter;
import com.zjcql.apparatus.Puller;
import com.zjcql.base.BaseActivity;
import com.zjcql.bmobbean.User;
import com.zjcql.broadcast.MyMessageReceiver;
import com.zjcql.campusappv2.R;
import com.zjcql.util.CommonUtils;
import com.zjcql.util.MyUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.PushListener;

/** 
* 聊天界面
* @author phlofy
* @date 2015年12月20日 下午2:53:41 
*/
public class ChatActivity extends BaseActivity implements OnClickListener,EventListener{
	/**
	 * View控件
	 */
	TextView mTitle;
	EditText mEditText;
	Button send;
	
	/**
	 * 目标对象
	 */
	BmobChatUser targetUser;
	String targetId;
	/**
	 * 聊天记录表第几页
	 */
	private static int MsgPagerNum;
	
	
	/**
	 * 刷新布局
	 */
	SwipeRefreshLayout mSwipeLayout;
	ChatAdapter mAdapter;
	ListView mListView;
	Puller mPuller;
	
	
	
	NewBroadcastReceiver  receiver;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		MsgPagerNum = 0;
		findView();
		
		/***********************************聊天数据管理**************************************/
		manager = BmobChatManager.getInstance(this);
		targetUser = (BmobChatUser) getIntent().getSerializableExtra("user");
		targetId = targetUser.getObjectId();
		
		/***********************************刷新布局**************************************/
		mPuller = new Puller(ChatActivity.this, mSwipeLayout, mListView);
		mPuller.setLoadCanWork(false);
		mPuller.setOnRefreshListener(new OnRefresh() {
			
			@Override
			public void onRefresh() {
				getOlderData();
			}

		});
		
		//注册广播接收器
		initNewMessageBroadCast();
		initView();
		
	}
	/**
	 * 相关数据显示
	 */
	private void initView() {
		mTitle.setText("与"+targetUser.getNick()+"对话");
		// 初始化消息
		initOrLoad();
	}

	private void findView() {
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
		mListView = (ListView) findViewById(R.id.listview);
		mEditText = (EditText) findViewById(R.id.edit);
		mTitle = (TextView) findViewById(R.id.bar_title);
		send = (Button) findViewById(R.id.send);
		send.setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
	}

	/**
	 * 加载消息历史，从数据库中读出
	 */
	private List<BmobMsg> initMsgData() {
		List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId,MsgPagerNum);
		return list;
	}
	/**
	 * 获取历史数据
	 */
	private void getOlderData() {
		MsgPagerNum++;
		int total = BmobDB.create(ChatActivity.this).queryChatTotalCount(targetId);
		int currents = mAdapter.getCount();
		if (total <= currents) {
			toast("聊天记录加载完了哦!");
		} else {
			List<BmobMsg> msgList = initMsgData();
			mAdapter.setList(msgList);
			mListView.setSelection(mAdapter.getCount() - currents);
		}
		mPuller.complete();
	}
	
	/**
	 * 发送消息添加进Adapter
	 * @Title: refreshMessage
	 * @Description: TODO
	 * @param @param message
	 * @return void
	 * @throws
	 */
	private void addSendMessageToAdapter(BmobMsg msg) {
		// 更新界面
		mAdapter.add(msg);
		mListView.setSelection(mAdapter.getCount() - 1);
		mEditText.setText("");
	}
	/**
	 * 类比于OnLoadingListener
	 * @Title: initOrRefresh
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initOrLoad() {
		if (mAdapter != null) {
			if (MyMessageReceiver.mNewNum != 0) {// 用于更新当在聊天界面锁屏期间来了消息，这时再回到聊天页面的时候需要显示新来的消息
				int news=  MyMessageReceiver.mNewNum;//有可能锁屏期间，来了N条消息,因此需要倒叙显示在界面上
				int size = initMsgData().size();
				for(int i=(news-1);i>=0;i--){
					mAdapter.add(initMsgData().get(size-(i+1)));// 添加最后一条消息到界面显示
				}
				mListView.setSelection(mAdapter.getCount() - 1);
			} else {
				mAdapter.notifyDataSetChanged();
			}
		} 
		else {
			mAdapter = new ChatAdapter(this, initMsgData(),targetUser);
			mListView.setAdapter(mAdapter);
			mListView.setSelection(mAdapter.getCount() - 1);
		}
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0x12) {
				BmobMsg message = (BmobMsg) msg.obj;
				String uid = message.getBelongId();
				BmobMsg m = BmobChatManager.getInstance(ChatActivity.this).getMessage(message.getConversationId(), message.getMsgTime());
				if (!uid.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
					return;
				mAdapter.add(m);
				// 定位
				mListView.setSelection(mAdapter.getCount() - 1);
				//取消当前聊天对象的未读标示
				BmobDB.create(ChatActivity.this).resetUnread(targetId);
			}
		}
	};
	

	/**
	 * 初始化广播
	 */
	private void initNewMessageBroadCast(){
		// 注册接收消息广播
		receiver = new NewBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
		//设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
		intentFilter.setPriority(5);
		registerReceiver(receiver, intentFilter);
	}
	/**
	 * 新消息广播接收者
	 * 
	 */
	private class NewBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String from = intent.getStringExtra("fromId");
			String msgId = intent.getStringExtra("msgId");
			String msgTime = intent.getStringExtra("msgTime");
			// 收到这个广播的时候，message已经在消息表中，可直接获取
			if(TextUtils.isEmpty(from)&&TextUtils.isEmpty(msgId)&&TextUtils.isEmpty(msgTime)){
				BmobMsg msg = BmobChatManager.getInstance(ChatActivity.this).getMessage(msgId, msgTime);
				if (!from.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
					return;
				//添加到当前页面
				mAdapter.add(msg);
				// 定位
				mListView.setSelection(mAdapter.getCount() - 1);
				//取消当前聊天对象的未读标示
				BmobDB.create(ChatActivity.this).resetUnread(targetId);
			}
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}
	
	/************************************一些回调接口*******************************************/
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.back:{
			finish();
			break;
		}
		case R.id.add:{
			getOlderData();
			break;
		}
		case R.id.send:{
			User user = BmobUser.getCurrentUser(ChatActivity.this, User.class);
			if(user != null){
				hideSoftInputView();
				final String msg = mEditText.getText().toString();
				if (msg.equals("")) {
					toast("请输入发送消息!");
					return;
				}
				boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
				if (!isNetConnected) {
					toast("网络异常！");
					// return;
				}
				// 组装BmobMessage对象
				final BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, msg);
				message.setExtra("Bmob");
				// 发送消息添加进Adapter(不一定已经发送完成)
				addSendMessageToAdapter(message);
				// 默认发送完成，将数据保存到本地消息表和最近会话表中
				message.setStatus(BmobConfig.STATUS_SEND_START);
				manager.sendTextMessage(targetUser, message, new PushListener() {
					
					@Override
					public void onSuccess() {
						for (BmobMsg msg : mAdapter.getList()) {
							if (msg.getConversationId().equals(message.getConversationId())
									&& msg.getMsgTime().equals(message.getMsgTime())) {
								msg.setStatus(BmobConfig.STATUS_SEND_SUCCESS);
							}
							mAdapter.notifyDataSetChanged();
						}
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						for (BmobMsg msg : mAdapter.getList()) {
							if (msg.getConversationId().equals(message.getConversationId())
									&& msg.getMsgTime().equals(message.getMsgTime())) {
								msg.setStatus(BmobConfig.STATUS_SEND_FAIL);
							}
							mAdapter.notifyDataSetChanged();
						}
					}
				});
			}
			else{
				toast("还未登录，不能评论");
			}
			break;
		}
		}
	}
	@Override
	public void onAddUser(BmobInvitation arg0) {
		// TODO Auto-generated method stub
		System.out.println("onAddUser");
		
	}
	@Override
	public void onMessage(BmobMsg arg0) {
		// TODO Auto-generated method stub
		Message handlerMsg = handler.obtainMessage(0x12);
		handlerMsg.obj = arg0;
		handler.sendMessage(handlerMsg);
		
	}
	@Override
	public void onNetChange(boolean arg0) {
		// TODO Auto-generated method stub
		System.out.println("onNetChange");
		
	}
	@Override
	public void onOffline() {
		// TODO Auto-generated method stub
		System.out.println("onOffline");
		
	}
	@Override
	public void onReaded(String arg0, String arg1) {
		// TODO Auto-generated method stub
		System.out.println("onReaded");
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		// 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知和清空未读消息数
		BmobNotifyManager.getInstance(this).cancelNotify();
		BmobDB.create(this).resetUnread(targetId);
		//清空消息未读数-这个要在刷新之后
		MyMessageReceiver.mNewNum=0;
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 监听推送的消息
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(receiver);
		} catch (Exception e) {
		}
	}
	
	
	
	
}
