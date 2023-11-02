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
   public partial class FmExportPhotoEx : Form
   {
      protected DataSet<int, Visit> dsVisit;
      protected DataSet<int, VisitInfo> dsVisitInfo;
      SimpleDataSet<ScriptDef> dsScriptDef;
      SimpleDataSet<ScriptDoc> dsScripts;

      private DataSet<string, Org> dsOrg;
      private SettingFmExportPhotoEx setting;
      private List<Org> orgsSelected;
      private Dictionary<String, int> docHash = new Dictionary<string, int>();

      Dictionary<int, ScriptDef> scriptDefs = new Dictionary<int, ScriptDef>();

      public FmExportPhotoEx()
      {
         InitializeComponent();
         dpv.Start = DateTime.Now;
         dpv.Finish = DateTime.Now;
         dsVisit = (DataSet<int, Visit>)DataModule.Get(Visit.OBJECT_NAME) ?? new DataSet<int, Visit>(Visit.OBJECT_NAME);
         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.COMMON_OBJECT_NAME) ?? new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);
         dsVisitInfo = (DataSet<int, VisitInfo>)DataModule.Get(VisitInfo.V_OBJECT_NAME) ?? new DataSet<int, VisitInfo>(VisitInfo.V_OBJECT_NAME);

         dsScriptDef = new SimpleDataSet<ScriptDef>(ScriptDef.OBJECT_NAME, false);

#if MorozkoSPBMonitor
         Manager mgr = CurrentUser.user as Manager;
         dsScriptDef.Filter = "\"suppl\" = '"  + mgr.src.suppl + "'";
         cbScript.Checked = true;
        cbScript.Enabled = false;
        //scripts.Enabled = false;

        //groupBox3.Visible = false;
#else
         dsScriptDef.Filter = "\"userid\" is null or not \"userid\" is null";
#endif

            dsScripts = new SimpleDataSet<ScriptDoc>(ScriptDoc.OBJECT_NAME, false);

         setting = BaseFormSetting<SettingFmExportPhotoEx>.Load();
         tbPath.Text = setting.path;

         try
         {
            cbLevel1.Checked = setting.lvl_1;
            cbLevel2.Checked = setting.lvl_2;
            cbLevel3.Checked = setting.lvl_3;
            cbLevel4.Checked = setting.lvl_4;
         }catch(Exception){

         }

         for(int i = 0; i < ScriptDocuments.Documents.Length; i++)
         {
            ScriptDocument sd = ScriptDocuments.Documents[i];
            imageList1.Images.Add(sd.icon);
            docHash[sd.type] = i;
         }

         lblInfo.Text = "";
         scriptItems.Enabled = false;
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

         if (cbScript.Checked)
            QueryForScript();
         else
            QueryAll();
      }

      private void QueryForScript()
      {
         ScriptDef def = scripts.SelectedItem as ScriptDef;

         if (def != null)
         {
            const string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy} 23:59:59')";
            dsScripts.Filter = String.Format(COMMON_FILTER_STR, "created", dpv.Start.Date, dpv.Finish.Date);
            dsScripts.Filter += string.Format(" and scriptId={0}", def.ID);

            String agentWhere = AddWhere();
            if (agentWhere.Length > 0)
               dsScripts.Filter += " and " + agentWhere;

            DateTime finish = dpv.Finish.AddDays(1);
            List<IDataSet> upd = new List<IDataSet>();
            if (dsOrg.Count == 0)
               upd.Add(dsOrg);
            upd.Add(dsScripts);
            FmWait.StdDataRefresh(this, upd, LoadVisitsFromScripts);
         }
      }

      private void LoadVisits()
      {
         foreach (ScriptDef sd in dsScriptDef.Data)
            scriptDefs[sd.id] = sd;

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

            c++;
            MakePhotos();
         }

         FmWait.CloseForm();

         if (MessageBox.Show(
               "Выгрузка завершена, открыть папку в проводнике Windows", "Вопрос", MessageBoxButtons.OKCancel,
               MessageBoxIcon.Question) == DialogResult.OK)
         {
            Process.Start("explorer.exe", tbPath.Text);
            //Close();
         }
      }

      private void LoadVisitsFromScripts()
      {
         StringBuilder where = new StringBuilder();

         Dictionary<string, string> agentVisits = new Dictionary<string, string>();

         foreach (ScriptDoc s in dsScripts.Data)
         {
            string whereDoc = "";
            for (int i = 0; i < s.items.Count; i++)
            {
#if MorozkoSPBMonitor
#else
               if (scriptItems.CheckedIndices.Contains(i))
#endif
               {
                  if (whereDoc.Length > 0)
                     whereDoc += " or ";

                  whereDoc += string.Format("created=ToDate('{0:dd/MM/yyyy HH:mm:ss}')", s.items[i].date);
               }
            }
            if(whereDoc.Length > 0)
            {
               string aw;
               if(!agentVisits.TryGetValue(s.AgentID, out aw))
               {
                  aw = "";
               }
               if (aw.Length > 0)
                  aw += " or ";
               aw += whereDoc;
               agentVisits[s.AgentID] = aw;
            }
         }

         foreach(KeyValuePair<string,string> kv in agentVisits )
         {
            if (where.Length > 0)
               where.Append(" or ");
            where.Append("(userid='" + kv.Key + "' and (" + kv.Value + "))");
         }

         if (where.Length > 0)
         {
            List<IDataSet> upd = new List<IDataSet>();
            dsVisitInfo.Filter = where.ToString();

            //dsVisitInfo.Filter = "(" + where.ToString() + ")";
            //String agentWhere = AddWhere();
            //if (agentWhere.Length > 0)
            //   dsVisitInfo.Filter += " and " + "(" + agentWhere + ")";

            upd.Add(dsVisitInfo);
            FmWait.StdDataRefresh(this, upd, LoadVisits);
         }
      }

      private void QueryAll()
      {
         const string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy} 23:59:59')";
         dsVisitInfo.Filter = String.Format(COMMON_FILTER_STR, "date", dpv.Start.Date, dpv.Finish.Date);
         dsScripts.Filter = String.Format(COMMON_FILTER_STR, "created", dpv.Start.Date, dpv.Finish.Date);

         DateTime finish = dpv.Finish.AddDays(1);

         String agentWhere = AddWhere();

         if (agentWhere.Length > 0)
         {
            dsVisitInfo.Filter += " and " + agentWhere;
            dsScripts.Filter += " and " + agentWhere;
         }

         List<IDataSet> upd = new List<IDataSet>();
         if (dsOrg.Count == 0)
            upd.Add(dsOrg);
         upd.Add(dsVisitInfo);

         upd.Add(dsScripts);
         upd.Add(dsScriptDef);

         FmWait.StdDataRefresh(this, upd, LoadVisits);
      }

      string GetScriptStep(Visit v)
      {
         string ret = "";

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
                        ret = def.items[cnt].Name;
                  }
                  break;
               }
               cnt++;
            }
         }

         return ret;
      }

      private void MakePhotos()
      {
         foreach (ScriptDef sd in dsScriptDef.Data)
            scriptDefs[sd.id] = sd;

         List<Visit> list = new List<Visit>();
         list.AddRange(dsVisit.Values);
         list.Sort((lhs, rhs) => lhs.AgentName.CompareTo(rhs.AgentName));
         string parent = tbPath.Text.Trim();
         const string saveName = @"{0}\{1}{2}{3}{4}{5}.jpg";
         Font font = new System.Drawing.Font("Arial", 15.75F, ((System.Drawing.FontStyle)((System.Drawing.FontStyle.Bold | System.Drawing.FontStyle.Italic))), System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         SolidBrush drawBrush = new SolidBrush(Color.Red);

         foreach (Visit v in list)
         {
            string scriptStep = GetScriptStep(v);
            if (scriptStep.Length > 0)
               scriptStep += ' ';
            if (v.items != null && v.items.Count > 0)
            {
               int cnt = 1;
               foreach (Visit.VisitItem item in v.items)
               {
                  if (item.id == null)
                     continue;

                  using (MemoryStream stream = new MemoryStream(item.id))
                  {
                     try
                     {
                         Image image = new Bitmap(stream);

                         // Не удаляйте этот код, если не нужет будет 
                         // просто закоментировать!
                         Graphics g = Graphics.FromImage(image);

                         string text = scriptStep + v.OrgName + "," + v.OrgAddr + ", " + v.created.ToString("dd/MM/yyyy HH:mm");
                         SizeF textSz = g.MeasureString(text, font);
                         //PointF drawPoint = new PointF(image.Width - textSz.Width - 5, image.Height - textSz.Height);
                         RectangleF rect = new RectangleF(0, 0, image.Width, image.Height);
                         System.Drawing.StringFormat fs = new System.Drawing.StringFormat();
                         fs.Alignment = StringAlignment.Far;
                         fs.LineAlignment = StringAlignment.Far;

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

                        StringBuilder dir = new StringBuilder(parent);

                        if (cbLevel1.Checked)
                        {
                           dir.Append("\\");
                           dir.Append(getValue(0, v));
                        }

                        if (cbLevel2.Checked)
                        {
                           dir.Append("\\");
                           dir.Append(getValue(1, v));
                        }

                        if (cbLevel3.Checked)
                        {
                           dir.Append("\\");
                           dir.Append(getValue(2, v));
                        }

                        if (cbLevel4.Checked)
                        {
                           dir.Append("\\");
                           dir.Append(WinChar(scriptStep));
                        }

                        if (!Directory.Exists(dir.ToString()))
                           Directory.CreateDirectory(dir.ToString());

                        //string file = string.Format(saveName, dir.ToString(), WinChar(v.OrgName), WinChar(v.OrgAddr), WinChar(v.Created.ToString("dd.MM.yyyy")), 
                        //   WinChar(scriptStep), cnt);
                        string file = string.Format(saveName, dir.ToString(), WinChar(v.OrgName), "", WinChar(v.Created.ToString() + " "), 
                           WinChar(scriptStep), cnt);

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

      private string getValue(int idx, Visit v)
      {
         string result = string.Empty;

         switch (idx)
         {
            case 0:
               result = WinChar(GetAgentDivision(v.agent));
               break;
            case 1:
               result = v.AgentName;
               break;
            case 2:
               result = WinChar(v.OrgName);
               break;
         }

         return result;
      }

      private string GetVisitStep(Visit v)
      {
         throw new NotImplementedException();
      }

      private string GetAgentDivision(Agent a)
      {
         Manager m = CurrentUser.user as Manager;
         Division d = m.GetAgentDivision(a);

         return d.Name ?? string.Empty;
      }

      protected string WinChar(string input)
      {
         string result = input.Trim();
         result = result.Replace('\\', '_').Replace('/', '_').Replace(':', '_').Replace('*', '_').Replace('?', '_')
            .Replace('"', '_').Replace('<', '_').Replace('>', '_').Replace('|', '_').Replace('\t', '_').Replace('\n', '_');

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
         setting.lvl_1 = cbLevel1.Checked;
         setting.lvl_2 = cbLevel2.Checked;
         setting.lvl_3 = cbLevel3.Checked;
         setting.lvl_4 = cbLevel3.Checked;
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

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsScriptDef);
         FmWait.StdDataRefresh(this, upd, DoLoadStartData, null);
      }

      private void DoLoadStartData()
      {
         List<ScriptDef> list = new List<ScriptDef>();
         list.AddRange(dsScriptDef.Values);
         list.Sort((x, y) => { return x.Name.CompareTo(y.Name); });
         list.ForEach((x) => { scripts.Items.Add(x); });

         if (scripts.Items.Count > 0)
            scripts.SelectedIndex = 0;
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

         if (rbOrg.Checked && orgsSelected != null && orgsSelected.Count > 0)
         {
            string orgIn = "";
            foreach (Org o in orgsSelected)
            {
               orgIn += "'" + o.id + "',";
               //if (result.Length > 0 && o != null)
               //   result.Append(" or ");

               //if (o != null)
               //   result.Append("\"id\" = '").Append(o.id).Append("'");
            }
            result.Append("\"id\" in (").Append(orgIn.Substring(0, orgIn.Length - 1)).Append(")");
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
         FmSelectOrgs fmorg = new FmSelectOrgs(true);
         if (fmorg.ShowDialog() == DialogResult.OK)
         {
            orgsSelected = fmorg.GetSelected();
            tbOrg.Text = fmorg.SearchText;
            rbOrg.Checked = true;
         }
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

      private void scripts_SelectedIndexChanged(object sender, EventArgs e)
      {
         scriptItems.Items.Clear();

         ScriptDef def = ((ComboBox)sender).SelectedItem as ScriptDef;

         foreach (ScriptDefItem i in def.items)
         {
           scriptItems.Items.Add(i.name, GetDocIndex(i.curType));
         }
      }

      private int GetDocIndex(string type)
      {
         int res = 0;

         if (docHash.ContainsKey(type))
            res = docHash[type];

         return res;
      }

      private void cbScript_CheckedChanged(object sender, EventArgs e)
      {
         scriptItems.Enabled = ((CheckBox)sender).Checked;
      }
   }

   [Serializable]
   class SettingFmExportPhotoEx : BaseFormSetting<SettingFmExportPhotoEx>
   {
      public string path = string.Empty;
      public bool lvl_1 = true;
      public bool lvl_2 = true;
      public bool lvl_3 = true;
      public bool lvl_4 = true;
   }
}
