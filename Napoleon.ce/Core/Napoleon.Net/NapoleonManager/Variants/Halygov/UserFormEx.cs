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
      DataSet<String, OrgStop> dsStop;
      DataSet<String, OrgStop> dsStopDel;
      DataGridViewCheckBoxColumn clmn = new DataGridViewCheckBoxColumn();

      public UserFormEx(Divisions owner)
         : base(owner)
      {
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

      protected override void BeforeUpdateData(String userid, List<IDataSet> updSets)
      {
         updSets.Add(dsStop);

         base.BeforeUpdateData(userid, updSets);
      }

      protected override void DataLoaded()
      {
         base.DataLoaded();
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
}