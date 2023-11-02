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
      private DataSet<int, Bonus> dsBonus;
      SimpleDataSet<Claim> dsClaim;

      public FmDetailEx(FmDetailData detailData)
         : base(detailData)
      {
         dsBonus = (DataSet<int, Bonus>)DataModule.Get(Bonus.OBJECT_NAME) ?? new DataSet<int, Bonus>(Bonus.OBJECT_NAME);
         dsClaim = new SimpleDataSet<Claim>(Claim.OBJECT_NAME, false);

         documents.Add(new DocumentInfo(dsBonus, ObjType.TObjType.Bonus));
         documents.Add(new DocumentInfo(dsClaim, ObjType.TObjType.Claim));

         List<DocView> views = new List<DocView>(docViews);
         views.Add(new DocView(Bonus.OBJECT_NAME, "Бонусный заказ", typeof(OrderOverview)));
         docViews = views.ToArray();
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);

         dsBonus.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsClaim.Filter = dsBonus.Filter;

         updSets.Add(dsBonus);
         updSets.Add(dsClaim);
      }

      internal override System.Windows.Forms.Control RefreshDetail(OrderDetailRepresentation odr)
      {
         Control result = null;

         if (odr.Doctype.Val == ObjType.TObjType.Bonus)
         {
            SetOrderItems(odr.StoreObject as Order);
            result = dgvOrderItems;
         }
         else if (odr.Doctype.Val == ObjType.TObjType.Claim)
         {
            tbVisitText.Text = ((odr.StoreObject as Claim).remark);
            result = tbVisitText;
         }

         return result;
      }
   }
}
