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
      SimpleDataSet<MatrixOrder> orderedMatrix = new SimpleDataSet<MatrixOrder>(MatrixOrder.OBJECT_NAME, false);
      DataSet<String, OrgStop> dsStop;
      DataSet<String, OrgStop> dsStopDel;
      DataGridViewCheckBoxColumn clmn = new DataGridViewCheckBoxColumn();

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

         clmn.HeaderText = "Блокировка";
         clmn.DataPropertyName = "Block";
         dgvOrgs.Columns.Add(clmn);
         dgvOrgs.CurrentCellDirtyStateChanged += new EventHandler(dgvOrgs_CurrentCellDirtyStateChanged);

         dsStop = new DataSet<string, OrgStop>(OrgStop.OBJECT_NAME, false);
         dsStopDel = new DataSet<string, OrgStop>(OrgStop.OBJECT_NAME, false);
      }

      void dgvOrgs_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         if (dgvOrgs.CurrentCell.ColumnIndex == clmn.DisplayIndex)
            dgvOrgs.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }

      protected override void FillListOrgs()
      {
         List<OrgEx> orgs = new List<OrgEx>();
         foreach (Org o in dsOrg.Data)
            orgs.Add(new OrgEx(o, this));

         orgs.Sort();
         dgvOrgs.DataSource = orgs;
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
         updSets.Add(dsStop);

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
   
      internal bool IsBlocked(Org o)
      {
 	      return dsStop.ContainsKey(o.id);
      }

      internal void SetBlocked(Org org,bool value)
      {
         OrgStop os = new OrgStop();
         os.id = org.id;
         if (value)
         {
            dsStop[org.id] = os;
            dsStopDel.Remove(org.id);
         } else 
         {
            dsStop.Remove(org.id);
            dsStopDel[org.id] = os;
         }
         if( dsStop.Count > 0 )
            owner.AddWriteSet(dsStop);
         if (dsStopDel.Count > 0)
            owner.AddRemovedSet(dsStopDel);
      }
   }

   class OrgEx : IComparable<OrgEx>
   {
      Org o;
      UserFormEx owner;

      public OrgEx(Org o, UserFormEx owner)
      {
         this.o = o;
         this.owner = owner;
      }

      public string Name { get { return o.Name; } }

      #region IComparable<OrgEx> Members

      public int CompareTo(OrgEx other)
      {
         return o.CompareTo(other.o);
      }

      public bool Block
      {
         get { return owner.IsBlocked(o); }
         set { owner.SetBlocked(o, value); }
      }

      #endregion
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