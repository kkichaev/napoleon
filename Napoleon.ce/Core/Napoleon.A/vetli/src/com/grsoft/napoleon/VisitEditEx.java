package com.grsoft.napoleon;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.TypeDistrib;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.napoleon.util.ImagesAdapter;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageView;

public class VisitEditEx extends VisitEdit {
	private final String DID = "did";
	
	@Override
	public void onCreateContextMenu(final ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		if(visit.isEditable()){
			DataTraveler.travel(TypeDistrib.class, new  DataTraveler.Travel<TypeDistrib>(){
				@Override
				public boolean travel(DataTraveler<TypeDistrib> item) {
					Intent i = new Intent();
					i.putExtra(DID, item.data.id);
					MenuItem mi =  menu.add(item.data.text);
					mi.setIntent(i); 
					return true;
				}}, null);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean result = super.onContextItemSelected(item);
		
		if(!result && visit.isEditable()){
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
			VisitItemEx visitItem = (VisitItemEx)adapter.getItem(menuInfo.position);
			
			Intent i = item.getIntent();
			
			if(i != null && visitItem != null)
			{
				visitItem.did = i.getStringExtra(DID);
				visitItem.dval = item.getTitle().toString();
				adapter.notifyDataSetChanged();
			}
		}
			
		return  result;
	}
	
	@Override
	protected ImagesAdapter createImageAdapter() {
		return new ImagesAdapter(this, visit){
			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				View result =  super.getView(arg0, arg1, arg2);
				
				if(result instanceof ImageView){
					VisitItemEx i = ((VisitItemEx) getItem(arg0));
					
					if(i.did.trim().length() > 0)
						((ImageView)result).setBackgroundColor(getResources().getColor(R.color.red));
					else
						((ImageView)result).setBackgroundColor(getResources().getColor(R.color.blue));
				}
				
				return result;
			}
		};
	}
}
