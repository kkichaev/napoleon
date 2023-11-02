using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.IO;
using System.ComponentModel;
using System.Windows.Media;
using System.Windows.Controls;
using System.Collections.ObjectModel;

namespace Ads2017
{
    public delegate void EmptyParamHandler();
    public delegate void InvokeDelegate();

    public class Agent : DataObject
    {
        public static string OBJECT_NAME = "Agents";
        public static bool id_in_str = false;

        [KeyField]
        public string id = "";
        public string name = "";
        public string login = "";
        public string password = "";
        public bool license = false;
        public int hidden = 0;

#if CHECK_LOGIN_PROGID
      public string progid = "";
#endif

#if Volnenko
      public int sklad = 0;
#endif

        public string Name { get { return name; } set { name = value; } }
        public string Login { get { return login; } set { login = value; } }
        public string Pwd { get { return password; } set { password = value; } }
        public bool License
        {
            get { return license; }
            set { license = value; }
        }

        public override string ToString()
        {
            if (id_in_str)
                return String.Format("({0}){1}", id, name);
            else
                return name;
        }

        public bool Equals(Agent agent)
        {
            return this.id == agent.id;
        }

        public bool Hidden
        {
            get { return hidden != 0; }
            set { hidden = value ? 1 : 0; }
        }
    }

    public class DivisionManager : DataObject
    {
        public static readonly string OBJECT_NAME = "DivisionManager";

        [KeyField]
        public string login = "";

        public string password = "";
        public int division = 0;
        public string prefix = string.Empty;
        public string name = string.Empty;

        public string id = string.Empty; //GUID

        public string Login { get { return login; } set { login = value; } }
        public string Password { get { return password; } set { password = value; } }
        public string Prefix { get { return prefix; } set { prefix = value; } }
        public string Name { get { return name; } set { name = value; } }

        public class Rights : DataObject
        {
            public string token = "";
            public int type = 0;
            public int right = 0;
        }
        public List<Rights> rights = new List<Rights>();

        public bool HaveRight(RightToken token, RightActions action)
        {
            foreach (Rights r in rights)
                if (r.token == token.key)
                    return action == RightActions.Read ? r.right >= 1 : r.right >= 2;

            return true;
        }

        public bool RejectForeignTaskEdit
        {
            get { return !HaveRight(RightTokens.Get("TaskWrite"), RightActions.Write); }
            set
            {
                if (value)
                {
                    Rights r = new Rights()
                    {
                        token = "TaskWrite",
                        right = (int)RightActions.Read
                    };

                    rights.Add(r);
                }
                else
                {
                    rights.RemoveAll((r) => { return r.token.Equals("TaskWrite"); });
                }
            }
        }

        public bool CanWriteTask
        {
            get { return HaveRight(RightTokens.Get("WriteADSTask"), RightActions.Write); }
        }
    }

    public class Division : DataObject, INotifyPropertyChanged
    {
        public static readonly string OBJECT_NAME = "Division";
        public event PropertyChangedEventHandler PropertyChanged;

        public class DivisionAgent : DataObject
        {
            [Reference("Agents", "id")]
            public Agent agent = new Agent();

            public string id = "";

            public string Name { get { return agent.name; } set { agent.name = value; } }
            public string Login { get => agent.login; set => agent.login = value; }
            public string Password { get => agent.password; set => agent.password = value; }

            public override string ToString()
            {
                return Name;
            }
        }

        [KeyField]
        public int id = 0;

        public string name = "";
        public string description = "";

        [Reference("Agents", "cheif")]
        public Agent cheif = null;

        [ItemType(typeof(DivisionAgent))]
        public ObservableCollection<DivisionAgent> agents = new ObservableCollection<DivisionAgent>();

        public int parent = 0;

        public Division parentDivision = null;

        public ObservableCollection<Division> childs = new ObservableCollection<Division>();
        ObservableCollection<DivisionManager> managers = new ObservableCollection<DivisionManager>();

        public override string ToString()
        {
            return name;
        }

        public string Name {
            get  => name;
            set
            {
                name = value;
                OnPropertyChanged("Name");
            }
        }

        public bool IsDirty()
        {
            if (dirty)
                return true;
            foreach (Division c in childs)
                if (c.IsDirty())
                    return true;

            return false;
        }

        public void ClearDirty()
        {
            dirty = false;
            foreach (Division c in childs)
                c.ClearDirty();
        }

        bool dirty = false;
        public ObservableCollection<Division> Childs { get { return childs; } }
        public ObservableCollection<DivisionManager> Managers { get { return managers; } }
        public ObservableCollection<DivisionAgent> Agents { get { return agents; } }

        public bool Dirty { get => dirty; set => dirty = value; }

        protected void OnPropertyChanged(string name)
        {
            PropertyChangedEventHandler handler = PropertyChanged;
            if (handler != null)
            {
                handler(this, new PropertyChangedEventArgs(name));
            }
        }

        static public Division PrepareTree(List<Division> divisions, List<DivisionManager> managers)
        {
            if (divisions.Count == 0)
            {
                Division d = new Division();
                d.name = Properties.Resources.main_division;
                d.description = Properties.Resources.main_division;
                d.parent = 0;
                d.id = 1;
                divisions.Add(d);
            }

            int root = 0;
            Dictionary<int, Division> items = new Dictionary<int, Division>();

            foreach (Division d in divisions)
            {
                if (items.ContainsKey(d.id))
                {
                    foreach (Division src in items[d.id].Childs)
                        d.Childs.Add(src);
                }
                items[d.id] = d;

                if (d.parent == 0)
                {
                    if (root == 0)
                        root = d.id;
                }
                else
                {
                    Division parent;
                    if (!items.TryGetValue(d.parent, out parent))
                    {
                        parent = new Division();
                        items[d.parent] = parent;
                    }
                    parent.Childs.Add(d);
                }
            }

            foreach (DivisionManager m in managers)
            {
                Division parent;
                if (items.TryGetValue(m.division, out parent))
                {
                    parent.Managers.Add(m);
                }
            }

            return (root == 0 ? null : items[root]);
        }

        public bool HaveAgent(Agent a)
        {
            foreach (DivisionAgent da in agents)
                if (da.agent == a)
                    return true;

            return false;
        }

        public void Remove(Division child)
        {
            foreach (Division ch in childs)
            {
                if (ch == child)
                {
                    childs.Remove(ch);
                    break;
                }
            }
        }

        public bool RemoveRecurs(Division rmv)
        {
            foreach (Division ch in childs)
            {
                if (ch == rmv)
                {
                    childs.Remove(ch);
                    dirty = true;
                    return true; ;
                }

                if (ch.RemoveRecurs(rmv))
                    return true;
            }

            return false;
        }

        internal void CheckAgents()
        {
            List<DivisionAgent> remove = new List<DivisionAgent>();
            foreach (DivisionAgent da in agents)
                if (da.agent == null)
                    remove.Add(da);

            foreach (DivisionAgent da in remove)
                agents.Remove(da);
        }

        public void Remove(List<Agent> needRmv)
        {
            List<DivisionAgent> rmvd = new List<DivisionAgent>();
            foreach(DivisionAgent da in agents)
            {
                if (needRmv.Contains(da.agent))
                    rmvd.Add(da);
            }

            rmvd.ForEach(x => agents.Remove(x));
            if (rmvd.Count > 0)
                Dirty = true;
        }

        /// <summary>
        /// Получить список агентов вместе с агентами childs
        /// </summary>
        /// <returns>List<DivisionAgent></returns>
        public List<DivisionAgent> GetAllAgents()
        {
            List<DivisionAgent> result = new List<DivisionAgent>();

            result.AddRange(agents);
            result.AddRange(FetchChildAgents(childs));

            return result;
        }

        private List<DivisionAgent> FetchChildAgents(IList<Division> childs)
        {
            List<DivisionAgent> result = new List<DivisionAgent>();

            if (childs != null)
            {
                foreach (Division child in childs)
                {
                    result.AddRange(child.agents);
                    result.AddRange(FetchChildAgents(child.childs));
                }
            }

            return result;
        }

        public override bool Equals(object obj)
        {
            if (obj != null && obj is Division)
                return id == ((Division)obj).id;
            return false;
        }

        public override int GetHashCode()
        {
            return base.GetHashCode();
        }
    }

    internal class LicensedUser : DataObject
    {
        public static readonly string OBJECT_NAME = "LicensedUsers";

        [KeyField]
        public string id = String.Empty;
        public string type = LicensedUsers.ADSLIGHT.Type;

        [Reference("Agents", "id")]
        public Agent agent = null;
    }

    internal class LicenseCountEx : DataObject
    {
        public static readonly string OBEJCT_NAME = "LicenseCountEx";
        [KeyField]
        public string type = string.Empty;
        public int count = 0;
    }

    public partial class Task : BaseDocument
    {
        public static readonly string OBJECT_NAME = "Task";

        [KeyField]
        public string taskid = string.Empty;
        public string text = string.Empty;
        public DateTime start = DateTime.MinValue;
        public DateTime finish = DateTime.MinValue;
        public string fio = string.Empty;
        public string phone = string.Empty;
        public int rem = 0;
        public string client = string.Empty;
        public string address = string.Empty;
        public int notify = 0;
        public string manager = string.Empty;
    }

    public partial class TaskQuery : Task
    {
        public static readonly new string OBJECT_NAME = "TaskQuery";
        public static readonly string OBJECT_NAME_MANAGER = "TaskQueryManager";

        public int solution = 0;
        public string execrem = string.Empty;
        public DateTime startexec = DateTime.MinValue;
        public DateTime finishexec = DateTime.MinValue;

        internal bool ActiveByDate(DateTime date)
        {
            return start.Date <= date.Date && finish.Date >= date.Date;
        }
    }

    public class PhotoCount : DataObject
    {
        public static readonly string OBJECT_NAME = "PhotoCount";

        public int count = 0;

        [KeyField]
        public string taskid = string.Empty;
    }

    public class TaskQuest : DataObject
    {
        public string id = string.Empty;
    }

    public class TaskItem : DataObject
    {
        public string type = string.Empty;

        public static TaskItem Visit
        {
            get
            {
                TaskItem result = new TaskItem();
                result.type = "Visit";

                return result;
            }
        }

        public static TaskItem Question
        {
            get
            {
                TaskItem result = new TaskItem();
                result.type = "Question";

                return result;
            }
        }

        public override bool Equals(object obj)
        {
            return ((TaskItem)obj).type.Equals(type);
        }

        public override int GetHashCode()
        {
            return base.GetHashCode();
        }
    }

    public enum Solution { Solved = 1, Rejected = 2, InProgress = 3, Missed = 4 }

    public class TaskStatus
    {
        private int status;
        private Color color;

        private TaskStatus(int status, Color color)
        {
            this.status = status;
            this.color = color;
        }

        public static TaskStatus Rejected = new TaskStatus((int)Solution.Rejected, Colors.DarkGray);
        public static TaskStatus Solved = new TaskStatus((int)Solution.Solved, Colors.Green);
        public static TaskStatus InProgress = new TaskStatus((int)Solution.InProgress, Colors.Yellow);
        public static TaskStatus Missed = new TaskStatus((int)Solution.Missed, Colors.Yellow);

        public Color Backgroud { get { return color; } }
    }

    public class AgentInfo : DataObject
    {
        public static readonly string OBJECT_NAME = "AgentInfo";
        public static readonly string REPORT_NAME = "agentinfo";

        [KeyField]
        public string id;

        public int today;
        public int lost;

        public override string ToString()
        {
            string result = string.Empty;

            if (lost > 0 || today > 0)
            {
                StringBuilder sb = new StringBuilder("(");
                sb.Append(today);
                if (lost > 0)
                    sb.Append("/").Append(lost);

                sb.Append(")");

                result = sb.ToString();
            }

            return result;
        }

        public override int GetHashCode() { return base.GetHashCode(); }
        public Color getColor()
        {
            Color result = Colors.Black;

            if (lost > 0 || today > 0)
                result = lost > 0 ? Colors.Orange : Colors.Blue;

            return result;
        }
    }

    /// <summary>
    /// Архив сообщений
    /// </summary>
    public partial class MessageArchive : DataObject
    {
        static public readonly string OBJECT_NAME = "MessageArchive";

        /// <summary>
        /// ID пользователя
        /// </summary>
        public string userid = string.Empty;

        /// <summary>
        /// Дата - время сообщения
        /// </summary>
        public DateTime date = DateTime.MinValue;

        /// <summary>
        /// Содержание сообощения
        /// </summary>
        public string message = string.Empty;
    }

    public class Message : DataObject
    {
        static public string OBJECT_NAME = "Message";
        public DateTime date = DateTime.Now;
        public string message = string.Empty;

        public Message()
        {
            MessageObject mo = new MessageObject(""); // чтобы добавить формат в список форматов
        }
    }

    class Agents : DataSet<string, Agent>
    {
        public static readonly string OBJECT_NAME = "Agents";

        public Agents()
           : base(OBJECT_NAME)
        {
        }

        public Agents(bool addToDataModule)
           : base(OBJECT_NAME, addToDataModule)
        {
        }

        public static Agents GetDataSet()
        {
            if (DataModule.Get(OBJECT_NAME) == null)
            {
                return new Agents();
            }

            return (Agents)DataModule.Get(OBJECT_NAME);
        }

        public Agent Find(string login, string password)
        {
            Agent a = null;
            foreach (Agent check in Data)
            {
                if (check.login == login && check.password == password)
                {
                    a = check;
                    break;
                }
            }
            return a;
        }
    }

    public class Question : DataObject
    {
        public static readonly string OBJECT_NAME = "Question";

        public const int USE_PERIOD = 1;
        public const int INWORK = 2;

        [KeyField]
        public string idquest = string.Empty;
        public string name = string.Empty;
        public DateTime from = DateTime.MinValue;
        public DateTime till = DateTime.MinValue;
        public string text = string.Empty;
        public string html = string.Empty;
        public int number = 0;

#if BTL
      public string category = string.Empty;
      public string producer = string.Empty;
#endif

        [DataField("params")]
        public int _params = 0;

        [ItemType(typeof(QuestionItem))]
        public List<QuestionItem> items = null;

        public Question Copy()
        {
            Question result = new Question();
            result.idquest = GenId();
            result.name = "Копия " + name;
            result.from = from;
            result.till = till;
            result.text = text;

            if (items != null)
            {
                result.items = new List<QuestionItem>();

                foreach (QuestionItem i in items)
                    result.items.Add(i.Copy());

                result.InvalidateHtml();
            }

            return result;
        }

        public string Name { get { return name; } }
        public string From
        {
            get
            {
                return IsUsePeriod()
                   ? from.ToShortDateString() : string.Empty;
            }
        }

        public bool IsUsePeriod()
        {
            return (_params & USE_PERIOD) == USE_PERIOD;
        }

        public string Till
        {
            get
            {
                return IsUsePeriod()
                   ? till.ToShortDateString() : string.Empty;
            }
        }

        public int Number { get { return number; } }

        public void SetUsePeriod()
        {
            _params |= USE_PERIOD;
        }

        public string Text { get { return text; } }

        public void InvalidateHtml()
        {
            //StringBuilder htmlPage = new StringBuilder();
            //int[] color = new int[] { 0xffffff, 0xceecf5 };

            //htmlPage.Append("<html>");
            //htmlPage.Append("<head>");
            //htmlPage.Append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n");
            //htmlPage.Append("<style type=\"text/css\">\r\n");
            //htmlPage.Append("input{width:100%;}\r\n");
            //htmlPage.Append("select{width:100%;}\r\n");
            //htmlPage.Append("</style>\r\n");
            //htmlPage.Append("</head>\r\n");
            //htmlPage.Append("%%script%%");
            //htmlPage.Append("<body %%onload%%>");
            //htmlPage.Append("<div align=\"center\">");
            //htmlPage.Append("<br>");
            //htmlPage.Append(StringUtil.EscapeQuotes(text));
            //htmlPage.Append("</div>");
            //htmlPage.Append("<form id=\"").Append(idquest).Append("\">\r\n");
            //htmlPage.Append("<table \"width=100%\">\r\n");
            //htmlPage.Append("<col width=\"50%\"/>\r\n");
            //htmlPage.Append("<col width=\"50%\"/>\r\n");

            //int i = 0;
            //foreach (QuestionItem item in items)
            //{
            //   htmlPage.Append("<tr bgcolor=\"#")
            //      .Append(color[i % 2 == 0 ? 0 : 1].ToString("x"))
            //      .Append("\"><td>").Append(StringUtil.EscapeQuotes(item.text))
            //      .Append("</td><td>").Append(item.ToHtmlControl()).Append("</td></tr>\r\n");
            //   i++;
            //}

            //htmlPage.Append("\r\n</table>");
            //htmlPage.Append("%%commitbutton%%");
            //htmlPage.Append("</form>");
            //htmlPage.Append("</body>");
            //htmlPage.Append("</html>");

            //html = htmlPage.ToString();

            //File.WriteAllText("quest.html", html);
        }

        public override string ToString()
        {
            return name;
        }
    }

    public class QuestionItem : DataObject
    {
        public const int TEXT = 0;
        public const int NUMBER = 1;
        public const int LIST = 2;
        public const int SET = 3;
        public const int BOOLEAN = 4;
        public const int DATASET = 5;

        public string iditem = string.Empty;
        public string id = string.Empty;
        public string text = string.Empty;
        public int type = 0;
        public int number;
        public int optional = 0;

        [ItemType(typeof(QuestionItemValue))]
        public List<QuestionItemValue> values = null;

        public string Id { get { return id; } }
        public int Number { get { return number; } }
        public string Text { get { return text; } }
        public string TypeStr { get { return TypeToStr(type); } }

        public QuestionItem Copy()
        {
            QuestionItem result = new QuestionItem();

            result.iditem = Question.GenId();
            result.id = id;
            result.text = text;
            result.type = type;
            result.number = number;

            if (values != null)
            {
                result.values = new List<QuestionItemValue>();

                foreach (QuestionItemValue val in values)
                    result.values.Add(val.Copy());
            }

            return result;
        }

        public static string TypeToStr(int code)
        {
            switch (code)
            {
                case TEXT: return "Текст";
                case NUMBER: return "Число";
                case LIST: return "Список";
                case SET: return "Множество";
                case BOOLEAN: return "Логическое";
                case DATASET: return "Справочник";
                default: return "Тип неопределен";
            }
        }

        internal string ToHtmlControl()
        {
            return string.Empty;
            //   int index = 1;
            //   switch (type)
            //   {
            //      case TEXT:
            //         StringBuilder textText = new StringBuilder();
            //         textText.Append("<input type=\"text\" ")
            //            .Append("id=\"").Append(iditem).Append("_0\" ")
            //            .Append("name=\"").Append(id).Append("_0\" ")
            //            .Append("value=\"\">");

            //         return textText.ToString();
            //      case NUMBER:
            //         StringBuilder textNumber = new StringBuilder();
            //         textNumber.Append("<input type=\"number\" ")
            //            .Append("onKeyPress=\"return numbersonly(this, event)\" ")
            //            .Append("id=\"").Append(iditem).Append("_0\" ")
            //            .Append("name=\"").Append(id).Append("_0\" ")
            //            .Append("value=\"\">");

            //         return textNumber.ToString();

            //      case LIST:
            //         StringBuilder list = new StringBuilder();

            //         foreach (QuestionItemValue val in values)
            //         {
            //            string capt = StringUtil.EscapeQuotes(val.value);
            //            list.Append("<input type=\"checkbox\" ")
            //               .Append("id=\"").Append(iditem).Append("_").Append(index).Append("\" ")
            //               .Append("name=\"").Append(id).Append("_").Append(index).Append("\" ")
            //               .Append("value=\"").Append(capt).Append("\">").Append(capt).Append("<br>");
            //            index++;
            //         }

            //         return list.ToString();

            //      case SET:
            //         StringBuilder set = new StringBuilder();

            //         foreach (QuestionItemValue val in values)
            //         {
            //            string capt = StringUtil.EscapeQuotes(val.value);
            //            set.Append("<input type=\"radio\" ")
            //               .Append("id=\"").Append(iditem).Append("_").Append(index).Append("\" ")
            //               .Append("name=\"").Append(id).Append("_0\" ")
            //               .Append("value=\"").Append(capt).Append("\">").Append(capt).Append("<br>");
            //            index++;
            //         }

            //         return set.ToString();

            //      case BOOLEAN:
            //         StringBuilder boolean = new StringBuilder();

            //         if (values.Count == 2)
            //         {
            //            QuestionItemValue trueVal = values[0];
            //            QuestionItemValue falseVal = values[1];

            //            boolean.Append("<input type=\"radio\" ")
            //               .Append("id=\"").Append(iditem).Append("_").Append(index).Append("\" ")
            //               .Append("name=\"").Append(id).Append("_0\" ")
            //               .Append("value=\"").Append(StringUtil.EscapeQuotes(trueVal.value))
            //               .Append("\">").Append(StringUtil.EscapeQuotes(trueVal.value)).Append("<br>");
            //            index++;
            //            boolean.Append("<input type=\"radio\" ")
            //               .Append("id=\"").Append(iditem).Append("_").Append(index).Append("\" ")
            //               .Append("name=\"").Append(id).Append("_0\" ")
            //               .Append("value=\"").Append(StringUtil.EscapeQuotes(falseVal.value))
            //               .Append("\">").Append(StringUtil.EscapeQuotes(falseVal.value)).Append("<br>");
            //         }

            //         return boolean.ToString();

            //      case DATASET:
            //         StringBuilder dataset = new StringBuilder();

            //         if (values.Count == 1)
            //            dataset.Append("%%dataset%%").Append(values[0].value)
            //               .Append("%%datasetname%%").Append(iditem).Append("_0");

            //         return dataset.ToString();


            //      default: return "Тип неопределен";
            //   }
        }
    }

    public class QuestionItemValue : DataObject
    {
        public string value;

        public QuestionItemValue Copy()
        {
            QuestionItemValue result = new QuestionItemValue();
            result.value = value;

            return result;
        }
    }

    public class AgentQuest : DataObject
    {
        public static readonly string OBJECT_NAME = "AgentQuest";

        public string userid = string.Empty;
        [KeyField]
        public string idquest = string.Empty;
    }


    internal class Answer : DataObject
    {
        public static readonly string OBJECT_NAME = "Answer";

        public DateTime created = DateTime.MinValue;
        [Reference("Agents", "userid")]
        public Agent agent = null;

        public String userid = "";

        [Reference("Question", "question")]
        public Question quest = null;

        [Reference("Org,PotenzialOrg,CommonOrgs", "id", typeof(Org))]
        public Org org = null;
        public string id = string.Empty;
        public DateTime sended = DateTime.MinValue;

        [ItemType(typeof(AnswerItem))]
        public List<AnswerItem> items = null;

        [Precision(5)]
        public double longitude = 0;

        [Precision(5)]
        public double latitude = 0;

        public string AgentID { get { return agent == null ? string.Empty : agent.id; } }
    }

    internal class AnswerItem : DataObject
    {
        public string id = string.Empty;
        public string answer = string.Empty;
        public int type = -1;
        public string remark = string.Empty;
        public string iditem = string.Empty;

        public string Id { get { return id; } }
        public string Answer { get { return answer; } }
    }

    public class Category : DataObject
    {
        public static readonly string OBJECT_NAME = "Category";

        [KeyField]
        public string id = string.Empty;
        public string name = string.Empty;

        public string Name { get { return name; } }

        public override string ToString()
        {
            return Name;
        }
    }

    public class Producer : DataObject
    {
        public static readonly string OBJECT_NAME = "Producer";

        [KeyField]
        public string id = string.Empty;
        public string name = string.Empty;

        public string Name { get { return name; } }

        public override string ToString()
        {
            return Name;
        }
    }

    public class Org : DataObject, IComparable<Org>
    {
        public static string OBJECT_NAME = "Org";
        public static string COMMON_OBJECT_NAME = "CommonOrgs";

        [KeyField]
        public string id = "";
        public string name = "";
        public string address = "";

        [Reference("Agents", "userid")]
        public Agent agent = null;

        [Precision(5)]
        public double longitude = 0;

        [Precision(5)]
        public double latitude = 0;

        public int type = 0;
        public string ido = string.Empty;

#if Ishim
      public double balance = 0;
      public double Balance { get { return balance; } }
#endif

        // это поле отоброжает цвет на КПК (разный порядок RGB & BGR)
        public int color = 0;

        public string Name
        {
            get
            {
                //string result = Config.GetConfig().isFullOrgName ?
                //    String.Format("{0} ({1})", name, Address)
                //    : name;

                //result = result.Replace('\n', ' ');

                //return result;
                return name;
            }
        }

        public string Address
        {
            get
            {
                return address == null ? "" : address;
            }
        }

        public override string ToString() { return Name; }

        public override bool Equals(object cmp)
        {
            //Org org = cmp as Org;
            //bool cmpi = (org != null && id.Equals(org.id));
            //return (Config.GetConfig().isFullOrgName && cmpi) ?
            //   Address.Equals(org.Address) :
            //   cmpi;
            return base.Equals(cmp);
        }

        //Чтобы убрать варнинг
        public override int GetHashCode()
        {
            return base.GetHashCode();
        }

        // это св-во для отображения цвета в .Net
        public Color Color
        {
            get
            {
                byte r = (byte)(color & 0xFF);
                byte g = (byte)((color & 0xFF00) >> 8);
                byte b = (byte)((color & 0xFF0000) >> 16);
                return Color.FromArgb(0, r, g, b);
            }

            set
            {
                // меняем местаи r & b
                int clr = (value.A | value.R | value.G | value.B) & 0xFFFFFF;
                color = (((clr & 0xFF0000) >> 16) | (clr & 0xFF00) | ((clr & 0xFF) << 16));
            }
        }

#if Agama
      public class UnitItem : DataObject
      {
         public int id;
         public String name = "";
      }

      [ItemType(typeof(UnitItem))]
      public List<UnitItem> units = null;
#endif

#if Tyapkin
      [ItemType(typeof(OrgMatrixName))]
      public List<OrgMatrixName> matrixName = null;
#endif

#if Michailova_O
      public class OrgMatrix : DataObject
      {
         public static string OBJECT_NAME = "OrgMatrix";
         public string name = "";
         public string id = "";
         public string userid = "";
      }
      [ItemType(typeof(OrgMatrix))]
      public List<OrgMatrix> matrix = null;
#endif

#if DELIVERY_ADDRESS
      [ItemType(typeof(OrgAddress))]
      public List<OrgAddress> orgAddress = new List<OrgAddress>();

      public string GetAddress(string id)
      {
         foreach (OrgAddress adr in orgAddress)
            if (adr.id == id)
               return adr.name;

         return Address;
      }
#endif

        #region Члены IComparable<Org>

        public int CompareTo(Org other)
        {
            return name.CompareTo(other.name);
        }

        #endregion
    }

    //public class TaskAnswer : DataObject
    //{
    //   public static readonly string OBJECT_NAME = "TaskAnswer";

    //   public const int RESOLVED = 1;
    //   public const int REJECT = 2;
    //   public const int APPLY = 4;
    //   public const int INWORK = 5;

    //   public string taskid = string.Empty;
    //   public string remark = string.Empty;
    //   public DateTime created = DateTime.MinValue;
    //   public int solution = 0;
    //   public string userid = string.Empty;
    //   public DateTime done = DateTime.MinValue;

    //   internal static string StatusToStr(int val)
    //   {
    //      string result = string.Empty;

    //      switch (val)
    //      {
    //         case RESOLVED:
    //            result = "Выполнена";
    //            break;
    //         case REJECT:
    //            result = "Отклонена";
    //            break;
    //         case APPLY:
    //            result = "Принятая";
    //            break;
    //         case INWORK:
    //            result = "Выполняется";
    //            break;
    //      }

    //      return result;
    //   }
    //}

    public class BaseDocument : DataObject
    {
        public DateTime date = DateTime.Now;
        public DateTime created = DateTime.Now;

        [Precision(5)]
        public double latitude = 0;
        [Precision(5)]
        public double longitude = 0;
        public DateTime sended = DateTime.MinValue;

        [Reference("Agents", "userid")]
        public Agent agent = null;
        public string userid = string.Empty;

        [Reference("Org,PotenzialOrg,CommonOrg,CommonOrgs", "id", typeof(Org))]
        public Org org = null;

        public string id = String.Empty;

        public int timeZone = 0;
        public int serverTimeZone = 0;

        public string remark = "";

        public virtual double Sum()
        {
            return 0;
        }

        internal virtual int Qty
        {
            get { return 0; }
        }

        internal virtual Org Org { get { return org; } }

        public DateTime Date
        {
            get
            {
                return date;
            }
        }

        public DateTime Created
        {
            get
            {
#if USE_TIMEZONE
            TimeSpan ts = TimeZone.CurrentTimeZone.GetUtcOffset(DateTime.Now);
            return created.AddMinutes(timeZone).Add(ts);
#else
                return created;
#endif
            }
        }

        public DateTime Sended
        {
            get
            {
#if USE_TIMEZONE
            TimeSpan ts = TimeZone.CurrentTimeZone.GetUtcOffset(DateTime.Now);
            return sended.AddMinutes(serverTimeZone).Add(ts);
#else
                return sended;
#endif
            }
        }

        public string AgentName { get { return agent == null ? userid : agent.Name; } }
        public string OrgName { get { return Org == null ? id : Org.name; } }

        public virtual string Remark { get { return remark; } }
    }



    public partial class VisitInfo : BaseDocument
    {
        public static readonly string V_OBJECT_NAME = "VisitInfo";

        [Precision(2)]
        public double rating = 0;

#if Agama
      public int unitCode = 0;
#endif

        public string AgentID { get { return agent == null ? string.Empty : agent.id; } }
        public string OrgAddr { get { return org == null ? string.Empty : org.Address; } }

#if VISIT_CAUSE
      public string cause = "";
#endif
    }

    public class ImageUtil : VisitInfo
    {
        public static Image createImage(byte[] data)
        {


            Image result = null;

            //if (data != null)
            //{
            //   Stream s = new MemoryStream(data);
            //   using (s)
            //      result = new Bitmap(s);
            //}

            return result;
        }
    }

    public partial class Visit : VisitInfo
    {
        public static readonly string OBJECT_NAME = "Visit";
        public string taskid = string.Empty;

        public class VisitItem : DataObject
        {
            public byte[] id = null;
            public int rating = 0;
            public string caption = string.Empty;
        }

        [ItemType(typeof(VisitItem))]
        public List<VisitItem> items = null;

        public void RefreshRating()
        {
            int count = 0;

            rating = 0;
            foreach (Visit.VisitItem vi in items)
                if (vi.rating > 0)
                {
                    rating += vi.rating;
                    count++;
                }

            if (count > 0)
                rating /= count;
        }
    }

    public class GPSPos : DataObject
    {
        public static readonly string OBJECT_NAME = "GPSPos";
        [Reference("Agents", "userid")]
        public Agent agent = null;

        [KeyField]
        public DateTime date = DateTime.Now;
        [Precision(5)]
        public double longitude = 0;
        [Precision(5)]
        public double latitude = 0;
        public double speed = 0;
        public int isGSM = 0;
        public string userid = string.Empty;
    }

    public class UserLog : DataObject
    {
        static public readonly string OBJECT_NAME = "UserLog";

        [Reference("Agents", "userid")]
        public Agent agent = null;

        /// <summary>
        /// Дата передачи
        /// </summary>
        public DateTime date = DateTime.Now;

        public string objType = "";

        /// <summary>
        /// Дата (ключ документа
        /// </summary>
        public DateTime objDate = DateTime.Now;

        public DateTime Date { get { return objDate; } }
        public string Agent { get { return (agent == null) ? "?" : agent.name; } }
        public string Action
        {
            get
            {
                switch (objType)
                {
                    case "Order":
                        return "Заявка";
                    case "OrgRemnants":
                        return "Съем остатков";
                    case "Visit":
                        return "Посещение";
                }

                return "";
            }
        }

        //public ObjType ObjType { get { return new ObjType(objType); } }
        public int action;
        public int category;
        public string comments;

        public class ActionInfo
        {
            public int action;
            public string name;

            public ActionInfo(int action, string name)
            {
                this.action = action;
                this.name = name;
            }

            public override string ToString()
            {
                return name;
            }
        }

        static ActionInfo[] logActions;

        public static ActionInfo[] LogActions
        {
            get
            {
                if (logActions == null)
                {
                    logActions = new ActionInfo[] {
                  new ActionInfo(1, ((App)System.Windows.Application.Current).resource.GetString("gps_on")),
                  new ActionInfo(2,  ((App)System.Windows.Application.Current).resource.GetString("gps_off")),
                  new ActionInfo(3,  ((App)System.Windows.Application.Current).resource.GetString("time_changed")),
                  new ActionInfo(4,  ((App)System.Windows.Application.Current).resource.GetString("pda_on")),
                  new ActionInfo(5,  ((App)System.Windows.Application.Current).resource.GetString("pda_off")),
                  new ActionInfo(6,  ((App)System.Windows.Application.Current).resource.GetString("programm_crashed")),
                  new ActionInfo(7,  ((App)System.Windows.Application.Current).resource.GetString("programm_start")),
                  new ActionInfo(8,  ((App)System.Windows.Application.Current).resource.GetString("programm_finish")),
                  new ActionInfo(9,  ((App)System.Windows.Application.Current).resource.GetString("pda_status")), 
                  /*new ActionInfo(10, "Фоновая синхронизация"),*/ new ActionInfo(11, "Очистка базы")};

                }
                return logActions;
            }
        }

        public string userAction
        {
            get
            {
                if (action == 9)
                    return String.Format("{0}: {1}",
                       ((App)System.Windows.Application.Current).resource.GetString("pda_status")
                       , comments);
                if (action == 3)
                    return String.Format("{0} ({1})", logActions[2].ToString(), comments);
                foreach (ActionInfo ai in LogActions)
                    if (ai.action == action)
                        return ai.name;
                return string.Format("Неизвестный код события({0}, требуется обновить программу)", action);
            }
        }

        public string Time { get { return date.ToShortTimeString(); } }
    }

    public partial class OrgFolderItem : DataObject
    {
        [Reference("Org,PotenzialOrg,CommonOrgs", "name", typeof(Org))]
        public Org org = null;
        public string name = "";
        public int pos;

        public override string ToString()
        {
            return (org != null) ? org.ToString() : "";
        }

        public override bool Equals(object obj)
        {
            OrgFolderItem ofi = obj as OrgFolderItem;
            return (ofi != null && name.Equals(ofi.name));
        }

        //Чтобы убрать варнинг
        public override int GetHashCode()
        {
            return base.GetHashCode();
        }
    }

    public class OrgFolder : DataObject
    {
        public static readonly string OBJECT_NAME = "OrgFolder";

        [KeyField]
        public string name = "";

        [KeyField]
        public string userid = "";

        public int id = -1;

        [Reference("Agents", "userid")]
        public Agent agent = null;

        [ItemType(typeof(OrgFolderItem))]
        public List<OrgFolderItem> items = new List<OrgFolderItem>();

        public string code = string.Empty;
        public int type = -1;
        public int last = 0;
    }

    public class PotenzialOrg : Org
    {
        new static public readonly string OBJECT_NAME = "PotenzialOrg";

        public string userid;
        //[Reference("Region", "region", typeof(Region))]
        //public GRSoft.NapoleonManager.Region region;
    }

    public class LiveArea : GRSoft.Network.DataObject
    {
        [KeyField]
        public string id = string.Empty;
        public string name = string.Empty;
        public string code = string.Empty;

        public string Name { get { return name; } }
        public string Code { get { return code; } }
        public string Id { get { return id; } }

        public override string ToString()
        {
            return Name;
        }
    }


    public class Region : LiveArea, IComparable
    {
        public static readonly string OBJECT_NAME = "Region";
        public string region1 = string.Empty;
        public string region2 = string.Empty;

        [Reference("Region1", "region1", typeof(Region1))]
        public Region1 r1;

        [Reference("Region2", "region2", typeof(Region2))]
        public Region2 r2;

        #region IComparable Members

        public int CompareTo(object obj)
        {
            return name.CompareTo(((Region)obj).name);
        }

        #endregion
    }

    public class Region1 : LiveArea
    {
        public static readonly string OBJECT_NAME = "Region1";
        public string region2;
    }

    public class Region2 : LiveArea
    {
        public static readonly string OBJECT_NAME = "Region2";
        public BindingList<Region1> childs = new BindingList<Region1>();
    }

    public partial class UserOrderRemark : BaseDocument
    {
        public static readonly string OBJECT_NAME = "UserOrderRemark";
        public int readed = 0;
    }

    public class UserLocation : GPSPos
    {
        public static readonly new string OBJECT_NAME = "UserLocation";

        public string UserName { get { return agent != null ? agent.Name : userid; } }
        public DateTime Date { get { return date; } }
    }

    public class TaskAttachment : DataObject
    {
        public static readonly string OBJECT_NAME = "TaskAttachment";
        public static readonly string OBJECT_INFO_NAME = "TaskAttachmentInfoManager";

        [KeyField]
        public string id = string.Empty;
        public string taskid = string.Empty;
        public string name = string.Empty;
        public byte[] data = null;

        public string Name { get { return name; } }

        public override string ToString()
        {
            return name;
        }
    }

    public partial class UserOrder : BaseDocument
    {
        public static readonly string OBJECT_NAME = "UserOrder";

        public string client = string.Empty;
        public string address = string.Empty;
        public string phone = string.Empty;
        public string fio = string.Empty;
        public int readed = 0;
    }

    public class ReportResult : GRSoft.Network.DataObject
    {
        public static readonly string OBJECT_NAME = "Result";

        public string name = "";
        public byte[] file = null;
    }

    public class AddressTemplate : GRSoft.Network.DataObject
    {
        public static readonly string OBJECT_NAME = "AddressTemplate";

        [KeyField]
        public string id = string.Empty;
        public string template = string.Empty;
    }

    public class TaskVisit : BaseDocument
    {
        public static readonly string OBJECT_NAME = "TaskVisit";

        public class TaskVisitItem : DataObject
        {
            public String id = "";
            public DateTime date = DateTime.MinValue;
        }

        [ItemType(typeof(TaskVisitItem))]
        public List<TaskVisitItem> items = new List<TaskVisitItem>();
        public String taskid = string.Empty;
    }

    public class TaskQueryVisit : TaskVisit
    {
        public static readonly new string OBJECT_NAME = "TaskVisitQuery";
    }

    public class PicStore : DataObject
    {
        public static readonly string OBJECT_NAME = "PicStoreQuery";

        [KeyField]
        public string id = string.Empty;
        public byte[] picture = null;
    }

    public class CommonConfig : DataObject
    {
        public static string OBJECT_NAME = "ServerConfig";

        [KeyField]
        public string key = string.Empty;
        public string value = string.Empty;
        public string userid = string.Empty;
    }
}
