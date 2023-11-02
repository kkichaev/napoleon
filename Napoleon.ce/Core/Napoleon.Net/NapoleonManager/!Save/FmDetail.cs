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
   public partial class FmDetail : Form
   {
      public static FmDetail Instance;

      protected const string COMMON_FILTER_STR = "{0} >= ToDate('{1:dd/MM/yyyy}') and {0} < ToDate('{2:dd/MM/yyyy} 23:59:59') and userid='{3}'";

      //Необходимые объекты данных для формы
      protected DataSet<string, Price> dsPrice;// = new DataSet<string, Price>("Price");
      protected DataSet<string, Org> dsOrg;// = new DataSet<string, Org>("Org");
      protected DataSet<int, Order> dsOrder;// = new DataSet<int, Order>("Order");
      protected DataSet<int, Visit> dsVisit;
      protected DataSet<int, Incass> dsIncass;
      protected DataSet<int, OrgRemnants> dsOrgRemnants;
      protected DataSet<int, PKO> dsPKO;
      protected DataSet<int, Returns> dsReturns;
      protected DataSet<int, UserLog> dsUserLog;
      //protected DataSet<int, ScriptDoc> dsScriptDoc;
      protected DataSet<int, DayDoc> dsDayDoc;
      protected DataSet<int, OrgFolder> dsOrgFolder;
      protected Dictionary<DateTime, bool> documetsCompleted = new Dictionary<DateTime, bool>();
      protected DataSet<int, CommonConfig> dsConfig;
      private DataSet<string, PotenzialOrg> dsPtnzOrg;
      private DataSet<int, Sales> dsSales;

#if QUESTION
      private DataSet<string, PotenzialOrg> dsPotenzailOrg;
      private DataSet<int, Answer> dsAnswer;
      private DataSet<string, Question> dsQuestion;
      protected DataGridView dgvAnswerItems = new System.Windows.Forms.DataGridView();
      private DataGridViewTextBoxColumn dgvAnswerItemsId = new System.Windows.Forms.DataGridViewTextBoxColumn();
      private DataGridViewTextBoxColumn dgvAnswerItemsAnswer = new System.Windows.Forms.DataGridViewTextBoxColumn();
#endif

      protected List<Org> routes;
      /// Набор отображаемых данных в таблице
      private OrdersDetail oDetail;

      //Декоратор формы
      private IDecorator decorator;

      //FmDetail
      public FmDetail(FmDetailData detailData)
      {
         Instance = this;
         InitializeComponent();

#if SNAPSHOT_RATING
         lvPhoto.ContextMenuStrip = cmPhotoRating;
         tbPhotoRate.Visible = true;
#endif
         oDetail = CreateOrderDetail();

         decorator = DecoratorFactory.GetDecorator(this);

#if FOCUSED_GROUP
         ToolStripMenuItem tsFocused = new ToolStripMenuItem();
         tsFocused.Name = "tsFocused";
         tsFocused.Size = new System.Drawing.Size(161, 22);
         tsFocused.Text = "Фокусный товар";
         tsFocused.Click += new System.EventHandler(tsFocused_Click);

         tsReportMenu.DropDownItems.Add(tsFocused);
#endif

#if QUESTION
         dsAnswer = (DataSet<int, Answer>)DataModule.Get(Answer.OBJECT_NAME) ??
            new DataSet<int, Answer>(Answer.OBJECT_NAME);
         dsQuestion = (DataSet<string, Question>)DataModule.Get(Question.OBJECT_NAME) ??
            new DataSet<string, Question>(Question.OBJECT_NAME);
         dsQuestion.Filter = "idquest is null or idquest is not null";

         dsPotenzailOrg = new DataSet<string, PotenzialOrg>(PotenzialOrg.OBJECT_NAME, false);
         dsPotenzailOrg.Filter = "id is null or id is not null";
         
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
         dgvDetailColumnSum.Visible = false;

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
         InitDataSets(detailData);
         AdjustForm(detailData);

      }

      internal virtual OrdersDetail CreateOrderDetail() { return new OrdersDetail(); }

#if FOCUSED_GROUP
      private void tsFocused_Click(object sender, EventArgs e)
      {
         string url = FocustReport.MakeReport(GetSelectedAgent(), dsOrder.Data);
         if (url != null && url.Length > 0)
            OpenLink.NewWindow(url);
      }
#endif
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

         AgentItem selAgent = null;

         foreach (Agent a in DataModule.Get("Agents").Data)
         {
            AgentItem ai = new AgentItem(a);
            cbAgents.Items.Add(ai);
            if (data.AgentId == a.id)
            {
               selAgent = ai;
            }
         }

         lblAdress.Text = string.Empty;
         cbAgents.SelectedItem = selAgent;
         dtpBegin.Value = data.DateBegin;
         dtpEnd.Value = data.DateEnd;
         tsslSum.Text = string.Empty;
         tsslCount.Text = string.Empty;
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
                     if (dsPrice != null && dsPrice.ContainsKey(answerItem.answer))
                        e.Value = dsPrice[answerItem.answer].name;
                     else
                        e.Value = String.Format("Код объекта <{0}> не найден", answerItem.answer);
                  }
                  else if (answerItem.remark.Equals("Организация"))
                  {
                     if( dsOrg.ContainsKey(answerItem.answer) )
                        e.Value = dsPotenzailOrg[answerItem.answer].name;
                     if (dsPotenzailOrg.ContainsKey(answerItem.answer))
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

         dsOrg = DataModule.GetUserDataSet(data.AgentId, "Org", typeof(DataSet<string, Org>)) as DataSet<string, Org>;

         dsOrder = DataModule.Get("Order") == null ? new DataSet<int, Order>("Order") :
            (DataSet<int, Order>)DataModule.Get("Order");
         dsVisit = DataModule.Get("Visit") == null ? new DataSet<int, Visit>("Visit") :
            (DataSet<int, Visit>)DataModule.Get("Visit");

         dsIncass = DataModule.Get(Incass.OBJECT_NAME) as DataSet<int, Incass> ??
            new DataSet<int, Incass>(Incass.OBJECT_NAME);

         dsOrgRemnants = DataModule.Get("OrgRemnants") == null ? new DataSet<int, OrgRemnants>("OrgRemnants") :
            (DataSet<int, OrgRemnants>)DataModule.Get("OrgRemnants");
         dsUserLog = DataModule.Get("UserLog") == null ? new DataSet<int, UserLog>("UserLog") :
            (DataSet<int, UserLog>)DataModule.Get("UserLog");
         //dsScriptDoc = DataModule.Get(ScriptDoc.OBJECT_NAME) == null ? new DataSet<int, ScriptDoc>(ScriptDoc.OBJECT_NAME) :
         //   (DataSet<int, ScriptDoc>)DataModule.Get(ScriptDoc.OBJECT_NAME);
         dsDayDoc = DataModule.Get(DayDoc.OBJECT_NAME) == null ? new DataSet<int, DayDoc>(DayDoc.OBJECT_NAME) :
            (DataSet<int, DayDoc>)DataModule.Get(DayDoc.OBJECT_NAME);
         dsPKO = DataModule.Get(PKO.OBJECT_NAME) == null ? new DataSet<int, PKO>(PKO.OBJECT_NAME) :
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
            new DataSet<int, Sales>(Sales.OBJECT_NAME);
      }

      //Настроить фильтры для наборов данных
      private void AdjustFilterForDS(string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         dsOrg.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agentID), dsOrg.Name);
         //dsOrg.Filter = String.Format("userid='{0}'", agentID);//DataUtils.MakeFilterFromAgents(null);

         dsOrder.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsSales.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsVisit.Filter = String.Format(COMMON_FILTER_STR, "date", dateBegin, dateEnd, agentID);

         string f = "(" + String.Format(COMMON_FILTER_STR, "date", dateBegin, dateEnd, agentID) + ") or (" +
            String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID) + ")";
         dsIncass.Filter = f;

         dsOrgRemnants.Filter = String.Format(COMMON_FILTER_STR, "date", dateBegin, dateEnd, agentID);
         dsUserLog.Filter = String.Format(COMMON_FILTER_STR, "objDate", dateBegin, dateEnd, agentID);
         //dsScriptDoc.Filter = String.Format(COMMON_FILTER_STR, "date", dateBegin, dateEnd, agentID);
         dsDayDoc.Filter = String.Format(COMMON_FILTER_STR, "start", dateBegin, dateEnd, agentID);
         dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
         dsPKO.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsReturns.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsOrgFolder.Filter = String.Format("userid='{0}'", agentID);
         dsConfig.Filter = "(not (userid is null)) or userid is null";
      }

      //Обновить наборы даных
      private void RefreshDataSets(string agentID, DateTime dateBegin, DateTime dateEnd,
         bool needToGetPrice)
      {
         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

         List<IDataSet> updSets = new List<IDataSet>();
         if (dsPrice.Count == 0)
            updSets.Add(dsPrice);

         dsOrg = DataModule.GetUserDataSet(agentID, "Org", typeof(DataSet<string, Org>)) as DataSet<string, Org>;
         if (dsOrg.Count == 0)
            updSets.Add(dsOrg);

         updSets.Add(dsOrder);
         updSets.Add(dsVisit);
         updSets.Add(dsOrgRemnants);
         updSets.Add(dsUserLog);
         //updSets.Add(dsScriptDoc);
         updSets.Add(dsDayDoc);
         updSets.Add(dsPKO);
         updSets.Add(dsReturns);
         updSets.Add(dsIncass);
         updSets.Add(dsOrgFolder);
         updSets.Add(dsConfig);
         updSets.Add(dsPtnzOrg);
         updSets.Add(dsSales);

         dateEnd = new DateTime(dateEnd.Year, dateEnd.Month, dateEnd.Day, 23, 59, 0, 0);
         AdjustFilterForDS(agentID, dateBegin, dateEnd);

#if QUESTION
         dsAnswer.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin.ToString("dd-MM-yyyy 00:00:00"), dateEnd.ToString("dd-MM-yyyy 00:00:00"), agentID);
         updSets.Add(dsQuestion);
         updSets.Add(dsAnswer);
         updSets.Add(dsPotenzailOrg);
#endif

         BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(MainForm.Instance.conn, updSets, FmWait.ProgressIndicator));
      }

      protected virtual void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
      }

      //Окончание выборки, заполняются внутренние наборы
      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();

         UpdateSendDate();

         this.Invoke(new InvokeDelegate(delegate
         {
            FmWait.CloseForm();

            ReloadData();
         }));

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
         routes = oDetail.Load(dd, IsOneDaySelected(), GetSelectedIdAgent());

         Cursor.Current = Cursors.Default;
         BindingSource bs = new BindingSource();
         bs.DataSource = oDetail;
         dgvDetail.DataSource = bs;
         tsslCount.Text = "Всего заявок: " + oDetail.OrderCount.ToString();
         tsslSum.Text = "Сумма: " + oDetail.Sum.ToString("C", Config.GetCultureInfo());
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
         Control[] check = new Control[] { dgvRemnantsItems, tbVisitText, dgvOrderItems, dgvReturns };
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
            case ObjType.TObjType.Sales:
               visible = dgvOrderItems;
               List<OrderItem> loi = new List<OrderItem>();
               loi.AddRange((curRow.Cells[8].Value as Order).items);
               dgvOrderItems.DataSource = loi;
               break;
            case ObjType.TObjType.OtVisit:
               Visit vd = (curRow.Cells[8].Value as Visit);
               visible = tbVisitText;
#if VISIT_CAUSE   
               tbVisitText.Text = vd.cause + " " + vd.remark;
#else
               tbVisitText.Text = vd.remark;
#endif
               break;
            case ObjType.TObjType.OtOrgRemnants:
               visible = dgvRemnantsItems;
               List<OrgRemnantsItem> remnantsItems = new List<OrgRemnantsItem>();
               remnantsItems.AddRange((curRow.Cells[8].Value as OrgRemnants).items);
               dgvRemnantsItems.DataSource = remnantsItems;
               break;
            case ObjType.TObjType.DayDoc:
               visible = tbVisitText;
               tbVisitText.Text = MakeDayDocText(curRow.Cells[8].Value as DayDoc);
               break;
            case ObjType.TObjType.PKO:
               visible = tbVisitText;
               tbVisitText.Text = MakePKOText(curRow.Cells[8].Value);
               break;
            case ObjType.TObjType.NotVisit:
               visible = tbVisitText;
               tbVisitText.Text = "Не посетил";
               break;
            case ObjType.TObjType.OtReturn:
               visible = dgvReturns;
               List<ReturnItem> returns = new List<ReturnItem>();
               returns.AddRange((curRow.Cells[8].Value as Returns).items);
               dgvReturns.DataSource = returns;
               break;
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
            str.Append(pko.Sum.ToString("C", Config.GetCultureInfo()));
            return str.ToString();
         }
         else
         {
            Incass i = p as Incass;
            if (i != null)
            {
               StringBuilder str = new StringBuilder(i.date.ToShortDateString());
               str.Append("\t");
               str.Append(i.Sum.ToString("C", Config.GetCultureInfo()));
               return str.ToString();
            }
         }

         return "";
      }

      bool IsSameDate(DateTime d1, DateTime d2)
      {
         return (d1.Year == d2.Year) && (d1.Month == d2.Month) && (d1.Day == d2.Day);
      }

      private void AddVisitPhotos(Visit v)
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
            catch { } //TO-DO: watch in logger!!!!!
         }

         imPhoto.Tag = nativePicture;

         rCount = photoCount;
      }

      //По новому алгоритму фото показываем для первой записи которая имеет ту же организаци
      //и ту же дату
      private void ShowCorrespondingPhoto(DateTime date, OrderDetailRepresentation o)
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
                  v = row.Cells[8].Value as Visit;

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
         if (dgvDetail.RowCount > 0 && row.Cells[8].Value != null)
         {
            lblAdress.Text = row.Cells[7].Value.ToString();
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
         new FmRoute(GetSelectedIdAgent(), dtpBegin.Value.Date).Show();
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
         FmAddrShow.AddrShow(lblAdress.Text, dgvDetail.CurrentRow.Cells[1].Value.ToString());
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
         if (photo.Tag != null)
         {
            VisitTag vt = photo.Tag as VisitTag;
            if (vt != null)
            {
               DateTime dt = vt.visit.date;
               tag = dt.ToString("dd.MM.yy HH:mm");
            }
            else
            {
               DateTime dt = (DateTime)photo.Tag;
               if( dt != null )
                  tag = dt.ToString("dd.MM.yy HH:mm");
            }
         }
         else
            tag = dgvDetail.CurrentRow.Cells[4].Value.ToString();
         FmViewPhoto.ShowPhoto(photo, tag);
      }

      //Составит и вывести отчет в HTML
      private void tsbMakeHtml_Click(object sender, EventArgs e)
      {
         HtmlReport htmlReport = new HtmlReport();
         OpenLink.NewWindow(String.Format("\"{0}\"", htmlReport.makeDetailsFileInfo(dgvDetail,
               new TimeInterval(dtpBegin.Value, dtpEnd.Value), (cbAgents.Items[cbAgents.SelectedIndex] as AgentItem).name)));
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

      private void dgvDetail_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         GRSoft.Network.DataObject dataObject = (GRSoft.Network.DataObject)dgvDetail.Rows[e.RowIndex].Cells[8].Value;
         DateTime created = DateTime.Now;
         Org docOrg = null;

         if (dataObject is Org)
         {
            e.CellStyle.BackColor = Color.Aqua;
            return;
         }

#if QUESTION
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
         DataSet<int, CommonConfig> configs = (DataSet<int, CommonConfig>)DataModule.Get(CommonConfig.OBJECT_NAME);
         SummaryData sd = new SummaryData(configs);
         DataSet<int, OrgFolder> routes = (DataSet<int, OrgFolder>)DataModule.Get(OrgFolder.OBJECT_NAME);
         List<OrgFolderItem> items = sd.GetAgentRoute(agentid, created, routes.Data);

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
      private SortOrder SortOrderDetail(SortOrder curOrder, int colmnIndex)
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

         OrdersDetail ordersDetail = (OrdersDetail)((BindingSource)dgvDetail.DataSource).DataSource;
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
         }

         if (fieldSort != null)
         {
            ordersDetail.DoSort(fieldSort, curOrder);
            dgvDetail.Refresh();
            dgvDetail.Columns[colmnIndex].HeaderCell.SortGlyphDirection = curOrder;
            UpdateDetailTable(dgvDetail.CurrentRow);
         }

         return curOrder;
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
               ClientCard.DoReport(dtpBegin.Value, dtpEnd.Value);
         }
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();

         Invoke(new EmptyParamHandler(delegate
         {
            FmWait.CloseForm();

            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }));
      }

      //Настройка контестного меню общей таблицы
      private void cmDgvDetail_Opening(object sender, CancelEventArgs e)
      {
         if (GetOrder(dgvDetail.CurrentRow) == null && GetIncass(dgvDetail.CurrentRow) == null)
            e.Cancel = true;
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

      private IDataSet GetDupDataSet()
      {
         IDataSet result = null;
         OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;

         if (odr != null)
         {
            GRSoft.Network.DataObject dataObject = odr.StoreObject;

            if (dataObject is Order)
            {
               DataSet<int, Order> ord = new DataSet<int, Order>(Order.OBJECT_NAME, false);
               ord.Add(ord.Count, (Order)dataObject);

               result = ord;
            }
            else if (dataObject is Incass)
            {
               DataSet<int, Incass> ird = new DataSet<int, Incass>(Incass.OBJECT_NAME, false);
               ird.Add(ird.Count, (Incass)dataObject);

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
      public FmDetail fmDetail;

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
   class OrdersDetail : List<OrderDetailRepresentation>
   {
      protected int orderCount;
      protected double sum;
      protected List<ObjType> filtersAvailable = new List<ObjType>();

      public int OrderCount { get { return orderCount; } }
      public double Sum { get { return sum; } }

      public void DoSort(string[] cmpFields, SortOrder sortOrder)
      {
         OrderDetailRepresentation.CC.SetCompareCondition(cmpFields, sortOrder == SortOrder.Ascending);
         Sort();
      }

      public List<Org> Load(FmDetailData cond, bool oneDay, string agentID)
      {
         List<Org> routes = GetRoutePeriod(cond.DateBegin, cond.DateEnd, agentID);

         Clear();

         orderCount = 0;
         sum = 0;
         filtersAvailable.Clear();

         bool checkRoute = false;
         if (routes != null && cond.OrderType != null && cond.OrderType.Equals(ObjType.TObjType.OutRoute))
         {
            cond.ClearOrderType();
            checkRoute = true;
         }

         LoadInt(cond, oneDay, checkRoute, agentID, routes);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.NotVisit) : !checkRoute && routes != null)
         {
            foreach (Org org in routes)
            {
               if (org != null && OrgNotInVisit(org))
               {
                  Add(new OrderDetailRepresentation(DateTime.MinValue,
                     new ObjType(ObjType.TObjType.NotVisit),
                     DateTime.MinValue, DateTime.MinValue, org, 0, 0, 0, org, oneDay));
               }
            }
         }

         filtersAvailable.Add(new ObjType(ObjType.TObjType.NotVisit));
         filtersAvailable.Add(new ObjType(ObjType.TObjType.OutRoute));

         return routes;
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
               if (checkRoute &&
                  FmDetail.IsCreatedBySelectedAgentRoute(
                  order.org, agentID, order.created))
                  continue;

               orderCount++;
               sum += order.DSum;
               Add(new OrderDetailRepresentation(order.Created,
                  new ObjType(ObjType.TObjType.OtOrder),
                  order.Date, order.Sended, order.org,
                     order.DSum, 0, order.Qty, order, oneDay,
                     order.remark));
            }
         }

         cdata = DataModule.Get("Visit");
         CheckFiltersForDocType(cdata, ObjType.TObjType.OtVisit, filtersAvailable);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.OtVisit) : true && cdata != null)
         {
            foreach (Visit visit in cdata.Data)
            {
               if (checkRoute &&
                  FmDetail.IsCreatedBySelectedAgentRoute(visit.org,
                  agentID, visit.date))
                  continue;

               Add(new OrderDetailRepresentation(visit.date,
                  new ObjType(ObjType.TObjType.OtVisit),
                  visit.date, visit.sended, visit.org, 0.0, 0, 0,
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
                  FmDetail.IsCreatedBySelectedAgentRoute(remnants.org,
                  agentID, remnants.date))
                  continue;

               Add(new OrderDetailRepresentation(remnants.date,
                  new ObjType(ObjType.TObjType.OtOrgRemnants),
                  remnants.date, remnants.sended, remnants.org, 0.0, 0, 0,
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
               if (checkRoute && FmDetail.IsCreatedBySelectedAgentRoute(
                  pko.org, agentID, pko.created))
                  continue;

               pkoAdded = true;
               Add(new OrderDetailRepresentation(pko.created,
                  new ObjType(ObjType.TObjType.PKO),
                  pko.date, pko.sended, pko.org, pko.Sum, 0, 0,
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
               if (checkRoute && FmDetail.IsCreatedBySelectedAgentRoute(
                  pko.org, agentID, pko.created))
                  continue;

               pkoAdded = true;
               Add(new OrderDetailRepresentation(pko.created,
                  new ObjType(ObjType.TObjType.PKO),
                  pko.date, pko.sended, pko.org, pko.Sum, 0, 0,
                  pko, oneDay, pko.remark));
            }
         }

         cdata = DataModule.Get(Returns.OBJECT_NAME);
         CheckFiltersForDocType(cdata, ObjType.TObjType.OtReturn, filtersAvailable);

         if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.OtReturn) : true && cdata != null)
         {
            foreach (Returns returns in cdata.Data)
            {
               if (checkRoute &&
                  FmDetail.IsCreatedBySelectedAgentRoute(returns.org,
                  agentID, returns.date))
                  continue;

               Add(new OrderDetailRepresentation(returns.Created,
                  new ObjType(ObjType.TObjType.OtReturn),
                  returns.date, returns.sended, returns.org, 0.0, 0, 0,
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
                  FmDetail.IsCreatedBySelectedAgentRoute(
                  sales.org, agentID, sales.created))
                  continue;

               orderCount++;
               sum += sales.DSum;
               Add(new OrderDetailRepresentation(sales.Created,
                  new ObjType(ObjType.TObjType.Sales),
                  sales.Date, sales.Sended, sales.org,
                     sales.DSum, 0, sales.Qty, sales, oneDay,
                     sales.remark));
            }
         }

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

               Add(new AnswerDetailRepresentation(answer.created,
                  new ObjType(ObjType.TObjType.Answer),
                  answer.created, answer.sended, answer.org, 0.0, 0, 0,
                  answer, oneDay));
            }
         }
#endif
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
         DataSet<int, Visit> dsVisit = (DataSet<int, Visit>)DataModule.Get(Visit.OBJECT_NAME);

         if (dsOrder != null)
            foreach (Order order in dsOrder.Data)
            {
               if (order.id.CompareTo(org.id) == 0)
                  return false;
            }

         if (dsVisit != null)
            foreach (Visit visit in dsVisit.Data)
            {
               if (visit.id.CompareTo(org.id) == 0)
                  return false;
            }

         return true;
      }

      //Возвращает список организаций - маршрут за период
      public static List<Org> GetRoutePeriod(DateTime begin, DateTime end, string agentID)
      {
         List<Org> result = new List<Org>();
         List<string> dayProcessed = new List<string>();
         DataSet<int, OrgFolder> routes = (DataSet<int, OrgFolder>)DataModule.Get(OrgFolder.OBJECT_NAME);
         DataSet<int, CommonConfig> configs = (DataSet<int, CommonConfig>)DataModule.Get(CommonConfig.OBJECT_NAME);

         SummaryData sd = new SummaryData(configs);

         while (begin.Date < end.Date)
         {
            WeekDay weekDay = new WeekDay(begin.DayOfWeek);

            List<OrgFolderItem> items = sd.GetAgentRoute(agentID, begin, routes.Data);
            if (items != null)
            {
               foreach (OrgFolderItem item in items)
                  if (!result.Contains(item.org))
                     result.Add(item.org);
            }

            //if (!dayProcessed.Contains(weekDay.FullName))
            //{
            //   foreach (OrgFolder orgFolder in routes.Data)
            //   {
            //      if (orgFolder.name.Equals(weekDay.FullName))
            //         foreach (OrgFolderItem item in orgFolder.items)
            //            if (!result.Contains(item.org))
            //               result.Add(item.org);
            //   }

            //   dayProcessed.Add(weekDay.FullName);
            //}

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

#if FOCUSED_GROUP
   class FocustReport
   {
      private static int docNumber = 0;

      public static string MakeReport(Agent agent, ICollection orders)
      {
         DataSet<string, ManagerFolder> dsFolder = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME, false);
         dsFolder.Filter = String.Format("userid in ('{0}')", agent.id);

         Thread t = DataModule.RefreshDataSet(dsFolder, Config.GetConfig().GetConnection(), false, null);
         t.Join();

         string url = System.IO.Path.GetTempPath() + String.Format("focus_data_{0}.html", ++docNumber);
         HtmlWriter sw = new HtmlWriter(url);

         sw.WriteTitle(String.Format("{0} не проданный фокусный товар", agent.Name));

         string[] data = { "Дата", "Контрагнет", "Товар", "Примечание" };
         sw.WriteTableHead(data);

         foreach (Order o in orders)
         {
            if (o.focusedFolders != null)
            {
               foreach (Order.FocusItem fi in o.focusedFolders)
               {
                  string folder = (dsFolder.ContainsKey(fi.fid)) ? dsFolder[fi.fid].name : String.Format("папка с кодом '<{0}>'", fi.fid);
                  data[0] = o.Date.ToString("dd.MM.yyyy");
                  data[1] = o.OrgName;
                  data[2] = folder;
                  data[3] = fi.remark;

                  sw.WriteTableRow(data);
               }
            }
         }

         sw.WriteTableTail();

         sw.Flush();
         sw.Close();
         return url;
      }
   }
#endif
}