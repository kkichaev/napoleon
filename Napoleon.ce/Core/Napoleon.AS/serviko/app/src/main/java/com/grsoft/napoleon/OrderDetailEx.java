package com.grsoft.napoleon;

import android.location.Location;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.OrgLocation;
import com.grsoft.dataobjects.RouteDeviation;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgLocationImpl;
import com.grsoft.napoleon.util.OrgFoldersTree;
import com.grsoft.napoleon.util.WeekDay;
import com.grsoft.script.dataobjects.Script;
import com.grsoft.util.Pair;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderDetailEx extends OrderDetail {
    @Override
    public void onBackPressed() {
        checkDeviation();
        super.onBackPressed();
    }

    private void checkDeviation() {
        if(doc.isEditable() && !doc.isEmpty() && ((OrderEx)doc.getData()).locChecked == 0) {
            OrgEx oe = (OrgEx) org.getData();
            checkAgentOrgDistance(oe);
            checkOutOfRoute(oe);
            ((OrderEx) doc.getData()).locChecked = 1;
            doc.write();
        }
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

        // distinct no route and out of route
        return route.size() == 0 ? null : new Pair<>(null, null);
    }

    static boolean haveDocs(String id) {
        Date today = new Date();
        Set<String> ret = new HashSet<>();
        long now = Util.getDayStart(today).getTime();

        String where = String.format("created >= %d and created < %d and id='%s'", now, now + 24 * 3600 * 1000, id);
        List<Order> docs = DbReader.fetch(Order.class, where);
        return docs.size() > 0;
    }

    void checkOutOfRoute(OrgEx org) {
        boolean byRoute = true;
        Pair<String, String> route = getPrevNextRoute(org.id);
        if(route != null) {
            // org is out of the route
            if(route.first == null && route.second == null) {
                byRoute = false;
            } else {
                byRoute =
                        (route.first == null || haveDocs(route.first)) && (route.second == null || !haveDocs(route.second));
            }
        }

        if(!byRoute) {
            ((NapoleonApp)getApplication()).addAlert(org, RouteDeviation.OUT_OF_ROUTE_ORDER);
        }
    }

    @Override
    public void send() {
        checkDeviation();
        super.send();
    }

    private void checkAgentOrgDistance(OrgEx org) {
        Location cur = GPSUtilNew.getCurrentLocation(this);
        if(cur == null) {
            return;
        }

        ConfigImpl cfg = new ConfigImpl();
        StringBuilder sb = new StringBuilder();
        if(!cfg.getValue(sb, ConfigHelper.ORG_RADIUS)) {
            return;
        }

        OrgLocationImpl loc = new OrgLocationImpl();
        if(org.latitude != 0) {
            OrgLocation ol = loc.getData();
            ol.latitude = org.latitude;
            ol.longitude = org.longitude;
        } else if(!loc.read("id", org.id)) {
            return;
        }

        try {
            double radius = Double.parseDouble(sb.toString());
            if(!DispositionActivity.isImIntoOrg(loc.getData(), cur, radius)) {
                ((NapoleonApp)getApplication()).addAlert(org, RouteDeviation.CREATED_FAR_FROM_ORG);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
