/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   18/11/2011   creating
 */
package com.grsoft.napoleon;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.RegDurationActivity;

public class Presentation extends RegDurationActivity {

	public static final String TABLE_NAME = "presentation";
	public static final String PHOTO_PATH = "photoPath";

	private static final String TAG = "Presentation";
	private static final String SETTING = "presentation_setting";
	private static final String NUM_COL_STR = "num_col";

	private static final int SETTING_DLG = 1;

	public static Class<? extends Activity> activity = Presentation.class; 
	
	private long docRowId;
	private ImageAdapter adapter; 
	private GridView gvPresentation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.presentation);
		
		docRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		String folderdList = getIntent().getStringExtra(ExtrasConst.FOLDERS_LIST_STR);
		
		try{
			final String PHOTO_PATH_NOT_NULL = "pic.photoPath NOT NULL";
			final String selection = folderdList.length() > 0 ? 
					String.format("p.folderid in (%s) and %s", folderdList, PHOTO_PATH_NOT_NULL) 
					: PHOTO_PATH_NOT_NULL;
			adapter = new ImageAdapter(this, selection);
			gvPresentation = (GridView) findViewById(R.id.gvPresentation);
			gvPresentation.setAdapter(adapter);
			gvPresentation.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					String path = (String) parent.getAdapter().getItem(position);
					if(path != null)
						PricePresentation.open(view.getContext(), path, docRowId, selection);
				}				
			});
			gvPresentation.setNumColumns(getNumColFromPref());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void open(Context context, long orderId, String folders, int topFolder, Class<? extends Activity> winndow ){
		Intent intent = new Intent(context, winndow);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, orderId);
		intent.putExtra(ExtrasConst.FOLDERS_LIST_STR, folders);
		intent.putExtra(ExtrasConst.FOLDER_ID, topFolder);
		context.startActivity(intent);
	}
	
	public static void open(Context context, long orderId, String folders, int topFolder){
		open(context, orderId, folders, topFolder, activity);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		((ImageAdapter)gvPresentation.getAdapter()).close();
		Log.d(TAG, "onStop");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.presentation_opt_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		
		if (itemId == R.id.itSetting)
			showDialog(SETTING_DLG);
		return true;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case SETTING_DLG:
			return createSettingDlg();
		default:
			return super.onCreateDialog(id);
		}
	}

	private Dialog createSettingDlg() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(R.string.setting);
		b.setView(View.inflate(this, R.layout.pres_setting_dialog, null));
		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) { refreshSetting(dialog); }
		});
		b.setNegativeButton(R.string.cancel, null);
		return b.create();
	}

	protected void refreshSetting(DialogInterface dialog) {
		int pos = ((Spinner)
				((AlertDialog)dialog).findViewById
					(R.id.spNumCol)).getSelectedItemPosition();
		pos = pos == 0 ? -1 : pos;
		SharedPreferences pref = getSharedPreferences(SETTING, MODE_PRIVATE);
		Editor edit = pref.edit();
		edit.putInt(NUM_COL_STR, pos);
		edit.commit();
		gvPresentation.setNumColumns(pos);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id){
		case SETTING_DLG: prepareSettingDlg(dialog); break;
		}
	}

	int getNumColFromPref(){
		SharedPreferences pref = getSharedPreferences(SETTING, MODE_PRIVATE);
		return pref.getInt(NUM_COL_STR, -1);
	}
	
	private void prepareSettingDlg(Dialog dialog) {
		int numCol = getNumColFromPref();
		((Spinner)dialog.findViewById(R.id.spNumCol))
			.setSelection(numCol == -1 ? 0 : numCol);
	}
	
	public static String makeSelectStmt(String condition) {
		String prcTable = DataObjectInfo.getInstance().getTableName(Price.class);
		String stmt = "select distinct pic." + Presentation.PHOTO_PATH + " from " + Presentation.TABLE_NAME + " pic, " + 
				prcTable + " p where pic.id = p.id";
		
		if( condition != null && condition.length() > 0 )
			stmt += " AND " + condition;
		
		stmt += " order by p.folderid, p.srchName";
		return stmt;
	}
}

class ImageAdapter extends BaseAdapter{
	@SuppressWarnings("unused")
	private static final String TAG = "ImageAdapter";
	private Context context;
	private Cursor cursor;
	
	public ImageAdapter(Context context, String condition){
		this.context = context;
		
		String stmt = Presentation.makeSelectStmt(condition);
		SQLiteDatabase db = DataBaseManager.getDataBase();
		cursor = db.rawQuery(stmt, null);
		
//		cursor = db.query(true, Presentation.TABLE_NAME, 
//				new String[]{Presentation.PHOTO_PATH}, condition, null, null, 
//				null, null, null);
	}
	
	Map<String, WeakReference<Bitmap>> pictures = new WeakHashMap<String, WeakReference<Bitmap>>();

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		
		if (convertView == null )
			imageView = new ImageView(context);
		else
			imageView = (ImageView) convertView;
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        
		int screenWidth = 
        		((Activity)context).getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ?
        				displaymetrics.heightPixels : displaymetrics.widthPixels;
        
        final int space = 20;
        int colCount = ((Presentation)context).getNumColFromPref();
        colCount = colCount == -1 ? 3 : colCount;
        int picSize = (screenWidth - space) / colCount; 
		imageView.setLayoutParams(new GridView.LayoutParams(picSize, picSize));
		imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		
		String picSrc = (String)getItem(position);
		
		if(picSrc != null){
			try{
				WeakReference<Bitmap> b = pictures.get(picSrc);
				Bitmap bd = null;
		        try {
					if( b == null  || b.get() == null ) {
			        	BitmapFactory.Options opt = new BitmapFactory.Options();
			        	opt.inSampleSize = 3;
						bd = BitmapFactory.decodeFile(picSrc, opt);
						pictures.put(picSrc, new WeakReference<Bitmap>(bd));
					}
					else 
						bd = b.get();
		        } catch(Exception e) {
		        	e.printStackTrace();
		        }
	        	imageView.setImageBitmap(bd);
				
//				Bitmap bm = pictures.get(picSrc);
//				if( bm == null ) {
//		        	BitmapFactory.Options opt = new BitmapFactory.Options();
//		        	opt.inSampleSize = 3;
//		        	bm = BitmapFactory.decodeFile(picSrc, opt);
//		        	pictures.put(picSrc, bm);
//				}
//	        	imageView.setImageBitmap(bm);
	        }
	        catch (Exception e){
	        	e.printStackTrace();
	        }
		}
        
		return imageView;
	}

	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public Object getItem(int arg0) {
		if (cursor.moveToPosition(arg0))
			return cursor.getString(
					cursor.getColumnIndex(Presentation.PHOTO_PATH));
		else
			return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
	
	public void close(){
		cursor.close();
	}
}