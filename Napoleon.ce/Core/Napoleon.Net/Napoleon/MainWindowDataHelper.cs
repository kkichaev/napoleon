using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Napoleon
{
    class MainWindowDataHelper
    {
        public static string GetDocFilter(List<OrgFolderItem> items)
        {
            string filter = "";
            Dictionary<string, bool> usedids = new Dictionary<string, bool>();
            items.ForEach(x =>
            {
                if (usedids.ContainsKey(x.name) == false)
                {
                    usedids[x.name] = true;
                    filter += "'" + x.name + "',";
                }
            });

            return filter.Length > 0 ? filter.Substring(0, filter.Length - 1) : "";
        }
        public List<MainWindowData> CreateData(DateTime data, Update.UpdateResult res)
        {
            List<MainWindowData> result = new List<MainWindowData>();

            Dictionary<string, bool> usedids = new Dictionary<string, bool>();

            List<OrgFolderItem> items = new OrgFolderHelper().GetAgentRoute(data);
            string filter = GetDocFilter(items);
            items.Sort();

            Update.UpdateResult dlvRes = null;
            if (filter.Length > 0)
            {
                Update.QueryList upd = new Update.QueryList();
                upd.Add(LastDelivery.OBJECT_NAME, filter);
                upd.Add(LastOrder.OBJECT_NAME, filter);

                dlvRes = Update.UpdateWait(upd);
            }

            Dictionary<string, Agent> adic = res.GetDictionary<Agent>(Agent.OBJECT_NAME);
            Dictionary<string, string> uids = new Dictionary<string, string>();
            res.GetList<AgentOrgs>(AgentOrgs.OBJECT_NAME).ForEach(x => uids[x.id] = x.userid);

            Dictionary<string, PhoneAction> paDict = new Dictionary<string, PhoneAction>();
            res.GetList<PhoneAction>(PhoneAction.OBJECT_NAME).ForEach((p) => { paDict[p.id] = p; });

            string[] sc = new string[] { "", "В", "ВМ", "М", "Д", "ДМ", "m", "ВД", "З" };
            Dictionary<string, ServoluxSheduleItem> shd = res.GetDictionary<ServoluxSheduleItem>(ServoluxSheduleItem.OBJECT_NAME);


            Dictionary<string, MainWindowData> dicData = new Dictionary<string, MainWindowData>();

            foreach (OrgFolderItem i in items)
            {
                string remark = string.Empty;
                string cellTime = string.Empty;
                //string lastOrderSum = string.Empty;
                //double lastOrderSumD = 0.0;
                //string lastOrderWeight = string.Empty;
                //double lastOrderWeightD = 0.0;
                //string lastDlvSum = string.Empty;
                //string lastDlvWeight = string.Empty;
                //double lastDlvSumD = 0.0;
                //double lastDlvWeightD = 0.0;
                //string dateTTN = string.Empty;
                string text = string.Empty;

                if (paDict.ContainsKey(i.name))
                {
                    PhoneAction pa = paDict[i.name];
                    remark = pa.Remark;
                    cellTime = string.Format("{0:HH:mm}", pa.changes);
                    text = pa.text;
                }

                //if (orders.ContainsKey(i.name))
                //{
                //    Order order = orders[i.name];
                //    lastOrderSumD = order.Sum;
                //    lastOrderWeightD = order.Weight;
                //    lastOrderSum = lastOrderSumD.ToString();
                //    lastOrderWeight = lastOrderWeightD.ToString();
                //}

                //if (dlvs.ContainsKey(i.name))
                //{
                //    Delivery dlv = dlvs[i.name];
                //    lastDlvSumD = dlv.Sum;
                //    lastDlvWeightD = dlv.Weight;
                //    lastDlvSum = lastDlvSumD.ToString();
                //    lastDlvWeight = lastDlvWeightD.ToString();
                //    dateTTN = dlv.Created.ToShortDateString();
                //}

                string uid = "";
                if (uids.TryGetValue(i.org.id, out uid))
                {
                    i.org.userid = uid;
                    Agent a;
                    if (adic.TryGetValue(uid, out a))
                        i.org.agent = a;
                }


                MainWindowData d = new MainWindowData()
                {
                    OrgID = i.name,
                    OrgName = i.org.Name,
                    OrgAddress = i.org.Address,
                    TPCode = i.org.userid,
                    Remark = remark,
                    CellTime = cellTime,
                    //LastOrderSum = lastOrderSum,
                    //LastOrderWeight = lastOrderWeight,
                    //LastOrderSumD = lastOrderSumD,
                    //LastOrderWeightD = lastDlvWeightD,
                    //LastDlvSum = lastDlvSum,
                    //LastDlvSumD = lastDlvSumD,
                    //LastDlvWeight = lastDlvWeight,
                    //LastDlvWeightD = lastDlvWeightD,
                    //DateTTN = dateTTN,
                    Text = text,
                };

                dicData[i.name] = d;

                //if (shd.ContainsKey(i.name))
                //{
                //    ServoluxSheduleItem s = shd[i.name];

                //    d.Mon = sc[s.mon];
                //    d.Tue = sc[s.tue];
                //    d.Wed = sc[s.wed];
                //    d.Thu = sc[s.thu];
                //    d.Fri = sc[s.fri];
                //    d.Sat = sc[s.sat];
                //    d.Sun = sc[s.sun];
                //}

                result.Add(d);
            }

            if (dlvRes != null)
            {
                dlvRes.GetList<LastOrder>(LastOrder.OBJECT_NAME).ForEach((p) =>
                {
                    MainWindowData md;
                    if (dicData.TryGetValue(p.id, out md))
                        md.orders[p.firmCode] = p;
                });

                dlvRes.GetList<LastDelivery>(LastDelivery.OBJECT_NAME).ForEach((p) =>
                {
                    MainWindowData md;
                    if (dicData.TryGetValue(p.id, out md))
                        md.dlvs[p.firm] = p;
                });
            }

            return result;
        }
    }
}
