using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetailEx : FmDetail
   {
      DataSet<string, OrgNotes> dsOrgNotes;

      public FmDetailEx(FmDetailData data) : base(data)
      {
         dsOrg = new DataSet<string, Org>(Org.COMMON_OBJECT_NAME, false);

         dsOrgNotes = (DataSet<string, OrgNotes>)DataModule.Get(OrgNotes.OBJECT_NAME) ??
            new DataSet<string, OrgNotes>(OrgNotes.OBJECT_NAME);
         dgvRemnantsItemsQty.Visible = false;
         dgvDetailColumnSum.Visible = false;

         DataGridViewCheckBoxColumn sklad = new DataGridViewCheckBoxColumn();
         sklad.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         sklad.DataPropertyName = "IsSklad";
         sklad.HeaderText = "Склад";
         sklad.Name = "dgvRemnantsItemsSklad";

         DataGridViewCheckBoxColumn shelf = new DataGridViewCheckBoxColumn();
         shelf.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         shelf.DataPropertyName = "IsShelf";
         shelf.HeaderText = "Полка";
         shelf.Name = "dgvRemnantsItemsShelf";

         dgvRemnantsItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] { sklad, shelf });
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);

         dsOrgFolder.Filter = String.Format(MainFormEx.ORG_FOLDERS_FILTER, dateBegin, dateEnd);

         Agent a = GetSelectedAgent();

         if(a != null)
         {
            dsOrgNotes.Filter = "userid='" + a.id + "'";
            updSets.Insert(0, dsOrg);
            updSets.Add(dsOrgNotes);

            if (!updSets.Contains(dsOrg))
            {
               updSets.Insert(0, dsOrg);
            }
         }
      }

      protected override void UpdateDetailTable(System.Windows.Forms.DataGridViewRow curRow)
      {
         base.UpdateDetailTable(curRow);

         if (curRow != null)
         {
            Agent a = GetSelectedAgent();
            string id = string.Empty;
            OrderDetailRepresentation ord = curRow.DataBoundItem as OrderDetailRepresentation;

            if (ord != null)
               id = ord.NOrg.id;

            if (id.Length > 0 && a != null && dsOrgNotes.ContainsKey(id))
            {
               lbNotes.Text = dsOrgNotes[id].text;
               lbNotes.Visible = true;
            }
         }
      }

      protected override DataSet<int, OrgFolder> CreateOrgFolder(String agentid)
      {
         return (DataSet<int, OrgFolder>) DataModule.Get(OrgFolder.OBJECT_NAME) ?? new DataSet<int, OrgFolder>(OrgFolder.OBJECT_NAME);
      }
   }

   class OrgNotes : GRSoft.Network.DataObject
   {
      public static string OBJECT_NAME = "OrgNotes";

      [KeyField]
      public string id = string.Empty;
      public string text = string.Empty;
      public string userid = string.Empty;
   }
}
