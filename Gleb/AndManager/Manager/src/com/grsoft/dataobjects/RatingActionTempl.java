package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="ratingactiontempl", keyFields="id")
public class RatingActionTempl extends GuidDataObject{
	public List<RatingActionTemplItem> items = new ArrayList<RatingActionTemplItem>();
}
