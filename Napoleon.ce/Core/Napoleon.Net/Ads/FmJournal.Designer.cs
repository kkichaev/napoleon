namespace GRSoft.NapoleonManager
{
   partial class FmJournal
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmJournal));
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnEdit = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnPhoto = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.toolStripSeparator3 = new System.Windows.Forms.ToolStripSeparator();
         this.toolStripLabel3 = new System.Windows.Forms.ToolStripLabel();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.btnClear = new System.Windows.Forms.ToolStripButton();
         this.panel1 = new System.Windows.Forms.Panel();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.grid = new System.Windows.Forms.DataGridView();
         this.dgvOrderBrigade = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrderCreated = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrderWorkTimeBegin = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrderWorkTimeEnd = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrderText = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrderClient = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrderAddress = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.groupBox2 = new System.Windows.Forms.GroupBox();
         this.lvPhoto = new System.Windows.Forms.ListView();
         this.imPhoto = new System.Windows.Forms.ImageList();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.tbReport = new System.Windows.Forms.TextBox();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.timer1 = new System.Windows.Forms.Timer();
         this.toolStrip1.SuspendLayout();
         this.panel1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).BeginInit();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         this.groupBox2.SuspendLayout();
         this.groupBox1.SuspendLayout();
         this.SuspendLayout();
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 547);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(1062, 22);
         this.statusStrip1.TabIndex = 0;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAdd,
            this.btnEdit,
            this.btnDel,
            this.btnRefresh,
            this.btnPhoto,
            this.toolStripSeparator1,
            this.toolStripLabel1,
            this.toolStripLabel2,
            this.toolStripSeparator2,
            this.toolStripSeparator3,
            this.toolStripLabel3,
            this.tbFind,
            this.btnClear});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(1062, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = ((System.Drawing.Image)(resources.GetObject("btnAdd.Image")));
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(23, 22);
         this.btnAdd.Text = "Создать";
         this.btnAdd.Visible = false;
         // 
         // btnEdit
         // 
         this.btnEdit.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEdit.Image = ((System.Drawing.Image)(resources.GetObject("btnEdit.Image")));
         this.btnEdit.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEdit.Name = "btnEdit";
         this.btnEdit.Size = new System.Drawing.Size(23, 22);
         this.btnEdit.Text = "Изменить";
         this.btnEdit.Visible = false;
         // 
         // btnDel
         // 
         this.btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDel.Image = ((System.Drawing.Image)(resources.GetObject("btnDel.Image")));
         this.btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(23, 22);
         this.btnDel.Text = "Удалить";
         this.btnDel.Visible = false;
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
         // btnPhoto
         // 
         this.btnPhoto.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnPhoto.Image = global::GRSoft.NapoleonManager.Properties.Resources.actgs;
         this.btnPhoto.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnPhoto.Name = "btnPhoto";
         this.btnPhoto.Size = new System.Drawing.Size(23, 22);
         this.btnPhoto.Text = "Загрузить фото";
         this.btnPhoto.Click += new System.EventHandler(this.btnPhoto_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Margin = new System.Windows.Forms.Padding(10, 0, 0, 0);
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(13, 22);
         this.toolStripLabel1.Text = "с";
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Margin = new System.Windows.Forms.Padding(140, 1, 0, 2);
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(21, 22);
         this.toolStripLabel2.Text = "по";
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Margin = new System.Windows.Forms.Padding(150, 0, 0, 0);
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // toolStripSeparator3
         // 
         this.toolStripSeparator3.Name = "toolStripSeparator3";
         this.toolStripSeparator3.Size = new System.Drawing.Size(6, 25);
         this.toolStripSeparator3.Visible = false;
         // 
         // toolStripLabel3
         // 
         this.toolStripLabel3.Name = "toolStripLabel3";
         this.toolStripLabel3.Size = new System.Drawing.Size(40, 22);
         this.toolStripLabel3.Text = "поиск";
         // 
         // tbFind
         // 
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(100, 25);
         this.tbFind.ToolTipText = "Введите строку для поиска";
         this.tbFind.TextChanged += new System.EventHandler(this.tbFind_TextChanged);
         // 
         // btnClear
         // 
         this.btnClear.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnClear.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.btnClear.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnClear.Name = "btnClear";
         this.btnClear.Size = new System.Drawing.Size(23, 22);
         this.btnClear.Text = "Очистить";
         this.btnClear.Click += new System.EventHandler(this.btnClear_Click);
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.splitContainer1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7, 8, 7, 8);
         this.panel1.Size = new System.Drawing.Size(1062, 522);
         this.panel1.TabIndex = 2;
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(7, 8);
         this.splitContainer1.Name = "splitContainer1";
         this.splitContainer1.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.grid);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.splitContainer2);
         this.splitContainer1.Size = new System.Drawing.Size(1048, 506);
         this.splitContainer1.SplitterDistance = 303;
         this.splitContainer1.TabIndex = 0;
         // 
         // grid
         // 
         this.grid.AllowUserToAddRows = false;
         this.grid.AllowUserToDeleteRows = false;
         this.grid.AllowUserToResizeRows = false;
         this.grid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.grid.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvOrderBrigade,
            this.dgvOrderCreated,
            this.dgvOrderWorkTimeBegin,
            this.dgvOrderWorkTimeEnd,
            this.dgvOrderText,
            this.dgvOrderClient,
            this.dgvOrderAddress,
            this.Column1});
         this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid.Location = new System.Drawing.Point(0, 0);
         this.grid.MultiSelect = false;
         this.grid.Name = "grid";
         this.grid.RowHeadersVisible = false;
         this.grid.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.grid.Size = new System.Drawing.Size(1048, 303);
         this.grid.TabIndex = 1;
         this.grid.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.grid_RowEnter);
         // 
         // dgvOrderBrigade
         // 
         this.dgvOrderBrigade.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrderBrigade.DataPropertyName = "User";
         this.dgvOrderBrigade.HeaderText = "Исполнитель";
         this.dgvOrderBrigade.Name = "dgvOrderBrigade";
         // 
         // dgvOrderCreated
         // 
         this.dgvOrderCreated.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrderCreated.DataPropertyName = "Created";
         this.dgvOrderCreated.HeaderText = "Создана";
         this.dgvOrderCreated.Name = "dgvOrderCreated";
         // 
         // dgvOrderWorkTimeBegin
         // 
         this.dgvOrderWorkTimeBegin.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrderWorkTimeBegin.DataPropertyName = "TimePlan";
         this.dgvOrderWorkTimeBegin.HeaderText = "Время план";
         this.dgvOrderWorkTimeBegin.Name = "dgvOrderWorkTimeBegin";
         // 
         // dgvOrderWorkTimeEnd
         // 
         this.dgvOrderWorkTimeEnd.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrderWorkTimeEnd.DataPropertyName = "TimeFact";
         this.dgvOrderWorkTimeEnd.HeaderText = "Время факт";
         this.dgvOrderWorkTimeEnd.Name = "dgvOrderWorkTimeEnd";
         // 
         // dgvOrderText
         // 
         this.dgvOrderText.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrderText.DataPropertyName = "Task";
         this.dgvOrderText.HeaderText = "Содержание";
         this.dgvOrderText.Name = "dgvOrderText";
         // 
         // dgvOrderClient
         // 
         this.dgvOrderClient.DataPropertyName = "Client";
         this.dgvOrderClient.HeaderText = "Клиент";
         this.dgvOrderClient.Name = "dgvOrderClient";
         // 
         // dgvOrderAddress
         // 
         this.dgvOrderAddress.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrderAddress.DataPropertyName = "Address";
         this.dgvOrderAddress.HeaderText = "Адрес";
         this.dgvOrderAddress.Name = "dgvOrderAddress";
         // 
         // Column1
         // 
         this.Column1.DataPropertyName = "Status";
         this.Column1.HeaderText = "Статус";
         this.Column1.Name = "Column1";
         // 
         // splitContainer2
         // 
         this.splitContainer2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer2.Location = new System.Drawing.Point(0, 0);
         this.splitContainer2.Name = "splitContainer2";
         // 
         // splitContainer2.Panel1
         // 
         this.splitContainer2.Panel1.Controls.Add(this.groupBox2);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.groupBox1);
         this.splitContainer2.Size = new System.Drawing.Size(1048, 199);
         this.splitContainer2.SplitterDistance = 494;
         this.splitContainer2.TabIndex = 1;
         // 
         // groupBox2
         // 
         this.groupBox2.Controls.Add(this.lvPhoto);
         this.groupBox2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.groupBox2.Location = new System.Drawing.Point(0, 0);
         this.groupBox2.Name = "groupBox2";
         this.groupBox2.Size = new System.Drawing.Size(494, 199);
         this.groupBox2.TabIndex = 0;
         this.groupBox2.TabStop = false;
         this.groupBox2.Text = "Фотоотчет";
         // 
         // lvPhoto
         // 
         this.lvPhoto.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lvPhoto.LargeImageList = this.imPhoto;
         this.lvPhoto.Location = new System.Drawing.Point(3, 16);
         this.lvPhoto.Name = "lvPhoto";
         this.lvPhoto.Size = new System.Drawing.Size(488, 180);
         this.lvPhoto.TabIndex = 0;
         this.lvPhoto.UseCompatibleStateImageBehavior = false;
         this.lvPhoto.DoubleClick += new System.EventHandler(this.lvPhoto_DoubleClick);
         // 
         // imPhoto
         // 
         this.imPhoto.ColorDepth = System.Windows.Forms.ColorDepth.Depth32Bit;
         this.imPhoto.ImageSize = new System.Drawing.Size(115, 115);
         this.imPhoto.TransparentColor = System.Drawing.Color.Transparent;
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.tbReport);
         this.groupBox1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.groupBox1.Location = new System.Drawing.Point(0, 0);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(550, 199);
         this.groupBox1.TabIndex = 1;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Отчет";
         // 
         // tbReport
         // 
         this.tbReport.BackColor = System.Drawing.Color.White;
         this.tbReport.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tbReport.Location = new System.Drawing.Point(3, 16);
         this.tbReport.Multiline = true;
         this.tbReport.Name = "tbReport";
         this.tbReport.ReadOnly = true;
         this.tbReport.Size = new System.Drawing.Size(544, 180);
         this.tbReport.TabIndex = 0;
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(89, 2);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(132, 20);
         this.dtpBegin.TabIndex = 3;
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(252, 2);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(132, 20);
         this.dtpEnd.TabIndex = 4;
         // 
         // timer1
         // 
         this.timer1.Interval = 500;
         this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
         // 
         // FmJournal
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(1062, 569);
         this.Controls.Add(this.dtpEnd);
         this.Controls.Add(this.dtpBegin);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.statusStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmJournal";
         this.Text = "Журнал задач";
         this.Load += new System.EventHandler(this.FmJournal_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.grid)).EndInit();
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.ResumeLayout(false);
         this.groupBox2.ResumeLayout(false);
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.DateTimePicker dtpBegin;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private System.Windows.Forms.TextBox tbReport;
      private System.Windows.Forms.GroupBox groupBox1;
      protected System.Windows.Forms.DataGridView grid;
      private System.Windows.Forms.GroupBox groupBox2;
      private System.Windows.Forms.ListView lvPhoto;
      private System.Windows.Forms.ToolStripButton btnPhoto;
      private System.Windows.Forms.ImageList imPhoto;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderBrigade;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderCreated;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderWorkTimeBegin;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderWorkTimeEnd;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderText;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderClient;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderAddress;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel3;
      private System.Windows.Forms.ToolStripButton btnClear;
      private System.Windows.Forms.Timer timer1;
   }
}