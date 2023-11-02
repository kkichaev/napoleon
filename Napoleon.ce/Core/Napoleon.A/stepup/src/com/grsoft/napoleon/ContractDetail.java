package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.grsoft.dataobjects.ContractItem;
import com.grsoft.dataobjects.impl.ContractImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.ContractDoc;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;


public class ContractDetail extends BaseActivity implements DataSetNotify {
	public static Class<?> activity = ContractDetail.class;
	
	private ListView list;
	private ImageButton btnAddItems;
	private ImageButton btnLines;
	private ImageButton btnSend;
	private TextView tvOrg;
	protected LinesCountController linesController;
	
	
	private ContractImpl document = new ContractImpl();
	
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(getContentViewId());
		init();
		initDoc();
		intiView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initDoc();
	}
	
	private void initDoc() {
		document.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID), false);
		document.close();
		updateTotalSum();
	}
	
	private void updateTotalSum(){
		updateTotalSum(document.qty(), document.face());
	}

	private void intiView() {
		list.setAdapter(createAdapter());
		list.setOnItemClickListener(onItemClick());
		btnAddItems.setOnClickListener(addItemsClick());
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(list, btnLines, this, true);
		linesController = linesOnClickListener.getController();
		btnSend.setOnClickListener(sendClick());
		OrgImpl org = new OrgImpl();
		org.read("id", document.getId());
		tvOrg.setText(org.getData().name);
	}

	private OnItemClickListener onItemClick() {
		return new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ContractItem item = (ContractItem) parent.getItemAtPosition(position);
				
				if(item != null && document.isEditable() && view != null && view.getTag() != null)
					document.editItem((Long) view.getTag(), view.getContext());
			}};
	}

	private OnClickListener sendClick() { return new OnClickListener() { @Override public void onClick(View v) { send(); } }; }

	protected void send() {
		new DocumentSender(this, btnSend, ContractDoc.instance().getObjectName(), document, document.getRowid()).execute((Void[])null);
	}

	private OnClickListener addItemsClick() { return new OnClickListener() { @Override public void onClick(View v) { addItems(); } }; }

	protected void addItems() {	Warehouse.open(this, document, true); }

	private ListAdapter createAdapter() { return new Adapter();	}

	private void init() {
		list = (ListView) findViewById(R.id.list);
		btnAddItems = (ImageButton) findViewById(R.id.btnAddItems);
		btnLines = (ImageButton) findViewById(R.id.btnLines);
		btnSend = (ImageButton) findViewById(R.id.btnSend);
		tvOrg = (TextView) findViewById(R.id.tvOrg);
	}

	private int getContentViewId() { return R.layout.contractdetail; }
	
	class Adapter extends BaseAdapter{
		private PriceImpl price = new PriceImpl();
		
		@Override
		public int getCount() {	return document.getData().items.size();	}

		@Override
		public Object getItem(int position) { return document.getData().items.get(position); }

		@Override
		public long getItemId(int position) { return 0;	}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if(view == null)
				view = View.inflate(ContractDetail.this, R.layout.contractrow, null);
		
			ContractItem item = (ContractItem) getItem(position);
			if(item != null){
				price.read("id", item.id);
				view.setTag(price.getRowid());
				
				TextView tv = (TextView) view.findViewById(R.id.tvName);
				tv.setText(price.getData().name);
				linesController.prepareTextView(tv);
				
				tv = (TextView) view.findViewById(R.id.tvQty);
				tv.setText(Util.IntToScaleStr(item.qty, Consts.QTY_SCALE));
				
				tv = (TextView) view.findViewById(R.id.tvFace);
				tv.setText(Util.IntToScaleStr(item.face, Consts.QTY_SCALE));
			}
			
			return view;
		}
		
	}

	@Override
	public void notifyDataSetChanged() {
		initDoc();
		((BaseAdapter)list.getAdapter()).notifyDataSetChanged(); 
	}
	
	@Override
	public void onBackPressed() {
		if (document.getData().items.size() == 0)
			document.delete();
		
		super.onBackPressed();
	}
}


