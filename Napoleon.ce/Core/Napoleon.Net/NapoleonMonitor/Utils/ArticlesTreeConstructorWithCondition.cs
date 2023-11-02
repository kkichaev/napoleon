/*
 * Copyright (C), 2011, Гильдия разработчиков
 * 
 * Класс конструирует дерево папок в TreeView 
 * и применяте фильтер для отображения каждого 
 * узла дерева
 * 
 * kki   21/03/2011   creating
 */
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager.Utils
{
   /// <summary>
   /// Класс конструирует дерево папок в TreeView 
   /// и применяте фильтер для отображения каждого 
   /// узла дерева
   /// </summary>
   internal class ArticlesTreeConstructorWithCondition : ArticlesTreeConstructor
   {
      private ATCFilter filter;

      public ArticlesTreeConstructorWithCondition(TreeView treeView,
         DataSet<string, ManagerFolder> dsManagerFolder,
         DataSet<string, Price> dsPrice, ATCFilter filter)
         : base(treeView, dsManagerFolder, dsPrice)
      {
         this.filter = filter;
      }

      public override void MakeArticlesTree(int folderImageIndex, int priceImageIndex)
      {
         MakeArticlesTree(folderImageIndex, priceImageIndex, null);
      }

      public override void MakeArticlesTree(int folderImageIndex, int priceImageIndex, IsPriceChecked isChecked)
      {
         base.MakeArticlesTree(folderImageIndex, priceImageIndex, isChecked);
         ApplyFilter();
      }

      private void ApplyFilter()
      {
         if (filter == null)
            return;

         TraverseNodes(treeView.Nodes);
      }

      /*Проходим по всем узлам, те узлы что не удовлетворяют
       условию ApplyTreeNodeFilter удаляем*/
      private void TraverseNodes(TreeNodeCollection nodes)
      {
         for (int i = nodes.Count -1; i >= 0; i--)
         {
            if (nodes[i].Nodes.Count > 0)
               TraverseNodes(nodes[i].Nodes);

            if (!filter.ApplyTreeNodeFilter(nodes[i]))
               nodes.RemoveAt(i);
         }
      }
   }

   internal interface ATCFilter
   {
      bool ApplyTreeNodeFilter(TreeNode treeNode);
   }
}
