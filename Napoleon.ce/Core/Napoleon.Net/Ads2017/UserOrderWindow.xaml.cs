using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Ribbon;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace Ads2017
{
    public partial class UserOrderWindow : RibbonWindow, Update.IDataLoadProcess
    {
        private Collection<UserOrder> allData = new ObservableCollection<UserOrder>();

        public UserOrderWindow()
        {
            InitializeComponent();

            start.SelectedDate = DateTime.Now;
            finish.SelectedDate = DateTime.Now;
        }

        public void DoLoadData(Update.UpdateResult data)
        {
            allData.Clear();

            Update.UpdateResult res = (Update.UpdateResult)data;

            foreach (UserOrder o in res.GetList<UserOrder>(UserOrder.OBJECT_NAME))
            {
                allData.Add(o);
            }

            panel.ItemsSource = allData;
        }

        public UIElement[] GetRefreshControls()
        {
            return new UIElement[] { btnRefresh };
        }

        private void RefreshExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            Refresh();
        }

        private void Refresh()
        {
            DateTime s = start.SelectedDate ?? DateTime.Now;
            DateTime f = finish.SelectedDate ?? DateTime.Now;

            string where = string.Format(
               "\"created\" >= ToDate('{0:dd/MM/yyyy}') and \"created\" < ToDate('{1:dd/MM/yyyy}')", s.Date,
               f.Date.AddDays(1));

            Update.QueryList upd = new Update.QueryList();
            upd.Add(UserOrder.OBJECT_NAME, String.Format("{0};{1};{2}",s.Date, f.Date.AddDays(1),ManagerHelper.Instance.AgentsWhere(false)));

            Update.StdDataRefresh(upd, this);
        }

        private void RibbonWindow_Loaded(object sender, RoutedEventArgs e)
        {
            Refresh();
        }
    }
}
