using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Data.OleDb;
using System.Drawing;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmLoadPlan : Form
   {
      BindingList<LoadData> data = new BindingList<LoadData>();
      DataSet<string, OrgPlan> dsOrgPlan = new DataSet<string, OrgPlan>(OrgPlan.OBJECT_NAME, false);
      public EditOrgPlan dataForm = null;

      public FmLoadPlan()
      {
         InitializeComponent();
         grid.AutoGenerateColumns = false;
      }

      private void FmLoadPlan_Load(object sender, EventArgs e)
      {
         Manager dm = CurrentUser.user as Manager;

         if (dm == null)
            return;

         List<LoadData> list = new List<LoadData>();
         foreach (Agent a in dm.GetAgents().Data)
         {
            LoadData ld = new LoadData();
            ld.ID = a.id;
            ld.Name = a.Name;
            list.Add(ld);
         }

         list.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });
         
         foreach(LoadData ld in list)
            data.Add(ld);

         grid.DataSource = data;
      }

      class LoadData
      {
         private string id = string.Empty;
         private string name = string.Empty;
         private string file = string.Empty;

         public string ID { get { return id; } set { id = value; } }
         public string Name { get { return name; } set { name = value; } }
         public string File { get { return file; } set { file = value; } }
      }

      private void btnLoad_Click(object sender, EventArgs e)
      {
         dsOrgPlan.Clear();

         Thread thread = new Thread(Loading);
         FmWait.ShowForm(this, thread);
         thread.Start();
      }

      private void Loading()
      {
         foreach (LoadData ld in data)
         {
            if (ld.File.Trim().Length > 0)
               LoadPlan(ld.ID, ld.File);
         }

         if( dsOrgPlan.Count == 0 )
         {
            FmWait.CloseForm();
            MessageBox.Show("Ошибка при загрузке файла - план не был загружен");
         }
         else
         {
            List<IDataSet> wr = new List<IDataSet>();
            wr.Add(dsOrgPlan);

            bool ret = DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection());

            Invoke(new EmptyParamHandler(() =>
            {
               FmWait.CloseForm();
               MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");

               if (dataForm != null)
                  dataForm.RefreshData();
            }));
         }
      }

      private void LoadPlan(string userid, string file)
      {
         ExcelLoader excel = new ExcelLoader();
         try
         {
            excel.userid = userid;
            excel.handler = AppendPlanRow;
            excel.OpenFile(file);
         }
         catch (Exception)
         {
         }
         excel.Close();
         excel.Dispose();
      }

      private void AppendPlanRow(string userid, string id, double plan)
      {
         OrgPlan orgplan = new OrgPlan();
         orgplan.id = id;
         orgplan.userid = userid;
         orgplan.value = plan;
         DateTime date = DateTime.Now;
         orgplan.created = date;
         orgplan.changed = date;
         orgplan.start = new DateTime(timepicker.Value.Year, timepicker.Value.Month, 1);
         orgplan.finish = orgplan.start.AddMonths(1).AddDays(-1);
         dsOrgPlan.Add(orgplan.Key, orgplan);
      }

      private void grid_CellContentClick(object sender, DataGridViewCellEventArgs e)
      {
         LoadData ld = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as LoadData;

         if (ld != null)
         {
            OpenFileDialog dialog = new OpenFileDialog();
            dialog.Filter = "Excel|*.xls;*.xlsx";
            if (dialog.ShowDialog() == DialogResult.OK)
            {
               ld.File = dialog.FileName;
               grid.Refresh();
            }
         }
      }
   }

   class ExcelLoader : Excel
   {
      public delegate void PlanHandler(string userid, string id, double plan);
      public PlanHandler handler = null;
      public string userid = string.Empty;

      public void OpenFile(string file)
      {
         if (handler == null)
            return;

         const int ANCHOR_CLMN = 1;
         const int PLAN_CLMN = 4;
         const int ORG_CLMN = 7;
         InvokeMethod(WorkBooks, "Open", file);

         int row = 2;
         while (true)
         {
            object anchor = GetValue(row, ANCHOR_CLMN);

            if (anchor != null && anchor.ToString().ToUpper().Equals("ИТОГО"))
               break;

            object id = GetValue(row, ORG_CLMN);

            if (id != null && id.ToString().Length > 0)
            {
               object plan = GetValue(row, PLAN_CLMN);

               if (plan != null && plan is double)
                  handler(userid, id.ToString(), (double)plan);
            }

            row += 1;
         }

         Close();
         Dispose();
      }
   }
}
