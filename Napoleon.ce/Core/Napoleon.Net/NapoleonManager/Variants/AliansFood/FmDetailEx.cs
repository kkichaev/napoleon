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
      private DataSet<int, ArchReturns> dsArchReturns;
      private DataSet<int, ArchSales> dsArchSales;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         dsArchReturns = (DataSet<int, ArchReturns>)DataModule.Get(ArchReturns.OBJECT_NAME) ??
            new DataSet<int, ArchReturns>(ArchReturns.OBJECT_NAME);

         dsArchSales = (DataSet<int, ArchSales>)DataModule.Get(ArchSales.OBJECT_NAME) ??
            new DataSet<int, ArchSales>(ArchSales.OBJECT_NAME);

         documents.Add(new DocumentInfo(dsArchReturns, ObjType.TObjType.ArchReturns));
         documents.Add(new DocumentInfo(dsArchSales, ObjType.TObjType.ArchSales));
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         string filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsArchReturns.Filter = filter;
         dsArchSales.Filter = filter;

         updSets.Add(dsArchReturns);
         updSets.Add(dsArchSales);
      }

      internal override System.Windows.Forms.Control RefreshDetail(OrderDetailRepresentation odr)
      {
         Control result = null;
         if (odr.Doctype.Val == ObjType.TObjType.ArchReturns)
         {
            result = dgvReturns;
            List<ReturnItem> returns = new List<ReturnItem>();
            returns.AddRange((odr.StoreObject as Returns).items);
            dgvReturns.DataSource = returns;
         }
         else if (odr.Doctype.Val == ObjType.TObjType.ArchSales)
         {
            result = dgvOrderItems;
            SetOrderItems(odr.StoreObject as Order);
         }

         return result;
      }
   }
}
