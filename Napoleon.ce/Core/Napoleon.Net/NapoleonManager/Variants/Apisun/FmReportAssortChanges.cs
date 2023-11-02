using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;
using System.Runtime.InteropServices;
using System.IO;

namespace GRSoft.NapoleonManager
{
   public partial class FmReportAssortChanges : Form
   {
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd); 

      private DataSet<string, OrgRegion> dsOrgRegion;
      static int count = 1;

      public FmReportAssortChanges()
      {
         InitializeComponent();

         dsOrgRegion = (DataSet<string, OrgRegion>)DataModule.Get(OrgRegion.OBJECT_NAME) ??
           new DataSet<string, OrgRegion>(OrgRegion.OBJECT_NAME);
         dsOrgRegion.Filter = "id not null";
      }

      private void FmReportAssortChanges_Load(object sender, EventArgs e)
      {
         List<Agent> list = new List<Agent>();
         list.AddRange(CurrentUser.user.GetAgents().Values);
         list.Sort((lhs, rhs) =>{ return lhs.Name.CompareTo(rhs.Name);});
         
         cbAgents.Items.AddRange(list.ToArray());

         if (dsOrgRegion.Count == 0)
         {
            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(dsOrgRegion);

            FmWait.StdDataRefresh(this, upd, () => { startInit(); });
         }
         else
            startInit();
      }

      private void startInit()
      {
         tvRegions.Nodes.Clear();

         Tree tree = Tree.Create(dsOrgRegion);

         foreach (Tree.Node n in tree.nodes)
            InsertTreeNode(tvRegions.Nodes, n);

      }

      private void InsertTreeNode(TreeNodeCollection nodes, Tree.Node n)
      {
         TreeData td = (TreeData)n.value;

         if(td != null && td.Data.Length > 0)
         {
            TreeNode nn = new TreeNode(td.Data[0]);
            nodes.Add(nn);
            nn.Tag = n.value;

            foreach (Tree.Node child in n.nodes)
               InsertTreeNode(nn.Nodes, child);
         }
      }

      private void btnSKU_Click(object sender, EventArgs e)
      {
         List<Price> list = new List<Price>();

         foreach(object i in lbPrice.Items)
            list.Add(i as Price);

         list = FmSelectSKU.SelectItems(this, list, null, true);

         if (list != null)
         {
            lbPrice.Items.Clear();
            lbPrice.Items.AddRange(list.ToArray());
         }
      }

      private void btnCheckAgent_Click(object sender, EventArgs e)
      {
         checkAgent(true);
      }

      private void checkAgent(bool val)
      {
         for (int i = 0; i < cbAgents.Items.Count; i++)
            cbAgents.SetItemChecked(i, val);
      }

      private void btnUncheckAgent_Click(object sender, EventArgs e)
      {
         checkAgent(false);
      }

      private void btnCheckRegion_Click(object sender, EventArgs e)
      {
         checkRegion(true);
      }

      private void checkRegion(bool val)
      {
         foreach (TreeNode n in tvRegions.Nodes)
            checkRegionRec(n, val);
      }

      private void checkRegionRec(TreeNode node, bool val)
      {
         foreach (TreeNode n in node.Nodes)
            checkRegionRec(n, val);

         node.Checked = val;
      }

      private void btnUncheckRegion_Click(object sender, EventArgs e)
      {
         checkRegion(false);
      }

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;
      }

      class Data : GRSoft.Network.DataObject
      {
         public string id = string.Empty;
         public DateTime range1start = DateTime.Now;
         public DateTime range1finish = DateTime.Now;
         public DateTime range2start = DateTime.Now;
         public DateTime range2finish = DateTime.Now;
         public string agents = string.Empty;
         public string regions = string.Empty;
         public string items = string.Empty;
      }

      private void btnReport_Click(object sender, EventArgs e)
      {
         const string REPORT_NAME = "assort_ch_report";

         Data data = new Data();
         data.id = "test_record";
         data.agents = CollectAgents();
         data.regions = CollectRegions();
         data.range1start = dtpRange1Start.Value.Date;
         data.range1finish = dtpRange1Finish.Value.Date;
         data.range2start = dtpRange2Start.Value.Date;
         data.range2finish = dtpRange2Finish.Value.Date;
         data.items = CollectPrice();

         Result result = new Result();
         SimpleDataSet<Result> resultSet = new SimpleDataSet<Result>("Result", false);
         Report r = new Report(REPORT_NAME, data, resultSet);

         Thread th = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), r, FmWait.ProgressIndicator);
         FmWait.ShowForm(this, th);
         th.Join();
         FmWait.CloseForm();

         if (resultSet.Count > 0)
         {
            Result res = resultSet[0];
            if (res.file.Length > 0)
            {
               string fileName = Path.GetTempPath() + "\\" + REPORT_NAME + count.ToString() + ".xlsx";
               while (File.Exists(fileName))
               {
                  count++;
                  fileName = Path.GetTempPath() + "\\" + REPORT_NAME + count.ToString() + ".xlsx";
               }
               File.WriteAllBytes(fileName, res.file);
               ShellExecute(IntPtr.Zero, "open", fileName, "", "", 1);
            }
         }
         else
            MessageBox.Show("Ошибка построения отчета");
      }

      private string CollectPrice()
      {
         StringBuilder result = new StringBuilder();

         foreach (object o in lbPrice.Items)
         {
            Price p = o as Price;

            if (p != null)
            {
               if (result.Length > 0)
                  result.Append(",");

               result.Append(p.id);
            }
         }

         return result.ToString();
      }

      private string CollectAgents()
      {
         List<Agent> list = new List<Agent>();

         for (int i = 0; i < cbAgents.Items.Count; i++)
            if (cbAgents.GetItemChecked(i))
               list.Add(cbAgents.Items[i] as Agent);

         StringBuilder result = new StringBuilder();

         for (int i = 0; i < list.Count; i++)
         {
            if (result.Length > 0)
               result.Append(",");

            result.Append("'").Append(list[i].id).Append("'");
         }

         return result.ToString();
      }

      private void NodeCheckRcv(TreeNode node, bool val)
      {
         foreach (TreeNode n in node.Nodes)
            NodeCheckRcv(n, val);

         node.Checked = val;
      }

      private void tvRegions_AfterCheck(object sender, TreeViewEventArgs e)
      {
         if(e.Action == TreeViewAction.ByMouse)
            NodeCheckRcv(e.Node, e.Node.Checked);
      }

      private string CollectRegions()
      {
         StringBuilder result = new StringBuilder();
         CollectRegionsRcv(tvRegions.Nodes, result);

         return result.ToString();
      }

      private void CollectRegionsRcv(TreeNodeCollection nodes, StringBuilder val)
      {
         foreach (TreeNode n in nodes)
         {
            CollectRegionsRcv(n.Nodes, val);

            if (n.Checked)
            {
               if (val.Length > 0)
                  val.Append(",");

               OrgRegion r = n.Tag as OrgRegion;

               val.Append(r.Id);
            }
         }
      }

      private void miDel_Click(object sender, EventArgs e)
      {
         Price p = lbPrice.SelectedItem as Price;

         if (p != null && MessageBox.Show("Запись будет удалена, удалить", "Вопрос", 
            MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
               lbPrice.Items.Remove(p);
      }
   }
}
