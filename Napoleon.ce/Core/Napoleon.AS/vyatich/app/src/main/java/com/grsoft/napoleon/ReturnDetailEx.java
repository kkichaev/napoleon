package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;

import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.PhotoDocument;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.HorizontalListView;
import com.grsoft.napoleon.util.ImagesItemsAdapter;
import com.grsoft.napoleon.util.VisitPhotoHandler;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;

import java.util.ArrayList;
import java.util.List;

public class ReturnDetailEx extends ReturnDetail implements PopupMenu.OnMenuItemClickListener {
    VisitImpl refVisit;

    VisitPhotoHandler photoHandler;

    ImagesItemsAdapter adapter;
    VisitItem selectedItem;

    @Override
    protected void setContentView() {
        setContentView(R.layout.returndetail);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        refVisit.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(refVisit.isEmpty()) {
            refVisit.delete();
        }
    }

    @Override
    public void send() {
        List<DocExportListener> sendDocs = new ArrayList<>();
        sendDocs.add(new DocSendListner(docType.getObjectName(), doc));
        if(!refVisit.isEmpty()) {
            sendDocs.add(new DocSendListner(VisitDoc.instance().getObjectName(), refVisit));
        }
        new DocumentSender(ReturnDetailEx.this, btnSend, sendDocs, this).execute((Void[])null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(photoHandler.onActivityResult(requestCode, resultCode, data)) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        photoHandler.storeData(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        photoHandler.restoreData(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(refVisit == null) {
            ReturnEx d = (ReturnEx) doc.getData();

            refVisit = new VisitImpl();
            refVisit.getData().created = d.visitDoc;
            if(!refVisit.read()) {
                refVisit.init(this, d.id, new GpsCoord(d.latitude, d.longitude, d.stltime));
                d.visitDoc = refVisit.getData().created;
                doc.write();
            }
            photoHandler = new VisitPhotoHandler(refVisit);
            if (doc.isEditable())
                findViewById(R.id.btnPhoto).setOnClickListener(photoHandler);
        } else {
            refVisit.read(refVisit.getRowid(), false);
        }

        adapter = new ImagesItemsAdapter(this, refVisit.getData().items);
        HorizontalListView g = (HorizontalListView) findViewById(R.id.gvItems);
        g.setAdapter(adapter);
        g.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                selectedItem = (VisitItem) adapter.getItem(arg2);
                PopupMenu menu = new PopupMenu(ReturnDetailEx.this, arg1);
                menu.setOnMenuItemClickListener(ReturnDetailEx.this);
                menu.inflate(R.menu.return_detail_photo);
                menu.show();
                return true;
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem arg0) {
        if (arg0.getItemId() == R.id.itShow) {
            String photo = new String(selectedItem.id);
            preview(photo);
        } else if (arg0.getItemId() == R.id.itDelete) {
            refVisit.getData().items.remove(selectedItem);
            doc.write();
            adapter.notifyDataSetChanged();
        }
        return false;
    }

    private void preview(String path) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = View.inflate(this, R.layout.image_show, null);
        ImageView preview = (ImageView) dialogView.findViewById(R.id.imageView1);
        Bitmap bm = BitmapFactory.decodeFile(path);
        preview.setImageBitmap(bm);
        builder.setView(dialogView);
        builder.create().show();
    }
}
