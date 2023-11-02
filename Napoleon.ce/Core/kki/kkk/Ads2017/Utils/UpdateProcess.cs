using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Reflection;
using System.Threading;
using System.Windows;

namespace Ads2017
{
    public class Update
    {
        public static string AdmPwd { get; set; }
        private static Dictionary<string, IDataSet> datasets = new Dictionary<string, IDataSet>();

        public class QueryList
        {
            public class Item
            {
                public string name;
                public object arg;
            }

            public Dictionary<string, Item> data = new Dictionary<string, Item>();

            public void Add(string name)
            {
                Add(name, string.Empty);
            }

            public void Add(string name, string where)
            {
                Item i = new Item();
                i.name = name;
                i.arg = where;

                data.Add(name, i);
            }

            public IEnumerable Items { get { return data.Values; } }

            private Item report = null;

            public void SetReport(string name, object arg)
            {
                report = new Item()
                {
                    name = name,
                    arg = arg
                };
            }

            public Item Report { get { return report; } }
        }

        public class UpdateResult
        {
            public UpdateResult(QueryList query)
            {
                foreach (QueryList.Item item in query.Items)
                {
                    if (datasets.ContainsKey(item.name))
                    {
                        IDataSet ds = datasets[item.name];
                        ds.Filter = item.arg.ToString();
                        Add(item.name, ds);
                    }
                }

                if (query.Report != null)
                    Add(Report.OBJECT_NAME, DataSetFactory.CreateReport(query.Report.name,
                       (GRSoft.Network.DataObject)query.Report.arg));
            }

            private Dictionary<string, IDataSet> data = new Dictionary<string, IDataSet>();

            public List<T> GetList<T>(string name)
            {
                List<T> result = new List<T>();

                if (data.ContainsKey(name))
                {
                    IDataSet ds = data[name];

                    foreach (T obj in ds.Data)
                        result.Add(obj);
                }

                return result;
            }

            public Dictionary<string, T> GetDictionary<T>(string name)
            {
                Dictionary<string, T> result = new Dictionary<string, T>();

                if (data.ContainsKey(name))
                {
                    IDataSet ds = data[name];

                    foreach (T obj in ds.Data)
                    {
                        FieldInfo kf = DataModule.GetKeyField(typeof(T));
                        result[(string)kf.GetValue(obj)] = obj;
                    }
                }

                return result;
            }

            public void Add(string name, IDataSet ds)
            {
                data[name] = ds;
            }

            public List<IDataSet> GetForUpdate()
            {
                List<IDataSet> result = new List<IDataSet>();

                foreach (IDataSet d in data.Values)
                    result.Add(d);

                return result;
            }

            public ReportResult GetReportResult()
            {
                ReportResult result = null;

                if (data.ContainsKey(Report.OBJECT_NAME))
                {
                    Report r = (Report)data[Report.OBJECT_NAME];

                    if (r.resultSet.Count > 0)
                    {
                        SimpleDataSet<ReportResult> sr = (SimpleDataSet<ReportResult>)r.resultSet[0];

                        if (sr.Count > 0)
                        {
                            ReportResult rr = sr[0];

                            if (rr.file.Length > 0)
                                result = rr;
                        }
                    }

                }

                return result;
            }
        }

        static Update()
        {
            datasets.Add(TaskQuery.OBJECT_NAME, DataSetFactory.Create(TaskQuery.OBJECT_NAME));
            datasets.Add(TaskQuery.OBJECT_NAME_MANAGER, DataSetFactory.Create(TaskQuery.OBJECT_NAME_MANAGER));
            datasets.Add(Division.OBJECT_NAME, DataSetFactory.Create(Division.OBJECT_NAME));
            datasets.Add(Agent.OBJECT_NAME, DataSetFactory.Create(Agent.OBJECT_NAME));
            datasets.Add(Visit.OBJECT_NAME, DataSetFactory.Create(Visit.OBJECT_NAME));
            datasets.Add(UserLocation.OBJECT_NAME, DataSetFactory.Create(UserLocation.OBJECT_NAME));
            datasets.Add(GPSPos.OBJECT_NAME, DataSetFactory.Create(GPSPos.OBJECT_NAME));
            datasets.Add(UserLog.OBJECT_NAME, DataSetFactory.Create(UserLog.OBJECT_NAME));
            datasets.Add(UserOrderRemark.OBJECT_NAME, DataSetFactory.Create(UserOrderRemark.OBJECT_NAME));
            datasets.Add(PhotoCount.OBJECT_NAME, DataSetFactory.Create(PhotoCount.OBJECT_NAME));
            datasets.Add(MessageArchive.OBJECT_NAME, DataSetFactory.Create(MessageArchive.OBJECT_NAME));
            datasets.Add(TaskAttachment.OBJECT_NAME, DataSetFactory.Create(TaskAttachment.OBJECT_NAME));
            datasets.Add(TaskAttachment.OBJECT_INFO_NAME, DataSetFactory.Create(TaskAttachment.OBJECT_INFO_NAME));
            datasets.Add(UserOrder.OBJECT_NAME, DataSetFactory.Create(UserOrder.OBJECT_NAME));
            datasets.Add(AddressTemplate.OBJECT_NAME, DataSetFactory.Create(AddressTemplate.OBJECT_NAME));
            datasets.Add(DivisionManager.OBJECT_NAME, DataSetFactory.Create(DivisionManager.OBJECT_NAME));
            datasets.Add(TaskVisit.OBJECT_NAME, DataSetFactory.Create(TaskVisit.OBJECT_NAME));
            datasets.Add(TaskQueryVisit.OBJECT_NAME, DataSetFactory.Create(TaskQueryVisit.OBJECT_NAME));
            datasets.Add(PicStore.OBJECT_NAME, DataSetFactory.Create(PicStore.OBJECT_NAME));
            datasets.Add(CommonConfig.OBJECT_NAME, DataSetFactory.Create(CommonConfig.OBJECT_NAME));
        }

        public enum Command
        {
            Remove, Write
        }

        public interface IDataLoadProcess
        {
            void DoLoadData(UpdateResult data);
            UIElement[] GetRefreshControls();
        }

        public static void StdDataRefresh(QueryList query, IDataLoadProcess listener)
        {
            StdDataRefresh(query, listener, false);
        }

        public static void StdDataRefresh(QueryList query, IDataLoadProcess listener, bool admin)
        {
            UpdateResult result = new UpdateResult(query);
            DBConnection conn = InitConnection(new DBConnection(), admin);

            WaitWindow waitWinodw = new WaitWindow();

            SetRefreshControlsEnable(listener, false);

            DataModule.DataProcessed += new EventHandler((o, e) =>
            {
                Application.Current.Dispatcher.Invoke(new Action(() =>
             {
                   DataModule.ClearEvents();
                   waitWinodw.Close();
                   listener.DoLoadData(result);
                   SetRefreshControlsEnable(listener, true);
               }));
            });

            DataModule.OnDataResponceError += new EventDataResponseError((e) =>
            {
                Application.Current.Dispatcher.Invoke(new Action(() =>
             {
                   DataModule.ClearEvents();
                   waitWinodw.Close();
                   MessageBox.Show(e.Msg, Properties.Resources.error, MessageBoxButton.OK, MessageBoxImage.Error);
                   SetRefreshControlsEnable(listener, true);
               }));
            });

            waitWinodw.Closed += WaitWinodw_Closed;
            waitWinodw.Process = DataModule.RefreshGiveSets(conn, result.GetForUpdate(), waitWinodw.ProgressIndicator);
            waitWinodw.ShowDialog();
        }

        private static object MapData(List<IDataSet> upd)
        {
            Dictionary<string, IDataSet> result = new Dictionary<string, IDataSet>();

            foreach (IDataSet d in upd)
            {
                result[d.Name] = d;
            }

            return result;
        }

        private static void SetRefreshControlsEnable(IDataLoadProcess listener, bool value)
        {
            if (listener != null)
            {
                UIElement[] ctrls = listener.GetRefreshControls();

                if (ctrls != null)
                    foreach (UIElement u in ctrls)
                        u.IsEnabled = value;
            }
        }

        static void WaitWinodw_Closed(object sender, EventArgs e)
        {
            Thread t = ((WaitWindow)sender).Process;

            if (t.IsAlive)
                t.Abort();
        }

        public static DBConnection GetConnection(bool admin)
        {
            return InitConnection(new DBConnection(), admin);
        }

        private static DBConnection InitConnection(DBConnection conn, bool admin)
        {
            if (!admin)
            {
                conn.login = Properties.Settings.Default.Login;
                conn.password = Properties.Settings.Default.Password;
            }
            else
            {
                conn.login = "admin";
                conn.password = AdmPwd;
            }

            conn.ip = Properties.Settings.Default.IP;
            conn.port = Properties.Settings.Default.Port;
            conn.PDTFile = Properties.Settings.Default.PDTFile;

            return conn;
        }

        public static IEnumerable GetList(string name)
        {
            List<object> result = new List<object>();

            if (datasets.ContainsKey(name))
            {
                IDataSet ds = datasets[name];

                foreach (object obj in ds.Data)
                    result.Add(obj);
            }

            return result;
        }

        public class WriteData
        {
            string name = string.Empty;

            public WriteData(string name)
            {
                this.name = name;
            }

            public List<object> items = new List<object>();

            public string Name { get { return name; } }
        }

        public static bool WriteObjects(UpdateCollection write, UpdateCollection remove)
        {
            return WriteObjects(write, remove, false);
        }

        public static bool WriteObjects(UpdateCollection write, UpdateCollection remove, bool admin)
        {
            return DataModule.UpdateDataSet(PopulateDataSet(write), PopulateDataSet(remove),
               null, Update.GetConnection(admin));
        }

        public static bool SendMessage(string userid, string msg)
        {
            return DataModule.SendMessage(msg, userid, Update.GetConnection(false));
        }

        private static List<IDataSet> PopulateDataSet(UpdateCollection write)
        {
            List<IDataSet> toWrite = new List<IDataSet>();

            if (write != null)
            {
                foreach (WriteData w in write.Data)
                {
                    if (w.items.Count > 0)
                    {
                        IDataSet ds = DataSetFactory.Create(w.Name, false);

                        if (ds != null)
                        {
                            int counter = 0;

                            foreach (object o in w.items)
                            {
                                Type t = ds.IndexType;

                                object key = null;

                                if (t == typeof(string))
                                    key = string.Format("{0}", counter);
                                else
                                    key = counter;

                                ds.Add(key, o);

                                counter++;
                            }
                        }

                        toWrite.Add(ds);
                    }
                }
            }

            return toWrite;
        }

        public static void CloseSession()
        {
            try
            {
                DataModule.EndSession(Update.GetConnection(false), true);
            }
            catch (Exception)
            {
            }
        }
    }

    public static class DataSetFactory
    {
        public static IDataSet Create(string name)
        {
            return Create(name, true);
        }

        public static IDataSet Create(string name, bool global)
        {
            IDataSet result = null;

            if (name == Agent.OBJECT_NAME)
                result = new DataSet<string, Agent>(Agent.OBJECT_NAME, global);
            else if (name == Division.OBJECT_NAME)
                result = new DataSet<int, Division>(Division.OBJECT_NAME, global);
            else if (name == Visit.OBJECT_NAME)
                result = new DataSet<int, Visit>(Visit.OBJECT_NAME, global);
            else if (name == UserLocation.OBJECT_NAME)
                result = new DataSet<int, UserLocation>(UserLocation.OBJECT_NAME, global);
            else if (name == GPSPos.OBJECT_NAME)
                result = new DataSet<DateTime, GPSPos>(GPSPos.OBJECT_NAME, global);
            else if (name == UserLog.OBJECT_NAME)
                result = new DataSet<int, UserLog>(UserLog.OBJECT_NAME, global);
            else if (name == TaskQuery.OBJECT_NAME)
                result = new DataSet<string, TaskQuery>(TaskQuery.OBJECT_NAME, global);
            else if (name == TaskQuery.OBJECT_NAME_MANAGER)
                result = new DataSet<string, TaskQuery>(TaskQuery.OBJECT_NAME_MANAGER, global);
            else if (name == Task.OBJECT_NAME)
                result = new DataSet<string, Task>(Task.OBJECT_NAME, global);
            else if (name == UserOrderRemark.OBJECT_NAME)
                result = new DataSet<int, UserOrderRemark>(UserOrderRemark.OBJECT_NAME);
            else if (name == PhotoCount.OBJECT_NAME)
                result = new DataSet<int, PhotoCount>(PhotoCount.OBJECT_NAME);
            else if (name == MessageArchive.OBJECT_NAME)
                result = new DataSet<int, MessageArchive>(MessageArchive.OBJECT_NAME);
            else if (name == TaskAttachment.OBJECT_NAME)
                result = new DataSet<string, TaskAttachment>(TaskAttachment.OBJECT_NAME);
            else if (name == TaskAttachment.OBJECT_INFO_NAME)
                result = new DataSet<string, TaskAttachment>(TaskAttachment.OBJECT_INFO_NAME);
            else if (name == UserOrder.OBJECT_NAME)
                result = new DataSet<int, UserOrder>(UserOrder.OBJECT_NAME);
            else if (name == AddressTemplate.OBJECT_NAME)
                result = new DataSet<string, AddressTemplate>(AddressTemplate.OBJECT_NAME);
            else if (name == DivisionManager.OBJECT_NAME)
                result = new DataSet<string, DivisionManager>(DivisionManager.OBJECT_NAME);
            else if (name == TaskVisit.OBJECT_NAME)
                result = new DataSet<int, TaskVisit>(TaskVisit.OBJECT_NAME);
            else if (name == TaskQueryVisit.OBJECT_NAME)
                result = new DataSet<int, TaskQueryVisit>(TaskQueryVisit.OBJECT_NAME);
            else if (name == PicStore.OBJECT_NAME)
                result = new DataSet<int, PicStore>(PicStore.OBJECT_NAME);
            else if (name == CommonConfig.OBJECT_NAME)
                result = new DataSet<string, CommonConfig>(CommonConfig.OBJECT_NAME);
            return result;
        }

        public static IDataSet CreateReport(string name, GRSoft.Network.DataObject arg)
        {
            SimpleDataSet<ReportResult> resultSet = new SimpleDataSet<ReportResult>(ReportResult.OBJECT_NAME, false);
            return new Report(name, arg, resultSet);
        }
    }
}
