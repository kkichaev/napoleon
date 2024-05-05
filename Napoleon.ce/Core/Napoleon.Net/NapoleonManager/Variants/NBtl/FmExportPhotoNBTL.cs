using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmExportPhotoNBTL : Form
   {
      private DataSet<string, ContractDef> dsContract;
      protected DataSet<int, Visit> dsVisit;
      protected DataSet<int, VisitInfo> dsVisitInfo;
      SimpleDataSet<ScriptDef> dsScriptDef;
      SimpleDataSet<ScriptDoc> dsScripts;
      SimpleDataSet<Question> questions;
      SimpleDataSet<Answer> answers;

      private DataSet<string, Org> dsOrg;
      private SettingFmExportPhotoNBTL setting;

      Dictionary<int, List<string>> answOnPhoto = new Dictionary<int, List<string>>();

      Dictionary<int, ScriptDef> scriptDefs = new Dictionary<int, ScriptDef>();

      public static readonly string CONTRACT = "Контракт";
      public static readonly string AGENT_NAME = "Фамилия сотрудника";
      public static readonly string SLS_NAME = "Название торговой сети";
      public static readonly string ORG_NAME = "Название и адрес магазина";

      List<string> items = new List<string>(new string[]{CONTRACT, AGENT_NAME, SLS_NAME , ORG_NAME});

      public FmExportPhotoNBTL()
      {
         InitializeComponent();
         dpv.Start = DateTime.Now;
         dpv.Finish = DateTime.Now;
         dsVisit = (DataSet<int, Visit>)DataModule.Get(Visit.OBJECT_NAME) ?? new DataSet<int, Visit>(Visit.OBJECT_NAME);
         dsOrg = (DataSet<string, Org>) DataModule.Get(Org.OBJECT_NAME) ?? new DataSet<string, Org>(Org.OBJECT_NAME);
         dsContract = (DataSet<string, ContractDef>)DataModule.Get(ContractDef.OBJECT_NAME) ?? new DataSet<string, ContractDef>(ContractDef.OBJECT_NAME);
         dsVisitInfo = (DataSet<int, VisitInfo>)DataModule.Get(VisitInfo.V_OBJECT_NAME) ?? new DataSet<int, VisitInfo>(VisitInfo.V_OBJECT_NAME);
         
         questions = new SimpleDataSet<Question>(Question.OBJECT_NAME, false);
         answers = new SimpleDataSet<Answer>(Answer.OBJECT_NAME, false);

         dsScriptDef = new SimpleDataSet<ScriptDef>(ScriptDef.OBJECT_NAME, false);
         dsScripts = new SimpleDataSet<ScriptDoc>(ScriptDoc.OBJECT_NAME, false);

         setting = BaseFormSetting<SettingFmExportPhotoNBTL>.Load();
         tbPath.Text = setting.path;

         cbLevel1Value.Items.AddRange(items.ToArray());
         cbLevel2Value.Items.AddRange(items.ToArray());
         cbLevel3Value.Items.AddRange(items.ToArray());
         cbLevel4Value.Items.AddRange(items.ToArray());

         try
         {
            cbLevel1.Checked = setting.lvl_1;

            if (setting.lvl1val != null)
               cbLevel1Value.SelectedIndex = cbLevel1Value.Items.IndexOf(setting.lvl1val);

            cbLevel2.Checked = setting.lvl_2;

            if (setting.lvl2val != null)
               cbLevel2Value.SelectedIndex = cbLevel2Value.Items.IndexOf(setting.lvl2val);

            cbLevel3.Checked = setting.lvl_3;

            if (setting.lvl3val != null)
               cbLevel3Value.SelectedIndex = cbLevel3Value.Items.IndexOf(setting.lvl3val);

            cbLevel4.Checked = setting.lvl_4;

            if (setting.lvl4val != null)
               cbLevel4Value.SelectedIndex = cbLevel4Value.Items.IndexOf(setting.lvl4val);

         }catch(Exception){

         }

         lblInfo.Text = "";
      }

      private void btnStart_Click(object sender, EventArgs e)
      {
         lblInfo.Text = "";

         string path = tbPath.Text.Trim();

         if (path.Length == 0 || !Directory.Exists(path))
         {
            MessageBox.Show("Укажите папку для сохранения");
            tbPath.SelectAll();
            return;
         }

         const string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy} 23:59:59')";
         dsVisitInfo.Filter = String.Format(COMMON_FILTER_STR, "date", dpv.Start.Date, dpv.Finish.Date);
         dsScripts.Filter = String.Format(COMMON_FILTER_STR, "created", dpv.Start.Date, dpv.Finish.Date);
         dsScriptDef.Filter = "\"userid\" is null or not \"userid\" is null";
         questions.Filter = "not \"id\" is null";
         answers.Filter = string.Format(COMMON_FILTER_STR, "created", dpv.Start.Date, dpv.Finish.Date);

         const string PERIOD_FILTER_STR = "\"start\" < ToDate('{1:dd/MM/yyyy}') and \"finish\" >= ToDate('{0:dd/MM/yyyy}')";
         DateTime finish = dpv.Finish.AddDays(1);
         dsContract.Filter = string.Format(PERIOD_FILTER_STR, dpv.Start, finish);
         dsOrg.Filter = "\"id\" is null or \"id\" is not null";

         String agentWhere = AddWhere();

         if (agentWhere.Length > 0)
            dsVisitInfo.Filter += " and " + agentWhere;

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsOrg);
         upd.Add(dsContract);
         upd.Add(dsVisitInfo);

         upd.Add(dsScripts);
         upd.Add(dsScriptDef);
         upd.Add(questions);
         upd.Add(answers);

         FmWait.StdDataRefresh(this, upd, DoLoadDataLow);
      }

      void PrepareArnswers()
      {
         // idquest => [idIitem]
         Dictionary<string, List<string>> photoItems = new Dictionary<string, List<string>>();

         foreach (Question q in questions.Data)
            foreach (QuestionItem qi in q.items)
            {
               if (qi.showInPhoto != 0)
               {
                  List<string> items;
                  if (!photoItems.TryGetValue(q.idquest, out items))
                  {
                     items = new List<string>();
                     photoItems[q.idquest] = items;
                  }

                  items.Add(qi.iditem);
               }
            }

         foreach (ScriptDef sd in dsScriptDef.Data)
         {
            scriptDefs[sd.id] = sd;
            foreach (ScriptDefItem sdi in sd.items)
            {
               if (sdi.curType == Answer.OBJECT_NAME)
               {
                  List<string> items;
                  if (!photoItems.TryGetValue(sdi.condParam, out items))
                     continue;

                  List<string> dest;
                  if (!answOnPhoto.TryGetValue(sd.id, out dest))
                  {
                     dest = new List<string>();
                     answOnPhoto[sd.id] = dest;
                  }
                  dest.AddRange(items);
               }
            }
         }
      }

      private void DoLoadDataLow()
      {
         PrepareArnswers();


         int c = 1;
         int sz = dsVisitInfo.Count;

         const string LABEL_TEXT = "Обработно запросов: {0} из {1}";

         foreach (VisitInfo i in dsVisitInfo.Values)
         {
            dsVisit.Filter = string.Format("\"created\" = ToDate('{0:dd/MM/yyyy HH:mm:ss}') and \"userid\"='{1}'", i.created, i.userid);

            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(dsVisit);
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), upd, null).Join();

            lblInfo.Text = string.Format(LABEL_TEXT, c, sz);
            DoLoadData(c);
            c++;
         }

         FmWait.CloseForm();

         if (MessageBox.Show(
               "Выгрузка завершена, открыть папку в проводнике Windows", "Вопрос", MessageBoxButtons.OKCancel,
               MessageBoxIcon.Question) == DialogResult.OK)
         {
            Process.Start("explorer.exe", tbPath.Text);
            Close();
         }
      }

      Dictionary<string,string> FindAnswers(ScriptDoc sd)
      {
         Dictionary<string, string> ret = new Dictionary<string, string>();

         foreach (ScriptDocItem sdi in sd.items)
         {
            if(sdi.type == Answer.OBJECT_NAME)
               foreach (Answer a in answers.Data)
               {
                  if (a.userid == sd.userid && a.created == sdi.date)
                  {
                     foreach(AnswerItem ai in a.items)
                     {
                        ret[ai.iditem] = ai.answer;
                     }
                  }
               }
         }
         return ret;
      }

      List<string> GetPhotoText(ScriptDoc sd)
      {
         List<string> ret = new List<string>();
         List<string> items;
         if(answOnPhoto.TryGetValue(sd.scriptId, out items))
         {
            Dictionary<string, string> answ = FindAnswers(sd);
            foreach(string item in items)
            {
               if(answ.ContainsKey(item))
               {
                  ret.Add(answ[item]);
               }
            }
         }
         return ret;
      }

      class ScriptData
      {
         public string name = "";
         public List<string> answers = new List<string>();
      }

      ScriptData GetScriptStep(Visit v)
      {
         ScriptData ret = new ScriptData();

         foreach(ScriptDoc sd in dsScripts.Data)
         {
            int cnt = 0;
            foreach(ScriptDocItem i in sd.items)
            {
               if(i.type == Visit.OBJECT_NAME && i.date.CompareTo(v.created) == 0)
               {
                  if (scriptDefs.ContainsKey(sd.scriptId))
                  {
                     ScriptDef def = scriptDefs[sd.scriptId];
                     if (def.items.Count > cnt)
                     {
                        ret.name = def.items[cnt].Name;
                        ret.answers = GetPhotoText(sd);
                     }
                  }
                  break;
               }
               cnt++;
            }
         }

         return ret;
      }

      private void DoLoadData(int cnt_v)
      {
         List<Visit> list = new List<Visit>();
         list.AddRange(dsVisit.Values);
         list.Sort((lhs, rhs) => lhs.AgentName.CompareTo(rhs.AgentName));
         string parent = tbPath.Text.Trim();
         const string saveName = @"{0}_{1}_{2}_{3}_({4}_{5})";
         Font font = new System.Drawing.Font("Arial", 15.75F, ((System.Drawing.FontStyle)((System.Drawing.FontStyle.Bold | System.Drawing.FontStyle.Italic))), System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         SolidBrush drawBrush = new SolidBrush(Color.Red);

         foreach (Visit v in list)
         {
            ScriptData scriptData = GetScriptStep(v);
            string scriptStep = scriptData.name;
            if (scriptStep.Length > 0)
               scriptStep += ' ';
            if (v.items != null && v.items.Count > 0 && v.contract != null)
            {
               v.items.Sort((x, y) => { return x.date.CompareTo(y.date); });

               int cnt = 1;
               foreach (Visit.VisitItem item in v.items)
               {
                  if (item.id == null)
                     continue;

                  using (MemoryStream stream = new MemoryStream(item.id))
                  {
                     Image image = new Bitmap(stream);

                     // Не удаляйте этот код, если не нужет будет 
                     // просто закоментировать!
                     Graphics g = Graphics.FromImage(image);

                     string phText = string.Join(",", scriptData.answers.ToArray());
                     string text = scriptStep + v.OrgName + "," + v.OrgAddr + ", " + item.date.ToString("dd/MM/yyyy HH:mm");
                     if(phText.Length > 0)
                     {
                        text += "\n" + phText;
                     }

                     SizeF textSz = g.MeasureString(text, font);
                     //PointF drawPoint = new PointF(image.Width - textSz.Width - 5, image.Height - textSz.Height);
                     RectangleF rect = new RectangleF(0, 0, image.Width, image.Height);
                     System.Drawing.StringFormat fs = new System.Drawing.StringFormat();
                     fs.Alignment = StringAlignment.Far;
                     fs.LineAlignment = StringAlignment.Far;

                     if (!cbNotDrawText.Checked) 
                     {
                        g.DrawString(text, font, drawBrush, rect, fs);

                        if (v.remark.Trim().Length > 0)
                        {
                           SizeF sf = g.MeasureString(v.remark, font, image.Width);
                           RectangleF rsf = new RectangleF(0, 0, image.Width, (int)sf.Height + 15);

                           Image foot = new Bitmap(image.Width, (int)rsf.Height);

                           fs = new System.Drawing.StringFormat();
                           fs.Alignment = StringAlignment.Near;
                           fs.LineAlignment = StringAlignment.Center;
                           Graphics gf = Graphics.FromImage(foot);
                           gf.FillRectangle(new SolidBrush(Color.White), new Rectangle(0, 0, foot.Width, foot.Height));
                           gf.DrawString(v.remark, font, drawBrush, rsf, fs);

                           Image result = new Bitmap(image.Width, image.Height + foot.Height);
                           using (Graphics g1 = Graphics.FromImage(result))
                           {
                              g1.DrawImage(image, new Point());
                              g1.DrawImage(foot, new Point(0, image.Height));
                           }

                           image = result;
                        }
                     }

                     try
                     {
                        StringBuilder dir = new StringBuilder(parent);

                        if (cbLevel1.Checked)
                        {
                           dir.Append("\\");
                           dir.Append(getValue(cbLevel1Value.SelectedIndex, v));
                        }

                        if (cbLevel2.Checked)
                        {
                           dir.Append("\\");
                           dir.Append(getValue(cbLevel2Value.SelectedIndex, v));
                        }

                        if (cbLevel3.Checked)
                        {
                           dir.Append("\\");
                           dir.Append(getValue(cbLevel3Value.SelectedIndex, v));
                        }

                        if (cbLevel4.Checked)
                        {
                           dir.Append("\\");
                           dir.Append(getValue(cbLevel4Value.SelectedIndex, v));
                        }

                        if (!Directory.Exists(dir.ToString()))
                           Directory.CreateDirectory(dir.ToString());

                        string cname = scriptStep.Replace(' ', '_');
                        cname += WinChar(v.contract.Name);

                        string curDir = Directory.GetCurrentDirectory();
                        Directory.SetCurrentDirectory(dir.ToString());

                        string file = string.Format(saveName, cname, 
                           WinChar(v.OrgName), WinChar(v.OrgAddr), WinChar(v.Created.ToString("dd.MM.yyyy")), cnt_v, cnt);

                        if(phText.Length > 0)
                        {
                           int l = file.Length + phText.Length;
                           if(l > 250)
                           {
                              file += phText.Substring(0, 250 - file.Length);
                           }
                           else
                           {
                              file += phText;
                           }
                        }
                        file += ".jpg";
                        image.Save(file);

                        Directory.SetCurrentDirectory(curDir);
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

      private string getValue(int idx, Visit v)
      {
         string result = string.Empty;

         switch (idx)
         {
            case 0:
               result = WinChar(v.contract.Name);
               break;
            case 1:
               result = v.AgentName;
               break;
            case 2:
               result = v.org.slsnet.name;
               break;
            case 3:
               result = WinChar(v.org.Name);
               break;
         }

         //string slsNet = (v.org == null || v.org.slsnet == null) ? "" : "\\" + v.org.slsnet.name;
         //string dir = parent + "\\" + v.contract.Name + "\\" +  v.AgentName + slsNet;

         return result;
      }

      private string WinChar(string input)
      {
         string result = input;
         result = result.Replace('\\', '_').Replace('/', '_').Replace(':','_').Replace('*','_').Replace('?','_')
            .Replace('"','_').Replace('<','_').Replace('>','_').Replace('|','_');

         return result;
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
         setting.lvl_1 = cbLevel1.Checked;

         if (cbLevel1Value.SelectedItem != null)
            setting.lvl1val = cbLevel1Value.SelectedItem.ToString();

         setting.lvl_2 = cbLevel2.Checked;

         if (cbLevel2Value.SelectedItem != null)
            setting.lvl2val = cbLevel2Value.SelectedItem.ToString();

         setting.lvl_3 = cbLevel3.Checked;

         if (cbLevel3Value.SelectedItem != null)
            setting.lvl3val = cbLevel3Value.SelectedItem.ToString();

         setting.lvl_4 = cbLevel3.Checked;

         if (cbLevel4Value.SelectedItem != null)
            setting.lvl4val = cbLevel4Value.SelectedItem.ToString();

         setting.Save();
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

         if (cbAgent.Items.Count > 0)
            cbAgent.SelectedIndex = 0;

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

      private String AddWhere()
      {
         StringBuilder result = new StringBuilder();

         if (rbOrg.Checked && tbOrg.Tag != null)
         {
            Org o = tbOrg.Tag as Org;

            if (o != null)
               result.Append("\"id\" = '").Append(o.id).Append("'");
         }
         else if (rbAgent.Checked && cbAgent.SelectedItem != null)
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
         else if (rbContract.Checked && tbContract.Tag != null)
         {
            ContractDef c = tbContract.Tag as ContractDef;

            if (c != null)
               result.Append("\"def\" = '").Append(c.id).Append("'");
         }

         return result.ToString();
      }

      private void rbOrg_CheckedChanged(object sender, EventArgs e)
      {
         if (sender is RadioButton)
         {
            bool v = (sender as RadioButton).Checked;
            tbOrg.Enabled = v;
            btnSelOrg.Enabled = v;
         }
      }

      private void btnSelOrg_Click(object sender, EventArgs e)
      {
         FmOrg fmorg = new FmOrg();
         fmorg.grid.RowEnter += grid_RowEnter;
         fmorg.ShowDialog();
      }

      void grid_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         Org o = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as Org;

         if(o != null)
         {
            tbOrg.Text = o.Name;
            tbOrg.Tag = o;
         }
      }

      private void btnContract_Click(object sender, EventArgs e)
      {
         FmContract form = new FmContract();
         form.grid.RowEnter += grid_RowEnterContract;
         form.ShowDialog();
      }

      private void grid_RowEnterContract(object sender, DataGridViewCellEventArgs e)
      {
         ContractDef c = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as ContractDef;

         if (c != null)
         {
            tbContract.Text = c.Name;
            tbContract.Tag = c;
         }
      }
   }

   [Serializable]
   class SettingFmExportPhotoNBTL : BaseFormSetting<SettingFmExportPhotoNBTL>
   {
      public string path = string.Empty;
      public bool lvl_1 = true;
      public string lvl1val = FmExportPhotoNBTL.CONTRACT;
      public bool lvl_2 = true;
      public string lvl2val = FmExportPhotoNBTL.SLS_NAME;
      public bool lvl_3 = true;
      public string lvl3val = FmExportPhotoNBTL.AGENT_NAME;
      public bool lvl_4 = true;
      public string lvl4val = FmExportPhotoNBTL.ORG_NAME;

   }
}
