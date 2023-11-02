using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Ribbon;
using System.Windows.Data;
using System.Windows.Input;
using System.Windows.Threading;

namespace Napoleon
{
    public partial class MainWindow : RibbonWindow, Update.IDataLoadProcess, IWindowListener
    {
        private ObservableCollection<MainWindowData> data = new ObservableCollection<MainWindowData>();
        private Dictionary<string, Window> windows = new Dictionary<string, Window>();
        private DispatcherTimer timer;

        public static DependencyProperty ResDlvSumProperty = DependencyProperty.Register(
            "ResDlvSum", typeof(double), typeof(MainWindow));

        public static DependencyProperty ResDlvWeightProperty = DependencyProperty.Register(
            "ResDlvWeight", typeof(double), typeof(MainWindow));

        public static DependencyProperty ResOrderSumProperty = DependencyProperty.Register(
           "ResOrderSum", typeof(double), typeof(MainWindow));

        public static DependencyProperty ResOrderWeightProperty = DependencyProperty.Register(
            "ResOrderWeight", typeof(double), typeof(MainWindow));

        public double ResDlvSum
        {
            get { return (double)GetValue(ResDlvSumProperty); }
            set { SetValue(ResDlvSumProperty, value); }
        }

        public double ResDlvWeight
        {
            get { return (double)GetValue(ResDlvWeightProperty); }
            set { SetValue(ResDlvWeightProperty, value); }
        }

        public double ResOrderSum
        {
            get { return (double)GetValue(ResOrderSumProperty); }
            set { SetValue(ResOrderSumProperty, value); }
        }

        public double ResOrderWeight
        {
            get { return (double)GetValue(ResOrderWeightProperty); }
            set { SetValue(ResOrderWeightProperty, value); }
        }

        public MainWindow()
        {
            InitializeComponent();
            grid.ItemsSource = data;
            datePicker.SelectedDate = DateTime.Now;

            timer = new DispatcherTimer
            {
                Interval = new TimeSpan(0, 0, 0, 0, 500),
            };

            timer.Tick += Timer_Tick;
            ((MenuItem)grid.ContextMenu.Items[0]).DataContext = new ReqReturnCmd(this);
        }

        private void RibbonWindow_Loaded(object sender, RoutedEventArgs e)
        {
#if DEBUG
#else
         if (Properties.Settings.Default.Login.Length > 0)
             Refresh();
#endif
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
                ObservableCollection<MainWindowData> filterData = new ObservableCollection<MainWindowData>();
                data.ForEachFilter<MainWindowData>((i) => filterData.Add(i), (i) => SearchCondition(str, i));
                grid.ItemsSource = filterData;
            }
        }

        private bool SearchCondition(string str, MainWindowData i)
        {
            str = str.ToUpper();
            return i.OrgName.ToUpper().Contains(str) || i.OrgAddress.ToUpper().Contains(str);
        }

        public void RefreshResult()
        {
            double lds = 0.0;
            double ldw = 0.0;
            double los = 0.0;
            double low = 0.0;

            foreach (MainWindowData i in data)
            {
                lds += i.LastDlvSumD;
                ldw += i.LastDlvWeightD;
                los += i.LastOrderSumD;
                low += i.LastOrderWeightD;
            }

            ResDlvSum = lds;
            ResDlvWeight = ldw;
            ResOrderSum = los;
            ResOrderWeight = low;
        }

        public void DoLoadData(Update.UpdateResult res)
        {
            data.Clear();
            DateTime date = datePicker.SelectedDate ?? DateTime.Now;

            new MainWindowDataHelper().CreateData(date, res).ForEach((i) =>
            {
                data.Add(i);
            });

            foreach (Agent a in res.GetList<Agent>(Agent.OBJECT_NAME))
                if (a.login == Properties.Settings.Default.Login)
                    Title = a.Name;



            List<Firms> firms = new List<Firms>();
            firms.AddRange(res.GetList<Firms>(Firms.OBJECT_NAME));
            firms.Sort();
            firms.Insert(0, new Firms());
            cbFactory.ItemsSource = firms;

            RefreshResult();
            grid.ItemsSource = data;
        }

        private void RefreshExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            btnRefresh.Focus();
            Refresh();
        }

        private void SettingExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            new Setting().Show();
        }

        public void Refresh()
        {
            DateTime s = (datePicker.SelectedDate ?? DateTime.Now).Date;
            DateTime f = s.AddDays(1);

            Update.QueryList upd = new Update.QueryList();
            upd.Add(Config.OBJECT_NAME);
            upd.Add(Agent.OBJECT_NAME);
            upd.Add(AgentOrgs.OBJECT_NAME);
            upd.Add(Org.COMMON_OBJECT_NAME);
            upd.Add(OrgFolder.OBJECT_NAME);
            upd.Add(Folder.OBJECT_NAME);
            upd.Add(Price.OBJECT_NAME, "SetQtyFilter(False)");
            upd.Add(Firms.OBJECT_NAME);
            upd.Add(PhoneAction.OBJECT_NAME, string.Format("\"date\">=ToDate('{0:dd/MM/yyyy}')", s));
            //upd.Add(LastRemnant.OBJECT_NAME);
            upd.Add(OrgMatrix.OBJECT_NAME);
            upd.Add(Brands.OBJECT_NAME);
            upd.Add(PlanNew.OBJECT_NAME);
            upd.Add(ServoluxSheduleItem.OBJECT_NAME);
            upd.Add(RejectCause.OBJECT_NAME);

            upd.Add(IDMTX.IDMTX_OBJ_NAME);
            upd.Add(ObjectMatrix.IDOMTX_OBJ_NAME);
            upd.Add(MMLFeatures.OBJECT_NAME);
            upd.Add(OrgDogovor.OBJECT_NAME);
            upd.Add(TradeAction.OBJECT_NAME);

            Update.StdDataRefresh(upd, this);
        }

        class ReqReturnCmd : ICommand
        {
            MainWindow owner;

            public ReqReturnCmd(MainWindow owner) { this.owner = owner; }

            public event EventHandler CanExecuteChanged;

            public bool CanExecute(object parameter) { return true; }
            public void Execute(object parameter) { owner.ReqReturn(); }
        }

        public void ReqReturn()
        {
            MainWindowData org = grid.CurrentItem as MainWindowData;
            if (org != null)
            {
                ReturnPriceWindow w = new ReturnPriceWindow(org.OrgID, null);
                w.Show();
            }
        }

        private void Row_DoubleClick(object sender, MouseButtonEventArgs e)
        {
            DataGridRow row = sender as DataGridRow;

            if (((DataGridRow)sender).DataContext is MainWindowData d)
            {
                string key = String.Format("{0}\t{1:dd/MM/yyyy}", d.OrgID, datePicker.SelectedDate ?? DateTime.Now);

                if (!windows.ContainsKey(key))
                {
                    windows[key] = new PhoneActionWindow(d)
                    {
                        WindowListener = this
                    };

                    windows[key].Show();
                }
                else
                {
                    Window w = windows[key];

                    if (w.WindowState == WindowState.Minimized)
                        w.WindowState = WindowState.Normal;

                    windows[key].Activate();
                }
            }
        }

        void IWindowListener.Closed(Window window, bool apply)
        {
            if (window is PhoneActionWindow paw)
            {
                RemoveStoredWindow(paw);

                if (apply)
                {
                    UpdateData(paw);
                }
            }
            else if (window is PriceWindow pw && apply)
            {
                UpdateData(pw);
            }

            if (apply)
                grid.Items.Refresh();

            RefreshResult();
        }

        private void UpdateData(PriceWindow pw)
        {
            //data.ForEachFilter<MainWindowData>((d) =>
            //{
            //    d.LastOrderSumD = pw.Document.Sum;
            //    d.LastOrderSum = d.LastOrderSumD.ToString();
            //    d.LastOrderWeightD = pw.Document.Weight;
            //    d.LastOrderWeight = d.LastOrderWeightD.ToString();

            //}, (d) => d.OrgID == pw.OrgID);

            List<OrgFolderItem> items = new OrgFolderHelper().GetAgentRoute(datePicker.SelectedDate ?? DateTime.Now);
            string filter = MainWindowDataHelper.GetDocFilter(items);
            Update.UpdateResult dlvRes = null;
            if (filter.Length > 0)
            {
                Update.QueryList upd = new Update.QueryList();
                upd.Add(LastOrder.OBJECT_NAME, filter);
                dlvRes = Update.UpdateWait(upd);

                dlvRes.GetList<LastOrder>(LastOrder.OBJECT_NAME).ForEach((p) =>
                {
                    data.ForEachFilter<MainWindowData>((d) => d.orders[p.firmCode] = p, (d) => d.OrgID == p.id);
                });
            }


            UpdateDataForFabric();
        }

        private void UpdateData(PhoneActionWindow paw)
        {
            data.ForEachFilter<MainWindowData>((d) =>
            {
                d.Remark = paw.Remark;
                d.Text = paw.Text;
                d.CellTime = string.Format("{0:HH:mm:ss}", paw.DateDocument);
            }, (d) => d.OrgID == paw.OrgID);
        }

        private void RemoveStoredWindow(PhoneActionWindow paw)
        {
            string key = String.Format("{0}\t{1:dd/MM/yyyy}", paw.OrgID, paw.DateDocument);

            if (windows.ContainsKey(key))
                windows.Remove(key);
        }

        private List<MainWindowData> FindData(string orgid)
        {
            List<MainWindowData> result = new List<MainWindowData>();

            foreach (MainWindowData d in data)
            {
                if (d.OrgID == orgid)
                {
                    result.Add(d);
                }
            }

            return result;
        }

        private void DatePicker_SelectedDateChanged(object sender, SelectionChangedEventArgs e)
        {
            data.Clear();
        }

        private bool clearing = false;

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

        private void BtnOrderJournal_Click(object sender, RoutedEventArgs e)
        {
            new OrderJournalWindow().Show();
        }

        private void BtnOrgWindow_Click(object sender, RoutedEventArgs e)
        {
            new OrgWindow().Show();
        }

        private void RetCauseExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            new RejectCauseEditor().Show();
        }

        void UpdateDataForFabric()
        {
            Firms f = cbFactory.SelectedItem as Firms;
            if (f == null)
               return;
            foreach (MainWindowData d in data)
            {
                d.SetFirm(f.id);
            }
            grid.Items.Refresh();
        }

        private void cbFactory_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            UpdateDataForFabric();
        }

        private void btnRetDocsJournal_Click(object sender, RoutedEventArgs e)
        {
            new RetDocsJournalWindow().Show();
        }
    }
}
