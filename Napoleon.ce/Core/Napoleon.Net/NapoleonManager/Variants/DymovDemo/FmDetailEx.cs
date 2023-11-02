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
   public class FmDetailEx : FmDetail
   {
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd); 

      static int count = 1;

      DataGridView dgvDistrib = new System.Windows.Forms.DataGridView();
      DataSet<string, DistributionMatrix> dsDistribMatrix = new DataSet<string, DistributionMatrix>(DistributionMatrix.OBJECT_NAME);

      SimpleDataSet<Bonus> dsBonus;
      SimpleDataSet<OrgDistribution> dsDistrib;
      DataSet<int, OrgPlan> dsOrgPlan;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         dsBonus = (SimpleDataSet<Bonus>)DataModule.Get(Bonus.BONUS_NAME)
            ?? new SimpleDataSet<Bonus>(Bonus.BONUS_NAME);

         dsDistrib = (SimpleDataSet<OrgDistribution>)DataModule.Get(OrgDistribution.OBJECT_NAME)
            ?? new SimpleDataSet<OrgDistribution>(OrgDistribution.OBJECT_NAME);

         //documents.Add(new DocumentInfo(dsBonus, ObjType.TObjType.Bonus));
         //documents.Add(new DocumentInfo(dsDistrib, ObjType.TObjType.OrgDistrib));

         DataGridViewTextBoxColumn clmn = new System.Windows.Forms.DataGridViewTextBoxColumn();
         clmn.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         clmn.DataPropertyName = "Unit";
         clmn.HeaderText = "Ед.изм";
         clmn.Name = "dgvOrderItemsUnit";
         dgvOrderItems.Columns.Add(clmn);

         clmn = new System.Windows.Forms.DataGridViewTextBoxColumn();
         clmn.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         clmn.DataPropertyName = "Weight";
         clmn.HeaderText = "Вес";
         clmn.Name = "dgvOrderItemsWeight";
         clmn.Width = 60;
         clmn.DefaultCellStyle.Format = "N2";
         dgvOrderItems.Columns.Add(clmn);

         clmn = new System.Windows.Forms.DataGridViewTextBoxColumn();
         clmn.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         clmn.DataPropertyName = "Sum";
         clmn.HeaderText = "Сумма";
         clmn.Name = "dgvOrderItemsSum";
         clmn.DefaultCellStyle.Format = "N2";
         dgvOrderItems.Columns.Add(clmn);

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

         clmn = new System.Windows.Forms.DataGridViewTextBoxColumn();
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

         dsOrgPlan = (DataSet<int, OrgPlan>)DataModule.Get(OrgPlan.OBJECT_NAME) ?? new DataSet<int, OrgPlan>(OrgPlan.OBJECT_NAME);
      }

      public class Data : GRSoft.Network.DataObject
      {
         public string userid = "";
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
      }

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;
      }

      protected override void DoClientCardReport()
      {
         const string REPORT_NAME = "clientcard";

         Data data = new Data();
         data.userid = GetSelectedIdAgent();
         data.start = dtpBegin.Value.Date;
         data.finish = dtpEnd.Value.Date;

         Result result = new Result();
         SimpleDataSet<Result> resultSet = new SimpleDataSet<Result>("Result", false);
         Report r = new Report(REPORT_NAME, data, resultSet);

         Thread th = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), r, FmWait.ProgressIndicator);
         FmWait.ShowForm(this, th);
         th.Join();
         FmWait.CloseForm();

         if (resultSet.Count > 0)
         {
            Result res = resultSet[0];
            if (res.file.Length > 0)
            {
               string fileName = Path.GetTempPath() + "\\" + REPORT_NAME + count.ToString() + ".xlsx";
               while (File.Exists(fileName))
               {
                  count++;
                  fileName = Path.GetTempPath() + "\\" + REPORT_NAME + count.ToString() + ".xlsx";
               }
               File.WriteAllBytes(fileName, res.file);
               ShellExecute(IntPtr.Zero, "open", fileName, "", "", 1);
            }
         }
         else
            MessageBox.Show("Ошибка построения отчета");

      }

      private string updateAgentId = string.Empty;

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);

         dsBonus.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsDistrib.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         
         SimpleDataSet<Order> orders = DataModule.Get("LoadedOrders") as SimpleDataSet<Order>;
         orders.KeepData = false;
         orders.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agentID), orders.Name);

         dsOrgPlan.Filter = String.Format("\"userid\"='{0}' and \"start\" <= ToDate('{1:dd/MM/yyyy}') "+
            "and \"finish\" >= ToDate('{2:dd/MM/yyyy 23:59:59}')", agentID, dateBegin, dateEnd);
         updSets.Add(dsBonus);
         updSets.Add(dsDistrib);
         updSets.Add(dsDistribMatrix);
         updSets.Add(dsOrgPlan);
         updSets.Add(orders);

         updateAgentId = agentID;
      }

      protected override void AfterRefreshData()
      {
         DateTime dtEndDate = dtpEnd.Value.Date.AddDays(1);
         Dictionary<string, bool> orgs = new Dictionary<string, bool>();
         foreach (Org o in dsOrg.Data)
            orgs[o.id] = true;


         SimpleDataSet<Order> orders = DataModule.Get("LoadedOrders") as SimpleDataSet<Order>;

         foreach (Order o in orders.Data)
         {
            if (orgs.ContainsKey(o.id) && o.created >= dtpBegin.Value.Date && o.created < dtEndDate.Date)
            {
               o.loadedFromKIS = 1;
               dsOrder.Add(dsOrder.Count, o);
            }
         }
      }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         dgvDistrib.Visible = false;
         if (odr.Doctype.Val == ObjType.TObjType.Bonus)
         {
            List<OrderItem> loi = new List<OrderItem>();
            loi.AddRange((odr.StoreObject as Order).items);
            dgvOrderItems.DataSource = loi;
            return dgvOrderItems;
         }

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

      protected override void OpenVisitReport()
      {
         Dictionary<DateTime, KeyValuePair<string, double>> planorgs = new Dictionary<DateTime, KeyValuePair<string, double>>();

         foreach (OrgPlan p in dsOrgPlan.Values)
         {
            KeyValuePair<string, double> val = new KeyValuePair<string, double>(p.id, p.value);
            planorgs[p.start] = val;
         }
            
         Dictionary<string, int> viscnt = new Dictionary<string, int>();
         DataSet<int, CommonConfig> configs = (DataSet<int, CommonConfig>)DataModule.Get(CommonConfig.OBJECT_NAME);

         SummaryData sd = new SummaryData(GetSelectedAgent(), configs);
         DateTime begin = new DateTime(dtpBegin.Value.Year, dtpBegin.Value.Month, 1);
         DateTime end = begin.AddDays(DateTime.DaysInMonth(dtpBegin.Value.Year, dtpBegin.Value.Month));
         
         DataSet<int, OrgFolder> routes = (DataSet<int, OrgFolder>)DataModule.Get(OrgFolder.OBJECT_NAME);

         while (begin.Date < end.Date)
         {
            WeekDay weekDay = new WeekDay(begin.DayOfWeek);

            List<OrgFolderItem> items = sd.GetAgentRoute(begin, routes.Data);
            if (items != null)
            {
               foreach (OrgFolderItem item in items)
               {
                  if (!viscnt.ContainsKey(item.name))
                     viscnt[item.name] = 1;
                  else
                     viscnt[item.name] += 1;
               }
            }

            begin = begin.AddDays(1);
         }

         Dictionary<DateTime, Dictionary<string, double>> planPerVisit = new Dictionary<DateTime, Dictionary<string, double>>();

         foreach (KeyValuePair<DateTime, KeyValuePair<string, double>> kv in planorgs)
         {
            double v = viscnt.ContainsKey(kv.Value.Key) ? kv.Value.Value / viscnt[kv.Value.Key] : 0;

            if(!planPerVisit.ContainsKey(kv.Key))
               planPerVisit[kv.Key] = new Dictionary<string,double>();

            planPerVisit[kv.Key].Add(kv.Value.Key, v);
         }

         Type rptType = FormEntries.GetFormType(typeof(HtmlReport));
         ConstructorInfo ci = rptType.GetConstructor(Type.EmptyTypes);
         HtmlReportEx htmlReport = (HtmlReportEx)ci.Invoke(new object[] { });
         htmlReport.planPerVisit = planPerVisit;

         OpenLink.NewWindow(String.Format("\"{0}\"", htmlReport.makeDetailsFileInfo(dgvDetail,
               new TimeInterval(dtpBegin.Value, dtpEnd.Value), (cbAgents.Items[cbAgents.SelectedIndex] as AgentItem))));
      }

      internal override OrdersDetail CreateOrderDetail()
      {
         return new ScriptDetailEx(documents);
      }

      protected override FmDetail.DocView GetDocView(string docType)
      {
         if (docType.Equals(Bonus.BONUS_NAME))
            return new DocView(Bonus.BONUS_NAME, "Дегустация", typeof(OrderOverview));
         else if (docType.Equals(OrgDistribution.OBJECT_NAME))
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
            IDataSet cdata = DataModule.Get(Bonus.BONUS_NAME);
            CheckFiltersForDocType(cdata, ObjType.TObjType.Bonus, filtersAvailable);

            if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.Bonus) : true && cdata != null)
            {
               foreach (Bonus bonus in cdata.Data)
               {
                  if (checkRoute &&
                     FmDetailBase.IsCreatedBySelectedAgentRoute(bonus.org,
                     agentID, bonus.date))
                     continue;

                  docCount++;

                  Add(new OrderDetailRepresentation(bonus.date,
                     new ObjType(ObjType.TObjType.Bonus),
                     bonus.date, bonus.sended, bonus.org, 0.0, 0, 0,
                     bonus, oneDay));
               }
            }

            cdata = DataModule.Get(OrgDistribution.OBJECT_NAME);
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

   class Bonus : Order
   {
      public static readonly string BONUS_NAME = "Bonus";
   }

   internal class BonusDoc : ScriptDocument
   {
      internal BonusDoc()
         : base("Bonus", "Дегустация", Resources.bonus_doc)
      {
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


   class OrderOverviewEx : OrderOverview
   {
      public OrderOverviewEx()
      {
         DataGridViewTextBoxColumn clmn = new System.Windows.Forms.DataGridViewTextBoxColumn();
         clmn.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         clmn.DataPropertyName = "Unit";
         clmn.HeaderText = "Ед.изм";
         clmn.Name = "dgvOrderItemsUnit";
         dgvItems.Columns.Add(clmn);

         clmn = new System.Windows.Forms.DataGridViewTextBoxColumn();
         clmn.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         clmn.DataPropertyName = "Weight";
         clmn.HeaderText = "Вес";
         clmn.Name = "dgvOrderItemsWeight";
         clmn.Width = 60;
         clmn.DefaultCellStyle.Format = "N2";
         dgvItems.Columns.Add(clmn);

         clmn = new System.Windows.Forms.DataGridViewTextBoxColumn();
         clmn.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         clmn.DataPropertyName = "Sum";
         clmn.HeaderText = "Сумма";
         clmn.Name = "dgvOrderItemsSum";
         clmn.DefaultCellStyle.Format = "N2";
         dgvItems.Columns.Add(clmn);
      }
   }
}
