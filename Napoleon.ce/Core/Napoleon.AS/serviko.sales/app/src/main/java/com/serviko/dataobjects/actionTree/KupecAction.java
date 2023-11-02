package com.serviko.dataobjects.actionTree;

import com.serviko.dataobjects.Price;

import java.util.List;
import java.util.Map;

public class KupecAction extends ActionDef{
    static KupecAction instance;

    private KupecAction() {
        super();
        action = new ActionCondition();
        action.id = "лого%20Купец.png";
        action.name = "Купец";
    }

    public static KupecAction get() {
        if(instance == null) {
            instance = new KupecAction();
        }
        return instance;
    }
}
