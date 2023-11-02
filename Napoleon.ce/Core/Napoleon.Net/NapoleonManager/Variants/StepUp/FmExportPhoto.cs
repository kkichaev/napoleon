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
   public partial class FmExportPhoto : Form
   {
      protected DataSet<int, Visit> dsVisit;
      private DataSet<string, Org> dsOrg;
      private SettingFmExportPhoto setting;

      DataSet<int, ScriptDef> scriptDef = new DataSet<int, ScriptDef>(ScriptDef.OBJECT_NAME, false);
      SimpleDataSet<ScriptDoc> docs = new SimpleDataSet<ScriptDoc>(ScriptDoc.OBJECT_NAME, false);

      private DataSet<string, ContractDef> dsContract;

      public FmExportPhoto()
      {
         InitializeComponent();
         dpv.Start = DateTime.Now;
         dpv.Finish = DateTime.Now;
         dsVisit = (DataSet<int, Visit>)DataModule.Get(Visit.OBJECT_NAME) ?? new DataSet<int, Visit>(Visit.OBJECT_NAME);
         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.COMMON_OBJECT_NAME) ?? new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);

         dsContract = (DataSet<string, ContractDef>)DataModule.Get(ContractDef.OBJECT_NAME) ?? new DataSet<string, ContractDef>(ContractDef.OBJECT_NAME);

         setting = BaseFormSetting<SettingFmExportPhoto>.Load();
         tbPath.Text = setting.path;

         dgvContracts.AutoGenerateColumns = false;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         LoadContracts();
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

         dgvContracts.CommitEdit(DataGridViewDataErrorContexts.Commit);

         const string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy} 23:59:59')";
         dsVisit.Filter = String.Format(COMMON_FILTER_STR, "date", dpv.Start.Date, dpv.Finish.Date);

         String agentWhere = AgentWhere();

         if (agentWhere.Length > 0)
            dsVisit.Filter += " and " + agentWhere;

         List<IDataSet> upd = new List<IDataSet>();

         if (dsOrg.Count == 0)
         {
            //dsOrg.Filter = "not \"id\" is null";
            upd.Add(dsOrg);
         }


         upd.Add(dsVisit);
         if (scriptDef.Count == 0)
         {
            scriptDef.Filter = "\"userid\" = '' or \"userid\" is null";
            upd.Add(scriptDef);
         }

         docs.Filter = String.Format(COMMON_FILTER_STR, "created", dpv.Start.Date, dpv.Finish.Date);
         upd.Add(docs);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         List<ContractDef> selContracts = new List<ContractDef>();

         foreach(DataItem di in (System.Collections.IList)dgvContracts.DataSource)
         {
            if (di.Checked)
               selContracts.Add(di.ContractDef);
         }

         List<Visit> list = new List<Visit>();
         list.AddRange(dsVisit.Values);
         list.Sort((lhs, rhs) => lhs.AgentName.CompareTo(rhs.AgentName));
         string parent = tbPath.Text.Trim();
//         const string saveName = @"{0}\{1}_{2}({3}).jpg";
         const string saveName = @"{0}\{1}_{2}_{3}({4}).jpg";
         Font font = new System.Drawing.Font("Arial", 15.75F, ((System.Drawing.FontStyle)((System.Drawing.FontStyle.Bold | System.Drawing.FontStyle.Italic))), System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         SolidBrush drawBrush = new SolidBrush(Color.Red);

         foreach (Visit v in list)
         {
            ContractDef cdef = FindContract(v, selContracts);
            if (cdef == null)
               continue;

            if(v.items != null && v.items.Count > 0)
            {
               int cnt = 1;
               foreach (Visit.VisitItem item in v.items)
               {
                  using (MemoryStream stream = new MemoryStream(item.id))
                  {
                     Image image = new Bitmap(stream);

                     //Graphics g = Graphics.FromImage(image);
                     //string text = v.created.ToString("dd/MM/yyyy hh:mm");
                     //SizeF textSz = g.MeasureString(text, font);
                     //PointF drawPoint = new PointF(image.Width - textSz.Width - 5, image.Height - textSz.Height);
                     //g.DrawString(text, font, drawBrush, drawPoint);

                     try
                     {
                        string dir = parent + "\\" + v.AgentName + "\\" + cdef.Name;

                        if (!Directory.Exists(dir))
                           Directory.CreateDirectory(dir);

                        //string file = string.Format(saveName, dir, WinChar(v.OrgName), WinChar(v.OrgAddr), cnt);
                        string file = string.Format(saveName, dir, WinChar(v.OrgName), WinChar(v.OrgAddr), WinChar(v.Created.ToString()), cnt);
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

         if (MessageBox.Show(
               "Выгрузка завершена, открыть папку в проводнике Windows", "Вопрос", MessageBoxButtons.OKCancel, 
               MessageBoxIcon.Question) == DialogResult.OK)
         {
            Process.Start("explorer.exe", tbPath.Text);
            Close();
         }
      }

      ContractDef IsContractSelected(ScriptDoc sdoc, List<ContractDef> selContracts)
      {
         if (scriptDef.ContainsKey(sdoc.scriptId) == false)
            return null;

         ScriptDef doc = scriptDef[sdoc.scriptId];
         ContractDef ret = null;
         foreach(ScriptDefItem sdi in doc.items)
         {
            if( sdi.curType == "Contract" )
            {
               foreach(ContractDef cdef in selContracts)
               {
                  if(cdef.id == sdi.condParam)
                  {
                     ret = cdef;
                     return ret;
                  }
               }
            }
         }
         return ret;
      }

      private ContractDef FindContract(Visit v, List<ContractDef> selContracts)
      {
         ContractDef ret = null;

         foreach(ScriptDoc cdoc in docs.Data)
         {
            ContractDef cdef = IsContractSelected(cdoc, selContracts);
            if( cdef == null )
               continue;

            foreach(ScriptDocItem sdi in cdoc.items)
            {
               if( sdi.type == "Visit" && sdi.date == v.created)
               {
                  ret = cdef;
                  return ret;
               }
            }

         }

         return ret;
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

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         LoadContracts();
      }

      private void LoadContracts()
      {
         const string CONTRACT_FILTER = "\"start\" <= ToDate('{0:dd/MM/yyyy}') and finish >= ToDate('{1:dd/MM/yyyy}')";
         dsContract.Filter = string.Format(CONTRACT_FILTER, dpv.Finish.AddDays(1), dpv.Start);

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsContract);

         FmWait.StdDataRefresh(this, upd, RefreshContracts);
      }

      void RefreshContracts()
      {
         List<DataItem> src = new List<DataItem>();
         foreach (ContractDef cd in dsContract.Data)
            src.Add(new DataItem(cd));

         dgvContracts.DataSource = src;
      }

      class DataItem
      {
         ContractDef c;
         public DataItem(ContractDef c)
         {
            this.c = c;
            Checked = false;
         }

         public bool Checked { get; set; }
         public string Contract { get { return c.ToString(); } }

         public ContractDef ContractDef { get { return c; } }
      }
   }


   [Serializable]
   class SettingFmExportPhoto : BaseFormSetting<SettingFmExportPhoto>
   {
      public string path = string.Empty;
   }
}
