package com.thecamhi.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * EditText值能输入如下字符
 * @author lt
 *
 */
public class SpcialCharFilter implements InputFilter {

	@Override
	public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
		   String regexStr = "[1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]+";  
           Pattern pattern = Pattern.compile(regexStr);  
           Matcher matcher = pattern.matcher(source.toString());  
           if (matcher.matches()) {  
               return null;  
           } else {  
               return "";  
           }  

       }  

}
