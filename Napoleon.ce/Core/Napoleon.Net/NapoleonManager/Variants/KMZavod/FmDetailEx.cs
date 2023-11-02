using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmDetailEx : FmDetail
   {
      public FmDetailEx(FmDetailData data)
         : base(data)
      {
      }

      protected override void SetScriptInfo(ScriptDoc sd)
      {
         base.SetScriptInfo(sd);
         ScriptRes res = new ScriptRes();
         res.Dock = DockStyle.Fill;

         TabPage tp = new TabPage("Общая");
         tp.Controls.Add(res);

         Dictionary<string, Data> dic = new Dictionary<string, Data>();

         foreach (ScriptDocItem i in sd.items)
         {
            OrgRemnants rem = i.Document as OrgRemnants;

            if (rem != null)
            {
               foreach (OrgRemnantsItem p in rem.items)
               {
                  if (!dic.ContainsKey(p.id))
                     dic[p.id] = new Data(p.Item);

                  Data d = dic[p.id];
                  d.Rem = p.qty.ToString();
               }
            }

            Returns ret = i.Document as Returns;

            if (ret != null)
            {
               foreach (ReturnItem p in ret.items)
               {
                  if (!dic.ContainsKey(p.id))
                     dic[p.id] = new Data(p.Item);

                  Data d = dic[p.id];
                  d.Ret = p.qty.ToString();
               }
            }

            Order ord = i.Document as Order;

            if (ord != null)
            {
               foreach (OrderItem p in ord.items)
               {
                  if (!dic.ContainsKey(p.id))
                     dic[p.id] = new Data(p.Item);

                  Data d = dic[p.id];
                  d.Ord = p.qty.ToString();
                  d.Auto = p.aqty.ToString();
                  d.Res = Math.Abs(p.qty - p.aqty).ToString();
               }
            }
         }

         List<Data> list = new List<Data>(dic.Values);
         list.Sort((x, y) => { return x.Name.CompareTo(y.Name); });
         res.setData(list);

         scriptDetail.TabPages.Add(tp);
      }

      public class Data
      {
         public Data(string name)
         {
            this.Name = name;
         }

         public string Name { get; set; }
         public string Rem { get; set; }
         public string Ret { get; set; }
         public string Ord { get; set; }
         public string Auto { get; set; }
         public string Res { get; set; }
      }
   }
}
