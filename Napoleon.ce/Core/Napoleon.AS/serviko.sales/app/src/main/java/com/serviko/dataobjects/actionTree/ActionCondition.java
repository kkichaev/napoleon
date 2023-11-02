package com.serviko.dataobjects.actionTree;

import com.serviko.dataobjects.xml.WSDLElement;

import java.util.ArrayList;
import java.util.List;

public class ActionCondition {
    @WSDLElement(name="»дентификатор”слови€—рабатывани€—кидки")
    public String id = "";

    @WSDLElement(name="”словие—рабатывани€—кидкиѕредставление")
    public String name = "";

    @WSDLElement(name="»дентификатор–одител€")
    public String parent = "";

    @WSDLElement(name="”словиеѕредоставлени€—кидки")
    public String condition = "";

    @WSDLElement(name="≈диница»змерени€”словий")
    public String unit = "";

    @WSDLElement(name="¬ид—равнени€")
    public String compareType = "";

    @WSDLElement(name="¬идќбъединени€”словий")
    public String combineType = "";

    @WSDLElement(name=" оличество")
    public float qty = 0;

    @WSDLElement(name="Ёто√руппа")
    public boolean isFolder = false;

    @WSDLElement(name="»дентификаторЌоменклатуры")
    public List<String> items = new ArrayList<>();
}
