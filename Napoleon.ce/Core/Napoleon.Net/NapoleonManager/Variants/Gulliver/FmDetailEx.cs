using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.Drawing;

namespace GRSoft.NapoleonManager
{
[System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public  
   class FmDetailEx : FmDetail
   {
      private DataSet<int, ArchSales> dsArchSales;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         dsArchSales = (DataSet<int, ArchSales>)DataModule.Get(ArchSales.OBJECT_NAME) ??
            new DataSet<int, ArchSales>(ArchSales.OBJECT_NAME);

         documents.Add(new DocumentInfo(dsArchSales, ObjType.TObjType.ArchSales));
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);

         string filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsArchSales.Filter = filter;

         updSets.Add(dsArchSales);
      }

      internal override System.Windows.Forms.Control RefreshDetail(OrderDetailRepresentation odr)
      {
         Control result = null;
         if (odr.Doctype.Val == ObjType.TObjType.ArchSales)
         {
            result = dgvOrderItems;
            SetOrderItems(odr.StoreObject as Order);
         }

         return result;
      }

      protected override void CellFormatting(DataGridViewCellFormattingEventArgs e)
      {
         base.CellFormatting(e);

         GRSoft.Network.DataObject dataObject = (dgvDetail.Rows[e.RowIndex].DataBoundItem as OrderDetailRepresentation).StoreObject;

         if (dataObject is Incass)
            e.CellStyle.ForeColor = Color.Blue;
      }
   }
}
