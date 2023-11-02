using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;
using System.Globalization;
using System.Collections;
using System.IO;
using System.Reflection;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Reports.Excel;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetailEx : FmDetail
   {
      DataSet<int, Bonus> dsBonus;
      public FmDetailEx(FmDetailData detailData) : base(detailData)
      {
         dsBonus = (DataSet<int, Bonus>)DataModule.Get(Bonus.BONUS_NAME) 
            ?? new DataSet<int, Bonus>(Bonus.BONUS_NAME);
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
            List<OrderItem> loi = new List<OrderItem>();
            loi.AddRange((odr.StoreObject as Order).items);
            dgvOrderItems.DataSource = loi;
            return dgvOrderItems;
         }

         return null;
      }

      internal override OrdersDetail CreateOrderDetail()
      {
         return new BonusDetail();
      }
   }

   class BonusDetail : ScriptDetail
   {
      protected override void LoadInt(FmDetailData cond, bool oneDay, bool checkRoute, string agentID, List<Org> routes)
      {
         base.LoadInt(cond, oneDay, checkRoute, agentID, routes);
         if (!((FmDetail)cond.fmDetail).IsScriptMode)
         {
            IDataSet cdata = DataModule.Get(Bonus.BONUS_NAME);
            CheckFiltersForDocType(cdata, ObjType.TObjType.DayDoc, filtersAvailable);
            if (cdata != null && cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.Bonus) : true)
            {
               foreach (Bonus doc in cdata.Data)
               {
                  Add(new OrderDetailRepresentation(doc.Created,
                     new ObjType(ObjType.TObjType.Bonus),
                     doc.Date, doc.Sended, doc.org, doc.DSum, 0, doc.Qty, doc, oneDay, doc.remark));
               }
            }
         }
      }
   }

   class Bonus : Order
   {
      public static readonly string BONUS_NAME = "Bonus";
   }
}