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
   public partial class SVPlanChanges : Form
   {
      static SVPlanChanges instance = null;

      List<Agent> agents = new List<Agent>();
      List<DivisionEx> divisions = new List<DivisionEx>();

      SimpleDataSet<PlanChanges> planChanges = new SimpleDataSet<PlanChanges>(PlanChanges.OBJECT_NAME, false);
      //SimpleDataSet<OrderAddConfig> firms;
      SimpleDataSet<Order> orders = new SimpleDataSet<Order>(Order.OBJECT_NAME, false);
      SimpleDataSet<SVPlanData> svPlans = new SimpleDataSet<SVPlanData>(SVPlanData.OBJECT_NAME, false);
      DataSet<string, DivisionManager> dsManagers;
      SimpleDataSet<AgentPlanNew> dsPlans = new SimpleDataSet<AgentPlanNew>(AgentPlanNew.OBJECT_NAME, false);

      public SVPlanChanges()
      {
         InitializeComponent();

         dgvAgents.AutoGenerateColumns = false;
         dgvItems.AutoGenerateColumns = false;

         dsManagers = (DataSet<string, DivisionManager>)DataModule.Get(DivisionManager.OBJECT_NAME) ??
            new DataSet<string, DivisionManager>(DivisionManager.OBJECT_NAME);

      }

      static public void Open(DateTime curDate)
      {
         if (instance == null)
         {
            instance = new SVPlanChanges();
            instance.dtWorkDate.Value = curDate;
            instance.Show();
         }
         else
         {
            instance.dtWorkDate.Value = curDate;
            instance.RefreshData();
            instance.BringToFront();
         }
      }

      protected override void OnClosed(EventArgs e)
      {
         instance = null;
         base.OnClosed(e);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      private void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();

         string uid = DataUtils.MakeFilterFromAgents(null, agents);
         DateTime now = dtWorkDate.Value.Date;
         DateTime end = now.AddDays(1);

         if (dsManagers.Count == 0)
            upd.Add(dsManagers);

         //firms = DataModule.Get("Firms") as SimpleDataSet<OrderAddConfig> ?? new SimpleDataSet<OrderAddConfig>("Firms", true);
         //if (firms.Count == 0)
         //   upd.Add(firms);

         planChanges.Filter = uid + " and " + DataUtils.MakeDateLogDataFilter(now, end);
         upd.Add(planChanges);

         dsPlans.Filter = planChanges.Filter;
         upd.Add(dsPlans);

         orders.Filter = uid + " and " + DataUtils.MakeDateLogDataFilter(now, end);
         upd.Add(orders);

         upd.Add(svPlans);

         DataModule.DataProcessed += new EventHandler((o, e) => {
            DataModule.ClearEvents();
            FmWait.CloseForm();

            Invoke(new EmptyParamHandler(delegate { DoLoadData(); }));
         });
         DataModule.OnDataResponceError += new EventDataResponseError(FmWait.StdErrorHandler);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), upd, FmWait.ProgressIndicator));
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
      }

      private void tsbRefresh_Click(object sender, EventArgs e)
      {
         if( CheckChanges() )
            RefreshData();
      }

      void DoLoadData()
      {
         tsDivisions.SelectedIndexChanged -= (FirmOrDivisionChanged);
         if (divisions.Count == 0)
            LoadDivisions();

         if (tsDivisions.SelectedItem == null && tsDivisions.Items.Count > 0)
            tsDivisions.SelectedIndex = 0;
         tsDivisions.SelectedIndexChanged += (FirmOrDivisionChanged);

         tsFirms.SelectedIndexChanged -= FirmOrDivisionChanged;
         List<Factory> fl = Factory.GetFactories();
         if (fl.Count != tsFirms.Items.Count)
         {
            Factory selF = tsFirms.SelectedItem as Factory;
            int selIndex = 0;
            tsFirms.Items.Clear();
            fl.ForEach(x =>
            {
               if (x == selF)
                  selIndex = tsFirms.Items.Count;
               tsFirms.Items.Add(x);
            });
            if (tsFirms.Items.Count > 0)
               tsFirms.SelectedIndex = selIndex;
         }
         tsFirms.SelectedIndexChanged += FirmOrDivisionChanged;

         FirmOrDivisionChanged(this, EventArgs.Empty);
      }

      private void LoadDivisions()
      {
         foreach (Agent a in ((Manager)CurrentUser.user).GetAgents().Data)
         {
            Division d = ((Manager)CurrentUser.user).GetAgentDivision(a);
            bool finded = false;
            foreach (DivisionEx de in divisions)
            {
               if (de.division == d)
               {
                  finded = true;
                  break;
               }
            }

            if (!finded)
            {
               DivisionEx newde = new DivisionEx(d, dsManagers);
               divisions.Add(newde);
               tsDivisions.Items.Add(newde);
            }

            agents.Add(a);
         }
      }

      void FirmOrDivisionChanged(object sender, EventArgs e)
      {
         Factory f = tsFirms.SelectedItem as Factory;
         DivisionEx d = tsDivisions.SelectedItem as DivisionEx;
         RefreshData(f, d);
      }

      private void ItemOrFilterChanged(object sender, EventArgs e)
      {
         if (dgvPrice.CurrentRow == null)
            return;

         Factory f = tsFirms.SelectedItem as Factory;
         DivisionEx d = tsDivisionFilter.SelectedItem as DivisionEx;
         ItemPriceData p = dgvPrice.CurrentRow.DataBoundItem as ItemPriceData;
         DivisionEx excluded = tsDivisions.SelectedItem as DivisionEx;

         RefreshAgents(d, p, excluded, f);
      }

      // обновляет только левую панель
      private void RefreshAgents(DivisionEx d, ItemPriceData p, DivisionEx excluded, Factory selF)
      {
         Dictionary<string, AgentPlanSalesData> items = new Dictionary<string,AgentPlanSalesData>();

         AddOrderData(d, p, excluded, selF, items);
         AddPlans(d, p, excluded, selF, items);

         List<AgentPlanSalesData> srcItems = new List<AgentPlanSalesData>();
         DateTime checkDate = dtWorkDate.Value.Date;
         foreach (PlanChanges pc in planChanges.Data)
         {
            if (pc.firm != selF.id || pc.date != checkDate || p.item.id != pc.id || items.ContainsKey(pc.userid) == false)
               continue;

            AgentPlanSalesData apsd = items[pc.userid];
            apsd.plan += pc.qty;
            srcItems.Add(apsd);
            items.Remove(pc.userid);
         }
         
         srcItems.AddRange(items.Values);
         srcItems.Sort();
         dgvAgents.DataSource = srcItems;
      }

      private void AddPlans(DivisionEx d, ItemPriceData p, DivisionEx excluded, Factory selF, Dictionary<string, AgentPlanSalesData> items)
      {
         DateTime checkDate = dtWorkDate.Value.Date;
         Dictionary<string, Agent> dagents = new Dictionary<string, Agent>();
         agents.ForEach(x => dagents.Add(x.id, x));
         foreach(AgentPlanNew apn in dsPlans.Data)
         {
            if (apn.firm != selF.id)
               continue;

            if (dagents.ContainsKey(apn.userid) == false)
               continue;
            Agent a = dagents[apn.userid];

            Division od = ((Manager)CurrentUser.user).GetAgentDivision(a);
            if (od.id == excluded.division.id)
               continue;
            if (d.division.id != DailyAgentPlans.DIVISION_ALL && od.id != d.division.id)
               continue;

            foreach(AgentPlanNew.Item ai in apn.items)
            {
               if (ai.id != p.item.id)
                  continue;
               if (items.ContainsKey(a.id) == false)
               {
                  AgentPlanSalesData apd = new AgentPlanSalesData(a, od);
                  apd.plan = ai.qty;
                  items.Add(a.id, apd);
               }
               else
                  items[a.id].plan = ai.qty;
            }
         }

         //foreach (Agent a in agents)
         //{
         //SimpleDataSet<AgentDailyPlans> plans =
         //   DataModule.GetUserDataSet(a.id, AgentDailyPlans.OBJECT_NAME, typeof(SimpleDataSet<AgentDailyPlans>)) as SimpleDataSet<AgentDailyPlans>;

         //Division od = ((Manager)CurrentUser.user).GetAgentDivision(a);
         //if (od.id == excluded.division.id)
         //   continue;
         //if (d.division.id != DailyAgentPlans.DIVISION_ALL && od.id != d.division.id)
         //   continue;

         //foreach (AgentDailyPlans pc in plans.Data)
         //{
         //   if (pc.firm != selF.id || pc.date != checkDate || p.item.id != pc.id)
         //      continue;
         //   if (items.ContainsKey(a.id) == false)
         //   {
         //      AgentPlanSalesData apd = new AgentPlanSalesData(a, od);
         //      apd.plan = pc.qty;
         //      items.Add(a.id, apd);
         //   }
         //   else
         //      items[a.id].plan = pc.qty;
         //}
         //}
      }

      private void AddOrderData(DivisionEx d, ItemPriceData p, DivisionEx excluded, Factory selF, Dictionary<string, AgentPlanSalesData> items)
      {
         double inpack = (p.item.inPack == 0) ? 1 : p.item.inPack;
         foreach (Order o in orders.Data)
         {
            if (o.agent == null || o.firmCode != selF.id)
               continue;
            Division od = ((Manager)CurrentUser.user).GetAgentDivision(o.agent);
            if (od.id == excluded.division.id)
               continue;
            if (d.division.id != DailyAgentPlans.DIVISION_ALL && od.id != d.division.id)
               continue;

            foreach (OrderItem oi in o.items)
            {
               if (p.ContainsItem(oi.item))
               {
                  double value = oi.qty / inpack;
                  if (items.ContainsKey(o.AgentID) == false)
                  {
                     AgentPlanSalesData apd = new AgentPlanSalesData(o.agent, od);
                     apd.sales = value;
                  }
                  else
                     items[o.AgentID].sales += value;
               }
            }
         }
      }

      // обновляет обе панели
      void RefreshData(Factory selFactory, DivisionEx selDivision)
      {
         UpdateDivisionFilter(selDivision);

         DataSet<String, Price> dsPrice = (DataSet<String, Price>)DataModule.Get(Price.OBJECT_NAME);
         Dictionary<String, List<ItemPriceData>> groups = UpdatePriceItems(dsPrice);

         if (selDivision.manager == null)
         {
            MessageBox.Show("Подразделению не назначен руководитель\nНазначте руководителя в упровлении командой", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
            return;
         }

         //update SV changes
         Dictionary<String, SVChangeItem> items = new Dictionary<string, SVChangeItem>();
         foreach (SVPlanData pi in svPlans.Data)
         {
            if (pi.firm == selFactory.id && pi.userid == selDivision.manager.login && pi.date == dtWorkDate.Value.Date)
            {
               SVChangeItem svData = new SVChangeItem(dsPrice[pi.id], pi);
               items[pi.id] = svData;
            }
         }
         foreach (List<ItemPriceData> groupItems in groups.Values)
         {
            String group = "";
            List<SVChangeItem> svGroups = new List<SVChangeItem>();
            foreach (ItemPriceData di in groupItems)
               if (items.ContainsKey(di.item.id))
               {
                  group = di.group;
                  svGroups.Add(items[di.item.id]);
               }

            foreach (SVChangeItem grItem in svGroups)
               grItem.AddGroup(svGroups, group);
         }

         List<SVChangeItem> svSource = new List<SVChangeItem>();
         svSource.AddRange(items.Values);
         svSource.Sort();
         dgvItems.DataSource = svSource;
      }

      Dictionary<String, List<ItemPriceData>> UpdatePriceItems(DataSet<String, Price> dsPrice)
      {
         Dictionary<String, ItemPriceData> items = new Dictionary<string, ItemPriceData>();
         Dictionary<String, List<ItemPriceData>> groups = new Dictionary<string, List<ItemPriceData>>();

         Dictionary<String, List<PlanGroup>> planGroups = new Dictionary<string, List<PlanGroup>>();
         SimpleDataSet<PlanGroup> dsGroups = (SimpleDataSet<PlanGroup>)DataModule.Get(PlanGroup.OBJECT_NAME) ??
            new SimpleDataSet<PlanGroup>(PlanGroup.OBJECT_NAME);
         foreach (PlanGroup pg in dsGroups.Data)
         {
            List<PlanGroup> lgroups;
            if (planGroups.ContainsKey(pg.group))
               lgroups = planGroups[pg.group];
            else
            {
               lgroups = new List<PlanGroup>();
               planGroups.Add(pg.group, lgroups);
            }
             lgroups.Add(pg);
         }
         foreach (KeyValuePair<String, List<PlanGroup>> kv in planGroups)
         {
            List<ItemPriceData> groupList = new List<ItemPriceData>();
            foreach (PlanGroup pg in kv.Value)
               if (items.ContainsKey(pg.id))
               {
                  ItemPriceData item = items[pg.id];
                  item.group = kv.Key;
                  groupList.Add(item);
               }

            groupList.ForEach((x) =>
            {
               x.AddGroup(groupList);
            });
         }


         //foreach (Agent a in agents)
         //{
         //   SimpleDataSet<AgentDailyPlans> plans =
         //      DataModule.GetUserDataSet(a.id, AgentDailyPlans.OBJECT_NAME, typeof(SimpleDataSet<AgentDailyPlans>)) as SimpleDataSet<AgentDailyPlans>;

         //   foreach (AgentDailyPlans pc in plans.Data)
         //   {
         //      if (dsPrice.ContainsKey(pc.id) == false)
         //         continue;

         //      ItemPriceData item = null;
         //      if (items.ContainsKey(pc.id))
         //         item = items[pc.id];
         //      else
         //      {
         //         item = new ItemPriceData(dsPrice[pc.id]);
         //         item.group = pc.group;
         //         items.Add(pc.id, item);
         //         if (pc.group.Length > 0)
         //         {
         //            List<ItemPriceData> groupItems;
         //            if (groups.ContainsKey(pc.group))
         //               groupItems = groups[pc.group];
         //            else
         //            {
         //               groupItems = new List<ItemPriceData>();
         //               groups.Add(pc.group, groupItems);
         //            }
         //            groupItems.Add(item);
         //         }
         //      }
         //   }
         //}

         //foreach (List<ItemPriceData> groupItems in groups.Values)
         //   foreach (ItemPriceData di in groupItems)
         //      di.AddGroup(groupItems);

         List<ItemPriceData> priceItems = new List<ItemPriceData>();
         priceItems.AddRange(items.Values);
         priceItems.Sort();
         dgvPrice.DataSource = priceItems;

         return groups;
      }

      private void UpdateDivisionFilter(DivisionEx selDivision)
      {
         tsDivisionFilter.SelectedIndexChanged -= ItemOrFilterChanged;

         DivisionEx seld = tsDivisionFilter.SelectedItem as DivisionEx;
         int selIndex = 0;

         tsDivisionFilter.Items.Clear();
         //tsDivisionFilter.Items.Add(new DivisionEx(DailyAgentPlans.AllDivision, null));
         foreach (DivisionEx cd in divisions)
         {
            if (cd != selDivision)
            {
               if (seld == cd)
                  selIndex = tsDivisionFilter.Items.Count;
               tsDivisionFilter.Items.Add(cd);
            }
         }
         tsDivisionFilter.SelectedIndex = selIndex;
         tsDivisionFilter.SelectedIndexChanged += ItemOrFilterChanged;
      }

      private void dgvPrice_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         ItemPriceData di = (ItemPriceData)dgvPrice.Rows[e.RowIndex].DataBoundItem;
         Color backColor = di.group == null || di.group.Length == 0 ? dgvPrice.DefaultCellStyle.BackColor : Color.LightBlue;
         e.CellStyle.BackColor = backColor;
      }

      private void dgvPrice_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         Factory f = tsFirms.SelectedItem as Factory;
         DivisionEx d = tsDivisionFilter.SelectedItem as DivisionEx;
         ItemPriceData p = dgvPrice.Rows[e.RowIndex].DataBoundItem as ItemPriceData;
         DivisionEx excluded = tsDivisions.SelectedItem as DivisionEx;

         RefreshAgents(d, p, excluded, f);
      }

      private void dtWorkDate_ValueChanged(object sender, EventArgs e)
      {
         RefreshData();
      }

      private void dgvItems_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         SVChangeItem di = (SVChangeItem)dgvItems.Rows[e.RowIndex].DataBoundItem;
         Color backColor = di.group == null || di.group.Length == 0 ? dgvPrice.DefaultCellStyle.BackColor : Color.LightBlue;
         e.CellStyle.BackColor = backColor;
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
         wr.Add(planChanges);
         wr.Add(svPlans);
         bool ret = DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection());
         if( showDialog )
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");

         return ret;
      }

      private void dgvAgents_CellEnter(object sender, DataGridViewCellEventArgs e)
      {
         if (e.ColumnIndex == clmnPlan.DisplayIndex)
         {
            AgentPlanSalesData data = dgvAgents.Rows[e.RowIndex].DataBoundItem as AgentPlanSalesData;
            double val = data.plan - data.sales;
            if (val > 0)
            {
               Rectangle r = dgvAgents.GetCellDisplayRectangle(e.ColumnIndex, e.RowIndex, true);

               InputQty iq = new InputQty();

               iq.Location = dgvAgents.PointToScreen(new Point(r.Left - iq.Width, r.Bottom - iq.Height));
               iq.Qty = val;
               iq.MaxValue = (int)(val + 0.005);
               if (iq.ShowDialog() == DialogResult.OK)
               {
                  val = iq.Qty;
                  tsbSave.Enabled = true;

                  SuspendLayout();

                  ItemPriceData p = dgvPrice.CurrentRow.DataBoundItem as ItemPriceData;
                  Factory f = tsFirms.SelectedItem as Factory;
                  DivisionEx selected = tsDivisions.SelectedItem as DivisionEx;

                  bool finded = false;
                  foreach(PlanChanges pc in planChanges.Data)
                  {
                     if (pc.firm != f.id || pc.date != dtWorkDate.Value.Date || pc.userid != data.agent.id)
                        continue;

                     if (p.ContainsItem(pc.id))
                     {
                        finded = true;
                        pc.qty -= val;
                     }
                  }
                  if (!finded)
                  {
                     PlanChanges pc = new PlanChanges(f, data.agent, p.item, -val, dtWorkDate.Value.Date);
                     planChanges.Add(pc);
                     foreach (ItemPriceData ipd in p.groupItems)
                     {
                        pc = new PlanChanges(f, data.agent, ipd.item, -val, dtWorkDate.Value.Date);
                        planChanges.Add(pc);
                     }
                  }

                  UpdateSVPlan(data, val);

                  data.plan -= val;
                  dgvAgents.InvalidateCell(e.ColumnIndex, e.RowIndex);

                  ResumeLayout();
               }
            }
         }
      }

      private void UpdateSVPlan(AgentPlanSalesData data, double val)
      {
         ItemPriceData p = dgvPrice.CurrentRow.DataBoundItem as ItemPriceData;
         Factory f = tsFirms.SelectedItem as Factory;
         DivisionEx selected = tsDivisions.SelectedItem as DivisionEx;

         bool finded = false;
         List<SVChangeItem> svSource = (List<SVChangeItem>)dgvItems.DataSource;
         foreach (SVChangeItem svi in svSource)
         {
            if (svi.item == p.item)
            {
               finded = true;
               svi.ChangeQty(val);
            }
         }
         if (!finded)
         {
            SVChangeItem pd = SVChangeItem.CreateItem(f, selected.manager, p, val);
            pd.src.date = dtWorkDate.Value.Date;
            svPlans.Add(pd.src);
            svSource.Add(pd);
            foreach (ItemPriceData ipd in pd.groupItems)
            {
               SVChangeItem grpItem = (SVChangeItem)ipd;
               grpItem.src.date = dtWorkDate.Value.Date;
               svPlans.Add(grpItem.src);
               svSource.Add(grpItem);
            }
         }
         dgvItems.DataSource = null;
         svSource.Sort();
         dgvItems.DataSource = svSource;
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }
   }

   public class SVPlanData : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "SVPlanChanges";

      [KeyField]
      public string id = "";

      [KeyField]
      public string userid = "";

      [KeyField]
      public string firm = "";
      
      [KeyField]
      public DateTime date = DateTime.Now.Date;

      public double qty = 0;
   }

   class AgentPlanSalesData : IComparable<AgentPlanSalesData>
   {
      public Agent agent;
      public Division division;

      public double plan = 0;
      public double sales = 0;

      public AgentPlanSalesData(Agent a, Division d)
      {
         agent = a;
         division = d;
      }

      public string Agent { get { return agent.Name + " / " + division.DivisionName; } }
      public string Plan
      {
         get
         {
            return String.Format("{0:N0}/{1:N0}", plan, sales);
         }
      }

      #region IComparable<AgentPlanSalesData> Members

      public int CompareTo(AgentPlanSalesData other)
      {
         int cmp = division.DivisionName.CompareTo(other.division.DivisionName);
         if (cmp != 0)
            return cmp;
         return agent.Name.CompareTo(other.agent.Name);
      }

      #endregion
   }

   class ItemPriceData : IComparable<ItemPriceData>
   {
      public Price item;
      public String group = "";
      internal List<ItemPriceData> groupItems = new List<ItemPriceData>();

      public ItemPriceData(Price p)
      {
         this.item = p;
      }

      internal void AddGroup(List<ItemPriceData> groups)
      {
         foreach (ItemPriceData item in groups)
         {
            if (item != this)
               groupItems.Add(item);
         }
      }

      internal bool ContainsItem(Price p)
      {
         return ContainsItem(p.id);
      }

      internal bool ContainsItem(String id)
      {
         if (item.id == id)
            return true;

         foreach (ItemPriceData ipd in groupItems)
            if (ipd.item.id == id)
               return true;

         return false;
      }

      #region IComparable<ItemPriceData> Members

      public int CompareTo(ItemPriceData other)
      {
         int cmp = Name.CompareTo(other.Name);
         if (cmp != 0)
            return cmp;

         cmp = group.CompareTo(other.group);
         return cmp != 0 ? cmp : State.CompareTo(other.State);
      }

      public string Name { get { return item.name; } }
      public string State { get { return item.thermalState + "/" + item.packName; } }

      #endregion
   }

   class SVChangeItem : ItemPriceData, IComparable<SVChangeItem>
   {
      public SVPlanData src;

      static public SVChangeItem CreateItem(Factory f, DivisionManager manager, ItemPriceData item, double qty)
      {
         List<SVChangeItem> items = new List<SVChangeItem>();
         SVChangeItem ret = new SVChangeItem(f, manager, item, qty);
         items.Add(ret);

         foreach(ItemPriceData ipd in item.groupItems)
            items.Add(new SVChangeItem(f, manager,ipd, qty));
         foreach (SVChangeItem gi in items)
            gi.AddGroup(items, item.group);

         return ret;
      }

      public SVChangeItem(Price p, SVPlanData src)
         : base(p)
      {
         this.src = src;
      }

      SVChangeItem(Factory f, DivisionManager manager, ItemPriceData item, double qty)
         : base(item.item)
      {
         src = new SVPlanData();
         src.id = item.item.id;
         src.firm = f.id;
         src.qty = qty;
         src.userid = manager.login;

         group = item.group;
      }

      internal void AddGroup(List<SVChangeItem> groups, String group)
      {
         foreach (SVChangeItem item in groups)
         {
            item.group = group;
            if (item != this)
               groupItems.Add(item);
         }
      }

      public double Qty { get { return src.qty; } }

      internal void ChangeQty(double val)
      {
         src.qty += val;
         groupItems.ForEach(x => ((SVChangeItem)x).src.qty += val);
      }

      #region IComparable<SVChangeItem> Members

      public int CompareTo(SVChangeItem other)
      {
         return base.CompareTo(other);
      }

      #endregion
   }

   class DivisionEx
   {
      public Division division;
      public DivisionManager manager;

      public DivisionEx(Division d, DataSet<string, DivisionManager> managers)
      {
         this.division = d;
         if (managers != null)
         {
            foreach (DivisionManager dm in managers.Data)
            {
               if (dm.division == d.id)
               {
                  manager = dm;
                  break;
               }
            }
         }
      }

      public override string ToString()
      {
         return division.ToString();
      }
   }
}
