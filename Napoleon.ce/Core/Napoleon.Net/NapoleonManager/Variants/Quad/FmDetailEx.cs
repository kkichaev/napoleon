using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.NapoleonManager;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         DataGridViewTextBoxColumn dgvDetailDebet = new DataGridViewTextBoxColumn();
         dgvDetailDebet.DisplayIndex = 7;
         dgvDetailDebet.HeaderText = "Просрочено";
         dgvDetailDebet.Name = "dgvDetailDebet";
         dgvDetailDebet.Width = 120;
         dgvDetailDebet.DataPropertyName = "Debet";

         dgvDetail.Columns.Add(dgvDetailDebet);

         dgvDetail.CellFormatting += new DataGridViewCellFormattingEventHandler(dgvDetail_CellFormatting);
      }

      void dgvDetail_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         OrderDetailRepresentation odr = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as OrderDetailRepresentation;
         if (odr != null && odr.StoreObject is Order && e.ColumnIndex == 9)
         {
            e.Value = String.Format("{0}р.", (odr.StoreObject as Order).debet);
         }
      }
   }
}

