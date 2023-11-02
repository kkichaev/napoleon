package com.ashberrysoft.leadertask.interfaces;

import java.util.Date;


/**
 * Интерфейс, предназначенный для передачи установленного
 * времени из TimePickerDialog в HomeActivity
 * 
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 *
 */
public interface OnTimeChangedListener {
    public void onTimeChanged(Date date);
}