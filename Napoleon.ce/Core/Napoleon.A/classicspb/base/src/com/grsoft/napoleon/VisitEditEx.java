package com.grsoft.napoleon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.grsoft.dataobjects.VisitEx;
import com.grsoft.napoleon.documents.PhotoDocument;
import com.grsoft.napoleon.util.PhotoClickHandler;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.SrcDataCounter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;

public class VisitEditEx extends VisitEditNew {
	View btnGallery;
	final int RESULT_LOAD_IMG = 100;
	
	@Override
	protected int getContentView() {
		return R.layout.visiteditnewex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btnGallery = findViewById(R.id.btnGallery);
		btnGallery.setVisibility(View.GONE);
		
		if (((VisitEx)visit.getData()).allowGallery == 1) {
			btnGallery.setVisibility(View.VISIBLE);
			btnGallery.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_PICK);
					intent.setType("image/*");
					startActivityForResult(intent, RESULT_LOAD_IMG);
				}
			});
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK) {
			final Uri imageUri = data.getData();
            try {
				final InputStream input = getContentResolver().openInputStream(imageUri);
				
				File path = new File(Path.getDataDir());
				path.mkdir();
				File file = new File(Path.getDataDir(), Integer.toString(SrcDataCounter.getValue()));
				
				OutputStream output = new FileOutputStream(file);
				int read = 0;

				byte[] bytes = new byte[1024];
				while ((read = input.read(bytes)) != -1) {
					output.write(bytes, 0, read);
				}
				output.close();

				((PhotoDocument) visit).addPhoto(file.getAbsolutePath().getBytes());
				((BaseAdapter) grid.getAdapter()).notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
