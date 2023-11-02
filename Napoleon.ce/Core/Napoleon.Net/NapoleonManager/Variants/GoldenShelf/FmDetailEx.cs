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
      public DataSet<int, Contract> dsContract;
      private DataSet<string, ReturnCause> dsReturnCause;
      private ToolStripItem cmiExcel;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         dsContract = (DataSet<int, Contract>) DataModule.Get(Contract.OBJECT_NAME) ?? new DataSet<int, Contract>(Contract.OBJECT_NAME);

         List<DocView> views = new List<DocView>(docViews);
         views.Add(new DocView(Contract.OBJECT_NAME, "Контракт", typeof(ContractOverview)));
         docViews = views.ToArray();
         dgvDetailColumnSum.Visible = false;
         tsClienCard.Visible = false;
         //btnCoverArea.Visible = false;

         documents.Add(new DocumentInfo(dsContract, ObjType.TObjType.Contract));
         cmiExcel = cmDgvDetail.Items.Add("Excel", null, cmExcel_Click);

         dsReturnCause = (DataSet<string, ReturnCause>)DataModule.Get(ReturnCause.OBJECT_NAME) ?? new DataSet<string, ReturnCause>(ReturnCause.OBJECT_NAME);
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         dsContract.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         updSets.Add(dsContract);
         updSets.Add(dsReturnCause);

         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
      }

      protected override void AfterRefreshData()
      {
         base.AfterRefreshData();
      }

      protected override void UpdateDetail(OrderDetailRepresentation odr)
      {
         Control c = RefreshDetail(odr);
         
         if(c != null)
            c.BringToFront();
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
               scBottom.Panel1.Controls.Add(result);
               result.Dock = DockStyle.Fill;
            }

            if (result is DataObjectViewer)
               ((DataObjectViewer)result).SetData(odr.StoreObject);

            result.Visible = true;
         }
         
         return result;
      }

      protected override void UpdateDetailTable(DataGridViewRow curRow)
      {
         HideContractView();
         base.UpdateDetailTable(curRow);
      }

      private void HideContractView()
      {
         string name = typeof(ContractOverview).Name;

         Control c = null;

         foreach (Control cc in scBottom.Panel1.Controls)
            if (cc.Name.Equals(name))
            {
               c = cc;
               break;
            }

         if (c != null)
            c.Visible = false;
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
         PageSetup(ActiveSheet, ORIENTATION_STR, xlLandscape);

         SetValue(3, 3, "Запрос на возврат");
         SetCellHorizontalAlign(3, 3, xlCenter);
         MergeCells(3, 3, 3, 17);

         MergeCells(4, 4, 4, 11);
         SetValue(4, 3, "Торговый предсавитель:");
         SetCellBoldFont(4, 3, true);
         SetCellItalicFont(4, 3, 4, 3, true);
         MergeCells(4, 4, 4, 11);
         SetValue(4, 4, retex.AgentName);
         SetCellBoldFont(4, 4, true);
         MergeCells(4, 12, 4, 14);
         MergeCells(4, 15, 4, 17);

         SetValue(5, 3, "Юридическое название:");
         SetCellBoldFont(5, 3, true);
         SetCellItalicFont(5, 3, 5, 3, true);
         MergeCells(5, 4, 5, 11);
         SetValue(5, 4, retex.OrgName);
         SetCellBoldFont(5, 4, true);
         MergeCells(5, 12, 5, 14);
         SetValue(5, 12, "Дата приема:");
         SetCellBoldFont(5, 12, true);
         SetCellItalicFont(5, 12, 5, 12, true);
         SetCellHorizontalAlign(5, 12, xlRight);
         MergeCells(5, 15, 5, 17);
         SetValue(5, 15, retex.Created.ToString("dd.MM.yyyy"));
         SetCellBoldFont(5, 15, true);

         MergeCells(6, 4, 6, 11);
         SetValue(6, 3, "Адрес:");
         SetCellBoldFont(6, 3, true);
         SetCellItalicFont(6, 3, 6, 3, true);
         SetValue(6, 4, retex.OrgAddr);
         SetCellBoldFont(6, 4, true);
         MergeCells(6, 12, 6, 14);
         SetWrapeText(6, 4, true);
         MergeCells(6, 15, 6, 17);

         SetBordersOnRange(3, 3, 6, 17, xlContinuous);

         MergeCells(8, 3, 8, 9);
         SetValue(8, 3, "Наименование продукции");
         SetCellHorizontalAlign(8,3 ,xlCenter);
         SetCellBoldFont(8, 3, true);
         SetValue(8, 10, "Ед. изм.");
         SetCellBoldFont(8, 10, true);
         SetCellHorizontalAlign(8, 10, xlCenter);
         MergeCells(8, 11, 8, 13);
         SetValue(8, 11, "Кол-во");
         SetCellHorizontalAlign(8, 11, xlCenter);
         SetCellBoldFont(8, 11, true);
         MergeCells(8, 14, 8, 15);
         SetValue(8, 14, "Дата выработки");
         SetCellHorizontalAlign(8, 14, xlCenter);
         SetCellBoldFont(8, 14, true);
         MergeCells(8, 16, 8, 17);
         SetValue(8, 16, "Причина возврата");
         SetCellBoldFont(8, 16, true);
         SetCellHorizontalAlign(8, 16, xlCenter);

         DataSet<string, ReturnCause> dsReturnCause = (DataSet<string, ReturnCause>)DataModule.Get(ReturnCause.OBJECT_NAME);
         int ROW_IDX = 9;
         foreach (ReturnItem item in retex.items)
         {
            SetValue(ROW_IDX, 3, item.Item);
            MergeCells(ROW_IDX, 3, ROW_IDX, 9);
            SetValue(ROW_IDX, 10, (item.flags & 1) == 1 ? "кг" : "шт");
            SetValue(ROW_IDX, 11, item.Qty);
            MergeCells(ROW_IDX, 11, ROW_IDX, 13);
            MergeCells(ROW_IDX, 14, ROW_IDX, 15);
            SetValue(ROW_IDX, 14, item.expdate.ToString("dd.MM.yyyy"));
            MergeCells(ROW_IDX, 16, ROW_IDX, 17);

            if(dsReturnCause != null && dsReturnCause.ContainsKey(item.causeid))
            {
               ReturnCause cause = dsReturnCause[item.causeid];

               StringBuilder sb = new StringBuilder();
               sb.Append(cause.report);

               if (!cause.NotPrint)
                  sb.Append("-").Append(cause.agent);

               SetWrapeText(ROW_IDX, 16, true);
               SetValue(ROW_IDX, 16, sb.ToString());
            }

            ROW_IDX++; 
         }

         SetBordersOnRange(8, 3, ROW_IDX - 1, 17, xlContinuous);
         ROW_IDX++;
         SetValue(ROW_IDX, 3, "Подпись ответственного лица в магазине _______________________/_____________");
         ROW_IDX++;
         ROW_IDX++;
         SetValue(ROW_IDX, 3, "Подпись торгового представителя     _______________________/__________________");
         ROW_IDX++;
         ROW_IDX++;
         SetValue(ROW_IDX, 3, "Подпись координатора отдела продаж    _______________________/_______________");
         ROW_IDX++;
         ROW_IDX++;
         SetValue(ROW_IDX, 3, "Начальник Отдела городских продаж   _______________________/_______________");

         ROW_IDX += 3;
         int r = ROW_IDX;

         if(dsReturnCause != null)
            foreach (ReturnCause rc in dsReturnCause.Data)
            {
               MergeCells(ROW_IDX, 3, ROW_IDX, 7);
               SetValue(ROW_IDX, 3, rc.Agent);
               MergeCells(ROW_IDX, 8, ROW_IDX, 17);
               SetValue(ROW_IDX, 8, rc.Report);
               ROW_IDX++;
            }

         if (r != ROW_IDX)
            SetBordersOnRange(r, 3, ROW_IDX - 1, 17, xlContinuous);

         SetColumnWidth(1, 1);
         SetColumnWidth(2, 1);
         SetColumnWidth(3, 23);
         SetColumnWidth(4, 6);
         SetColumnWidth(5, 6);
         SetColumnWidth(6, 6);
         SetColumnWidth(7, 6);
         SetColumnWidth(8, 6);
         SetColumnWidth(9, 6);
         SetColumnWidth(10, 7.50);
         SetColumnWidth(11, 3);
         SetColumnWidth(12, 3);
         SetColumnWidth(13, 3);
         SetColumnWidth(14, 7.50);
         SetColumnWidth(15, 6);
         SetColumnWidth(16, 9);
         SetColumnWidth(17, 18);
      }
   }

}
