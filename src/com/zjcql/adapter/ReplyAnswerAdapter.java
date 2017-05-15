package com.zjcql.adapter;

import java.util.ArrayList;
import java.util.List;

import com.zjcql.Interface.GeneralListener;
import com.zjcql.activity.UserActivity;
import com.zjcql.adapter.ReplyAnswerAdapter.ViewHolder;
import com.zjcql.apparatus.Poper;
import com.zjcql.base.BaseContentAdapter;
import com.zjcql.bmobbean.AnswerPopMark;
import com.zjcql.bmobbean.Goods;
import com.zjcql.bmobbean.ReplyAnswer;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;
import com.zjcql.popup.UpAndDwonPopup;
import com.zjcql.rebuildview.CircleImageView;
import com.zjcql.util.MyBitmapUtil;
import com.zjcql.util.TimeUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/** 
* 回答问题adapter
* @author phlofy
* @date 2016年1月27日 下午1:26:01 
*/
public class ReplyAnswerAdapter extends BaseContentAdapter<ReplyAnswer>{
	User currentUser;
	public ReplyAnswerAdapter(Context context, List<ReplyAnswer> list) {
		super(context, list);
		currentUser = BmobUser.getCurrentUser(mContext, User.class);
	}

	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		/*****************************获取convertView**********************************/
		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_reply_answer, null);
			viewHolder.icon = (CircleImageView) convertView.findViewById(R.id.icon);
			viewHolder.content = (TextView) convertView.findViewById(R.id.content);
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);
			viewHolder.pop = (TextView) convertView.findViewById(R.id.pop);
			viewHolder.lay = (RelativeLayout) convertView.findViewById(R.id.layout_menu);
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		/*******************************得到信息、发布者实体******************************/
		final ReplyAnswer replyAnswer = dataList.get(position);
		final User user = replyAnswer.getUser();
		
		/*******************************给各个view添加信息******************************/
		MyBitmapUtil.displayIcon(mContext, user.getAvatar(), viewHolder.icon);
		viewHolder.content.setText(replyAnswer.getContent());
		viewHolder.time.setText(TimeUtil.getDescriptionTimeFromTimestamp(TimeUtil.stringToLong(replyAnswer.getCreatedAt(), "yyyy-MM-dd HH:mm:ss")));
		viewHolder.pop.setText(replyAnswer.getPop()+"");
		
		/*******************************添加监听器******************************/
		viewHolder.lay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in = new Intent(mContext,UserActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("user", user);
				in.putExtras(bundle);
				mContext.startActivity(in);
			}
		});
		viewHolder.pop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 说明：只有赞同操作会对回答问题的用户pop值加0.1（Intger值加1）
				UpAndDwonPopup uadp = new UpAndDwonPopup(mContext, viewHolder.pop, "赞同", "反对");
				uadp.setGeneralListener(new GeneralListener() {
					
					@Override
					public void onReturn(boolean arg0, int arg1, String arg2) {
						if(arg1 == 1){
							isPopExist(replyAnswer, new GeneralListener() {
								
								@Override
								public void onReturn(boolean arg0, int arg1, String arg2) {
									if(!arg0){
										replyAnswer.increment("pop", 1);
										replyAnswer.update(mContext, new UpdateListener() {
											
											@Override
											public void onSuccess() {
												toast("操作成功");
												replyAnswer.setPop(replyAnswer.getPop()+1);
												viewHolder.pop.setText(replyAnswer.getPop()+"");
												Poper.getInstance().increme(true, mContext, replyAnswer.getUser(), 1, new GeneralListener() {
													
													@Override
													public void onReturn(boolean arg0, int arg1, String arg2) {
														//不处理
													}
												});
												AnswerPopMark apm = new AnswerPopMark();
												apm.setUser(currentUser);
												apm.setReplyAnswer(replyAnswer);
												apm.save(mContext);
											}
											
											@Override
											public void onFailure(int arg0, String arg1) {
												toast("操作失败");
											}
										});
									}
									else{
										toast("已操作");
									}
								}
							});
						}
						else{
							isPopExist(replyAnswer, new GeneralListener() {
								
								@Override
								public void onReturn(boolean arg0, int arg1, String arg2) {
									if(!arg0){
										replyAnswer.increment("pop", -1);
										replyAnswer.update(mContext, new UpdateListener() {
											
											@Override
											public void onSuccess() {
												toast("操作成功");
												replyAnswer.setPop(replyAnswer.getPop()-1);
												viewHolder.pop.setText(replyAnswer.getPop()+"");
												Poper.getInstance().increme(true, mContext, replyAnswer.getUser(), -1, new GeneralListener() {
													
													@Override
													public void onReturn(boolean arg0, int arg1, String arg2) {
														//不处理
													}
												});
												AnswerPopMark apm = new AnswerPopMark();
												apm.setUser(currentUser);
												apm.setReplyAnswer(replyAnswer);
												apm.save(mContext);
											}
											
											@Override
											public void onFailure(int arg0, String arg1) {
												toast("操作失败");
											}
										});
									}
									else{
										toast("已操作");
									}
								}
							});
						}
					}
				});
				uadp.show();
			}
		});
		
		return convertView;
	}
	public static class ViewHolder {
		CircleImageView icon;
		TextView content;
		TextView time;
		TextView pop;
		RelativeLayout lay;
	}
	/**
	 * 该用户是否对该回答执行过赞或贬操作
	 * @param gl,true为有记录，false为没有记录
	 */
	private void isPopExist(ReplyAnswer replyAnswer,final GeneralListener gl){
		BmobQuery<AnswerPopMark> query1 = new BmobQuery<AnswerPopMark>();
		query1.addWhereEqualTo("user", currentUser);
		BmobQuery<AnswerPopMark> query2 = new BmobQuery<AnswerPopMark>();
		query2.addWhereEqualTo("replyAnswer", replyAnswer);
		List<BmobQuery<AnswerPopMark>> andQuery = new ArrayList<BmobQuery<AnswerPopMark>>();
		andQuery.add(query1);
		andQuery.add(query2);
		BmobQuery<AnswerPopMark> query = new BmobQuery<AnswerPopMark>();
		query.and(andQuery);
		query.findObjects(mContext, new FindListener<AnswerPopMark>() {
			
			@Override
			public void onSuccess(List<AnswerPopMark> arg0) {
				if(arg0.size() > 0){
					gl.onReturn(true, 0, null);
				}
				else{
					gl.onReturn(false, 0, null);
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				gl.onReturn(false, 0, null);
			}
		});
	}

}
