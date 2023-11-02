package com.grsoft.napoleon;

import java.io.File;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.Display;
import com.grsoft.dataobjects.DisplayItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DisplayImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DisplayDoc;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.SrcDataCounter;
import com.grsoft.view.BaseActivity;

public class DisplayEdit extends BaseActivity implements SendResultListener {

	private static final int CAMERA_ACTIVITY = 10;
	
	/**
	 * растояние между фотками 
	 */
	private static final int GAP = 10;
	
	/**
	 * Число колонок в портретонм режиме
	 */
	private static final int COLUMN_PORTRAIT_DEFAULT = 3;

	private static final int ITEM_MENU_DIALOG = 0;
	
	String picPath;
	long rowid;
	boolean cache = true;
	int picSize = 0;
	int columnLandscape = 0, columnPortrait = 0;

	protected static final int GET_FOLDER_ACTIVITY = 320;
	protected DisplayItem editingItem;

	SetFolderHandler fh = new SetFolderHandler();
	ItemMenu imenu = new ItemMenu(); 
			
	DisplayImpl doc = new DisplayImpl();
	OrgImpl oi = new OrgImpl();
	DisplayAdapter da = new DisplayAdapter();
	
	public static void open(Context ctx, DisplayImpl doc) {
		Intent i = new Intent(ctx, DisplayEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		ctx.startActivity(i);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == ITEM_MENU_DIALOG ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			CharSequence[] items = { "Просмотр", "Удалить"};
			b.setItems(items, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if( editingItem != null ) {
						String str = new String(editingItem.id);
						if( which == 0 ) {
							preview(str);
						} else if(which == 1) {
							removeItem(editingItem, str);
						}
					}
					editingItem = null;
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		doc.read(rowid, cache);
		da.notifyDataSetChanged();
		cache = false;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.display);
		
		rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		if( rowid != ExtrasConst.INVALID_ID )
			doc.read(rowid);
		
		OrgEx o = (OrgEx) oi.getData();
		o.id = doc.getId();
		oi.read();
		oi.close();

		calcPictureSize();

		da = new DisplayAdapter();
		GridView gv = (GridView)findViewById(R.id.gvItems);
		gv.setAdapter(da);

		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { send(); }
		});
		
		View photo = findViewById(R.id.btnPhoto); 
		if( !doc.isExported() ) {
			photo.setOnClickListener(new PhotoClickListener());
		} else {
			photo.setEnabled(false);			
		}
	}
	
	protected void send() {
		new DocumentSender(this, findViewById(R.id.btnSend), DisplayDoc.OBJ_NAME, doc, doc.getRowid(), this)
			.execute((Void[])null);
	}

		
	private void preview(String path) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View dialogView = View.inflate(this, R.layout.image_show, null);
		ImageView preview = (ImageView) dialogView.findViewById(R.id.imageView1);
		Bitmap bm = BitmapFactory.decodeFile(path);
    	preview.setImageBitmap(bm);
		builder.setView(dialogView);
		builder.create().show();
	}

	private void removeItem(DisplayItem item, String path) {
		if( !doc.isExported() ) {
			File file = new File(path);
			file.delete();
			doc.getData().items.remove(item);
			doc.write();
			da.notifyDataSetChanged();
		}
	}

	private void calcPictureSize() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        
		int screenWidth = Math.min(displaymetrics.widthPixels, displaymetrics.heightPixels);
		int screenHeight = Math.max(displaymetrics.widthPixels, displaymetrics.heightPixels);

		picSize = screenWidth / COLUMN_PORTRAIT_DEFAULT - GAP;
		columnPortrait = COLUMN_PORTRAIT_DEFAULT;
		columnLandscape = screenHeight / (picSize + GAP);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		int curColumn = (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) ? columnPortrait : columnLandscape;
		GridView gv = (GridView)findViewById(R.id.gvItems);
		gv.setNumColumns(curColumn);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(doc.isExported())
			return;
		
		if(requestCode == CAMERA_ACTIVITY && resultCode == RESULT_OK){
			if(picPath != null && picPath.trim().length() > 0) {
				doc.addPhoto(picPath.getBytes());
				da.notifyDataSetChanged();
			}
		}
		
		if( data != null && requestCode == GET_FOLDER_ACTIVITY && editingItem != null ) {
			String fld = data.getExtras().getString(ExtrasConst.FOLDER_ID);
			if( fld != null ) {
				editingItem.folder = fld;
				doc.write();
				da.notifyDataSetChanged();
			}
			editingItem = null;
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		doc.close();
	}
	
	@Override
	public void postSendExecute(boolean result) {
		if( result ) {
			findViewById(R.id.btnPhoto).setEnabled(false);
		}
	}

	class PhotoClickListener extends OnClickListenerToNotify {

		@Override
		public void onClick(View v) {
			super.onClick(v);
			long lim = ((CfgNplW)ConfigManager.getConfig()).max_packet_len;
			if((doc.size() + 200) > lim){
				Toast.makeText(v.getContext(), R.string.over_limit_photo, Toast.LENGTH_LONG).show();
			}else{
				com.grsoft.napoleon.util.CfgNpl cfg = 
						(com.grsoft.napoleon.util.CfgNpl) ConfigManager.getConfig();
				
				if (cfg.dataDirShare && cfg.androidPhoto)
					openPhotoActivity();
				else
					CameraPreview.open(v.getContext(), doc);
			}
		}

		private void openPhotoActivity() {
			try{
				File path = new File(Path.getDataDir());
				path.mkdir();
				File file = new File(Path.getDataDir(), Integer.toString(SrcDataCounter.getValue()));
				picPath = file.getAbsolutePath();
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			    startActivityForResult(intent, CAMERA_ACTIVITY);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	class SetFolderHandler implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			DisplayItem d = (DisplayItem)v.getTag();
			if( d != null) {
				editingItem = d;
				Intent i = new Intent(DisplayEdit.this, FolderTree.class);
				i.putExtra(ExtrasConst.FOLDER_ID, d.folder);
				startActivityForResult(i, GET_FOLDER_ACTIVITY);
			}
		}		
	}
	
	class ItemMenu implements View.OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {
			DisplayItem d = (DisplayItem)v.getTag();
			if( d != null) {
				editingItem = d;
				showDialog(ITEM_MENU_DIALOG);
			}
			return true;
		}
		
	}
	
	class DisplayAdapter extends BaseAdapter {

		HashMap<String, BitmapDrawable> pictures = new HashMap<String, BitmapDrawable>();

		@Override
		public int getCount() { return doc.getData().items.size(); }

		@Override
		public Object getItem(int position) { 
			return doc.getData().items.get(position); 
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			//if( view == null )
				view = new TextView(DisplayEdit.this);
			
			TextView tv = (TextView)view;
			tv.setLayoutParams(new GridView.LayoutParams(picSize, picSize));
			tv.setTextSize(12);
			
			DisplayItem di = (DisplayItem)getItem(position);
			if(di.id != null){
				tv.setTag(di);
				if( !doc.isExported() )
					tv.setOnClickListener(fh);

				tv.setOnLongClickListener(imenu);
				tv.setText(di.folder);
				tv.setTextColor(Color.BLACK);
				
				try{
					String picSrc = new String(di.id);
					BitmapDrawable bm = pictures.get(picSrc);
					if( bm == null ) {
			        	BitmapFactory.Options opt = new BitmapFactory.Options();
			        	opt.inSampleSize = 1;
			        	Bitmap bmp = BitmapFactory.decodeFile(picSrc, opt);
			        	bmp = Bitmap.createScaledBitmap(bmp, picSize, picSize, true);
			        	bm = new BitmapDrawable(bmp);
			        	pictures.put(picSrc, bm);
					}

					tv.setCompoundDrawablesWithIntrinsicBounds(null, bm, null, null);
				}
		        catch (Exception e){
		        	e.printStackTrace();
		        }
			}
			return view;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if( keyCode == KeyEvent.KEYCODE_BACK) {
			
			if( doc.isExported() == false && doc.getData().items.size() == 0 ) {
				doc.delete();
			} else {
				DisplayDoc.instance().refreshDocSum(doc.getId());
			}
			
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
