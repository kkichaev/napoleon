package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org {
    public String email = "";

    public List<OrgDogovor> dogovors = new ArrayList<>();
    public List<OrgDogovor> agreements = new ArrayList<>();
}
