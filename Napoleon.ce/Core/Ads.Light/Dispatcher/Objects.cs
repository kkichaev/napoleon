using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Drawing;

namespace GRSoft.Ads.Dispatcher
{
   public delegate void EmptyParamHandler();
   public delegate void InvokeDelegate();

   public class TaskBase
   {
      public static readonly int TASK_TYPE = 0;

      public string id = "";
      public string parent = "";

      public int type = TASK_TYPE;
   }

   public class TaskHead : TaskBase, ICloneable
   {
      #region Data fields
      
      public DateTime start = DateTime.MinValue;
      public DateTime finish = DateTime.MinValue;

      public string text = string.Empty;
      public string city = string.Empty;
      public string street = string.Empty;
      public string house = string.Empty;
      public string clientname = string.Empty;
      public string clientphone = string.Empty;

      public List<TaskBase> childs = new List<TaskBase>();

      #endregion

      protected Band band;
      protected Solution solution; 
      public Band Band { get { return band; } set { band = value; } }
      public Solution Solution { get { return solution; } set { solution = value; } } 
      

      #region Visual & Edit properties

      public event EventHandler Changed;

      #endregion

      #region ICloneable Members

      public object Clone()
      {
         TaskHead result = new TaskHead();
         result.id = Utils.GUID;
         result.parent = parent;
         result.start = start;
         result.finish = finish;
         result.text = text;
         result.city = city;
         result.street = street;
         result.house = house;
         result.clientname = clientname;
         result.clientphone = clientphone;

         return result;
      }

      #endregion
   }

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
   }

   public class DivisionManager : DataObject
   {
      public static readonly string OBJECT_NAME = "DivisionManager";

      [KeyField]
      public string login = "";

      public string password = "";
      public int division = 0;

      public string Login { get { return login; } set { login = value; } }
      public string Password { get { return password; } set { password = value; } }
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

   public class Task : DataObject
   {
      public static readonly string OBJECT_NAME = "Task";
      [KeyField]
      public string id = string.Empty;
      public string parent = string.Empty;

      [Reference("Agents", "userid")]
      public Agent agent = null;

      public string text = string.Empty;
      public string city = string.Empty;
      public string street = string.Empty;
      public string house = string.Empty;
      public DateTime start = DateTime.MinValue;
      public DateTime finish = DateTime.MinValue;
      public string clientid = string.Empty;
      public string clientname = string.Empty;
      public string clientphone = string.Empty;

      [ItemType(typeof(TaskItem))]
      public List<TaskItem> items = new List<TaskItem>();
      [ItemType(typeof(TaskQuest))]
      public List<TaskQuest> questions = new List<TaskQuest>();
   }

   public class TaskQuest : DataObject
   {
      public string id = string.Empty;
   }

   public class TaskInfo : Task
   {
      public static new readonly string OBJECT_NAME = "TaskInfo";
      public static readonly string REPORT_NAME = "task";

      [ItemType(typeof(TaskAnswer))]
      public List<TaskAnswer> answers = new List<TaskAnswer>();
   }

   public class TaskAnswer : DataObject
   {
      public DateTime created = DateTime.MinValue;
      public String remark;
      public int solution;
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

      public static TaskStatus Rejected = new TaskStatus((int)Solution.Rejected, Color.DarkGray);
      public static TaskStatus Solved = new TaskStatus((int)Solution.Solved, Color.Green);
      public static TaskStatus InProgress = new TaskStatus((int)Solution.InProgress, Color.Yellow);
      public static TaskStatus Missed = new TaskStatus((int)Solution.Missed, Color.Yellow);

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

         if( lost > 0 || today > 0)
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
         Color result = Color.Black;

         if (lost > 0 || today > 0)
            result  = lost > 0 ? Color.Orange : Color.Blue;

         return result;
      }
   }

   /// <summary>
   /// Архив сообщений
   /// </summary>
   public class MessageArchive : DataObject
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
         StringBuilder htmlPage = new StringBuilder();
         int[] color = new int[] { 0xffffff, 0xceecf5 };

         htmlPage.Append("<html>");
         htmlPage.Append("<head>");
         htmlPage.Append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n");
         htmlPage.Append("<style type=\"text/css\">\r\n");
         htmlPage.Append("input{width:100%;}\r\n");
         htmlPage.Append("select{width:100%;}\r\n");
         htmlPage.Append("</style>\r\n");
         htmlPage.Append("</head>\r\n");
         htmlPage.Append("%%script%%");
         htmlPage.Append("<body %%onload%%>");
         htmlPage.Append("<div align=\"center\">");
         htmlPage.Append("<br>");
         htmlPage.Append(StringUtil.EscapeQuotes(text));
         htmlPage.Append("</div>");
         htmlPage.Append("<form id=\"").Append(idquest).Append("\">\r\n");
         htmlPage.Append("<table \"width=100%\">\r\n");
         htmlPage.Append("<col width=\"50%\"/>\r\n");
         htmlPage.Append("<col width=\"50%\"/>\r\n");

         int i = 0;
         foreach (QuestionItem item in items)
         {
            htmlPage.Append("<tr bgcolor=\"#")
               .Append(color[i % 2 == 0 ? 0 : 1].ToString("x"))
               .Append("\"><td>").Append(StringUtil.EscapeQuotes(item.text))
               .Append("</td><td>").Append(item.ToHtmlControl()).Append("</td></tr>\r\n");
            i++;
         }

         htmlPage.Append("\r\n</table>");
         htmlPage.Append("%%commitbutton%%");
         htmlPage.Append("</form>");
         htmlPage.Append("</body>");
         htmlPage.Append("</html>");

         html = htmlPage.ToString();

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
         int index = 1;
         switch (type)
         {
            case TEXT:
               StringBuilder textText = new StringBuilder();
               textText.Append("<input type=\"text\" ")
                  .Append("id=\"").Append(iditem).Append("_0\" ")
                  .Append("name=\"").Append(id).Append("_0\" ")
                  .Append("value=\"\">");

               return textText.ToString();
            case NUMBER:
               StringBuilder textNumber = new StringBuilder();
               textNumber.Append("<input type=\"number\" ")
                  .Append("onKeyPress=\"return numbersonly(this, event)\" ")
                  .Append("id=\"").Append(iditem).Append("_0\" ")
                  .Append("name=\"").Append(id).Append("_0\" ")
                  .Append("value=\"\">");

               return textNumber.ToString();

            case LIST:
               StringBuilder list = new StringBuilder();

               foreach (QuestionItemValue val in values)
               {
                  string capt = StringUtil.EscapeQuotes(val.value);
                  list.Append("<input type=\"checkbox\" ")
                     .Append("id=\"").Append(iditem).Append("_").Append(index).Append("\" ")
                     .Append("name=\"").Append(id).Append("_").Append(index).Append("\" ")
                     .Append("value=\"").Append(capt).Append("\">").Append(capt).Append("<br>");
                  index++;
               }

               return list.ToString();

            case SET:
               StringBuilder set = new StringBuilder();

               foreach (QuestionItemValue val in values)
               {
                  string capt = StringUtil.EscapeQuotes(val.value);
                  set.Append("<input type=\"radio\" ")
                     .Append("id=\"").Append(iditem).Append("_").Append(index).Append("\" ")
                     .Append("name=\"").Append(id).Append("_0\" ")
                     .Append("value=\"").Append(capt).Append("\">").Append(capt).Append("<br>");
                  index++;
               }

               return set.ToString();

            case BOOLEAN:
               StringBuilder boolean = new StringBuilder();

               if (values.Count == 2)
               {
                  QuestionItemValue trueVal = values[0];
                  QuestionItemValue falseVal = values[1];

                  boolean.Append("<input type=\"radio\" ")
                     .Append("id=\"").Append(iditem).Append("_").Append(index).Append("\" ")
                     .Append("name=\"").Append(id).Append("_0\" ")
                     .Append("value=\"").Append(StringUtil.EscapeQuotes(trueVal.value))
                     .Append("\">").Append(StringUtil.EscapeQuotes(trueVal.value)).Append("<br>");
                  index++;
                  boolean.Append("<input type=\"radio\" ")
                     .Append("id=\"").Append(iditem).Append("_").Append(index).Append("\" ")
                     .Append("name=\"").Append(id).Append("_0\" ")
                     .Append("value=\"").Append(StringUtil.EscapeQuotes(falseVal.value))
                     .Append("\">").Append(StringUtil.EscapeQuotes(falseVal.value)).Append("<br>");
               }

               return boolean.ToString();

            case DATASET:
               StringBuilder dataset = new StringBuilder();

               if (values.Count == 1)
                  dataset.Append("%%dataset%%").Append(values[0].value)
                     .Append("%%datasetname%%").Append(iditem).Append("_0");

               return dataset.ToString();


            default: return "Тип неопределен";
         }
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
            string result = Config.GetConfig().isFullOrgName ?
                String.Format("{0} ({1})", name, Address)
                : name;

            result = result.Replace('\n', ' ');

            return result;
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
         Org org = cmp as Org;
         bool cmpi = (org != null && id.Equals(org.id));
         return (Config.GetConfig().isFullOrgName && cmpi) ?
            Address.Equals(org.Address) :
            cmpi;
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
            int r = color & 0xFF;
            int g = (color & 0xFF00) >> 8;
            int b = (color & 0xFF0000) >> 16;
            return Color.FromArgb(r, g, b);
         }

         set
         {
            // меняем местаи r & b
            int clr = value.ToArgb() & 0xFFFFFF;
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
}
