namespace GRSoft.Ads
{
   partial class FmBrigade
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmBrigade));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.panel1 = new System.Windows.Forms.Panel();
         this.dgvBrigade = new System.Windows.Forms.DataGridView();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnAddBrigade = new System.Windows.Forms.ToolStripButton();
         this.btnEditBrigade = new System.Windows.Forms.ToolStripButton();
         this.btnDeleteBrigade = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator3 = new System.Windows.Forms.ToolStripSeparator();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.btnSearchBack = new System.Windows.Forms.ToolStripButton();
         this.btnSearchForward = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.Schedule = new System.Windows.Forms.ToolStripButton();
         this.btnAddress = new System.Windows.Forms.ToolStripButton();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.panel2 = new System.Windows.Forms.Panel();
         this.dgvDistrict = new System.Windows.Forms.DataGridView();
         this.dgvDistrictName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.btnDistrict = new System.Windows.Forms.ToolStripButton();
         this.btnDelDistrict = new System.Windows.Forms.ToolStripButton();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.panel3 = new System.Windows.Forms.Panel();
         this.dgvStuff = new System.Windows.Forms.DataGridView();
         this.dgvStuffFio = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip3 = new System.Windows.Forms.ToolStrip();
         this.btnStuff = new System.Windows.Forms.ToolStripButton();
         this.btnDelStuff = new System.Windows.Forms.ToolStripButton();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.dgvBrigadeLogin = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvBrigadePassw = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvBrigadeName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvBrigadeJobType = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvBrigadePrefix = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.panel1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvBrigade)).BeginInit();
         this.toolStrip1.SuspendLayout();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         this.panel2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvDistrict)).BeginInit();
         this.toolStrip2.SuspendLayout();
         this.panel3.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvStuff)).BeginInit();
         this.toolStrip3.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 0);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.panel1);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.splitContainer2);
         this.splitContainer1.Size = new System.Drawing.Size(836, 528);
         this.splitContainer1.SplitterDistance = 449;
         this.splitContainer1.TabIndex = 0;
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.dgvBrigade);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7);
         this.panel1.Size = new System.Drawing.Size(449, 503);
         this.panel1.TabIndex = 1;
         // 
         // dgvBrigade
         // 
         this.dgvBrigade.AllowUserToAddRows = false;
         this.dgvBrigade.AllowUserToDeleteRows = false;
         this.dgvBrigade.AllowUserToResizeRows = false;
         this.dgvBrigade.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvBrigade.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvBrigadeLogin,
            this.dgvBrigadePassw,
            this.dgvBrigadeName,
            this.dgvBrigadeJobType,
            this.dgvBrigadePrefix});
         this.dgvBrigade.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvBrigade.Location = new System.Drawing.Point(7, 7);
         this.dgvBrigade.Name = "dgvBrigade";
         this.dgvBrigade.RowHeadersVisible = false;
         this.dgvBrigade.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvBrigade.Size = new System.Drawing.Size(435, 489);
         this.dgvBrigade.TabIndex = 0;
         this.dgvBrigade.ColumnHeaderMouseClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.dgvBrigade_ColumnHeaderMouseClick);
         this.dgvBrigade.DoubleClick += new System.EventHandler(this.dgvBrigade_DoubleClick);
         this.dgvBrigade.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvBrigade_CellFormatting);
         this.dgvBrigade.SelectionChanged += new System.EventHandler(this.dgvBrigade_SelectionChanged);
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddBrigade,
            this.btnEditBrigade,
            this.btnDeleteBrigade,
            this.btnRefresh,
            this.toolStripSeparator3,
            this.tbFind,
            this.btnSearchBack,
            this.btnSearchForward,
            this.toolStripSeparator1,
            this.Schedule,
            this.btnAddress});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(449, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnAddBrigade
         // 
         this.btnAddBrigade.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddBrigade.Image = ((System.Drawing.Image)(resources.GetObject("btnAddBrigade.Image")));
         this.btnAddBrigade.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddBrigade.Name = "btnAddBrigade";
         this.btnAddBrigade.Size = new System.Drawing.Size(23, 22);
         this.btnAddBrigade.Text = "Добавить";
         this.btnAddBrigade.Click += new System.EventHandler(this.btnAddBrigade_Click);
         // 
         // btnEditBrigade
         // 
         this.btnEditBrigade.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEditBrigade.Image = ((System.Drawing.Image)(resources.GetObject("btnEditBrigade.Image")));
         this.btnEditBrigade.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEditBrigade.Name = "btnEditBrigade";
         this.btnEditBrigade.Size = new System.Drawing.Size(23, 22);
         this.btnEditBrigade.Text = "Изменить";
         this.btnEditBrigade.Click += new System.EventHandler(this.btnEditBrigade_Click);
         // 
         // btnDeleteBrigade
         // 
         this.btnDeleteBrigade.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDeleteBrigade.Image = ((System.Drawing.Image)(resources.GetObject("btnDeleteBrigade.Image")));
         this.btnDeleteBrigade.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDeleteBrigade.Name = "btnDeleteBrigade";
         this.btnDeleteBrigade.Size = new System.Drawing.Size(23, 22);
         this.btnDeleteBrigade.Text = "Удалить";
         this.btnDeleteBrigade.Click += new System.EventHandler(this.btnDeleteBrigade_Click);
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = ((System.Drawing.Image)(resources.GetObject("btnRefresh.Image")));
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
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
         // tbFind
         // 
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(100, 25);
         this.tbFind.ToolTipText = "Введите строку для поиска";
         this.tbFind.KeyDown += new System.Windows.Forms.KeyEventHandler(this.tbFind_KeyDown);
         // 
         // btnSearchBack
         // 
         this.btnSearchBack.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSearchBack.Image = ((System.Drawing.Image)(resources.GetObject("btnSearchBack.Image")));
         this.btnSearchBack.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSearchBack.Name = "btnSearchBack";
         this.btnSearchBack.Size = new System.Drawing.Size(23, 22);
         this.btnSearchBack.Text = "Искать назад";
         this.btnSearchBack.Click += new System.EventHandler(this.btnSearchBack_Click);
         // 
         // btnSearchForward
         // 
         this.btnSearchForward.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSearchForward.Image = ((System.Drawing.Image)(resources.GetObject("btnSearchForward.Image")));
         this.btnSearchForward.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSearchForward.Name = "btnSearchForward";
         this.btnSearchForward.Size = new System.Drawing.Size(23, 22);
         this.btnSearchForward.Text = " Искать вперед";
         this.btnSearchForward.Click += new System.EventHandler(this.btnSearchForward_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // Schedule
         // 
         this.Schedule.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.Schedule.Image = ((System.Drawing.Image)(resources.GetObject("Schedule.Image")));
         this.Schedule.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.Schedule.Name = "Schedule";
         this.Schedule.Size = new System.Drawing.Size(23, 22);
         this.Schedule.Text = "График рабт";
         this.Schedule.Click += new System.EventHandler(this.Schedule_Click);
         // 
         // btnAddress
         // 
         this.btnAddress.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddress.Image = ((System.Drawing.Image)(resources.GetObject("btnAddress.Image")));
         this.btnAddress.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddress.Name = "btnAddress";
         this.btnAddress.Size = new System.Drawing.Size(23, 22);
         this.btnAddress.Text = "Адрес";
         this.btnAddress.Click += new System.EventHandler(this.btnAddress_Click);
         // 
         // splitContainer2
         // 
         this.splitContainer2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer2.Location = new System.Drawing.Point(0, 0);
         this.splitContainer2.Name = "splitContainer2";
         this.splitContainer2.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer2.Panel1
         // 
         this.splitContainer2.Panel1.Controls.Add(this.panel2);
         this.splitContainer2.Panel1.Controls.Add(this.toolStrip2);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.panel3);
         this.splitContainer2.Panel2.Controls.Add(this.toolStrip3);
         this.splitContainer2.Panel2.Controls.Add(this.statusStrip1);
         this.splitContainer2.Size = new System.Drawing.Size(383, 528);
         this.splitContainer2.SplitterDistance = 165;
         this.splitContainer2.TabIndex = 0;
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.dgvDistrict);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel2.Location = new System.Drawing.Point(0, 25);
         this.panel2.Name = "panel2";
         this.panel2.Padding = new System.Windows.Forms.Padding(7);
         this.panel2.Size = new System.Drawing.Size(383, 140);
         this.panel2.TabIndex = 1;
         // 
         // dgvDistrict
         // 
         this.dgvDistrict.AllowDrop = true;
         this.dgvDistrict.AllowUserToAddRows = false;
         this.dgvDistrict.AllowUserToDeleteRows = false;
         this.dgvDistrict.AllowUserToResizeRows = false;
         this.dgvDistrict.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvDistrict.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvDistrictName});
         this.dgvDistrict.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvDistrict.Location = new System.Drawing.Point(7, 7);
         this.dgvDistrict.Name = "dgvDistrict";
         this.dgvDistrict.RowHeadersVisible = false;
         this.dgvDistrict.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvDistrict.Size = new System.Drawing.Size(369, 126);
         this.dgvDistrict.TabIndex = 0;
         this.dgvDistrict.DragEnter += new System.Windows.Forms.DragEventHandler(this.dgvDistrict_DragEnter);
         this.dgvDistrict.DragDrop += new System.Windows.Forms.DragEventHandler(this.dgvDistrict_DragDrop);
         // 
         // dgvDistrictName
         // 
         this.dgvDistrictName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvDistrictName.DataPropertyName = "Name";
         this.dgvDistrictName.HeaderText = "Наименование";
         this.dgvDistrictName.Name = "dgvDistrictName";
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnDistrict,
            this.btnDelDistrict,
            this.toolStripLabel1});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(383, 25);
         this.toolStrip2.TabIndex = 0;
         // 
         // btnDistrict
         // 
         this.btnDistrict.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDistrict.Image = ((System.Drawing.Image)(resources.GetObject("btnDistrict.Image")));
         this.btnDistrict.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDistrict.Name = "btnDistrict";
         this.btnDistrict.Size = new System.Drawing.Size(23, 22);
         this.btnDistrict.Text = "Районы";
         this.btnDistrict.Click += new System.EventHandler(this.btnDistrict_Click);
         // 
         // btnDelDistrict
         // 
         this.btnDelDistrict.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelDistrict.Image = ((System.Drawing.Image)(resources.GetObject("btnDelDistrict.Image")));
         this.btnDelDistrict.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelDistrict.Name = "btnDelDistrict";
         this.btnDelDistrict.Size = new System.Drawing.Size(23, 22);
         this.btnDelDistrict.Text = "Удалить";
         this.btnDelDistrict.Click += new System.EventHandler(this.btnDelDistrict_Click);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(45, 22);
         this.toolStripLabel1.Text = "Районы";
         // 
         // panel3
         // 
         this.panel3.Controls.Add(this.dgvStuff);
         this.panel3.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel3.Location = new System.Drawing.Point(0, 25);
         this.panel3.Name = "panel3";
         this.panel3.Padding = new System.Windows.Forms.Padding(7);
         this.panel3.Size = new System.Drawing.Size(383, 312);
         this.panel3.TabIndex = 2;
         // 
         // dgvStuff
         // 
         this.dgvStuff.AllowDrop = true;
         this.dgvStuff.AllowUserToAddRows = false;
         this.dgvStuff.AllowUserToDeleteRows = false;
         this.dgvStuff.AllowUserToResizeRows = false;
         this.dgvStuff.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvStuff.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvStuffFio});
         this.dgvStuff.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvStuff.Location = new System.Drawing.Point(7, 7);
         this.dgvStuff.Name = "dgvStuff";
         this.dgvStuff.RowHeadersVisible = false;
         this.dgvStuff.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvStuff.Size = new System.Drawing.Size(369, 298);
         this.dgvStuff.TabIndex = 0;
         this.dgvStuff.DragEnter += new System.Windows.Forms.DragEventHandler(this.dgvStuff_DragEnter);
         this.dgvStuff.DragDrop += new System.Windows.Forms.DragEventHandler(this.dgvStuff_DragDrop);
         // 
         // dgvStuffFio
         // 
         this.dgvStuffFio.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvStuffFio.DataPropertyName = "FIO";
         this.dgvStuffFio.HeaderText = "ФИО";
         this.dgvStuffFio.Name = "dgvStuffFio";
         // 
         // toolStrip3
         // 
         this.toolStrip3.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnStuff,
            this.btnDelStuff,
            this.toolStripLabel2});
         this.toolStrip3.Location = new System.Drawing.Point(0, 0);
         this.toolStrip3.Name = "toolStrip3";
         this.toolStrip3.Size = new System.Drawing.Size(383, 25);
         this.toolStrip3.TabIndex = 1;
         this.toolStrip3.Text = "toolStrip3";
         // 
         // btnStuff
         // 
         this.btnStuff.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnStuff.Image = ((System.Drawing.Image)(resources.GetObject("btnStuff.Image")));
         this.btnStuff.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnStuff.Name = "btnStuff";
         this.btnStuff.Size = new System.Drawing.Size(23, 22);
         this.btnStuff.Text = "Состав бригады";
         this.btnStuff.Click += new System.EventHandler(this.btnStuff_Click);
         // 
         // btnDelStuff
         // 
         this.btnDelStuff.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelStuff.Image = ((System.Drawing.Image)(resources.GetObject("btnDelStuff.Image")));
         this.btnDelStuff.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelStuff.Name = "btnDelStuff";
         this.btnDelStuff.Size = new System.Drawing.Size(23, 22);
         this.btnDelStuff.Text = "Удалить";
         this.btnDelStuff.Click += new System.EventHandler(this.btnDelStuff_Click);
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(43, 22);
         this.toolStripLabel2.Text = "Состав";
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 337);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(383, 22);
         this.statusStrip1.TabIndex = 0;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // dgvBrigadeLogin
         // 
         this.dgvBrigadeLogin.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvBrigadeLogin.DataPropertyName = "Login";
         this.dgvBrigadeLogin.HeaderText = "Логин";
         this.dgvBrigadeLogin.Name = "dgvBrigadeLogin";
         // 
         // dgvBrigadePassw
         // 
         this.dgvBrigadePassw.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvBrigadePassw.DataPropertyName = "Password";
         this.dgvBrigadePassw.HeaderText = "Пароль";
         this.dgvBrigadePassw.Name = "dgvBrigadePassw";
         // 
         // dgvBrigadeName
         // 
         this.dgvBrigadeName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvBrigadeName.DataPropertyName = "Name";
         this.dgvBrigadeName.FillWeight = 120F;
         this.dgvBrigadeName.HeaderText = "Название в системе";
         this.dgvBrigadeName.Name = "dgvBrigadeName";
         // 
         // dgvBrigadeJobType
         // 
         this.dgvBrigadeJobType.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvBrigadeJobType.DataPropertyName = "JobType";
         this.dgvBrigadeJobType.HeaderText = "Вид работ";
         this.dgvBrigadeJobType.Name = "dgvBrigadeJobType";
         // 
         // dgvBrigadePrefix
         // 
         this.dgvBrigadePrefix.DataPropertyName = "Prefix";
         this.dgvBrigadePrefix.HeaderText = "Префикс";
         this.dgvBrigadePrefix.Name = "dgvBrigadePrefix";
         // 
         // FmBrigade
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(836, 528);
         this.Controls.Add(this.splitContainer1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmBrigade";
         this.Text = "Бригады";
         this.Load += new System.EventHandler(this.FmBrigade_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmBrigade_FormClosed);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         this.panel1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvBrigade)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel1.PerformLayout();
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.Panel2.PerformLayout();
         this.splitContainer2.ResumeLayout(false);
         this.panel2.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvDistrict)).EndInit();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.panel3.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvStuff)).EndInit();
         this.toolStrip3.ResumeLayout(false);
         this.toolStrip3.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.Panel panel3;
      private System.Windows.Forms.ToolStrip toolStrip3;
      private System.Windows.Forms.DataGridView dgvBrigade;
      private System.Windows.Forms.ToolStripButton btnAddBrigade;
      private System.Windows.Forms.ToolStripButton btnEditBrigade;
      private System.Windows.Forms.ToolStripButton btnDeleteBrigade;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnDistrict;
      private System.Windows.Forms.DataGridView dgvDistrict;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvDistrictName;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripButton btnStuff;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.DataGridView dgvStuff;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvStuffFio;
      private System.Windows.Forms.ToolStripButton btnDelDistrict;
      private System.Windows.Forms.ToolStripButton btnDelStuff;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton btnSearchBack;
      private System.Windows.Forms.ToolStripButton btnSearchForward;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripButton Schedule;
      private System.Windows.Forms.ToolStripButton btnAddress;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvBrigadeLogin;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvBrigadePassw;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvBrigadeName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvBrigadeJobType;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvBrigadePrefix;
   }
}