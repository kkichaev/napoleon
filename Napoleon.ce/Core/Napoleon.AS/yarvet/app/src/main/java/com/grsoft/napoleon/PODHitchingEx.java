package com.grsoft.napoleon;

import android.database.sqlite.SQLiteStatement;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.PODHitching;
import com.grsoft.database.ProceededDocHandler;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderProceeded;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.TargetDoc;

public class PODHitchingEx extends PODHitching {

    @Override
    protected ProceededDocHandler createHandler() {
        return new ProceededDocHandler(){
            @Override
            public void handle(OrderProceeded proceeded, int param) {
                if (DocType.getDocType(proceeded.type) == TargetDoc.instance())
                    handleTarget(proceeded);
                else
                    super.handle(proceeded, param);
            }
        };
    }

    SQLiteStatement stmt = null;
    private void handleTarget(OrderProceeded proceeded) {

        if (stmt == null) {
            DbWriter.checkDBTable(TargetDoc.instance().dataType());
            StringBuilder sql = new StringBuilder();
            String table = DataObjectInfo.getInstance().getTableName(TargetDoc.instance().dataType());
            sql.append("UPDATE '").
                    append(table).
                    append("' SET CLOSED=1 WHERE CREATED = ?");
            stmt = DataBaseManager.getDataBase().compileStatement(sql.toString());
        }

        stmt.clearBindings();
        stmt.bindLong(1, proceeded.created.getTime());
        stmt.execute();
    }
}
