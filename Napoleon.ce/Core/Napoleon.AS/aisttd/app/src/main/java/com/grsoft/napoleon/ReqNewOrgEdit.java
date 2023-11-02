package com.grsoft.napoleon;

import java.io.File;

import com.grsoft.dataobjects.ReqNewOrg;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.dataobjects.impl.ReqNewOrgImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.PhotoDocument;
import com.grsoft.napoleon.documents.ReqNewOrgDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.ImagesItemsAdapter;
import com.grsoft.napoleon.util.PhotoClickHandler;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.RuntimeEnv;
import com.grsoft.view.BaseActivity;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.Toast;

public class ReqNewOrgEdit extends BaseActivity implements SendResultListener, PhotoClickHandler.EventHandler {
	
	ReqNewOrgImpl doc = new ReqNewOrgImpl();

	static final String PIC_PATH = "pic_path";

	ImagesItemsAdapter adapter;
	Gallery gPictures;
	protected String picPath;
	boolean cache = true;
	
	public static void open(Context context, ReqNewOrgImpl doc) {
		Intent i = new Intent(context, ReqNewOrgEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.reg_new_org);
		
		long rid = (savedInstanceState != null) ? 
				savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID) : 
				getIntent().getExtras().getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID) ;
				
		doc.read(rid);
		
		ReqNewOrg rq = doc.getData();
		setField(R.id.edINN, rq.inn);
		setField(R.id.edOGRN, rq.ogrn);
		setField(R.id.edName, rq.name);
		setField(R.id.edJurAdr, rq.jurAddress);
		setField(R.id.edAddress, rq.address);
	
		ImageButton btnPhoto = (ImageButton) findViewById(R.id.btnPhoto);
		btnPhoto.setOnClickListener(new PhotoClickHandler((PhotoDocument) doc, this, ReqNewOrgDoc.instance()));
		
		gPictures = (Gallery) findViewById(R.id.gPictures);
		
		CfgNplW cfg = (CfgNplW) ConfigManager.getConfig(); 
		if(!RuntimeEnv.isPhotoSupported() || 
				cfg.cameraWidth == 0 || cfg.cameraHeight == 0)
		{
			btnPhoto.setVisibility(View.GONE);
			gPictures.setVisibility(View.GONE);
		} else {
			registerForContextMenu(gPictures);
		}

		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { send(); }
		});
	}
	
	void setEnabled(Boolean enabled) {
		for(int id : new int[] {R.id.edINN, R.id.edOGRN, R.id.edName, R.id.edJurAdr, R.id.edAddress, R.id.btnPhoto}) {
			findViewById(id).setEnabled(enabled);
		}
	}
	
	protected void send() {
		saveDoc();
		if(!doc.isEmpty()) {
			new DocumentSender(this, findViewById(R.id.btnSend), "ReqNewOrg", doc, doc.getRowid(), this).execute((Void[])null);
		} else {
			Toast.makeText(this, "Не могу отправить пустой документ", Toast.LENGTH_SHORT).show();
		}
	}

	void setField(int id, String value) {
		EditText ed = (EditText)findViewById(id);
		ed.setText(value);
	}
	
	String getEdField(int id) {
		EditText ed = (EditText)findViewById(id);
		return ed.getText().toString();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		outState.putString(PIC_PATH, picPath);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		picPath = savedInstanceState.getString(PIC_PATH);
	}

	@Override
	public void prepareBoforeClick() {
		saveDoc();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		saveDoc();
		if(doc.isEmpty())
			doc.delete();
	}
	
	void saveDoc() {
		ReqNewOrg rq = doc.getData();
		rq.inn = getEdField(R.id.edINN);
		rq.ogrn = getEdField(R.id.edOGRN);
		rq.name = getEdField(R.id.edName);
		rq.jurAddress = getEdField(R.id.edJurAdr);
		rq.address = getEdField(R.id.edAddress);
		
		
		doc.write();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		doc.read(doc.getRowid(), cache);
		cache = false;

		adapter = new ImagesItemsAdapter(this, doc.getData().items);
		gPictures.setAdapter(adapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		((BaseAdapter)gPictures.getAdapter()).notifyDataSetChanged();
		
		if(requestCode == PhotoClickHandler.CAMERA_ACTIVITY && resultCode == RESULT_OK){
			if(picPath != null && picPath.trim().length() > 0)
				((PhotoDocument)doc).addPhoto(picPath.getBytes());
		}
	}
	
	@Override
	public void makePhotoFile(File newFile) {
		picPath = newFile.getAbsolutePath();
	}

	@Override
	public void postSendExecute(boolean result) {
		if(result) {
			doc.read(doc.getRowid(), false);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Log.d(Consts.D_TAG,"VisitEdit.onCreateContextMenu");
		
		getMenuInflater().inflate(R.menu.visit_context_menu, menu);
		
		if(!doc.isEditable()){
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
		
		VisitItem visitItem = (VisitItem)adapter.getItem(menuInfo.position);
		String picPath = new String(visitItem.id);
		if (item.getItemId() == R.id.itDelete) {
			removeItem(visitItem, picPath);
		} else if (item.getItemId() == R.id.itShow) {
			preview(picPath);
		}
		return super.onContextItemSelected(item);
	}

	private void removeItem(VisitItem visitItem, String picPath) {
		File file = new File(picPath);
		file.delete();
		doc.getData().items.remove(visitItem);
		doc.getData().sendedPhotos = 0;
		
		doc.write();
		adapter.notifyDataSetChanged();
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
