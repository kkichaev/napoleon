/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   02/08/2011   creating
 */
package com.grsoft.network;
import com.grsoft.aceteam.R;

import android.text.format.Time;

public class TimeMember extends Member {
	public TimeMember() {
		setValue(new Time());
	}
	
	@Override
	public String toString() {
		return ((Time)getValue()).toString();
	}
}
