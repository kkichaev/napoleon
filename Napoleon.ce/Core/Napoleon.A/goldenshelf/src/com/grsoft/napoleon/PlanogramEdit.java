package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Display;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import com.grsoft.dataobjects.Contract;
import com.grsoft.dataobjects.ContractDef;
import com.grsoft.dataobjects.ContractOrgImg;
import com.grsoft.dataobjects.Planogram;
import com.grsoft.dataobjects.impl.ContractDefImpl;
import com.grsoft.dataobjects.impl.PlanogramImpl;
import com.grsoft.util.ExtrasConst;


public class PlanogramEdit extends Activity {
	public static Class<? extends Activity> activity = PlanogramEdit.class;
	private ImageView image;
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

	private void initView() {
		Planogram pl = document.getData();
		cbApproved.setChecked(pl.approved > 0);
		edRemark.setText(pl.remark);
		
		ContractDefImpl cd = new ContractDefImpl();
		ContractDef con = cd.getData();
		
		if(cd.read("id", pl.def)){
			byte[] arr = null;
			if(con.orgImg != null)
				for(ContractOrgImg img : con.orgImg){
					if (img.id.equals(pl.id))
						arr = img.photo;
				}
			
			if(arr == null)
			 arr = cd.getData().photo;
			
			if (arr != null){
				Bitmap src = BitmapFactory.decodeByteArray(arr, 0, arr.length);
				Display display = getWindowManager().getDefaultDisplay();
				Bitmap dst = Bitmap.createScaledBitmap(src, display.getWidth(), display.getHeight(), true);
				image.setImageBitmap(dst);
			}
		}
		
		cd.close();
	}

	private void init() {
		image = (ImageView) findViewById(R.id.image);
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
}
