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
   public partial class FmAgentPlanAssign : Form
   {
      static FmAgentPlanAssign instance = null;
      SimpleDataSet<PCAgentMonthlyPlans> plans = new SimpleDataSet<PCAgentMonthlyPlans>(PCAgentMonthlyPlans.OBJECT_NAME, false);

      public FmAgentPlanAssign()
      {
         InitializeComponent();
         dtpDate.Value = DateTime.Now.Date;
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         if (!CheckChanges())
         {
            e.Cancel = true;
            return;
         }
         instance = null;
         base.OnClosing(e);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();
         DateTime dtPlan = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month, 1);
         plans.Filter = String.Format("\"date\" >= ToDate('{0:dd/MM/yyyy}') and \"date\" <= ToDate('{1:dd/MM/yyyy}')", dtPlan.Date, dtPlan.AddMonths(1).Date);
         upd.Add(plans);
         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      void DoLoadData()
      {
         List<DataItem> data = new List<DataItem>();
         Manager m = CurrentUser.user as Manager;
         
         foreach(Agent a in m.GetAgents().Data)
         {
            DataItem item = new DataItem(GetPlan(a), this);
            data.Add(item);
         }
         data.Sort();

         dgvItems.DataSource = new SortableBindingList<DataItem>(data);
      }

      private PCAgentMonthlyPlans GetPlan(Agent a)
      {
         foreach(PCAgentMonthlyPlans item in plans.Data)
         {
            if(item.agent == a)
               return item;
         }

         DateTime dtPlan = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month, 1);
         
         PCAgentMonthlyPlans pi = new PCAgentMonthlyPlans();
         pi.userid = a.id;
         pi.agent = a;
         pi.date = dtPlan;

         plans.Add(pi);
         return pi;
      }

      bool CheckChanges()
      {
         if (!tsbSave.Enabled)
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
         List<IDataSet> wr = new List<IDataSet>();

         SimpleDataSet<PCAgentMonthlyPlans> wrPlans = new SimpleDataSet<PCAgentMonthlyPlans>(PCAgentMonthlyPlans.OBJECT_NAME, false);
         foreach(DataItem di in (SortableBindingList<DataItem>)dgvItems.DataSource)
         {
            wrPlans.Add(di.item);
         }
         wr.Add(wrPlans);

         bool ret = DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection());
         if (ret)
         {
            MarkDirty(false);
         }

         if (showDialog)
         {
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
         }

         return ret;
      }

      public static void Open()
      {
         if(instance == null)
         {
            instance = new FmAgentPlanAssign();
            instance.Show();
         }
         else
         {
            instance.BringToFront();
         }
      }

      public void MarkDirty(bool dirty)
      {
         tsbSave.Enabled = dirty;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      class DataItem : IComparable<DataItem>
      {
         public PCAgentMonthlyPlans item = null;
         FmAgentPlanAssign owner;

         public DataItem(PCAgentMonthlyPlans item, FmAgentPlanAssign owner)
         {
            this.item = item;
            this.owner = owner;
         }

         public string Name { get { return item.agent.Name; } }

         public double Plan
         {
            get { return item.plan; }
            set
            {
               if (item.plan != value)
               {
                  item.plan = value;
                  owner.MarkDirty(true);
               }
            }
         }

         public double PDZ
         {
            get { return item.pdz; }
            set
            {
               if (item.pdz != value)
               {
                  item.pdz = value;
                  owner.MarkDirty(true);
               }
            }
         }

         public int CompareTo(DataItem other)
         {
            return Name.CompareTo(other.Name);
         }
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }
   }
}
