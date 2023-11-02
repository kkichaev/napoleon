using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      private DataSet<int, Procuration> dsProcuration;
      private ProcurationView prcView;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         dsProcuration = (DataSet<int, Procuration>)DataModule.Get(Procuration.OBJECT_NAME) ??
            new DataSet<int, Procuration>(Procuration.OBJECT_NAME);

         documents.Add(new DocumentInfo(dsProcuration, ObjType.TObjType.Procuration));
         prcView = new ProcurationView();
         prcView.Dock = System.Windows.Forms.DockStyle.Fill;
         detailPanel.Controls.Add(prcView);
      }


      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         string filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsProcuration.Filter = filter;

         updSets.Add(dsProcuration);
      }

      internal override System.Windows.Forms.Control RefreshDetail(GRSoft.NapoleonManager.OrderDetailRepresentation odr)
      {
         if (odr.Doctype.Val == ObjType.TObjType.Procuration)
         {
            Procuration p = odr.StoreObject as Procuration;

            if (p != null)
            {
               prcView.lblRoute.Text = p.route;
               prcView.lblQty.Text = p.qty.ToString();
               prcView.lblRemark.Text = p.remark;
               prcView.lblFIO.Text = p.fio;
               prcView.lblProcurationBy.Text = p.parent;
            }

            return prcView;
         }
         else return null;
      }
   }

   
}
