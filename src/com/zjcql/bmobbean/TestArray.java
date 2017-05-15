package com.zjcql.bmobbean;

import cn.bmob.v3.BmobObject;

/** 
*
* @author phlofy
* @date 2015年9月2日 下午1:24:06 
*/
public class TestArray extends BmobObject{
	String[] array;
	int no;
	public String[] getArray() {
		return array;
	}
	public void setArray(String[] array) {
		this.array = array;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
}
