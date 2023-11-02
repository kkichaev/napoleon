package com.grsoft.napoleon;

import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.DMPImpl;
import com.grsoft.dataobjects.impl.DMPTypeImpl;
import com.grsoft.util.BitmapUtils;
import com.grsoft.util.ExtrasConst;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DMPItemEdit extends Activity implements OnClickListener, OnLongClickListener {
	private DMPImpl doc = new DMPImpl();
	private String priceID = "", dmpID = "";
	private View btnPhoto;
	LinearLayout preview;
	
	private final static int PHOTO_REQUEST = 0;
	public static final String REFRESH_ACTION = "REFRESH_ACTION";
	
	private final static String DMP_ID = "dmp_id"; 
	
	public static void open(Context context, long rowid, String priceID, String dmpID) {
		Intent i = new Intent(context, DMPItemEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		i.putExtra(DMPItemsList.PRICE_ID, priceID);
		i.putExtra(DMP_ID, dmpID);
		
		context.startActivity(i);
	}
	
	BroadcastReceiver refresh = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			initPreview();
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dmpitemedit);
		
		btnPhoto = findViewById(R.id.btnPhoto);
		preview = (LinearLayout) findViewById(R.id.preview);
		TextView tv = (TextView) findViewById(R.id.tvOrgInfo);
		
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		doc.close();
		
		if(doc.isEditable())
			btnPhoto.setOnClickListener(this);
		
		priceID = getIntent().getStringExtra(DMPItemsList.PRICE_ID);
		dmpID = getIntent().getStringExtra(DMP_ID);
		
		DMPTypeImpl t = new DMPTypeImpl();
		t.read("id", dmpID);
		
		tv.setText(t.getData().text);
		
		initPreview();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
			String f = data.getStringExtra(CameraPreview.PHOTO_PATH);
			
			if(f != null && f.trim().length() > 0) {
				doc.addPhoto(priceID, dmpID, f.getBytes());
				
				initPreview();
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnPhoto)
			takePhoto();
	}

	private void takePhoto() {
		Intent i = new Intent(this, CameraPreview.class);
		startActivityForResult(i, PHOTO_REQUEST);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(refresh, new IntentFilter(REFRESH_ACTION));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(refresh);
	}
	
	protected void initPreview() {
		int w = (int) getResources().getDimension(R.dimen.previewPhotoWidth);
		int h = (int) getResources().getDimension(R.dimen.previewPhotoHight);
		
		int space = (int) getResources().getDimension(R.dimen.previewPhotoSpace);
		
		preview.removeAllViews();
		
		Visit v = doc.getRefVisit().getData();
		
		for(int i = 0; i < v.items.size(); i++){
			VisitItemEx vi = (VisitItemEx) v.items.get(i); 
			
			if (vi.itemId.endsWith(priceID) && vi.dmpId.endsWith(dmpID)){
				String p = new String(vi.id);
				TextView t = new TextView(this);
				t.setCompoundDrawablesWithIntrinsicBounds(null, BitmapUtils.createBitmap(this, p, w, h), null, null);
				t.setOnLongClickListener(this);
				t.setTag(vi.key);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(0, 0, space, 0);
				lp.gravity = Gravity.CENTER_VERTICAL;
				t.setLayoutParams(lp);
				preview.addView(t);
			}
		}
	}

	@Override
	public boolean onLongClick(View v) {
		ManagePhotoDlgEx dlg = new ManagePhotoDlgEx();
		Bundle args = new Bundle();
		args.putString(ManagePhotoDlg.PIC_ID, v.getTag().toString());
		args.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		dlg.setArguments(args);
		dlg.show(getFragmentManager(), dlg.getClass().getCanonicalName());
		return true;

	}
}
