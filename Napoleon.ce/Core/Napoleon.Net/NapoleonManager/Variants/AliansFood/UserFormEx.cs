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
      DataGridViewTextBoxColumn dgvOrgDelay;
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

         dgvOrgDelay = new DataGridViewTextBoxColumn();
         dgvOrgDelay.DataPropertyName = "Delay";
         dgvOrgDelay.HeaderText = "Допустимое число дней просрочки";
         dgvOrgDelay.Width = 50;
         dgvOrgDelay.AutoSizeMode = DataGridViewAutoSizeColumnMode.None;


         dgvOrgSalesBan = new DataGridViewCheckBoxColumn();
         dgvOrgSalesBan.DataPropertyName = "SalesBan";
         dgvOrgSalesBan.HeaderText = "Запрет продаж";
         dgvOrgSalesBan.Width = 50;
         dgvOrgSalesBan.AutoSizeMode = DataGridViewAutoSizeColumnMode.None;

         dgvOrgs.Columns.Add(dgvOrgDelay);
         dgvOrgs.Columns.Add(dgvOrgSalesBan);
      }

      protected override void FillListOrgs()
      {
         List<OrgEx> orgs = new List<OrgEx>();

         foreach (Org o in dsOrg.Data)
         {
            bool ban = false;
            string delay = string.Empty;

            if (dsSalesBan != null && dsSalesBan.ContainsKey(o.id))
            {
               ban = dsSalesBan[o.id].value == 1;
               delay = dsSalesBan[o.id].delay;
            }

            orgs.Add(new OrgEx(o, ban, delay, this));
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

      internal void CheckedChanged(OrgEx o)
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
               ss.value = o.SalesBan ? 1 : 0;
               ss.delay = o.Delay;
            }
         }

         owner.AddReplacedSet(Agent.id, dsSalesBan);
      }
   }

   class OrgEx : Org, IComparable<OrgEx>
   {
      bool ban;
      UserFormEx owner;
      string delay = string.Empty;

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

      public OrgEx(Org src, bool ban, string delay, UserFormEx owner)
      {
         this.ban = ban;
         this.owner = owner;
         this.delay = delay;
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
            owner.CheckedChanged(this);
         }
      }

      public string Delay
      {
         get
         {
            return delay;
         }
         set
         {
            delay = value;
            owner.CheckedChanged(this);
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