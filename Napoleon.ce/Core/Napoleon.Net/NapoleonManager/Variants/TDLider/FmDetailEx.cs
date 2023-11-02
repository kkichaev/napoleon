using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Threading;
using System.IO;
using System.Windows.Forms;
using System.Runtime.InteropServices;
using GRSoft.NapoleonManager.Properties;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd); 

      static int count = 1;

      DataSet<int, Bonus> dsBonus;


      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         dsBonus = (DataSet<int, Bonus>)DataModule.Get(Bonus.BONUS_NAME)
            ?? new DataSet<int, Bonus>(Bonus.BONUS_NAME);
      }

      public class Data : GRSoft.Network.DataObject
      {
         public string userid = "";
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
      }

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;
      }

      protected override void DoClientCardReport()
      {
         const string REPORT_NAME = "clientcard";

         Data data = new Data();
         data.userid = GetSelectedIdAgent();
         data.start = dtpBegin.Value.Date;
         data.finish = dtpEnd.Value.Date;

         Result result = new Result();
         SimpleDataSet<Result> resultSet = new SimpleDataSet<Result>("Result", false);
         Report r = new Report(REPORT_NAME, data, resultSet);

         Thread th = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), r, FmWait.ProgressIndicator);
         FmWait.ShowForm(this, th);
         th.Join();
         FmWait.CloseForm();

         if (resultSet.Count > 0)
         {
            Result res = resultSet[0];
            if (res.file.Length > 0)
            {
               string fileName = Path.GetTempPath() + "\\" + REPORT_NAME + count.ToString() + ".xlsx";
               while (File.Exists(fileName))
               {
                  count++;
                  fileName = Path.GetTempPath() + "\\" + REPORT_NAME + count.ToString() + ".xlsx";
               }
               File.WriteAllBytes(fileName, res.file);
               ShellExecute(IntPtr.Zero, "open", fileName, "", "", 1);
            }
         }
         else
            MessageBox.Show("Ошибка построения отчета");

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
         return new BonusDetail();
      }

      class BonusDetail : ScriptDetail
      {
         protected override void LoadInt(FmDetailData cond, bool oneDay, bool checkRoute, string agentID, List<Org> routes)
         {
            base.LoadInt(cond, oneDay, checkRoute, agentID, routes);

            if (!((FmDetail)cond.fmDetail).IsScriptMode)
            {
               IDataSet cdata = DataModule.Get(Bonus.BONUS_NAME);
               CheckFiltersForDocType(cdata, ObjType.TObjType.Bonus, filtersAvailable);
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

   internal class BonusDoc : ScriptDocument
   {
      internal BonusDoc()
         : base("Bonus", "Заявка бонус", Resources.bonus_doc)
      {
      }
   }
}
