using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Windows.Forms;

namespace GRSoft.NapoleonAdmin
{
   public class MainFormEx : MainForm
   {
      DataSet<int, ScriptDef> dsScripts;
      DataSet<string, Monitor> dsMonitors;
      SimpleDataSet<ManagerFolder> dsFolders;

      SimpleDataSet<Monitor> dsRemoveMonitor;

      public MainFormEx()
      {
         //btnAdd.Visible = true;
         btnEdit.Visible = true;
         btnDel.Visible = true;

         dsScripts = new DataSet<int, ScriptDef>(ScriptDef.OBJECT_NAME);
         dsScripts.Filter = "\"id\" <> -1";

         dsMonitors = new DataSet<string, Monitor>(Monitor.OBJECT_NAME);
         dsMonitors.Filter = "not (\"userid\" is null)";
         
         dsFolders = new SimpleDataSet<ManagerFolder>(ManagerFolder.OBJECT_NAME);
         dsFolders.Filter = "\"userid\" is null";

         dsRemoveMonitor = new SimpleDataSet<Monitor>(Monitor.OBJECT_NAME, false);
      }

      protected override void AddUpdDataSet(List<IDataSet> upd)
      {
         upd.Add(dsScripts);
         upd.Add(dsMonitors);
         upd.Add(dsFolders);
      }

      protected override void DrawCell(UserDataItem udi, DataGridViewCellFormattingEventArgs e)
      {
         base.DrawCell(udi, e);
         DivisionManager m = udi.Manager;
         if (m != null && dsMonitors.ContainsKey(m.guid))
            e.CellStyle.ForeColor = System.Drawing.Color.Blue;
         else
            e.CellStyle.ForeColor = usersView.DefaultCellStyle.ForeColor;
      }


      protected override void UpdateLoadedData()
      {
         foreach (DivisionManager dm in dsManagers.Data)
            if (dm.guid.Length == 0)
               dm.SetGuid();
      }
      void EnableEditControls(bool enable)
      {
         //btnAdd.Enabled = enable;
         btnEdit.Enabled = enable;
         btnDel.Enabled = enable;
      }

      protected override void RefreshUserData()
      {
         bool isAgentView = cbUserType.SelectedIndex == 0;
         EnableEditControls(!isAgentView);

         base.RefreshUserData();
      }

      //protected override void btnAdd_Click(object sender, System.EventArgs e)
      //{
      //   Monitor m = new Monitor();
      //   DivisionManager dm = new DivisionManager();
      //   dm.SetGuid();

      //   FmMonitorEdit fm = new FmMonitorEdit();
      //   fm.SetData(dsScripts, dsFolders, dsDivision, dsManagers, dsAgents, dm, m);
      //   if(fm.ShowDialog() == System.Windows.Forms.DialogResult.OK)
      //   {
      //      dsManagers[dm.login] = dm;
      //      dsMonitors[m.userid] = m;
            
      //      userChangesSave.Enabled = true;

      //      UserDataItem udi = AddManager(dm);
      //      userData.Add(udi);

      //      usersViewUpdateBindingSource();
      //   }
      //}

      protected override void btnDel_Click(object sender, System.EventArgs e)
      {
         bool isAgentView = cbUserType.SelectedIndex == 0;
         if (isAgentView)
            return;

         DataGridViewRow row = usersView.CurrentRow;
         if (row != null)
         {
            UserDataItem udi = (UserDataItem)row.DataBoundItem;
            DivisionManager dm = udi.Manager;

            Monitor m;
            if (!dsMonitors.TryGetValue(dm.guid, out m))
            {
               MessageBox.Show("Нельзя удалить менеджера.");
               return;
            }

            if (MessageBox.Show("Удалить монитор?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == System.Windows.Forms.DialogResult.Yes)
            {
               dsMonitors.Remove(m.userid);
               dsRemoveMonitor.Add(m);

               usersViewUpdateBindingSource();
               userChangesSave.Enabled = true;
            }
         }
      }

      protected override void btnEdit_Click(object sender, System.EventArgs e)
      {
         bool isAgentView = cbUserType.SelectedIndex == 0;
         if (isAgentView)
            return;

         DataGridViewRow row = usersView.CurrentRow;
         if (row != null)
         {
            UserDataItem udi = (UserDataItem)row.DataBoundItem;
            DivisionManager dm = udi.Manager;
            if (dm.guid.Length == 0)
               dm.SetGuid();

            Monitor m;
            if (!dsMonitors.TryGetValue(dm.guid, out m))
               m = new Monitor();

            FmMonitorEdit fm = new FmMonitorEdit();
            fm.SetData(dsScripts, dsFolders, dsDivision, dsManagers, dsAgents, dm, m);
            if (fm.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
               dsMonitors[m.userid] = m;
               userChangesSave.Enabled = true;

               usersView.InvalidateRow(row.Index);
            }
         }
      }

      protected override bool SaveChanges()
      {
         if(dsMonitors.Count > 0)
            userData.AddWriteSet(dsMonitors);
         if(dsRemoveMonitor.Count > 0)
            userData.AddRemoveSet(dsRemoveMonitor);

         bool ret = base.SaveChanges();
         if(ret)
         {
            dsRemoveMonitor.Clear();
         }
         return ret;
      }
   }
}