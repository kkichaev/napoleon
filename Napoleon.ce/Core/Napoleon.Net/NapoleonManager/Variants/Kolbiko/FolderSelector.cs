using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FolderSelector : Form
   {
      ICollection<FolderData> selected = null;

      protected FolderSelector()
      {
         InitializeComponent();
      }

      FolderData FindFolder(ManagerFolder f)
      {
         if( selected != null )
            foreach (FolderData fd in selected)
               if (fd.folder.id.CompareTo(f.id) == 0)
                  return fd;

         return null;
      }

      bool FolderChecked(ManagerFolder f)
      {
         return (FindFolder(f) != null);
      }

      internal FolderSelector(ICollection<ManagerFolder> folders, ICollection<FolderData> selected)
         : this()
      {
         this.selected = selected;
         FolderTree.MakeTree(treeFolders.Nodes, folders, FolderChecked);
      }

      internal static List<FolderData> SelectFolders(ICollection<ManagerFolder> folders, ICollection<FolderData> selected, OffTakeCoefEdit owner)
      {
         FolderSelector f = new FolderSelector(folders, selected);
         if (f.ShowDialog() == DialogResult.OK)
         {
            return f.GetSelectedFolders(owner);
         }
         return null;
      }

      private List<FolderData> GetSelectedFolders(OffTakeCoefEdit owner)
      {
         List<FolderData> ret = new List<FolderData>();
         AddChecked(ret, treeFolders.Nodes, owner);

         return ret;
      }

      private void AddChecked(List<FolderData> ret, TreeNodeCollection nodes, OffTakeCoefEdit owner)
      {
         foreach (TreeNode tn in nodes)
         {
            if (tn.Checked)
            {
               ManagerFolder mf = tn.Tag as ManagerFolder;
               FolderData fd = FindFolder(mf);
               if (fd == null)
                  fd = new FolderData(mf, 1.2, owner);
               ret.Add(fd);
            }

            if (tn.Nodes.Count > 0)
               AddChecked(ret, tn.Nodes, owner);
         }
      }

      private void btnOK_Click(object sender, EventArgs e)
      {
         DialogResult = DialogResult.OK;
      }

      private void btnCancel_Click(object sender, EventArgs e)
      {
         DialogResult = DialogResult.Cancel;
      }
   }
}
