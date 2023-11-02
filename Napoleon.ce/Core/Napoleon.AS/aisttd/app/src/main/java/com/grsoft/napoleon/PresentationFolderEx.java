package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindOnClickListener;
import com.grsoft.napoleon.util.FindTextWatcher;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.PriceTextFilter;
import com.grsoft.util.WarehouseAdapter;
import com.grsoft.util.ZeroPositionFilter;
import com.grsoft.util.WarehouseAdapter.OnChangeListener;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class PresentationFolderEx extends PresentationFolder {
	protected EditText edFind;
	protected ImageButton btnFind;
	FindTextWatcher textWatcher;
	protected static final int DLG_WAIT = 2;
	
	@Override
	protected int getItemLayoutId() {
		return R.layout.presentation_itemex;
	}
	
	protected FoldersAdapter createAdapter() { 
		return new PhotoFolder(this) {
			@Override
			public void onClick(int pos) {
				super.onClick(pos);
			}
		}; 
	}

	@Override
	public void editItem(long rowid) {
		((Itemsable) doc).editItem(rowid, this);
	}
	
	protected int getLayoutId() {
		return R.layout.presentationfolderex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnFind = (ImageButton) findViewById(R.id.btnFind);
		edFind = (EditText) findViewById(R.id.edFind);
		
		View v = findViewById(R.id.llFind);
		FindOnClickListener findOnClickListener = new FindOnClickListener(edFind, gvPresentation, v);
		btnFind.setOnClickListener(findOnClickListener);
		
		textWatcher = new FindTextWatcher(edFind, gvPresentation);
		edFind.addTextChangedListener(textWatcher);
		
		v = findViewById(R.id.btnDelFind);
		if (v != null) {
			v.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) { edFind.setText(""); }
			});
		}
		adapter.setOnChangeListener(adapterOnChangeListener);
	}
	
	protected PriceTextFilter createPriceTextFilter() {
		return new PriceTextFilter();
	}
	
	public void applySearchFilter(String value) {
		if( buildingProcess )
			return;
		if (value.trim().length() > 0) {
			PriceTextFilter filter = (PriceTextFilter) adapter
					.getFilter(PriceTextFilter.NAME);

			if (filter == null) {
				filter = createPriceTextFilter();
				adapter.putFilter(filter);
			}

			filter.build(adapter, value, false);
			adapter.buildSet();
		} else
			((FilterAdapter) adapter).resetFilter();
	}
	
	protected WarehouseAdapter.OnChangeListener adapterOnChangeListener = new OnChangeListener() {
		
		@Override
		public void startBuildSet(WarehouseAdapter adapter) {
			buildingProcess = true;
			btnFind.setEnabled(false);
			textWatcher.blockListner(true);
//			edFind.setEnabled(false);
			
			if(folderPath != null)
				folderPath.setEnabled(false);
			
			showDialog(DLG_WAIT);
		}

		@Override
		public void endBuildSet(WarehouseAdapter adapter) {
			try {
				btnFind.setEnabled(true);
				textWatcher.blockListner(false);
//				edFind.setEnabled(true);
				
				if(folderPath != null)
					folderPath.setEnabled(true);
				
				buildingProcess = false;
				//adapter.setFolder(adapter.getPrevTopFolder());
				//fireBuildSet();

				dismissDialog(DLG_WAIT);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onAdapterChange(final WarehouseAdapter adapter) {
//			ivGoUp.setVisibility(adapter.isTop() ? View.INVISIBLE
//					: View.VISIBLE);
//			tvItemSelectUpLevel.setText(adapter.getTitle());
//			ivFilter.setVisibility(adapter.getFilter(ZeroPositionFilter.NAME) != null ? View.VISIBLE
//					: View.GONE);

			if (folderPath != null)
				folderPath.refreshPath(adapter);
			
//			postAdapterChange();
		}

		@Override
		public void setSelection(int position) {
//			if(lvItemSelect.getAdapter().getCount() > 0 && position == -1)
//				position = 0;
//			lvItemSelect.setSelection(position);
		}
	};
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DLG_WAIT:
			return createWaitDlgDialog();
		default:
			return super.onCreateDialog(id);
		}
	}
	
	private Dialog createWaitDlgDialog() {
		ProgressDialog result = new ProgressDialog(this);
		result.setMessage(getString(R.string.price_loading));
		result.setCancelable(false);

		result.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if (adapter != null)
					adapter.close();
			}
		});

		return result;
	}
}
