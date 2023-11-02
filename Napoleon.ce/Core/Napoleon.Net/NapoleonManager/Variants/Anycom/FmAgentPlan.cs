using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using GRSoft.UILib;
using ExcelLibrary;

namespace GRSoft.NapoleonManager
{
   public partial class FmAgentPlan : Form
   {
      private DataSet<string, ManagerFolder> dsFolder = new DataSet<string,ManagerFolder>(ManagerFolder.OBJECT_NAME, false);
      private SimpleDataSet<AgentPlan> dsAgentPlan = new SimpleDataSet<AgentPlan>(AgentPlan.OBJECT_NAME, false);

      AgentPlan currentPlan = null;

      public FmAgentPlan()
      {
         InitializeComponent();

#if DEBUG
         dtpDate.MinDate = MonthStart(DateTime.Now.AddMonths(-4));
#else
         dtpDate.MinDate = MonthStart(DateTime.Now.AddMonths(-1));
#endif
         dtpDate.MaxDate = MonthStart(DateTime.Now.AddMonths(12));
         dtpDate.Value = MonthStart(DateTime.Now);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      public static DateTime MonthStart(DateTime date)
      {
         return new DateTime(date.Year, date.Month, 1);
      }

      void RefreshData()
      {
         Manager m = CurrentUser.user as Manager;
         if (m == null)
         {
            if (!MainForm.Instance.CheckIsMainDataPresents(true))
               return;
         }

         List<IDataSet> upd = new List<IDataSet>();

         dsAgentPlan.Filter = string.Format("[begin]=ToDate('{0:dd/MM/yyyy}')", MonthStart(dtpDate.Value));

         upd.Add(dsAgentPlan);
         if(dsFolder.Count == 0)
         {
            upd.Add(dsFolder);
         }

         FmWait.StdDataRefresh(this, upd, LoadData);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         if (CheckChanges())
            RefreshData();
      }

      void LoadData()
      {
         Manager m = CurrentUser.user as Manager;
         if (m == null)
            return;

         if (cbDivision.Items.Count == 0)
         {
            foreach (Division d in m.AllDivisions)
            {
               cbDivision.Items.Add(d);
            }
            if (cbDivision.Items.Count > 0)
               cbDivision.SelectedIndex = 0;
         }

         Agent a = (Agent)cbAgents.SelectedItem;
         if(a != null)
         {
            LoadPlan(a);
         }
      }

      private void fillGridRecursive(TreeNode node, TreeGridNodeCollection parent)
      {
         TreeGridNode result = null;

         if (node.Tag is ManagerFolder)
         {
            ManagerFolder folder = node.Tag as ManagerFolder;
            AgentPlan.Item item = new AgentPlan.Item();
            item.id = folder.id;
            item.folder = folder;               

            result = parent.AddDataItem(item);

            foreach (TreeNode n in node.Nodes)
               fillGridRecursive(n, result.Nodes);
         }

         if (null != result)
            result.Tag = node.Tag;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         btnSave.Enabled = !SaveChanges(true);
      }


      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
      }

      bool CheckChanges()
      {
         if (!btnSave.Enabled)
            return true;

         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         if (dr == DialogResult.No)
            return true;
         if (dr == DialogResult.Cancel)
            return false;

         return SaveChanges(false);
      }

      private void CommitGridChages()
      {
         tgvPrice.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }

      private bool SaveChanges(bool showDialog)
      {
         CommitGridChages();

         SimpleDataSet<AgentPlan> wrset = new SimpleDataSet<AgentPlan>(AgentPlan.OBJECT_NAME, false);

         foreach (AgentPlan api in dsAgentPlan.Data)
         {
            if(api.dirty)
            {
               AgentPlan dest = new AgentPlan();
               dest.userid = api.userid;
               dest.begin = api.begin;
               foreach(AgentPlan.Item i in api.plans)
               {
                  if (!i.Empty)
                     dest.plans.Add(i);
               }
               wrset.Add(dest);
            }
         }

         bool ret = true;
         if (wrset.Count > 0)
         {
            List<IDataSet> wrSet = new List<IDataSet>();
            wrSet.Add(wrset);
            ret = DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection());
            if (showDialog)
            {
               MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
            }
         }
         return ret;
      }


      private void tgvPrice_CellEndEdit(object sender, DataGridViewCellEventArgs e)
      {
         btnSave.Enabled = true;
         if(currentPlan != null)
         {
            currentPlan.dirty = true;
         }
      }

      private void cbDivision_SelectedIndexChanged(object sender, EventArgs e)
      {
         Division d = (Division)cbDivision.SelectedItem;
         if(d != null)
         {
            cbAgents.Items.Clear();
            foreach(Division.DivisionAgent da in d.agents)
            {
               if(da.agent != null)
               {
                  cbAgents.Items.Add(da.agent);
               }
            }
            if (cbAgents.Items.Count > 0)
               cbAgents.SelectedIndex = 0;
         }
      }

      private void cbAgents_SelectedIndexChanged(object sender, EventArgs e)
      {
         //if(currentPlan != null)
         //{
         //   SaveAgentPlan(selected, MonthStart(dtpDate.Value));
         //}

         Agent a = (Agent)cbAgents.SelectedItem;
         if(a != null)
         {
            LoadPlan(a);
         }
      }

      void LoadTree(DataSet<string, Price> dsPrice, Dictionary<string, AgentPlan.Item> data)
      {
         TreeView tmpTree = new TreeView();
         ArticlesTreeConstructor treeCnt = new ArticlesTreeConstructor(tmpTree, dsFolder, dsPrice);
         treeCnt.MakeArticlesTree();
         treeCnt.RemoveEmptyNodes();

         tgvPrice.SuspendLayout();
         tgvPrice.Nodes.Clear();
         tgvPrice.Rows.Clear();

         foreach (TreeNode n in tmpTree.Nodes)
            fillGridRecursive(n, tgvPrice.Nodes);

         //foreach(TreeGridNode tn in tgvPrice.Nodes)
         //{
         //   tn.Expand();
         //}

         LoadPlans(tgvPrice.Nodes, data);
         tgvPrice.ResumeLayout();
      }

      void LoadPlan(Agent agent)
      {
         bool dirty = false;
         DateTime planDate = MonthStart(dtpDate.Value);
         currentPlan = null;

         Dictionary<string, AgentPlan.Item> data = new Dictionary<string, AgentPlan.Item>();
         foreach (AgentPlan ap in dsAgentPlan.Data)
         {
            if (!dirty && ap.dirty)
               dirty = true;

            if (ap.userid != agent.id || ap.begin != planDate) continue;
            currentPlan = ap;
            foreach (AgentPlan.Item i in ap.plans)
            {
               data[i.id] = i;
            }
         }

         if(currentPlan == null)
         {
            currentPlan = new AgentPlan();
            currentPlan.begin = planDate;
            currentPlan.userid = agent.id;
            dsAgentPlan.Add(currentPlan);
         }

         DataSet<string, Price> aprc = (DataSet<string, Price>)DataModule.GetUserDataSet(agent.id, Price.OBJECT_NAME, typeof(DataSet<string, Price>), true);
         if(aprc.Count == 0)
         {
            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(aprc);

            FmWait.StdDataRefresh(this, upd, () => {
               LoadTree(aprc, data);
               btnSave.Enabled = dirty;
            });
         }
         else
         {
            LoadTree(aprc, data);
            btnSave.Enabled = dirty;
         }
      }

      private void LoadPlans(TreeGridNodeCollection nodes, Dictionary<string, AgentPlan.Item> data)
      {
         foreach(TreeGridNode n in nodes)
         {
            ManagerFolder mf = n.Tag as ManagerFolder;
            if (mf == null) continue;

            AgentPlan.Item plan;
            if(!data.TryGetValue(mf.id, out plan))
            {
               plan = new AgentPlan.Item
               {
                  folder = mf,
                  id = mf.id
               };
               currentPlan.plans.Add(plan);
            }
            else
            {
               plan.folder = mf;
            }

            n.DataItem = plan;

            LoadPlans(n.Nodes, data);
         }
      }

      private void dtpDate_ValueChanged(object sender, EventArgs e)
      {
         Agent a = (Agent)cbAgents.SelectedItem;
         if (a != null)
         {
            LoadPlan(a);
         }
      }

      Dictionary<string, ManagerFolder> FoldersByName()
      {
         Dictionary<string, ManagerFolder> ret = new Dictionary<string, ManagerFolder>();
         foreach(ManagerFolder mf in dsFolder.Data)
         {
            ret[mf.name] = mf;
         }

         return ret;
      }

      Dictionary<string, AgentPlan> loadPlansXLSX(out DateTime planDate)
      {
         Dictionary<string, AgentPlan> plans = new Dictionary<string, AgentPlan>();
         if (!PlanDateDialog.AskDate(out planDate))
         {
            return plans;
         }

         Dictionary<string, ManagerFolder> folders = FoldersByName();

         OpenFileDialog ofd = new OpenFileDialog();
         ofd.Filter = "Excel | *.xlsx";
         if (ofd.ShowDialog() == DialogResult.OK)
         {
            string fileName = ofd.FileName;

            Workbook wb = new Workbook();
            wb.Open(fileName);

            int ctr = 0;
            foreach (Sheet sheet in wb.Sheets)
            {
               foreach (Row r in sheet.Rows)
               {
                  if (ctr >= 2)
                  {
                     AgentPlanData apd = AgentPlanData.load(r);
                     if (apd != null)
                     {
                        AgentPlan ap;
                        if (!plans.TryGetValue(apd.id, out ap))
                        {
                           ap = new AgentPlan();
                           ap.begin = planDate;
                           ap.userid = apd.id;
                           plans[ap.userid] = ap;
                        }

                        ManagerFolder mf;
                        if (folders.TryGetValue(apd.folder, out mf))
                        {
                           AgentPlan.Item item = new AgentPlan.Item();
                           item.id = mf.id;
                           item.order = apd.orders;
                           item.akb = apd.akb;

                           ap.plans.Add(item);
                        }
                     }
                  }
                  ctr++;
               }
               break;
            }
         }

         return plans;
      }

      private void tsbLoad_Click(object sender, EventArgs e)
      {
         DateTime planDate;
         Dictionary<string, AgentPlan> plans = loadPlansXLSX(out planDate);
         if(plans.Count > 0)
         {
            SimpleDataSet<AgentPlan> wr = new SimpleDataSet<AgentPlan>(AgentPlan.OBJECT_NAME, false);
            foreach(AgentPlan ap in plans.Values)
            {
               wr.Add(ap);
            }

            List<IDataSet> wro = new List<IDataSet>();
            wro.Add(wr);
            bool res = DataModule.UpdateDataSet(wro, null, null, Config.GetConfig().GetConnection());
            if(!res)
            {
               MessageBox.Show("Ошибка при записи");
            }
            else
            {
               foreach(AgentPlan ap in dsAgentPlan.Data)
               {
                  if(ap.begin != planDate || !plans.ContainsKey(ap.userid))
                  {
                     wr.Add(ap);
                  }
               }
               dsAgentPlan = wr;

               // reload plans
               dtpDate.Value = planDate;
            }
         }
      }

      class AgentPlanData
      {
         public string folder;
         public string id;
         public string name;
         public double orders;
         public int akb;

         static public AgentPlanData load(Row r)
         {
            AgentPlanData apd = new AgentPlanData();

            Cell c = r.Cell(7);
            if (c == null)
               return null;
            string type = c.Value;
            if (type == "НОП" || type == "СКС")
               return null;

            c = r.Cell(3);
            if (c == null)
               return null;
            apd.folder = c.Value;

            c = r.Cell(5);
            if (c == null)
               return null;
            apd.id = c.Value;

            c = r.Cell(6);
            if (c == null)
               return null;
            apd.name = c.Value;

            c = r.Cell(9);
            if (c == null)
               return null;
            double.TryParse(c.Value, out apd.orders);

            c = r.Cell(11);
            if (c == null)
               return null;
            int.TryParse(c.Value, out apd.akb);

            return apd.Valid ? apd : null;
         }

         bool Valid
         {
            get
            {
               return name.Length > 0 && id.Length > 0 && folder.Length > 0 && akb > 0 && orders > 0;
            }
         }
      }
   }
}
