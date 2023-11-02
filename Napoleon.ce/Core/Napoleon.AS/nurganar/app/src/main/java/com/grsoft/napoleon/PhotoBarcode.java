package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.grsoft.dataobjects.InvEqu;
import com.grsoft.dataobjects.InvEquItem;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.InvEquImpl;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.napoleon.util.VisitPhotoHandler;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;


public class PhotoBarcode extends Activity {
    private static final String ID = "id";
    private static final String BARCODE = "barcode";
    InvEquImpl doc = new InvEquImpl();
    static VisitPhotoHandler photoHandler;
    VisitImplEx refVisit;
    String itemid;
    String barcode;
    ImageView ivPhoto;

    public static void open(Activity context, long rowid, String id, String barcode){
        Intent i = new Intent(context, PhotoBarcode.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
        i.putExtra(ID, id);
        i.putExtra(BARCODE, barcode);

        context.startActivityForResult(i, R.id.barcode_request);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_barcode);

        ivPhoto = findViewById(R.id.ivPhoto);
        TextView tvBarcode = findViewById(R.id.tvBarcode);

        barcode = getIntent().getStringExtra(BARCODE);
        itemid = getIntent().getStringExtra(ID);

        doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
        doc.close();

        tvBarcode.setText(barcode);

        findViewById(R.id.btnOK).setOnClickListener((x)->finish());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (photoHandler != null)
            photoHandler.storeData(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (photoHandler != null)
            photoHandler.restoreData(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(refVisit == null) {
            InvEqu d = doc.getData();
            refVisit = new VisitImplEx();
            refVisit.getData().created = d.visitDoc;
            if(!refVisit.read()) {
                refVisit.init(this, d.id, new GpsCoord(d.latitude, d.longitude, d.stltime));
                d.visitDoc = refVisit.getData().created;
                doc.write();
            }
            photoHandler = new VisitPhotoHandler(refVisit){
                @Override
                public void onClick(View v) {
                    VisitImplEx.setPhotoTag(itemid);
                    super.onClick(v);
                }
            };

            if (doc.isEditable())
                findViewById(R.id.ivPhoto).setOnClickListener(photoHandler::onClick);
        } else {
            refVisit.read(refVisit.getRowid(), false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (photoHandler.onActivityResult(requestCode, resultCode, data)) {
            InvEquItem i = doc.findItem(itemid);

            if (i != null){
                i.check = 1;

                doc.write();
                doc.close();
            }

            thumb();
        }
    }

    protected void thumb() {
        refVisit.read();
        refVisit.close();;

        VisitItemEx i = refVisit.findPhoto(itemid);

        if(i != null) {
            Bitmap bm = BitmapFactory.decodeFile(new String(i.id));
            ivPhoto.setImageBitmap(bm);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing()){
            InvEquItem i = doc.findItem(itemid);

            if (i != null && i.newItem == 1 && i.check == 0) {
                doc.getData().items.remove(i);
                doc.write();
                doc.close();
            }
        }
    }
}
