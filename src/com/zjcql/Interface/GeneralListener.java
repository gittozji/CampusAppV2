package com.zjcql.Interface;
/** 
* 通用接口,我不知道这接口是否要定义那么多（感觉都一样）。所以定义这个通用接口，希望可以替代其他接口的定义
* 由于该接口是通用的，因此在使用时都应该对其进行注释其具体用法、三个参数的具体意义。
* @author phlofy 
* @date 2015年10月1日 下午3:29:34 
*/
public interface GeneralListener {
	public void onReturn(boolean arg0,int arg1,String arg2);
}
