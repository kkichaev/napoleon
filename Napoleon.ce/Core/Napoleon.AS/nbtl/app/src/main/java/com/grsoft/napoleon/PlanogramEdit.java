package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.ContractDef;
import com.grsoft.dataobjects.ContractOrgImg;
import com.grsoft.dataobjects.Planogram;
import com.grsoft.dataobjects.impl.ContractDefImpl;
import com.grsoft.dataobjects.impl.PlanogramImpl;
import com.grsoft.util.BitmapUtils;
import com.grsoft.util.ExtrasConst;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class PlanogramEdit extends Activity implements OnItemClickListener {
	public static Class<? extends Activity> activity = PlanogramEdit.class;
	//private ImageView image;
	private CheckBox cbApproved;
	private EditText edRemark;
	private PlanogramImpl document = new PlanogramImpl();
	
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

		List<ContractOrgImg> photos = new ArrayList<ContractOrgImg>();
		
		public void init(String ctrId, String orgId) {
			ContractDefImpl cd = new ContractDefImpl();
			ContractDef con = cd.getData();
			
			if(cd.read("id", ctrId)){
				if(con.orgImg != null)
					for(ContractOrgImg img : con.orgImg){
						if (img.id.equals(orgId)) {
							photos.add(img);
						}
					}

				if(photos.size() == 0) {
					ContractOrgImg coi = new ContractOrgImg();
					coi.id = orgId;
					coi.name = "Базовая планограмма";
//					coi.photo = con.photo; 
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
			
			ContractOrgImg coi = (ContractOrgImg)getItem(arg0);
			TextView tv = (TextView)view.findViewById(R.id.tvItem);
			
//			Drawable img = makePhoto(coi.photo);
			tv.setText(coi.name);
//			tv.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
			
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
		lv.setOnItemClickListener(this);
		a.init(pl.def, pl.id);
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		ContractOrgImg img = (ContractOrgImg) arg0.getItemAtPosition(arg2);
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(img.href));
		startActivity(i);
	}
}
