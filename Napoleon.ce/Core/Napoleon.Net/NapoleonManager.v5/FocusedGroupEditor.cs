using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

using GRSoft.Network;
using System.Collections;
using System.Threading;

namespace GRSoft.NapoleonManager
{
   public partial class FocusedGroupEditor : Form
   {
      static readonly Color SELECTED_COLOR = Color.Red;
      static readonly Color DEFAULT_COLOR = Color.Black;

      DataSet<string, FocusedGroup> dsFocused;
      FocusController controller;

      public FocusedGroupEditor()
      {
         InitializeComponent();

         dsFocused = new DataSet<string, FocusedGroup>(FocusedGroup.OBJECT_NAME, false);
         controller = new FocusController(cbAgents, dgvOrgs, tvFolders, tbSave, dsFocused);
         controller.BeforeSave += new EventHandler(controller_BeforeSave);
         controller.OrgChanged += new OrgChangedHandle(controller_OrgChanged);
         controller.Init();
      }

      void controller_OrgChanged(object sender, OrgChangedArgs args)
      {
         FocusedGroup prev = (controller.TreeChanged) ? GetOrgFocusGroup(args.prevOrg) : null;

         FocusedGroup curr = new FocusedGroup();
         if (dsFocused.ContainsKey(args.newOrg.id))
            curr = dsFocused[args.newOrg.id];

         tvFolders.SuspendLayout();

         tvFolders.AfterCheck -= new System.Windows.Forms.TreeViewEventHandler(this.tvFolders_AfterCheck);
         MarkTree(tvFolders.Nodes, prev, curr);
         tvFolders.AfterCheck += new System.Windows.Forms.TreeViewEventHandler(this.tvFolders_AfterCheck);

         tvFolders.ResumeLayout();
      }

      void controller_BeforeSave(object sender, EventArgs e)
      {
         if (controller.CurOrg != null)
         {
            FocusedGroup group = GetOrgFocusGroup(controller.CurOrg);
            MarkTree(tvFolders.Nodes, group, null);
         }

         List<String> removed = new List<string>();
         foreach (KeyValuePair<string, FocusedGroup> kv in dsFocused)
            if (kv.Value.items.Count == 0)
               removed.Add(kv.Key);

         foreach (String key in removed)
            dsFocused.Remove(key);
      }

      FocusedGroup GetOrgFocusGroup(Org org)
      {
         FocusedGroup prev = null;
         if (org != null)
         {
            if (dsFocused.ContainsKey(org.id))
            {
               prev = dsFocused[org.id];
               prev.items.Clear();
            }
            else
            {
               prev = new FocusedGroup();
               prev.id = org.id;
               prev.items = new List<FocusedGroupItem>();
               prev.userid = controller.CurAgent.id;

               dsFocused.Add(org.id, prev);
            }
         }
         return prev;
      }

      private void MarkTree(TreeNodeCollection treeNodeCollection, FocusedGroup prevItems, FocusedGroup currItems)
      {
         foreach (TreeNode node in treeNodeCollection)
         {
            if (prevItems != null && node.Checked)
            {
               FocusedGroupItem i = new FocusedGroupItem();
               i.fid = ((ManagerFolder)node.Tag).fid;
               prevItems.items.Add(i);
            }

            if (currItems != null)
            {
               if (currItems.ContainsItem(((ManagerFolder)node.Tag).fid))
               {
                  node.Checked = true;
                  node.ForeColor = SELECTED_COLOR;
               }
               else
               {
                  node.Checked = false;
                  node.ForeColor = DEFAULT_COLOR;
               }
            }
            
            MarkTree(node.Nodes, prevItems, currItems);
         }
      }

      private void MarkChecked(TreeNodeCollection treeNodeCollection, bool check)
      {
         foreach (TreeNode node in treeNodeCollection)
         {
            node.Checked = check;
         }
      }

      private void tvFolders_AfterCheck(object sender, TreeViewEventArgs e)
      {
         TreeNode n = e.Node;
         n.ForeColor = (n.Checked) ? SELECTED_COLOR : DEFAULT_COLOR;
         MarkChecked(n.Nodes, n.Checked);

         controller.MarkDirty(true);
      }

      private void FocusedGroupEditor_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (!controller.CheckChanges())
            e.Cancel = true;
      }
   }

   class FocusedGroupItem : GRSoft.Network.DataObject
   {
      public string fid = "";
   }

   class FocusedGroup : GRSoft.Network.DataObject
   {
      public static string OBJECT_NAME = "FocusedGroup";

      public string userid = "";

      [KeyField]
      public string id = "";

      [ItemType(typeof(FocusedGroupItem))]
      public List<FocusedGroupItem> items = null;

      public bool ContainsItem(string id)
      {
         if (items != null)
         {
            foreach (FocusedGroupItem item in items)
               if (item.fid == id)
                  return true;
         }

         return false;
      }
   }
}
