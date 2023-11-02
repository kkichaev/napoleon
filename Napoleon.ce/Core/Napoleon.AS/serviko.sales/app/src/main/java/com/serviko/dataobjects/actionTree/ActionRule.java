package com.serviko.dataobjects.actionTree;

import com.serviko.dataobjects.xml.WSDLElement;

public class ActionRule {
    @WSDLElement(name="ИдентификаторТовара")
    public String idPrice = "";

    @WSDLElement(name="ИдентификаторУсловия")
    public String idCondition = "";

    @WSDLElement(name="Скидка")
    public float discount = 0;

    @WSDLElement(name="ЭтоПодарок")
    public boolean isGift = false;

    @WSDLElement(name="Ранжирование")
    public int range = 100;
}
