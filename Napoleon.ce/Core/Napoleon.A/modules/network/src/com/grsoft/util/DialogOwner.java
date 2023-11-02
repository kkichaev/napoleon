package com.grsoft.util;

import android.app.Dialog;

/***
 * Инетрфейс для формы на которой могут подниматься Dialog,
 * когда форма закрывается, необходимо закрывать активные диалоги.
 * @author kki
 *
 */
public interface DialogOwner {
	void setActiveDialog(Dialog dlg);
}
