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
      DataGridViewComboBoxColumn whColumn = new DataGridViewComboBoxColumn();

      public UserFormEx(Divisions owner)
         : base(owner)
      {
         dgvOrgs.EditMode = DataGridViewEditMode.EditOnEnter;
         dgvOrgs.CurrentCellDirtyStateChanged += new System.EventHandler(CurrentCellDirtyStateChanged);

         whColumn.DataPropertyName = "OrgMatrix";
         whColumn.DisplayStyle = System.Windows.Forms.DataGridViewComboBoxDisplayStyle.ComboBox;
         whColumn.HeaderText = "Матрица";
         whColumn.Name = "matrix";

         dgvOrgs.Columns.Add(whColumn);
      }

      void CurrentCellDirtyStateChanged(object sender, System.EventArgs e)
      {
         DataGridViewCell cell = dgvOrgs.CurrentCell;
         if (cell != null && dgvOrgs.Columns[cell.ColumnIndex].HeaderText == whColumn.HeaderText)
         {
            dgvOrgs.CommitEdit(DataGridViewDataErrorContexts.Commit);
         }
      }

      protected override void DataLoaded()
      {
      }

      protected override void FillListOrgs()
      {
         whColumn.Items.Clear();
         whColumn.Items.Add("");
         foreach (Matrix i in dsCommonMatrix.Data)
            whColumn.Items.Add(i.name);

         List<OrgItemEx> orgs = new List<OrgItemEx>();
         foreach (Org o in dsOrg.Data)
         {
            if (o.matrixName != null && o.matrixName.Count > 0)
            {
               String mtx = o.matrixName[0].name;
               if (whColumn.Items.Contains(mtx) == false)
               {
                  o.matrixName = null;
                  //mtx = "<" + mtx + ">";
                  //if (whColumn.Items.Contains(mtx) == false)
                  //{
                  //   whColumn.Items.Add(mtx);
                  //   o.matrixName[0].name = mtx;
                  //}
               }
            }
            orgs.Add(new OrgItemEx(o, owner, Agent.id, dsOrg));
         }

         orgs.Sort();
         dgvOrgs.DataSource = orgs;
      }

      class OrgItemEx : IComparable<OrgItemEx>
      {
         Org o;
         Divisions owner;
         string userid;
         DataSet<string, Org> dsOrgs;

         public string Name { get { return o.Name; } }

         public OrgItemEx(Org o, Divisions owner, string userid, DataSet<string, Org> dsOrgs)
         {
            this.o = o;
            this.owner = owner;
            this.userid = userid;
            this.dsOrgs = dsOrgs;
         }

         public string OrgMatrix
         {
            get
            {
               string ret = "";
               if (o.matrixName != null && o.matrixName.Count > 0)
                  ret = o.matrixName[0].name;

               return ret;
            }

            set
            {
               SimpleDataSet<OrgMatrixName> wr = new SimpleDataSet<OrgMatrixName>(OrgMatrixName.OBJECT_NAME);
               foreach (Org org in dsOrgs.Data)
                  if (org != o && org.matrixName != null && org.matrixName.Count > 0)
                     wr.Add(org.matrixName[0]);

               o.matrixName = new List<OrgMatrixName>();
               if (value != null && value.Length > 0)
               {
                  OrgMatrixName omx = new OrgMatrixName();
                  omx.userid = userid;
                  omx.orgid = o.id;
                  omx.name = value;
                  o.matrixName.Add(omx);
                  wr.Add(omx);
               }

               owner.AddReplacedSet(userid, wr);
            }
         }

         #region Члены IComparable<OrgItemEx>

         public int CompareTo(OrgItemEx other)
         {
            return Name.CompareTo(other.Name);
         }

         #endregion
      }
   }

   public class OrgMatrixName : GRSoft.Network.DataObject
   {
      public static string OBJECT_NAME = "OrgMatrix";
      public string name = "";
      public string orgid = "";
      public string userid = "";
   }
}