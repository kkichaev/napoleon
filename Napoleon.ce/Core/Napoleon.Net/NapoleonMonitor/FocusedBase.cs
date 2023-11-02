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
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   class FocusOrgData : IComparable<FocusOrgData>
   {
      Org org;

      public FocusOrgData(Org o) { org = o; }

      public string OrgName { get { return org.Name; } }
      public Org Org { get { return org; } }

      int IComparable<FocusOrgData>.CompareTo(FocusOrgData other) { return OrgName.CompareTo(other.OrgName); }
   }

   class FocusEmptyOrg : Org
   {
      public FocusEmptyOrg()
      {
         name = "<Для всех клиентов>";
      }
   }

   class FocusController
   {
      ComboBox cbAgents;
      DataGridView dgvOrgs;
      TreeView tvFolders;
      ToolStripButton tbSave;

      DataSet<string, Org> dsOrg;
      DataSet<string, ManagerFolder> dsFolder;
      DataSet<string, ManagerFolder> dsCommonFolder;
      DataSet<string, Price> dsPrice;
      IDataSet dsFocused;

      bool showPriceNodes;

      Org selected = null;
      Agent curAgent = null;
      bool orgChanged = false, treeChanged = false;

      public event EventHandler BeforeSave;
      public event OrgChangedHandle OrgChanged;

      public Org CurOrg { get { return selected; } }
      public Agent CurAgent { get { return curAgent; } }
      public bool TreeChanged { get { return treeChanged; } set { treeChanged = value; } }

      public FocusController(ComboBox cbAgents, DataGridView dgvOrgs, TreeView tvFolders, ToolStripButton tbSave, IDataSet dsFocused) :
         this(cbAgents, dgvOrgs, tvFolders, tbSave, dsFocused, false)
      {
      }

      public FocusController(ComboBox cbAgents, DataGridView dgvOrgs, TreeView tvFolders, ToolStripButton tbSave, IDataSet dsFocused, bool showPriceNodes)
      {
         this.cbAgents = cbAgents;
         this.dgvOrgs = dgvOrgs;
         this.tvFolders = tvFolders;
         this.tbSave = tbSave;
         this.showPriceNodes = showPriceNodes;

         dsOrg = new DataSet<string, Org>(Org.OBJECT_NAME, false);
         dsFolder = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME, false);
         dsCommonFolder = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME, false);
         if (showPriceNodes)
            dsPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);

         this.dsFocused = dsFocused;

         dgvOrgs.AutoGenerateColumns = false;

         this.cbAgents.SelectionChangeCommitted += new System.EventHandler(this.cbAgents_SelectionChangeCommitted);
         this.tbSave.Click += new EventHandler((o, e) => {
            SaveChanges();
         });

         this.dgvOrgs.RowEnter += new DataGridViewCellEventHandler((o, e) => {
            OnSelectedOrg((dgvOrgs.Rows[e.RowIndex].DataBoundItem as FocusOrgData).Org);
         });
      }

      private void OnSelectedOrg(Org org)
      {
         if (tvFolders.Nodes.Count == 0)
            return;

         Org prev = selected;
         selected = org;
         if (OrgChanged != null)
            OrgChanged.Invoke(this, new OrgChangedArgs(org, prev));
         
         treeChanged = false;
      }

      public void Init()
      {
         DataModule.DataProcessed += new EventHandler(CommonDataLoaded);

         Agents dsAgents = Agents.GetDataSet();

         List<IDataSet> refreshList = new List<IDataSet>();

         dsCommonFolder.Filter = "\"userid\" is null";
         refreshList.Add(dsAgents);
         refreshList.Add(dsCommonFolder);

         DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), refreshList, null);
      }

      private void cbAgents_SelectionChangeCommitted(object sender, EventArgs e)
      {
         if (!CheckChanges())
         {
            cbAgents.SelectedItem = curAgent;
            return;
         }
         OnAgentSelected(cbAgents.SelectedItem as Agent);
      }

      private void CommonDataLoaded(object sender, EventArgs e)
      {
         DataModule.DataProcessed -= new EventHandler(CommonDataLoaded);
         dgvOrgs.BeginInvoke(new EmptyParamHandler(FillAgents));
      }

      //Заполнение выпадающего списка агентов - "Агент"
      private void FillAgents()
      {
         foreach (Agent a in DataModule.Get("Agents").Data)
            cbAgents.Items.Add(a);

         cbAgents.Sorted = true;
         if (cbAgents.Items.Count > 0)
         {
            Agent a = cbAgents.Items[0] as Agent;
            cbAgents.SelectedItem = a;
            OnAgentSelected(a);
         }
      }

      void OnAgentSelected(Agent agent)
      {
         List<IDataSet> refreshList = new List<IDataSet>();

         String filter = String.Format("\"userid\" in ('{0}')", agent.id);
         dsOrg.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agent.id), dsOrg.Name);

         //dsOrg.Filter = filter;
         dsFolder.Filter = filter;
         dsFocused.Filter = filter;

         refreshList.Add(dsOrg);
         refreshList.Add(dsFolder);

         if (showPriceNodes)
         {
            dsPrice.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agent.id), dsPrice.Name);
            refreshList.Add(dsPrice);
         }

         refreshList.Add(dsFocused);

         selected = null;
         MarkDirty(false);

         curAgent = agent;

         DataModule.DataProcessed += new EventHandler(DataLoaded);
         DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
            refreshList, FmWait.ProgressIndicator);
      }

      public void MarkDirty(bool dirty)
      {
         treeChanged = dirty;
         orgChanged = dirty;
         tbSave.Enabled = dirty;
      }

      private void DataLoaded(object sender, EventArgs e)
      {
         DataModule.DataProcessed -= new EventHandler(DataLoaded);
         tvFolders.BeginInvoke(new EmptyParamHandler(RefreshForm));
      }

      void RefreshForm()
      {
         tvFolders.SuspendLayout();
         try
         {
            DataSet<string, ManagerFolder> folders = (dsFolder.Count > 0) ? dsFolder : dsCommonFolder;
            if (showPriceNodes)
            {
               ArticlesTreeConstructor tc = new ArticlesTreeConstructor(tvFolders, folders, dsPrice);
               tc.MakeArticlesTree(0 ,1);
            }
            else
            {
               FolderTree.MakeTree(tvFolders.Nodes, (ICollection<ManagerFolder>)folders.Data);
            }
            //tvFolders.ExpandAll();
            if (tvFolders.Nodes.Count > 0)
               tvFolders.TopNode = tvFolders.Nodes[0];
         }
         finally
         {
            tvFolders.ResumeLayout();
         }

         List<FocusOrgData> bs = new List<FocusOrgData>();
         foreach (Org o in dsOrg.Data)
            bs.Add(new FocusOrgData(o));

         bs.Sort();
         bs.Insert(0, new FocusOrgData(new FocusEmptyOrg()));
         dgvOrgs.DataSource = bs;
      }

      public bool CheckChanges()
      {
         if (!orgChanged)
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
         if( BeforeSave != null )
            BeforeSave.Invoke(this, EventArgs.Empty);

         if (DataModule.ReplaceDataSet(dsFocused, curAgent.id, Config.GetConfig().GetConnection()))
         {
            orgChanged = false;
            tbSave.Enabled = false;
         }
      }
   }

   public class OrgChangedArgs
   {
      public Org newOrg;
      public Org prevOrg;

      public OrgChangedArgs(Org newOrg, Org prevOrg)
      {
         this.newOrg = newOrg;
         this.prevOrg = prevOrg;
      }
   }

   public delegate void OrgChangedHandle (object sender, OrgChangedArgs args);
}