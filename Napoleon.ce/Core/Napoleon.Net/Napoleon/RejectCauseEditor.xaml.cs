using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Ribbon;
using System.Windows.Input;
using System.Windows.Threading;

namespace Napoleon
{
    public partial class RejectCauseEditor : RibbonWindow, Update.IDataLoadProcess
    {
        private ObservableCollection<RejectCause> data = new ObservableCollection<RejectCause>();
        private Dictionary<string, RejectCause> update = new Dictionary<string, RejectCause>();
        private Dictionary<string, RejectCause> delete = new Dictionary<string, RejectCause>();
        private DispatcherTimer timer;
        private bool clearing = false;
        public IWindowListener WindowListener { get; set; }

        public RejectCauseEditor()
        {
            InitializeComponent();
            grid.ItemsSource = data;

            timer = new DispatcherTimer
            {
                Interval = new TimeSpan(0, 0, 0, 0, 500),
            };

            timer.Tick += Timer_Tick;

            Update.GetStoredList<RejectCause>(RejectCause.OBJECT_NAME).ForEach((i)=>data.Add(i)); 
        }

        private void Timer_Tick(object sender, EventArgs e)
        {
            timer.Stop();
            DoSearch(tbSearch.Text);
        }

        private void DoSearch(string str)
        {
            str = str.ToUpper();
            ObservableCollection<RejectCause> filterData = new ObservableCollection<RejectCause>();

            foreach (RejectCause i in data)
                if (SearchCondition(str, i))
                    filterData.Add(i);

            grid.ItemsSource = filterData;
        }

        private bool SearchCondition(string str, RejectCause i)
        {
            return i.Name.ToUpper().Contains(str);
        }

        private void RefreshExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            Update.QueryList upd = new Update.QueryList();
            upd.Add(RejectCause.OBJECT_NAME);

            Update.StdDataRefresh(upd, this);
        }

        private bool Save()
        {
            List<RejectCause> toSave = FiltrEmptyRecord();


            Dictionary<string, RejectCause> stored = Update.GetStoredDictionary<RejectCause>(RejectCause.OBJECT_NAME);
            toSave.ForEach((i) =>
            {
                if (!stored.ContainsKey(i.id))
                    Update.PutStoredDict(i);
            });

            UpdateCollection write = new UpdateCollection();
            write.Add(RejectCause.OBJECT_NAME, toSave);
            UpdateCollection remove = new UpdateCollection();
            remove.Add(RejectCause.OBJECT_NAME, delete.Values);

            return Update.WriteObjects(write, remove); ;
        }

        private List<RejectCause> FiltrEmptyRecord()
        {
            List<RejectCause> toSave = new List<RejectCause>();
            foreach (RejectCause a in update.Values)
                if (a.name.Trim().Length > 0)
                    toSave.Add(a);
            return toSave;
        }

        private void TbSearch_TextChanged(object sender, TextChangedEventArgs e)
        {
            timer.Stop();

            if (tbSearch.Text.Length > 0)
                timer.Start();
            else if (!clearing)
                ClearSearch();
        }

        private void ClearSearch()
        {
            clearing = true;
            tbSearch.Text = string.Empty;
            grid.ItemsSource = data;
            clearing = false;
        }

        private void BtnSearchClear_Click(object sender, RoutedEventArgs e)
        {
            ClearSearch();
        }

        public void DoLoadData(Update.UpdateResult res)
        {
            data.Clear();
            res.GetList<RejectCause>(RejectCause.OBJECT_NAME).ForEach((i) => data.Add(i));
        }

        private void DeleteExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            if (grid.SelectedItem is RejectCause a && StdDialog.AskToDel(this))
            {
                ((ObservableCollection<RejectCause>)grid.ItemsSource).Remove(a);
                delete[a.id] = a;

                if (update.ContainsKey(a.id))
                    update.Remove(a.id);
            }
        }

        private void Grid_CellEditEnding(object sender, DataGridCellEditEndingEventArgs e)
        {
            if (e.Row.Item is RejectCause a && e.EditAction == DataGridEditAction.Commit)
            {
                if (a.id.Trim().Length == 0)
                    a.id = RejectCause.GenId();

                update[a.id] = a;
                CommandManager.InvalidateRequerySuggested();
            }
        }

        private void CanSaveExecute(object sender, CanExecuteRoutedEventArgs e)
        {
            e.CanExecute = HasUnsavedData();
        }

        private bool HasUnsavedData()
        {
            return update.Count > 0 || delete.Count > 0;
        }

        private void RibbonWindow_Closing(object sender, CancelEventArgs e)
        {
            if (HasUnsavedData() && StdDialog.AskToSave(this))
                Save();

            e.Cancel = false;

            if (WindowListener != null)
                WindowListener.Closed(this, true);
        }

        private void AddExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            ObservableCollection<RejectCause> list = (ObservableCollection<RejectCause>)grid.ItemsSource;
            RejectCause a = new RejectCause();
            a.id = RejectCause.GenId();
            list.Add(a);

            grid.Focus();
            grid.CurrentCell = new System.Windows.Controls.DataGridCellInfo(grid.Items[0], grid.Columns[0]);
            grid.BeginEdit();

            update[a.id] = a;
            CommandManager.InvalidateRequerySuggested();
        }

        private void SaveExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            bool result = Save();

            if (result)
            {
                update.Clear();
                delete.Clear();
                CommandManager.InvalidateRequerySuggested();
                StdDialog.SavedGood(this);
            }
            else
                StdDialog.UpdateErrMsg(this);
        }
    }
}
