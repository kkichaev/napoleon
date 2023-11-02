using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

using GRSoft.Network;
using System.Collections;
using System.Threading;

namespace GRSoft.NapoleonManager
{
   public partial class ProvFocusedGroupEditor : Form
   {
      bool dirty;

      Agent curAgent = null;

      Dictionary<string, List<FocusedItems>> items = new Dictionary<string,List<FocusedItems>>();

      public ProvFocusedGroupEditor()
      {
         InitializeComponent();

         ReceiveData();
         FillGrids();

         dirty = false;
      }

      List<Division.DivisionAgent> dagents = new List<Division.DivisionAgent>();
      void ReceiveData()
      {
         List<IDataSet> updSets = new List<IDataSet>();
         Config cfg = Config.GetConfig();

         Agents agents = DataModule.Get("Agents") as Agents;
         if (agents == null)
         {
            agents = new Agents();
            updSets.Add(agents);
            updSets.Add(DivisionList.GetDataSet());
         }

         DataSet<string, DivisionManager> dsManager = DataModule.Get(DivisionManager.OBJECT_NAME) as DataSet<string, DivisionManager>;
         if (dsManager == null)
            dsManager = new DataSet<string, DivisionManager>(DivisionManager.OBJECT_NAME);
         if (dsManager.Count == 0)
         {
            dsManager.Filter = "login = '" + cfg.login + "' and password = '" + cfg.password + "'";
            updSets.Add(dsManager);
         }

         if (updSets.Count > 0)
         {
            DataModule.OnDataResponceError += new EventDataResponseError(DataConnectionError);
            Thread tj = DataModule.RefreshGiveSets(cfg.GetConnection(), updSets, null);
            tj.Join();
            DataModule.ClearEvents();

            updSets.Clear();

            if (dsManager == null || dsManager.Count == 0)
            {
               MessageBox.Show("Текущий пользователь не является менеджером");
               return;
            }
         }

         foreach (DivisionManager dm in dsManager.Data)
         {
            if (dm.division != 0)
            {
               DivisionList dl = DivisionList.GetDataSet();
               Division d = dl[dm.division];
               dagents = d.GetAllAgents();
            }
            break;
         }

         DataSet<string, Price> dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ??
            new DataSet<string, Price>(Price.OBJECT_NAME);

         if (dsPrice.Count == 0)
         {
            string priceFilter = "userid in (";
            foreach (Division.DivisionAgent da in dagents)
               priceFilter += "'" + da.id + "',";

            priceFilter = priceFilter.TrimEnd(new char[] { ',' }) + ")";
            dsPrice.Filter = priceFilter;
            updSets.Add(dsPrice);
         }

         DataSet<int, FocusedItems> dsFocused = (DataSet<int, FocusedItems>)DataModule.Get(FocusedItems.OBJECT_NAME);
         if( dsFocused == null )
            dsFocused = new DataSet<int, FocusedItems>(FocusedItems.OBJECT_NAME);
         dsFocused.Filter = "not userid is null";
         updSets.Add(dsFocused);

         Thread j = DataModule.RefreshGiveSets(cfg.GetConnection(), updSets, null);
         FmWait.ShowForm(this, j);
         j.Join();

         DataModule.ClearEvents();
         FmWait.CloseForm();
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();
         MessageBox.Show(e.Msg);
         FmWait.CloseForm(true);
      }

      void FillGrids()
      {
         DataSet<int, FocusedItems> dsFocused = (DataSet<int, FocusedItems>)DataModule.Get(FocusedItems.OBJECT_NAME);
         foreach (FocusedItems fi in dsFocused.Data)
         {
            List<FocusedItems> fitems;
            if (items.ContainsKey(fi.userid))
               fitems = items[fi.userid];
            else
            {
               fitems = new List<FocusedItems>();
               items.Add(fi.userid, fitems);
            }

            fitems.Add(fi);
         }

         Dictionary<string, bool> keys = new Dictionary<string, bool>();
         List<FAgentData> agentData = new List<FAgentData>();
         foreach (Division.DivisionAgent a in dagents)
         {
            Agent agent = a.agent;
            if (agent != null && keys.ContainsKey(agent.id) == false)
            {
               keys.Add(agent.id, true);
               agentData.Add(new FAgentData(agent));
            }
         }
         agentData.Sort();
         dgvAgents.DataSource = agentData;
      }

      bool CheckChanges()
      {
         if (!dirty)
            return true;

         DialogResult res = MessageBox.Show("Сохранить изменнения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         if (res == DialogResult.Cancel)
            return false;

         if (res == DialogResult.Yes)
            SaveChanges();

         return true;
      }

      private void SaveChanges()
      {
         items[curAgent.id] = dgvItems.DataSource as List<FocusedItems>;
         List<ReplacedSet> rpl = new List<ReplacedSet>();

         foreach (KeyValuePair<string, List<FocusedItems>> de in items)
         {
            DataSet<int, FocusedItems> wr = new DataSet<int,FocusedItems>(FocusedItems.OBJECT_NAME);
            foreach(FocusedItems fi in de.Value)
               wr.Add(wr.Count, fi);

            ReplacedSet rs = new ReplacedSet(de.Key, wr);
            rpl.Add(rs);
         }

         if (DataModule.UpdateDataSet(null, null, rpl, Config.GetConfig().GetConnection()))
            MarkDirty(false);
      }

      private void MarkDirty(bool dirty)
      {
         this.dirty = dirty;
         tbSave.Enabled = dirty;
      }

      private void tbSave_Click(object sender, EventArgs e)
      {
         SaveChanges();
      }

      private void FocusedGroupEditor_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (!CheckChanges())
            e.Cancel = true;
      }

      private void dgvAgents_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         DataGridViewRow r = dgvAgents.Rows[e.RowIndex];
         FAgentData a = r.DataBoundItem as FAgentData;

         if (curAgent != null)
            items[curAgent.id] = dgvItems.DataSource as List<FocusedItems>;

         curAgent = a.agent;
         List<FocusedItems> fi;
         if( items.ContainsKey(curAgent.id) )
            fi = items[curAgent.id];
         else
         {
            fi = new List<FocusedItems>();
            items.Add(curAgent.id, fi);
         }
         dgvItems.DataSource = fi;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (dgvItems.SelectedCells.Count > 0)
         {
            if (MessageBox.Show("Удалить данные?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
            {
               List<FocusedItems> fd = (List<FocusedItems>)dgvItems.DataSource;
               List<FocusedItems> newFd = new List<FocusedItems>(fd);
               foreach (DataGridViewCell c in dgvItems.SelectedCells)
               {
                  DataGridViewRow r = dgvItems.Rows[c.RowIndex];
                  newFd.Remove(r.DataBoundItem as FocusedItems);
               }

               dgvItems.DataSource = newFd;
               MarkDirty(true);
            }
         }
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         List<FocusedItems> l = dgvItems.DataSource as List<FocusedItems>;
         List<Price> selected = new List<Price>();
         if( l != null )
            foreach(FocusedItems fi in l)
               selected.Add(fi.price);

         List<Price> newSelected = FmSelectSKU.SelectItems(this, selected, curAgent.id);
         if (newSelected != null)
         {
            List<FocusedItems> newI = new List<FocusedItems>();
            foreach (Price p in newSelected)
            {
               FocusedItems i = new FocusedItems();
               i.price = p;
               i.id = p.id;
               i.userid = curAgent.id;

               newI.Add(i);
            }

            items[curAgent.id] = newI;
            dgvItems.DataSource = newI;

            MarkDirty(true);
         }
      }
   }

   class FocusedItems : GRSoft.Network.DataObject
   {
      public static string OBJECT_NAME = "FocusedItems";

      public string userid = "";

      public string id = "";

      [Reference("ManagerPrice", "id")]
      public Price price = null;

      public string ItemName { get { return (price == null) ? "" : price.name; } }
   }

   class FAgentData : IComparable<FAgentData>
   {
      public FAgentData(Agent a) { agent = a; }

      public Agent agent;

      public string AgentName { get { return agent.Name; } }

      public int CompareTo(FAgentData other) { return AgentName.CompareTo(other.AgentName); }
   }
}
