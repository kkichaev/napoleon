package com.serviko.dataobjects;

import com.serviko.dataobjects.actionTree.ActionCondition;
import com.serviko.dataobjects.actionTree.ActionDef;
import com.serviko.dataobjects.actionTree.ActionGoods;
import com.serviko.dataobjects.actionTree.ActionRule;
import com.serviko.dataobjects.actionTree.KupecAction;
import com.serviko.dataobjects.priceTree.PriceTree;
import com.serviko.dataobjects.ws.GetKupecItem;
import com.serviko.dataobjects.xml.WSDLElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Partner {
    public List<GetKupecItem> kupecAction = new ArrayList<>();

    PriceTree price = new PriceTree();

    List<ActionDef> actionDefs = new ArrayList<>();

    @WSDLElement(name="Идентификатор")
    public String id = "";

    @WSDLElement(name="АдресДоставки")
    public String address = "";

    @WSDLElement(name="Наименование")
    public String name = "";

    @WSDLElement(name="ТорговляПоЛицензии")
    public boolean haveLicense = false;

    public List<Order> orders = new ArrayList<>();

    @WSDLElement(name="ТорговыеПредставители")
    public List<Agent> agents = new ArrayList<>();

    public Set<String> manufacturer = new HashSet<>();

    public Basket basket = new Basket();

    public String toText() {
        return toText(false);
    }

    public String toText(boolean oneLine) {
        if(name.length() == 0)
            return address;
        String text = "<b>" + name + "</b> ";
        if(haveLicense)
            text += "<i>алк.</i> ";
        if(!oneLine)
            text += "<br/>";
        text += address;
        return  text;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void setPrice(List<Price> newPrice, List<ActionRule> actionRules,
                         List<ActionCondition> actionContidions)  {
        price = PriceTree.make(newPrice, manufacturer);
        actionDefs = ActionDef.create(newPrice, actionRules, actionContidions);
    }

    public PriceTree getPrice() { return price; }

    public List<ActionDef> getActions() {
        KupecAction ka = KupecAction.get();
        if(kupecAction.size() > 0) {
            if(!actionDefs.contains(ka)) {
                actionDefs.add(0, ka);
            }
        } else {
            actionDefs.remove(ka);
        }
        return actionDefs;
    }

    public ActionDef getAction(String id) {
        for(ActionDef ad : actionDefs)
            if(ad.getId().equals(id))
                return ad;
        return null;
    }

    public Order getOrder(String uid) {
        for(Order o : orders)
            if(o.uid.equals(uid))
                return o;

        return new Order();
    }
}
