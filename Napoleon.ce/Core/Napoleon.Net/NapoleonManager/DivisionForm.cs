/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма подразделения
 * 
 * ert   05/05/2010   creating
 */

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Collections;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   delegate void DivisionChangedHandler(object o, Division d);

   public partial class DivisionForm : UserControl
   {
      public Divisions parent = null;
      protected Division division = null;
      RefreshableSource userSource = new RefreshableSource();
      RefreshableSource divisionSource = new RefreshableSource();
      public DataSet<int, CommonConfig> dsCommonConfig = null;
      private DataSet<string, ManagerFolder> dsDivisionManagerFolder;
      private bool canCheckNode = false;

      internal event DivisionChangedHandler Changed;

      public DivisionForm()
      {
         InitializeComponent();

         childUserList.AutoGenerateColumns = false;
         childDivisionList.AutoGenerateColumns = false;
         childUserList.DataSource = userSource;
         childDivisionList.DataSource = divisionSource;

         AdjustForm();
         Dock = DockStyle.Fill;

         dsDivisionManagerFolder = new DataSet<string, ManagerFolder>("ManagerFolder", false);

#if DIVISION_ARTICLES
         childArticles.Show();
#else
         tabControl1.TabPages.Remove(childArticles);
#endif
         if (CurrentUser.user != null && CurrentUser.user is Manager)
         {
#if DEBUG
#else
            setCheif.Enabled = ((Manager)CurrentUser.user).config.canChangePassword;
#endif
         }
#if ORG_DISPOSITION
         DispositionClmn.Visible = true;
         OrgRadiusColumn.Visible = true;
#else
         DispositionClmn.Visible = false;
         OrgRadiusColumn.Visible = false;
#endif
      }

      virtual internal void BeforeUpdate(List<IDataSet> updSet)
      {
      }

      virtual internal void DataLoaded()
      {
      }

      virtual protected void AdjustForm()
      {
         tabControl1.TabPages[0].ToolTipText = "Добавьте подчинённых в это подразделение с помощью кнопки \"Создать\",\nлибо перетащите в эту таблицу агентов из группы \"Свободные агенты\"";
         tabControl1.TabPages[1].ToolTipText = " Добавьте подразделения используя кнопку \"Создать\"";
         ToolTip setCheifToolTip = new ToolTip();
         setCheifToolTip.SetToolTip(setCheif, "Добавьте руководителя подразделения");
      }

      internal void SetParent(Divisions parent)
      {
         this.parent = parent;
      }

      internal void CheckChanges()
      {
         bool changed = false;

         if (division == null)
            return;

         if (name.Text != division.name)
         {
            changed = true;
            division.name = name.Text;
         }

         if (description.Text != division.description)
         {
            changed = true;
            division.description = description.Text;
         }

         changed = CheckChildChanges() || changed;

         if (changed)
            Changed(this, division);
      }

      protected virtual bool CheckChildChanges()
      {
         return false;
      }

      protected class DataItem
      {
         public Agent agent;
         protected DivisionForm owner;

         public DataItem(Agent a, DivisionForm o) { agent = a; owner = o; }

         public string AgentName { get { return (agent==null) ? "" : agent.Name; } }

         public bool Tracking
         {
            get { return GetTrackingCode((agent == null) ? "" : agent.id); }
            set { owner.SetTracking(agent, ToTrackingCode(value)); }
         }
#if ORG_DISPOSITION
         public bool Disposition
         {
            get { return GetDispositionCode((agent == null) ? "" : agent.id); }
            set { owner.SetDisposition(agent, ToDispositionCode(value)); }
         }


         private string ToDispositionCode(bool value)
         {
            return value ? "1" : "";
         }

         private bool GetDispositionCode(string userid)
         {
            string v = owner.GetDisposition(userid);
            if (v != null) return (v != "");

            foreach (CommonConfig serverConfig in owner.dsCommonConfig.Data)
               if (serverConfig.userid.Equals(userid) &&
                     serverConfig.key.Equals("Disposition"))
                  return (serverConfig.value != "");

            return false;
         }

         public string OrgRadius
         {
            get { return GetOrgRadius((agent == null) ? "" : agent.id); }
            set { owner.SetOrgRadius(agent, value); }
         }

         private string GetOrgRadius(string userid)
         {
            string v = owner.GetOrgRadius(userid);
            if (v != null)
               return v;

            foreach (CommonConfig serverConfig in owner.dsCommonConfig.Data)
               if (serverConfig.userid.Equals(userid) &&
                     serverConfig.key.Equals("OrgRadius"))
                  return serverConfig.value;

            return string.Empty;
         }
#endif

         public bool GetTrackingCode(string userid)
         {
            string v = owner.GetTracking(userid);
            if( v != null ) return (v != "none");

            foreach (CommonConfig serverConfig in owner.dsCommonConfig.Data)
               if (serverConfig.userid.Equals(userid) &&
                     serverConfig.key.Equals("Tracking"))
                  return (serverConfig.value != "none");

            return false;
         }

         private string ToTrackingCode(bool caption)
         {
            return !caption ? "none" : "GPSroute";
         }
      }

      virtual protected DataItem CreateItem(Agent a, DivisionForm form)
      {
         return new DataItem(a, form);
      }

      private void UpdateUserSource()
      {
         List<object> src = new List<object>();
         foreach (Division.DivisionAgent agent in division.agents)
         {
            Agent a = agent.agent;
            if (a == null) continue;

            src.Add(CreateItem(a, this));
         }
         userSource.DataSource = src;
      }
      
      internal Division Division
      {
         get { return division; }
         set
         {
            if (division != null)
               CheckChanges();

            division = value;
            
            name.Text = division.name;
            description.Text = division.description;

            divisionSource.DataSource = division.Childs;
            UpdateUserSource();

            dsCommonConfig = DataModule.Get(CommonConfig.OBJECT_NAME) as DataSet<int, CommonConfig>;
            SetNodeChecking();

            PostDivisionChanged();
         }
      }

      protected virtual void PostDivisionChanged() { }

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

      public void SetNodeChecking()
      {
         canCheckNode = false;
         tvAccessibleArticles.SuspendLayout();

         ICollection folders = parent.Folders;
         if(folders != null)
         {
            SetCheckAllNode(folders.Count == 0);
            foreach (ManagerFolder folder in folders)
            {
               SetNodeStateFromId(folder.id);
            }
         }
         tvAccessibleArticles.ResumeLayout();
         canCheckNode = true;
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

      internal void RefreshDataSets()
      {
         UpdateUserSource();
         divisionSource.Refresh();
      }

      private void setCheif_Click(object sender, EventArgs e)
      {
         Type type = FormEntries.GetFormType(typeof(DivisionChief));
         ConstructorInfo ci = type.GetConstructor(new Type[]{typeof(Division)});
         Form fm = (Form)ci.Invoke(new object[] { division });
         fm.Show();
      }

      public virtual void AddAgents(Agent[] agents, bool exclusive)
      {
         if (exclusive)
         {
            DivisionList d = DataModule.Get(DivisionList.ObjName) as DivisionList;
            if (d != null)
            {
               List<Division> changed = d.RemoveAgents(agents);
               foreach (Division cd in changed)
                  Changed(this, cd);
            }
         }

         bool added = false;
         foreach(Agent a in agents)
         {
            if (division.HaveAgent(a) == false)
            {
               Division.DivisionAgent da = new Division.DivisionAgent();
               da.agent = a;
               da.id = a.id;
               division.agents.Add(da);
               added = true;
            }
         }

         if (added)
         {
            RefreshDataSets();
            if (Changed != null)
               Changed(this, division);
         }
      }

      enum SetCheifAction { Ack, Move, Copy };

      private void SetCheif(Agent newCheif, SetCheifAction action)
      {
         if (newCheif == division.cheif)
            return;

         DivisionList d = DataModule.Get(DivisionList.ObjName) as DivisionList;
         if (d != null)
         {
            Division finded = d.Find(newCheif);
            if (finded != null)
            {
               if (action == SetCheifAction.Ack)
               {
                  DialogResult res = MessageBox.Show("Этот сотрудник входит в '" + finded.name +
                     "' подразделение\nУбрать его из этого подразделения?",
                     "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);

                  if (res == DialogResult.Cancel)
                     return;

                  if (res == DialogResult.Yes) action = SetCheifAction.Move;
                  else action = SetCheifAction.Copy;
               }

               if (action == SetCheifAction.Move)
               {
                  d.RemoveAgents(new Agent[] { newCheif });

                  if (finded == division)
                     (childUserList.DataSource as RefreshableSource).Refresh();
                  else if (Changed != null)
                     Changed(this, finded);
               }
            }
         }

         // add agent
         division.cheif = newCheif;

         if (Changed != null)
            Changed(this, division);
      }

      private void childDivisionList_CellMouseDoubleClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         Division dc = childDivisionList.Rows[e.RowIndex].DataBoundItem as Division;
         if (parent != null)
            parent.SelectedDivision = dc;
      }

      private void childUserList_CellMouseDoubleClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         if (e.RowIndex == -1)
         {
            return;
         }

         DataItem ac = childUserList.Rows[e.RowIndex].DataBoundItem as DataItem;// Division.DivisionAgent;
         //Division.DivisionAgent ac = childUserList.Rows[e.RowIndex].DataBoundItem as DataItem;// Division.DivisionAgent;
         if (parent != null)
            parent.SelectedAgent = ac.agent;
      }

      private void OnDragOver(object sender, DragEventArgs e)
      {
         if (e.Data.GetDataPresent("Agent"))
            e.Effect = ((e.KeyState & 8) != 0) ? DragDropEffects.Copy : DragDropEffects.Move;
      }

      private void OnDragEnter(object sender, DragEventArgs e)
      {
         if (e.Data.GetDataPresent("Agent"))
            e.Effect = ((e.KeyState & 8) != 0) ? DragDropEffects.Copy : DragDropEffects.Move;
      }

      private void OnDragDrop(object sender, DragEventArgs e)
      {
         Agent a = e.Data.GetData("Agent") as Agent;
         if (a != null)
         {
            //AddAgents(new Agent[] { a }, (e.Effect == DragDropEffects.Move));
            // remove agent from the other divisions always
            AddAgents(new Agent[] { a }, true);
         }
      }

      private void cheif_DragDrop(object sender, DragEventArgs e)
      {
         Agent newCheif = e.Data.GetData("Agent") as Agent;
         if (newCheif != null)
            SetCheif(newCheif,
               ((e.KeyState & 8) != 0) ? SetCheifAction.Copy : SetCheifAction.Move);
      }

      private void OnRowEnter(object sender, DataGridViewCellEventArgs e)
      {
         //if (parent != null)
         //{
         //   object db = (sender as DataGridView).Rows[e.RowIndex].DataBoundItem;
         //   object v = (db is Division.DivisionAgent) ? 
         //      (object)(db as Division.DivisionAgent).agent : (db is Division) ? 
         //      (object)(db as Division) :
         //      null;
         //   parent.CanRemove = v;
         //}
      }

      private void OnRowLeave(object sender, DataGridViewCellEventArgs e)
      {
         //if (parent != null)
         //   parent.CanRemove = null;
      }

      internal void SetTracking(Agent agent, string value)
      {
         foreach (CommonConfig cfg in dsCommonConfig.Data)
         {
            if (cfg.userid == agent.id && cfg.key == "Tracking")
               cfg.value = value;
         }
         if( parent != null )
            parent.SetTracking(agent, value);
      }

      internal string GetTracking(string aid)
      {
         return (parent != null) ? parent.GetTracking(aid) : null;
      }

      private void childUserList_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         if (IsAllowCommit(childUserList.CurrentCell))
         {
            childUserList.CommitEdit(DataGridViewDataErrorContexts.Commit);
         }
      }

      protected virtual bool IsAllowCommit(DataGridViewCell cell)
      {
         //const string TRACKING_COLUMN_TEXT = "Слежение";
         return cell != null && (cell.ColumnIndex == tracking.Index 
            || cell.ColumnIndex == DispositionClmn.Index);
      }

      internal virtual bool BeforeWriteChanges(List<IDataSet> wrObj, List<IDataSet> rmvObj, List<ReplacedSet> replaced, DBConnection conn)
      {
         return true;
      }

      internal virtual void AfterWrited()
      {
      }

      private void DivisionForm_Load(object sender, EventArgs e)
      {
         if (parent != null)
            tvAccessibleArticles.ImageList = parent.images;

         tvAccessibleArticles.Visible = true;
      }

      private void UpdateDataAfterModifyTree()
      {
         tvAccessibleArticles.SuspendLayout();

         division.folder.Clear();

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
            division.folder.Add(dest);
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
            if (Changed != null)
               Changed(this, division);
            canCheckNode = true;
         }
      }

      private void name_KeyDown(object sender, KeyEventArgs e)
      {
          parent.MarkChanged();
      }

#if ORG_DISPOSITION
      internal void SetDisposition(Agent agent, string value)
      {
         foreach (CommonConfig cfg in dsCommonConfig.Data)
         {
            if (cfg.userid == agent.id && cfg.key == "Disposition")
               cfg.value = value;
         }

         if (parent != null)
            parent.SetDisposition(agent, value);

      }

      internal string GetDisposition(string userid)
      {
         return (parent != null) ? parent.GetDisposition(userid) : null;
      }

      internal void SetOrgRadius(Agent agent, string value)
      {
         foreach (CommonConfig cfg in dsCommonConfig.Data)
         {
            if (cfg.userid == agent.id && cfg.key == "OrgRadius")
               cfg.value = value;
         }

         if (parent != null)
            parent.SetOrgRadius(agent, value);
      }

      internal string GetOrgRadius(string userid)
      {
         return (parent != null) ? parent.GetOrgRadius(userid) : null;
      }
#endif
   }

   class RefreshableSource : BindingSource
   {
      public RefreshableSource() { }

      public void Refresh()
      {
         ListChangedEventArgs ee = new ListChangedEventArgs(ListChangedType.Reset, 0);
         OnListChanged(ee);
      }
   }
}
