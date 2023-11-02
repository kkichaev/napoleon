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

      DataSet<string, OrgDisablePhoto> dsDisabled = new DataSet<string, OrgDisablePhoto>(OrgDisablePhoto.OBJECT_NAME, false);

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
         dgvOrgSelected.Width = 60;
         dgvOrgSelected.DataPropertyName = "DisablePhoto";

         dgvOrgSelected.HeaderText = "Запрет фото";
         //dgvOrgsName.AutoSizeMode = DataGridViewAutoSizeColumnMode.DisplayedCells;

         dgvOrgs.Columns.Add(dgvOrgSelected);
         dgvOrgsName.HeaderText = "Наименование";
      }

      protected override void BeforeUpdateData(String userid, List<IDataSet> updSets)
      {
         dsDisabled.Command = new ServerCommand(Commands.Impersonate(Commands.GET, userid), dsDisabled.Name);
         updSets.Add(dsDisabled);
      }

      protected override void FillListOrgs()
      {
         List<OrgEx> orgs = new List<OrgEx>();
         foreach (Org o in dsOrg.Data)
         {
            orgs.Add(new OrgEx(o, dsDisabled.ContainsKey(o.id), this));
         }

         orgs.Sort();
         dgvOrgs.DataSource = orgs;
      }

      internal void CheckedChanged(Org o, bool newVal)
      {
         if (!newVal)
            dsDisabled.Remove(o.id);
         else
         {
            OrgDisablePhoto odp = new OrgDisablePhoto();
            odp.id = o.id;
            odp.userid = Agent.id;
            dsDisabled[o.id] = odp;
         }
         owner.AddReplacedSet(Agent.id, dsDisabled);
      }
   }

   class OrgEx : Org, IComparable<OrgEx>
   {
      bool disablePhoto;
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

      public OrgEx(Org src, bool disablePhoto, UserFormEx owner)
      {
         this.disablePhoto = disablePhoto;
         this.owner = owner;
         CopyTo(this, src);
      }

      public bool DisablePhoto
      {
         get
         {
            return disablePhoto;
         }
         set
         {
            disablePhoto = value;
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