using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.NapoleonManager.Utils;
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
   public partial class FmReturnReport : Form
   {
      SimpleDataSet<Returns> returns = new SimpleDataSet<Returns>(Returns.OBJECT_NAME, false);

      public FmReturnReport()
      {
         InitializeComponent();

         if (MainForm.Instance.CheckIsMainDataPresents(true))
         {
            Manager m = CurrentUser.user as Manager;
            if (m != null)
            {
               List<Agent> agents = new List<Agent>();
               foreach (Agent a in m.GetAgents().Data)
                  agents.Add(a);

               agents.Sort();
               cbAgents.Items.AddRange(agents.ToArray());

               cbDivisions.Items.Add(m.Division);
               foreach (Division d in m.Childs)
                  cbDivisions.Items.Add(d);
            }
         }
      }

      public void SetDate(DateTime start, DateTime end, Agent selAgent, Division selDivision)
      {
         if (selAgent != null)
         {
            cbAgents.SelectedItem = selAgent;
            rbAgents.Checked = true;
         }
         else if (selDivision != null )
         {
            cbDivisions.SelectedItem = selDivision;
            rbDivision.Checked = true;
         }
         UpdateComboBoxes();

         dpvReport.Start = start;
         dpvReport.Finish = end;
      }

      void AddAgentFiles(List<IDataSet> upd, Agent a)
      {
         IDataSet orgs = DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>), true);
         if (orgs.Count == 0)
            upd.Add(orgs);
      }

      private void btnExcelReport_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         
         string where = String.Format("\"created\" >= ToDate('{0:dd/MM/yyyy}') and \"created\" < ToDate('{1:dd/MM/yyyy}')", 
            dpvReport.Start.Date, dpvReport.Finish.Date.AddDays(1));

         IDataSet price = DataModule.Get(Price.OBJECT_NAME);
         if (price == null)
            price = new DataSet<string, Price>(Price.OBJECT_NAME);
         if (price.Count == 0)
         {
            price.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            upd.Add(price);
         }

         string agents = "";
         if(rbDivision.Checked)
         {
            foreach(Division.DivisionAgent a in ((Division)cbDivisions.SelectedItem).GetAllAgents())
            {
               if( a.agent == null )
                  continue;
               agents += "'"+ a.id + "',";
               AddAgentFiles(upd, a.agent);
            }

            agents = agents.Substring(0, agents.Length - 1);
         } else if(rbAgents.Checked)
         {
            Agent sel = (Agent)cbAgents.SelectedItem;
            if (sel != null)
            {
               agents = "'" + sel.id + "'";
               AddAgentFiles(upd, sel);
            }
         }

         if( agents.Length == 0 )
         {
            MessageBox.Show("Укажите, пожалуйста, подразделени или агента");
            return;
         }

         where += " and \"userid\" in (" + agents + ")";
         returns.Filter = where;

         upd.Add(returns);
         FmWait.StdDataRefresh(this, upd, DoReport);
      }

      void DoReport()
      {
         RetExcel re = new RetExcel();
         re.Build(returns);
         re.Visible = true;
      }

      void UpdateComboBoxes()
      {
         cbAgents.Enabled = rbAgents.Checked;
         cbDivisions.Enabled = rbDivision.Checked;
      }

      private void rbDivision_Click(object sender, EventArgs e)
      {
         UpdateComboBoxes();
      }

      private void rbAgents_Click(object sender, EventArgs e)
      {
         UpdateComboBoxes();
      }
   }

   class RetExcel : Excel
   {
      public RetExcel()
      {
      }

      void MakeHeader(string[] columns, int cr)
      {
         int cc = 1;
         foreach(string col in columns)
         {
            object cell = GetCell(cr, cc++);
            SetValue(cell, col);
            SetCellBoldFont(cell, true);
            SetCellHorizontalAlign(cell, xlCenter);
         }
      }

      void MakeRow(object[] data, int cr)
      {
         int cc = 1;
         foreach (object col in data)
         {
            object cell = GetCell(cr, cc++);
            SetValue(cell, col);
         }
      }

      public void Build(SimpleDataSet<Returns> docs)
      {
         int cr = 1;
         MakeHeader(new string[] { "дата", "агент ФИО, № склада", "контрагент", "наименование", "вес", "сумма", "причина возврата" }, cr++);

         foreach(Returns doc in docs.Data)
         {
            foreach(ReturnItem ri in doc.items)
            {
               double weight = ((ri.item == null) ? 1 : ri.item.weight);
               if (weight == 0)
                  weight = 1;
               weight = ri.qty* weight;
               object[] data = new object[] {doc.created.ToString("dd.MM.yy"), doc.AgentName, doc.OrgName, ri.Item, weight, ri.cost * ri.qty, ri.cause};
               MakeRow(data, cr++);
            }
         }

         int col = 1;
         foreach(int wdh in new int[] {13, 40, 45, 50, 12, 15, 70})
            SetColumnWidth(col++, wdh);
      }
   }
}
