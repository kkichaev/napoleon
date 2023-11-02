package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.MatrixOrder;
import com.grsoft.dataobjects.MatrixOrderItem;
import com.grsoft.dataobjects.MtxStageItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class WarehouseEx extends WarehouseNew {
	int curMatrix = 0;
	List<MatrixOrderItem> mtxitems;
	boolean hideMatrix = true;
	private String LAST_DOC_TYPE = "last_doc_type";
	private String[] mtxremark = null; 
	
	private OnClickListener setMatrixRemark = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Spinner sp = (Spinner)((AlertDialog)dialog).findViewById(R.id.spRemark);
			setCurmatrixRemark(sp.getSelectedItem().toString().trim());
			dialog.dismiss();
		}
	}; 
	
	@Override
	protected int getLayoutId() {
		return R.layout.warehouse_ex;
	}
	
	protected void setCurmatrixRemark(String rem) {
		MtxStageItem m = getMtxStageItem();
		
		if(m != null)
			m.remark = rem;
		
		document.write();
		document.close();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.ibNextPrice).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(stepComplete()){
					curMatrix++;
					resetMatrix();
				}else
					Toast.makeText(v.getContext(), R.string.matrix_step_error, Toast.LENGTH_SHORT).show();
			}
		});
		
		StringBuilder sb = new StringBuilder();
		ConfigImpl cfg = new ConfigImpl();
		cfg.getValue(sb, "mtxremark");
		
		mtxremark = sb.toString().trim().split(";");
	}
	
	protected boolean stepComplete() {
//		boolean isCompete = false;
//		
//		Set<String> ids = new HashSet<String>();
//		for(OrderItem i : ((Order)document.getData()).items)
//			ids.add(i.id);
//		
//		MatrixAdapter a = (MatrixAdapter) lvItemSelect.getAdapter();
//		for(MatrixItem i : a.getMatrix().items)
//			if (ids.contains(i.id)){
//				isCompete = true;
//				break;
//			}
//		
//		if(!isCompete)
//			isCompete = getCurMatrixRemark().trim().length() > 0;
		
		/**
		 *  омментарий в категории об€зателен в любом случае, продал или не продал, кроме последней категории, там оставл€ем как есть
		 *  абанов 19.09.2016
		 * */
		return getCurMatrixRemark().trim().length() > 0;
	}

	@Override
	protected BaseAdapter createListAdapter() {
		String lastDocType = getPreferences(Context.MODE_PRIVATE).getString(LAST_DOC_TYPE, "");
		String curDocName = DocType.getCurDoc().getName();
		
		if(!curDocName.equals(lastDocType)){
			Editor ed = getPreferences(Context.MODE_PRIVATE).edit();
			ed.putString(LAST_DOC_TYPE, curDocName);
			ed.commit();
			
			FoldersAdapter.resetCache();
		}
		
		if( DocType.getCurDoc() == OrderDoc.instance() && document.getRowid() != ExtrasConst.INVALID_ROWID ) {
			if( mtxitems == null ) {
				mtxitems = new ArrayList<MatrixOrderItem>();
				MatrixOrder matrixOrder = new MatrixOrder();
				String table = DataObjectInfo.getInstance().getTableName(matrixOrder.getClass());
				DbReader r = new DbReader();
				r.select(matrixOrder, table, null);
				r.close();
				
				mtxitems.addAll(matrixOrder.items);
				Collections.sort(mtxitems, new Comparator<MatrixOrderItem>() {

					@Override
					public int compare(MatrixOrderItem lhs, MatrixOrderItem rhs) {
						return lhs.order - rhs.order;
					}});
			}
			
			if (mtxitems.size() == 0){
				findViewById(R.id.llMatrixOrder).setVisibility(View.GONE);
				return super.createListAdapter();
			}
			
			if (curMatrix >= mtxitems.size())
				curMatrix = mtxitems.size() - 1;
			
			if(hideMatrix)
				hideMatrix = curMatrix < mtxitems.size() - 1;
			
			findViewById(R.id.llMatrixOrder).setVisibility(View.VISIBLE);
			String name = changeMatrix();
			
			return new MatrixAdapter(this, name);
		}
		
		findViewById(R.id.llMatrixOrder).setVisibility(View.GONE);
		
		return super.createListAdapter();
	}

	protected String changeMatrix() {
		String name = getCurMatrixName();
		TextView tv = (TextView)findViewById(R.id.tvMatrixName);
		tv.setText(Html.fromHtml("<u><i>" + name + "</u></i>"));
		tv.setTextColor(getResources().getColor(R.color.blue));
		tv.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { showDialog(R.id.matrix_remark_dlg_id); }
		});
		
		if(((OrderImpl)document).isEditable()){
			OrderEx o = (OrderEx) document.getData();
			
			if(o.mtxstage.size() > 0)
				o.mtxstage.get(o.mtxstage.size() - 1).finish = new Date();
			
			MtxStageItem i = new MtxStageItem();
			i.name = name;
			i.start = new Date();
			o.mtxstage.add(i);
			
			document.write();
			document.close();
			
			setDefRemLastMtx();
		}
		
		return name;
	}

	protected void setDefRemLastMtx() {
		if(curMatrix == mtxitems.size()-1 && mtxremark.length > 0){
			if(getCurMatrixRemark().trim().length() == 0)
				setCurmatrixRemark(mtxremark[0]);
		}
	}

	protected String getCurMatrixName() {
		return mtxitems.size() > curMatrix ?  mtxitems.get(curMatrix).name : matrixName;
	}
	
	private Dialog createMatrixRemarkDlg(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.matrixremarktitle);
		View view = View.inflate(this, R.layout.matrixremarkdlg, null);
		Spinner sp = (Spinner) view.findViewById(R.id.spRemark);
		ArrayAdapter<CharSequence> aa = new ArrayAdapter<CharSequence>(this, R.layout.simple_spinner_layout, mtxremark);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		sp.setAdapter(aa);
		
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, setMatrixRemark);
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.matrix_remark_dlg_id)
			return createMatrixRemarkDlg();
		else
			return super.onCreateDialog(id);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean ret = super.onPrepareOptionsMenu(menu);
		MenuItem mi = menu.findItem(R.id.itMatrix);
		if( mi != null)
			mi.setVisible(!hideMatrix);
		return ret;
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == R.id.matrix_remark_dlg_id)
			prepareMatrixRemarkDlg(dialog);
		else
			super.onPrepareDialog(id, dialog);
	}
	
	private void prepareMatrixRemarkDlg(Dialog dialog) {
		String r = getCurMatrixRemark();
		
		if(r.trim().length() > 0){
			Spinner sp = (Spinner)(dialog.findViewById(R.id.spRemark));
			
			for (int i = 0; i < sp.getCount(); i++){
				if (sp.getItemAtPosition(i).toString().trim().equals(r)){
					sp.setSelection(i, true);
					break;
				}
			}
		}
		
	}

	private String getCurMatrixRemark() {
		MtxStageItem m = getMtxStageItem();
		return m != null ? m.remark : "";
	}
	
	private MtxStageItem getMtxStageItem(){
		MtxStageItem result = null;
		String name = getCurMatrixName();
		
		for(MtxStageItem i : ((OrderEx)document.getData()).mtxstage){
			if(i.name.equals(name)){
				result = i;
				break;
			}
		}
		
		return result;
	}

	protected Dialog createMatrixSelectDlg() {
		ArrayList<String> items = new ArrayList<String>();

		for(MatrixOrderItem i : mtxitems)
			items.add(i.name);

		items = prepareMatrixList(items);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_matrix);

		final String[] items_array = items.toArray(new String[items.size()]);
		
		builder.setSingleChoiceItems(items_array, curMatrix,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						applayMatrix(items_array[item]);
						curMatrix = item;
						changeMatrix();
						removeDialog(DLG_MATRIX);
					}
				});

		return builder.create();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(DocType.getCurDoc() == OrderDoc.instance() && ((OrderImpl)document).isEditable()){
			OrderEx o = (OrderEx) document.getData();
						
			if(o.mtxstage.size() > 0)
				o.mtxstage.get(o.mtxstage.size() - 1).finish = new Date();
			
			document.write();
			document.close();
		}
	}
}





