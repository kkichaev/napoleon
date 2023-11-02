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
      DataSet<string, OrgAsmMatrix> dsOrgAsm;
      DataSet<string, OrgAsmMatrix> dsRmv = new DataSet<string, OrgAsmMatrix>(OrgAsmMatrix.OBJECT_NAME, false);
      DataGridViewCheckBoxColumn clmn = new DataGridViewCheckBoxColumn();

      public UserFormEx(Divisions owner)
         : base(owner)
      {
         clmn.HeaderText = "Не исп. акт. асс.";
         clmn.DataPropertyName = "NotUseAsm";
         dgvOrgs.Columns.Add(clmn);
         dgvOrgs.CurrentCellDirtyStateChanged += new EventHandler(dgvOrgs_CurrentCellDirtyStateChanged);

         dsOrgAsm = new DataSet<string, OrgAsmMatrix>(OrgAsmMatrix.OBJECT_NAME, false);
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
         dsOrgAsm.Filter = "userid='" + Agent.id + "'";
         updSets.Add(dsOrgAsm);

         base.BeforeUpdateData(userid, updSets);
      }

      protected override void DataLoaded()
      {
         base.DataLoaded();
      }
   
      internal bool NotUseASM(Org o)
      {
 	      return dsOrgAsm.ContainsKey(o.id);
      }

      internal void SetNotUseASM(Org org,bool value)
      {
         OrgAsmMatrix os = new OrgAsmMatrix();
         os.id = org.id;
         os.userid = Agent.id;
         if (value)
         {
            dsOrgAsm[org.id] = os;
            dsRmv.Remove(org.id);
         } else 
         {
            dsOrgAsm.Remove(org.id);
            dsRmv[org.id] = os;
         }
         if(dsOrgAsm.Count > 0 )
            owner.AddWriteSet(dsOrgAsm);
         if (dsRmv.Count > 0)
            owner.AddRemovedSet(dsRmv);
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

      public bool NotUseAsm
      {
         get { return owner.NotUseASM(o); }
         set { owner.SetNotUseASM(o, value); }
      }

      #endregion
   }
}