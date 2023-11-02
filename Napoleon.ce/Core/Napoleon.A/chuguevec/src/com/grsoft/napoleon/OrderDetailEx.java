package com.grsoft.napoleon;

import java.io.File;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.HorizontalListView;
import com.grsoft.napoleon.util.ImagesAdapter;
import com.grsoft.napoleon.util.PhotoClickHandler;
import com.grsoft.napoleon.util.PhotoClickHandler.EventHandler;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail implements EventHandler {
	
	VisitImpl visit;
	String picPath;
	ImagesAdapter adapter;
	boolean inited = false;
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		openAssociatedVisit();
		adapter = new ImagesAdapter(this, visit);
		
		HorizontalListView g = (HorizontalListView)findViewById(R.id.gvItems);
		g.setAdapter(adapter);
		
		findViewById(R.id.btnPhoto).setOnClickListener(new PhotoClickHandler(visit, this, VisitDoc.instance()));
	}

	private void openAssociatedVisit() {
		visit = new VisitImpl();
		Order o = doc.getData();
		long created = o.created.getTime() + 1000;
		Date dt = new Date(created);
		visit.getData().created = dt;
		
		if( visit.read() == false ) {
			Visit v = visit.getData();
			v.date = Util.getDateTime();
			v.created = dt;			
			v.id = doc.getId();
			v.latitude = o.latitude;
			v.longitude = o.longitude;
			v.params = 0;
			
			visit.write();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if( inited ) {
			visit.read(visit.getRowid(), false);
			adapter.notifyDataSetChanged();
		}
		inited = true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		visit.close();
	}

	@Override
	public void prepareBoforeClick() {
	}

	@Override
	public void makePhotoFile(File newFile) {
		picPath = newFile.getAbsolutePath();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == PhotoClickHandler.CAMERA_ACTIVITY && resultCode == RESULT_OK){
			if(picPath != null && picPath.trim().length() > 0) {
				visit.addPhoto(picPath.getBytes());
				adapter.notifyDataSetChanged();
			}
		}
	}
}
