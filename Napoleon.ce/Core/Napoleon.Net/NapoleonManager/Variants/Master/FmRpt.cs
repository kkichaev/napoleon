using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmRpt : Form
   {
      DataSet<int, Order> dsOrder;
      DataSet<string, OrgType> dsOrgType;
      DataSet<string, Org> dsOrg;
      DataSet<string, OrgMem> dsOrgMem;
      DataSet<int, CheckPhoto> dsCheckPhoto;

      public FmRpt()
      {
         InitializeComponent();
         dsOrder = (DataSet<int, Order>) DataModule.Get(Order.OBJECT_NAME) ?? new DataSet<int, Order>(Order.OBJECT_NAME);
         dsOrgType = (DataSet<string, OrgType>)DataModule.Get(OrgType.OBJECT_NAME) ?? new DataSet<string, OrgType>(OrgType.OBJECT_NAME);
         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.OBJECT_NAME) ?? new DataSet<string, Org>(Org.OBJECT_NAME);
         dsOrgMem = (DataSet<string, OrgMem>)DataModule.Get(OrgMem.OBJECT_NAME) ?? new DataSet<string, OrgMem>(OrgMem.OBJECT_NAME);
         dsCheckPhoto = (DataSet<int, CheckPhoto>)DataModule.Get(CheckPhoto.OBJECT_NAME) ?? new DataSet<int, CheckPhoto>(CheckPhoto.OBJECT_NAME);
      }

      private void FmRpt_Load(object sender, EventArgs e)
      {
         Manager dm = CurrentUser.user as Manager;

         if (dm != null)
         {
            Agents agents = dm.GetAgents();
            List<Agent> list = new List<Agent>();
            list.AddRange(agents.Values);
            list.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });
            clbAgent.Items.AddRange(list.ToArray());
         }
      }

      private void CheckUnCheck(object sender, EventArgs e) 
      {
         bool sel = false;

         if(Boolean.TryParse(((ToolStripButton)sender).Tag.ToString(), out sel))
            for (int i = 0; i < clbAgent.Items.Count; i++)
               clbAgent.SetItemChecked(i, sel);
      }

      private Dictionary<string, Agent> SelectedAgent()
      {
         Dictionary<string, Agent> result = new Dictionary<string, Agent>();

         foreach (int i in clbAgent.CheckedIndices)
         {
            Agent agent = (Agent)clbAgent.Items[i];
            result.Add(agent.id, agent);
         }

         return result;
      }

      private string StrAgentIds()
      {
         StringBuilder sb = new StringBuilder();

         foreach (Agent a in SelectedAgent().Values)
         {
            if (sb.Length > 0)
               sb.Append(",");
            sb.Append("'").Append(a.id).Append("'");
         }

         return sb.ToString();
      }

      private string OrderFilter()
      {
         const string ORD_FILTER = "\"created\" >= ToDate('{0:dd/MM/yyyy}') and \"created\" < ToDate('{1:dd/MM/yyyy}') and \"userid\" in ({2})";
         DateTime start =  new DateTime(dtpDate.Value.Date.Year, dtpDate.Value.Date.Month, 1);
         DateTime end = start.AddMonths(1);

         return string.Format(ORD_FILTER, start, end, StrAgentIds());
      }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         dsOrder.Filter = OrderFilter();
         dsCheckPhoto.Filter = CheckPhotoFilter();

         upd.Add(dsOrgType);
         upd.Add(dsOrg);
         upd.Add(dsOrder);
         upd.Add(dsOrgMem);
         upd.Add(dsCheckPhoto);

         FmWait.StdDataRefresh(this, upd, DoReport);
      }

      private string CheckPhotoFilter()
      {
         const string DATE_FORMAT = "ToDate('{0:dd/MM/yyyy}')";
         DateTime start = new DateTime(dtpDate.Value.Date.Year, dtpDate.Value.Date.Month, 1);
         DateTime end = start.AddMonths(1);
         StringBuilder sb = new StringBuilder();
         sb.Append(String.Format(DATE_FORMAT, start)).Append(";");
         sb.Append(String.Format(DATE_FORMAT, end)).Append(";");
         sb.Append(StrAgentIds());
         return sb.ToString();
      }

      private void DoReport()
      {
         RepExcelData data = new RepExcelData(this);
         RepExcel rep = new RepExcel(data);
         rep.Visible = true;
      }

      class DataItemRowVal
      {
         public Double sum = 0.0;
         public bool photo = false;
      }

      class DataItemRow
      {
         public string org;
         public string type;
         public Dictionary<int, DataItemRowVal> order = new Dictionary<int, DataItemRowVal>();
      }

      class DataItem
      {
         public string agent;
         public List<DataItemRow> rows = new List<DataItemRow>();
      }

      class UserOrgOrder : Dictionary<string, Dictionary<string, List<Order>>> { }
      class UserOrgVisit : Dictionary<string, Dictionary<string, List<int>>> 
      {
         public Boolean CheckVisit(string userid, string id, DateTime created)
         {
            return ContainsKey(userid) && this[userid].ContainsKey(id) && this[userid][id].IndexOf(created.Day) != -1;
         }
      }

      class RepExcelData
      {
         public List<DataItem> items = new List<DataItem>();
         public DateTime month;
         public int daysInMonth;

         public RepExcelData(FmRpt form)
         {
            month = new DateTime(form.dtpDate.Value.Year, form.dtpDate.Value.Month, 1);
            daysInMonth = DateTime.DaysInMonth(form.dtpDate.Value.Year, form.dtpDate.Value.Month);
            UserOrgOrder uoo = new UserOrgOrder();
            CollectFromOrder(form, uoo);
            UserOrgVisit uov = new UserOrgVisit();
            CollectFromVisit(form, uov);
            ProcessItems(form, uoo, uov);
            items.Sort((lhs, rhs) => { return lhs.agent.CompareTo(rhs.agent); });
         }

         private void CollectFromVisit(FmRpt form, UserOrgVisit uov)
         {
            foreach (CheckPhoto v in form.dsCheckPhoto.Values)
            {
               if (form.IsBadOrgid(v.id))
                  continue;

               Dictionary<string, List<int>> orgs = null;

               if (!uov.ContainsKey(v.userid))
               {
                  orgs = new Dictionary<string, List<int>>();
                  uov.Add(v.userid, orgs);
               }
               else
                  orgs = uov[v.userid];

               List<int> list = null;

               if (!orgs.ContainsKey(v.id))
               {
                  list = new List<int>();
                  orgs.Add(v.id, list);
               }
               else
                  list = orgs[v.id];

               list.Add(v.date.Day);
            }
         }

         private void ProcessItems(FmRpt form, UserOrgOrder uoo, UserOrgVisit uov)
         {
            Dictionary<string, Dictionary<string, List<Order>>>.Enumerator iter = uoo.GetEnumerator();
            Dictionary<string, Agent> agents = form.SelectedAgent();

            while (iter.MoveNext())
            {
               DataItem i = new DataItem();

               if (agents.ContainsKey(iter.Current.Key))
                  i.agent = agents[iter.Current.Key].Name;
               else
                  i.agent = iter.Current.Key;

               Dictionary<string, List<Order>>.Enumerator iterorg = iter.Current.Value.GetEnumerator();

               while (iterorg.MoveNext())
               {
                  List<Order> orders = iterorg.Current.Value;
                  orders.Sort((lhs, rhs) => { return lhs.created.CompareTo(rhs.created); });

                  DataItemRow row = new DataItemRow();
                  row.org = form.dsOrg[iterorg.Current.Key].Name;

                  row.type = form.dsOrgType[form.dsOrgMem[iterorg.Current.Key].type].Name;

                  foreach (Order o in iterorg.Current.Value)
                  {
                     if (row.order.ContainsKey(o.created.Day))
                        row.order[o.created.Day].sum += o.Sum();
                     else
                     {
                        DataItemRowVal v = new DataItemRowVal();
                        v.sum = o.Sum();
                        v.photo = uov.CheckVisit(o.userid, o.id, o.created);
                        row.order[o.created.Day] = v;
                     }
                  }

                  i.rows.Add(row);
               }

               i.rows.Sort((lhs, rhs) =>
               {
                  int result = 0;
                  result = lhs.type.CompareTo(rhs.type);

                  if (result == 0)
                     result = lhs.org.CompareTo(rhs.org);

                  return result;
               }
               );
               items.Add(i);
            }
         }

         private static void CollectFromOrder(FmRpt form, UserOrgOrder uoo)
         {
            foreach (Order o in form.dsOrder.Values)
            {
               if (form.IsBadOrgid(o.id))
                  continue;

               Dictionary<string, List<Order>> orgs = null;

               if (!uoo.ContainsKey(o.userid))
               {
                  orgs = new Dictionary<string, List<Order>>();
                  uoo.Add(o.userid, orgs);
               }
               else
                  orgs = uoo[o.userid];

               List<Order> list = null;

               if (!orgs.ContainsKey(o.id))
               {
                  list = new List<Order>();
                  orgs.Add(o.id, list);
               }
               else
                  list = orgs[o.id];

               list.Add(o);
            }
         }
      }

      class RepExcel : Excel
      {
         const double CELL_WIDTH = 3.5;
         const double ORG_WIDTH = 40;
         const int START_VAR_ROW = 4;
         const int START_DAY_CLMN = 3;
         const string SUMM_RC0_RC2 = "=СУММ(RC[-{0}]:RC[-2])";
         const string SUMM_R0C_R1C = "=СУММ(R[-{0}]C:R[-1]C)";
         const string ITOGO = "ИТОГО";
         int LAST_CLMN = 0;
         int RESULT_CLMN = 0;
         const string MONTH_FORMAT = "MMMM";
         const string MONTH_STR = "месяц";
         const string DAY_STR = "число";
         const string WHOLE = "ВСЕГО";
         const string PHOTO_STR = "c фото";
         const string UNPHOTO_STR = "без фото";
         const double RESULT_CELL_WIDTH = 5;

         public RepExcel(RepExcelData data)
         {
            LAST_CLMN = START_DAY_CLMN + data.daysInMonth;
            RESULT_CLMN = LAST_CLMN + 1;

            data.items.Reverse();
            foreach (DataItem item in data.items)
            {
               object sheet = AddSheet();
               SetSheetName(sheet, item.agent);
               PrintHeader(data);
               int row = START_VAR_ROW;
               int gr = 0;
               List<int> gidx = new List<int>();
               PrintItems(data, item, ref row, ref gr, gidx);
               SetGroupResult(row, gr);
               row++;
               SetWholeCell(row, gidx);
               PageSetup(sheet);
               SetBordersOnRange(1, 1, row, RESULT_CLMN, xlContinuous);
            }
         }

         private void PrintHeader(RepExcelData data)
         {
            SetValue(1, 1, MONTH_STR);
            SetValue(2, 1, DAY_STR);
            PrintDaysCell(data);
            MergeCells(1, START_DAY_CLMN, 1, LAST_CLMN - 1);
            SetValue(1, START_DAY_CLMN, data.month.ToString(MONTH_FORMAT));
            SetCellHorizontalAlign(1, START_DAY_CLMN, xlCenter);
            SetColumnWidth(RESULT_CLMN, RESULT_CELL_WIDTH);
         }

         private void PrintDaysCell(RepExcelData data)
         {
            int c = START_DAY_CLMN;

            for (int i = 1; i <= data.daysInMonth; i++)
            {
               SetValue(2, c, i);
               SetColumnWidth(c, CELL_WIDTH);
               c++;
            }
            
         }

         private void PageSetup(object sheet)
         {
            PageSetup(sheet, ORIENTATION_STR, xlLandscape);
            PageSetup(sheet, ZOOM_STR, false);
            PageSetup(sheet, FIT_TO_PAGES_TALL_STR, false);
         }

         private void SetWholeCell(int r, List<int> sr)
         {
            SetValue(r, LAST_CLMN, WHOLE);
            StringBuilder sb = new StringBuilder();
            sb.Append("=СУММ(");
            foreach (int i in sr)
               sb.Append("R[-").Append(r - i).Append("]C;");
            sb.Append("R[-1]C");
            SetValue(r, RESULT_CLMN, sb.ToString());
         }

         private void PrintItems(RepExcelData data, DataItem item, ref int r, ref int gr, List<int> sr)
         {
            string type = null;

            foreach (DataItemRow row in item.rows)
            {
               if (type == null || !type.Equals(row.type))
               {
                  if (type != null)
                  {
                     SetGroupResult(r, gr);
                     sr.Add(r);
                     r++;
                     gr = 0;
                  }

                  PrintOrgType(r, row);
                  type = row.type;
                  r++;
               }

               MergeCells(r, 1, r + 1, 1);
               SetValue(r, 1, row.org);
               SetColumnWidth(1, ORG_WIDTH);
               SetWrapeText(r, 1, true);
               SetCellVerticalAlign(r, 1, xlCenter);
               SetCellHorizontalAlign(r, 1, xlLeft);
               SetValue(r, 2, PHOTO_STR);
               SetValue(r + 1, 2, UNPHOTO_STR);

               Dictionary<int, DataItemRowVal>.Enumerator iter = row.order.GetEnumerator();

               while (iter.MoveNext())
               {
                  int rr = r;
                  if (!iter.Current.Value.photo)
                     rr++;

                  int c = iter.Current.Key + START_DAY_CLMN;
                  SetValue(rr, c, iter.Current.Value.sum / 1000);
                  SetNumberFormat(rr, c);
               }


               int MONTH_SUM_RANGE = data.daysInMonth + 1;
               SetValue(r, LAST_CLMN, PHOTO_STR);
               SetValue(r, RESULT_CLMN, string.Format(SUMM_RC0_RC2, MONTH_SUM_RANGE));
               SetValue(r + 1, RESULT_CLMN, string.Format(SUMM_RC0_RC2, MONTH_SUM_RANGE));
               SetValue(r + 1, LAST_CLMN, UNPHOTO_STR);
               SetNumberFormat(r, RESULT_CLMN);
               SetNumberFormat(r + 1, RESULT_CLMN);
               r += 2;
               gr += 2;
            }
         }

         private void SetNumberFormat(int row, int cell)
         {
            SetProperty(GetCell(row, cell), "NumberFormat", "0,0");
         }

         private void PrintOrgType(int row, DataItemRow data)
         {
            MergeCells(row, 1, row, RESULT_CLMN);
            SetValue(row, 1, data.type);
            SetCellHorizontalAlign(row, 1, xlCenter);
            SetBackColor(GetCell(row, 1), Color.LightGray);
         }

         private void SetGroupResult(int row, int group)
         {
            SetValue(row, LAST_CLMN, ITOGO);
            SetValue(row, RESULT_CLMN, string.Format(SUMM_R0C_R1C, group));
            SetNumberFormat(row, RESULT_CLMN);
         }
      }

      internal bool IsBadOrgid(string id)
      {
         return !dsOrg.ContainsKey(id) || !dsOrgMem.ContainsKey(id) || !dsOrgType.ContainsKey(dsOrgMem[id].type);
      }
   }
}


