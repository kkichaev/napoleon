using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.Reflection;
using System.Collections;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
  public class FmDetailEx : FmDetail
   {
      SimpleDataSet<Rko> dsRko = new SimpleDataSet<Rko>(Rko.OBJECT_NAME);
      SimpleDataSet<Movement> dsMovement = new SimpleDataSet<Movement>(Movement.OBJECT_NAME);
      DataSet<string, OrdConfig> dsOrdConfig;

      MovementDetail mvDetail = new MovementDetail();
      
      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         Init();

         documents.Add(new DocumentInfo(dsRko, ObjType.TObjType.Rko));
         documents.Add(new DocumentInfo(dsMovement, ObjType.TObjType.Move));
      }

      void Init()
      {
         mvDetail.Dock = DockStyle.Fill;
         mvDetail.Location = new System.Drawing.Point(0, 0);
         mvDetail.Size = new System.Drawing.Size(611, 187);
         mvDetail.Visible = false;
         detailPanel.Controls.Add(mvDetail);
         mvDetail.SendToBack();

         dsOrdConfig = (DataSet<string, OrdConfig>)DataModule.Get(OrdConfig.OBJECT_NAME) ??
            new DataSet<string, OrdConfig>(OrdConfig.OBJECT_NAME);
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         dsRko.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         updSets.Add(dsRko);
         dsMovement.Filter = dsRko.Filter;
         updSets.Add(dsMovement);
         updSets.Add(dsOrdConfig);
      }


      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         mvDetail.Visible = false;
         switch(odr.Doctype.Val)
         {
            case ObjType.TObjType.Rko:
               {
                  Rko doc = odr.StoreObject as Rko;
                  StringBuilder str = new StringBuilder(doc.date.ToShortDateString());
                  str.Append("\t");
                  str.Append(doc.Sum().ToString("C", Config.GetCultureInfo()));
                  tbVisitText.Text = str.ToString();
                  return tbVisitText;
               }
            case ObjType.TObjType.Move:
               {
                  Movement doc = odr.StoreObject as Movement;
                  String cfgWh = "";
                  if (dsOrdConfig.ContainsKey("Склады"))
                     cfgWh = dsOrdConfig["Склады"].value;
                  mvDetail.SetMovement(doc, cfgWh);
                  mvDetail.Visible = true;
                  return mvDetail;
               }
         }

         return null; 
      }

      internal override OrdersDetail CreateOrderDetail()
      {
         return new ScriptDetail(documents);
      }
   }

   class Movement : BaseDocument
   {
      public static readonly String OBJECT_NAME = "MovementWh";

      public string whSrc = "";
      public string whDest = "";

      [ItemType(typeof(Item))]
      public List<Item> items = null;

      public class Item : GRSoft.Network.DataObject
      {
         [Reference("ManagerPrice", "id", typeof(Price))]
         public Price item = null;
         public string id = "";
         public int qty = 0;

         public int Qty { get { return qty; } }
         public string Price { get { return item == null ? "Товар с кодом '<" + id + ">'" : item.Name; } }
      }

      internal override Org Org { get { return Org.Empty; } }
   }

   class Rko : BaseDocument
   {
      public static readonly String OBJECT_NAME = "Rko";

      public string number = "";
      public double sum = 0;

      public string AgentID { get { return agent == null ? string.Empty : agent.id; } }
      public string OrgName { get { return org == null ? string.Empty : org.name; } }
      public string OrgAddr { get { return org == null ? string.Empty : org.address; } }

      public override double Sum() { return sum; }

      internal override Org Org { get { return Org.Empty; } }
   }

   class OrdConfig : Network.DataObject
   {
      public static readonly string OBJECT_NAME = "Config";
      [KeyField]
      public string key = string.Empty;
      public string value = string.Empty;
   }
}
