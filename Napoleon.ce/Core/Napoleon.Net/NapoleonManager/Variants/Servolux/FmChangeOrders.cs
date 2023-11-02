using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   public partial class FmChangeOrders : Form
   {
      static FmChangeOrders instance = null;

      Factory allFactory = new Factory("<Все>\t");

      List<Agent> selectedAgents = new List<Agent>();
      List<AgentData> allAgents = new List<AgentData>();

      Price selectedPriceItem = null;

      double curValue = -1;
      ManagerLogList mgrLog = new ManagerLogList();

      SimpleDataSet<Order> orders = new SimpleDataSet<Order>("SVOrderChanges", false, true);
      //SimpleDataSet<Order> needSaveOrders = new SimpleDataSet<Order>("AgentsOrders", false, true);
      //SimpleDataSet<Order> loadedAgentsOrders = new SimpleDataSet<Order>("AgentsOrders", false, true);

      SimpleDataSet<OrgDogovor> dogovors = new SimpleDataSet<OrgDogovor>(OrgDogovor.OBJECT_NAME, false);
      DataSet<string, Factory> firms = new DataSet<string, Factory>(Factory.OBJECT_NAME, false);

      //Dictionary<string, Order> agentsOrders = new Dictionary<string, Order>();

      SimpleDataSet<Order> changed = new SimpleDataSet<Order>(Order.OBJECT_NAME, false, true);
      bool refreshData = false;
      bool markChanged = false;
      Font itemsBoldFont = null;
      bool canWrite = false;

      public FmChangeOrders()
      {
         InitializeComponent();

         dgvAgents.AutoGenerateColumns = false;
         dgvOrders.AutoGenerateColumns = false;
         dgvOrderDetail.AutoGenerateColumns = false;

         Manager m = CurrentUser.user as Manager;
         canWrite = m.HaveRight(RightTokens.Get("CanChangeOrder"), RightActions.Write);
      }

      public static void Open(Division d, DateTime curDate)
      {
         List<Agent> agents = new List<Agent>();
         foreach (Division.DivisionAgent da in d.GetAllAgents())
         {
            if (da.agent != null)
               agents.Add(da.agent);
         }

         Open(agents, curDate);
      }

      public static void Open(Agent a, DateTime curDate)
      {
         Open(new List<Agent>(new Agent[] {a}), curDate);
      }

      static void Open(List<Agent> selectedAgents, DateTime curDate)
      {
         if (instance == null)
         {
            instance = new FmChangeOrders();
            instance.SetSelectedAgents(selectedAgents);
            instance.dtWorkDate.Value = curDate;
            instance.Show();
         }
         else
         {
            instance.SetSelectedAgents(selectedAgents);
            instance.dtWorkDate.Value = curDate;
            instance.RefreshData();
            instance.BringToFront();
         }
      }

      void SetSelectedAgents(List<Agent> selectedAgents)
      {
         this.selectedAgents.Clear();
         this.selectedAgents.AddRange(selectedAgents);
      }

      protected override void OnClosed(EventArgs e)
      {
         base.OnClosed(e);
         instance = null;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      void RefreshData()
      {
         if (refreshData)
            return;

         refreshData = true;
         Manager dm = CurrentUser.user as Manager;
         if (allAgents.Count == 0)
         {
            if (dm == null)
               return;

            foreach (Agent a in dm.GetAgents().Data)
               allAgents.Add(new AgentData(a, this));

            dgvAgents.DataSource = allAgents;
         }

         DateTime now = dtWorkDate.Value.Date;
         DateTime end = now.AddDays(1);
         string uid = DataUtils.MakeFilterFromAgents(null, dm.GetAgents());
         string docsFilter = uid + " and " + DataUtils.MakeDateLogDataFilter(now, end); // MakeCreatedDataFilter

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dogovors);
         upd.Add(firms);

         foreach (Agent a in dm.GetAgents().Data)
         {
            DataSet<string, Org> orgs =
               DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;

            if (orgs.Count == 0)
            {
               orgs.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), orgs.Name);
               upd.Add(orgs);
            }
         }
         
         DataSet<String, Price> price = (DataSet<String, Price>)DataModule.Get(Price.OBJECT_NAME) ??
            new DataSet<String, Price>(Price.OBJECT_NAME);
         if (price.Count == 0)
         {
            price.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            upd.Add(price);
         }

         orders.Filter = docsFilter;
         upd.Add(orders);
         //loadedAgentsOrders.Filter = docsFilter;
         //upd.Add(loadedAgentsOrders);

         changed.Clear();

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      bool InSet(Order o, Factory newF)
      {
         if (newF != allFactory && o.firmCode != newF.id) return false;
         if (selectedAgents.Contains(o.agent) == false)
            return false;
         if (selectedPriceItem != null && o.HaveItem(selectedPriceItem.id) == false)
            return false;

         return true;
      }

      string OrderKey(Order o)
      {
         return o.userid + o.created.ToString("yyyyMMddHHmmss");
      }

      void DoLoadData()
      {
         tsFirms.SelectedIndexChanged -= new EventHandler(tsFirms_SelectedIndexChanged);
      
         List<Factory> fl = new List<Factory>((IEnumerable<Factory>)firms.Data);
         if (fl.Count != tsFirms.Items.Count)
         {
            Factory selF = tsFirms.SelectedItem as Factory;
            int selIndex = 0;
            tsFirms.Items.Clear();
            tsFirms.Items.Add(allFactory);

            fl.ForEach(x =>
            {
               if (x == selF)
                  selIndex = tsFirms.Items.Count;
               tsFirms.Items.Add(x);
            });
            if (tsFirms.Items.Count > 0)
               tsFirms.SelectedIndex = selIndex;
         }
         
         //agentsOrders.Clear();
         //foreach (Order o in loadedAgentsOrders.Data)
         //   agentsOrders[OrderKey(o)] = o;
         //loadedAgentsOrders.Clear();
         //foreach (Order o in orders.Data)
         //{
         //   string key = OrderKey(o);
         //   if (agentsOrders.ContainsKey(key) == false)
         //   {
         //      Order dup = MakeDup(o);
         //      agentsOrders[key] = dup;

         //      // если нашли модифицированнный заказ без сохраненного агентом, сохраним его
         //      if (o.modify != o.created)
         //         needSaveOrders.Add(dup);
         //   }
         //}
         RefreshOrders();

         tsFirms.SelectedIndexChanged += new EventHandler(tsFirms_SelectedIndexChanged);
         refreshData = false;
      }

      private Order MakeDup(Order o)
      {
         Order ret = new Order();
         FieldInfo[] fi = ret.GetType().GetFields(BindingFlags.Instance | BindingFlags.Public);
         FieldInfo[] itemfi = typeof(OrderItem).GetFields(BindingFlags.Instance | BindingFlags.Public);
         foreach (FieldInfo f in fi)
         {
            try
            {
               if (f.Name == "items")
               {
                  foreach(OrderItem oi in o.items)
                  {
                     OrderItem di = new OrderItem();
                     foreach(FieldInfo cf in itemfi)
                     {
                        try
                        {
                           object val = cf.GetValue(oi);
                           cf.SetValue(di, val);
                        }
                        catch (Exception)
                        {
                        }
                     }
                     ret.items.Add(di);
                  }
               }
               else
               {
                  object val = f.GetValue(o);
                  f.SetValue(ret, val);
               }
            } catch(Exception)
            {

            }
         }
         return ret;
      }

      void tsFirms_SelectedIndexChanged(object sender, EventArgs e)
      {
         RefreshOrders();
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
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

      void PutReplacedSets(List<ReplacedSet> rpl, SimpleDataSet<Order> data, string setName, bool changeModify)
      {
         Dictionary<string, ReplacedSet> wr = new Dictionary<string, ReplacedSet>();
         foreach (Order o in data.Data)
         {
            ReplacedSet rs = null;
            if (wr.ContainsKey(o.userid))
               rs = wr[o.userid];
            else
            {
               rs = new ReplacedSet(o.userid, new SimpleDataSet<Order>(setName, false, true));
               rs.dontRemove = true;
               wr[o.userid] = rs;
            }
            if (changeModify)
               o.modify = DateTime.Now;
            ((SimpleDataSet<Order>)rs.data).Add(o);
         }
         foreach (ReplacedSet rs in wr.Values)
            rpl.Add(rs);
      }

      private bool SaveChanges(bool showDialog)
      {
         List<IDataSet> wr = new List<IDataSet>();
         List<ReplacedSet> rpl = new List<ReplacedSet>();
         PutReplacedSets(rpl, changed, "SVOrderChanges", true);
         //PutReplacedSets(rpl, needSaveOrders, "AgentsOrders", false);

         if(mgrLog.Count > 0)
         {
            wr.Add(mgrLog);
         }

         bool ret = DataModule.UpdateDataSet(wr, null, rpl, Config.GetConfig().GetConnection());
         if (ret)
         {
            changed.Clear();
            //needSaveOrders.Clear();
         }

         if (showDialog)
         {
            dgvOrders.Refresh();
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
         }

         return ret;
      }

      double GetItemQTY(Order o, OrderItem i)
      {
         foreach (OrderItem oi in o.items)
            if (oi.id == i.id)
               return oi.qty;

         return 0;
      }

      void OnFactoryChanged(Factory newF)
      {
         dgvOrderDetail.CellValueChanged -= dgvOrderDetail_CellValueChanged;
         dgvOrderDetail.DataSource = null;
         List<OrderData> osrc = new List<OrderData>();
         foreach (Order o in orders.Data)
         {
            if (!InSet(o, newF))
               continue;

            foreach (OrderItem oi in o.items)
            {
               if (oi.agentQty == 0)
                  oi.agentQty = oi.qty;
            }


            //osrc.Add(new OrderData(o, agentsOrders[OrderKey(o)], firms.ContainsKey(o.firmCode) ? firms[o.firmCode] : null));
            OrderData od = new OrderData(o, firms.ContainsKey(o.firmCode) ? firms[o.firmCode] : null);
            if( od.belowMinSize )
            {
               foreach(OrderItem oi in o.items)
               {
                  if (oi.qty == 0)
                     break;
                  oi.agentQty = oi.qty;
                  oi.qty = 0;
               }

               if (changed.ContainsValue(o) == false)
                  changed.Add(o);
            }
            od.SetFiltredItem(selectedPriceItem == null ? null : selectedPriceItem.id);
            osrc.Add(od);
         }
         OrderDataTotal ot = new OrderDataTotal(osrc);
         List<OrderData> dataSrc = new List<OrderData>();
         dataSrc.Add(ot);
         dataSrc.AddRange(osrc);

         dgvOrders.DataSource = new SortableBindingList<OrderData>(dataSrc);
         dgvOrders.Rows[0].Frozen = true;
         dgvOrderDetail.CellValueChanged += dgvOrderDetail_CellValueChanged;
      }

      void RefreshStatus()
      {
         if (selectedPriceItem == null)
         {
            tsText.Text = "";
            return;
         }

         double qty = 0;
         foreach (OrderData od in (IList<OrderData>)dgvOrders.DataSource)
         {
            if (od.Order == null)
               continue;
            foreach (OrderItem oi in od.Order.items)
               if (oi.id == selectedPriceItem.id)
                  qty += oi.qty;
         }
         string text = String.Format("Выбранного товара в заказах ({0:N2}/{1:N3})", qty, qty / selectedPriceItem.inPack);
         tsText.Text = text;
      }

      bool IsAgentChecked(Agent a)
      {
         return selectedAgents.Contains(a);
      }

      void RefreshOrders()
      {
         Factory fs = tsFirms.SelectedItem as Factory;
         if (fs != null)
         {
            OnFactoryChanged(fs);
            RefreshStatus();
         }
      }

      void SetAgentChecked(Agent a, bool check)
      {
         selectedAgents.Remove(a);
         if (check)
            selectedAgents.Add(a);

         RefreshOrders();
      }

      class AgentData
      {
         Agent agent;
         FmChangeOrders owner;

         public AgentData(Agent a, FmChangeOrders owner)
         {
            agent = a;
            this.owner = owner;
         }

         public string Division { get { return agent.Division; } }
         public int DivisionID { get { return agent.DivisionID; } }
         public string Name { get { return agent.Name; } }
         public bool Checked { 
            get { return owner.IsAgentChecked(agent); } 
            set { owner.SetAgentChecked(agent, value); } 
         }
      }

      class OrderDataTotal : OrderData
      {
         List<OrderData> data;
         public OrderDataTotal(List<OrderData> data)
         {
            this.data = data;
         }

         public override string Org { get { return "Всего"; } }

         public override double AgentQTY
         {
	         get 
	         {
               double count = 0;
               foreach (OrderData od in data)
                  count += od.AgentQTY;
               return count;
	         }
         }
         public override double Qty
         {
            get
            {
               double count = 0;
               foreach (OrderData od in data)
                  count += od.Qty;
               return count;
            }
         }
         public override double PackQty
         {
            get
            {
               double count = 0;
               foreach (OrderData od in data)
                  count += od.PackQty;
               return count;
            }
         }
         public override double Sum
         {
            get
            {
               double count = 0;
               foreach (OrderData od in data)
                  count += od.Sum;
               return count;
            }
         }
      }

      internal class OrderData
      {
         Order src;
         public bool belowMinSize = false;
         public double dropSize = 0;
         string filtredItem = null;

         //Order agentsOrder;
         //public OrderData(Order src, Order agentsOrder, Factory f)
         //{
         //   this.src = src;
         //   this.agentsOrder = agentsOrder;

         //   if (src.org.noDrop == 0 && f != null)
         //   {
         //      dropSize = f.dropSize;
         //      if (f.dropSize > 0 && src.Sum() < f.dropSize && src.modify == src.created)
         //         belowMinSize = false;
         //   }
         //}
         public OrderData(Order src, Factory f)
         {
            this.src = src;

            if (src.org.noDrop == 0 && f != null)
            {
               dropSize = f.dropSize;
               if (f.dropSize > 0 && src.Sum() < f.dropSize && src.modify == src.created)
                  belowMinSize = false;
            }
         }

         protected OrderData()
         {

         }

         public virtual string Org { get { return src == null ? "" : src.org == null ? String.Format("Контрагент с кодом <{0}>", src.id) : src.org.Name; } }
         public Nullable<DateTime> Date { get { return src == null ? null : new Nullable<DateTime>(src.date); } }
         public Nullable<DateTime> Created { get { return src == null ? null : new Nullable<DateTime>(src.created); } }
         public Nullable<DateTime> Sended { get { return src == null ? null : new Nullable<DateTime>(src.sended); } }
         public virtual double Sum 
         { 
            get 
            {
               if (belowMinSize)
                  return 0;

               double sum = 0;
               foreach (OrderItem oi in src.items)
               {
                  if (filtredItem == null || oi.id == filtredItem)
                     sum += oi.qty * oi.cost;
               }
               return sum;
            }
         }
         public Order Order { get { return src; } }
         public Nullable<DateTime> DlvDate { get { return src == null ? null : new Nullable<DateTime>(src.dlvDate); } }

         public void SetFiltredItem(string fi) { this.filtredItem = fi; }

         public virtual double Qty
         {
            get
            {
               if (belowMinSize)
                  return 0;
               double qty = 0;
               foreach(OrderItem oi in src.items)
               {
                  if (filtredItem == null || oi.id == filtredItem)
                     qty += oi.qty;
               }
               return qty;
            }
         }

         public virtual double PackQty
         {
            get
            {
               if (belowMinSize)
                  return 0;
               double qty = 0;
               foreach (OrderItem oi in src.items)
               {
                  if (filtredItem == null || oi.id == filtredItem && oi.item != null)
                     qty += oi.qty / oi.item.inPack;
               }
               return qty;
            }
         }

         public virtual double AgentQTY
         {
            get
            {
               if (belowMinSize)
                  return 0;
               double qty = 0;
               foreach (OrderItem oi in src.items)
               {
                  if (filtredItem == null || oi.id == filtredItem && oi.item != null)
                     qty += GetAgentQty(oi);
               }
               return qty;
            }
         }

         public bool IsChanged { get { return src != null && src.modify != src.created; } }

         public String Modify { get { return src == null || src.modify == src.created ? "" : src.modify.ToString("dd.MM.yyyy HH:mm"); } }

         internal double GetAgentQty(OrderItem src)
         {
            if (src == null)
               return 0;
            double inPack = src.item == null ? 1 : src.item.inPack;
            return src.agentQty / inPack;
            //foreach (OrderItem oi in agentsOrder.items)
            //   if (oi.id == src.id)
            //      return oi.qty / inPack;

            //return src.qty / inPack;
         }

         //public NapoleonManager.Order AgentOrder { get { return agentsOrder; } }
      }

      private void dgvAgents_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         if (refreshData)
            return;

         if (dgvAgents.CurrentCell.ColumnIndex == clmnAgentCheck.DisplayIndex)
            dgvAgents.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }

      private void dtWorkDate_ValueChanged(object sender, EventArgs e)
      {
         RefreshData();
      }

      private void dgvOrders_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         OrderData od = dgvOrders.Rows[e.RowIndex].DataBoundItem as OrderData;
         Order order = od.Order;

         int row = 0;
         int clmn = clmnQty.DisplayIndex;

         List<OrderItemEx> loi = new List<OrderItemEx>();
         if (order == null)
         {
            dgvOrderDetail.DataSource = new SortableBindingList<OrderItemEx>(loi);
            cbOrderFirm.SelectedIndexChanged -= cbOrderFirm_SelectedIndexChanged;
            cbOrderFirm.SelectedItem = null;
            cbOrderFirm.Items.Clear();
            cbOrderFirm.SelectedIndexChanged += cbOrderFirm_SelectedIndexChanged;
            return;
         }

         foreach (OrderItem src in order.items)
         {
            if (selectedPriceItem == null || selectedPriceItem.id == src.id)
               loi.Add(new OrderItemEx(src, dgvOrderDetail, row++, clmn, od));
         }

         dgvOrderDetail.DataSource = new SortableBindingList<OrderItemEx>(loi);

         String ido = order.org == null ? null : order.org.ido;
         cbOrderFirm.SelectedIndexChanged -= cbOrderFirm_SelectedIndexChanged;
         cbOrderFirm.Items.Clear();
         if (ido != null)
         {
            cbOrderFirm.Items.Add(allFactory);
            foreach(Factory f in firms.Data)
            {
               if (Factory.HaveFirm(ido, f.id, dogovors))
                  cbOrderFirm.Items.Add(f);
            }

            foreach (Factory f in cbOrderFirm.Items)
            {
               if (f.id == order.firmCode)
               {
                  cbOrderFirm.SelectedItem = f;
                  break;
               }
            }
         }
         cbOrderFirm.SelectedIndexChanged += cbOrderFirm_SelectedIndexChanged;

         dtpOrderDate.ValueChanged -= dtpOrderDate_ValueChanged;
         dtpOrderDate.Value = order.Date;
         dtpOrderDate.ValueChanged += dtpOrderDate_ValueChanged;
      }

      void MarkChanged()
      {
         if (!canWrite)
            return;
#if DEBUG
#else
         if (dtWorkDate.Value < DateTime.Now.Date)
            return;
#endif

         OrderData od = dgvOrders.CurrentRow.DataBoundItem as OrderData;
         if (changed.ContainsValue(od.Order) == false)
            changed.Add(od.Order);

         //if (od.Order.modify == od.Order.created && !needSaveOrders.ContainsValue(od.AgentOrder))
         //   needSaveOrders.Add(od.AgentOrder);
         tsbSave.Enabled = true;
      }

      void cbOrderFirm_SelectedIndexChanged(object sender, EventArgs e)
      {
         Factory f = (Factory)cbOrderFirm.SelectedItem as Factory;
         if (dgvOrders.CurrentRow == null || f == null)
            return;

         OrderData od = dgvOrders.CurrentRow.DataBoundItem as OrderData;
         Order order = od.Order;
         order.firma = f.name;
         order.firmCode = f.id;

         MarkChanged();
      }

      private void dtpOrderDate_ValueChanged(object sender, EventArgs e)
      {
         if (dgvOrders.CurrentRow == null)
            return;

         OrderData od = dgvOrders.CurrentRow.DataBoundItem as OrderData;
         Order order = od.Order;
         order.date = dtpOrderDate.Value.Date;
         order.dlvDate = dtpOrderDate.Value.Date.AddDays(1);

         MarkChanged();
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }

      private void dgvOrderDetail_CellValueChanged(object sender, DataGridViewCellEventArgs e)
      {
         if (e.ColumnIndex == clmnPack.DisplayIndex)
         {
            OrderItemEx curItem = dgvOrderDetail.Rows[e.RowIndex].DataBoundItem as OrderItemEx;
            OrderData od = dgvOrders.CurrentRow.DataBoundItem as OrderData;

            mgrLog.PutLog(od.Order.userid, od.Order.id, curItem.ID, "ChangeOrders", curItem.PackQty.ToString(), curValue.ToString());
            MarkChanged();
            RefreshStatus();
         }
      }

      private void tsbPriceFilter_Click(object sender, EventArgs e)
      {
         Price p;
         if (FmSelectSKUEx.SkuQuery(this, out p) == DialogResult.OK)
         {
            selectedPriceItem = p;
            tsFilterText.Text = p.name + " " + p.thermalState + "/" + p.packName + " нажмите на надпись чтобы выключить фильтр";
            tsFilterText.Visible = true;
            RefreshOrders();
         }
      }

      private void tsFilterText_Click(object sender, EventArgs e)
      {
         selectedPriceItem = null;
         tsFilterText.Text = "";
         tsFilterText.Visible = false;
         RefreshOrders();
      }

      private void dgvOrders_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         if(e.Value == null)
         {
            e.Value = e.CellStyle.NullValue;
            e.FormattingApplied = true;
         }
         OrderData od = dgvOrders.Rows[e.RowIndex].DataBoundItem as OrderData;
         if( od is OrderDataTotal)
         {
            if (itemsBoldFont == null)
               itemsBoldFont = new System.Drawing.Font(e.CellStyle.Font, FontStyle.Bold);
            e.CellStyle.Font = itemsBoldFont;
            e.CellStyle.BackColor = Color.LightGray;
         }
         else if (markChanged && od.IsChanged)
         {
            e.CellStyle.BackColor = Color.LightPink;
         }
         else
         {
            e.CellStyle.BackColor = od.belowMinSize ? Color.Orange : dgvOrders.DefaultCellStyle.BackColor;
         }
      }

      private void toolStripButton1_Click(object sender, EventArgs e)
      {
         FmChOrdersReport.Do(firms);
      }

      private void tsbRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      private void dgvOrders_Sorted(object sender, EventArgs e)
      {
         SortableBindingList<OrderData> src = dgvOrders.DataSource as SortableBindingList<OrderData>;
         foreach(OrderData od in src)
         {
            if(od is OrderDataTotal && src.IndexOf(od) != 0)
            {
               dgvOrders.Rows[0].Frozen = false;
               src.Remove(od);
               src.Insert(0, od);
               dgvOrders.Rows[0].Frozen = true;
               break;
            }
         }
      }

      private void tsbShowChanged_Click(object sender, EventArgs e)
      {
         markChanged = !markChanged;
         dgvOrders.Invalidate();
      }

      private void dgvAgents_CellClick(object sender, DataGridViewCellEventArgs e)
      {
         if ((ModifierKeys & Keys.Shift) != 0)
         {
            AgentData ad = dgvAgents.Rows[e.RowIndex].DataBoundItem as AgentData;
            CheckAgents(!ad.Checked, ad.DivisionID);
            dgvAgents.Invalidate();
         }
      }

      private void CheckAgents(bool isChecked, int division)
      {
         List<AgentData> agents = (List<AgentData>)dgvAgents.DataSource;
         foreach(AgentData ad in agents)
         {
            if(ad.DivisionID == division)
               ad.Checked = isChecked;
         }
      }

      private void dgvOrderDetail_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         OrderItemEx curItem = dgvOrderDetail.Rows[e.RowIndex].DataBoundItem as OrderItemEx;
         curValue = curItem == null ? -1 : curItem.PackQty;
      }

      private void tsbRemoveItemQty_Click(object sender, EventArgs e)
      {
         if (dgvOrderDetail.CurrentRow == null)
            return;

         OrderItemEx cur = dgvOrderDetail.CurrentRow.DataBoundItem as OrderItemEx;
         cur.PackQty = 0;
         dgvOrderDetail.InvalidateRow(dgvOrderDetail.CurrentRow.Index);

         SortableBindingList<OrderData> src = (SortableBindingList<OrderData>)dgvOrders.DataSource;
         foreach(OrderData od in src)
         {
            if (od is OrderDataTotal)
               continue;
            bool changedOrder = false;
            foreach(OrderItem oi in od.Order.items)
            {
               if (oi.ID == cur.ID)
               {
                  oi.qty = 0;
                  changedOrder = true;
                  if (!tsbSave.Enabled)
                     tsbSave.Enabled = true;
               }
            }
            if (changedOrder & !changed.ContainsValue(od.Order))
            {
               changed.Add(od.Order);
            }
         }
      }

   }

   class FmSelectSKUEx : FmSelectSKU
   {
      FmSelectSKUEx(List<Price> checkList, string userID)
         : base(checkList, userID)
      {

      }

      FmSelectSKUEx() { }

      public static List<Price> SelectItemsEx(IWin32Window owner, List<Price> checkList, string userID, bool checkingFolder)
      {
         FmSelectSKUEx form = new FmSelectSKUEx(checkList, userID);
         form.checkingFolder = checkingFolder;

         if (form.ShowDialog(owner) == DialogResult.OK)
            return form.checkedPrice;

         return null;
      }

      public static DialogResult SkuQuery(IWin32Window owner, out Price price)
      {
         FmSelectSKU form = new FmSelectSKUEx();

         DialogResult result = form.ShowDialog(owner);
         price = form.SelectedPrice;

         return result;
      }

      protected override void FillTreeView(TreeView treeView, DataSet<string, ManagerFolder> dsManagerFolder, DataSet<string, Price> dsPrice)
      {
         //System.Diagnostics.Debug.WriteLine(dsManagerFolder.Count.ToString() + ' ' + dsPrice.Count.ToString());

         loading = true;
         ArticlesTreeConstructorWithCondition a = new ArticlesTreeConstructorWithCondition(tvArticles, dsManagerFolder, dsPrice, this);
         a.GetPriceName = PriceName;
         a.MakeArticlesTree(0, 1, ((checkedPrice == null) ? (IsPriceChecked)null : PriceChecked));
         //CheckNodes(tvArticles.Nodes);
         loading = false;
      }

      private void CheckNodes(TreeNodeCollection nodes)
      {
         foreach(TreeNode tn in nodes)
         {
            if (tn.Nodes.Count > 0)
               tn.Checked = IsAllChecked(tn.Nodes);
         }
      }

      private bool IsAllChecked(TreeNodeCollection nodes)
      {
         foreach(TreeNode tn in nodes)
         {
            if (tn.Nodes.Count > 0)
               tn.Checked = IsAllChecked(tn.Nodes);
            if (!tn.Checked)
               return false;
         }

         return true;
      }

      string PriceName(Price p)
      {
         return p.name + " " + p.thermalState + "/" + p.packName;
      }
   }

   class OrderItemEx
   {
      OrderItem item;
      DataGridView dgv;
      int row;
      int refColumn;
      double agentQty;
      FmChangeOrders.OrderData order;

      public OrderItemEx(OrderItem src, DataGridView dgv, int row, int refColumn, FmChangeOrders.OrderData order )
      {
         item = src;
         this.dgv = dgv;
         this.refColumn = refColumn;
         this.row = row;
         this.order = order;

         if( order != null )
            agentQty = order.GetAgentQty(src);
         else
         {
            double inPack = src.item == null ? 1 : src.item.inPack;
            agentQty = src.agentQty / inPack;
         }
      }

      public double Qty { get { return order != null && order.belowMinSize ? 0 : item.qty; } }
      public double Cost { get { return (item.cost == 0) ? item.sum / item.qty : item.cost; } }
      public string SCost { get { return Cost.ToString("C", Config.GetCultureInfo()); } }
      public double Sum { get { return (item.sum == 0) ? item.cost * item.qty : item.sum; } }
      public double PackQty
      {
         get { return order != null && order.belowMinSize ? 0 : item.item == null ? 0 : item.qty / item.item.inPack; }
         set
         {
            if (item.item == null || (order != null && order.belowMinSize))
               return;

            double prevValue = item.qty;
            item.qty = value * item.item.inPack;
            if( order.dropSize > 0 && order.Sum < order.dropSize )
            {
               if (MessageBox.Show("Сумма заказа меньше минимальгной. Продолжить?", "Предупреждение", MessageBoxButtons.YesNo, MessageBoxIcon.Warning) == DialogResult.No)
                  item.qty = prevValue;
            }
            dgv.InvalidateCell(dgv.Rows[row].Cells[refColumn]);
         }
      }


      public double AgentQTY { get { return agentQty; } }

      //Наименование
      public string Item { get { return item.item != null ? item.item.Name : "товар с кодом <" + item.id + ">"; } }

      public string State { get { return item.item == null ? "" : item.item.thermalState + "/" + item.item.packName; } }

      public string ID { get { return item.id; } }
   }
}
