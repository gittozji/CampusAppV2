package com.zjcql.base;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

/** 
* Base适配
* @author phlofy
* @date 2015年9月19日 下午12:39:00 
*/
public abstract class BaseContentAdapter<T> extends BaseAdapter{
	protected Context mContext;
	protected List<T> dataList ;

	protected LayoutInflater mInflater;
	
	public BaseContentAdapter(Context context,List<T> list){
		mContext = context;
		dataList = list;
		mInflater = LayoutInflater.from(mContext);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return getConvertView(position,convertView,parent);
	}

	public abstract View getConvertView(int position, View convertView, ViewGroup parent);

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}
	
	public void toast(String string){
		try{
			Toast.makeText(mContext, string, Toast.LENGTH_LONG).show();
		}
		catch(Exception e){
			System.out.println("toast空指针异常。");
		}
	}
	public void add(T e) {
		this.dataList.add(e);
		notifyDataSetChanged();
	}

	public void addAll(List<T> list) {
		this.dataList.addAll(list);
		notifyDataSetChanged();
	}
	
	public void setList(List<T> list) {
		this.dataList = list;
		notifyDataSetChanged();
	}
	public List<T> getList(){
		return dataList;
	}
}
