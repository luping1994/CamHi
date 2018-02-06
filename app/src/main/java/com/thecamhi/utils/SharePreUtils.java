package com.thecamhi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 配置文件(偏好设置工具类)
 * @author  creat by lt
 *
 */
public class SharePreUtils {
	/**
	 * 存储字符串到配置文件中
	 * @param preName  文件名
	 * @param context
	 * @param key      存储的键
	 * @param values   你要存放的值
	 * @return         保存成功的标志
	 */
	public static Boolean putString(String preName,Context context,String key,String values){
		SharedPreferences shared=context.getSharedPreferences(preName, context.MODE_PRIVATE);
		Editor editor=shared.edit();
		editor.putString(key, values);
		return editor.commit();
	}
	/**
	 * 存储数字到配置文件中
	 * @param preName 文件名
	 * @param context
	 * @param key     存储的键 
	 * @param values  你要存放的值
	 * @return        保存成功的标志
	 */ 
	public static Boolean putInt(String preName,Context context,String key,int values){
		SharedPreferences shared=context.getSharedPreferences(preName, context.MODE_PRIVATE);
		Editor editor=shared.edit();
		editor.putInt(key, values);
		return editor.commit();
	}
	
	
	
	/**
	 * 从配置文件中读取字符串
	 * @param preName  文件名
	 * @param context
	 * @param key      键值
	 * @return         键值对应的字符串，默认返回 ""
	 */
	public static String getString(String preName,Context context,String key){
		SharedPreferences shared=context.getSharedPreferences(preName, context.MODE_PRIVATE);
		return shared.getString(key, "");
	}
	
	/**
	 * 从配置文件中读取        int
	 * @param preName 文件名
	 * @param context
	 * @param key     键值
	 * @return        键值对应的int 默认返回-1
	 */
	public static int getInt(String preName,Context context,String key){
		SharedPreferences shared=context.getSharedPreferences(preName, context.MODE_PRIVATE);
		return shared.getInt(key, -1);
		
	}
	
	
	

}






