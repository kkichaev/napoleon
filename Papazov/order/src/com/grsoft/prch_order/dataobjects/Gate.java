package com.grsoft.prch_order.dataobjects;

import java.lang.reflect.Field;
import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.Scale;

@TableInfo(name="Gate", keyFields="id")
public class Gate extends DataObject {
	static final int DIST_SCALE = 100;
	
	public String id = "";
	
	/**
	 * Заказчик:
	 */
	public String customer = "";
	
	public String phone = "";
	public String email = "";
	public String address = "";
	
	/**
	 * Расст. от скл.:
	 */
	@Scale(value=DIST_SCALE)
	public int distance = 0;
	
	/**
	 * Тип ворот
	 */
	public String type = "";
	/**
	 * Цвет ворот
	 */
	public String color = "";
	
	/**
	 * Окна, шт.:
	 */
	public int window = 0;
	
	/**
	 * калитка
	 */	
	public int gate = 0;
	
	/**
	 * Тип привода
	 */
	public String drive = "";
	
	/**
	 * Высота проема:
	 */
	public int height = 0;
	
	/**
	 * Ширина проема:
	 */
	public int width = 0;
	/**
	 * Притолока:
	 */
	public int up = 0;
	
	/**
	 * Пристенок
	 */
	public int nearWall = 0;

	/**
	 * Тип подъема:
	 */
	public String climbType = "";
	
	/**
	 * Материал проема:
	 */
	public String climbMaterial = "";
	
	
	/**
	 * Дополнительная комплектация:
	 */
	public String addComplect = "";
	
	/**
	 * Дополнительные работы и материалы:
	 */
	public String addWork = "";
	
	/**
	 * Изготовление проема:
	 */
	public int buildClimb = 0;
	
	/**
	 * Сечение проф.трубы:
	 */
	public String tubeCut = "";
	
	/**
	 * Длина, м.:
	 */
	public int tubeLength = 0;
	
	public String tubeColor = "";
	
	/**
	 * Комментарии для монтажников:
	 */
	public String comment = "";
	
	/**
	 * Заказчику рекомендовано:
	 */
	public String commentCustomer = "";
	
	/**
	 * Объект не готов к монтажу:
	 */
	public int notReady = 0;
	
	/**
	 * Продолжительность монтажа (план):
	 */
	public int installTime = 0;
	
	/**
	 * Замер произвел:
	 */
	public String autor = "";
	
	public Date date = new Date();
	
	/**
	 * Повторный осмотр (дата):
	 */
	public Date repeatInspect = new Date();
	
	public Object getFieldValue(String fieldName) {
		Object ret = null;
		try {
			Field f = getClass().getField(fieldName);
			if(f != null)
				ret = f.get(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public void setFieldValue(String fieldName, Object val) {
		try {
			Field f = getClass().getField(fieldName);
			if(f != null)
				f.set(this, val);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getFieldScale(String fieldName) {
		int ret = 0;
		try {
			Field f = getClass().getField(fieldName);
			if(f != null) {
				Scale s = f.getAnnotation(Scale.class);
				if(s != null)
					ret = s.value();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}
