package com.grsoft.napoleon;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.AllDocList;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocWType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.DatePeriod;

public class DocListEx extends DocList {
    @Override protected int getViewID() { return R.layout.doclist_ex; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });
    }

    @Override
    protected String getDocText(Org o, Document<?> doc) {
        String text = "<b>" + ((OrgEx)o).fullName() + "</b>";
        if( Features.SHOW_ORG_ADDRESS && o.address.length() > 0 )
            text += "<br/>" + o.address;

        if(doc instanceof DocWType) {
            text += "<br/>" + ((DocWType)doc).docType() ;
        }
        return text;
    }

    @Override
    protected void drawData(View view, Document<?> doc, int position) {
        super.drawData(view, doc, position);
        if(doc instanceof DocWType) {
            ImageView ivStatus = (ImageView) view.findViewById(R.id.ivStatus);
            ivStatus.setVisibility(View.VISIBLE);
            CreatableDocument<?> src = ((DocWType) doc).getSource();
            int resouce = src == null ? R.drawable.notsend : getDocStatusResource(src);
            ivStatus.setImageResource(resouce);

        }
    }

    @Override
    protected void refreshTotalSum(boolean useFilter) {
        long sum = 0;
        int weight = 0;
        int count = 0;

        for( int i=0; i<adapter.getCount(); i++ ) {
            Document<?> d = (Document<?>) adapter.getItem(i);

            if(d != null){
                sum += getDocSum(d);
                weight += ((DocWType)d).weight();
                //count += (DocWType)d).count();
            }
        }

        DocType.getCurDoc().updateTotalSum(this, sum, weight, count, R.id.tvDocSum);
    }

    @Override
    protected DocListAdapter createListAdapter(DocType docType) {
        return new DocListAdapterEx(this, saveDatePeriod);
    }

    class DocListAdapterEx extends DocListAdapter {

        public DocListAdapterEx(Context context, DatePeriod filter) {
            super(context, OrderDoc.instance(), filter);
        }

        @Override
        public com.grsoft.napoleon.documents.DocList fillDocList(DocType docType, String orgId, String order, DatePeriod dp) {
            return new AllDocList(orgId, dp, true);
        }

    }
}
