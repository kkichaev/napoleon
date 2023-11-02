package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.dataobjects.MerchItem;
import com.grsoft.dataobjects.impl.MerchImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.MerchDoc;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class MerchDetail extends BaseActivity {
    MerchImpl doc = new MerchImpl();
    PriceImpl priceImpl = new PriceImpl();
    Adapter adapter;
    protected LinesCountController linesController;

    public static void open(Context context, MerchImpl doc) {
        Intent i = new Intent(context, MerchDetail.class);

        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merchdetail);

        long rowid;
        if( savedInstanceState == null )
            rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
        else
            rowid = savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR);

        doc.read(rowid);

        OrgImpl orgIml = new OrgImpl();
        orgIml.getData().id = doc.getData().id;

        if(orgIml.read())
        {
            TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
            tvOrg.setText(orgIml.getData().name);
            orgIml.close();
        }

        adapter = new Adapter();
        ListView lv = findViewById(R.id.lvItems);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MerchItem mi = (MerchItem) adapter.getItem(position);
                priceImpl.getData().id = mi.id;
                priceImpl.read();
                doc.editItem(priceImpl.getRowid(), MerchDetail.this, position);
            }
        });

        LinesOnClickListener linesOnClickListener = new LinesOnClickListener(lv, (ImageButton)findViewById(R.id.btnLines), this, true);
        linesController = linesOnClickListener.getController();

        registerForContextMenu(lv);

        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        View btnSend =findViewById(R.id.btnSend);
        if( Features.CANT_SEND_SCRIPT_PART ) {
            if(ScriptImpl.containsDocument(MerchDoc.instance().getObjectName(), doc.getData().created, doc.getId()) != null)
                btnSend.setVisibility(View.GONE);
        }
        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!doc.isEmpty())
                    send();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(doc.isEditable() && doc.isEmpty()) {
            doc.delete();
        }
        super.onBackPressed();
    }

    void addItem() {
        DocType.setCurDoc(MerchDoc.instance());
        Warehouse.open(this, doc, true);
    }

    protected void send() {
        new DocumentSender(this, null, MerchDoc.instance().getObjectName(), doc, doc.getRowid()).execute((Void[])null);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (!doc.isExported())
            getMenuInflater().inflate(R.menu.order_detail_context_menu, menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        doc.read(doc.getRowid(), false);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        priceImpl.close();
        doc.close();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        MerchItem pitem = (MerchItem)((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).targetView.getTag();
        int pos = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
        priceImpl.getData().id = pitem.id;
        priceImpl.read();

        if (item.getItemId() == R.id.itDelete) {
            doc.getData().items.remove(pos);
            doc.write();
            doc.close();
        } else if (item.getItemId() == R.id.itEdit) {
            doc.editItem(priceImpl.getRowid(), this, pos);
        }
        adapter.notifyDataSetChanged();

        return super.onContextItemSelected(item);
    }

    class Adapter extends BaseAdapter {
        @Override
        public int getCount() {
            return doc.getData().items.size();
        }

        @Override
        public Object getItem(int arg0) {
            return doc.getData().items.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            MerchItem remnantItem = (MerchItem) getItem(arg0);
            priceImpl.getData().id = remnantItem.id;
            priceImpl.read();

            View view = setView(arg1, priceImpl, remnantItem);
            return view;
        }

        protected View setView(View view, PriceImpl priceImpl, MerchItem item) {
            if (view == null)
                view = View.inflate(MerchDetail.this, R.layout.merchdetailrow, null);

            TextView tvName = (TextView) view.findViewById(R.id.tvName);
            linesController.prepareTextView(tvName);
            tvName.setText(priceImpl.getData().name);
            TextView tvQty = (TextView) view.findViewById(R.id.tvQty);
            tvQty.setText(Util.IntToScaleStr(item.qty, Consts.QTY_SCALE));

            TextView tv = view.findViewById(R.id.tvBestBefore);
            tv.setText(Util.simpleDateFormat.format(item.bestBefore));

            view.setTag(item);

            return view;
        }
    }
}
