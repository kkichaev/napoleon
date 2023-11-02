package com.grsoft.ads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SettingMainPage extends ListActivity {
	private static final String IMAGE_FIELD = "IMAGE";
	private static final String CAPTION_FIELD = "CAPTION";
	private static final String SETTING_FIELD = "SETTING";
	
	public static void open(Context context){
		Intent intent = new Intent(context, SettingMainPage.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_main_page);
		
		ListAdapter adapter = new SimpleAdapter(this, 
				getData(), R.layout.setting_main_page_row, 
				new String[]{IMAGE_FIELD, CAPTION_FIELD, SETTING_FIELD}, 
				new int[]{R.id.ivPicture, R.id.tvCaption, R.id.tvSetting});
		setListAdapter(adapter);
	}
	
	private List<java.util.Map<String, Object>> getData(){
		List<java.util.Map<String, Object>> result = new ArrayList<java.util.Map<String, Object>>();
		Resources res = getResources();
		String[] item = res.getStringArray(R.array.items);
		String[] item_pic = res.getStringArray(R.array.items_pic);
		String[] setting = res.getStringArray(R.array.setting);
		
		for(int i = 0; i < item.length; i++){
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(IMAGE_FIELD, res.getIdentifier(item_pic[i], "drawable", getPackageName()));
			map.put(CAPTION_FIELD, item[i]);
			map.put(SETTING_FIELD, setting[i]);
			result.add(map);
		}
		
		return result;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		TextView tv = (TextView) v.findViewById(R.id.tvSetting);
		Intent intent = new Intent(tv.getText().toString());
		startActivity(intent);
	}
}
