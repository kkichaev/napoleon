using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.IO;
using System.Runtime.InteropServices;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   public partial class FmOneDayReport : Form
   {
      static readonly string CFG_FILE_NAME = "FmOneDayReport.cfg";

      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd);
      
      DataSet<string, ManagerFolder> folders;
      DataSet<string, Price> dsPrice;
      string selectedFolders = "";
      Agent current = null;

      Dictionary<string, string> checkedFolders = new Dictionary<string, string>();
      Dictionary<string, int> existingFolders = new Dictionary<string, int>();
      
      private const string REPORT_NAME = "oneday";
      private static int count = 0;
      SimpleDataSet<Result> dsResult = new SimpleDataSet<Result>("Result", false);

      public FmOneDayReport()
      {
         InitializeComponent();
      }

      private void FmOneDayReport_Load(object sender, EventArgs e)
      {
         folders = new DataSet<string, ManagerFolder>("ManagerFolder", false);
         folders.Filter = DataUtils.USERID_IS_NULL_STR;

         dsPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);
         dsPrice.Filter = "\"userid\" is null or \"userid\"=''";
         
         FmWait.StdDataRefresh(this, new List<IDataSet>(new IDataSet[] { folders, dsPrice }), DoLoad);
      }

      void DoLoad()
      {
         try
         {
            string folder = Config.GetAppHomeDir();
            Directory.CreateDirectory(folder);
            string fileName = folder + "\\" + CFG_FILE_NAME;
            if (File.Exists(fileName))
            {
               string[] lines = File.ReadAllLines(fileName, Encoding.UTF8);
               foreach (string line in lines)
               {
                  string[] parts = line.Split(new char[] { '=' });
                  if (parts.Length > 1)
                     checkedFolders[parts[0]] = parts[1];
               }
            }
         }
         catch (Exception)
         {
         }

         foreach (Price p in dsPrice.Data)
            if (p.qty > 0)
            {
               if (!existingFolders.ContainsKey(p.fid))
                  existingFolders[p.fid] = 1;
               else
                  existingFolders[p.fid] += (int)(p.qty + 0.0005);
            }

         FolderTree.MakeTree(tvFolders.Nodes, (ICollection<ManagerFolder>)folders.Data, null);
         RemoveEmpty(tvFolders.Nodes);

         List<Agent> list = new List<Agent>();
         foreach (Agent a in ((Manager)CurrentUser.user).GetAgents().Data)
            list.Add(a);

         list.Sort(new Comparison<Agent>(delegate(Agent lhs, Agent rhs) { return lhs.Name.CompareTo(rhs.Name); }));
         cbAgent.Items.AddRange(list.ToArray());

         if (cbAgent.Items.Count > 0)
            cbAgent.SelectedIndex = 0;
      }

      bool RemoveEmpty(TreeNodeCollection collection)
      {
         bool ret;
         do
         {
            ret = false;
            foreach (TreeNode tn in collection)
            {
               if (tn.Nodes.Count == 0 && existingFolders.ContainsKey((tn.Tag as ManagerFolder).id) == false)
               {
                  collection.Remove(tn);
                  ret = true;
                  break;
               }
               if (RemoveEmpty(tn.Nodes))
               {
                  ret = true;
               }
            }
         } while (ret);
         return ret;
      }

      void PutChecked(TreeNodeCollection collection)
      {
         foreach (TreeNode tn in collection)
         {
            if (tn.Checked)
               selectedFolders += ((ManagerFolder)tn.Tag).id + ",";

            PutChecked(tn.Nodes);
         }
      }

      void SaveConfig()
      {
         try
         {
            if (current != null)
            {
               selectedFolders = ",";
               PutChecked(tvFolders.Nodes);
               checkedFolders[current.id] = selectedFolders;
            }

            string folder = Config.GetAppHomeDir();
            Directory.CreateDirectory(folder);
            string fileName = folder + "\\" + CFG_FILE_NAME;
            List<string> lines = new List<string>();
            foreach (KeyValuePair<string, string> kv in checkedFolders)
               lines.Add(kv.Key + "=" + kv.Value);
            File.WriteAllLines(fileName, lines.ToArray(), Encoding.UTF8);
         }
         catch (Exception)
         {
         }
      }

      void SetCheckedNodes(TreeNodeCollection collection)
      {
         foreach (TreeNode tn in collection)
         {
            tn.Checked = FolderChecked(tn.Tag as ManagerFolder);
            SetCheckedNodes(tn.Nodes);
         }
      }

      private void cbAgent_SelectedIndexChanged(object sender, EventArgs e)
      {
         SaveConfig();

         Agent a = cbAgent.SelectedItem as Agent;
         current = a;
         if (checkedFolders.ContainsKey(a.id))
            selectedFolders = checkedFolders[a.id];
         else
            selectedFolders = ",";

         SetCheckedNodes(tvFolders.Nodes);
      }

      bool FolderChecked(ManagerFolder f)
      {
         return selectedFolders.Contains("," + f.id + ",");
      }

      void SetNodes(TreeNodeCollection collection, bool check)
      {
         foreach (TreeNode tn in collection)
         {
            tn.Checked = check;
            SetNodes(tn.Nodes, check);
         }
      }

      private void tvFolders_AfterCheck(object sender, TreeViewEventArgs e)
      {
         SetNodes(e.Node.Nodes, e.Node.Checked);
      }

      class Data : GRSoft.Network.DataObject
      {
         public string agent = string.Empty;
         public DateTime date = DateTime.MinValue;
         public string folders = "";
      }

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;
      }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         Agent a = cbAgent.SelectedItem as Agent;
         if (a != null)
         {
            SaveConfig();

            Data data = new Data();
            data.agent = a.id;
            data.date = dtpDate.Value.Date;
            data.folders = selectedFolders;

            Report reportResultSet = new Report(REPORT_NAME, data, dsResult);
            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(reportResultSet);
            FmWait.StdDataRefresh(this, upd, DoReport);
         }
         else
            MessageBox.Show("Выберите агента");
      }

      private void DoReport()
      {
         if (dsResult.Count > 0)
         {
            Result res = dsResult[0];
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

      }
   }
}
