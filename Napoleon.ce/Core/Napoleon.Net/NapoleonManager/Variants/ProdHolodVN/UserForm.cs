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
using GRSoft.UILib;

namespace GRSoft.NapoleonManager
{
   public partial class UserForm : UserControl
   {
      public DataSet<string, ManagerFolder> dsManagerFolder, mainFolders;
      private DataSet<string, Org> dsOrg;
      private DataSet<int, OrgFolder> mgrRoute;
      private DataSet<string, Price> dsPrice;
      private DataSet<int, Matrix> dsCommonMatrix;
      private DataSet<int, AgentMatrix> dsAgentMatrix;
      private DataSet<string, UserInfo> dsUserInfo;
      private Agent agent = null;
      private Divisions owner;
      private bool canCheckNode = false;

      private DataSet<string, AllowedDogovor> dsAllowedDogovors;
      private DataSet<string, AgentDogovors> dsDogovors;
      private DataSet<string, FirmConfig> dsFirms;

      public UserForm(Divisions owner)
      {
         InitializeComponent();
         this.owner = owner;
         AdjustForm();
         InitDataSets();
      }

      private void AdjustForm()
      {
         Dock = DockStyle.Fill;
         tvAccessibleArticles.ImageList = owner.images;
         tvAccessibleArticles.Visible = true;
         wbArticlesMessage.Visible = false;
         userDetails.ShowToolTips = true;
         userDetails.TabPages[0].ToolTipText = "перенесите желаемого контрагента в нужный день недели";
         userDetails.TabPages[1].ToolTipText = "оставьте выделенным только нужные для агента группы товаров";

         CustomAdjustForm();
      }

      public Agent Agent
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

         mgrRoute = new DataSet<int, OrgFolder>("OrgFolder", false);
         DataModule.DataProcessed += new EventHandler(DataCurAgentLoaded);

         const string USERID_IN_STR = "userid in ('{0}')";


         string filter = String.Format(USERID_IN_STR, AgentID);

         List<IDataSet> updSets = new List<IDataSet>();

         updSets.Add(dsManagerFolder);
         dsManagerFolder.Filter = filter;

         updSets.Add(mgrRoute);
         mgrRoute.Filter = filter;

         updSets.Add(dsAgentMatrix);
         dsAgentMatrix.Filter = filter;

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
         if (dsOrg.Count == 0)
         {
            updSets.Add(dsOrg);
            dsOrg.Filter = filter;
         }

         AddCustomDataSets(updSets, filter);

         FmWait.ShowForm(owner, DataModule.RefreshGiveSets(conn, updSets, FmWait.ProgressIndicator));
      }

      private void DataCurAgentLoaded(object sender, EventArgs e)
      {
         DataModule.DataProcessed -= new EventHandler(DataCurAgentLoaded);
         FmWait.CloseForm();

         // если нет контрагентов, то берем общих
         if (dsOrg.Count == 0)
            dsOrg = DataModule.Get("Org") as DataSet<string, Org>;
         BeginInvoke(new EmptyParamHandler(ControlsFillAfterLoaded));
      }

      //Установка визуальных компонентов после загрузки из базы
      private void ControlsFillAfterLoaded()
      {
         FillListOrgs();
         FillRoute();
         FillMatrix();

         if (owner.mainArticleFolder.Count <= 0)
         {
            MakeArticlesTreeWithoutMainFolders();
         }

         SetNodeChecking();
         tbPhone.Text = dsUserInfo.ContainsKey(Agent.id) ? dsUserInfo[Agent.id].phone : tbPhone.Text = string.Empty;

         CustomFills();
      }

      //Наполнить tvAccessibleArticles без общего набора из набора агента, или показать 
      //wbArticlesMessage с сообщением об ошибке, если такого набора нет.
      private void MakeArticlesTreeWithoutMainFolders()
      {
         if (dsManagerFolder.Count > 0)
         {
            ArticlesControlVisible(true);
            MakeArticlesTree();
         }
         else
         {
            const string NOT_PRESENT_ARTICLES_DATA_SET = "<html><div align=\"left\"><font color=Gray size=3>" +
               "Невозможно отобразить \"Доступный товар\", потому что нет " +
               "общего файла данных и отсутствует файл выбранного агента.</font></div></html>";
            wbArticlesMessage.DocumentText = NOT_PRESENT_ARTICLES_DATA_SET;
            ArticlesControlVisible(false);
         }
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
         if (mgrRoute != null)
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

                        if (dsOrg.ContainsKey(oi.name))
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

      private void FillListOrgs()
      {
         DataUtils.FillGridFromDS(dgvOrgs, dgvOrgsName, dsOrg);
      }

      private void SetNodeChecking()
      {
         canCheckNode = false;
         tvAccessibleArticles.SuspendLayout();
         SetCheckAllNode(dsManagerFolder.Count == 0);

         foreach (ManagerFolder folder in dsManagerFolder.Data)
         {
            SetNodeStateFromId(folder.id);
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
            CheckChildNodes(n, status);
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
         dsManagerFolder = new DataSet<string, ManagerFolder>("ManagerFolder", false);

         //dsOrg = new DataSet<string, Org>("Org", false);

         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         dsCommonMatrix = DataModule.Get(Matrix.OBJECT_NAME) == null ? new DataSet<int, Matrix>(Matrix.OBJECT_NAME, true) :
            (DataSet<int, Matrix>)DataModule.Get(Matrix.OBJECT_NAME);
         dsAgentMatrix = DataModule.Get(AgentMatrix.OBJECT_NAME) == null ? new DataSet<int, AgentMatrix>(AgentMatrix.OBJECT_NAME) :
            (DataSet<int, AgentMatrix>)DataModule.Get(AgentMatrix.OBJECT_NAME);
         dsUserInfo = DataModule.Get(UserInfo.OBJECT_NAME) == null ? new DataSet<string, UserInfo>(UserInfo.OBJECT_NAME) :
            (DataSet<string, UserInfo>)DataModule.Get(UserInfo.OBJECT_NAME);
      }

      private void AddArticleNode(TreeNode parent, TreeNode node)
      {
         if (parent == null)
         {
            tvAccessibleArticles.Nodes.Add(node);
         }
         else
         {
            parent.Nodes.Add(node);
         }
      }

      public void MakeArticlesTree()
      {
         tvAccessibleArticles.SuspendLayout();
         try
         {
            int lvl = -1;
            TreeNode parent = null;
            TreeNode prevNode = null;

            tvAccessibleArticles.Nodes.Clear();

            IDataSet folders = owner.mainArticleFolder.Count > 0 ?
               owner.mainArticleFolder : dsManagerFolder;

            foreach (ManagerFolder mFolder in folders.Data)
            {
               TreeNode node = new TreeNode(mFolder.name, 0, 0);
               node.Tag = mFolder;

               if (lvl == -1)
               {
                  tvAccessibleArticles.Nodes.Add(node);
               }
               else if (lvl == mFolder.level)
               {
                  AddArticleNode(parent, node);
               }
               else if (lvl < mFolder.level)
               {
                  parent = prevNode;
                  parent.Nodes.Add(node);
               }
               else if (lvl > mFolder.level)
               {
                  TreeNode leftNode = prevNode.Parent;
                  if (leftNode == null)
                  {
                     MessageBox.Show("Некорректный объект Folder", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
                     break;
                  }

                  int reqLvl = mFolder.level;

                  while (reqLvl < (leftNode.Tag as ManagerFolder).level)
                  {
                     leftNode = leftNode.Parent;
                  }

                  parent = leftNode.Parent;
                  AddArticleNode(parent, node);
               }

               prevNode = node;
               lvl = mFolder.level;
            }
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

         foreach (TreeNode node in tvAccessibleArticles.Nodes)
         {
            UpdateDataAfterModifyTreeLow(node);
         }

         tvAccessibleArticles.ResumeLayout();
      }

      private void UpdateDataAfterModifyTreeLow(TreeNode parent)
      {
         if (parent.Checked)
         {
            ManagerFolder folder = parent.Tag as ManagerFolder;
            dsManagerFolder.Add(folder.id, folder);
         }

         if (parent.Nodes.Count > 0)
         {
            foreach (TreeNode node in parent.Nodes)
            {
               UpdateDataAfterModifyTreeLow(node);
            }
         }
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

      private void dgvOrgs_MouseDown(object sender, MouseEventArgs e)
      {
         DataUtils.beginDragAndDropOnDataGrid<Org>(sender as DataGridView, e);

         if (e.Button == MouseButtons.Left && e.Clicks == 2)
         {
            dgvOrgs_DoubleClick(sender, e);
         }
      }

      private void tvDayTasks_DragEnter(object sender, DragEventArgs e)
      {
         e.Effect = DragDropEffects.Copy | DragDropEffects.Move;
      }

      private void tvDayTasks_DragDrop(object sender, DragEventArgs e)
      {
         TreeNode targetNode = DataUtils.GetNodeFromPoint(tvDayTasks, new Point(e.X, e.Y));


         if (targetNode != null)
         {
            if (e.Data.GetDataPresent(typeof(Org)))
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

      private void dgvOrgs_DoubleClick(object sender, EventArgs e)
      {
         TreeNode tn = DataUtils.getTopParent(tvDayTasks.SelectedNode);
         if (tn != null)
         {
            addOrgInTree(tn, dgvOrgs.CurrentRow.Cells[0].Value as Org);
         }
      }

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

      private DataSet<int, AgentMatrix> GetAgentMatrixDataSet()
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

      private void tvAgentMatrix_AfterCheck(object sender, TreeViewEventArgs e)
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

      private void tvAgentMatrix_BeforeCheck(object sender, TreeViewCancelEventArgs e)
      {
         if (e.Action == TreeViewAction.ByMouse)
         {
            e.Cancel = e.Node.Level > 0;
         }
      }

      private void tbPhone_KeyPress(object sender, KeyPressEventArgs e)
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

         userinfo.phone = tbPhone.Text + e.KeyChar;
         owner.AddReplacedSet(Agent.id, dsUserInfo);
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

      //
      // ------------------------- Voshod ----------------------------------------
      //
      private void CustomAdjustForm()
      {
         tvDogovors.AfterCheck += new TreeViewEventHandler(tvDogovors_AfterCheck);
      }

      private IDataSet GetAllowedDogovorsDataSet()
      {
         dsAllowedDogovors.Clear();
         foreach (TreeNode node in tvDogovors.Nodes)
         {
            foreach (TreeNode ch in node.Nodes)
            {
               if (ch.Checked)
               {
                  AllowedDogovor ad = new AllowedDogovor();
                  ad.name = ch.Text;
                  ad.userid = Agent.id;
                  if (dsAllowedDogovors.ContainsKey(ad.name) == false)
                     dsAllowedDogovors.Add(ad.name, ad);
               }
            }
         }

         return dsAllowedDogovors;
      }

      void tvDogovors_AfterCheck(object sender, TreeViewEventArgs e)
      {
         tvDogovors.BeginUpdate();

         if (e.Node.Level == 0)
         {
            foreach (TreeNode tn in e.Node.Nodes)
            {
               tn.Checked = e.Node.Checked;
            }
         }

         tvDogovors.EndUpdate();

         owner.AddReplacedSet(Agent.id, GetAllowedDogovorsDataSet());
      }

      private void AddCustomDataSets(List<IDataSet> updSets, string filter)
      {
         if (dsAllowedDogovors == null)
            dsAllowedDogovors = new DataSet<string, AllowedDogovor>("AllowedDogovors", false);

         dsAllowedDogovors.Filter = filter;
         dsAllowedDogovors.Clear();
         updSets.Add(dsAllowedDogovors);

         if (dsDogovors == null)
         {
            dsDogovors = new DataSet<string, AgentDogovors>("FirmDogovor", false);
            updSets.Add(dsDogovors);
         }

         if (dsFirms == null)
         {
            dsFirms = new DataSet<string, FirmConfig>("FirmsConfig", false);
            updSets.Add(dsFirms);
         }
      }

      private Dictionary<string, string> LoadFirms(DataSet<string, FirmConfig> dsFirms)
      {
         Dictionary<string, string> firms = new Dictionary<string, string>();

         if (dsFirms.Count > 0)
         {
            foreach (FirmConfig fc in dsFirms.Data)
            {
               string[] fv = fc.value.Split(new char[] {';'});
               foreach (string v in fv)
               {
                  string[] items = v.Split(new char[] { '\t' });
                  firms[items[1]] = items[0];
               }
               break;
            }
         }

         return firms;
      }

      private void CustomFills()
      {
         Dictionary<string, string> firms = LoadFirms(dsFirms);

         tvDogovors.BeginUpdate();
         TreeNodeCollection tnc = tvDogovors.Nodes;
         tnc.Clear();

         foreach (AgentDogovors dog in dsDogovors.Data)
         {
            if (firms.ContainsKey(dog.firm) == false)
               continue;

            TreeNode node = new TreeNode(firms[dog.firm]);

            bool haveUncheck = false;
            foreach (AgentDogovorItem item in dog.items)
            {
               TreeNode chNode = new TreeNode(item.id);

               if (dsAllowedDogovors.ContainsKey(item.id) == false)
                  haveUncheck = true;
               else
                  chNode.Checked = true;
               node.Nodes.Add(chNode);
            }
            if (!haveUncheck)
               node.Checked = true;

            tnc.Add(node);
         }
         tvDogovors.Sort();
         tvDogovors.EndUpdate();
      }
   }

   class FirmConfig : GRSoft.Network.DataObject
   {
      [KeyField]
      public string key = "";
      public string value = "";
   }

   class AgentDogovorItem : GRSoft.Network.DataObject
   {
      public string id = "";
   }

   class AgentDogovors : GRSoft.Network.DataObject
   {
      [KeyField]
      public string firm = "";

      [ItemType(typeof(AgentDogovorItem))]
      public List<AgentDogovorItem> items = new List<AgentDogovorItem>();
   }

   class AllowedDogovor : GRSoft.Network.DataObject
   {
      [KeyField]
      public string name = "";
      public string userid = "";
   }
}
