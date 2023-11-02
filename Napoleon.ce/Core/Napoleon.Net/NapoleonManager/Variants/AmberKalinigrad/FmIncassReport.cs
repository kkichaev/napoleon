using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Threading;
using GRSoft.Network;
using GRSoft.NapoleonManager.Reports.Excel;

namespace GRSoft.NapoleonManager
{
   public partial class FmIncassReport : Form
   {
      private DataSet<int, CommonIncass> dsCommonIncass;

      public FmIncassReport()
      {
         InitializeComponent();

         dsCommonIncass = (DataSet<int, CommonIncass>)DataModule.Get(CommonIncass.OBJECT_NAME) ??
            new DataSet<int, CommonIncass>(CommonIncass.OBJECT_NAME);
      }

      private void FmIncassReport_Load(object sender, EventArgs e)
      {
         Manager m = CurrentUser.user as Manager;
         if (m != null)
         {
            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && cbAgents.Items.Contains(a.agent) == false)
                  cbAgents.Items.Add(a.agent);

            cbDivisions.Items.Add(m.Division);
            foreach (Division d in m.Childs)
               cbDivisions.Items.Add(d);
         }

         DateTime now = DateTime.Now;
         dtpBegin.Value = now.Date.AddMonths(-1);
         dtpEnd.Value = now.Date;

         if (cbAgents.Items.Count > 0)
            cbAgents.SelectedIndex = 0;

         if (cbDivisions.Items.Count > 0)
            cbDivisions.SelectedIndex = 0;
      }

      private void RadioButtonSelect(object sender, EventArgs e)
      {
         Control[] ctrl = new Control[] { cbDivisions, cbAgents };
         RadioButton rb = sender as RadioButton;

         if (rb != null)
         {
            int idx = int.Parse(rb.Tag.ToString());
            ctrl[idx].Enabled = rb.Checked;
         }
      }

      private void ok_Click(object sender, EventArgs e)
      {
         string f = string.Empty;
         if (rbAgents.Checked && cbAgents.SelectedIndex >= 0)
            f = "\"userid\" in ('" + ((Agent)cbAgents.SelectedItem).id + "')";
         else if(cbDivisions.SelectedIndex >= 0)
            f = FmMessageHistory.UserIdIsStr( ((Division)cbDivisions.SelectedItem).GetAllAgents());

         string DATA_FILTER = String.Format("{0} >= ToDate('{1:dd/MM/yyyy}') and {0} < ToDate('{2:dd/MM/yyyy}')",
              "\"created\"", dtpBegin.Value.Date, dtpEnd.Value.Date.AddDays(1).Date);

         if (f.Length > 0)
            f += " and ";

         f += DATA_FILTER;

         List<IDataSet> upd = new List<IDataSet>();
         dsCommonIncass.Filter = f;
         upd.Add(dsCommonIncass);

         FmWait.StdDataRefresh(this, upd, DoReport);
      }

      private void DoReport()
      {
         new CIReport();
      }

   }

   class CIReport : Excel
   {
      DataSet<int, CommonIncass> data;
      private int row;
      private int counter;
      private double sum;

      public CIReport()
      {
         data = (DataSet<int, CommonIncass>)DataModule.Get(CommonIncass.OBJECT_NAME);
         
         if (data != null)
         {
            MakeHeader();
            MakeData();
            MakeFooter();
         }
         Visible = true;
      }

      private void MakeFooter()
      {
         SetValue(row, 3, "Всего позиций");
         SetValue(row, 4, counter);
         row++;
         SetValue(row, 3, "Сумма итого");
         SetValue(row, 4, sum);
      }

      private void MakeData()
      {
         List<CommonIncass> list = new List<CommonIncass>();
         list.Sort((lhs, rhs)=>{ return lhs.created.CompareTo(rhs.created); });
         foreach (CommonIncass ci in data.Data)
            foreach (CommonIncassItem cii in ci.items)
            {
               SetValue(row, 1, cii.org.Name);
               SetValue(row, 2, ci.created.ToString("dd.MM.yyyy"));
               SetValue(row, 3, ci.sended.ToString("dd.MM.yyyy"));
               SetValue(row, 4, cii.sum);
               SetValue(row, 5, ci.remark);
               
               row++;
               counter++;
               sum += cii.sum;
            }
      }

      private void MakeHeader()
      {
         SetValue(1, 1, "Контрагент");
         SetValue(1, 2, "Дата создания");
         SetValue(1, 3, "Дата передачи");
         SetValue(1, 4, "Сумма");
         SetValue(1, 5, "Примечание");

         SetColumnWidth(1, 75);
         SetColumnWidth(2, 20);
         SetColumnWidth(3, 20);
         SetColumnWidth(4, 20);
         SetColumnWidth(5, 70);

         row = 2;
      }
   }
}
