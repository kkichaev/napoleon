using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class EditOrgPlan : Form
   {
      List<Agent> agents = new List<Agent>();

      Dictionary<string, DataSet<string, Org>> orgs = new Dictionary<string, DataSet<string, Org>>();
      SimpleDataSet<OrgPlan> plan = new SimpleDataSet<OrgPlan>(OrgPlan.OBJECT_NAME, false);
      DataSet<string, OrgPlan> changed = new DataSet<string, OrgPlan>(OrgPlan.OBJECT_NAME, false);
      SortableBindingList<PlanRowData> allData = new SortableBindingList<PlanRowData>();

      public EditOrgPlan()
      {
         InitializeComponent();
         
         grid.AutoGenerateColumns = false;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      public void RefreshData()
      {
         Manager dm = CurrentUser.user as Manager;
         if (agents.Count == 0)
         {
            if (dm == null)
               return;

            foreach (Agent a in dm.GetAgents().Data)
               agents.Add(a);
         }

         string uid = DataUtils.MakeFilterFromAgents(null, dm.GetAgents());

         List<IDataSet> upd = new List<IDataSet>();

         foreach (Agent a in dm.GetAgents().Data)
         {
            DataSet<string, Org> orgs =
               DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;

            if (orgs.Count == 0)
            {
               orgs.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), orgs.Name);
               upd.Add(orgs);
            }
            this.orgs[a.id] = orgs;
         }

         DateTime start = new DateTime(timepicker.Value.Year, timepicker.Value.Month, 1);
         DateTime finish = start.AddMonths(1).AddDays(-1);
         const string FILTER = " and \"start\" <= ToDate('{0:dd/MM/yyyy}') and \"finish\" >= ToDate('{1:dd/MM/yyyy}')";

         plan.Filter = uid + string.Format(FILTER, finish, start);
         upd.Add(plan);

         FmWait.StdDataRefresh(this, upd, DoLoadData, btnRefresh);
      }

      void DoLoadData()
      {
         changed.Clear();
         btnSave.Enabled = false;

         Dictionary<string, OrgPlan> data = new Dictionary<string, OrgPlan>();
         foreach (OrgPlan om in plan.Data)
            data[om.Key] = om;

         List<PlanRowData> src = new List<PlanRowData>();

         foreach (KeyValuePair<string, DataSet<string, Org>> kv in orgs)
         {
            foreach (Org o in kv.Value.Data)
            {
               OrgPlan op = new OrgPlan(kv.Key, o.id);
               if (data.ContainsKey(op.Key))
                  op = data[op.Key];
               PlanRowData mrd = new PlanRowData(op, o, DataChanged);
               src.Add(mrd);
            }
         }

         allData = new SortableBindingList<PlanRowData>(src, TypeDescriptor.GetProperties(typeof(PlanRowData))["Name"], ListSortDirection.Ascending);
         grid.DataSource = allData;
      }

      private void DataChanged(PlanRowData data)
      {
         btnSave.Enabled = true;
         DateTime now = DateTime.Now;

         OrgPlan op = new OrgPlan();
         op.userid = data.UserID;
         op.id = data.ID;
         op.value = data.Plan;
         op.created = data.Created == DateTime.MinValue ? now : data.Created ;
         data.Created = op.created;
         op.changed = now;
         data.Changed = op.changed;
         op.start = new DateTime(timepicker.Value.Year, timepicker.Value.Month, 1);
         op.finish = op.start.AddMonths(1).AddDays(-1);

         string key = op.Key;
         if (changed.ContainsKey(key))
            changed[key].value = op.value;
         else
            changed[key] = op;
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         e.Cancel = !CheckChanges();
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
         List<IDataSet> wr = new List<IDataSet>();
         wr.Add(changed);

         bool ret = DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection());
         if (showDialog)
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
         if (ret)
            changed.Clear();

         return ret;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         btnSave.Enabled = !SaveChanges(true);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         if (CheckChanges())
            RefreshData();
      }

      private void tbSetCheck_Click(object sender, EventArgs e)
      {
         Checking(true);
      }

      private void tbResetCheck_Click(object sender, EventArgs e)
      {
         Checking(false);
      }

      private void Checking(bool val)
      {
         int i = 0;

         SortableBindingList<PlanRowData> src = (SortableBindingList<PlanRowData>)grid.DataSource;
         foreach (PlanRowData mrd in src)
         {
            mrd.Checked = val;
            src.ResetItem(i++);
         }
      }

      private void btnApply_Click(object sender, EventArgs e)
      {
         double val = 0;
         if (!Double.TryParse(tbPlan.Text, out val))
         {
            MessageBox.Show("Ошибка при вводе числа");
            return;
         }

         SortableBindingList<PlanRowData> src = (SortableBindingList<PlanRowData>)grid.DataSource;
         int i = 0;
         foreach (PlanRowData mrd in src)
         {
            if (mrd.Checked)
            {
               mrd.Plan = val;
               src.ResetItem(i);
            }
            i++;
         }
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         timer1.Stop();
         DoSearch(tbFind.Text);
      }

      private void DoSearch(string p)
      {
         p = p.ToUpper();

         List<PlanRowData> src = new List<PlanRowData>();
         foreach (PlanRowData mrd in allData)
         {
            if (mrd.Owner.ToUpper().Contains(p) || mrd.Name.ToUpper().Contains(p))
               src.Add(mrd);
         }

         grid.DataSource = new SortableBindingList<PlanRowData>(src);
      }

      private void btnClearFind_Click(object sender, EventArgs e)
      {
         timer1.Stop();
         clearing = true;
         tbFind.Clear();

         grid.DataSource = allData;

         clearing = false;
      }

      bool clearing = false;

      private void tbFind_TextChanged(object sender, EventArgs e)
      {
         timer1.Stop();

         if (tbFind.Text.Length > 0)
            timer1.Start();
         else if (!clearing)
            btnClearFind_Click(sender, e);
      }

      private void grid_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         PlanRowData sd = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as PlanRowData;

         if (sd.Checked)
         {
            e.CellStyle.BackColor = Color.LightSteelBlue;
         }
         else
         {
            if (sd != null && sd.Created != sd.Changed)
               e.CellStyle.BackColor = Color.Gray;
            else
               e.CellStyle.BackColor = Color.White;
         }

         if (sd.Plan == 0)
            e.CellStyle.Font = new Font(((DataGridView)sender).Font, FontStyle.Bold);
         else
            e.CellStyle.Font = ((DataGridView)sender).Font;
      }

      private void grid_DataError(object sender, DataGridViewDataErrorEventArgs e)
      {
         if (e.Exception is FormatException)
            MessageBox.Show(this, "Недопустимый сивло для поля, доступны цифровые значения и знак \",\"", "Ошибка", 
               MessageBoxButtons.OK, MessageBoxIcon.Error);
      }

      private void grid_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         if (grid.CurrentCell.ColumnIndex == clmnChecked.DisplayIndex)
         {
            grid.CommitEdit(DataGridViewDataErrorContexts.Commit);
            grid.InvalidateRow(grid.CurrentCell.RowIndex);
         }
      }

      private void btnPlanLoad_Click(object sender, EventArgs e)
      {
         FmLoadPlan f = new FmLoadPlan();
         f.dataForm = this;
         f.Show();
      }

      private void grid_CellEndEdit(object sender, DataGridViewCellEventArgs e)
      {
         if (grid.CurrentCell.ColumnIndex == clmnMargin.DisplayIndex)
         {
            grid.InvalidateRow(grid.CurrentCell.RowIndex);
         }
      }
   }


   public class PlanRowData : IComparable<PlanRowData>
   {
      public delegate void ChangedData(PlanRowData data);

      Org org = null;
      string userid = null;
      double value;
      DateTime created;
      DateTime changed;

      ChangedData Handler;
      bool check = false;

      public PlanRowData(OrgPlan src, Org org, ChangedData Handler)
      {
         this.org = org;
         created = src.created;
         userid = src.userid;
         value = src.value;
         changed = src.changed;

         this.Handler = Handler;
      }

      public double Plan
      {
         get { return value; }
         set
         {
            if (this.value != value)
            {
               this.value = value;
               Handler(this);
            }
         }
      }

      public string Name { get { return org.Name; } }
      public string Address { get { return org.Address; } }
      public string Owner { get { return org.ownerData; } }

      public string UserID { get { return userid; } }
      public string ID { get { return org.id; } }

      public Org Org { get { return org; } }

      public int CompareTo(PlanRowData other)
      {
         return org.Name.CompareTo(other.org.Name);
      }

      public DateTime Created { get { return created; } set { created = value; } }
      public DateTime Changed { get { return changed; } set { changed = value; } }
      public bool Checked { get { return check; } set { check = value; } }
   }
}
