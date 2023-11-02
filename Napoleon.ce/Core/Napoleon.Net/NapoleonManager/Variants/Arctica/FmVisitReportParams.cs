using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmVisitReportParams : Form
   {
      DataSet<string, ManagerFolder> folders;
      List<string> checkedFolders = new List<string>();
      public FmVisitReportParams()
      {
         InitializeComponent();
         folders = DataModule.Get("ManagerFolder") as DataSet<string, ManagerFolder> ?? new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         if (folders.Count == 0)
         {
            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(folders);

            FmWait.StdDataRefresh(this, upd, LoadData);
         }
         else
            LoadData();
      }

      bool IsChecked(ManagerFolder f) { return checkedFolders.Contains(f.id); }

      void CollectChecked(TreeNodeCollection nodes, List<string> col)
      {
         foreach(TreeNode tn in nodes)
         {
            if(tn.Checked)
            {
               ManagerFolder mf = tn.Tag as ManagerFolder;
               col.Add(mf.id);
            }
            if (tn.Nodes.Count > 0)
               CollectChecked(tn.Nodes, col);
         }
      }

      public List<string> CheckedFolders
      {
         set { checkedFolders = value; }
         get
         {
            checkedFolders = new List<string>();
            CollectChecked(tvFolders.Nodes, checkedFolders);
            return checkedFolders;
         }
      }

      void LoadData()
      {
         FolderTree.MakeTree(tvFolders.Nodes, (ICollection<ManagerFolder>)folders.Data, IsChecked);
      }

      void SetChecked(TreeNodeCollection nodes, bool check)
      {
         foreach(TreeNode tn in nodes)
         {
            tn.Checked = check;
            if (tn.Nodes.Count > 0)
               SetChecked(tn.Nodes, check);
         }
      }

      private void tvFolders_AfterCheck(object sender, TreeViewEventArgs e)
      {
         SetChecked(e.Node.Nodes, e.Node.Checked);
      }
   }
}
