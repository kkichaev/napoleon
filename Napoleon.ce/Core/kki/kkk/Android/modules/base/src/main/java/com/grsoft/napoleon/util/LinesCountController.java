/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Изменяет количество строк в списке 
 *
 * kki   29/01/2011   creating
 */
package com.grsoft.napoleon.util;

import android.widget.TextView;

public interface LinesCountController
{
	void prepareTextView(TextView textView);
	boolean isMinLines();
	boolean isVariable();
	void setVariable();
	void setLinesCount(int value);
	void setMinLines(int minLines);
}
