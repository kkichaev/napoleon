package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDocEx;

public class OrderDetailEx extends OrderDetail {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DocType.setCurDoc(getDocType());

        super.onCreate(savedInstanceState);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if( doc.isEmpty() )
                    doc.delete();
                finish();
            }
        });
    }

    protected DocType getDocType() { return OrderDocEx.instance(); }

    @Override
    protected String getOrgText(Org o) {
        return ((OrgEx)o).fullName();
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.orderdetailex);
    }
}
