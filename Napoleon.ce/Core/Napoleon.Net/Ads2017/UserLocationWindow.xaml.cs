using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Ribbon;
using System.Windows.Input;

namespace Ads2017
{
    public partial class UserLocationWindow : RibbonWindow, Ads2017.Update.IDataLoadProcess
    {
        MapControlHelper mhc = new MapControlHelper();

        public UserLocationWindow()
        {
            InitializeComponent();
            mhc.InitControl(rgMaps, rgcMaps);

            Loaded += UserLocationWindow_Loaded;
            list.OnItemClick = OnLocationSelected;
            list.OnItemDoubleClick = OnLocationDoubleClick;
        }

        private void OnLocationDoubleClick(object sender, object item)
        {
            UserRouteWindow r = new UserRouteWindow
            {
                UserId = ((UserLocationTicket)item).UserId
            };

            r.Show();
        }

        private void OnLocationSelected(object sender, object item)
        {
            if (((UserLocationTicket)item).StoredObject is UserLocationData data)
            {
                browser.InvokeScript("showInfo", new object[] { data.Pos });
            }
        }

        void UserLocationWindow_Loaded(object sender, RoutedEventArgs e)
        {
            Refresh();
        }

        private void RefreshExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            Refresh();
        }

        private void Refresh()
        {
            Update.QueryList upd = new Update.QueryList();
            upd.Add(UserLocation.OBJECT_NAME);

            Update.StdDataRefresh(upd, this);
        }

        public void DoLoadData(Update.UpdateResult res)
        {
            List<UserLocation> list = res.GetList<UserLocation>(UserLocation.OBJECT_NAME);
            list.Sort((x, y) => { return x.UserName.CompareTo(y.UserName); });

            ObservableCollection<UserLocationData> data = new ObservableCollection<UserLocationData>();
            int pos = 1;

            foreach (UserLocation u in list)
            {
                if (ManagerHelper.Instance.HaveAgent(u.userid) && u.Date >= DateTime.Now.Date)
                {
                    UserLocationData ud = new UserLocationData
                    {
                        pos = pos++,
                        location = u
                    };

                    data.Add(ud);
                }
            }

            string map = rgMaps.SelectedItem.ToString();
            MapData md = new MapData();
            md.userlocation.AddRange(data);

            browser.NavigateToString(mhc.CreateMap(map, md));

            this.list.Adapter = new ListAdapter(data);
        }

        public UIElement[] GetRefreshControls()
        {
            return new UIElement[] { btnRefresh };
        }

        private class ListAdapter : ListViewAdapter
        {
            List<UserLocationData> data = new List<UserLocationData>();

            public ListAdapter(ICollection<UserLocationData> data)
            {
                this.data.AddRange(data);
            }

            public override int Count
            {
                get { return data.Count; }
            }

            public override object GetItem(int position)
            {
                return data[position];
            }

            public override UserControl GetView(int position)
            {
                UserLocationData p = (UserLocationData)GetItem(position);

                UserLocationTicket ticket = new UserLocationTicket()
                {
                    StoredObject = p,
                    Number = p.Pos,
                    Time = p.TimeStr,
                    Client = p.UserName,
                    UserId = p.location.userid
                };

                return ticket;
            }
        }
    }

    
    class UserLocationData
    {
        public int pos = 0;
        public UserLocation location;
        public int Pos { get { return pos; } }
        public string UserName { get { return location != null ? location.UserName : string.Empty; } }
        public string TimeStr { get { return location != null ? location.Date.ToShortTimeString() : string.Empty; } }
    }
}
