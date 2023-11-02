package com.grsoft.napoleon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentBalance;
import com.grsoft.dataobjects.BankIncass;
import com.grsoft.dataobjects.impl.BankIncassImpl;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.napoleon.documents.BankIncassDoc;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.PhotoDocument;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.PhotoClickHandler;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.RuntimeEnv;
import com.grsoft.util.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ImageView;
import android.widget.TextView;

public class BankIncassEdit extends IncassEdit implements PhotoClickHandler.EventHandler {
	
	int imageWidth = 0;
	int imageHeight = 0;
	String photoPath = "";
	VisitImpl refVisit;
	
	public static void open(Context context, BankIncassImpl doc) {
		Intent i = new Intent(context, BankIncassEdit.class);		
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		imageWidth = (int) getResources().getDimension(R.dimen.visit_preview_width);
		imageHeight = (int) getResources().getDimension(R.dimen.visit_preview_height);
		
		int balance = 0;
		AgentBalance ab = new AgentBalance();
		DbReader r = new DbReader();
		if( r.select(ab, ab.getTableName(), "userid = uid") )
			balance = ab.balance;
		r.close();
		
		String text = "Долга агента, руб: " + Util.IntToScaleStr(balance, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		((TextView)findViewById(R.id.tvInfo)).setText(text);
	
		View btnPhoto = findViewById(R.id.btnPhoto);
		btnPhoto.setOnClickListener(new PhotoClickHandler((PhotoDocument) doc, this, BankIncassDoc.instance()));
		
		CfgNplW cfg = (CfgNplW) ConfigManager.getConfig(); 
		if(!RuntimeEnv.isPhotoSupported() ||  cfg.cameraWidth == 0 || cfg.cameraHeight == 0)
			btnPhoto.setVisibility(View.GONE);
		
		registerForContextMenu(findViewById(R.id.ivPhoto));

		refVisit = ((BankIncassImpl)doc).refVisit;
		refVisit.getData().created = ((BankIncass)doc.getData()).visitDoc;

		if (!refVisit.read()){
			if(refVisit.read() == false) {
				refVisit.init(this, doc.getId(), new GpsCoord(doc.getData().latitude, doc.getData().longitude, doc.getData().stltime));
				((BankIncass)doc.getData()).visitDoc = refVisit.getData().created;
				doc.write();
				doc.close();
			}
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if(v.getId() == R.id.ivPhoto) {
			getMenuInflater().inflate(R.menu.bi_context_menu, menu);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int id = item.getItemId();
		BankIncassImpl bi = (BankIncassImpl)doc;
		if(id == R.id.itShow) {
			String path = bi.getPhoto();
			preview(path);
		} else if(id == R.id.itDel) {
			bi.delPhoto();
			refreshPhoto();
		}
		return super.onContextItemSelected(item);
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
	
	private void refreshPhoto() {
		ImageView iv = (ImageView)findViewById(R.id.ivPhoto);
		BankIncassImpl bi = (BankIncassImpl)doc;
		if(bi.count() > 0) {
			Bitmap img = createImage(bi.getPhoto().getBytes());
			if(img != null) {
				iv.setVisibility(View.VISIBLE);
				iv.setImageBitmap(img);
				return;
			}
		}
		iv.setVisibility(View.GONE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		doc.read(doc.getRowid(), false);
		refreshPhoto();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(doc.isEditable() == false)
			return;
		if(requestCode == PhotoClickHandler.CAMERA_ACTIVITY && resultCode == RESULT_OK){
			if(photoPath.trim().length() > 0) {
				((BankIncassImpl)doc).addPhoto(photoPath.getBytes());
				refreshPhoto();
			}
		}
	}
	
	
	protected Bitmap createImage(byte[] picture) {
		Bitmap result = null;
		String picSrc = new String(picture);
        try{
        	BitmapFactory.Options opt = new BitmapFactory.Options();
        	opt.inSampleSize = 4;
        	result = BitmapFactory.decodeFile(picSrc, opt);
        	result = Bitmap.createScaledBitmap(result, imageWidth, imageHeight, true);
        }
        catch (Exception e){
        	e.printStackTrace();
        }
        
        return result;
	}		

	
	@Override
	protected DocumentSender createDocumentSender() {
		List<DocExportListener> export = new ArrayList<>();
		export.add(new DocSendListner(BankIncassDoc.instance().getObjectName(), doc, doc.getRowid()));
		if (refVisit.getData().items.size() > 0)
			export.add(new DocSendListner(VisitDoc.instance().getObjectName(), refVisit, refVisit.getRowid()));

		return new DocumentSender(this, findViewById(R.id.btnSend), export);
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (isFinishing())
			if (refVisit.getData().items.size() == 0){
				refVisit.delete();
				refVisit.close();
			}
	}

	@Override
	protected int getContentViewID() {
		doc = new BankIncassImpl();
		return R.layout.bank_incass;
	}
	
	@Override
	protected boolean save() {
		boolean res = super.save();
		BankIncassDoc.instance().refreshDocSum(doc.getId());

		return  res;
	}

	@Override public void prepareBoforeClick() { doc.write(); }

	@Override public void makePhotoFile(File newFile) { photoPath = newFile.getAbsolutePath(); }
}
