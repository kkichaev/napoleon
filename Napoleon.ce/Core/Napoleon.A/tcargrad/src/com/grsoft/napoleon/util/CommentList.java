package com.grsoft.napoleon.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

public class CommentList {
	
	private static final String COMMENT_LIST = "CommentListProperties";
	private static final String COMMENT_STRING = "CommentString";
	private static final String SEP_STR = ",";
	
	public static List<String> getCommentList(Context ctx) {
		
		SharedPreferences sp = ctx.getApplicationContext().getSharedPreferences(COMMENT_LIST, Context.MODE_PRIVATE);
		
		String str = sp.getString(COMMENT_STRING, "");
		if( str.length() == 0 )
			return new ArrayList<String>();
		return new ArrayList<String>(Arrays.asList(str.split(SEP_STR)));
	}
	
	public static void putCommentList(List<String> list, Context ctx) {
		StringBuilder str = new StringBuilder();
		for(String s : list) {
			if(str.length() > 0)
				str.append(SEP_STR);
			str.append(s);
		}

		SharedPreferences sp = ctx.getApplicationContext().getSharedPreferences(COMMENT_LIST, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = sp.edit();
		e.putString(COMMENT_STRING, str.toString());
		e.commit();
	}
}
