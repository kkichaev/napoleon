using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   delegate bool FolderChecked(ManagerFolder f);

   class FolderTree
   {
      public static void MakeTree(TreeNodeCollection nodes, ICollection<ManagerFolder> folders)
      {
         MakeTree(nodes, folders, null);
      }
      public static void MakeTree(TreeNodeCollection nodes, ICollection<ManagerFolder> folders, FolderChecked fc)
      {
         bool starting = false;
         int lvl = -1;
         TreeNode parent = null;
         TreeNode prevNode = null;

         nodes.Clear();
         foreach (ManagerFolder mFolder in folders)
         {
            TreeNode node = new TreeNode(mFolder.name, 0, 0);
            node.Tag = mFolder;
            node.Checked = (fc != null && fc(mFolder));

            if (!starting)
            {
               starting = true;
               nodes.Add(node);
            } else if (lvl == mFolder.level)
            {
               if (parent != null)
                  parent.Nodes.Add(node);
               else
                  nodes.Add(node);
            }
            else if (lvl < mFolder.level)
            {
               parent = prevNode;
               parent.Nodes.Add(node);
            }
            else if (lvl > mFolder.level)
            {
               TreeNode leftNode = prevNode.Parent == null ? prevNode : prevNode.Parent;
               int reqLvl = mFolder.level;

               while (leftNode.Parent != null && reqLvl < (leftNode.Tag as ManagerFolder).level)
               {
                  if (leftNode.Parent == null)
                     break;
                  leftNode = leftNode.Parent;
               }

               if (reqLvl > (leftNode.Tag as ManagerFolder).level)
                  parent = leftNode;
               else
                  parent = leftNode.Parent;
               if (parent != null)
                  parent.Nodes.Add(node);
               else
                  nodes.Add(node);
            }

            prevNode = node;
            lvl = mFolder.level;
         }

      }
   }
}