package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.RejectCause;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.DMPImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.util.ExtrasConst;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DMPEdit extends Activity implements OnItemClickListener, OnLongClickListener, android.content.DialogInterface.OnClickListener {
	private DMPImpl doc = new DMPImpl();
	private TextView tvOrgInfo;
	private PriceImpl price = new PriceImpl();
	private ListView list;
	private String itemSelected = "";
	List<CharSequence> items = new ArrayList<CharSequence>();
	private VisitImpl refVisit;
	private Set<String> ids = new HashSet<String>();
	
	
	public static void open(Context context, long rowid) {
		Intent i = new Intent(context, DMPEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dmpedit);
		tvOrgInfo = (TextView) findViewById(R.id.tvOrgInfo);
		list = (ListView) findViewById(R.id.list);
		
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		doc.close();
		
		OrgImpl org = new OrgImpl();
		org.read("id", doc.getId());
		tvOrgInfo.setText(org.getData().name);
		
		list.setAdapter(new DMPAdapter(this, doc.getId()));
		list.setOnItemClickListener(this);
		
		registerForContextMenu(list);
		
		DataTraveler.travel(RejectCause.class, new DataTraveler.Travel<RejectCause>(){

			@Override
			public boolean travel(DataTraveler<RejectCause> item) {
				items.add(item.data.text);
				return true;
			}}, null);
		
		refVisit = doc.getRefVisit();
	}
	
	public View getViewRow(View view, int position, OrgMatrix item) {
		price.getData().id = item.id_i;
		price.read();
		
		TextView tv = (TextView) view.findViewById(R.id.tvItem);
		tv.setText(price.getData().name);
		
		if (ids.contains(item.id_i))
			tv.setTextColor(Color.GREEN);
		else
			tv.setTextColor(Color.BLACK);
		
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
		OrgMatrix i = (OrgMatrix) parent.getItemAtPosition(position);
		
		if (i != null)
			DMPItemsList.open(this, doc.getRowid(), i.id_i);
	}

	private Dialog createRejectCauseDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setSingleChoiceItems(items.toArray(new CharSequence[items.size()]), -1, this);
		return builder.create();
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
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
		
		
		itemSelected = "";
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		refVisit.read();
		
		ids.clear();
		
		for(VisitItem i : refVisit.getData().items) {
			VisitItemEx e = (VisitItemEx)i;
			ids.add(e.itemId);
		}
		
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
