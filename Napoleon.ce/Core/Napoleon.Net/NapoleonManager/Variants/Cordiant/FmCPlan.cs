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
   public partial class FmCPlan : Form
   {
      SimpleDataSet<CPlan> dsPlan = new SimpleDataSet<CPlan>(CPlan.OBJECT_NAME);

      public FmCPlan()
      {
         InitializeComponent();

         grid.AutoGenerateColumns = false;
         grid.DataSource = PlanData.FromPlan(new CPlan());
      }

      private void FmCPlan_Load(object sender, EventArgs e)
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
         }

         DateTime min = DateTime.MinValue.Date;

         while (min < DateTime.MaxValue.AddMonths(-3))
         {
            domainUpDown1.Items.Add(new QuartedDateTime(min));
            min = min.AddMonths(3);
         }

         int q = GetQuarted(DateTime.Now);
         domainUpDown1.SelectedItemChanged -= domainUpDown1_SelectedItemChanged;
         domainUpDown1.SelectedItem = new QuartedDateTime(new DateTime(DateTime.Now.Year, GetQuartedStartMonth(q), 1));
         domainUpDown1.SelectedItemChanged += domainUpDown1_SelectedItemChanged;

         Reload();
      }

      public static int GetQuarted(DateTime date)
      {
         return (date.Month + 2) / 3;
      }

      public static int GetQuartedStartMonth(int quarted)
      {
         return quarted * 3 - 2;
      }

      private void grid_CellValueChanged(object sender, DataGridViewCellEventArgs e)
      {
         btnSave.Enabled = true;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         grid.CommitEdit(DataGridViewDataErrorContexts.Commit);

         CPlan plan = ((PlanData)grid.DataSource).ToPlan();
         plan.userid = ((Agent)cbAgents.SelectedItem).id;
         plan.date = ((QuartedDateTime)domainUpDown1.SelectedItem).value;

         List<IDataSet> wr = new List<IDataSet>();
        
         dsPlan.Add(plan);
         wr.Add(dsPlan);

         if (DataModule.WriteDataSet(wr, Config.GetConfig().GetConnection()))
         {
            DialogUtil.SavedGood(this);
            btnSave.Enabled = false;
         }
         else
            DialogUtil.UpdateErrMsg(this);
      }

      public void Reload()
      {
         List<IDataSet> upd = new List<IDataSet>();
         dsPlan.Filter = String.Format("\"date\"=ToDate('{0:dd/MM/yyyy}') and \"userid\"='{1}'",
            ((QuartedDateTime)domainUpDown1.SelectedItem).value, ((Agent)cbAgents.SelectedItem).id);

         upd.Add(dsPlan);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         if (dsPlan.Count > 0)
            grid.DataSource = PlanData.FromPlan(dsPlan[0]);
         else
            grid.DataSource = PlanData.FromPlan(new CPlan());

         btnSave.Enabled = false;
      }

      private void cbAgents_SelectedIndexChanged(object sender, EventArgs e)
      {
         Reload();
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         Reload();
      }

      private void domainUpDown1_SelectedItemChanged(object sender, EventArgs e)
      {
         Reload();
      }

      private void FmCPlan_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && DialogUtil.AskToSave(this))
            btnSave.PerformClick();
      }
   }

   class QuartedDateTime
   {
      public DateTime value = DateTime.MinValue;

      public QuartedDateTime(DateTime value)
      {
         this.value = value;
      }

      public override string ToString()
      {
         return String.Format("{0} кв. {1}", (value.Month + 2) / 3, value.Year);
      }

      public override bool Equals(object obj)
      {
         return obj.Equals(value);
      }

      public override int GetHashCode()
      {
         return value.GetHashCode();
      }
   }

   class PlanItem
   {
      public string Title { get; set; }
      public string Field { get; set; }
      public double Value { get; set; }
   }

   class PlanData : BindingList<PlanItem>
   {
      public static PlanData FromPlan(CPlan plan)
      {
         PlanData res = new PlanData();
         res.Items.Clear();

         res.Items.Add(new PlanItem()
         {
            Title = "SKU Лето",
            Field = "summer",
            Value = plan.summer
             
         });

         res.Items.Add(new PlanItem()
         {
            Title = "KEY SKU Лето",
            Field = "keySummer",
            Value = plan.keySummer

         });

         res.Items.Add(new PlanItem()
         {
            Title = "17-18 дюйм Лето",
            Field = "d17_18Summer",
            Value = plan.d17_18Summer
         });

         res.Items.Add(new PlanItem()
         {
            Title = "SKU Зима",
            Field = "winter",
            Value = plan.winter
         });

         res.Items.Add(new PlanItem()
         {
            Title = "KEY SKU Зима",
            Field = "keyWinter",
            Value = plan.keyWinter
         });

         res.Items.Add(new PlanItem()
         {
            Title = "17-18 дюйм Зима",
            Field = "d17_18Winter",
            Value = plan.d17_18Winter
         });

         res.Items.Add(new PlanItem()
         {
            Title = "ЛГШ",
            Field = "lgsh",
            Value = plan.lgsh
         });

         return res;
      }

      public CPlan ToPlan()
      {
         CPlan res = new CPlan();

         for (int i = 0; i < Count; i++)
         {
            PlanItem t = Items[i];
            res.GetType().GetField(t.Field).SetValue(res, t.Value);
         }

         return res;
      }
   }
}
