package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;

import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.napoleon.util.ImagesAdapter;

public class VisitEditEx extends VisitEdit{

	private static final int SELECT_PHOTO_CAPTION_DLG = 0;
	private int selectedPhotoPosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btnSend.setVisibility(View.GONE);
		registerForContextMenu(gPictures);
	}
	
	@Override
	protected void setContextMenu(boolean isEditable) {
		//Установка конекстного меню в onCreate()
	}
	
	@Override
	protected ImagesAdapter createImageAdapter() {
		return new ImagesAdapterEx(this, (VisitImpl) visit);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.visit_context_menu_ex, menu);
		
		if (visit.isExported()){
			menu.findItem(R.id.itDelete).setVisible(false);
			menu.findItem(R.id.itSetCaption).setVisible(false);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = 
				(AdapterContextMenuInfo) item.getMenuInfo();
		selectedPhotoPosition  = menuInfo.position;
		
		if (item.getItemId() == R.id.itSetCaption){
			showDialog(SELECT_PHOTO_CAPTION_DLG);
			return true;
		} else
			return super.onContextItemSelected(item);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == SELECT_PHOTO_CAPTION_DLG)
			return createPhotoCaptionDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createPhotoCaptionDlg() {
		ConfigImpl configImpl = new ConfigImpl();
		StringBuilder value = new StringBuilder();
		Dialog result = null;
		
		if (configImpl.getValue(value, "Фото") &&
				value.length() > 0){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final String[] cpts = value.toString().split(";"); 
			builder.setItems(cpts, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Adapter adapter = gPictures.getAdapter();
					if (adapter != null && adapter instanceof ImagesAdapter 
							&& visit != null){
						VisitItemEx vie = (VisitItemEx)adapter.getItem(selectedPhotoPosition);
						vie.caption = cpts[which];
						visit.write();
						((ImagesAdapter)adapter).notifyDataSetChanged();
					}
					
					dialog.dismiss();
				}
			});
			
			result = builder.create();
		}
		
		return result;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			
			if (!saveVisit())
				visit.delete();
			
			finish();
			
			return true;
		}else
			return super.onKeyDown(keyCode, event);
	}
}


class ImagesAdapterEx extends ImagesAdapter{
	public ImagesAdapterEx(Context context, VisitImpl visit) {
		super(context, visit);
	}
	
	@Override
	public View getView(int arg0, View view, ViewGroup arg2) {
		if(view == null)
			view = View.inflate(context, R.layout.visit_item, null);
		
		VisitItemEx vie = ((VisitItemEx) getItem(arg0));
		
		if (vie != null){
			byte[] picture = vie.id;
			 
			if (picture != null){
				Bitmap b = createImage(picture);
				BitmapDrawable bd = new BitmapDrawable(Bitmap.createScaledBitmap(
						b, 150, 130, false));
				((TextView)view).setCompoundDrawablesWithIntrinsicBounds(
						null, bd, null, null);
			}
			
			((TextView)view).setText(vie.caption);
		}
		
		return view;
	}
	
}
