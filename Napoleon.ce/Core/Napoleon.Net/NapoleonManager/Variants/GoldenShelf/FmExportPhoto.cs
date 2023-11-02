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
      private DataSet<string, ContractDef> dsContract;
      protected DataSet<int, Visit> dsVisit;
      private DataSet<string, Org> dsOrg;
      private SettingFmExportPhoto setting;

      public FmExportPhoto()
      {
         InitializeComponent();
         dpv.Start = DateTime.Now;
         dpv.Finish = DateTime.Now;
         dsVisit = (DataSet<int, Visit>)DataModule.Get(Visit.OBJECT_NAME) ?? new DataSet<int, Visit>(Visit.OBJECT_NAME);
         dsOrg = (DataSet<string, Org>) DataModule.Get(Org.OBJECT_NAME) ?? new DataSet<string, Org>(Org.OBJECT_NAME);
         dsContract = (DataSet<string, ContractDef>)DataModule.Get(ContractDef.OBJECT_NAME) ?? new DataSet<string, ContractDef>(ContractDef.OBJECT_NAME);

         setting = BaseFormSetting<SettingFmExportPhoto>.Load();
         tbPath.Text = setting.path;
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

         const string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy} 23:59:59')";
         dsVisit.Filter = String.Format(COMMON_FILTER_STR, "date", dpv.Start.Date, dpv.Finish.Date);
         const string PERIOD_FILTER_STR = "\"start\" < ToDate('{1:dd/MM/yyyy}') and \"finish\" >= ToDate('{0:dd/MM/yyyy}')";
         DateTime finish = dpv.Finish.AddDays(1);
         dsContract.Filter = string.Format(PERIOD_FILTER_STR, dpv.Start, finish);
         dsOrg.Filter = "\"id\" is null or \"id\" is not null";

         String agentWhere = AgentWhere();

         if (agentWhere.Length > 0)
            dsVisit.Filter += " and " + agentWhere;

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsOrg);
         upd.Add(dsContract);
         upd.Add(dsVisit);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         List<Visit> list = new List<Visit>();
         list.AddRange(dsVisit.Values);
         list.Sort((lhs, rhs) => lhs.AgentName.CompareTo(rhs.AgentName));
         string parent = tbPath.Text.Trim();
         const string saveName = @"{0}\{1}_{2}_{3}_{4}({5}).jpg";
         Font font = new System.Drawing.Font("Arial", 15.75F, ((System.Drawing.FontStyle)((System.Drawing.FontStyle.Bold | System.Drawing.FontStyle.Italic))), System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         SolidBrush drawBrush = new SolidBrush(Color.Red);

         foreach (Visit v in list)
         {
            if(v.items != null && v.items.Count > 0)
            {
               int cnt = 1;
               foreach (Visit.VisitItem item in v.items)
               {
                  using (MemoryStream stream = new MemoryStream(item.id))
                  {
                     Image image = new Bitmap(stream);

                     Graphics g = Graphics.FromImage(image);
                     string text = v.OrgName + "," +  v.OrgAddr + ", " + v.created.ToString("dd/MM/yyyy HH:mm");
                     SizeF textSz = g.MeasureString(text, font);
                     //PointF drawPoint = new PointF(image.Width - textSz.Width - 5, image.Height - textSz.Height);
                     RectangleF rect = new RectangleF(0, 0, image.Width, image.Height);
                     System.Drawing.StringFormat fs = new System.Drawing.StringFormat();
                     fs.Alignment = StringAlignment.Far;
                     fs.LineAlignment = StringAlignment.Far;

                     g.DrawString(text, font, drawBrush, rect, fs);

                     try
                     {
                        string dir = parent + "\\" + v.AgentName;

                        if (!Directory.Exists(dir))
                           Directory.CreateDirectory(dir);

                        string file = string.Format(saveName, dir, WinChar(v.contract.Name), 
                           WinChar(v.OrgName), WinChar(v.OrgAddr), WinChar(v.Created.ToString()), cnt);

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
   }

   [Serializable]
   class SettingFmExportPhoto : BaseFormSetting<SettingFmExportPhoto>
   {
      public string path = string.Empty;
   }
}
