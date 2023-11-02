using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Utils;
using System.Windows.Forms;
using System.ComponentModel;
using System.Reflection;
using GRSoft.NapoleonManager.Reports;
using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.Network;
using System.Threading;
using System.Drawing;

namespace GRSoft.NapoleonManager
{
   class PAgentTask : GRSoft.Network.DataObject
   {
      static public string OBJECT_NAME = "AgentTask";

      public DateTime date = DateTime.Now;
      public DateTime appointDate = DateTime.Now;

      public String id = "";
      public String category = "";
      public int flags = 0;
      public String text = "";
      public int done = 0;
   }

   class ClientCardReport : Excel, IReportImplementation
   {
      public ReportData MakeData(FmDetail form)
      {
         SalesHistory.Data data = new SalesHistory.Data();
         data.from = form.GetDateForStartPeriod();
         data.till = form.GetDateForEndPeriod();
         data.agent = form.GetSelectedAgent();

         SalesHistoryParams paramDlg = new SalesHistoryParams(data, form.GetAgentOrgs, form.CurrentOrgId);
         if (paramDlg.ShowDialog() == DialogResult.OK)
         {
            return data;
         }

         return null;
      }

      public void Show()
      {
         Visible = true;
      }

      public bool IsPrepared { get { return dataFetched; } }

      AutoResetEvent waitEvent;
      bool dataFetched;
      private void FetchData(DataSet<int, PAgentTask> tasks, SalesHistory.Data data)
      {
         tasks.Filter = string.Format("userid='{0}' and id='{1}' and date >= ToDate('{2}') and date < ToDate('{3}')",
            data.agent.id, data.org.id, data.from.ToString("dd.MM.yyyy 00:00:00"), data.till.AddDays(1).ToString("dd.MM.yyyy"));

         List<IDataSet> updSets = new List<IDataSet>();
         updSets.Add(tasks);

         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

         waitEvent = new AutoResetEvent(false);
         FmWait.ShowForm(Form.ActiveForm, DataModule.RefreshGiveSets(MainForm.Instance.conn, updSets, FmWait.ProgressIndicator));
         waitEvent.WaitOne();
      }

      class TaskCmp : IComparer<PAgentTask>
      {
         #region Члены IComparer<PAgentTask>

         public int Compare(PAgentTask x, PAgentTask y)
         {
            int cmp = x.category.CompareTo(y.category);
            if (cmp != 0)
               return cmp;

            cmp = x.appointDate.CompareTo(y.appointDate);
            if (cmp != 0)
               return cmp;

            return x.text.CompareTo(y.text);
         }

         #endregion
      }

      public void Build(ReportData data)
      {
         DataSet<int, PAgentTask> tasks = new DataSet<int, PAgentTask>(PAgentTask.OBJECT_NAME, false);
         FetchData(tasks, (SalesHistory.Data)data);

         if (dataFetched == false)
            return;

         List<PAgentTask> ta = new List<PAgentTask>();
         ta.AddRange(tasks.Values);
         ta.Sort(new TaskCmp());

         int startRow = 3;
         int startCell = 1;

         SetFontSize(8);

         object cell;
         cell = GetCell(1, 1);
         SetValue(cell, "Клиент: " + ((SalesHistory.Data)data).org.Name);
         SetCellBoldFont(cell, true);

         cell = GetCell(2, 1);
         SetValue(cell, "Агент: " + ((SalesHistory.Data)data).agent.Name);
         SetCellBoldFont(cell, true);

         int row = startRow;
         String categ = null;
         DateTime checkDate = new DateTime(2000, 1, 1);
         foreach (PAgentTask t in ta)
         {
            if (categ == null || categ.CompareTo(t.category) != 0)
            {
               cell = GetCell(row, startCell);
               SetValue(cell, t.category);
               SetCellHorizontalAlign(cell, xlCenter);
               SetCellBoldFont(cell, true);
               SetBackColor(cell, Color.LightGray);

               MergeCells(row, startCell, row, startCell + 2);
               categ = t.category;

               row++;
               cell = GetCell(row, startCell);
               SetValue(cell, "Задача");
               SetCellHorizontalAlign(cell, xlCenter);
               SetCellBoldFont(cell, true);

               cell = GetCell(row, startCell+1);
               SetValue(cell, "Срок исполнения");
               SetCellHorizontalAlign(cell, xlCenter);
               SetCellBoldFont(cell, true);

               cell = GetCell(row, startCell+2);
               SetValue(cell, "Да / Нет");
               SetCellHorizontalAlign(cell, xlCenter);
               SetCellBoldFont(cell, true);

               row++;
            }

            cell = GetCell(row, startCell);
            SetValue(cell, t.text);

            cell = GetCell(row, startCell+1);
            if(t.appointDate.CompareTo(checkDate) < 0)
               SetValue(cell,  "?");
            else
               SetValue(cell,  t.appointDate);

            cell = GetCell(row, startCell + 2);
            SetValue(cell, (t.done != 0) ? "+" : "-");
            SetFontSize(cell, 14);
            
            row++;
         }

         AutoFit(startCell);
         AutoFit(startCell+1);
         AutoFit(startCell+2);

         SetBordersOnRange(startRow, startCell, row, startCell + 2, xlContinuous);

         SetSelectedCell("A1");
      }

      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         dataFetched = true;
         if (waitEvent != null)
            waitEvent.Set();
      }

      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         dataFetched = false;

         const string TITLE = "Ошибка";
         MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK, MessageBoxIcon.Error);

         if (waitEvent != null)
            waitEvent.Set();
      }
   }
}