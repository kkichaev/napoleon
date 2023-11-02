using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Collections;

namespace GRSoft.NapoleonManager
{
   public partial class UserForm : UserControl
   {
      public DataSet<string, ManagerFolder> dsManagerFolder;
      protected DataSet<string, Org> dsOrg;
      private DataSet<int, OrgFolder> mgrRoute;
      private DataSet<string, Price> dsPrice;
      protected DataSet<int, Matrix> dsCommonMatrix;
      private DataSet<int, AgentMatrix> dsAgentMatrix;
      private DataSet<string, UserInfo> dsUserInfo;

#if SCRIPT_DOC
      private DataSet<int, ScriptDef> dsCommonScriptDefs;
      private DataSet<int, AgentScript> dsAgentScript;
#endif

      private Agent agent = null;
      protected Divisions owner;
      private bool canCheckNode = false;

#if QUESTION
      private DataSet<string, Question> dsCommonQuest;
      private DataSet<string, AgentQuest> dsAgentQuest;
#endif

      public UserForm(Divisions owner)
      {
         InitializeComponent();
         dgvOrgs.AutoGenerateColumns = false;
         this.owner = owner;
         
#if NO_ROUTE_EDITOR
         btnEditRoute.Visible = false;
#endif

#if DISABLE_PRICE
         userDetails.TabPages.Remove(udFolders);
#endif
#if DISABLE_MATRIX
         userDetails.TabPages.Remove(udMatrix);
#endif

#if SELECT_ORG_LOCATION
         Button mtxBtn = new Button();
         mtxBtn.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
         Point p = new Point(btnEditRoute.Left, btnEditRoute.Top);
         p.Offset(btnEditRoute.Width + 2, 0);
         mtxBtn.Location = p;
         mtxBtn.Name = "mtxBtn";
         mtxBtn.Size = new System.Drawing.Size(150, btnEditRoute.Height);
         mtxBtn.TabIndex = 2;
         mtxBtn.Text = "Координаты клиента";
         mtxBtn.UseVisualStyleBackColor = true;
         mtxBtn.Click += new EventHandler((o, e) => SelectOrgLocation.Open(Agent));

         panel3.Controls.Add(mtxBtn);
#endif 

         AdjustForm();
         InitDataSets();
      }

      virtual protected void AdjustForm()
      {
         Dock = DockStyle.Fill;
         tvAccessibleArticles.ImageList = owner.images;
         tvAccessibleArticles.Visible = true;
         wbArticlesMessage.Visible = false;
         userDetails.ShowToolTips = true;
         userDetails.TabPages[0].ToolTipText = "перенесите желаемого контрагента в нужный день недели";
         userDetails.TabPages[1].ToolTipText = "оставьте выделенным только нужные для агента группы товаров";
      }

      virtual public Agent Agent
      {
         get { return agent; }
         set
         {
            agent = value;
            name.Text = agent.name;
            GetDataForCurAgent(agent.id);
         }
      }

      private void GetDataForCurAgent(string AgentID)
      {
         Config c = Config.GetConfig();
         if (c.CheckLogin() == false)
            return;

         DBConnection conn = c.GetConnection();

         mgrRoute = new DataSet<int,OrgFolder>("OrgFolder", false);
         //DataModule.DataProcessed += new EventHandler(DataCurAgentLoaded);
         DataModule.SetDataRepsonceHandlers(DataCurAgentLoaded, DataConnectionError);

         const string USERID_IN_STR = "\"userid\" in ('{0}')";
         
         
         string filter = String.Format(USERID_IN_STR, AgentID);

         List<IDataSet> updSets = new List<IDataSet>();

         updSets.Add(dsManagerFolder);
         dsManagerFolder.Command = new ServerCommand(Commands.Impersonate(Commands.GET, AgentID), dsManagerFolder.Name);
         //dsManagerFolder.Filter = filter;

         updSets.Add(mgrRoute);
         mgrRoute.Filter = filter;

         updSets.Add(dsAgentMatrix);
         dsAgentMatrix.Filter = filter;

#if SCRIPT_DOC
         updSets.Add(dsAgentScript);
         dsAgentScript.Filter = filter;
         updSets.Add(dsCommonScriptDefs);
#endif

#if QUESTION
         updSets.Add(dsAgentQuest);
         dsAgentQuest.Filter = filter;
         updSets.Add(dsCommonQuest);
#endif

         updSets.Add(dsUserInfo);
         dsUserInfo.Filter = filter;

         if (dsPrice.Count == 0)
         {
            updSets.Add(dsPrice);
            dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
         }

         if (dsCommonMatrix.Count == 0)
         {
            updSets.Add(dsCommonMatrix);
            dsCommonMatrix.Filter = DataUtils.USERID_IS_NULL_STR;
         }

         dsOrg = DataModule.GetUserDataSet(agent.id, "Org", typeof(DataSet<string, Org>)) as DataSet<string, Org>;
         if (NeedUpdateOrg())
         {
            // получаем контрагентов так же как их видит торговый
            dsOrg.Command = new ServerCommand(Commands.Impersonate(Commands.GET, AgentID), dsOrg.Name);
            updSets.Add(dsOrg);
            //dsOrg.Filter = filter;
         }

         BeforeUpdateData(agent.id, updSets);
         FmWait.ShowForm(owner, DataModule.RefreshGiveSets(conn, updSets, FmWait.ProgressIndicator));
      }

      protected virtual bool NeedUpdateOrg()
      {
         return dsOrg.Count == 0;
      }

      protected virtual void BeforeUpdateData(String userid, List<IDataSet> updSets)
      {
      }

      protected virtual void DataLoaded()
      {
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();

         Invoke(new EmptyParamHandler(delegate
         {
            FmWait.CloseForm();

            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }));
      }

      private void DataCurAgentLoaded(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         DataLoaded();

         // если нет контрагентов, то берем общих
         if (dsOrg.Count == 0)
         {
            DataSet<string, Org>  tdsOrg = DataModule.Get("Org") as DataSet<string, Org>;
            if (tdsOrg != null)
               dsOrg = tdsOrg;
         }
         BeginInvoke(new EmptyParamHandler(ControlsFillAfterLoaded));
      }

      //Установка визуальных компонентов после загрузки из базы
      private void ControlsFillAfterLoaded()
      {
         FillListOrgs();
         FillRoute();
         FillMatrix();
#if SCRIPT_DOC
         FillScripts();
#endif

#if QUESTION
         FillQuest();
#endif
         
         if ( owner.mainArticleFolder.Count <= 0 )
         {
            MakeArticlesTreeWithoutMainFolders();
         } else
            MakeArticlesTree();

         SetNodeChecking();
         tbPhone.Text = dsUserInfo.ContainsKey(Agent.id) ? dsUserInfo[Agent.id].phone : tbPhone.Text = string.Empty;

         AfterControlFilled();
      }

      protected virtual void AfterControlFilled()
      {
      }

      //Наполнить tvAccessibleArticles без общего набора из набора агента, или показать 
      //wbArticlesMessage с сообщением об ошибке, если такого набора нет.
      private void MakeArticlesTreeWithoutMainFolders()
      {
         //if (dsManagerFolder.Count > 0)
         {
            ArticlesControlVisible(true);
            MakeArticlesTree();
         }
         //else
         //{
         //   const string NOT_PRESENT_ARTICLES_DATA_SET = "<html><div align=\"left\"><font color=Gray size=3>"+
         //      "Невозможно отобразить \"Доступный товар\", потому что нет " +
         //      "общего файла данных и отсутствует файл выбранного агента.</font></div></html>";
         //   wbArticlesMessage.DocumentText = NOT_PRESENT_ARTICLES_DATA_SET;
         //   ArticlesControlVisible(false);
         //}
      }

      //Управляет свойством Visible для группы "Доступный товар", т.е либо кажем
      //TreeView с данными, либо WebBrowser с сообощением об ошибке
      private void ArticlesControlVisible(bool visible)
      {
         tvAccessibleArticles.Visible = visible;
         wbArticlesMessage.Visible = !visible;
      }

      private void FillRoute()
      {
         ClearRoutNodes();
         if (mgrRoute != null && dsOrg != null)
         {
            foreach (KeyValuePair<int, OrgFolder> kv in mgrRoute)
            {
               OrgFolder of = kv.Value;
               TreeNode tn = FindRouteNode(of.name);
               if (tn != null)
               {
                  foreach (OrgFolderItem oi in of.items)
                  {
                     if (oi.org == null)
                     {
                        Org o = null;

                        if(dsOrg.ContainsKey(oi.name))
                        {
                            o = dsOrg[oi.name];
                        }

                        if (o != null)
                           oi.org = o;
                     }
                     if (oi.org != null)
                     {
                        TreeNode child = new TreeNode(oi.org.ToString());
                        child.Tag = oi.org;
                        tn.Nodes.Add(child);
                        tn.BackColor = Color.Gold;
                     }
                  }
               }
            }
            mgrRoute = null;
         }
      }

      private bool AgentMatrixContais(String matrix)
      {
         foreach (AgentMatrix am in dsAgentMatrix.Data)
         {
            if (am.name == matrix)
            {
               return true;
            }
         }

         return false;
      }

      private void FillMatrix()
      {
         tvAgentMatrix.BeginUpdate();

         tvAgentMatrix.Nodes.Clear();

         foreach (Matrix matrix in dsCommonMatrix.Data)
         {
            TreeNode node = new TreeNode();
            node.Tag = matrix;
            node.Text = matrix.name;

            if (AgentMatrixContais(matrix.name))
            {
               node.Checked = true;
            }

            foreach (MatrixItem mi in matrix.items)
            {
               TreeNode nodeItem = new TreeNode();
               
               nodeItem.Text = mi.price == null ? string.Empty : mi.price.name;
               nodeItem.Tag = mi;
               nodeItem.Checked = node.Checked;
               node.Nodes.Add(nodeItem);
            }

            tvAgentMatrix.Nodes.Add(node);
         }

         tvAgentMatrix.EndUpdate();
      }

#if SCRIPT_DOC
      private void FillScripts()
      {
         tvScript.BeginUpdate();

         tvScript.Nodes.Clear();

         List<ScriptDef> list = new List<ScriptDef>();
         list.AddRange(dsCommonScriptDefs.Values);
         list.Sort(new Comparison<ScriptDef>(delegate(ScriptDef sd1, ScriptDef sd2) { return sd1.Name.CompareTo(sd2.Name); }));

         foreach (ScriptDef script in list)
         {
            TreeNode node = new TreeNode();
            node.Tag = script;
            node.Text = script.Name;

            if (IsAgentScript(script.id))
               node.Checked = true;

            if (script.items != null)
               foreach (ScriptDefItem item in script.items)
               {
                  TreeNode n = node.Nodes.Add(item.Name);
                  n.StateImageIndex = 0;
               }

            tvScript.Nodes.Add(node);
         }

         tvScript.EndUpdate();
      }

      private bool IsAgentScript(int id)
      {
         bool result = false;

         foreach (AgentScript agentScript in dsAgentScript.Data)
            if (agentScript.script == id)
            {
               result = true;
               break;
            }

         return result;
      }
#endif

#if QUESTION
      private void FillQuest()
      {
         tvQuest.BeginUpdate();

         tvQuest.Nodes.Clear();

         List<Question> list = new List<Question>();
         list.AddRange(dsCommonQuest.Values);
         list.Sort(new Comparison<Question>(delegate(Question q1, Question q2) 
            { 
               return q1.Number.CompareTo(q2.Number); 
            }));

         foreach (Question quest in list)
         {
            TreeNode node = new TreeNode();
            node.Tag = quest;
            node.Text = quest.Name;

            if (IsAgentQuest(quest.idquest))
               node.Checked = true;

            if (quest.items != null)
               foreach (QuestionItem item in quest.items)
               {
                  TreeNode n = node.Nodes.Add(item.Id);
                  n.StateImageIndex = 0;
               }

            tvQuest.Nodes.Add(node);
         }

         tvQuest.EndUpdate();
      }

      private bool IsAgentQuest(string id)
      {
         bool result = false;

         foreach (AgentQuest agentQuest in dsAgentQuest.Data)
            if (agentQuest.idquest.Equals(id))
            {
               result = true;
               break;
            }

         return result;
      }
#endif

      private void ClearRoutNodes()
      {
         ClearTreeView(tvDayTasks, false);
      }

      private void ClearMatrixNodes()
      {
         //ClearTreeView(tvMatrix, true);
      }

      private void ClearTreeView(TreeView treeView, bool fullCreal)
      {
         treeView.SuspendLayout();

         try
         {
            foreach (TreeNode treeNode in treeView.Nodes)
            {
               treeNode.Nodes.Clear();
               treeNode.BackColor = Color.White;
            }

            if (fullCreal)
            {
               treeView.Nodes.Clear();
            }
         }
         finally
         {
            treeView.ResumeLayout();
         }
      }

      TreeNode FindRouteNode(string dayOfWeek)
      {
         TreeNode ret = null;

         foreach (TreeNode tv in tvDayTasks.Nodes)
         {
            if (String.Compare(tv.Text, dayOfWeek, true) == 0)
            {
               ret = tv;
               break;
            }
         }

         return ret;
      }

      protected virtual void FillListOrgs()
      {
         List<Org> orgs = new List<Org>();
         foreach (Org o in dsOrg.Data)
            orgs.Add(o);

         orgs.Sort();
         dgvOrgs.DataSource = orgs;

         //DataUtils.FillGridFromDS(dgvOrgs, dgvOrgsName, dsOrg);
      }

      private void SetNodeChecking()
      {
         canCheckNode = false;
         tvAccessibleArticles.SuspendLayout();

         ICollection folderList = dsManagerFolder.Count != 0 ? dsManagerFolder.Data : owner.Folders;

         if (folderList != null)
         {
            SetCheckAllNode(folderList.Count == 0);
            foreach (ManagerFolder folder in folderList)
            {
               SetNodeStateFromId(folder.id);
            }
         }

         tvAccessibleArticles.ResumeLayout();
         canCheckNode = true;
      }

      private void SetNodeStateFromId(string folderId)
      { 
         foreach (TreeNode node in tvAccessibleArticles.Nodes)
         {
            TreeNode n = FindNodeById(node, folderId);
            if (n != null)
            {
               n.Checked = true;
            }
         }
      }

      private TreeNode FindNodeById(TreeNode parent, string id)
      {
         if ((parent.Tag as ManagerFolder).id.Equals(id))
         {
            return parent;
         }

         foreach (TreeNode node in parent.Nodes)
         {
            if (node.Nodes.Count > 0)
            {
               TreeNode n = FindNodeById(node, id);
               {
                  if (n != null)
                  {
                     n.Checked = true;
                  }
               }
            }

            if ((node.Tag as ManagerFolder).id.Equals(id))
            {
               return node;
            }
         }

         return null;
      }

      private void SetCheckAllNode(bool status)
      {
         foreach (TreeNode n in tvAccessibleArticles.Nodes)
         {
            CheckChildNodes(n,status);
         }
      }

      private void CheckChildNodes(TreeNode node, bool status)
      {
         if (node.Nodes.Count > 0)
         {
            foreach (TreeNode n in node.Nodes)
            {
               CheckChildNodes(n, status);
            }
         }

         node.Checked = status;
      }

      private void InitDataSets()
      {
         dsManagerFolder = new DataSet<string, ManagerFolder>("ManagerFolder", false, true);

         //dsOrg = new DataSet<string, Org>("Org", false);

         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         dsCommonMatrix = DataModule.Get(Matrix.OBJECT_NAME) == null ? new DataSet<int, Matrix>(Matrix.OBJECT_NAME, true) :
            (DataSet<int, Matrix>)DataModule.Get(Matrix.OBJECT_NAME);
         dsAgentMatrix = DataModule.Get(AgentMatrix.OBJECT_NAME) == null ? new DataSet<int, AgentMatrix>(AgentMatrix.OBJECT_NAME) :
            (DataSet<int, AgentMatrix>)DataModule.Get(AgentMatrix.OBJECT_NAME);
         dsUserInfo = DataModule.Get(UserInfo.OBJECT_NAME) == null ? new DataSet<string, UserInfo>(UserInfo.OBJECT_NAME) :
            (DataSet<string, UserInfo>)DataModule.Get(UserInfo.OBJECT_NAME);
#if SCRIPT_DOC
         dsCommonScriptDefs = (DataSet<int, ScriptDef>)DataModule.Get(ScriptDef.OBJECT_NAME) ??
            new DataSet<int, ScriptDef>(ScriptDef.OBJECT_NAME);
         dsCommonScriptDefs.Filter = "\"userid\" is null or \"userid\" is not null";
         dsAgentScript = (DataSet<int, AgentScript>)DataModule.Get(AgentScript.OBJECT_NAME) ??
            new DataSet<int, AgentScript>(AgentScript.OBJECT_NAME);
#endif
#if QUESTION
         dsCommonQuest = (DataSet<string, Question>) DataModule.Get(Question.OBJECT_NAME) ??
            new DataSet<string, Question>(Question.OBJECT_NAME);
         dsCommonQuest.Filter = "\"idquest\" is null or \"idquest\" is not null";
         dsAgentQuest = (DataSet<string, AgentQuest>)DataModule.Get(AgentQuest.OBJECT_NAME) ??
            new DataSet<string, AgentQuest>(AgentQuest.OBJECT_NAME);
#endif
      }

      public void MakeArticlesTree()
      {
         tvAccessibleArticles.SuspendLayout();
         try
         {

            FolderTree.MakeTree(tvAccessibleArticles.Nodes, (ICollection<ManagerFolder>)(owner.mainArticleFolder.Count > 0 ?
               owner.mainArticleFolder.Data : dsManagerFolder.Data));
         }
         finally
         {
            tvAccessibleArticles.ResumeLayout();
         }
      }

      private void UpdateDataAfterModifyTree()
      {
         tvAccessibleArticles.SuspendLayout();

         dsManagerFolder.Clear();

         int firstLevel = -1;
         foreach (TreeNode node in tvAccessibleArticles.Nodes)
         {
            firstLevel = UpdateDataAfterModifyTreeLow(node, firstLevel, 0, true);
         }

         tvAccessibleArticles.ResumeLayout();
      }

      private int UpdateDataAfterModifyTreeLow(TreeNode parent, int firstLevel, int levelShift, bool checkRoot)
      {
         if (parent.Checked)
         {
            ManagerFolder folder = parent.Tag as ManagerFolder;

            // проверим поле уровень, чтобы выбранные корневые папки имели одинаковый уровень
            if (checkRoot)
            {
               if (firstLevel < 0)
                  firstLevel = folder.level;
               else
                  levelShift = firstLevel - folder.level;
               checkRoot = false;
            }
            ManagerFolder dest = new ManagerFolder(folder);
            dest.level += levelShift;
            dsManagerFolder.Add(dest.id, dest);
         }

         if (parent.Nodes.Count > 0)
         {
            foreach (TreeNode node in parent.Nodes)
            {
               firstLevel = UpdateDataAfterModifyTreeLow(node, firstLevel, levelShift, checkRoot);
            }
         }

         return firstLevel;
      }

      private void tvAccessibleArticles_AfterCheck(object sender, TreeViewEventArgs e)
      {
         if (canCheckNode)
         {
            canCheckNode = false;
            CheckChildNodes(e.Node, e.Node.Checked);
            UpdateDataAfterModifyTree();
            owner.AddReplacedSet(Agent.id, dsManagerFolder);
            canCheckNode = true;
         }
      }

      //private void dgvOrgs_MouseDown(object sender, MouseEventArgs e)
      //{
      //   DataUtils.beginDragAndDropOnDataGrid<Org>(sender as DataGridView, e);

      //   if (e.Button == MouseButtons.Left && e.Clicks == 2)
      //   {
      //      dgvOrgs_DoubleClick(sender, e);
      //   }
      //}

      private void tvDayTasks_DragEnter(object sender, DragEventArgs e)
      {
         e.Effect = DragDropEffects.Copy | DragDropEffects.Move;
      }

      private void tvDayTasks_DragDrop(object sender, DragEventArgs e)
      {
         TreeNode targetNode = DataUtils.GetNodeFromPoint(tvDayTasks, new Point(e.X, e.Y));
         
         
         if (targetNode != null)
         {
            if(e.Data.GetDataPresent(typeof(Org)))
            {
               addOrgInTree(DataUtils.getTopParent(targetNode), e.Data.GetData(typeof(Org)) as Org); 
            }
         }
      }

      

      private void addOrgInTree(TreeNode targetNode, Org org)
      {
         TreeNode child = new TreeNode(org.ToString());
         child.Tag = org;

         bool contains = false;
         foreach (TreeNode tn in targetNode.Nodes)
         {
            if (tn.Tag.ToString().Equals(child.Tag.ToString()))
            {
               contains = true;
               break;
            }
         }

         if (!contains)
         {
            targetNode.Nodes.Add(child);
            targetNode.Expand();
            targetNode.BackColor = Color.Gold;
            owner.AddReplacedSet(Agent.id, GetOrgFolderDataSet());
         }
      }

      private void tvDayTasks_DragOver(object sender, DragEventArgs e)
      {
         Point pos = tvDayTasks.PointToClient(new Point(e.X, e.Y));
         TreeNode targetNode = tvDayTasks.GetNodeAt(pos);
         if (targetNode != null)
         {
            tvDayTasks.SelectedNode = targetNode;
         }
      }

      //private void dgvOrgs_DoubleClick(object sender, EventArgs e)
      //{
      //   TreeNode tn = DataUtils.getTopParent(tvDayTasks.SelectedNode);
      //   if (tn != null)
      //   {
      //      addOrgInTree(tn, dgvOrgs.CurrentRow.Cells[0].Value as Org);
      //   }
      //}

      private void cmsDayTask_Opening(object sender, CancelEventArgs e)
      {
         TreeNode tn = tvDayTasks.SelectedNode;
         if (tn == null || tn.Level == 0)
         {
            e.Cancel = true;
         }
      }

      private void miDelete_Click(object sender, EventArgs e)
      {
         TreeNode tn = tvDayTasks.SelectedNode;
         TreeNode parent = DataUtils.getTopParent(tn);

         if (tn != null && tn.Level == 1)
         {
            tn.Remove();

            if (parent != null && parent.Nodes.Count == 0)
            {
               parent.BackColor = Color.White;
            }

            owner.AddReplacedSet(Agent.id, GetOrgFolderDataSet());
         }
      }

      public DataSet<int, OrgFolder> GetOrgFolderDataSet()
      {
         DataSet<int, OrgFolder> result = new DataSet<int, OrgFolder>("OrgFolder", false);
         int index = 0;

         foreach (TreeNode nodeDay in tvDayTasks.Nodes)
         {
            OrgFolder orgFolder = new OrgFolder();
            orgFolder.agent = Agent;
            orgFolder.name = nodeDay.Text;
            List<OrgFolderItem> orgFolderItemsList = new List<OrgFolderItem>();
            foreach (TreeNode nodeTasks in nodeDay.Nodes)
            { 
               OrgFolderItem orgFolderItem = new OrgFolderItem();
               orgFolderItem.name = (nodeTasks.Tag as Org).id;
               orgFolderItem.org = nodeTasks.Tag as Org;
               orgFolderItemsList.Add(orgFolderItem);
            }

            if (orgFolderItemsList.Count > 0)
            {
               orgFolder.items = orgFolderItemsList;
               result.Add(index++, orgFolder);
            }
         }

         return result;
      }

      protected DataSet<int, AgentMatrix> GetAgentMatrixDataSet()
      {
         dsAgentMatrix.Clear();
         int index = 0;
         foreach (TreeNode node in tvAgentMatrix.Nodes)
         {
            if (node.Checked)
            { 
               AgentMatrix am = new AgentMatrix();
               am.name = node.Text;
               am.userid = Agent.id;
               dsAgentMatrix.Add(index++, am);
            }
         }

         return dsAgentMatrix;
      }

      protected void tvAgentMatrix_AfterCheck(object sender, TreeViewEventArgs e)
      {
         tvAgentMatrix.BeginUpdate();

         if (e.Node.Level == 0)
         {
            foreach (TreeNode tn in e.Node.Nodes)
            {
               tn.Checked = e.Node.Checked;
            }
         }

         tvAgentMatrix.EndUpdate();

         owner.AddReplacedSet(Agent.id, GetAgentMatrixDataSet());
         //List<IDataSet> wrObj = new List<IDataSet>();
         //wrObj.Add(GetAgentMatrixDataSet());
         //DataModule.UpdateDataSet(wrObj, null, null, DataUtils.GetConnection());

      }

#if SCRIPT_DOC
      private void tvScript_AfterCheck(object sender, TreeViewEventArgs e)
      {
         owner.AddReplacedSet(Agent.id, GetAgentScript());

         if (owner.dsCommonConfig != null)
         {
            string value = "0";
            foreach (TreeNode node in ((TreeView)sender).Nodes)
               if (node.Checked)
               {
                  value = "1";
                  break;
               }

            bool edit = false;

            foreach (CommonConfig c in owner.dsCommonConfig.Data)
            {
               if (c.userid.Equals(Agent.id) && c.key.Equals(ConfigKeyItems.ALLOW_SCRIPTING.Key))
               {
                  c.value = value;
                  edit = true;
               }
            }

            if (!edit)
            {
               CommonConfig cc = new CommonConfig();
               cc.userid = Agent.id;
               cc.value = value;
               cc.key = ConfigKeyItems.ALLOW_SCRIPTING.Key;

               owner.dsCommonConfig.Add(owner.dsCommonConfig.Count, cc);
            }

            owner.AddReplacedSet(Agent.id, owner.dsCommonConfig);
         }
      }

      private DataSet<int, AgentScript> GetAgentScript()
      {
         dsAgentScript.Clear();
         int index = 0;
         foreach (TreeNode node in tvScript.Nodes)
         {
            if (node.Checked)
            {
               AgentScript scr = new AgentScript();
               scr.script = ((ScriptDef)node.Tag).id;
               scr.userid = Agent.id;
               dsAgentScript.Add(index++, scr);
            }
         }

         return dsAgentScript;
      } 
#endif

      protected void tvAgentMatrix_BeforeCheck(object sender, TreeViewCancelEventArgs e)
      {
         if (e.Action == TreeViewAction.ByMouse)
         {
            e.Cancel = e.Node.Level > 0;
         }
      }

      private void tvDayTasks_MouseDown(object sender, MouseEventArgs e)
      {
         if (e.Button == MouseButtons.Right)
         {
            TreeViewHitTestInfo hti = tvDayTasks.HitTest(e.X, e.Y);

            try
            {
               tvDayTasks.SelectedNode = hti.Node;
            }
            catch
            {
            }

         }
      }

      private void tvAccessibleArticles_BeforeCheck(object sender, TreeViewCancelEventArgs e)
      {
         //Если мы в процессе построения дерева, то мы просто выходим
         if (!canCheckNode)
         {
            return;
         }

         //Если нет общего файла Folders, то мы не разрешаем редактировать дерево, 
         //выводим сообщение
         if (owner.mainArticleFolder.Count <= 0)
         {
            const string MAIN_FOLDERS_NOT_PRESENT = "Редактирование \"Доступного товара\" запрещено, потому что нет общей таблицы.";
            Invoke(new EmptyParamHandler(delegate { MessageBox.Show(MAIN_FOLDERS_NOT_PRESENT); }));
            e.Cancel = true;
         }
      }

      private void UserForm_Load(object sender, EventArgs e)
      {
         tvDayTasks.DragDrop -= new System.Windows.Forms.DragEventHandler(tvDayTasks_DragDrop);
         tvDayTasks.MouseDown -= new System.Windows.Forms.MouseEventHandler(tvDayTasks_MouseDown);
         tvDayTasks.DragEnter -= new System.Windows.Forms.DragEventHandler(tvDayTasks_DragEnter);
         tvDayTasks.DragOver -= new System.Windows.Forms.DragEventHandler(tvDayTasks_DragOver);
         tvDayTasks.ContextMenuStrip = null;
         tvDayTasks.Dock = DockStyle.Fill;
         splitContainer1.Panel1Collapsed = true;

         //dgvOrgs.MouseDown -= new System.Windows.Forms.MouseEventHandler(dgvOrgs_MouseDown);
         //dgvOrgs.DoubleClick -= new System.EventHandler(dgvOrgs_DoubleClick);
      }

      private void btnEditRoute_Click(object sender, EventArgs e)
      {
         if(Agent != null)
            Route.Show(Agent);
      }

      bool userChangedText = false;

      private void tbPhone_TextChanged(object sender, EventArgs e)
      {
         if (userChangedText)
         {
            UserInfo userinfo = null;

            if (dsUserInfo.ContainsKey(Agent.id))
            {
               userinfo = dsUserInfo[Agent.id];
            }
            else
            {
               userinfo = new UserInfo();
               userinfo.userid = Agent.id;
               dsUserInfo.Add(userinfo.userid, userinfo);
            }

            userinfo.phone = tbPhone.Text;
            owner.AddReplacedSet(Agent.id, dsUserInfo);
            userChangedText = false;
         }
      }

      private void tbPhone_KeyDown(object sender, KeyEventArgs e)
      {
         userChangedText = true;
      }

#if QUESTION

      private void tvQuest_AfterCheck(object sender, TreeViewEventArgs e)
      {
         dsAgentQuest.Clear();

         foreach (TreeNode node in tvQuest.Nodes)
         {
            if (node.Checked)
            {
               Question question = node.Tag as Question;
               if (question != null)
               {
                  AgentQuest quest = new AgentQuest();
                  quest.idquest = question.idquest;
                  quest.userid = Agent.id;
                  dsAgentQuest.Add(quest.idquest, quest);
               }
            }
         }

         owner.AddReplacedSet(Agent.id, dsAgentQuest);
      }

#endif

   }
}
