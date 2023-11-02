using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Ribbon;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Threading;


namespace Ads2017
{
    public partial class TaskJournalWindow : RibbonWindow
    {
        private ObservableCollection<JournalItem> allData = new ObservableCollection<JournalItem>();
        private bool clearing = false;
        private DispatcherTimer timer;

        public TaskJournalWindow()
        {
            InitializeComponent();

            Loaded += TaskJournal_Loaded;
            start.SelectedDate = DateTime.Now;
            finish.SelectedDate = DateTime.Now;

            timer = new DispatcherTimer(new TimeSpan(0, 0, 0, 0, 500), DispatcherPriority.Normal, Timer_Tick, Dispatcher.CurrentDispatcher);
        }

        void Timer_Tick(object sender, EventArgs e)
        {
            timer.Stop();
            DoSearch(tbSearch.Text);
        }

        private void DoSearch(string str)
        {
            str = str.ToUpper();
            ObservableCollection<JournalItem> filterData = new ObservableCollection<JournalItem>();

            foreach (JournalItem i in allData)
                if (SearchCondition(str, i))
                    filterData.Add(i);

            grid.ItemsSource = filterData;
        }

        private static bool SearchCondition(string search, JournalItem ji)
        {
            return ji.User.ToUpper().Contains(search) ||
                ji.Task.ToUpper().Contains(search) ||
                ji.Client.ToUpper().Contains(search) ||
                ji.Address.ToUpper().Contains(search) ||
                ji.Status.ToUpper().Contains(search);
        }

        void TaskJournal_Loaded(object sender, RoutedEventArgs e)
        {
            Refresh();
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
            string taskFilter = string.Format(Constants.TASK_FILTER, s.Date, f.Date.AddDays(1), ManagerHelper.Instance.AgentsWhere(false));
            upd.Add(TaskQuery.OBJECT_NAME, taskFilter);
            upd.Add(PhotoCount.OBJECT_NAME, taskFilter);
            upd.Add(TaskQueryVisit.OBJECT_NAME, taskFilter);
            Update.StdDataRefresh(upd, new MainDataUpdater(this));
        }

        public void DoLoadData(object data)
        {
            allData.Clear();

            Update.UpdateResult ur = ((Update.UpdateResult)data);

            Dictionary<string, int> photo = new Dictionary<string, int>();
            UpdatePhoto(photo, ur.GetDictionary<PhotoCount>(PhotoCount.OBJECT_NAME));
            UpdatePhoto(photo, ur.GetList<TaskQueryVisit>(TaskQueryVisit.OBJECT_NAME));

            List<TaskQuery> list = ur.GetList<TaskQuery>(TaskQuery.OBJECT_NAME);

            list.Sort((x, y) => { return x.Created.CompareTo(y.Created); });

            foreach (TaskQuery o in list)
            {
                int sz = photo.ContainsKey(o.taskid) ? photo[o.taskid] : 0;
                JournalItem ji = CreateJournalItem(o, sz);
                allData.Add(ji);
            }

            grid.ItemsSource = allData;
        }

        private void UpdatePhoto(Dictionary<string, int> dict, Dictionary<string, PhotoCount> data)
        {
            foreach (PhotoCount p in data.Values)
                dict[p.taskid] = p.count;
        }

        private void UpdatePhoto(Dictionary<string, int> dict, List<TaskQueryVisit> data)
        {
            foreach (TaskQueryVisit p in data)
                dict[p.taskid] = p.items.Count;
        }

        private ObservableCollection<BitmapImage> CreateStubImages(int count)
        {
            ObservableCollection<BitmapImage> result = new ObservableCollection<BitmapImage>();

            for (int x = 0; x < count; x++)
                result.Add(CreateStubBitmap());

            return result;
        }

        private static BitmapImage CreateStubBitmap()
        {
            BitmapImage result = new BitmapImage();
            result.BeginInit();
            result.UriSource = new Uri(@"/Images/loadphoto.png", UriKind.RelativeOrAbsolute);
            result.EndInit();
            return result;
        }

        private JournalItem CreateJournalItem(TaskQuery t, int pfsz)
        {
            JournalItem i = new JournalItem()
            {
                taskid = t.taskid,
                user = t.agent == null ? string.Empty : t.agent.Name,
                created = t.created,
                timeplan = FormatHelper.Instance.Range(t.start, t.finish),
                task = t.text,
                client = t.client,
                address = t.address,
                status = Task.StatusToStr(t.solution),
                report = t.execrem,
                statusColor = TaskHelper.BkgItemColor(t.solution),
                photoCount = pfsz,
                Image = pfsz > 0 ? CreateStubBitmap() : null,
                timefact = t.solution == Task.RESOLVED ? FormatHelper.Instance.Range(t.startexec, t.finishexec) : string.Empty,
            };

            return i;
        }

        public UIElement GetRefreshControl()
        {
            return btnRefresh;
        }

        public void DoLoadPhoto(Update.UpdateResult data)
        {
            if (grid.CurrentItem is JournalItem jit)
            {
                jit.images.Clear();

                foreach (Visit v in data.GetList<Visit>(Visit.OBJECT_NAME))
                {
                    foreach (Visit.VisitItem item in v.items)
                    {
                        if (item.id == null)
                            continue;

                        jit.images.Add(LoadBitmap(item.id));
                    }
                }

                foreach (PicStore p in data.GetList<PicStore>(PicStore.OBJECT_NAME))
                {
                    jit.images.Add(LoadBitmap(p.picture));
                }
            }

            JournalItem ji = grid.SelectedItem as JournalItem;

            PhotoWindow w = new PhotoWindow();
            w.Images = ji.Images;
            w.Show();
        }

        private BitmapImage LoadBitmap(byte[] src)
        {
            using (MemoryStream stream = new MemoryStream(src))
            {
                BitmapImage image = new BitmapImage();

                image.BeginInit();
                image.CacheOption = BitmapCacheOption.OnLoad;
                image.StreamSource = stream;
                image.EndInit();

                return image;
            }
        }

        abstract class DataUpdater : Update.IDataLoadProcess
        {
            public TaskJournalWindow source = null;

            public DataUpdater(TaskJournalWindow source)
            {
                this.source = source;
            }

            public UIElement[] GetRefreshControls()
            {
                return new UIElement[] { source.btnRefresh };
            }

            public abstract void DoLoadData(Update.UpdateResult data);
        }

        class MainDataUpdater : DataUpdater
        {
            public MainDataUpdater(TaskJournalWindow source)
               : base(source)
            {
            }

            public override void DoLoadData(Update.UpdateResult data)
            {
                source.DoLoadData(data);
            }
        }

        class PhotoDataUpdater : DataUpdater
        {
            public PhotoDataUpdater(TaskJournalWindow source)
               : base(source)
            {
            }

            public override void DoLoadData(Update.UpdateResult data)
            {
                source.DoLoadPhoto(data);
            }
        }

        private void Clear_Click(object sender, RoutedEventArgs e)
        {
            ClearSearch();
        }

        private void ClearSearch()
        {
            clearing = true;
            tbSearch.Text = string.Empty;
            grid.ItemsSource = allData;
            clearing = false;
        }

        private void TbSearch_TextChanged(object sender, System.Windows.Controls.TextChangedEventArgs e)
        {
            timer.Stop();

            if (tbSearch.Text.Length > 0)
                timer.Start();
            else if (!clearing)
                ClearSearch();
        }

        private void Image_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            if (e.ChangedButton == MouseButton.Left && e.ClickCount == 2)
            {
                Image img = (Image)sender;

                if (img.Source is BitmapImage bi)
                {
                    var encoder = new PngBitmapEncoder();
                    encoder.Frames.Add(BitmapFrame.Create(bi));

                    string name = string.Format("{0}.png", Path.GetTempFileName());

                    using (var stream = File.Create(name))
                    {
                        encoder.Save(stream);
                    }

                    System.Diagnostics.Process.Start(name);
                }
            }
        }

        private void Image_MouseUp(object sender, MouseButtonEventArgs e)
        {
            if (grid.CurrentItem is JournalItem jit)
            {
                if (jit.photoCount > 0)
                {
                    Update.QueryList query = new Update.QueryList();
                    query.Add(Visit.OBJECT_NAME, string.Format("\"taskid\"='{0}'", jit.taskid));
                    query.Add(PicStore.OBJECT_NAME, jit.taskid);

                    Update.StdDataRefresh(query, new PhotoDataUpdater(this));
                }
            }
        }
    }
}
