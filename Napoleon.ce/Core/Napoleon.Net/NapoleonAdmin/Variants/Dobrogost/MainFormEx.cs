using System.Windows.Forms;
using GRSoft.Network;
using System;
using System.Collections.Generic;

namespace GRSoft.NapoleonAdmin
{
   public class MainFormEx : MainForm
   {
      ContextMenuStrip userMenu;


      FmAgentActivity agents = new FmAgentActivity();
      SimpleDataSet<AgentActivity> agentActivity = new SimpleDataSet<AgentActivity>(AgentActivity.OBJECT_NAME, false);

      DataGridViewTextBoxColumn tryCount;
      DataGridViewCheckBoxColumn authPin;
      DataGridViewCheckBoxColumn resetPin;

      public static DataSet<string, UserPinData> dsUserPins = new DataSet<string, UserPinData>(UserPinData.OBJECT_NAME, false);

      public static int DEFAULT_TRY_COUNT = 5;


      public MainFormEx()
      {
         userMenu = new ContextMenuStrip();
         userMenu.Size = new System.Drawing.Size(119, 26);

         ToolStripMenuItem mi = new ToolStripMenuItem();
         mi.Name = "miAssign new";
         mi.Size = new System.Drawing.Size(118, 22);
         mi.Text = "Перенести данные";
         mi.Click += mi_Click;
         userMenu.Items.Add(mi);
         usersView.MouseDown += usersView_MouseDown;


         TabPage tp = new TabPage();
         tp.Name = "agentActivity";
         tp.Padding = new System.Windows.Forms.Padding(3);
         tp.Location = new System.Drawing.Point(4, 22);
         tp.Size = new System.Drawing.Size(1238, 503);
         tp.Text = "Активность агентов";
         tp.UseVisualStyleBackColor = true;

         tp.Controls.Add(agents);

         agents.Size = new System.Drawing.Size(20, 20);
         agents.Location = new System.Drawing.Point(0, 0);
         agents.Dock = DockStyle.Fill;

         int index = tabControl1.Controls.IndexOf(userActivity) + 1;
         List<Control> upd = new List<Control>();
         while (tabControl1.Controls.Count > index)
         {
            upd.Add(tabControl1.Controls[index]);
            tabControl1.Controls.RemoveAt(index);
         }

         tabControl1.Controls.Add(tp);
         tabControl1.Controls.SetChildIndex(tp, index);

         upd.ForEach(x => { tabControl1.Controls.Add(x); });

         ProgID.Width = 70;
         login.Width = 70;
         password.Width = 70;
         registred.Width = 100;

         tryCount = new DataGridViewTextBoxColumn();
         tryCount.DataPropertyName = "TryCount";
         tryCount.HeaderText = "Попыток\nPIN";
         tryCount.Name = "TryCount";
         tryCount.Width = 70;
         index = usersView.Columns.IndexOf(ProgID) + 1;
         usersView.Columns.Insert(index++, tryCount);

         authPin = new DataGridViewCheckBoxColumn();
         authPin.DataPropertyName = "AuthByPin";
         authPin.HeaderText = "Автрзц\nпо PIN";
         authPin.Name = "AuthByPin";
         authPin.Width = 60;
         usersView.Columns.Insert(index++, authPin);

         resetPin = new DataGridViewCheckBoxColumn();
         resetPin.DataPropertyName = "ResetPin";
         resetPin.HeaderText = "Сброс PIN";
         resetPin.Name = "resetByPin";
         resetPin.Width = 60;
         usersView.Columns.Insert(index++, resetPin);

         Width += 70;
      }

      void usersView_MouseDown(object sender, MouseEventArgs e)
      {
         if (e.Button == System.Windows.Forms.MouseButtons.Right)
         {
            DataGridView.HitTestInfo info = usersView.HitTest(e.X, e.Y);
            if (info.ColumnIndex == -1 || info.RowIndex == -1)
            {
               return;
            }

            usersView.CurrentCell = usersView[info.ColumnIndex, info.RowIndex];
         }
      }

      protected override void RefreshUserData()
      {
         base.RefreshUserData();

         bool isAgentView = cbUserType.SelectedIndex == 0;
         usersView.ContextMenuStrip = (isAgentView) ? userMenu : null;
      }

      void mi_Click(object sender, EventArgs e)
      {
         if (usersView.CurrentRow == null)
            return;

         UserDataItem udi = usersView.CurrentRow.DataBoundItem as UserDataItem;
         FmMoveAgentData form = (new FmMoveAgentData());
         form.SetSrcAgent(udi.Agent, config);

         form.Show();
      }

      protected override void PrepareViewComponents(bool agentView)
      {
         base.PrepareViewComponents(agentView);

         if (tryCount != null)
            tryCount.Visible = agentView;
         if (authPin != null)
            authPin.Visible = agentView;
         if (resetPin != null)
            resetPin.Visible = agentView;
      }

      protected override void AddUpdDataSet(System.Collections.Generic.List<IDataSet> upd)
      {
         agentActivity.Clear();
         upd.Add(agentActivity);

         dsUserPins.Filter = "not \"userid\" is null";
         upd.Add(dsUserPins);
      }

      protected override void UpdateLoadedData()
      {
         agents.RefreshData(agentActivity);
      }

      protected override void usersView_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         base.usersView_CurrentCellDirtyStateChanged(sender, e);

         DataGridViewColumn cur = usersView.Columns[usersView.CurrentCell.ColumnIndex];
         if (cur == resetPin || cur == authPin)
            usersView.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }
   }

   public class AgentActivity : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "AgentActivity";

      public string userid = "";
      public string login = "";
      public DateTime sended = DateTime.Now;
      public string phone = "";
      public string imei = "";
   }

   public class UserPinData : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "UserPinData";

      [KeyField]
      public string userid = "";

      public string pinHash = "";
      public int authByPin = 0;
      public int resetPin = 0;
   }
}