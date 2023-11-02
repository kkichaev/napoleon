using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public 
   class FmDetailEx : FmDetail
   {
      private DataSet<int, InvAudit> dsInvAudit;
      private InvAuditCtrl ctrl;
      private DocView iadv;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         dsInvAudit = (DataSet<int, InvAudit>)DataModule.Get(InvAudit.OBJECT_NAME) ?? new DataSet<int, InvAudit>(InvAudit.OBJECT_NAME);
         documents.Add(new DocumentInfo(dsInvAudit, ObjType.TObjType.InvAudit));

         ctrl = new InvAuditCtrl();
         ctrl.Dock = System.Windows.Forms.DockStyle.Fill;
         detailPanel.Controls.Add(ctrl);
         iadv = new DocView(InvAudit.OBJECT_NAME, "Аудит оборудования", typeof(InvAuditCtrl));
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
         string filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsInvAudit.Filter = filter;

         updSets.Add(dsInvAudit);
      }

      internal override System.Windows.Forms.Control RefreshDetail(GRSoft.NapoleonManager.OrderDetailRepresentation odr)
      {
         if (odr.Doctype.Val == ObjType.TObjType.InvAudit)
         {
            InvAudit p = odr.StoreObject as InvAudit;

            if (p != null)
               ctrl.InitDataSet(p.items);

            return ctrl;
         }
         else return null;
      }

      protected override FmDetail.DocView GetDocView(string docType)
      {
         if (docType.Equals(InvAudit.OBJECT_NAME))
            return iadv;

         return base.GetDocView(docType);
      }

   }
}
