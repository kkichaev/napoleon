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
   public
   class FmDetailEx : FmDetail
   {
      public DataSet<int, Contract> dsContract;
      private DataSet<string, ReturnCause> dsReturnCause;
      private ToolStripItem cmiExcel;
      private DataSet<int, ReturnOnDelivery> dsReturnOnDelivery;

      SimpleDataSet<CMonitoring> dsMonitor;
      SimpleDataSet<Distrib> dsDistrib;

      DataSet<string, ContractDef> contractsDef;
      List<Control> created = new List<Control>();

#if NbtlMonitor
      string filterBase;
#endif

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         dsMonitor = (SimpleDataSet<CMonitoring>)DataModule.Get(CMonitoring.OBJECT_NAME) ??
            new SimpleDataSet<CMonitoring>(CMonitoring.OBJECT_NAME);

         dsContract = (DataSet<int, Contract>) DataModule.Get(Contract.OBJECT_NAME) ?? new DataSet<int, Contract>(Contract.OBJECT_NAME);

         contractsDef = (DataSet<string, ContractDef>)DataModule.Get(ContractDef.OBJECT_NAME) ?? new DataSet<string, ContractDef>(ContractDef.OBJECT_NAME);
         dsReturnOnDelivery = (DataSet<int, ReturnOnDelivery>)DataModule.Get(ReturnOnDelivery.OBJECT_NAME) ?? new DataSet<int, ReturnOnDelivery>(ReturnOnDelivery.OBJECT_NAME);

         dsDistrib = (SimpleDataSet<Distrib>)DataModule.Get(Distrib.OBJECT_NAME) ??
            new SimpleDataSet<Distrib>(Distrib.OBJECT_NAME);

         List<DocView> views = new List<DocView>(docViews);
         views.Add(new DocView(Contract.OBJECT_NAME, "Контракт", typeof(ContractOverview)));
         views.Add(new DocView(CMonitoring.OBJECT_NAME, "Мониторинг", typeof(FmCMonitor)));
         views.Add(new DocView(ReturnOnDelivery.OBJECT_NAME, "Возврат при поставке", typeof(ReturnOverview)));
         views.Add(new DocView(Distrib.OBJECT_NAME, "Дистрибуция", typeof(DistirbDetail)));

         docViews = views.ToArray();
         dgvDetailColumnSum.Visible = false;
         tsClienCard.Visible = false;
         //btnCoverArea.Visible = false;

         documents.Add(new DocumentInfo(dsContract, ObjType.TObjType.Contract));
         cmiExcel = cmDgvDetail.Items.Add("Excel", null, cmExcel_Click);
         cmDgvDetail.Items.Add("Еxcel (Форма 2)", null, CmExcelF2_Click);


         dsReturnCause = (DataSet<string, ReturnCause>)DataModule.Get(ReturnCause.OBJECT_NAME) ?? new DataSet<string, ReturnCause>(ReturnCause.OBJECT_NAME);

         LinkLabel ll = new LinkLabel();
         ll.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         ll.AutoSize = true;
         ll.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(0)))), ((int)(((byte)(0)))), ((int)(((byte)(192)))));
         ll.LinkColor = System.Drawing.Color.Blue;
         ll.Location = new System.Drawing.Point(Width - 120, 2);
         ll.Name = "lblAdress";
         ll.Size = new System.Drawing.Size(54, 14);
         ll.TabIndex = 12;
         ll.TabStop = true;
         ll.Text = "Планограмма";
         ll.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.OpenOrgPlanogram);

         panel2.Controls.Add(ll);

         documents.Add(new DocumentInfo(dsMonitor, ObjType.TObjType.CMonitoring));
         documents.Add(new DocumentInfo(dsReturnOnDelivery, ObjType.TObjType.ReturnOnDelivery));
         documents.Add(new DocumentInfo(dsDistrib, ObjType.TObjType.Distrib));

#if NbtlMonitor
         filterBase = COMMON_FILTER_STR;
         tbnMessage.Visible = false;
         tsReportMenu.Visible = false;
         dgvDetail.ContextMenuStrip = null;
         toolStripSeparator2.Visible = false;
         toolStripSeparator3.Visible = false;
         btnRoute.Visible = false;
         btnCoverArea.Visible = false;
#else
         if((CurrentUser.user as Manager).HaveRight(RightTokens.Get("CanManageContracts"), RightActions.Write))
         {
            ContextMenuStrip menu = new ContextMenuStrip();
            menu.Items.Add("Создать на основании", null, new EventHandler(delegate (object sender, EventArgs arg)
            {
               OrgTaskInfo info = new OrgTaskInfo();
               OrderDetailRepresentation o = (OrderDetailRepresentation)dgvDetail.CurrentRow.DataBoundItem;
               ScriptDoc script = o.StoreObject as ScriptDoc;

               if (script != null)
               {
                  FmCreateScript form = new FmCreateScript(script);
                  form.Show();
               }
            }));
            dgvDetail.ContextMenuStrip = menu;
         }
#endif
      }

      class ExcelF2Data : GRSoft.Network.DataObject
      {
         public DateTime created = DateTime.MinValue;
         public string userid = string.Empty;
      }

      private void CmExcelF2_Click(object sender, EventArgs e)
      {
         OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;

         if (odr != null && odr.StoreObject is Returns)
         {
            Returns ret = (Returns)odr.StoreObject;

            if (ret != null)
            {
               ExcelF2Data arg = new ExcelF2Data()
               {
                  created = ret.created,
                  userid = ret.userid
               };

               ReportResult.DoReport("return_report_f2", arg, this);
            }
         }
      }

      void OpenOrgPlanogram(object sender, LinkLabelLinkClickedEventArgs e)
      {
         if (dgvDetail.CurrentRow == null)
            return;

         OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;
         ScriptDoc sd = odr.StoreObject as ScriptDoc;
         if (sd != null && dsScriptDef.ContainsKey(sd.scriptId))
         {
            ScriptDef scrDef = dsScriptDef[sd.scriptId];
            if (contractsDef.ContainsKey(scrDef.cdefid))
               FmOrgPlanogram.ShowPlanogram(contractsDef[scrDef.cdefid], sd.Org);
         }
      }

      protected override void AdjustFilterForDS(string agentID, DateTime dateBegin, DateTime dateEnd)
      {
#if NbtlMonitor
         COMMON_FILTER_STR = "\"userid\"='{3}' and " + ((MainFormEx)MainForm.Instance).GetMonitorFilter(agentID);
         base.AdjustFilterForDS(agentID, dateBegin, dateEnd.AddDays(1));
#else
         base.AdjustFilterForDS(agentID, dateBegin, dateEnd);
#endif      
         dsVisit.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {

         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);

         string docFilter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
#if NbtlMonitor
         dsScriptDoc.Filter = String.Format(filterBase + " and " + ((MainFormEx)MainForm.Instance).ScriptFilter(""), "created", dateBegin, dateEnd, agentID);
#endif

         dsContract.Filter = docFilter;
         dsMonitor.Filter = docFilter;
         dsReturnOnDelivery.Filter = docFilter;
         dsDistrib.Filter = docFilter;

         updSets.Add(dsContract);
         updSets.Add(dsReturnCause);
         updSets.Add(dsMonitor);
         updSets.Add(dsReturnOnDelivery);
         updSets.Add(dsDistrib);

         if (contractsDef.Count == 0)
         {
            contractsDef.Filter = "not id is null";
            updSets.Add(contractsDef);
         }

         string filter = "not \"id\" is null";;
         if (updSets.Contains(dsPrice) == false || dsPrice.Filter != filter)
         {
            dsPrice.Filter = filter;
            if (updSets.Contains(dsPrice) == false)
               updSets.Add(dsPrice);
         }
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

         foreach (Control cc in created)
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
               created.Add(result);
            }

            if (result is DataObjectViewer)
               ((DataObjectViewer)result).SetData(odr.StoreObject);

            result.Visible = true;
         }
         
         return result;
      }

      protected override void UpdateDetailTable(DataGridViewRow curRow)
      {
         HideCreated();
         base.UpdateDetailTable(curRow);
      }

      private void HideCreated()
      {
         foreach (Control c in created)
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
         SetValue(ROW_IDX, 3, "Подпись Зам. Начальника отдела сбыта     _______________________/__________________");
         ROW_IDX++;
         ROW_IDX++;
         SetValue(ROW_IDX, 3, "Подпись Начальника отдела сбыта    _______________________/_______________");
         ROW_IDX++;
         ROW_IDX++;
         SetValue(ROW_IDX, 3, "Подпись Склада   _______________________/_______________");

         ROW_IDX += 3;
         int r = ROW_IDX;

         //if(dsReturnCause != null)
         //   foreach (ReturnCause rc in dsReturnCause.Data)
         //   {
         //      MergeCells(ROW_IDX, 3, ROW_IDX, 7);
         //      SetValue(ROW_IDX, 3, rc.Agent);
         //      MergeCells(ROW_IDX, 8, ROW_IDX, 17);
         //      SetValue(ROW_IDX, 8, rc.Report);
         //      ROW_IDX++;
         //   }

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
