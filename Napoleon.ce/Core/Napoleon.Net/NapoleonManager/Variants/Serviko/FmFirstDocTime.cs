using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmFirstDocTime : Form
   {
      SimpleDataSet<FirstDocTime> dsDocTime = new SimpleDataSet<FirstDocTime>(FirstDocTime.OBJECT_NAME, false);
      Dictionary<string, Item> data = new Dictionary<string, Item>();

      public FmFirstDocTime()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
         cbDivision.SelectedIndexChanged += CbDivision_SelectedIndexChanged;
      }

      private void CbDivision_SelectedIndexChanged(object sender, EventArgs e)
      {
         Division d = cbDivision.SelectedItem as Division;
         LoadAgents(d);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         LoadData();
      }

      private void LoadData()
      {
         Manager m = CurrentUser.user as Manager;
         if (m == null)
            return;

         string where = "userid in(";
         foreach(Agent a in m.GetAgents().Data)
         {
            where += "'" + a.id + "',";
         }
         where = where.Substring(0, where.Length - 1) + ")";
         dsDocTime.Filter = where;

         List<IDataSet> sets = new List<IDataSet>();

         sets.Add(dsDocTime);

         FmWait.StdDataRefresh(this, sets, OnDataLoaded, btnRefresh);
      }

      void OnDataLoaded()
      {
         cbDivision.Items.Clear();

         Manager m = CurrentUser.user as Manager;
         foreach(Division d in m.AllDivisions)
         {
            cbDivision.Items.Add(d);
         }

         data.Clear();
         foreach(FirstDocTime fd in dsDocTime.Data)
         {
            Item i;
            if(!data.TryGetValue(fd.userid, out i))
            {
               i = new Item(fd, this);
               data.Add(fd.userid, i);
            }
            else
            {
               i.Add(fd);
            }
         }

         int idx = cbDivision.SelectedIndex;
         if (cbDivision.Items.Count > 0 && idx < 0)
         {
            cbDivision.SelectedIndex = 0;
            LoadAgents(cbDivision.SelectedItem as Division);
         }
      }

      void LoadAgents(Division d)
      {
         List<Item> src = new List<Item>();
         if(d != null)
         {
            foreach(Division.DivisionAgent a in d.agents)
            {
               if (a.agent == null) continue;

               Item i;
               if(!data.TryGetValue(a.id, out i))
               {
                  i = new Item(a.agent, this);
                  data.Add(a.id, i);
               }
               src.Add(i);
            }
         }
         dgvItems.DataSource = new SortableBindingList<Item>(src);
      }

      public void SetDirty(bool dirty)
      {
         btnSave.Enabled = dirty;
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
      }

      bool CheckChanges()
      {
         if (!btnSave.Enabled)
            return true;

         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         if (dr == DialogResult.No)
            return true;
         if (dr == DialogResult.Cancel)
            return false;

         return SaveChanges(false);
      }

      private bool SaveChanges(bool showDialog)
      {
         dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);

         SimpleDataSet<FirstDocTime> wrset = new SimpleDataSet<FirstDocTime>(FirstDocTime.OBJECT_NAME, false);
         SimpleDataSet<FirstDocTime> rmset = new SimpleDataSet<FirstDocTime>(FirstDocTime.OBJECT_NAME, false);

         foreach (Item i in data.Values)
         {
            foreach(FirstDocTime fdi in i.GetDocs())
            {
               wrset.Add(fdi);
            }
            foreach (FirstDocTime fdi in i.GetRemoved())
            {
               rmset.Add(fdi);
            }
         }

         List<IDataSet> wr = new List<IDataSet>();
         wr.Add(wrset);


         List<IDataSet>rm = new List<IDataSet>();
         rm.Add(rmset);

         bool ret = DataModule.UpdateDataSet(wr, rm, null, Config.GetConfig().GetConnection());
         if (showDialog)
         {
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
         }
         return ret;
      }


      class Item
      {
         Agent agent;
         FmFirstDocTime owner;
         public bool modified = false;

         Dictionary<int, int> data = new Dictionary<int, int>();
         public Item(FirstDocTime item, FmFirstDocTime owner)
         {
            data[item.day] = item.time;
            agent = item.agent;
            this.owner = owner;
         }

         public Item(Agent agent, FmFirstDocTime owner)
         {
            this.owner = owner;
            this.agent = agent;
         }

         public void Add(FirstDocTime item)
         {
            data[item.day] = item.time;
         }

         string GetTime(DayOfWeek d)
         {
            int time = 0;
            data.TryGetValue((int)d, out time);
            return TimeCell.ToStr(time);
         }

         void SetTime(DayOfWeek d, string value)
         {
            int v = TimeCell.From(value);
            data[(int)d] = v;
            owner.SetDirty(true);
            modified = true;
         }

         internal List<FirstDocTime> GetDocs()
         {
            List<FirstDocTime> ret = new List<FirstDocTime>();
            if(modified)
            {
               foreach (KeyValuePair<int, int> kv in data)
               {
                  if (kv.Value == 0) continue;

                  FirstDocTime fdi = new FirstDocTime();
                  fdi.userid = agent.id;
                  fdi.time = kv.Value;
                  fdi.day = kv.Key;

                  ret.Add(fdi);
               }
            }
            return ret;
         }

         internal List<FirstDocTime> GetRemoved()
         {
            List<FirstDocTime> ret = new List<FirstDocTime>();
            if (modified)
            {
               foreach (KeyValuePair<int, int> kv in data)
               {
                  if (kv.Value != 0) continue;

                  FirstDocTime fdi = new FirstDocTime();
                  fdi.userid = agent.id;
                  fdi.time = 0;
                  fdi.day = kv.Key;

                  ret.Add(fdi);
               }
            }
            return ret;
         }

         public string Name { get { return agent.name; } }

         public string Mon 
         {
            get { return GetTime(DayOfWeek.Monday); }
            set { SetTime(DayOfWeek.Monday, value); }
         }

         public string Tue
         {
            get { return GetTime(DayOfWeek.Tuesday); }
            set { SetTime(DayOfWeek.Tuesday, value); }
         }

         public string Wed
         {
            get { return GetTime(DayOfWeek.Wednesday); }
            set { SetTime(DayOfWeek.Wednesday, value); }
         }

         public string Thu
         {
            get { return GetTime(DayOfWeek.Thursday); }
            set { SetTime(DayOfWeek.Thursday, value); }
         }

         public string Fri
         {
            get { return GetTime(DayOfWeek.Friday); }
            set { SetTime(DayOfWeek.Friday, value); }
         }

         public string Sat
         {
            get { return GetTime(DayOfWeek.Saturday); }
            set { SetTime(DayOfWeek.Saturday, value); }
         }

         public string Sun
         {
            get { return GetTime(DayOfWeek.Sunday); }
            set { SetTime(DayOfWeek.Sunday, value); }
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         btnSave.Enabled = !SaveChanges(true);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         LoadData();
      }
   }
}
