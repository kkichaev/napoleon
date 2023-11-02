/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма подразделений
 * 
 * ert   03/05/2010   creating
 */

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Globalization;
using System.Collections;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   public partial class Divisions : Form
   {
      public ToolStripButton btnQuestion = new System.Windows.Forms.ToolStripButton();
      public ToolStripButton btnPriceMonitoring = new ToolStripButton();
      public ToolStripButton btnStopList = new ToolStripButton();

      DivisionList divisions = DivisionList.GetDataSet();

      protected UserForm userForm;
      DivisionForm divisionForm = FormEntries.OpenDivisionForm();

      DivisionList changedDivisons = new DivisionList(false);
      DivisionList removedDivisons = new DivisionList(false);
      Agents changedAgents = new Agents(false);
      public DataSet<string, ManagerFolder> mainArticleFolder;

      private DataSet<string, Org> dsOrg; // общие организации (файл ORGS) - может потребоваться в других местах

      public DataSet<string, OrderAddConfig> dsConfig = new DataSet<string, OrderAddConfig>(OrderAddConfig.OBJECT_NAME, false);
      public DataSet<int, CommonConfig> dsCommonConfig = null;

      List<ReplacedSet> replaced = new List<ReplacedSet>();
      List<IDataSet> setToWrite = new List<IDataSet>();
      List<IDataSet> setToDel = new List<IDataSet>();

      Dictionary<string, string> tracking = new Dictionary<string, string>();
      Dictionary<string, string> disposition = new Dictionary<string, string>();
      Dictionary<string, string> orgRadius = new Dictionary<string, string>();

      TreeNode unusedUsers = null;
      //TreeNode managersNode = null;

      object canRemove = null;

      bool changed = false;
      string baseTitle = "";
      private ToolTip ttDivision = new ToolTip();
      protected SysColors colors = new SysColors();
      bool clearing = false;

      public void __Initing()
      {

         this.tvDivisions.ItemDrag += new System.Windows.Forms.ItemDragEventHandler(this.tvDivisions_ItemDrag);
         this.tvDivisions.AfterSelect += new System.Windows.Forms.TreeViewEventHandler(this.tvDivisons_AfterSelect);
         this.tvDivisions.Enter += new System.EventHandler(this.tvDivisions_Enter);
         this.tvDivisions.Leave += new System.EventHandler(this.tvDivisions_Leave);
         this.tvDivisions.MouseMove += new System.Windows.Forms.MouseEventHandler(this.tvDivisions_MouseMove);
         this.tbFind.TextChanged += new System.EventHandler(this.tbFind_TextChanged);
         this.btnFindClear.Click += new System.EventHandler(this.btnFindClear_Click);
         this.newButton.DropDownOpening += new System.EventHandler(this.newButton_DropDownOpening);
         this.miAddDivision.Click += new System.EventHandler(this.miAddDivision_Click);
         this.miAddAgent.Click += new System.EventHandler(this.miAddAgent_Click);
         this.delButton.Click += new System.EventHandler(this.delButton_Click);
         this.saveButton.Click += new System.EventHandler(this.saveButton_Click);
         this.tsbMatrixDesigner.Click += new System.EventHandler(this.tsbMatrixDesigner_Click);
         this.orgsSetColor.Click += new System.EventHandler(this.orgsSetColor_Click);
         this.priceSetColor.Click += new System.EventHandler(this.priceSetColor_Click);
         this.tsEditColor.Click += new System.EventHandler(this.tsEditColor_Click);
         this.tbCoef.Click += new System.EventHandler(this.toolStripLabel1_Click);
         this.btnScriptDesigner.Click += new System.EventHandler(this.btnScriptDesigner_Click);
         this.tsbOrgRadiusDocs.Click += new System.EventHandler(this.tsbOrgRadiusDocs_Click);
         this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.Divisions_FormClosing);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.Divisions_FormClosed);
         this.Load += new System.EventHandler(this.Divisions_Load);

         tbSep1.Visible = false;
         tbSep2.Visible = false;
         tbCoef.Visible = false;

#if FOCUSED_GROUP
         ToolStripMenuItem fgtsb = new ToolStripMenuItem();
         fgtsb.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         fgtsb.Name = "fgtsbFocusedGroup";
         fgtsb.Size = new System.Drawing.Size(101, 22);
         fgtsb.Text = "Группы";
         fgtsb.Click += new System.EventHandler((obj, arg) => {
            new FocusedGroupEditor().Show();
         });

         tb.Items.Add(fgtsb);
#endif

#if FOCUSED_ITEMS
         ToolStripMenuItem fitsb = new ToolStripMenuItem();
         fitsb.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         fitsb.Name = "fitsbFocusedGroup";
         fitsb.Size = new System.Drawing.Size(101, 22);
         fitsb.Text = "Товары";
         fitsb.Click += new System.EventHandler((obj, arg) =>
         {
            new FocusedItemsEditor().Show();
         });

         tb.Items.Add(fitsb);
#endif

#if FOCUSED_GROUP || FOCUSED_ITEMS
         ToolStripSplitButton focusMenu = new ToolStripSplitButton();
         focusMenu.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         focusMenu.Name = "fgtsbFocusedGroup";
         focusMenu.Size = new System.Drawing.Size(101, 22);
         focusMenu.Text = "Фокусный ассортимент";
         focusMenu.ImageTransparentColor = System.Drawing.Color.Magenta;
#if FOCUSED_GROUP
         focusMenu.DropDownItems.Add(fgtsb);
#endif
#if FOCUSED_ITEMS
         focusMenu.DropDownItems.Add(fitsb);
#endif
         tb.Items.Add(focusMenu);
#endif

#if ORG_DISPOSITION
         tsbOrgRadiusDocs.Visible = true;
#endif

#if QUESTION

         btnQuestion.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         btnQuestion.Name = "btnQuestion";
         btnQuestion.Size = new System.Drawing.Size(101, 22);
         btnQuestion.Text = "Анкеты";
         btnQuestion.Click += new System.EventHandler((obj, arg) =>
         {
            new FmQuestionary().Show();
         });

         ToolStripSeparator sp = new ToolStripSeparator();
         tb.Items.Add(sp);
         tb.Items.Add(btnQuestion);
#endif

#if MANAGER_QUEST
         ToolStripButton btnManagerQuestion = new System.Windows.Forms.ToolStripButton();
         btnManagerQuestion.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         btnManagerQuestion.Name = "btnManagerQuestion";
         btnManagerQuestion.Size = new System.Drawing.Size(101, 22);
         btnManagerQuestion.Text = "Анкеты менеджера";
         btnManagerQuestion.Click += new System.EventHandler((obj, arg) =>
         {
            new FmManagerQuestionary().Show();
         });

         tb.Items.Add(btnManagerQuestion);
#endif
         tbSep1.Visible = true;
         btnScriptDesigner.Visible = true;

         IDecorator d = DecoratorFactory.GetDecorator(this);
         if (d != null)
            d.AdjustForm();

         divisionForm.SetParent(this);
         userForm = FormEntries.OpenUserForm(this);
         baseTitle = this.Text;
         divisionForm.Changed += new DivisionChangedHandler(divisionForm_Changed);

         dsOrg = DataModule.Get(Org.COMMON_OBJECT_NAME) as DataSet<string, Org>;
         if (dsOrg == null)
            dsOrg = new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);

         //GetData();
         DataModule.OnDataResponceError += new EventDataResponseError(DataError);
      }

      public ToolStrip GetToolStrip()
      {
         return tb;
      }

      delegate void StatusTextHandler(string msg);
      void StatusText(string msg)
      {
         connectStatus.Text = msg;
      }

      void DataError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm(true);

         MessageBox.Show(e.Msg, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
         BeginInvoke(new StatusTextHandler(StatusText), new object[] { "" });
      }

      void divisionForm_Changed(object o, Division d)
      {
         AddChanged(d);
      }

      void GetData()
      {
         if (!MainForm.Instance.CheckIsMainDataPresents(true))
         {
            return;
         }

         Config c = Config.GetConfig();
         DBConnection conn = c.GetConnection();

         Agents agents = DataModule.Get("Agents") as Agents;
         if (agents == null)
            agents = new Agents();

         bool refreshReference = (agents.Count == 0);

#if FOLDER_CONSTRUCTOR
         string folderName = ManagerFolder.COMMON_FOLDERS_NAME;
#else
         string folderName = ManagerFolder.OBJECT_NAME;
#endif

         mainArticleFolder = DataModule.Get(folderName) as DataSet<string, ManagerFolder>;

         if (mainArticleFolder == null)
         {
            mainArticleFolder = new DataSet<string, ManagerFolder>(folderName);
         }
         mainArticleFolder.Filter = DataUtils.USERID_IS_NULL_STR;
         DataModule.DataProcessed += new EventHandler(DataLoaded);
         List<IDataSet> updSets = new List<IDataSet>();

         if (refreshReference)
            updSets.Add(agents);
         CurrentUser.InitCurrentUser(updSets);

         dsCommonConfig = DataModule.Get(CommonConfig.OBJECT_NAME) as DataSet<int, CommonConfig>;
         if (dsCommonConfig == null)
            dsCommonConfig = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME);
         dsCommonConfig.Filter = "not (\"userid\" is null)";

         updSets.Add(mainArticleFolder);
         updSets.Add(divisions);
         updSets.Add(dsConfig);
         updSets.Add(dsCommonConfig);

         if (dsOrg.Count == 0)
         {
            //dsOrg.Filter = DataUtils.USERID_IS_NULL_STR;
            //updSets.Add(dsOrg);
            //Никто не понял нафига тут они берутся, в СПК висит!!! 2018.10.17 kki

         }

         BeforeUpdate(updSets);
         FmWait.ShowForm(this, DataModule.RefreshGiveSets(conn, updSets, FmWait.ProgressIndicator));
         //connectStatus.Text = "Получение данных...";
      }

      protected virtual void BeforeUpdate(List<IDataSet> updSets)
      {
         divisionForm.BeforeUpdate(updSets);
      }

      public void MarkChanged()
      {
         Text = baseTitle + " *";
         changed = true;
         saveButton.Enabled = true;
      }

      internal void AddReplacedSet(string userID, IDataSet set)
      {
         bool finded = false;
         foreach (ReplacedSet rs in replaced)
         {
            if (rs.userID == userID)
            {
               if (set == rs.data)
               {
                  finded = true;
                  break;
               }
               else if (set.GetType() == rs.data.GetType()) // был такой же набор, заменим его
               {
                  finded = true;
                  rs.data = set;
               }
            }
         }

         if (!finded)
            replaced.Add(new ReplacedSet(userID, set));

         MarkChanged();
      }

      internal void AddWriteSet(IDataSet set)
      {
         if (!setToWrite.Contains(set))
            setToWrite.Add(set);

         MarkChanged();
      }

      internal void AddRemovedSet(IDataSet set)
      {
         if (!setToDel.Contains(set))
            setToDel.Add(set);

         MarkChanged();
      }

      internal object CanRemove
      {
         get { return canRemove; }
         set
         {
            canRemove = value;
            if (canRemove == null)
            {
               delButton.Enabled = false;
               delButton.Text = "Удалить";
            }
            else
            {
               delButton.Enabled = true;
               delButton.Text = "Удалить '" + value.ToString() + "'";
            }
         }
      }

      void AddRemoved(Division d)
      {
         if (changedDivisons.ContainsKey(d.id))
            changedDivisons.Remove(d.id);

         if (removedDivisons.ContainsKey(d.id) == false)
            removedDivisons.Add(d.id, d);

         MarkChanged();
      }

      void AddChanged(Division d)
      {
         if (removedDivisons.ContainsKey(d.id))
            removedDivisons.Remove(d.id);

         if (changedDivisons.ContainsKey(d.id) == false)
            changedDivisons.Add(d.id, d);

         RefreshNode(d);
         MarkChanged();
      }

      void AddChanged(Agent a)
      {
         if (changedAgents.ContainsKey(a.id) == false)
            changedAgents.Add(a.id, a);

         RefreshNode(a);
         MarkChanged();
      }

      void RefreshNode(Division d)
      {
         tvDivisions.SuspendLayout();

         TreeNode n = FindNode(d, tvDivisions.Nodes);
         if (n != null)
         {
            List<Division> ld = new List<Division>();
            List<Agent> la = new List<Agent>();

            foreach (Division dc in d.Childs)
            {
               ld.Add(dc);
            }

            int i = 0;
            for (; i < d.agents.Count;)
            {
               Division.DivisionAgent da = d.agents[i];
               if (da.agent != null)
               {
                  la.Add(da.agent);
                  i++;
               }
               else
                  d.agents.Remove(da);
            }

            //foreach (Division.DivisionAgent da in d.agents)
            //{
            //   if (da.agent != null)
            //      la.Add(da.agent);
            //}
            n.Text = d.ToString();
            Refresh(n, ld, la);

            if (unusedUsers != null)
            {
               List<Agent> unused = divisions.UnusedAgents();
               Refresh(unusedUsers, null, unused);
            }
         }

         tvDivisions.ResumeLayout();
      }

      void RefreshNode(Agent a)
      {
         TreeNode n = FindNode(a, tvDivisions.Nodes);
         if (n != null)
            n.Text = a.ToString();
      }

      TreeNode FindNode(object tag, TreeNodeCollection nodes)
      {
         foreach (TreeNode node in nodes)
         {
            if (node.Tag == tag)
               return node;

            TreeNode n = FindNode(tag, node.Nodes);
            if (n != null)
               return n;
         }
         return null;
      }

      void Refresh(TreeNode node, List<Division> divisions, List<Agent> users)
      {
         Dictionary<Division, TreeNode> dn = new Dictionary<Division, TreeNode>();
         Dictionary<Agent, TreeNode> an = new Dictionary<Agent, TreeNode>();

         TreeNode firstAgent = null;
         TreeNodeCollection nc = node.Nodes;

         foreach (TreeNode n in nc)
         {
            if (n.Tag is Agent)
            {
               if (firstAgent == null)
                  firstAgent = n;
               an[n.Tag as Agent] = n;
            }
            if (n.Tag is Division)
               dn[n.Tag as Division] = n;
         }

         if (divisions != null)
         {
            foreach (Division d in divisions)
            {
               if (dn.ContainsKey(d))
                  dn.Remove(d);
               else
               {
                  TreeNode newNode = NewNode(d);
                  if (firstAgent == null) nc.Add(newNode);
                  else nc.Insert(nc.IndexOf(firstAgent), newNode);
               }
            }

            foreach (KeyValuePair<Division, TreeNode> kvn in dn)
               nc.Remove(kvn.Value);
         }

         if (users != null)
         {
            foreach (Agent a in users)
            {
               if (an.ContainsKey(a)) an.Remove(a);
               else
               {
                  TreeNode newNode = NewNode(a);

                  if (tbFind.Text.Trim().Length == 0 || a.FullName().ToUpper().Contains(tbFind.Text.ToUpper()))
                     nc.Add(newNode);
               }
            }

            foreach (KeyValuePair<Agent, TreeNode> avn in an)
               nc.Remove(avn.Value);
         }
      }

      protected virtual void CheckData()
      {
         connectStatus.Text = "";
         Division selected = null;

         CurrentUser.SetCurrentUser(false);

         if (divisions.Count == 0)
         {
            if (MessageBox.Show("Нет ни одного подразделения. Создать автоматически?", "Вопрос",
               MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
            {
               Division d = new Division();
               d.parent = 0;
               d.name = "Основное";
               d.description = "Автоматически созданное подразделение.";
               d.id = divisions.NextID();

               List<Agent> unused = divisions.UnusedAgents();

               d.agents = new List<Division.DivisionAgent>();
               foreach (Agent a in unused)
               {
                  Division.DivisionAgent da = new Division.DivisionAgent();
                  da.agent = a;
                  da.id = a.id;
                  d.agents.Add(da);
               }

               divisions.Add(d.id, d);
               AddChanged(d);
               selected = d;
            }
         }

         if (mainArticleFolder.Count > 0)
         {
            userForm.MakeArticlesTree();

            foreach (TreeNode node in userForm.tvAccessibleArticles.Nodes)
            {
               TreeNode n = (TreeNode)node.Clone();
               divisionForm.tvAccessibleArticles.Nodes.Add(n);
            }
         }

         RefreshDivisionTree();

         if (selected != null)
            SelectDivision(selected, tvDivisions.Nodes);

         divisionForm.DataLoaded();
      }

      private bool SelectDivision(Division selected, TreeNodeCollection nodes)
      {
         bool ret = false;
         foreach (TreeNode node in nodes)
         {
            if (node.Tag == selected)
            {
               tvDivisions.SelectedNode = node;
               ret = true;
               break;
            }

            if (SelectDivision(selected, node.Nodes))
            {
               ret = true;
               break;
            }
         }

         return ret;
      }

      TreeNode NewNode(Division d)
      {
         TreeNode node = new TreeNode(d.ToString(), 0, 1);
         node.Tag = d;
         return node;
      }

      TreeNode NewNode(Agent a)
      {
         TreeNode node = new TreeNode(a.ToString(), 2, 2);
         node.Tag = a;
         return node;
      }

      private void AddChildNodes(TreeNode parent, Division division)
      {
         foreach (Division child in division.Childs)
         {
            TreeNode node = NewNode(child);
            AddChildNodes(node, child);
            parent.Nodes.Add(node);
         }
         foreach (Division.DivisionAgent da in division.agents)
         {
            if (da.agent != null &&
               (tbFind.Text.Trim().Length == 0 ||
                  da.agent.FullName().ToUpper().Contains(tbFind.Text.Trim().ToUpper())))
            {
               TreeNode un = NewNode(da.agent);
               parent.Nodes.Add(un);
            }
         }
      }

      private void RefreshDivisionTree()
      {
         tvDivisions.Nodes.Clear();
         Division root = divisions.Root;

         if (root != null)
         {
            TreeNode rootNode = new TreeNode(root.name, 0, 1);
            rootNode.Tag = root;
            AddChildNodes(rootNode, root);

            tvDivisions.Nodes.Add(rootNode);
            tvDivisions.SelectedNode = rootNode;
         }

         //managersNode = new TreeNode("Руководители отделов", 3, 3);
         //Refresh(managersNode, null, divisions.Managers());
         //tvDivisions.Nodes.Add(managersNode);

         unusedUsers = new TreeNode("Свободные агенты", 3, 3);
         Refresh(unusedUsers, null, divisions.UnusedAgents());
         tvDivisions.Nodes.Add(unusedUsers);
      }

      private void SaveChanges()
      {
         divisionForm.CheckChanges();

         List<IDataSet> wrObj = new List<IDataSet>();
         List<IDataSet> rmvObj = new List<IDataSet>();

         if (setToWrite.Count > 0)
         {
            wrObj.AddRange(setToWrite);
            setToWrite.Clear();
         }

         if (changedDivisons.Count > 0) wrObj.Add(changedDivisons);
         if (changedAgents.Count > 0) wrObj.Add(changedAgents);
         if (removedDivisons.Count > 0) rmvObj.Add(removedDivisons);
         if (setToDel.Count > 0)
         {
            rmvObj.AddRange(setToDel);
            setToDel.Clear();
         }

         DataSet<int, CommonConfig> cfg = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);

         if (tracking.Count > 0)
         {
            foreach (KeyValuePair<string, string> kv in tracking)
            {
               CommonConfig cc = new CommonConfig();
               cc.key = "Tracking";
               cc.userid = kv.Key;
               cc.value = kv.Value;
               cfg[cfg.Count] = cc;
            }

         }

         if (disposition.Count > 0)
         {
            DataSet<int, CommonConfig> addCfg = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);
            foreach (KeyValuePair<string, string> kv in disposition)
            {
               CommonConfig cc = new CommonConfig();
               cc.key = "Disposition";
               cc.userid = kv.Key;
               cc.value = kv.Value;
               cfg[cfg.Count] = cc;
            }
         }

         if (orgRadius.Count > 0)
         {
            DataSet<int, CommonConfig> addCfg = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);
            foreach (KeyValuePair<string, string> kv in orgRadius)
            {
               CommonConfig cc = new CommonConfig();
               cc.key = "OrgRadius";
               cc.userid = kv.Key;
               cc.value = kv.Value;
               cfg[cfg.Count] = cc;
            }
         }

         if (cfg.Count > 0)
            wrObj.Add(cfg);

         Config c = Config.GetConfig();
         DBConnection conn = c.GetConnection();

         connectStatus.Text = "Сохранение...";

         bool done = divisionForm.BeforeWriteChanges(wrObj, rmvObj, replaced, conn);

         if (done && (wrObj.Count > 0 || rmvObj.Count > 0 || replaced.Count > 0))
            done = DataModule.UpdateDataSet(wrObj, rmvObj, replaced, conn);
         if (!done)
         {
            MessageBox.Show("Ошибка при записи в базу данных.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            return;
         }

         divisionForm.AfterWrited();

         ResetChangeStatus();

         changedAgents.Clear();
         changedDivisons.Clear();
         removedDivisons.Clear();
         replaced.Clear();
      }

      private void ResetChangeStatus()
      {
         saveButton.Enabled = false;
         changed = false;
         connectStatus.Text = "";
         Text = baseTitle;
      }

      void DataLoaded(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         BeginInvoke(new EmptyParamHandler(CheckData));
      }

      internal Agent SelectedAgent
      {
         set
         {
            TreeNode tn = FindNode(value, tvDivisions.Nodes);
            if (tn != null)
               tvDivisions.SelectedNode = tn;
         }

         get
         {
            TreeNode tn = tvDivisions.SelectedNode;
            if (tn != null)
               return tn.Tag as Agent;

            return null;
         }
      }

      internal ICollection Folders
      {
         get
         {
            List<ManagerFolder> result = null;

            Division d = SelectedDivision;

            if (d != null)
            {

               while (d.folder.Count == 0 && d.parent != 0)
               {
                  if (divisions.ContainsKey(d.parent))
                     d = divisions[d.parent];
                  else
                     break;
               }

               result = d.folder;
            }

            return result;
         }
      }

      internal Division SelectedDivision
      {
         get
         {
            Division selected = null;

            TreeNode tn = tvDivisions.SelectedNode;
            if (tn != null)
            {
               if (tn.Tag is Division)
                  selected = tn.Tag as Division;
               else if (tn.Tag is Agent)
               {
                  tn = tn.Parent;
                  if (tn != null && tn.Tag is Division)
                     selected = tn.Tag as Division;
               }
            }

            return selected;
         }

         set
         {
            TreeNode tn = FindNode(value, tvDivisions.Nodes);
            if (tn != null)
               tvDivisions.SelectedNode = tn;
         }
      }

      private void tvDivisons_AfterSelect(object sender, TreeViewEventArgs e)
      {
         //if (changed)
         //{
         //   DialogResult dr = AskToSaveChanges();

         //   if (dr == DialogResult.Yes)
         //      SaveChanges();
         //   else
         //      ResetChangeStatus();

         //}
         Control.ControlCollection cc = splitContainer1.Panel2.Controls;
         if (e.Node.Tag is Division)
         {
            divisionForm.Division = e.Node.Tag as Division;
            if (cc.Count == 0 || cc[0] != divisionForm)
            {
               cc.Clear();
               cc.Add(divisionForm);
            }
         }
         else if (e.Node.Tag is Agent)
         {
            if (cc.Count == 0 || cc[0] != userForm)
            {
               cc.Clear();
               cc.Add(userForm);
            }
            userForm.Agent = e.Node.Tag as Agent;
         }
         else
         {
            cc.Clear();
         }

         CanRemove = (e.Node.Parent == unusedUsers) ? null : e.Node.Tag;
      }

      private void Divisions_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (changed)
         {
            DialogResult dr = AskToSaveChanges();

            if (dr == DialogResult.Cancel)
            {
               e.Cancel = true;
               return;
            }

            if (dr == DialogResult.Yes)
               SaveChanges();
         }
      }

      public static DialogResult AskToSaveChanges()
      {
         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         return dr;
      }

      private void saveButton_Click(object sender, EventArgs e)
      {
         SaveChanges();
      }

      private void Divisions_FormClosed(object sender, FormClosedEventArgs e)
      {
         DataModule.Remove(divisions);
         DataModule.OnDataResponceError -= new EventDataResponseError(DataError);
      }

      private void newButton_DropDownOpening(object sender, EventArgs e)
      {
         Division selected = SelectedDivision;

         if (selected != null)
         {
            miAddAgent.Text = "Добавить агента в '" + selected.ToString() + "'";
            miAddDivision.Text = "Добавить подразделение в '" + selected.ToString() + "'";

            miAddAgent.Enabled = true;
            miAddDivision.Enabled = true;
         }
         else
         {
            miAddAgent.Text = "Добавить агента";
            miAddDivision.Text = "Добавить подразделение";

            miAddAgent.Enabled = false;
            miAddDivision.Enabled = false;
         }
      }

      private void miAddAgent_Click(object sender, EventArgs e)
      {
         Division s = SelectedDivision;
         if (s != null)
         {
            SelectAgents sa = new SelectAgents();
            sa.MultiSelect = true;
            sa.SetAgents(divisions.UnusedAgents());

            if (sa.ShowDialog() == DialogResult.OK)
            {
               divisionForm.AddAgents(sa.SelectedAgents, false);
            }
         }
      }

      private void miAddDivision_Click(object sender, EventArgs e)
      {
         Division s = SelectedDivision;
         if (s != null)
         {
            AckName an = new AckName();
            if (an.ShowDialog() == DialogResult.OK && an.EnteredName.Length > 0)
            {
               Division d = new Division();
               d.name = an.EnteredName;
               d.parent = s.id;
               d.id = divisions.NextID();

               s.Childs.Add(d);
               divisions.Add(d.id, d);

               if (s == divisionForm.Division)
                  divisionForm.RefreshDataSets();

               AddChanged(d);
               // не понятно зачем сохраняли парента. Вроде ничего в нем не меняется.
               //AddChanged(s);
               RefreshNode(s);
            }
         }
      }

      private void tvDivisions_ItemDrag(object sender, ItemDragEventArgs e)
      {
         if (e.Button == MouseButtons.Left)
         {
            TreeNode tn = e.Item as TreeNode;
#if ClassicSpb
            if (tn.Tag is Agent)
               DoDragDrop(new System.Windows.Forms.DataObject("Agent", tn.Tag), DragDropEffects.Move);
#else
            if (tn.Tag is Agent && tn.Parent == unusedUsers)
               DoDragDrop(new System.Windows.Forms.DataObject("Agent", tn.Tag), DragDropEffects.Move | DragDropEffects.Copy);
#endif
         }
      }

      private void tvDivisions_Leave(object sender, EventArgs e)
      {
         CanRemove = null;
      }

      private void tvDivisions_Enter(object sender, EventArgs e)
      {
         TreeNode n = tvDivisions.SelectedNode;
         if (n != null)
            CanRemove = n.Tag;
      }


      private void delButton_Click(object sender, EventArgs e)
      {
         if (MessageBox.Show(delButton.Text + "?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
         {
            if (canRemove is Agent)
            {
               Division d = SelectedDivision;
               if (d != null)
               {
                  Dictionary<Agent, bool> r = new Dictionary<Agent, bool>();
                  r.Add(canRemove as Agent, true);
                  if (d.Remove(r))
                  {
                     divisionForm.RefreshDataSets();
                     AddChanged(d);
                  }
               }
            }
            else if (canRemove is Division)
            {
               Division parent = null;
               Division rd = canRemove as Division;
               if (rd.parent != 0)
                  parent = divisions[rd.parent];

               List<Division> removed = divisions.RemoveTree(rd);
               foreach (Division rdi in removed)
                  AddRemoved(rdi);

               if (parent != null)
               {
                  changed = false;
                  parent.Remove(rd);
                  AddChanged(parent);

                  SelectedDivision = parent;
               }
            }
            RefreshDivisionTree();
         }
      }

      private void tvDivisions_MouseMove(object sender, MouseEventArgs e)
      {
         TreeNode node = tvDivisions.GetNodeAt(e.X, e.Y);

         if (node != null && node.Parent != null)
         {
            if (node.Parent.Text.Equals("Руководители отделов"))
            {
               if (ttDivision.GetToolTip(tvDivisions) == string.Empty)
               {
                  ttDivision.SetToolTip(tvDivisions, "руководитель не может быть агентом");
               }
            }
            else if (node.Parent.Text.Equals("Свободные агенты"))
            {
               if (ttDivision.GetToolTip(tvDivisions) == string.Empty)
               {
                  ttDivision.SetToolTip(tvDivisions, "чтобы зачислить в подразделение откройте нужное подразделение и перенесите в таблицу подчинённые");
               }
            }
            else
            {
               ttDivision.SetToolTip(tvDivisions, string.Empty);
            }
         }
         else
         {
            ttDivision.SetToolTip(tvDivisions, string.Empty);
         }
      }

      //Редактор матриц
      private void tsbMatrixDesigner_Click(object sender, EventArgs e)
      {
         Type prcType = FormEntries.GetFormType(typeof(FmMatrixDesigner));
         ConstructorInfo ci = prcType.GetConstructor(Type.EmptyTypes);
         Form fm = (Form)ci.Invoke(new object[] { });
         fm.Show();
      }

      private void Divisions_Load(object sender, EventArgs e)
      {
         GetData();
      }

      private void toolStripLabel1_Click(object sender, EventArgs e)
      {
         FmAutoCoef ac = new FmAutoCoef();
         double cv = 1.5;
         const string OFF_TAKE_COEFF_STR = "OffTakeCoef";

         if (dsConfig.Count > 0 &&
            dsConfig.ContainsKey(OFF_TAKE_COEFF_STR))
         {
            OrderAddConfig c = dsConfig[OFF_TAKE_COEFF_STR];
            if (c != null)
               cv = Double.Parse(c.value) / 100;
         }
         ac.Coef = cv;
         if (ac.ShowDialog() == DialogResult.OK)
         {
            OrderAddConfig c = new OrderAddConfig();
            int iv = (int)(ac.Coef * 100 + 0.5);
            c.value = iv.ToString();
            c.key = OFF_TAKE_COEFF_STR;
            dsConfig[OFF_TAKE_COEFF_STR] = c;

            List<IDataSet> wrObj = new List<IDataSet>();
            wrObj.Add(dsConfig);
            Config config = Config.GetConfig();
            DBConnection conn = config.GetConnection();

            connectStatus.Text = "Сохранение...";
            DataModule.UpdateDataSet(wrObj, null, null, conn);
         }
      }

      private void priceSetColor_Click(object sender, EventArgs e)
      {
         colors.Load(dsCommonConfig);

         Type type = FormEntries.GetFormType(typeof(FmSetPriceColor));
         ConstructorInfo ci = type.GetConstructor(BindingFlags.Instance | BindingFlags.NonPublic | BindingFlags.Public, null, new Type[] { typeof(SysColors) }, null);
         Form fm = (Form)ci.Invoke(new object[] { colors });
         fm.Show();
      }

      private void orgsSetColor_Click(object sender, EventArgs e)
      {
         colors.Load(dsCommonConfig);
         FmSetOrgColor oclr = new FmSetOrgColor(colors);
         oclr.Show();
      }

      internal void SetTracking(Agent agent, string value)
      {
         tracking[agent.id] = value;
         MarkChanged();
      }

      internal string GetTracking(string aid)
      {
         if (tracking.ContainsKey(aid))
            return tracking[aid];

         return null;
      }

      private void tsEditColor_Click(object sender, EventArgs e)
      {
         FmColorEditor form = new FmColorEditor();
         colors.Load(dsCommonConfig);
         form.Colors = colors;
         if (form.ShowDialog() == DialogResult.OK)
         {
            colors = form.Colors;
            CommonConfig ocfg = colors.ToConfig();

            bool added = false;

            foreach (CommonConfig cc in dsCommonConfig.Values)
            {
               if (cc.key.Equals(SysColors.ConfigKey))
               {
                  added = true;
                  cc.value = ocfg.value;
               }
            }

            if (!added)
               dsCommonConfig.Add(dsCommonConfig.Count, ocfg);

            DataSet<int, CommonConfig> uc = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);
            List<IDataSet> update = new List<IDataSet>();
            uc.Add(0, ocfg);
            update.Add(uc);

            Config cfg = Config.GetConfig();
            DataModule.WriteDataSet(update, cfg.GetConnection());
         }
      }

      private void btnScriptDesigner_Click(object sender, EventArgs e)
      {
         Type ft = FormEntries.GetFormType(typeof(FmScriptDesigner));
         ConstructorInfo ci = ft.GetConstructor(Type.EmptyTypes);
         Form fm = (Form)ci.Invoke(new object[] { });
         fm.Show();
      }

      private void tbFind_TextChanged(object sender, EventArgs e)
      {
         timer1.Stop();

         if (tbFind.Text.Length > 0)
            timer1.Start();
         else if (!clearing)
            btnFindClear_Click(this, EventArgs.Empty);

      }

      private void btnFindClear_Click(object sender, EventArgs e)
      {
         clearing = true;
         tbFind.Clear();
         DoSearch("");
         clearing = false;
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         timer1.Stop();
         DoSearch(tbFind.Text);
      }

      private void DoSearch(string srch)
      {
         RefreshDivisionTree();
         tvDivisions.ExpandAll();
      }

      internal string GetDisposition(string userid)
      {
         if (disposition.ContainsKey(userid))
            return disposition[userid];

         return null;
      }

      internal void SetDisposition(Agent agent, string value)
      {
         disposition[agent.id] = value;
         MarkChanged();
      }

      internal string GetOrgRadius(string userid)
      {
         if (orgRadius.ContainsKey(userid))
            return orgRadius[userid];

         return null;
      }

      internal void SetOrgRadius(Agent agent, string value)
      {
         orgRadius[agent.id] = value;
         MarkChanged();
      }

      private void tsbOrgRadiusDocs_Click(object sender, EventArgs e)
      {
         Type type = FormEntries.GetFormType(typeof(FmOrgRadiusDocs));
         ConstructorInfo ci = type.GetConstructor(Type.EmptyTypes);
         FmOrgRadiusDocs fm = (FmOrgRadiusDocs)ci.Invoke(new object[] { });

         fm.SetConfig(dsCommonConfig);
         fm.Show();
      }
   }

   public class SysColors : List<Color>
   {
      private static Color[] defaultColors = new Color[] { Color.Red, Color.Blue, Color.Green, Color.Yellow };
      public static string ConfigKey = "Colors";

      public SysColors() { LoadDefault(); }

      public void LoadDefault()
      {
         Clear();
         AddRange(defaultColors);
      }

      public CommonConfig ToConfig()
      {
         CommonConfig cfg = new CommonConfig();
         cfg.key = ConfigKey;
         StringBuilder value = new StringBuilder();
         foreach (Color clr in this)
         {
            if (value.Length > 0)
               value.Append(';');
            value.Append(clr.ToArgb());
         }
         cfg.value = value.ToString();

         return cfg;
      }

      public void Load(DataSet<int, CommonConfig> cfg)
      {
         bool f = false;
         Clear();
         foreach (CommonConfig cc in cfg.Values)
         {
            if (cc.key.Equals(ConfigKey))
            {
               f = true;

               string[] colors = cc.value.Split(new char[] { ';' });

               foreach (string clr in colors)
               {
                  Color c = Color.FromArgb(int.Parse(clr));
                  Add(c);
               }
            }
         }

         if (!f)
         {
            AddRange(defaultColors);
         }
      }
   }

   delegate void SelectColorHandler(object[] args);
   class ColorMenu : ContextMenuStrip
   {
      protected SysColors colors;

      public ColorMenu(SysColors colors)
      {
         this.colors = colors;

         SuspendLayout();
         Size = new Size(236, 96);
         ResumeLayout();
      }

      public SelectColorHandler SelectColor;

      internal virtual void RefreshItems()
      {
         Items.Clear();

         Size sz = new Size(235, 22);
         ToolStripMenuItem m1 = new ToolStripMenuItem(CreateImage(sz, Color.Black));
         m1.Tag = Color.Black;
         Items.Add(m1);

         foreach (Color clr in colors)
         {
            ToolStripMenuItem mi = new ToolStripMenuItem(CreateImage(sz, clr));
            mi.Tag = clr;
            Items.Add(mi);
         }
      }

      static public Image CreateImage(Size size, Color color)
      {
         Bitmap b = new Bitmap(size.Width, size.Height);

         using (Graphics g = Graphics.FromImage(b))
         {
            Brush br = new SolidBrush(color);
            RectangleF rect = new RectangleF(0, 0, size.Width, size.Height);
            g.FillRectangle(br, rect);

            br.Dispose();
         }

         return b;
      }

      protected override void OnItemClicked(ToolStripItemClickedEventArgs e)
      {
         Hide();
         if (e.ClickedItem.Tag != null && SelectColor != null)
         {
            Color selColor = (Color)e.ClickedItem.Tag;
            SelectColor(new object[] { selColor });
         }
      }
   }

   class FmSetPriceColor : FmSelectSKU
   {
      private ColorMenu menu;

      internal FmSetPriceColor(SysColors colors)
      {
         menu = CreateColorMenu(colors);

         tsbOK.Visible = false;
         tsbCancel.Visible = false;

         menu.SelectColor += CreateMenuSelector();

         tvArticles.MouseDown += new MouseEventHandler(tvArticles_MouseDown);
#if Serviko
            tvArticles.CheckBoxes = true;
#endif

         Colors = colors;
      }

      protected virtual SelectColorHandler CreateMenuSelector()
      {
         return new SelectColorHandler(menu_SelectColor);
      }

      protected virtual ColorMenu CreateColorMenu(SysColors colors) { return new ColorMenu(colors); }

      void tvArticles_MouseDown(object sender, MouseEventArgs e)
      {
         if (e.Button == MouseButtons.Right)
         {
            TreeNode tn = tvArticles.GetNodeAt(e.Location);

            if (tn != null && tn.Tag is Price)
            {
               tvArticles.SelectedNode = tn;
               menu.RefreshItems();
               menu.Show(tvArticles, e.Location, ToolStripDropDownDirection.BelowRight);
            }
         }
      }

      void menu_SelectColor(object[] args)
      {
         if (args.Length > 0)
         {
            Color clr = (Color)args[0];
            TreeNode selected = tvArticles.SelectedNode;
            DataSet<int, SysColor> uc = new DataSet<int, SysColor>(SysColor.OBJECT_NAME, false);

            if (selected != null)
            {
               SetColor(clr, selected, uc);
#if Serviko
                    foreach(TreeNode n in GetChecked())
                       SetColor(clr, n, uc);

                    SetChecked(false, tvArticles.Nodes);
#endif
               if (uc.Count > 0)
               {
                  List<IDataSet> update = new List<IDataSet>();

                  update.Add(uc);
                  Config cfg = Config.GetConfig();
                  DataModule.UpdateDataSet(update, null, null, cfg.GetConnection());
               }
            }
         }
      }

      private static void SetColor(Color clr, TreeNode selected, DataSet<int, SysColor> uc)
      {
         int rgbColor = clr.ToArgb() & 0xFFFFFF; // remove alpha chanel
         Price p = selected.Tag as Price;
         if (p != null)
         {
            if ((p.Color.ToArgb() & 0xFFFFFF) != rgbColor)
            {
               SysColor sysClr = new SysColor();

               p.Color = clr;
               selected.ForeColor = clr;

               sysClr.id = p.id;
               sysClr.type = (int)SysColor.Type.Price;
               sysClr.face = p.color;

               uc[uc.Count] = sysClr;

            }
         }
      }
   }
}
