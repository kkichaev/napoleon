package com.grsoft.util;

import java.io.Serializable;

/**
 * Размер ширина Х высота
 * @author kki
 *
 */
@SuppressWarnings("serial")
public class Size implements Serializable {
	public int width;
	public int hight;
	
	
	public Size(int w,int h){
		this.width = w;
		this.hight = h;
	}
}
