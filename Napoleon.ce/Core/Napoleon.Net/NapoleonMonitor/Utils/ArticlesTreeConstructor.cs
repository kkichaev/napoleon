/*
 * Copyright (C), 2010, Гильдия разработчиков
 * 
 * Класс конструирует дерево папок в TreeView
 * 
 * kki   26/09/2010   creating
 */

using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Drawing;

namespace GRSoft.NapoleonManager.Utils
{
   public delegate bool IsPriceChecked(Price p);
   public delegate string GetPriceName(Price p);

   /// <summary>
   ///  Класс конструирует дерево папок в TreeView из данных ManagerFolder и Price
   ///  TreeNode.tag или ManagerFolder или Price
   /// </summary>
   public class ArticlesTreeConstructor
   {
      protected TreeView treeView;
      private DataSet<string, ManagerFolder> dsManagerFolder;
      private DataSet<string, Price> dsPrice;

      public GetPriceName GetPriceName = null;

      private void Init(TreeView treeView, DataSet<string, ManagerFolder> dsManagerFolder, DataSet<string, Price> dsPrice)
      {
         this.treeView = treeView;
         this.dsManagerFolder = dsManagerFolder;
         this.dsPrice = dsPrice;
      }

      public ArticlesTreeConstructor(TreeView treeView, DataSet<string, ManagerFolder> dsManagerFolder)
      {
         Init(treeView, dsManagerFolder, null);
      }

      public ArticlesTreeConstructor(TreeView treeView, DataSet<string, ManagerFolder> dsManagerFolder, DataSet<string, Price> dsPrice)
      {
         Init(treeView, dsManagerFolder, dsPrice);
      }

      private void AddArticleNode(TreeNode parent, TreeNode node)
      {
         if (parent == null)
         {
            treeView.Nodes.Add(node);
         }
         else
         {
            parent.Nodes.Add(node);
         }
      }

      public void MakeArticlesTree()
      {
         MakeArticlesTree(0, 0);
      }

      public virtual void MakeArticlesTree(int folderImageIndex, int priceImageIndex)
      {
         MakeArticlesTree(folderImageIndex, priceImageIndex, null);
      }

      public virtual void MakeArticlesTree(int folderImageIndex, int priceImageIndex, IsPriceChecked isChecked)
      {
         int lvl = -1;
         TreeNode parent = null;
         TreeNode prevNode = null;

         try
         {
            treeView.SuspendLayout();
            treeView.Nodes.Clear();

            List<KeyValuePair<string, Price>> priceList = null;

            if (dsPrice != null)
            {
               priceList = new List<KeyValuePair<string, Price>>(dsPrice);
               priceList.Sort(new Comparison<KeyValuePair<string, Price>>(delegate(KeyValuePair<string, Price> p1, KeyValuePair<string, Price> p2)
               { return ((Price)(p1.Value)).name.CompareTo(((Price)(p2.Value)).name); }));
            }
            foreach (ManagerFolder mFolder in dsManagerFolder.Data)
            {
               try
               {
                  TreeNode node = new TreeNode(mFolder.name, folderImageIndex, folderImageIndex);
                  node.Tag = mFolder;

                  if (lvl == -1)
                  {
                     AddArticleNode(null, node);
                  }
                  else if (lvl == mFolder.level)
                  {
                     AddArticleNode(parent, node);
                  }
                  else if (lvl < mFolder.level)
                  {
                     parent = prevNode;
                     AddArticleNode(parent, node);
                  }
                  else if (lvl > mFolder.level)
                  {
                     TreeNode leftNode = prevNode.Parent;

                     if (leftNode == null)
                     {
                        MessageBox.Show("Некорректный объект Folder", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
                        break;
                     }

                     if (leftNode.Tag is Price)
                     {
                        leftNode = leftNode.Parent;
                     }

                     if (!(leftNode.Tag is ManagerFolder))
                     {
                        MessageBox.Show("Неправильная иерархия объектов, прайс не может быть владельцем прайса",
                           "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
                        break;
                     }

                     int reqLvl = mFolder.level;

                     while (leftNode.Parent != null && reqLvl < (leftNode.Tag as ManagerFolder).level)
                     {
                        leftNode = leftNode.Parent;
                     }

                     if (reqLvl > (leftNode.Tag as ManagerFolder).level)
                        parent = leftNode;
                     else
                        parent = leftNode.Parent;

                     AddArticleNode(parent, node);
                  }

                  prevNode = node;
                  lvl = mFolder.level;

                  if (dsPrice != null)
                     AddPriceItems(priceList, node, mFolder.id, priceImageIndex, isChecked);
               }
               catch (Exception e)
               {
                  MessageBox.Show(String.Format("{0}\n{1}", e.Message, e.StackTrace));
               }
            }
         }
         finally
         {
            treeView.ResumeLayout();
         }
      }

      private void AddPriceItems(List<KeyValuePair<string, Price>> priceList, TreeNode node, string id, int priceImageIndex, IsPriceChecked isChecked)
      {
         foreach (KeyValuePair<string, Price> price in priceList)
         {
            try
            {
               if (price.Value.fid.Equals(id))
               {
                  String name = (GetPriceName != null) ? name = GetPriceName(price.Value) : price.Value.name;

                  TreeNode newNode = new TreeNode(name, priceImageIndex, priceImageIndex);
                  newNode.Tag = price.Value;
                  newNode.ForeColor = price.Value.Color;
                  if (isChecked != null)
                     newNode.Checked = isChecked(price.Value);

                  node.Nodes.Add(newNode);
               }
            }
            catch (Exception e)
            {
               MessageBox.Show(String.Format("{0}\n{1}", e.Message, e.StackTrace));
            }
         }
      }

      bool RemoveEmptyNodes(TreeNode node)
      {
         bool deleted;
         do
         {
            deleted = false;
            foreach (TreeNode tn in node.Nodes)
            {
               if (RemoveEmptyNodes(tn))
               {
                  deleted = true;
                  break;
               }
            }
         } while (deleted);

         if (node.Tag is ManagerFolder && node.Nodes.Count == 0)
         {
            node.Remove();
            return true;
         }
         return false;
      }

      internal void RemoveEmptyNodes()
      {
         TreeNode root = new TreeNode();
         while(treeView.Nodes.Count > 0)
         {
            TreeNode tn = treeView.Nodes[0];
            tn.Remove();
            root.Nodes.Add(tn);
         }

         if (!RemoveEmptyNodes(root))
         {
            while (root.Nodes.Count > 0)
            {
               TreeNode tn = root.Nodes[0];
               tn.Remove();
               treeView.Nodes.Add(tn);
            }
         }
      }
   }
}
