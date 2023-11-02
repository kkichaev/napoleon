package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.BalanceDoc;
import com.grsoft.dataobjects.impl.BalanceDocImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import java.util.List;

public class BalanceView extends DocumentsBase {
    public static void open(Context ctx, String orgId) {
        Intent i = new Intent(ctx, BalanceView.class);
        i.putExtra(ExtrasConst.ORG_ID_STR, orgId);
        ctx.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.debt_doc_title);
        Adapter adapter = new Adapter(this, org.getData().id);
        ListView lv = (ListView) findViewById(R.id.lvDocs);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener((parent, view, position, id) -> {
            BalanceDocImpl bd = (BalanceDocImpl) adapter.getItem(position);
            bd.open(BalanceView.this);
        });
    }

    @Override
    protected void adjustViewForDocType(DocType docType) {
        if( docType != DebtDoc.instance() ) {
            DocType.setCurDoc(docType);
            Documents.open(this, org.getData());
            finish();
        } else
            super.adjustViewForDocType(docType);
    }

    class Adapter extends DocumentsAdapter {
        public Adapter(Context context, String orgId) {
            super(context, DebtDoc.instance(), orgId, "", R.layout.balance_row);
        }

        @Override
        protected void setData(View view, Document<?> doc, int position) {
            BalanceDoc bd = (BalanceDoc) doc.getData();
            int color = bd.isOverdue() ? Color.RED : Color.BLACK;
            TextView tv;
            tv = view.findViewById(R.id.tvOther);
            tv.setText(bd.src.number);
            tv.setTextColor(color);

            String text = Util.simpleDateFormat.format(bd.src.date);
            if(bd.src.isDelivery()) {
                text += "\n" + Util.simpleDateFormat.format(bd.src.payDate);
            }
            tv = view.findViewById(R.id.tvDate);
            tv.setText(text);
            tv.setTextColor(color);

            if(bd.src.isDelivery()) {
                text = Util.IntToScaleStr(bd.src.sumDoc, Consts.SUM_SCALE, Util.DEC_DELIM, false);
                text += "\n" + Util.IntToScaleStr(bd.src.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
            } else {
                text = Util.IntToScaleStr(bd.src.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
            }

            tv = view.findViewById(R.id.tvSum);
            tv.setText(text);
            tv.setTextColor(color);

            tv = view.findViewById(R.id.tvTitle);
            tv.setText(bd.src.title);
        }
    }
}
