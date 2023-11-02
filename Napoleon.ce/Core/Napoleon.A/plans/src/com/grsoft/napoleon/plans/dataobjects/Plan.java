package com.grsoft.napoleon.plans.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DocDataObject;
import com.grsoft.types.Scale;

@TableInfo(name="plan", keyFields = "date")
public class Plan extends DocDataObject{
     public String name = "";
     public Date from;
     public Date till;
     
     @Scale(value=100)
     public int plan;
     public String text = "";
     
     @Scale(value=100)
     public int fact;
}
