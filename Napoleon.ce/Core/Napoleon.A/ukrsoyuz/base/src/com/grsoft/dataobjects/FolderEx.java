package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class FolderEx extends Folder {
	/***
	 * Коэффициент для автозаказа
	 */
	@Scale(value=Consts.SUM_SCALE)
	public int coeff = 0;
}
