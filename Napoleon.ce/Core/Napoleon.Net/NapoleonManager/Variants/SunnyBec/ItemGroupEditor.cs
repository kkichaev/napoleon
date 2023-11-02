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
   public partial class ItemGroupEditor : Form
   {
      static ItemGroupEditor instance = null;

      SimpleDataSet<ItemGroup> groups = new SimpleDataSet<ItemGroup>(ItemGroup.OBJECT_NAME, false);
      DataSet<string, ManagerFolder> dsFolders;
      DataSet<string, Price> dsPrice;

      BindingList<AgentEx> agents;

      public ItemGroupEditor()
      {
         InitializeComponent();

         dgvGroups.AutoGenerateColumns = false;
         dgvItems.AutoGenerateColumns = false;

         dsFolders = (DataSet<string, ManagerFolder>)DataModule.Get(ManagerFolder.OBJECT_NAME) ??
            new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);

         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ??
            new DataSet<string, Price>(Price.OBJECT_NAME);
      }

      public static void Open()
      {
         if (instance == null)
         {
            instance = new ItemGroupEditor();
            instance.Show();
         }
         else
         {
            instance.RefreshData();
            instance.BringToFront();
         }
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
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
         List<IDataSet> updSets = new List<IDataSet>();
         if (dsFolders.Count == 0)
         {
            dsFolders.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            updSets.Add(dsFolders);
         }

         if (dsPrice.Count == 0)
         {
            dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            updSets.Add(dsPrice);
         }

         groups.Filter = "(not \"userid\" is null) or (\"userid\" <> '')";
         updSets.Add(groups);
         FmWait.StdDataRefresh(this, updSets, DoLoadData, tsbRefresh);
      }

      void DoLoadData()
      {
         List<AgentEx> agentsList = new List<AgentEx>();
         foreach (Agent a in (CurrentUser.user as Manager).GetAgents().Data)
            agentsList.Add(new AgentEx(a, groups));

         agentsList.Sort();
         agents = new BindingList<AgentEx>(agentsList);

         cbAgents.DataSource = agents;
         if (agents.Count > 0)
            cbAgents.SelectedIndex = 0;
      }

      private void dgvGroups_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         ItemGroup item = dgvGroups.Rows[e.RowIndex].DataBoundItem as ItemGroup;
         if (item == null)
            return;
         dgvItems.DataSource = item.items;
         tsStatusText.Text = "Всего " + item.items.Count + " товаров";
      }

      private void tsbRefresh_Click(object sender, EventArgs e)
      {
         if (CheckChanges())
            RefreshData();
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
         List<ReplacedSet> rpl = new List<ReplacedSet>();
         foreach (AgentEx a in cbAgents.Items)
         {
            if (a.Dirty == false)
               continue;

            SimpleDataSet<ItemGroup> items = new SimpleDataSet<ItemGroup>(ItemGroup.OBJECT_NAME, false);
            foreach (ItemGroup ig in a.Groups)
            {
               ig.userid = a.Agent.id;
               items.Add(ig);
            }

            ReplacedSet rs = new ReplacedSet(a.Agent.id, items);
            rpl.Add(rs);
         }

         bool ret = DataModule.UpdateDataSet(null, null, rpl, Config.GetConfig().GetConnection());
         if (showDialog)
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");

         return ret;
      }

      private void tsbAddGroup_Click(object sender, EventArgs e)
      {
         AgentEx a = cbAgents.SelectedItem as AgentEx;
         
         ItemGroup newGroup = a.Groups.AddNew();
         newGroup.id = GRSoft.Network.DataObject.GenId();
         newGroup.name = "";
         newGroup.userid = a.Agent.id;

         SetDirty();
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         if (SaveChanges(true))
            ClearDirty();
      }

      private void tsbDelGroup_Click(object sender, EventArgs e)
      {
         AgentEx a = cbAgents.SelectedItem as AgentEx;

         List<ItemGroup> removed = new List<ItemGroup>();
         foreach(DataGridViewRow row in dgvGroups.SelectedRows)
            removed.Add(row.DataBoundItem as ItemGroup);

         removed.ForEach(x => a.Groups.Remove(x));
         SetDirty();
      }

      private void tsbDelItem_Click(object sender, EventArgs e)
      {
         if( dgvGroups.CurrentRow == null )
            return;

         ItemGroup item = dgvGroups.CurrentRow.DataBoundItem as ItemGroup;
         if (item == null)
            return;

         List<ItemGroup.Item> removed = new List<ItemGroup.Item>();
         foreach (DataGridViewRow row in dgvItems.SelectedRows)
            removed.Add(row.DataBoundItem as ItemGroup.Item);

         removed.ForEach(x => item.items.Remove(x));
         dgvItems.DataSource = null;
         dgvItems.DataSource = item.items;

         SetDirty();
      }

      private void tsbAddItem_Click(object sender, EventArgs e)
      {
         if (dgvGroups.CurrentRow == null)
            return;

         ItemGroup item = dgvGroups.CurrentRow.DataBoundItem as ItemGroup;
         if (item == null)
            return;

         List<Price> selected = new List<Price>();
         item.items.ForEach(x => selected.Add(x.price));

         List<Price> newItems = FmSelectSKU.SelectItems(this, selected, null, true);
         if (newItems == null)
            return;

         List<ItemGroup.Item> items = new List<ItemGroup.Item>();
         foreach (Price p in newItems)
            items.Add(new ItemGroup.Item(p));

         item.items = items;
         dgvItems.DataSource = item.items;
         SetDirty();
      }

      void SetDirty()
      {
         tsbSave.Enabled = true;
         ((AgentEx)cbAgents.SelectedItem).Dirty = true;
         agents.ResetItem(cbAgents.SelectedIndex);
      }

      void ClearDirty()
      {
         tsbSave.Enabled = false;

         for(int i=0; i<agents.Count; i++ )
         {
            AgentEx ae = agents[i];
            ae.Dirty = false;
            agents.ResetItem(i);
         }
      }

      private void cbAgents_SelectedIndexChanged(object sender, EventArgs e)
      {
         AgentEx ae = cbAgents.SelectedItem as AgentEx;

         if( dgvGroups.DataSource != ae.Groups)
            dgvGroups.DataSource = ae.Groups; ;
      }
   }

   class AgentEx : IComparable<AgentEx>
   {
      bool dirty = false;
      Agent agent;
      BindingList<ItemGroup> groups = new BindingList<ItemGroup>();

      public AgentEx(Agent a, SimpleDataSet<ItemGroup> dsGroups)
      {
         agent = a;
         dirty = false;

         foreach (ItemGroup ig in dsGroups.Data)
            if (ig.userid == a.id)
               groups.Add(ig);
      }

      public bool Dirty { get { return dirty; } set { dirty = value; } }

      public override string ToString()
      {
         return agent.name + (dirty ? "*" : "");
      }

      public Agent Agent { get { return agent; } }
      public BindingList<ItemGroup> Groups { get { return groups; } }

      public int CompareTo(AgentEx other)
      {
         return agent.name.CompareTo(other.agent.name);
      }
   }
}
