using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public interface TreeData
   {
      string[] Data { get; }
      string Id { get; }
      string Parent { get; }
   }

   public class Tree
   {
      public class Node
      {
         public object value;
         public List<Node> nodes = new List<Node>();
      };

      public List<Node> nodes = new List<Node>();

      public static Tree Create(params IDataSet[] dataset)
      {
         Tree result = new Tree();
         Dictionary<string, Tree.Node> mapNode = new Dictionary<string, Tree.Node>();
         Dictionary<string, Tree.Node> freeNode = new Dictionary<string, Tree.Node>();

         IDataSet[] treeData = new IDataSet[dataset.Length];
         
         for(int i = 0; i < dataset.Length; i++)
            treeData[i] = dataset[i];
         
         foreach (IDataSet dataSet in treeData)
            foreach (TreeData r in dataSet.Data)
               updateTree(result, mapNode, freeNode, r);

         return result;
      }

      public static void updateTree(Tree data, Dictionary<string, Tree.Node> mapNode, Dictionary<string, Tree.Node> freeNode, TreeData r)
      {
         if (mapNode.ContainsKey(r.Parent))
         {
            Tree.Node n = mapNode[r.Parent];
            Tree.Node cn = new Tree.Node();
            cn.value = r;
            n.nodes.Add(cn);
            mapNode.Add(r.Id, cn);
         }
         else
         {
            Tree.Node n = new Tree.Node();
            n.value = r;
            mapNode.Add(r.Id, n);

            if (r.Parent.Trim().Length == 0)
               data.nodes.Add(n);
            else
               freeNode.Add(r.Id, n);
         }
      }
   }

   
}
