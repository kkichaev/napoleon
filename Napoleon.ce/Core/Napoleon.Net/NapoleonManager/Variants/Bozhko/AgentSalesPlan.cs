using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Collections;

namespace GRSoft.NapoleonManager
{
   public partial class AgentSalesPlan : Form
   {
      Agents agents;
      SimpleDataSet<AgentPlanData> dsPlans;

      Agent curentAgent;
      AgentPlanData currentPlan;

      public AgentSalesPlan()
      {
         InitializeComponent();
         
         dtpStart.Value = DateTime.Now.AddDays(-7);
         dtpEnd.Value = DateTime.Now.AddDays(7);
         dsPlans = new SimpleDataSet<AgentPlanData>(AgentPlanData.OBJECT_NAME, false);

         dgvAgents.AutoGenerateColumns = false;
         dgvItems.AutoGenerateColumns = false;
         dgvPlans.AutoGenerateColumns = false;

         LoadData();
      }

      private void LoadData()
      {
         Manager m = (CurrentUser.user as Manager);
         agents = m.GetAgents();

         List<IDataSet> updSets = new List<IDataSet>();

         DataSet<string, Price> dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         if (dsPrice.Count == 0)
         {
            dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            updSets.Add(dsPrice);
         }

         DataSet<string, ManagerFolder> dsFolders = (DataSet<string, ManagerFolder>)DataModule.Get(ManagerFolder.OBJECT_NAME) ?? new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);
         if (dsFolders.Count == 0)
         {
            dsFolders.Filter = DataUtils.USERID_IS_NULL_STR;
            updSets.Add(dsFolders);
         }

         string uidFilter = DataUtils.MakeFilterFromAgents(null, agents);
         //string dateFilter = String.Format("(\"dateStart\" >= ToDate('{0:dd/MM/yyyy}') and \"dateStart\" <= ToDate('{1:dd/MM/yyyy} 23:59:59'))",
         //   dtpStart.Value, dtpEnd.Value);
         string dateFilter = String.Format("(\"dateStart\" >= ToDate('{0:dd/MM/yyyy}') and \"dateStart\" <= ToDate('{1:dd/MM/yyyy} 23:59:59')) or ((\"dateEnd\" >= ToDate('{0:dd/MM/yyyy}') and \"dateEnd\" <= ToDate('{1:dd/MM/yyyy} 23:59:59')))",
            dtpStart.Value, dtpEnd.Value);

         dsPlans.Filter = dateFilter + " and " + uidFilter;
         updSets.Add(dsPlans);

         btnRefresh.Enabled = false;
         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(MainForm.Instance.conn, updSets, FmWait.ProgressIndicator));
      }

      void ReloadData()
      {
         bool refreshPlans = (dgvAgents.CurrentRow != null);
         List<Agent> asrc = new List<Agent>((IEnumerable<Agent>)agents.Data);
         dgvAgents.DataSource = asrc;
         if (refreshPlans)
         {
            Agent sv = curentAgent;
            curentAgent = null;
            LoadPlans(sv);
         }

         MarkDirty(false);
      }

      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();
         this.Invoke(new InvokeDelegate(delegate
         {
            FmWait.CloseForm();
            btnRefresh.Enabled = true;
            ReloadData();
         }));
      }

      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();
         Invoke(new EmptyParamHandler(delegate
         {
            FmWait.CloseForm();
            btnRefresh.Enabled = true;
            MessageBox.Show(e.Msg, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
         }));
      }

      private void dgvAgents_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         Agent curAgent = dgvAgents.Rows[e.RowIndex].DataBoundItem as Agent;
         LoadPlans(curAgent);
      }

      private void dgvPlans_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         AgentPlanData plan = dgvPlans.Rows[e.RowIndex].DataBoundItem as AgentPlanData;

         Color foreColor =
            (plan.IsActive) ? Color.Black :
            (plan.IsFuture) ? Color.LightGreen :
            Color.LightGray;

         e.CellStyle.ForeColor = foreColor;
      }

      private void dgvPlans_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         AgentPlanData plan = dgvPlans.Rows[e.RowIndex].DataBoundItem as AgentPlanData;
         LoadItems(plan);
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         if (!CanChange())
         {
            e.Cancel = true;
            return;
         }
         base.OnClosing(e);
      }

      bool CanChange()
      {
         if (!IsDirty || currentPlan == null)
            return true;

         DialogResult res = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         if (res == DialogResult.No)
         {
            MarkDirty(false);
            return true;
         }

         if (res == DialogResult.Cancel)
            return false;

         return SavePlan(currentPlan);
      }

      void SetCurrentRow(DataGridView dgv, object boundedObject)
      {
         foreach (DataGridViewRow r in dgv.Rows)
         {
            if (r.DataBoundItem == boundedObject)
            {
               if (dgv.CurrentCell != null)
                  dgv.CurrentCell = r.Cells[clmnItemName.Index];
               break;
            }
         }
      }

      private void LoadPlans(Agent curAgent)
      {
         if (curentAgent == curAgent)
            return;

         if (!CanChange())
         {
            SetCurrentRow(dgvAgents, curentAgent);
            return;
         }

         curentAgent = curAgent;
         List<AgentPlanData> plans = new List<AgentPlanData>();
         foreach (AgentPlanData pi in dsPlans.Data)
            if (pi.userid.Equals(curentAgent.id))
               plans.Add(pi);
         plans.Sort();

         dgvPlans.DataSource = plans;
      }

      void RefreshItems()
      {
         List<AgentPlanData.Item> items = new List<AgentPlanData.Item>();
         currentPlan.items.ForEach(i => items.Add(i));
         items.Sort();

         dgvItems.DataSource = items;
      }

      private void LoadItems(AgentPlanData plan)
      {
         if (currentPlan == plan)
            return;

         if (!CanChange())
         {
            SetCurrentRow(dgvPlans, currentPlan);
            return;
         }

         currentPlan = plan;
         RefreshItems();
      }

      private void btnDelPlan_Click(object sender, EventArgs e)
      {
         if( dgvPlans.SelectedRows.Count == 0 )
            return;

         String text = "Удалить план";
         if (dgvPlans.SelectedRows.Count > 1)
            text += "ы";
         text += "?";

         if (MessageBox.Show(text, "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
         {
            SimpleDataSet<AgentPlanData> rmv = new SimpleDataSet<AgentPlanData>(AgentPlanData.OBJECT_NAME, false);
            foreach (DataGridViewRow r in dgvPlans.SelectedRows)
               rmv.Add(r.DataBoundItem as AgentPlanData);

            List<IDataSet> rmvSet = new List<IDataSet>(new IDataSet[] { rmv });
            if (DataModule.UpdateDataSet(null, rmvSet, null, MainForm.Instance.conn))
            {
               foreach (DataGridViewRow r in dgvPlans.SelectedRows)
                  dgvPlans.Rows.Remove(r);
            }
         }
      }

      private void btnDelItem_Click(object sender, EventArgs e)
      {
         List<DataGridViewRow> selected = new List<DataGridViewRow>();
         foreach (DataGridViewCell c in dgvItems.SelectedCells)
         {
            DataGridViewRow r = dgvItems.Rows[c.RowIndex];
            if (!selected.Contains(r))
               selected.Add(r);
         }

         if (selected.Count == 0 || currentPlan == null)
            return;

         String text = "Удалить товар";
         if (selected.Count > 1)
            text += "ы";
         text += "?";

         if (MessageBox.Show(text, "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
         {
            foreach (DataGridViewRow r in selected)
               currentPlan.items.Remove(r.DataBoundItem as AgentPlanData.Item);

            RefreshItems();
            MarkDirty(true);
         }
      }

      private void MarkDirty(bool isDirty)
      {
         btnSavePlan.Enabled = isDirty;
      }

      bool IsDirty { get { return btnSavePlan.Enabled; } }

      bool SavePlan(AgentPlanData plan)
      {
         SimpleDataSet<AgentPlanData> wr = new SimpleDataSet<AgentPlanData>(AgentPlanData.OBJECT_NAME, false);
         wr.Add(plan);
         List<IDataSet> wrSet = new List<IDataSet>(new IDataSet[] { wr });

         bool res = DataModule.UpdateDataSet(wrSet, null, null, MainForm.Instance.conn);
         if (res)
            MarkDirty(false);
         else
            MessageBox.Show("Ошибка при записи плана", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);

         return res;
      }

      private void btnSavePlan_Click(object sender, EventArgs e)
      {
         if (currentPlan == null)
            return;

         SavePlan(currentPlan);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         if (!CanChange())
            return;
         LoadData();
      }

      private void tbAddPlan_Click(object sender, EventArgs e)
      {
         if (curentAgent == null)
            return;

         AgentPlanData plan = new AgentPlanData();
         plan.agent = curentAgent;
         plan.userid = curentAgent.id;
         plan.dateStart = DateTime.Now.Date;
         plan.dateEnd = DateTime.Now.Date.AddDays(3);

         AgentSalesPlanEdit form = new AgentSalesPlanEdit();
         List<AgentPlanData> plans = (List<AgentPlanData>)dgvPlans.DataSource;
         form.Plan = plan;
         form.Plans = plans;
         if (form.ShowDialog() == DialogResult.OK)
         {
            plan = form.Plan;
            plan.items = new List<AgentPlanData.Item>();

            if (SavePlan(plan))
            {
               plans.Add(plan);
               dgvPlans.DataSource = new List<AgentPlanData>((IEnumerable<AgentPlanData>)plans);

               currentPlan = plan;
               SetCurrentRow(dgvPlans, currentPlan);
            }
         }
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         if (currentPlan == null)
            return;

         AgentSalesPlanEdit form = new AgentSalesPlanEdit();
         form.Plan = currentPlan;
         form.Plans = (List<AgentPlanData>)dgvPlans.DataSource;
         if (form.ShowDialog() == DialogResult.OK)
         {
            AgentPlanData plan = form.Plan;
            if (SavePlan(plan))
            {
               currentPlan.SetFrom(plan);
               dgvPlans.InvalidateRow(dgvPlans.Rows.IndexOf(dgvPlans.CurrentRow));
            }
         }
      }

      private void btnAddItem_Click(object sender, EventArgs e)
      {
         if( currentPlan == null )
            return;
         
         List<Price> check = new List<Price>();
         foreach(AgentPlanData.Item i in currentPlan.items)
         {
            if( i.item != null )
               check.Add(i.item);
         }

         List<Price> res = FmSelectSKU.SelectItems(this, check, curentAgent.id, true);
         if (res == null)
            return;

         List<AgentPlanData.Item> removed = new List<AgentPlanData.Item>();
         foreach (AgentPlanData.Item i in currentPlan.items)
            if (!res.Remove(i.item))
               removed.Add(i);

         if (res.Count > 0 || removed.Count > 0)
         {
            MarkDirty(true);
            currentPlan.items.RemoveAll(obj => removed.Contains(obj));

            foreach (Price pi in res)
            {
               AgentPlanData.Item api = new AgentPlanData.Item();
               api.item = pi;
               api.id = pi.id;
               api.qty = 1;

               currentPlan.items.Add(api);
            }

            RefreshItems();
         }
      }

      private void dgvItems_CellValueChanged(object sender, DataGridViewCellEventArgs e)
      {
         if (e.ColumnIndex == clmnPlanQty.Index)
            MarkDirty(true);
      }

      private void dgvPlans_CellMouseDoubleClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         btnEdit_Click(this, EventArgs.Empty);
      }

      private void btnReport_Click(object sender, EventArgs e)
      {
         AgentSalesPlanReport form = new AgentSalesPlanReport();
         form.StartDate = dtpStart.Value;
         form.EndDate = dtpEnd.Value;
         form.ShowDialog();
      }
   }

   public class AgentPlanData : GRSoft.Network.DataObject, IComparable<AgentPlanData>
   {
      public static readonly string OBJECT_NAME = "AgentSalesPlan";

      [Reference("Agents", "userid")]
      public Agent agent = null;

      public String userid;
      public String name;
      public DateTime dateStart;
      public DateTime dateEnd;

      public class Item : GRSoft.Network.DataObject, IComparable<Item>
      {
         [Reference("ManagerPrice", "id", typeof(Price))]
         public Price item = null;
         public String id;
         public double qty;

         public string Name { get { return item == null ? "Товар с кодом '" + id + "'" : item.Name; } }
         public double Qty
         {
            get { return qty; }
            set { qty = value; }
         }

         public int CompareTo(Item other) { return Name.CompareTo(other.Name); }
      }

      [ItemType(typeof(Item))]
      public List<Item> items;

      public string Name { get { return name; } }
      public DateTime Start { get { return dateStart; } }
      public DateTime End { get { return dateEnd; } }

      public AgentPlanData Copy() { return (AgentPlanData)MemberwiseClone(); }

      public void SetFrom(AgentPlanData src)
      {
         name = src.name;
         dateStart = src.dateStart;
         dateEnd = src.dateEnd;
      }

      public bool IsActive
      {
         get
         {
            DateTime now = DateTime.Now.Date;
            return now.CompareTo(dateStart) >= 0 && now.CompareTo(dateEnd) <= 0;
         }
      }

      public bool IsFuture { get { return DateTime.Now.Date.CompareTo(dateStart) < 0; } }

      public int CompareTo(AgentPlanData other)
      {
         int cmp = 0;
         if (IsActive) cmp++;
         if (other.IsActive) cmp--;
         if (cmp == 0)
            cmp = dateStart.CompareTo(other.dateStart);

         // делаем инверсию. Сначала активные, затем по убыванию даты
         return (cmp < 0) ? 1 :
                (cmp > 0) ? -1 :
                0;
      }
   }
}
