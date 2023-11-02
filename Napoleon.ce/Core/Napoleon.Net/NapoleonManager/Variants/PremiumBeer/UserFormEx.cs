using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Properties;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   public class UserFormEx : UserForm
   {
      DataGridViewCheckBoxColumn dgvOrgSelected;
      DataGridViewTextBoxColumn dgvOrgAddress;

      DataSet<string, Org> dsCommonOrgs = new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);

      public UserFormEx(Divisions owner) :
         base(owner)
      {
         Init();

         dgvOrgs.CurrentCellDirtyStateChanged += new EventHandler(dgvOrgs_CurrentCellDirtyStateChanged);
      }

      void dgvOrgs_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         if (dgvOrgs.Columns[dgvOrgs.CurrentCell.ColumnIndex] == dgvOrgSelected)
            dgvOrgs.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }

      void Init()
      {
         dgvOrgSelected = new DataGridViewCheckBoxColumn();
         dgvOrgSelected.Width = 30;
         dgvOrgSelected.DataPropertyName = "Checked";

         dgvOrgAddress = new DataGridViewTextBoxColumn();
         dgvOrgAddress.HeaderText = "Адрес";
         dgvOrgAddress.Name = "dgvOrgAddress";
         dgvOrgAddress.DataPropertyName = "Address";
         dgvOrgAddress.AutoSizeMode = DataGridViewAutoSizeColumnMode.DisplayedCells;

         dgvOrgsName.HeaderText = "Название";
         dgvOrgsName.AutoSizeMode = DataGridViewAutoSizeColumnMode.DisplayedCells;

         dgvOrgs.Columns.Insert(0, dgvOrgSelected);
         dgvOrgs.Columns.Add(dgvOrgAddress);
      }

      protected override void BeforeUpdateData(String userid, List<IDataSet> updSets)
      {
         if (dsCommonOrgs.Count == 0)
            updSets.Add(dsCommonOrgs);
      }

      protected override void FillListOrgs()
      {
         List<OrgEx> orgs = new List<OrgEx>();
         foreach (Org o in dsCommonOrgs.Data)
         {
            orgs.Add(new OrgEx(o, dsOrg.ContainsKey(o.id), this));
         }

         orgs.Sort();
         dgvOrgs.DataSource = orgs;
      }

      internal void CheckedChanged(Org o, bool newVal)
      {
         DataSet<string, Org> orgs = new DataSet<string,Org>(Org.OBJECT_NAME, false);
         List<OrgEx> src = (List<OrgEx>)dgvOrgs.DataSource;
         foreach(OrgEx oe in src)
         {
            if (oe.id == o.id)
            {
               if (newVal)
               {
                  orgs.Add(oe.id, oe);
                  if (!dsOrg.ContainsKey(oe.id))
                     dsOrg.Add(oe.id, oe);
               }
               else
                  dsOrg.Remove(oe.id);
               continue;
            }
            if( oe.Checked )
               orgs.Add(oe.id, oe);
         }
         orgs.UseReceivedFields = true;
         owner.AddReplacedSet(Agent.id, orgs);
      }
   }

   class OrgEx : Org, IComparable<OrgEx>
   {
      bool chkd;
      UserFormEx owner;

      public static void CopyTo(Org dest, Org src)
      {
         FieldInfo[] fields = src.GetType().GetFields(BindingFlags.Instance | BindingFlags.Public);
         foreach (FieldInfo fi in fields)
         {
            try
            {
               fi.SetValue(dest, fi.GetValue(src));
            }
            catch (Exception)
            {
            }
         }
      }

      public OrgEx(Org src, bool isChecked, UserFormEx owner)
      {
         chkd = isChecked;
         this.owner = owner;
         CopyTo(this, src);
      }

      public bool Checked
      {
         get
         {
            return chkd;
         }
         set
         {
            chkd = value;
            owner.CheckedChanged(this, value);
         }
      }

      #region Члены IComparable<OrgEx>

      int IComparable<OrgEx>.CompareTo(OrgEx other)
      {
         return this.Name.CompareTo(other.Name);
      }

      #endregion
   }
}