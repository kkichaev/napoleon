/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Обработчик смены текущего фильтра
 *
 * kki   03/02/2011   creating
 */
package com.grsoft.util;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.napoleon.Features;
import com.grsoft.aceteam.R;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.Selector;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.documents.ScriptDoc;

public class DocFilterOnClickListener extends OnClickListenerToNotify
{
	private Selector docTypeSelector;
	private boolean creatable, showScriptOnly;
	protected List<DocTypeBase> data = new ArrayList<DocTypeBase>();
	protected List<DocTypeBase> filter;
	
	public static List<DocTypeBase> HiddenTypes = new ArrayList<DocTypeBase>();
	
	public DocFilterOnClickListener(Selector selector) {
		this(selector, false, ScriptDefImpl.canScripting());
	}
	
	public DocFilterOnClickListener(Selector docTypeSelector, boolean createable, boolean showScriptOnly) {
		this(docTypeSelector, createable, showScriptOnly, null);
	}
	
	public DocFilterOnClickListener(Selector docTypeSelector, boolean createable, boolean showScriptOnly, List<DocTypeBase> filter)	{
		this.creatable = createable;
		this.showScriptOnly = showScriptOnly;
		this.docTypeSelector = docTypeSelector;
		this.filter = filter;
	}
	
	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		selectDocType((DialogOwner) v.getContext(), docTypeSelector, creatable);
	}

	protected void initData(boolean creatableFilter) {
		if (data.size() == 0){
			if (filter != null)
				data.addAll(filter);
			else if( Features.SCRIPT_DOC && showScriptOnly ) {
				DocType sd = ScriptDoc.instance();
				if(creatableFilter)
					data.add(sd);
				else {
					for( DocTypeBase dt : DocType.docTypes )
						if( dt.outOfScript() || dt == sd )
							data.add((DocType) dt);
					for( DocTypeBase dt : ScriptDefImpl.docInScript ) {
						if( data.contains(dt) == false )
							data.add(dt);
					}
				}
			} else {
				if (creatableFilter){
					for(DocTypeBase dt : DocType.docTypes){
						if (dt.isCreatable())
							data.add((DocType) dt);
					}
				} else 
					data.addAll(DocType.docTypes);
				
				if( Features.SCRIPT_DOC )
					data.remove(ScriptDoc.instance());
			}
		
			data.removeAll(HiddenTypes);
		}
	}
	
	public void selectDocType(final DialogOwner ctx, final Selector selector, boolean creatable)
	{
		class SelectDocAdapter extends BaseAdapter{
			public SelectDocAdapter(boolean creatableFilter) {
				initData(creatableFilter);
			}
			
			@Override
			public int getCount() { return data.size(); }

			@Override
			public Object getItem(int arg0) { return data.get(arg0); }

			@Override
			public long getItemId(int arg0) { return 0;	}

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				if (arg1 == null)
					arg1 = View.inflate((Context) ctx, R.layout.doc_type_list_row, null);
				
				DocType docType = (DocType) getItem(arg0);
				TextView tvText = (TextView) arg1.findViewById(R.id.tvText);
				tvText.setCompoundDrawablesWithIntrinsicBounds(docType.getResurceId(), 0, 0, 0);
				int titleDocId = docType.getDocTitle();
				
				if(titleDocId == -1)
					tvText.setText(docType.getName());
				else
					tvText.setText(titleDocId);
				
				return arg1;
			}
		}

		AlertDialog.Builder builder = new AlertDialog.Builder((Context) ctx);
		builder.setTitle(R.string.doc_type);
		View view = View.inflate((Context) ctx, R.layout.doc_type_list, null);
		ListView lvDocTypes = (ListView) view.findViewById(R.id.lvDocTypes);
		lvDocTypes.setAdapter(new SelectDocAdapter(creatable));
		builder.setView(view);
	 	final AlertDialog dlg =  builder.create();
	 	ctx.setActiveDialog(dlg);
		lvDocTypes.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				DocType dt = (DocType) ((SelectDocAdapter)arg0.getAdapter()).getItem(arg2);
				selector.selectedType(dt);
				try{
					dlg.dismiss();
				}catch (Exception e){}
			}
		});
		
		dlg.show();
	}
}
