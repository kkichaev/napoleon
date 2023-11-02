using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Ribbon;
using System.Windows.Input;

namespace Ads2017
{
   public partial class UsersWindow : RibbonWindow, Update.IDataLoadProcess
   {
      private Dictionary<string, Agent> updateAgents = new Dictionary<string, Agent>();
      private Dictionary<string, Agent> deleteAgents = new Dictionary<string, Agent>();
      private Dictionary<string, DivisionManager> updateManagers = new Dictionary<string, DivisionManager>();
      private Dictionary<string, DivisionManager> deleteManagers = new Dictionary<string, DivisionManager>();

      public UsersWindow()
      {
         InitializeComponent();
         agents.Items.SortDescriptions.Add(new SortDescription("Name", ListSortDirection.Ascending));
         ObservableCollection<Agent> agentCollection = new ObservableCollection<Agent>();
         agents.ItemsSource = agentCollection;

         managers.Items.SortDescriptions.Add(new SortDescription("Name", ListSortDirection.Ascending));
         managers.ItemsSource = new ObservableCollection<DivisionManager>();
      }

      void OnChecked(object sender, RoutedEventArgs e)
      {
         switch(tabcontrol.SelectedIndex)
         {
            case 0:
               agents.CommitEdit(DataGridEditingUnit.Row, true);
               break;
            case 1:
               managers.CommitEdit(DataGridEditingUnit.Row, true);
               break;
         }
      }

      private void SaveExecuted(object sender, ExecutedRoutedEventArgs e)
      {
         bool result = Save();

         if (result)
         {
            updateAgents.Clear();
            deleteAgents.Clear();
            updateManagers.Clear();
            deleteManagers.Clear();
            CommandManager.InvalidateRequerySuggested();
            StdDialog.SavedGood(this);
         }
         else
            StdDialog.UpdateErrMsg(this);
      }

      private bool Save()
      {
         agents.CommitEdit(DataGridEditingUnit.Row, true);
         managers.CommitEdit(DataGridEditingUnit.Row, true);

         UpdateCollection write = new UpdateCollection();
         write.Add(Agent.OBJECT_NAME, AgentFiltrEmptyRecord());
         write.Add(DivisionManager.OBJECT_NAME, ManagerFiltrEmptyRecord());

         Division d = GetUpdateDivision();

         if (d != null)
            write.Add(Division.OBJECT_NAME, d);

         UpdateCollection remove = new UpdateCollection();
         remove.Add(Agent.OBJECT_NAME, deleteAgents.Values);
         remove.Add(DivisionManager.OBJECT_NAME, deleteManagers.Values);

         return Update.WriteObjects(write, remove, true); ;
      }

      private List<Agent> AgentFiltrEmptyRecord()
      {
         List<Agent> toSave = new List<Agent>();
         foreach (Agent a in updateAgents.Values)
            if (a.name.Trim().Length > 0)
               toSave.Add(a);

         return toSave;
      }

      private List<DivisionManager> ManagerFiltrEmptyRecord()
      {
         List<DivisionManager> toSave = new List<DivisionManager>();
         foreach (DivisionManager m in updateManagers.Values)
            if (m.login.Trim().Length > 0)
               toSave.Add(m);

         return toSave;
      }

      private Division GetUpdateDivision()
      {
         Division result = null;
         IEnumerator ie = Update.GetList(Division.OBJECT_NAME).GetEnumerator();

         if (ie.MoveNext())
         {
            if (ie.Current is Division d)
            {
               HashSet<string> hash = new HashSet<string>();

               foreach (Division.DivisionAgent ds in d.agents)
                  if (!hash.Contains(ds.id))
                     hash.Add(ds.id);

               foreach (Agent a in updateAgents.Values)
               {
                  if (!hash.Contains(a.id))
                     d.agents.Add(new Division.DivisionAgent { id = a.id });
               }

               foreach (Agent a in deleteAgents.Values)
               {
                  foreach (Division.DivisionAgent da in d.agents)
                     if (da.id.Equals(a.id))
                     {
                        d.agents.Remove(da);
                        break;
                     }
               }

               result = d;
            }
         }

         return result;
      }

      private void DeleteExecuted(object sender, ExecutedRoutedEventArgs e)
      {
         switch (tabcontrol.SelectedIndex)
         {
            case 0:
               DeleteAgent();
               break;
            case 1:
               DeleteManager();
               break;
         }
      }

      private void DeleteManager()
      {
         if (managers.SelectedItem is DivisionManager m && StdDialog.AskToDel(this))
         {
            ((ObservableCollection<DivisionManager>)managers.ItemsSource).Remove(m);
            deleteManagers[m.id] = m;

            if (updateManagers.ContainsKey(m.id))
               updateManagers.Remove(m.id);
         }
      }

      private void DeleteAgent()
      {
         if (agents.SelectedItem is Agent a && StdDialog.AskToDel(this))
         {
            ((ObservableCollection<Agent>)agents.ItemsSource).Remove(a);
            deleteAgents[a.id] = a;

            if (updateAgents.ContainsKey(a.id))
               updateAgents.Remove(a.id);
         }
      }

      private void RefreshExecuted(object sender, ExecutedRoutedEventArgs e)
      {
         Refresh();
      }

      private void Refresh()
      {
         Update.QueryList upd = new Update.QueryList();
         upd.Add(Agent.OBJECT_NAME);
         upd.Add(DivisionManager.OBJECT_NAME);

         Update.StdDataRefresh(upd, this, true);
      }

      private void RibbonWindow_Loaded(object sender, RoutedEventArgs e)
      {
         Refresh();
      }

      public void DoLoadData(Update.UpdateResult data)
      {
         ObservableCollection<Agent> list = (ObservableCollection<Agent>)agents.ItemsSource;
         list.Clear();

         foreach (Agent a in data.GetList<Agent>(Agent.OBJECT_NAME))
            list.Add(a);

         agents.ItemsSource = list;

         ObservableCollection<DivisionManager> list2 = (ObservableCollection<DivisionManager>)managers.ItemsSource;
         list2.Clear();

         foreach (DivisionManager d in data.GetList<DivisionManager>(DivisionManager.OBJECT_NAME))
            list2.Add(d);

         managers.ItemsSource = list2;

         updateAgents.Clear();
         deleteAgents.Clear();
         updateManagers.Clear();
         deleteManagers.Clear();

         CommandManager.InvalidateRequerySuggested();
      }

      public UIElement[] GetRefreshControls()
      {
         return new UIElement[] { btnRefresh, btnSave };
      }

      private void Grid_CellEditEnding(object sender, DataGridCellEditEndingEventArgs e)
      {
         switch (tabcontrol.SelectedIndex)
         {
            case 0:
               EndEndingAgent(e);
               break;
            case 1:
               EndEndingManager(e);
               break;
         }
      }

      private void EndEndingManager(DataGridCellEditEndingEventArgs e)
      {
         if (e.Row.Item is DivisionManager m && e.EditAction == DataGridEditAction.Commit)
         {
            if (m.id.Trim().Length == 0)
            {
               m.id = DivisionManager.GenId();
               m.division = 1;
            }

            updateManagers[m.login] = m;
            CommandManager.InvalidateRequerySuggested();
         }
      }

      private void EndEndingAgent(DataGridCellEditEndingEventArgs e)
      {
         if (e.Row.Item is Agent a && e.EditAction == DataGridEditAction.Commit)
         {
            if (a.id.Trim().Length == 0)
               a.id = Agent.GenId();

            updateAgents[a.id] = a;
            CommandManager.InvalidateRequerySuggested();
         }
      }

      private void CanSaveExecute(object sender, CanExecuteRoutedEventArgs e)
      {
         e.CanExecute = HasUnsavedData();
      }

      private bool HasUnsavedData()
      {
         return updateAgents.Count > 0 || deleteAgents.Count > 0 || updateManagers.Count > 0 || deleteManagers.Count > 0;
      }

      private void RibbonWindow_Closing(object sender, CancelEventArgs e)
      {
         if (HasUnsavedData())
         {
                MessageBoxResult res = StdDialog.AskToSave(this);
                if (res == MessageBoxResult.Cancel)
                {
                    e.Cancel = true;
                    return;
                }
                if(res == MessageBoxResult.Yes)
                    Save();
            }

            e.Cancel = false;
      }

      private void AddExecuted(object sender, ExecutedRoutedEventArgs e)
      {
         switch (tabcontrol.SelectedIndex)
         {
            case 0:
               AddAgent();
               break;
            case 1:
               AddManager();
               break;
         }
      }

      private void AddManager()
      {
         ObservableCollection<DivisionManager> list = (ObservableCollection<DivisionManager>)managers.ItemsSource;
         DivisionManager m = new DivisionManager() { id = DivisionManager.GenId(), division=1 };
         list.Add(m);

         managers.Focus();
         managers.CurrentCell = new System.Windows.Controls.DataGridCellInfo(managers.Items[0], managers.Columns[0]);
         managers.BeginEdit();

         updateManagers[m.id] = m;
         CommandManager.InvalidateRequerySuggested();
      }

      private void AddAgent()
      {
         ObservableCollection<Agent> list = (ObservableCollection<Agent>)agents.ItemsSource;
         Agent a = new Agent() { id = Agent.GenId() };
         list.Add(a);

         agents.Focus();
         agents.CurrentCell = new System.Windows.Controls.DataGridCellInfo(agents.Items[0], agents.Columns[0]);
         agents.BeginEdit();

         updateAgents[a.id] = a;
         CommandManager.InvalidateRequerySuggested();
      }
   }
}
