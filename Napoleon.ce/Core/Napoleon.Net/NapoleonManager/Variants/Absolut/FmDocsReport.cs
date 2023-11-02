using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmDocsReport : Form
   {
      private static readonly string COMMA = ",";
      private static readonly string UPPER_COMMA = "'";

      private SimpleDataSet<Org> dsOrg = new SimpleDataSet<Org>(Org.OBJECT_NAME, false);
      private System.Windows.Forms.Timer srchTimer = new System.Windows.Forms.Timer();
      private Dictionary<string, OrgSel> doclist = new Dictionary<string, OrgSel>();
      private IDataSet[] datasets;
      private DataSet<int, OrgFolder> dsOrgFolder;

      public FmDocsReport()
      {
         InitializeComponent();
         period.Start = DateTime.Now;
         period.Finish = DateTime.Now;
         srchTimer.Interval = 500;
         srchTimer.Tick += DocSearch;
         grid.AutoGenerateColumns = false;

         datasets = new IDataSet[]
         {
            (DataSet<int, Order>) DataModule.Get(Order.OBJECT_NAME) ?? new DataSet<int, Order>(Order.OBJECT_NAME),
            (DataSet<int, VisitInfo>) DataModule.Get(VisitInfo.V_OBJECT_NAME) ?? new DataSet<int, VisitInfo>(VisitInfo.V_OBJECT_NAME),
            (DataSet<int, OrgRemnants>) DataModule.Get(OrgRemnants.OBJECT_NAME) ?? new DataSet<int, OrgRemnants>(OrgRemnants.OBJECT_NAME),
            (DataSet<int, Incass>)DataModule.Get(Incass.OBJECT_NAME) ?? new DataSet<int, Incass>(Incass.OBJECT_NAME),
            (DataSet<int, TaskDone>)DataModule.Get(TaskDone.OBJECT_NAME) ?? new DataSet<int, TaskDone>(TaskDone.OBJECT_NAME),
            (DataSet<int, Answer>)DataModule.Get(Answer.OBJECT_NAME) ?? new DataSet<int, Answer>(Answer.OBJECT_NAME),
            (DataSet<int, Sales>)DataModule.Get(Sales.OBJECT_NAME) ?? new DataSet<int, Sales>(Sales.OBJECT_NAME)
         };

         dsOrgFolder = (DataSet<int, OrgFolder>)DataModule.Get(OrgFolder.OBJECT_NAME) ?? new DataSet<int, OrgFolder>(OrgFolder.OBJECT_NAME);
      }

      private void DocSearch(object sender, EventArgs e)
      {
         srchTimer.Stop();
         OrgListFiltered(tbFind.Text.Trim().ToUpper());
      }

      private void OrgListFiltered(string str)
      {
         List<OrgSel> list = new List<OrgSel>();

         foreach (OrgSel o in doclist.Values)
            if (str.Length == 0 || o.Name.ToUpper().Contains(str))
            {
               o.Sel = false;
               list.Add(o);
            }

         list.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });
         grid.DataSource = list;
      }

      public static void Open()
      {
         new FmDocsReport().Show();
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         const string COMMON_FILTER_STR = "\"created\" >= ToDate('{0:dd/MM/yyyy}') and \"created\" < ToDate('{1:dd/MM/yyyy} 23:59:59') and \"userid\" in({2})";
         string userids = CollectUserIds();
         string filter = string.Format(COMMON_FILTER_STR, period.Start, period.Finish, userids);

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsOrg);

         foreach (IDataSet ds in datasets)
         {
            ds.Filter = filter;
            upd.Add(ds);
         }

         const string USERIDIN = "\"userid\" in({0})";
         dsOrgFolder.Filter = string.Format(USERIDIN, userids);
         upd.Add(dsOrgFolder);
         FmWait.StdDataRefresh(this, upd, DoOrgListLoaded);
      }

      private void DoOrgListLoaded()
      {
         CollectDocs();
         OrgListFiltered(string.Empty);
      }

      private void CollectDocs()
      {
         doclist.Clear();

         foreach (IDataSet ds in datasets)
            DocListAppend(ds.Data);
      }

      private void DocListAppend(ICollection docs)
      {
         foreach (object o in docs)
         {
            BaseDocument bd = (BaseDocument)o;
            if (!doclist.ContainsKey(bd.id))
            {
               OrgSel os = new OrgSel(bd.org);
               doclist[bd.id] = os;
            }

            doclist[bd.id].AppendDoc(bd);
         }
      }

      class OrgSel
      {
         private bool sel;
         private Org org;
         private List<BaseDocument> docs = new List<BaseDocument>();

         public OrgSel(Org o)
         {
            this.org = o;
            this.sel = false;
         }

         public bool Sel { get { return sel; } set { sel = value; } }
         public string Name { get { return org.Name; } set { } }

         public string OrgID { get { return org != null ? org.id : string.Empty; } set { } }
         internal void AppendDoc(BaseDocument doc){ docs.Add(doc); }
         public List<BaseDocument> Docs { get { return docs; } }
      }

      private string CollectUserIds()
      {
         StringBuilder result = new StringBuilder();

         Division d = cbDivision.SelectedItem as Division;

         if (d != null)
         {
            List<Division.DivisionAgent>.Enumerator iter = d.GetAllAgents().GetEnumerator();
            

            while (iter.MoveNext())
            {
               if (result.Length > 0)
                  result.Append(COMMA);

               result.Append(UPPER_COMMA).Append(iter.Current.id).Append(UPPER_COMMA);
            }
         }

         return result.ToString();
      }

      private void DoLoadData()
      {
         throw new NotImplementedException();
      }

      private void FmDocsReport_Load(object sender, EventArgs e)
      {
         Manager mc = CurrentUser.user as Manager;

         if (mc != null)
         {
            List<Division> list = mc.AllDivisions;
            list.Sort((lhs, rhs) => { return lhs.DivisionName.CompareTo(rhs.DivisionName); });
            cbDivision.Items.AddRange(list.ToArray());
            SelectDivision(mc);
         }
      }

      private void SelectDivision(Manager mc)
      {
         int sel = -1;
         for (int i = 0; i < cbDivision.Items.Count; i++)
         {
            Division d = (Division)cbDivision.Items[i];

            if (d.id.Equals(mc.Division.id))
            {
               sel = i;
               break;
            }
         }

         cbDivision.SelectedIndex = sel;
      }

      private void tbFind_TextChanged(object sender, EventArgs e)
      {
         srchTimer.Stop();

         if (tbFind.Text.Length > 0)
            srchTimer.Start();
         else
            DoOrgListLoaded();

      }

      private void btnSel_Click(object sender, EventArgs e)
      {
         selectAll(true);
      }

      private void selectAll(bool sel)
      {
         List<OrgSel> list = grid.DataSource as List<OrgSel>;

         if (list != null)
         {
            foreach (OrgSel o in list)
               o.Sel = sel;
         }

         grid.Refresh();
      }

      private void btnUnsel_Click(object sender, EventArgs e)
      {
         selectAll(false);
      }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         DocsReportParam param = new DocsReportParam();
         param.start = period.Start;
         param.finish = period.Finish;
         param.docs = CollectSelDocs();
         param.agents = CollectAgents();

         DocsReport report = new DocsReport(param);
      }

      private List<Agent> CollectAgents()
      {
         List<Agent> result = new List<Agent>();
         Division div = cbDivision.SelectedItem as Division;

         foreach (Division.DivisionAgent a in div.GetAllAgents())
            result.Add(a.agent);

         return result;
      }

      private List<BaseDocument> CollectSelDocs()
      {
         List<BaseDocument> result = new List<BaseDocument>();
         List<OrgSel> list = grid.DataSource as List<OrgSel>;

         if (list != null)
            foreach (OrgSel o in list)
               if (o.Sel)
                  foreach (BaseDocument bd in o.Docs)
                     result.Add(bd);

         return result;
      }

      private void grid_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         DataGridViewCell cell = grid.CurrentCell;
         if (cell != null && cell.ColumnIndex == 0)
            grid.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }
   }

   class DocsReportParam
   {
      public List<BaseDocument> docs;
      public DateTime start;
      public DateTime finish;
      public List<Agent> agents;
   }

   class DocReportDataItem
   { 
      public string org = string.Empty;
      public string agent = string.Empty;
      public string type = string.Empty;
      public string inroute = string.Empty;
      public string date = string.Empty;
      public string time = string.Empty;
      public string sended = string.Empty;
      public string remark = string.Empty;
      public string address = string.Empty;
   }

   class DocReportData
   {
      public List<DocReportDataItem> items = new List<DocReportDataItem>();
      public DateTime start;
      public DateTime finish;

      private int CompareDoc(BaseDocument lhs, BaseDocument rhs)
      {
         int result = lhs.OrgName.CompareTo(rhs.OrgName);

         if (result == 0)
            result = lhs.Created.CompareTo(rhs.Created);

         return result;
      }

      private Dictionary<string, Dictionary<DateTime, List<string>>> routes = new Dictionary<string, Dictionary<DateTime, List<string>>>();

      public DocReportData(DocsReportParam param)
      {
         start = param.start.Date;
         finish = param.finish.Date;

         CreateRoutes(param);
         List<BaseDocument> docs = param.docs;
         docs.Sort((lhs, rhs) => { return CompareDoc(lhs, rhs); });

         foreach (BaseDocument doc in docs)
         {
            DocReportDataItem item = new DocReportDataItem();
            item.org = doc.OrgName;
            item.agent = doc.AgentName;
            item.type = GetDocType(doc);
            item.inroute = DocInRoute(doc);
            item.date = string.Format("{0:dd.MM.yyyy}", doc.Created);
            item.time = string.Format("{0:hh:mm}", doc.Created);
            item.sended = string.Format("{0:dd.MM.yyyy}", doc.Sended);
            item.remark = doc.Remark;
            item.address = GetAddress(doc);

            items.Add(item);
         }
      }

      private void CreateRoutes(DocsReportParam param)
      {
         foreach (Agent a in param.agents)
         {
            Dictionary<DateTime, List<string>> ar = null;

            if (!routes.ContainsKey(a.id))
               routes[a.id] = new Dictionary<DateTime, List<string>>();

            ar = routes[a.id];

            DateTime v = start;
            if (v <= finish)
               while (v <= finish)
               {
                  List<string> list = new List<string>();

                  foreach (Org o in OrdersDetail.GetRoutePeriod(v, v.AddDays(1), a))
                     if (o != null)
                        list.Add(o.id);

                  ar[v] = list;
                  v = v.AddDays(1);
               }
         }
      }

      private string GetAddress(BaseDocument doc)
      {
         return doc.Org.Address;
      }

      private string DocInRoute(BaseDocument doc)
      {
         const string YES = "";
         const string NO = "нет";
         string result = NO;

         if (routes.ContainsKey(doc.userid))
         {
            Dictionary<DateTime, List<string>> ar = routes[doc.userid];

            if (ar.ContainsKey(doc.Created.Date) && ar[doc.Created.Date].Contains(doc.id))
               result = YES;
         }

         return result;
      }

      private string GetDocType(BaseDocument doc)
      {
         string result = string.Empty;
         const string TASK_STR = "Задачи";

         if (doc is Order)
            result = new ObjType(ObjType.TObjType.OtOrder).ToString();
         else if (doc is VisitInfo)
            result = new ObjType(ObjType.TObjType.OtVisit).ToString();
         else if (doc is OrgRemnants)
            result = new ObjType(ObjType.TObjType.OtOrgRemnants).ToString();
         else if (doc is Incass)
            result = new ObjType(ObjType.TObjType.Incass).ToString();
         else if (doc is TaskDone)
            result = TASK_STR;
         else if (doc is Answer)
            result = new ObjType(ObjType.TObjType.Answer).ToString();
         else if (doc is Sales)
            result = new ObjType(ObjType.TObjType.Sales).ToString();

         return result;
      }
   }

   class DocsReport : Excel
   {
      public DocsReport(DocsReportParam param )
      {
         DoReport(param);
         Visible = true;
      }

      private void DoReport(DocsReportParam param)
      {
         DocReportData data = new DocReportData(param);

         SetValue(1, 1, string.Format("Период {0:dd.MM.yyyy}-{1:dd.MM.yyyy}", data.start, data.finish));

         const int START_HEADER_ROW = 5;
         int row = START_HEADER_ROW;

         SetValue(row, 1, "Контрагенты");
         SetValue(row, 2, "Агент");
         SetValue(row, 3, "Тип посещения");
         SetValue(row, 4, "По маршруту");
         SetValue(row, 5, "Дата");
         SetValue(row, 6, "Время создания");
         SetValue(row, 7, "Дата передачи");
         SetValue(row, 8, "Коментарий");
         SetValue(row, 9, "Адрес");

         SetBackColor(GetRange(row, 1, row, 9), Color.LightGray);
         SetColumnWidth(1, 40);
         SetColumnWidth(2, 25);
         SetColumnWidth(3, 15);
         SetColumnWidth(4, 13);
         SetColumnWidth(5, 15);
         SetColumnWidth(6, 15);
         SetColumnWidth(7, 15);
         SetColumnWidth(8, 30);
         SetColumnWidth(9, 50);
         row++;

         foreach (DocReportDataItem item in data.items)
         {
            SetValue(row, 1, item.org);
            SetValue(row, 2, item.agent);
            SetValue(row, 3, item.type);
            SetValue(row, 4, item.inroute);
            SetValue(row, 5, item.date);
            SetValue(row, 6, item.time);
            SetValue(row, 7, item.sended);
            SetValue(row, 8, item.remark);
            SetValue(row, 9, item.address);
            row++;
         }

         row--;
         SetWrapeText(GetRange(START_HEADER_ROW + 1, 1, row, 9), true);
         SetCellVerticalAlign(START_HEADER_ROW + 1, 1, row, 9, xlCenter);
         SetBordersOnRange(START_HEADER_ROW, 1, row, 9, xlContinuous);
      }
   }
}
