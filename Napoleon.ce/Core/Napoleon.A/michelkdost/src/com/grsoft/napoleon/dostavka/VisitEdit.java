package com.grsoft.napoleon.dostavka;

import java.io.File;
import java.util.ArrayList;
import com.grsoft.dataobjects.DVisit;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DVisitDoc;
import com.grsoft.napoleon.util.ImagesItemsAdapter;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;

public class VisitEdit extends Activity{
	public static Class<? extends Activity> activity = VisitEdit.class;
	
	protected CreatableDocument<? extends DVisit> visit = createDocument();
	protected EditText edNotes;
	private ImageButton btnPhoto;
	protected Gallery gPictures;
	//protected BaseAdapter adapter;
	protected String picPath;
	
	ArrayList<CharSequence> visitCause = new ArrayList<CharSequence>(); 
	final static int ASK_TO_DEL_VISIT_MSG = R.id.ask_to_del_visit_msg;

	private static final String PIC_PATH = "pic_path";
	
	@SuppressWarnings("unchecked")
	protected CreatableDocument<? extends DVisit> createDocument() { return (CreatableDocument<? extends DVisit>) DVisitDoc.instance().create(); }
	
	
	@Override
	public void onBackPressed() {
		if(visit.isEditable()){
			if(!saveVisit()){
				visit.delete();
				visit.close();
			}
		}
			
		super.onBackPressed();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == ASK_TO_DEL_VISIT_MSG)
			return createAskToDelDlg();
		else
			return super.onCreateDialog(id);
	}
	
	private Dialog createAskToDelDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert);
		builder.setMessage(R.string.askToDelVisitMsg);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				visit.delete();
				finish();
			}
		});
		
		builder.setNegativeButton(R.string.cancel, null);
		
		return builder.create();
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveVisit();
	}
	
	protected boolean saveVisit() {
		DVisit v = visit.getData();
		v.remark = edNotes.getText().toString();
		
		boolean canDelete = (v.remark.trim().length() == 0 && v.items.size() == 0);
		
		visit.write();
		visit.close();
		
		return !canDelete;
	}
	
	boolean cache = true;

	private android.view.View.OnClickListener photoClick = new android.view.View.OnClickListener() {
		@Override public void onClick(View v) { 
			cache = false;
			picPath = PhotoUtil.takePhoto(v.getContext()); 
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(getContentView());
		
		init(savedInstanceState);
	}

	protected int getContentView() {
		return R.layout.visitedit;
	}
	
	static public void open(Context context, long rowid) {
		Intent i = new Intent(context, activity);		
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);		
	}

	protected void init(Bundle savedInstanceState) {
		edNotes = (EditText) findViewById(R.id.edNotes);
		
		long visitRowId = ExtrasConst.INVALID_ID;
		
		if (savedInstanceState == null)
		{
			visitRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		}
		else
		{
			visitRowId = savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR);
		}
		
		if (visitRowId != ExtrasConst.INVALID_ID)
		{
			visit.read(visitRowId);
			edNotes.setText(visit.getData().remark);
		}

		btnPhoto = (ImageButton) findViewById(R.id.btnPhoto);
		btnPhoto.setOnClickListener(photoClick );
		
		gPictures = (Gallery) findViewById(R.id.gPictures);
		gPictures.setAdapter(createImageAdapter());
		
		setEditableControl(visit.isEditable());
	}

	protected BaseAdapter createImageAdapter() {
		return new ImagesItemsAdapter(this, visit.getData().items);
	}
	
	protected void setEditableControl(boolean isEditable)
	{
		edNotes.setEnabled(isEditable);
		btnPhoto.setEnabled(isEditable);
		
		setContextMenu(isEditable);
	}

	protected void setContextMenu(boolean isEditable) {
		registerForContextMenu(gPictures);
	}
		
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		saveVisit();
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, visit.getRowid());
		outState.putString(PIC_PATH, picPath);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		picPath = savedInstanceState.getString(PIC_PATH);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK && requestCode == R.id.photo_dlg_result){
			if(picPath != null && picPath.trim().length() > 0){
				DVisit v = (DVisit) visit.getData();
				VisitItem i = new VisitItem();
				i.id = picPath.getBytes();
				v.items.add(i);
				visit.write();
				visit.close();
				
				gPictures.setAdapter(createImageAdapter());
			}
		}
	}
		
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Log.d(Consts.D_TAG,"VisitEdit.onCreateContextMenu");
		
		getMenuInflater().inflate(R.menu.visit_context_menu, menu);
		
		if(!visit.isEditable()){
			MenuItem mi = menu.findItem(R.id.itDelete);
			if(mi != null)
				mi.setVisible(false);
		}
		
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.d(Consts.D_TAG,"VisitEdit.onContextItemSelected");
		
		AdapterView.AdapterContextMenuInfo menuInfo = 
			(AdapterContextMenuInfo) item.getMenuInfo();
		
		VisitItem visitItem = (VisitItem)gPictures.getAdapter().getItem(menuInfo.position);
		String picPath = new String(visitItem.id);
		if (item.getItemId() == R.id.itDelete) {
			removeItem(menuInfo.position, picPath);
		} else if (item.getItemId() == R.id.itShow) {
			preview(picPath);
		}
		return super.onContextItemSelected(item);
	}

	private void removeItem(int pos, String picPath) {
		File file = new File(picPath);
		file.delete();
		visit.getData().items.remove(pos);
		visit.write();
		gPictures.setAdapter(createImageAdapter());
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
}
