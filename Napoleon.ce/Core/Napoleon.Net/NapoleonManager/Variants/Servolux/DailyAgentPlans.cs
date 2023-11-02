using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Reflection;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Reflection.Emit;
using System.Collections;

namespace GRSoft.NapoleonManager
{
   public partial class DailyAgentPlans : Form, IStackedHeaderGenerator
   {
      static DailyAgentPlans instance = null;

      public const int DIVISION_ALL = -123;
      //public static Division AllDivision = new Division() { name = "<Все>", id = DIVISION_ALL };

      Font boldFont = null;

      StackedHeaderDecorator headerDecorator = null;
      List<AgentColumnData> agentsData = new List<AgentColumnData>();
      
      SimpleDataSet<PlanChanges> planChanges = new SimpleDataSet<PlanChanges>(PlanChanges.OBJECT_NAME, false);
      //SimpleDataSet<OrderAddConfig> firms = new SimpleDataSet<OrderAddConfig>("Firms", true);
      SimpleDataSet<Order> orders = new SimpleDataSet<Order>(Order.OBJECT_NAME, false);
      SimpleDataSet<SVPlanData> svPlans = new SimpleDataSet<SVPlanData>(SVPlanData.OBJECT_NAME);
      SimpleDataSet<AgentPlanNew> dsPlans = new SimpleDataSet<AgentPlanNew>(AgentPlanNew.OBJECT_NAME, false);
      SimpleDataSet<PlanGroup> dsGroups;

      DataSet<string, ManagerFolder> dsFolders;

      Dictionary<String, List<PlanGroup>> planGroups = new Dictionary<string, List<PlanGroup>>();

      Agent total = new Agent { name = "Сводная информация", id = "TOTAL" };
      Type dataItemType;

      List<Agent> agents = new List<Agent>();
      
      int changedRow = -1;
      DataItem changedItem = null;
      Dictionary<DataGridViewColumn, Double> changedValues = new Dictionary<DataGridViewColumn, double>();

      public DailyAgentPlans()
      {
         InitializeComponent();

         headerDecorator = new StackedHeaderDecorator(this, dgvPlans);
         clmnName.Frozen = true;
         clmnState.Frozen = true;
         clmnQty.Frozen = true;

         dgvPlans.AutoGenerateColumns = false;
         dgvPlans.Visible = false;

         dsFolders = DataModule.Get(ManagerFolder.OBJECT_NAME) as DataSet<string, ManagerFolder>;
         if (dsFolders == null)
            dsFolders = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);

         dsGroups = (SimpleDataSet<PlanGroup>)DataModule.Get(PlanGroup.OBJECT_NAME) ??
            new SimpleDataSet<PlanGroup>(PlanGroup.OBJECT_NAME);

         dsFolders.Filter = DataUtils.USERID_IS_NULL_STR;
         if (CurrentUser.user != null)
            tsbSend.Enabled = CurrentUser.user.HaveRight(RightTokens.Get("DailyAgentPlansCommit"), RightActions.Write);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);

         if (!MainForm.Instance.CheckIsMainDataPresents(true))
         {
            tsbRefresh.Enabled = false;
            return;
         }
         agents.Clear();
         foreach (Agent a in ((Manager)CurrentUser.user).GetAgents().Data)
            agents.Add(a);

         RefreshData(true);
      }

      void RefreshData(bool loadPlans)
      {
         List<IDataSet> upd = new List<IDataSet>();
         DataSet<String, Price> price = (DataSet<String, Price>)DataModule.Get(Price.OBJECT_NAME)?? 
            new DataSet<String, Price>(Price.OBJECT_NAME);
         
         if (price.Count == 0)
         {
            price.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            upd.Add(price);
         }

         if (dsFolders.Count == 0)
            upd.Add(dsFolders);

         DateTime now = dtWorkDate.Value.Date;
         DateTime end = now.AddDays(1);
         string uid = DataUtils.MakeFilterFromAgents(null, agents);
         string docsFilter = uid + " and " + DataUtils.MakeDateLogDataFilter(now, end); ;

         planChanges.Filter = docsFilter;
         upd.Add(planChanges);
         if (loadPlans)
         {
            dsPlans.Filter = docsFilter + " and \"isMonthly\" = 0";
            upd.Add(dsPlans);
            //foreach (Agent a in ((Manager)CurrentUser.user).GetAgents().Data)
            //{
            //   SimpleDataSet<AgentDailyPlans> plans =
            //      DataModule.GetUserDataSet(a.id, AgentDailyPlans.OBJECT_NAME, typeof(SimpleDataSet<AgentDailyPlans>)) as SimpleDataSet<AgentDailyPlans>;

            //   plans.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), plans.Name);
            //   upd.Add(plans);
            //}
         }

         if (dsGroups.Count == 0)
            upd.Add(dsGroups);

         IDataSet ds = DataModule.Get(Factory.OBJECT_NAME);
         if( ds != null)
            upd.Add(ds);

         orders.Filter = docsFilter;
         upd.Add(orders);

         svPlans.Filter = "\"userid\"='" + CurrentUser.user.User.login + "' and \"qty\" > 0";
         upd.Add(svPlans);

         DataModule.DataProcessed += new EventHandler(DataProcessed);
         DataModule.OnDataResponceError += new EventDataResponseError(FmWait.StdErrorHandler);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), upd, FmWait.ProgressIndicator));
      }

      void DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         planGroups.Clear();
         foreach(PlanGroup pg in dsGroups.Data)
         {
            List<PlanGroup> groups;
            if (planGroups.ContainsKey(pg.group))
               groups = planGroups[pg.group];
            else
            {
               groups = new List<PlanGroup>();
               planGroups.Add(pg.group, groups);
            }
            groups.Add(pg);
         }

         Invoke(new EmptyParamHandler(delegate { DoLoadData(); }));
      }

      List<Division> LoadAgents(List<Division> selDivision, bool onlyDivision)
      {
         List<Division> dvs = new List<Division>();
         //dvs.Add(AllDivision);

         dgvPlans.SuspendLayout();
         List<DataGridViewColumn> removed = new List<DataGridViewColumn>();

         foreach (DataGridViewColumn ci in dgvPlans.Columns)
            if (ci != clmnName && ci != clmnState && ci != clmnQty )
               removed.Add(ci);

         removed.ForEach((c) => { dgvPlans.Columns.Remove(c.Name); });

         agentsData.Clear();

         const int CLMN_WIDTH = 60;
         String id;
         DataGridViewTextBoxColumn clmn;
         AgentColumnData acd;

         DataGridViewCellStyle style = new DataGridViewCellStyle();
         style.Format = "N2";

         DataGridViewCellStyle styleInt = new DataGridViewCellStyle();
         styleInt.Format = "N0";

         int index = 0;
         foreach (Agent a in agents)
         {
            index++;
            Division d = ((Manager)CurrentUser.user).GetAgentDivision(a);
            //if (selDivision != null && selDivision.id != DIVISION_ALL && d != selDivision)
            if (selDivision != null && !selDivision.Contains(d) )
               continue;

            if (dvs.Contains(d) == false)
               dvs.Add(d);

            if (onlyDivision)
               continue;

            acd = new AgentColumnData { agent = a, division = d };
            acd.columns = new DataGridViewColumn[3];

            id = index.ToString();
            clmn = new DataGridViewTextBoxColumn { Width = CLMN_WIDTH, HeaderText = "лимит", DataPropertyName = "Limit_" + id, Name = "Limit_" + id };
            clmn.Tag = a;
            acd.columns[0] = clmn;
            clmn.DefaultCellStyle = styleInt;

            clmn = new DataGridViewTextBoxColumn { Width = CLMN_WIDTH, HeaderText = "заявлено", DataPropertyName = "Order_" + id, Name = "Order_" + id };
            clmn.DefaultCellStyle = styleInt;
            acd.columns[1] = clmn;

            clmn = new DataGridViewTextBoxColumn { Width = CLMN_WIDTH, HeaderText = "%", DataPropertyName = "Prc_" + id, Name = "Prc_" + id };
            acd.columns[2] = clmn;
            clmn.DefaultCellStyle = style;

            agentsData.Add(acd);

         }
         agentsData.Sort();

         for (int i = 0; i < dvs.Count; i++)
         {
            Division d = dvs[i];
            Agent dva = new Agent() { name = d.name, id = d.id.ToString() };
            acd = new AgentColumnData { agent = dva, division = null };
            acd.columns = new DataGridViewColumn[3];

            id = d.id.ToString();
            clmn = new DataGridViewTextBoxColumn { Width = CLMN_WIDTH, HeaderText = "лимит", DataPropertyName = "SVLimit_" + id, Name = "SVLimit_" + id };
            clmn.Tag = dva;
            acd.columns[0] = clmn;
            clmn.DefaultCellStyle = styleInt;

            clmn = new DataGridViewTextBoxColumn { Width = CLMN_WIDTH, HeaderText = "заявлено", DataPropertyName = "SVOrder_" + id, Name = "SVOrder_" + id };
            clmn.DefaultCellStyle = styleInt;
            acd.columns[1] = clmn;

            clmn = new DataGridViewTextBoxColumn { Width = CLMN_WIDTH, HeaderText = "%", DataPropertyName = "SVPrc_" + id, Name = "SVPrc_" + id };
            acd.columns[2] = clmn;
            clmn.DefaultCellStyle = style;

            agentsData.Add(acd);
         }

         acd = new AgentColumnData { agent = total };
         acd.columns = new DataGridViewColumn[3];

         clmn = new DataGridViewTextBoxColumn { Width = CLMN_WIDTH, HeaderText = "заявлено", DataPropertyName = "OrderTotal", Name = "OrderTotal" };
         acd.columns[0] = clmn;

         clmn = new DataGridViewTextBoxColumn { Width = CLMN_WIDTH, HeaderText = "%", DataPropertyName = "PrcTotal", Name = "PrcTotal" };
         acd.columns[1] = clmn;
         clmn.DefaultCellStyle = style;

         clmn = new DataGridViewTextBoxColumn { Width = CLMN_WIDTH, HeaderText = "остаток", DataPropertyName = "RestQty", Name = "RestQty" };
         acd.columns[2] = clmn;

         agentsData.Add(acd);

         dataItemType = DataItem.CreateChildType(agents, dvs);

         dgvPlans.ResumeLayout();

         return dvs;
      }

      private void DoLoadData()
      {
         tsSVAlert.Visible = svPlans.Count > 0;

         //tsDivisions.SelectedIndexChanged -= new System.EventHandler(this.RefreshAgents);
         tsDivisions.ItemCheck -= tsDivisions_ItemCheck;

         //Division d = tsDivisions.SelectedItem as Division;
         List<Division> dvs = new List<Division>();
         if (tsDivisions.Items.Count == 0)
         {
            dvs = LoadAgents(null, cbSVOnly.Checked);
            Division allDv = new Division();
            allDv.name = "<Все>";
            allDv.id = DIVISION_ALL;
            tsDivisions.Items.Add(allDv, true);

            dvs.ForEach((el) => tsDivisions.Items.Add(el, true));
         }
         else
         {
            foreach (Division d in tsDivisions.CheckedItems)
               dvs.Add(d);
            LoadAgents(dvs, cbSVOnly.Checked);
         }

         //if (tsDivisions.Items.Count == 0)
         //{
//            tsDivisions.SelectedIndex = 0;
//            tsbChangeSVQty.Enabled = tsDivisions.Items.Count > 2;
         //}

         if (tsFolders.Items.Count == 0)
         {
            tsFolders.Items.Add("<Все>");
            foreach (ManagerFolder mf in dsFolders.Data)
               tsFolders.Items.Add(new FolderEx(mf));
            tsFolders.SelectedIndex = 0;
            tsFolders.SelectedIndexChanged += (tsFolders_SelectedIndexChanged);
         }

         headerDecorator.Recreate();

         tsDivisions.ItemCheck += tsDivisions_ItemCheck;
         //tsDivisions.SelectedIndexChanged += new System.EventHandler(this.RefreshAgents);

         List<Factory> fl = Factory.GetFactories();         
         if (fl.Count != tsFirms.Items.Count)
         {
            Factory selF = tsFirms.SelectedItem as Factory;
            int selIndex = 0;
            tsFirms.Items.Clear();
            fl.ForEach(x => {
               if (x == selF)
                  selIndex = tsFirms.Items.Count;
               tsFirms.Items.Add(x);
            });
            if (tsFirms.Items.Count > 0)
               tsFirms.SelectedIndex = selIndex;
         }
         Factory fs = tsFirms.SelectedItem as Factory;
         if (fs != null)
            OnFactoryChanged(fs);
      }

      void tsDivisions_ItemCheck(object sender, ItemCheckEventArgs e)
      {
         Division curChecked = tsDivisions.Items[e.Index] as Division;
         RefreshAgents(curChecked, e.NewValue == CheckState.Checked);
      }

      protected override void OnClosed(EventArgs e)
      {
         base.OnClosed(e);
         instance = null;
         if (boldFont != null)
            boldFont.Dispose();
      }

      public static void Open()
      {
         if (instance == null)
         {
            instance = new DailyAgentPlans();
            instance.Show();
         }
         else
         {
            instance.BringToFront();
            instance.RefreshData(false);
         }
      }

      public static void RefreshOpened()
      {
         if (instance != null)
            instance.RefreshData(false);
      }

      private void tsbRefresh_Click(object sender, EventArgs e)
      {
         RefreshData(true);
      }

      public double EditPlanChanges(DataItem item, String userid)
      {
         double res = 0;
         if (item == changedItem)
         {
            foreach (KeyValuePair<DataGridViewColumn, Double> kv in changedValues)
            {
               Agent a = kv.Key.Tag as Agent;
               if (a.id == userid)
               {
                  res = kv.Value;
                  break;
               }
            }
         }
         return res;
      }

      public Header GenerateStackedHeader()
      {
         Header ret = new Header();
         ret.Children.Add(new Header { Name = clmnName.HeaderText, ColumnId = clmnName.Index, X = 0 });
         ret.Children.Add(new Header { Name = clmnState.HeaderText, ColumnId = clmnState.Index, X = clmnState.Width });
         ret.Children.Add(new Header { Name = clmnQty.HeaderText, ColumnId = clmnQty.Index, X = clmnQty.Width });

         List<Header> headList = ret.Children;

         //Division current = null;
         int ix = clmnName.Width + clmnState.Width + clmnQty.Width;
         foreach (AgentColumnData acd in agentsData)
         {
            //if (acd.division != current)
            //{
            //   current = acd.division;
            //   if (current != null)
            //   {
            //      Header manager = new Header { Name = current.DivisionName, X = 200 };
            //      ret.Children.Add(manager);
            //      headList = manager.Children;
            //   }
            //   else
            //      headList = ret.Children;
            //}
            String name = acd.agent.Name;
            if (acd.division != null)
               name += " / " + acd.division.DivisionName;

            Header ah = new Header { Name = name, X = ix };
            foreach (DataGridViewColumn clmn in acd.columns)
            {
               dgvPlans.Columns.Add(clmn);
               ah.Children.Add(new Header { Name = clmn.HeaderText, ColumnId = clmn.Index, X = ix });
               ix += clmnState.Width;
            }
            headList.Add(ah);
         }
         return ret;
      }

      private void tsFirms_SelectedIndexChanged(object sender, EventArgs e)
      {
         Factory f = tsFirms.SelectedItem as Factory;
         OnFactoryChanged(f);
      }

      Dictionary<String, Dictionary<String, Double>> GetOrderData(String factory)
      {
         Dictionary<String, Dictionary<String, Double>> ret = new Dictionary<string, Dictionary<string, double>>();
         foreach (Order o in orders.Data)
         {
            if (o.firmCode != factory)
               continue;

            Dictionary<String, Double> agentData = null;
            if (ret.ContainsKey(o.AgentID))
               agentData = ret[o.AgentID];
            else
            {
               agentData = new Dictionary<string, double>();
               ret.Add(o.AgentID, agentData);
            }

            foreach (OrderItem oi in o.items)
            {
               double val = (agentData.ContainsKey(oi.id)) ? agentData[oi.id] : 0;
               val += oi.qty;
               agentData[oi.id] = val;
            }
         }

         return ret;
      }

      Dictionary<String, Dictionary<String, Double>> GetChangesData(String factory)
      {
         Dictionary<String, Dictionary<String, Double>> ret = new Dictionary<string, Dictionary<string, double>>();
         foreach (PlanChanges pc in planChanges.Data)
         {
            if (pc.firm != factory)
               continue;

            Dictionary<String, Double> agentData = null;
            if (ret.ContainsKey(pc.userid))
               agentData = ret[pc.userid];
            else
            {
               agentData = new Dictionary<string, double>();
               ret.Add(pc.userid, agentData);
            }

            agentData.Add(pc.id, pc.qty);
         }
         return ret;
      }

      String FindGroup(String itemID)
      {
         String ret = "";
         foreach(List<PlanGroup> pg in planGroups.Values)
            foreach(PlanGroup grp in pg)
               if(grp.id == itemID)
               {
                  ret = grp.group;
                  break;
               }
         return ret;
      }

      void OnFactoryChanged(Factory newF)
      {
         ConstructorInfo ci = dataItemType.GetConstructor(Type.EmptyTypes);

         Dictionary<String, DataItem> items = new Dictionary<string, DataItem>();
         Dictionary<String, List<DataItem>> groups = new Dictionary<string, List<DataItem>>();

         //agent=>price=>qty
         Dictionary<String, Dictionary<String, Double>> orderData = GetOrderData(newF.id);
         Dictionary<String, Dictionary<String, Double>> changesData = GetChangesData(newF.id);

         DateTime checkDate = dtWorkDate.Value.Date;
         DataSet<String, Price> dsPrice = (DataSet<String, Price>)DataModule.Get(Price.OBJECT_NAME);
         foreach(AgentPlanNew pc in dsPlans.Data)
         {
            if (pc.firm != newF.id || pc.date.Date != checkDate)
               continue;

            Dictionary<String, Double> agentOrders = (orderData.ContainsKey(pc.userid)) ? orderData[pc.userid] : null;
            Dictionary<String, Double> agentChanges = (changesData.ContainsKey(pc.userid)) ? changesData[pc.userid] : null;

            foreach(AgentPlanNew.Item api in pc.items)
            {
               if (!dsPrice.ContainsKey(api.id))
                  continue;

               DataItem item = null;
               if (items.ContainsKey(api.id))
                  item = items[api.id];
               else
               {
                  item = (DataItem)ci.Invoke(null);
                  item.item = dsPrice[api.id];
                  item.group = FindGroup(api.id);
                  item.owner = this;

                  items.Add(api.id, item);
               }
               AgentDailyPlanData adpd = new AgentDailyPlanData();
               adpd.plan = api.qty;
               if (agentOrders != null && agentOrders.ContainsKey(api.id))
               {
                  double val = agentOrders[api.id];
                  if (item.item.inPack != 0)
                     val /= item.item.inPack;
                  adpd.order = val;
               }
               if (agentChanges != null && agentChanges.ContainsKey(api.id))
               {
                  adpd.planChanges = agentChanges[api.id];
               }

               item.data[pc.userid] = adpd;
            }
         }

         //foreach (Agent a in agents)
         //{
         //   SimpleDataSet<AgentDailyPlans> plans =
         //      DataModule.GetUserDataSet(a.id, AgentDailyPlans.OBJECT_NAME, typeof(SimpleDataSet<AgentDailyPlans>)) as SimpleDataSet<AgentDailyPlans>;
            
         //   Dictionary<String,Double> agentOrders = (orderData.ContainsKey(a.id)) ? orderData[a.id] : null;
         //   Dictionary<String, Double> agentChanges = (changesData.ContainsKey(a.id)) ? changesData[a.id] : null;

         //   foreach (AgentDailyPlans pc in plans.Data)
         //   {
         //      if (pc.firm != newF.id || !dsPrice.ContainsKey(pc.id) || pc.date.Date != checkDate)
         //         continue;

         //      DataItem item = null;
         //      if (items.ContainsKey(pc.id))
         //         item = items[pc.id];
         //      else
         //      {
         //         item = (DataItem)ci.Invoke(null);
         //         item.item = dsPrice[pc.id];
         //         item.group = pc.group;

         //         item.owner = this;

         //         items.Add(pc.id, item);
         //         if (pc.group.Length > 0)
         //         {
         //            List<DataItem> groupItems;
         //            if (groups.ContainsKey(pc.group))
         //               groupItems = groups[pc.group];
         //            else
         //            {
         //               groupItems = new List<DataItem>();
         //               groups.Add(pc.group, groupItems);
         //            }
         //            groupItems.Add(item);
         //         }
         //      }
         //      AgentDailyPlanData adpd = new AgentDailyPlanData();
         //      adpd.plan = pc.qty;
         //      if (agentOrders != null && agentOrders.ContainsKey(pc.id))
         //      {
         //         double val = agentOrders[pc.id];
         //         if (item.item.inPack != 0)
         //            val /= item.item.inPack;
         //         adpd.order = val;
         //      }
         //      if (agentChanges != null && agentChanges.ContainsKey(pc.id))
         //      {
         //         adpd.planChanges = agentChanges[pc.id];
         //      }

         //      item.data[a.id] =  adpd;
         //   }
         //}

         // добавим товар из заявок которого нет в плане. Для этого сначала пересортируем price=>agent=>qty
         Dictionary<String, Dictionary<String, Double>> priceAgentQty = new Dictionary<string, Dictionary<string, double>>();
         foreach (KeyValuePair<String, Dictionary<String, Double>> agentOrderQty in orderData)
            foreach (KeyValuePair<String, Double> orderQty in agentOrderQty.Value)
            {
               if (priceAgentQty.ContainsKey(orderQty.Key) == false)
                  priceAgentQty[orderQty.Key] = new Dictionary<string, double>();

               priceAgentQty[orderQty.Key][agentOrderQty.Key] = orderQty.Value;
            }

         foreach(KeyValuePair<String, Dictionary<String, Double>> paq in priceAgentQty)
         {
            String itemId = paq.Key;
            if (!dsPrice.ContainsKey(itemId))
               continue;

            DataItem item;
            bool dontCheck = false;
            if (items.ContainsKey(itemId) == false)
            {
               item = (DataItem)ci.Invoke(null);
               item.item = dsPrice[itemId];
               item.group = "";
               item.owner = this;

               items.Add(itemId, item);
               dontCheck = true;
            }
            else
               item = items[itemId];

            foreach (KeyValuePair<String, Double> orderQty in paq.Value)
            {
               if( dontCheck || item.HaveAgentData(orderQty.Key) == false )
               {
                  AgentDailyPlanData adpd = new AgentDailyPlanData();
                  adpd.order = orderQty.Value / item.item.inPack;
                  item.data[orderQty.Key] = adpd;
               }
            }
         }

         foreach(KeyValuePair<String, List<PlanGroup>> kv in planGroups)
         {
            List<DataItem> groupList = new List<DataItem>();
            foreach(PlanGroup pg in kv.Value)
               if(items.ContainsKey(pg.id))
               {
                  DataItem di = items[pg.id];
                  di.SetInPack(pg.inPack);
                  groupList.Add(di);
               }

            groupList.ForEach((x) =>
            {
               x.AddGroup(groupList, 0);
            });
         }

         //foreach (List<DataItem> groupItems in groups.Values)
         //{
         //   if (groupItems.Count > 1)
         //   {
         //      double minInPack = groupItems[0].item.inPack;
         //      foreach (DataItem di in groupItems)
         //         if (minInPack > di.item.inPack)
         //            minInPack = di.item.inPack;

         //      foreach (DataItem di in groupItems)
         //         di.AddGroup(groupItems, minInPack);
         //   }
         //}

         Type listType = typeof(List<>).MakeGenericType(new Type[] { dataItemType });
         ConstructorInfo lci = listType.GetConstructor(Type.EmptyTypes);
         IList list = (IList)lci.Invoke(null);
         foreach (DataItem di in items.Values)
         {
            FolderEx fe = tsFolders.SelectedItem as FolderEx;
            if (fe == null || fe.Folder.id == di.Item.fid)
               list.Add(di);
         }

         MethodInfo mi = listType.GetMethod("Sort", Type.EmptyTypes);
         mi.Invoke(list, null);

         dgvPlans.DataSource = Convert.ChangeType(list, listType);
         dgvPlans.Visible = true;
      }

      bool IsGroupName(String name)
      {
         String templ = "PrcTotal|RestQty";
         return name.StartsWith("Limit_") || name.StartsWith("Prc_") || name.StartsWith("SVLimit_") || name.StartsWith("SVPrc_") || templ.Contains(name);
      }

      private void dgvPlans_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         Color backColor, textColor = dgvPlans.DefaultCellStyle.ForeColor;
         DataItem di = dgvPlans.Rows[e.RowIndex].DataBoundItem as DataItem;
         DataGridViewColumn clmn = dgvPlans.Columns[e.ColumnIndex];
         //if (di.PrcTotal > 100)
         //{
         //   backColor = Color.LightYellow;
         //} else
         //{
         //   if (clmnQty.DisplayIndex == e.ColumnIndex || IsGroupName(clmn.Name))
         //   {
         //      backColor = di.group == null || di.group.Length == 0 ? dgvPlans.DefaultCellStyle.BackColor : Color.LightBlue;
         //   }
         //   else
         //   {
         //      backColor = (e.RowIndex % 2) == 0 ? dgvPlans.DefaultCellStyle.BackColor : Color.LightGray;
         //   }
         //}

         //Визуальное представление ЦУП: охл-голубым цветом, зам-белым цветом;
         backColor = di.Item.thermalState == "Охл" ? Color.LightBlue : dgvPlans.DefaultCellStyle.BackColor;

         // При перелимите - фон ячейки факт светло-красный или оранжевый.
         if (clmn.Name.StartsWith("Order_") )
         {
            string id = clmn.Name.Substring(6);
            PropertyInfo pi = di.GetType().GetProperty("Prc_" + id);
            backColor = Color.Beige;

            if (pi != null)
            {
               object val = pi.GetValue(di, null);
               if (val != null && ((double)val) > 100)
                  backColor = Color.Orange;
            }
         }
         else if (clmn.Name.StartsWith("SVOrder_"))
         {
            string id = clmn.Name.Substring(8);
            PropertyInfo pi = di.GetType().GetProperty("SVPrc_" + id);
            backColor = Color.Beige;

            if (pi != null)
            {
               object val = pi.GetValue(di, null);
               if (val != null && ((double)val) > 100)
                  backColor = Color.Orange;
            }
         }

         Agent a = clmn.Tag as Agent;
         if (a != null)
         {
            double val = di.GetChanges(a.id);
            if (val != 0)
               textColor = Color.Red;
         }
         e.CellStyle.BackColor = backColor;
         e.CellStyle.ForeColor = textColor;

         if (changedValues.ContainsKey(clmn) && e.RowIndex == changedRow)
         {
            if (boldFont == null)
               boldFont = new Font(e.CellStyle.Font, FontStyle.Bold);

            e.CellStyle.Font = boldFont;
         }
      }

      bool CanSendOrders()
      {
         //foreach (DataItem di in (IList)dgvPlans.DataSource)
         //{
         //   if (di.PrcTotal > 100)
         //      return false;
         //}
         return true;
      }

      private void tsbSend_Click(object sender, EventArgs e)
      {
         if (!CanSendOrders())
         {
            MessageBox.Show("Заявки превышают лимит", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
            return;
         }

         Dictionary<String, SimpleDataSet<Order>> sets = new Dictionary<string, SimpleDataSet<Order>>();

         Factory selF = tsFirms.SelectedItem as Factory;
         foreach (Order ord in orders.Data)
         {
            //if (ord.items.Count == 0 || ord.Sum() == 0)
            if (ord.items.Count == 0 || (selF != null && ord.firmCode != selF.id))
               continue;
            SimpleDataSet<Order> oset;
            if (sets.ContainsKey(ord.AgentID))
               oset = sets[ord.AgentID];
            else
            {
               oset = new SimpleDataSet<Order>(Order.ORDER_SAVE, false, true);
               sets.Add(ord.AgentID, oset);
            }
            oset.Add(ord);
         }

         List<ReplacedSet> wr = new List<ReplacedSet>();
         foreach (KeyValuePair<String, SimpleDataSet<Order>> kv in sets)
         {
            ReplacedSet rs = new ReplacedSet(kv.Key, kv.Value);
            rs.dontRemove = true;
            wr.Add(rs);
         }
         Format fmt = Format.Find(Order.ORDER_SAVE);
         if (fmt == null)
         {
            Format src = Format.Find(Order.OBJECT_NAME);
            if (src != null)
            {
               src.CloneFormat(Order.ORDER_SAVE);
            }
         }
         if (wr.Count == 0)
         {
            MessageBox.Show("Нет данных для отправки");
         }
         else
         {
            if (DataModule.UpdateDataSet(null, null, wr, Config.GetConfig().GetConnection()) == false)
               MessageBox.Show("Ошибка при отправке заявок");
            else
               MessageBox.Show("Заявки успешно отправлены");
         }
      }

      void InputChangesValue(DataGridViewColumn clmn, int clmnIndex, int rowIndex)
      {
         double val = changedValues.ContainsKey(clmn) ? changedValues[clmn] : -GetAvailSum();
         Rectangle r = dgvPlans.GetCellDisplayRectangle(clmnIndex, rowIndex, true);
         InputQty iq = new InputQty();

         iq.Location = dgvPlans.PointToScreen(new Point(r.Left, r.Bottom + iq.Height < dgvPlans.Bottom ? r.Bottom : r.Top - iq.Height));
         iq.Qty = val;
         if (iq.ShowDialog() == DialogResult.OK)
         {
            changedValues[clmn] = iq.Qty;
            RefreshAvailText();
            tsbSave.Enabled = (Math.Abs(GetAvailSum()) < 0.01);
         }
      }

      private void dgvPlans_CellClick(object sender, DataGridViewCellEventArgs e)
      {

      }

      private void dgvPlans_CellEnter(object sender, DataGridViewCellEventArgs e)
      {
         if (dtWorkDate.Value < DateTime.Now.Date)
            return;

         DataGridViewColumn clmn = dgvPlans.Columns[e.ColumnIndex];
         Agent a = clmn.Tag as Agent;
         if (changedRow != -1)
         {
            if (a != null && e.RowIndex == changedRow)
               InputChangesValue(clmn, e.ColumnIndex, e.RowIndex);

         } else
            tsbChangePlan.Enabled = (a != null);
      }

      void StartPlan(int row, int column)
      {
         if (changedRow != -1 || dtWorkDate.Value < DateTime.Now.Date)
            return;

         DataItem di = dgvPlans.Rows[row].DataBoundItem as DataItem;
         DataGridViewColumn clmn = dgvPlans.Columns[column];
         Agent a = clmn.Tag as Agent;

         //double curChanges = di.data.ContainsKey(a.id) ? di.data[a.id].planChanges : 0;

         Rectangle r = dgvPlans.GetCellDisplayRectangle(column, row, true);
         InputQty iq = new InputQty();

         iq.Location = dgvPlans.PointToScreen(new Point(r.Left, r.Bottom + iq.Height < dgvPlans.Bottom ? r.Bottom : r.Top - iq.Height));
         iq.Qty = 0;
         if (iq.ShowDialog() == DialogResult.OK && iq.Qty != 0)
         {
            changedRow = row;
            changedItem = di;
            changedValues.Clear();
            changedValues.Add(clmn, iq.Qty);

            RefreshAvailText();
         }
      }

      private void tsbChangePlan_Click(object sender, EventArgs e)
      {
         DataGridViewCell cell = dgvPlans.SelectedCells[0];
         StartPlan(cell.RowIndex, cell.ColumnIndex);
      }

      double GetAvailSum()
      {
         double val = 0;
         foreach (Double ival in changedValues.Values)
            val += ival;

         return Math.Round(val, 2, MidpointRounding.ToEven);
      }

      void RefreshAvailText()
      {
         double val = GetAvailSum();
         if (val == 0)
            tsbInfo.Text = "Все распределено. Можно сохранять";
         else
            tsbInfo.Text = String.Format("Осталось {1}{0:F0}", val, val > 0 ? "убрать " :  "добавить " );
         tsbInfo.Visible = true;
      }

      void ResetPlanChanges()
      {
         if (changedRow != -1)
         {
            dgvPlans.InvalidateRow(changedRow);

            changedRow = -1;
            changedValues.Clear();
            changedItem = null;
            tsbInfo.Visible = false;
            tsbSave.Enabled = false;
         }
      }

      void CommitPlanChanges()
      {
         foreach (KeyValuePair<DataGridViewColumn, Double> kv in changedValues)
         {
            string userid = (kv.Key.Tag as Agent).id;
            changedItem.SetPlanChanges(kv.Value, userid);
         }
         for (int i = 0; i < dgvPlans.Rows.Count; i++)
         {
            DataItem di = dgvPlans.Rows[i].DataBoundItem as DataItem;
            if (di == changedItem || changedItem.groupItems.Contains(di))
               dgvPlans.InvalidateRow(i);
         }
         ResetPlanChanges();
      }

      private void DailyAgentPlans_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Escape && changedRow != -1)
            ResetPlanChanges();
      }

      private void dgvPlans_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
      {
         DataGridViewColumn clmn = dgvPlans.Columns[e.ColumnIndex];
         Agent a = clmn.Tag as Agent;
         if (a != null)
            StartPlan(e.RowIndex, e.ColumnIndex);
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         DateTime planDate = dtWorkDate.Value.Date;
         SimpleDataSet<PlanChanges> changes = new SimpleDataSet<PlanChanges>(PlanChanges.OBJECT_NAME, false);
         foreach (KeyValuePair<DataGridViewColumn, Double> kv in changedValues)
         {
            String userid = (kv.Key.Tag as Agent).id;
            String firmid = ((Factory)tsFirms.SelectedItem).id;

            PlanChanges pc = new PlanChanges();
            double changedQty = changedItem.GetChanges(userid) + kv.Value; 
            pc.id = changedItem.item.id;
            pc.firm = firmid;
            pc.userid = userid;
            pc.qty = changedQty;
            pc.date = planDate;
            pc.created = DateTime.Now;

            changes.Add(pc);

            foreach (DataItem ch in changedItem.groupItems)
            {
               pc = new PlanChanges();
               pc.id = ch.item.id;
               pc.firm = firmid;
               pc.userid = userid;
               pc.qty = changedQty;
               pc.date = planDate;
               pc.created = DateTime.Now;

               changes.Add(pc);
            }
         }

         List<IDataSet> wr = new List<IDataSet>();
         wr.Add(changes);
         if (DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection()))
         {
            CommitPlanChanges();
            MessageBox.Show("Изменения сохранены");
         }
         else
         {
            MessageBox.Show("Ошибка при записи изменений");
         }
      }

      private void RefreshAgents(object sender, EventArgs e)
      {
         RefreshAgents(null, false);
      }

      void RefreshAgents(Division curChecked, bool isChecked)
      {
         //Division d = tsDivisions.SelectedItem as Division;
         List<Division> dvs = new List<Division>();
         foreach (Division d in tsDivisions.CheckedItems)
            dvs.Add(d);
         if (curChecked != null)
         {
            if (curChecked.id == DIVISION_ALL)
            {
               tsDivisions.ItemCheck -= tsDivisions_ItemCheck;

               dvs.Clear();
               for (int i = 1; i < tsDivisions.Items.Count; i++ )
               {
                  tsDivisions.SetItemChecked(i, isChecked);
                  if (isChecked)
                     dvs.Add(tsDivisions.Items[i] as Division);
               }

               tsDivisions.ItemCheck += tsDivisions_ItemCheck;
            }
            else
            {
               if (isChecked)
                  dvs.Add(curChecked);
               else
                  dvs.Remove(curChecked);
            }
         }

         LoadAgents(dvs, cbSVOnly.Checked);
         headerDecorator.Recreate();

         if(curChecked != null)
         {
            Factory f = tsFirms.SelectedItem as Factory;
            OnFactoryChanged(f);
         }
      }

      private void tsbChangeSVQty_Click(object sender, EventArgs e)
      {
         SVPlanChanges.Open(dtWorkDate.Value);
      }

      private void tsbDoChcangeQty_Click(object sender, EventArgs e)
      {
         SVPlanCommit.Open();
      }

      private void dtWorkDate_ValueChanged(object sender, EventArgs e)
      {
         RefreshData(true);
      }

      private void tsFolders_SelectedIndexChanged(object sender, EventArgs e)
      {
         Factory f = tsFirms.SelectedItem as Factory;
         OnFactoryChanged(f);
      }

      private void tsDisableFirms_Click(object sender, EventArgs e)
      {
         FmDisabledFirms form = new FmDisabledFirms();

         form.SetFactories(Factory.GetFactories());
         form.ShowDialog(this);
      }
   }

   public class AgentColumnData : IComparable<AgentColumnData>
   {
      public Division division;
      public Agent agent;
      public DataGridViewColumn[] columns;

      #region IComparable<AgentColumnData> Members

      public int CompareTo(AgentColumnData other)
      {
         if (division == null)
            return other.division == null ? 0 : 1;

         int cmp = division.name.CompareTo(other.division.name);
         if (cmp == 0)
            cmp = agent.name.CompareTo(other.agent.name);

         return cmp;
      }

      #endregion
   }

   
   public class DataItem : IComparable
   {
      public Price item;
      public String group;
      public DailyAgentPlans owner;
      
      // коэффициент пересчета для группы (заказ в группе считается в минимальных единицах)
      double groupOrderCoef = 1;

      public Dictionary<String, AgentDailyPlanData> data = new Dictionary<string, AgentDailyPlanData>();
      public List<DataItem> groupItems = new List<DataItem>();

      public DataItem() { }

      public bool HaveAgentData(string userid) { return data.ContainsKey(userid); }

      public int CompareTo(object other)
      {
         int cmp = Name.CompareTo(((DataItem)other).Name);
         if (cmp != 0)
            return cmp;

         cmp = group.CompareTo(((DataItem)other).group);
         return cmp != 0 ? cmp : State.CompareTo(((DataItem)other).State);
      }

      public void SetInPack(double inPack) { groupOrderCoef = item.inPack / inPack; }

      public Price Item { get { return item; } }
      public String Name { get { return item.Name; } }
      public String State { get { return item.thermalState + "/" + item.packName; } }

      public void SetPlanChanges(double changes, string userid)
      {
         AgentDailyPlanData ad = null;
         if (data.ContainsKey(userid))
            ad = data[userid];
         else
         {
            ad = new AgentDailyPlanData();
            data.Add(userid, ad);
         }
         ad.planChanges += changes;
         foreach (DataItem ch in groupItems)
            ch.ForceSetChanges(ad.planChanges, userid);
      }

      void ForceSetChanges(double newValue, string userid)
      {
         AgentDailyPlanData ad = null;
         if (data.ContainsKey(userid))
            ad = data[userid];
         else
         {
            ad = new AgentDailyPlanData();
            data.Add(userid, ad);
         }
         ad.planChanges = newValue;
      }

      public double GetChanges(String userid)
      {
         return data.ContainsKey(userid) ? data[userid].planChanges : 0;
      }

      public string GetLimit(String userid)
      {
         if( !data.ContainsKey(userid) )
            return "0";

         AgentDailyPlanData planData = data[userid];
         double planChanges = planData.planChanges;
         planChanges += owner.EditPlanChanges(this, userid);
         if( planChanges == 0 )
            return String.Format("{0:F0}", planData.plan);

         return String.Format("{0:F0}/{1:F0}", planData.plan, planData.plan + planChanges);
      }

      public double GetOrder(String userid)
      {
         return data.ContainsKey(userid) ? data[userid].order : 0;
      }

      public double GetPrc(String userid)
      {
         if( !data.ContainsKey(userid) )
            return 0;

         AgentDailyPlanData ad = data[userid];
         double qty = ad.plan + ad.planChanges;
         double order = GroupOrder(userid);

         return  qty == 0 ? 0 : order / qty * 100;
      }

      public string GetSVLimit(String userid)
      {
         double plan = 0;
         double changes = 0;

         int divid;
         if (Int32.TryParse(userid, out divid) && DivisionList.GetDataSet().ContainsKey(divid))
         {
            Division d = DivisionList.GetDataSet()[divid];
            foreach (KeyValuePair<string, AgentDailyPlanData> kv in data)
            {
               if (d.HaveAgent(kv.Key))
               {
                  plan += kv.Value.plan;
                  changes += kv.Value.planChanges;
               }
            }
         }

         if (changes == 0)
            return String.Format("{0:F0}", plan);

         return String.Format("{0:F0}/{1:F0}", plan, plan + changes);
      }

      public double GetSVOrder(String userid)
      {
         double val = 0;
         int divid;
         if (Int32.TryParse(userid, out divid) && DivisionList.GetDataSet().ContainsKey(divid))
         {
            Division d = DivisionList.GetDataSet()[divid];
            foreach (KeyValuePair<string, AgentDailyPlanData> kv in data)
            {
               if (d.HaveAgent(kv.Key))
                  val += kv.Value.order;
            }
         }

         return val;
      }

      public double GetSVPrc(String userid)
      {
         double qty = 0;
         double order = 0;
         int divid;
         if (Int32.TryParse(userid, out divid) && DivisionList.GetDataSet().ContainsKey(divid))
         {
            Division d = DivisionList.GetDataSet()[divid];
            foreach (KeyValuePair<string, AgentDailyPlanData> kv in data)
            {
               if (d.HaveAgent(kv.Key))
               {
                  order += GroupOrder(kv.Key);
                  qty += kv.Value.plan + kv.Value.planChanges;
               }
            }
         }

         return qty == 0 ? 0 : order / qty * 100;
      }

      public double OrderTotal
      {
         get
         {
            double val = 0;
            foreach (AgentDailyPlanData d in data.Values)
               val += d.order;
            return val;
         }
      }
      public double RestQty
      {
         get
         {
            double val = TotalPlan;
            double order = GroupOrder(null);
            return val - order;
         }
      }

      public double TotalPlan
      {
         get
         {
            double val = 0;
            foreach (AgentDailyPlanData d in data.Values)
               val += d.plan;
            return val;
         }
      }

      public double PrcTotal
      {
         get
         {
            double plan = TotalPlan;

            double val = GroupOrder(null);

            return plan == 0 ? 0 : val / plan * 100;
         }
      }

      double GroupOrder(string userid)
      {
         double val = (userid == null) ? OrderTotal : GetOrder(userid);
         val *= groupOrderCoef;

         foreach (DataItem gi in groupItems)
         {
            double curVal = (userid == null) ? gi.OrderTotal : gi.GetOrder(userid);
            val += curVal * gi.groupOrderCoef;
         }
         return val;
      }

      internal void AddGroup(List<DataItem> groups, double minInpack)
      {
         if (minInpack != 0)
            groupOrderCoef = item.inPack / minInpack;

         foreach (DataItem di in groups)
         {
            if (di != this)
               groupItems.Add(di);
         }
      }

      static void CreateProperty(TypeBuilder typeBuilder, string propName, string methodName, string userid)
      {
         PropertyBuilder propertyBuilder = typeBuilder.DefineProperty(propName, System.Reflection.PropertyAttributes.None, typeof(double), Type.EmptyTypes);

         MethodBuilder getMethodBuilder = typeBuilder.DefineMethod("get_" + propName, MethodAttributes.Public, CallingConventions.HasThis, typeof(double), Type.EmptyTypes);
         ILGenerator ilg = getMethodBuilder.GetILGenerator();
         ilg.Emit(OpCodes.Ldarg_0);
         ilg.Emit(OpCodes.Ldstr, userid);
         ilg.Emit(OpCodes.Call, typeof(DataItem).GetMethod(methodName));
         ilg.Emit(OpCodes.Ret);
         propertyBuilder.SetGetMethod(getMethodBuilder);
      }

      static void CreateLimitProperty(TypeBuilder typeBuilder, string propName, string method, string userid)
      {
         PropertyBuilder propertyBuilder = typeBuilder.DefineProperty(propName, System.Reflection.PropertyAttributes.None, typeof(string), Type.EmptyTypes);

         MethodBuilder getMethodBuilder = typeBuilder.DefineMethod("get_" + propName, MethodAttributes.Public, CallingConventions.HasThis, typeof(string), Type.EmptyTypes);
         ILGenerator ilg = getMethodBuilder.GetILGenerator();
         ilg.Emit(OpCodes.Ldarg_0);
         ilg.Emit(OpCodes.Ldstr, userid);
         ilg.Emit(OpCodes.Call, typeof(DataItem).GetMethod(method));
         ilg.Emit(OpCodes.Ret);
         propertyBuilder.SetGetMethod(getMethodBuilder);
      }

      public static Type CreateChildType(List<Agent> agents, List<Division> divisions)
      {
         Type retType = typeof(DataItem);
         try
         {
            AssemblyBuilder assemblyBuilder = AppDomain.CurrentDomain.DefineDynamicAssembly(new AssemblyName("GRSoft.NapoleonManager"), AssemblyBuilderAccess.Run);
            ModuleBuilder moduleBuilder = assemblyBuilder.DefineDynamicModule("Dynamic.dll");

            TypeBuilder typeBuilder = moduleBuilder.DefineType("DataItemEx");
            typeBuilder.SetParent(typeof(DataItem));

            ConstructorBuilder cb = typeBuilder.DefineConstructor(MethodAttributes.Public, CallingConventions.Standard, Type.EmptyTypes);
            ILGenerator ilg = cb.GetILGenerator();
            ilg.Emit(OpCodes.Ldarg_0);
            ilg.Emit(OpCodes.Call, typeof(DataItem).GetConstructor(Type.EmptyTypes));
            ilg.Emit(OpCodes.Ret);

            int index = 1;
            foreach (Agent a in agents)
            {
               String id = index.ToString();
               CreateLimitProperty(typeBuilder, "Limit_" + id, "GetLimit", a.id);
               CreateProperty(typeBuilder, "Order_" + id, "GetOrder", a.id);
               CreateProperty(typeBuilder, "Prc_" + id, "GetPrc", a.id);
               index++;
            }

            foreach(Division d in divisions)
            {
               String divId = d.id.ToString();

               CreateLimitProperty(typeBuilder, "SVLimit_" + divId, "GetSVLimit", divId);
               CreateProperty(typeBuilder, "SVOrder_" + divId, "GetSVOrder", divId);
               CreateProperty(typeBuilder, "SVPrc_" + divId, "GetSVPrc", divId);
            }

            //index = 1;
            //for (; index < divisions.Count; index ++ )
            //{
            //   Division d = divisions[index];
            //   String divId = d.id.ToString();

            //   CreateLimitProperty(typeBuilder, "SVLimit_" + divId, "GetSVLimit", divId);
            //   CreateProperty(typeBuilder, "SVOrder_" + divId, "GetSVOrder", divId);
            //   CreateProperty(typeBuilder, "SVPrc_" + divId, "GetSVPrc", divId);
            //}

            retType = typeBuilder.CreateType();
         }
         catch (Exception e)
         {
            MessageBox.Show(e.Message);
            retType = typeof(DataItem);
         }

         return retType;
      }
   }

   class FolderEx
   {
      ManagerFolder folder;
      public FolderEx(ManagerFolder mf)
      {
         folder = mf;
      }

      public ManagerFolder Folder { get { return folder; } }

      public override string ToString()
      {
         String offset = "";
         for (int i = 0; i < folder.level; i++)
            offset += "  ";
         return offset + folder.name;
      }
   }
}
