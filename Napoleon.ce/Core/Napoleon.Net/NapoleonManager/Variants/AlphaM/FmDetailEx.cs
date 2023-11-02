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
      SimpleDataSet<WhRequest> dsWhReq;


      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         dsBonus = (DataSet<int, Bonus>)DataModule.Get(Bonus.BONUS_NAME)
            ?? new DataSet<int, Bonus>(Bonus.BONUS_NAME);

         dsWhReq = (SimpleDataSet<WhRequest>)DataModule.Get(WhRequest.OBJ_NAME)
            ?? new SimpleDataSet<WhRequest>(WhRequest.OBJ_NAME);

         List<DocView> views = new List<DocView>(docViews);
         views.Add(new DocView(WhRequest.OBJ_NAME, "Заявка на склад", typeof(OrderOverview)));

         docViews = views.ToArray();


         documents.Add(new DocumentInfo(dsWhReq, ObjType.TObjType.WhRequest));
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

         string filter = string.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsBonus.Filter = filter;
         dsWhReq.Filter = filter;
         updSets.Add(dsBonus);
         updSets.Add(dsWhReq);
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
         if (odr.Doctype.Val == ObjType.TObjType.Bonus)
         {
            List<OrderItem> loi = new List<OrderItem>();
            loi.AddRange((odr.StoreObject as Order).items);
            dgvOrderItems.DataSource = loi;
            return dgvOrderItems;
         }
         if (odr.Doctype.Val == ObjType.TObjType.WhRequest)
         {
            SetOrderItems(odr.StoreObject as Order);
            return dgvOrderItems;
         }

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

      internal override OrdersDetail CreateOrderDetail()
      {
         return new BonusDetail(documents);
      }

      class BonusDetail : ScriptDetail
      {
         public BonusDetail(List<DocumentInfo> documents) : base(documents) { }
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
