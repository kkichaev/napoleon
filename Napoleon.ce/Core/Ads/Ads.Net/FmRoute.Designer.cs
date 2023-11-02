namespace GRSoft.Ads
{
   partial class FmRoute
   {
      /// <summary>
      /// Required designer variable.
      /// </summary>
      private System.ComponentModel.IContainer components = null;

      /// <summary>
      /// Clean up any resources being used.
      /// </summary>
      /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
      protected override void Dispose(bool disposing)
      {
         if (disposing && (components != null))
         {
            components.Dispose();
         }
         base.Dispose(disposing);
      }

      #region Windows Form Designer generated code

      /// <summary>
      /// Required method for Designer support - do not modify
      /// the contents of this method with the code editor.
      /// </summary>
      private void InitializeComponent()
      {
         this.components = new System.ComponentModel.Container();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmRoute));
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle2 = new System.Windows.Forms.DataGridViewCellStyle();
         this.tsbMessage = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator3 = new System.Windows.Forms.ToolStripSeparator();
         this.dtpDate = new System.Windows.Forms.DateTimePicker();
         this.wb = new System.Windows.Forms.WebBrowser();
         this.panel1 = new System.Windows.Forms.Panel();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.tabControl1 = new System.Windows.Forms.TabControl();
         this.tpRoute = new System.Windows.Forms.TabPage();
         this.dgvOrgs = new System.Windows.Forms.DataGridView();
         this.dgvOrgsNum = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrgsName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrgsAction = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrgsTime = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnSum = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnDuration = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnAddress = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnFactAdres = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.panel2 = new System.Windows.Forms.Panel();
         this.lbWorkEnd = new System.Windows.Forms.Label();
         this.label1 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.lbWorkBegin = new System.Windows.Forms.Label();
         this.tpLog = new System.Windows.Forms.TabPage();
         this.dgvLog = new System.Windows.Forms.DataGridView();
         this.dgvLogDate = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvLogAction = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.lbDistance = new System.Windows.Forms.ToolStripStatusLabel();
         this.cbBrigade = new System.Windows.Forms.ComboBox();
         this.cbFilter = new System.Windows.Forms.ComboBox();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn6 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn7 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn8 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn9 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn10 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn11 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.cbRoadPoints = new System.Windows.Forms.CheckBox();
         this.numInterval = new System.Windows.Forms.NumericUpDown();
         this.toolTip1 = new System.Windows.Forms.ToolTip(this.components);
         this.tsbMessage.SuspendLayout();
         this.panel1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.tabControl1.SuspendLayout();
         this.tpRoute.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).BeginInit();
         this.panel2.SuspendLayout();
         this.tpLog.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvLog)).BeginInit();
         this.statusStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.numInterval)).BeginInit();
         this.SuspendLayout();
         // 
         // tsbMessage
         // 
         this.tsbMessage.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel1,
            this.toolStripSeparator2,
            this.btnRefresh,
            this.toolStripSeparator3});
         this.tsbMessage.Location = new System.Drawing.Point(0, 0);
         this.tsbMessage.Name = "tsbMessage";
         this.tsbMessage.Size = new System.Drawing.Size(1022, 25);
         this.tsbMessage.TabIndex = 1;
         this.tsbMessage.Text = "toolStrip1";
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Margin = new System.Windows.Forms.Padding(274, 1, 0, 2);
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(45, 22);
         this.toolStripLabel1.Text = "Фильтр";
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Margin = new System.Windows.Forms.Padding(130, 0, 0, 0);
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // btnRefresh
         // 
         this.btnRefresh.Checked = true;
         this.btnRefresh.CheckState = System.Windows.Forms.CheckState.Checked;
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = ((System.Drawing.Image)(resources.GetObject("btnRefresh.Image")));
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Margin = new System.Windows.Forms.Padding(10, 1, 10, 2);
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // toolStripSeparator3
         // 
         this.toolStripSeparator3.Name = "toolStripSeparator3";
         this.toolStripSeparator3.Size = new System.Drawing.Size(6, 25);
         // 
         // dtpDate
         // 
         this.dtpDate.Location = new System.Drawing.Point(134, 2);
         this.dtpDate.Name = "dtpDate";
         this.dtpDate.Size = new System.Drawing.Size(142, 20);
         this.dtpDate.TabIndex = 2;
         // 
         // wb
         // 
         this.wb.AllowWebBrowserDrop = false;
         this.wb.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.wb.IsWebBrowserContextMenuEnabled = false;
         this.wb.Location = new System.Drawing.Point(0, 0);
         this.wb.MinimumSize = new System.Drawing.Size(20, 20);
         this.wb.Name = "wb";
         this.wb.ScriptErrorsSuppressed = true;
         this.wb.Size = new System.Drawing.Size(533, 445);
         this.wb.TabIndex = 4;
         this.wb.WebBrowserShortcutsEnabled = false;
         this.wb.DocumentCompleted += new System.Windows.Forms.WebBrowserDocumentCompletedEventHandler(this.wb_DocumentCompleted);
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.splitContainer1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7);
         this.panel1.Size = new System.Drawing.Size(1022, 459);
         this.panel1.TabIndex = 5;
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(7, 7);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.wb);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.tabControl1);
         this.splitContainer1.Size = new System.Drawing.Size(1008, 445);
         this.splitContainer1.SplitterDistance = 532;
         this.splitContainer1.SplitterWidth = 7;
         this.splitContainer1.TabIndex = 5;
         // 
         // tabControl1
         // 
         this.tabControl1.Controls.Add(this.tpRoute);
         this.tabControl1.Controls.Add(this.tpLog);
         this.tabControl1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tabControl1.Location = new System.Drawing.Point(0, 0);
         this.tabControl1.Name = "tabControl1";
         this.tabControl1.SelectedIndex = 0;
         this.tabControl1.Size = new System.Drawing.Size(469, 445);
         this.tabControl1.TabIndex = 1;
         // 
         // tpRoute
         // 
         this.tpRoute.Controls.Add(this.dgvOrgs);
         this.tpRoute.Controls.Add(this.panel2);
         this.tpRoute.Location = new System.Drawing.Point(4, 22);
         this.tpRoute.Name = "tpRoute";
         this.tpRoute.Padding = new System.Windows.Forms.Padding(3);
         this.tpRoute.Size = new System.Drawing.Size(461, 419);
         this.tpRoute.TabIndex = 0;
         this.tpRoute.Text = "Маршрут";
         this.tpRoute.UseVisualStyleBackColor = true;
         // 
         // dgvOrgs
         // 
         this.dgvOrgs.AllowUserToAddRows = false;
         this.dgvOrgs.AllowUserToDeleteRows = false;
         this.dgvOrgs.AllowUserToResizeRows = false;
         this.dgvOrgs.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrgs.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvOrgsNum,
            this.dgvOrgsName,
            this.dgvOrgsAction,
            this.dgvOrgsTime,
            this.clmnSum,
            this.clmnDuration,
            this.clmnAddress,
            this.clmnFactAdres});
         this.dgvOrgs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrgs.Location = new System.Drawing.Point(3, 3);
         this.dgvOrgs.MultiSelect = false;
         this.dgvOrgs.Name = "dgvOrgs";
         this.dgvOrgs.RowHeadersVisible = false;
         this.dgvOrgs.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvOrgs.Size = new System.Drawing.Size(455, 355);
         this.dgvOrgs.TabIndex = 0;
         this.dgvOrgs.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvOrgs_CellFormatting);
         // 
         // dgvOrgsNum
         // 
         this.dgvOrgsNum.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrgsNum.HeaderText = "№";
         this.dgvOrgsNum.Name = "dgvOrgsNum";
         // 
         // dgvOrgsName
         // 
         this.dgvOrgsName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrgsName.FillWeight = 200F;
         this.dgvOrgsName.HeaderText = "Организация";
         this.dgvOrgsName.Name = "dgvOrgsName";
         // 
         // dgvOrgsAction
         // 
         this.dgvOrgsAction.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrgsAction.HeaderText = "Действие";
         this.dgvOrgsAction.Name = "dgvOrgsAction";
         // 
         // dgvOrgsTime
         // 
         this.dgvOrgsTime.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrgsTime.HeaderText = "Время";
         this.dgvOrgsTime.Name = "dgvOrgsTime";
         // 
         // clmnSum
         // 
         this.clmnSum.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         dataGridViewCellStyle1.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleRight;
         this.clmnSum.DefaultCellStyle = dataGridViewCellStyle1;
         this.clmnSum.HeaderText = "Сумма";
         this.clmnSum.Name = "clmnSum";
         this.clmnSum.Visible = false;
         // 
         // clmnDuration
         // 
         this.clmnDuration.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnDuration.HeaderText = "Продолжительность";
         this.clmnDuration.Name = "clmnDuration";
         // 
         // clmnAddress
         // 
         this.clmnAddress.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnAddress.FillWeight = 200F;
         this.clmnAddress.HeaderText = "Адрес";
         this.clmnAddress.Name = "clmnAddress";
         // 
         // clmnFactAdres
         // 
         this.clmnFactAdres.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnFactAdres.HeaderText = "Факт.Адрес";
         this.clmnFactAdres.Name = "clmnFactAdres";
         this.clmnFactAdres.Visible = false;
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.lbWorkEnd);
         this.panel2.Controls.Add(this.label1);
         this.panel2.Controls.Add(this.label3);
         this.panel2.Controls.Add(this.lbWorkBegin);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel2.Location = new System.Drawing.Point(3, 358);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(455, 58);
         this.panel2.TabIndex = 1;
         // 
         // lbWorkEnd
         // 
         this.lbWorkEnd.AutoSize = true;
         this.lbWorkEnd.Location = new System.Drawing.Point(136, 33);
         this.lbWorkEnd.Name = "lbWorkEnd";
         this.lbWorkEnd.Size = new System.Drawing.Size(35, 13);
         this.lbWorkEnd.TabIndex = 5;
         this.lbWorkEnd.Text = "label4";
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(14, 8);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(114, 13);
         this.label1.TabIndex = 2;
         this.label1.Text = "Начало рабочего дня";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(14, 33);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(108, 13);
         this.label3.TabIndex = 4;
         this.label3.Text = "Конец рабочего дня";
         // 
         // lbWorkBegin
         // 
         this.lbWorkBegin.AutoSize = true;
         this.lbWorkBegin.Location = new System.Drawing.Point(136, 8);
         this.lbWorkBegin.Name = "lbWorkBegin";
         this.lbWorkBegin.Size = new System.Drawing.Size(35, 13);
         this.lbWorkBegin.TabIndex = 3;
         this.lbWorkBegin.Text = "label2";
         // 
         // tpLog
         // 
         this.tpLog.Controls.Add(this.dgvLog);
         this.tpLog.Location = new System.Drawing.Point(4, 22);
         this.tpLog.Name = "tpLog";
         this.tpLog.Padding = new System.Windows.Forms.Padding(3);
         this.tpLog.Size = new System.Drawing.Size(461, 419);
         this.tpLog.TabIndex = 1;
         this.tpLog.Text = "Лог";
         this.tpLog.UseVisualStyleBackColor = true;
         // 
         // dgvLog
         // 
         this.dgvLog.AllowUserToAddRows = false;
         this.dgvLog.AllowUserToDeleteRows = false;
         this.dgvLog.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvLog.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvLogDate,
            this.dgvLogAction});
         this.dgvLog.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvLog.Location = new System.Drawing.Point(3, 3);
         this.dgvLog.Name = "dgvLog";
         this.dgvLog.RowHeadersVisible = false;
         this.dgvLog.Size = new System.Drawing.Size(455, 413);
         this.dgvLog.TabIndex = 0;
         this.dgvLog.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvLog_CellFormatting);
         // 
         // dgvLogDate
         // 
         this.dgvLogDate.DataPropertyName = "TimeStr";
         this.dgvLogDate.HeaderText = "Время";
         this.dgvLogDate.Name = "dgvLogDate";
         this.dgvLogDate.Width = 70;
         // 
         // dgvLogAction
         // 
         this.dgvLogAction.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvLogAction.DataPropertyName = "userAction";
         this.dgvLogAction.HeaderText = "Событие";
         this.dgvLogAction.Name = "dgvLogAction";
         // 
         // statusStrip1
         // 
         this.statusStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.lbDistance});
         this.statusStrip1.Location = new System.Drawing.Point(0, 484);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(1022, 22);
         this.statusStrip1.TabIndex = 6;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // lbDistance
         // 
         this.lbDistance.Name = "lbDistance";
         this.lbDistance.Size = new System.Drawing.Size(109, 17);
         this.lbDistance.Text = "toolStripStatusLabel1";
         // 
         // cbBrigade
         // 
         this.cbBrigade.DrawMode = System.Windows.Forms.DrawMode.OwnerDrawVariable;
         this.cbBrigade.FormattingEnabled = true;
         this.cbBrigade.ItemHeight = 14;
         this.cbBrigade.Location = new System.Drawing.Point(8, 2);
         this.cbBrigade.Name = "cbBrigade";
         this.cbBrigade.Size = new System.Drawing.Size(121, 20);
         this.cbBrigade.TabIndex = 7;
         this.cbBrigade.DrawItem += new System.Windows.Forms.DrawItemEventHandler(this.cbAgents_DrawItem);
         this.cbBrigade.MeasureItem += new System.Windows.Forms.MeasureItemEventHandler(this.cbAgents_MeasureItem);
         // 
         // cbFilter
         // 
         this.cbFilter.FormattingEnabled = true;
         this.cbFilter.Location = new System.Drawing.Point(332, 2);
         this.cbFilter.Name = "cbFilter";
         this.cbFilter.Size = new System.Drawing.Size(121, 21);
         this.cbFilter.TabIndex = 8;
         this.cbFilter.SelectionChangeCommitted += new System.EventHandler(this.cbFilter_SelectionChangeCommitted);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.HeaderText = "№";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.FillWeight = 200F;
         this.dataGridViewTextBoxColumn2.HeaderText = "Оргинизация";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.FillWeight = 200F;
         this.dataGridViewTextBoxColumn3.HeaderText = "Действие";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn4.FillWeight = 200F;
         this.dataGridViewTextBoxColumn4.HeaderText = "Время";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         // 
         // dataGridViewTextBoxColumn5
         // 
         dataGridViewCellStyle2.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleRight;
         this.dataGridViewTextBoxColumn5.DefaultCellStyle = dataGridViewCellStyle2;
         this.dataGridViewTextBoxColumn5.HeaderText = "Продолжительность";
         this.dataGridViewTextBoxColumn5.Name = "dataGridViewTextBoxColumn5";
         this.dataGridViewTextBoxColumn5.Width = 70;
         // 
         // dataGridViewTextBoxColumn6
         // 
         this.dataGridViewTextBoxColumn6.HeaderText = "Продолжительность";
         this.dataGridViewTextBoxColumn6.Name = "dataGridViewTextBoxColumn6";
         this.dataGridViewTextBoxColumn6.Width = 80;
         // 
         // dataGridViewTextBoxColumn7
         // 
         this.dataGridViewTextBoxColumn7.HeaderText = "Адрес";
         this.dataGridViewTextBoxColumn7.Name = "dataGridViewTextBoxColumn7";
         // 
         // dataGridViewTextBoxColumn8
         // 
         this.dataGridViewTextBoxColumn8.HeaderText = "Факт.Адрес";
         this.dataGridViewTextBoxColumn8.Name = "dataGridViewTextBoxColumn8";
         // 
         // dataGridViewTextBoxColumn9
         // 
         this.dataGridViewTextBoxColumn9.FillWeight = 80F;
         this.dataGridViewTextBoxColumn9.HeaderText = "Дата";
         this.dataGridViewTextBoxColumn9.Name = "dataGridViewTextBoxColumn9";
         // 
         // dataGridViewTextBoxColumn10
         // 
         this.dataGridViewTextBoxColumn10.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn10.HeaderText = "Событие";
         this.dataGridViewTextBoxColumn10.Name = "dataGridViewTextBoxColumn10";
         // 
         // dataGridViewTextBoxColumn11
         // 
         this.dataGridViewTextBoxColumn11.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn11.HeaderText = "Комментарий";
         this.dataGridViewTextBoxColumn11.Name = "dataGridViewTextBoxColumn11";
         // 
         // cbRoadPoints
         // 
         this.cbRoadPoints.AutoSize = true;
         this.cbRoadPoints.Location = new System.Drawing.Point(552, 4);
         this.cbRoadPoints.Name = "cbRoadPoints";
         this.cbRoadPoints.Size = new System.Drawing.Size(109, 17);
         this.cbRoadPoints.TabIndex = 10;
         this.cbRoadPoints.Text = "Точки маршрута";
         this.cbRoadPoints.UseVisualStyleBackColor = true;
         this.cbRoadPoints.CheckedChanged += new System.EventHandler(this.cbRoadPoints_CheckedChanged);
         // 
         // numInterval
         // 
         this.numInterval.Enabled = false;
         this.numInterval.Increment = new decimal(new int[] {
            15,
            0,
            0,
            0});
         this.numInterval.Location = new System.Drawing.Point(668, 2);
         this.numInterval.Maximum = new decimal(new int[] {
            105,
            0,
            0,
            0});
         this.numInterval.Minimum = new decimal(new int[] {
            15,
            0,
            0,
            0});
         this.numInterval.Name = "numInterval";
         this.numInterval.Size = new System.Drawing.Size(38, 20);
         this.numInterval.TabIndex = 11;
         this.numInterval.Value = new decimal(new int[] {
            15,
            0,
            0,
            0});
         // 
         // FmRoute
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(1022, 506);
         this.Controls.Add(this.numInterval);
         this.Controls.Add(this.cbRoadPoints);
         this.Controls.Add(this.cbFilter);
         this.Controls.Add(this.cbBrigade);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.dtpDate);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.tsbMessage);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmRoute";
         this.Text = "Маршрут";
         this.Load += new System.EventHandler(this.FmRoute_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmRoute_FormClosed);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmRoute_FormClosing);
         this.tsbMessage.ResumeLayout(false);
         this.tsbMessage.PerformLayout();
         this.panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         this.tabControl1.ResumeLayout(false);
         this.tpRoute.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).EndInit();
         this.panel2.ResumeLayout(false);
         this.panel2.PerformLayout();
         this.tpLog.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvLog)).EndInit();
         this.statusStrip1.ResumeLayout(false);
         this.statusStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.numInterval)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip tsbMessage;
      private System.Windows.Forms.DateTimePicker dtpDate;
      private System.Windows.Forms.WebBrowser wb;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.DataGridView dgvOrgs;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStripStatusLabel lbDistance;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      private System.Windows.Forms.ComboBox cbBrigade;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ComboBox cbFilter;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn5;
      private System.Windows.Forms.TabControl tabControl1;
      private System.Windows.Forms.TabPage tpRoute;
      private System.Windows.Forms.TabPage tpLog;
      private System.Windows.Forms.DataGridView dgvLog;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn6;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn7;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn8;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn9;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn10;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn11;
      private System.Windows.Forms.Label lbWorkEnd;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label lbWorkBegin;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrgsNum;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrgsName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrgsAction;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrgsTime;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnSum;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnDuration;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAddress;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnFactAdres;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
      private System.Windows.Forms.CheckBox cbRoadPoints;
      private System.Windows.Forms.NumericUpDown numInterval;
      private System.Windows.Forms.ToolTip toolTip1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvLogDate;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvLogAction;
      protected System.Windows.Forms.ToolStripButton btnRefresh;


   }
}