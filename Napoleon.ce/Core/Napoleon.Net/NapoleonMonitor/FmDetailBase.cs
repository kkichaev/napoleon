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

namespace GRSoft.NapoleonManager
{
   public partial class FmDetailBase : Form
   {
      public static FmDetailBase Instance;

      public const string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy} 23:59:59') and \"userid\"='{3}'";

      //Необходимые объекты данных для формы
      protected DataSet<string, Price> dsPrice;// = new DataSet<string, Price>("Price");
      protected DataSet<string, Price> dsAgentPrice = new DataSet<string,Price>(Price.OBJECT_NAME, false);
      protected DataSet<string, Org> dsOrg;// = new DataSet<string, Org>("Org");
      protected DataSet<int, Order> dsOrder;// = new DataSet<int, Order>("Order");
      protected DataSet<int, OrderW> dsOrderW;
      protected DataSet<int, Visit> dsVisit;
      protected DataSet<int, Incass> dsIncass;
      protected DataSet<int, OrgRemnants> dsOrgRemnants;
      protected DataSet<int, PKO> dsPKO;
      protected DataSet<int, Returns> dsReturns;
      protected DataSet<int, UserLog> dsUserLog;
      //protected DataSet<int, ScriptDoc> dsScriptDoc;
      protected DataSet<int, DayDoc> dsDayDoc;
      protected DataSet<int, OrgFolder> dsOrgFolder;

      protected List<DocumentInfo> documents = new List<DocumentInfo>();

      protected Dictionary<DateTime, bool> documetsCompleted = new Dictionary<DateTime, bool>();
      protected DataSet<int, CommonConfig> dsConfig;
      protected DataSet<string, PotenzialOrg> dsPtnzOrg;
      private DataSet<int, Sales> dsSales;
      private DataSet<int, OrderCommitted> dsOrderCommitted;
      private ToolTip tooltipRemark;

#if ORDER_CHARGE
      SimpleDataSet<OrderCharge> dsOrderCharges = new SimpleDataSet<OrderCharge>(OrderCharge.OBJECT_NAME);
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
      private Config config;

      //FmDetail
      public FmDetailBase(FmDetailData detailData)
      {
         Instance = this;
         InitializeComponent();

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
         ToolStripMenuItem tsFocused = new ToolStripMenuItem();
         tsFocused.Name = "tsFocused";
         tsFocused.Size = new System.Drawing.Size(161, 22);
         tsFocused.Text = "Фокусный товар";
         tsFocused.Click += new System.EventHandler((o, e) => {
            string url = FocusReport.MakeReport(GetSelectedAgent(), dsOrder.Data, dsPrice);
            if (url != null && url.Length > 0)
               OpenLink.NewWindow(url);
         });

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
         config = Config.GetConfig();
         InitDataSets(detailData);
         AdjustForm(detailData);

      }

      internal virtual OrdersDetail CreateOrderDetail() { return new OrdersDetail(documents); }

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

         AgentItem selAgent = null;
         Manager mc = CurrentUser.user as Manager;
         if (mc != null)
         {
            foreach (Division.DivisionAgent da in mc.Division.GetAllAgents())
            {
               if (da.agent == null)
                  continue;

               AgentItem ai = new AgentItem(da.agent);
               cbAgents.Items.Add(ai);
               if (data.AgentId == da.agent.id)
               {
                  selAgent = ai;
               }
            }
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
                     DataSet<string, Price> src = (dsPrice != null && dsPrice.Count > 0 ) ? dsPrice : dsAgentPrice;
                     if (src != null && src.ContainsKey(answerItem.answer))
                        e.Value = src[answerItem.answer].name;
                     else
                        e.Value = String.Format("Код объекта <{0}> не найден", answerItem.answer);
                  }
                  else if (answerItem.remark.Equals("Организация"))
                  {
                     if( dsOrg.ContainsKey(answerItem.answer) )
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
         dsOrderW = DataModule.Get(OrderW.OBJECT_NAME) == null ? new DataSet<int, OrderW>(OrderW.OBJECT_NAME) :
            (DataSet<int, OrderW>)DataModule.Get(OrderW.OBJECT_NAME);
         dsVisit = DataModule.Get("Visit") == null ? new DataSet<int, Visit>("Visit") :
            (DataSet<int, Visit>)DataModule.Get("Visit");

         dsIncass = DataModule.Get(Incass.OBJECT_NAME) as DataSet<int, Incass> ??
            new DataSet<int, Incass>(Incass.OBJECT_NAME, true, true);

         dsOrgRemnants = DataModule.Get("OrgRemnants") == null ? new DataSet<int, OrgRemnants>("OrgRemnants") :
            (DataSet<int, OrgRemnants>)DataModule.Get("OrgRemnants");
         dsUserLog = DataModule.Get("UserLog") == null ? new DataSet<int, UserLog>("UserLog") :
            (DataSet<int, UserLog>)DataModule.Get("UserLog");
         //dsScriptDoc = DataModule.Get(ScriptDoc.OBJECT_NAME) == null ? new DataSet<int, ScriptDoc>(ScriptDoc.OBJECT_NAME) :
         //   (DataSet<int, ScriptDoc>)DataModule.Get(ScriptDoc.OBJECT_NAME);
         dsDayDoc = DataModule.Get(DayDoc.OBJECT_NAME) == null ? new DataSet<int, DayDoc>(DayDoc.OBJECT_NAME) :
            (DataSet<int, DayDoc>)DataModule.Get(DayDoc.OBJECT_NAME);
         dsPKO = DataModule.Get(PKO.OBJECT_NAME) == null ? new DataSet<int, PKO>(PKO.OBJECT_NAME, true, true) :
            (DataSet<int, PKO>)DataModule.Get(PKO.OBJECT_NAME);
         dsReturns = DataModule.Get(Returns.OBJECT_NAME) == null ? new DataSet<int, Returns>(Returns.OBJECT_NAME) :
            (DataSet<int, Returns>)DataModule.Get(Returns.OBJECT_NAME);
         dsOrgFolder = (DataSet<int, OrgFolder>)DataModule.Get(OrgFolder.OBJECT_NAME) ??
            new DataSet<int, OrgFolder>(OrgFolder.OBJECT_NAME);

         dsConfig = (DataSet<int, CommonConfig>)DataModule.Get(CommonConfig.OBJECT_NAME) ??
            new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME);

         dsPtnzOrg = (DataSet<string, PotenzialOrg>)DataModule.Get(PotenzialOrg.OBJECT_NAME) ??
            new DataSet<string, PotenzialOrg>(PotenzialOrg.OBJECT_NAME);

         dsPtnzOrg.Filter = DataUtils.MakeFilterFromAgents(null, Agents.GetDataSet());
         dsSales = (DataSet<int, Sales>)DataModule.Get(Sales.OBJECT_NAME) ??
            new DataSet<int, Sales>(Sales.OBJECT_NAME, true, true);

#if PRICE_MONITORING
         dsMonitoring = (DataSet<int, Monitoring>)DataModule.Get(Monitoring.OBJECT_NAME) ??
            new DataSet<int, Monitoring>(Monitoring.OBJECT_NAME);
         dsItems = DataModule.Get(MonitoringItem.OBJECT_NAME) as DataSet<string, MonitoringItem>;
         if (dsItems == null)
            dsItems = new DataSet<string, MonitoringItem>(MonitoringItem.OBJECT_NAME);
#endif
         dsOrderCommitted = DataModule.GetUserDataSet(data.AgentId, OrderCommitted.OBJECT_NAME, 
            typeof(DataSet<int, OrderCommitted>)) as DataSet<int, OrderCommitted>;
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
      }

      //Настроить фильтры для наборов данных
      private void AdjustFilterForDS(string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         dsOrg.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agentID), dsOrg.Name);
         dsOrderCommitted.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agentID), dsOrderCommitted.Name);
         dsAgentPrice.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agentID), dsAgentPrice.Name);
         //dsOrg.Filter = String.Format("userid='{0}'", agentID);//DataUtils.MakeFilterFromAgents(null);

         dsOrder.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsOrderW.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsSales.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsVisit.Filter = String.Format(COMMON_FILTER_STR, "date", dateBegin, dateEnd, agentID);

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
         dsUserLog.Filter = String.Format(COMMON_FILTER_STR, "objDate", dateBegin, dateEnd, agentID);
         //dsScriptDoc.Filter = String.Format(COMMON_FILTER_STR, "date", dateBegin, dateEnd, agentID);
         dsDayDoc.Filter = String.Format(COMMON_FILTER_STR, "start", dateBegin, dateEnd, agentID);
         
#if Vyatich
#else
         dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
#endif
         dsPKO.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsReturns.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsOrgFolder.Filter = String.Format("\"userid\"='{0}'", agentID);
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
         btnRefresh.Enabled = false;

#if USE_TIMEZONE
         System.Globalization.CultureInfo.CurrentCulture.ClearCachedData();
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
            if ( dsAgentPrice.Count == 0 )
               updSets.Add(dsAgentPrice);
#endif
         }

         dsOrg = DataModule.GetUserDataSet(agentID, "Org", typeof(DataSet<string, Org>), true) as DataSet<string, Org>;
         if (dsOrg.Count == 0)
            updSets.Add(dsOrg);

         updSets.Add(dsOrder);
         updSets.Add(dsOrderW);
         updSets.Add(dsVisit);
         updSets.Add(dsOrgRemnants);
         updSets.Add(dsUserLog);
         updSets.Add(dsDayDoc);
         updSets.Add(dsPKO);
         updSets.Add(dsReturns);
         updSets.Add(dsIncass);
         updSets.Add(dsOrgFolder);
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
         dateEnd = new DateTime(dateEnd.Year, dateEnd.Month, dateEnd.Day, 23, 59, 0, 0);
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
         BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
         DBConnection conn = Config.GetConfig().GetConnection();
         conn.ReceiveTimeout = 60 * 1000 * 3;
         FmWait.ShowForm(this, DataModule.RefreshGiveSets(conn, updSets, FmWait.ProgressIndicator));
      }

      protected virtual void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
      }

      protected virtual void AfterRefreshData(){}

      //Окончание выборки, заполняются внутренние наборы
      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();

         // Если нет общего прайса - используем прайс агента
         if (dsPrice.Count == 0 && dsAgentPrice.Count > 0)
            dsPrice = dsAgentPrice;

         UpdateSendDate();
         AfterRefreshData();

         this.Invoke(new InvokeDelegate(delegate
         {
            FmWait.CloseForm();

            ReloadData();
            btnRefresh.Enabled = true;
         }));

      }

      protected override void OnKeyDown(KeyEventArgs e)
      {
         base.OnKeyDown(e);
         if( e.KeyCode == Keys.F5 && e.Modifiers == Keys.None)
            RefreshDataSets((cbAgents.Items[cbAgents.SelectedIndex] as AgentItem).id, dtpBegin.Value.Date, dtpEnd.Value.Date, false);

      }

      protected void ReloadData()
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

      private void UpdateSendDate()
      {
         // предполагаем, что нет документов разного типа у одного агента с одной датой
         SendDate sendDate = new SendDate();
         sendDate.Load(dsUserLog.Data);

         BindingFlags bf = BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic;
         sendDate.UpdateSet(dsOrder.Data, typeof(Order).GetField("created", bf), typeof(Order).GetField("sended", bf));
         sendDate.UpdateSet(dsOrderW.Data, typeof(OrderW).GetField("created", bf), typeof(OrderW).GetField("sended", bf));
         sendDate.UpdateSet(dsVisit.Data, typeof(Visit).GetField("date", bf), typeof(Visit).GetField("sended", bf));
         sendDate.UpdateSet(dsOrgRemnants.Data, typeof(OrgRemnants).GetField("date", bf), typeof(OrgRemnants).GetField("sended", bf));
         sendDate.UpdateSet(dsPKO.Data, typeof(PKO).GetField("created", bf), typeof(PKO).GetField("sended", bf));
         sendDate.UpdateSet(dsReturns.Data, typeof(PKO).GetField("created", bf), typeof(Returns).GetField("sended", bf));
      }

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

      // Обновить таблицу в соответсвии с выбранными параметрами (FmDetailData)
      private void UpdateGrid(bool refreshFilterCB)
      {
         FmDetailData dd = new FmDetailData(GetSelectedIdAgent(), 
            dtpBegin.Value, GetDateForEndPeriod(),
            cbFilter.SelectedItem is ObjType ? cbFilter.SelectedItem as ObjType : null);

         dd.fmDetail = this;
         SetRelatedGridTitle();
         routes = oDetail.Load(dd, IsOneDaySelected(), GetSelectedAgent());

         Cursor.Current = Cursors.Default;
         BindingSource bs = new BindingSource();
         bs.DataSource = oDetail;
         dgvDetail.DataSource = bs;
         tsslCount.Text = "Всего документов: " + oDetail.OrderCount.ToString();
         tsslSum.Text = "Сумма: " + oDetail.Sum.ToString("C", Config.GetCultureInfo());

#if SUM_WEIGHT_LABEL
         tsslSum.Text += string.Format(" Вес: {0} кг. ", oDetail.Weight.ToString("0.000", CultureInfo.InvariantCulture));
#endif

         btnRoute.Enabled = true;

         SortOrderDetail(SortOrder.None, 4);

         if (refreshFilterCB)
            UpdateFiltersListInComboBox();
      }

      private void UpdateFiltersListInComboBox()
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
      private bool IsOneDaySelected()
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

      void UpdateVisibility(Control visible)
      {
         Control[] check = new Control[] { dgvRemnantsItems, tbVisitText, 
            dgvOrderItems, dgvReturns, dgvMoveItem, dgvInvoiceItem, dgvAnswerItems };
         foreach (Control c in check)
            c.Visible = (c == visible);

         if (visible != null && visible is DataGridView)
         {
            if (lbNotes.Visible == false)
               visible.Height = visible.Height + lbNotes.Height;
            else
               visible.Height = panel3.Height;
         }
         if( visible != null )
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
         loi.AddRange(o.items);
         dgvOrderItems.DataSource = loi;
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

            if (odr.Notes.Length > 0)
            {
               lbNotes.Visible = true;
               lbNotes.Text = odr.Notes;

               tooltipRemark.SetToolTip(lbNotes, lbNotes.Text);
            }
            else
               lbNotes.Visible = false;
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
               Visit vd = (odr.StoreObject as Visit);
               visible = tbVisitText;
#if VISIT_CAUSE   
               tbVisitText.Text = vd.cause + " " + vd.Remark;
#else
               tbVisitText.Text = vd.Remark;
#endif
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
               tbVisitText.Text = MakePKOText(odr.StoreObject);
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
#if PRICE_MONITORING
            case ObjType.TObjType.Monitoring:
               visible = tbVisitText;
               tbVisitText.Text = "";
               break;
#endif
#if QUESTION
            case ObjType.TObjType.Answer:
               visible = dgvAnswerItems;
               List<AnswerItem> aswItems = new List<AnswerItem>();
               aswItems.AddRange((odr.StoreObject as Answer).items);
               dgvAnswerItems.DataSource = aswItems;

               if ((odr.StoreObject as Answer).quest != null)
               {
                  lbNotes.Visible = true;
                  lbNotes.Text = (odr.StoreObject as Answer).quest.Name;
               }
               break;
#endif
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

         UpdateVisibility(visible);
         ShowCorrespondingPhoto(odr.DateCreatedDT, odr);
         SetLabelAddressText(curRow);
      }

      
      
      internal virtual Control RefreshDetail(OrderDetailRepresentation odr)
      {
         return null;
      }

      private string MakePKOText(object p)
      {
         PKO pko = p as PKO;
         if (pko != null)
         {
            StringBuilder str = new StringBuilder(pko.date.ToShortDateString());
            str.Append("\t");
            str.Append(pko.number);
            str.Append("\t");
            str.Append(pko.Sum().ToString("C", Config.GetCultureInfo()));
            return str.ToString();
         }
         else
         {
            Incass i = p as Incass;
            if (i != null)
            {
               StringBuilder str = new StringBuilder(i.date.ToShortDateString());
               str.Append("\t");
               str.Append(i.Sum().ToString("C", Config.GetCultureInfo()));
               return str.ToString();
            }
         }

         return "";
      }

      bool IsSameDate(DateTime d1, DateTime d2)
      {
         return (d1.Year == d2.Year) && (d1.Month == d2.Month) && (d1.Day == d2.Day);
      }

      protected void AddVisitPhotos(Visit v)
      {
         int photoCount = 0;
         AddVisitPhotos(v, null, 0, out photoCount);
      }

      private void AddVisitPhotos(Visit v, List<Image> picList, int count, out int rCount)
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
               stream.Close();
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
            catch (Exception e) {
               String str = e.StackTrace;
            } //TO-DO: watch in logger!!!!!
         }

         imPhoto.Tag = nativePicture;

         rCount = photoCount;
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
               AddVisitPhotos(v);
            else
            {
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

               foreach (DataGridViewRow row in dgvDetail.Rows)
               {
                  OrderDetailRepresentation odr = row.DataBoundItem as OrderDetailRepresentation;
                  if (odr == null)
                     continue;
                  v = odr.StoreObject as Visit;

#if Agama
                  if (v != null && IsSameDate(v.date, date) && v.org.id == o.NOrg.id && (uc == -1 || v.unitCode == uc))
#else
                  if (v != null && IsSameDate(v.date, date) && v.org.id == o.NOrg.id)
#endif
                  {
                     AddVisitPhotos(v, photos, pCounter, out pCounter);
                  }
               }
            }
         }
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
         return (cbAgents.Items[cbAgents.SelectedIndex] as AgentItem).id;
      }

      public DataSet<String, Org> GetAgentOrgs
      {
         get { return dsOrg; }
      }

      // агентя для выбранного агента из списка(cbAgents)
      public Agent GetSelectedAgent()
      {
         DataSet<string, Agent> dsAgent = (DataSet<string, Agent>)DataModule.Get("Agents");
         return dsAgent[(cbAgents.Items[cbAgents.SelectedIndex] as AgentItem).id];
      }

      // Показать форму "Маршрут"
      private void btnRoute_Click(object sender, EventArgs e)
      {
         FmRoute route = new FmRoute(GetSelectedIdAgent(), dtpBegin.Value.Date);
         route.SetDocuments(documents);

         route.Show();
      }

      // Событие смена значение фильтра
      private void cbFilter_SelectedIndexChanged(object sender, EventArgs e)
      {
         if (Visible)
         {
            UpdateGrid(false);
         }
      }

      //Показать адрес организации на карте
      private void lblAdress_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
      {
         OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;
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

      private void btnRefresh_Click_1(object sender, EventArgs e)
      {
         RefreshDataSets((cbAgents.Items[cbAgents.SelectedIndex] as AgentItem).id,
            dtpBegin.Value.Date, dtpEnd.Value.Date, false);
      }

      private void lvPhoto_DoubleClick(object sender, EventArgs e)
      {
         Image photo = GetNativePicture((sender as ListView).SelectedItems[0].Index);
         string tag = "";
         string comment = string.Empty;
         if (photo.Tag != null)
         {
            VisitTag vt = photo.Tag as VisitTag;
            if (vt != null)
            {
               DateTime dt = vt.visit.date;
               tag = dt.ToString("dd.MM.yy HH:mm");
               comment = vt.visit.Remark;
            }
            else
            {
               DateTime dt = (DateTime)photo.Tag;
               if (dt != null)
                  tag = dt.ToString("dd.MM.yy HH:mm");
            }
         }
         else
         {
            OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;
            tag = odr.DateCreatedDT.ToString();
         }

#if SVIMMC || GoldenShelf || NBtl
         FmViewPhoto.ShowPhoto(photo, tag, dgvDetail.CurrentRow.Cells[1].Value.ToString(), comment);
#else
         FmViewPhoto.ShowPhoto(photo, tag);
#endif
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
               new TimeInterval(dtpBegin.Value, dtpEnd.Value), (cbAgents.Items[cbAgents.SelectedIndex] as AgentItem))));
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

         if (dataObject is Order)
         {
            Order d = (dataObject as Order);
            created = d.created;
            docOrg = d.org;
         }
         else if (dataObject is Visit)
         {
            Visit d = (dataObject as Visit);
            created = d.date;
            docOrg = d.org;
         }
         else if (dataObject is OrgRemnants)
         {
            OrgRemnants d = (dataObject as OrgRemnants);
            created = d.date;
            docOrg = d.org;
         }
         else if (dataObject is Returns)
         {
            Returns d = (dataObject as Returns);
            created = d.created;
            docOrg = d.org;
         }
      }

      public static bool OrderMissed(Order order, DataSet<int, OrderCommitted> dataSet)
      {
         bool result = true;

         if (order != null)
         {
            DateTime created = order.created;
            string userid = order.AgentID;

            if (dataSet != null)
               foreach (OrderCommitted oc in dataSet.Data)
                  if (oc.created.Equals(created) && userid.Equals(oc.userid))
                  {
                     result = false;
                     break;
                  }
         }

         return result;
      }

      private void dgvDetail_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         CellFormatting(e);
      }

      protected virtual void CellFormatting(DataGridViewCellFormattingEventArgs e)
      {
         GRSoft.Network.DataObject dataObject = (dgvDetail.Rows[e.RowIndex].DataBoundItem as OrderDetailRepresentation).StoreObject;
         DateTime created = DateTime.Now;
         Org docOrg = null;

         if (dataObject is Order)
         {
            if (config.highliteOrderMissed && OrderMissed((Order)dataObject, dsOrderCommitted))
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

         GetDocData(out created, out docOrg, dataObject);

         if (docOrg == null)
         {
            e.CellStyle.BackColor = Color.White;
            return;
         }

         if (!IsCreatedBySelectedAgentRoute(docOrg, GetSelectedAgent().id, created))
            e.CellStyle.ForeColor = Color.Gray;

         if (!IsDocCompleted(created, dataObject))
         {
            e.CellStyle.BackColor = Color.LightGray;
         }
         else
         {
            e.CellStyle.BackColor = Color.White;
         }
      }

      internal static bool IsCreatedBySelectedAgentRoute(Org org, string agentid, DateTime created)
      {
         Agents agents = Agents.GetDataSet();
         if (agents.ContainsKey(agentid) == false)
            return false;

         DataSet<int, CommonConfig> configs = (DataSet<int, CommonConfig>)DataModule.Get(CommonConfig.OBJECT_NAME);
         SummaryData sd = new SummaryData(agents[agentid], configs);
         DataSet<int, OrgFolder> routes = (DataSet<int, OrgFolder>)DataModule.Get(OrgFolder.OBJECT_NAME);
         List<OrgFolderItem> items = sd.GetAgentRoute(created, routes.Data);

         if (items != null)
         {
            foreach (OrgFolderItem ofi in items)
               if (ofi.name.Equals(org.id))
                  return true;
         }

         return false;
      }

      private void dgvDetail_ColumnHeaderMouseClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         SortOrder curOrder = dgvDetail.Columns[e.ColumnIndex].HeaderCell.SortGlyphDirection;
         int colmnIndex = e.ColumnIndex;

         curOrder = SortOrderDetail(curOrder, colmnIndex);
         UpdateDetailTable(dgvDetail.CurrentRow);
      }

      //Выполнить сортировку документов 
      protected SortOrder SortOrderDetail(SortOrder curOrder, int colmnIndex)
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

            switch (colmnIndex)
            {
               case 1: fieldSort = new string[] { "org", "dateCreated" };
                  break;
               case 2: fieldSort = new string[] { "doctype" };
                  break;
               case 3: fieldSort = new string[] { "dateExec" };
                  break;
               case 4: fieldSort = new string[] { "dateCreated" };
                  break;
               case 5: fieldSort = new string[] { "sended" };
                  break;
               case 6: fieldSort = new string[] { "sum" };
                  break;
               default:
                  string dpn = dgvDetail.Columns[colmnIndex].DataPropertyName;
                  if( dpn.Length > 0 )
                     fieldSort = new string[] { dpn };
                  break;
            }

            if (fieldSort != null)
            {
               ordersDetail.DoSort(fieldSort, curOrder);
               dgvDetail.Refresh();
               dgvDetail.Columns[colmnIndex].HeaderCell.SortGlyphDirection = curOrder;
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
         ConstructorInfo ci = cct.GetConstructor(new Type[] {});
         ClientCard cc = (ClientCard)ci.Invoke(new object[] { });
         cc.DoReport(dtpBegin.Value, dtpEnd.Value);
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
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

      protected IDataSet GetDupDataSet()
      {
         IDataSet result = null;
         OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;

         if (odr != null)
         {
            GRSoft.Network.DataObject dataObject = odr.StoreObject;
            result = GetDuplicate(dataObject);
            if (result != null)
               return result;

            if (dataObject is OrderCharge)
            {
               DataSet<int, OrderCharge> ord = new DataSet<int, OrderCharge>(OrderCharge.OBJECT_NAME, false, true);
               ord.Add(ord.Count, (OrderCharge)dataObject);

               result = ord;
            }
            else if (dataObject is Sales)
            {
               DataSet<int, Order> ord = new DataSet<int, Order>(Sales.OBJECT_NAME, false, true);
               ord.Add(ord.Count, (Sales)dataObject);

               result = ord;
            } else if (dataObject is Order)
            {
               DataSet<int, Order> ord = new DataSet<int, Order>(Order.OBJECT_NAME, false, true);
               ord.Add(ord.Count, (Order)dataObject);

               result = ord;
            }
            else if (dataObject is Incass)
            {
               DataSet<int, Incass> ird = new DataSet<int, Incass>(Incass.OBJECT_NAME, false, true);
               ird.Add(ird.Count, (Incass)dataObject);

               result = ird;
            }
            else if (dataObject is Returns)
            {
               DataSet<int, Returns> ord = new DataSet<int, Returns>(Returns.OBJECT_NAME, false, true);
               ord.Add(ord.Count, (Returns)dataObject);

               result = ord;
            }
            else if (dataObject is PKO)
            {
               SimpleDataSet<PKO> ird = new SimpleDataSet<PKO>(PKO.OBJECT_NAME, false, true);
               ird.Add((PKO)dataObject);

               result = ird;
            }
         }

         return result;
      }

      //Продублировать заявку в базе данных
      private void miMakeDup_Click(object sender, EventArgs e)
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
         foreach(ListViewItem lvi in sel)
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
         //new FmCoverArea(GetSelectedIdAgent(), dtpBegin.Value.Date).Show();

         Type prcType = FormEntries.GetFormType(typeof(FmCoverArea));
         ConstructorInfo ci = prcType.GetConstructor(new Type[]{typeof(string), typeof(DateTime)});
         Form fm = (Form)ci.Invoke(new object[] {GetSelectedIdAgent(), dtpBegin.Value.Date});
         fm.Show();
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

      public FmDetailData(string agent, 
         DateTime begin, DateTime end, ObjType orderType)
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
      protected int docCount;
      protected double sum;
      protected double weight;

      protected List<DocumentInfo> documents;

      public OrdersDetail() { }
      public OrdersDetail(List<DocumentInfo> documents) { this.documents = documents; }

      protected List<ObjType> filtersAvailable = new List<ObjType>();

      public int OrderCount { get { return docCount; } }
      public double Sum { get { return sum; } }
      public double Weight { get { return weight; } }

      public void DoSort(string[] cmpFields, SortOrder sortOrder)
      {
         OrderDetailRepresentation.CC.SetCompareCondition(cmpFields, sortOrder == SortOrder.Ascending);
         Sort();
      }

      public List<Org> Load(FmDetailData cond, bool oneDay, Agent agent)
      {
         List<Org> routes = GetRoutePeriod(cond.DateBegin, cond.DateEnd, agent);

         Clear();

         docCount = 0;
         sum = 0;
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

         LoadInt(cond, oneDay, checkRoute, agent.id, routes);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.NotVisit) : !checkRoute && routes != null)
         {
            foreach (Org org in routes)
            {
               if (org == null)
                  continue;

               if (documents != null)
               {
                  bool found = false;
                  foreach (DocumentInfo di in documents)
                  {
                     IDataSet cdata = di.DataSet;
                     if (cdata != null)
                     {
                        foreach (BaseDocument doc in cdata.Data)
                        {
                           if( doc.id == org.id )
                           {
                              found = true;
                              break;
                           }
                        }
                     }
                     if (found)
                        break;
                  }

                  if (found)
                     continue;
               }


               AddNotVisitedOrg(oneDay, org);
            }
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

         return routes;
      }

      protected virtual void AddNotVisitedOrg(bool oneDay, Org org)
      {
         if (OrgNotInVisit(org))
         {
            Add(new OrderDetailRepresentation(DateTime.MinValue,
               new ObjType(ObjType.TObjType.NotVisit),
               DateTime.MinValue, DateTime.MinValue, org, 0, 0, 0, org, oneDay));
         }
      }

      protected virtual OrderDetailRepresentation CreateOrderRow(Order order, bool oneDay)
      {
         return new OrderDetailRepresentation(order.Created,
                  new ObjType(ObjType.TObjType.OtOrder),
                  order.Date, order.Sended, order.Org,
                     order.DSum, 0, order.Qty, order, oneDay,
                     order.Remark);
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

         cdata = DataModule.Get("Order");
         CheckFiltersForDocType(cdata, ObjType.TObjType.OtOrder, filtersAvailable);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.OtOrder) : true && cdata != null)
         {
            foreach (Order order in cdata.Data)
            {
               if (checkRoute && FmDetailBase.IsCreatedBySelectedAgentRoute(order.org, agentID, order.created))
                  continue;

               docCount++;
               sum += order.DSum;
               weight += order.Weight;

               Add(CreateOrderRow(order, oneDay));
            }
         }

#if ORDER_CHARGE
         cdata = DataModule.Get("OrderCharge");
         CheckFiltersForDocType(cdata, ObjType.TObjType.OrderCharge, filtersAvailable);
         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.OrderCharge) : true && cdata != null)
         {
            foreach (Order order in cdata.Data)
            {
               if (checkRoute &&
                  FmDetailBase.IsCreatedBySelectedAgentRoute(
                  order.org, agentID, order.created))
                  continue;

               docCount++;
               sum += order.DSum;
               weight += order.Weight;

               Add(new OrderDetailRepresentation(order.Created,
                  new ObjType(ObjType.TObjType.OrderCharge),
                  order.Date, order.Sended, order.org,
                     order.DSum, 0, order.Qty, order, oneDay,
                     order.Remark));
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

               docCount++;
               sum += pko.Sum();

               Add(new OrderDetailRepresentation(pko.created,
                  new ObjType(ObjType.TObjType.ArchIncass),
                  pko.date, pko.sended, pko.org, pko.Sum(), 0, 0,
                  pko, oneDay));
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

               docCount++;
               sum += order.DSum;
               Add(new OrderDetailRepresentation(order.Created,
                  new ObjType(ObjType.TObjType.OrderW),
                  order.Date, order.Sended, order.org,
                     order.DSum, 0, order.Qty, order, oneDay,
                     order.Remark));
            }
         }
#endif

         cdata = DataModule.Get("Visit");
         CheckFiltersForDocType(cdata, ObjType.TObjType.OtVisit, filtersAvailable);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.OtVisit) : true && cdata != null)
         {
            foreach (Visit visit in cdata.Data)
            {
               if (checkRoute &&
                  FmDetailBase.IsCreatedBySelectedAgentRoute(visit.org,
                  agentID, visit.date))
                  continue;

               docCount++;

               Add(new OrderDetailRepresentation(visit.Created,
                  new ObjType(ObjType.TObjType.OtVisit),
                  visit.Date, visit.Sended, visit.org, 0.0, 0, 0,
                  visit, oneDay));
            }
         }

         cdata = DataModule.Get("OrgRemnants");
         CheckFiltersForDocType(cdata, ObjType.TObjType.OtOrgRemnants, filtersAvailable);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.OtOrgRemnants) : true && cdata != null)
         {
            foreach (OrgRemnants remnants in cdata.Data)
            {
               if (checkRoute &&
                  FmDetailBase.IsCreatedBySelectedAgentRoute(remnants.org,
                  agentID, remnants.date))
                  continue;

               docCount++;

               Add(new OrderDetailRepresentation(remnants.Created,
                  new ObjType(ObjType.TObjType.OtOrgRemnants),
                  remnants.Date, remnants.Sended, remnants.org, 0.0, 0, 0,
                  remnants, oneDay));
            }
         }

         bool pkoAdded = false;
         cdata = DataModule.Get(PKO.OBJECT_NAME);
         CheckFiltersForDocType(cdata, ObjType.TObjType.PKO, filtersAvailable);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.PKO) : true && cdata != null)
         {
            foreach (PKO pko in cdata.Data)
            {
               if (checkRoute && FmDetailBase.IsCreatedBySelectedAgentRoute(
                  pko.org, agentID, pko.created))
                  continue;

               pkoAdded = true;
               docCount++;
               sum += pko.Sum();

               Add(new OrderDetailRepresentation(pko.Created,
                  new ObjType(ObjType.TObjType.PKO),
                  pko.Date, pko.Sended, pko.org, pko.Sum(), 0, 0,
                  pko, oneDay));
            }
         }

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

               pkoAdded = true;
               docCount++;
               sum += pko.Sum();

               Add(new OrderDetailRepresentation(pko.Created,
                  new ObjType(ObjType.TObjType.PKO),
                  pko.Date, pko.Sended, pko.org, pko.Sum(), 0, 0,
                  pko, oneDay, pko.Remark));
            }
         }

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

               docCount++;
               sum += returns.Sum();

               Add(new OrderDetailRepresentation(returns.Created,
                  new ObjType(ObjType.TObjType.OtReturn),
                  returns.Date, returns.Sended, returns.org, returns.Sum(), 0, 0,
                  returns, oneDay));
            }
         }

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

               docCount++;
               sum += sales.DSum;

               Add(new OrderDetailRepresentation(sales.Created,
                  new ObjType(ObjType.TObjType.Sales),
                  sales.Date, sales.Sended, sales.org,
                     sales.DSum, 0, sales.Qty, sales, oneDay,
                     sales.Remark));
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

               docCount++;

               Add(new OrderDetailRepresentation(distr.created,
                  new ObjType(ObjType.TObjType.Distr),
                  distr.created, distr.sended, distr.org,
                     0, 0, 0, distr, oneDay,
                     ""));
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

               docCount++;
               sum += move.Sum();

               Add(new OrderDetailRepresentation(move.created,
                  new ObjType(ObjType.TObjType.Move),
                  move.date, move.sended, move.org,
                     0.0, 0, move.Qty, move, oneDay,
                     move.Remark));
            }
         }
#endif
#if QUESTION
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

               docCount++;
               Add(new AnswerDetailRepresentation(answer.Created,
                  new ObjType(ObjType.TObjType.Answer),
                  answer.Created, answer.Sended, answer.org, 0.0, 0, 0,
                  answer, oneDay));
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

               docCount++;

               Add(new OrderDetailRepresentation(doc.created,
                  new ObjType(ObjType.TObjType.Monitoring),
                  doc.date, doc.sended, doc.org, 0.0, 0, 0,
                  doc, oneDay));
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

               Add(new OrderDetailRepresentation(rmn.created,
                  new ObjType(ObjType.TObjType.Invoice),
                  rmn.created, rmn.sended, rmn.org,
                     0, 0, 0, rmn, oneDay,
                     ""));
            }
         }
#endif
         if (documents != null)
         {
            foreach (DocumentInfo di in documents)
            {
               cdata = di.DataSet;
               CheckFiltersForDocType(cdata, di.Type, filtersAvailable);
               if (cdata != null && cond.OrderType != null ? cond.OrderType.Equals(di.Type) : true)
               {
                  foreach (BaseDocument doc in cdata.Data)
                     Add(new OrderDetailRepresentation(doc, new ObjType(di.Type), oneDay));
               }
            }
         }
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


         return true;
      }

      //Возвращает список организаций - маршрут за период
      public static List<Org> GetRoutePeriod(DateTime begin, DateTime end, Agent agent)
      {
         List<Org> result = new List<Org>();
         List<string> dayProcessed = new List<string>();
         DataSet<int, OrgFolder> routes = (DataSet<int, OrgFolder>)DataModule.Get(OrgFolder.OBJECT_NAME);
         DataSet<int, CommonConfig> configs = (DataSet<int, CommonConfig>)DataModule.Get(CommonConfig.OBJECT_NAME);

         SummaryData sd = new SummaryData(agent, configs);

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

#if FOCUSED_GROUP || FOCUSED_ITEMS
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
#endif

#if AliansFood
   class ArchIncass : PKO
   {
      static public readonly string ARCH_INCASS_NAME = "ArchIncass";
   }
#endif
}