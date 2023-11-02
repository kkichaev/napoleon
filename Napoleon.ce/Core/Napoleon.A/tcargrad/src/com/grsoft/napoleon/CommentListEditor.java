package com.grsoft.napoleon;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.napoleon.util.CommentList;
import com.grsoft.view.BaseActivity;

public class CommentListEditor extends BaseActivity {
	protected static final int EDIT_VALUE = 0;
	Adapter adapter;
	List<String> items;

	int editPos;
	EditText editString;
	
	
	public static void open(Context ctx) {
		Intent i = new Intent(ctx, CommentListEditor.class);
		ctx.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.comment_list);
		ListView lv = (ListView)findViewById(R.id.lvItems);
		adapter = new Adapter();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				editPos = (pos == items.size() - 1) ?  -1 : pos;
				showDialog(EDIT_VALUE);
			}
		});
	}
	
	protected Dialog onCreateDialog(int id) {
		if( id == EDIT_VALUE ) {
			LayoutInflater inflater = getLayoutInflater();			
			View v = inflater.inflate(R.layout.string_prop, null);
			
			editString = (EditText)v.findViewById(R.id.edValue);
			
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle(R.string.value_editor_title);
			b.setView(v);
			
			b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) { 
					updateString(editString.getText().toString());
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if( id == EDIT_VALUE ) {
			String s = "";
			if( editPos >= 0)
				s = items.get(editPos);
			editString.setText(s);
		}
		super.onPrepareDialog(id, dialog);
	}
	
	protected void updateString(String string) {
		items.remove(items.size()-1);
		if( editPos < 0 || items.size() < editPos ) {
			items.add(string);			
		} else {
			items.remove(editPos);
			items.add(editPos, string);
		}
		CommentList.putCommentList(items, CommentListEditor.this);
		adapter.refresh();			
	}

//	OnClickListener editItem = new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			editPos = (Integer)v.getTag();
//			showDialog(EDIT_VALUE);
//		}
//	};
//	
//	OnClickListener newItem = new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			editPos = -1;
//			showDialog(EDIT_VALUE);
//		}
//	};
//	
	OnClickListener delItem = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int pos = (Integer)v.getTag();
			if( pos < items.size() - 1) {
				items.remove(items.size()-1);
				items.remove(pos);
				CommentList.putCommentList(items, CommentListEditor.this);
				adapter.refresh();
			}
		}
	};
	
	class Adapter extends BaseAdapter {
				
		public Adapter() {
			load();
		}
		
		void load() {
			items = CommentList.getCommentList(CommentListEditor.this);
			items.add("");
		}
		
		public void refresh() {
			load();
			notifyDataSetChanged();
		}

		@Override public int getCount() { return items.size(); }
		@Override public Object getItem(int arg0) { return items.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if( view == null ) {
				view = View.inflate(CommentListEditor.this, R.layout.comment_list_row, null);
			}
			String value = (String) getItem(pos);
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(value);

			ImageView iv = (ImageView) view.findViewById(R.id.ivDel);
			iv.setTag(pos);
			if( pos == getCount() - 1) {
				iv.setVisibility(View.INVISIBLE);
				iv.setOnClickListener(null);
			} else {
				iv.setVisibility(View.VISIBLE);
				iv.setOnClickListener(delItem);
			}
			
//			OnClickListener editor = null;
//			OnClickListener remover = null;
//			int image = 0;
//			if( pos == getCount() - 1) {
//				image = R.drawable.new_string;
//				editor = newItem;
//			} else {
//				image = R.drawable.edit_string;
//				editor = editItem;
//				remover = delItem;
//			}
//			ImageView iv;
//			iv = (ImageView) view.findViewById(R.id.ivEdit);
//			iv.setTag(pos);
//			iv.setOnClickListener(editor);
//			iv.setImageResource(image);
//			
//			iv = (ImageView) view.findViewById(R.id.ivDel);
//			iv.setTag(pos);
//			if( remover == null )
//				iv.setVisibility(View.INVISIBLE);
//			else {
//				iv.setVisibility(View.VISIBLE);
//				iv.setOnClickListener(remover);
//			}
			return view;
		}
		
	}
}
