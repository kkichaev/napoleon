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
    public partial class OrderJournalWindow : RibbonWindow, Update.IDataLoadProcess, IWindowListener
    {
        private ObservableCollection<Order> data = new ObservableCollection<Order>();
        private DispatcherTimer timer;
        private bool clearing = false;

        ObservableCollection<CheckedFirm> firms = new ObservableCollection<CheckedFirm>();

        public OrderJournalWindow()
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
                ObservableCollection<Order> filterData = new ObservableCollection<Order>();

                foreach (Order i in data)
                    if (SearchCondition(str, i))
                        filterData.Add(i);

                grid.ItemsSource = filterData;
            }
        }

        private bool SearchCondition(string str, Order o)
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

            if (((DataGridRow)sender).DataContext is Order d)
            {

                PriceWindow p = new PriceWindow(d.id, d.linked, d.dlvDate)
                {
                    WindowListener = this
                };

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
            upd.Add(Order.OBJECT_NAME, where);
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
            OrderJournalWindow owner;

            public DelCmd(OrderJournalWindow owner) { this.owner = owner; }

            public event EventHandler CanExecuteChanged;

            public bool CanExecute(object parameter) { return true; }
            public void Execute(object parameter) { owner.DelCurRow(); }
        }

        public void DelCurRow()
        {
            Order delOrder = grid.CurrentItem as Order;
            if (delOrder != null && MessageBox.Show("Удалить заказ?", "Вопрос", MessageBoxButton.YesNo, MessageBoxImage.Question) == MessageBoxResult.Yes)
            {
                UpdateCollection rmv = new UpdateCollection();
                rmv.Add(Order.OBJECT_NAME).Add(delOrder);
                if (Update.WriteObjects(null, rmv))
                {
                    data.Remove(delOrder);
                    if (grid.ItemsSource != data)
                        ((ObservableCollection<Order>)grid.ItemsSource).Remove(delOrder);
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
                if (i.type == "" || i.type == "Order")
                    inKIS.Add(new OPKey(i), true);
            });

            res.GetList<Order>(Order.OBJECT_NAME).ForEach((i) =>
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
            List<Order> dest = new List<Order>();
            foreach(Order o in data)
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

    class CheckedFirm
    {
        bool __isCheck;
        Firms firm;
        EventHandler checkChanged;

        public CheckedFirm(Firms firm, EventHandler checkChanged)
        {
            this.firm = firm;
            __isCheck = true;
            this.checkChanged = checkChanged;
        }

        public bool IsChecked {
            get => __isCheck;
            set
            {
                if (value != __isCheck)
                {
                    __isCheck = value;
                    checkChanged?.Invoke(this, EventArgs.Empty);
                }
            }
        }
        public string Name { get { return firm.Name; } }
        public Firms Firm { get => firm; }
    }

    class OPKey : IEquatable<OPKey>
    {
        string userid;
        DateTime created;

        public OPKey(Order doc)
        {
            userid = doc.userid;
            created = doc.created;
        }

        public OPKey(OrderProceeded doc)
        {
            userid = doc.userid;
            created = doc.created;
        }

        public OPKey(ReturnRequest doc)
        {
            userid = doc.userid;
            created = doc.created;
        }

        public bool Equals(OPKey other)
        {
            if (other == null)
                return false;
            return userid.Equals(other.userid) && created.Equals(other.created);
        }

        public override int GetHashCode()
        {
            return userid.GetHashCode() ^ created.GetHashCode();
        }
    }

}
