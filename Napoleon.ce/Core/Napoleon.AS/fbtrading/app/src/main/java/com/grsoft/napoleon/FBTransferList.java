package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.grsoft.dataobjects.impl.FBTransferImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocDeleteHelper;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.FBTransferDoc;
import com.grsoft.util.gps.GPSUtilNew;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.Refreshable;

public class FBTransferList extends BaseActivity implements Refreshable {

    Adapter adapter;

    static DocType prevDoc;

    static void open(Context context) {
        Intent i = new Intent(context, FBTransferList.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transfer_list);

        if(DocType.getCurDoc() != FBTransferDoc.instance())
            prevDoc = DocType.getCurDoc();

        ListView lv = findViewById(R.id.lvDocs);
        adapter = new Adapter(this);
        lv.setDividerHeight(0);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Document<?> doc = (Document<?>) parent.getAdapter().getItem(position);
                if(doc != null)
                    doc.open(FBTransferList.this);
            }
        });

        registerForContextMenu(lv);

        findViewById(R.id.btnNewDoc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FBTransferImpl doc = (FBTransferImpl) FBTransferDoc.instance().create();
                if (doc.init(FBTransferList.this, "", GPSUtilNew.getLastKnownLocation())) {
                    doc.open(FBTransferList.this);
                }
                doc.close();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(prevDoc != null)
            DocType.setCurDoc(prevDoc);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.doc_context_menu, menu);
        menu.removeItem(R.id.itCopy);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Document<?> doc = (Document<?>) adapter.getItem(menuInfo.position);
        int id = item.getItemId();
        if(id == R.id.itEdit) {
            doc.open(this);
        }
        if(id == R.id.itDelete) {
            DocDeleteHelper.delete((CreatableDocument<?>) doc, this);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        DocType.setCurDoc(FBTransferDoc.instance());
        adapter.fetchByPeriod(FBTransferDoc.instance(), null, null, null, null);
    }

    @Override
    public void refreshContent() {
        adapter.fetchByPeriod(FBTransferDoc.instance(), null, null, null, null);
    }

    class Adapter extends DocumentsAdapter {
        public Adapter(Context context) {
            super(context, FBTransferDoc.instance(), null, "created desc");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View ret =  super.getView(position, convertView, parent);
            ret.findViewById(R.id.tvSum).setVisibility(View.GONE);
            return ret;
        }
    }
}
