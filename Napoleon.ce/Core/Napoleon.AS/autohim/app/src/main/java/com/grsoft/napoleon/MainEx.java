package com.grsoft.napoleon;

import android.widget.BaseAdapter;

import com.grsoft.dataobjects.OrgSum;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;

public class MainEx extends Main{

    @Override
    protected BaseAdapter createSolidMainAdapter() {
        return new MainAdapter(this);
    }

    static class MainAdapter extends SolidMainAdapter {
        String filter;
        public MainAdapter(Main main) {
            super(main);
        }

        @Override
        public void adjustView() {
            super.adjustView();
            load(filter);
        }

        @Override
        protected void load(String filter) {
            this.filter = filter;
            super.load(filter);
        }

        @Override
        protected String getWhereStr() {
            String ret = super.getWhereStr();
            if(DocType.getCurDoc() == DebtDoc.instance()) {
                if(ret.length() > 0) {
                    ret += " and ";
                }
                ret += "id in (select id from " + new OrgSum().getTableName() + " where sum <> 0 and type='" +
                        DebtDoc.DOC_NAME + "')";
            }
            return ret;
        }
    }
}
