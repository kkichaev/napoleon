namespace GRSoft.NapoleonManager
{
   partial class MainForm
   {
      /// <summary>
      /// Требуется переменная конструктора.
      /// </summary>
      protected System.ComponentModel.IContainer components = null;

      /// <summary>
      /// Освободить все используемые ресурсы.
      /// </summary>
      /// <param name="disposing">истинно, если управляемый ресурс должен быть удален; иначе ложно.</param>
      protected override void Dispose(bool disposing)
      {
         if (disposing && (components != null))
         {
            components.Dispose();
         }
         base.Dispose(disposing);
      }

      #region Код, автоматически созданный конструктором форм Windows

      /// <summary>
      /// Обязательный метод для поддержки конструктора - не изменяйте
      /// содержимое данного метода при помощи редактора кода.
      /// </summary>
      protected void InitializeComponent()
      {
         this.components = new System.ComponentModel.Container();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MainForm));
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle2 = new System.Windows.Forms.DataGridViewCellStyle();
         this.btnTask = new System.Windows.Forms.ToolStripButton();
         this.dtpBeginDate = new System.Windows.Forms.DateTimePicker();
         this.dtpEndDate = new System.Windows.Forms.DateTimePicker();
         this.menuAgentsSummary = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.smiDetail = new System.Windows.Forms.ToolStripMenuItem();
         this.smiRoute = new System.Windows.Forms.ToolStripMenuItem();
         this.smiWriteMessage = new System.Windows.Forms.ToolStripMenuItem();
         this.smiInfo = new System.Windows.Forms.ToolStripMenuItem();
         this.tsbConfig = new System.Windows.Forms.ToolStrip();
         this.tsbSelectRange = new System.Windows.Forms.ToolStripSplitButton();
         this.tsmiToday = new System.Windows.Forms.ToolStripMenuItem();
         this.tsmiRange = new System.Windows.Forms.ToolStripMenuItem();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.tsbMakeHtml = new System.Windows.Forms.ToolStripButton();
         this.tsbConfigBtn = new System.Windows.Forms.ToolStripButton();
         this.btnOrderReport = new System.Windows.Forms.ToolStripButton();
         this.btnCensus = new System.Windows.Forms.ToolStripButton();
         this.btnPriceRemnants = new System.Windows.Forms.ToolStripButton();
         this.btnGpsReport = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.panel1 = new System.Windows.Forms.Panel();
         this.tgvAgentsSummary = new GRSoft.UILib.TreeGridView();
         this.tgvAgentsSummaryAgent = new GRSoft.UILib.TreeGridColumn();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.LastAccess = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tgvAgentsSummaryProgres = new System.Windows.Forms.DataGridViewImageColumn();
         this.tgvAgentsSummaryAgentID = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tgvAgentsSummaryDivision = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tgvAgentsSummaryProgressValue = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.MissedOrder = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.UniqOrder = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.linkLabel1 = new System.Windows.Forms.LinkLabel();
         this.lbVersion = new System.Windows.Forms.Label();
         this.cbConfig = new System.Windows.Forms.ComboBox();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.menuAgentsSummary.SuspendLayout();
         this.tsbConfig.SuspendLayout();
         this.panel1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.tgvAgentsSummary)).BeginInit();
         this.SuspendLayout();
         // 
         // btnTask
         // 
         this.btnTask.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnTask.Image = global::GRSoft.NapoleonMonitor.Properties.Resources.taskdoc;
         this.btnTask.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnTask.Name = "btnTask";
         this.btnTask.Size = new System.Drawing.Size(23, 22);
         this.btnTask.Text = "Задачи";
         this.btnTask.Click += new System.EventHandler(this.btnTask_Click);
         // 
         // dtpBeginDate
         // 
         this.dtpBeginDate.Location = new System.Drawing.Point(54, 2);
         this.dtpBeginDate.Name = "dtpBeginDate";
         this.dtpBeginDate.Size = new System.Drawing.Size(144, 20);
         this.dtpBeginDate.TabIndex = 1;
         this.dtpBeginDate.Value = new System.DateTime(2010, 4, 22, 0, 0, 0, 0);
         this.dtpBeginDate.ValueChanged += new System.EventHandler(this.dtpBeginDate_ValueChanged);
         // 
         // dtpEndDate
         // 
         this.dtpEndDate.Location = new System.Drawing.Point(229, 2);
         this.dtpEndDate.Name = "dtpEndDate";
         this.dtpEndDate.Size = new System.Drawing.Size(144, 20);
         this.dtpEndDate.TabIndex = 5;
         this.dtpEndDate.Value = new System.DateTime(2010, 5, 11, 0, 0, 0, 0);
         this.dtpEndDate.ValueChanged += new System.EventHandler(this.dtpEndDate_ValueChanged);
         // 
         // menuAgentsSummary
         // 
         this.menuAgentsSummary.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.smiDetail,
            this.smiRoute,
            this.smiWriteMessage,
            this.smiInfo});
         this.menuAgentsSummary.Name = "menuAgentsSummary";
         this.menuAgentsSummary.Size = new System.Drawing.Size(233, 92);
         this.menuAgentsSummary.Opening += new System.ComponentModel.CancelEventHandler(this.menuAgentsSummary_Opening);
         // 
         // smiDetail
         // 
         this.smiDetail.Name = "smiDetail";
         this.smiDetail.Size = new System.Drawing.Size(232, 22);
         this.smiDetail.Text = "Подробно...";
         this.smiDetail.Click += new System.EventHandler(this.smiDetail_Click);
         // 
         // smiRoute
         // 
         this.smiRoute.Name = "smiRoute";
         this.smiRoute.Size = new System.Drawing.Size(232, 22);
         this.smiRoute.Text = "Редактирование маршрута...";
         this.smiRoute.Click += new System.EventHandler(this.smiRoute_Click);
         // 
         // smiWriteMessage
         // 
         this.smiWriteMessage.Name = "smiWriteMessage";
         this.smiWriteMessage.Size = new System.Drawing.Size(232, 22);
         this.smiWriteMessage.Text = "Написать сообщение...";
         this.smiWriteMessage.Click += new System.EventHandler(this.smiWriteMessage_Click);
         // 
         // smiInfo
         // 
         this.smiInfo.Name = "smiInfo";
         this.smiInfo.Size = new System.Drawing.Size(232, 22);
         this.smiInfo.Text = "Инфо...";
         this.smiInfo.Click += new System.EventHandler(this.smiInfo_Click);
         // 
         // tsbConfig
         // 
         this.tsbConfig.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbSelectRange,
            this.toolStripLabel1,
            this.toolStripLabel2,
            this.btnRefresh,
            this.btnTask,
            this.toolStripSeparator1,
//            this.tsbMakeHtml,
            this.tsbConfigBtn,
            //this.btnOrderReport,
//            this.btnCensus,
//            this.btnPriceRemnants,
            this.btnGpsReport});
         this.tsbConfig.Location = new System.Drawing.Point(0, 0);
         this.tsbConfig.Name = "tsbConfig";
         this.tsbConfig.Size = new System.Drawing.Size(812, 25);
         this.tsbConfig.TabIndex = 10;
         // 
         // tsbSelectRange
         // 
         this.tsbSelectRange.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSelectRange.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsmiToday,
            this.tsmiRange});
         this.tsbSelectRange.Image = ((System.Drawing.Image)(resources.GetObject("tsbSelectRange.Image")));
         this.tsbSelectRange.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSelectRange.Name = "tsbSelectRange";
         this.tsbSelectRange.Size = new System.Drawing.Size(32, 22);
         this.tsbSelectRange.Text = "toolStripSplitButton1";
         this.tsbSelectRange.ToolTipText = "За сегодня";
         this.tsbSelectRange.ButtonClick += new System.EventHandler(this.tsbSelectRange_Click);
         // 
         // tsmiToday
         // 
         this.tsmiToday.Checked = true;
         this.tsmiToday.CheckState = System.Windows.Forms.CheckState.Checked;
         this.tsmiToday.Image = ((System.Drawing.Image)(resources.GetObject("tsmiToday.Image")));
         this.tsmiToday.Name = "tsmiToday";
         this.tsmiToday.Size = new System.Drawing.Size(141, 22);
         this.tsmiToday.Text = "За сегодня";
         this.tsmiToday.Click += new System.EventHandler(this.tsmiToday_Click);
         // 
         // tsmiRange
         // 
         this.tsmiRange.Image = ((System.Drawing.Image)(resources.GetObject("tsmiRange.Image")));
         this.tsmiRange.Name = "tsmiRange";
         this.tsmiRange.Size = new System.Drawing.Size(141, 22);
         this.tsmiRange.Text = "За интервал";
         this.tsmiRange.Click += new System.EventHandler(this.tsmiRange_Click);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(13, 22);
         this.toolStripLabel1.Text = "с";
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Margin = new System.Windows.Forms.Padding(150, 1, 0, 2);
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(21, 22);
         this.toolStripLabel2.Text = "по";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonMonitor.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Margin = new System.Windows.Forms.Padding(160, 1, 0, 2);
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "toolStripButton1";
         this.btnRefresh.ToolTipText = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // tsbMakeHtml
         // 
         this.tsbMakeHtml.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbMakeHtml.Image = ((System.Drawing.Image)(resources.GetObject("tsbMakeHtml.Image")));
         this.tsbMakeHtml.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbMakeHtml.Name = "tsbMakeHtml";
         this.tsbMakeHtml.Size = new System.Drawing.Size(23, 22);
         this.tsbMakeHtml.Text = "Составить отчет";
         this.tsbMakeHtml.Click += new System.EventHandler(this.tsbMakeHtml_Click);
         // 
         // tsbConfigBtn
         // 
         this.tsbConfigBtn.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.tsbConfigBtn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbConfigBtn.Image = ((System.Drawing.Image)(resources.GetObject("tsbConfigBtn.Image")));
         this.tsbConfigBtn.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbConfigBtn.Name = "tsbConfigBtn";
         this.tsbConfigBtn.Size = new System.Drawing.Size(23, 22);
         this.tsbConfigBtn.Text = "Настройки входа";
         this.tsbConfigBtn.Click += new System.EventHandler(this.tsbConfigBtn_Click);
         // 
         // btnOrderReport
         // 
         this.btnOrderReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnOrderReport.Image = ((System.Drawing.Image)(resources.GetObject("btnOrderReport.Image")));
         this.btnOrderReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnOrderReport.Name = "btnOrderReport";
         this.btnOrderReport.Size = new System.Drawing.Size(23, 22);
         this.btnOrderReport.Text = "Отчет по заявкам";
         this.btnOrderReport.Click += new System.EventHandler(this.btnOrderReport_Click);
         // 
         // btnCensus
         // 
         this.btnCensus.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnCensus.Image = ((System.Drawing.Image)(resources.GetObject("btnCensus.Image")));
         this.btnCensus.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnCensus.Name = "btnCensus";
         this.btnCensus.Size = new System.Drawing.Size(23, 22);
         this.btnCensus.Text = "Census";
         this.btnCensus.Click += new System.EventHandler(this.btnCensus_Click);
         // 
         // btnPriceRemnants
         // 
         this.btnPriceRemnants.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnPriceRemnants.Image = ((System.Drawing.Image)(resources.GetObject("btnPriceRemnants.Image")));
         this.btnPriceRemnants.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnPriceRemnants.Name = "btnPriceRemnants";
         this.btnPriceRemnants.Size = new System.Drawing.Size(23, 22);
         this.btnPriceRemnants.Text = "Прайс";
         this.btnPriceRemnants.Visible = false;
         this.btnPriceRemnants.Click += new System.EventHandler(this.btnPriceRemnants_Click);
         // 
         // btnGpsReport
         // 
         this.btnGpsReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnGpsReport.Image = global::GRSoft.NapoleonMonitor.Properties.Resources.goto_;
         this.btnGpsReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnGpsReport.Name = "btnGpsReport";
         this.btnGpsReport.Size = new System.Drawing.Size(23, 22);
         this.btnGpsReport.Text = "Отчет по километражу";
         this.btnGpsReport.Click += new System.EventHandler(this.btnGpsReport_Click);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 480);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(812, 22);
         this.statusStrip1.TabIndex = 11;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // panel1
         // 
         this.panel1.BackColor = System.Drawing.SystemColors.Control;
         this.panel1.Controls.Add(this.tgvAgentsSummary);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7);
         this.panel1.Size = new System.Drawing.Size(812, 455);
         this.panel1.TabIndex = 12;
         // 
         // tgvAgentsSummary
         // 
         this.tgvAgentsSummary.AllowUserToAddRows = false;
         this.tgvAgentsSummary.AllowUserToDeleteRows = false;
         this.tgvAgentsSummary.AllowUserToResizeRows = false;
         this.tgvAgentsSummary.ColumnHeadersHeight = 22;
         this.tgvAgentsSummary.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.tgvAgentsSummaryAgent,
            this.Column1,
            this.LastAccess,
            this.tgvAgentsSummaryProgres,
            this.tgvAgentsSummaryAgentID,
            this.tgvAgentsSummaryDivision,
            this.tgvAgentsSummaryProgressValue,
            this.MissedOrder,
            this.UniqOrder});
         this.tgvAgentsSummary.ContextMenuStrip = this.menuAgentsSummary;
         this.tgvAgentsSummary.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tgvAgentsSummary.EditMode = System.Windows.Forms.DataGridViewEditMode.EditProgrammatically;
         this.tgvAgentsSummary.ImageList = null;
         this.tgvAgentsSummary.Location = new System.Drawing.Point(7, 7);
         this.tgvAgentsSummary.MultiSelect = false;
         this.tgvAgentsSummary.Name = "tgvAgentsSummary";
         this.tgvAgentsSummary.RowHeadersVisible = false;
         this.tgvAgentsSummary.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.tgvAgentsSummary.Size = new System.Drawing.Size(798, 441);
         this.tgvAgentsSummary.TabIndex = 9;
         this.tgvAgentsSummary.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.tgvAgentsSummary_CellFormatting);
         this.tgvAgentsSummary.DoubleClick += new System.EventHandler(this.tgvAgentsSummary_DoubleClick);
         this.tgvAgentsSummary.MouseDown += new System.Windows.Forms.MouseEventHandler(this.tgvAgentsSummary_MouseDown);
         // 
         // tgvAgentsSummaryAgent
         // 
         this.tgvAgentsSummaryAgent.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.tgvAgentsSummaryAgent.DataPropertyName = "Name";
         this.tgvAgentsSummaryAgent.DefaultNodeImage = null;
         this.tgvAgentsSummaryAgent.FillWeight = 221.5736F;
         this.tgvAgentsSummaryAgent.HeaderText = "Подразделение/Агент";
         this.tgvAgentsSummaryAgent.Name = "tgvAgentsSummaryAgent";
         this.tgvAgentsSummaryAgent.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DataPropertyName = "Visits";
         this.Column1.FillWeight = 55.3934F;
         this.Column1.HeaderText = "Визиты";
         this.Column1.Name = "Column1";
         this.Column1.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // LastAccess
         // 
         this.LastAccess.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.LastAccess.DataPropertyName = "LastAccess";
         this.LastAccess.FillWeight = 60F;
         this.LastAccess.HeaderText = "Посл. доступ";
         this.LastAccess.Name = "LastAccess";
         this.LastAccess.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // tgvAgentsSummaryProgres
         // 
         this.tgvAgentsSummaryProgres.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.tgvAgentsSummaryProgres.DataPropertyName = "ProgressImage";
         dataGridViewCellStyle2.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleCenter;
         dataGridViewCellStyle2.NullValue = null;
         dataGridViewCellStyle2.Padding = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.tgvAgentsSummaryProgres.DefaultCellStyle = dataGridViewCellStyle2;
         this.tgvAgentsSummaryProgres.FillWeight = 55.3934F;
         this.tgvAgentsSummaryProgres.HeaderText = "Прогресс";
         this.tgvAgentsSummaryProgres.Name = "tgvAgentsSummaryProgres";
         this.tgvAgentsSummaryProgres.Resizable = System.Windows.Forms.DataGridViewTriState.False;
         // 
         // tgvAgentsSummaryAgentID
         // 
         this.tgvAgentsSummaryAgentID.DataPropertyName = "AgentID";
         this.tgvAgentsSummaryAgentID.HeaderText = "Column1";
         this.tgvAgentsSummaryAgentID.Name = "tgvAgentsSummaryAgentID";
         this.tgvAgentsSummaryAgentID.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         this.tgvAgentsSummaryAgentID.Visible = false;
         // 
         // tgvAgentsSummaryDivision
         // 
         this.tgvAgentsSummaryDivision.HeaderText = "Division";
         this.tgvAgentsSummaryDivision.Name = "tgvAgentsSummaryDivision";
         this.tgvAgentsSummaryDivision.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         this.tgvAgentsSummaryDivision.Visible = false;
         // 
         // tgvAgentsSummaryProgressValue
         // 
         this.tgvAgentsSummaryProgressValue.DataPropertyName = "ProgressValue";
         this.tgvAgentsSummaryProgressValue.HeaderText = "ProgressValue";
         this.tgvAgentsSummaryProgressValue.Name = "tgvAgentsSummaryProgressValue";
         this.tgvAgentsSummaryProgressValue.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         this.tgvAgentsSummaryProgressValue.Visible = false;
         // 
         // MissedOrder
         // 
         this.MissedOrder.DataPropertyName = "HasMissedOrder";
         this.MissedOrder.HeaderText = "Невыгруженные заявки";
         this.MissedOrder.Name = "MissedOrder";
         this.MissedOrder.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         this.MissedOrder.Visible = false;
         // 
         // UniqOrder
         // 
         this.UniqOrder.DataPropertyName = "UniqOrders";
         this.UniqOrder.HeaderText = "Уникальные заявки";
         this.UniqOrder.Name = "UniqOrder";
         this.UniqOrder.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         this.UniqOrder.Visible = false;
         // 
         // linkLabel1
         // 
         this.linkLabel1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.linkLabel1.AutoSize = true;
         this.linkLabel1.Location = new System.Drawing.Point(576, 484);
         this.linkLabel1.Name = "linkLabel1";
         this.linkLabel1.Size = new System.Drawing.Size(205, 14);
         this.linkLabel1.TabIndex = 13;
         this.linkLabel1.TabStop = true;
         this.linkLabel1.Text = "Гильдия разработчиков www.grsoft.ru";
         this.linkLabel1.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.linkLabel1_LinkClicked);
         // 
         // lbVersion
         // 
         this.lbVersion.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.lbVersion.AutoSize = true;
         this.lbVersion.Location = new System.Drawing.Point(392, 484);
         this.lbVersion.Name = "lbVersion";
         this.lbVersion.Size = new System.Drawing.Size(35, 14);
         this.lbVersion.TabIndex = 14;
         this.lbVersion.Text = "label1";
         // 
         // cbConfig
         // 
         this.cbConfig.FormattingEnabled = true;
         this.cbConfig.Location = new System.Drawing.Point(595, 2);
         this.cbConfig.Name = "cbConfig";
         this.cbConfig.Size = new System.Drawing.Size(160, 22);
         this.cbConfig.TabIndex = 15;
         this.cbConfig.SelectionChangeCommitted += new System.EventHandler(this.cbConfig_SelectionChangeCommitted);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Agent";
         this.dataGridViewTextBoxColumn1.HeaderText = "Агент";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.DataPropertyName = "OrderCount";
         this.dataGridViewTextBoxColumn2.HeaderText = "Количество заявок";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.DataPropertyName = "Sum";
         this.dataGridViewTextBoxColumn3.HeaderText = "Сумма";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.DataPropertyName = "AgentId";
         this.dataGridViewTextBoxColumn4.HeaderText = "Column1";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         this.dataGridViewTextBoxColumn4.Visible = false;
         // 
         // MainForm
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(812, 502);
         this.Controls.Add(this.lbVersion);
         this.Controls.Add(this.linkLabel1);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.cbConfig);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.dtpEndDate);
         this.Controls.Add(this.dtpBeginDate);
         this.Controls.Add(this.tsbConfig);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.KeyPreview = true;
         this.Name = "MainForm";
         this.Text = "Дела";
         this.Load += new System.EventHandler(this.MainForm_Load);
         this.Move += new System.EventHandler(this.MainForm_Move);
         this.menuAgentsSummary.ResumeLayout(false);
         this.tsbConfig.ResumeLayout(false);
         this.tsbConfig.PerformLayout();
         this.panel1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.tgvAgentsSummary)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      protected System.Windows.Forms.DateTimePicker dtpBeginDate;
      protected System.Windows.Forms.DateTimePicker dtpEndDate;
      public System.Windows.Forms.ContextMenuStrip menuAgentsSummary;
      protected System.Windows.Forms.ToolStripMenuItem smiDetail;
      public System.Windows.Forms.ToolStripMenuItem smiRoute;
      public GRSoft.UILib.TreeGridView tgvAgentsSummary;
      protected System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      protected System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      protected System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      protected System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      protected System.Windows.Forms.ToolStripSplitButton tsbSelectRange;
      protected System.Windows.Forms.ToolStripMenuItem tsmiToday;
      protected System.Windows.Forms.ToolStripMenuItem tsmiRange;
      protected System.Windows.Forms.ToolStripLabel toolStripLabel1;
      protected System.Windows.Forms.ToolStripLabel toolStripLabel2;
      protected System.Windows.Forms.ToolStripButton btnRefresh;
      protected System.Windows.Forms.StatusStrip statusStrip1;
      protected System.Windows.Forms.Panel panel1;
      protected System.Windows.Forms.LinkLabel linkLabel1;
      protected System.Windows.Forms.ToolStripMenuItem smiWriteMessage;
      protected System.Windows.Forms.ToolStripMenuItem smiInfo;
      protected System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      protected System.Windows.Forms.ToolStripButton tsbConfigBtn;
      protected System.Windows.Forms.Label lbVersion;
      public System.Windows.Forms.ToolStrip tsbConfig;
      public System.Windows.Forms.ToolStripButton tsbMakeHtml;
      public System.Windows.Forms.ToolStripButton btnOrderReport;
      public System.Windows.Forms.ToolStripButton btnPriceRemnants;
      protected System.Windows.Forms.ToolStripButton btnGpsReport;
      public System.Windows.Forms.ToolStripButton btnCensus;
      protected System.Windows.Forms.ComboBox cbConfig;
      protected System.Windows.Forms.ToolStripButton btnTask;
      protected UILib.TreeGridColumn tgvAgentsSummaryAgent;
      protected System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      protected System.Windows.Forms.DataGridViewTextBoxColumn LastAccess;
      protected System.Windows.Forms.DataGridViewImageColumn tgvAgentsSummaryProgres;
      protected System.Windows.Forms.DataGridViewTextBoxColumn tgvAgentsSummaryAgentID;
      protected System.Windows.Forms.DataGridViewTextBoxColumn tgvAgentsSummaryDivision;
      protected System.Windows.Forms.DataGridViewTextBoxColumn tgvAgentsSummaryProgressValue;
      protected System.Windows.Forms.DataGridViewTextBoxColumn MissedOrder;
      protected System.Windows.Forms.DataGridViewTextBoxColumn UniqOrder;
   }
}

