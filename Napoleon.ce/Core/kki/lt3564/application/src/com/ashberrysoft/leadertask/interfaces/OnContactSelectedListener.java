package com.ashberrysoft.leadertask.interfaces;

/**
 * Интерфейс, предназначенный для передачи помеченных/не помеченных контактов (cheched / unchecked) 
 * из списка контактов, который составляет диалог выбора контактов, непосредственно в диалог выбора контактов
 * 
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * 
 */
public interface OnContactSelectedListener {
    public void onContactAdded(String contactName);
    public void onContactRemoved(String contactName);
}
