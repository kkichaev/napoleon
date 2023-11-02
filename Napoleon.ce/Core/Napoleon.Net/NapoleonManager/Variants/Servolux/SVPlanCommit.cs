using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   public partial class SVPlanCommit : Form
   {
      static SVPlanCommit instance = null;

      DateTime minDate = DateTime.MaxValue, maxDate = DateTime.MinValue;
      bool planChangesLoaded = false;

      //SimpleDataSet<OrderAddConfig> firms;
      SimpleDataSet<PlanChanges> planChanges = new SimpleDataSet<PlanChanges>(PlanChanges.OBJECT_NAME, false);
      SimpleDataSet<SVPlanData> svPlans = new SimpleDataSet<SVPlanData>(SVPlanData.OBJECT_NAME);
      Dictionary<String, String> groups = new Dictionary<string, string>();

      SVPlanCommit()
      {
         InitializeComponent();

         dgvPlans.AutoGenerateColumns = false;
         dgvAgents.AutoGenerateColumns = false;
      }

      static public void Open()
      {
         if (instance == null)
         {
            instance = new SVPlanCommit();
            instance.Show();
         }
         else
         {
            instance.RefreshData();
            instance.BringToFront();
         }
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();
         Manager cu = ((Manager)CurrentUser.user);
         string uid = DataUtils.MakeFilterFromAgents(null, cu.Agents);

         svPlans.Filter = "\"userid\" = '" + cu.User.login + "'";
         upd.Add(svPlans);

         //firms = DataModule.Get("Firms") as SimpleDataSet<OrderAddConfig> ?? new SimpleDataSet<OrderAddConfig>("Firms", true);
         //if (firms.Count == 0)
         //   upd.Add(firms);

         DataModule.DataProcessed += new EventHandler((o, e) =>
         {
            DataModule.ClearEvents();
            FmWait.CloseForm();

            Invoke(new EmptyParamHandler(delegate { DoLoadData(); }));
         });

         DataModule.OnDataResponceError += new EventDataResponseError(FmWait.StdErrorHandler);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), upd, FmWait.ProgressIndicator));
      }

      void DoLoadData()
      {
         if (groups.Count == 0)
            LoadGroups();

         List<PlanDataItem> items = new List<PlanDataItem>();
         DataSet<String, Price> dsPrice = (DataSet<String, Price>)DataModule.Get(Price.OBJECT_NAME);
         foreach (SVPlanData sdi in svPlans.Data)
         {
            PlanDataItem item = new PlanDataItem();
            item.item = dsPrice[sdi.id];
            item.data = sdi;
            item.group = groups.ContainsKey(sdi.id) ? groups[sdi.id] : "";
            item.factory = GetFactory(sdi.firm);
            items.Add(item);

            if (minDate > sdi.date)
               minDate = sdi.date;
            if (maxDate < sdi.date)
               maxDate = sdi.date;
         }

         items.Sort();
         dgvPlans.DataSource = items;
      }

      Dictionary<String, Factory> fcach = new Dictionary<string, Factory>();
      private string GetFactory(string p)
      {
         if (fcach.ContainsKey(p))
            return fcach[p].name;

         List<Factory> fl = Factory.GetFactories();
         foreach (Factory f in fl)
         {
            if (f.id == p)
            {
               fcach[p] = f;
               return f.name;
            }
         }
         return "";
      }

      private void LoadGroups()
      {
         SimpleDataSet<PlanGroup> dsGroups = (SimpleDataSet<PlanGroup>)DataModule.Get(PlanGroup.OBJECT_NAME);
         foreach (PlanGroup pg in dsGroups.Data)
         {
            if (groups.ContainsKey(pg.id) == false)
               groups[pg.id] = pg.group;
         }
         //foreach (Division.DivisionAgent a in ((Manager)CurrentUser.user).Agents)
         //{
         //   SimpleDataSet<AgentDailyPlans> plans =
         //      DataModule.GetUserDataSet(a.id, AgentDailyPlans.OBJECT_NAME, typeof(SimpleDataSet<AgentDailyPlans>)) as SimpleDataSet<AgentDailyPlans>;

         //   foreach (AgentDailyPlans adp in plans.Data)
         //   {
         //      if (adp.group == null || adp.group.Length == 0)
         //         continue;
         //      if (groups.ContainsKey(adp.id) == false)
         //         groups[adp.id] = adp.group;
         //   }
         //}
      }

      class PlanDataItem : IComparable<PlanDataItem>
      {
         public Price item;
         public String group;
         public SVPlanData data;
         public string factory;

         #region IComparable<PlanDataItem> Members

         public int CompareTo(PlanDataItem other)
         {
            int cmp = factory.CompareTo(other.factory);
            if (cmp != 0)
               return cmp;
            cmp = data.date.CompareTo(other.data.date);
            if (cmp != 0)
               return cmp;

            cmp = Name.CompareTo(other.Name);
            if (cmp != 0)
               return cmp;

            cmp = group.CompareTo(other.group);
            return cmp != 0 ? cmp : State.CompareTo(other.State);
         }

         #endregion

         public string Name { get { return item.name; } }
         public string State { get { return item.thermalState + "/" + item.packName; } }
         public DateTime Date { get { return data.date; } }
         public double Qty { get { return data.qty; } }
         public string Factory { get { return factory; } }
      }

      class AgentDataItem : IComparable<AgentDataItem>
      {
         public Agent agent = null;
         public PlanChanges data;
         public double plan = 0;

         #region IComparable<AgentDataItem> Members

         public int CompareTo(AgentDataItem other)
         {
            return agent.Name.CompareTo(other.agent.Name);
         }

         #endregion

         public string Agent { get { return agent.Name; } }
         public double Qty { get { return plan + data.qty; } }
      }

      private void dgvPlans_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         PlanDataItem di = (PlanDataItem)dgvPlans.Rows[e.RowIndex].DataBoundItem;
         Color backColor = di.group == null || di.group.Length == 0 ? dgvPlans.DefaultCellStyle.BackColor : Color.LightBlue;
         e.CellStyle.BackColor = backColor;
      }

      private void dgvPlans_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         PlanDataItem pdi = dgvPlans.Rows[e.RowIndex].DataBoundItem as PlanDataItem;
         RefreshAgents(pdi);
      }

      protected override void OnClosed(EventArgs e)
      {
         instance = null;
         base.OnClosed(e);
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
      }

      private void RefreshAgents(PlanDataItem pdi)
      {
         CheckAndLoadPlans();

         Agents agents = Agents.GetDataSet();
         Manager cu = ((Manager)CurrentUser.user);
         List<AgentDataItem> srcItems = new List<AgentDataItem>();
         //foreach (Division.DivisionAgent a in cu.Agents)
         //{
         //   SimpleDataSet<AgentDailyPlans> plans =
         //      DataModule.GetUserDataSet(a.id, AgentDailyPlans.OBJECT_NAME, typeof(SimpleDataSet<AgentDailyPlans>)) as SimpleDataSet<AgentDailyPlans>;

         //   foreach (AgentDailyPlans adp in plans.Data)
         //   {
         //      if (adp.firm != pdi.data.firm || adp.date != pdi.data.date || adp.id != pdi.data.id)
         //         continue;

         //      AgentDataItem adi = new AgentDataItem();
         //      adi.agent = agents[a.id];
         //      adi.plan = adp.qty;
         //      srcItems.Add(adi);

         //   }
         //}

         Dictionary<string, PlanChanges> items = new Dictionary<string, PlanChanges>();
         foreach (PlanChanges pi in planChanges.Data)
         {
            if (pi.firm != pdi.data.firm || pi.date != pdi.data.date || pi.id != pdi.data.id)
               continue;
            items[pi.userid] = pi;
         }

         foreach (AgentDataItem item in srcItems)
         {
            if( items.ContainsKey(item.agent.id) )
               item.data = items[item.agent.id];
            else
            {
               PlanChanges fake = new PlanChanges();
               fake.created = DateTime.MinValue;
               fake.date = pdi.data.date;
               fake.id = pdi.data.id;
               fake.qty = 0;
               fake.userid = item.agent.id;
               fake.firm = pdi.data.firm;

               item.data = fake;
            }
         }

         srcItems.Sort();
         dgvAgents.DataSource = srcItems;
      }

      private void CheckAndLoadPlans()
      {
         if (planChangesLoaded == false)
         {
            List<IDataSet> upd = new List<IDataSet>();
            Manager cu = ((Manager)CurrentUser.user);
            string uid = DataUtils.MakeFilterFromAgents(null, cu.Agents);

            planChanges.Filter = uid +
               String.Format(" and \"date\" >= ToDate('{0:dd/MM/yyyy}') and \"date\" <= ToDate('{1:dd/MM/yyyy})", minDate, maxDate);
            upd.Add(planChanges);

            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), upd, null).Join();

            planChangesLoaded = true;
         }
      }

      private void dgvAgents_CellEnter(object sender, DataGridViewCellEventArgs e)
      {
         if (e.ColumnIndex == clmnAgentQty.DisplayIndex)
         {
            PlanDataItem pdi = dgvPlans.CurrentRow.DataBoundItem as PlanDataItem;
            Rectangle r = dgvAgents.GetCellDisplayRectangle(e.ColumnIndex, e.RowIndex, true);

            InputQty iq = new InputQty();

            double val = pdi.Qty;
            iq.Location = dgvAgents.PointToScreen(new Point(r.Left - iq.Width, r.Bottom - iq.Height));
            iq.Qty = val;
            iq.MaxValue = (int)(val + 0.005);
            if (iq.ShowDialog() == DialogResult.OK)
            {
               val = iq.Qty;
               pdi.data.qty -= val;
               dgvPlans.InvalidateCell(clmnPlanQty.DisplayIndex, dgvPlans.CurrentRow.Index);

               AgentDataItem adi = dgvAgents.Rows[e.RowIndex].DataBoundItem as AgentDataItem;
                  adi.data.qty += val;
               if (adi.data.created == DateTime.MinValue) // is fake
               {
                  adi.data.created = DateTime.Now;
                  planChanges.Add(adi.data);
               }
               dgvAgents.InvalidateCell(clmnAgentQty.DisplayIndex, e.RowIndex);

               if (pdi.group != null && pdi.group.Length > 0)
               {
                  UpdateGroupItems(pdi, val, adi);
               }

               tsbSave.Enabled = true;
            }
         }
      }

      private void UpdateGroupItems(PlanDataItem pdi, double val, AgentDataItem adi)
      {
         foreach (DataGridViewRow row in dgvPlans.Rows)
         {
            PlanDataItem dbi = row.DataBoundItem as PlanDataItem;
            if (dbi.group == pdi.group && dbi != pdi)
            {
               dbi.data.qty -= val;
               dgvPlans.InvalidateCell(clmnPlanQty.DisplayIndex, row.Index);

               // update plan changes
               bool finded = false;
               foreach (PlanChanges pc in planChanges.Data)
               {
                  if (pc.firm != dbi.data.firm || pc.date != dbi.data.date || pc.id != dbi.data.id || pc.userid != adi.agent.id)
                     continue;
                  finded = true;
                  pc.qty += val;
                  break;
               }

               if (!finded)
               {
                  PlanChanges fake = new PlanChanges();
                  fake.created = DateTime.Now;
                  fake.date = dbi.data.date;
                  fake.id = dbi.data.id;
                  fake.qty = val;
                  fake.userid = adi.agent.id;
                  fake.firm = dbi.data.firm;

                  planChanges.Add(fake);
               }
            }
         }
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
         List<IDataSet> del = new List<IDataSet>();

         SimpleDataSet<SVPlanData> rm = new SimpleDataSet<SVPlanData>(SVPlanData.OBJECT_NAME, false);
         List<int> removed = new List<int>();
         foreach (KeyValuePair<int, SVPlanData> kv in svPlans)
         {
            if (kv.Value.qty <= 0)
            {
               rm.Add(kv.Value);
               removed.Add(kv.Key);
            }
         }
         removed.ForEach(x => svPlans.Remove(x));
         if( svPlans.Count > 0 )
            wr.Add(svPlans);

         if (rm.Count > 0)
            del.Add(rm);

         wr.Add(planChanges);
         bool ret = DataModule.UpdateDataSet(wr, del, null, Config.GetConfig().GetConnection());
         if (showDialog)
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");

         return ret;
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }
   }
}
