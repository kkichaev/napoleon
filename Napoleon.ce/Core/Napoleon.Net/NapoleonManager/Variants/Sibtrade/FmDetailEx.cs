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
using GRSoft.NapoleonManager.Properties;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetailEx : FmDetail
   {
      SimpleDataSet<Bonus> dsBonus = new SimpleDataSet<Bonus>(Bonus.OBJECT_NAME, false);
      public FmDetailEx(FmDetailData detailData) : base(detailData)
      {
         documents.Add(new DocumentInfo(dsBonus, ObjType.TObjType.Bonus));
         
         DataGridViewTextBoxColumn retCause = new DataGridViewTextBoxColumn();
         retCause.Width = 150;
         retCause.FillWeight = 150;
         retCause.DataPropertyName = "Cause";
         retCause.HeaderText = "Примечание";
         retCause.Name = "Cause";

         dgvReturns.Columns.Add(retCause);
      }
      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);

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
         return new ScriptDetail(documents);
      }
   }

   public class Bonus : Order
   {
      public static new readonly string OBJECT_NAME = "Bonus";
   }

   internal class BonusDoc : ScriptDocument
   {
      internal BonusDoc()
         : base("Bonus", "Заявка бонус", Resources.bonus_doc)
      {
      }
   }
}