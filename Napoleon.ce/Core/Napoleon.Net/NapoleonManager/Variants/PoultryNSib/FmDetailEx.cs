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
      ToolStripItem cmiExcel;
      ToolStripItem cmiForsakeReturn;
      private SimpleDataSet<ReturnsEx> dsReturnEx = new SimpleDataSet<ReturnsEx>(ReturnsEx.OBJECT_NAME, false);

      public FmDetailEx(FmDetailData detailData) : base(detailData)
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
   }

   class ReturnExcel : Excel
   {
      public ReturnExcel(ReturnsEx retex)
      {
         SetValue(1, 2, "Уведомление о возврате");
         SetCellHorizontalAlign(1, 2, xlRight);
         SetValue(3, 1, string.Format("Дата: {0}", retex.Created.ToString("dd.MM.yyyy")));
         SetValue(4, 1, string.Format("Грузоотправитель: {0}", retex.OrgName));
         SetValue(5, 1, "Грузополучатель: ОАО Новосибирская птицефабрика,633220 ,РФ,Искитимский район, ст. Евсино");

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
            SetWrapeText(ROW_IDX, 2, true);
            SetValue(ROW_IDX, 3, (item.flags & 1) == 1 ? "кг" : "шт");
            SetValue(ROW_IDX, 4, item.Qty);
            SetValue(ROW_IDX, 5, item.comment);
            SetWrapeText(ROW_IDX, 5, true);

            ROW_IDX++; pos++;
         }

         SetBordersOnRange(8, 1, ROW_IDX - 1, 5, xlContinuous);

         SetFontSize(GetRange(ROW_IDX + 2, 1, ROW_IDX + 2, 1),8);
         SetValue(ROW_IDX + 2, 1, "Заявка на возврат Продукции, подлежащей возврату, должна быть подана Покупателем  за 3(три) дня до окончания срока годности Продукции.");
         SetFontSize(GetRange(ROW_IDX + 3, 1, ROW_IDX + 3, 1), 8);
         SetValue(ROW_IDX + 3, 1, "Вся продукция, заявленная на возврат,  должна быть в замороженном виде (температура хранения не выше – 18С) . При нарушении данных условий Продукция возврату не подлежит.");

         SetValue(ROW_IDX + 5, 4, "ОАО Новосибирская птицефабрика");
         SetValue(ROW_IDX + 7, 4, "________________________");
         SetValue(ROW_IDX + 8, 4, "/Лесик  С.П./");

         SetColumnWidth(1, 6);
         SetColumnWidth(2, 66);
         SetColumnWidth(5, 35);
      }
   }
}