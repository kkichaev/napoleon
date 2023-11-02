package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.grsoft.dataobjects.impl.BankIncassImpl;
import com.grsoft.napoleon.documents.BankDoc;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocDeleteHelper;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.util.GpsCoord;
import com.grsoft.view.Refreshable;

public class BankIncassList extends Activity implements Refreshable {

    public static void open(Context context){
        Intent intent = new Intent(context, BankIncassList.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank_incass_list);

        findViewById(R.id.btnNewDoc).setOnClickListener(view -> {
            BankIncassImpl doc = new BankIncassImpl();
            if(doc.init(view.getContext(), "", GpsCoord.empty))
                doc.open(view.getContext());
        });

        ListView lv = findViewById(R.id.lvDocs);
        lv.setOnItemClickListener((parent, view, position, id) -> {
            BankIncassImpl doc = (BankIncassImpl) parent.getAdapter().getItem(position);
            doc.open(BankIncassList.this);
        });

        registerForContextMenu(lv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListView lv = findViewById(R.id.lvDocs);
        lv.setAdapter(new DocumentsAdapter(this, BankDoc.instance(), "", "created desc"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Adapter a = ((ListView)findViewById(R.id.lvDocs)).getAdapter();
        if(a instanceof DocumentsAdapter) {
            ((DocumentsAdapter)a).close();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate( R.menu.doc_context_menu, menu);
        menu.removeItem(R.id.itCopy);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Adapter adapter = ((ListView)findViewById(R.id.lvDocs)).getAdapter();
        CreatableDocument<?> doc = (CreatableDocument<?>) adapter.getItem(menuInfo.position);
        if( doc != null ) {
            if (item.getItemId() == R.id.itDelete)
                docDelete(doc);
            else if (item.getItemId() == R.id.itEdit)
                doc.open(this);
        }
        return true;
    }

    protected void docDelete(CreatableDocument<?> doc) {
        DocDeleteHelper.delete(doc, this);
    }

    @Override
    public void refreshContent() {
        ((DocumentsAdapter)((ListView)findViewById(R.id.lvDocs)).getAdapter()).setDocType(BankDoc.instance());
    }
}
