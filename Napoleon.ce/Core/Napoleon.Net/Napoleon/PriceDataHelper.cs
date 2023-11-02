using System.Collections.Generic;

namespace Napoleon
{
    class PriceDataHelper
    {
        public List<DataNode> CreateReturnPrice(string id, List<NotExpiredItems> items)
        {
            Dictionary<string, Firms> firms = Update.GetStoredDictionary<Firms>(Firms.OBJECT_NAME);
            Dictionary<string, Folder> folders = Update.GetStoredDictionary<Folder>(Folder.OBJECT_NAME);
            Dictionary<string, Price> price = Update.GetStoredDictionary<Price>(Price.OBJECT_NAME);

            Dictionary<Folder, FolderNode> fdata = new Dictionary<Folder, FolderNode>();               
            foreach(NotExpiredItems i in items)
            {
                Price p;
                Folder f;
                if (!price.TryGetValue(i.id, out p))
                    continue;

                if (!folders.TryGetValue(p.fid, out f))
                    continue;

                FolderNode fn;
                if(!fdata.TryGetValue(f, out fn))
                {
                    fn = new FolderNode(f);
                    fdata.Add(f, fn);
                }
                PriceNode pnode = new PriceNode(p, 0);
                pnode.Cost = i.sum / i.qty;
                pnode.DocNumberInt = i.number;
                pnode.DocDate = i.date;
                pnode.DlvQty = i.qty;
                pnode.DocNumber = i.number + " / " + i.date.ToShortDateString();
                pnode.Expired = i.expired;
                pnode.Party = i.party;
                pnode.InPack = 1;

                Firms firm;
                if (firms.TryGetValue(i.firm, out firm))
                {
                    pnode.FirmN = firm;
                }
                else
                {
                    continue;
                }

                fn.Items.Add(pnode);
            }

            List<FolderNode> outF = new List<FolderNode>(fdata.Values);
            outF.Sort();
            List<DataNode> ret = new List<DataNode>();

            foreach(FolderNode fn in outF)
            {
                ret.Add(fn);
                fn.Items.Sort();
                foreach (DataNode ch in fn.Items)
                    ret.Add(ch);
            }

            return ret;
        }

        public List<DataNode> CreatePrice(Org org, Dictionary<string, List<string>> mtx, Dictionary<string, PlanNew> plans, 
            Dictionary<string, bool> mml, Dictionary<string, bool> mustbe, Dictionary<string, PriceActionData> actions)
        {
            string id = org.id;
            int costype = org.costype;

            //LastDelivery dlv = null;
            //LastRemnant rmn = null;

            //Update.GetStoredList<LastDelivery>(LastDelivery.OBJECT_NAME).ForEachFilter((d) => dlv = d, (d) => d.id == id);
            //Update.GetStoredList<LastRemnant>(LastRemnant.OBJECT_NAME).ForEachFilter((d) => rmn = d, (d) => d.id == id);

            //Dictionary<string, DeliveryItem> dlvItems = new Dictionary<string, DeliveryItem>();
            //dlv?.items.ForEach((i) => dlvItems[i.id] = i);

            //Dictionary<string, OrgRemnantsItem> rmnItems = new Dictionary<string, OrgRemnantsItem>();
            //rmn?.items.ForEach((i) => rmnItems[i.id] = i);

            Dictionary<string, Folder> folders = Update.GetStoredDictionary<Folder>(Folder.OBJECT_NAME);
            Dictionary<string, Price> price = Update.GetStoredDictionary<Price>(Price.OBJECT_NAME);
            Dictionary<string, Firms> firms = Update.GetStoredDictionary<Firms>(Firms.OBJECT_NAME);
            Dictionary<string, Brands> brands = Update.GetStoredDictionary<Brands>(Brands.OBJECT_NAME);

            Dictionary<string, FolderNode> fnodes = new Dictionary<string, FolderNode>();

            foreach (KeyValuePair<string, List<string>> kv in mtx)
            {
                Firms firma;
                if (!firms.TryGetValue(kv.Key, out firma))
                {
                    continue;
                    //firma = new Firms();
                    //firma.id = kv.Key;
                    //firma.name = kv.Key;
                    //firms[kv.Key] = firma;
                }

                PlanNew plan;
                if (!plans.TryGetValue(kv.Key, out plan))
                    plan = new PlanNew();
                Dictionary<string, PlanNew.Item> plnItems = new Dictionary<string, PlanNew.Item>();
                foreach (PlanNew.Item pli in plan.items)
                    plnItems[pli.id] = pli;

                foreach (string pid in kv.Value)
                {
                    Price p;
                    if (price.TryGetValue(pid, out p))
                    {
                        PriceActionData action = null;
                        actions.TryGetValue(pid, out action);
                        PriceNode pn = new PriceNode(p, costype, action);

                        Brands b;
                        if (!brands.TryGetValue(p.idBrand, out b))
                        {
                            b = new Brands();
                            b.id = p.idBrand;
                            b.name = p.idBrand;
                            brands[p.idBrand] = b;
                        }

                        pn.FirmN = firma;
                        pn.BrandN = b;
                        pn.PackName = p.packName;

                        string prefix = "";
                        if (mustbe.ContainsKey(p.id)) prefix = "!" + prefix;
                        if (mml.ContainsKey(p.id)) prefix += "M";
                        pn.Prefix = prefix;

                        //DeliveryItem di;
                        //if (dlvItems.TryGetValue(p.id, out di))
                        //{
                        //    pn.SellD = di.qty;
                        //    pn.Sell = pn.SellD.ToString();
                        //}

                        //OrgRemnantsItem ori;
                        //if (rmnItems.TryGetValue(p.id, out ori))
                        //{
                        //    pn.RemnantsD = ori.qty;
                        //    pn.Remnants = pn.RemnantsD.ToString();
                        //}

                        PlanNew.Item plItem;
                        if (plnItems.TryGetValue(p.id, out plItem))
                        {
                            pn.PlanD = plItem.qty;
                            pn.Plan = pn.PlanD.ToString();
                        }



                        FolderNode fn;
                        if (!fnodes.TryGetValue(p.fid, out fn))
                        {
                            Folder f;
                            if (!folders.TryGetValue(p.fid, out f))
                            {
                                f = new Folder();
                                f.name = p.fid;
                                f.fid = p.fid;
                                folders[p.fid] = f;
                            }
                            fn = new FolderNode(f);
                            fnodes[p.fid] = fn;
                        }
                        fn.Items.Add(pn);
                    }
                }
            }

            List<FolderNode> tfld = new List<FolderNode>(fnodes.Values);
            tfld.Sort();

            List<DataNode> result = new List<DataNode>();
            foreach(FolderNode fn in tfld)
            {
                result.Add(fn);
                fn.Items.Sort();
                foreach (DataNode dn in fn.Items)
                    result.Add(dn);
            }
            return result;
        }
    }
}
