using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmExportPhotoW : Form
   {
      protected DataSet<int, Visit> dsVisit;
      private DataSet<string, Org> dsOrg;
      private SettingFmExportPhoto setting;
      private Thread exportThread;

      class QueryParam
      {
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
         public List<Agent> agents = new List<Agent>();
      }

      public FmExportPhotoW()
      {
         InitializeComponent();
         dpv.Start = DateTime.Now;
         dpv.Finish = DateTime.Now;
         dsVisit = (DataSet<int, Visit>)DataModule.Get(Visit.OBJECT_NAME) ?? new DataSet<int, Visit>(Visit.OBJECT_NAME);
         dsOrg = (DataSet<string, Org>) DataModule.Get(Org.COMMON_OBJECT_NAME) ?? new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);

         setting = BaseFormSetting<SettingFmExportPhoto>.Load();
         tbPath.Text = setting.path;
      }

      protected const string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy}') and \"userid\"='{3}'";

      private void DoExportPhotot(object obj)
      {
         QueryParam p = (QueryParam) obj;

         List<Agent> agents = p.agents;

         SimpleDataSet<VisitInfo> visitInfo = new SimpleDataSet<VisitInfo>(VisitInfo.V_OBJECT_NAME);

         foreach (Agent a in agents)
         {
            WriteMsg("выгружем агента: " + a.Name);
            DateTime start = p.start;
            DateTime finish = p.finish;

            List<IDataSet> upd = new List<IDataSet>();
            dsOrg = DataModule.GetUserDataSet(a.id, "Org", typeof(DataSet<string, Org>), true) as DataSet<string, Org>;
            visitInfo.Filter = String.Format(COMMON_FILTER_STR, "date", start, finish, a.id);
            upd.Add(dsOrg);
            upd.Add(visitInfo);
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), upd, FmWait.ProgressIndicator).Join();
            int count = 0;

            WriteMsg("++++запрос фотографий  агента: " + a.name);
            foreach (VisitInfo vi in visitInfo.Data)
            {
               dsVisit.Filter = String.Format("\"date\" = ToDate('{0:dd/MM/yyyy HH:mm:ss}') and \"userid\"='{1}'", vi.date, a.id);

               upd = new List<IDataSet>();
               upd.Add(dsVisit);

               AddDataSet(upd, a, start, finish);


               DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), upd, FmWait.ProgressIndicator).Join();
               count += CountVisitItems(dsVisit);
               DoLoadData();
            }
            WriteMsg("------получено фотографий:" + count.ToString());
         }

         if (MessageBox.Show(
              "Выгрузка завершена, открыть папку в проводнике Windows", "Вопрос", MessageBoxButtons.OKCancel,
              MessageBoxIcon.Question) == DialogResult.OK)
         {
            Process.Start("explorer.exe", tbPath.Text);
         }
      }

      protected virtual void AddDataSet(List<IDataSet> upd, Agent a, DateTime start, DateTime finish)
      {
        
      }

      private int CountVisitItems(DataSet<int, Visit> dsVisit)
      {
         int result = 0;

         foreach(Visit v in dsVisit.Values)
            result += v.items.Count;

         return result;
      }

      private void WriteMsg(string p)
      {
         BeginInvoke(new InvokeParamHandler(WriteMsgThread), p);
      }

      private void WriteMsgThread(object p)
      {
         listBox.Items.Insert(0, p);
      }

      private void btnStart_Click(object sender, EventArgs e)
      {
         string path = tbPath.Text.Trim();

         if (path.Length == 0 || !Directory.Exists(path))
         {
            MessageBox.Show("Укажите папку для сохранения");
            tbPath.SelectAll();
            return;
         }

         QueryParam p = new QueryParam();
         p.agents = CollectAgents();
         p.start = dpv.Start.Date;
         p.finish = dpv.Finish.Date.AddDays(1);

         listBox.Items.Clear();

         BeforeStarting();

         exportThread = new Thread(DoExportPhotot);
         exportThread.Start(p);
      }

      protected virtual void BeforeStarting() { } 

      protected virtual string GetPhotoText(BaseDocument doc) 
      {
         return doc.created.ToString("dd/MM/yyyy HH:mm");
      }

      protected virtual bool CheckDoc(Visit v) 
      {
         return v.items != null && v.items.Count > 0; 
      }

      protected virtual string GetFolder()
      {
         return tbPath.Text.Trim();
      }

      protected virtual void DoLoadData()
      {
         List<Visit> list = new List<Visit>();
         MakeVisitList(list);
         list.Sort((lhs, rhs) => lhs.AgentName.CompareTo(rhs.AgentName));
         string parent = GetFolder();
         string saveName = GetFileNameMask();
         Font font = new System.Drawing.Font("Arial", 15.75F, ((System.Drawing.FontStyle)((System.Drawing.FontStyle.Bold | System.Drawing.FontStyle.Italic))), System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         SolidBrush drawBrush = new SolidBrush(Color.Red);

         int cnt = 1;

         foreach (Visit v in list)
         {
            if(CheckDoc(v))
            {
               foreach (Visit.VisitItem item in v.items)
               {
                  if (CheckItem(item))
                  {
                     if (item.id != null)
                     {
                        using (MemoryStream stream = new MemoryStream(item.id))
                        {
                           try
                           {
                              Image image = new Bitmap(stream);

                              Graphics g = Graphics.FromImage(image);
                              string text = GetPhotoText(v);
                              SizeF textSz = g.MeasureString(text, font, image.Size);
                              PointF drawPoint = new PointF(5, image.Height - textSz.Height);
                              RectangleF rect = new RectangleF(drawPoint, image.Size);
                              g.DrawString(text, font, drawBrush, rect);


                              string dir = parent + "\\" + WinChar(v.AgentName);

                              if (!Directory.Exists(dir))
                                 Directory.CreateDirectory(dir);

                              string file = FileName(saveName, v, cnt, dir);
                              image.Save(file);
                           }
                           catch (Exception e)
                           {
                              Console.WriteLine(e.Message);
                           }
                        }

                        cnt++;
                     }
                  }
               }
            }
         }
      }

      protected virtual string GetFileNameMask()
      {
         return @"{0}\{1}_{2}_{3}({4}).jpg";
      }

      protected virtual string FileName(string saveName, Visit v, int cnt, string dir)
      {
         return string.Format(saveName, dir, WinChar(v.OrgName), WinChar(v.OrgAddr), WinChar(v.Created.ToString()), cnt);
      }

      protected virtual void MakeVisitList(List<Visit> list)
      {
         list.AddRange(dsVisit.Values);
      }

      protected virtual bool CheckItem(Visit.VisitItem item)
      {
         return true;
      }

      protected string WinChar(string input)
      {
         string result = input;
         result = result.Replace('\\', '_').Replace('/', '_').Replace(':','_').Replace('*','_').Replace('?','_')
            .Replace('"','_').Replace('<','_').Replace('>','_').Replace('|','_').Replace('\t','_').Replace('\n', '_');

         return result.Substring(0, Math.Min(60, result.Length));
      }


      private void btnFolder_Click(object sender, EventArgs e)
      {
         FolderBrowserDialog fbd = new FolderBrowserDialog();
         if (fbd.ShowDialog() == DialogResult.OK)
            tbPath.Text = fbd.SelectedPath;
      }

      private void FmExportPhoto_FormClosed(object sender, FormClosedEventArgs e)
      {
         setting.path = tbPath.Text.Trim();

         setting.Save();

         if (exportThread != null)
            exportThread.Abort();
      }

      private void FmExportPhoto_Load(object sender, EventArgs e)
      {
         Manager m = CurrentUser.user as Manager;

         if (m != null)
         {
            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && cbAgent.Items.Contains(a.agent) == false)
                  cbAgent.Items.Add(a.agent);

            cbDivision.Items.Add(m.Division);
            foreach (Division d in m.Childs)
               cbDivision.Items.Add(d);
         }

         //if (cbAgent.Items.Count > 0)
         //   cbAgent.SelectedIndex = 0;

         if (cbDivision.Items.Count > 0)
            cbDivision.SelectedIndex = 0;

         cbAgent.Enabled = false;
         cbDivision.Enabled = false;
      }

      private void rbAgent_CheckedChanged(object sender, EventArgs e)
      {
         if (sender is RadioButton)
            cbAgent.Enabled = (sender as RadioButton).Checked;
      }

      private void rbDivision_CheckedChanged(object sender, EventArgs e)
      {
         if (sender is RadioButton)
            cbDivision.Enabled = (sender as RadioButton).Checked;
      }

      private List<Agent> CollectAgents()
      {
         List<Agent> result = new List<Agent>();

         if (rbAgent.Checked)
         {
            foreach(Agent a in cbAgent.CheckedItems)
            {
               result.Add(a);
            }
         }
         else if (rbDivision.Checked && cbDivision.SelectedItem != null)
         {
            Division division = cbDivision.SelectedItem as Division;

            if (division != null)
            {
               List<GRSoft.NapoleonManager.Division.DivisionAgent> agents = division.GetAllAgents();

               if (agents.Count > 0)
               {
                  List<GRSoft.NapoleonManager.Division.DivisionAgent>.Enumerator iter = agents.GetEnumerator();

                  while (iter.MoveNext())
                  {
                     GRSoft.NapoleonManager.Division.DivisionAgent agent = iter.Current;

                     if (agent != null && agent.agent != null)
                        result.Add(agent.agent);
                  };
               }
            }
         }
         else
         {
            Manager m = CurrentUser.user as Manager;

            if (m != null)
               result.AddRange(m.GetAgents().Values);
         }

         return result;
      }

      private String AgentWhere()
      {
         StringBuilder result = new StringBuilder();

         if (rbAgent.Checked && cbAgent.SelectedItem != null)
         {
            Agent agent = cbAgent.SelectedItem as Agent;

            if (agent != null)
               result.Append("\"userid\" = '").Append(agent.id).Append("'");
         }
         else if (rbDivision.Checked && cbDivision.SelectedItem != null)
         {
            Division division = cbDivision.SelectedItem as Division;

            if (division != null)
            {
               List<GRSoft.NapoleonManager.Division.DivisionAgent> agents = division.GetAllAgents();

               if (agents.Count > 0)
               {
                  result.Append("\"userid\" in (");

                  List<GRSoft.NapoleonManager.Division.DivisionAgent>.Enumerator iter = agents.GetEnumerator();
                  List<string> ids = new List<string>();

                  while (iter.MoveNext())
                  {
                     GRSoft.NapoleonManager.Division.DivisionAgent agent = iter.Current;

                     if (agent != null)
                        ids.Add(String.Format("'{0}'", agent.id));
                  };

                  result.Append(String.Join(",", ids.ToArray()));
                  result.Append(")");
               }
            }
         }

         return result.ToString();
      }
   }

   //[Serializable]
   //class SettingFmExportPhoto : BaseFormSetting<SettingFmExportPhoto>
   //{
   //   public string path = string.Empty;
   //}
}
