namespace GRSoft.NapoleonManager
{
   partial class FmDetail
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmDetail));
         this.dgvDetail = new System.Windows.Forms.DataGridView();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.scCenter = new System.Windows.Forms.SplitContainer();
         this.panel2 = new System.Windows.Forms.Panel();
         this.lblAdress = new System.Windows.Forms.LinkLabel();
         this.label1 = new System.Windows.Forms.Label();
         this.label5 = new System.Windows.Forms.Label();
         this.scBottom = new System.Windows.Forms.SplitContainer();
         this.dgvSchedule = new System.Windows.Forms.DataGridView();
         this.label6 = new System.Windows.Forms.Label();
         this.Annonce = new System.Windows.Forms.TextBox();
         this.label7 = new System.Windows.Forms.Label();
         this.imPhoto = new System.Windows.Forms.ImageList(this.components);
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.cbAgents = new System.Windows.Forms.ToolStripComboBox();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripLabel3 = new System.Windows.Forms.ToolStripLabel();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.btnRoute = new System.Windows.Forms.ToolStripButton();
         this.toolStripLabel4 = new System.Windows.Forms.ToolStripLabel();
         this.tbnMessage = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator3 = new System.Windows.Forms.ToolStripSeparator();
         this.tsbMakeHtml = new System.Windows.Forms.ToolStripButton();
         this.panel1 = new System.Windows.Forms.Panel();
         this.cbFilter = new System.Windows.Forms.ComboBox();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn6 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn7 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn8 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvDetailClass = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvDetailSchoolSubject = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvDetailData = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvDetailHomework = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvScheduleStudent = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvSchedulePoint = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvScheduleBehavior = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvScheduleNotes = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn9 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn10 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn11 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn12 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn13 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn14 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         ((System.ComponentModel.ISupportInitialize)(this.dgvDetail)).BeginInit();
         this.scCenter.Panel1.SuspendLayout();
         this.scCenter.Panel2.SuspendLayout();
         this.scCenter.SuspendLayout();
         this.panel2.SuspendLayout();
         this.scBottom.Panel1.SuspendLayout();
         this.scBottom.Panel2.SuspendLayout();
         this.scBottom.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvSchedule)).BeginInit();
         this.toolStrip1.SuspendLayout();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // dgvDetail
         // 
         this.dgvDetail.AllowUserToAddRows = false;
         this.dgvDetail.AllowUserToDeleteRows = false;
         this.dgvDetail.AllowUserToResizeRows = false;
         this.dgvDetail.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.dgvDetail.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvDetail.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvDetailClass,
            this.dgvDetailSchoolSubject,
            this.dgvDetailData,
            this.dgvDetailHomework});
         this.dgvDetail.Location = new System.Drawing.Point(0, 23);
         this.dgvDetail.Margin = new System.Windows.Forms.Padding(3, 4, 3, 4);
         this.dgvDetail.MultiSelect = false;
         this.dgvDetail.Name = "dgvDetail";
         this.dgvDetail.ReadOnly = true;
         this.dgvDetail.RowHeadersVisible = false;
         this.dgvDetail.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvDetail.Size = new System.Drawing.Size(878, 290);
         this.dgvDetail.TabIndex = 2;
         this.dgvDetail.SelectionChanged += new System.EventHandler(this.dgvDetail_SelectionChanged);
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(254, 2);
         this.dtpBegin.Margin = new System.Windows.Forms.Padding(3, 4, 3, 4);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(130, 20);
         this.dtpBegin.TabIndex = 5;
         this.dtpBegin.Value = new System.DateTime(2010, 5, 11, 0, 0, 0, 0);
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(406, 2);
         this.dtpEnd.Margin = new System.Windows.Forms.Padding(3, 4, 3, 4);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(130, 20);
         this.dtpEnd.TabIndex = 7;
         this.dtpEnd.Value = new System.DateTime(2010, 5, 11, 0, 0, 0, 0);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.statusStrip1.Location = new System.Drawing.Point(0, 590);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Padding = new System.Windows.Forms.Padding(1, 0, 15, 0);
         this.statusStrip1.Size = new System.Drawing.Size(892, 22);
         this.statusStrip1.TabIndex = 9;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // scCenter
         // 
         this.scCenter.Dock = System.Windows.Forms.DockStyle.Fill;
         this.scCenter.Location = new System.Drawing.Point(7, 7);
         this.scCenter.Margin = new System.Windows.Forms.Padding(30);
         this.scCenter.Name = "scCenter";
         this.scCenter.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // scCenter.Panel1
         // 
         this.scCenter.Panel1.BackColor = System.Drawing.Color.Blue;
         this.scCenter.Panel1.Controls.Add(this.panel2);
         this.scCenter.Panel1.Controls.Add(this.label5);
         this.scCenter.Panel1.Controls.Add(this.dgvDetail);
         // 
         // scCenter.Panel2
         // 
         this.scCenter.Panel2.BackColor = System.Drawing.Color.Blue;
         this.scCenter.Panel2.Controls.Add(this.scBottom);
         this.scCenter.Size = new System.Drawing.Size(878, 551);
         this.scCenter.SplitterDistance = 335;
         this.scCenter.SplitterWidth = 7;
         this.scCenter.TabIndex = 11;
         // 
         // panel2
         // 
         this.panel2.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.panel2.BackColor = System.Drawing.Color.PaleGreen;
         this.panel2.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
         this.panel2.Controls.Add(this.lblAdress);
         this.panel2.Controls.Add(this.label1);
         this.panel2.Location = new System.Drawing.Point(0, 313);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(878, 22);
         this.panel2.TabIndex = 13;
         // 
         // lblAdress
         // 
         this.lblAdress.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
         this.lblAdress.AutoSize = true;
         this.lblAdress.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(0)))), ((int)(((byte)(0)))), ((int)(((byte)(192)))));
         this.lblAdress.LinkColor = System.Drawing.Color.Blue;
         this.lblAdress.Location = new System.Drawing.Point(123, 2);
         this.lblAdress.Name = "lblAdress";
         this.lblAdress.Size = new System.Drawing.Size(54, 14);
         this.lblAdress.TabIndex = 12;
         this.lblAdress.TabStop = true;
         this.lblAdress.Text = "linkLabel1";
         // 
         // label1
         // 
         this.label1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
         this.label1.AutoSize = true;
         this.label1.Font = new System.Drawing.Font("Arial", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.label1.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(0)))), ((int)(((byte)(0)))), ((int)(((byte)(192)))));
         this.label1.Location = new System.Drawing.Point(3, 2);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(120, 15);
         this.label1.TabIndex = 4;
         this.label1.Text = "Адрес организации:";
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Font = new System.Drawing.Font("Arial", 9.75F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.label5.ForeColor = System.Drawing.Color.White;
         this.label5.Location = new System.Drawing.Point(3, 3);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(81, 16);
         this.label5.TabIndex = 3;
         this.label5.Text = "Документы";
         // 
         // scBottom
         // 
         this.scBottom.BackColor = System.Drawing.SystemColors.Control;
         this.scBottom.Dock = System.Windows.Forms.DockStyle.Fill;
         this.scBottom.Location = new System.Drawing.Point(0, 0);
         this.scBottom.Name = "scBottom";
         // 
         // scBottom.Panel1
         // 
         this.scBottom.Panel1.BackColor = System.Drawing.Color.Blue;
         this.scBottom.Panel1.Controls.Add(this.dgvSchedule);
         this.scBottom.Panel1.Controls.Add(this.label6);
         // 
         // scBottom.Panel2
         // 
         this.scBottom.Panel2.BackColor = System.Drawing.Color.Blue;
         this.scBottom.Panel2.Controls.Add(this.Annonce);
         this.scBottom.Panel2.Controls.Add(this.label7);
         this.scBottom.Size = new System.Drawing.Size(878, 209);
         this.scBottom.SplitterDistance = 565;
         this.scBottom.SplitterWidth = 6;
         this.scBottom.TabIndex = 0;
         // 
         // dgvSchedule
         // 
         this.dgvSchedule.AllowUserToAddRows = false;
         this.dgvSchedule.AllowUserToDeleteRows = false;
         this.dgvSchedule.AllowUserToResizeRows = false;
         this.dgvSchedule.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.dgvSchedule.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvSchedule.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvScheduleStudent,
            this.dgvSchedulePoint,
            this.dgvScheduleBehavior,
            this.dgvScheduleNotes});
         this.dgvSchedule.Location = new System.Drawing.Point(0, 25);
         this.dgvSchedule.Name = "dgvSchedule";
         this.dgvSchedule.ReadOnly = true;
         this.dgvSchedule.RowHeadersVisible = false;
         this.dgvSchedule.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvSchedule.Size = new System.Drawing.Size(565, 184);
         this.dgvSchedule.TabIndex = 12;
         // 
         // label6
         // 
         this.label6.AutoSize = true;
         this.label6.Font = new System.Drawing.Font("Arial", 9.75F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.label6.ForeColor = System.Drawing.Color.White;
         this.label6.Location = new System.Drawing.Point(3, 3);
         this.label6.Name = "label6";
         this.label6.Size = new System.Drawing.Size(91, 16);
         this.label6.TabIndex = 11;
         this.label6.Text = "Содержание";
         // 
         // Annonce
         // 
         this.Annonce.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.Annonce.Location = new System.Drawing.Point(0, 25);
         this.Annonce.Multiline = true;
         this.Annonce.Name = "Annonce";
         this.Annonce.Size = new System.Drawing.Size(307, 184);
         this.Annonce.TabIndex = 2;
         // 
         // label7
         // 
         this.label7.AutoSize = true;
         this.label7.Font = new System.Drawing.Font("Arial", 9F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.label7.ForeColor = System.Drawing.Color.White;
         this.label7.Location = new System.Drawing.Point(3, 3);
         this.label7.Name = "label7";
         this.label7.Size = new System.Drawing.Size(82, 15);
         this.label7.TabIndex = 1;
         this.label7.Text = "Объявления";
         // 
         // imPhoto
         // 
         this.imPhoto.ColorDepth = System.Windows.Forms.ColorDepth.Depth32Bit;
         this.imPhoto.ImageSize = new System.Drawing.Size(115, 115);
         this.imPhoto.TransparentColor = System.Drawing.Color.Transparent;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel1,
            this.cbAgents,
            this.toolStripSeparator1,
            this.toolStripLabel2,
            this.toolStripLabel3,
            this.btnRefresh,
            this.toolStripSeparator2,
            this.btnRoute,
            this.toolStripLabel4,
            this.tbnMessage,
            this.toolStripSeparator3,
            this.tsbMakeHtml});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(892, 25);
         this.toolStrip1.TabIndex = 14;
         this.toolStrip1.Text = "Составить отчёт";
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(37, 22);
         this.toolStripLabel1.Text = "Агент";
         // 
         // cbAgents
         // 
         this.cbAgents.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(140, 25);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Margin = new System.Windows.Forms.Padding(10, 0, 0, 0);
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Margin = new System.Windows.Forms.Padding(7, 1, 0, 2);
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(41, 22);
         this.toolStripLabel2.Text = "Дата с";
         // 
         // toolStripLabel3
         // 
         this.toolStripLabel3.Margin = new System.Windows.Forms.Padding(135, 1, 0, 2);
         this.toolStripLabel3.Name = "toolStripLabel3";
         this.toolStripLabel3.Size = new System.Drawing.Size(19, 22);
         this.toolStripLabel3.Text = "по";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = ((System.Drawing.Image)(resources.GetObject("btnRefresh.Image")));
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Margin = new System.Windows.Forms.Padding(134, 1, 0, 2);
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "toolStripButton1";
         this.btnRefresh.ToolTipText = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Margin = new System.Windows.Forms.Padding(5, 0, 0, 0);
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // btnRoute
         // 
         this.btnRoute.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.btnRoute.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRoute.Image = ((System.Drawing.Image)(resources.GetObject("btnRoute.Image")));
         this.btnRoute.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRoute.Margin = new System.Windows.Forms.Padding(0, 1, 7, 2);
         this.btnRoute.MergeIndex = 2;
         this.btnRoute.Name = "btnRoute";
         this.btnRoute.Size = new System.Drawing.Size(23, 22);
         this.btnRoute.Text = "toolStripButton1";
         this.btnRoute.ToolTipText = "Показать маршрут";
         this.btnRoute.Visible = false;
         // 
         // toolStripLabel4
         // 
         this.toolStripLabel4.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.toolStripLabel4.Margin = new System.Windows.Forms.Padding(0, 1, 132, 2);
         this.toolStripLabel4.MergeIndex = 2;
         this.toolStripLabel4.Name = "toolStripLabel4";
         this.toolStripLabel4.Size = new System.Drawing.Size(45, 22);
         this.toolStripLabel4.Text = "Фильтр";
         this.toolStripLabel4.Visible = false;
         // 
         // tbnMessage
         // 
         this.tbnMessage.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbnMessage.Image = ((System.Drawing.Image)(resources.GetObject("tbnMessage.Image")));
         this.tbnMessage.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbnMessage.Margin = new System.Windows.Forms.Padding(4, 1, 0, 2);
         this.tbnMessage.Name = "tbnMessage";
         this.tbnMessage.Size = new System.Drawing.Size(23, 22);
         this.tbnMessage.Text = "Сообщение";
         this.tbnMessage.Click += new System.EventHandler(this.tbnMessage_Click);
         // 
         // toolStripSeparator3
         // 
         this.toolStripSeparator3.Name = "toolStripSeparator3";
         this.toolStripSeparator3.Size = new System.Drawing.Size(6, 25);
         // 
         // tsbMakeHtml
         // 
         this.tsbMakeHtml.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbMakeHtml.Image = ((System.Drawing.Image)(resources.GetObject("tsbMakeHtml.Image")));
         this.tsbMakeHtml.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbMakeHtml.Margin = new System.Windows.Forms.Padding(4, 1, 0, 2);
         this.tsbMakeHtml.Name = "tsbMakeHtml";
         this.tsbMakeHtml.Size = new System.Drawing.Size(23, 22);
         this.tsbMakeHtml.Text = "Составить отчет";
         this.tsbMakeHtml.Click += new System.EventHandler(this.tsbMakeHtml_Click);
         // 
         // panel1
         // 
         this.panel1.BackColor = System.Drawing.SystemColors.Control;
         this.panel1.Controls.Add(this.scCenter);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7);
         this.panel1.Size = new System.Drawing.Size(892, 565);
         this.panel1.TabIndex = 15;
         // 
         // cbFilter
         // 
         this.cbFilter.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.cbFilter.DrawMode = System.Windows.Forms.DrawMode.OwnerDrawVariable;
         this.cbFilter.FormattingEnabled = true;
         this.cbFilter.ItemHeight = 14;
         this.cbFilter.Location = new System.Drawing.Point(732, 2);
         this.cbFilter.Name = "cbFilter";
         this.cbFilter.Size = new System.Drawing.Size(125, 20);
         this.cbFilter.TabIndex = 17;
         this.cbFilter.Visible = false;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Recno";
         this.dataGridViewTextBoxColumn1.HeaderText = "№";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         this.dataGridViewTextBoxColumn1.Visible = false;
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Org";
         this.dataGridViewTextBoxColumn2.FillWeight = 200F;
         this.dataGridViewTextBoxColumn2.HeaderText = "Котрагент";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.ReadOnly = true;
         this.dataGridViewTextBoxColumn2.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Programmatic;
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.DataPropertyName = "Doctype";
         this.dataGridViewTextBoxColumn3.HeaderText = "Тип документа";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         this.dataGridViewTextBoxColumn3.ReadOnly = true;
         this.dataGridViewTextBoxColumn3.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Programmatic;
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn4.DataPropertyName = "DateExec";
         this.dataGridViewTextBoxColumn4.FillWeight = 200F;
         this.dataGridViewTextBoxColumn4.HeaderText = "Дата";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         this.dataGridViewTextBoxColumn4.ReadOnly = true;
         this.dataGridViewTextBoxColumn4.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Programmatic;
         // 
         // dataGridViewTextBoxColumn5
         // 
         this.dataGridViewTextBoxColumn5.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn5.DataPropertyName = "DateCreated";
         this.dataGridViewTextBoxColumn5.FillWeight = 101F;
         this.dataGridViewTextBoxColumn5.HeaderText = "Дата создания";
         this.dataGridViewTextBoxColumn5.MinimumWidth = 150;
         this.dataGridViewTextBoxColumn5.Name = "dataGridViewTextBoxColumn5";
         this.dataGridViewTextBoxColumn5.ReadOnly = true;
         this.dataGridViewTextBoxColumn5.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Programmatic;
         // 
         // dataGridViewTextBoxColumn6
         // 
         this.dataGridViewTextBoxColumn6.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn6.DataPropertyName = "Sended";
         this.dataGridViewTextBoxColumn6.FillWeight = 101F;
         this.dataGridViewTextBoxColumn6.HeaderText = "Дата передачи";
         this.dataGridViewTextBoxColumn6.Name = "dataGridViewTextBoxColumn6";
         this.dataGridViewTextBoxColumn6.ReadOnly = true;
         this.dataGridViewTextBoxColumn6.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Programmatic;
         // 
         // dataGridViewTextBoxColumn7
         // 
         this.dataGridViewTextBoxColumn7.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn7.DataPropertyName = "Sum";
         this.dataGridViewTextBoxColumn7.FillWeight = 70F;
         this.dataGridViewTextBoxColumn7.HeaderText = "Сумма";
         this.dataGridViewTextBoxColumn7.Name = "dataGridViewTextBoxColumn7";
         this.dataGridViewTextBoxColumn7.ReadOnly = true;
         this.dataGridViewTextBoxColumn7.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Programmatic;
         // 
         // dataGridViewTextBoxColumn8
         // 
         this.dataGridViewTextBoxColumn8.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn8.DataPropertyName = "OrgAddr";
         this.dataGridViewTextBoxColumn8.HeaderText = "OrgAddr";
         this.dataGridViewTextBoxColumn8.Name = "dataGridViewTextBoxColumn8";
         this.dataGridViewTextBoxColumn8.ReadOnly = true;
         this.dataGridViewTextBoxColumn8.Visible = false;
         // 
         // dgvDetailClass
         // 
         this.dgvDetailClass.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvDetailClass.HeaderText = "Школа(класс)";
         this.dgvDetailClass.Name = "dgvDetailClass";
         this.dgvDetailClass.ReadOnly = true;
         // 
         // dgvDetailSchoolSubject
         // 
         this.dgvDetailSchoolSubject.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvDetailSchoolSubject.HeaderText = "Предмет";
         this.dgvDetailSchoolSubject.Name = "dgvDetailSchoolSubject";
         this.dgvDetailSchoolSubject.ReadOnly = true;
         // 
         // dgvDetailData
         // 
         this.dgvDetailData.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvDetailData.HeaderText = "Дата создания";
         this.dgvDetailData.Name = "dgvDetailData";
         this.dgvDetailData.ReadOnly = true;
         // 
         // dgvDetailHomework
         // 
         this.dgvDetailHomework.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvDetailHomework.FillWeight = 200F;
         this.dgvDetailHomework.HeaderText = "Домашнее задание";
         this.dgvDetailHomework.Name = "dgvDetailHomework";
         this.dgvDetailHomework.ReadOnly = true;
         // 
         // dgvScheduleStudent
         // 
         this.dgvScheduleStudent.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvScheduleStudent.HeaderText = "Ученик";
         this.dgvScheduleStudent.Name = "dgvScheduleStudent";
         this.dgvScheduleStudent.ReadOnly = true;
         // 
         // dgvSchedulePoint
         // 
         this.dgvSchedulePoint.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvSchedulePoint.FillWeight = 30F;
         this.dgvSchedulePoint.HeaderText = "Оценка";
         this.dgvSchedulePoint.Name = "dgvSchedulePoint";
         this.dgvSchedulePoint.ReadOnly = true;
         // 
         // dgvScheduleBehavior
         // 
         this.dgvScheduleBehavior.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvScheduleBehavior.FillWeight = 70F;
         this.dgvScheduleBehavior.HeaderText = "Поведение";
         this.dgvScheduleBehavior.Name = "dgvScheduleBehavior";
         this.dgvScheduleBehavior.ReadOnly = true;
         // 
         // dgvScheduleNotes
         // 
         this.dgvScheduleNotes.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvScheduleNotes.HeaderText = "Замечания";
         this.dgvScheduleNotes.Name = "dgvScheduleNotes";
         this.dgvScheduleNotes.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn9
         // 
         this.dataGridViewTextBoxColumn9.DataPropertyName = "StoreObject";
         this.dataGridViewTextBoxColumn9.HeaderText = "StoreObject";
         this.dataGridViewTextBoxColumn9.Name = "dataGridViewTextBoxColumn9";
         this.dataGridViewTextBoxColumn9.Visible = false;
         // 
         // dataGridViewTextBoxColumn10
         // 
         this.dataGridViewTextBoxColumn10.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn10.DataPropertyName = "Item";
         this.dataGridViewTextBoxColumn10.FillWeight = 500F;
         this.dataGridViewTextBoxColumn10.HeaderText = "Наименование";
         this.dataGridViewTextBoxColumn10.Name = "dataGridViewTextBoxColumn10";
         // 
         // dataGridViewTextBoxColumn11
         // 
         this.dataGridViewTextBoxColumn11.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn11.DataPropertyName = "Qty";
         this.dataGridViewTextBoxColumn11.HeaderText = "Присутствие";
         this.dataGridViewTextBoxColumn11.Name = "dataGridViewTextBoxColumn11";
         // 
         // dataGridViewTextBoxColumn12
         // 
         this.dataGridViewTextBoxColumn12.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn12.DataPropertyName = "Item";
         this.dataGridViewTextBoxColumn12.FillWeight = 400F;
         this.dataGridViewTextBoxColumn12.HeaderText = "Наименование";
         this.dataGridViewTextBoxColumn12.Name = "dataGridViewTextBoxColumn12";
         // 
         // dataGridViewTextBoxColumn13
         // 
         this.dataGridViewTextBoxColumn13.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn13.DataPropertyName = "Qty";
         this.dataGridViewTextBoxColumn13.HeaderText = "Количество";
         this.dataGridViewTextBoxColumn13.Name = "dataGridViewTextBoxColumn13";
         // 
         // dataGridViewTextBoxColumn14
         // 
         this.dataGridViewTextBoxColumn14.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn14.DataPropertyName = "SCost";
         this.dataGridViewTextBoxColumn14.HeaderText = "Цена";
         this.dataGridViewTextBoxColumn14.Name = "dataGridViewTextBoxColumn14";
         // 
         // FmDetail
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(892, 612);
         this.Controls.Add(this.cbFilter);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.dtpEnd);
         this.Controls.Add(this.dtpBegin);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.statusStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(3, 4, 3, 4);
         this.Name = "FmDetail";
         this.Text = "Подробно";
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmDetail_FormClosed);
         ((System.ComponentModel.ISupportInitialize)(this.dgvDetail)).EndInit();
         this.scCenter.Panel1.ResumeLayout(false);
         this.scCenter.Panel1.PerformLayout();
         this.scCenter.Panel2.ResumeLayout(false);
         this.scCenter.ResumeLayout(false);
         this.panel2.ResumeLayout(false);
         this.panel2.PerformLayout();
         this.scBottom.Panel1.ResumeLayout(false);
         this.scBottom.Panel1.PerformLayout();
         this.scBottom.Panel2.ResumeLayout(false);
         this.scBottom.Panel2.PerformLayout();
         this.scBottom.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvSchedule)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.DateTimePicker dtpBegin;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      public System.Windows.Forms.DataGridView dgvDetail;
      public System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.SplitContainer scCenter;
      private System.Windows.Forms.SplitContainer scBottom;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.Label label6;
      private System.Windows.Forms.Label label7;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.ToolStripLabel toolStripLabel3;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripLabel toolStripLabel4;
      private System.Windows.Forms.Panel panel1;
      public System.Windows.Forms.ToolStripButton btnRoute;
      private System.Windows.Forms.LinkLabel lblAdress;
      private System.Windows.Forms.ComboBox cbFilter;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ImageList imPhoto;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.ToolStripButton tbnMessage;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
      private System.Windows.Forms.ToolStripButton tsbMakeHtml;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn5;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn6;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn7;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn8;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn9;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn10;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn11;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn12;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn13;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn14;
      private System.Windows.Forms.TextBox Annonce;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvDetailClass;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvDetailSchoolSubject;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvDetailData;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvDetailHomework;
      private System.Windows.Forms.DataGridView dgvSchedule;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvScheduleStudent;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvSchedulePoint;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvScheduleBehavior;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvScheduleNotes;
      private System.Windows.Forms.ToolStripComboBox cbAgents;
   }
}