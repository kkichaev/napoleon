using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager 
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetailEx : FmDetail
   {
      private ToolStripItem cmiExcel;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         cmiExcel = cmDgvDetail.Items.Add("Excel", null, cmExcel_Click);
      }

      public void cmExcel_Click(object sender, EventArgs e)
      {

         OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;

         if (odr != null && odr.StoreObject is Returns)
         {
            Returns ret = (Returns)odr.StoreObject;

            if (ret != null)
            {
               ReturnExcel re = new ReturnExcel(ret);
               re.Visible = true;
            }
         }
      }
   }


   class ReturnExcel : Excel
   {
      public ReturnExcel(Returns retex)
      {
         SetValue(1, 2, "Уведомление о возврате");
         SetCellHorizontalAlign(1, 2, xlRight);
         SetValue(3, 1, string.Format("Дата: {0}", retex.Created.ToString("dd.MM.yyyy")));
         SetValue(4, 1, string.Format("Организация: {0}", retex.OrgName));
         SetValue(5, 1, string.Format("Адрес: {0}", retex.OrgAddr));

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

         //SetFontSize(GetRange(ROW_IDX + 2, 1, ROW_IDX + 2, 1), 8);
         //SetValue(ROW_IDX + 2, 1, "Заявка на возврат Продукции, подлежащей возврату, должна быть подана Покупателем  за 3(три) дня до окончания срока годности Продукции.");
         //SetFontSize(GetRange(ROW_IDX + 3, 1, ROW_IDX + 3, 1), 8);
         //SetValue(ROW_IDX + 3, 1, "Вся продукция, заявленная на возврат,  должна быть в замороженном виде (температура хранения не выше – 18С) . При нарушении данных условий Продукция возврату не подлежит.");

         //SetValue(ROW_IDX + 5, 4, "ОАО Новосибирская птицефабрика");
         //SetValue(ROW_IDX + 7, 4, "________________________");
         //SetValue(ROW_IDX + 8, 4, "/Лесик  С.П./");

         SetColumnWidth(1, 6);
         SetColumnWidth(2, 66);
         SetColumnWidth(5, 35);
      }
   }

}
