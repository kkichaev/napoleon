package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import com.grsoft.dataobjects.impl.PriceImpl;

public interface PriceMover {
	/**
	 * Передвижение по прайсу 
	 * @param price - текущий элемент
	 * @param next - true - идем вперед, false - назад 
	 * @return следующий элемент. null - нет больше элементов
	 */
	PriceImpl move(PriceImpl price, boolean next);
}
