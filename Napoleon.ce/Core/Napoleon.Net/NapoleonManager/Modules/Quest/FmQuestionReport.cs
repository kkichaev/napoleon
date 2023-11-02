using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.NapoleonManager.Reports;
using GRSoft.NapoleonManager;
using System.Threading;
using System.IO;
using System.Drawing;
using GRSoft.NapoleonManager.Utils;
using System.Collections;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   public partial class FmQuestionReport : Form
   {
      private DataSet<int, Answer> dsAnswer;
      Dictionary<string, DataSet<string, Org>> orgs = new Dictionary<string, DataSet<string, Org>>();
      private DataSet<string, Question> dsQuestion;
      private DataSet<string, Category> dsCategory;
      private DataSet<string, Producer> dsProducer;
      private DataSet<string, Region> dsRegion;
      private DataSet<string, Region1> dsRegion1;
      private DataSet<string, Region2> dsRegion2;
      private DataSet<string, PotenzialOrg> dsPtnzOrg;
      private DataSet<DateTime, GPSPos> dsGPS;
      private Agents dsAgents;
      protected DataSet<int, Visit> dsVisit;

      public bool shortAddr = false; 
      
      public FmQuestionReport()
      {
         InitializeComponent();

         dsAnswer = (DataSet<int, Answer>)DataModule.Get(Answer.OBJECT_NAME) ??
            new DataSet<int, Answer>(Answer.OBJECT_NAME);
         dsQuestion = (DataSet<string, Question>)DataModule.Get(Question.OBJECT_NAME) ??
            new DataSet<string, Question>(Question.OBJECT_NAME);
         dsQuestion.Filter = "\"idquest\" is null or \"idquest\" is not null";
         dsProducer = (DataSet<string, Producer>)DataModule.Get(Producer.OBJECT_NAME) ??
            new DataSet<string, Producer>(Producer.OBJECT_NAME);
         dsCategory = (DataSet<string, Category>)DataModule.Get(Category.OBJECT_NAME) ??
            new DataSet<string, Category>(Category.OBJECT_NAME);
         dsRegion = (DataSet<string, Region>)DataModule.Get(GRSoft.NapoleonManager.Region.OBJECT_NAME) ??
            new DataSet<string, Region>(GRSoft.NapoleonManager.Region.OBJECT_NAME);
         dsRegion1 = (DataSet<string, Region1>)DataModule.Get(Region1.OBJECT_NAME) ??
            new DataSet<string, Region1>(Region1.OBJECT_NAME);
         dsRegion2 = (DataSet<string, Region2>)DataModule.Get(Region2.OBJECT_NAME) ??
            new DataSet<string, Region2>(Region2.OBJECT_NAME);
         dsPtnzOrg = (DataSet<string, PotenzialOrg>)DataModule.Get(PotenzialOrg.OBJECT_NAME) ??
            new DataSet<string, PotenzialOrg>(PotenzialOrg.OBJECT_NAME);

         dsGPS = (DataSet<DateTime, GPSPos>)DataModule.Get(GPSPos.OBJECT_NAME) ??
            new DataSet<DateTime, GPSPos>(GPSPos.OBJECT_NAME);

         dsPtnzOrg.Filter = "(not (\"userid\" is null)) or \"userid\" is null";
         dsAgents = Agents.GetDataSet();

         
#if !QUESTION_REPORT_PYTHON
         gbQuestType.Visible = false;
#endif
      }

      private string makeUseridWhere()
      {
         StringBuilder result = new StringBuilder();
         List<string> ids = CollectUserids();

         if (ids.Count > 0)
         {
            result.Append(" and \"userid\" in (");
            result.Append(String.Join(",", ids.ToArray()));
            result.Append(")");
         }

         return result.ToString();
      }

      protected virtual List<string> CollectUserids()
      {
         List<string> result = new List<string>();
         CheckedListBox.CheckedIndexCollection list = clbAgent.CheckedIndices;

         for (int i = 0; i < list.Count; i++)
            result.Add(String.Format("'{0}'", ((Agent)clbAgent.Items[list[i]]).id));

         return result;
      }

      private void AppendOrg(List<IDataSet> list)
      {
         Manager dm = CurrentUser.user as Manager;
         string uid = DataUtils.MakeFilterFromAgents(null, dm.GetAgents());

         foreach (Agent a in dm.GetAgents().Data)
         {
            DataSet<string, Org> orgs =
               DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;

            if (orgs.Count == 0)
            {
               orgs.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), orgs.Name);
               list.Add(orgs);
            }
            this.orgs[a.id] = orgs;
         }
      }

      protected virtual string GetReportName(bool horizontal)
      {
         return horizontal ? "quest_rep" : "quest_pivot_rep";
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
#if !QUESTION_REPORT_PYTHON
         DoReport();
#else
         if (clbAgent.CheckedItems.Count == 0 || clbQuest.CheckedItems.Count == 0)
            MessageBox.Show("Выберите условия для отчета");
         else
            DoPythonReport(GetReportName(rbHor.Checked));
#endif
      }

      protected class Param : GRSoft.Network.DataObject
      {
         public class Item : GRSoft.Network.DataObject
         {
            public String id = "";
         }


         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
         public List<Item> userids = new List<Item>();
         public List<Item> quests = new List<Item>();
         public string hrefBase = Config.GetConfig().HrefBase;
         public int param = 0;
      }

      private void DoPythonReport(string rep)
      {
         ReportResult.DoReport(rep, CreateParam(), this);
      }

      protected virtual Param CreateParam()
      {
         Param arg = new Param();
         arg.start = dtpFrom.Value.Date;
         arg.finish = dtpTill.Value.Date;
         arg.userids = DoItems(CollectUserids());
         arg.quests = DoItems(CollectQuests());

         return arg;
      }

      private List<Param.Item> DoItems(List<string> list) {
         List<Param.Item> res = new List<Param.Item>();

         foreach (string id in list)
         {
            Param.Item i = new Param.Item();
            i.id = id;
            res.Add(i);
         }

         return res;
      }

      private List<string> CollectQuests()
      {
         List<string> res = new List<string>();

         CheckedListBox.CheckedIndexCollection list = clbQuest.CheckedIndices;

         for (int i = 0; i < list.Count; i++)
            res.Add(String.Format("'{0}'", ((Question)clbQuest.Items[list[i]]).idquest));

         return res;
      }

      private void DoReport()
      {
#if HTTP_SERVER
         dsVisit = new DataSet<int, Visit>(!cbPhoto.Checked ? VisitInfo.V_OBJECT_NAME : Visit.OBJECT_NAME_HTTP);
#else
         dsVisit = new DataSet<int, Visit>(!cbPhoto.Checked ? VisitInfo.V_OBJECT_NAME : Visit.OBJECT_NAME);
#endif
         String userids = makeUseridWhere();

         dsAnswer.Filter = String.Format("\"created\" >= ToDate('{0:dd/MM/yyyy}') and \"created\" < ToDate('{1:dd/MM/yyyy}')",
            dtpFrom.Value.Date.Date, dtpTill.Value.Date.Date.AddDays(1)) + userids;
         dsVisit.Filter = String.Format("\"date\" >= ToDate('{0:dd/MM/yyyy}') and \"date\" < ToDate('{1:dd/MM/yyyy}')",
            dtpFrom.Value.Date.Date, dtpTill.Value.Date.Date.AddDays(1)) + userids;
         dsGPS.Filter = String.Format("\"date\" >= ToDate('{0:dd/MM/yyyy}') and \"date\" < ToDate('{1:dd/MM/yyyy}')",
            dtpFrom.Value.Date.Date, dtpTill.Value.Date.Date.AddDays(1)) + userids;

         List<IDataSet> list = new List<IDataSet>();
         AppendOrg(list);
         list.Add(dsAgents);
         list.Add(dsRegion1);
         list.Add(dsRegion2);
         list.Add(dsRegion);
         list.Add(dsPtnzOrg);
         list.Add(dsCategory);
         list.Add(dsProducer);
         list.Add(dsQuestion);
         list.Add(dsAnswer);

#if ZooOpt
         if( cbPhoto.Checked )
            list.Add(dsVisit);
#else
         list.Add(dsVisit);
#endif

         list.Add(dsGPS);

         DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed, DataModule_OnDataResponceError);
         DBConnection conn = Config.GetConfig().GetConnection();
         conn.ReceiveTimeout = 60 * 1000 * 3;
         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(
            conn, list, FmWait.ProgressIndicator)
         );
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         FmWait.CloseForm();
         DataModule.ClearEvents();
         Invoke(new InvokeDelegate(RefreshData));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         FmWait.CloseForm();
         DataModule.ClearEvents();
         MessageBox.Show(e.Msg);
      }

      void RefreshData()
      {
         /** Отфильтровать анкеты **/
         List<string> filterQuest = new List<string>();
         filterQuest.AddRange(dsQuestion.Keys);

         if (clbQuest.Items.Count > 0)
         {
            IList sel = clbQuest.CheckedIndices;

            if (sel.Count > 0)
            {
               filterQuest.Clear();

               for (int i = 0; i < clbQuest.Items.Count; i++)
                  if (!sel.Contains(i))
                     filterQuest.Add((clbQuest.Items[i] as Question).idquest);

               foreach (string id in filterQuest)
                  dsQuestion.Remove(id);
            }
         }

         List<Answer> list = new List<Answer>();
         list.AddRange(dsAnswer.Values);
         list.Sort(new Comparison<Answer>(delegate(Answer a1, Answer a2)
            {
               int result = 0;

               if (a1 != null && a2 != null && a1.org != null && a2.org != null)
               {
                  result = a1.org.id.CompareTo(a2.org.id);

#if BTL
                  if (a1.quest != null && a2.quest != null)
                  {
                     if (result == 0)
                        result = a1.quest.category.CompareTo(a2.quest.category);

                     if (result == 0)
                        result = a2.quest.producer.CompareTo(a2.quest.producer);
                  }
#endif
               }
               return result;
            }));
         
         Dictionary<string, Dictionary<string, Dictionary<string, List<Answer>>>> data =
            new Dictionary<string, Dictionary<string, Dictionary<string, List<Answer>>>>();

         foreach (Answer answer in list)
         {
            if (answer.org != null &&
               answer.quest != null &&
               answer.items != null &&
               answer.items.Count > 0 &&
               dsQuestion.ContainsKey(answer.quest.idquest))
            {
               Dictionary<string, Dictionary<string, List<Answer>>> row = null;

               //id
               if (data.ContainsKey(answer.org.id))
                  row = data[answer.org.id];
               else
               {
                  row = new Dictionary<string, Dictionary<string, List<Answer>>>();
                  data.Add(answer.org.id, row);
               }

               string categ = "";
               string prod = "";
#if BTL
               categ = answer.quest.category;
               prod = answer.quest.producer;
#endif

               //category
               Dictionary<string, List<Answer>> rrow = null;
               if (row.ContainsKey(categ))
                  rrow = row[categ];
               else
               {
                  rrow = new Dictionary<string, List<Answer>>();
                  row.Add(categ, rrow);
               }

               //producer
               List<Answer> answers = null;
               if (rrow.ContainsKey(prod))
                  answers = rrow[prod];
               else
               {
                  answers = new List<Answer>();
                  rrow.Add(prod, answers);
               }

               answers.Add(answer);
            }
         }

         new Thread(new ParameterizedThreadStart(delegate(object o)
         {
            BeginInvoke(new EmptyParamHandler(delegate(){ FmWait.ShowForm(this, true);}));
            QuestionExelReport rpt = MakeReport();
            rpt.dsCategory = dsCategory;
            rpt.dsProducer = dsProducer;
            rpt.dsVisit = dsVisit;
            rpt.dsGPS = dsGPS;
            rpt.dsQuestion = dsQuestion;
            rpt.shortAddr = shortAddr;
            rpt.name = String.Format("{0}_{1}", dtpFrom.Value.Date.Date.ToShortDateString(),
               dtpTill.Value.Date.Date.ToShortDateString()); 
            rpt.Build(data);
            rpt.Show();
            BeginInvoke(new EmptyParamHandler(delegate(){ FmWait.CloseForm();}));
         })).Start();
      }

      private QuestionExelReport MakeReport()
      {
         Type prcType = FormEntries.GetFormType(typeof(QuestionExelReport));
         ConstructorInfo ci = prcType.GetConstructor(Type.EmptyTypes);
         return (QuestionExelReport)ci.Invoke(new object[] { });
      }

      private void btnQuestRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsQuestion);

         FmWait.StdDataRefresh(this, upd, () => { DoLoadQuest(); });
      }

      private void DoLoadQuest()
      {
         clbQuest.Items.Clear();

         List<Question> list = new List<Question>();
         list.AddRange(dsQuestion.Values);
         list.Sort((Question lhs, Question rhs) => { return lhs.Name.CompareTo(rhs.Name); });

         clbQuest.Items.AddRange(list.ToArray());
      }

      private void setChecked(object sender, EventArgs args)
      {
         checkList(sender as ToolStripButton, clbQuest);
      }

      private void setCheckedAgent(object sender, EventArgs args)
      {
         checkList(sender as ToolStripButton, clbAgent);
      }

      private void checkList(ToolStripButton control, CheckedListBox box)
      {
         try
         {
            if (control != null)
            {
               bool val = Boolean.Parse(control.Tag.ToString());

               for (int i = 0; i < box.Items.Count; i++)
                  box.SetItemChecked(i, val);
            }
         }
         catch (Exception) { }
      }

      private void FmQuestionReport_Load(object sender, EventArgs e)
      {
         OnLoad();
      }

      public virtual void OnLoad()
      {
         List<Agent> agents = new List<Agent>();
         List<Division> divisions = new List<Division>();
         Manager m = CurrentUser.user as Manager;

         if (m != null)
         {
            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && agents.Contains(a.agent) == false)
                  agents.Add(a.agent);
         }

         if (agents.Count > 0)
         {
            agents.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });
            for (int i = 0; i < agents.Count; i++)
            {
               clbAgent.Items.Add(agents[i]);
               clbAgent.SetItemChecked(i, true);
            }
         }
      }
   }
}

class QuestionExelReport : Excel
{
   public DataSet<string, Category> dsCategory;
   public DataSet<string, Producer> dsProducer;
   public DataSet<int, Visit> dsVisit;
   public DataSet<DateTime, GPSPos> dsGPS;
   public DataSet<string, Question> dsQuestion;
   public string name;
   public bool shortAddr = false;
   int questClmn = 14;

   string hrefBase;
   
   Dictionary<string, Dictionary<string, Dictionary<string, List<Answer>>>> repData;
   Dictionary<string, string> captions = new Dictionary<string, string>();

   public QuestionExelReport()
   {
#if STD_QUESTION_REPORT
      questClmn = 8;
#endif
      hrefBase = Config.GetConfig().HrefBase;
   }

   // orgID => Category => Producer => [Answ]

   public void Build(Dictionary<string, Dictionary<string, Dictionary<string, List<Answer>>>> data)
   {
      int row = 2;

      List<string> collumns = new List<string>();
      captions.Clear();

      repData = data;

      try
      {
#if BTL
         List<Answer> commonData = new List<Answer>();
         foreach (KeyValuePair<string, Dictionary<string, Dictionary<string, List<Answer>>>> d1 in data)
         {
            commonData.Clear();

            foreach (KeyValuePair<string, Dictionary<string, List<Answer>>> d2 in d1.Value)
            {
               foreach (KeyValuePair<string, List<Answer>> d3 in d2.Value)
               {
                  foreach (Answer a in d3.Value)
                  {
                     UpdateRowForOrg(row, collumns, a, d2.Key, d3.Key);
                     //row++
                  }

                  foreach (Answer a in commonData)
                  {
                     UpdateRowForOrg(row, collumns, a, d2.Key, d3.Key);
                     //row++
                  }

                  if (d3.Key.Equals(string.Empty) && d2.Key.Equals(string.Empty))
                     commonData.AddRange(d3.Value);

                  if (!d3.Key.Equals(string.Empty))
                     row++;
               }
            }
            row++;
         }
#else
         foreach (KeyValuePair<string, Dictionary<string, Dictionary<string, List<Answer>>>> d1 in data)
         {
            foreach (KeyValuePair<string, Dictionary<string, List<Answer>>> d2 in d1.Value)
            {
               foreach (KeyValuePair<string, List<Answer>> d3 in d2.Value)
               {
                  Dictionary<string, bool> usedQuest = new Dictionary<string, bool>();
                  foreach (Answer a in d3.Value)
                  {
                     if (usedQuest.ContainsKey(a.quest.idquest))
                        row++;
                     else
                        usedQuest[a.quest.idquest] = true;

                     UpdateRowForOrg(row, collumns, a, d2.Key, d3.Key);
                     //row++
                  }
               }
            }
            row++;
         }
#endif

#if !QUEST_VISIT_DATA_SKIP
         Dictionary<String, Boolean> usedOrgs = new Dictionary<String, Boolean>();
         foreach (Visit v in dsVisit.Data)
         {
            if (data.ContainsKey(v.id) || usedOrgs.ContainsKey(v.id))
               continue;

            usedOrgs.Add(v.id, true);

            double lat = v.latitude;
            double lon = v.longitude;
            if (v.latitude == 0)
               TryFindLocation(out lat, out lon, v.created, v.org);

            String agname = (v.agent == null) ? "Агент с кодом " + v.userid : v.agent.Name;
#if BTL
                     SetOrgData(row, v.org, v.created.ToString("yyyy.MM.dd HH:mm"), agname, lat, lon);
#else
            SetOrgData(row, v.org, v.created.ToShortDateString(), agname, lat, lon);
#endif
            MakePhotos(row, collumns, FindSameVisits(v));

            row++;
         }
#endif
         row = 1;

         foreach (KeyValuePair<string, Dictionary<string, Dictionary<string, List<Answer>>>> d1 in data)
         {
            row++;

            if (!rowData.ContainsKey(row))
               continue;

            RowData rd = rowData[row];

            List<Visit> visits = new List<Visit>();
            foreach (Visit visit in dsVisit.Data)
               if (visit.org.id.Equals(rd.id.ToString()) && rd.date.Date.Equals(visit.date.Date))
               {
                  visits.Add(visit);
               }

            if (visits.Count == 0)
               continue;

            foreach (KeyValuePair<string, Dictionary<string, List<Answer>>> d2 in d1.Value)
            {
               foreach (KeyValuePair<string, List<Answer>> d3 in d2.Value)
               {
                  MakePhotos(row, collumns, visits);

                  if (!d3.Key.Equals(string.Empty))
                     row++;
               }
            }
         }

         double[] widths = new double[collumns.Count + questClmn];

         Header();
         for (int i = 0; i < collumns.Count; i++)
         {
            string val = collumns[i];
            
            if (captions.ContainsKey(collumns[i]))
               val = captions[collumns[i]];

            SetValue(1, i + questClmn, val);
         }

         for (int i = 1; i <= collumns.Count + questClmn; i++)
            AutoFit(i);

         if (shortAddr)
            HideColumns("A:C");

         if (collumns.Count > 250)
         {
            string bookName = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments) + "\\"
               + name + ".xlsx";
            if (File.Exists(bookName))
               File.Delete(bookName);

            SaveAs(bookName, xlOpenXMLWorkbook);
         }
         else
         {
            string bookName = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments) + "\\"
               + name + ".xls";
            if (File.Exists(bookName))
               File.Delete(bookName);

            SaveAs(bookName);
         }
      }
      catch (Exception e)
      {
         MainForm.Instance.Invoke(new EmptyParamHandler(delegate() {
            ViewException ve = new ViewException();
            ve.Exception = e;
            ve.Show(MainForm.Instance);
         }));
      }
   }

   protected virtual void Header()
   {
      SetValue(1, 1, "Область");
      SetValue(1, 2, "Район");
      SetValue(1, 3, "НП");
      SetValue(1, 4, "Адрес");
      SetValue(1, 5, "Наименование");
      SetValue(1, 6, "Дата");
#if STD_QUESTION_REPORT
      SetValue(1, 7, "Торговый представитель");
#else
         SetValue(1, 7, "Аудитор");
         SetValue(1, 8, "№РТТ");
         SetValue(1, 9, "Широта");
         SetValue(1, 10, "Долгота");
         SetValue(1, 11, "Адрес");
         SetValue(1, 12, "Категория");
         SetValue(1, 13, "Производитель");
#endif
   }

   private List<Visit> FindSameVisits(Visit v)
   {
      List<Visit> ret = new List<Visit>();
      ret.Add(v);
      foreach (Visit vd in dsVisit.Data)
      {
         if (vd != v && vd.id == v.id && vd.Date.Date == v.Date.Date)
            ret.Add(vd);
      }
      return ret;
   }

   private void MakePhotos(int row, List<string> collumns, List<Visit> visits)
   {
      int fileIndex = 0;
      int idx = 1;
      foreach (Visit visit in visits)
      {
         if(visit.items != null)
            foreach (Visit.VisitItem item in visit.items)
            {
               if (item == null)
                  continue;

               string caption = item.caption;
               if (caption.Length == 0)
               {
                  caption = "Фото" + idx.ToString();
                  idx++;
               }
               if (!collumns.Contains(caption))
                  collumns.Add(caption);

               int clmn = collumns.IndexOf(caption);

#if HTTP_SERVER
               if (item.name.Length == 0)
                  continue;

               string refn = item.name;
               refn = refn.Replace("\\", "/");
               if (refn.StartsWith("/"))
                  refn = refn.Substring(1);
               
               MakeHyperlinks(row, clmn + questClmn, "", hrefBase + refn);
#else
               if (item.id == null)
                  continue;

               string path = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments) + "\\" + name;
               DirectoryInfo dir = new DirectoryInfo(path);

               if (!dir.Exists)
                  dir.Create();

               string fileName = visit.id.Replace("\t", "").Replace("/", "").Replace("\\", "").Replace("?", "").Replace("*", "").Replace(":", "") + "_" + fileIndex.ToString() + "_" + caption + ".jpg";
               FileStream fs = new FileStream(path + "\\" + fileName, FileMode.OpenOrCreate);
               fs.Write(item.id, 0, item.id.Length);
               fs.Close();

               MakeHyperlinks(row, clmn + questClmn, "", name + "\\" + fileName);
#endif
               SetValue(row, clmn + questClmn, caption);
            }

         fileIndex++;
      }
   }

   private void UpdateRowForOrg(int row, List<string> columns, Answer a, string c, string p)
   {
      if (a.org != null)
         SetRowValue(row, columns, a, a.org, c, p);
   }

   private Dictionary<string, List<KeyValuePair<string, string>>> visitLink 
      = new Dictionary<string,List<KeyValuePair<string,string>>>();

   protected virtual void SetOrgData(int row, Org org, string created, string agent, double lat, double lon)
   {
      PotenzialOrg po = org as PotenzialOrg;
      
      if (po != null && po.region != null)
      {
         Region1 r1 = po.region.r1;
         Region2 r2 = po.region.r2;

         if (r1 != null && r2 != null)
         {
            SetValue(row, 1, r2.Name);
            SetValue(row, 2, r1.Name);
         }

         if (po.region != null)
            SetValue(row, 3, po.region.Name);
      }

      SetValue(row, 4, org.Address);
      SetValue(row, 5, org.Name);
      SetValue(row, 6, created);
      SetValue(row, 7, agent);
#if STD_QUESTION_REPORT
#else
      SetValue(row, 8, org.id);

      SetValue(row, 9, lat);
      SetValue(row, 10, lon);
      SetValue(row, 11, new Location(lat, lon).GetAddress());
#endif
   }

   private bool TryFindLocation(out double lat, out double lon, DateTime date, Org org)
   {
      lat = 0;
      lon = 0;

      if (org.latitude != 0)
      {
         lat = org.latitude;
         lon = org.longitude;

         return true;
      }

      if (repData.ContainsKey(org.id))
         foreach (KeyValuePair<string, Dictionary<string, List<Answer>>> d2 in repData[org.id])
            foreach (KeyValuePair<string, List<Answer>> d3 in d2.Value)
               foreach (Answer a in d3.Value)
                  if (a.latitude != 0)
                  {
                     lat = a.latitude;
                     lon = a.longitude;
                     return true;
                  }

      GPSPos before = null, after = null;
      foreach (GPSPos pos in dsGPS.Data)
      {
         int cmp = pos.date.CompareTo(date);
         if( cmp == 0 )
         {
            lat = pos.latitude;
            lon = pos.longitude;
            return true;
         }
         if (cmp < 0)
            before = pos;

         if (cmp > 0)
         {
            after = pos;
            break;
         }
      }

      if (before != null && after != null)
      {
         double dist = Coordutils.Distance(before.latitude, before.longitude, after.latitude, after.longitude);
         if (dist < 5000)
         {
            lat = (before.latitude + after.latitude) / 2;
            lon = (before.longitude + after.longitude) / 2;
            return true;
         }
      }

      return false;
   }

   private Dictionary<string, bool> questCoolumnsCreated = new Dictionary<string, bool>();

   private class RowData
   {
      public string id = string.Empty;
      public DateTime date = DateTime.MinValue;
   }

   Dictionary<int, RowData> rowData = new Dictionary<int, RowData>();

   private void SetRowValue(int row, List<string> columns, Answer a, Org org, string c, string p)
   {
      RowData rd = new RowData();
      rd.id = org.id;
      rd.date = a.created;
      rowData[row] = rd;

      double lat = a.latitude;
      double lon = a.longitude;
      if (a.latitude == 0)
         TryFindLocation(out lat, out lon, a.created, org);

      String name = (a.agent == null) ? "Агент с кодом " + a.userid : a.agent.Name;
#if BTL
      SetOrgData(row, org, a.created.ToString("yyyy.MM.dd HH:mm"), name, lat, lon);
#else
      SetOrgData(row, org, a.created.ToShortDateString(), name, lat, lon);
#endif

      if (dsCategory.ContainsKey(c))
         SetValue(row, 12, dsCategory[c].Name);

      if(dsProducer.ContainsKey(p))
         SetValue(row, 13, dsProducer[p].Name);

      if (!questCoolumnsCreated.ContainsKey(a.quest.idquest))
      {
         questCoolumnsCreated.Add(a.quest.idquest, true);

         if (dsQuestion.ContainsKey(a.quest.idquest))
         {
            Question quest = dsQuestion[a.quest.idquest];

            foreach (QuestionItem item in quest.items)
            {
               if (item.type == QuestionItem.LIST)
               {
                  foreach (QuestionItemValue val in item.values)
                  {
                     string id = item.iditem + val.value;
                     if (!captions.ContainsKey(id))
                     {
                        columns.Add(id);
                        captions.Add(id, val.value);
                     }
                  }
               }
               else
               {
                  columns.Add(item.iditem);
                  captions.Add(item.iditem, item.id);
               }
            }
         }

      }

      string itemsid = string.Empty;
      const string VALUE_PRESENT = "X";

      Question q = a.quest;

      if (q != null && q.items != null)
         foreach (QuestionItem i in q.items) 
         {
            if (i.type == QuestionItem.NUMBER)
            {
               int clmn  = columns.IndexOf(i.iditem);

               if (clmn != -1)
                  SetValue(row, clmn + questClmn, "0");
            }
         }

      foreach (AnswerItem items in a.items)
      {
         string val = items.answer;
         int clmn = -1;

         if (items.type == QuestionItem.LIST)
         {
            clmn = columns.IndexOf(items.iditem+items.answer);
            val = VALUE_PRESENT;
            SetCellHorizontalAlign(row, clmn + questClmn, xlCenter);
         }
         else
            clmn = columns.IndexOf(items.iditem);
         
         if (clmn != -1)
            SetValue(row, clmn + questClmn, val);
      }
   }

   private string MakeLink(List<string> list)
   {
      StringBuilder sb = new StringBuilder();

      foreach (string s in list)
         sb.Append(s).Append('\n');

      return sb.ToString();
   }

   public void Show()
   {
      Visible = true;
   }
}
