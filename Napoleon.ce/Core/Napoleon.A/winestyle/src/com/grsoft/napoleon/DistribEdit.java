package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DistribRemark;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.RejectCause;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.DistribImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.ExtrasConst;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DistribEdit extends Activity implements OnItemClickListener, OnClickListener, OnLongClickListener, android.content.DialogInterface.OnClickListener {
	private DistribImpl doc = new DistribImpl();
	private TextView tvOrgInfo;
	private PriceImpl price = new PriceImpl();
	private ListView list;
	private String itemSelected = "";
	List<CharSequence> items = new ArrayList<CharSequence>();
	private final static int PHOTO_REQUEST = 0;
	
	public static void open(Context context, long rowid) {
		Intent i = new Intent(context, DistribEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.distribedit);
		tvOrgInfo = (TextView) findViewById(R.id.tvOrgInfo);
		list = (ListView) findViewById(R.id.list);
		
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		doc.close();
		
		OrgImpl org = new OrgImpl();
		org.read("id", doc.getId());
		tvOrgInfo.setText(org.getData().name);
		
		list.setAdapter(new DistribAdapter(this, doc.getId()));
		
		if (doc.isEditable())
			list.setOnItemClickListener(this);
		
		registerForContextMenu(list);
		
		DataTraveler.travel(RejectCause.class, new DataTraveler.Travel<RejectCause>(){

			@Override
			public boolean travel(DataTraveler<RejectCause> item) {
				items.add(item.data.text);
				return true;
			}}, null);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		VisitItemEx di = doc.findPhotoItem(itemSelected);
		
		if (di != null && di.id != null && di.id.length > 0)
			getMenuInflater().inflate(R.menu.distrib_conext_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itDel) {
			doc.deletePhotoItem(itemSelected);
			((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
			return true;
		}else if (item.getItemId() == R.id.itView) {
			viewItem();
			return true;
		}else
			return super.onContextItemSelected(item);
	}

	private void viewItem() {
		VisitItemEx di = doc.findPhotoItem(itemSelected);
		
		if(di != null && di.id != null && di.id.length > 0) {
			Intent i = new Intent();
			i.setAction(Intent.ACTION_VIEW);
			i.setDataAndType(Uri.parse("file://" + new String(di.id)), "image/*");
			startActivity(i);
		}
	}

	public View getViewRow(View view, int position, OrgMatrix item) {
		price.getData().id = item.id_i;
		price.read();
		
		TextView tv = (TextView) view.findViewById(R.id.tvItem);
		tv.setText(price.getData().name);
		
		DistribRemark dr = doc.findRemarkItem(item.id_i);
		tv = (TextView) view.findViewById(R.id.tvRemark);
		tv.setText(dr != null ? dr.remark : "");
		
		final ImageView iv = (ImageView) view.findViewById(R.id.ivPic);
		iv.setTag(item.id_i);
		
		if (doc.isEditable()) {
			iv.setOnClickListener(this);
			iv.setOnLongClickListener(this);
		}
		
		iv.setImageResource(android.R.color.transparent);
		
		VisitItemEx dp = doc.findPhotoItem(item.id_i);
		if(dp != null && dp.id != null && dp.id.length > 0) 
			iv.setImageBitmap(createImage(dp.id));
		
		return view;
	}

	private Bitmap createImage(byte[] photo) {
		Bitmap result = null;
		String picSrc = new String(photo);
        try{
        	BitmapFactory.Options opt = new BitmapFactory.Options();
        	opt.inSampleSize = 4;
        	result = BitmapFactory.decodeFile(picSrc, opt);
        	result = Bitmap.createScaledBitmap(result, 100, 85, true);
        }
        catch (Exception e){
        	e.printStackTrace();
        }
        
        return result;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		itemSelected = ((OrgMatrix)parent.getItemAtPosition(position)).id_i;
		VisitItemEx dp = doc.findPhotoItem(itemSelected);
		
		if (dp == null)
			showDialog(R.id.reject_cause_dlg);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.reject_cause_dlg)
			return createRejectCauseDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createRejectCauseDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setSingleChoiceItems(items.toArray(new CharSequence[items.size()]), -1, this);
		return builder.create();
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.ivPic) {
			itemSelected = v.getTag().toString();
			
			Intent i = new Intent(this, CameraPreview.class);
			startActivityForResult(i, PHOTO_REQUEST);
		}
	}

	@Override
	public boolean onLongClick(View v) {
		if(v.getId() == R.id.ivPic) {
			itemSelected = v.getTag().toString();
			openContextMenu(v);
			return true;
		}
		
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
			String f = data.getStringExtra(CameraPreview.PHOTO_PATH);
			
			if(f != null && f.trim().length() > 0) {
				doc.addPhoto(itemSelected, f.getBytes());
				((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
				itemSelected = "";
			}
		}
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
		
		doc.setRemark(itemSelected, items.get(which).toString());
		((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
		itemSelected = "";
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (isFinishing()) {
			if (doc.isEditable() && doc.emptyItems() && doc.getRefVisit().isEmpty()) {
				doc.delete();
				doc.close();
			}
		}
	}
}
