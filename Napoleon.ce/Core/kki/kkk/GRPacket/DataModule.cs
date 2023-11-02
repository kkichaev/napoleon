using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.ComponentModel;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Reflection;
using System.Collections;
using System.IO;

namespace GRSoft.Network
{
   public delegate void DataRetrieveComplete();
   public delegate void ServerResponseHandle(PacketObject result, List<IDataSet> sets);
   public delegate void ServerStrResponseHandle(PacketObject result, List<string> sets);
   public delegate void EventDataResponseError(EDataResponse e);
   public delegate void DataSetAddingItem(object item);

   /// <summary>
   /// Класс содержит данные об ошибке, что произошла в момент
   /// получения данных
   /// </summary>
   public class EDataResponse
   {
      private string msg;

      public EDataResponse(string msg)
      {
         this.msg = msg;
      }

      public string Msg { get { return msg; } }
   }

   public class PrecisionAttribute : Attribute
   {
      private short prec;
      public PrecisionAttribute(short prec)
      {
         this.prec = prec;
      }

      public short Precision { get { return prec; } }
   }

   public class KeyFieldAttribute : Attribute
   {
      public KeyFieldAttribute() { }
   }

   public class ReferenceAttribute : Attribute
   {
      private string fieldName, setName;
      private Type createType = null;

      public ReferenceAttribute(string setName, string fieldName)
      {
         this.fieldName = fieldName;
         this.setName = setName;
      }
      /// <summary>
      /// Если нет связи создает пустой элемент указанного типа и связывает его
      /// </summary>
      /// <param name="setName"></param>
      /// <param name="fieldName"></param>
      /// <param name="createType">должен иметь конструктор без параметров</param>
      public ReferenceAttribute(string setName, string fieldName, Type createType)
      {
         this.fieldName = fieldName;
         this.setName = setName;
         this.createType = createType;
      }

      public string Field { get { return fieldName; } }
      public string DataSet { get { return setName; } }

      public Type CreateType { get { return createType; } }
   }

   public class ItemTypeAttribute : Attribute
   {
      private Type itemType;

      public ItemTypeAttribute(Type itemType) { this.itemType = itemType; }
      public Type ItemType { get { return itemType; } }
   }

   public class DataFieldAttribute : Attribute
   {
      private string name;

      public DataFieldAttribute(string name) { this.name = name; }
      public string Name { get { return name; } }
   }

   public class DataObject
   {
      public bool Read(GRSoft.Network.Object o, List<IDataSet> sets)
      {
         return DataModule.Load(this, o, sets);
      }

      public List<FieldInfo> GetKeyFields()
      {
         List<FieldInfo> keys = new List<FieldInfo>();
         FieldInfo[] fields = GetType().GetFields(BindingFlags.Instance | BindingFlags.Public);
         foreach (FieldInfo f in fields)
         {
            object[] a = f.GetCustomAttributes(typeof(KeyFieldAttribute), false);
            if (a.Length > 0)
            {
               keys.Add(f);
               //break;
            }
         }

         return keys;
      }

      public Dictionary<string, object> srvFields = new Dictionary<string, object>();

      public static string GenId()
      {
         string result = System.Guid.NewGuid().ToString().ToUpper();
         return result.Replace("-", "");
      }
   }

   public class SendParam
   {
      public SendParam(ObjectList command, ServerResponseHandle handle, List<IDataSet> upd)
      {
         po = new PacketObject();
         po.Add(command);

         this.handle = handle;
         this.upd = upd;
      }

      public SendParam(PacketObject obj, ServerResponseHandle handle, List<IDataSet> upd)
      {
         po = obj;
         this.handle = handle;
         this.upd = upd;
      }

      public List<IDataSet> upd;
      public PacketObject po;
      public ServerResponseHandle handle;
      public IProgress progress = null;
   }

   public class SendParamStr
   {
      public SendParamStr(ObjectList command, ServerStrResponseHandle handle, List<string> upd)
      {
         po = new PacketObject();
         po.Add(command);

         this.handle = handle;
         this.upd = upd;
      }

      public SendParamStr(PacketObject obj, ServerStrResponseHandle handle, List<string> upd)
      {
         po = obj;
         this.handle = handle;
         this.upd = upd;
      }

      public List<string> upd;
      public PacketObject po;
      public ServerStrResponseHandle handle;
      public IProgress progress = null;
   }

   public class DBConnection
   {
      public static int TIMEOUT = 60 * 1000 * 5;
      static object locker = new object();

      private int rcvtimeout = TIMEOUT;

      public string login = "";
      public string password = "";

      public string ip = "";
      public int port;

      public DBConnection() { }
      public DBConnection(string ip, int port)
      {
         this.ip = ip;
         this.port = port;

         //ReadServerID();
      }

      /// <summary>
      /// Таймаут на прием в миллисекундах
      /// </summary>
      public int ReceiveTimeout { get { return rcvtimeout; } set { rcvtimeout = value; } }

      public Thread SendCommand(SendParamStr param)
      {

         Thread t = new Thread(SendWorkerStr);
         t.Start(param);
         return t;
      }

      public Thread SendCommand(SendParam param)
      {
         Thread t = new Thread(SendWorker);
         t.Start(param);
         return t;
      }

      public PacketObject GetDataSet(SendParam param)
      {
         return SendRequest(param.po, param.progress);
      }

      static private uint sessionID = 0;
      static string fileName = "GRPacket.pdt";

      public void SetNewSession(String fileName)
      {
         sessionID = 0;
         //SaveSessionId(fileName);
         PDTFile = fileName;
      }

      public string PDTFile
      {
         set
         {
            fileName = Path.GetTempPath() + value;
            ReadServerID();
         }
      }

      private void ReadServerID()
      {
         try
         {
            if (sessionID == 0 && File.Exists(fileName))
            {
               using (FileStream fs = File.OpenRead(fileName))
               {
                  int len = (int)fs.Length;
                  byte[] data = new byte[len];
                  fs.Read(data, 0, len);
                  sessionID = BitConverter.ToUInt32(data, 0);
               }
            }
         }
         catch (Exception) { }
      }

      ~DBConnection()
      {
         //SaveSessionId(fileName);
         try
         {
            using (FileStream fs = File.OpenWrite(fileName))
            {
               byte[] bc = BitConverter.GetBytes(sessionID);
               fs.Write(bc, 0, bc.Length);
               fs.Close();
            }
         }
         catch (Exception) { }
      }

      private static void SaveSessionId(string fileName)
      {
         try
         {
            using (FileStream fs = File.OpenWrite(fileName))
            {
               byte[] bc = BitConverter.GetBytes(sessionID);
               fs.Write(bc, 0, bc.Length);
               fs.Close();
            }
         }
         catch (Exception) { }
      }

      private void AssignSessionID(PacketObject res)
      {
         if (res.Count > 0)
         {
            ObjectList answ = res[0];
            if (answ.Count > 0 && answ.Name == "ServerAnswer")
            {
               Object ansObj = answ[0];
               Member m = ansObj.GetMember("response");
               if (m != null && Convert.ToDouble(m.Value) > 0)
               {
                  m = ansObj.GetMember("message");
                  if (m != null)
                  {
                     if (UInt32.TryParse(Convert.ToString(m.Value), System.Globalization.NumberStyles.HexNumber, null, out sessionID))
                        SaveSessionId(fileName);
                  }
               }
            }
         }
      }

      private PacketObject SendRequest(PacketObject po, IProgress progress)
      {
         PacketObject res = null;

         try
         {
            TcpClient client = new TcpClient();

            client.SendTimeout = TIMEOUT;
            client.ReceiveTimeout = ReceiveTimeout;
            client.NoDelay = true;

            if (GRPacket.GRJSHelper.IsJSAddress(ip))
            {
               string error;
               GRPacket.GRJSHelper.GRJSConnect(ip, client, out error);
            }
            else
            {
               IPAddress addr;
               if (!IPAddress.TryParse(ip, out addr))
               {
                  IPAddress[] addresslist = Dns.GetHostAddresses(ip);
                  if (addresslist.Length == 0)
                     return null;
                  addr = addresslist[0];
               }
               client.Connect(addr, port);
            }

            NetworkStream stream = client.GetStream();

            stream.ReadTimeout = ReceiveTimeout;

            ServerCommand sc = po[0] as ServerCommand;
            if (sc != null)
               sc.Duration = sessionID;

            po.Send(stream);

            res = new PacketObject();
            res.Receive(stream, progress);
            AssignSessionID(res);

            po.Clear();
            po.Add(new ServerCommand(Commands.BYE_COMMAND, ""));
            po.Send(stream, "");

            stream.Close();
            client.Close();
         }
         catch (Exception)
         {
            //System.Windows.Forms.MessageBox.Show(ex.Message, "Ошибка", System.Windows.Forms.MessageBoxButtons.OK,
            //   System.Windows.Forms.MessageBoxIcon.Information);
         }

         return res;
      }

      private static object lockThis = new System.Object();
      private void SendWorker(object _param)
      {
         //System.Diagnostics.Debug.WriteLine("in send worker " + DateTime.Now);
         lock (lockThis)
         {
            SendParam param = _param as SendParam;
            PacketObject po = SendRequest(param.po, param.progress);
            if (param.handle != null)
               param.handle(po, param.upd);
         }
         //System.Diagnostics.Debug.WriteLine("out send worker " + DateTime.Now);
      }

      private void SendWorkerStr(object _param)
      {
         //System.Diagnostics.Debug.WriteLine("in send worker str " + DateTime.Now);
         lock (lockThis)
         {
            SendParamStr param = _param as SendParamStr;
            PacketObject po = SendRequest(param.po, param.progress);
            if (param.handle != null)
               param.handle(po, param.upd);
         }
         //System.Diagnostics.Debug.WriteLine("out send worker str " + DateTime.Now);
      }
   }

   /// <summary>
   /// Соединитель наборов данных и физуальных компонентов
   /// </summary>
   public class UIDataSource : BindingSource
   {
      public event DataRetrieveComplete RetrieveComplete;

      private Control uiControl;
      private IDataSet table;

      public UIDataSource(IDataSet table, System.Windows.Forms.Control uiControl)
      {
         this.table = table;
         this.uiControl = uiControl;

         DataSource = table.Data;
      }

      private void UpdateUI()
      {
         //DataSource = null;
         //DataSource = table.GetData();

         ListChangedEventArgs ee = new ListChangedEventArgs(ListChangedType.Reset, 0);
         OnListChanged(ee);

         if (RetrieveComplete != null)
         {
            RetrieveComplete();
         }
      }

      public void OnDataComplete()
      {
         uiControl.BeginInvoke(new DataRetrieveComplete(UpdateUI));
      }
   }

   /// <summary>
   /// Интерфейс "простого" набора данных, который
   /// не содежит в себе других наборов данных
   /// </summary>
   public interface IDataSet : IEnumerable
   {
      ICollection Data { get; }

      Thread Refresh(DBConnection conn, bool withReferences);
      string Name { get; set; }
      void Load(ObjectList source, List<IDataSet> sets);
      UIDataSource DataSource { get; set; }
      bool HasReference();
      List<string> GetReferences();
      void Clear();
      void Add(object key, object value);
      bool ContainsKey(object key);
      bool Remove(object key);

      ServerCommand Command { get; set; }
      string Filter { get; set; }


      Type ElementType { get; }
      Type IndexType { get; }

      Dictionary<string, object> RcvdFields { get; }
      bool KeepData { get; set; }
      int Count { get; }
      string UserID { get; set; }
   }

   /// <summary>
   /// Абстрактный класс.
   /// Реализация общего интерфейса для "простых" наборов данныз
   /// которые не содержат в себе данные из других наборов
   /// </summary>
   /// <typeparam name="DataRepType">
   /// Тип объекта, который будет отвечать за представление набора данных.
   /// </typeparam>
   public class DataSet<KeyType, DataRepType> : Dictionary<KeyType, DataRepType>, IDataSet
   {
      /// <summary>Объект связи с визуальным компонентом.</summary>
      protected UIDataSource dataSource;

      /// <summary>Имя набора данных.</summary>
      protected string name;

      protected string userid = null;

      /// <summary>Команда на выборку данных.</summary>
      protected ServerCommand serverCommand;

      private string filter;

      protected event DataSetAddingItem OnAddingItem;

      bool useRcvdFields = false;

      /// <summary>Конструктор объекта DataSet</summary>
      /// <param name="name">Имя набора данных.</param>
      /// <param name="connection">Объект для связи с сервером</param>
      /// <param name="uiControl">UI объект для обновления связанного визуального компонента</param>
      public DataSet(string name) : this(name, true)
      {
      }

      public DataSet(string name, bool addToDataModule) : this(name, addToDataModule, false)
      {
      }

      public DataSet(string name, bool addToDataModule, bool useRcvdFields)
      {
         this.name = name;
         this.useRcvdFields = useRcvdFields;

         serverCommand = new ServerCommand(Commands.SELECT, name + ":");

         if (addToDataModule)
            DataModule.AddDataSet(name, this);
      }

      public string UserID { get { return userid; } set { userid = value; } }

      public virtual void Load(ObjectList source, List<IDataSet> sets)
      {
         if (!typeof(GRSoft.Network.DataObject).IsAssignableFrom(typeof(DataRepType)))
         {
            return;
         }

         if (!KeepData)
            Clear();

         FieldInfo keyField = DataModule.GetKeyField(typeof(DataRepType));
         int defKey = Count;

         foreach (GRSoft.Network.Object o in source)
         {
            object instance = Activator.CreateInstance(typeof(DataRepType)) as GRSoft.Network.DataObject;

            if ((instance as GRSoft.Network.DataObject).Read(o, sets))
            {
               object key = null;
               if (keyField != null && typeof(KeyType).IsAssignableFrom(keyField.FieldType))
                  key = keyField.GetValue(instance);
               else
                  key = defKey++;
               if (ContainsKey((KeyType)key) == false)
                  Add((KeyType)key, (DataRepType)instance);

               if (OnAddingItem != null)
                  OnAddingItem(instance);
            }
         }

         LoadComplete();
      }

      protected virtual void LoadComplete()
      {
         if (dataSource != null)
         {
            dataSource.OnDataComplete();
         }
      }

      public bool HasReference()
      {
         return GetReferences().Count > 0;
      }

      public List<string> GetReferences()
      {
         List<string> result = new List<string>();

         FieldInfo[] fields = typeof(DataRepType).GetFields(BindingFlags.Public | BindingFlags.Instance);

         foreach (FieldInfo fi in fields)
         {
            object[] atts = fi.GetCustomAttributes(false);
            foreach (object at in atts)
            {
               if (at.GetType() == typeof(ReferenceAttribute))
               {
                  result.Add((at as ReferenceAttribute).DataSet);
               }
            }
         }

         return result;
      }

      public void Add(object key, object value) { base.Add((KeyType)key, (DataRepType)value); }
      public bool ContainsKey(object key) { return base.ContainsKey((KeyType)key); }
      public bool Remove(object key) { return base.Remove((KeyType)key); }

      /// <summary>
      /// Возвращает список List<T> данных ассоциированные с текущим набором данных
      /// </summary>
      /// <returns>
      /// Список объектов набора данных тип задается шаблоном набора данных
      /// </returns>
      /// 
      public ICollection Data
      {
         get { return Values; }
      }

      public IList ValueList
      {
         get
         {
            List<DataRepType> list = new List<DataRepType>();
            list.AddRange(Values);
            return list;
         }
      }

      public Type ElementType
      {
         get { return typeof(DataRepType); }
      }

      public Type IndexType
      {
         get { return typeof(KeyType); }
      }

      /// <summary>Обновить набор данных.</summary>
      public virtual Thread Refresh(DBConnection connection)
      {
         return Refresh(connection, true);
      }

      /// <summary>Обновить набор данных.</summary>
      public virtual Thread Refresh(DBConnection connection, bool withReferences)
      {
         Clear();
         return DataModule.RefreshDataSet(this, connection, withReferences, null);
      }

      public string Filter
      {
         get { return filter; }
         set
         {
            if (filter != value)
            {
               filter = value;
               Command[ServerCommand.PARAM_TAG].Value = Name + ":" + filter;
            }
         }
      }

      /// <summary>Имя набора данных</summary>
      public string Name { get { return name; } set { name = value; } }

      public UIDataSource DataSource { get { return dataSource; } set { dataSource = value; } }

      public ServerCommand Command { get { return serverCommand; } set { serverCommand = value; } }

      public bool UseReceivedFields { get { return useRcvdFields; } set { useRcvdFields = value; } }

      public Dictionary<string, object> RcvdFields
      {
         get
         {
            if (!useRcvdFields)
               return null;

            IEnumerator ie = Values.GetEnumerator();
            if (ie.MoveNext() == false)
               return null;

            return ((DataObject)ie.Current).srvFields;
         }
      }

      bool keepData = false;
      public virtual bool KeepData { get { return keepData; } set { keepData = value; } }
   }

   public class SimpleDataSet<DataType> : DataSet<int, DataType>
   {
      public SimpleDataSet(string name) :
         base(name)
      {
      }

      public SimpleDataSet(string name, bool addToModule) :
         base(name, addToModule)
      {
      }

      public SimpleDataSet(string name, bool addToModule, bool useRcvdFields) :
         base(name, addToModule, useRcvdFields)
      {
      }

      public void Add(DataType obj)
      {
         if (lastKey == -1)
            InitLastKey();
         Add(++lastKey, obj);
      }

      private void InitLastKey()
      {
         foreach (int k in Keys)
         {
            if (k > lastKey)
               lastKey = k;
         }
      }

      Int32 lastKey = -1;
   }

   public class Report : DataSet<int, DataObject>
   {
      public static readonly string OBJECT_NAME = "Report";

      public Report(string name, IDataSet param, List<IDataSet> resultSet)
         : base(OBJECT_NAME, false)
      {
         serverCommand = new ServerCommand(Commands.GET_REPORT, name);
         this.resultSet = resultSet;
         this.param = param;
      }

      public Report(string name, DataObject param, IDataSet result)
         : base(OBJECT_NAME, false)
      {
         serverCommand = new ServerCommand(Commands.GET_REPORT, name);

         this.resultSet = new List<IDataSet>();
         if (result != null)
            this.resultSet.Add(result);
         this.param = CreateSimplDataSet(param);
      }

      public static IDataSet CreateSimplDataSet(DataObject param)
      {
         Type t = typeof(SimpleDataSet<>).MakeGenericType(param.GetType());
         IDataSet ret = Activator.CreateInstance(t, new object[] { "Param", false }) as IDataSet;
         MethodInfo mi = t.GetMethod("Add", new Type[] { param.GetType() });
         mi.Invoke(ret, new object[] { param });
         return ret;
      }

      public IDataSet Param { get { return param; } }
      public List<IDataSet> Result { get { return resultSet; } }

      IDataSet param;
      public List<IDataSet> resultSet;
   }

   /// <summary>
   /// IDataSet + userID
   /// </summary>
   public class ReplacedSet
   {
      public string userID;
      public IDataSet data;
      public bool haveUserID = true;
      public bool dontRemove = false;

      ServerCommand removeCommand = null;

      public ReplacedSet(string userID, IDataSet data)
      {
         this.userID = userID;
         this.data = data;
      }

      public ReplacedSet(IDataSet data)
      {
         this.userID = null;
         this.data = data;
         this.haveUserID = false;
      }

      public ServerCommand RemoveCommand
      {
         set { removeCommand = value; }
         get
         {
            if (!dontRemove)
            {
               if (removeCommand == null)
               {
                  string filter = data.Name + ":";
                  if (haveUserID)
                  {
                     filter += "\"userid\"";
                     if (userID != null) filter += " = '" + userID + "'";
                     else filter += " is null";
                  }

                  removeCommand = new ServerCommand(Commands.REMOVE, filter);
               }
            }
            return removeCommand;
         }
      }
   }

   class DataSetList : List<IDataSet>
   {
      public IDictionary FindDataSet(string name)
      {
         IDictionary d = null;
         foreach (IDataSet ds in this)
            if (ds.Name == name)
            {
               d = ds as IDictionary;
               break;
            }

         return d;
      }
   }

   /// <summary>
   /// Объект хранит наборы данных
   /// </summary>
   public sealed class DataModule
   {
      private static Dictionary<string, IDataSet> globalDataSets = new Dictionary<string, IDataSet>();
      private static Dictionary<string, DataSetList> userDataSets = new Dictionary<string, DataSetList>();
      private static string currentUserID;

      /// <summary>
      /// Событие окончание выборки данных
      /// </summary>
      public static event EventHandler DataProcessed = null;

      /// <summary>
      /// Событие ошибка при получении данных
      /// </summary>
      public static event EventDataResponseError OnDataResponceError = null;

      /// <summary>
      /// Текущий пользователь от которого берутся данные из userDataSets
      /// </summary>
      public static string CurrentUser
      {
         get { return currentUserID; }
         set { currentUserID = value; }
      }

      public static void ClearDataSets()
      {
         foreach (KeyValuePair<string, IDataSet> de in globalDataSets)
         {
            de.Value.Clear();
         }

         foreach (KeyValuePair<string, DataSetList> de in userDataSets)
         {
            foreach (IDataSet ds in de.Value)
               ds.Clear();
         }
      }

      public static void EndSession(DBConnection conn, bool wait)
      {
         ServerCommand sc = new ServerCommand(Commands.QUIT_COMMAND, "");
         sc.UserID = conn.login;
         sc.Password = conn.password;
         Thread t = conn.SendCommand(new SendParam(sc, null, null));
         if (wait)
            t.Join();
      }

      /// <summary>
      /// </summary>
      /// <param name="name"></param>
      /// <param name="conn"></param>
      /// <returns></returns>
      public static Thread RefreshDataSet(IDataSet set, DBConnection conn, bool withReferences, IProgress progress)
      {
         ServerCommand commands = MakeComplexCommand(set);
         commands.UserID = conn.login;
         commands.Password = conn.password;
         List<IDataSet> updDS = new List<IDataSet>();
         if (withReferences)
         {
            IList<string> refs = set.GetReferences();
            foreach (string rs in refs)
            {
               IDataSet refSet = globalDataSets[rs];
               if (refSet != null)
                  updDS.Add(refSet);
            }
         }
         updDS.Add(set);
         SendParam sp = new SendParam(commands, DataProcessRetrieve, updDS);
         sp.progress = progress;
         return conn.SendCommand(sp);
      }

      public static IDataSet GetUserDataSet(string userid, string setName, Type setType, bool makeImperonateCommand)
      {
         IDataSet ret = null;
         if (userDataSets.ContainsKey(userid))
         {
            List<IDataSet> cl = userDataSets[userid];
            foreach (IDataSet ds in cl)
            {
               if (ds.GetType() == setType)
               {
                  ret = ds;
                  break;
               }
            }
         }
         else
         {
            userDataSets[userid] = new DataSetList();
         }

         if (ret == null)
         {
            ret = (IDataSet)Activator.CreateInstance(setType, new object[] { setName, false });
            if (makeImperonateCommand)
               ret.Command = new ServerCommand(Commands.Impersonate(Commands.GET, userid), setName);

            ret.UserID = userid;
            userDataSets[userid].Add(ret);
         }

         currentUserID = userid;
         return ret;
      }

      public static IDataSet GetUserDataSet(string userid, string setName, Type setType)
      {
         return GetUserDataSet(userid, setName, setType, false);
      }

      public static bool RemoveDataSet(IDataSet set, DBConnection conn)
      {
         List<string> writed = new List<string>();
         PacketObject po = new PacketObject();

         string filter = set.Name + ":";
         if (set.Filter != null && set.Filter.Length > 0)
            filter += set.Filter;

         ServerCommand cmd = new ServerCommand(Commands.REMOVE, filter);
         cmd.Password = conn.password;
         cmd.UserID = conn.login;
         po.Add(cmd);

         writed.Add(set.Name);
         Thread t = conn.SendCommand(new SendParamStr(po, CheckWrited, writed));
         t.Join();
         return (writed.Count == 0);
      }

      public static bool ReplaceDataSet(IDataSet set, DBConnection conn)
      {
         return ReplaceDataSet(set, conn, set.Name);
      }

      public static bool ReplaceDataSet(IDataSet set, DBConnection conn, String filter)
      {
         List<IDataSet> wr = new List<IDataSet>();
         List<string> writed = new List<string>();
         PacketObject po = new PacketObject();

         wr.Add(set);

         int div = filter.IndexOf(':');
         if (div > 0)
         {
            string ds = filter.Substring(0, div);
            writed.Add(ds);
         }
         else
         {
            filter = set.Name + ':' + filter;
            writed.Add(set.Name);
         }
         ServerCommand cmd = new ServerCommand(Commands.REMOVE, filter);
         cmd.Password = conn.password;
         cmd.UserID = conn.login;
         po.Add(cmd);

         if (set.Count > 0)
         {
            cmd = new ServerCommand(Commands.FORCE_PUT, "");
            cmd.Password = conn.password;
            cmd.UserID = conn.login;
            po.Add(cmd);

            PrepareWrited(wr, writed, po);
         }
         Thread t = conn.SendCommand(new SendParamStr(po, CheckWrited, writed));
         t.Join();
         return (writed.Count == 0);
      }

      public static bool ReplaceDataSet(IDataSet set, string userID, DBConnection conn)
      {
         List<IDataSet> wr = new List<IDataSet>();
         List<string> writed = new List<string>();
         PacketObject po = new PacketObject();

         wr.Add(set);

         string filter = set.Name + ":\"userid\"";
         if (userID != null) filter += " = '" + userID + "'";
         else filter += " is null";

         ServerCommand cmd = new ServerCommand(Commands.REMOVE, filter);
         cmd.Password = conn.password;
         cmd.UserID = conn.login;

         po.Add(cmd);

         cmd = new ServerCommand(Commands.Impersonate(Commands.FORCE_PUT, userID), "");
         cmd.Password = conn.password;
         cmd.UserID = conn.login;
         po.Add(cmd);

         PrepareWrited(wr, writed, po);

         Thread t = conn.SendCommand(new SendParamStr(po, CheckWrited, writed));
         t.Join();
         return (writed.Count == 0);
      }

      public static bool SendMessage(string message, string userID, DBConnection conn)
      {
         List<string> writed = new List<string>();
         PacketObject po = new PacketObject();

         ServerCommand cmd = new ServerCommand(Commands.Impersonate(Commands.FORCE_PUT, userID), "");
         cmd.Password = conn.password;
         cmd.UserID = conn.login;
         po.Add(cmd);

         po.Add(new MessageObject(message));

         writed.Add("Message");
         Thread t = conn.SendCommand(new SendParamStr(po, CheckWrited, writed));
         t.Join();

         return (writed.Count == 0);
      }

      /// <summary>
      /// Поиск полей помеченных указанным аттрибутом у данного типа
      /// </summary>
      private static List<FieldInfo> FindField(Type attrType, Type dataType)
      {
         List<FieldInfo> fil = new List<FieldInfo>();
         FieldInfo[] fields = dataType.GetFields(BindingFlags.Public | BindingFlags.Instance);

         foreach (FieldInfo field in fields)
         {
            object[] atts = field.GetCustomAttributes(attrType, false);
            if (atts.Length > 0)
               fil.Add(field);
         }

         return fil;
      }

      private static FieldInfo FindKeyForInsert(Type dataType)
      {
         FieldInfo ret = null;
         List<FieldInfo> check = FindField(typeof(KeyFieldAttribute), dataType);
         if (check.Count == 1)
         {
            FieldInfo checkField = check[0];
            if (checkField.FieldType == typeof(Int32) || checkField.FieldType == typeof(Int64))
               ret = checkField;
         }

         return ret;
      }

      class UpdaterID : IDataSet
      {
         private FieldInfo keyField;
         private IDataSet src;

         public UpdaterID(FieldInfo keyField, IDataSet src)
         {
            this.keyField = keyField;
            this.src = src;
         }

         public bool Update(ObjectList result)
         {
            bool ret = false;
            ICollection data = src.Data;
            if (data.Count == result.Count)
            {
               bool is32 = (keyField.FieldType == typeof(Int32));
               int i = 0;
               foreach (object dest in data)
               {
                  object value = result[i][0].Value;
                  if (is32)
                     value = Convert.ToInt32(value);
                  else
                     Convert.ToInt64(value);
                  keyField.SetValue(dest, value);
                  i++;
               }

               ret = true;
            }

            return ret;
         }

         #region Члены IDataSet

         public ICollection Data { get { return src.Data; } }
         public Thread Refresh(DBConnection conn, bool withReferences) { return src.Refresh(conn, withReferences); }
         public void Clear() { src.Clear(); }
         public bool HasReference() { return src.HasReference(); }
         public List<string> GetReferences() { return src.GetReferences(); }
         public Type ElementType { get { return src.ElementType; } }
         public Type IndexType { get { return src.IndexType; } }
         public IEnumerator GetEnumerator() { return src.GetEnumerator(); }
         public void Load(ObjectList source, List<IDataSet> sets) { src.Load(source, sets); }
         public void Add(object key, object value) { src.Add(key, value); }
         public bool ContainsKey(object key) { return src.ContainsKey(key); }
         public bool Remove(object key) { return src.Remove(key); }
         public ServerCommand Command
         {
            get { return src.Command; }
            set { src.Command = value; }
         }
         public string Filter
         {
            get { return src.Filter; }
            set { src.Filter = value; }
         }
         public string Name
         {
            get { return src.Name; }
            set { src.Name = value; }
         }
         public UIDataSource DataSource
         {
            get { return src.DataSource; }
            set { src.DataSource = value; }
         }

         public Dictionary<string, object> RcvdFields { get { return null; } }

         public bool KeepData { get { return false; } set { } }
         public int Count { get { return src.Count; } }
         #endregion

         #region IDataSet Members
         #endregion


         public string UserID { get { return null; } set { } }
      }

      class UpdaterList : List<IDataSet>
      {
         private bool done = false;

         private string cmpName = "";
         private bool CmpName(IDataSet obj) { return (cmpName == obj.Name); }

         private bool CheckAnswer(Object result)
         {
            bool res = false;

            if (result.Name == "ServerAnswer")
               res = ((double)result["response"].Value > 0);

            return res;
         }

         public void UpdateData(PacketObject result)
         {
            done = false;
            if (result == null || result.Count == 0)
               return;

            int updated = 0;
            for (int li = 0; li < result.Count; li++)
            {
               ObjectList ol = result[li];
               for (int oi = 0; oi < ol.Count; oi++)
               {
                  Object o = ol[oi];
                  if (li == 0 && oi == 0)
                  {
                     if (!CheckAnswer(o))
                        break;
                     continue;
                  }

                  if (o.Name != "ServerAnswer")
                     continue;

                  cmpName = (string)o["message"].Value;
                  if ((double)o["response"].Value <= 0) // не прошла вставка - надо бы обработать ошибку
                     continue;

                  UpdaterID upd = Find(CmpName) as UpdaterID;
                  if (upd != null)
                  {
                     // после объекта ServerAnswer должен идти объект с данными, проверим это
                     if (oi == ol.Count - 1 && li < result.Count - 1)
                     {
                        li++;
                        upd.Update(result[li]);
                        updated++;
                     }
                  }
               }
            }

            done = (Count == updated);
         }

         public bool Done { get { return done; } }
      }

      private static Format MakeInsertFormat(Format src, string keyField)
      {
         Format format = new Format(src.Name);

         foreach (MemberFormat member in src)
         {
            if (member.name != keyField)
               format.Add(member);
         }
         return format;
      }

      private static UpdaterID PrepareInsert(PacketObject po, IDataSet set)
      {
         UpdaterID ret = null;
         ICollection data = set.Data;
         if (data.Count > 0)
         {
            FieldInfo keyField = FindKeyForInsert(set.ElementType);
            if (keyField == null)
               throw new ArgumentException(set.ElementType.ToString() + " не подоходит для InsertDataSets");

            Format dataFormat = Format.FindOrCreate(set);
            Format insertFormat = MakeInsertFormat(dataFormat, keyField.Name);

            if (AddDataSet(po, insertFormat, set))
               ret = new UpdaterID(keyField, set);
         }
         return ret;
      }

      private static void HandleInsertResponse(PacketObject result, List<IDataSet> sets)
      {
         UpdaterList ul = sets as UpdaterList;
         if (ul != null)
            ul.UpdateData(result);
      }

      /// <summary>
      /// Работает только для объектов у которых ключевое поле Int32 || Int64
      /// </summary>
      public static bool InsertDataSets(List<IDataSet> insertSet, DBConnection conn)
      {
         if (insertSet.Count == 0)
            return true;

         foreach (IDataSet set in insertSet)
         {
            FieldInfo keyField = FindKeyForInsert(set.ElementType);
            if (keyField != null)
            {
               foreach (object o in set.Data)
               {
                  Guid g = Guid.NewGuid();
                  byte[] b = g.ToByteArray();

                  bool is32 = (keyField.FieldType == typeof(Int32));
                  if (is32)
                  {
                     Int32 val = BitConverter.ToInt32(b, 12);
                     keyField.SetValue(o, val);
                  }
                  else
                  {
                     Int64 val = BitConverter.ToInt64(b, 8);
                     keyField.SetValue(o, val);
                  }
               }
            }
         }

         return UpdateDataSet(insertSet, null, null, conn);

         //UpdaterList updater = new UpdaterList();
         //PacketObject po = new PacketObject();

         //ServerCommand cmd = new ServerCommand(Commands.FORCE_PUT, Commands.RET_IDS);
         //cmd.Password = conn.password;
         //cmd.UserID = conn.login;
         //po.Add(cmd);
         //foreach (IDataSet set in insertSet)
         //{
         //   UpdaterID updID = PrepareInsert(po, set);
         //   if (updID != null)
         //      updater.Add(updID);
         //}

         //Thread t = conn.SendCommand(new SendParam(po, HandleInsertResponse, updater));
         //t.Join();
         //return updater.Done;
      }

      public static bool WriteDataSet(List<IDataSet> wrSet, DBConnection conn)
      {
         return UpdateDataSet(wrSet, null, null, conn, null);
      }

      public static bool UpdateDataSet(List<IDataSet> wrSet, List<IDataSet> rmvSet, List<ReplacedSet> rplSet, DBConnection conn)
      {
         return UpdateDataSet(wrSet, rmvSet, rplSet, conn, null);
      }

      public static bool PutNoExec(List<IDataSet> wrSet, DBConnection conn)
      {
         List<string> writed = new List<string>();

         PacketObject po = new PacketObject();

         ServerCommand cmd = new ServerCommand(Commands.PUT_NO_EXEC, "");
         cmd.Password = conn.password;
         cmd.UserID = conn.login;

         po.Add(cmd);

         PrepareWrited(wrSet, writed, po);

         if (po.Count <= 1)
            return false;

         Thread t = conn.SendCommand(new SendParamStr(po, CheckWrited, writed));
         t.Join();

         return (writed.Count == 0);
      }

      public static bool UpdateDataSet(List<IDataSet> wrSet, List<IDataSet> rmvSet, List<ReplacedSet> rplSet, DBConnection conn, string userid)
      {
         List<string> writed = new List<string>();

         PacketObject po = new PacketObject();

         if (wrSet != null && wrSet.Count > 0)
            AddWrited(wrSet, conn, writed, po, userid);

         if (rmvSet != null && rmvSet.Count > 0)
            AddRemoved(rmvSet, conn, writed, po);

         if (rplSet != null && rplSet.Count > 0)
         {
            foreach (ReplacedSet rs in rplSet)
            {
               ServerCommand cmd = rs.RemoveCommand;
               if (cmd != null)
               {
                  cmd.Password = conn.password;
                  cmd.UserID = conn.login;

                  po.Add(cmd);
               }
               if (rs.data.Data.Count > 0)
               {
                  List<IDataSet> wr = new List<IDataSet>();
                  wr.Add(rs.data);

                  if (rs.haveUserID)
                     cmd = new ServerCommand(Commands.Impersonate(Commands.FORCE_PUT, rs.userID), "");
                  else
                     cmd = new ServerCommand(Commands.FORCE_PUT, "");
                  cmd.Password = conn.password;
                  cmd.UserID = conn.login;
                  po.Add(cmd);

                  PrepareWrited(wr, writed, po);
               }
            }
         }

         if (po.Count == 0)
            return false;

         Thread t = conn.SendCommand(new SendParamStr(po, CheckWrited, writed));
         t.Join();

         return (writed.Count == 0);
      }

      private static void AddRemoved(List<IDataSet> sets, DBConnection conn, List<string> writed, PacketObject po)
      {
         foreach (IDataSet set in sets)
         {
            List<FieldInfo> keys = null;
            foreach (DataObject d in set.Data)
            {
               if (keys == null)
               {
                  keys = d.GetKeyFields();
                  if (keys == null || keys.Count == 0)
                     break;
               }

               bool first = true;
               string filter = set.Name + ":";
               foreach (FieldInfo keyField in keys)
               {
                  object value = keyField.GetValue(d);
                  if (value != null)
                  {
                     if (first) first = false;
                     else filter += " AND ";
                     filter += '"' + keyField.Name + '"' + "=";
                     if (value is int || value is double)
                     {
                        filter += value.ToString();
                     }
                     else if (value is string)
                     {
                        filter += "'" + value.ToString() + "'";
                     }
                     else if (value is DateTime)
                     {
                        filter += "ToDate('" + ((DateTime)value).ToString("dd-MM-yyyy HH:mm:ss") + "')";
                     }
                     else
                        break;
                  }
               }
               ServerCommand cmd = new ServerCommand(Commands.REMOVE, filter);
               cmd.Password = conn.password;
               cmd.UserID = conn.login;

               writed.Add(set.Name);

               po.Add(cmd);

            }
         }
      }

      private static void AddWrited(List<IDataSet> wrSet, DBConnection conn, List<string> writed, PacketObject po)
      {
         AddWrited(wrSet, conn, writed, po, null);
      }

      private static void AddWrited(List<IDataSet> wrSet, DBConnection conn, List<string> writed, PacketObject po, string userid)
      {
         ServerCommand cmd = new ServerCommand(userid == null ?
            Commands.FORCE_PUT :
            Commands.Impersonate(Commands.FORCE_PUT, userid),
            "");
         cmd.Password = conn.password;
         cmd.UserID = conn.login;

         po.Add(cmd);

         PrepareWrited(wrSet, writed, po);
      }

      private static bool AddDataSet(PacketObject po, Format dataFormat, IDataSet dataSet)
      {
         bool ret = false;


         if (dataSet.Count > 0)
         {
            IEnumerator ie = dataSet.Data.GetEnumerator();
            ie.MoveNext();

            ObjectWriter ow = new ObjectWriter();
            if (ow.CreateWriter(dataFormat, ie.Current as DataObject, dataSet.RcvdFields))
            {
               ObjectList destList = new ObjectList(dataFormat);

               ICollection data = dataSet.Data;
               foreach (DataObject src in data)
               {
                  Object dest = destList.AddObject();
                  ow.Write(dest, src);
               }

               po.Add(destList);
               ret = true;
            }
         }

         return ret;
      }

      private static void PrepareWrited(List<IDataSet> wrSet, List<string> writed, PacketObject po)
      {
         foreach (IDataSet dataSet in wrSet)
         {
            Format dataFormat = Format.FindOrCreate(dataSet);

            if (dataSet.Data.Count > 0 && AddDataSet(po, dataFormat, dataSet))
               writed.Add(dataSet.Name);
         }
      }

      public static void CheckWrited(PacketObject source, List<string> sets)
      {
         if (source != null)
         {
            foreach (ObjectList ol in source)
            {
               if (ol.Name == "ServerAnswer")
               {
                  foreach (Object o in ol)
                  {
                     if ((double)o["response"].Value > 0)
                     {
                        string str = (string)o["message"].Value;
                        sets.Remove(str);
                     }
                  }
               }
            }
         }
      }

      /// <summary>
      /// Обновить все наборы данных, берем команды только от наборов данных,
      /// предполагая что вложенные наборы данных есть в DateModule и они имеют
      /// команды для выборки
      /// </summary>
      /// <param name="conn"></param>
      /// <returns></returns>
      public static Thread RefreshAll(DBConnection conn, IProgress progress)
      {
         return RefreshGiveSets(conn, new List<IDataSet>(globalDataSets.Values), progress);
      }

      public static Thread RefreshGiveSets(DBConnection conn, IDataSet dataSet, IProgress progress)
      {
         List<IDataSet> ls = new List<IDataSet>(new IDataSet[] { dataSet });
         return RefreshGiveSets(conn, ls, progress);
      }

      public static PacketObject PrepareListToRetrieve(string login, string password, List<IDataSet> list)
      {
         PacketObject po = new PacketObject();
         ServerCommand commands = null;

         List<IDataSet> added = new List<IDataSet>();
         foreach (IDataSet ds in list)
         {
            if (commands == null)
            {
               commands = new ServerCommand(ds.Command[ServerCommand.COMMAND_TAG].Value.ToString(),
                  ds.Command[ServerCommand.PARAM_TAG].Value.ToString());
               commands.UserID = login;
               commands.Password = password;

               po.Add(commands);
            }
            else
            {
               GRSoft.Network.Object simpleCmd = commands.AddObject();
               simpleCmd[ServerCommand.COMMAND_TAG].Value = ds.Command[ServerCommand.COMMAND_TAG].Value;
               simpleCmd[ServerCommand.PARAM_TAG].Value = ds.Command[ServerCommand.PARAM_TAG].Value;
            }

            Report r = ds as Report;
            if (r != null)
            {
               Format dataFormat = Format.Create(r.Param.Name, r.Param.ElementType);
               AddDataSet(po, dataFormat, r.Param);
               commands = null;

               added.AddRange(r.Result);
            }
         }

         list.AddRange(added);
         return po;
      }

      //Обновить наборы данных из списка
      public static Thread RefreshGiveSets(DBConnection conn, List<IDataSet> list, IProgress progress)
      {
         PacketObject po = PrepareListToRetrieve(conn.login, conn.password, list);

         SendParam sp = new SendParam(po, DataProcessRetrieve, list);
         sp.progress = progress;
         return conn.SendCommand(sp);
      }

      //Обновить наборы данных из массива
      public static Thread RefreshGiveSets(DBConnection conn, object[] ds, IProgress progress)
      {
         List<IDataSet> l = new List<IDataSet>(ds.Length);
         foreach (object d in ds)
         {
            l.Add(d as IDataSet);
         }
         return RefreshGiveSets(conn, l, progress);
      }

      /// <summary>
      /// Собрать команду из главного и вложенных наборов
      /// </summary>
      /// <param name="name"></param>
      /// <returns></returns>
      private static ServerCommand MakeComplexCommand(IDataSet ds)
      {
         ServerCommand curCmd = ds.Command;

         List<string> refds = ds.GetReferences();

         foreach (string needUpd in refds)
         {
            IDataSet child = globalDataSets[needUpd];
            foreach (GRSoft.Network.Object obj in MakeComplexCommand(child))
            {
               curCmd.Insert(0, obj);
            }
         }

         return curCmd;
      }

      static bool CheckResponse(PacketObject result, out string response)
      {
         if (result == null || result.Count == 0)
         {
            response = "Сервер не отвечает";
            return false;
         }

         ObjectList answ = result[0];

         if (answ.Name != "ServerAnswer")
         {
            response = "Не правильный ответ сервера " + answ.Name;
            return false;
         }

         GRSoft.Network.Object o = answ[0];
         bool res = ((double)o["response"].Value == 1.0);
         response = (string)o["message"].Value;

         return res;
      }

      static Dictionary<String, String> dataSetAliases = new Dictionary<string, string>();
      public static void AddAlias(string oldName, string newName)
      {
         dataSetAliases[oldName] = newName;
      }

      /// <summary>
      /// Процесс выборки данных и заполнения наборов данных
      /// </summary>
      /// <param name="source"></param>
      /// <param name="sets"></param>
      public static void DataProcessRetrieve(PacketObject source, List<IDataSet> sets)
      {
         //System.Diagnostics.Debug.WriteLine("DataProcessRetrieve enter " + sets.Count.ToString());

         string answer;
         if (!CheckResponse(source, out answer))
         {
            if (OnDataResponceError != null)
            {
               OnDataResponceError(new EDataResponse(answer));
            }

            return;
         }

         if (source != null)
         {
            foreach (IDataSet aset in sets)
            {
               if (!aset.KeepData)
                  aset.Clear();

               ObjectList ol = null;
               if (aset.UserID != null)
               {
                  foreach (ObjectList curO in source)
                  {
                     if (curO.Name != aset.Name)
                        continue;

                     // берем любой
                     ol = curO;
                     int indx = curO.FindField("userid");
                     if (indx < 0 || curO.Count == 0)
                        continue;

                     string uid = curO[0][indx].ToString();
                     //if( uid.Length == 0 )
                     //{
                     //   // если не заполнено (как в Вятиче на сервере) возьмем хотябы что-то
                     //   ol = curO;
                     //   continue;
                     //}
                     if (uid == aset.UserID)
                     {
                        // если нужный - то выходим
                        break;
                     }
                  }
               }
               else
                  ol = source[aset.Name];
               if (ol != null)
               {
                  aset.Load(ol, sets);
                  //if (ol.Name == "ManagerFolder")
                  //{
                  //   System.Diagnostics.Debug.WriteLine("MF:" + ol.Count.ToString() + "/" + aset.Count.ToString());
                  //}
                  source.Remove(ol);
               }
            }
         }
         if (DataProcessed != null)
         {
            //System.Diagnostics.Debug.WriteLine("DataProcessRetrieve DataProcessed ");
            DataProcessed(null, new EventArgs());
         }
      }

      static public void AddDataSet(string name, IDataSet d)
      {
         if (!globalDataSets.ContainsKey(name))
         {
            globalDataSets[name] = d;
         }
      }

      static public void AddDataSet(IDataSet d)
      {
         AddDataSet(d.Name, d);
      }

      static public void Remove(string name)
      {
         if (globalDataSets.ContainsKey(name))
            globalDataSets.Remove(name);
      }

      static public void Remove(IDataSet d)
      {
         if (globalDataSets.ContainsKey(d.Name))
            globalDataSets.Remove(d.Name);
      }

      static public IDataSet Get(string name)
      {
         return (globalDataSets.ContainsKey(name)) ? globalDataSets[name] : null;
      }

      static public bool Load(DataObject dest, GRSoft.Network.Object src, List<IDataSet> sets)
      {
         Type t = dest.GetType();
         FieldInfo[] fields = t.GetFields(BindingFlags.Public | BindingFlags.Instance);

         for (int i = 0; i < src.Format.Count; i++)
            dest.srvFields.Add(src.Format[i].name, src[i].Value);

         foreach (FieldInfo fld in fields)
         {
            Member field;
            string name = fld.Name;
            string nameRest = "";
            object[] atts = fld.GetCustomAttributes(false);
            bool dataAssigned = false;

            // set source field name
            object[] dfAttribute = fld.GetCustomAttributes(typeof(DataFieldAttribute), false);
            if (dfAttribute.Length > 0)
            {
               name = (dfAttribute[0] as DataFieldAttribute).Name;
               int pos = name.IndexOf(".");
               if (pos > 0)
               {
                  nameRest = name.Substring(pos + 1);
                  name = name.Substring(0, pos);
               }
            }

            if (typeof(IList).IsAssignableFrom(fld.FieldType))
            {
               Type[] tp = fld.FieldType.GetGenericArguments();
               if (tp.Length > 0)
               {
                  Type itemT = tp[0];
                  LoadList(dest, src.GetMember(name), fld, itemT, sets);
                  dataAssigned = true;
               }
            }
            else
            {
               foreach (object att in atts)
               {
                  Type attType = att.GetType();
                  if (attType == typeof(ReferenceAttribute))
                  {
                     AssignReference(dest, src, fld, att as ReferenceAttribute, sets);
                     dataAssigned = true;
                     break;
                  }
               }
            }

            if (!dataAssigned)
            {
               field = src.GetMember(name);
               if (field != null)
                  SetFieldValue(dest, fld, field, nameRest);
            }

            if (dest.srvFields.ContainsKey(name))
               dest.srvFields.Remove(name);
         }

         return true;
      }

      private static bool LoadList(DataObject dest, Member field, FieldInfo fld, Type itemType, List<IDataSet> sets)
      {
         bool ret = false;
         ObjectList data = null;

         if (field == null || (data = field.Value as ObjectList) == null)
            return ret;

         if (!fld.FieldType.IsArray && typeof(IList).IsAssignableFrom(fld.FieldType))
         {
            IList dlist = fld.GetValue(dest) as IList;
            if (dlist == null)
            {
               dlist = Activator.CreateInstance(fld.FieldType) as IList;
               fld.SetValue(dest, dlist);
            }

            foreach (GRSoft.Network.Object o in data)
            {
               DataObject v = Activator.CreateInstance(itemType) as DataObject;
               if (Load(v, o, sets))
                  dlist.Add(v);
            }
         }
         return ret;
      }

      private static void SetFieldValue(DataObject dest, FieldInfo fld, Member field, string nameRest)
      {
         object value = field.Value;
         if (fld.FieldType.IsArray && nameRest.Length > 0)
         {
            value = null;
            ObjectList ol = field.Value as ObjectList;
            if (ol != null)
            {
               Array array = (Array)Activator.CreateInstance(fld.FieldType, new object[] { ol.Count });

               int i = 0;
               foreach (GRSoft.Network.Object ci in ol)
                  array.SetValue(ci[nameRest].Value, i++);

               value = array;
            }
         }

         if (value != null)
         {
            try
            {
               if (fld.FieldType == typeof(Int32))
                  value = Convert.ToInt32(value);
               else if (fld.FieldType == typeof(Int64))
                  value = Convert.ToInt64(value);
               else if (fld.FieldType == typeof(bool))
                  value = (Convert.ToInt32(value) != 0);
            }
            catch (Exception)
            {
               if (fld.FieldType == typeof(bool))
                  value = false;
               else
                  value = 0;
            }
            fld.SetValue(dest, value);
         }
      }

      private static object FindInUsersDataSet(string setName, object value)
      {
         object retVal = null;
         IDictionary d;
         if (currentUserID != null && userDataSets.ContainsKey(currentUserID))
         {
            d = userDataSets[currentUserID].FindDataSet(setName);
            if (d != null && d.Contains(value))
               retVal = d[value];
         }

         if (retVal == null)
         {
            foreach (KeyValuePair<string, DataSetList> el in userDataSets)
            {
               d = el.Value.FindDataSet(setName);
               if (d != null && d.Contains(value))
               {
                  retVal = d[value];
                  break;
               }
            }
         }

         return retVal;
      }

      private static void AssignReference(DataObject dest, GRSoft.Network.Object src, FieldInfo fld, ReferenceAttribute r, List<IDataSet> sets)
      {
         IDictionary d = null;

         Member field = src.GetMember(r.Field);
         if (field == null)
            return;

         bool setted = false;
         string[] dataSets = r.DataSet.Split(new char[] { ',' });
         foreach (string dataSet in dataSets)
         {
            object value;
            string dsm = dataSet;
            if (dataSetAliases.ContainsKey(dataSet))
               dsm = dataSetAliases[dataSet];

            if (sets != null)
            {
               foreach (IDataSet s in sets)
               {
                  if (s.Name == dsm)
                  {
                     IDictionary dic = s as IDictionary;
                     if (dic != null && dic.Contains(field.Value))
                     {
                        setted = true;
                        fld.SetValue(dest, dic[field.Value]);
                        break;
                     }
                  }
               }
            }

            if (!setted && (value = FindInUsersDataSet(dsm, field.Value)) != null)
            {
               setted = true;
               fld.SetValue(dest, value);
            }

            if (!setted && globalDataSets.ContainsKey(dsm))
            {
               d = globalDataSets[dsm] as IDictionary;

               if (d.Count > 0)
               {
                  Type keyType = d.Keys.GetType().GetGenericArguments()[0];

                  if (keyType != field.Value.GetType())
                     field.Value = Convert.ChangeType(field.Value, keyType);

                  if (d.Contains(field.Value))
                  {
                     setted = true;
                     fld.SetValue(dest, d[field.Value]);
                  }
               }
            }

            if (setted)
               break;
         }

         // если можно, попытаемся создать fake 
         if (!setted && r.CreateType != null && field.Value != null)
         {
            DataObject obj = null;
            // сначала попробуем найти специальный метод создания пустых элементов eg: public static Org GetEmpty(string id)
            MethodInfo mi = r.CreateType.GetMethod("GetEmpty", BindingFlags.Static | BindingFlags.Public);
            if (mi != null)
            {
               try
               {
                  obj = mi.Invoke(null, new object[] { field.Value.ToString() }) as DataObject;
               }
               catch (Exception) { }
            }

            if (obj == null)
            {
               ConstructorInfo ci = r.CreateType.GetConstructor(Type.EmptyTypes);
               if (ci != null)
               {
                  try
                  {
                     obj = ci.Invoke(new object[] { }) as DataObject;
                     if (obj != null)
                     {
                        List<FieldInfo> fi = obj.GetKeyFields();
                        if (fi.Count == 1)
                           fi[0].SetValue(obj, field.Value);

                        FieldInfo nameF = r.CreateType.GetField("name", BindingFlags.Instance | BindingFlags.Public);
                        if (nameF != null)
                        {
                           string nameV = "< не найден объект  код '" + field.Value.ToString() + "' >";
                           nameF.SetValue(obj, nameV);
                        }
                     }
                  }
                  catch (Exception) { }
               }

            }
            try
            {
               if (obj != null)
                  fld.SetValue(dest, obj);
            }
            catch (Exception)
            {
            }
         }
      }

      static FieldInfo GetField(Type type, Attribute a)
      {
         FieldInfo[] fields = type.GetFields(BindingFlags.Instance | BindingFlags.Public);
         foreach (FieldInfo field in fields)
         {
            object[] atts = field.GetCustomAttributes(a.GetType(), false);
            if (atts.Length > 0)
               return field;
         }

         return null;
      }

      public static FieldInfo GetKeyField(Type dataType)
      {
         return GetField(dataType, new KeyFieldAttribute());
      }

      public static void ClearEvents()
      {
         OnDataResponceError = null;
         DataProcessed = null;
      }

      public static void SetDataRepsonceHandlers(EventHandler dataProcessed,
         EventDataResponseError responceError)
      {
         ClearEvents();
         OnDataResponceError += responceError;
         DataProcessed += dataProcessed;
      }

   }

   public class ObjectWriter
   {
      abstract class FieldWriter
      {
         public abstract bool Write(Object dest, object data);
      }

      class StdFieldWriter : FieldWriter
      {
         public StdFieldWriter(int index)
         {
            this.index = index;
         }

         public override bool Write(Object dest, object data)
         {
            dest[index].Value = data;
            return true;
         }

         int index;
      }

      class ObjectFieldWriter : FieldWriter
      {
         public ObjectFieldWriter(int fi, Format destFormat, IList dataSet)
         {
            index = fi;
            format = destFormat;

            Type srcType = dataSet.GetType().GetGenericArguments()[0];
            writer = new ObjectWriter();

            DataObject v = null;
            Dictionary<string, object> rcvd = null;
            if (dataSet.Count > 0)
            {
               v = dataSet[0] as DataObject;
               if (v != null)
               {
                  rcvd = v.srvFields;
               }
            }
            writer.CreateWriter(format, v, rcvd);
         }

         public override bool Write(Object dest, object data)
         {
            Member m = dest[index];
            ObjectList destObj = m.ToObjectList();
            if (destObj == null)
            {
               destObj = new ObjectList(format);
               m.Value = destObj;
            }

            bool ret = false;
            ICollection dataC = data as ICollection;
            if (dataC != null)
            {
               ret = true;
               foreach (DataObject src in dataC)
               {
                  Object o = destObj.AddObject();
                  writer.Write(o, src);
               }
            }

            return ret;
         }

         ObjectWriter writer;
         Format format;
         int index;
      }

      class ReferenceFieldWriter : FieldWriter
      {
         public ReferenceFieldWriter(int index, FieldInfo fi)
         {
            this.index = index;

            FieldInfo[] finfo = fi.FieldType.GetFields(BindingFlags.Public | BindingFlags.Instance);
            foreach (FieldInfo tf in finfo)
            {
               object[] kAtt = tf.GetCustomAttributes(typeof(KeyFieldAttribute), false);
               if (kAtt.Length > 0)
               {
                  keyField = tf;
                  break;
               }
            }
         }

         public override bool Write(Object dest, object data)
         {
            if (keyField != null && data != null)
            {
               object v = keyField.GetValue(data);
               dest[index].Value = v;
            }

            return true;
         }

         int index;
         FieldInfo keyField = null;
      }

      class FieldReader
      {
         FieldInfo fi;

         protected FieldReader() { }
         public FieldReader(FieldInfo fi) { this.fi = fi; }

         public virtual object GetValue(DataObject o) { return fi.GetValue(o); }
      }

      class RcvdFieldReader : FieldReader
      {
         String name;
         public RcvdFieldReader(String name) { this.name = name; }
         public override object GetValue(DataObject o)
         {
            object res = null;
            if (o.srvFields.TryGetValue(name, out res))
               return res;
            return null;
         }
      }

      class WriterItem
      {
         public FieldWriter writer;
         public FieldReader reader;

         public WriterItem(FieldWriter fw, FieldInfo fi)
         {
            writer = fw;
            reader = new FieldReader(fi);
         }

         public WriterItem(FieldWriter fw, FieldReader fr)
         {
            writer = fw;
            reader = fr;
         }
      }

      public ObjectWriter() { }

      void GetAttributes(out DataFieldAttribute da, out ReferenceAttribute ra, out ItemTypeAttribute ia, FieldInfo field)
      {
         object[] atts = field.GetCustomAttributes(false);

         da = null;
         ra = null;
         ia = null;

         foreach (object a in atts)
         {
            if (a is DataFieldAttribute) da = a as DataFieldAttribute;
            else if (a is ReferenceAttribute) ra = a as ReferenceAttribute;
            else if (a is ItemTypeAttribute) ia = a as ItemTypeAttribute;
         }
      }

      public bool CreateWriter(Format format, DataObject sample, Dictionary<String, object> rcvdFields)
      {
         if (sample == null)
            return false;

         Type dataType = sample.GetType();
         FieldInfo[] fields = dataType.GetFields(BindingFlags.Public | BindingFlags.Instance);

         foreach (FieldInfo field in fields)
         {
            DataFieldAttribute da;
            ReferenceAttribute ra;
            ItemTypeAttribute ia;

            GetAttributes(out da, out ra, out ia, field);

            string name = (da != null) ? da.Name : (ra != null) ? ra.Field : field.Name;

            FieldWriter fw = null;
            MemberFormat mf = format.FindMember(name);
            if (mf != null)
            {
               int index = format.IndexOf(mf);
               if (mf is ObjectFormat)
               {
                  //if (ia != null)
                  //{
                  //Type srcT = ia.ItemType;
                  Type[] tp = field.FieldType.GetGenericArguments();
                  IList val = field.GetValue(sample) as IList;
                  if (val != null && tp.Length > 0)
                  {
                     Type srcT = tp[0];

                     Format destFmt = null;
                     string fname = format.Name + "$" + name;
                     //if(val.Count > 0)
                     //{
                     //    DataObject v = val[0] as DataObject;
                     //    if (v != null)
                     //        destFmt = Format.FindOrCreate(name, v);
                     //} 
                     //if(destFmt == null)
                     destFmt = Format.Find(fname);
                     if (destFmt != null)
                        fw = new ObjectFieldWriter(index, destFmt, val);
                  }
               }
               else if (ra != null)
               {
                  fw = new ReferenceFieldWriter(index, field);
               }
               else
               {
                  fw = new StdFieldWriter(index);
               }
            }
            if (fw != null)
               writer.Add(new WriterItem(fw, field));
         }

         if (rcvdFields != null)
            foreach (KeyValuePair<string, object> de in rcvdFields)
            {
               MemberFormat mf = format.FindMember(de.Key);
               if (mf != null)
               {
                  int index = format.IndexOf(mf);
                  FieldWriter fw = new StdFieldWriter(index);
                  writer.Add(new WriterItem(fw, new RcvdFieldReader(de.Key)));
               }
            }

         return (writer.Count > 0);
      }

      public bool Write(Object dest, DataObject src)
      {
         bool res = true;
         foreach (WriterItem item in writer)
         {
            object val = item.reader.GetValue(src);
            if (item.writer.Write(dest, val) == false)
            {
               res = false;
               break;
            }
         }

         return res;
      }

      List<WriterItem> writer = new List<WriterItem>();
   }


   public class DataSetFactory<DataSetType, KeyType, DataRepType> : DataSet<KeyType, DataRepType>
   {

      protected DataSetFactory()
         : base(ObjectName)
      { }

      protected DataSetFactory(bool addToDataModule)
         : base(ObjectName, addToDataModule)
      { }

      private static readonly string OBJECT_NAME = "OBJECT_NAME";

      private static string ObjectName
      {
         get
         {
            FieldInfo fi = typeof(DataRepType).GetField(OBJECT_NAME);
            return (string)fi.GetValue(null);
         }
      }

      public static DataSetType GetDataSet()
      {
         DataSetType result = (DataSetType)DataModule.Get(ObjectName);

         if (result == null)
         {
            return (DataSetType)Activator.CreateInstance(typeof(DataSetType));
         }

         return result;
      }

      public static DataSetType GetDataSet(bool addToDataModule)
      {
         ActivatorAdapter aa = new ActivatorAdapter();
         aa.addToDataModule = addToDataModule;
         return (DataSetType)Activator.CreateInstance(typeof(DataSetType), aa);
      }
   }

   public class ActivatorAdapter
   {
      public bool addToDataModule;
   }
}
