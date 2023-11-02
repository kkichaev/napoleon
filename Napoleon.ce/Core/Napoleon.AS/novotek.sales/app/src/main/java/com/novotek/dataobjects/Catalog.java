package com.novotek.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class Catalog {
    public NameObj name = new NameObj();
    public String url = "";
    public List<Catalog> children = new ArrayList<>();
}
