package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.dataobjects.ActionWithText;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ActionDialog {
	public interface Actions {
		void selected(ActionWithText action);
		void closing();
	}
	
	ActionDialogAdapter adapter = null;
	
	public AlertDialog create(Context context, final Actions handler, OrderEx doc, boolean allActions) {
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		
		b.setTitle("Доступные акции");
		adapter = new ActionDialogAdapter(context, allActions);
		adapter.refresh(doc);
		b.setAdapter(adapter, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface arg0, int arg1) { handler.selected((ActionWithText) adapter.getItem(arg1)); }
		});
		
		b.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) { 
				handler.closing();
				dialog.dismiss();
			}
		});
		
		b.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override public void onCancel(DialogInterface dialog) { handler.closing(); }
		});
		
		return adapter.getCount() > 0 ? b.create() : null;
	}
	
	public ActionDialogAdapter getAdapter() { return adapter; }
	
	public class ActionDialogAdapter extends BaseAdapter {

		List<ActionWithText> actions;
		boolean allActions;
		Context context;
		
		public ActionDialogAdapter(Context context, boolean allActions) {
			this.allActions = allActions;
			this.context = context;
		}
		
		public void refresh(OrderEx doc) {
			actions = allActions ? ActionWithText.getAllActions(doc) : ActionWithText.getActiveActions(doc);
			notifyDataSetChanged();
		}
		
		@Override public int getCount() { return actions == null ? 0 : actions.size(); }
		@Override public Object getItem(int arg0) { return actions == null ? null : actions.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if(view == null)
				view = View.inflate(context, R.layout.action_row, null);
			
			ActionWithText item = (ActionWithText) getItem(pos);
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(item.getActionText());
			tv = (TextView)view.findViewById(R.id.tvSum);
			if(allActions)
				tv.setVisibility(View.GONE);
			else {
				tv.setText(Util.IntToScaleStr(item.getOrderDiscountSum(), Consts.SUM_SCALE, Util.DEC_DELIM, false));
			}
			
			view.setBackgroundResource((pos % 2) != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);
			
			return view;
		}
		
	}
}
