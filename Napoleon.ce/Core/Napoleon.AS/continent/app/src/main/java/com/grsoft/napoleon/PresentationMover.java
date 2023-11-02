package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.PriceImpl;

public class PresentationMover implements PriceMover{
    private PresentationList list;
    private int position = 0;

    public PresentationMover(){
        initPresentList();
    }

    protected void initPresentList() {
        list = PresentationFolderW.items;

        if (list.size() == 0)
            PresentationFolderW.items.fill(false);
    }

    @Override
    public PriceImpl move(PriceImpl prev, boolean next) {
        for (int i = 0; i < list.size(); i ++){
            PresentationData data =  list.get(i);

            if (data.id.equals(prev.getData().id)) {
                position = i;
                break;
            }
        }

        position = next ? position + 1 : position - 1;

        if (position < 0) position = list.size() - 1;
        else if (position >= list.size()) position = 0;

        PriceImpl price = new PriceImpl();
        PresentationData data =  list.get(position);
        price.read(data.rowid);
        price.close();

        return price;
    }
}
