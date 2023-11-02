/*
 * Copyright (C), 2010 - 2011, Гильдия Разработчиков
 *
 * Подразделения для Закромов - выбор склада
 * 
 * ert   28/03/2011   creating
 */
using GRSoft.Network;
using System.Windows.Forms;
using System.Collections;
using System.Collections.Generic;
using System;

namespace GRSoft.NapoleonManager
{
   class UserFormEx : UserForm
   {
      string ASSORTIMENT_MATRIX = "<Активный ассортимент>";
      SimpleDataSet<MatrixOrder> orderedMatrix = new SimpleDataSet<MatrixOrder>(MatrixOrder.OBJECT_NAME, false);

      MatrixOrderEditor matrixEditor;
      public UserFormEx(Divisions owner)
         : base(owner)
      {
         matrixEditor = new MatrixOrderEditor();
         matrixEditor.Dock = DockStyle.Fill;

         matrixEditor.Location = new System.Drawing.Point(0, 0);
         matrixEditor.Name = "tvAgentMatrix";
         matrixEditor.Size = new System.Drawing.Size(466, 279);
         matrixEditor.TabIndex = 0;
         matrixEditor.tvAgentMatrix.AfterCheck += new System.Windows.Forms.TreeViewEventHandler(this.tvAgentMatrix_AfterCheck);
         matrixEditor.tvAgentMatrix.BeforeCheck += new System.Windows.Forms.TreeViewCancelEventHandler(this.tvAgentMatrix_BeforeCheck);
         matrixEditor.DataChanged += new EventHandler(matrixEditor_DataChanged);

         udMatrix.Controls.Clear();
         udMatrix.Controls.Add(matrixEditor);
         tvAgentMatrix = matrixEditor.tvAgentMatrix;

      }

      void matrixEditor_DataChanged(object sender, EventArgs e)
      {
         SimpleDataSet<MatrixOrder> send = new SimpleDataSet<MatrixOrder>(MatrixOrder.OBJECT_NAME, false);
         MatrixOrder mo = new MatrixOrder();

         send.Add(mo);
         mo.userid = Agent.id;
         int order = 0;
         List<String> usedMatrix = new List<string>();
         foreach (ListViewItem lvi in matrixEditor.lvOrderedMatrix.Items)
         {
            MatrixOrder.Item item = lvi.Tag as MatrixOrder.Item;
            usedMatrix.Add(item.name);
            item.order = order++;
            mo.items.Add(item);
         }

         bool matrixChanged = false;
         foreach (TreeNode tn in matrixEditor.tvAgentMatrix.Nodes)
         {
            Matrix m = tn.Tag as Matrix;
            if (usedMatrix.Contains(m.name) && tn.Checked == false)
            {
               tn.Checked = true;
               matrixChanged = true;
            }
         }

         owner.AddReplacedSet(Agent.id, send);
         if (matrixChanged)
            owner.AddReplacedSet(Agent.id, GetAgentMatrixDataSet());
      }

      protected override void BeforeUpdateData(String userid, List<IDataSet> updSets)
      {
         orderedMatrix.Filter = "\"userid\"='" + userid + "'";
         updSets.Add(orderedMatrix);
         base.BeforeUpdateData(userid, updSets);
      }

      protected override void DataLoaded()
      {
         base.DataLoaded();

         ListView.ListViewItemCollection lvc = matrixEditor.lvOrderedMatrix.Items;
         lvc.Clear();
         foreach (MatrixOrder mo in orderedMatrix.Data)
         {
            mo.items.Sort();
            foreach (MatrixOrder.Item mi in mo.items)
            {
               ListViewItem lvi = new ListViewItem(mi.name);
               lvi.Tag = mi;
               lvc.Add(lvi);
            }
         }
         matrixEditor.UpdateButtonsState();
      }

      protected override void FillMatrix()
      {
         bool noAssMtx = true;
         foreach (Matrix m in dsCommonMatrix.Data)
         {
            if (m.name == ASSORTIMENT_MATRIX)
            {
               noAssMtx = false;
               break;
            }
         }
         if(noAssMtx)
         {
            Matrix m = new Matrix();
            m.name = ASSORTIMENT_MATRIX;
            dsCommonMatrix.Add(dsCommonMatrix.Count + 1, m);
         }

         base.FillMatrix();
      }
   
   }

   class MatrixOrder : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "MatrixOrder";

      public class Item : GRSoft.Network.DataObject, IComparable<Item>
      {
         public string name = "";
         public int order = 0;

         #region IComparable<Item> Members

         public int CompareTo(Item other)
         {
            return order - other.order;
         }

         #endregion
      }

      public string userid = "";

      [ItemType(typeof(Item))]
      public List<Item> items = new List<Item>();
   }
}