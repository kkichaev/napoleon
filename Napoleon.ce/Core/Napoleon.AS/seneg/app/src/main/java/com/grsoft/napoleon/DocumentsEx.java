package com.grsoft.napoleon;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.TextView;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.Documents;
import com.grsoft.napoleon.documents.AllDocList;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocWType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.Selector;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {
    DocFilterOnClickListener docFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        docFilter = new DocFilterOnClickListener(this, true, ScriptDefImpl.canScripting());
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { onBackPressed();}
        });
    }

    @Override protected int getContextMenuId() { return R.menu.doc_context_menu_ex; }

    @Override
    protected void createNewDoc() {
        docFilter.selectDocType(this, this, true);
    }

    @Override
    protected String orgInfo(Org o) {
        String ret = ((OrgEx)o).fullName();
        if(Features.SHOW_ORG_ADDRESS && o.address.length() > 0 ) {
            ret += "<br><i>" + o.address + "</i>";
        }
        return ret;
    }

    @Override
    public void updateTotalSum(long sum, int weight) {
//        super.updateTotalSum(sum, weight);
    }

    @Override
    public void updateTotalSum(long sum, int weight, int count) {
//        super.updateTotalSum(sum, weight, count);
    }

    @Override
    public void selectedType(DocType newDocType) {
        DocType.setCurDoc(newDocType);
        super.createNewDoc();
    }

    @Override
    protected DocumentsAdapter createAdapter(DocType docType, String id) {
        return new DocumentsAdapterEx(this, docType, id);
    }

    View.OnClickListener acceptClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DocWType d = (DocWType) v.getTag();
            boolean checked = d.accepted();
            boolean svChecked = checked;

            if(d.isEditable()) {
                if(!d.accepted()) {
                    if(!isGpsPosValid()) {
                        makeLocationAlert();
                    } else {
                        checked = d.accept(v.getContext());
                    }
                } else {
                    checked = d.accept(v.getContext());
                }
            }
            if(svChecked != checked) {
                adapter.notifyDataSetChanged();
            }
            ((CheckBox) v).setChecked(checked);
        }
    };

    class DocumentsAdapterEx extends DocumentsAdapter {

        public DocumentsAdapterEx(Context context, DocType docType, String orgId) {
            super(context, docType, orgId, "", R.layout.docs_list_row_ex);
        }

        @Override
        public DocList fillDocList(DocType docType, String orgId, String order, DatePeriod dp) {
            return new AllDocList(orgId, dp, false);
        }

        @Override
        protected void setData(View view, Document<?> doc, int position) {
            super.setData(view, doc, position);

            DocWType d = (DocWType)doc;

            CheckBox cb = view.findViewById(R.id.cbAccept);
            cb.setVisibility(d.canAccept() ? View.VISIBLE : View.INVISIBLE);
            cb.setOnClickListener(acceptClick);
            cb.setChecked(d.accepted());
            cb.setTag(d);

            TextView tv;
            tv = view. findViewById(R.id.tvDocName);
            tv.setText(d.docDescription());

            String wt = "";
            tv = view.findViewById(R.id.tvWeigh);
            int w = d.weight();
            if(w != 0)
                wt = Util.IntToScaleStr(w, Consts.WEIGHT_SCALE) + " Í„";
            tv.setText(wt);

            view.findViewById(R.id.tvSum).setVisibility(d.sum() == 0 ? View.INVISIBLE : View.VISIBLE);
        }
    }
}
