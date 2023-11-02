package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.FoldersAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

public class WarehouseEx extends Warehouse {
	
	boolean showFullPrice = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnShowFolders).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { switchFolderView(); }
		});

		findViewById(R.id.btnFullPrice).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { swtichFullPriceView(); }
		});
		
		updateButtonView();
	}
	
	private void updateButtonView() {
		ImageButton ib;
		ib = (ImageButton)findViewById(R.id.btnShowFolders);
		ib.setImageResource(adapter.isExpanded() ? R.drawable.show_items : R.drawable.show_folders );
		
		ib = (ImageButton)findViewById(R.id.btnFullPrice);
		ib.setImageResource(showFullPrice ? R.drawable.full_price : R.drawable.ass_btn );
	}

	protected void swtichFullPriceView() {
		showFullPrice = !showFullPrice;
		resetMatrix();
		updateButtonView();
	}

	protected void switchFolderView() {
		adapter.expandSwitch();
		updateButtonView();
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = null;
		
		if( !showFullPrice )
			ret = createAssortementMatrixAdapter();
		else 
			ret = (FoldersAdapter) super.createListAdapter();
		
		return ret;
	}
	
	@Override
	protected AssortmentMatrixAdapter createAssortementMatrixAdapter() {
		AssortmentMatrixAdapter.MATRIX_DOC = (DocType.getCurDoc() == ReturnDoc.instance()) ? DeliveryDoc.instance() : OrderDoc.instance();
		return super.createAssortementMatrixAdapter();
	}

	@Override
	protected void updateTotalSum() {
		if( document != null )
			updateTotalSum(document.sum(), ((document instanceof OrderImpl) ? ((OrderImpl)document).weight() : 0));
	}
	
	@Override
	protected int getLayoutId() { return R.layout.warehouseex; }
}
