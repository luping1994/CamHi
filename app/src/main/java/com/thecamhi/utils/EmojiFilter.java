package com.thecamhi.utils;

import java.util.regex.Matcher;

import java.util.regex.Pattern;

import android.text.InputFilter;
import android.text.Spanned;
/**
 * EditText的表情过滤
 * @author lt
 *
 */

public class EmojiFilter implements InputFilter  {

	@Override
	public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
		   Pattern emoji = Pattern.compile(  
                   "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",  
                   Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);  
           Matcher emojiMatcher = emoji.matcher(source);  
           if (emojiMatcher.find()) {  
               return "";  
           }  
           return null;  
	}

}
