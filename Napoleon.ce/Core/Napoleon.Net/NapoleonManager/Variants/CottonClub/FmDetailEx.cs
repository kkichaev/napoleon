using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.Reflection;

namespace GRSoft.NapoleonManager
{ 
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetailEx : FmDetail
   {
      DataSet<int, CommonAudit> dsCommonAudit;
      DataSet<int, PromoAudit> dsPromoAudit;
      DataSet<string, OrdConfig> dsOrdConfig;

      DataGridView dgvDetailItems = new DataGridView();
      DataGridViewTextBoxColumn clmnName = new DataGridViewTextBoxColumn();

      DataGridViewCheckBoxColumn clmnPresents = new DataGridViewCheckBoxColumn();
      DataGridViewTextBoxColumn clmnPrice = new DataGridViewTextBoxColumn();
      DataGridViewTextBoxColumn clmnStock = new DataGridViewTextBoxColumn();
      DataGridViewTextBoxColumn clmnMerch = new DataGridViewTextBoxColumn();

      public static Dictionary<string, string> priceCfg = new Dictionary<string, string>();
      public static Dictionary<string, string> stockCfg = new Dictionary<string, string>();
      public static Dictionary<string, string> merchCfg = new Dictionary<string, string>();

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         Init();
         dsCommonAudit = (DataSet<int, CommonAudit>)DataModule.Get(CommonAudit.OBJECT_NAME) ?? 
            new DataSet<int, CommonAudit>(CommonAudit.OBJECT_NAME);
         dsPromoAudit = (DataSet<int, PromoAudit>)DataModule.Get(PromoAudit.OBJECT_NAME) ??
            new DataSet<int, PromoAudit>(PromoAudit.OBJECT_NAME);
         dsOrdConfig = (DataSet<string, OrdConfig>)DataModule.Get(OrdConfig.OBJECT_NAME) ??
            new DataSet<string, OrdConfig>(OrdConfig.OBJECT_NAME);

         documents.Add(new DocumentInfo(dsCommonAudit, ObjType.TObjType.CommonAudit));
         documents.Add(new DocumentInfo(dsPromoAudit, ObjType.TObjType.PromoAudit));
         tsClienCard.Visible = false;
      }

      private void Init()
      {
         dgvDetailItems.Visible = false;
         ((System.ComponentModel.ISupportInitialize)dgvDetailItems).BeginInit();

         dgvDetailItems.AllowUserToAddRows = false;
         dgvDetailItems.AllowUserToDeleteRows = false;
         dgvDetailItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         dgvDetailItems.Dock = System.Windows.Forms.DockStyle.Fill;
         dgvDetailItems.Location = new System.Drawing.Point(0, 0);
         dgvDetailItems.Margin = new System.Windows.Forms.Padding(3, 4, 3, 4);
         dgvDetailItems.Name = "dgvDetailItems";
         dgvDetailItems.RowHeadersVisible = false;
         dgvDetailItems.Size = new System.Drawing.Size(611, 187);

         dgvDetailItems.Columns.AddRange(new DataGridViewColumn[] {
            clmnName,
            clmnPresents,
            clmnPrice,
            clmnStock,
            clmnMerch
         });

         clmnName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnName.DataPropertyName = "Name";
         clmnName.FillWeight = 400F;
         clmnName.HeaderText = "Наименование";
         clmnName.Name = "Name";

         clmnPresents.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnPresents.DataPropertyName = "Presents";
         clmnPresents.HeaderText = "Наличие";
         clmnPresents.Name = "Presents";

         clmnPrice.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnPrice.DataPropertyName = "Price";
         clmnPrice.HeaderText = "Ценник";
         clmnPrice.Name = "Price";

         clmnStock.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnStock.DataPropertyName = "Stock";
         clmnStock.HeaderText = "Товарный запас";
         clmnStock.Name = "Stock";

         clmnMerch.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnMerch.DataPropertyName = "Merch";
         clmnMerch.HeaderText = "Мерчендайзинг";
         clmnMerch.Name = "Merch";

         ((System.ComponentModel.ISupportInitialize)dgvDetailItems).EndInit();

         detailPanel.Controls.Add(dgvDetailItems);

      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         string filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsCommonAudit.Filter = filter;
         dsPromoAudit.Filter = filter;

         updSets.Add(dsCommonAudit);
         updSets.Add(dsPromoAudit);
         updSets.Add(dsOrdConfig);
      }

      internal override OrdersDetail CreateOrderDetail()
      {
         return new OrderDetailEx(documents);
      }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         dgvDetailItems.Visible = false;
         if (odr.Doctype.Val == ObjType.TObjType.CommonAudit)
         {
            dgvDetailItems.Visible = true;
            CommonAudit ca = odr.StoreObject as CommonAudit;

            if (ca != null)
            {
               dgvDetailItems.DataSource = ca.items;
            }

            return dgvDetailItems;
         }
         else
            return base.RefreshDetail(odr);
      }

      protected override void AfterRefreshData()
      {
         fillCfgDic("ТипЦенника", priceCfg);
         fillCfgDic("ТипТоварногоЗапаса", stockCfg);
         fillCfgDic("Мерчендайзинг", merchCfg);
      }

      private void fillCfgDic(string key, Dictionary<string, string> dic)
      {
         dic.Clear();
         if (dsOrdConfig.ContainsKey(key))
         {
            string[] val = dsOrdConfig[key].value.Split(';');

            foreach (string s in val)
            {
               string[] sk = s.Split('\t');

               if (sk.Length >= 2)
                  dic.Add(sk[1], sk[0]);
            }
         }
      }
   }


   class OrderDetailEx : ScriptDetail
   {
      List<DocumentInfo> documents;
      public OrderDetailEx(List<DocumentInfo> documents)
      {
         this.documents = documents;
      }

      protected override void LoadInt(FmDetailData cond, bool oneDay, bool checkRoute, string agentID, List<Org> routes)
      {
         base.LoadInt(cond, oneDay, checkRoute, agentID, routes);
         foreach (DocumentInfo di in documents)
         {
            IDataSet cdata = di.DataSet;
            CheckFiltersForDocType(cdata, di.Type, filtersAvailable);
            if (cdata != null && cond.OrderType != null ? cond.OrderType.Equals(di.Type) : true)
            {
               foreach (BaseDocument doc in cdata.Data)
                  Add(new OrderDetailRepresentation(doc, new ObjType(di.Type), oneDay));
            }
         }
      }

   }

   class CommonAudit : BaseDocument
   {
      public static readonly string OBJECT_NAME = "CommonAudit";

      [ItemType(typeof(CommonAuditItem))]
      public List<CommonAuditItem> items = null;
   }

   public class CommonAuditItem : Network.DataObject
   {
      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price item = null;

      public string id = string.Empty;
      public int presents = 0;
      public string stock = string.Empty;
      /*ценник*/
      public string price = string.Empty;
      public string merch = string.Empty;

      public string Name { get { return item != null ? item.name : id; } }
      public bool Presents { get { return presents > 0; } }
      public String Price
      {
         get
         {
            if (FmDetailEx.priceCfg.ContainsKey(price))
               return FmDetailEx.priceCfg[price];

            return price;
         }
      }

      public String Stock
      {
         get
         {
            if (FmDetailEx.stockCfg.ContainsKey(stock))
               return FmDetailEx.priceCfg[stock];

            return stock;
         }
      }

      public String Merch
      {
         get
         {
            if (FmDetailEx.merchCfg.ContainsKey(merch))
               return FmDetailEx.merchCfg[merch];

            return merch;
         }
      }
   }

   class PromoAudit : BaseDocument
   {
      public static readonly string OBJECT_NAME = "PromoAudit";
   }

   class OrdConfig : Network.DataObject
   {
      public static readonly string OBJECT_NAME = "Config";
      [KeyField]
      public string key = string.Empty;
      public string value = string.Empty;
   }
}
