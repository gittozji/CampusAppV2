package com.zjcql.apparatus;

import java.io.ObjectOutputStream.PutField;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zjcql.Interface.GeneralListener;
import com.zjcql.bmobbean.PopMark;
import com.zjcql.bmobbean.Pops;
import com.zjcql.bmobbean.User;

import android.content.Context;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetServerTimeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/** 
* 该类专门处理pop值得加载、更新等操作
* @author phlofy
* @date 2015年10月1日 下午2:59:31 
*/
public class Poper {
	/**
	 * 保存疑已查询过的pop值，以供直接使用而不用每次都访问网络
	 * String是user的id，map是一个（popid、pop值）散列
	 */
	Map<String, Map> map;
	private static Poper poper = null;
	
	private Poper(){
		//外部不许调用
	}
	
	public static Poper getInstance(){
		if(poper == null){
			poper = new Poper();
		}
		return poper;
	}
	
	/**
	 * 获取pop值得对外接口
	 * @param context
	 * @param user
	 * @param listener 规则是成功放回（true，0，pop值），失败返回（false，0，null）
	 */
	public void getPop(Context context,User user,GeneralListener listener){
		if(getLocalPop(user) != null){  //如果本地有所保存则直接去本地而不访问网络
			listener.onReturn(true,0,int2Float(getLocalPop(user)));
		}
		else{
			getNetPop(context, user, listener);
		}
	}
	
	/**
	 * 保存pop值的接口
	 * @param user 用户
	 * @param pop pop值
	 * @param objectId Pops表中的id
	 */
	private void put(User user,int pop,String objectId){
		Map m = new HashMap<String, Object>();
		m.put("pop", pop);
		m.put("id", objectId);
		getMapInstance().put(user.getObjectId(), m);
	}
	/**
	 * 获取本地pop值
	 * @param user
	 * @return
	 */
	private Integer getLocalPop(User user){
		Integer pop = null;
		Map m = getMap(user);
		if(m == null){
			return null;
		}
		else{
			pop = (Integer) m.get("pop");
			return pop;
		}
	}
	/**
	 * 获取网络pop值
	 * @param context
	 * @param user
	 * @param listener 规则是成功返回（true，0，pop值），失败返回（false，0，null）
	 */
	private void getNetPop(final Context context,final User user,final GeneralListener listener){
		/**********首先查找到数据库对应的pop信息**********/
		BmobQuery<Pops> query = new BmobQuery<Pops>();
		query.addWhereEqualTo("user", user);
		query.findObjects(context, new FindListener<Pops>() {
			@Override
			public void onSuccess(List<Pops> arg0) {
				if(arg0.size() == 0){
					
					/***********没有该记录，创建一个************/
					Pops pops = new Pops();
					pops.setUser(user);
					pops.setPop(0);
					pops.save(context, new SaveListener() {
						
						@Override
						public void onSuccess() {
							/***********创建成功后继续前面的步骤************/
							BmobQuery<Pops> query = new BmobQuery<Pops>();
							query.addWhereEqualTo("user", user);
							query.findObjects(context, new FindListener<Pops>() {
								@Override
								public void onSuccess(List<Pops> arg0) {
									if(arg0.size() == 1){
										put(user, arg0.get(0).getPop(), arg0.get(0).getObjectId());
										listener.onReturn(true,0,int2Float(arg0.get(0).getPop()));
									}
									else{
										listener.onReturn(false,0,null);
									}
								}
								
								@Override
								public void onError(int arg0, String arg1) {
									System.out.println("onError:"+arg1);
									listener.onReturn(false,0,null);
								}
							});
						}
						
						@Override
						public void onFailure(int arg0, String arg1) {
							listener.onReturn(false,0,null);
						}
					});
				}
				else{
					if(arg0.size() == 1){
						put(user, arg0.get(0).getPop(), arg0.get(0).getObjectId());
						listener.onReturn(true,0,int2Float(arg0.get(0).getPop()));
					}
					else{
						listener.onReturn(false,0,null);
					}
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				listener.onReturn(false,0,null);
			}
		});
	}
	
	/**
	 * 获取Map对象
	 * @return
	 */
	private Map<String,Map> getMapInstance(){
		if(map == null)
			map = new HashMap<String,Map>();
		return map;
	}
	
	/**
	 * 获取key为user.getobjectId()的Map对象.
	 * @param user
	 * @return
	 */
	private Map getMap(User user){
		return getMapInstance().get(user.getObjectId());
	}

	/**
	 * 增加pop值
	 * @param isJudge
	 * @param context
	 * @param user
	 * @param number
	 * @param listener 规则是成功返回（true，0，null），失败返回（false，0，null）
	 */
	public void increme(final boolean isJudge,final Context context,final User user , final int number,final GeneralListener listener){
		if(getMap(user) != null){
			isLiked(context, BmobUser.getCurrentUser(context, User.class), user, new GeneralListener() {
				
				@Override
				public void onReturn(boolean arg0, int arg1, String arg2) {
					if(!arg0 || isJudge){
						Pops pops = new Pops();
						pops.increment("pop", number);
						pops.update(context, getMap(user).get("id")+"", new UpdateListener() {
							@Override
							public void onSuccess() {
								put(user, (Integer)getMap(user).get("pop")+number, getMap(user).get("id")+"");
								takeNotesOnPopMark(context, BmobUser.getCurrentUser(context,User.class),user);
								listener.onReturn(true,0,null);
							}
							
							@Override
							public void onFailure(int arg0, String arg1) {
								listener.onReturn(false,0,null);
							}
						});
					}
					else{
						listener.onReturn(false, 0, null);
						System.out.println("已经赞了");
					}
				}
			});
			
		}
		else{
			/**********首先查找到数据库对应的pop信息**********/
			BmobQuery<Pops> query = new BmobQuery<Pops>();
			query.addWhereEqualTo("user", user);
			query.findObjects(context, new FindListener<Pops>() {
				@Override
				public void onSuccess(final List<Pops> arg0) {
					if(arg0.size() == 0){
						
						/***********没有该记录，创建一个************/
						Pops pops = new Pops();
						pops.setUser(user);
						pops.setPop(0);
						pops.save(context, new SaveListener() {
							
							@Override
							public void onSuccess() {
								/***********创建成功后继续前面的步骤************/
								BmobQuery<Pops> query = new BmobQuery<Pops>();
								query.addWhereEqualTo("user", user);
								query.findObjects(context, new FindListener<Pops>() {
									@Override
									public void onSuccess(final List<Pops> listPops) {
										if(listPops.size() == 1){
											isLiked(context, BmobUser.getCurrentUser(context,User.class), user, new GeneralListener() {
												
												@Override
												public void onReturn(boolean arg0, int arg1, String arg2) {
													if(!arg0 || isJudge){
														Pops pops = new Pops();
														pops.increment("pop", number);
														pops.update(context, listPops.get(0).getObjectId(), new UpdateListener() {
															@Override
															public void onSuccess() {
																// 保存至内存
																put(user, number, listPops.get(0).getObjectId());
																takeNotesOnPopMark(context, BmobUser.getCurrentUser(context,User.class),user);
																listener.onReturn(true,0,null);
															}
															
															@Override
															public void onFailure(int arg0, String arg1) {
																listener.onReturn(false,0,null);
															}
														});
													}
													else{
														listener.onReturn(false, 0, null);
														System.out.println("已经赞了");
													}
												}
											});
											
										}
										else{
											listener.onReturn(false,0,null);
										}
									}
									
									@Override
									public void onError(int arg0, String arg1) {
										System.out.println("onError:"+arg1);
										listener.onReturn(false,0,null);
									}
								});
							}
							
							@Override
							public void onFailure(int arg0, String arg1) {
								listener.onReturn(false,0,null);
							}
						});
					}
					else{
						if(arg0.size() == 1){
							isLiked(context, BmobUser.getCurrentUser(context,User.class), user, new GeneralListener() {
								
								@Override
								public void onReturn(boolean arg1, int arg2, String arg3) {
									if(!arg1 || isJudge){
										Pops pops = new Pops();
										pops.increment("pop", number);
										pops.update(context, arg0.get(0).getObjectId(), new UpdateListener() {
											@Override
											public void onSuccess() {
												put(user, arg0.get(0).getPop()+number, arg0.get(0).getObjectId());
												takeNotesOnPopMark(context, BmobUser.getCurrentUser(context,User.class),user);
												listener.onReturn(true,0,null);
											}
											
											@Override
											public void onFailure(int arg0, String arg1) {
												listener.onReturn(false,0,null);
											}
										});
									}
									else{
										listener.onReturn(false, 0, null);
										System.out.println("已经赞了");
									}
								}
							});
							
						}
						else{
							listener.onReturn(false,0,null);
						}
					}
				}
				
				@Override
				public void onError(int arg0, String arg1) {
					System.out.println("onError:"+arg1);
					listener.onReturn(false,0,null);
				}
			});
		}
	}
	
	/**
	 * 判断initiative是否今天已经执行了对possivity的赞或贬动作
	 * @param context
	 * @param initiative
	 * @param possivity
	 * @param listener 规则是已经执行过赞或贬动作返回（true，0，null），否则返回（false，0，null）。当且仅当查询记录为0条时返回true
	 */
	private void isLiked(final Context context,final User initiative,final User possivity,final GeneralListener listener){
		Bmob.getServerTime(context, new GetServerTimeListener() {
			
			@Override
			public void onSuccess(long arg0) {
		        BmobQuery<PopMark> query1 = new BmobQuery<PopMark>();
				BmobQuery<PopMark> query2 = new BmobQuery<PopMark>();
				BmobQuery<PopMark> query3 = new BmobQuery<PopMark>();
				BmobQuery<PopMark> query4 = new BmobQuery<PopMark>();
				BmobQuery<PopMark> query5 = new BmobQuery<PopMark>();
				BmobQuery<PopMark> query = new BmobQuery<PopMark>();  
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		        String times = formatter.format(new Date(arg0 * 1000L));
				Date date = null;
				try {
					date = formatter.parse(times);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				query1.addWhereEqualTo("initiative", initiative);
				query2.addWhereEqualTo("possivity", possivity);
				query3.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(date));	
				query4.addWhereEqualTo("type", 1);
				
				List<BmobQuery<PopMark>> list = new ArrayList<BmobQuery<PopMark>>();
				list.add(query1);
				list.add(query2);
				list.add(query3);
				list.add(query4);
		        query.and(list);
		        query.findObjects(context, new FindListener<PopMark>() {
					
					@Override
					public void onSuccess(List<PopMark> arg0) {
						if(arg0.size() == 0)
							listener.onReturn(false, 0, null);
						else
							listener.onReturn(true, 0, null);
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						listener.onReturn(true, 0, null);
					}
				});
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				listener.onReturn(true, 0, null);
			}
		});
		
	}
	/**
	 * 记录对用户的赞或贬动作
	 * @param context
	 * @param initiative
	 * @param possivity
	 */
	private void takeNotesOnPopMark(Context context,User initiative,User possivity){
		PopMark popMark = new PopMark();
		popMark.setInitiative(initiative);
		popMark.setPossivity(possivity);
		popMark.setType(1);
		popMark.save(context);
	}
	private String int2Float(Integer value) {
		String s = value+"";
		Float f = Float.parseFloat(s);
		f = f/10;
		DecimalFormat	df = new DecimalFormat("#.0"); 
		return df.format(f);
	}
}
