package com.grsoft.napoleon.dostavka;

import java.io.File;
import com.grsoft.dataobjects.DispatchItem;
import com.grsoft.dataobjects.DispatchPhoto;
import com.grsoft.dataobjects.PointDoc;
import com.grsoft.dataobjects.impl.DispathImpl;
import com.grsoft.dataobjects.impl.RoutePointImpl;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.SrcDataCounter;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class DocumentDlg extends BaseDialogFragment {
	private String photoPath;
	private DispathImpl doc;
	private String number;
	private LinearLayout scrollView;
	private int imageWidth;
	private int imageHieght;
	private int imagePadding;
	private EditText edRemark;
	private View btnDone;
	private View btnReject;
	
	public DocumentDlg(DispathImpl doc, String number) {
		this.doc = doc;
		this.number = number;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.documentdlg, container, false);
		scrollView = (LinearLayout) view.findViewById(R.id.llPhoto);
		edRemark = (EditText) view.findViewById(R.id.edRemark);
		btnDone = view.findViewById(R.id.btnDone);
		btnReject = view.findViewById(R.id.btnReject);
		
		imageWidth = (int)getResources().getDimension(R.dimen.previewImgWidth);
		imageHieght = (int)getResources().getDimension(R.dimen.previewImgHeight);
		imagePadding = (int)getResources().getDimension(R.dimen.previewImgPadding);
		
		btnDone.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { done(); }});
		btnReject.setOnClickListener(new OnClickListener() { @Override	public void onClick(View v) { reject();	}});
		
		RoutePointImpl rpi = new RoutePointImpl();
		rpi.read("id", doc.getId());
		
		String r = "";
		PointDoc d = rpi.findDoc(number);
		if(d != null)
			r = d.remark;
		
		TextView tv = (TextView)view.findViewById(R.id.tvRemark);
		tv.setText(r);
		
		tv = (TextView)view.findViewById(R.id.tvNumber);
		tv.setText(number.trim());
		
		View btnPhoto = view.findViewById(R.id.btnPhoto);
		btnPhoto.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) { takePhoto(); } });
		
		for(DispatchPhoto p : doc.getData().photos)
			if(number.equals(p.number))
				addImage(new String(p.id));
		
		edRemark.setText("");
		
		DispatchItem i = doc.findItem(number);
		if(i != null){
			edRemark.setText(i.remark);
			if (i.state != DispatchItem.WAITING){
				btnDone.setEnabled(false);
				btnReject.setEnabled(false);
			}
		}
		
		return view;
	}

	protected void reject() {
		DialogFragment dlg = new RejectCauseDlg();
		dlg.setTargetFragment(this, R.id.cause_dlg_result);
		dlg.show(getFragmentManager(), dlg.getClass().getCanonicalName());
	}

	protected void takePhoto() {
		try{
			File path = new File(Path.getDataDir());
			path.mkdir();
			File file = new File(Path.getDataDir(), Integer.toString(SrcDataCounter.getValue()));
			photoPath = file.getAbsolutePath();
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			startActivityForResult(intent, R.id.photo_dlg_result);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void done() {
		DispatchItem i = doc.findItem(number);
		
		if(i != null){
			i.state = DispatchItem.DONE;
			doc.write();
		}
		
		getTargetFragment().onActivityResult(R.id.document_dlg_result, 0, null);
		dismiss();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == Activity.RESULT_OK && requestCode == R.id.photo_dlg_result){
			DispatchPhoto p = new DispatchPhoto();
			p.id = photoPath.getBytes();
			p.number = number;
			doc.getData().photos.add(p);
			doc.write();
			doc.close();
			addImage(photoPath);
		}else if (requestCode == R.id.cause_dlg_result){
			DispatchItem i = doc.findItem(number);
			
			if(i != null){
				i.state = DispatchItem.REJECT;
				i.remark = data.getStringExtra(RejectCauseDlg.REJECT_CAUSE);
				edRemark.setText(i.remark);
				doc.write();
				doc.close();
			}
			
			getTargetFragment().onActivityResult(R.id.document_dlg_result, 0, null);
			dismiss();
		}
	}
	
	private void addImage(String src){
		ImageView img = new ImageView(getActivity());
		img.setLayoutParams(new ViewGroup.LayoutParams(imageWidth, imageHieght));
		img.setPadding(imagePadding, 0, imagePadding, 0);
		img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		img.setBackgroundColor(Color.WHITE);
		img.setImageBitmap(createImage(src));
		img.setTag(src);
		
		registerForContextMenu(img);
		scrollView.addView(img);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getActivity().getMenuInflater().inflate(R.menu.photo_context_menu, menu);
		MenuItem i =  menu.findItem(R.id.itDelete);
		
		if(i != null){
			i.setOnMenuItemClickListener(deletePhotoClick);
			i.setActionView(v);
		}
		
		i = menu.findItem(R.id.itShow);
		
		if( i != null){
			i.setOnMenuItemClickListener(showPhotoClick);
			i.setActionView(v);
		}
	}
	
	OnMenuItemClickListener deletePhotoClick = new OnMenuItemClickListener(){

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			View v = item.getActionView();
			String path =  v.getTag().toString();
			
			if(doc.removePhoto(path)){
				new File(path).delete();
				((ViewManager)v.getParent()).removeView(v);
			}
			
			return true;
		}
	};
	
	OnMenuItemClickListener showPhotoClick = new OnMenuItemClickListener(){

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			String path =  item.getActionView().getTag().toString();
			
			Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(path)), "image/*");
            startActivity(intent);
            
			return true;
		}
	};

	
	protected Bitmap createImage(String src) {
		Bitmap result = null;
		String picSrc = src;
        try{
        	BitmapFactory.Options opt = new BitmapFactory.Options();
        	opt.inSampleSize = 4;
        	result = BitmapFactory.decodeFile(picSrc, opt);
        	result = Bitmap.createScaledBitmap(result, 100, 85, true);
        }
        catch (Exception e){
        	e.printStackTrace();
        }
        
        return result;
	}
	
	@Override
	public void onPause() {
		super.onPause();

		DispatchItem i = doc.findItem(number);
		
		if( i != null){
			i.remark = edRemark.getText().toString().trim();
			doc.write();
			doc.close();
		}
	}
}
