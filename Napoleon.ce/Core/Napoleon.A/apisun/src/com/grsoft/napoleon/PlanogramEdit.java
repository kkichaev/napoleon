package com.grsoft.napoleon;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.grsoft.database.AgentOrgHitching;
import com.grsoft.database.PotenzialOrgHitching;
import com.grsoft.dataobjects.Planogram;
import com.grsoft.dataobjects.PlanogramDef;
import com.grsoft.dataobjects.PlanogramDefItem;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PlanogramDefImpl;
import com.grsoft.dataobjects.impl.PlanogramImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.PlanogramDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ObjectListener;
import com.grsoft.util.BitmapUtils;
import com.grsoft.util.ExtrasConst;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class PlanogramEdit extends Activity implements OnItemClickListener, OnClickListener {
	public static Class<? extends Activity> activity = PlanogramEdit.class;
	//private ImageView image;
	private CheckBox cbApproved;
	private EditText edRemark;
	private PlanogramImpl document = new PlanogramImpl();
	private final static String PREVIEW_NAME = "preview.img";
	
	public static void open(Context context, PlanogramImpl planogram){
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, planogram.getRowid());
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.planogram);
		init();
		document.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		document.close();
		initView();
	}
	
	class Adapter extends BaseAdapter {

		public List<PlanogramDefItem> photos = new ArrayList<PlanogramDefItem>();
		
		public void init(String orgId) {
			PlanogramDefImpl cd = new PlanogramDefImpl();
			PlanogramDef con = cd.getData();
			
			if(cd.read("id", orgId)){
				if(con.items != null)
					for(PlanogramDefItem img : con.items){
							photos.add(img);
					}
			}
			cd.close();
		}

		private Drawable makePhoto(byte[] arr) {
			Display display = getWindowManager().getDefaultDisplay();
			return BitmapUtils.createBitmap(PlanogramEdit.this, arr, 
					display.getWidth(), display.getHeight());
		}
		
		@Override public int getCount() { return photos.size(); }
		@Override public Object getItem(int arg0) { return photos.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(PlanogramEdit.this, R.layout.planogram_row, null);
			
			PlanogramDefItem coi = (PlanogramDefItem)getItem(arg0);
			TextView tv = (TextView)view.findViewById(R.id.tvItem);
			
			Drawable img = makePhoto(coi.photo);
			tv.setText(coi.name);
			tv.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			
			return view;
		}
		
	}

	private void initView() {
		Planogram pl = document.getData();
		cbApproved.setChecked(pl.approved > 0);
		edRemark.setText(pl.remark);
		
		Adapter a = new Adapter();
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(a);
		a.init( pl.id);
		lv.setOnItemClickListener(this);
		
		findViewById(R.id.btnSend).setOnClickListener(this);
		
		OrgImpl org = new OrgImpl();
		org.read("id", document.getId());
		((TextView)findViewById(R.id.tvOrg)).setText(org.getData().name);
	}

	private void init() {
		cbApproved = (CheckBox) findViewById(R.id.cbApproved);
		edRemark = (EditText) findViewById(R.id.edRemark);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		save();
	}

	private void save() {
		Planogram p = document.getData();
		p.approved = cbApproved.isChecked() ? 1 : 0;
		p.remark = edRemark.getText().toString().trim();
		document.write();
		document.close();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		PlanogramDefItem item = (PlanogramDefItem) parent.getItemAtPosition(position);
		String path = Path.getDataDir() + PREVIEW_NAME;
		
		if (writeToFile(path, item.photo))
			preview(path);
		
	}
	
	protected void preview(String path) {
		Intent i = new Intent();
		i.setAction(Intent.ACTION_VIEW);
		
		Uri uri = null;
		
		if (Build.VERSION.SDK_INT >= 24) 
			uri = FileProvider.getUriForFile(this,"com.grsoft.napoleon.fileprovider", new File(path)); 
		else
			uri = Uri.parse("file://" + path);
		
		i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		i.setDataAndType(uri, "image/*");
		
		startActivity(i);
	}
	
	public boolean writeToFile(String path, byte[] data) {
		boolean result = false;
		
		try {
		    InputStream in = new BufferedInputStream(new ByteArrayInputStream(data));
		    OutputStream out = new BufferedOutputStream(new FileOutputStream(path));
		    final int CPY_BUF_SIZE = 1024;
		
		    byte[] buf = new byte[CPY_BUF_SIZE];
		    int len;
		    while ((len = in.read(buf)) > 0) {
		        out.write(buf, 0, len);
		    }
		    in.close();
		    out.close();
		    
		    result = true;
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnSend)
			send();
	}

	private void send() {
		new DocumentSender(PlanogramEdit.this, null,
				PlanogramDoc.instance().getObjectName(), document, 
				document.getRowid()){
			
			protected Collection<ObjectListener> getObjectsToSend() {
				Collection<ObjectListener> result = super.getObjectsToSend();
				
				PotenzialOrgHitching poh = new PotenzialOrgHitching("Org");
				if( poh.size() > 0 ){
					result.add(poh);
					result.add(new AgentOrgHitching(poh));
				}
				
				return result;
			};
			
			protected com.grsoft.database.PotenzialOrgHitching createPotenzialOrgHitching() {return null; };
		}.execute((Void[])null);
	}
}
