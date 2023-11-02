package com.serviko.dataobjects;

import com.serviko.dataobjects.actionTree.ActionDef;
import com.serviko.dataobjects.xml.WSDLElement;

import java.util.ArrayList;
import java.util.List;

public class Price {
    @WSDLElement(name="Родитель")
    public String parent = "";

    @WSDLElement(name="ЭтоГруппа")
    public boolean isFolder = false;

    @WSDLElement(name="Идентификатор")
    public String id = "";

    @WSDLElement(name="Код")
    public String code = "";

    @WSDLElement(name="Наименование")
    public String name = "";

    @WSDLElement(name="Остаток")
    public float qty = 0;

    @WSDLElement(name="Цена")
    public float cost = 0;

    @WSDLElement(name="Скидка")
    public float discount = 0;

    @WSDLElement(name="ЕстьОграниченияПоБюджету")
    public boolean haveBudgetConstrain = false;

    @WSDLElement(name="АлкогольнаяПродукция")
    public boolean isAlcohol = false;

    @WSDLElement(name="Кратность")
    public int quant = 1;

    @WSDLElement(name="Коэффициент")
    public int inPack = 1;

    @WSDLElement(name="Объем")
    public float volume = 0;

    @WSDLElement(name="КодКонтракта")
    public String contract = "";

    @WSDLElement(name="Производитель")
    public String suplyer = "";

    @WSDLElement(name="ХарактеристикиНоменклатуры")
    public List<PriceCategory> categories = new ArrayList<>();

    public ActionDef action;
}
