package com.grsoft.napoleon;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;

public class DocumentsEx extends Documents {

    @Override
    protected DocumentsAdapter createAdapter(DocType docType, String id) {
        String order = getOrder(docType);
        return new Adapter(this, docType, id, order);
    }

    class Adapter extends DocumentsAdapter {

        public Adapter(Context context, DocType docType, String orgId, String order) {
            super(context, docType, orgId, order);
        }

        @Override
        protected void setData(View view, Document<?> doc, int position) {
            super.setData(view, doc, position);
            Object order = doc.getData();
            if(order instanceof OrderEx) {
                int color = ((OrderEx)order).fromKIS > 0 ? getResources().getColor(R.color.item_highlight) : Color.BLACK;
                int[] ids = new int[] {
                        R.id.tvDate,
                        R.id.tvSum,
                        R.id.tvOther,
                };
                for(int id : ids) {
                    ((TextView)view.findViewById(id)).setTextColor(color);
                }
            }
        }
    }
}
