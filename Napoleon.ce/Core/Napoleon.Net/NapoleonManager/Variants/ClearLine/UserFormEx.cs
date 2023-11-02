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
      DataGridViewCheckBoxColumn dgvOrgSalesBan;
      DataGridViewTextBoxColumn dgvOrgAddress;
      DataSet<string, SalesBan> dsSalesBan;

      public UserFormEx(Divisions owner) :
         base(owner)
      {
         Init();

         dgvOrgs.CurrentCellDirtyStateChanged += new EventHandler(dgvOrgs_CurrentCellDirtyStateChanged);
      }

      void dgvOrgs_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         if (dgvOrgs.Columns[dgvOrgs.CurrentCell.ColumnIndex] == dgvOrgSalesBan)
            dgvOrgs.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }

      void Init()
      {
         dgvOrgsName.HeaderText = "Название";
         dgvOrgsName.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;

         dgvOrgSalesBan = new DataGridViewCheckBoxColumn();
         dgvOrgSalesBan.DataPropertyName = "SalesBan";
         dgvOrgSalesBan.HeaderText = "Запрет продаж";
         dgvOrgSalesBan.Width = 50;
         dgvOrgSalesBan.AutoSizeMode = DataGridViewAutoSizeColumnMode.None;

         dgvOrgs.Columns.Add(dgvOrgSalesBan);
      }

      protected override void FillListOrgs()
      {
         List<OrgEx> orgs = new List<OrgEx>();

         foreach (Org o in dsOrg.Data)
         {
            bool ban = false;

            if (dsSalesBan != null && dsSalesBan.ContainsKey(o.id))
            {
               ban = dsSalesBan[o.id].value == 1;
            }

            orgs.Add(new OrgEx(o, ban, this));
         }

         orgs.Sort();
         dgvOrgs.DataSource = orgs;
      }

      protected override void BeforeUpdateData(string userid, List<IDataSet> updSets)
      {
         base.BeforeUpdateData(userid, updSets);

         dsSalesBan = DataModule.GetUserDataSet(userid, SalesBan.OBJECT_NAME,
            typeof(DataSet<string, SalesBan>)) as DataSet<string, SalesBan>;

         dsSalesBan.Command = new ServerCommand(Commands.Impersonate(Commands.GET, userid), dsSalesBan.Name);

         updSets.Add(dsSalesBan);
      }

      internal void CheckedChanged(Org o, bool val)
      {
         List<OrgEx> src = (List<OrgEx>)dgvOrgs.DataSource;
         foreach(OrgEx oe in src)
         {
            if (oe.id == o.id)
            {
               if (!dsSalesBan.ContainsKey(o.id))
               {
                  SalesBan s = new SalesBan();
                  s.id = o.id;
                  s.userid = Agent.id;

                  dsSalesBan[o.id] = s;
               }

               SalesBan ss = dsSalesBan[o.id];
               ss.value = val ? 1 : 0;

            }
         }

         owner.AddReplacedSet(Agent.id, dsSalesBan);
      }
   }

   class OrgEx : Org, IComparable<OrgEx>
   {
      bool ban;
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

      public OrgEx(Org src, bool ban, UserFormEx owner)
      {
         this.ban = ban;
         this.owner = owner;
         CopyTo(this, src);
      }

      public bool SalesBan
      {
         get
         {
            return ban;
         }
         set
         {
            ban = value;
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