using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.IO;
using System.ComponentModel;
using System.Windows.Media;
using System.Windows.Controls;

namespace Napoleon
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

        public int isDsp = 0;
        public string phone = "";
        public string userid = "";

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

        public string Login { get { return login; } set { login = value; } }
        public string Password { get { return password; } set { password = value; } }
        public string Prefix { get { return prefix; } set { prefix = value; } }

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
    }

    public class Division : DataObject
    {
        public static readonly string OBJECT_NAME = "Division";

        public class DivisionAgent : DataObject
        {
            [Reference("Agents", "id")]
            public Agent agent = null;

            public string id = "";

            public string AgentName
            {
                get { return (agent == null) ? "?" : agent.name; }
            }

            public override string ToString()
            {
                return AgentName;
            }
        }

        [KeyField]
        public int id = 0;

        public string name = "";
        public string description = "";

        [Reference("Agents", "cheif")]
        public Agent cheif = null;

        [ItemType(typeof(DivisionAgent))]
        public List<DivisionAgent> agents = new List<DivisionAgent>();

        public int parent = 0;

        public Division parentDivision = null;

        public List<Division> childs = new List<Division>();

        public override string ToString()
        {
            return name;
        }

        public string DivisionName
        {
            get { return name; }
        }

        public void SetReferences(Dictionary<int, Division> divisions)
        {
            if (divisions.ContainsKey(parent))
            {
                parentDivision = divisions[parent];
                parentDivision.childs.Add(this);
            }
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

        internal void CheckAgents()
        {
            List<DivisionAgent> remove = new List<DivisionAgent>();
            foreach (DivisionAgent da in agents)
                if (da.agent == null)
                    remove.Add(da);

            foreach (DivisionAgent da in remove)
                agents.Remove(da);
        }

        public bool Remove(Dictionary<Agent, bool> agentSet)
        {
            bool ret = false;
            int i = 0;
            for (; i < agents.Count; i++)
            {
                if (agents[i].agent != null &&
                    agentSet.ContainsKey(agents[i].agent))
                {
                    ret = true;
                    agents.RemoveAt(i);
                }
            }

            return ret;
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

        private List<DivisionAgent> FetchChildAgents(List<Division> childs)
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
    }

    public partial class TaskQuery : Task
    {
        public static readonly new string OBJECT_NAME = "TaskQuery";
        public static readonly string OBJECT_NAME_MANAGER = "TaskQueryManager";

        public int solution = 0;
        public string execrem = string.Empty;
        public DateTime startexec = DateTime.MinValue;
        public DateTime finishexec = DateTime.MinValue;
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

    public class AgentOrgs : DataObject
    {
        public static readonly string OBJECT_NAME = "AgentOrgs";

        [KeyField]
        public string id = "";
        public string userid = "";
    }

    public class Org : DataObject, IComparable<Org>
    {
        //public static string OBJECT_NAME = "Org";
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
        public string userid = "";

        public int noDrop = 0;
        public string formatTT = "";
        public string idChannel = "";
        public string idRetailer = "";

        public int costype = 0;

        // это поле отоброжает цвет на КПК (разный порядок RGB & BGR)
        public int color = 0;

        public string Name => name;

        public string Address => address ?? "";

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

        public class OrgContact : DataObject
        {
            public string name = string.Empty;
            public string phone = string.Empty;

            public string Name { get { return name; } }
            public string Phone { get { return phone; } }
        }

        [ItemType(typeof(OrgContact))]
        public List<OrgContact> contacts = new List<OrgContact>();
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

    public class Cagent : DataObject
    {
        public static readonly string OBJECT_NAME = "Cagents";

        [KeyField]
        public string id = string.Empty;

        public string name = string.Empty;
    }

    public class BaseDocument : DataObject
    {
        public DateTime date = DateTime.Now;

        [KeyField]
        public DateTime created = DateTime.Now;

        [Precision(5)]
        public double latitude = 0;
        [Precision(5)]
        public double longitude = 0;
        public DateTime sended = DateTime.MinValue;

        [Reference("Agents", "userid")]
        public Agent agent = null;

        [KeyField]
        public string userid = string.Empty;

        [Reference("Org,PotenzialOrg,CommonOrg,CommonOrgs", "id", typeof(Org))]
        public Org org = null;

        public string id = String.Empty;

        public int timeZone = 0;
        public int serverTimeZone = 0;

        public string remark = "";

        public virtual double Sum
        {
            get { return 0; }
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
        public string OrgAddr { get { return Org == null ? id : Org.address; } }

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
                  new ActionInfo(1,"GPS - Включен"), new ActionInfo(2, "GPS - Выключен"), new ActionInfo(3, "Время изменено"),
                  new ActionInfo(4, "КПК - Включен"), new ActionInfo(5, "КПК - Выключен"), new ActionInfo(6, "Сбой программы"),
                  new ActionInfo(7, "Наполеон - Запуск"), new ActionInfo(8, "Наполеон - Выход"), new ActionInfo(9, "КПК статус:"), 
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
                    return String.Format("КПК статус: {0}", comments);
                if (action == 3)
                    return String.Format("{0} ({1})", logActions[2].ToString(), comments);
                foreach (ActionInfo ai in LogActions)
                    if (ai.action == action)
                        return ai.name;
                return string.Format("Неизвестный код события({0}, требуется обновить программу)", action);
                //switch (action)
                //{ 
                //   case 1:
                //      return "GPS - Включен";
                //   case 2:
                //      return "GPS - Выключен";
                //   case 3:
                //      return "Время изменено";
                //   case 4:
                //      return "КПК - Включен";
                //   case 5:
                //      return "КПК - Выключен";
                //   case 6:
                //      return "Сбой программы";
                //   case 7:
                //      return "Наполеон - Запуск";
                //   case 8:
                //      return "Наполеон - Выход";
                //   case 9:
                //      return String.Format("КПК статус: {0}", comments);
                //   case 10:
                //      return "Фоновая синхронизация";
                //   case 11:
                //      return "Очистка базы";
                //   default:
                //      return string.Format("Неизвестный код события({0}, требуется обновить программу)", action);
                //}
            }
        }

        public string Time { get { return date.ToShortTimeString(); } }
    }

    public partial class OrgFolderItem : DataObject, IComparable<OrgFolderItem>
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

        public int CompareTo(OrgFolderItem other)
        {
            return pos - other.pos;
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
        static public readonly string OBJECT_NAME = "PotenzialOrg";

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

    public partial class Note : BaseDocument
    {
        public static readonly string OBJECT_NAME = "Note";
        public string client = string.Empty;
        public string address = string.Empty;
    }

    public partial class NoteAction : BaseDocument
    {
        public static readonly string OBJECT_NAME = "NoteAction";
        public int readed = 0;
    }

    public class UserLocation : GPSPos
    {
        public static readonly new string OBJECT_NAME = "UserLocation";

        public string UserName { get { return agent != null ? agent.Name : userid; } }
        public DateTime Date { get { return date; } }
    }

    public class Config : DataObject
    {
        public static readonly string OBJECT_NAME = "Config";

        [KeyField]
        public string key = string.Empty;
        public string value = string.Empty;
    }

    public class Folder : DataObject, IDataFiltrable, IComparable<Folder>, IEquatable<Folder>
    {
        public static readonly string OBJECT_NAME = "Folder";

        [KeyField]
        public string fid = "";

        public int level = 0;
        public string name = "";
        public string userid = "";
        public int id = 0;

        public string Name { get { return name; } }
        public string ID { get { return fid; } }

        public int CompareTo(Folder other)
        {
            return name.CompareTo(other.name);
        }

        public string GetId { get { return fid; } }
        public string GetName { get { return name; } }
        public override int GetHashCode() { return fid.GetHashCode(); }

        public bool Equals(Folder other)
        {
            return other == null ? false : fid.Equals(other.fid);
        }
    }

    public partial class Price : DataObject, IComparable<Price>, IEquatable<Price>
    {
        public static readonly string OBJECT_NAME = "ManagerPrice";

        [KeyField]
        public string id = "";
        public string name = "";
        public double weight = 0;
        public double qty = 0.0;
        public int folderID = 0;
        public string fid = string.Empty;

        public string packName = "";
        public string thermalState = "";
        public string idType = "";
        public string idBrand = "";

        [DataField("qtyInPack")]
        public double inPack = 0; // кол-во в упаковке

        [DataField("cost.cost")]
        public double[] cost = new double[0];

        public override string ToString() { return name; }

        // это поле отоброжает цвет на КПК (разный порядок RGB & BGR)
        public int color = 0;

        // это св-во для отображения цвета в .Net
        public Color Color
        {
            get
            {
                byte r = (byte)(color & 0xFF);
                byte g = (byte)((color & 0xFF00) >> 8);
                byte b = (byte)((color & 0xFF0000) >> 16);
                return Color.FromArgb(0xff, r, g, b);
            }

            set
            {
                // меняем местаи r & b
                color = value.B | value.G | value.R;
            }
        }

        public override bool Equals(object obj)
        {
            if (obj is Price)
                return obj == null ? false : id.Equals(((Price)obj).id);
            else
                return false;
        }

        public override int GetHashCode()
        {
            return id.GetHashCode();
        }

        public string Name
        {
            get
            {
                StringBuilder result = new StringBuilder();
#if SHOW_PRICE_ID
            result.Append(id);
            result.Append("; ");
#endif
                result.Append(name);

                return result.ToString();
            }
        }

        public static Price GetEmpty(string id) { return EmptyPrice.Get(id); }

        public int CompareTo(Price other)
        {
            return Name.CompareTo(other.Name);
        }

        public bool Equals(Price other)
        {
            return other == null ? false : id == other.id;
        }
    }

    public class EmptyPrice : Price
    {
        static Dictionary<string, Price> used = new Dictionary<string, Price>();

        public static Price Get(string id)
        {
            if (!used.ContainsKey(id))
                used[id] = new EmptyPrice(id);
            return used[id];
        }

        EmptyPrice(String id)
        {
            this.name = "Товар с кодом <" + id + ">";
            this.id = id;
            this.inPack = 1;
        }
    }

    public class PhoneAction : BaseDocument
    {
        public static readonly string OBJECT_NAME = "PhoneAction";

        public DateTime changes = DateTime.Now;
        public string rejectCause = string.Empty;
        public string text = string.Empty;
        public string contactFIO = string.Empty;
        public string contactPHONE = string.Empty;
        public string tpcode = string.Empty;
        public string fio = string.Empty;
    }

    public partial class OrderItem : DataObject
    {
        public static readonly int IN_PACK = 1;

        [Reference("ManagerPrice,Price", "id", typeof(Price))]
        public Price item = null;
        public string id = "";

        public int flags = 0;

        [Precision(3)]
        public double qty = 0;
        public double cost = 0;
        public double sum = 0;

        public double offTakeDiff = 0;

        public int hasAction = 0;

        public int pack = 0;
        public double taxSum = 0;
        public string Item { get { return item != null ? item.Name : "товар с кодом <" + id + ">"; } }
    }


    public partial class Order : BaseDocument
    {
        public static readonly string OBJECT_NAME = "Order";


        [DataField("params")]
        public int _params = 0;
        public bool OutOfPlan { get { return ((_params & 0x40000) != 0); } }


        [ItemType(typeof(OrderItem))]
        public List<OrderItem> items = new List<OrderItem>();


        public static readonly string ORDER_SAVE = "OrderCommit";
        public String firmCode;
        public DateTime modify;
        public DateTime dlvDate;

        public DateTime DlvDate { get => dlvDate; }

        public int sumType = 0;

        [Reference("Firms", "firmCode")]
        public Firms firmObj = null;

        public long linked;

        public string UserID { get { return userid; } }

        public bool UPP { get; set; }
        public string BkColor
        {
            get
            {
                return (agent != null && agent.isDsp > 0) ? "LightGray" : "White";
            }
        }

        public string FullName {
            get
            {
                string val = OrgName;
                if (firmObj != null)
                    val += " / " + firmObj.name;
                return val;
            }
        }

        public bool HaveItem(string id)
        {
            foreach (OrderItem oi in items)
                if (oi.id == id) // && oi.qty != 0)
                    return true;

            return false;
        }

        // поля для дублирования
        public string ctype = "";
        public string firma = "";
        public int delay;
        public int cash;

        override internal int Qty
        {
            get
            {
                int q = 0;
                foreach (OrderItem item in items)
                    q += (int)(item.qty + 0.5);
                return q;
            }
        }

        override public double Sum
        {
            get
            {
                double sum = 0;
                foreach (OrderItem item in items)
                    sum += item.cost * item.qty;

                return sum;
            }
        }

        public double Weight
        {
            get
            {
                double res = 0;
                foreach (OrderItem item in items)
                {
                    if (item.item != null)
                        res += (item.item.weight * item.qty);
                }
                return res;
            }
        }
    }

    public class LastOrder : Order
    {
        public static readonly new string OBJECT_NAME = "LastOrder";
    }

    public class DeliveryItem : DataObject
    {
        public string id;
        [Precision(3)]
        public double qty;
        public double sum;

        [Reference("ManagerPrice,Price", "id", typeof(Price))]
        public Price item = null;
    }

    public class Delivery : BaseDocument
    {
        public static readonly string OBJECT_NAME = "Delivery";

        public string firm = "";
        public double sumD;

        public string number = "";
        public DateTime payDate = DateTime.MinValue;

        [ItemType(typeof(DeliveryItem))]
        public List<DeliveryItem> items;

        public override double Sum
        {
            get
            {
                double result = 0;

                if (items != null)
                    foreach (DeliveryItem item in items)
                        result += item.sum;

                return result;
            }
        }

        public double Weight
        {
            get
            {
                double res = 0;
                foreach (DeliveryItem item in items)
                {
                    if (item.item != null)
                        res += (item.item.weight * item.qty);
                }
                return res;
            }
        }
    };


    public class LastDelivery : Delivery
    {
        public static readonly new string OBJECT_NAME = "LastDelivery";
    }

    public partial class OrgRemnantsItem : DataObject
    {
        [Reference("ManagerPrice,Price", "id", typeof(Price))]
        public Price item = null;
        [Precision(3)]
        public double qty = 0;
        public string id = string.Empty;

        //Свойства для отображение в гриде
        //Количество
        public double Qty { get { return qty; } }
        //Наименование
        public string Item { get { return item != null ? item.Name : "товар с кодом <" + id + ">"; } }
        public double Weight { get { return item == null ? 0 : item.weight * qty; } }
    }

    public partial class OrgRemnants : BaseDocument
    {
        public static readonly string OBJECT_NAME = "OrgRemnants";

        [ItemType(typeof(OrgRemnantsItem))]
        public List<OrgRemnantsItem> items = null;
    }

    //public class LastRemnant : OrgRemnants
    //{
    //    public static readonly new string OBJECT_NAME = "LastRemnant";
    //}

    public interface IDataFiltrable
    {
        string GetId { get; }
        string GetName { get; }
    }

    public class Firms : DataObject, IComparable<Firms>, IDataFiltrable
    {
        public static readonly string OBJECT_NAME = "Firms";

        [KeyField]
        public string id = string.Empty;
        public string name = string.Empty;

        public string ID { get { return id; } }
        public string Name { get { return name; } }

        public int CompareTo(Firms other)
        {
            return Name.CompareTo(other.Name);
        }

        public string GetId { get { return id; } }
        public string GetName { get { return name; } }

        public override string ToString()
        {
            return name;
        }
    }

    public class Brands : DataObject, IComparable<Brands>, IDataFiltrable
    {
        public static readonly string OBJECT_NAME = "Brands";

        [KeyField]
        public string id = string.Empty;
        public string name = string.Empty;

        public string ID { get { return id; } }
        public string Name { get { return name; } }

        public int CompareTo(Brands other)
        {
            return Name.CompareTo(other.Name);
        }

        public string GetId { get { return id; } }
        public string GetName { get { return name; } }
    }


    public class OrgDogovor : DataObject
    {
        public static readonly string OBJECT_NAME = "MgrOrgDogovor";

        public string ido = string.Empty;
        public string firm = string.Empty;
    }

    class IDMTX : DataObject
    {
        public static readonly string IDMTX_OBJ_NAME = "IdMtx";

        [KeyField]
        public string id = string.Empty;
        [KeyField]
        public string firm = string.Empty;
        public string mtx = string.Empty;
    }

    class ObjectMatrix : IDMTX
    {
        public static readonly string IDOMTX_OBJ_NAME = "IdoMtx";

        public static readonly string CHANNEL_OBJ = "channel";
        public static readonly string RETAIL_OBJ = "retail";
        public static readonly string ORG_TYPE_OBJ = "orgtype";
        public static readonly string ORG_OBJ = "org_obj";

        public string objectType = "";
    }

    public class OrgMatrix : DataObject
    {
        public static readonly string OBJECT_NAME = "OrgMatrix";

        public class Item : DataObject
        {
            public string id = string.Empty;
            public int mustBe = 0;
        }

        [KeyField]
        public string name = string.Empty;

        [ItemType(typeof(Item))]
        public List<Item> items = null;
    }


    public class MMLFeatures : DataObject
    {
        public static readonly string OBJECT_NAME = "MMLFeatures";

        public static readonly string ORG_TYPE_KIND = "orgType";
        public static readonly string SALES_PLACE_KIND = "salesPlace";


        public string id = "";
        public string kind = "";

        public class Item : DataObject
        {
            public string id = "";
        }

        public List<Item> items = new List<Item>();

        public bool IsOrgType { get { return kind == ORG_TYPE_KIND; } }
    }

    public class PlanNew : DataObject
    {
        public static readonly string OBJECT_NAME = "PlanNew";

        public string firm = string.Empty;
        public DateTime date = DateTime.Now;

        public class Item : DataObject
        {
            public string id = string.Empty;
            public double qty = 0.0;
        }

        [ItemType(typeof(Item))]
        public List<Item> items = new List<Item>();
    }

    public class ServoluxSheduleItem : DataObject
    {
        public static readonly string OBJECT_NAME = "ServoluxSheduleItem";

        [KeyField]
        public string id = string.Empty;

        public int mon = 0;
        public int tue = 0;
        public int wed = 0;
        public int thu = 0;
        public int fri = 0;
        public int sat = 0;
        public int sun = 0;
    }

    public class RejectCause : DataObject
    {
        public static readonly string OBJECT_NAME = "RejectCause";

        [KeyField]
        public string id = string.Empty;
        public string name = string.Empty;

        public string Name { get { return name; } set { name = value; } }
    }

    public class LastSalesItems : DataObject
    {
        public static readonly string OBJECT_NAME = "LastSalesItems";

        [KeyField]
        public string id_i = "";

        public DateTime date = DateTime.Now;
        public double qty = 0;
    }

    public class NotExpiredItems : DataObject
    {
        public static readonly string OBJECT_NAME = "NotExpiredItems";

        public string id = "";
        public string number = "";
        public string firm = "";
        public string party = "";
        public DateTime date = DateTime.Now;

        public DateTime expired = DateTime.Now;
        public double qty = 0;
        public double sum = 0;
    }

    public class OrderProceeded : DataObject
    {
        public static readonly string OBJECT_NAME = "ArchiveOrderProceeded";

        public DateTime created = DateTime.Now;

        public string type = "";
        public string firm = "";
        public string userid = "";
        public string remark = "";
        public int status = 0;
    }

    public class ReturnCause : DataObject, IComparable<ReturnCause>
    {
        public static readonly string OBJECT_NAME = "ReturnCause";

        public DateTime created = DateTime.Now;

        public string idType = "";
        public string firm = "";
        public string id = "";
        public string name = "";

        public int CompareTo(ReturnCause other)
        {
            return name.CompareTo(other.name);
        }

        public override string ToString()
        {
            return name;
        }
    }

    public class ReturnRequest : BaseDocument
    {
        public static readonly string OBJECT_NAME = "ReturnRequest";

        public string firmCode = "";
        public int accepted = 0;
        // имеет корректное значение только если accepted = 1
        public List<RRItem> items = new List<RRItem>();

        override internal int Qty
        {
            get
            {
                int q = 0;
                foreach (RRItem item in items)
                    q += (int)(item.Qty + 0.5);
                return q;
            }
        }

        override public double Sum
        {
            get
            {
                double sum = 0;
                foreach (RRItem item in items)
                    sum += item.Sum;

                return sum;
            }
        }

        public double Weight
        {
            get
            {
                double res = 0;
                foreach (RRItem item in items)
                {
                    if (item.item != null)
                        res += (item.item.weight * item.Qty);
                }
                return res;
            }

        }

        [Reference("Firms", "firmCode")]
        public Firms firmObj = null;

        public bool UPP { get; set; }
        public string UserID { get { return userid; } }
        public string BkColor
        {
            get
            {
                return (agent != null && agent.isDsp > 0) ? "LightGray" : "White";
            }
        }

        public string FullName
        {
            get
            {
                string val = OrgName;
                if (firmObj != null)
                    val += " / " + firmObj.name;
                return val;
            }
        }


        public class RRItem : DataObject
        {
            [Reference("ManagerPrice,Price", "id", typeof(Price))]
            public Price item = null;
            public string id = "";

            public int flags = 0;

            [Precision(3)]
            public double qty = 0;
            public double cost = 0;

            public string cause = "";
            public string uid = "";
            public DateTime mfrDate;

            public List<ReturnDlv> items = new List<ReturnDlv>();

            public double Qty
            {
                get
                {
                    double ret = 0;
                    foreach (ReturnDlv rd in items)
                        ret += rd.qty;
                    return ret;
                }
            }

            public double Sum
            {
                get
                {
                    double ret = 0;
                    foreach (ReturnDlv rd in items)
                        ret += (rd.cost * rd.qty);
                    return ret;
                }
            }
        }

        public class ReturnDlv : DataObject
        {
            public string number = "";
            public DateTime date;

            [Precision(3)]
            public double qty;

            [Precision(2)]
            public double cost;

            public string remark = "";
            public string party = "";
        }

        internal double GetQty(PriceNode pn)
        {
            double ret = 0;
            foreach (RRItem i in items)
            {
                if (i.id == pn.ID)
                {
                    foreach (ReturnDlv rd in i.items)
                    {
                        if (rd.number == pn.DocNumberInt)
                        {
                            ret = rd.qty;
                            break;
                        }
                    }
                }
            }
            return ret;
        }
    }

    public class ReturnLimit : DataObject
    {
        public static readonly string OBJECT_NAME = "ReturnLimit";
        public static readonly int LIMIT_SUM = 0;
        public static readonly int LIMIT_WEIGHT = 1;

        public DateTime start = DateTime.Now;
        public DateTime end = DateTime.Now;

        public String priceType = "";

        public int limitType = 0;
        public long limit = 0;

        public int canOverlimit = 0;

        public bool Active
        {
            get { return start <= DateTime.Now.Date && end >= DateTime.Now.Date; }
        }

        public bool InPeriod(BaseDocument doc)
        {
            return start <= doc.created && end >= doc.created;
        }

        public bool CanOverlimit { get { return canOverlimit != 0; } }

        public double GetNodeValue(PriceNode pn)
        {
            if (limitType == LIMIT_SUM)
                return pn.Order * pn.Cost;
            return pn.Order * pn.Price.weight;
        }

        public double CountValue(ReturnRequest doc)
        {
            double ret = 0;

            if (doc != null)
            {
                foreach (ReturnRequest.RRItem i in doc.items)
                {
                    if (i.item.idType != priceType)
                        continue;

                    if (limitType == LIMIT_SUM)
                    {
                        foreach (ReturnRequest.ReturnDlv rdi in i.items)
                            ret += rdi.cost * rdi.qty;
                    }
                    else
                    {
                        if (i.item != null)
                            ret += i.Qty * i.item.weight;
                    }
                }
            }
            return ret;
        }
    }

    public class TradeAction : DataObject
    {
        public static readonly string OBJECT_NAME = "TradeAction";

        public String id = "";

        public DateTime start = DateTime.Now;
        public DateTime end = DateTime.Now;
        public DateTime startAction = DateTime.Now;
        public DateTime endAction = DateTime.Now;

        public class OrgItem : DataObject
        {
            public string id = "";
        }

        public class ActionItem : DataObject
        {
            public string id = "";
            public double cost = 0;
        }

        public List<OrgItem> orgs = new List<OrgItem>();
        public List<OrgItem> stores = new List<OrgItem>();

        public List<ActionItem> items = new List<ActionItem>();

        internal bool IsActive(DateTime date, Org o)
        {
            DateTime prev = date.Date.AddDays(-1);
            DateTime next = date.Date.AddDays(1);

            if (start > next || end < prev)
                return false;

            foreach (OrgItem oi in orgs)
                if (oi.id == o.ido)
                    return true;

            foreach (OrgItem oi in stores)
                if (oi.id == o.id)
                    return true;

            return false;
        }
    }
}
