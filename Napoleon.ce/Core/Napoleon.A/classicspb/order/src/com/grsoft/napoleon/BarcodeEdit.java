package com.grsoft.napoleon;

import java.io.File;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.grsoft.dataobjects.Barcode;
import com.grsoft.dataobjects.BarcodeItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.BarcodeImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.napoleon.util.VisitPhotoHandler;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class BarcodeEdit extends Activity implements OnClickListener {
	public static final String ITEM_ID = "item_id";
	ImageView ivBarcode;
	public final static int WHITE = 0xFFFFFFFF;
	public final static int BLACK = 0xFF000000;
	VisitImplEx refVisit = new VisitImplEx();
	VisitPhotoHandler photoHandler;
	BarcodeImpl doc = new BarcodeImpl();
	String itemid;
	ImageView ivPreview;


	public static void open(Context context, long rowid, String itemid) {
		Intent intent = new Intent(context, BarcodeEdit.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		intent.putExtra(ITEM_ID, itemid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.barcodeedit);
		
		ivPreview = (ImageView)findViewById(R.id.ivPreview);
		ivBarcode = (ImageView) findViewById(R.id.ivBarcode);
		
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		itemid = getIntent().getStringExtra(ITEM_ID);
		
		OrgImpl org = new OrgImpl();
		org.read("id", doc.getId());
		PriceImpl price = new PriceImpl();
		price.read("id", itemid);
		String bc = ((PriceEx)price.getData()).barcode;
		
		if (bc.length() > 0)
			ivBarcode.setImageBitmap(encodeAsBitmap(bc));
		
		Barcode d = doc.getData();
		Visit v = refVisit.getData();
		v.created = doc.getData().visitDoc;
		
		if(refVisit.read() == false) {
			refVisit.init(this, d.id, new GpsCoord(d.latitude, d.longitude, d.stltime));
			doc.getData().visitDoc = refVisit.getData().created;
			doc.write();
		}
		
		VisitImplEx.setPhotoTag(itemid);
		photoHandler = new VisitPhotoHandler(refVisit);
		if (doc.isEditable())
			findViewById(R.id.btnPhoto).setOnClickListener(photoHandler);
		
		thumb();
		
		ivPreview.setOnClickListener(this);
	}
	
	Bitmap encodeAsBitmap(String str) {
	    BitMatrix result;
	    try {
	        result = new MultiFormatWriter().encode(str, BarcodeFormat.CODE_128, getResources().getDisplayMetrics().widthPixels, 
	        		(int)getResources().getDimension(R.dimen.bc_height), null);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	    int width = result.getWidth();
	    int h = result.getHeight();
	    int[] pixels = new int[width * h];
	    for (int y = 0; y < h; y++) {
	        int offset = y * width;
	        for (int x = 0; x < width; x++) {
	            pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
	        }
	    }
	    Bitmap bitmap = Bitmap.createBitmap(width, h, Bitmap.Config.ARGB_8888);
	    bitmap.setPixels(pixels, 0, width, 0, 0, width, h);
	    return bitmap;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (photoHandler.onActivityResult(requestCode, resultCode, data)) {
			
			if (doc.findItem(itemid) == null) {
				BarcodeItem i = new BarcodeItem();
				i.id = itemid;
				
				doc.getData().items.add(i);
				doc.write();
				doc.close();
			}
			
			thumb();
		}
	}

	protected void thumb() {
		VisitItemEx i = refVisit.findPhoto(itemid);
		
		if(i != null) {
			Bitmap bm = BitmapFactory.decodeFile(new String(i.id));
			ivPreview.setImageBitmap(bm);
		}
	}
	
	private void preview(String path) {
		Intent i = new Intent();
		i.setAction(Intent.ACTION_VIEW);
		
		Uri uri = null;
		
		if (Build.VERSION.SDK_INT >= 24) {
			uri = FileProvider.getUriForFile(this,"com.grsoft.napoleon.fileprovider", new File(path)); 
		}else
			uri = Uri.parse("file://" + path);
		
		i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		i.setDataAndType(uri, "image/*");
		
		startActivity(i);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ivPreview){
			previewImage();
		}
		
	}

	private void previewImage() {
		VisitItemEx i = refVisit.findPhoto(itemid);
		
		if (i != null)
			preview(new String(i.id));
	}
}
