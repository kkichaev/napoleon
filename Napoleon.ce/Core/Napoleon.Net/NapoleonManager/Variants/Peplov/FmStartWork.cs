using GRSoft.NapoleonManager.Utils;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Globalization;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class FmStartWork : Form
   {
      private BindingList<StartWorkData> data = new BindingList<StartWorkData>();
      private SimpleDataSet<StartWork> dsStartWork = new SimpleDataSet<StartWork>(StartWork.OBJECT_NAME, false);
      private Dictionary<string, List<string>> userdata = new Dictionary<string, List<string>>();
      private Dictionary<string, StartWork> changed = new Dictionary<string, StartWork>();
      private const int DATA_LENGTH = 7;
      private const char TIME_SEPARATOR = ':';

      public FmStartWork()
      {
         InitializeComponent();

         grid.AutoGenerateColumns = false;
      }

      private void FmStartWork_Load(object sender, EventArgs e)
      {
         Manager mc = CurrentUser.user as Manager;
         if (mc != null)
         {
            List<Agent> al = new List<Agent>();
            foreach (Division.DivisionAgent da in mc.Division.GetAllAgents())
            {
               if (da.agent == null)
                  continue;

               al.Add(da.agent);
            }

            al.Sort();
            al.ForEach(x => cbAgents.Items.Add(x));

            if (cbAgents.Items.Count > 0)
            {
               cbAgents.SelectedIndexChanged -= cbAgents_SelectedIndexChanged;
               cbAgents.SelectedIndex = 0;
               cbAgents.SelectedIndexChanged += cbAgents_SelectedIndexChanged;
            }

            for (int i = 0; i < DATA_LENGTH; i++)
               data.Add(new StartWorkData(this, i));

            grid.DataSource = data;
         }

         btnRefresh.PerformClick();
      }

      public class StartWorkData
      {
         public int day = 0;
         private string hour = string.Empty;
         private string min = string.Empty;
         private FmStartWork controller;

         public StartWorkData(FmStartWork master, int day)
         {
            this.day = day;
            this.controller = master;
         }

         public String Day
         {
            get
            {
               return WeekDay.fullnames[day];
            }
         }

         public string Hour { 
            get{ return hour;}
            set
            {
               hour = value ?? string.Empty;
               controller.TimeChanged(this);
            }
         }

         public string Min {
            get { return min; }
            set
            {
               min = value ?? string.Empty;
               controller.TimeChanged(this);
            }
         }

         public string Time
         {
            get
            {
               string result = string.Empty;

               if (Hour.Trim().Length > 0 || Min.Trim().Length > 0)
               {
                  result = string.Format("{0}:{1}",
                     Hour.Trim().Length == 0 ? "00" : Hour.Trim(),
                     Min.Trim().Length == 0 ? "00" : Min.Trim());
               }

               return result;
            }

            set
            {
               hour = "";
               min = "";

               if (value != null)
               {
                  string[] arr = value.Split(TIME_SEPARATOR);

                  if (arr.Length == 2)
                  {
                     hour = arr[0];
                     min = arr[1];
                  }
               }
            }
         }
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsStartWork);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         btnSave.Enabled = false;
         changed.Clear();

         foreach (StartWork sw in dsStartWork.Values)
         {
            if (!userdata.ContainsKey(sw.userid))
               userdata[sw.userid] = new List<string>(new string[DATA_LENGTH]);

            userdata[sw.userid][sw.day] = sw.time;
         }

         cbAgents_SelectedIndexChanged(cbAgents, EventArgs.Empty);
      }

      public void TimeChanged(StartWorkData value)
      {
         Agent a = cbAgents.SelectedItem as Agent;

         if (a != null)
         {
            string key = string.Format("{0}{1}", a.id, value.day);
            StartWork w = new StartWork();
            w.userid = a.id;
            w.day = value.day;
            w.time = value.Time;
            changed[key] = w;

            btnSave.Enabled = true;
         }
      }

      private void grid_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         ((DataGridView)sender).CommitEdit(DataGridViewDataErrorContexts.Commit);
         ((DataGridView)sender).InvalidateRow(((DataGridView)sender).CurrentRow.Index);
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         SimpleDataSet<StartWork> ds = new SimpleDataSet<StartWork>(StartWork.OBJECT_NAME, false);

         foreach (StartWork sw in changed.Values)
            ds.Add(sw);

         upd.Add(ds);

         if (DataModule.UpdateDataSet(upd, null, null, Config.GetConfig().GetConnection()))
         {
            DialogUtil.SavedGood(this);
            btnSave.Enabled = false;
            changed.Clear();
         }else
            DialogUtil.UpdateErrMsg(this);
      }

      private void cbAgents_SelectedIndexChanged(object sender, EventArgs e)
      {
         Agent a = cbAgents.SelectedItem as Agent;

         if (a != null)
         {
            if (userdata.ContainsKey(a.id))
            {
               for (int i = 0; i < DATA_LENGTH; i++)
                  data[i].Time = userdata[a.id][i];
            }
            else
               for (int i = 0; i < DATA_LENGTH; i++)
                  data[i].Time = null;
         }

         grid.Update();
         grid.Refresh();
      }

      private void grid_DataError(object sender, DataGridViewDataErrorEventArgs e)
      {

      }
   }
}
