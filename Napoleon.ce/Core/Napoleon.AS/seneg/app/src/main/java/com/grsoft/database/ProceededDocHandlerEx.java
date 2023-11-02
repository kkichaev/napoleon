package com.grsoft.database;

import android.database.sqlite.SQLiteStatement;

import com.grsoft.dataobjects.OrderProceeded;
import com.grsoft.dataobjects.OrderProceededEx;
import com.grsoft.napoleon.documents.DocType;

public class ProceededDocHandlerEx extends ProceededDocHandler {
    boolean haveLinkField = false;

    @Override
    protected String getParamStmt(DocType docType) {
        try {
            docType.getDocClass().getField("link");
            haveLinkField = true;
            return "' SET params = (params | ?), podRemark = ?, link=? WHERE created = ?";
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return super.getParamStmt(docType);
    }

    @Override
    protected void bindArgs(OrderProceeded proceeded, int param, SQLiteStatement stmt) {
        if(!haveLinkField)
            super.bindArgs(proceeded, param, stmt);
        else {
            stmt.bindLong(1, param);
            stmt.bindString(2, proceeded.remark);
            stmt.bindString(3, ((OrderProceededEx)proceeded).link);
            stmt.bindLong(4, proceeded.created.getTime());
        }
    }
}
