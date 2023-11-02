package com.grsoft.napoleon;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.BaseActivity;

public class FolderTree extends BaseActivity {

	ArrayList<Folder> folders = new ArrayList<Folder>();
	FolderTreeAdapter fa;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.folder_tree);
		
		Folder f = new Folder();
		String table = DataObjectInfo.getInstance().getTableName(Folder.class);
		DbReader r = new DbReader();
		boolean bdo = r.select(f, table, "", "id");
		while(bdo) {
			folders.add(f);
			f = new Folder();
			bdo = r.selectNext(f);
		}
		r.close();
		
		String folder = getIntent().getStringExtra(ExtrasConst.FOLDER_ID);
		ListView lv = (ListView)findViewById(R.id.lvItems);
		fa = new FolderTreeAdapter(folder);
		lv.setAdapter(fa);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Folder f = folders.get(position);
				Intent i = new Intent();
				i.putExtra(ExtrasConst.FOLDER_ID, f.name);
				setResult(RESULT_OK, i);
				finish();
			}
		});
		
	}
	
	class FolderTreeAdapter extends BaseAdapter {
		
		String selected;
		
		public FolderTreeAdapter(String selected) {
			this.selected = selected;
		}

		@Override
		public int getCount() {
			return folders.size();
		}

		@Override
		public Object getItem(int position) {
			return (position < folders.size()) ? folders.get(position) : null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if( view == null ) {
				view = new TextView(FolderTree.this);
				
				AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
				view.setLayoutParams(lp);
			}
			
			Folder f = (Folder) getItem(position);
			if( f != null ) {
				TextView tv = (TextView)view;
				String text = "";
				for( int i=0; i< f.level ; i++ )
					text += " ";
				text += f.name;
				tv.setText(text);
				int color = Color.BLACK;
				if(f.name.equals(selected))
					color = Color.GREEN;
				
				tv.setTextColor(color);
				tv.setBackgroundResource(R.drawable.list_selector);
				tv.setTextSize(18);
			}
			return view;
		}
		
	}
}
