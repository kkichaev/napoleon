package com.grsoft.dataobjects;

public class OrderStatusData {
	static public final int STATUS_ALL = -1;
	static public final int STATUS_NOT_IN_ROUTE = 100;
	
	public int status = STATUS_ALL;
	public String text = "";
	
	public String toString() { return text; }
	
	public static OrderStatusData create(int status) {
		OrderStatusData ret = new OrderStatusData();
		ret.status = status;
		switch(status) {
		case OrderDriverRouteInfo.STATUS_ACTIVE:
			ret.text = "Выполняется";
			break;
		case OrderDriverRouteInfo.STATUS_REJECT:
			ret.text = "Отменен";
			break;
		case OrderDriverRouteInfo.STATUS_FINISHED:
			ret.text = "Выполнен";
			break;
		case OrderDriverRouteInfo.STATUS_IN_ROUTE:
			ret.text = "В рейсе";
			break;
		case OrderDriverRouteInfo.STATUS_DONE_WIITH_RETURNS:
			ret.text = "Выполнен c возвратом";
			break;
		case STATUS_ALL:
			ret.text = "<Все>";
			break;
		case STATUS_NOT_IN_ROUTE:
			ret.text = "Маршрут не назначен";
			break;
		default:
			ret = null;
		}
		
		return ret;
	}
}
