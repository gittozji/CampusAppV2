package com.zjcql.bmobbean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/** 
* 测试多个成员变量是否都对应为表中字段
* 结果是为null的成员不会上传至云端。
* @author phlofy
* @date 2015年8月27日 下午8:57:41 
*/
public class TestMore extends BmobObject{
	String one;
	String two;
	String three;
	Boolean b;
	BmobFile[] pict;
	public Boolean getB() {
		return b;
	}
	public void setB(Boolean b) {
		this.b = b;
	}
	public String getOne() {
		return one;
	}
	public void setOne(String one) {
		this.one = one;
	}
	public String getTwo() {
		return two;
	}
	public void setTwo(String two) {
		this.two = two;
	}
	public String getThree() {
		return three;
	}
	public void setThree(String three) {
		this.three = three;
	}
	public BmobFile[] getPict() {
		return pict;
	}
	public void setPict(BmobFile[] pict) {
		this.pict = pict;
	}
}
