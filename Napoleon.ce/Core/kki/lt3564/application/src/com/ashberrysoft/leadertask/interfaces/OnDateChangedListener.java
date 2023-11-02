package com.ashberrysoft.leadertask.interfaces;

import java.util.Date;


/**
 * Интерфейс, предназначенный для передачи установленной
 * даты из DatePickerDialog в HomeActivity
 * 
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 *
 */
public interface OnDateChangedListener {
    public void onDateChanged(Date date);
}
