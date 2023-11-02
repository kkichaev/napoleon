using System;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Text.RegularExpressions;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Ribbon;
using System.Windows.Input;
using System.Windows.Threading;

namespace Napoleon
{
    public partial class OrgWindow : RibbonWindow, Update.IDataLoadProcess
    {
        private ObservableCollection<Org> data = new ObservableCollection<Org>();
        private DispatcherTimer timer;

        public OrgWindow()
        {
            InitializeComponent();
            grid.ItemsSource = data;
            timer = new DispatcherTimer
            {
                Interval = new TimeSpan(0, 0, 0, 0, 500),
            };

            timer.Tick += Timer_Tick;
            WinSizeHelper.Resotre(this, grid);

            ((MenuItem)grid.ContextMenu.Items[0]).DataContext = new ReqReturnCmd(this);
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
                string re = "";
                string[] parts = str.Split(' ');
                foreach(string p in parts)
                {
                    if (re.Length > 0)
                        re += ".+";
                    re += p;
                }

                Regex rege = new Regex(re, RegexOptions.IgnoreCase);

                ObservableCollection<Org> filterData = new ObservableCollection<Org>();
                data.ForEachFilter<Org>((i) => filterData.Add(i), (i) => SearchCondition(rege, i));
                grid.ItemsSource = filterData;
            }
        }

        private bool SearchCondition(Regex rege, Org i)
        {
            string na = i.Name + " " + i.Address;
            return rege.IsMatch(na);
            //str = str.ToUpper();
            //return i.Name.ToUpper().Contains(str) || i.Address.ToUpper().Contains(str);
        }

        private void RefreshExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            Update.QueryList upd = new Update.QueryList();
            upd.Add(Org.COMMON_OBJECT_NAME);

            Update.StdDataRefresh(upd, this);
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

        class ReqReturnCmd : ICommand
        {
            OrgWindow owner;

            public ReqReturnCmd(OrgWindow owner) { this.owner = owner; }

            public event EventHandler CanExecuteChanged;

            public bool CanExecute(object parameter) { return true; }
            public void Execute(object parameter) { owner.ReqReturn(); }
        }

        public void ReqReturn()
        {
            Org org = grid.CurrentItem as Org;
            if(org != null)
            {
                ReturnPriceWindow w = new ReturnPriceWindow(org.id, null);
                w.Show();
            }
        }


        private void BtnSearchClear_Click(object sender, RoutedEventArgs e)
        {
            ClearSearch();
        }

        private void Row_DoubleClick(object sender, MouseButtonEventArgs e)
        {
            DataGridRow row = sender as DataGridRow;

            if (((DataGridRow)sender).DataContext is Org o)
            {
                long linked = DateTime.Now.Ticks / 10000;
                DateTime dlvDate = DateTime.Now.AddDays(1);

                foreach (PhoneAction p in Update.GetStoredList<PhoneAction>(PhoneAction.OBJECT_NAME))
                {
                    if (p.id == o.id)
                    {
                        linked = p.created.Ticks / 10000;
                        dlvDate = p.date;
                        break;
                    }
                }
                PriceWindow pw = new PriceWindow(o.id, linked, dlvDate);
                pw.Show();
            }
        }

        public void DoLoadData(Update.UpdateResult res)
        {
            data.Clear();
            res.GetList<Org>(Org.COMMON_OBJECT_NAME).ForEach((i) => data.Add(i));
        }

        private void RibbonWindow_Loaded(object sender, RoutedEventArgs e)
        {
            Update.GetStoredList<Org>(Org.COMMON_OBJECT_NAME).ForEach((i) => data.Add(i));
        }
    }
}
