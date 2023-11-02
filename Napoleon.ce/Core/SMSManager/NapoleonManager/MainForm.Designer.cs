namespace GRSoft.NapoleonManager
{
   partial class MainForm
   {
      /// <summary>
      /// Требуется переменная конструктора.
      /// </summary>
      private System.ComponentModel.IContainer components = null;

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
      private void InitializeComponent()
      {
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MainForm));
         this.tgvAgentsSummaryProgres = new System.Windows.Forms.DataGridViewImageColumn();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbSelectRange = new System.Windows.Forms.ToolStripSplitButton();
         this.miToday = new System.Windows.Forms.ToolStripMenuItem();
         this.miRange = new System.Windows.Forms.ToolStripMenuItem();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnReport = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.tsbFile = new System.Windows.Forms.ToolStripDropDownButton();
         this.miConfig = new System.Windows.Forms.ToolStripMenuItem();
         this.tsbDatas = new System.Windows.Forms.ToolStripDropDownButton();
         this.miLocality = new System.Windows.Forms.ToolStripMenuItem();
         this.miSchool = new System.Windows.Forms.ToolStripMenuItem();
         this.miClass = new System.Windows.Forms.ToolStripMenuItem();
         this.miStudent = new System.Windows.Forms.ToolStripMenuItem();
         this.miParents = new System.Windows.Forms.ToolStripMenuItem();
         this.miDogovors = new System.Windows.Forms.ToolStripMenuItem();
         this.miSchoolSubject = new System.Windows.Forms.ToolStripMenuItem();
         this.miSchedule = new System.Windows.Forms.ToolStripMenuItem();
         this.miAgents = new System.Windows.Forms.ToolStripMenuItem();
         this.miRoute = new System.Windows.Forms.ToolStripMenuItem();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.dgvSummary = new System.Windows.Forms.DataGridView();
         this.dgvSummaryAgent = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvSummarySchools = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvSummarySubjects = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.panel1 = new System.Windows.Forms.Panel();
         this.lbVersion = new System.Windows.Forms.Label();
         this.linkLabel1 = new System.Windows.Forms.LinkLabel();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tgvAgentsSummaryAgent = new GRSoft.UILib.TreeGridColumn();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tgvAgentsSummaryCount = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tgvAgentsSummarySum = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tgvAgentsSummaryAgentID = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tgvAgentsSummaryDivision = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tgvAgentsSummaryProgressValue = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvSummary)).BeginInit();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // tgvAgentsSummaryProgres
         // 
         this.tgvAgentsSummaryProgres.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         dataGridViewCellStyle1.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleCenter;
         dataGridViewCellStyle1.NullValue = ((object)(resources.GetObject("dataGridViewCellStyle1.NullValue")));
         dataGridViewCellStyle1.Padding = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.tgvAgentsSummaryProgres.DefaultCellStyle = dataGridViewCellStyle1;
         this.tgvAgentsSummaryProgres.HeaderText = "Прогресс";
         this.tgvAgentsSummaryProgres.Name = "tgvAgentsSummaryProgres";
         this.tgvAgentsSummaryProgres.Resizable = System.Windows.Forms.DataGridViewTriState.False;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbSelectRange,
            this.toolStripLabel1,
            this.toolStripLabel2,
            this.btnRefresh,
            this.toolStripSeparator1,
            this.btnReport,
            this.toolStripSeparator2,
            this.tsbFile,
            this.tsbDatas});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(754, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsbSelectRange
         // 
         this.tsbSelectRange.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSelectRange.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miToday,
            this.miRange});
         this.tsbSelectRange.Image = ((System.Drawing.Image)(resources.GetObject("tsbSelectRange.Image")));
         this.tsbSelectRange.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSelectRange.Name = "tsbSelectRange";
         this.tsbSelectRange.Size = new System.Drawing.Size(32, 22);
         this.tsbSelectRange.Text = "toolStripSplitButton1";
         this.tsbSelectRange.ToolTipText = "За сегодня";
         this.tsbSelectRange.Click += new System.EventHandler(this.tsbSelectRange_Click);
         // 
         // miToday
         // 
         this.miToday.Checked = true;
         this.miToday.CheckState = System.Windows.Forms.CheckState.Checked;
         this.miToday.Image = ((System.Drawing.Image)(resources.GetObject("miToday.Image")));
         this.miToday.Name = "miToday";
         this.miToday.Size = new System.Drawing.Size(141, 22);
         this.miToday.Text = "За сегодня";
         this.miToday.Click += new System.EventHandler(this.miToday_Click);
         // 
         // miRange
         // 
         this.miRange.Image = ((System.Drawing.Image)(resources.GetObject("miRange.Image")));
         this.miRange.Name = "miRange";
         this.miRange.Size = new System.Drawing.Size(141, 22);
         this.miRange.Text = "За период";
         this.miRange.Click += new System.EventHandler(this.miRange_Click);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(12, 22);
         this.toolStripLabel1.Text = "с";
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Margin = new System.Windows.Forms.Padding(150, 1, 0, 2);
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(19, 22);
         this.toolStripLabel2.Text = "по";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = ((System.Drawing.Image)(resources.GetObject("btnRefresh.Image")));
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Margin = new System.Windows.Forms.Padding(155, 1, 0, 2);
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // btnReport
         // 
         this.btnReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnReport.Image = ((System.Drawing.Image)(resources.GetObject("btnReport.Image")));
         this.btnReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnReport.Name = "btnReport";
         this.btnReport.Size = new System.Drawing.Size(23, 22);
         this.btnReport.Text = "Отчет";
         this.btnReport.Click += new System.EventHandler(this.btnReport_Click);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // tsbFile
         // 
         this.tsbFile.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         this.tsbFile.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miConfig});
         this.tsbFile.Image = ((System.Drawing.Image)(resources.GetObject("tsbFile.Image")));
         this.tsbFile.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbFile.Name = "tsbFile";
         this.tsbFile.Size = new System.Drawing.Size(46, 22);
         this.tsbFile.Text = "Файл";
         // 
         // miConfig
         // 
         this.miConfig.Name = "miConfig";
         this.miConfig.Size = new System.Drawing.Size(139, 22);
         this.miConfig.Text = "Настройки";
         this.miConfig.Click += new System.EventHandler(this.miConfig_Click);
         // 
         // tsbDatas
         // 
         this.tsbDatas.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         this.tsbDatas.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miLocality,
            this.miSchool,
            this.miClass,
            this.miStudent,
            this.miParents,
            this.miDogovors,
            this.miSchoolSubject,
            this.miSchedule,
            this.miAgents,
            this.miRoute});
         this.tsbDatas.Image = ((System.Drawing.Image)(resources.GetObject("tsbDatas.Image")));
         this.tsbDatas.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbDatas.Name = "tsbDatas";
         this.tsbDatas.Size = new System.Drawing.Size(87, 22);
         this.tsbDatas.Text = "Справочники";
         // 
         // miLocality
         // 
         this.miLocality.Name = "miLocality";
         this.miLocality.Size = new System.Drawing.Size(143, 22);
         this.miLocality.Text = "Город";
         this.miLocality.Click += new System.EventHandler(this.miLocality_Click);
         // 
         // miSchool
         // 
         this.miSchool.Name = "miSchool";
         this.miSchool.Size = new System.Drawing.Size(143, 22);
         this.miSchool.Text = "Школа";
         this.miSchool.Click += new System.EventHandler(this.miSchool_Click);
         // 
         // miClass
         // 
         this.miClass.Name = "miClass";
         this.miClass.Size = new System.Drawing.Size(143, 22);
         this.miClass.Text = "Классы";
         this.miClass.Click += new System.EventHandler(this.miClass_Click);
         // 
         // miStudent
         // 
         this.miStudent.Name = "miStudent";
         this.miStudent.Size = new System.Drawing.Size(143, 22);
         this.miStudent.Text = "Ученики";
         this.miStudent.Click += new System.EventHandler(this.miStudent_Click);
         // 
         // miParents
         // 
         this.miParents.Name = "miParents";
         this.miParents.Size = new System.Drawing.Size(143, 22);
         this.miParents.Text = "Родители";
         this.miParents.Click += new System.EventHandler(this.miParents_Click);
         // 
         // miDogovors
         // 
         this.miDogovors.Name = "miDogovors";
         this.miDogovors.Size = new System.Drawing.Size(143, 22);
         this.miDogovors.Text = "Договоры";
         this.miDogovors.Click += new System.EventHandler(this.miDogovors_Click);
         // 
         // miSchoolSubject
         // 
         this.miSchoolSubject.Name = "miSchoolSubject";
         this.miSchoolSubject.Size = new System.Drawing.Size(143, 22);
         this.miSchoolSubject.Text = "Предметы";
         this.miSchoolSubject.Click += new System.EventHandler(this.miSchoolSubject_Click);
         // 
         // miSchedule
         // 
         this.miSchedule.Name = "miSchedule";
         this.miSchedule.Size = new System.Drawing.Size(143, 22);
         this.miSchedule.Text = "Расписание";
         this.miSchedule.Click += new System.EventHandler(this.miSchedule_Click);
         // 
         // miAgents
         // 
         this.miAgents.Name = "miAgents";
         this.miAgents.Size = new System.Drawing.Size(143, 22);
         this.miAgents.Text = "Агенты";
         this.miAgents.Click += new System.EventHandler(this.miAgents_Click);
         // 
         // miRoute
         // 
         this.miRoute.Name = "miRoute";
         this.miRoute.Size = new System.Drawing.Size(143, 22);
         this.miRoute.Text = "Маршрут";
         this.miRoute.Click += new System.EventHandler(this.miRoute_Click);
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(60, 2);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(134, 20);
         this.dtpBegin.TabIndex = 2;
         this.dtpBegin.ValueChanged += new System.EventHandler(this.dtpBeginDate_ValueChanged);
         // 
         // dtpEnd
         // 
         this.dtpEnd.Enabled = false;
         this.dtpEnd.Location = new System.Drawing.Point(232, 2);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(134, 20);
         this.dtpEnd.TabIndex = 3;
         this.dtpEnd.ValueChanged += new System.EventHandler(this.dtpEndDate_ValueChanged);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 416);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(754, 22);
         this.statusStrip1.TabIndex = 4;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // dgvSummary
         // 
         this.dgvSummary.AllowUserToAddRows = false;
         this.dgvSummary.AllowUserToDeleteRows = false;
         this.dgvSummary.AllowUserToResizeRows = false;
         this.dgvSummary.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvSummary.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvSummaryAgent,
            this.dgvSummarySchools,
            this.dgvSummarySubjects});
         this.dgvSummary.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvSummary.Location = new System.Drawing.Point(7, 7);
         this.dgvSummary.Name = "dgvSummary";
         this.dgvSummary.ReadOnly = true;
         this.dgvSummary.RowHeadersVisible = false;
         this.dgvSummary.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvSummary.Size = new System.Drawing.Size(740, 377);
         this.dgvSummary.TabIndex = 5;
         this.dgvSummary.DoubleClick += new System.EventHandler(this.dgvSummary_DoubleClick);
         // 
         // dgvSummaryAgent
         // 
         this.dgvSummaryAgent.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvSummaryAgent.HeaderText = "Агент";
         this.dgvSummaryAgent.Name = "dgvSummaryAgent";
         this.dgvSummaryAgent.ReadOnly = true;
         // 
         // dgvSummarySchools
         // 
         this.dgvSummarySchools.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvSummarySchools.HeaderText = "Школы";
         this.dgvSummarySchools.Name = "dgvSummarySchools";
         this.dgvSummarySchools.ReadOnly = true;
         // 
         // dgvSummarySubjects
         // 
         this.dgvSummarySubjects.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvSummarySubjects.HeaderText = "Предметы";
         this.dgvSummarySubjects.Name = "dgvSummarySubjects";
         this.dgvSummarySubjects.ReadOnly = true;
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.dgvSummary);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7);
         this.panel1.Size = new System.Drawing.Size(754, 391);
         this.panel1.TabIndex = 6;
         // 
         // lbVersion
         // 
         this.lbVersion.AutoSize = true;
         this.lbVersion.Location = new System.Drawing.Point(344, 420);
         this.lbVersion.Name = "lbVersion";
         this.lbVersion.Size = new System.Drawing.Size(35, 14);
         this.lbVersion.TabIndex = 7;
         this.lbVersion.Text = "label1";
         // 
         // linkLabel1
         // 
         this.linkLabel1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.linkLabel1.AutoSize = true;
         this.linkLabel1.Location = new System.Drawing.Point(524, 420);
         this.linkLabel1.Name = "linkLabel1";
         this.linkLabel1.Size = new System.Drawing.Size(207, 14);
         this.linkLabel1.TabIndex = 8;
         this.linkLabel1.TabStop = true;
         this.linkLabel1.Text = "Гильдия разработчиков www.grsoft.ru";
         this.linkLabel1.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.linkLabel1_LinkClicked);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Agent";
         this.dataGridViewTextBoxColumn1.HeaderText = "Агент";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "OrderCount";
         this.dataGridViewTextBoxColumn2.HeaderText = "Количество заявок";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.DataPropertyName = "Sum";
         this.dataGridViewTextBoxColumn3.HeaderText = "Сумма";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         // 
         // tgvAgentsSummaryAgent
         // 
         this.tgvAgentsSummaryAgent.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.tgvAgentsSummaryAgent.DefaultNodeImage = null;
         this.tgvAgentsSummaryAgent.FillWeight = 400F;
         this.tgvAgentsSummaryAgent.HeaderText = "Подразделение/Агент";
         this.tgvAgentsSummaryAgent.Name = "tgvAgentsSummaryAgent";
         this.tgvAgentsSummaryAgent.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.HeaderText = "Визиты";
         this.Column1.Name = "Column1";
         this.Column1.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // tgvAgentsSummaryCount
         // 
         this.tgvAgentsSummaryCount.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.tgvAgentsSummaryCount.HeaderText = "Заказы";
         this.tgvAgentsSummaryCount.Name = "tgvAgentsSummaryCount";
         this.tgvAgentsSummaryCount.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // tgvAgentsSummarySum
         // 
         this.tgvAgentsSummarySum.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.tgvAgentsSummarySum.HeaderText = "Сумма";
         this.tgvAgentsSummarySum.Name = "tgvAgentsSummarySum";
         this.tgvAgentsSummarySum.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // tgvAgentsSummaryAgentID
         // 
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
         this.tgvAgentsSummaryProgressValue.HeaderText = "ProgressValue";
         this.tgvAgentsSummaryProgressValue.Name = "tgvAgentsSummaryProgressValue";
         this.tgvAgentsSummaryProgressValue.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         this.tgvAgentsSummaryProgressValue.Visible = false;
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
         this.ClientSize = new System.Drawing.Size(754, 438);
         this.Controls.Add(this.linkLabel1);
         this.Controls.Add(this.lbVersion);
         this.Controls.Add(this.dtpEnd);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.dtpBegin);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "MainForm";
         this.Text = "АРМ СМС-дневник";
         this.Load += new System.EventHandler(this.MainForm_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvSummary)).EndInit();
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      private GRSoft.UILib.TreeGridColumn tgvAgentsSummaryAgent;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn tgvAgentsSummaryCount;
      private System.Windows.Forms.DataGridViewTextBoxColumn tgvAgentsSummarySum;
      private System.Windows.Forms.DataGridViewImageColumn tgvAgentsSummaryProgres;
      private System.Windows.Forms.DataGridViewTextBoxColumn tgvAgentsSummaryAgentID;
      private System.Windows.Forms.DataGridViewTextBoxColumn tgvAgentsSummaryDivision;
      private System.Windows.Forms.DataGridViewTextBoxColumn tgvAgentsSummaryProgressValue;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripSplitButton tsbSelectRange;
      private System.Windows.Forms.ToolStripMenuItem miToday;
      private System.Windows.Forms.ToolStripMenuItem miRange;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.DateTimePicker dtpBegin;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripButton btnReport;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.DataGridView dgvSummary;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.ToolStripDropDownButton tsbFile;
      private System.Windows.Forms.ToolStripMenuItem miConfig;
      private System.Windows.Forms.ToolStripDropDownButton tsbDatas;
      private System.Windows.Forms.ToolStripMenuItem miLocality;
      private System.Windows.Forms.ToolStripMenuItem miSchool;
      private System.Windows.Forms.ToolStripMenuItem miClass;
      private System.Windows.Forms.ToolStripMenuItem miStudent;
      private System.Windows.Forms.ToolStripMenuItem miParents;
      private System.Windows.Forms.ToolStripMenuItem miDogovors;
      private System.Windows.Forms.ToolStripMenuItem miSchoolSubject;
      private System.Windows.Forms.ToolStripMenuItem miSchedule;
      private System.Windows.Forms.ToolStripMenuItem miAgents;
      private System.Windows.Forms.ToolStripMenuItem miRoute;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvSummaryAgent;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvSummarySchools;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvSummarySubjects;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.Label lbVersion;
      private System.Windows.Forms.LinkLabel linkLabel1;
   }
}

