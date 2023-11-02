using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Collections;
using System.IO;
using System.Threading;

namespace GRSoft.NapoleonManager
{
   public partial class FmJournal : Form
   {
      DataSet<string, TaskQuery> dsTask;
      DataSet<int, Visit> dsVisit;
      SortBindingList<JournalItem> allData = new SortBindingList<JournalItem>();

      public FmJournal()
      {
         InitializeComponent();
         dsTask = (DataSet<string, TaskQuery>)DataModule.Get(TaskQuery.OBJECT_NAME) ?? new DataSet<string, TaskQuery>(TaskQuery.OBJECT_NAME);
         dsVisit = (DataSet<int, Visit>) DataModule.Get(Visit.OBJECT_NAME) ?? new DataSet<int, Visit>(Visit.OBJECT_NAME);
         grid.AutoGenerateColumns = false;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         dsTask.Filter = string.Format(FmMain.TASK_FILTER, dtpBegin.Value.Date, dtpEnd.Value.Date.AddDays(1));
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsTask);

         FmWait.StdDataRefresh(this, list, DoLoadData);
      }

      private static int SortAnswer(Task lhs, Task rhs)
      {
         int result = lhs.taskid.CompareTo(rhs.taskid);

         if(result == 0)
            result = lhs.created.CompareTo(rhs.created) * -1;

         return result;
      }

      private void DoLoadData()
      {
         allData.Clear();

         foreach (TaskQuery t in dsTask.Values)
         {
            JournalItem i = new JournalItem();
            i.taskid = t.taskid;
            i.user = t.agent == null ? string.Empty : t.agent.Name;
            i.created = t.created;
            const string RANGE = "{0:HH:mm} - {1:HH:mm}";
            i.timeplan = string.Format(RANGE, t.start, t.finish);
            i.task = t.text;
            i.client = t.client;
            i.address = t.address;
            i.status = "Новая";
            i.timefact = string.Empty;

            i.status = Task.StatusToStr(t.solution);
            i.report = t.execrem;

            if (t.solution == Task.RESOLVED)
               i.timefact = string.Format(RANGE, t.startexec, t.finishexec);

            allData.Add(i);
         }

         grid.DataSource = allData;
         grid.Sort(grid.Columns[0], ListSortDirection.Descending);
      }

      class JournalItem
      {
         public string number = string.Empty;
         public string user = string.Empty;
         public DateTime created = DateTime.MinValue;
         public string timeplan = string.Empty;
         public string timefact = string.Empty;
         public string task = string.Empty;
         public string client = string.Empty;
         public string address = string.Empty;
         public string status = string.Empty;
         public string report = string.Empty;
         public string taskid = string.Empty;

         public List<Image> images = new List<Image>();

         public string Number { get { return number; } }
         public string User { get { return user; } }
         public DateTime Created { get { return created; } }
         public string TimePlan { get { return timeplan; } }
         public string TimeFact { get { return timefact; } }
         public string Task { get { return task; } }
         public string Client { get { return client; } }
         public string Address { get { return address; } }
         public string Status { get { return status; } }
      }

      private void grid_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         JournalItem jit =  grid.Rows[e.RowIndex].DataBoundItem as JournalItem;

         if (jit != null)
            tbReport.Text = jit.report;

         imPhoto.Images.Clear();
         lvPhoto.Items.Clear();

         for (int i = 0; i < jit.images.Count; i++)
         {
            Image image = jit.images[i];
            imPhoto.Images.Add(image);
            String tag = (i + 1).ToString();
            ListViewItem lvi = lvPhoto.Items.Add(tag);
            lvi.ImageIndex = i;
         }
      }

      private void btnPhoto_Click(object sender, EventArgs e)
      {
         DataGridViewRow r = grid.CurrentRow;

         if (r != null)
         {
            JournalItem i = r.DataBoundItem as JournalItem;

            if (i != null)
            {

               dsVisit.Filter = string.Format("\"taskid\"='{0}'", i.taskid);
               List<IDataSet> upd = new List<IDataSet>();
               upd.Add(dsVisit);

               FmWait.StdDataRefresh(this, upd, LoadPhoto);
            }
         }
      }

      private void LoadPhoto()
      {
         JournalItem jit = grid.CurrentRow.DataBoundItem as JournalItem;

         if (jit != null)
         {
            if (dsVisit.Count > 0)
            {
               Visit v = dsVisit[0];
               jit.images.Clear();

               foreach (Visit.VisitItem item in v.items)
               {
                  try
                  {
                     if (item.id == null)
                        continue;

                     MemoryStream stream = new MemoryStream(item.id);
                     Image image = new Bitmap(stream);
                     jit.images.Add(image);
                     stream.Close();
                  }
                  catch (Exception) { }
               }

               grid_RowEnter(grid, new DataGridViewCellEventArgs(grid.CurrentCell.ColumnIndex, grid.CurrentRow.Index));
            }
         }
      }

      private void lvPhoto_DoubleClick(object sender, EventArgs e)
      {
         JournalItem jit = grid.CurrentRow.DataBoundItem as JournalItem;

         if(jit != null)
         {
            int idx = (sender as ListView).SelectedItems[0].Index;

            if (idx >= 0 && jit.images.Count > 0 && idx < jit.images.Count)
            {
               Image photo = jit.images[idx];
               string tag = "";
               string comment = string.Empty;
               FmViewPhoto.ShowPhoto(photo, tag);
            }
         }
      }

      private void FmJournal_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void tbFind_TextChanged(object sender, EventArgs e)
      {
         timer1.Stop();
         if (tbFind.Text.Length > 0)
            timer1.Start();
         else if (!clearing)
            ClearFind();
      }

      bool clearing = false;

      private void ClearFind()
      {
         clearing = true;

         tbFind.Text = string.Empty;
         grid.DataSource = allData;
         clearing = false;
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         timer1.Stop();
         DoSearch(tbFind.Text);
      }

      private void DoSearch(string str)
      {
         str = str.ToUpper();
         SortBindingList<JournalItem> data = new SortBindingList<JournalItem>();

         foreach (JournalItem i in allData)
            if (i.User.ToUpper().Contains(str) || i.Task.ToUpper().Contains(str) || i.Client.ToUpper().Contains(str) 
               || i.Address.ToUpper().Contains(str) || i.Status.ToUpper().Contains(str))
               data.Add(i);

         grid.DataSource = data;
      }

      private void btnClear_Click(object sender, EventArgs e)
      {
         ClearFind();
      }
   }
   
}
