namespace GRSoft.NapoleonManager
{
   partial class MainForm
   {

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
         this.dtpBeginDate = new System.Windows.Forms.DateTimePicker();
         this.btnTask = new System.Windows.Forms.ToolStripButton();
         this.menuAgentsSummary = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.smiDetail = new System.Windows.Forms.ToolStripMenuItem();
         this.smiRoute = new System.Windows.Forms.ToolStripMenuItem();
         this.smiWriteMessage = new System.Windows.Forms.ToolStripMenuItem();
         this.smiInfo = new System.Windows.Forms.ToolStripMenuItem();
         this.tsbConfig = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnDivision = new System.Windows.Forms.ToolStripButton();
         this.tsbMakeHtml = new System.Windows.Forms.ToolStripButton();
         this.tsbConfigBtn = new System.Windows.Forms.ToolStripButton();
         this.btnOrderReport = new System.Windows.Forms.ToolStripButton();
         this.btnCensus = new System.Windows.Forms.ToolStripButton();
         this.btnPriceRemnants = new System.Windows.Forms.ToolStripButton();
         this.btnGpsReport = new System.Windows.Forms.ToolStripButton();
         this.btnRouteAp = new System.Windows.Forms.ToolStripButton();
         this.btnUserLocation = new System.Windows.Forms.ToolStripButton();
         this.btnReport = new System.Windows.Forms.ToolStripButton();
         this.tsbCoverArea = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.panel1 = new System.Windows.Forms.Panel();
         this.tgvAgentsSummary = new GRSoft.UILib.TreeGridView();
         this.tgvAgentsSummaryAgent = new GRSoft.UILib.TreeGridColumn();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tgvAgentsSummaryCount = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tgvAgentsSummarySum = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.LastAccess = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tgvAgentsSummaryProgres = new System.Windows.Forms.DataGridViewImageColumn();
         this.tgvAgentsSummaryAgentID = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tgvAgentsSummaryDivision = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tgvAgentsSummaryProgressValue = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.MissedOrder = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.UniqOrders = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.cbConfig = new System.Windows.Forms.ComboBox();
         this.linkLabel1 = new System.Windows.Forms.LinkLabel();
         this.lbVersion = new System.Windows.Forms.Label();
         this.label1 = new System.Windows.Forms.Label();
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
         // dtpBeginDate
         // 
         this.dtpBeginDate.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.dtpBeginDate.Location = new System.Drawing.Point(11, 7);
         this.dtpBeginDate.Name = "dtpBeginDate";
         this.dtpBeginDate.Size = new System.Drawing.Size(184, 26);
         this.dtpBeginDate.TabIndex = 1;
         this.dtpBeginDate.Value = new System.DateTime(2010, 4, 22, 0, 0, 0, 0);
         // 
         // btnTask
         // 
         this.btnTask.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnTask.Image = ((System.Drawing.Image)(resources.GetObject("btnTask.Image")));
         this.btnTask.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnTask.Name = "btnTask";
         this.btnTask.Size = new System.Drawing.Size(36, 36);
         this.btnTask.Text = "Задачи";
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
         // 
         // smiRoute
         // 
         this.smiRoute.Name = "smiRoute";
         this.smiRoute.Size = new System.Drawing.Size(232, 22);
         this.smiRoute.Text = "Редактирование маршрута...";
         // 
         // smiWriteMessage
         // 
         this.smiWriteMessage.Name = "smiWriteMessage";
         this.smiWriteMessage.Size = new System.Drawing.Size(232, 22);
         this.smiWriteMessage.Text = "Написать сообщение...";
         // 
         // smiInfo
         // 
         this.smiInfo.Name = "smiInfo";
         this.smiInfo.Size = new System.Drawing.Size(232, 22);
         this.smiInfo.Text = "Инфо...";
         // 
         // tsbConfig
         // 
         this.tsbConfig.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.tsbConfig.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.toolStripSeparator1,
            this.btnTask,
            this.btnDivision,
            this.tsbMakeHtml,
            this.tsbConfigBtn,
            this.btnOrderReport,
            this.btnCensus,
            this.btnPriceRemnants,
            this.btnGpsReport,
            this.btnRouteAp,
            this.btnUserLocation,
            this.btnReport,
            this.tsbCoverArea});
         this.tsbConfig.Location = new System.Drawing.Point(0, 0);
         this.tsbConfig.Name = "tsbConfig";
         this.tsbConfig.Size = new System.Drawing.Size(998, 39);
         this.tsbConfig.TabIndex = 10;
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Margin = new System.Windows.Forms.Padding(190, 1, 0, 2);
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(36, 36);
         this.btnRefresh.Text = "toolStripButton1";
         this.btnRefresh.ToolTipText = "Обновить";
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 39);
         // 
         // btnDivision
         // 
         this.btnDivision.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.btnDivision.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDivision.Image = ((System.Drawing.Image)(resources.GetObject("btnDivision.Image")));
         this.btnDivision.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDivision.Name = "btnDivision";
         this.btnDivision.Padding = new System.Windows.Forms.Padding(0, 0, 10, 0);
         this.btnDivision.Size = new System.Drawing.Size(46, 36);
         this.btnDivision.Text = "Управление командой";
         // 
         // tsbMakeHtml
         // 
         this.tsbMakeHtml.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbMakeHtml.Image = ((System.Drawing.Image)(resources.GetObject("tsbMakeHtml.Image")));
         this.tsbMakeHtml.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbMakeHtml.Name = "tsbMakeHtml";
         this.tsbMakeHtml.Size = new System.Drawing.Size(36, 36);
         this.tsbMakeHtml.Text = "Составить отчет";
         this.tsbMakeHtml.Visible = false;
         // 
         // tsbConfigBtn
         // 
         this.tsbConfigBtn.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.tsbConfigBtn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbConfigBtn.Image = ((System.Drawing.Image)(resources.GetObject("tsbConfigBtn.Image")));
         this.tsbConfigBtn.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbConfigBtn.Name = "tsbConfigBtn";
         this.tsbConfigBtn.Size = new System.Drawing.Size(36, 36);
         this.tsbConfigBtn.Text = "Настройки входа";
         // 
         // btnOrderReport
         // 
         this.btnOrderReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnOrderReport.Image = ((System.Drawing.Image)(resources.GetObject("btnOrderReport.Image")));
         this.btnOrderReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnOrderReport.Name = "btnOrderReport";
         this.btnOrderReport.Size = new System.Drawing.Size(36, 36);
         this.btnOrderReport.Text = "Отчет по заявкам";
         this.btnOrderReport.Visible = false;
         // 
         // btnCensus
         // 
         this.btnCensus.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnCensus.Image = ((System.Drawing.Image)(resources.GetObject("btnCensus.Image")));
         this.btnCensus.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnCensus.Name = "btnCensus";
         this.btnCensus.Size = new System.Drawing.Size(36, 36);
         this.btnCensus.Text = "Census";
         // 
         // btnPriceRemnants
         // 
         this.btnPriceRemnants.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnPriceRemnants.Image = ((System.Drawing.Image)(resources.GetObject("btnPriceRemnants.Image")));
         this.btnPriceRemnants.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnPriceRemnants.Name = "btnPriceRemnants";
         this.btnPriceRemnants.Size = new System.Drawing.Size(36, 36);
         this.btnPriceRemnants.Text = "Прайс";
         this.btnPriceRemnants.Visible = false;
         // 
         // btnGpsReport
         // 
         this.btnGpsReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnGpsReport.Image = ((System.Drawing.Image)(resources.GetObject("btnGpsReport.Image")));
         this.btnGpsReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnGpsReport.Name = "btnGpsReport";
         this.btnGpsReport.Size = new System.Drawing.Size(36, 36);
         this.btnGpsReport.Text = "Отчет по километражу";
         this.btnGpsReport.Visible = false;
         // 
         // btnRouteAp
         // 
         this.btnRouteAp.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRouteAp.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRouteAp.Name = "btnRouteAp";
         this.btnRouteAp.Size = new System.Drawing.Size(23, 36);
         this.btnRouteAp.Text = "Утверждение маршрута";
         this.btnRouteAp.Visible = false;
         // 
         // btnUserLocation
         // 
         this.btnUserLocation.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnUserLocation.Image = ((System.Drawing.Image)(resources.GetObject("btnUserLocation.Image")));
         this.btnUserLocation.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnUserLocation.Name = "btnUserLocation";
         this.btnUserLocation.Size = new System.Drawing.Size(36, 36);
         this.btnUserLocation.Text = "Агенты в полях";
         // 
         // btnReport
         // 
         this.btnReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnReport.Image = global::GRSoft.NapoleonManager.Properties.Resources.excel;
         this.btnReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnReport.Name = "btnReport";
         this.btnReport.Size = new System.Drawing.Size(36, 36);
         this.btnReport.Text = "Отчеты";
         // 
         // tsbCoverArea
         // 
         this.tsbCoverArea.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbCoverArea.Image = global::GRSoft.NapoleonManager.Properties.Resources.cover_area;
         this.tsbCoverArea.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbCoverArea.Name = "tsbCoverArea";
         this.tsbCoverArea.Size = new System.Drawing.Size(36, 36);
         this.tsbCoverArea.Text = "Покрытие территории";
         this.tsbCoverArea.Click += new System.EventHandler(this.tsbCoverArea_Click);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 558);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(998, 22);
         this.statusStrip1.TabIndex = 11;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // panel1
         // 
         this.panel1.BackColor = System.Drawing.SystemColors.Control;
         this.panel1.Controls.Add(this.tgvAgentsSummary);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 39);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7);
         this.panel1.Size = new System.Drawing.Size(998, 519);
         this.panel1.TabIndex = 12;
         // 
         // tgvAgentsSummary
         // 
         this.tgvAgentsSummary.AllowUserToAddRows = false;
         this.tgvAgentsSummary.AllowUserToDeleteRows = false;
         this.tgvAgentsSummary.AllowUserToResizeRows = false;
         this.tgvAgentsSummary.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.tgvAgentsSummary.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.tgvAgentsSummaryAgent,
            this.Column1,
            this.tgvAgentsSummaryCount,
            this.tgvAgentsSummarySum,
            this.LastAccess,
            this.tgvAgentsSummaryProgres,
            this.tgvAgentsSummaryAgentID,
            this.tgvAgentsSummaryDivision,
            this.tgvAgentsSummaryProgressValue,
            this.MissedOrder,
            this.UniqOrders});
         this.tgvAgentsSummary.ContextMenuStrip = this.menuAgentsSummary;
         this.tgvAgentsSummary.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tgvAgentsSummary.EditMode = System.Windows.Forms.DataGridViewEditMode.EditProgrammatically;
         this.tgvAgentsSummary.ImageList = null;
         this.tgvAgentsSummary.Location = new System.Drawing.Point(7, 7);
         this.tgvAgentsSummary.MultiSelect = false;
         this.tgvAgentsSummary.Name = "tgvAgentsSummary";
         this.tgvAgentsSummary.RowHeadersVisible = false;
         this.tgvAgentsSummary.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.tgvAgentsSummary.Size = new System.Drawing.Size(984, 505);
         this.tgvAgentsSummary.TabIndex = 9;
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
         // tgvAgentsSummaryCount
         // 
         this.tgvAgentsSummaryCount.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.tgvAgentsSummaryCount.DataPropertyName = "Orders";
         this.tgvAgentsSummaryCount.FillWeight = 55.3934F;
         this.tgvAgentsSummaryCount.HeaderText = "Заказы";
         this.tgvAgentsSummaryCount.Name = "tgvAgentsSummaryCount";
         this.tgvAgentsSummaryCount.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // tgvAgentsSummarySum
         // 
         this.tgvAgentsSummarySum.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.tgvAgentsSummarySum.DataPropertyName = "DocSum";
         dataGridViewCellStyle1.Format = "C2";
         this.tgvAgentsSummarySum.DefaultCellStyle = dataGridViewCellStyle1;
         this.tgvAgentsSummarySum.FillWeight = 55.3934F;
         this.tgvAgentsSummarySum.HeaderText = "Сумма";
         this.tgvAgentsSummarySum.Name = "tgvAgentsSummarySum";
         this.tgvAgentsSummarySum.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
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
         // UniqOrders
         // 
         this.UniqOrders.DataPropertyName = "UniqOrders";
         this.UniqOrders.HeaderText = "Уникальные заявки";
         this.UniqOrders.Name = "UniqOrders";
         this.UniqOrders.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         this.UniqOrders.Visible = false;
         // 
         // cbConfig
         // 
         this.cbConfig.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.cbConfig.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.cbConfig.FormattingEnabled = true;
         this.cbConfig.Location = new System.Drawing.Point(747, 7);
         this.cbConfig.Name = "cbConfig";
         this.cbConfig.Size = new System.Drawing.Size(160, 26);
         this.cbConfig.TabIndex = 15;
         // 
         // linkLabel1
         // 
         this.linkLabel1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.linkLabel1.AutoSize = true;
         this.linkLabel1.Location = new System.Drawing.Point(884, 562);
         this.linkLabel1.Name = "linkLabel1";
         this.linkLabel1.Size = new System.Drawing.Size(81, 14);
         this.linkLabel1.TabIndex = 13;
         this.linkLabel1.TabStop = true;
         this.linkLabel1.Text = "www.grsoft.ru";
         // 
         // lbVersion
         // 
         this.lbVersion.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
         this.lbVersion.AutoSize = true;
         this.lbVersion.Location = new System.Drawing.Point(5, 562);
         this.lbVersion.Name = "lbVersion";
         this.lbVersion.Size = new System.Drawing.Size(35, 14);
         this.lbVersion.TabIndex = 14;
         this.lbVersion.Text = "label1";
         // 
         // label1
         // 
         this.label1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(756, 562);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(128, 14);
         this.label1.TabIndex = 16;
         this.label1.Text = "Гильдия разработчиков";
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
         this.ClientSize = new System.Drawing.Size(998, 580);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.lbVersion);
         this.Controls.Add(this.linkLabel1);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.cbConfig);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.dtpBeginDate);
         this.Controls.Add(this.tsbConfig);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.KeyPreview = true;
         this.Name = "MainForm";
         this.Text = "Дела";
         this.Load += new System.EventHandler(this.MainForm_Load);
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
      public System.Windows.Forms.ContextMenuStrip menuAgentsSummary;
      protected System.Windows.Forms.ToolStripMenuItem smiDetail;
      public System.Windows.Forms.ToolStripMenuItem smiRoute;
      public GRSoft.UILib.TreeGridView tgvAgentsSummary;
      protected System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      protected System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      protected System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      protected System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      protected System.Windows.Forms.ToolStripButton btnRefresh;
      protected System.Windows.Forms.ToolStripButton btnDivision;
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
      public System.Windows.Forms.DataGridViewTextBoxColumn tgvAgentsSummaryCount;
      public System.Windows.Forms.DataGridViewTextBoxColumn tgvAgentsSummarySum;
      protected System.Windows.Forms.DataGridViewTextBoxColumn LastAccess;
      public System.Windows.Forms.DataGridViewImageColumn tgvAgentsSummaryProgres;
      protected System.Windows.Forms.DataGridViewTextBoxColumn tgvAgentsSummaryAgentID;
      protected System.Windows.Forms.DataGridViewTextBoxColumn tgvAgentsSummaryDivision;
      protected System.Windows.Forms.DataGridViewTextBoxColumn tgvAgentsSummaryProgressValue;
      protected System.Windows.Forms.DataGridViewTextBoxColumn MissedOrder;
      protected System.Windows.Forms.DataGridViewTextBoxColumn UniqOrder;
      private System.Windows.Forms.Label label1;
      private System.ComponentModel.IContainer components;
      private System.Windows.Forms.ToolStripButton btnRouteAp;
      protected System.Windows.Forms.DataGridViewTextBoxColumn UniqOrders;
      private System.Windows.Forms.ToolStripButton btnUserLocation;
      private System.Windows.Forms.ToolStripButton btnReport;
      public System.Windows.Forms.ToolStripButton tsbCoverArea;
   }
}

