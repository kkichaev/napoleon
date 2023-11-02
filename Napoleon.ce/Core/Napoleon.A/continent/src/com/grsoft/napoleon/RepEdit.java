package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.Folder;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.TextView;


public class RepEdit extends WarehouseNew {
	public static final String START = "start";
	public static final String FINISH = "finish";
	public static final String MODE = "mode";
	public static final String PRCSEL = "prcsel";
	public static final String FLDSEL = "fldsel";
	public static final String DELIM = ";";
	public static String IDS = "ids";
	
	private TextView tvStart;
	private TextView tvFinish;
	private View btnOK;
	private RadioGroup rgMode;
	
	private Set<Integer> fldsel = new HashSet<Integer>();
	private Set<String> prcsel = new HashSet<String>();
	
	public static void open(Context ctx){
		Intent i = new Intent(ctx, RepEdit.class);
		ctx.startActivity(i);
	}
	
	@Override protected int getFolderLayoutId() { return R.layout.itemselectrowex; }
	
	protected int getItemLayoutId() { return R.layout.priceitemrowex; };
	
	@Override
	protected int getLayoutId() { return R.layout.ordrep; }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tvStart = (TextView) findViewById(R.id.tvStart);
		tvFinish = (TextView) findViewById(R.id.tvFinish);
		btnOK = findViewById(R.id.btnOK);
		rgMode = (RadioGroup) findViewById(R.id.rgMode);
		
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		Date finish = Util.resetTime(new Date());
		Calendar c = Calendar.getInstance();
		c.setTime(finish);
		c.add(Calendar.DATE, -7);
		Date start = c.getTime();
		
		finish = new Date(p.getLong(FINISH, finish.getTime()));
		start = new Date(p.getLong(START, start.getTime()));

		String v = p.getString(FLDSEL, "");
		fldsel.clear();
		for(String s : v.split(DELIM))
			try{
				int i = Integer.parseInt(s);
					fldsel.add(i);
			}catch(Exception e){}
		
		v = p.getString(PRCSEL, "");
		prcsel.clear();
		for(String s : v.split(DELIM))
				prcsel.add(s);
		
		tvStart.setText(textAsLink(Util.simpleDateFormat.format(start)));
		setDataHandler(tvStart, start);
		tvStart.setOnClickListener(dateClick);
		tvFinish.setText(textAsLink(Util.simpleDateFormat.format(finish)));
		setDataHandler(tvFinish, finish);
		
		btnOK.setOnClickListener(reportClick());
		folderTree.load();
		
		rgMode.check(p.getInt(MODE, R.id.rbDocs));
	}
	
	private OnClickListener reportClick() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SQLiteDatabase db = DataBaseManager.getDataBase();
				
				for(int i : fldsel){
					int index = folderTree.findFolder(i);
					if( index >= 0 ) {
						Folder f = folderTree.get(index);
						for(  ; index < folderTree.size(); index++) {
							Folder check = folderTree.get(index);
							if( check.level <= f.level && f != check )
								break;
							
							Cursor c = db.rawQuery("select id from price where folderid=?", new String[]{Integer.toString(f.id)});
							
							while (c.moveToNext()){
								String id = c.getString(0);
									prcsel.add(id);
							}
						}
					}
				}
				
				StringBuilder d = new StringBuilder();
				for (String s : prcsel){
					if(d.length() > 0)
						d.append(DELIM);
					d.append(s);
				}
				
				Intent i = new Intent(v.getContext(), RepView.class);
				i.putExtra(START, (Long)((Date)tvStart.getTag()).getTime());
				
				Calendar c = Calendar.getInstance();
				c.setTime((Date)tvFinish.getTag());
				c.add(Calendar.DAY_OF_MONTH, 1);
				i.putExtra(FINISH, c.getTime().getTime());
				i.putExtra(IDS, d.toString());
				i.putExtra(MODE, rgMode.getCheckedRadioButtonId());
				
				startActivity(i);
			}
		};
	}

	@Override public boolean isPriceExpand() { return false; }
	
	private void setDataHandler(TextView tv, Object tag){
		tv.setTag(tag);
		tv.setOnClickListener(dateClick);
	}
	
	private OnClickListener dateClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(v.getContext(), CalendarActivity.class);
			i.putExtra(ExtrasConst.DATE_TAG, ((Date)v.getTag()).getDate());
			startActivityForResult(i, v.getId());
		}
	};
	
	private Spanned textAsLink(String s){
		StringBuilder sb = new StringBuilder();
		sb.append("<font color='blue'><u><i>");
		sb.append(s);
		sb.append("</i></u></font>");
		return Html.fromHtml(sb.toString());
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == Activity.RESULT_OK){
			TextView tv = (TextView) findViewById(requestCode);
			
			if(tv != null){
				Date d = new Date(data.getExtras().getLong(ExtrasConst.DATE_TAG, new Date().getTime()));
				tv.setTag(d);
				tv.setText(textAsLink(Util.simpleDateFormat.format(d)));
			}
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		Editor e = p.edit();
		e.putLong(START, ((Date)tvStart.getTag()).getTime());
		e.putLong(FINISH, ((Date)tvFinish.getTag()).getTime());
		
		StringBuilder v = new StringBuilder();
		for(String s : prcsel){
			if(v.length() > 0)
				v.append(DELIM);
			
			v.append(s);
		}
		
		e.putString(PRCSEL, v.toString());
		
		v.setLength(0);
		for(Integer s : fldsel){
			if(v.length() > 0)
				v.append(DELIM);
			
			v.append(s);
		}
		
		e.putString(FLDSEL, v.toString());
		
		e.putInt(MODE, rgMode.getCheckedRadioButtonId());
		e.commit();
	}
	
	@Override
	public View getFolderView(FolderTreeNode node, View convertView) {
	    View view = super.getFolderView(node, convertView);
	    CheckBox cb = (CheckBox) view.findViewById(R.id.cbSel);
	    cb.setOnCheckedChangeListener(foldercheck);
	    cb.setTag(node.id);
	    cb.setChecked(fldsel.contains(node.id));
	    
	    return view;
	}
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View view = super.getPriceView(node, convertView);
		CheckBox cb = (CheckBox) view.findViewById(R.id.cbSel);
	    cb.setOnCheckedChangeListener(prccheck);
	    cb.setTag(node.getId());
	    cb.setChecked(prcsel.contains(node.getId()));
	    
		return view;
	}
	
	private OnCheckedChangeListener foldercheck = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			Integer id = (Integer) ((View)buttonView).getTag();
			
			if(isChecked)
				fldsel.add(id);
			else
				fldsel.remove(id);
		}
	};
	
    private OnCheckedChangeListener prccheck = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			String id = (String)((View)buttonView).getTag();
			
			if (isChecked)
				prcsel.add(id);
			else
				prcsel.remove(id);
		}
	};
}
