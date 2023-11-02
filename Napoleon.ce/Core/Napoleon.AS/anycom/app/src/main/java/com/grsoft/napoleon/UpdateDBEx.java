package com.grsoft.napoleon;

import com.grsoft.database.AgentPlanRcv;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.DeliveryHitching;
import com.grsoft.database.HitchOnSelect;
import com.grsoft.database.Hitching;
import com.grsoft.database.PriceCostHitching;
import com.grsoft.database.PriceTypeHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.StoreHitching;
import com.grsoft.database.StoreQtyHitching;
import com.grsoft.dataobjects.OrderFulfillment;
import com.grsoft.dataobjects.discount.ClientCard;
import com.grsoft.dataobjects.discount.Discount;
import com.grsoft.dataobjects.discount.DiscountPriority;
import com.grsoft.dataobjects.discount.DiscountTree;
import com.grsoft.dataobjects.discount.OrgDiscount;
import com.grsoft.dataobjects.discount.OrgPrice;
import com.grsoft.dataobjects.discount.StoreDiscount;
import com.grsoft.network.exception.RuntimeException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.FolderTree;

public class UpdateDBEx extends UpdateDB{
    boolean rcvFolders = false;

    @Override
    protected DeliveryHitching getDeliveryHitching() {
        return new DeliveryHitching("AgentDelivery");
    }

    @Override
    protected String agentConfigName() {
        return "AgentConfig";
    }

    @Override
    protected List<Hitching> getGenDataHitchings() throws RuntimeException {
        List<Hitching> res = super.getGenDataHitchings();
        res.add(new PriceTypeHitching());
        res.add(new PriceCostHitching());
        res.add(new StoreHitching());
        res.add(new StoreQtyHitching());

        res.add(new RcvNewHitching(DiscountTree.class));
        res.add(new RcvNewHitching(DiscountPriority.class));
        res.add(new RcvNewHitching(OrgPrice.class));
        res.add(new RcvNewHitching(Discount.class));
        res.add(new RcvNewHitching(ClientCard.class));
        res.add(new RcvNewHitching(OrgDiscount.class));
        res.add(new RcvNewHitching(StoreDiscount.class));

        res.add(new Hitching(OrderFulfillment.class));
        res.add(new AgentPlanRcv());

        CostStrategyEx.resetCache();
        rcvFolders = true;
        return res;
    }

    @Override
    protected void postSync(Boolean result) {
        super.postSync(result);
        DbWriter.checkDBTable(StoreDiscount.class);
        DbWriter.checkDBTable(OrgDiscount.class);
    }
//
//    private void compactFolders() {
//        Set<String> folders = new HashSet<>();
//        FolderTree ft = new FolderTree();
//        ft.load();
//
//        for (PriceEx p : DbReader.fetch(PriceEx.class)) {
//            if(!folders.contains(p.fid)) {
//                for(Folder f : ft.getWithDescendats(p.fid)) {
//                    folders.add(f.fid);
//                }
//            }
//        }
//
//        try {
//            String ids = "";
//            for(String id : folders) {
//                if(ids.length() > 0) ids += ",";
//                ids += "'" + id + "'";
//            }
//            String stmt = "DELETE FROM " + new Folder().getTableName() + " WHERE not fid in (" + ids + ")";
//            DataBaseManager.getDataBase().execSQL(stmt);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
