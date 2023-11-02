using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Reflection;
using System.Threading;
using System.Windows;

namespace Napoleon
{
    public class Update
    {
        private static Dictionary<string, IDataSet> datasets = new Dictionary<string, IDataSet>();

        public class QueryList
        {
            public struct Item
            {
                public string name;
                public string where;
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
                i.where = where;

                data.Add(name, i);
            }

            public IEnumerable Items { get { return data.Values; } }
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
                        ds.Filter = item.where;
                        Add(item.name, ds);
                    }
                }
            }

            private Dictionary<string, IDataSet> data = new Dictionary<string, IDataSet>();

            public List<T> GetList<T>(string name)
            {
                return Update.CreateList<T>(name, data);
            }

            public Dictionary<string, T> GetDictionary<T>(string name)
            {
                return Update.CreateDictionary<T>(name, data);
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
        }

        static Update()
        {
            ServerCommand.Category = "dispatcher";

            datasets.Add(TaskQuery.OBJECT_NAME, DataSetFactory.Create(TaskQuery.OBJECT_NAME));
            datasets.Add(TaskQuery.OBJECT_NAME_MANAGER, DataSetFactory.Create(TaskQuery.OBJECT_NAME_MANAGER));
            datasets.Add(Division.OBJECT_NAME, DataSetFactory.Create(Division.OBJECT_NAME));
            datasets.Add(Agent.OBJECT_NAME, DataSetFactory.Create(Agent.OBJECT_NAME));
            datasets.Add(Visit.OBJECT_NAME, DataSetFactory.Create(Visit.OBJECT_NAME));
            datasets.Add(UserLocation.OBJECT_NAME, DataSetFactory.Create(UserLocation.OBJECT_NAME));
            datasets.Add(GPSPos.OBJECT_NAME, DataSetFactory.Create(GPSPos.OBJECT_NAME));
            datasets.Add(UserLog.OBJECT_NAME, DataSetFactory.Create(UserLog.OBJECT_NAME));
            datasets.Add(Note.OBJECT_NAME, DataSetFactory.Create(Note.OBJECT_NAME));
            datasets.Add(NoteAction.OBJECT_NAME, DataSetFactory.Create(NoteAction.OBJECT_NAME));
            datasets.Add(PhotoCount.OBJECT_NAME, DataSetFactory.Create(PhotoCount.OBJECT_NAME));
            datasets.Add(MessageArchive.OBJECT_NAME, DataSetFactory.Create(MessageArchive.OBJECT_NAME));
            //datasets.Add(Org.OBJECT_NAME, DataSetFactory.Create(Org.OBJECT_NAME));
            datasets.Add(OrgFolder.OBJECT_NAME, DataSetFactory.Create(OrgFolder.OBJECT_NAME));
            datasets.Add(Config.OBJECT_NAME, DataSetFactory.Create(Config.OBJECT_NAME));
            datasets.Add(Price.OBJECT_NAME, DataSetFactory.Create(Price.OBJECT_NAME));
            datasets.Add(Folder.OBJECT_NAME, DataSetFactory.Create(Folder.OBJECT_NAME));
            datasets.Add(PhoneAction.OBJECT_NAME, DataSetFactory.Create(PhoneAction.OBJECT_NAME));
            datasets.Add(Order.OBJECT_NAME, DataSetFactory.Create(Order.OBJECT_NAME));
            datasets.Add(LastOrder.OBJECT_NAME, DataSetFactory.Create(LastOrder.OBJECT_NAME));
            datasets.Add(LastDelivery.OBJECT_NAME, DataSetFactory.Create(LastDelivery.OBJECT_NAME));
            //datasets.Add(LastRemnant.OBJECT_NAME, DataSetFactory.Create(LastRemnant.OBJECT_NAME));
            datasets.Add(OrgMatrix.OBJECT_NAME, DataSetFactory.Create(OrgMatrix.OBJECT_NAME));
            datasets.Add(IDMTX.IDMTX_OBJ_NAME, DataSetFactory.Create(IDMTX.IDMTX_OBJ_NAME));
            datasets.Add(ObjectMatrix.IDOMTX_OBJ_NAME, DataSetFactory.Create(ObjectMatrix.IDOMTX_OBJ_NAME));
            datasets.Add(Firms.OBJECT_NAME, DataSetFactory.Create(Firms.OBJECT_NAME));
            datasets.Add(Brands.OBJECT_NAME, DataSetFactory.Create(Brands.OBJECT_NAME));
            datasets.Add(PlanNew.OBJECT_NAME, DataSetFactory.Create(PlanNew.OBJECT_NAME));
            datasets.Add(Org.COMMON_OBJECT_NAME, DataSetFactory.Create(Org.COMMON_OBJECT_NAME));
            datasets.Add(RejectCause.OBJECT_NAME, DataSetFactory.Create(RejectCause.OBJECT_NAME));
            datasets.Add(OrgDogovor.OBJECT_NAME, DataSetFactory.Create(OrgDogovor.OBJECT_NAME));
            datasets.Add(MMLFeatures.OBJECT_NAME, DataSetFactory.Create(MMLFeatures.OBJECT_NAME));
            datasets.Add(LastSalesItems.OBJECT_NAME, DataSetFactory.Create(LastSalesItems.OBJECT_NAME));
            datasets.Add(OrderProceeded.OBJECT_NAME, DataSetFactory.Create(OrderProceeded.OBJECT_NAME));
            datasets.Add(NotExpiredItems.OBJECT_NAME, DataSetFactory.Create(NotExpiredItems.OBJECT_NAME));
            datasets.Add(ReturnCause.OBJECT_NAME, DataSetFactory.Create(ReturnCause.OBJECT_NAME));
            datasets.Add(ReturnLimit.OBJECT_NAME, DataSetFactory.Create(ReturnLimit.OBJECT_NAME));
            datasets.Add(ReturnRequest.OBJECT_NAME, DataSetFactory.Create(ReturnRequest.OBJECT_NAME));
            datasets.Add(AgentOrgs.OBJECT_NAME, DataSetFactory.Create(AgentOrgs.OBJECT_NAME));
            datasets.Add(TradeAction.OBJECT_NAME, DataSetFactory.Create(TradeAction.OBJECT_NAME));
        }

        public enum Command
        {
            Remove, Write
        }

        public interface IDataLoadProcess
        {
            void DoLoadData(UpdateResult data);
        }

        public static void StdDataRefresh(QueryList query, IDataLoadProcess listener)
        {
            UpdateResult result = new UpdateResult(query);
            DBConnection conn = InitConnection(new DBConnection());
            WaitWindow waitWinodw = new WaitWindow();

            DataModule.DataProcessed += new EventHandler((o, e) =>
            {
                Application.Current.Dispatcher.Invoke(new Action(() =>
                {
                    DataModule.ClearEvents();
                    waitWinodw.Close();
                    listener.DoLoadData(result);
                }));
            });

            DataModule.OnDataResponceError += new EventDataResponseError((e) =>
            {
                Application.Current.Dispatcher.Invoke(new Action(() =>
                {
                    DataModule.ClearEvents();
                    waitWinodw.Close();
                    MessageBox.Show(e.Msg, "Ошибка", MessageBoxButton.OK, MessageBoxImage.Error);
                }));
            });

            waitWinodw.Closed += WaitWinodw_Closed;
            waitWinodw.Process = DataModule.RefreshGiveSets(conn, result.GetForUpdate(), waitWinodw.ProgressIndicator);
            waitWinodw.ShowDialog();
        }

        public static UpdateResult UpdateWait(QueryList query)
        {
            UpdateResult result = new UpdateResult(query);
            DBConnection conn = InitConnection(new DBConnection());
            Thread t = DataModule.RefreshGiveSets(conn, result.GetForUpdate(), null);
            t.Join();
            return result;
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

        static void WaitWinodw_Closed(object sender, EventArgs e)
        {
            Thread t = ((WaitWindow)sender).Process;

            if (t.IsAlive)
                t.Abort();
        }

        public static DBConnection GetConnection()
        {
            return InitConnection(new DBConnection());
        }

        private static DBConnection InitConnection(DBConnection conn)
        {
            conn.login = Properties.Settings.Default.Login;
            conn.password = Properties.Settings.Default.Password;
            conn.ip = Properties.Settings.Default.IP;
            conn.port = Properties.Settings.Default.Port;
            conn.PDTFile = Properties.Settings.Default.PDTFile;

            return conn;
        }

        public static List<T> CreateList<T>(string name, Dictionary<string, IDataSet> res)
        {
            List<T> result = new List<T>();

            if (res.ContainsKey(name))
            {
                IDataSet ds = res[name];

                foreach (T obj in ds.Data)
                    result.Add(obj);
            }

            return result;
        }

        public static Dictionary<string, T> CreateDictionary<T>(string name, Dictionary<string, IDataSet> res)
        {
            Dictionary<string, T> result = new Dictionary<string, T>();

            if (res.ContainsKey(name))
            {
                IDataSet ds = res[name];

                foreach (T obj in ds.Data)
                {
                    FieldInfo kf = DataModule.GetKeyField(typeof(T));
                    result[(string)kf.GetValue(obj)] = obj;
                }
            }

            return result;
        }

        public static List<T> GetStoredList<T>(string name)
        {
            return CreateList<T>(name, datasets);
        }

        public static void PutStored(object stored)
        {
            FieldInfo f = stored.GetType().GetField("OBJECT_NAME", BindingFlags.Public | BindingFlags.Static);

            if (f != null)
            {
                string str = (string)f.GetValue(stored);

                if (datasets.ContainsKey(str))
                {
                    IDataSet ds = datasets[str];
                    ds.Add(ds.Count, stored);
                }
            }
        }

        public static void PutStoredDict(object stored)
        {
            FieldInfo f = stored.GetType().GetField("OBJECT_NAME", BindingFlags.Public | BindingFlags.Static);

            if (f != null && stored is GRSoft.Network.DataObject o)
            {
                List<FieldInfo> keys = o.GetKeyFields();

                if (keys != null && keys.Count > 0)
                {
                    string key = (string)keys[0].GetValue(stored);
                    string str = (string)f.GetValue(stored);

                    if (datasets.ContainsKey(str))
                    {
                        IDataSet ds = datasets[str];

                        if (!ds.ContainsKey(key))
                            ds.Add(key, stored);
                    }
                }
            }
        }

        public static Dictionary<string, T> GetStoredDictionary<T>(string name)
        {
            return CreateDictionary<T>(name, datasets);
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
            return DataModule.UpdateDataSet(PopulateDataSet(write), PopulateDataSet(remove),
               null, Update.GetConnection());
        }

        public static bool SendMessage(string userid, string msg)
        {
            return DataModule.SendMessage(msg, userid, Update.GetConnection());
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
            else if (name == Note.OBJECT_NAME)
                result = new DataSet<int, Note>(Note.OBJECT_NAME);
            else if (name == NoteAction.OBJECT_NAME)
                result = new DataSet<int, Note>(NoteAction.OBJECT_NAME);
            else if (name == PhotoCount.OBJECT_NAME)
                result = new DataSet<int, PhotoCount>(PhotoCount.OBJECT_NAME);
            else if (name == MessageArchive.OBJECT_NAME)
                result = new DataSet<int, MessageArchive>(MessageArchive.OBJECT_NAME);
            //else if (name == Org.OBJECT_NAME)
            //    result = new DataSet<string, Org>(Org.OBJECT_NAME);
            else if (name == Org.COMMON_OBJECT_NAME)
                result = new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);
            else if (name == OrgFolder.OBJECT_NAME)
                result = new DataSet<int, OrgFolder>(OrgFolder.OBJECT_NAME);
            else if (name == Config.OBJECT_NAME)
                result = new DataSet<string, Config>(Config.OBJECT_NAME);
            else if (name == Folder.OBJECT_NAME)
                result = new DataSet<int, Folder>(Folder.OBJECT_NAME);
            else if (name == Price.OBJECT_NAME)
                result = new DataSet<string, Price>(Price.OBJECT_NAME);
            else if (name == PhoneAction.OBJECT_NAME)
                result = new DataSet<int, PhoneAction>(PhoneAction.OBJECT_NAME);
            else if (name == Order.OBJECT_NAME)
                result = new DataSet<int, Order>(Order.OBJECT_NAME);
            else if (name == LastOrder.OBJECT_NAME)
                result = new DataSet<int, LastOrder>(LastOrder.OBJECT_NAME);
            else if (name == LastDelivery.OBJECT_NAME)
                result = new DataSet<int, LastDelivery>(LastDelivery.OBJECT_NAME);
            //else if (name == LastRemnant.OBJECT_NAME)
            //    result = new DataSet<int, LastRemnant>(LastRemnant.OBJECT_NAME);
            else if (name == OrgMatrix.OBJECT_NAME)
                result = new DataSet<string, OrgMatrix>(OrgMatrix.OBJECT_NAME);
            else if (name == IDMTX.IDMTX_OBJ_NAME)
                result = new SimpleDataSet<IDMTX>(IDMTX.IDMTX_OBJ_NAME);
            else if (name == ObjectMatrix.IDOMTX_OBJ_NAME)
                result = new SimpleDataSet<ObjectMatrix>(ObjectMatrix.IDOMTX_OBJ_NAME);
            else if (name == Firms.OBJECT_NAME)
                result = new DataSet<string, Firms>(Firms.OBJECT_NAME);
            else if (name == PlanNew.OBJECT_NAME)
                result = new DataSet<int, PlanNew>(PlanNew.OBJECT_NAME);
            else if (name == ServoluxSheduleItem.OBJECT_NAME)
                result = new DataSet<string, ServoluxSheduleItem>(ServoluxSheduleItem.OBJECT_NAME);
            else if (name == RejectCause.OBJECT_NAME)
                result = new DataSet<string, RejectCause>(RejectCause.OBJECT_NAME);
            else if (name == OrgDogovor.OBJECT_NAME)
                result = new SimpleDataSet<OrgDogovor>(OrgDogovor.OBJECT_NAME);
            else if (name == MMLFeatures.OBJECT_NAME)
                result = new SimpleDataSet<MMLFeatures>(MMLFeatures.OBJECT_NAME);
            else if (name == Brands.OBJECT_NAME)
                result = new DataSet<string, Brands>(Brands.OBJECT_NAME);
            else if (name == LastSalesItems.OBJECT_NAME)
                result = new DataSet<string, LastSalesItems>(LastSalesItems.OBJECT_NAME);
            else if (name == OrderProceeded.OBJECT_NAME)
                result = new DataSet<int, OrderProceeded>(OrderProceeded.OBJECT_NAME);
            else if (name == NotExpiredItems.OBJECT_NAME)
                result = new DataSet<int, NotExpiredItems>(NotExpiredItems.OBJECT_NAME);
            else if (name == ReturnCause.OBJECT_NAME)
                result = new DataSet<int, ReturnCause>(ReturnCause.OBJECT_NAME);
            else if (name == ReturnLimit.OBJECT_NAME)
                result = new DataSet<int, ReturnLimit>(ReturnLimit.OBJECT_NAME);
            else if (name == ReturnRequest.OBJECT_NAME)
                result = new DataSet<int, ReturnRequest>(ReturnRequest.OBJECT_NAME);
            else if (name == AgentOrgs.OBJECT_NAME)
                result = new DataSet<int, AgentOrgs>(AgentOrgs.OBJECT_NAME);
            else if (name == TradeAction.OBJECT_NAME)
                result = new DataSet<int, TradeAction>(TradeAction.OBJECT_NAME);
            return result;
        }
    }
}

