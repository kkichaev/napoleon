using GRSoft.NapoleonAdmin.Properties;
using GRSoft.Network;
using Microsoft.VisualBasic;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Resources;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;
using static System.Windows.Forms.VisualStyles.VisualStyleElement;
using static System.Windows.Forms.VisualStyles.VisualStyleElement.TextBox;

namespace GRSoft.NapoleonAdmin
{
   internal class MainFormEx : MainForm
   {
      DataGridViewCheckBoxColumn clmnMinInterface;
      private System.Threading.Timer textWait = null;

      class Data 
      {
         public string  ID { get; set; }
         public string Agent { get; set; }
         public string Version { get; set; }
         public int Action { get; set; }

         public bool IsAgent { get; set; }
      }

      DataGridView grid;
      List<Data> data = new List<Data>();
      SortableBindingList<Data> filter = new SortableBindingList<Data>();
      ToolStripTextBox tbSearch;
      ToolStripComboBox cbAction;
      ToolStripComboBox cbView;
      DataSet<string, NewVersionAction> dsAction = new DataSet<string, NewVersionAction>(NewVersionAction.OBJECT_NAME);
      ToolStripButton save;
      ContextMenuStrip managerRights = new ContextMenuStrip();
      DataGridViewTextBoxColumn clmnManagerRight = new DataGridViewTextBoxColumn();

      public static RightToken writeScript = RightTokens.Get("ScriptDef");
      public static RightToken writeQuest = RightTokens.Get("Question");
      public static RightToken writeAgentSched = new RightToken("FirstDocTime", "Может редактировать работу агентов");

      class Action
      {
         public int ID { get; set; }
         public string Title { get; set; }

         public override string ToString()
         {
            return Title;
         }
      }

      public MainFormEx()
      {
         update.Text = "Загрузка лицензий";
         TabPage tp = new TabPage();
         tp.Text = "Обновление версий";
         tabControl1.TabPages.Add(tp);

         ToolStrip ts = new ToolStrip();
         ToolStripButton sync = new ToolStripButton();
         sync.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         sync.Image = userUpdate.Image;
         sync.ImageTransparentColor = System.Drawing.Color.Magenta;
         sync.Name = "sync";
         sync.Size = new System.Drawing.Size(23, 22);
         sync.Text = "Обновить";
         sync.Click += new System.EventHandler(Sync_Click);

         ts.Items.Add(sync);

         save = new ToolStripButton();
         save.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         save.Enabled = false;
         save.Image = userChangesSave.Image;
         save.ImageTransparentColor = System.Drawing.Color.Magenta;
         save.Name = "userChangesSave";
         save.Size = new System.Drawing.Size(23, 22);
         save.Text = "Сохранить изменения";
         save.Click += new System.EventHandler(Save_Click);

         ts.Items.Add(save);

         ToolStripLabel label = new ToolStripLabel();
         label.Text = "Поиск";
         label.Margin = new System.Windows.Forms.Padding(10, 0, 0, 0);

         ts.Items.Add(label);

         tbSearch = new ToolStripTextBox();
         tbSearch.Size = new System.Drawing.Size(150, 25);
         tbSearch.TextChanged += TbSearch_TextChanged;

         cbView = new ToolStripComboBox();
         cbView.Items.Add("Агенты");
         cbView.Items.Add("Менеджеры");
         cbView.SelectedIndex = 0;
         cbView.SelectedIndexChanged += CbView_SelectedIndexChanged;
         
         ts.Items.Add(tbSearch);
         ts.Items.Add(cbView);

         ToolStripLabel label2 = new ToolStripLabel();
         label2.Text = "Для всех";
         label2.Alignment = ToolStripItemAlignment.Right;
         label2.Margin = new System.Windows.Forms.Padding(10, 0, 0, 0);

         cbAction = new ToolStripComboBox();
         cbAction.Alignment = ToolStripItemAlignment.Right;
         cbAction.Items.Add(new Action { ID = 2, Title = "нет" });
         cbAction.Items.Add(new Action { ID = 0, Title = "Предупреждение" });
         cbAction.Items.Add(new Action { ID = 1, Title = "Блокировка" });
         cbAction.SelectedIndex = 0;


         ToolStripButton btnAction = new ToolStripButton();
         btnAction.DisplayStyle = ToolStripItemDisplayStyle.Text;
         btnAction.Name = "btnAction";
         btnAction.Size = new System.Drawing.Size(100, 22);
         btnAction.Text = "Применить";
         btnAction.Margin = new Padding(0, 0, 20, 0);
         btnAction.Alignment = ToolStripItemAlignment.Right;
         btnAction.Click += new System.EventHandler(Action_Click);

         this.version.Left -= 20;

         ts.Items.Add(btnAction);
         ts.Items.Add(cbAction);
         ts.Items.Add(label2);

         StatusStrip ss = new StatusStrip();

         grid = new DataGridView();
         grid.Dock = DockStyle.Fill;
         grid.BringToFront();
         grid.AllowUserToAddRows = false;
         grid.AllowUserToDeleteRows = false;
         grid.AllowUserToResizeColumns = false;
         grid.RowHeadersVisible = false;
         grid.AutoGenerateColumns = false;
         grid.DataSource = filter;
         grid.CurrentCellDirtyStateChanged += Grid_CurrentCellDirtyStateChanged;

         DataGridViewTextBoxColumn id = new DataGridViewTextBoxColumn();
         id.HeaderText = "ID";
         id.DataPropertyName = "ID";
         grid.Columns.Add(id);

         DataGridViewTextBoxColumn user = new DataGridViewTextBoxColumn();
         user.HeaderText = "Пользователь";
         user.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
         user.DataPropertyName = "Agent";
         grid.Columns.Add(user);

         DataGridViewTextBoxColumn version = new DataGridViewTextBoxColumn();
         version.HeaderText = "Версия";
         version.Width = 150;
         version.DataPropertyName = "Version";
         grid.Columns.Add(version);

         DataGridViewComboBoxColumn action = new DataGridViewComboBoxColumn();
         action.HeaderText = "Действие";
         action.DataPropertyName = "Action";
         action.DataSource = cbAction.Items;
         action.ValueMember = "ID";
         action.DisplayMember = "Title";
         grid.Columns.Add(action);
         action.Width = 150;

         tp.Controls.Add(grid);
         tp.Controls.Add(ts);
         tp.Controls.Add(ss);

         clmnMinInterface = new DataGridViewCheckBoxColumn();
         clmnMinInterface.DataPropertyName = "MinimizeInterface";
         clmnMinInterface.HeaderText = "Сокр.Функц";
         clmnMinInterface.Name = "clmnSellWithoutRest";
         clmnMinInterface.Visible = false;
         clmnMinInterface.Width = 90;
         usersView.Columns.Add(clmnMinInterface);

         ToolStripMenuItem mi = new ToolStripMenuItem();
         mi.Text = "Права";
         mi.Size = new System.Drawing.Size(134, 24);
         mi.Name = "miManagerRight";
         mi.Click += Mi_Click;
         managerRights.Items.Add(mi);
         managerRights.Size =  new System.Drawing.Size(135, 28);

         clmnManagerRight.HeaderText = "Права";
         clmnManagerRight.DataPropertyName = "RightText";
         clmnManagerRight.Width = 90;
         clmnManagerRight.Visible = false;
         usersView.Columns.Add(clmnManagerRight);
      }

      private void CbView_SelectedIndexChanged(object sender, EventArgs e)
      {
         LoadData(cbView.SelectedIndex == 0);
      }

      private void Mi_Click(object sender, EventArgs e)
      {
         if (usersView.CurrentRow == null)
            return;
         UserDataItem udi = usersView.CurrentRow.DataBoundItem as UserDataItem;
         FmManagerRights form = new FmManagerRights();
         form.Manager = udi.Manager;
         if( form.ShowDialog() == DialogResult.OK)
         {
            IsDirty = true;
            usersView.InvalidateRow(usersView.CurrentRow.Index);
         }
      }

      protected override void usersView_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         base.usersView_CurrentCellDirtyStateChanged(sender, e);
         if (usersView.CurrentCell.ColumnIndex == clmnMinInterface.DisplayIndex)
            usersView.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }

      protected override void PrepareViewComponents(bool agentView)
      {
         base.PrepareViewComponents(agentView);

         if (clmnMinInterface != null)
            clmnMinInterface.Visible = agentView;
         usersView.ContextMenuStrip = agentView ? null : managerRights;
         if (clmnManagerRight != null)
            clmnManagerRight.Visible = !agentView;
      }

      private void Grid_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         save.Enabled = true;
      }

      private void TbSearch_TextChanged(object sender, EventArgs e)
      {
         if (textWait != null)
            textWait.Dispose();
         textWait = new System.Threading.Timer(new TimerCallback(TimePassed), ((ToolStripTextBox)sender).Text, 500, 0);
      }

      void LoadData(bool agents)
      {
         data.Clear();
         filter.Clear();

         if (agents)
         {
            foreach (Agent a in dsAgents.Values)
            {
               Data d = new Data
               {
                  ID = a.id,
                  Agent = a.Name,
                  Version = GetVersion(a.id),
                  Action = GetAction(a.id),
                  IsAgent = true,
               };

               data.Add(d);
            }
         } else
         {
            foreach (DivisionManager dm in dsManagers.Data)
            {
               Data d = new Data
               {
                  ID = dm.login,
                  Agent = dm.name.Length == 0 ? dm.login : dm.name,
                  Version = GetVersion(dm.login),
                  Action = GetAction(dm.login),
                  IsAgent = false,
               };

               data.Add(d);
            }
         }
         data.ForEach(d => filter.Add(d));
      }

      private void Sync_Click(object sender, EventArgs e)
      {
         IsDirty = false;

         DBConnection conn = config.GetConnection();
         List<IDataSet> upd = new List<IDataSet>(new IDataSet[] { 
            dsAgents, 
            dsManagers,
            dsUserActivity, 
            dsAction});

         AddUpdDataSet(upd);

         conn.ReceiveTimeout = 3 * 60 * 1000;
         DataModule.RefreshGiveSets(conn, upd, null).Join();

         LoadData(cbView.SelectedIndex == 0);

         tbSearch.Text = "";
      }

      private int GetAction(string id)
      {
         if (dsAction.ContainsKey(id))
            return dsAction[id].action;
         return 2;
      }

      private string GetVersion(string id)
      {
         if (dsUserActivity.ContainsKey(id))
         {
            return dsUserActivity[id].version;
         }

         return "";
      }

      private void Action_Click(object sender, EventArgs e)
      {
         Action a = cbAction.SelectedItem as Action;

         if (a != null)
         {
            foreach(Data d in filter)
            {
               d.Action = a.ID;
            }
         }

         grid.Refresh();
         save.Enabled = true;
      }

      private void Save_Click(object sender, EventArgs e)
      {
         grid.CommitEdit(DataGridViewDataErrorContexts.Commit);
         List<IDataSet> list = new List<IDataSet>();
         list.Add(CollectActionDataSet());

         bool ret = DataModule.UpdateDataSet(list, null, null, config.GetConnection());
         MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");

         if (ret)
         {
            save.Enabled = false;
            data.ForEach((d) => dsAction[d.ID] = new NewVersionAction { action = d.Action, userid = d.ID });
         }
      }

      private IDataSet CollectActionDataSet()
      {
         DataSet<int, NewVersionAction> res = new DataSet<int, NewVersionAction>(NewVersionAction.OBJECT_NAME, false);
         data.ForEach((d) => res.Add(res.Count, new NewVersionAction { action = d.Action, userid = d.ID }));
         return res;
      }

      private void DoSearch(string val)
      {
         grid.SuspendLayout();

         filter.Clear();

         val = val.Trim().ToUpper();

         data.ForEach((d) => 
         {
            if (val.Length == 0 || d.Agent.ToUpper().Contains(val))
               filter.Add(d);
         });

         grid.ResumeLayout();
      }

      delegate void InvokeParamHandler(object objects);

      void TimePassed(object o)
      {
         try
         {
            Mutex m = new Mutex(false, "FMMainFormSearchMutex");
            if (m.WaitOne(0))
               Invoke(new InvokeParamHandler(delegate (object param) { DoSearch((string)param); }), new object[] { o });
            m.ReleaseMutex();
         }
         catch (Exception) { }
      }

   }
}
