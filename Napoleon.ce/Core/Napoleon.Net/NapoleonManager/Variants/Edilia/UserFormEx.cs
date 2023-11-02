using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Collections;
using GRSoft.UILib;

namespace GRSoft.NapoleonManager
{
   public class UserFormEx : UserForm
   {
      System.Windows.Forms.TreeView tvReturnFolders;
      System.Windows.Forms.TabPage udRetFolders;

      private DataSet<string, ReturnFolders> dsRetFolders;


      public UserFormEx(Divisions owner)
         : base(owner)
      {
         this.udRetFolders = new System.Windows.Forms.TabPage();
         this.tvReturnFolders = new System.Windows.Forms.TreeView();

         this.udRetFolders.SuspendLayout();
         this.userDetails.Controls.Add(this.udRetFolders);
         // 
         // udDogovors
         // 
         this.udRetFolders.Controls.Add(this.tvReturnFolders);
         this.udRetFolders.Location = new System.Drawing.Point(4, 23);
         this.udRetFolders.Name = "udDogovors";
         this.udRetFolders.Padding = new System.Windows.Forms.Padding(3);
         this.udRetFolders.Size = new System.Drawing.Size(466, 279);
         this.udRetFolders.TabIndex = 3;
         this.udRetFolders.Text = "Товар для возврата";
         this.udRetFolders.UseVisualStyleBackColor = true;
         // 
         // tvDogovors
         // 
         this.tvReturnFolders.CheckBoxes = true;
         this.tvReturnFolders.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvReturnFolders.Location = new System.Drawing.Point(3, 3);
         this.tvReturnFolders.Name = "tvDogovors";
         this.tvReturnFolders.Size = new System.Drawing.Size(460, 273);
         this.tvReturnFolders.TabIndex = 1;

         this.udRetFolders.ResumeLayout(false);

         userDetails.SelectedIndexChanged += userDetails_SelectedIndexChanged;

         owner.Size = new Size(owner.Width + 50, owner.Height);
      }

      void userDetails_SelectedIndexChanged(object sender, EventArgs e)
      {
         if (userDetails.SelectedTab == udRetFolders)
            MakeTree();
      }

      protected override void BeforeUpdateData(String userid, List<IDataSet> updSets)
      {
         if (dsRetFolders == null)
            dsRetFolders = new DataSet<string, ReturnFolders>(ReturnFolders.OBJECT_NAME, false);

         dsRetFolders.Command = new ServerCommand(Commands.Impersonate(Commands.GET, userid), dsRetFolders.Name);
         dsRetFolders.Clear();
         updSets.Add(dsRetFolders);
      }

      void AddChecked(DataSet<string, ReturnFolders> ret, TreeNodeCollection nodes)
      {
         foreach (TreeNode node in nodes)
         {
            if (node.Checked)
            {
               ManagerFolder folder = node.Tag as ManagerFolder;
               ReturnFolders ad = new ReturnFolders();
               ad.fid = folder.id;
               ad.userid = Agent.id;
               ret.Add(ad.fid, ad);
            }
            if (node.Nodes.Count > 0)
               AddChecked(ret, node.Nodes);
         }
      }

      void PutChecked(bool check, TreeNodeCollection nodes)
      {
         foreach(TreeNode node in nodes)
         {
            node.Checked = check;
            if (node.Nodes.Count > 0)
               PutChecked(check, node.Nodes);
         }
      }

      private IDataSet GetRetFoldersDataSet()
      {
         dsRetFolders.Clear();
         AddChecked(dsRetFolders, tvReturnFolders.Nodes);
         return dsRetFolders;
      }

      void tvReturnFolders_AfterCheck(object sender, TreeViewEventArgs e)
      {
         tvReturnFolders.BeginUpdate();
         PutChecked(e.Node.Checked, e.Node.Nodes);
         tvReturnFolders.EndUpdate();

         owner.AddReplacedSet(Agent.id, GetRetFoldersDataSet());
      }

      void MarkTreeNodes(TreeNodeCollection nodes)
      {
         foreach(TreeNode node in nodes)
         {
            ManagerFolder folder = node.Tag as ManagerFolder;
            node.Checked = (dsRetFolders.ContainsKey(folder.id));
            if (node.Nodes.Count > 0)
               MarkTreeNodes(node.Nodes);
         }
      }

      void MakeTree()
      {
         tvReturnFolders.AfterCheck -= (tvReturnFolders_AfterCheck);
         tvReturnFolders.BeginUpdate();

         FolderTree.MakeTree(tvReturnFolders.Nodes, (ICollection<ManagerFolder>)(dsManagerFolder.Count == 0 ? 
            owner.mainArticleFolder.Data : dsManagerFolder.Data));
         MarkTreeNodes(tvReturnFolders.Nodes);

         tvReturnFolders.EndUpdate();
         tvReturnFolders.AfterCheck += (tvReturnFolders_AfterCheck);
      }

      protected override void AfterControlFilled()
      {
         MakeTree();
      }
   }
};