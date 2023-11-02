package com.grsoft.napoleon;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrgImpl;

import java.util.Date;
import java.util.HashSet;

public class MainEx extends Main{
    boolean loadedDebs = false;
    private HashSet<String> outOrgs = new HashSet<String>();

    @Override
    protected void onResume() {
        loadedDebs = false;
        super.onResume();
    }

    private boolean isOutOrg(Org o) {
        if( !loadedDebs ) {
            loadedDebs = true;

            outOrgs.clear();

            SQLiteDatabase db = DataBaseManager.getDataBase();

            String table = DataObjectInfo.getInstance().getTableName(Delivery.class);
            String sql = "SELECT id FROM " + table + " WHERE paydate < ? and sumD <> 0 GROUP BY id";

            Date curDate = new Date();
            String[] args = { Long.toString(curDate.getTime()) };

            try {
                Cursor c = db.rawQuery(sql, args);
                while( c.moveToNext() )
                    outOrgs.add(c.getString(0));
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return outOrgs.contains(o.id);
    }

    @Override
    protected void drawOrg(Org oi, View view) {
        super.drawOrg(oi, view);

        if( orgSum.getData().sum < 0 )
            return;

        if( isOutOrg(oi) ) {
            TextView tv = (TextView)view.findViewById(R.id.tvOrgName);
            tv.setTextColor(Color.RED);
        }
    }

}
