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
    public partial class RetDocsJournalWindow : RibbonWindow, Update.IDataLoadProcess, IWindowListener
    {
        private ObservableCollection<ReturnRequest> data = new ObservableCollection<ReturnRequest>();
        private DispatcherTimer timer;
        private bool clearing = false;

        ObservableCollection<CheckedFirm> firms = new ObservableCollection<CheckedFirm>();

        public RetDocsJournalWindow()
        {
            InitializeComponent();
            datePicker.SelectedDate = DateTime.Now;
            grid.ItemsSource = data;

            timer = new DispatcherTimer
            {
                Interval = new TimeSpan(0, 0, 0, 0, 500),
            };

            timer.Tick += Timer_Tick;
            ((MenuItem)grid.ContextMenu.Items[0]).DataContext = new DelCmd(this);

            WinSizeHelper.Resotre(this, grid);
        }

        protected override void OnClosing(CancelEventArgs e)
        {
            WinSizeHelper.Save(this, grid);
            base.OnClosing(e);
        }

        private void Timer_Tick(object sender, EventArgs e)
        {
            timer.Stop();
            DoSearch(tbSearch.Text);
        }

        private void DoSearch(string str)
        {
            if (str.Length > 0)
            {
                str = str.ToUpper();
                ObservableCollection<ReturnRequest> filterData = new ObservableCollection<ReturnRequest>();

                foreach (ReturnRequest i in data)
                    if (SearchCondition(str, i))
                        filterData.Add(i);

                grid.ItemsSource = filterData;
            }
        }

        private bool SearchCondition(string str, ReturnRequest o)
        {
            return o.OrgName.ToUpper().Contains(str) || o.OrgAddr.ToUpper().Contains(str);
        }

        private void BtnSearchClear_Click(object sender, RoutedEventArgs e)
        {
            ClearSearch();
        }

        private void DatePicker_SelectedDateChanged(object sender, SelectionChangedEventArgs e)
        {
            data.Clear();
        }

        private void Row_DoubleClick(object sender, MouseButtonEventArgs e)
        {
            DataGridRow row = sender as DataGridRow;

            if (((DataGridRow)sender).DataContext is ReturnRequest d)
            {
                ReturnPriceWindow p = new ReturnPriceWindow(d.id, d);
                p.Show();
            }
        }

        private void TbSearch_TextChanged(object sender, TextChangedEventArgs e)
        {
            timer.Stop();

            if (tbSearch.Text.Length > 0)
                timer.Start();
            else if (!clearing)
                ClearSearch();
        }

        private void RibbonWindow_Loaded(object sender, RoutedEventArgs e)
        {
            Refresh();
        }

        private void Refresh()
        {
            DateTime s = (datePicker.SelectedDate ?? DateTime.Now).Date;
            DateTime f = s.AddDays(1);

            Update.QueryList upd = new Update.QueryList();
            string where = string.Format("\"created\" >= ToDate('{0:dd/MM/yyyy}') and \"created\" < ToDate('{1:dd/MM/yyyy}')", s, f);
            upd.Add(ReturnRequest.OBJECT_NAME, where);
            upd.Add(OrderProceeded.OBJECT_NAME, where);
            upd.Add(Firms.OBJECT_NAME);

            Update.StdDataRefresh(upd, this);
        }

        private void RefreshExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            Refresh();
        }

        class DelCmd : ICommand
        {
            RetDocsJournalWindow owner;

            public DelCmd(RetDocsJournalWindow owner) { this.owner = owner; }

            public event EventHandler CanExecuteChanged;

            public bool CanExecute(object parameter) { return true; }
            public void Execute(object parameter) { owner.DelCurRow(); }
        }

        public void DelCurRow()
        {
            ReturnRequest delOrder = grid.CurrentItem as ReturnRequest;
            if (delOrder != null && MessageBox.Show("Удалить документ?", "Вопрос", MessageBoxButton.YesNo, MessageBoxImage.Question) == MessageBoxResult.Yes)
            {
                UpdateCollection rmv = new UpdateCollection();
                rmv.Add(ReturnRequest.OBJECT_NAME).Add(delOrder);
                if (Update.WriteObjects(null, rmv))
                {
                    data.Remove(delOrder);
                    if (grid.ItemsSource != data)
                        ((ObservableCollection<ReturnRequest>)grid.ItemsSource).Remove(delOrder);
                }
            }
        }

        //public ICommand DeleteOrder
        //{
        //    get { return new DelCmd(this); }
        //}

        public void DoLoadData(Update.UpdateResult res)
        {
            data.Clear();
            Dictionary<OPKey, bool> inKIS = new Dictionary<OPKey, bool>();
            res.GetList<OrderProceeded>(OrderProceeded.OBJECT_NAME).ForEach((i) =>
            {
                if (i.type == "ReturnRequest" )
                    inKIS.Add(new OPKey(i), true);
            });

            res.GetList<ReturnRequest>(ReturnRequest.OBJECT_NAME).ForEach((i) =>
            {
                i.UPP = inKIS.ContainsKey(new OPKey(i));
                data.Add(i);
            });

            firms.Clear();
            List<Firms> src = res.GetList<Firms>(Firms.OBJECT_NAME);
            src.Sort();
            src.ForEach(x => firms.Add(new CheckedFirm(x, FirmChecked)));

            lbFirms.ItemsSource = firms;
        }

        void FirmChecked(object firm, EventArgs arg)
        {
            Dictionary<string, bool> avail = new Dictionary<string, bool>();
            foreach(CheckedFirm cf in lbFirms.ItemsSource as ObservableCollection<CheckedFirm>)
            {
                if (cf.IsChecked)
                    avail[cf.Firm.id] = true;
            }
            List<ReturnRequest> dest = new List<ReturnRequest>();
            foreach(ReturnRequest o in data)
            {
                if (avail.ContainsKey(o.firmCode))
                    dest.Add(o);
            }
            grid.ItemsSource = dest;
        }

        private void ClearSearch()
        {
            clearing = true;
            tbSearch.Text = string.Empty;
            grid.ItemsSource = data;
            clearing = false;
        }

        void IWindowListener.Closed(Window window, bool apply)
        {
            if (apply && window is PriceWindow pw)
            {
                Refresh();
            }
        }

    }
}
