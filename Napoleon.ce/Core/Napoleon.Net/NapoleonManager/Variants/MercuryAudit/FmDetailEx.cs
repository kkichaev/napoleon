using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      DataSet<int, OrgDistrib> dsOrgDistrib;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         dsOrgDistrib = (DataSet<int, OrgDistrib>)DataModule.Get(OrgDistrib.OBJECT_NAME) ?? new DataSet<int, OrgDistrib>(OrgDistrib.OBJECT_NAME);

         documents.Add(new DocumentInfo(dsOrgDistrib, ObjType.TObjType.OrgDistrib));

         List<DocView> docs = new List<DocView>(docViews);
         ObjType ot = new ObjType(ObjType.TObjType.OrgDistrib);
         docs.Add(new DocView(OrgDistrib.OBJECT_NAME, ot.ToString(), typeof(FmDistribView)));
         docViews = docs.ToArray();
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         String docFilter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsOrgDistrib.Filter = docFilter;

         updSets.Add(dsOrgDistrib);

         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
      }

      internal override OrdersDetail CreateOrderDetail()
      {
         return new ScriptDetail(documents);
      }

      private Control FindDetailControl(DocView dv)
      {
         Control result = null;

         foreach (Control cc in scBottom.Panel1.Controls)
            if (cc.Name.Equals(dv.viewer.Name))
            {
               result = cc;
               break;
            }

         return result;
      }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         Control result = null;

         DocView dv = GetDocView(Enum.GetName(typeof(ObjType.TObjType), odr.Doctype.Val));

         if (dv != null)
         {
            result = FindDetailControl(dv); ;

            if (result == null)
            {
               result = dv.MakeControl();
               detailPanel.Controls.Add(result);
               result.Dock = DockStyle.Fill;
            }

            if (result is DataObjectViewer)
               ((DataObjectViewer)result).SetData(odr.StoreObject);

            result.Visible = true;
         }

         return result;
      }
   }
}
