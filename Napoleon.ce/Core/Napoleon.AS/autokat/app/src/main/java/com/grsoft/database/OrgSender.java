package com.grsoft.database;

import androidx.annotation.NonNull;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.network.ObjectExportListener;

import java.util.List;

public class OrgSender extends Hitching implements ObjectExportListener {

    List<OrgEx> orgs;

    public OrgSender() {
        this(false);
    }

    public OrgSender(boolean sent){
        super(OrgEx.class, "Org");

        String where = compileWhere(sent);
        orgs = DbReader.fetch(OrgEx.class, where);
    }

    @NonNull
    private String compileWhere(boolean exported) {
        StringBuilder sb = new StringBuilder();

        if (!exported){
            sb.append("([flags] & (");
            sb.append(Org.FL_EXPORTED).append("|");
            sb.append( Org.FL_USER_CREATED ).append(")) = ").append(Org.FL_USER_CREATED);
        }

        return sb.toString();
    }

    @Override
    public int size() {
        return orgs.size();
    }

    @Override
    public DataObject get(int i) {
        return orgs.get(i);
    }

    @Override
    public void onEnd() {
        DbWriter w = new DbWriter();
        for(OrgEx o : orgs) {
            o.flags |= Org.FL_EXPORTED;
            w.insertRecord(o);
        }
    }
}
