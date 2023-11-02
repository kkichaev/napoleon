package com.grsoft.napoleon;

import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Fridge;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.util.ExtrasConst;

public class DocumentsEx extends Documents {

	static final String DOC_DATA = "docdata";

	public static final int DLG_DEBT_OVERDUE = 20;
	public static final int DLG_DEBT_OVERDUE_DATE = 21;

	static public void open(Context context, SalesEx doc) {
		Intent i = new Intent(context, activity);

		i.putExtra(ExtrasConst.ORG_ID_STR, doc.id);
		context.startActivity(i);
	}

	@Override
	protected int getContentViewID() {	return R.layout.documentsex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ImageButton btnFridges = (ImageButton) findViewById(R.id.btnFridges);
		
		if(((OrgEx)org.getData()).fridges.size() > 0){
			btnFridges.setEnabled(true);
			btnFridges.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) { showDialog(R.id.fridges_dls);	}
			});
			
		}else
			btnFridges.setEnabled(false);
	}

	@Override
	protected void onResume() {
		super.onResume();

		SalesEx sales = SalesEx.getNoNumber();
		if(sales != null) {
			SalesImpl si = (SalesImpl) SalesDoc.instance().create();
			Sales s = si.getData();
			s.created = sales.created;
			si.read();
			si.close();
			si.open(this);
			Toast.makeText(this, "«аполните номер документа или удалите его", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.fridges_dls)
			return createFridgesDlg();
		else if (id == DLG_DEBT_OVERDUE_DATE)
			return createOverdueDate();
		else if (id == DLG_DEBT_OVERDUE)
			return createDebtOverdue();
		
		else return super.onCreateDialog(id);
	}

	private Dialog createFridgesDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		List<Fridge> fridges = ((OrgEx)org.getData()).fridges;
		CharSequence title[] = new CharSequence[fridges.size()];
		
		for(int i = 0; i < title.length; i++)
			title[i] = fridges.get(i).name;
		
		builder.setItems(title, null);
		builder.setTitle(R.string.fridges);
		
		return builder.create();
	}
	private Dialog createOverdueDate() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.warning);
		builder.setMessage(R.string.debt_overdue_date);
		
		builder.setPositiveButton(R.string.go_on, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				createNewDoc();
			dialog.dismiss();
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			finish();
			}
		});
		return builder.create();	
	}
	
    @Override
    protected void doCreate() {
		if (!isGpsPosValid()) { makeLocationAlert(); return;}
		SQLiteDatabase db = DataBaseManager.getDataBase();
		String table = DataObjectInfo.getInstance().getTableName(Delivery.class);
		String sql = "select min (paydate) from " + table + " where sumD > 0 and id = '" + org.getData().id + "'";
		Cursor c = null;
		try{
			c = db.rawQuery(sql, null);
			if (c.moveToNext()){
				long d = c.getLong(0);
					if (d==0){
					createNewDoc();
					return;
				}
				Date date = new Date(d);
				if (date.compareTo(new Date()) < 10) showDialog(DLG_DEBT_OVERDUE_DATE);
				else showDialog(DLG_DEBT_OVERDUE);
			}
			else
				createNewDoc();
			
		    } catch (Exception e) {
				e.printStackTrace();
				createNewDoc();
			} finally {
			if( c != null )
				c.close();	
		}
	}
    
	private Dialog createDebtOverdue() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.warning);
		builder.setMessage(R.string.debt_overdue);
		
		builder.setPositiveButton(R.string.go_on, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				createNewDoc();
			dialog.dismiss();
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			finish();
			}
		});
		return builder.create();	
	}

}
