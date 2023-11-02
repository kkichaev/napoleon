package com.serviko.dataobjects;

import com.serviko.dataobjects.xml.WSDLElement;

public class Contract implements Comparable<Contract> {
    @WSDLElement(name="Код")
    public String id = "";

    @WSDLElement(name="Наименование")
    public String name = "";

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Contract contract) {
        return name.compareTo(contract.name);
    }
}
