package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.impl.AgentImpl;
import com.grsoft.dataobjects.impl.ManagerAgentImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@TableInfo(name="RoutDeviation", keyFields = "date,userid")
public class RouteDeviation extends DataObject {

    public static final int CREATED_FAR_FROM_ORG = 1;
    public static final int OUT_OF_ROUTE_ORDER = 2;
    public static final int SHORT_VISIT_TIME = 3;
    public static final int DONT_WORK = 4;

    public Date date = new Date();
    public String userid = "";
    public String id = "";
    public int type = 0;
    public String orgName = "";

    public static List<RouteDeviation> fromData(Map<String,String> data) {
        List<RouteDeviation> ret = new ArrayList<>();

        String split = "<,>";
        String id = data.get("id");
        if(!id.contains(split)) {
            split = ",";
        }

        String[] ids = id.split(split);
        String[] uids = data.get("userid").split(split);
        String[] dates = data.get("date").split(split);
        String[] types = data.get("type").split(split);
        String[] names = data.get("orgName").split(split);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        for(int i=0; i<uids.length; i++) {
            try {
                RouteDeviation rd = new RouteDeviation();
                rd.date = sdf.parse(dates[i]);
                rd.userid = uids[i];
                rd.type = Integer.parseInt(types[i]);

                if(i < ids.length)
                    rd.id = ids[i];
                if(i < names.length)
                    rd.orgName = names[i];

                ret.add(rd);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public String getTitle() {
        switch (type) {
            case CREATED_FAR_FROM_ORG:
                return "Заказ вне торговой точки";
            case OUT_OF_ROUTE_ORDER:
                return "Отклонение от маршрута";
            case SHORT_VISIT_TIME:
                return "Заказ быстрее минимального значения";
        }
        return "Агент не вышел на маршрут";
    }

    public String getNotifyText(boolean noAgent) {
        String name = "";
        if(!noAgent) {
            ManagerAgentImpl ai = new ManagerAgentImpl();
            ai.read("id", userid);
            name = "Агент " + ai.getData().name + " ";
        }

        switch (type) {
            case CREATED_FAR_FROM_ORG:
                return String.format("%sсоздал заказ на %s не в торговой точке", name, orgName);
            case OUT_OF_ROUTE_ORDER:
                return String.format("%sнарушил маршрут, создав визит на %s", name, orgName);
            case SHORT_VISIT_TIME:
                return String.format("%sоформил визит в %s быстрее минимального значения", name, orgName);
        }
        return String.format("%sне вышел на маршрут", name);
    }
}
