package com.grsoft.util;
import com.grsoft.aceteam.R;

import android.hardware.Camera;

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

	public Size(Camera.Size sz) {
		this.width = sz.width;
		this.hight = sz.height;
	}

	public boolean isEqual(Camera.Size sz) {
		return minDim() == Math.min(sz.width, sz.height) && maxDim() == Math.max(sz.width, sz.height);
	}

	int minDim() { return Math.min(width, hight); }
	int maxDim() { return Math.max(width, hight); }

	public boolean isLower(Size ref) {
		if(maxDim() > ref.minDim()) return false;
		if(maxDim() < ref.maxDim()) return true;

		return minDim() < ref.minDim();
	}
}
