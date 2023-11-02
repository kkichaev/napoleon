using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Threading;
using System.IO;
using System.Windows.Forms;
using System.Runtime.InteropServices;
using GRSoft.NapoleonManager.Properties;
using System.ComponentModel;
using GRSoft.NapoleonManager.Reports.Excel;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd);
      ToolStripItem cmiExcel;
      ToolStripItem cmiForsakeReturn;
      private SimpleDataSet<ReturnsEx> dsReturnEx = new SimpleDataSet<ReturnsEx>(ReturnsEx.OBJECT_NAME, false);


      static int count = 1;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         cmiExcel = cmDgvDetail.Items.Add("Excel", null, cmExcel_Click);
         cmiForsakeReturn = cmDgvDetail.Items.Add("Отметить незабран", null, cmiForsakeReturn_Click);
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
         dsReturnEx.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         updSets.Add(dsReturnEx);
      }

      protected override void cmDgvDetail_Opening(object sender, CancelEventArgs e)
      {
         base.cmDgvDetail_Opening(sender, e);

         OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;

         setControlVisible(odr != null && odr.StoreObject is Returns);
      }

      private void setControlVisible(bool value)
      {
         cmiExcel.Visible = value;
         cmiForsakeReturn.Visible = value;
      }

      public void cmExcel_Click(object sender, EventArgs e)
      {

         OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;

         if (odr != null && odr.StoreObject is Returns)
         {
            Returns ret = (Returns)odr.StoreObject;
            ReturnsEx retex = null;

            foreach (ReturnsEx re in dsReturnEx.Data)
               if (re.created == ret.created)
               {
                  retex = re;
                  break;
               }

            if (retex != null)
            {
               ReturnExcel re = new ReturnExcel(retex);
               re.Visible = true;
            }
            else
               MessageBox.Show("Невозможно найти перегруженную накладную для отчета");
         }
      }

      public void cmiForsakeReturn_Click(object sender, EventArgs e)
      {
         try
         {
            OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;

            if (odr != null && odr.StoreObject is Returns)
            {
               Returns ret = (Returns)odr.StoreObject;
               ReturnsEx retex = new ReturnsEx();

               FieldInfo[] fields = typeof(Returns).GetFields(BindingFlags.Instance | BindingFlags.Public);

               foreach (FieldInfo f in fields)
               {
                  object val = f.GetValue(ret);
                  retex.GetType().GetField(f.Name, BindingFlags.Instance | BindingFlags.Public).SetValue(retex, val);
               }

               retex.forsake = 1;

               SimpleDataSet<ReturnsEx> fixDS = new SimpleDataSet<ReturnsEx>(ReturnsEx.OBJECT_NAME, false);
               fixDS.Add(retex);

               List<IDataSet> update = new List<IDataSet>();
               update.Add(fixDS);
               Config cfg = Config.GetConfig();

               if (DataModule.UpdateDataSet(update, null, null, cfg.GetConnection(), GetSelectedIdAgent()))
                  MessageBox.Show("Операция завершена успешно", "Информация", MessageBoxButtons.OK,
                     MessageBoxIcon.Information);
               else
                  MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
                     MessageBoxIcon.Error);
            }
         }
         catch (Exception exc)
         {
            MessageBox.Show(exc.Message);
         }
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
   }

   class ReturnExcel : Excel
   {
      public ReturnExcel(ReturnsEx retex)
      {
         SetValue(1, 2, "Уведомление о возврате");
         SetCellHorizontalAlign(1, 2, xlRight);
         SetValue(3, 1, string.Format("Дата: {0}", retex.Created.ToString("dd.MM.yyyy")));
         SetValue(4, 1, string.Format("Грузоотправитель: {0}", retex.OrgName));
         SetValue(5, 1, "Грузополучатель: ООО ТД Мясной Альянс");

         int pos = 1;

         SetCellHorizontalAlign(8, 1, xlCenter);
         SetValue(8, 1, "Поз.");
         SetCellHorizontalAlign(8, 2, xlCenter);
         SetValue(8, 2, "Наименование");
         SetCellHorizontalAlign(8, 3, xlCenter);
         SetValue(8, 3, "Ед изм. уп.");
         SetCellHorizontalAlign(8, 4, xlCenter);
         SetWrapeText(8, 3, true);
         SetValue(8, 4, "Кол-во");
         SetCellHorizontalAlign(8, 5, xlCenter);
         SetValue(8, 5, "Комментарии");


         int ROW_IDX = 9;
         foreach (ReturnItem item in retex.items)
         {
            SetValue(ROW_IDX, 1, pos.ToString());
            SetValue(ROW_IDX, 2, item.Item);
            SetValue(ROW_IDX, 3, (item.flags & 1) == 1 ? "кг" : "шт");
            SetValue(ROW_IDX, 4, item.Qty);
            SetValue(ROW_IDX, 5, item.comment);
            SetWrapeText(ROW_IDX, 5, true);

            ROW_IDX++; pos++;
         }

         SetBordersOnRange(8, 1, ROW_IDX - 1, 5, xlContinuous);

         SetFontSize(GetRange(ROW_IDX + 2, 1, ROW_IDX + 2, 1), 8);
         SetValue(ROW_IDX + 2, 1, "Заявка на возврат Продукции, подлежащей возврату, должна быть подана Покупателем  за 3(три) дня до окончания срока годности Продукции.");
         SetFontSize(GetRange(ROW_IDX + 3, 1, ROW_IDX + 3, 1), 8);
         SetValue(ROW_IDX + 3, 1, "Вся продукция, заявленная на возврат,  должна быть в замороженном виде (температура хранения не выше – 18С) . При нарушении данных условий Продукция возврату не подлежит.");

         SetValue(ROW_IDX + 5, 4, "ООО ТД Мясной Альянс");
         SetValue(ROW_IDX + 7, 4, "________________________");
         SetValue(ROW_IDX + 8, 4, "/Рау В.А./");

         SetColumnWidth(1, 6);
         SetColumnWidth(2, 66);
         SetColumnWidth(5, 35);
      }
   }
}
