package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.util.OrgInfoClickListener;

public class DocumentsEx extends Documents {

	protected static final String DebtDocEx = null;
	LinearLayout llDebtMode;
	Spinner spDlvMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		llDebtMode = (LinearLayout) findViewById(R.id.llDebtMode);
		spDlvMode = (Spinner) findViewById(R.id.spDlvMode);
		
		spDlvMode.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				DocType curType = DocType.getCurDoc();
				
				if(curType instanceof DebtDocEx){
					((DebtDocEx)curType).setDlvMode(position);
					adjustViewForDocType(curType);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}});
	}

	@Override
	protected Dialog createWarningStopListDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert);
		builder.setMessage(((OrgEx) org.getData()).stopMsg);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				doCreate();
			}
		});

		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	@Override
	protected int getContentViewID() {
		return R.layout.documentsex;
	}

	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		return new Adapter(this, docType, id, "date", R.layout.docs_list_row_ex);
	}

	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);

		llDebtMode.setVisibility(DocType.getCurDoc() == DebtDoc.instance() ? View.VISIBLE : View.GONE);
	}

	@Override
	protected OnClickListener createInfoClickListener() {
		return new OrgInfoClickListener(org.getData(), getContactViewid(), this) {

			@Override protected int getContentView() { return R.layout.org_detail_infoex; }

			@Override
			protected void adjustDialogView(View view) {
				super.adjustDialogView(view);
				TextView tvInfo = (TextView) view.findViewById(R.id.tvInfo);
				tvInfo.setText(((OrgEx) org.getData()).info);
			}
		};
	}

	class Adapter extends DocumentsAdapter {

		public Adapter(Context context, DocType docType, String orgId, String order, int id) {
			super(context, docType, orgId, order, id);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = super.getView(position, convertView, parent);
			int vsbl = (curDocType == DebtDoc.instance()) ? View.VISIBLE : View.GONE;
			View tv = v.findViewById(R.id.tvOverPay);
			tv.setVisibility(vsbl);
			
			tv = v.findViewById(R.id.tvInfo);
			tv.setVisibility(vsbl);
			
			return v;
		}
	}
}
