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
using System.Net;


namespace GRSoft.NapoleonManager
{
   [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public partial class FmDetailBase : Form
   {
      public static FmDetailBase Instance;

      public string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" <= ToDate('{2:dd/MM/yyyy} 23:59:59') and \"userid\"='{3}'";

      //Необходимые объекты данных для формы
      protected DataSet<string, Price> dsPrice;// = new DataSet<string, Price>("Price");
      protected DataSet<string, Price> dsAgentPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);
      protected DataSet<string, Org> dsOrg;// = new DataSet<string, Org>("Org");
      protected DataSet<int, Order> dsOrder;// = new DataSet<int, Order>("Order");
      protected DataSet<int, OrderW> dsOrderW;
      protected DataSet<int, Visit> dsVisit;
      protected DataSet<int, Incass> dsIncass;
      protected DataSet<int, OrgRemnants> dsOrgRemnants;
      protected DataSet<int, PKO> dsPKO;
      protected DataSet<int, Returns> dsReturns;
      protected SimpleDataSet<MoneyProxy> dsMoneyProxy;

      //protected DataSet<int, UserLog> dsUserLog;
      //protected DataSet<int, ScriptDoc> dsScriptDoc;
      protected DataSet<int, DayDoc> dsDayDoc;
      protected DataSet<int, OrgFolder> dsOrgFolder;

      protected List<DocumentInfo> documents = new List<DocumentInfo>();

      protected Dictionary<DateTime, bool> documetsCompleted = new Dictionary<DateTime, bool>();
      protected DataSet<int, CommonConfig> dsConfig;
      protected DataSet<string, PotenzialOrg> dsPtnzOrg;
      private DataSet<int, Sales> dsSales;
      protected DataSet<int, OrderCommitted> dsOrderCommitted;
      private ToolTip tooltipRemark;
      private string curUserID = string.Empty;
      protected ToolStripMenuItem tsFocused;
      protected string assignedHtml = "";
      protected bool refreshing = false;
      protected OrgLocations dsOrgLocation = OrgLocations.GetDataSet();

      public static Dictionary<string, AgentRouteSheduleHelper> routeHelpers = new Dictionary<string, AgentRouteSheduleHelper>();

      protected MoneyProxyDetail mpdetail = new MoneyProxyDetail();

      private DataSet<int, PicStore> dsPicStore;
      private Dictionary<string, PicStore> picMap = new Dictionary<string, PicStore>();

#if ORDER_CHARGE
      SimpleDataSet<OrderCharge> dsOrderCharges;
#endif

#if AliansFood
      SimpleDataSet<ArchIncass> dsArchIncass = new SimpleDataSet<ArchIncass>(ArchIncass.ARCH_INCASS_NAME);
#endif

#if DISTR_DOC
      private DataSet<int, Distr> dsDistrDoc;
#endif

#if INVOICE_DOC
      private DataSet<int, Invoice> dsInvoice;
#endif

#if MOVEMENT_DOC
      private DataSet<int, MoveDoc> dsMove;
      private DataSet<string, Sklad> dsSklad;
#endif
      protected DataGridView dgvAnswerItems = new System.Windows.Forms.DataGridView();
      private DataGridViewTextBoxColumn dgvAnswerItemsId = new System.Windows.Forms.DataGridViewTextBoxColumn();
      private DataGridViewTextBoxColumn dgvAnswerItemsAnswer = new System.Windows.Forms.DataGridViewTextBoxColumn();
#if QUESTION
      private DataSet<string, PotenzialOrg> dsPotenzailOrg;
      private DataSet<int, Answer> dsAnswer;
      private DataSet<string, Question> dsQuestion;
#endif


#if PRICE_MONITORING
      protected DataSet<int, Monitoring> dsMonitoring;
      protected DataSet<string, MonitoringItem> dsItems;
#endif

      protected List<Org> routes;
      /// Набор отображаемых данных в таблице
      protected OrdersDetail oDetail;

      //Декоратор формы
      private IDecorator decorator;
      protected Config config;

      Font itemsBoldFont = null;

#if VISIT_LITE
      Dictionary<Visit, List<Image>> largePhotos = new Dictionary<Visit, List<Image>>();
#endif

      private SimpleDataSet<GPSGather> dsGather;

      public void __Initing(FmDetailData detailData)
      {
         this.dgvDetail.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvDetail_CellFormatting);
         this.dgvDetail.ColumnHeaderMouseClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.dgvDetail_ColumnHeaderMouseClick);
         this.dgvDetail.DataError += new System.Windows.Forms.DataGridViewDataErrorEventHandler(this.dgvDetail_DataError);
         this.dgvDetail.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvDetail_RowEnter);
         this.dgvDetail.SelectionChanged += new System.EventHandler(this.dgvDetail_SelectionChanged);
         this.dgvDetail.MouseDown += new System.Windows.Forms.MouseEventHandler(this.dgvDetail_MouseDown);
         this.cmDgvDetail.Opening += new System.ComponentModel.CancelEventHandler(this.cmDgvDetail_Opening);
         this.miMakeDup.Click += new System.EventHandler(this.miMakeDup_Click);
         this.dgvOrderItems.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvOrderItems_CellFormatting);
         this.lblAdress.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.lblAdress_LinkClicked);
         this.lbNotes.Click += new System.EventHandler(this.lbNotes_Click);
         this.lbNotes.MouseLeave += new System.EventHandler(this.lbNotes_MouseLeave);
         this.lvPhoto.DoubleClick += new System.EventHandler(this.lvPhoto_DoubleClick);
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click_1);
         this.btnCoverArea.Click += new System.EventHandler(this.btnCoverArea_Click);
         this.btnRoute.Click += new System.EventHandler(this.btnRoute_Click);
         this.tbnMessage.Click += new System.EventHandler(this.tbnMessage_Click);
         this.tsbMakeHtml.Click += new System.EventHandler(this.tsbMakeHtml_Click);
         this.tsClienCard.Click += new System.EventHandler(this.tsClienCard_Click);
         this.tbPhotoRate.Click += new System.EventHandler(this.tbPhotoRate_Click);
         this.cbAgents.DrawItem += new System.Windows.Forms.DrawItemEventHandler(this.cbAgents_DrawItem);
         this.cbAgents.MeasureItem += new System.Windows.Forms.MeasureItemEventHandler(this.cbAgents_MeasureItem);
         this.cbFilter.DrawItem += new System.Windows.Forms.DrawItemEventHandler(this.cbFilter_DrawItem);
         this.cbFilter.MeasureItem += new System.Windows.Forms.MeasureItemEventHandler(this.cbFilter_MeasureItem);
         this.cbFilter.SelectedIndexChanged += new System.EventHandler(this.cbFilter_SelectedIndexChanged);
         this.cmPhotoRating.ItemClicked += new System.Windows.Forms.ToolStripItemClickedEventHandler(this.cmPhotoRating_ItemClicked);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmDetail_FormClosed);
         this.Shown += new System.EventHandler(this.FmDetail_Shown);

         Instance = this;

         tooltipRemark = new ToolTip();
         tooltipRemark.ToolTipIcon = ToolTipIcon.Info;
         tooltipRemark.IsBalloon = true;
         tooltipRemark.ShowAlways = true;

#if SNAPSHOT_RATING
         lvPhoto.ContextMenuStrip = cmPhotoRating;
         tbPhotoRate.Visible = true;
#endif
         oDetail = CreateOrderDetail();

         decorator = DecoratorFactory.GetDecorator(this);

#if FOCUSED_GROUP || FOCUSED_ITEMS
         tsFocused = new ToolStripMenuItem();
         tsFocused.Name = "tsFocused";
         tsFocused.Size = new System.Drawing.Size(161, 22);
         tsFocused.Text = "Фокусный товар";
         tsFocused.Click += new System.EventHandler((o, e) => { FocusReport_Click(); });

         tsReportMenu.DropDownItems.Add(tsFocused);
#endif

#if QUESTION
         dsAnswer = (DataSet<int, Answer>)DataModule.Get(Answer.OBJECT_NAME) ??
            new DataSet<int, Answer>(Answer.OBJECT_NAME);
         dsQuestion = (DataSet<string, Question>)DataModule.Get(Question.OBJECT_NAME) ??
            new DataSet<string, Question>(Question.OBJECT_NAME);
         dsQuestion.Filter = "\"idquest\" is null or \"idquest\" is not null";

         dsPotenzailOrg = new DataSet<string, PotenzialOrg>(PotenzialOrg.OBJECT_NAME, true);
         dsPotenzailOrg.Filter = "\"id\" is null or \"id\" is not null";

         dgvAnswerItems.AllowUserToAddRows = false;
         dgvAnswerItems.AllowUserToDeleteRows = false;
         dgvAnswerItems.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top
                     | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         dgvAnswerItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         dgvAnswerItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            dgvAnswerItemsId,
            dgvAnswerItemsAnswer});
         dgvAnswerItems.Location = new System.Drawing.Point(0, 0);
         dgvAnswerItems.Margin = new System.Windows.Forms.Padding(3, 4, 3, 4);
         dgvAnswerItems.Name = "dgvOrderItems";
         dgvAnswerItems.RowHeadersVisible = false;
         dgvAnswerItems.Dock = DockStyle.Fill;
         dgvAnswerItems.TabIndex = 10;
         dgvAnswerItems.CellFormatting += new DataGridViewCellFormattingEventHandler(dgvAnswerItems_CellFormatting);
         dgvAnswerItemsId.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         dgvAnswerItemsId.DataPropertyName = "Id";
         dgvAnswerItemsId.FillWeight = 100F;
         dgvAnswerItemsId.HeaderText = "Вопрос";
         dgvAnswerItemsId.Name = "dgvAnswerItemsId";

         dgvAnswerItemsAnswer.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         dgvAnswerItemsAnswer.DataPropertyName = "Answer";
         dgvAnswerItemsAnswer.FillWeight = 100F;
         dgvAnswerItemsAnswer.HeaderText = "Ответ";
         dgvAnswerItemsAnswer.Name = "dgvAnswerItemsAnswer";

         detailPanel.Controls.Add(dgvAnswerItems);
         //dgvDetailColumnSum.Visible = false;
#endif
#if ORG_HAVE_GPS_LOCATION
         DataGridViewCheckBoxColumn dgvDetailHacLos = new DataGridViewCheckBoxColumn();
         dgvDetailHacLos.DisplayIndex = 2;
         dgvDetailHacLos.HeaderText = "GPS";
         dgvDetailHacLos.Name = "dgvDetailHacLos";
         dgvDetailHacLos.Width = 35;

         dgvDetail.Columns.Add(dgvDetailHacLos);
#endif
#if BTL
         tsReportMenu.Visible = false;
#endif

#if PRICE_MONITORING
         ToolStripItem tsi = new ToolStripMenuItem();
         tsi.Name = "tsbMakeMonitor";
         tsi.Size = new System.Drawing.Size(152, 22);
         tsi.Text = "Мониторинг";
         tsi.Click += new EventHandler(tsi_MonitoringClick);

         tsReportMenu.DropDownItems.Add(tsi);
#endif
#if START_STOP
         ToolStripMenuItem wtReport = new ToolStripMenuItem();
         wtReport.Text = "Рабочее время";
         wtReport.Click += new System.EventHandler((o, e) => { WorkTimeReport.Do(GetDateForStartPeriod(), GetDateForEndPeriod(), this); });

         tsReportMenu.DropDownItems.Add(wtReport);
#endif

#if HTTP_SERVER
         lvPhoto.Visible = false;
         wbPhoto.Visible = true;
         object[] atts = this.GetType().GetCustomAttributes(false);
         wbPhoto.ObjectForScripting = this;
#endif

#if STL_TIME_COLUMN
         dgvDetailColumnStlTime.Visible = true;
#else
         dgvDetailColumnStlTime.Visible = false;
#endif
         detailPanel.Controls.Add(mpdetail);
         mpdetail.Visible = true;
         mpdetail.Dock = DockStyle.Fill;

         config = Config.GetConfig();
         InitDataSets(detailData);
         AdjustForm(detailData);
      }

      protected virtual void FocusReport_Click()
      {
         string url = FocusReport.MakeReport(GetSelectedAgent(), dsOrder.Data, dsPrice);
         if (url != null && url.Length > 0)
            OpenLink.NewWindow(url);
      }

      internal virtual OrdersDetail CreateOrderDetail() { return new OrdersDetail(documents); }

      // Обновить наборы данных
      // Возвращает дату для конечной границы выборки выборки
      public DateTime GetDateForEndPeriod()
      {
         return dtpEnd.Value.Date.AddDays(1);
      }

      public DateTime GetDateForStartPeriod()
      {
         return dtpBegin.Value.Date;
      }


      void tsi_MonitoringClick(object sender, EventArgs e)
      {
         MonitoringReports.Do(GetDateForStartPeriod(), GetDateForEndPeriod().AddDays(-1), GetSelectedIdAgent(), this);
      }

      /// Настроить форму
      private void AdjustForm(FmDetailData data)
      {
         cbFilter.Items.Add("Все");
         SetRelatedGridTitle();

         foreach (ObjType.TObjType ot in Enum.GetValues(typeof(ObjType.TObjType)))
         {
            cbFilter.Items.Add(new ObjType(ot));
         }

         //cbFilter.Sorted = true;
         cbFilter.SelectedIndex = 0;
         dgvDetail.AutoGenerateColumns = false;
         dgvOrderItems.AutoGenerateColumns = false;
         dgvMoveItem.AutoGenerateColumns = false;
         dgvRemnantsItems.AutoGenerateColumns = false;

         Agent selAgent = null;
         Manager mc = CurrentUser.user as Manager;
         if (mc != null)
         {
            List<Agent> al = new List<Agent>();
            foreach (Division.DivisionAgent da in mc.Division.GetAllAgents())
            {
               if (da.agent == null)
                  continue;

               al.Add(da.agent);
               if (data.AgentId == da.agent.id)
                  selAgent = da.agent;
            }

            al.Sort();
            al.ForEach(x => cbAgents.Items.Add(x));
         }

         lblAdress.Text = string.Empty;
         cbAgents.SelectedItem = selAgent;
         dtpBegin.Value = data.DateBegin;
         dtpEnd.Value = data.DateEnd;
         tsslSum.Text = string.Empty;
         tsslCount.Text = string.Empty;

         decorator.AdjustForm();
      }

#if QUESTION
      void dgvAnswerItems_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         if (e.ColumnIndex == 1)
         {
            DataGridViewRow row = dgvAnswerItems.Rows[e.RowIndex];
            Agent agent = GetSelectedAgent();

            if (row != null && agent != null)
            {
               AnswerItem answerItem = row.DataBoundItem as AnswerItem;

               if (answerItem != null &&
                  answerItem.type == QuestionItem.DATASET)
               {
                  if (answerItem.remark.Equals("Прайс"))
                  {
                     DataSet<string, Price> src = (dsPrice != null && dsPrice.Count > 0) ? dsPrice : dsAgentPrice;
                     if (src != null && src.ContainsKey(answerItem.answer))
                        e.Value = src[answerItem.answer].name;
                     else
                        e.Value = String.Format("Код объекта <{0}> не найден", answerItem.answer);
                  }
                  else if (answerItem.remark.Equals("Организация"))
                  {
                     if (dsOrg.ContainsKey(answerItem.answer))
                        e.Value = dsOrg[answerItem.answer].name;
                     else if (dsPotenzailOrg.ContainsKey(answerItem.answer))
                        e.Value = dsPotenzailOrg[answerItem.answer].name;
                     else
                        e.Value = String.Format("Код объекта <{0}> не найден", answerItem.answer);
                  }
               }
            }
         }
      }
#endif

      //Создать или инициализировать наборы данных
      private void InitDataSets(FmDetailData data)
      {
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);

         dsOrg = DataModule.GetUserDataSet(data.AgentId, "Org", typeof(DataSet<string, Org>), true) as DataSet<string, Org>;

         dsOrder = DataModule.Get("Order") == null ? new DataSet<int, Order>("Order", true, true) :
            (DataSet<int, Order>)DataModule.Get("Order");
         dsOrder.UseReceivedFields = true;

         dsOrderW = DataModule.Get(OrderW.OBJECT_NAME) == null ? new DataSet<int, OrderW>(OrderW.OBJECT_NAME) :
            (DataSet<int, OrderW>)DataModule.Get(OrderW.OBJECT_NAME);
#if HTTP_SERVER
         dsVisit = DataModule.Get(Visit.OBJECT_NAME_HTTP) == null ? new DataSet<int, Visit>(Visit.OBJECT_NAME_HTTP) :
            (DataSet<int, Visit>)DataModule.Get(Visit.OBJECT_NAME_HTTP);
#elif Servolux
         DataModule.Remove(VisitInfo.V_OBJECT_NAME);
         dsVisit = new DataSet<int, Visit>(VisitInfo.V_OBJECT_NAME);
#elif VISIT_LITE
         dsVisit = DataModule.Get(Visit.OBJECT_NAME_LITE) == null ? new DataSet<int, Visit>(Visit.OBJECT_NAME_LITE) :
            (DataSet<int, Visit>)DataModule.Get(Visit.OBJECT_NAME_LITE);
#else
         dsVisit = DataModule.Get(Visit.OBJECT_NAME) == null ? new DataSet<int, Visit>(Visit.OBJECT_NAME) :
            (DataSet<int, Visit>)DataModule.Get(Visit.OBJECT_NAME);
#endif
         oDetail.VisitName = dsVisit.Name;

         dsIncass = DataModule.Get(Incass.OBJECT_NAME) as DataSet<int, Incass> ??
            new DataSet<int, Incass>(Incass.OBJECT_NAME, true, true);
         dsIncass.UseReceivedFields = true;

         dsOrgRemnants = DataModule.Get("OrgRemnants") == null ? new DataSet<int, OrgRemnants>("OrgRemnants") :
            (DataSet<int, OrgRemnants>)DataModule.Get("OrgRemnants");
         dsOrgRemnants.UseReceivedFields = true;

         //dsUserLog = DataModule.Get("UserLog") == null ? new DataSet<int, UserLog>("UserLog") :
         //   (DataSet<int, UserLog>)DataModule.Get("UserLog");
         //dsScriptDoc = DataModule.Get(ScriptDoc.OBJECT_NAME) == null ? new DataSet<int, ScriptDoc>(ScriptDoc.OBJECT_NAME) :
         //   (DataSet<int, ScriptDoc>)DataModule.Get(ScriptDoc.OBJECT_NAME);
         dsDayDoc = DataModule.Get(DayDoc.OBJECT_NAME) == null ? new DataSet<int, DayDoc>(DayDoc.OBJECT_NAME) :
            (DataSet<int, DayDoc>)DataModule.Get(DayDoc.OBJECT_NAME);
         dsPKO = DataModule.Get(PKO.OBJECT_NAME) == null ? new DataSet<int, PKO>(PKO.OBJECT_NAME, true, true) :
            (DataSet<int, PKO>)DataModule.Get(PKO.OBJECT_NAME);
         dsPKO.UseReceivedFields = true;

         dsReturns = DataModule.Get(Returns.OBJECT_NAME) == null ? new DataSet<int, Returns>(Returns.OBJECT_NAME) :
            (DataSet<int, Returns>)DataModule.Get(Returns.OBJECT_NAME);
         dsReturns.UseReceivedFields = true;

         dsMoneyProxy = DataModule.Get(MoneyProxy.OBJECT_NAME) == null ? new SimpleDataSet<MoneyProxy>(MoneyProxy.OBJECT_NAME) :
            (SimpleDataSet<MoneyProxy>)DataModule.Get(MoneyProxy.OBJECT_NAME);

         dsConfig = (DataSet<int, CommonConfig>)DataModule.Get(CommonConfig.OBJECT_NAME) ??
            new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME);

         dsPtnzOrg = (DataSet<string, PotenzialOrg>)DataModule.Get(PotenzialOrg.OBJECT_NAME) ??
            new DataSet<string, PotenzialOrg>(PotenzialOrg.OBJECT_NAME);

         dsPtnzOrg.Filter = DataUtils.MakeFilterFromAgents(null, Agents.GetDataSet());
         dsSales = (DataSet<int, Sales>)DataModule.Get(Sales.OBJECT_NAME) ??
            new DataSet<int, Sales>(Sales.OBJECT_NAME, true, true);
         dsSales.UseReceivedFields = true;

         dsPicStore = new DataSet<int, PicStore>(PicStore.OBJECT_COPY_NAME);

#if PRICE_MONITORING
         dsMonitoring = (DataSet<int, Monitoring>)DataModule.Get(Monitoring.OBJECT_NAME) ??
            new DataSet<int, Monitoring>(Monitoring.OBJECT_NAME);
         dsItems = DataModule.Get(MonitoringItem.OBJECT_NAME) as DataSet<string, MonitoringItem>;
         if (dsItems == null)
            dsItems = new DataSet<string, MonitoringItem>(MonitoringItem.OBJECT_NAME);
#endif
         dsOrderCommitted = DataModule.GetUserDataSet(data.AgentId, OrderCommitted.OBJECT_NAME, typeof(DataSet<int, OrderCommitted>), true) as DataSet<int, OrderCommitted>;
#if DISTR_DOC
         dsDistrDoc = (DataSet<int, Distr>)DataModule.Get(Distr.OBJECT_NAME) ?? 
            new DataSet<int, Distr>(Distr.OBJECT_NAME);
#endif
#if MOVEMENT_DOC
         dsMove = (DataSet<int, MoveDoc>)DataModule.Get(MoveDoc.OBJECT_NAME) ?? 
            new DataSet<int, MoveDoc>(MoveDoc.OBJECT_NAME);
         dsSklad = (DataSet<string, Sklad>)DataModule.Get(Sklad.OBJECT_NAME) ??
            new DataSet<string, Sklad>(Sklad.OBJECT_NAME);
#endif

#if INVOICE_DOC
         dsInvoice = (DataSet<int, Invoice>)DataModule.Get(Invoice.OBJECT_NAME) ??
            new DataSet<int, Invoice>(Invoice.OBJECT_NAME);
#endif
#if ORDER_CHARGE
         dsOrderCharges = (SimpleDataSet <OrderCharge>)DataModule.Get(OrderCharge.OBJECT_NAME) ??
            new SimpleDataSet<OrderCharge>(OrderCharge.OBJECT_NAME);
#endif

         dsGather = (SimpleDataSet<GPSGather>)DataModule.Get(GPSGather.OBJECT_NAME) ??
            new SimpleDataSet<GPSGather>(GPSGather.OBJECT_NAME);
         documents.Add(new DocumentInfo(dsGather, ObjType.TObjType.GPSGather));
      }

      protected virtual DataSet<int, OrgFolder> CreateOrgFolder(String agentid)
      {
         return DataModule.GetUserDataSet(agentid, OrgFolder.OBJECT_NAME, typeof(DataSet<int, OrgFolder>), true) as DataSet<int, OrgFolder>;
      }

      //Настроить фильтры для наборов данных
      protected virtual void AdjustFilterForDS(string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         dsOrg.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agentID), dsOrg.Name);
         //dsOrderCommitted.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agentID), dsOrderCommitted.Name);
         //dsOrderCommitted.Filter = "\"userid\" in ('" + agentID + "')";
         dsAgentPrice.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agentID), dsAgentPrice.Name);
         //dsOrg.Filter = String.Format("userid='{0}'", agentID);//DataUtils.MakeFilterFromAgents(null);

         dsOrder.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsOrderW.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsSales.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsVisit.Filter = String.Format(COMMON_FILTER_STR, "date", dateBegin, dateEnd, agentID);
         dsGather.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);

#if ORDER_CHARGE
         dsOrderCharges.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
#endif
#if AliansFood
         dsArchIncass.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
#endif

         //string f = "(" + String.Format(COMMON_FILTER_STR, "date", dateBegin, dateEnd, agentID) + ") or (" +
         //   String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID) + ")";
         //dsIncass.Filter = f;
         dsIncass.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID); ;

         dsOrgRemnants.Filter = String.Format(COMMON_FILTER_STR, "date", dateBegin, dateEnd, agentID);
         //dsUserLog.Filter = String.Format(COMMON_FILTER_STR, "objDate", dateBegin, dateEnd, agentID);
         //dsScriptDoc.Filter = String.Format(COMMON_FILTER_STR, "date", dateBegin, dateEnd, agentID);
         dsDayDoc.Filter = String.Format(COMMON_FILTER_STR, "start", dateBegin, dateEnd, agentID);

#if Vyatich
#else
         dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
#endif
         dsPKO.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsReturns.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsMoneyProxy.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         //dsOrgFolder.Filter = String.Format("\"userid\"='{0}'", agentID);
         dsConfig.Filter = "(not (\"userid\" is null)) or \"userid\" is null";
#if MOVEMENT_DOC
         dsMove.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
#endif

#if PRICE_MONITORING
         dsMonitoring.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
#endif
#if DISTR_DOC
         dsDistrDoc.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
#endif
#if INVOICE_DOC
         dsInvoice.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
#endif
      }

      //Обновить наборы даных
      private void RefreshDataSets(string agentID, DateTime dateBegin, DateTime dateEnd,
         bool needToGetPrice)
      {
         refreshing = true;
         btnRefresh.Enabled = false;

#if USE_TIMEZONE
         System.Globalization.CultureInfo.CurrentCulture.ClearCachedData();
#endif

#if VISIT_LITE
         largePhotos.Clear();
#endif
         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

         List<IDataSet> updSets = new List<IDataSet>();
         if (dsPrice.Count == 0)
         {
            updSets.Add(dsPrice);
#if Vyatich
            // если прайса нет - берем прайс текущего агента
            dsPrice.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agentID), dsPrice.Name);
#else
            if (dsAgentPrice.Count == 0)
               updSets.Add(dsAgentPrice);
#endif
         }

         dsOrg = DataModule.GetUserDataSet(agentID, "Org", typeof(DataSet<string, Org>), true) as DataSet<string, Org>;
         dsOrgFolder = CreateOrgFolder(agentID);

         if (dsOrg.Count == 0)
            updSets.Add(dsOrg);

         dsOrder.Clear();
         updSets.Add(dsOrder);
         updSets.Add(dsOrderW);
         updSets.Add(dsVisit);
         updSets.Add(dsOrgRemnants);
         updSets.Add(dsDayDoc);
         updSets.Add(dsPKO);
         updSets.Add(dsReturns);
         updSets.Add(dsMoneyProxy);
         updSets.Add(dsIncass);
         updSets.Add(dsOrgFolder);
         updSets.Add(dsOrgLocation);

#if ROUTE_HISTORY
         Agent a = cbAgents.SelectedItem as Agent;
         AgentRouteSheduleHelper rh = null;
         if (routeHelpers.ContainsKey(a.id))
            rh = routeHelpers[a.id];
         else
         {
            rh = new AgentRouteSheduleHelper();
            routeHelpers[a.id] = rh;
         }
         rh.Update(updSets, a, dateBegin, dateEnd);
#endif

         updSets.Add(dsConfig);
         updSets.Add(dsPtnzOrg);
         updSets.Add(dsSales);
         updSets.Add(dsOrderCommitted);
#if ORDER_CHARGE
         updSets.Add(dsOrderCharges);
#endif
#if AliansFood
         updSets.Add(dsArchIncass);
#endif
#if DISTR_DOC
         updSets.Add(dsDistrDoc);
#endif
#if MOVEMENT_DOC
         updSets.Add(dsMove);
         updSets.Add(dsSklad);
#endif
#if INVOICE_DOC
         updSets.Add(dsInvoice);
#endif
         dgvDetail.Focus();

         dateEnd = new DateTime(dateEnd.Year, dateEnd.Month, dateEnd.Day, 23, 59, 59, 99);
         AdjustFilterForDS(agentID, dateBegin, dateEnd);

#if QUESTION
         dsAnswer.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         updSets.Add(dsQuestion);
         updSets.Add(dsAnswer);
         updSets.Add(dsPotenzailOrg);
#endif

         OrgLocations ol = OrgLocations.GetDataSet();
         if (ol.Count == 0)
            updSets.Add(ol);

#if PRICE_MONITORING
         updSets.Add(dsMonitoring);
         updSets.Add(dsItems);
#endif
         updSets.Add(dsGather);

         dsPicStore.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         updSets.Add(dsPicStore);

         BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
         DBConnection conn = Config.GetConfig().GetConnection();
         conn.ReceiveTimeout = 60 * 1000 * 3;
         FmWait.ShowForm(this, DataModule.RefreshGiveSets(conn, updSets, FmWait.ProgressIndicator));
      }

      protected virtual void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
      }

      protected virtual void AfterRefreshData() { }

      //Окончание выборки, заполняются внутренние наборы
      void DataProcessed(System.Object setnder, EventArgs e)
      {
         refreshing = false;
         DataModule.ClearEvents();

         // Если нет общего прайса - используем прайс агента
         if (dsPrice.Count == 0 && dsAgentPrice.Count > 0)
            dsPrice = dsAgentPrice;

         picMap.Clear();
         foreach (PicStore p in dsPicStore.Data)
         {
            picMap[p.id] = p;
         }
         
         //UpdateSendDate();
         AfterRefreshData();

         this.Invoke(new InvokeDelegate(delegate
         {
            FmWait.CloseForm();
            curUserID = GetSelectedIdAgent();
            ReloadData();
            btnRefresh.Enabled = true;
         }));

      }

      protected override void OnKeyDown(KeyEventArgs e)
      {
         base.OnKeyDown(e);
         if (e.KeyCode == Keys.F5 && e.Modifiers == Keys.None)
            RefreshDataSets((cbAgents.Items[cbAgents.SelectedIndex] as Agent).id,
               dtpBegin.Value.Date, dtpEnd.Value.Date, false);

      }

      protected virtual void ReloadData()
      {
         dgvDetail.SuspendLayout();
         MakeMapToCheckCompetDocuments();
         UpdateGrid(true);

         dgvDetail.ResumeLayout();
      }

      class SendDate : Dictionary<DateTime, List<DateTime>>
      {
         public void Load(ICollection data)
         {
            foreach (UserLog ul in data)
            {
               if (ContainsKey(ul.objDate))
               {
                  this[ul.objDate].Add(ul.date);
               }
               else
               {
                  List<DateTime> dl = new List<DateTime>();
                  dl.Add(ul.date);
                  this[ul.objDate] = dl;
               }
            }
         }

         public void UpdateSet(ICollection set, FieldInfo dateField, FieldInfo sendField)
         {
            if (dateField == null || sendField == null)
               return;

            foreach (object o in set)
            {
               object sv = sendField.GetValue(o);
               DateTime snd = (sv == null) ? DateTime.MinValue : (DateTime)sv;
               if (snd == DateTime.MinValue)
               {
                  DateTime dt = (DateTime)dateField.GetValue(o);
                  if (ContainsKey(dt))
                  {
                     List<DateTime> l = this[dt];
                     sendField.SetValue(o, l[l.Count - 1]);
                  }
               }
            }
         }
      }

      //private void UpdateSendDate()
      //{
      //   // предполагаем, что нет документов разного типа у одного агента с одной датой
      //   SendDate sendDate = new SendDate();
      //   sendDate.Load(dsUserLog.Data);

      //   BindingFlags bf = BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic;
      //   sendDate.UpdateSet(dsOrder.Data, typeof(Order).GetField("created", bf), typeof(Order).GetField("sended", bf));
      //   sendDate.UpdateSet(dsOrderW.Data, typeof(OrderW).GetField("created", bf), typeof(OrderW).GetField("sended", bf));
      //   sendDate.UpdateSet(dsVisit.Data, typeof(Visit).GetField("date", bf), typeof(Visit).GetField("sended", bf));
      //   sendDate.UpdateSet(dsOrgRemnants.Data, typeof(OrgRemnants).GetField("date", bf), typeof(OrgRemnants).GetField("sended", bf));
      //   sendDate.UpdateSet(dsPKO.Data, typeof(PKO).GetField("created", bf), typeof(PKO).GetField("sended", bf));
      //   sendDate.UpdateSet(dsReturns.Data, typeof(PKO).GetField("created", bf), typeof(Returns).GetField("sended", bf));
      //}



      protected virtual string TotalCount()
      {
         return "Всего документов: " + oDetail.OrderCount.ToString();
      }

      // Обновить таблицу в соответсвии с выбранными параметрами (FmDetailData)
      protected virtual void UpdateGrid(bool refreshFilterCB)
      {
         FmDetailData dd = new FmDetailData(GetSelectedIdAgent(), dtpBegin.Value, GetDateForEndPeriod(),
            cbFilter.SelectedItem is ObjType ? cbFilter.SelectedItem as ObjType : null);

         dd.fmDetail = this;
         SetRelatedGridTitle();

         routes = oDetail.Load(dd, IsOneDaySelected(), GetSelectedAgent());

         Cursor.Current = Cursors.Default;
         BindingSource bs = new BindingSource();
         bs.DataSource = oDetail;
         dgvDetail.DataSource = bs;
         tsslCount.Text = TotalCount();

         tsslSum.Text = String.Format("Сумма документов: {0}:, Сумма ПКО: {1} ",
            oDetail.Sum.ToString("C", Config.GetCultureInfo()),
            oDetail.IncassSum.ToString("C", Config.GetCultureInfo()));

#if SUM_WEIGHT_LABEL
         tsslSum.Text += string.Format(" Вес: {0} кг. ", oDetail.Weight.ToString("0.000", CultureInfo.InvariantCulture));
#endif

         btnRoute.Enabled = true;

         SortOrderDetail(SortOrder.None, "DateCreated", 5);

         if (refreshFilterCB)
            UpdateFiltersListInComboBox();
      }

      protected virtual void UpdateFiltersListInComboBox()
      {
         cbFilter.SuspendLayout();
         cbFilter.Items.Clear();
         cbFilter.Items.Add("Все");

         foreach (ObjType tObjType in oDetail.FiltersAvailable)
            cbFilter.Items.Add(tObjType);

         //cbFilter.Sorted = true;
         cbFilter.SelectedIndex = 0;
         cbFilter.ResumeLayout();
      }

      //Выбран один день или период
      protected bool IsOneDaySelected()
      {
         return dtpBegin.Value.Date == dtpEnd.Value.Date;
      }

      //Настроить соответствующие заголовки таблицы
      private void SetRelatedGridTitle()
      {
         const string CREATED_STR = "создания";
         const string TRANSFER_STR = "передачи";
         const string DATE_STR = "Дата";
         const string TIME_STR = "Время";
         const string STR_FORMAT = "{0} {1}";

         SetDgvDetailHeaderText(
            string.Format(STR_FORMAT,
               IsOneDaySelected() ? TIME_STR : DATE_STR, CREATED_STR),
            string.Format(STR_FORMAT,
               IsOneDaySelected() ? TIME_STR : DATE_STR, TRANSFER_STR));
      }

      //Установить текст для столбцов грида
      private void SetDgvDetailHeaderText(string txtCreated, string txtTransfer)
      {
         dgvDetailColumnCreated.HeaderText = txtCreated;
         dgvDetailColumnTransfer.HeaderText = txtTransfer;
      }

      // Событие отображение формы
      private void FmDetail_Shown(object sender, EventArgs e)
      {
         RefreshDataSets(GetSelectedIdAgent(), dtpBegin.Value, dtpEnd.Value, true);
         if (dgvDetail.RowCount == 0)
         {
            return;
         }
      }

      // При смене записи в таблице в соответсвии с типом записи,
      // настроить отобразить другие элементы
      private void dgvDetail_SelectionChanged(object sender, EventArgs e)
      {
         //UpdateDetailTable();
      }

      private string MakeDayDocText(DayDoc dd)
      {
         StringBuilder text = new StringBuilder();
         text.AppendLine();
         text.Append("Начало дня: ");
         text.AppendLine(dd.start.ToString("\tyyyy-MM-dd HH:mm"));
         text.AppendLine();
         text.Append("Конец дня: ");
         text.AppendLine(dd.end.ToString("\tyyyy-MM-dd HH:mm\n"));
         text.AppendLine();
         text.AppendFormat("Пробег\t\t{0} ({1} - {2})", dd.valueEnd - dd.valueStart, dd.valueEnd, dd.valueStart);

         return text.ToString();
      }

      protected virtual void UpdateVisibility(Control visible, GRSoft.Network.DataObject src)
      {
         //Control[] check = new Control[] { dgvRemnantsItems, tbVisitText, 
         //   dgvOrderItems, dgvReturns, dgvMoveItem, dgvInvoiceItem, dgvAnswerItems };

         foreach (Control c in detailPanel.Controls)
         {
            if (c == lbNotes || c == pnlNotes)
               continue;

            c.Visible = (c == visible);
         }

         if (visible != null && visible is DataGridView)
         {
            if (lbNotes.Visible == false)
               visible.Height = visible.Height + lbNotes.Height;
            else
               visible.Height = panel3.Height;
         }
         if (visible != null)
            visible.BringToFront();
      }

      public string CurrentOrgId
      {
         get
         {
            string id = null;
            if (dgvDetail.CurrentRow != null)
            {
               OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;
               if (odr != null && odr.NOrg != null)
                  id = odr.NOrg.id;
            }

            return id;
         }
      }

      protected virtual void SetOrderItems(Order o)
      {
         List<OrderItem> loi = new List<OrderItem>();
         loi.Add(new OrderItemTotal(o.items));
         loi.AddRange(o.items);
         dgvOrderItems.DataSource = loi;
         dgvOrderItems.ClearSelection();
         dgvOrderItems.Rows[0].Frozen = true;
      }

      private void dgvOrderItems_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         if (dgvOrderItems.Rows[e.RowIndex].DataBoundItem is OrderItemTotal)
         {
            if (itemsBoldFont == null)
               itemsBoldFont = new System.Drawing.Font(e.CellStyle.Font, FontStyle.Bold);
            e.CellStyle.Font = itemsBoldFont;
            e.CellStyle.BackColor = Color.LightGray;
         }
      }

      protected virtual string GetVisitText(Visit v)
      {
#if VISIT_CAUSE
         return v.cause + " " + v.Remark;
#else
         return v.Remark;
#endif

      }

      protected virtual void UpdateDetailTable(DataGridViewRow curRow)
      {
         lvPhoto.Items.Clear();
         imPhoto.Images.Clear();

         dgvRemnantsItems.Visible = true;
         tbVisitText.Visible = false;
         dgvOrderItems.Visible = false;

         if (curRow == null)
         {
            dgvRemnantsItems.DataSource = new List<OrgRemnantsItem>();
            lblAdress.Text = string.Empty;
            dgvRemnantsItems.Height = dgvRemnantsItems.Height + lbNotes.Height;
            return;
         }

         ObjType docType = null;
         OrderDetailRepresentation odr = null;
         try
         {
            odr = curRow.DataBoundItem as OrderDetailRepresentation;
            docType = odr.Doctype;
            UpdateRemark(odr);
         }
         catch
         {
         }

         if (docType == null)
         {
            return;
         }

         Control visible = null;
         switch (docType.Val)
         {
            case ObjType.TObjType.OtOrder:
            case ObjType.TObjType.OrderCharge:
            case ObjType.TObjType.OrderW:
            case ObjType.TObjType.Sales:
               visible = dgvOrderItems;
               SetOrderItems(odr.StoreObject as Order);
               break;
            case ObjType.TObjType.OtVisit:
               visible = tbVisitText;
               tbVisitText.Text = GetVisitText((odr.StoreObject as Visit));
               break;
            case ObjType.TObjType.OtOrgRemnants:
               visible = dgvRemnantsItems;
               List<OrgRemnantsItem> remnantsItems = new List<OrgRemnantsItem>();
               remnantsItems.AddRange((odr.StoreObject as OrgRemnants).items);
               dgvRemnantsItems.DataSource = remnantsItems;
               break;
#if DISTR_DOC
            case ObjType.TObjType.Distr:
               visible = dgvRemnantsItems;
               List<DistrItem> list = new List<DistrItem>();
               list.AddRange((odr.StoreObject as Distr).items);
               dgvRemnantsItems.DataSource = list;
               break;
#endif
#if INVOICE_DOC
            case ObjType.TObjType.Invoice:
               visible = dgvInvoiceItem;
               List<InvoiceItem> list = new List<InvoiceItem>();
               list.AddRange((odr.StoreObject as Invoice).items);
               dgvInvoiceItem.DataSource = list;
               break;
#endif
            case ObjType.TObjType.DayDoc:
               visible = tbVisitText;
               tbVisitText.Text = MakeDayDocText(odr.StoreObject as DayDoc);
               break;
            case ObjType.TObjType.PKO:
            case ObjType.TObjType.ArchIncass:
               visible = tbVisitText;
               MakePKOText(tbVisitText, odr.StoreObject);
               break;
            case ObjType.TObjType.NotVisit:
               visible = tbVisitText;
               tbVisitText.Text = "Не посетил";
               break;
            case ObjType.TObjType.OtReturn:
               visible = dgvReturns;
               List<ReturnItem> returns = new List<ReturnItem>();
               returns.AddRange((odr.StoreObject as Returns).items);
               dgvReturns.DataSource = returns;
               break;
            case ObjType.TObjType.GPSGather:
               GPSGather doc = odr.StoreObject as GPSGather;
               String text = GetGPSGatherInfo(doc);
               tbVisitText.Text = text;
               visible = tbVisitText;
               break;

#if PRICE_MONITORING
            case ObjType.TObjType.Monitoring:
               visible = RefreshMonitoring(odr);
               break;
#endif
#if QUESTION
            case ObjType.TObjType.Answer:
               visible = dgvAnswerItems;
               Answer a = odr.StoreObject as Answer;
               List<AnswerItem> aswItems = new List<AnswerItem>();
               aswItems.AddRange(a.items);

               if (a.quest != null)
               {
                  Dictionary<String, int> weights = new Dictionary<string, int>();

                  foreach (QuestionItem i in a.quest.items)
                  {
                     weights[i.iditem] = i.number;
                  }

                  aswItems.Sort((x, y) =>
                  {
                     int vX = 0;
                     int vY = 0;

                     if (weights.ContainsKey(x.iditem))
                        vX = weights[x.iditem];

                     if (weights.ContainsKey(y.iditem))
                        vY = weights[y.iditem];

                     return vX - vY;
                  });
               }


               dgvAnswerItems.DataSource = aswItems;

               if ((odr.StoreObject as Answer).quest != null)
               {
                  lbNotes.Visible = true;
                  lbNotes.Text = (odr.StoreObject as Answer).quest.Name;
               }
               break;
#endif
            case ObjType.TObjType.MoneyProxy:
               mpdetail.SetData(odr.StoreObject as MoneyProxy);
               visible = mpdetail;
               break;
#if MOVEMENT_DOC
               case ObjType.TObjType.Move:
               visible = dgvMoveItem;
               List<MoveItem> mi = new List<MoveItem>();
               mi.AddRange((odr.StoreObject as MoveDoc).items);
               dgvMoveItem.DataSource = mi;
               break;
#endif
            default:
               visible = RefreshDetail(odr);
               break;
         }

         UpdateVisibility(visible, odr.StoreObject);
         ShowCorrespondingPhoto(odr.DateCreatedDT, odr);
         SetLabelAddressText(curRow);
      }

      protected virtual Control RefreshMonitoring(OrderDetailRepresentation odr)
      {
         tbVisitText.Text = "";
         return tbVisitText;
      }

      protected virtual void InitVisible(Control visible, GRSoft.Network.DataObject src) { }

      private static string GetGPSGatherInfo(GPSGather doc)
      {
         String text = "";

         if (doc != null)
         {
            text += String.Format("Координаты (долгота/широта) : {0}/{1}", doc.longitude, doc.latitude);
            text += Environment.NewLine;
            text += String.Format("Точность : {0} м.", doc.accuracy);
         }

         return text;
      }

      protected virtual void UpdateRemark(OrderDetailRepresentation odr)
      {
         if (odr.Notes.Length > 0)
         {
            lbNotes.Visible = true;
            lbNotes.Text = odr.Notes;

            tooltipRemark.SetToolTip(lbNotes, lbNotes.Text);
         }
         else
            lbNotes.Visible = false;
      }



      internal virtual Control RefreshDetail(OrderDetailRepresentation odr)
      {
         tbVisitText.Text = "";
         return tbVisitText;
      }

      protected virtual void MakePKOText(RichTextBox tb, object p)
      {
         string ret = "";
         PKO pko = p as PKO;
         if (pko != null)
         {
            StringBuilder str = new StringBuilder(pko.date.ToShortDateString());
            str.Append("Номер\t");
            str.Append(pko.number).Append("\r\n");
            str.Append("Сумма\t");
            str.Append(pko.Sum().ToString("C", Config.GetCultureInfo()));
            ret = str.ToString();
         }
         else
         {
            Incass i = p as Incass;
            if (i != null)
            {
               StringBuilder str = new StringBuilder("Дата\t" + i.date.ToShortDateString());
               str.Append("\r\nСумма\t");
               str.Append(i.Sum().ToString("C", Config.GetCultureInfo()));
               ret = str.ToString();
            }
         }

         tb.Text = ret;
      }

      public bool IsSameDate(DateTime d1, DateTime d2)
      {
         return (d1.Year == d2.Year) && (d1.Month == d2.Month) && (d1.Day == d2.Day);
      }

      protected void AddVisitPhotos(Visit v)
      {
         int photoCount = 0;
         AddVisitPhotos(v, null, 0, out photoCount);
      }

      protected void AddVisitPhotos(Visit v, List<Image> picList, int count, out int rCount)
      {
         List<Image> nativePicture = picList == null ? new List<Image>() : picList;
         int photoCount = count;

         int i = count;
         foreach (Visit.VisitItem item in v.items)
         {
            try
            {
               if (item.id == null)
                  continue;

               MemoryStream stream = new MemoryStream(item.id);
               Image image = new Bitmap(stream);
               image.Tag = new VisitTag(v, item);

               nativePicture.Add(image);
               imPhoto.Images.Add(image);
               //stream.Close();
               photoCount++;

               String tag = (i + 1).ToString();
#if SNAPSHOT_RATING
               if (item.rating != 0)
                  tag += "\nОценка: " + item.rating.ToString();
#endif
#if SNAPSHOT_CAPTION
               if (item.caption != null && item.caption.Length > 0)
                  tag += "\n" + item.caption;
#endif
               ListViewItem lvi = lvPhoto.Items.Add(tag);
               lvi.ImageIndex = i;
               lvi.Tag = new VisitTag(v, item);
               i++;
            }
            catch (Exception e)
            {
               String str = e.StackTrace;
            } //TO-DO: watch in logger!!!!!
         }

         imPhoto.Tag = nativePicture;

         rCount = photoCount;
      }

      protected void StartPhotoHTML(StringBuilder sb)
      {
         sb.Append("<html><head>\n<meta charset='utf-8'>\n<style type='text/css'>\ndiv.inline{\n    display:inline;\n   margin-right: 6px}" +
            "p.nomargine{\n    margin-top: 0px;\n    text-align: center;\n}\n</style>\n</head>\n<body>\n");
      }

      public void ShowLargePicture(string url, string name)
      {
         try
         {
            WebClient wc = new WebClient();
            byte[] b = wc.DownloadData(url);
            MemoryStream ms = new MemoryStream(b);
            Image i = Image.FromStream(ms);
            //ms.Dispose();
            wc.Dispose();

            ShowPhoto(i, name);
         }
         catch (Exception e)
         {
            MessageBox.Show(e.Message);
         }
      }

      protected void AddAnswerPhotos(StringBuilder htmlBuilder, Answer a)
      {
         foreach (AnswerItem ai in a.items)
         {
            if (ai.type == QuestionItem.IMAGE && picMap.ContainsKey(ai.answer))
            {
               PicStore ps = picMap[ai.answer];
               AddPhotoToHtml(htmlBuilder, ai.id, ps.name, ps.smallName, ps.smallSize, a.created.ToString("dd.MM.yy HH:mm"), ps.created);
            }
         }
      }

      protected bool AddPhotoToHtml(StringBuilder sb, string name, string img, string smallImg, string smallSize, string docDate, DateTime photoCreated)
      {
         if (smallImg.Length == 0)
            return false;

         string[] hw = smallSize.Split(new char[] { '*' });

         sb.AppendLine("<div class='inline' style='width: " + hw[0] + "px;'>");
         smallImg = smallImg.Replace("\\", "/");
         if (smallImg.StartsWith("/"))
            smallImg = smallImg.Substring(1);

         img = img.Replace("\\", "/");
         if (img.StartsWith("/"))
            img = img.Substring(1);
         string largHref = config.HrefBase + img;
         string docTag = docDate + " " + name.Replace("\"", "");

#if VISIT_ITEM_DATE
         if (photoCreated.Year > 2010)
         {
            docTag = photoCreated.ToString("dd/MM/yyyy HH:mm") + " " + name.Replace("\"", "");
         }
#endif

         sb.AppendLine("<img ondblclick='window.external.ShowLargePicture(\"" + largHref + "\", \"" + docTag + "\")' src='" + config.HrefBase + smallImg + "' width='" +
            hw[0] + "px' height='" + (hw.Length > 1 ? hw[1] : "165") + "px' />");
#if VISIT_ITEM_DATE
         if(photoCreated.Year > 2010)
         {
            name += " " + photoCreated.ToString("dd/MM/yyyy HH:mm");
         }
#endif
         sb.AppendLine("<p class='nomargine'>" + name + "</div>");

         return true;
      }

      protected void EndPhtoHtml(StringBuilder sb)
      {
         sb.AppendLine("</body></html>");
      }

      //По новому алгоритму фото показываем для первой записи которая имеет ту же организаци
      //и ту же дату
      protected virtual void ShowCorrespondingPhoto(DateTime date, OrderDetailRepresentation o)
      {
         lvPhoto.Clear();

         if (o.NOrg == null)
            return;

         int photoCount = 0;
         List<Image> nativePicture = new List<Image>();

         StringBuilder htmlBuilder = new StringBuilder();
         DayDoc dd = o.StoreObject as DayDoc;
         if (dd != null)
         {
            try
            {
               MemoryStream stream = new MemoryStream(dd.photoStart);
               Image image = new Bitmap(stream);
               image.Tag = dd.start;

               nativePicture.Add(image);
               imPhoto.Images.Add(image);
               stream.Close();
               photoCount++;

               stream = new MemoryStream(dd.photoEnd);
               image = new Bitmap(stream);
               image.Tag = dd.end;

               nativePicture.Add(image);
               imPhoto.Images.Add(image);
               stream.Close();
               photoCount++;

               imPhoto.Tag = nativePicture;
               for (int i = 0; i < photoCount; i++)
               {
                  lvPhoto.Items.Add((i + 1).ToString()).ImageIndex = i;
               }
            }
            catch { } //TO-DO: watch in logger!!!!!
         }
         else
         {
            Visit v = o.StoreObject as Visit;
            if (v != null)
            {
#if HTTP_SERVER
               StartPhotoHTML(htmlBuilder);
               int i = 0;
               string docDate = v.created.ToString("dd.MM.yy HH:mm");
               v.items.Sort();
               foreach(Visit.VisitItem vi in v.items)
               {
                  DateTime photoCr = DateTime.MinValue;
#if VISIT_ITEM_DATE
                  photoCr = vi.date;
#endif
                  String name = (i + 1).ToString();
#if ClassicSpb

                  if (dsPrice.ContainsKey(vi.tag))
                     name = dsPrice[vi.tag].Name;
#endif
                  if(vi.date > v.created)
                  {
                     docDate = vi.date.ToString("dd.MM.yy HH:mm");
                  }
                  AddPhotoToHtml(htmlBuilder, name, vi.name, vi.smallName, vi.smallSize, docDate, photoCr);

                  i++;
               }
#else
               AddVisitPhotos(v);
#endif
            }
            else
            {
#if HTTP_SERVER
               StartPhotoHTML(htmlBuilder);
               int i = 0;
               foreach (Visit vis in dsVisit.Data)
               {
                  if (IsVisitForDoc(date, o, vis))
                  {
                     string docDate = vis.created.ToString("dd.MM.yy HH:mm");

                     vis.items.Sort();
                     foreach (Visit.VisitItem vi in vis.items)
                     {
                        DateTime photoCr = DateTime.MinValue;
#if VISIT_ITEM_DATE
                        photoCr = vi.date;
#endif
                        String name = (i + 1).ToString();
#if ClassicSpb
                        if (dsPrice.ContainsKey(vi.tag))
                           name = dsPrice[vi.tag].Name;
#endif
                        AddPhotoToHtml(htmlBuilder, name, vi.name, vi.smallName, vi.smallSize, docDate, photoCr);

                        i++;
                     }
                  }
               }

               Answer a = o.StoreObject as Answer;
               if (a != null)
               {
                  AddAnswerPhotos(htmlBuilder, a);
               }
               else
               {
                  AddObjectPhoto(htmlBuilder, o.StoreObject);
               }
#else
#if Agama
               int uc = -1;
               Order order = o.StoreObject as Order;
               if (order != null)
                  uc = order.unitCode;
#endif
               lvPhoto.Items.Clear();
               imPhoto.Images.Clear();
               List<Image> photos = new List<Image>();
               int pCounter = 0;
               foreach (Visit vis in dsVisit.Data)
               {
#if Agama
                  if (IsSameDate(vis.date, date) && vis.org.id == o.NOrg.id && (uc == -1 || vis.unitCode == uc))
#else
                  if (IsSameDate(vis.date, date) && vis.org.id == o.NOrg.id)
#endif
                  {
                     AddVisitPhotos(vis, photos, pCounter, out pCounter);
                  }
               }
#endif
            }
         }

#if HTTP_SERVER
         UpdateWebHtml(htmlBuilder);
#endif
      }

      public virtual bool IsVisitForDoc(DateTime date, OrderDetailRepresentation o, Visit vis)
      {
         return IsSameDate(vis.date, date) && vis.org.id == o.NOrg.id;
      }

      protected virtual void AddObjectPhoto(StringBuilder htmlBuilder, Network.DataObject dataObject)
      {

      }

      protected void UpdateWebHtml(StringBuilder htmlBuilder)
      {
         if (htmlBuilder.Length > 0)
         {
            EndPhtoHtml(htmlBuilder);
            string text = htmlBuilder.ToString();
            if (!text.Equals(assignedHtml))
            {
               assignedHtml = text;
               wbPhoto.Stop();
               wbPhoto.DocumentText = text;
            }
         }
         else
            wbPhoto.DocumentText = "<html></html>";
      }

      //Возвращает "исходную по размерам" фотографию связанную со списком фото,
      private Image GetNativePicture(int index)
      {
         List<Image> nativePictures = imPhoto.Tag as List<Image>;

         if (nativePictures == null)
         {
            MessageBox.Show("Невозможно получить исходную фотографию, обратитесь к разработчикам программы");
            return null;
         }

         return nativePictures[index];
      }

      // Отобразить адрес организации
      protected void SetLabelAddressText(DataGridViewRow row)
      {
         OrderDetailRepresentation odr = row.DataBoundItem as OrderDetailRepresentation;
         if (dgvDetail.RowCount > 0 && odr != null)
         {
            lblAdress.Text = odr.OrgAddr;
         }
         else
         {
            lblAdress.Text = string.Empty;
         }
      }

      // FormClosed
      private void FmDetail_FormClosed(object sender, FormClosedEventArgs e)
      {
         DataModule.DataProcessed -= DataProcessed;
      }

      // Получить ID выделенного агента из списка cbAgents
      protected string GetSelectedIdAgent()
      {
         return (cbAgents.Items[cbAgents.SelectedIndex] as Agent).id;
      }

      public DataSet<String, Org> GetAgentOrgs
      {
         get { return dsOrg; }
      }

      // агентя для выбранного агента из списка(cbAgents)
      public Agent GetSelectedAgent()
      {
         DataSet<string, Agent> dsAgent = (DataSet<string, Agent>)DataModule.Get("Agents");
         return dsAgent[(cbAgents.Items[cbAgents.SelectedIndex] as Agent).id];
      }

      // Показать форму "Маршрут"
      private void btnRoute_Click(object sender, EventArgs e)
      {
         if (!WebViewWarning.IsWebViewExists())
         {
            WebViewWarning.Open();
            return;
         }

         Type prcType = FormEntries.GetFormType(typeof(FmRoute));
         ConstructorInfo ci = prcType.GetConstructor(new Type[] { typeof(string), typeof(DateTime) });
         FmRoute route = (FmRoute)ci.Invoke(new object[] { GetSelectedIdAgent(), dtpBegin.Value.Date });
         route.SetDocuments(documents);
         route.Show();

         //FmRoute route = new FmRoute(GetSelectedIdAgent(), GetStartDate());
         //route.SetDocuments(documents);
         //route.Show();
      }

      // Событие смена значение фильтра
      protected void cbFilter_SelectedIndexChanged(object sender, EventArgs e)
      {
         if (Visible)
         {
            UpdateGrid(false);
         }
      }

      //Показать адрес организации на карте
      protected virtual void lblAdress_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
      {
         if (!WebViewWarning.IsWebViewExists())
         {
            WebViewWarning.Open();
            return;
         }
         OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;
#if SELECT_ORG_LOCATION
         if (dsOrgLocation.ContainsKey(odr.NOrg.id))
         {
            OrgLocation loc = dsOrgLocation[odr.NOrg.id];

            FmAddrShow.AddrShow(new Location(loc.latitude, loc.longitude), odr.NOrg);
         }
#endif
         FmAddrShow.AddrShow(lblAdress.Text, odr.NOrg);
      }

      #region Методы для обработки отображения выпадающих списков
      private void cbAgents_DrawItem(object sender, DrawItemEventArgs e)
      {
         CommonDrawItem(sender, e);
      }

      private void CommonDrawItem(object sender, DrawItemEventArgs e)
      {
         e.DrawBackground();
         e.Graphics.DrawString((sender as ComboBox).Items[e.Index].ToString(), e.Font,
            System.Drawing.Brushes.Black, new RectangleF(e.Bounds.X, e.Bounds.Y, e.Bounds.Width, e.Bounds.Height));
      }

      private void cbAgents_MeasureItem(object sender, MeasureItemEventArgs e)
      {
         e.ItemHeight = 14;
      }

      private void cbFilter_MeasureItem(object sender, MeasureItemEventArgs e)
      {
         e.ItemHeight = 14;
      }

      private void cbFilter_DrawItem(object sender, DrawItemEventArgs e)
      {
         CommonDrawItem(sender, e);
      }
      #endregion

      protected virtual void btnRefresh_Click_1(object sender, EventArgs e)
      {
         RefreshDataSets((cbAgents.Items[cbAgents.SelectedIndex] as Agent).id,
            dtpBegin.Value.Date, dtpEnd.Value.Date, false);
      }

#if VISIT_LITE
      Image GetLargePhoto(Visit v, Visit.VisitItem vi, Image curPhoto)
      {
         List<Image> lst = null;
         if (largePhotos.ContainsKey(v))
         {
            lst = largePhotos[v];
         }
         else
         {
            SimpleDataSet<Visit> refVis = new SimpleDataSet<Visit>(Visit.OBJECT_NAME, false);
            refVis.Filter = String.Format("\"userid\"='{0}' and \"date\"=ToDate('{1:dd-MM-yyyy HH:mm:ss}')", v.userid, v.created);
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), refVis, null).Join();
            if (refVis.Count > 0)
            {
               lst = new List<Image>();
               foreach (Visit doc in refVis.Data)
               {
                  foreach (Visit.VisitItem item in doc.items)
                  {
                     try
                     {
                        MemoryStream stream = new MemoryStream(item.id);
                        Image image = new Bitmap(stream);
                        lst.Add(image);
                     }
                     catch (Exception)
                     {

                     }
                  }
                  break;
               }
               largePhotos[v] = lst;
            }
         }
         if (lst != null && vi.pos > 0 && vi.pos <= lst.Count)
            return lst[vi.pos - 1];
         return curPhoto;
      }
#endif

      public virtual void lvPhoto_DoubleClick(object sender, EventArgs e)
      {
         Image photo = GetNativePicture((sender as ListView).SelectedItems[0].Index);
         string tag = "";
         string comment = string.Empty;
         if (photo.Tag != null)
         {
            VisitTag vt = photo.Tag as VisitTag;
            if (vt != null)
            {
#if VISIT_LITE
               photo = GetLargePhoto(vt.visit, vt.item, photo);
#endif

               DateTime dt = vt.visit.created;
               tag = dt.ToString("dd.MM.yy HH:mm");
               comment = vt.visit.Remark;
            }
            else
            {
               if (photo.Tag is DateTime)
               {
                  DateTime dt = (DateTime)photo.Tag;
                  if (dt != null)
                     tag = dt.ToString("dd.MM.yy HH:mm");
               }
            }
         }
         else
         {
            OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;
            tag = odr.DateCreatedDT.ToString();
         }

#if SVIMMC || GoldenShelf || NBtl || TDKalina
         FmViewPhoto.ShowPhoto(photo, tag, dgvDetail.CurrentRow.Cells[1].Value.ToString(), comment);
#else
         ShowPhoto(photo, tag);
#endif
      }

      protected virtual void ShowPhoto(Image photo, string tag)
      {
         FmViewPhoto.ShowPhoto(photo, tag);
      }

      //Составит и вывести отчет в HTML
      private void tsbMakeHtml_Click(object sender, EventArgs e)
      {
         OpenVisitReport();
      }

      virtual protected void OpenVisitReport()
      {
         Type rptType = FormEntries.GetFormType(typeof(HtmlReport));
         ConstructorInfo ci = rptType.GetConstructor(Type.EmptyTypes);
         HtmlReport htmlReport = (HtmlReport)ci.Invoke(new object[] { });

         OpenLink.NewWindow(String.Format("\"{0}\"", htmlReport.makeDetailsFileInfo(dgvDetail,
               new TimeInterval(dtpBegin.Value, dtpEnd.Value), (cbAgents.Items[cbAgents.SelectedIndex] as Agent))));
      }

      private void tbnMessage_Click(object sender, EventArgs e)
      {
         FmMessage.MessageShow(GetSelectedAgent());
      }

      private void MakeMapToCheckCompetDocuments()
      {
         //foreach (ScriptDoc sd in dsScriptDoc.Data)
         //{
         //   if (sd.items.Count == 3)
         //   {
         //      foreach (ScriptDocItem si in sd.items)
         //      {
         //         if (!documetsCompleted.ContainsKey(si.date))
         //         {
         //            documetsCompleted.Add(si.date, true);
         //         }
         //      }
         //   }
         //}
      }

      virtual protected bool IsDocCompleted(DateTime date, GRSoft.Network.DataObject dataObject)
      {
         Order o = dataObject as Order;
         if (o != null)
            return !o.OutOfPlan;

         const string FUNC_NAME = "IsDocCompleted";
         FunctionArgsType args = new FunctionArgsType(FUNC_NAME, date, dataObject);

         if (decorator.ExecFunction(args))
         {
            return (bool)args.RetVal;
         }

         //Поведение функции по умолчанию
         return true;
      }

      protected virtual void GetDocData(out DateTime created, out Org docOrg, GRSoft.Network.DataObject dataObject)
      {
         docOrg = null;
         created = DateTime.MinValue;
      }

      public static bool OrderMissed(BaseDocument order, DataSet<int, OrderCommitted> dataSet)
      {
         bool result = false;

         if (order != null)
         {
            if (order.Sum() != 0)
            {
               result = true;
               DateTime created = order.created;
               string userid = order.AgentID;

               if (dataSet != null)
                  foreach (OrderCommitted oc in dataSet.Data)
                     if (oc.created.Equals(created) && userid.Equals(oc.userid))
                     {
                        result = (oc.number.Trim().Length == 0);
                        break;
                     }
            }
         }

         return result;
      }

      private void dgvDetail_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         CellFormatting(e);
      }

      public virtual bool DocCanMissed(BaseDocument doc)
      {
         return doc is Order;
      }

      protected virtual void CellFormatting(DataGridViewCellFormattingEventArgs e)
      {
         if (e.RowIndex < 0 || e.RowIndex >= dgvDetail.Rows.Count || refreshing)
            return;

         GRSoft.Network.DataObject dataObject = (dgvDetail.Rows[e.RowIndex].DataBoundItem as OrderDetailRepresentation).StoreObject;
         BaseDocument bd = dataObject as BaseDocument;

         DateTime created = DateTime.Now;
         Org docOrg = null;

#if Demetra
         if (e.ColumnIndex == dgvDetailColumnOrg.DisplayIndex && bd != null && bd.Org != null)
            e.CellStyle.ForeColor = bd.Org.Color;
#endif

         if (DocCanMissed(bd))
         {
            if (config.highliteOrderMissed && OrderMissed(bd, dsOrderCommitted))
            {
               e.CellStyle.BackColor = Color.Orange;
               return;
            }
         }

         if (dataObject is Org)
         {
            e.CellStyle.BackColor = Color.Aqua;
            return;
         }

#if ORG_HAVE_GPS_LOCATION
         if (dataObject != null && dataObject is Answer && e.ColumnIndex == 2 && ((Answer)dataObject).quest != null)
         {
            e.Value = String.Format("{0} ({1})", e.Value, (((Answer)dataObject).quest).Name);
            return;
         }

         if (dgvDetail.Columns[e.ColumnIndex].Name == "dgvDetailHacLos")
         {
            OrderDetailRepresentation odr = dgvDetail.Rows[e.RowIndex].DataBoundItem as OrderDetailRepresentation;

            if (odr != null && odr.NOrg != null)
               e.Value = odr.NOrg.latitude != 0.0 || odr.NOrg.longitude != 0.0;
            else
               e.Value = false;

            return;
         }
#endif

         if (bd != null)
         {
            created = bd.created;
            docOrg = bd.Org;
         }
         else
            GetDocData(out created, out docOrg, dataObject);

         if (docOrg == null)
         {
            e.CellStyle.BackColor = Color.White;
            return;
         }

#if !Demetra
         if (!IsCreatedBySelectedAgentRoute(docOrg, curUserID, created))
            e.CellStyle.ForeColor = Color.Gray;
#endif

         if (!IsDocCompleted(created, dataObject))
         {
            e.CellStyle.BackColor = Color.LightGray;
         }
         else
         {
            e.CellStyle.BackColor = Color.White;
         }
      }

      private void dgvDetail_ColumnHeaderMouseClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         int columnIndex = e.ColumnIndex;
         SortOrder curOrder = dgvDetail.Columns[columnIndex].HeaderCell.SortGlyphDirection;
         curOrder = SortOrderDetail(curOrder, dgvDetail.Columns[columnIndex].DataPropertyName, columnIndex);
         UpdateDetailTable(dgvDetail.CurrentRow);
      }

      //Выполнить сортировку документов 
      protected SortOrder SortOrderDetail(SortOrder curOrder, string propName, int columnIndex)
      {
         foreach (DataGridViewColumn column in dgvDetail.Columns)
         {
            column.HeaderCell.SortGlyphDirection = SortOrder.None;
         }

         switch (curOrder)
         {
            case SortOrder.Ascending: curOrder = SortOrder.Descending; break;
            case SortOrder.Descending:
            case SortOrder.None: curOrder = SortOrder.Ascending; break;
         }

         BindingSource bs = (BindingSource)dgvDetail.DataSource;

         if (bs != null)
         {
            OrdersDetail ordersDetail = (OrdersDetail)bs.DataSource;
            string[] fieldSort = null;

            switch (propName)
            {
               case "Org":
                  fieldSort = new string[] { "org", "dateCreated" };
                  break;
               case "Doctype":
                  fieldSort = new string[] { "doctype" };
                  break;
               case "DateExec":
                  fieldSort = new string[] { "dateExec" };
                  break;
               case "DateCreated":
                  fieldSort = new string[] { "dateCreated", "doctype" };
                  break;
               case "Sended":
                  fieldSort = new string[] { "sended" };
                  break;
               case "Sum":
                  fieldSort = new string[] { "sum" };
                  break;
               default:
                  fieldSort = new string[] { propName };
                  break;
            }

            if (fieldSort != null)
            {
               ordersDetail.DoSort(fieldSort, curOrder);
               dgvDetail.Refresh();
               dgvDetail.Columns[columnIndex].HeaderCell.SortGlyphDirection = curOrder;
               UpdateDetailTable(dgvDetail.CurrentRow);
            }
         }
         return curOrder;
      }

      protected virtual string[] GetOverrideSort()
      {
         return null;
      }

      public Dictionary<DateTime, bool> DocumentsComleted
      {
         get
         {
            return documetsCompleted;
         }
      }

      private void tsClienCard_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvDetail.CurrentRow;
         if (row != null)
         {
            OrderDetailRepresentation odr = row.DataBoundItem as OrderDetailRepresentation;
            if (odr != null)
               DoClientCardReport();
         }
      }

      virtual protected void DoClientCardReport()
      {
         Type cct = FormEntries.GetFormType(typeof(ClientCard));
         ConstructorInfo ci = cct.GetConstructor(new Type[] { });
         ClientCard cc = (ClientCard)ci.Invoke(new object[] { });
         cc.DoReport(dtpBegin.Value, dtpEnd.Value);
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         refreshing = false;
         DataModule.ClearEvents();

         Invoke(new EmptyParamHandler(delegate
         {
            FmWait.CloseForm();
            btnRefresh.Enabled = true;

            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }));
      }

      //Настройка контестного меню общей таблицы
      protected virtual void cmDgvDetail_Opening(object sender, CancelEventArgs e)
      {
         if (dgvDetail.CurrentRow == null)
         {
            e.Cancel = true;
            return;
         }

#if DETAIL_AGENT_TASK_MENU
         string taskMenuName = "tsiCreateTask";
         if( cmDgvDetail.Items.Find(taskMenuName, true).Length == 0 )
         {
            ToolStripItem tsi = new ToolStripMenuItem("Создать задачу", null);
            tsi.Click += (obj, ev) =>
            {
               OrgTaskInfo info = new OrgTaskInfo();
               OrderDetailRepresentation o = (OrderDetailRepresentation)dgvDetail.CurrentRow.DataBoundItem;
               info.id = o.NOrg.id;
               info.name = o.NOrg.name;
               Agent a = GetSelectedAgent();

               if (a != null)
                  FmAgentTaskList.ShowForm(info, dtpBegin.Value.Date, dtpEnd.Value.Date, a.id);
            };
            tsi.Name = taskMenuName;
            cmDgvDetail.Items.Add(tsi);
         }

         ToolStripItem[] ti = cmDgvDetail.Items.Find("miMakeDup", true);
         if (ti.Length > 0)
            ti[0].Visible = GetDupDataSet() != null;
#else
         if (GetDupDataSet() == null)
            e.Cancel = true;
#endif
      }

      protected virtual Order GetOrder(DataGridViewRow curRow)
      {
         Order ret = null;
         if (curRow != null)
            ret = (curRow.DataBoundItem as OrderDetailRepresentation).StoreObject as Order;

         return ret;
      }

      protected virtual Incass GetIncass(DataGridViewRow curRow)
      {
         Incass ret = null;
         if (curRow != null)
            ret = (curRow.DataBoundItem as OrderDetailRepresentation).StoreObject as Incass;

         return ret;
      }

      protected virtual IDataSet GetDuplicate(GRSoft.Network.DataObject dataObject)
      {
         return null;
      }

      protected virtual bool CanDuplicate(GRSoft.Network.DataObject dataObject) { return true; }

      protected IDataSet GetDupDataSet()
      {
         if (dgvDetail.CurrentRow == null)
            return null;

         IDataSet result = null;
         OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;

         if (odr != null)
         {
            GRSoft.Network.DataObject dataObject = odr.StoreObject;
            if (!CanDuplicate(dataObject))
               return null;

            result = GetDuplicate(dataObject);
            if (result != null)
               return result;

            if (dataObject is OrderCharge)
            {
               DataSet<int, OrderCharge> ord = new DataSet<int, OrderCharge>(OrderCharge.OBJECT_NAME, false, true);
               ord.UseReceivedFields = true;
               ord.Add(ord.Count, (OrderCharge)dataObject);

               result = ord;
            }
            else if (dataObject is Sales)
            {
               DataSet<int, Sales> ord = new DataSet<int, Sales>(Sales.OBJECT_NAME, false, true);
               ord.UseReceivedFields = true;
               ord.Add(ord.Count, (Sales)dataObject);

               result = ord;
            }
            else if (dataObject is Order)
            {
               DataSet<int, Order> ord = new DataSet<int, Order>(Order.OBJECT_NAME, false, true);
               ord.UseReceivedFields = true;
               ord.Add(ord.Count, (Order)dataObject);

               result = ord;
            }
            else if (dataObject is Incass)
            {
               DataSet<int, Incass> ird = new DataSet<int, Incass>(Incass.OBJECT_NAME, false, true);
               ird.UseReceivedFields = true;
               ird.Add(ird.Count, (Incass)dataObject);

               result = ird;
            }
            else if (dataObject is Returns)
            {
               DataSet<int, Returns> ord = new DataSet<int, Returns>(Returns.OBJECT_NAME, false, true);
               ord.UseReceivedFields = true;
               ord.Add(ord.Count, (Returns)dataObject);

               result = ord;
            }
            else if (dataObject is PKO)
            {
               SimpleDataSet<PKO> ird = new SimpleDataSet<PKO>(PKO.OBJECT_NAME, false, true);
               ird.UseReceivedFields = true;
               ird.Add((PKO)dataObject);

               result = ird;
            }
         }

         return result;
      }

      //Продублировать заявку в базе данных
      protected virtual void miMakeDup_Click(object sender, EventArgs e)
      {
         IDataSet dupDS = GetDupDataSet();

         if (dupDS != null)
         {
#if PavlovaOI
            if( !CheckAdmin.CheckPassword() )
               return;
#endif
            List<IDataSet> update = new List<IDataSet>();
            update.Add(dupDS);
            Config cfg = Config.GetConfig();

            if (DataModule.UpdateDataSet(update, null, null, cfg.GetConnection(), GetSelectedIdAgent()))
               MessageBox.Show("Операция завершена успешно", "Информация", MessageBoxButtons.OK,
                  MessageBoxIcon.Information);
            else
               MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
                  MessageBoxIcon.Error);
         }
      }

      private void dgvDetail_MouseDown(object sender, MouseEventArgs e)
      {
         if (e.Button == MouseButtons.Right)
         {
            DataGridView.HitTestInfo info = dgvDetail.HitTest(e.X, e.Y);

            if (info.ColumnIndex == -1 || info.RowIndex == -1)
            {
               return;
            }

            dgvDetail.CurrentCell = dgvDetail[info.ColumnIndex, info.RowIndex];
         }
      }

      private void lbNotes_DoubleClick(object sender, EventArgs e)
      {
         new CommentWindow().Show(((Label)sender).Text);
      }

      private void dgvDetail_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         UpdateDetailTable(dgvDetail.Rows[e.RowIndex]);
      }

      private void cmPhotoRating_ItemClicked(object sender, ToolStripItemClickedEventArgs e)
      {
         ListView.SelectedListViewItemCollection sel = lvPhoto.SelectedItems;
         int rate;
         if (!Int32.TryParse(e.ClickedItem.Text, out rate))
            rate = 0;

         DataSet<int, Visit> upd = new DataSet<int, Visit>(Visit.OBJECT_NAME, false);
         List<Visit> used = new List<Visit>();
         foreach (ListViewItem lvi in sel)
         {
            VisitTag vt = lvi.Tag as VisitTag;
            if (vt != null)
            {
               vt.item.rating = rate;
               if (used.Contains(vt.visit) == false)
               {
                  used.Add(vt.visit);
                  upd.Add(upd.Count, vt.visit);
               }
            }
         }

         foreach (Visit v in used)
            v.RefreshRating();

         if (upd.Count > 0)
         {
            List<IDataSet> wrList = new List<IDataSet>();
            wrList.Add(upd);
            if (DataModule.UpdateDataSet(wrList, null, null, Config.GetConfig().GetConnection(), GetSelectedIdAgent()))
            {
               foreach (ListViewItem lvi in sel)
               {
                  string tag = lvi.Text;
                  int idx = tag.IndexOf('\n');
                  if (idx >= 0)
                     tag = tag.Substring(0, idx);

                  if (rate > 0)
                     tag += "\nОценка: " + rate.ToString();
                  lvi.Text = tag;
               }
            }
         }
      }

      private void tbPhotoRate_Click(object sender, EventArgs e)
      {
         FmPhotoRateReport rep = new FmPhotoRateReport();
         rep.SetSelectedAgent(GetSelectedIdAgent());
         rep.Show();
      }

      private void lbNotes_Click(object sender, EventArgs e)
      {
         tooltipRemark.SetToolTip(((Label)sender), ((Label)sender).Text);
      }

      private void lbNotes_MouseLeave(object sender, EventArgs e)
      {
         tooltipRemark.Hide(lbNotes);
      }

      private void btnCoverArea_Click(object sender, EventArgs e)
      {
         //new FmCoverArea(GetSelectedIdAgent(), GetStartDate()).Show();
         if (!WebViewWarning.IsWebViewExists())
         {
            WebViewWarning.Open();
            return;
         }

         Type prcType = FormEntries.GetFormType(typeof(FmCoverArea));
         ConstructorInfo ci = prcType.GetConstructor(new Type[] { typeof(string), typeof(DateTime) });
         Form fm = (Form)ci.Invoke(new object[] { GetSelectedIdAgent(), dtpBegin.Value.Date });
         fm.Show();
      }

      private void dgvDetail_DataError(object sender, DataGridViewDataErrorEventArgs e)
      {
         DataGridView view = (DataGridView)sender;
         if (view != null)
         {
            view.Rows[e.RowIndex].ErrorText = e.Exception.Message;
            view.Rows[e.RowIndex].Cells[e.ColumnIndex].ErrorText = e.Exception.Message;
         }
         e.ThrowException = false;
      }

      internal static bool IsCreatedBySelectedAgentRoute(Org org, string agentid, DateTime created)
      {
         Agents agents = Agents.GetDataSet();
         if (agents.ContainsKey(agentid) == false)
            return false;

         List<OrgFolderItem> items = null;

#if ROUTE_HISTORY
         if( routeHelpers.ContainsKey(agentid) )
         {
            AgentRouteSheduleHelper rh = routeHelpers[agentid];
            items = rh.GetRoute(agents[agentid], created.Date);
         }

         if (items == null || items.Count == 0)
         {
            DataSet<int, CommonConfig> configs = (DataSet<int, CommonConfig>)DataModule.Get(CommonConfig.OBJECT_NAME);
            SummaryData sd = SummaryData.Create(agents[agentid], configs);
            DataSet<int, OrgFolder> routes;
            routes = (DataSet<int, OrgFolder>)DataModule.GetUserDataSet(agentid, OrgFolder.OBJECT_NAME, typeof(DataSet<int, OrgFolder>));
            if (routes.Count == 0)
               routes = (DataSet<int, OrgFolder>)DataModule.Get(OrgFolder.OBJECT_NAME);

            items = sd.GetAgentRoute(created, routes.Data);
         }
#else
         DataSet<int, CommonConfig> configs = (DataSet<int, CommonConfig>)DataModule.Get(CommonConfig.OBJECT_NAME);
         SummaryData sd = SummaryData.Create(agents[agentid], configs);
         DataSet<int, OrgFolder> routes;
         routes = (DataSet<int, OrgFolder>)DataModule.GetUserDataSet(agentid, OrgFolder.OBJECT_NAME, typeof(DataSet<int, OrgFolder>));
         if (routes.Count == 0)
            routes = (DataSet<int, OrgFolder>)DataModule.Get(OrgFolder.OBJECT_NAME);

         items = sd.GetAgentRoute(created, routes.Data);
#endif

         if (items != null)
         {
            foreach (OrgFolderItem ofi in items)
               if (ofi.name.Equals(org.id))
                  return true;
         }

         return false;
      }

      public virtual bool LoadIntDocument(BaseDocument doc)
      {
         return true;
      }

   }

   /// <summary>
   /// Данные условия для выборки 
   /// </summary>
   public class FmDetailData
   {
      private string agentId;
      private DateTime dateBegin;
      private DateTime dateEnd;
      private ObjType orderType;
      public FmDetailBase fmDetail;

      public FmDetailData(string agent, DateTime begin, DateTime end, ObjType orderType)
      {
         agentId = agent;
         dateBegin = begin;
         dateEnd = end;
         this.orderType = orderType;
      }

      public void ClearOrderType() { orderType = null; }

      public string AgentId { get { return agentId; } }
      public DateTime DateBegin { get { return dateBegin; } }
      public DateTime DateEnd { get { return dateEnd; } }
      public ObjType OrderType { get { return orderType; } }

      public bool filter()
      {
         return true;
      }
   }

#if QUESTION
   class AnswerDetailRepresentation : OrderDetailRepresentation
   {
      public AnswerDetailRepresentation(DateTime created, ObjType doctype,
         DateTime date, DateTime sended, Org org, double sum, double isum, int qty,
         GRSoft.Network.DataObject dataObject, bool oneDay) :
         base(created, doctype, date, sended, org, sum, isum, qty, dataObject, oneDay)
      {

      }

      public AnswerDetailRepresentation(BaseDocument doc, ObjType doctype, bool oneDay) :
         base(doc, doctype, oneDay)
      {

      }

      public override int CompareTo(OrderDetailRepresentation other)
      {
         if (other is AnswerDetailRepresentation && CC.Fields[0].Equals("doctype"))
         {
            Answer ans1 = StoreObject as Answer;
            Answer ans2 = ((AnswerDetailRepresentation)other).StoreObject as Answer;

            if (ans1 != null && ans2 != null && ans1.quest != null && ans2.quest != null)
               return CC.IsAscending ? ans1.quest.Name.CompareTo(ans2.quest.Name)
                  : ans2.quest.Name.CompareTo(ans1.quest.Name);
            else if (ans1.quest == null && ans2.quest != null)
               return CC.IsAscending ? -1 : 1;
            else if (ans2.quest == null && ans1.quest != null)
               return CC.IsAscending ? 1 : -1;
         }

         return base.CompareTo(other);
      }
   }
#endif

   /// Набор данных для представления
   public class OrdersDetail : List<OrderDetailRepresentation>
   {
      protected string visitName = Visit.OBJECT_NAME;
      protected int docCount;
      protected double sum;
      protected double incassSum;
      protected double weight;
      protected List<string> usedDocs = new List<string>();

      protected List<DocumentInfo> documents;

      public OrdersDetail() { }
      public OrdersDetail(List<DocumentInfo> documents) { this.documents = documents; }

      protected List<ObjType> filtersAvailable = new List<ObjType>();

      public string VisitName { get { return visitName; } set { visitName = value; } }

      public int OrderCount { get { return docCount; } }
      public double Sum { get { return sum; } }
      public double IncassSum { get { return incassSum; } }
      public double Weight { get { return weight; } }

      public void DoSort(string[] cmpFields, SortOrder sortOrder)
      {
         OrderDetailRepresentation.CC.SetCompareCondition(cmpFields, sortOrder == SortOrder.Ascending);
         Sort();
      }

      protected List<Org> MakeUniqOrgList(Dictionary<DateTime, List<OrgFolderItem>> droute)
      {
         List<Org> routes = new List<Org>();
         foreach (KeyValuePair<DateTime, List<OrgFolderItem>> kv in droute)
            foreach (OrgFolderItem ofi in kv.Value)
               if (routes.Contains(ofi.org) == false)
                  routes.Add(ofi.org);

         return routes;
      }

      OrgRouteOrder FindOrg(DateTime dt, OrderDetailRepresentation src, List<OrgFolderItem> orgs)
      {
         int index = 1;
         foreach (OrgFolderItem o in orgs)
         {
            if (src.NOrg == o.org)
               return new OrgRouteOrder(src, dt, index, this, o);
            index++;
         }
         return null;
      }

      OrgRouteOrder MakeFromOrg(OrderDetailRepresentation src, Dictionary<DateTime, List<OrgFolderItem>> droute)
      {
         OrgRouteOrder res = null;
         foreach (KeyValuePair<DateTime, List<OrgFolderItem>> kv in droute)
            if ((res = FindOrg(kv.Key, src, kv.Value)) != null)
               break;

         return res;
      }

      OrgRouteOrder MakeFromDate(DateTime dt, OrderDetailRepresentation src, Dictionary<DateTime, List<OrgFolderItem>> droute)
      {
         OrgRouteOrder res = null;
         if (droute.ContainsKey(dt.Date))
            res = FindOrg(dt.Date, src, droute[dt.Date]);

         return res;
      }

      void SetRouteOrder(Dictionary<DateTime, List<OrgFolderItem>> droute)
      {
         foreach (OrderDetailRepresentation item in this)
         {
            DateTime dt = item.DateCreatedDT;
            if (dt == DateTime.MinValue)
               item.RouteOrder = MakeFromOrg(item, droute);
            else
               item.RouteOrder = MakeFromDate(dt, item, droute);

            if (item.RouteOrder == null)
               item.RouteOrder = new OrgRouteOrder(item, item.DateCreatedDT.Date, -1, this, null);

            item.SetOwner(this);
         }
      }

      protected virtual bool NeedAddNotVisited(FmDetailData cond, bool checkRoute, List<Org> routes)
      {
         return cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.NotVisit) : !checkRoute && routes != null;
      }

      public List<Org> Load(FmDetailData cond, bool oneDay, Agent agent)
      {
         //List<Org> routes = GetRoutePeriod(cond.DateBegin, cond.DateEnd, agent);
         Dictionary<DateTime, List<OrgFolderItem>> droute = GetAgentRoute(cond.DateBegin, cond.DateEnd, agent);
         List<Org> routes = MakeUniqOrgList(droute);

         Clear();

         docCount = 0;
         sum = 0;
         incassSum = 0;
         weight = 0;

         filtersAvailable.Clear();

         bool checkRoute = false;
         if (routes != null && cond.OrderType != null && cond.OrderType.Equals(ObjType.TObjType.OutRoute))
         {
            cond.ClearOrderType();
            checkRoute = true;
         }

         bool census = false;
         if (cond.OrderType != null && cond.OrderType.Equals(ObjType.TObjType.Census))
         {
            cond.ClearOrderType();
            census = true;
         }

         usedDocs.Clear();
         LoadInt(cond, oneDay, checkRoute, agent.id, routes);

         if (NeedAddNotVisited(cond, checkRoute, routes))
         {
            Dictionary<string, bool> haveOrg = new Dictionary<string, bool>();
            if (cond != null && cond.OrderType != null && cond.OrderType.Equals(ObjType.TObjType.NotVisit))
            {
               haveOrg = LoadHaveDocs(usedDocs);
            }
            else
            {
               foreach (OrderDetailRepresentation str in this)
               {
                  if (str.NOrg != null)
                     haveOrg[str.NOrg.id] = true;
               }
            }

            CreateNotVisitedList(oneDay, routes, haveOrg);
         }

         if (census)
         {
            List<OrderDetailRepresentation> rep = new List<OrderDetailRepresentation>();

            foreach (OrderDetailRepresentation odr in this)
               if (odr.Org != null && odr.NOrg is PotenzialOrg)
                  rep.Add(odr);

            Clear();
            AddRange(rep);
         }

         filtersAvailable.Add(new ObjType(ObjType.TObjType.NotVisit));
         filtersAvailable.Add(new ObjType(ObjType.TObjType.OutRoute));

#if CENSUS_FILTER
         filtersAvailable.Add(new ObjType(ObjType.TObjType.Census));
#endif

         SetRouteOrder(droute);
         return routes;
      }

      protected Dictionary<string, bool> LoadHaveDocs(List<string> docs)
      {
         Dictionary<string, bool> haveOrg = new Dictionary<string, bool>();

         foreach (string doc in docs)
         {
            IDataSet ds = DataModule.Get(doc);
            if (ds == null)
               continue;

            foreach (BaseDocument bd in ds.Data)
               haveOrg[bd.id] = true;
         }
         return haveOrg;
      }

      protected virtual void CreateNotVisitedList(bool oneDay, List<Org> routes, Dictionary<string, bool> haveOrg)
      {
         foreach (Org org in routes)
         {
            if (org == null || haveOrg.ContainsKey(org.id))
               continue;
            AddNotVisitedOrg(oneDay, org);
         }
      }

      protected virtual void AddNotVisitedOrg(bool oneDay, Org org)
      {
         //if (OrgNotInVisit(org))
         {
            Add(new OrderDetailRepresentation(DateTime.MinValue,
               new ObjType(ObjType.TObjType.NotVisit),
               DateTime.MinValue, DateTime.MinValue, org, 0, 0, 0, org, oneDay));
         }
      }

      protected virtual OrderDetailRepresentation CreateOrderRow(Order order, bool oneDay)
      {
         return new OrderDetailRepresentation(order, new ObjType(ObjType.TObjType.OtOrder), oneDay);
      }

      protected virtual void LoadInt(FmDetailData cond, bool oneDay, bool checkRoute, string agentID, List<Org> routes)
      {
         IDataSet cdata = DataModule.Get(DayDoc.OBJECT_NAME);
         CheckFiltersForDocType(cdata, ObjType.TObjType.DayDoc, filtersAvailable);

         if (cdata != null && cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.DayDoc) : true)
         {
            foreach (DayDoc dayDoc in cdata.Data)
            {
               Add(new OrderDetailRepresentation(dayDoc.end,
                  new ObjType(ObjType.TObjType.DayDoc),
                  dayDoc.end, DateTime.MinValue, null, 0, 0, 0, dayDoc, oneDay));
            }
         }

         usedDocs.Add(Order.OBJECT_NAME);
         cdata = DataModule.Get(Order.OBJECT_NAME);
         CheckFiltersForDocType(cdata, ObjType.TObjType.OtOrder, filtersAvailable);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.OtOrder) : true && cdata != null)
         {
            foreach (Order order in cdata.Data)
            {
               if (checkRoute && FmDetailBase.IsCreatedBySelectedAgentRoute(order.org, agentID, order.created))
                  continue;

               if (!LoadIntDocument(cond, order))
                  continue;

               docCount++;
               sum += order.DSum;
               weight += order.Weight;

               Add(CreateOrderRow(order, oneDay));
            }
         }

#if ORDER_CHARGE
         cdata = DataModule.Get(OrderCharge.OBJECT_NAME);
         CheckFiltersForDocType(cdata, ObjType.TObjType.OrderCharge, filtersAvailable);
         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.OrderCharge) : true && cdata != null)
         {
            foreach (Order order in cdata.Data)
            {
               if (checkRoute &&
                  FmDetailBase.IsCreatedBySelectedAgentRoute(
                  order.org, agentID, order.created))
                  continue;

               if (!LoadIntDocument(cond, order))
                  continue;

               docCount++;
               sum += order.DSum;
               weight += order.Weight;

               Add(new OrderDetailRepresentation(order, new ObjType(ObjType.TObjType.OrderCharge),oneDay));
            }
         }
#endif

#if AliansFood
         cdata = DataModule.Get("ArchIncass");
         CheckFiltersForDocType(cdata, ObjType.TObjType.ArchIncass, filtersAvailable);
         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.ArchIncass) : true && cdata != null)
         {
            foreach (PKO pko in cdata.Data)
            {
               if (checkRoute && FmDetailBase.IsCreatedBySelectedAgentRoute(
                  pko.org, agentID, pko.created))
                  continue;

               if (!LoadIntDocument(cond, pko))
                  continue;

               docCount++;
               sum += pko.Sum();

               Add(new OrderDetailRepresentation(pko, new ObjType(ObjType.TObjType.ArchIncass), oneDay));
            }
         }
#endif

#if Burov || Alecon
         cdata = DataModule.Get(OrderW.OBJECT_NAME);
         CheckFiltersForDocType(cdata, ObjType.TObjType.OrderW, filtersAvailable);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.OrderW) : true && cdata != null)
         {
            foreach (Order order in cdata.Data)
            {
               if (checkRoute &&
                  FmDetailBase.IsCreatedBySelectedAgentRoute(
                  order.org, agentID, order.created))
                  continue;

               if (!LoadIntDocument(cond, order))
                  continue;

               docCount++;
               sum += order.DSum;
               Add(new OrderDetailRepresentation(order,new ObjType(ObjType.TObjType.OrderW), oneDay));
            }
         }
#endif

         usedDocs.Add(visitName);
         cdata = DataModule.Get(visitName);
         CheckFiltersForDocType(cdata, ObjType.TObjType.OtVisit, filtersAvailable);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.OtVisit) : true && cdata != null)
         {
            foreach (Visit visit in cdata.Data)
            {
               if (checkRoute &&
                  FmDetailBase.IsCreatedBySelectedAgentRoute(visit.org,
                  agentID, visit.date))
                  continue;

               if (!LoadIntDocument(cond, visit))
                  continue;

               docCount++;

               Add(new OrderDetailRepresentation(visit, new ObjType(ObjType.TObjType.OtVisit), oneDay));
            }
         }

         usedDocs.Add(OrgRemnants.OBJECT_NAME);
         cdata = DataModule.Get(OrgRemnants.OBJECT_NAME);
         CheckFiltersForDocType(cdata, ObjType.TObjType.OtOrgRemnants, filtersAvailable);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.OtOrgRemnants) : true && cdata != null)
         {
            foreach (OrgRemnants remnants in cdata.Data)
            {
               if (checkRoute &&
                  FmDetailBase.IsCreatedBySelectedAgentRoute(remnants.org,
                  agentID, remnants.date))
                  continue;

               if (!LoadIntDocument(cond, remnants))
                  continue;

               docCount++;

               Add(new OrderDetailRepresentation(remnants, new ObjType(ObjType.TObjType.OtOrgRemnants), oneDay));
            }
         }

         bool pkoAdded = false;
         usedDocs.Add(PKO.OBJECT_NAME);
         cdata = DataModule.Get(PKO.OBJECT_NAME);
         CheckFiltersForDocType(cdata, ObjType.TObjType.PKO, filtersAvailable);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.PKO) : true && cdata != null)
         {
            foreach (PKO pko in cdata.Data)
            {
               if (checkRoute && FmDetailBase.IsCreatedBySelectedAgentRoute(
                  pko.org, agentID, pko.created))
                  continue;

               if (!LoadIntDocument(cond, pko))
                  continue;

               pkoAdded = true;
               docCount++;
               incassSum += pko.Sum();

               Add(new OrderDetailRepresentation(pko, new ObjType(ObjType.TObjType.PKO), oneDay));
            }
         }

         usedDocs.Add(Incass.OBJECT_NAME);
         cdata = DataModule.Get(Incass.OBJECT_NAME);
         if (!pkoAdded)
            CheckFiltersForDocType(cdata, ObjType.TObjType.PKO, filtersAvailable);
         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.PKO) : true && cdata != null)
         {
            foreach (Incass pko in cdata.Data)
            {
               if (checkRoute && FmDetailBase.IsCreatedBySelectedAgentRoute(
                  pko.org, agentID, pko.created))
                  continue;

               if (!LoadIntDocument(cond, pko))
                  continue;

               pkoAdded = true;
               docCount++;
               incassSum += pko.Sum();

               Add(new OrderDetailRepresentation(pko, new ObjType(ObjType.TObjType.PKO), oneDay));
            }
         }

         usedDocs.Add(Returns.OBJECT_NAME);
         cdata = DataModule.Get(Returns.OBJECT_NAME);
         CheckFiltersForDocType(cdata, ObjType.TObjType.OtReturn, filtersAvailable);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.OtReturn) : true && cdata != null)
         {
            foreach (Returns returns in cdata.Data)
            {
               if (checkRoute &&
                  FmDetailBase.IsCreatedBySelectedAgentRoute(returns.org,
                  agentID, returns.date))
                  continue;

               if (!LoadIntDocument(cond, returns))
                  continue;

               docCount++;
               sum += returns.Sum();

               Add(new OrderDetailRepresentation(returns, new ObjType(ObjType.TObjType.OtReturn), oneDay));
            }
         }

         usedDocs.Add(Sales.OBJECT_NAME);
         cdata = DataModule.Get(Sales.OBJECT_NAME);
         CheckFiltersForDocType(cdata, ObjType.TObjType.Sales, filtersAvailable);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.Sales) : true && cdata != null)
         {
            foreach (Sales sales in cdata.Data)
            {
               if (checkRoute &&
                  FmDetailBase.IsCreatedBySelectedAgentRoute(
                  sales.org, agentID, sales.created))
                  continue;

               if (!LoadIntDocument(cond, sales))
                  continue;

               docCount++;
               sum += sales.DSum;

               Add(new OrderDetailRepresentation(sales, new ObjType(ObjType.TObjType.Sales), oneDay));
            }
         }

         usedDocs.Add(MoneyProxy.OBJECT_NAME);
         cdata = DataModule.Get(MoneyProxy.OBJECT_NAME);
         CheckFiltersForDocType(cdata, ObjType.TObjType.MoneyProxy, filtersAvailable);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.MoneyProxy) : true && cdata != null)
         {
            foreach (MoneyProxy doc in cdata.Data)
            {
               if (checkRoute &&
                   FmDetailBase.IsCreatedBySelectedAgentRoute(doc.org,
                   agentID, doc.date))
                  continue;

               if (!LoadIntDocument(cond, doc))
                  continue;

               docCount++;
               sum += doc.Sum();

               Add(new OrderDetailRepresentation(doc, new ObjType(ObjType.TObjType.MoneyProxy), oneDay));
            }
         }



#if DISTR_DOC
         cdata = DataModule.Get(Distr.OBJECT_NAME);
         CheckFiltersForDocType(cdata, ObjType.TObjType.Distr, filtersAvailable);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.Distr) : true && cdata != null)
         {
            foreach (Distr distr in cdata.Data)
            {
               if (checkRoute &&
                  FmDetailBase.IsCreatedBySelectedAgentRoute(
                  distr.org, agentID, distr.created))
                  continue;

               if (!LoadIntDocument(cond, distr))
                  continue;

               docCount++;

               Add(new OrderDetailRepresentation(distr, new ObjType(ObjType.TObjType.Distr), oneDay));
            }
         }
#endif
#if MOVEMENT_DOC
         cdata = DataModule.Get(MoveDoc.OBJECT_NAME);
         CheckFiltersForDocType(cdata, ObjType.TObjType.Move, filtersAvailable);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.Move) : true && cdata != null)
         {
            foreach (MoveDoc move in cdata.Data)
            {
               if (checkRoute &&
                  FmDetailBase.IsCreatedBySelectedAgentRoute(
                  move.org, agentID, move.created))
                  continue;

               if (LoadIntDocument(cond, move))
                  continue;

               docCount++;
               sum += move.Sum();

               Add(new OrderDetailRepresentation(move, new ObjType(ObjType.TObjType.Move), oneDay));
            }
         }
#endif
#if QUESTION
         usedDocs.Add(Answer.OBJECT_NAME);
         cdata = DataModule.Get(Answer.OBJECT_NAME);
         CheckFiltersForDocType(cdata, ObjType.TObjType.Answer, filtersAvailable);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.Answer) : true && cdata != null)
         {
            foreach (Answer answer in cdata.Data)
            {
               if (checkRoute &&
                  FmDetail.IsCreatedBySelectedAgentRoute(answer.org,
                  agentID, answer.created))
                  continue;

               if (!LoadIntDocument(cond, answer))
                  continue;

               docCount++;
               Add(new AnswerDetailRepresentation(answer, new ObjType(ObjType.TObjType.Answer), oneDay));
            }
         }
#endif

#if PRICE_MONITORING
         cdata = DataModule.Get(Monitoring.OBJECT_NAME);
         CheckFiltersForDocType(cdata, ObjType.TObjType.Monitoring, filtersAvailable);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.Monitoring) : true && cdata != null)
         {
            foreach (Monitoring doc in cdata.Data)
            {
               if (checkRoute &&
                  FmDetail.IsCreatedBySelectedAgentRoute(doc.org,
                  agentID, doc.date))
                  continue;

               if (!LoadIntDocument(cond, doc))
                  continue;

               docCount++;

               Add(new OrderDetailRepresentation(doc, new ObjType(ObjType.TObjType.Monitoring), oneDay));
            }
         }
#endif
#if INVOICE_DOC
         cdata = DataModule.Get(Invoice.OBJECT_NAME);
         CheckFiltersForDocType(cdata, ObjType.TObjType.Invoice, filtersAvailable);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.Invoice) : true && cdata != null)
         {
            foreach (Invoice rmn in cdata.Data)
            {
               if (checkRoute &&
                  FmDetailBase.IsCreatedBySelectedAgentRoute(
                  rmn.org, agentID, rmn.created))
                  continue;

               docCount++;

               Add(new OrderDetailRepresentation(rmn.created, new ObjType(ObjType.TObjType.Invoice), oneDay));
            }
         }
#endif
         if (documents != null)
         {
            foreach (DocumentInfo di in documents)
            {
               if (!LoadIntDocument(cond, di))
                  continue;

               usedDocs.Add(di.DataSet.Name);

               cdata = di.DataSet;
               CheckFiltersForDocType(cdata, di.Type, filtersAvailable);
               if (cdata != null && cond.OrderType != null ? cond.OrderType.Equals(di.Type) : true)
               {
                  foreach (BaseDocument doc in cdata.Data)
                  {
                     if (checkRoute && FmDetail.IsCreatedBySelectedAgentRoute(doc.org, agentID, doc.date))
                        continue;
                     //Add(new OrderDetailRepresentation(doc, new ObjType(di.Type), oneDay));
                     Add(CreateDocRepr(doc, di.Type, oneDay));
                     docCount++;
                     sum += doc.Sum();
                  }
               }
            }
         }
      }

      protected virtual OrderDetailRepresentation CreateDocRepr(BaseDocument doc, ObjType.TObjType docType, bool oneDay)
      {
         return new OrderDetailRepresentation(doc, new ObjType(docType), oneDay);
      }

      protected virtual bool LoadIntDocument(FmDetailData fdd, DocumentInfo di)
      {
         return true;
      }

      protected virtual bool LoadIntDocument(FmDetailData fdd, BaseDocument doc)
      {
         return fdd.fmDetail.LoadIntDocument(doc);
      }

      //Проверяем содержит ли набора данных записи, если содержит, то 
      //добавляем в список фильтров необходимый фильтр
      protected void CheckFiltersForDocType(IDataSet cdata, ObjType.TObjType checkingType,
         List<ObjType> filters)
      {
         if (cdata != null && cdata.Data != null && cdata.Data.Count > 0)
            filtersAvailable.Add(new ObjType(checkingType));
      }

      // Возвращает true, если организация не встречалась в заявках или визитах
      internal static bool OrgNotInVisit(Org org)
      {
         DataSet<int, Order> dsOrder = (DataSet<int, Order>)DataModule.Get(Order.OBJECT_NAME);
         DataSet<int, OrderW> dsOrderW = (DataSet<int, OrderW>)DataModule.Get(OrderW.OBJECT_NAME);
         DataSet<int, Visit> dsVisit = (DataSet<int, Visit>)DataModule.Get(Visit.OBJECT_NAME);
         DataSet<int, Incass> dsIncass = (DataSet<int, Incass>)DataModule.Get(Incass.OBJECT_NAME);
         DataSet<int, Sales> dsSales = (DataSet<int, Sales>)DataModule.Get(Sales.OBJECT_NAME);
         DataSet<int, PKO> dsPKO = (DataSet<int, PKO>)DataModule.Get(PKO.OBJECT_NAME);
         DataSet<int, OrgRemnants> dsRemnants = (DataSet<int, OrgRemnants>)DataModule.Get(OrgRemnants.OBJECT_NAME);

#if QUESTION
         DataSet<int, Answer> dsAnswer = (DataSet<int, Answer>)DataModule.Get(Answer.OBJECT_NAME);
#endif

         if (dsOrder != null)
            foreach (Order order in dsOrder.Data)
            {
               if (order.id.CompareTo(org.id) == 0)
                  return false;
            }
#if Burov || Alecon
         if (dsOrderW != null)
            foreach (Order order in dsOrderW.Data)
            {
               if (order.id.CompareTo(org.id) == 0)
                  return false;
            }
#endif
         if (dsVisit != null)
            foreach (Visit visit in dsVisit.Data)
            {
               if (visit.id.CompareTo(org.id) == 0)
                  return false;
            }

#if QUESTION
         if (dsAnswer != null)
            foreach (Answer answer in dsAnswer.Data)
            {
               if (answer != null &&
                     answer.org != null &&
                     answer.org.id.CompareTo(org.id) == 0)
                  return false;
            }
#endif
         if (dsIncass != null)
            foreach (Incass incass in dsIncass.Data)
            {
               if (incass.id.CompareTo(org.id) == 0)
                  return false;
            }

         if (dsSales != null)
            foreach (Sales sales in dsSales.Data)
            {
               if (sales.id.CompareTo(org.id) == 0)
                  return false;
            }

         if (dsPKO != null)
            foreach (PKO pko in dsPKO.Data)
            {
               if (pko.id.CompareTo(org.id) == 0)
                  return false;
            }

         if (dsRemnants != null)
            foreach (OrgRemnants or in dsRemnants.Data)
            {
               if (or.id.CompareTo(org.id) == 0)
                  return false;
            }


         return true;
      }

      public static Dictionary<DateTime, List<OrgFolderItem>> GetAgentRoute(DateTime begin, DateTime end, Agent agent)
      {
         Dictionary<DateTime, List<OrgFolderItem>> route = new Dictionary<DateTime, List<OrgFolderItem>>();
#if ROUTE_HISTORY
         if (FmDetailBase.routeHelpers.ContainsKey(agent.id))
         {
            AgentRouteSheduleHelper rh = FmDetailBase.routeHelpers[agent.id];

            while (begin.Date < end.Date)
            {
               List<OrgFolderItem> items = rh.GetRoute(agent, begin);
               if (items.Count > 0)
               {
                  List<Org> orgs = new List<Org>();
                  route[begin.Date] = orgs;
                  foreach (OrgFolderItem item in items)
                     if (item.org != null && orgs.Contains(item.org) == false)
                        orgs.Add(item.org);
               }

               begin = begin.AddDays(1);
            }

            if (route.Count > 0)
               return route;
         }
#endif

         DataSet<int, OrgFolder> routes = (DataSet<int, OrgFolder>)DataModule.GetUserDataSet(agent.id, OrgFolder.OBJECT_NAME, typeof(DataSet<int, OrgFolder>));
         if (routes.Count == 0)
            routes = (DataSet<int, OrgFolder>)DataModule.Get(OrgFolder.OBJECT_NAME);
         DataSet<int, CommonConfig> configs = (DataSet<int, CommonConfig>)DataModule.Get(CommonConfig.OBJECT_NAME);

         SummaryData sd = SummaryData.Create(agent, configs);
         while (begin.Date < end.Date)
         {
            WeekDay weekDay = new WeekDay(begin.DayOfWeek);

            List<OrgFolderItem> items = sd.GetAgentRoute(begin, routes.Data);
            if (items != null)
            {
               Dictionary<string, bool> used = new Dictionary<string, bool>();
               List<OrgFolderItem> orgs = new List<OrgFolderItem>();
               route[begin.Date] = orgs;
               foreach (OrgFolderItem item in items)
                  if (item.org != null)
                  {
                     if (!used.ContainsKey(item.org.id))
                     {
                        orgs.Add(item);
                        used.Add(item.org.id, true);
                     }
                  }
            }

            begin = begin.AddDays(1);
         }

         return route;
      }

      //Возвращает список организаций - маршрут за период
      public static List<Org> GetRoutePeriod(DateTime begin, DateTime end, Agent agent)
      {
         List<Org> result = new List<Org>();

#if ROUTE_HISTORY
         if (FmDetailBase.routeHelpers.ContainsKey(agent.id))
         {
            AgentRouteSheduleHelper rh = FmDetailBase.routeHelpers[agent.id];

            while (begin.Date < end.Date)
            {
               List<OrgFolderItem> items = rh.GetRoute(agent, begin);
               foreach (OrgFolderItem item in items)
                  if (item.org != null && result.Contains(item.org) == false)
                     result.Add(item.org);

               begin = begin.AddDays(1);
            }

            if (result.Count > 0)
               return result;
         }
#endif

         //List<string> dayProcessed = new List<string>();
         DataSet<int, OrgFolder> routes;
         routes = (DataSet<int, OrgFolder>)DataModule.GetUserDataSet(agent.id, OrgFolder.OBJECT_NAME, typeof(DataSet<int, OrgFolder>));
         if (routes.Count == 0)
            routes = (DataSet<int, OrgFolder>)DataModule.Get(OrgFolder.OBJECT_NAME);
         DataSet<int, CommonConfig> configs = (DataSet<int, CommonConfig>)DataModule.Get(CommonConfig.OBJECT_NAME);

         SummaryData sd = SummaryData.Create(agent, configs);

         while (begin.Date < end.Date)
         {
            WeekDay weekDay = new WeekDay(begin.DayOfWeek);

            List<OrgFolderItem> items = sd.GetAgentRoute(begin, routes.Data);
            if (items != null)
            {
               foreach (OrgFolderItem item in items)
                  if (!result.Contains(item.org))
                     result.Add(item.org);
            }

            begin = begin.AddDays(1);
         }

         return result;
      }


      public List<ObjType> FiltersAvailable { get { return filtersAvailable; } }
   }

   internal class CommentWindow : Form
   {
      private TextBox tbNotes;

      public CommentWindow()
      {
         FormBorderStyle = FormBorderStyle.FixedDialog;
         StartPosition = FormStartPosition.Manual;
         Size = new Size(200, 100);

         tbNotes = new TextBox();
         tbNotes.Multiline = true;
         tbNotes.Dock = DockStyle.Fill;

         Controls.Add(tbNotes);
      }

      public void Show(string text)
      {
         Location = Cursor.Position;
         tbNotes.Text = text.Substring(12);
         Show();
      }
   }
   public class HtmlWriter : StreamWriter
   {
      private static string HTML_BEGIN = "<html><head>" +
            "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"></head>" +
            "<body><FONT FACE=\"Arial\">";

      private static string HTML_END =
         "<br><br><FONT SIZE=\"2\"><SUB>Построен в системе \"Наполеон\" <a href=http://grsoft.ru/>http://grsoft.ru/</a></SUB></body></html>";

      public HtmlWriter(string path)
         : base(path)
      {
         Write(HTML_BEGIN);
      }

      public void WriteTitle(string title)
      {
         Write("<H3>");
         Write(title);
         Write("</H3>");
      }

      public void WriteTableHead(object[] columns)
      {
         Write("<table cellpadding=5 CELLSPACING=0 border=1 BORDERCOLOR=\"#000000\">");
         Write("<tr BGCOLOR=\"#CCCCCC\">");
         foreach (object o in columns)
         {
            Write("<td><FONT SIZE=\"2\"><b>");
            Write(o.ToString());
            Write("</b></td>");
         }
         Write("</tr>");
      }

      public void WriteTableRow(object[] rowData)
      {
         Write("<tr>");
         foreach (object o in rowData)
         {
            Write("<td><FONT SIZE=\"2\">");
            Write(o.ToString());
            Write("</td>");
         }
         Write("</tr>");
      }

      public void WriteTableTail()
      {
         Write("</table>");
      }

      public override void Close()
      {
         Write(HTML_END);
         base.Close();
      }
   }

   class VisitTag
   {
      public Visit visit;
      public Visit.VisitItem item;

      public VisitTag(Visit v, Visit.VisitItem item)
      {
         this.visit = v;
         this.item = item;
      }
   }

   class FocusReport
   {
      private static int docNumber = 0;

      /// <summary>
      /// Отчет строится по папкам товара, иначе Order.FocuseItem.fid - товар
      /// </summary>
      public static bool FocusItemIsFolder = true;

      public static string MakeReport(Agent agent, ICollection orders, DataSet<string, Price> dsPrice)
      {
         DataSet<string, ManagerFolder> dsFolder = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME, false);
         //DataSet<string, Price> dsPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);
         //dsFolder.Filter = String.Format("\"userid\" in ('{0}')", agent.id);
         dsFolder.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agent.id), dsFolder.Name);
         //dsPrice.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agent.id), dsPrice.Name);

         IDataSet refSet = (IDataSet)dsFolder;
         Thread t = DataModule.RefreshDataSet(refSet, Config.GetConfig().GetConnection(), false, null);
         t.Join();

         string url = System.IO.Path.GetTempPath() + String.Format("focus_data_{0}.html", ++docNumber);
         HtmlWriter sw = new HtmlWriter(url);

         sw.WriteTitle(String.Format("{0} не проданный фокусный товар", agent.Name));

#if FOCUSED_GROUP && FOCUSED_ITEMS
         string[] data = { "Дата", "Контрагнет", "Папка", "Товар", "Примечание" };
#else
         string[] data = { "Дата", "Контрагнет", "Товар", "Примечание" };
#endif

         sw.WriteTableHead(data);

         foreach (Order o in orders)
         {
#if FOCUSED_GROUP
            if (o.focusedFolders != null)
            {
               foreach (Order.FocusFolder fi in o.focusedFolders)
               {
                  int ctr = 0;
                  string name;
                  if (FocusItemIsFolder)
                     name = (dsFolder.ContainsKey(fi.fid)) ? dsFolder[fi.fid].name : String.Format("папка с кодом '<{0}>'", fi.fid);
                  else
                     name = (dsPrice.ContainsKey(fi.fid)) ? dsPrice[fi.fid].name : String.Format("товар с кодом '<{0}>'", fi.fid);

                  data[ctr++] = o.Date.ToString("dd.MM.yyyy");
                  data[ctr++] = o.OrgName;
                  data[ctr++] = name;
#if FOCUSED_ITEMS
                  data[ctr++] = "";
#endif
                  data[ctr++] = fi.remark;

                  sw.WriteTableRow(data);
               }
            }
#endif
#if FOCUSED_ITEMS
            if (o.focusedItems != null)
            {
               foreach (Order.FocusItem fi in o.focusedItems)
               {
                  int ctr = 0;
                  string name;
                  name = (dsPrice.ContainsKey(fi.id)) ? dsPrice[fi.id].name : String.Format("товар с кодом '<{0}>'", fi.id);

                  data[ctr++] = o.Date.ToString("dd.MM.yyyy");
                  data[ctr++] = o.OrgName;
#if FOCUSED_GROUP
                  data[ctr++] = "";
#endif
                  data[ctr++] = name;
                  data[ctr++] = fi.remark;

                  sw.WriteTableRow(data);
               }
            }
#endif
         }

         sw.WriteTableTail();

         sw.Flush();
         sw.Close();
         return url;
      }
   }

#if AliansFood
   class ArchIncass : PKO
   {
      static public readonly string ARCH_INCASS_NAME = "ArchIncass";
   }
#endif
}
