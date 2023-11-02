package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderTemplate;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderTemplateImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {
	
	OrderTemplateImpl orderTempl = new OrderTemplateImpl();
	
	@Override protected int getContextMenuId() { return R.menu.documents_menu_ex; }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if(  DocType.getCurDoc() != OrderDoc.instance() )
			menu.removeItem(R.id.itMakeTemplate);
	}
	
	@Override
	protected void onDestroy() {
		orderTempl.close();
		super.onDestroy();
	}
	
	@Override
	protected void onContextAction(MenuItem item, Document<?> doc) {
		if( item.getItemId() == R.id.itMakeTemplate) {
			DbReader r= new DbReader();
			OrderTemplateImpl templ = new OrderTemplateImpl();
			r.read(templ.getData(), DataObjectInfo.getInstance().getTableName(Order.class), doc.getRowid());
			r.close();

			templ.getData().params = 0;
			templ.write();
			templ.close();
			return;
		}
		super.onContextAction(item, doc);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == R.id.ask_make_from_template) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle(R.string.choose_create);
			b.setSingleChoiceItems(R.array.choose_order_create, -1, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if( which == 1 ) {
						OrderImpl oi = orderTempl.makeOrder();
						if( oi != null ) {
							oi.open(DocumentsEx.this);
							return;
						}
					}
					DocumentsEx.super.createNewDoc();
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected String orgInfo(Org o) {
		String res = super.orgInfo(o);
		res += "<br>Долг контрагента <b>" + Util.IntToScaleStr(((OrgEx)o).balance, Consts.SUM_SCALE) + "</b>";
		return res;
	}
	
	@Override
	protected void createNewDoc() {
		if(DocType.getCurDoc() == OrderDoc.instance()) {
			OrderTemplate doc = orderTempl.getData();
			doc.id = org.getData().id;
			if( orderTempl.read() ) {
				showDialog(R.id.ask_make_from_template);
				return;
			}
		}
		super.createNewDoc();
	}
}