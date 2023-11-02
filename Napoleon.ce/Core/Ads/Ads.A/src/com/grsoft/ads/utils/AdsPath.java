package com.grsoft.ads.utils;

import android.content.Context;

import com.grsoft.napoleon.util.debug.Path;

public class AdsPath extends Path {
	{
		filesDirPath = "files";
		BASE_NAME = "ads.db";
	}
	
	public static void init(Context context)
	{
		filesDirPath = context.getFilesDir().getAbsoluteFile().toString();
	}
}
