package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="RoutePhotos", keyFields="created")
@ServerInfo(name="RoutePhotos")
public class RoutePhotos extends VisitInfo {
	public String routeItemId = "";
	public List<RoutePhotoItem> items = new ArrayList<RoutePhotoItem>();
}
