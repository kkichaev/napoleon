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
      SimpleDataSet<GoodsAudit> dsAudit = new SimpleDataSet<GoodsAudit>(GoodsAudit.OBJECT_NAME, false);
      SimpleDataSet<ActiveOrgActions> dsActions = new SimpleDataSet<ActiveOrgActions>(ActiveOrgActions.OBJECT_NAME, false);

      DataSet<string, OrgActions> dsOrgActions;
      DataSet<string, ActionCategory> dsCategory;
      DataSet<string, Goods> dsGoods;

      DataGridView dgvOrgActions = new DataGridView();
      DataGridView dgvAudit = new DataGridView();

      DataGridViewTextBoxColumn clmnAction = new DataGridViewTextBoxColumn();
      DataGridViewTextBoxColumn clmnGoods = new DataGridViewTextBoxColumn();
      DataGridViewTextBoxColumn clmnShelfOur = new DataGridViewTextBoxColumn();
      DataGridViewTextBoxColumn clmnShelfAll = new DataGridViewTextBoxColumn();
      DataGridViewTextBoxColumn clmnScuAll = new DataGridViewTextBoxColumn();
      DataGridViewTextBoxColumn clmnScuOur = new DataGridViewTextBoxColumn();

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         documents.Add(new DocumentInfo(dsAudit, ObjType.TObjType.Merch));
         documents.Add(new DocumentInfo(dsActions, ObjType.TObjType.Actions));

         dsOrgActions = (DataSet<string, OrgActions>)DataModule.Get(OrgActions.OBJECT_NAME) ??
            new DataSet<string, OrgActions>(OrgActions.OBJECT_NAME);

         dsCategory = (DataSet<string, ActionCategory>)DataModule.Get(ActionCategory.OBJECT_NAME) ??
            new DataSet<string, ActionCategory>(ActionCategory.OBJECT_NAME);

         dsGoods = (DataSet<string, Goods>)DataModule.Get(Goods.OBJECT_NAME) ??
            new DataSet<string, Goods>(Goods.OBJECT_NAME);

         Init();
      }

      void InitDGV(DataGridView dgv, String name, DataGridViewColumn[] clmns)
      {
         dgv.Visible = false;
         ((System.ComponentModel.ISupportInitialize)dgv).BeginInit();

         dgv.AllowUserToAddRows = false;
         dgv.AllowUserToDeleteRows = false;
         dgv.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         dgv.Dock = System.Windows.Forms.DockStyle.Fill;
         dgv.Location = new System.Drawing.Point(0, 0);
         dgv.Margin = new System.Windows.Forms.Padding(3, 4, 3, 4);
         dgv.Name = name;
         dgv.RowHeadersVisible = false;
         dgv.Size = new System.Drawing.Size(611, 187);

         dgv.Columns.AddRange(clmns);

         ((System.ComponentModel.ISupportInitialize)dgv).EndInit();

         detailPanel.Controls.Add(dgv);
      }

      void Init()
      {
         InitDGV(dgvOrgActions, "dgvOrgActions", new DataGridViewColumn[] { clmnAction });
         InitDGV(dgvAudit, "dgvAudit", new DataGridViewColumn[] { clmnGoods, clmnShelfAll, clmnShelfOur, clmnScuAll, clmnScuOur });

         clmnAction.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnAction.DataPropertyName = "Action";
         clmnAction.HeaderText = "Акция";
         clmnAction.Name = "clmnGoods";

         clmnGoods.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnGoods.DataPropertyName = "Goods";
         clmnGoods.HeaderText = "Группа";
         clmnGoods.Name = "clmnGoods";
         clmnGoods.Width = 300;

         clmnShelfOur.DataPropertyName = "ShelfOur";
         clmnShelfOur.HeaderText = "Метров, свои";
         clmnShelfOur.Name = "clmnShelfOur";
         clmnShelfOur.Width = 75;

         clmnShelfAll.DataPropertyName = "ShelfAll";
         clmnShelfAll.HeaderText = "Метров, всего";
         clmnShelfAll.Width = 75;
         clmnShelfAll.Name = "clmnShelfAll";

         clmnScuOur.DataPropertyName = "ScuOur";
         clmnScuOur.HeaderText = "SCU, свои";
         clmnScuOur.Name = "clmnScuOur";
         clmnScuOur.Width = 75;

         clmnScuAll.DataPropertyName = "ScuAll";
         clmnScuAll.HeaderText = "SCU, всего";
         clmnScuAll.Name = "clmnScuAll";
         clmnScuAll.Width = 75;

         {
            ToolStripItem tsi = new ToolStripMenuItem();
            tsi.Name = "tsbActions";
            tsi.Size = new System.Drawing.Size(152, 22);
            tsi.Text = "Акции";
            tsi.Click += new EventHandler((o, e)=>ActionReport.Do(this));

            tsReportMenu.DropDownItems.Add(tsi);
         }

/*
         {
            ToolStripItem tsi = new ToolStripMenuItem();
            tsi.Name = "tsbMerch";
            tsi.Size = new System.Drawing.Size(152, 22);
            tsi.Text = "Мерчендайзинг";
            tsi.Click += new EventHandler((o, e) => MerchReport.Do());

            tsReportMenu.DropDownItems.Add(tsi);
         }
*/
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         if (dsOrgActions.Count == 0)
            updSets.Add(dsOrgActions);

         if (dsCategory.Count == 0)
            updSets.Add(dsCategory);

         if (dsGoods.Count == 0)
            updSets.Add(dsGoods);

         dsAudit.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         updSets.Add(dsAudit);

         dsActions.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         updSets.Add(dsActions);

      }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         dgvOrgActions.Visible = false;
         dgvAudit.Visible = false;

         if (odr.Doctype.Val == ObjType.TObjType.Merch)
         {
            dgvAudit.Visible = true;
            dgvAudit.DataSource = ((GoodsAudit)odr.StoreObject).items;
            return dgvAudit;
         }

         if (odr.Doctype.Val == ObjType.TObjType.Actions)
         {
            dgvOrgActions.Visible = true;
            dgvOrgActions.DataSource = ((ActiveOrgActions)odr.StoreObject).items;
            return dgvOrgActions;
         }

         return null;
      }


      internal override OrdersDetail CreateOrderDetail()
      {
         return new ScriptDetail(documents);
      }
   }


   class GoodsAudit : BaseDocument
   {
      public static readonly String OBJECT_NAME = "GoodsAudit";

      [ItemType(typeof(Item))]
      public List<Item> items = null;


      public class Item : GRSoft.Network.DataObject
      {
         public String id = "";

         [Reference("Goods", "id")]
         public Goods goods = null;

         public double shelfOur = 0;
         public double shelfAll = 0;
         public double scuOur = 0;
         public double scuAll = 0;

         public String Goods { get { return goods == null ? "" : goods.name; } }

         public double ShelfOur { get { return shelfOur; } }
         public double ShelfAll { get { return shelfAll; } }

         public double ScuOur { get { return scuOur; } }
         public double ScuAll { get { return scuAll; } }
      }
   }

   class ActiveOrgActions : BaseDocument
   {
      public static readonly String OBJECT_NAME = "ActiveOrgActions";

      [ItemType(typeof(Item))]
      public List<Item> items = null;


      public class Item : GRSoft.Network.DataObject
      {
         public String id = "";

         [Reference("OrgActions", "id")]
         public OrgActions action = null;

         public string Action { get { return action == null ? "" : action.name; } }
      }
   }

   class OrgActions : GRSoft.Network.DataObject
   {
      public static readonly String OBJECT_NAME = "OrgActions";

      [KeyField]
      public string id = "";

      public string name = "";

      public DateTime start = DateTime.Now;
      public DateTime end = DateTime.Now;
   }

   public class ActionCategory : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "Category";

      public ActionCategory() { }

      public string Name { get { return name; } }

      [KeyField]
      public string id = string.Empty;
      public int level = 0;
      public string name = string.Empty;
      public ActionCategory parent = null;
   }

   public class Goods : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "Goods";

      public Goods() { }


      [KeyField]
      public string id = string.Empty;
      public string fid = string.Empty;
      public string name = string.Empty;
   }

}
