package com.ashberrysoft.leadertask.data_providers;

import java.sql.SQLException;

import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.utils.Utils;

/**
 * Временный класс для отложенной загрузки данных. После рефакторинга должен стать не нужным.ß
 * 
 * @author V.Shcryabets<vshcryabets@gmail.com>
 * 
 */
public final class LazyLoadMethods {
    /**
     * Обновление кол-ва сообщений у задачи.
     * 
     * @param task
     * @param dbHelper
     */
    public static void updateTaskMessagesCount(Task task, DbHelper dbHelper) {
        if (task != null && task.getMessagesCount() == null) {
            try {
                task.setMessagesCount(dbHelper.getMessagesCount(task));
            } catch (SQLException e) {
                Utils.toLog(e);
            }
        }
    }
}