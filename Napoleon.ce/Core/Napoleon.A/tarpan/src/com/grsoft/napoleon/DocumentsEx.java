package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.OrgInfoClickListener;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadService;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.UserInfo;
import com.grsoft.network.util.ProgressManager;
import com.grsoft.util.gps.GPSUtilNew;

public class DocumentsEx extends Documents {
	private static final int COPY_TO_ORG_DLG = R.id.copy_to_org_dlg;
	private Org[] orgs;
	private String[] orgNames;
	private CreatableDocument<?> seldoc;
	

	@Override
	protected int getContentViewID() {
		return R.layout.documentsex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.ivFilter).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(DocType.getCurDoc() == DebtDocEx.instance())
					((FilterAdapter)lvDocs.getAdapter()).filter();
			}
		});
		
//		findViewById(R.id.btnSync).setOnClickListener(new View.OnClickListener() {
//			@Override public void onClick(View v) { UpdateDB.open(DocumentsEx.this); }
//		});

		findViewById(R.id.btnShowDocList).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { DocList.open(DocumentsEx.this, org.getData().id); }
		});
		
		findViewById(R.id.btnSyncStatus).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new UpdateDocStatus(v.getContext(), new UpdateDocStatus.PostExecute() {
					
					@Override
					public void finish(boolean result) {
						adapter.notifyDataSetChanged();
						Toast.makeText(DocumentsEx.this, R.string.status_updated, Toast.LENGTH_SHORT).show();
					}
				}).execute((Void[]) null);
				
			}
		});
		
		Org data = new Org();
		DbReader reader = new DbReader();
		boolean bdo = reader.select(data, DataObjectInfo.getInstance().getTableName(Org.class), null);
		ArrayList<Org> list = new ArrayList<Org>();
		
		while(bdo){
			if(!data.id.equals(org.getData().id))
				list.add((Org) data.clone());
			bdo = reader.selectNext(data);
		}
				
		Collections.sort(list, new Comparator<Org>(){

			@SuppressLint("DefaultLocale")
			@Override
			public int compare(Org lhs, Org rhs) {
				return lhs.name.toUpperCase(Locale.getDefault()).compareTo(rhs.name.toUpperCase());
			}});
		orgs = new Org[list.size()];
		orgNames = new String[orgs.length];
		
		list.toArray(orgs);
		for(int i = 0; i < orgs.length; i++)
			orgNames[i] = orgs[i].name;
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (DocType.getCurDoc() == DebtDoc.instance()){
			getMenuInflater().inflate(R.menu.delivery_menu, menu);
		}else
			super.onCreateContextMenu(menu, v, menuInfo);
	};
	
	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		return new FilterAdapter(this, docType, id, "date");
	}
	
	@Override
	protected Dialog createWarningStopListDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert);
		builder.setMessage(((OrgEx)org.getData()).stopMsg);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {				
			@Override
			public void onClick(DialogInterface dialog, int which) { doCreate(); }
		});
		
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}
	
	@Override
	protected OnClickListener createInfoClickListener() {
		return new OrgInfoClickListener(org.getData(), getContactViewid(), this) {
			@Override	
			protected int getContentView() {
				return R.layout.org_detail_infoex;
			}
			
			@Override
			protected void adjustDialogView(View view) {
				super.adjustDialogView(view);
				TextView tvInfo = (TextView) view.findViewById(R.id.tvInfo);
				tvInfo.setText(((OrgEx)org.getData()).info);
			}
		};
	}
	
	@Override
	protected int getContextMenuId() {
		return R.menu.doc_context_menuex;
	}
	
	@Override
	protected void onContextAction(MenuItem item, Document<?> doc) {
		if(item.getItemId() == R.id.itCreateOrder){
			DeliveryImpl dlv = (DeliveryImpl)doc;
			
			OrderImpl order = new OrderImpl();
			CostStrategy costStrategy = CostStrategy.getInstance(OrderImpl.class);
			
			if (order.initSilent(dlv.getId(), GPSUtilNew.getLastKnownLocation())){
				PriceImpl priceImpl = new PriceImpl();
				Price price = priceImpl.getData();
				
				for(DeliveryItem di: dlv.getData().items){
					priceImpl.getData().id = di.id;
					int qty = di.qty;
					if(priceImpl.read())
						order.updateQty(priceImpl, qty, 
								costStrategy.getItemCost(price, order), false);
				}
				
				priceImpl.close();
			}
		}else if(item.getItemId() == R.id.itCopyToOrg){
			showDialog(COPY_TO_ORG_DLG);
			seldoc = ((CreatableDocument<?>) doc).copy();
		}else
			super.onContextAction(item, doc);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case COPY_TO_ORG_DLG: return createCopyOrgDlg();
		default: return super.onCreateDialog(id);
		}
	}

	private Dialog createCopyOrgDlg() {
		AlertDialog.Builder result = new AlertDialog.Builder(this);
		result.setTitle(R.string.copy_to_org);
		result.setItems(orgNames, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Org o = orgs[which];
				seldoc.getData().id = o.id;
				seldoc.write();
				Documents.open(DocumentsEx.this, o);
				finish();
			}
		});
		return result.create();
	}
}

class FilterAdapter extends DocumentsAdapter{
	boolean filtered = false;
	
	public FilterAdapter(Context context, DocType docType, String orgId,
			String order) {
		super(context, docType, orgId, order, R.layout.docs_list_rowex);
	}

	public void filter() {
		filtered = !filtered;
		documents.close();
		
		if (!filtered)
			documents = curDocType.docList(orgId, order, datePeriod);
		else
			documents = ((DebtDocEx)curDocType).dlvList(orgId, order, "sumd > 0");
			
			
		notifyDataSetChanged();		
	}
	
}

class UpdateDocStatus extends NetworkAsyncTask{
	interface PostExecute { void finish(boolean result); }
	UserInfo userInfo;
	static Lock lock = new ReentrantLock();
	PostExecute postExec;
	Context context;

	public interface TaskDoneHandler {
		void taskDone(NetworkAsyncTask task);
	}
	
	public UpdateDocStatus(Context context, PostExecute postExec) {
		super(new ProgressManager(context));
		((ProgressManager)this.progressHelper).setUpdateProcess(this);
		
		this.postExec = postExec;
		this.context = context;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		boolean ret = false;
		if( !lock.tryLock() )
			return ret;
		try {
			String errMessage = null;
			
			onUpdate(UpdateStatus.BEGIN_UPDATE, 0);
			
			Config config = ConfigManager.getConfig();
			userInfo = new LoginData(config.login, config.passw, config.impersonate, context);
			ArrayList<Hitching> h = new ArrayList<Hitching>();
			ReadService dataBaseUpdater = (ReadService) RWServiceFactory.instance.createReadService(h);
			dataBaseUpdater.setUpdateProcessListenet(this);
			if( !dataBaseUpdater.update(context, userInfo, false) )
				errMessage = dataBaseUpdater.getMessage();
			
			if (!isCancelled()) {
				onUpdate(UpdateStatus.END_OF_PROCESS, 0);
				
				if( errMessage != null ) {
					showErrorMsg(errMessage, progressHelper.getContext());
				} else {
					ret = true;
				}
			}
			
			return ret;
		} catch(Exception e) {
			SQLiteDatabase dataBase = DataBaseManager.getDataBase();			
			if (dataBase.isDbLockedByCurrentThread() || dataBase.isDbLockedByOtherThreads())
				dataBase.endTransaction();
			
			if (!isCancelled()) {
				String message = e.getMessage();
				if( message == null )
					message = context.getString(R.string.recieved_error);
				showErrorMsg(message, progressHelper.getContext());
			}
			
			e.printStackTrace();
			
			return false;
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if( postExec != null )
			postExec.finish(result);

		super.onPostExecute(result);
	}

	@Override
	public void onUpdate(UpdateStatus status, int progress) {
	}
}