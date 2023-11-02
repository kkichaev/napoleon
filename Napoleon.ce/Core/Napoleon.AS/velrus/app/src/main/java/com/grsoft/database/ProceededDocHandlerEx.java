package com.grsoft.database;

import android.database.sqlite.SQLiteStatement;

import com.grsoft.dataobjects.OrderProceeded;
import com.grsoft.dataobjects.OrderProceededEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesDoc;

public class ProceededDocHandlerEx extends ProceededDocHandler {
    @Override
    protected String getParamStmt(DocType docType) {
        if(docType == SalesDoc.instance()) {
            return "' SET params = (params | ?), podRemark = ?, number = ? WHERE created = ?";
        }
        return super.getParamStmt(docType);
    }

    @Override
    protected void bindArgs(OrderProceeded proceeded, int param, SQLiteStatement stmt) {
        if(proceeded.type.equals(SalesDoc.instance().getObjectName())) {
            stmt.bindLong(1, param);
            stmt.bindString(2, proceeded.remark);
            stmt.bindString(3,((OrderProceededEx)proceeded).number);
            stmt.bindLong(4, proceeded.created.getTime());
        } else
            super.bindArgs(proceeded, param, stmt);
    }
}
