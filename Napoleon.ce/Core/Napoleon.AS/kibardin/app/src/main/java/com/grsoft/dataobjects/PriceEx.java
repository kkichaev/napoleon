package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class PriceEx extends Price{
    public List<RelatedItem> related = new ArrayList<>();
    public String group = "";
}
