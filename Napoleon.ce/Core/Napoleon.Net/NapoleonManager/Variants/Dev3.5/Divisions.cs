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
using GRSoft.UILib;

namespace GRSoft.NapoleonManager
{
   public partial class Divisions : Form
   {
      DivisionList divisions = DivisionList.GetDataSet();

      UserForm userForm;
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

      //TreeNode unusedUsers = null;
      TreeGridNode freeAgents = null;
      bool clearing = false;
      bool doClosing = false;

      //object canRemove = null;

      bool changed = false;
      string baseTitle = "";
      private ToolTip ttDivision = new ToolTip();
      protected SysColors colors = new SysColors();

      public Divisions()
      {
         InitializeComponent();

#if HappyLand
#else
         tbSep1.Visible = false;
         tbSep2.Visible = false;
         tbCoef.Visible = false;
#endif

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


#if QUESTION
         ToolStripButton btnQuestion = new System.Windows.Forms.ToolStripButton();
         btnQuestion.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         btnQuestion.Name = "btnQuestion";
         btnQuestion.Size = new System.Drawing.Size(101, 22);
         btnQuestion.Text = "Анкеты";
         btnQuestion.Click += new System.EventHandler((obj, arg) => {
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
#if SCRIPT_DOC
         tbSep1.Visible = true;
         btnScriptDesigner.Visible = true;
#endif

#if PRICE_MONITORING
         ToolStripButton tsbm = new ToolStripButton();
         tsbm.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         tsbm.Name = "tb";
         tsbm.Size = new System.Drawing.Size(101, 22);
         tsbm.Text = "Мониторинг";
         tsbm.Click += new System.EventHandler(OpenMonitoring);

         tb.Items.Add(tsbm);
#endif

#if ORG_STOP_EDITOR
         {
         ToolStripButton tbst = new ToolStripButton();
         tbst.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         tbst.Name = "tb";
         tbst.Size = new System.Drawing.Size(101, 22);
         tbst.Text = "Стоп-лист";
         tbst.Click += new System.EventHandler(EditStopList);
         tb.Items.Add(tbst);
         }
#endif

#if DISABLE_MATRIX
         tsbMatrixDesigner.Visible = false;
#endif

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

#if PRICE_MONITORING
      void OpenMonitoring(object sender, EventArgs e)
      {
         Type agentTask = FormEntries.GetFormType(typeof(MonitoringItems));
         ConstructorInfo ci = agentTask.GetConstructor(Type.EmptyTypes);
         Form fm = (Form)ci.Invoke(new object[] { });
         fm.Show();
      }
#endif

#if ORG_STOP_EDITOR
      void EditStopList(object sender, EventArgs e)
      {
         Type stopType = FormEntries.GetFormType(typeof(FmStopOrgList));
         ConstructorInfo ci = stopType.GetConstructor(Type.EmptyTypes);
         Form fm = (Form)ci.Invoke(new object[] { });
         fm.Show();
      }
#endif

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
         Config c = Config.GetConfig();
         if (c.CheckLogin() == false)
            return;

         DBConnection conn = c.GetConnection();

         Agents agents = DataModule.Get("Agents") as Agents;
         if (agents == null)
            agents = new Agents();

         bool refreshReference = (agents.Count == 0);

         mainArticleFolder = DataModule.Get("ManagerFolder") as DataSet<string, ManagerFolder>;

         if (mainArticleFolder == null)
         {
            mainArticleFolder = new DataSet<string, ManagerFolder>("ManagerFolder");
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
            updSets.Add(dsOrg);
         }

         divisionForm.BeforeUpdate(updSets);
         FmWait.ShowForm(this,  DataModule.RefreshGiveSets(conn, updSets, FmWait.ProgressIndicator));
         //connectStatus.Text = "Получение данных...";
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

      //internal object CanRemove
      //{
      //   get { return canRemove; }
      //   set
      //   {
      //      canRemove = value;
      //      if (canRemove == null)
      //      {
      //         delButton.Enabled = false;
      //         delButton.Text = "Удалить";
      //      }
      //      else
      //      {
      //         delButton.Enabled = true;
      //         delButton.Text = "Удалить '" + value.ToString() + "'";
      //      }
      //   }
      //}

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

      TreeGridNode FindNode(object d, TreeGridNodeCollection nodes)
      {
         foreach (TreeGridNode tgn in nodes)
         {
            if (tgn.DataItem == d)
               return tgn;

            if (tgn.Nodes.Count > 0)
            {
               TreeGridNode fnd = FindNode(d, tgn.Nodes);
               if (fnd != null)
                  return fnd;
            }
         }

         return null;
      }

      void RefreshNode(Division d)
      {
         //tvDivisions.SuspendLayout();

         //TreeNode n = FindNode(d, tvDivisions.Nodes);
         //if (n != null)
         //{
         //   List<Division> ld = new List<Division>();
         //   List<Agent> la = new List<Agent>();

         //   foreach (Division dc in d.Childs)
         //   {
         //      ld.Add(dc);
         //   }

         //   int i=0;
         //   for (; i < d.agents.Count; )
         //   {
         //      Division.DivisionAgent da = d.agents[i];
         //      if (da.agent != null)
         //      {
         //         la.Add(da.agent);
         //         i++;
         //      }
         //      else
         //         d.agents.Remove(da);
         //   }

         //   //foreach (Division.DivisionAgent da in d.agents)
         //   //{
         //   //   if (da.agent != null)
         //   //      la.Add(da.agent);
         //   //}
         //   n.Text = d.ToString();
         //   Refresh(n, ld, la);

         //   if (unusedUsers != null)
         //   {
         //      List<Agent> unused = divisions.UnusedAgents();
         //      Refresh(unusedUsers, null, unused);
         //   }
         //}

         //tvDivisions.ResumeLayout();
      }

      void RefreshNode(Agent a)
      {
         //TreeNode n = FindNode(a, tvDivisions.Nodes);
         //if (n != null)
         //   n.Text = a.ToString();
      }

      //TreeNode FindNode(object tag, TreeNodeCollection nodes)
      //{
      //   foreach (TreeNode node in nodes)
      //   {
      //      if (node.Tag == tag)
      //         return node;

      //      TreeNode n = FindNode(tag, node.Nodes);
      //      if (n != null)
      //         return n;
      //   }
      //   return null;
      //}

      //void Refresh(TreeNode node, List<Division> divisions, List<Agent> users)
      //{
      //   Dictionary<Division, TreeNode> dn = new Dictionary<Division, TreeNode>();
      //   Dictionary<Agent, TreeNode> an = new Dictionary<Agent,TreeNode>();

      //   TreeNode firstAgent = null;
      //   TreeNodeCollection nc = node.Nodes;

      //   foreach (TreeNode n in nc)
      //   {
      //      if (n.Tag is Agent)
      //      {
      //         if (firstAgent == null)
      //            firstAgent = n;
      //         an[n.Tag as Agent] = n;
      //      }
      //      if (n.Tag is Division)
      //         dn[n.Tag as Division] = n;
      //   }

      //   if (divisions != null)
      //   {
      //      foreach(Division d in divisions)
      //      {
      //         if (dn.ContainsKey(d))
      //            dn.Remove(d);
      //         else
      //         {
      //            TreeNode newNode = NewNode(d);
      //            if (firstAgent == null) nc.Add(newNode);
      //            else nc.Insert(nc.IndexOf(firstAgent), newNode);
      //         }
      //      }

      //      foreach (KeyValuePair<Division, TreeNode> kvn in dn)
      //         nc.Remove(kvn.Value);
      //   }

      //   if (users != null)
      //   {
      //      foreach (Agent a in users)
      //      {
      //         if (an.ContainsKey(a)) an.Remove(a);
      //         else
      //         {
      //            TreeNode newNode = NewNode(a);
      //            nc.Add(newNode);
      //         }
      //      }

      //      foreach (KeyValuePair<Agent, TreeNode> avn in an)
      //         nc.Remove(avn.Value);
      //   }
      //}

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
            SelectDivision(selected);//, tvDivisions.Nodes);

         divisionForm.DataLoaded();
      }

      bool SelectDivision(Division selected)
      {
         TreeGridNode sel = FindNode(selected, tgvDivisions.Nodes);
         if (sel != null)
            tgvDivisions.CurrentCell = sel.Cells[0];
         return sel != null;
      }

      //private bool SelectDivision(Division selected, TreeNodeCollection nodes)
      //{
      //   bool ret = false;
      //   foreach (TreeNode node in nodes)
      //   {
      //      if (node.Tag == selected)
      //      {
      //         tvDivisions.SelectedNode = node;
      //         ret = true;
      //         break;
      //      }

      //      if (SelectDivision(selected, node.Nodes))
      //      {
      //         ret = true;
      //         break;
      //      }
      //   }

      //   return ret;
      //}

      //TreeNode NewNode(Division d)
      //{
      //   TreeNode node = new TreeNode(d.ToString(), 0, 1);
      //   node.Tag = d;
      //   return node;
      //}

      //TreeNode NewNode(Agent a)
      //{
      //   TreeNode node = new TreeNode(a.ToString(), 2, 2);
      //   node.Tag = a;
      //   return node;
      //}

      private void tgvDivisions_ColumnHeaderMouseClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         DoSort(tgvDivisions.Columns[e.ColumnIndex].DataPropertyName);
         TreeGridNodeCollection nodes = tgvDivisions.Nodes;
         tgvDivisions.UnSiteAll();
         foreach(TreeGridNode tn in nodes)
         {
            tgvDivisions.SiteNode(tn);
         }
      }

      void DoSort(string fieldName)
      {
         DataGridViewColumn sortedColumnt = null;
         foreach(DataGridViewColumn dvc in tgvDivisions.Columns)
         {
            if (dvc.DataPropertyName == fieldName)
               sortedColumnt = dvc;
            else
               dvc.HeaderCell.SortGlyphDirection = SortOrder.None;
         }

         if( sortedColumnt == null)
            return;

         SortOrder so = sortedColumnt.HeaderCell.SortGlyphDirection;
         if (so == SortOrder.None || so == SortOrder.Descending)
            so = SortOrder.Ascending;
         else
            so = SortOrder.Descending;

         sortedColumnt.SortMode = DataGridViewColumnSortMode.Programmatic;
         sortedColumnt.HeaderCell.SortGlyphDirection = so;
         SortNodes(fieldName, so, tgvDivisions.Nodes);
      }

      class CmpNodes : IComparer<TreeGridNode>
      {
         string field;
         bool ascending;
         TreeGridNode freeAgents;

         public CmpNodes(string field, bool ascending, TreeGridNode freeAgents)
         {
            this.field = field;
            this.ascending = ascending;
            this.freeAgents = freeAgents;
         }

         public int Compare(TreeGridNode x, TreeGridNode y)
         {
            Division a = x.DataItem as Division;
            if( a != null )
            {
               Division b = y.DataItem as Division;
               if (b == null)
                  return -1;
               if(x == freeAgents)
                  return 1;
               if (y == freeAgents)
                  return -1;
               return ascending ? a.Name.CompareTo(b.Name) : b.Name.CompareTo(a.Name);
            }

            Agent ag = x.DataItem as Agent;
            if( ag != null )
            {
               Agent bg = y.DataItem as Agent;
               if (bg == null)
                  return -1;

               PropertyInfo prop = ag.GetType().GetProperty(field);
               if( prop != null)
               {
                  string src1 = (ascending ? prop.GetValue(ag, null) : prop.GetValue(bg, null)) as string;
                  string src2 = (ascending ? prop.GetValue(bg, null) : prop.GetValue(ag, null)) as string;

                  if (src1 == null)
                     src1 = "";
                  if (src2 == null)
                     src2 = "";
                  return src1.CompareTo(src2);
               }
            }
            return -1;
         }
      }

      private void SortNodes(string fieldName, SortOrder so, TreeGridNodeCollection nodes)
      {
         nodes.Sort(new CmpNodes(fieldName, so == SortOrder.Ascending, freeAgents));
         foreach(TreeGridNode tn in nodes)
         {
            if (tn.Nodes.Count > 0)
               SortNodes(fieldName, so, tn.Nodes);
         }
      }

      void AddDivision(Division d, TreeGridNodeCollection nodes)
      {
         if (d == null)
            return;

         TreeGridNode n = nodes.AddDataItem(d);
         foreach(Division ch in d.Childs)
            AddDivision(ch, n.Nodes);

         List<Agent> agents = new List<Agent>();
         foreach(Division.DivisionAgent da in d.agents)
         {
            if (da.agent != null)
               n.Nodes.AddDataItem(da.agent);
         }
      }

      void RefreshDivisionTree()
      {
         tgvDivisions.SuspendLayout();
         tgvDivisions.Nodes.Clear();

         AddDivision(divisions.Root, tgvDivisions.Nodes);

         freeAgents = tgvDivisions.Nodes.Add("Свободные агенты");
         List<Agent> agents = divisions.UnusedAgents();

         foreach (DataGridViewColumn dvc in tgvDivisions.Columns)
            dvc.HeaderCell.SortGlyphDirection = SortOrder.None;

         agents.ForEach(x => freeAgents.Nodes.AddDataItem(x));
         DoSort("Name");

         tgvDivisions.ExpandAll();

         tgvDivisions.ResumeLayout();
         
         if(tgvDivisions.Nodes.Count > 0)
         {
            DataGridViewCellEventArgs arg = new DataGridViewCellEventArgs(0, 0);
            tgvDivisions_RowEnter(this, arg);
         }
      }


      //private void AddChildNodes(TreeNode parent, Division division)
      //{
      //   foreach (Division child in division.Childs)
      //   {
      //      TreeNode node = NewNode(child);
      //      AddChildNodes(node, child);
      //      parent.Nodes.Add(node);
      //   }
      //   foreach (Division.DivisionAgent da in division.agents)
      //   {
      //      if (da.agent != null)
      //      {
      //         TreeNode un = NewNode(da.agent);
      //         parent.Nodes.Add(un);
      //      }
      //   }
      //}

      //private void RefreshDivisionTree()
      //{
      //   tvDivisions.Nodes.Clear();
      //   Division root = divisions.Root;

      //   if (root != null)
      //   {
      //      TreeNode rootNode = new TreeNode(root.name, 0, 1);
      //      rootNode.Tag = root;
      //      AddChildNodes(rootNode, root);

      //      tvDivisions.Nodes.Add(rootNode);
      //      tvDivisions.SelectedNode = rootNode;
      //   }

      //   //managersNode = new TreeNode("Руководители отделов", 3, 3);
      //   //Refresh(managersNode, null, divisions.Managers());
      //   //tvDivisions.Nodes.Add(managersNode);

      //   unusedUsers = new TreeNode("Свободные агенты", 3, 3);
      //   Refresh(unusedUsers, null, divisions.UnusedAgents());
      //   tvDivisions.Nodes.Add(unusedUsers);
      //}

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

         if (tracking.Count > 0)
         {
            int ctr = 0;
            DataSet<int, CommonConfig> addCfg = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);
            foreach (KeyValuePair<string, string> kv in tracking)
            {
               CommonConfig cfg = new CommonConfig();
               cfg.key = "Tracking";
               cfg.userid = kv.Key;
               cfg.value = kv.Value;
               addCfg[ctr++] = cfg;
            }
            wrObj.Add(addCfg);
         }

         Config c = Config.GetConfig();
         DBConnection conn = c.GetConnection();

         connectStatus.Text = "Сохранение...";
         
         bool done = divisionForm.BeforeWriteChanges(wrObj, rmvObj, replaced, conn);

         if (done && (wrObj.Count > 0 || rmvObj.Count > 0 || replaced.Count > 0))
            done = DataModule.UpdateDataSet(wrObj, rmvObj, replaced, conn);
         if( !done )
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
            TreeGridNode tn = FindNode(value, tgvDivisions.Nodes);
            if (tn != null)
               tgvDivisions.CurrentCell = tn.Cells[0];
         }

         get
         {
            TreeGridNode sel = tgvDivisions.CurrentRow;
            return sel == null ? null : sel.DataItem as Agent;
         }
      }

      internal Division SelectedDivision
      {
         get
         {
            Division selected = null;
            TreeGridNode sel = tgvDivisions.CurrentRow;
            if (sel != null)
            {
               selected = sel.DataItem as Division;
               if (selected == null)
               {
                  sel = sel.Parent;
                  if (sel != null)
                     selected = sel.DataItem as Division;
               }
            }

            return selected;
         }

         set
         {
            TreeGridNode tn = FindNode(value, tgvDivisions.Nodes);
            if (tn != null)
               tgvDivisions.CurrentCell = tn.Cells[0];
         }
      }

      //internal Agent SelectedAgent
      //{
      //   set
      //   {
      //      TreeNode tn = FindNode(value, tvDivisions.Nodes);
      //      if (tn != null)
      //         tvDivisions.SelectedNode = tn;
      //   }

      //   get
      //   {
      //      TreeNode tn = tvDivisions.SelectedNode;
      //      if (tn != null)
      //         return tn.Tag as Agent;

      //      return null;
      //   }
      //}

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

               result = d.folder;            }

            return result;
         }
      }

      //internal Division SelectedDivision
      //{
      //   get
      //   {
      //      Division selected = null;

      //      TreeNode tn = tvDivisions.SelectedNode;
      //      if (tn != null)
      //      {
      //         if (tn.Tag is Division)
      //            selected = tn.Tag as Division;
      //         else if (tn.Tag is Agent)
      //         {
      //            tn = tn.Parent;
      //            if (tn != null && tn.Tag is Division)
      //               selected = tn.Tag as Division;
      //         }
      //      }

      //      return selected;
      //   }

      //   set
      //   {
      //      TreeNode tn = FindNode(value, tvDivisions.Nodes);
      //      if (tn != null)
      //         tvDivisions.SelectedNode = tn;
      //   }
      //}

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
         doClosing = true;
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

         RefreshDivisionTree();
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
               AddChanged(s);
            }
         }
         RefreshDivisionTree();
      }

      //private void tvDivisions_ItemDrag(object sender, ItemDragEventArgs e)
      //{
      //   if (e.Button == MouseButtons.Left)
      //   {
      //      TreeNode tn = e.Item as TreeNode;
      //      if (tn.Tag is Agent && tn.Parent == unusedUsers)
      //         DoDragDrop(new System.Windows.Forms.DataObject("Agent", tn.Tag), DragDropEffects.Move|DragDropEffects.Copy);
      //   }
      //}

      //private void tvDivisions_Leave(object sender, EventArgs e)
      //{
      //   CanRemove = null;
      //}

      //private void tvDivisions_Enter(object sender, EventArgs e)
      //{
      //   TreeNode n = tvDivisions.SelectedNode;
      //   if (n != null)
      //      CanRemove = n.Tag;
      //}


      private void delButton_Click(object sender, EventArgs e)
      {
         if (MessageBox.Show("Удалить выделенные данные?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
         {
            List<Division> rmvd = new List<Division>();
            List<Agent> rmva = new List<Agent>();
            
            foreach(DataGridViewRow r in tgvDivisions.SelectedRows)
            {
               Division d = ((TreeGridNode)r).DataItem as Division;
               if (d != null)
                  rmvd.Add(d);
               else
               {
                  Agent a = ((TreeGridNode)r).DataItem as Agent;
                  if (a != null)
                     rmva.Add(a);
               }
            }

            foreach(Division d in rmvd)
            {
               if (removedDivisons.ContainsKey(d.id))
                  continue;

               List<Division> removed = divisions.RemoveTree(d);
               foreach (Division rdi in removed)
                  AddRemoved(rdi);
            }

            foreach(Agent a in rmva)
            {
               while(true)
               {
                  Division d = divisions.Find(a);
                  if (d == null)
                     break;
                  if (removedDivisons.ContainsKey(d.id) == false)
                  {
                     Dictionary<Agent, bool> r = new Dictionary<Agent, bool>();
                     r.Add(a, true);
                     if (d.Remove(r))
                     {
                        AddChanged(d);
                     }
                  }
               }
            }

            RefreshDivisionTree();
         }
         //if (MessageBox.Show(delButton.Text + "?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
         //{
         //   if (canRemove is Agent)
         //   {
         //      Division d = SelectedDivision;
         //      if (d != null)
         //      {
         //         Dictionary<Agent, bool> r = new Dictionary<Agent,bool>();
         //         r.Add(canRemove as Agent, true);
         //         if (d.Remove(r))
         //         {
         //            divisionForm.RefreshDataSets();
         //            AddChanged(d);
         //         }
         //      }
         //   }
         //   else if (canRemove is Division)
         //   {
         //      Division parent = null;
         //      Division rd = canRemove as Division;
         //      if (rd.parent != 0)
         //         parent = divisions[rd.parent];

         //      List<Division> removed = divisions.RemoveTree(rd);
         //      foreach (Division rdi in removed)
         //         AddRemoved(rdi);

         //      if (parent != null)
         //      {
         //         changed = false;
         //         parent.Remove(rd);
         //         AddChanged(parent);

         //         SelectedDivision = parent;
         //      }
         //   }
         //   RefreshDivisionTree();
         //}
      }

      //private void tvDivisions_MouseMove(object sender, MouseEventArgs e)
      //{
      //   TreeNode node = tvDivisions.GetNodeAt(e.X, e.Y);
         
      //   if (node != null && node.Parent != null)
      //   {
      //      if (node.Parent.Text.Equals("Руководители отделов"))
      //      {
      //         if (ttDivision.GetToolTip(tvDivisions) == string.Empty)
      //         {
      //            ttDivision.SetToolTip(tvDivisions, "руководитель не может быть агентом");
      //         }
      //      }
      //      else if (node.Parent.Text.Equals("Свободные агенты"))
      //      {
      //         if (ttDivision.GetToolTip(tvDivisions) == string.Empty)
      //         {
      //            ttDivision.SetToolTip(tvDivisions, "чтобы зачислить в подразделение откройте нужное подразделение и перенесите в таблицу подчинённые");
      //         }
      //      }
      //      else
      //      {
      //         ttDivision.SetToolTip(tvDivisions, string.Empty);
      //      }
      //   }
      //   else
      //   {
      //      ttDivision.SetToolTip(tvDivisions, string.Empty);
      //   }
      //}

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
         new FmScriptDesigner().Show();
      }

      private void tsbFind_TextChanged(object sender, EventArgs e)
      {
         timer1.Stop();

         if (tsbFind.Text.Length > 0)
            timer1.Start();
         else if (!clearing)
            tsbClearFind_Click(this, EventArgs.Empty);
      }

      private void tsbClearFind_Click(object sender, EventArgs e)
      {
         clearing = true;
         tsbFind.Clear();

         DoSearch("");

         clearing = false;
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         timer1.Stop();
         DoSearch(tsbFind.Text);
      }

      void SearchNodes(TreeGridNodeCollection nodes, string text, PropertyInfo sort, bool sortAsc)
      {
         foreach (TreeGridNode tn in nodes)
         {
            Division d = tn.DataItem as Division;
            if( d != null || tn == freeAgents)
            {
               SearchAgents(tn, text, sort, sortAsc);
            }
            if(tn.Nodes.Count > 0)
            {
               SearchNodes(tn.Nodes, text, sort, sortAsc);
            }
         }
      }

      class SortAgents : IComparer<Agent>
      {
         PropertyInfo prop;
         bool ascending;

         public SortAgents(PropertyInfo sort, bool sortAsc)
         {
            this.prop = sort;
            this.ascending = sortAsc;
         }

         public int Compare(Agent x, Agent y)
         {
            string src1 = (ascending ? prop.GetValue(x, null) : prop.GetValue(y, null)) as string;
            string src2 = (ascending ? prop.GetValue(y, null) : prop.GetValue(x, null)) as string;

            if (src1 == null)
               src1 = "";
            if (src2 == null)
               src2 = "";
            return src1.CompareTo(src2);
         }
      }

      private void SearchAgents(TreeGridNode dnode, string text, PropertyInfo sort, bool sortAsc)
      {
         List<TreeGridNode> rmv = new List<TreeGridNode>();
         foreach(TreeGridNode tn in dnode.Nodes)
         {
            if (tn.DataItem is Agent)
               rmv.Add(tn);
         }
         rmv.ForEach(x => dnode.Nodes.Remove(x));

         List<Agent> agents = new List<Agent>();

         Division d = dnode.DataItem as Division;
         if( d == null )
            agents = divisions.UnusedAgents();
         else
         {
            foreach (Division.DivisionAgent da in d.agents)
               if (da.agent != null)
                  agents.Add(da.agent);
         }

         agents.Sort(new SortAgents(sort, sortAsc));
         foreach(Agent a in agents)
         {
            if(text.Length == 0 ||  a.name.ToUpper().Contains(text))
               dnode.Nodes.AddDataItem(a).ResetLevel();
         }
      }

      private void DoSearch(string srch)
      {
         PropertyInfo sort = null;
         bool sortAsc = false;
         foreach(DataGridViewColumn dvc in tgvDivisions.Columns)
         {
            if(dvc.HeaderCell.SortGlyphDirection != SortOrder.None)
            {
               sort = typeof(Agent).GetProperty(dvc.DataPropertyName);
               sortAsc = (dvc.HeaderCell.SortGlyphDirection == SortOrder.Ascending);
               break;
            }
         }
         
         tgvDivisions.SuspendLayout();
         SearchNodes(tgvDivisions.Nodes, srch.ToUpper(), sort, sortAsc);
         tgvDivisions.ResumeLayout();
      }

      private void tgvDivisions_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         if (doClosing)
            return;

         object di = (tgvDivisions.Rows[e.RowIndex] as TreeGridNode).DataItem;
         Control.ControlCollection cc = splitContainer1.Panel2.Controls;
         if (di is Division)
         {
            divisionForm.Division = di as Division;
            if (cc.Count == 0 || cc[0] != divisionForm)
            {
               cc.Clear();
               cc.Add(divisionForm);
            }
         }
         else if (di is Agent)
         {
            userForm.Agent = di as Agent;
            if (cc.Count == 0 || cc[0] != userForm)
            {
               cc.Clear();
               cc.Add(userForm);
            }
         }
         else
         {
            cc.Clear();
         }
      }

      private void tgvDivisions_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         DataGridViewRow r = tgvDivisions.Rows[e.RowIndex];
         if (r == freeAgents)
            e.CellStyle.BackColor = Color.LightGray;
      }
   }

   public class SysColors : List<Color>
   {
      private static Color[] defaultColors = new Color[] { Color.Red, Color.Blue, Color.Green, Color.Yellow};
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
            if( value.Length > 0 )
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

         if(!f)
         {
            AddRange(defaultColors);
         }
      }
   }

   delegate void SelectColorHandler(object [] args);
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
            SelectColor(new object[]{selColor});
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

         Colors = colors;
      }

      protected virtual SelectColorHandler CreateMenuSelector()
      {
         return new SelectColorHandler(menu_SelectColor);
      }

      protected virtual ColorMenu CreateColorMenu(SysColors colors) {return new ColorMenu(colors); }

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

      void menu_SelectColor(object [] args)
      {
         if (args.Length > 0)
         {
            Color clr = (Color)args[0];
            TreeNode selected = tvArticles.SelectedNode;
            if (selected != null)
            {
               DataSet<int, SysColor> uc = new DataSet<int, SysColor>(SysColor.OBJECT_NAME, false);
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
   }
}
