package com.novotek.dataobjects;

import com.novotek.dataobjects.xml.Alias;

import java.util.ArrayList;
import java.util.List;

public class CommonInfo {
    public float min_order = 0;

    @Alias(name = "dateWorkBegin")
    public int start_hour = 0;

    @Alias(name = "dateWorkEnd")
    public int end_hour = 0;

    public int poll_interval = 0;

    public List<String> phone = new ArrayList<>();
}
