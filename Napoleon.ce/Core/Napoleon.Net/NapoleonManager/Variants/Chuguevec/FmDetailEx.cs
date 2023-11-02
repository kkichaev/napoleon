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
      DataSet<string, OrgEx> dsOrgEx;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         dgvDetail.Columns.Add("OrgType", "ТТ");
         
         DataGridViewColumn clmn =  dgvDetail.Columns["OrgType"];
         clmn.DisplayIndex = 1;
         clmn.Width = 30;
         
         dsOrgEx = new DataSet<string, OrgEx>(Org.COMMON_OBJECT_NAME, false);
         dgvDetail.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(dgvDetail_CellFormatting);
      }

      void dgvDetail_CellFormatting(object sender, System.Windows.Forms.DataGridViewCellFormattingEventArgs e)
      {
         if (dgvDetail.Columns[e.ColumnIndex].Name == "OrgType")
         {
            try
            {
               OrderDetailRepresentation odr = dgvDetail.Rows[e.RowIndex].DataBoundItem as OrderDetailRepresentation;

               if (odr != null && dsOrgEx.ContainsKey(odr.NOrg.id))
               {
                  e.Value = dsOrgEx[odr.NOrg.id].mid;
               }
            }
            catch (Exception) { }
         }
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         updSets.Add(dsOrgEx);
      }
   }

   class OrgEx : Org
   {
      public string mid = string.Empty;
   }
}
