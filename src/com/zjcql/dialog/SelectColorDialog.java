package com.zjcql.dialog;

import java.util.zip.Inflater;

import com.zjcql.campusappv2.R;
import com.zjcql.util.MyUtil;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

/** 
* 选择图片的Dialog
* @author phlofy
* @date 2015年10月19日 下午7:42:28 
*/
public abstract class SelectColorDialog{
	Context context;
	TextView title;
	GridView grid;
	Dialog dialog;
	BaseAdapter adapter;
	public static String[] colors = {
			"#ffffff","#00BCD4","#0097A7","#B2EBF2",
			"#FF66FF","#212121","#727272","#3F51B5",
			"#795547","#1D89E4","#8BC24A","#9E9E9E",
			"#D42F2D","#03A9F5","#CDDC39","#424242",
			"#EA1E63","#FFEB3C","#9C28B1","#009788",
			"#FEC009","#5D35B0","#4CB050","#FF9700"
		};
	public SelectColorDialog(Context context,String title) {
		this.context = context;
		dialog = new Dialog(context, R.style.DialogTheme);
		dialog.setContentView(R.layout.dialog_select_color);
		this.title = (TextView) dialog.findViewById(R.id.title);
		this.title.setText(title);
		grid = (GridView) dialog.findViewById(R.id.grid);
		setGrid();
	}
	
	public void show(){
		dialog.show();
	}
		
	private void setGrid(){
		adapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				int mScreenWidth = MyUtil.getScreenWidth(context) - MyUtil.dip2px(context,150);
				AbsListView.LayoutParams mLayoutParams =  new AbsListView.LayoutParams(mScreenWidth/4, mScreenWidth/4);
				ImageView mImageView = new ImageView(context);
				mImageView.setBackgroundColor(Color.parseColor(colors[position]));
				mImageView.setLayoutParams(mLayoutParams);
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
				return colors.length;
			}
		};
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClick(position,colors[position]);
				dialog.dismiss();
			}
		});
	}
	
	public abstract void onClick(int index,String color);
}
