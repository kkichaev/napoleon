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
   public partial class FmSPKExportPhoto : Form
   {
      protected DataSet<int, Visit> dsVisit;
      protected DataSet<int, VisitInfo> dsVisitInfo;
      SimpleDataSet<ScriptDef> dsScriptDef;
      SimpleDataSet<ScriptDoc> dsScripts;

      private DataSet<string, Org> dsOrg;
      private SettingFmSPKExportPhoto setting;
      private List<Org> orgsSelected = new List<Org>();
      private List<Division> divsSelected = new List<Division>();
      private List<Agent> agentSelected = new List<Agent>();
      private Dictionary<String, int> docHash = new Dictionary<string, int>();

      Dictionary<int, ScriptDef> scriptDefs = new Dictionary<int, ScriptDef>();

      public FmSPKExportPhoto()
      {
         InitializeComponent();
         dpv.Start = DateTime.Now;
         dpv.Finish = DateTime.Now;
         dsVisit = (DataSet<int, Visit>)DataModule.Get(Visit.OBJECT_NAME) ?? new DataSet<int, Visit>(Visit.OBJECT_NAME);
         dsOrg = new DataSet<string, Org>(Org.OBJECT_NAME, false);
         dsVisitInfo = (DataSet<int, VisitInfo>)DataModule.Get(VisitInfo.V_OBJECT_NAME) ?? new DataSet<int, VisitInfo>(VisitInfo.V_OBJECT_NAME);

         dsScriptDef = new SimpleDataSet<ScriptDef>(ScriptDef.OBJECT_NAME, false);
         dsScriptDef.Filter = "\"userid\" is null or not \"userid\" is null";

         dsScripts = new SimpleDataSet<ScriptDoc>(ScriptDoc.OBJECT_NAME, false);

         setting = BaseFormSetting<SettingFmSPKExportPhoto>.Load();
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

            DateTime finish = dpv.Finish.AddDays(1);
            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(dsScripts);
            FmWait.StdDataRefresh(this, upd, DoLoadDataW);
         }
      }

      private void DoLoadDataLow()
      {
         foreach (ScriptDef sd in dsScriptDef.Data)
            scriptDefs[sd.id] = sd;

         int c = 1;
         int sz = dsVisitInfo.Count;

         const string LABEL_TEXT = "Обработно запросов: {0} из {1}";

         foreach (VisitInfo i in dsVisitInfo.Values)
         {
            dsVisit.Filter = string.Format("\"created\" = ToDate('{0:dd/MM/yyyy HH:mm:ss}') and \"userid\"='{1}'", i.created, i.userid);
            dsOrg.Filter = string.Format("\"id\"='{0}'", i.id);

            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(dsOrg);
            upd.Add(dsVisit);
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), upd, null).Join();

            lblInfo.Text = string.Format(LABEL_TEXT, c, sz);

            c++;
            DoLoadData();
         }

         FmWait.CloseForm();

         if (MessageBox.Show(
               "Выгрузка завершена, открыть папку в проводнике Windows", "Вопрос", MessageBoxButtons.OKCancel,
               MessageBoxIcon.Question) == DialogResult.OK)
         {
            Process.Start("explorer.exe", tbPath.Text);
         }
      }

      private void DoLoadDataW()
      {
         StringBuilder where = new StringBuilder();

         foreach (ScriptDoc s in dsScripts.Data)
         {
            for (int i = 0; i < s.items.Count; i++)
            {
               if (scriptItems.CheckedIndices.Contains(i))
               {
                  if (where.Length > 0)
                     where.Append(" or ");

                  where.Append(string.Format("created = ToDate('{0:dd/MM/yyyy hh:mm:ss}')", s.items[i].date));
               }
            }
         }

         if (where.Length > 0)
         {
            List<IDataSet> upd = new List<IDataSet>();
            dsVisitInfo.Filter = "(" + where.ToString() + ")";

            String agentWhere = AddWhere();

            if (agentWhere.Length > 0)
               dsVisitInfo.Filter += " and " + "(" + agentWhere + ")";

            upd.Add(dsVisitInfo);
            FmWait.StdDataRefresh(this, upd, DoLoadDataLow);
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
            dsVisitInfo.Filter += " and " + agentWhere;

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsVisitInfo);

         upd.Add(dsScripts);
         upd.Add(dsScriptDef);

         FmWait.StdDataRefresh(this, upd, DoLoadDataLow);
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

      private void DoLoadData()
      {
         foreach (ScriptDef sd in dsScriptDef.Data)
            scriptDefs[sd.id] = sd;

         List<Visit> list = new List<Visit>();
         list.AddRange(dsVisit.Values);
         list.Sort((lhs, rhs) => lhs.AgentName.CompareTo(rhs.AgentName));
         string parent = tbPath.Text.Trim();
         const string saveName = @"{0}\{1} {2} {3} {4}.jpg";
         Font font = new System.Drawing.Font("Arial", 30.75F, ((System.Drawing.FontStyle)((System.Drawing.FontStyle.Bold | System.Drawing.FontStyle.Italic))), System.Drawing.GraphicsUnit.Point, ((byte)(204)));
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

                     try
                     {
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

                        string file = string.Format(saveName, dir.ToString(), WinChar(v.OrgName), WinChar(v.Created.ToString("dd.MM.yyyy")), 
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

      private string WinChar(string input)
      {
         string result = input.Trim();
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
         setting.lvl_2 = cbLevel2.Checked;
         setting.lvl_3 = cbLevel3.Checked;
         setting.lvl_4 = cbLevel3.Checked;
         setting.Save();
      }

      private void FmExportPhoto_Load(object sender, EventArgs e)
      {
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

      private String AddWhere()
      {
         StringBuilder res = new StringBuilder();
         StringBuilder sb = new StringBuilder();
         CollectSelectedUser(sb);
         
         if (sb.Length > 0)
            res.Append(string.Format("\"userid\" in ({0})", sb.ToString()));

         sb.Length = 0;

         if (cbOrgs.Checked && orgsSelected != null)
         {
            foreach (Org o in orgsSelected)
            {
               if (sb.Length > 0 && o != null)
                  sb.Append(",");

               if (o != null)
                  sb.Append("'").Append(o.id).Append("'");
            }

            if (sb.Length > 0)
            {
               if (res.Length > 0)
                  res.Append(" and ");

               res.Append(String.Format("\"id\" in ({0})", sb.ToString()));
            }
         }

         return res.ToString();
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
         StringBuilder sb = new StringBuilder();
         string where = string.Empty;

         CollectSelectedUser(sb);

         if (sb.Length > 0)
            where = string.Format("\"userid\" in ({0})", sb.ToString());
         else
            where = "\"userid\" is null or \"userid\" is not null"; 

         dsOrg.Filter = where;

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsOrg);

         FmWait.StdDataRefresh(this, upd, DoOrgLoad);
      }

private void CollectSelectedUser(StringBuilder sb)
{
         if (cbAgent.Checked && agentSelected != null)
            foreach (Agent a in agentSelected)
               AddAgentID(sb, a);
         else if (cbDiv.Checked && divsSelected != null)
            foreach (Division d in divsSelected)
               foreach(Division.DivisionAgent a in d.agents)
                  AddAgentID(sb, a.agent);
}

      private static void AddAgentID(StringBuilder sb, Agent a)
      {
         if (sb.Length > 0)
            sb.Append(',');

         sb.Append(string.Format("'{0}'", a.id));
      }

      private void DoOrgLoad()
      {
         cbOrgs.Checked = FmSelectOrgs.DoSelect(new List<Org>(dsOrg.Values), orgsSelected) || cbOrgs.Checked;

         if (cbOrgs.Checked)
         {
            StringBuilder sb = new StringBuilder();

            foreach (Org o in orgsSelected)
            {
               if (sb.Length > 0)
                  sb.Append(',');
               sb.Append(o.Name);
            }

            tbOrg.Text = sb.ToString();
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
           ListViewItem v = scriptItems.Items.Add(i.name, GetDocIndex(i.curType));
           v.Checked = true;
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

      private void btnDiv_Click(object sender, EventArgs e)
      {
         cbDiv.Checked = FmSelectDivision.DoSelect(divsSelected) || cbDiv.Checked;

         if (cbDiv.Checked)
         {
            StringBuilder sb = new StringBuilder();

            foreach (Division d in divsSelected)
            {
               if (sb.Length > 0)
                  sb.Append(',');
               sb.Append(d.Name);
            }

            tbSelecDivs.Text = sb.ToString();
         }
      }

      private void btnAgent_Click(object sender, EventArgs e)
      {
         cbAgent.Checked = FmSelectAgent.DoSelect(divsSelected, agentSelected) || cbAgent.Checked;

         if (cbAgent.Checked)
         {
            StringBuilder sb = new StringBuilder();

            foreach (Agent a in agentSelected)
            {
               if (sb.Length > 0)
                  sb.Append(',');
               sb.Append(a.Name);
            }

            tbSelectAgent.Text = sb.ToString();
         }
      }
   }

   [Serializable]
   class SettingFmSPKExportPhoto : BaseFormSetting<SettingFmExportPhoto>
   {
      public string path = string.Empty;
      public bool lvl_1 = true;
      public bool lvl_2 = true;
      public bool lvl_3 = true;
      public bool lvl_4 = true;
   }
}
