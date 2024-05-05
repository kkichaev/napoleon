package com.grsoft.napoleon;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.Contact;
import com.grsoft.dataobjects.ContactEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;

public class DocumentsEx extends Documents {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if(BuildConfig.DEBUG) {
//            OrgEx oe = (OrgEx) org.getData();
//            ContactEx ce = new ContactEx();
//            ce.name = "This is test name";
//            ce.email = "email@test.com";
//            ce.phone = "+7(111)244 55-30-44";
//            oe.contacts.add(ce);
//        }
    }

    @Override
    protected void adjustViewForDocType(DocType docType) {
        if(docType == DebtDoc.instance()) {
            BalanceView.open(this, org.getData().id);
            finish();
        }
        super.adjustViewForDocType(docType);
    }

    ContactView cv = new ContactView();
    @Override protected int getContactViewid() {
        return cv.getResourceID();
    }
    @Override public void setContactView(Contact contact, View view) {cv.setContactView(this, contact, view);}
}
