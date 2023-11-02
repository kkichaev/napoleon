package com.serviko.dataobjects.ws;

import com.serviko.dataobjects.Contract;
import com.serviko.dataobjects.Price;
import com.serviko.dataobjects.actionTree.ActionCondition;
import com.serviko.dataobjects.actionTree.ActionRule;
import com.serviko.dataobjects.xml.WSDLElement;

import java.util.ArrayList;
import java.util.List;

@WSDLElement(name="ПолучитьНоменклатуру_v3Response")
public class ReqPriceResult extends ErrResult {
    @WSDLElement(name="Товары")
    public List<Price> price = new ArrayList<>();

    @WSDLElement(name="ПравилаАкций")
    public List<ActionRule> actionRules = new ArrayList<>();

    @WSDLElement(name="УсловияАкций")
    public List<ActionCondition> actionConditions = new ArrayList<>();

    @WSDLElement(name="Контракты")
    public List<Contract> contracts = new ArrayList<>();
}
