using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Threading;
using System.IO;
using System.Windows.Forms;
using System.Runtime.InteropServices;
using GRSoft.NapoleonManager.Properties;
using GRSoft.NapoleonManager.Utils;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd); 

      DataGridView dgvDistrib = new System.Windows.Forms.DataGridView();
      DataSet<string, DistributionMatrix> dsDistribMatrix = new DataSet<string, DistributionMatrix>(DistributionMatrix.OBJECT_NAME);

      SimpleDataSet<OrgDistribution> dsDistrib;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         dsDistrib = (SimpleDataSet<OrgDistribution>)DataModule.Get(OrgDistribution.OBJECT_NAME)
            ?? new SimpleDataSet<OrgDistribution>(OrgDistribution.OBJECT_NAME);

         dgvDistrib.AutoGenerateColumns = false;

         dgvDistrib.AllowUserToAddRows = false;
         dgvDistrib.AllowUserToDeleteRows = false;
         dgvDistrib.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         dgvDistrib.Dock = System.Windows.Forms.DockStyle.Fill;
         dgvDistrib.Location = new System.Drawing.Point(0, 0);
         dgvDistrib.Margin = new System.Windows.Forms.Padding(3, 4, 3, 4);
         dgvDistrib.Name = "dgvDistrib";
         dgvDistrib.RowHeadersVisible = false;
         dgvDistrib.Size = new System.Drawing.Size(611, 187);

         DataGridViewTextBoxColumn clmn = new System.Windows.Forms.DataGridViewTextBoxColumn();
         clmn.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmn.DataPropertyName = "Item";
         clmn.HeaderText = "Товар";
         clmn.Name = "dgvDistribItem";
         clmn.FillWeight = 80F;
         dgvDistrib.Columns.Add(clmn);

         clmn = new System.Windows.Forms.DataGridViewTextBoxColumn();
         clmn.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmn.DataPropertyName = "Present";
         clmn.HeaderText = "Наличие";
         clmn.Name = "dgvDistribPresent";
         clmn.FillWeight = 20F;
         dgvDistrib.Columns.Add(clmn);

         detailPanel.Controls.Add(dgvDistrib);

         ToolStripMenuItem item = new ToolStripMenuItem();
         item.Name = "tbOrgDistrib";
         item.Size = new System.Drawing.Size(189, 22);
         item.Text = "Дистрибуция";
         item.Visible = true;
         item.Click += new System.EventHandler((o,e)=>DistribReport.Do(this, GetSelectedAgent()));
         tsReportMenu.DropDownItems.Add(item);

         item = new ToolStripMenuItem();
         item.Name = "tbOrgRemnants";
         item.Size = new System.Drawing.Size(189, 22);
         item.Text = "Остатки";
         item.Visible = true;
         item.Click += new System.EventHandler((o, e) => RemnantsReport.Do(this, GetSelectedAgent()));

         tsReportMenu.DropDownItems.Add(item);
      }


      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);

         dsDistrib.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         
         updSets.Add(dsDistrib);
         updSets.Add(dsDistribMatrix);
      }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         dgvDistrib.Visible = false;
         if (odr.Doctype.Val == ObjType.TObjType.OrgDistrib)
         {
            DistributionMatrix distrMatrix = null;

            if (odr.NOrg != null && dsDistribMatrix.ContainsKey(odr.NOrg.id))
               distrMatrix = dsDistribMatrix[odr.NOrg.id];

            List<DistribItemEx> loi = new List<DistribItemEx>();
            List<String> orgDistr = new List<string>();

            foreach (OrgDistribution.DistribItem item in (odr.StoreObject as OrgDistribution).items)
               orgDistr.Add(item.id);

            if(distrMatrix != null && distrMatrix.items !=null)
               foreach(DistributionMatrix.Item i in distrMatrix.items)
                  loi.Add(new DistribItemEx(i, orgDistr.Contains(i.id)));

            loi.Sort(new Comparison<DistribItemEx>(delegate(DistribItemEx lhs, DistribItemEx rhs)
               {
                  int result = 0;
                  result = lhs.present.CompareTo(rhs.present) * -1;

                  if (result == 0)
                     result = lhs.Item.CompareTo(rhs.Item);

                  return result;
               }));

            dgvDistrib.DataSource = loi;
            dgvDistrib.Visible = true;
            return dgvDistrib;
         }

         return null;
      }

      internal override OrdersDetail CreateOrderDetail()
      {
         return new ScriptDetailEx(documents);
      }

      protected override FmDetail.DocView GetDocView(string docType)
      {
         if (docType.Equals(OrgDistribution.OBJECT_NAME))
            return new DocView(OrgDistribution.OBJECT_NAME, "Дистриб.", typeof(DistribOverview));
         return base.GetDocView(docType);
      }
   }

   internal class DistribItemEx : OrgDistribution.DistribItem
   {
      public DistribItemEx() { }
      public DistribItemEx(DistributionMatrix.Item item, bool present)
      {
         this.id = item.id;
         this.item = item.item;
         this.present = present;
      }

      public bool present = false;
      public string Present { get { return present ? "Да" : "Нет"; } }
   }

   class ScriptDetailEx : ScriptDetail
   {
      public ScriptDetailEx() : base() { }
      public ScriptDetailEx(List<DocumentInfo> documents) : base(documents) {}

      protected override void LoadInt(FmDetailData cond, bool oneDay, bool checkRoute, string agentID, List<Org> routes)
      {
         base.LoadInt(cond, oneDay, checkRoute, agentID, routes);

         if (!((FmDetail)cond.fmDetail).IsScriptMode)
         {
            IDataSet cdata = DataModule.Get(OrgDistribution.OBJECT_NAME);
            CheckFiltersForDocType(cdata, ObjType.TObjType.OrgDistrib, filtersAvailable);

            if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.OrgDistrib) : true && cdata != null)
            {
               foreach (OrgDistribution od in cdata.Data)
               {
                  if (checkRoute &&
                     FmDetailBase.IsCreatedBySelectedAgentRoute(od.org,
                     agentID, od.date))
                     continue;

                  docCount++;

                  Add(new OrderDetailRepresentation(od.date,
                     new ObjType(ObjType.TObjType.OrgDistrib),
                     od.date, od.sended, od.org, 0.0, 0, 0,
                     od, oneDay));
               }
            }
         }
      }
   }

   internal class OrgDistribution : BaseDocument
   {
      public static readonly string OBJECT_NAME = "OrgDistribution";

      internal class DistribItem : GRSoft.Network.DataObject
      {
         [Reference("ManagerPrice", "id", typeof(Price))]
         public Price item = null;
         public string id = "";

         public string Item { get { return item != null ? item.Name : "товар с кодом <" + id + ">"; } }
      }

      [ItemType(typeof(DistribItem))]
      public List<DistribItem> items = null;


      internal bool Contains(string id)
      {
         foreach (DistribItem i in items)
            if (i.id == id)
               return true;
         return false;
      }
   }

   internal class OrgDistributionDoc : ScriptDocument
   {
      internal OrgDistributionDoc()
         : base("OrgDistribution", "Дистриб.", Resources.distrib_doc)
      {
      }
   }
}
