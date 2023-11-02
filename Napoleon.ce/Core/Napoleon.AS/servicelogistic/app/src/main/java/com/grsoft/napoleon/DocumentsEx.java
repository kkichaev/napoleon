package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.Toast;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.Pa;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.util.OrgFoldersTree;
import com.grsoft.napoleon.util.WeekDay;
import com.grsoft.util.Pair;
import com.grsoft.util.Util;

import java.util.Date;
import java.util.List;

public class DocumentsEx extends Documents{

    boolean allowCreatingDoc = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            StringBuilder sb = new StringBuilder();
            ConfigImpl ci = new ConfigImpl();
            if (ci.getValue(sb, "ByRoute") && Integer.parseInt(sb.toString()) != 0) {
                allowCreatingDoc = false;
                // get today route
                Pair<String, String> route = getPrevNextRoute(org.getData().id);
                if(route != null) {
                    allowCreatingDoc =
                            (route.first == null || haveDocs(route.first)) &&
                            (route.second == null || !haveDocs(route.second));
                    if(!allowCreatingDoc) {
                        Toast.makeText(this, "Создание документов на точку не по маршруту запрещено", Toast.LENGTH_LONG).show();
                    }
                }
                if(!allowCreatingDoc) {
                    Toast.makeText(this, "Создание документов на точку не по маршруту запрещено", Toast.LENGTH_LONG).show();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    static boolean haveDocs(String orgId) {
        long now = Util.getDate().getTime();
        String where = String.format("id='%s' and created > %d and created < %d"
                ,orgId
                , now, now + 24 * 3600 * 1000);

        for(DocTypeBase dt : DocType.docTypes) {
            if(dt.isCreatable()) {
                for(Document<?> d : dt.docList(null, null, where)) {
                    return true;
                }
            }
        }

        return false;
    }

    static Pair<String, String> getPrevNextRoute(String id) {
        Date now = new Date();
        int wi = OrgFoldersTree.GetWeekIndex(now);
        WeekDay wd = WeekDay.today();

        String where = String.format("name='%s' or name='%s'", wd.getCaption(), Integer.toString(wi) + wd.getCaption());
        List<OrgFolders> route = DbReader.fetch(OrgFolders.class, where);
        for(OrgFolders of : route) {
            for(int i=0; i<of.items.size(); i++) {
                OrgFolderItem ofi = of.items.get(i);
                if(ofi.name.equals(id)) {
                    String prev = i > 0 ? of.items.get(i-1).name : null;
                    String next = i < of.items.size() - 1? of.items.get(i+1).name : null;
                    return new Pair<>(prev, next);
                }
            }
            break;
        }

        return null;
    }

    @Override
    protected boolean canCreateDoc(DocType docType) {
        return allowCreatingDoc && super.canCreateDoc(docType);
    }
}
