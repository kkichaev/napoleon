package com.serviko.view;

import android.content.Context;
import android.util.AttributeSet;

import com.serviko.dataobjects.Basket;
import com.serviko.dataobjects.Price;
import com.serviko.view.numberpicker.NumberPicker;

public class PriceQtyPickerOld extends NumberPicker implements NumberPicker.EventsHandler {

    int step = 1;
    int pack = 1;
    Price item;
    Basket basket;

    public PriceQtyPickerOld(Context context) {
        super(context);
    }

    public PriceQtyPickerOld(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PriceQtyPickerOld(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setData(Price item, Basket basket) {
        this.basket = basket;
        this.item = item;
        setData(item.quant, item.inPack, basket.getQty(item.id));
    }

    void setData(int step, int pack, int current) {
        this.step = step;
        this.pack = pack;
        setValue(current);

        setEventsHandler(this);
    }

    @Override
    public int nextValue(int current) {
        return current + step;
    }

    @Override
    public int prevValue(int current) {
        if(current > step)
            return current - step;
        return 0;
    }

    @Override
    public String getText(int current) {
        String text = Integer.toString(current);
        if(current > 0 && pack != 1) {
            if((current % pack) == 0) {
                int pq = current / pack;
                text += " (" + Integer.toString(pq) + " уп.)";
            }
        }
        return text;
    }

    @Override
    public void onValueChanged(NumberPicker sender, int current) {
        basket.changeQty(item, current, false);
    }
}
