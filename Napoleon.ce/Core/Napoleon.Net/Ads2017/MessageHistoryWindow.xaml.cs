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
    public partial class MessageHistoryWindow : RibbonWindow, Update.IDataLoadProcess
    {
        private ObservableCollection<MessageArchive> data = new ObservableCollection<MessageArchive>();

        public MessageHistoryWindow()
        {
            InitializeComponent();

            grid.ItemsSource = data;

            start.SelectedDate = new DateTime(DateTime.Now.Year, DateTime.Now.Month, 1);
            finish.SelectedDate = new DateTime(DateTime.Now.Year, DateTime.Now.Month, DateTime.DaysInMonth(DateTime.Now.Year, DateTime.Now.Month));
        }

        public void DoLoadData(Update.UpdateResult res)
        {
            data.Clear();

            foreach(MessageArchive a in res.GetList<MessageArchive>(MessageArchive.OBJECT_NAME))
                data.Add(a);
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

            Update.QueryList upd = new Update.QueryList();
            upd.Add(MessageArchive.OBJECT_NAME, string.Format("\"date\">= ToDate('{0:dd/MM/yyyy}') and \"date\" <= ToDate('{1:dd/MM/yyyy}') " +
                "and \"userid\" in ({2})",
                s.Date, f.Date.AddDays(1), UserIds));

            Update.StdDataRefresh(upd, this);
        }

        public string UserIds { get; set; }

        private void RibbonWindow_Loaded(object sender, RoutedEventArgs e)
        {
            Refresh();
        }
    }
}
