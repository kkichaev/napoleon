package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Action;
import com.grsoft.dataobjects.AnswerEx;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.PromoAuditImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.PromoAuditDoc;

public class PromoAuditEdit extends AuditActivityBase {

	public static void open(Context context, PromoAuditImpl doc) {
		AuditActivityBase.open(context, doc, PromoAuditEdit.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Adapter adapter = (Adapter) parent.getAdapter();
				String itemid = (String) parent.getItemAtPosition(position);
				PromoAuditActionList.open(view.getContext(),
						document.getRowid(), itemid, adapter.getActions());
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		document.read();
		list.setAdapter(new Adapter(this));
	}

	@Override
	protected CreatableDocument<? extends CreateDocDataObject> createDocument() {
		return (PromoAuditImpl) PromoAuditDoc.instance().create();
	}

	@Override
	protected int getLayoutID() {
		return R.layout.promo_audit;
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		if(isFinishing()){
			PromoAuditImpl audit = (PromoAuditImpl)document;
			if(audit.getData().answer.size() == 0)
				audit.delete();
		}
	}
	
	class Adapter extends BaseAdapter {
		private Context context;
		private PriceImpl price = new PriceImpl();
		private List<String> priceIds = new ArrayList<String>();
		private List<String> actionIds = new ArrayList<String>();
		private Map<String, List<String>> questions = new HashMap<String, List<String>>();
		private Map<String, List<String>> answers = new HashMap<String, List<String>>();
		
		public Adapter(Context context) {
			this.context = context;

			Set<String> orgMtx = new HashSet<String>();
			OrgImpl orgImpl = new OrgImpl();
			orgImpl.getData().id = document.getId();
			orgImpl.read();
			orgImpl.close();

			OrgEx org = (OrgEx) orgImpl.getData();
			for (MatrixItem mi : org.price)
				if (!orgMtx.contains(mi.id))
					orgMtx.add(mi.id);

			Cursor c = null;
			long now = new Date().getTime();
			try {
				c = DataBaseManager
						.getDataBase()
						.query(DataObjectInfo.getInstance().getTableName(
								Action.class),
								null,
								"org=? and begin<=? and end>=?",
								new String[] { document.getId(),
										Long.toString(now), Long.toString(now) },
								null, null, null);
				while (c.moveToNext()) {
					String item = c.getString(c.getColumnIndex("item"));
					String id = c.getString(c.getColumnIndex("id"));
					String action = c.getString(c.getColumnIndex("action"));

					if (!priceIds.contains(item) && orgMtx.contains(item)) {
						priceIds.add(item);

						if (!actionIds.contains(id))
							actionIds.add(id);

						List<String> list = null;
						if (questions.containsKey(item))
							list = questions.get(item);
						else {
							list = new ArrayList<String>();
							questions.put(item, list);
						}

						if (!list.contains(action))
							list.add(action);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (c != null)
					c.close();
			}
			
			DbReader reader = new DbReader();
			AnswerEx answ = new AnswerEx();
			boolean bdo =  reader.select(answ, DataObjectInfo.getInstance().getTableName(answ.getClass()), 
					"created="+ document.getData().created.getTime());
			
			while(bdo){
				List<String> list = null;
				
				if(!answers.containsKey(answ.price)){
					list = new ArrayList<String>();
					answers.put(answ.price, list);
				}
				else
					list = answers.get(answ.price);
				
				if(!list.contains(answ.question))
					list.add(answ.question);
				
				bdo = reader.selectNext(answ);
			}
			
		}

		@Override
		public int getCount() {
			return priceIds.size();
		}

		@Override
		public Object getItem(int position) {
			return priceIds.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null)
				view = View.inflate(context, R.layout.promo_audit_row, null);

			String id = (String) getItem(position);
			price.getData().id = id;
			price.read();
			price.close();

			Price p = price.getData();
			TextView tvName = (TextView) view.findViewById(R.id.tvName);
			tvName.setText(p.name);

			if (questions.containsKey(p.id)) {
				List<String> q = questions.get(p.id);
				List<String> qid = new ArrayList<String>(q);
				
				boolean done = false;
				
				if(answers.containsKey(p.id)){
					List<String> ans = answers.get(p.id);
					
					for(String a : ans)
						qid.remove(a);
					
					done = qid.size() == 0;
				}

				if (done)
					view.setBackgroundResource(R.drawable.list_green_selector);
				else
					setBackground(view, position);

			} else
				setBackground(view, position);

			return view;
		}

		public List<String> getActions() {
			return actionIds;
		}

		private void setBackground(View v, int pos) {
			v.setBackgroundResource(pos % 2 != 0 ? R.drawable.even_row_selector
					: R.drawable.list_selector);
		}
	}
}
