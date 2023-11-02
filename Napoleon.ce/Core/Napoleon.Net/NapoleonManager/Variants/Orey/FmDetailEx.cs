using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.Reflection;
using System.Collections;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      SimpleDataSet<Bonus> dsBonus = new SimpleDataSet<Bonus>(Bonus.OBJ_NAME);

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         documents.Add(new DocumentInfo(dsBonus, ObjType.TObjType.Bonus));
      }


      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         dsBonus.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         updSets.Add(dsBonus);
      }


      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         if (odr.Doctype.Val == ObjType.TObjType.Bonus)
         {
            dgvOrderItems.DataSource =  (odr.StoreObject as Bonus).items;
            return dgvOrderItems;
         }

         return null; 
      }

      protected override IDataSet GetDuplicate(GRSoft.Network.DataObject dataObject)
      {
         if (dataObject is Bonus)
         {
            DataSet<int, Bonus> ord = new DataSet<int, Bonus>(Bonus.OBJECT_NAME, false, true);
            ord.Add(ord.Count, (Bonus)dataObject);

            return ord;
         }
         return null;
      }
   }
}
