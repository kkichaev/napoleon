namespace GRSoft.NapoleonManager
{
   partial class FmUserOrder
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmUserOrder));
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnRange = new System.Windows.Forms.ToolStripSplitButton();
         this.miToday = new System.Windows.Forms.ToolStripMenuItem();
         this.miRange = new System.Windows.Forms.ToolStripMenuItem();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.toolStripLabel3 = new System.Windows.Forms.ToolStripLabel();
         this.btnSearchBack = new System.Windows.Forms.ToolStripButton();
         this.btnSearchForward = new System.Windows.Forms.ToolStripButton();
         this.btnReport = new System.Windows.Forms.ToolStripButton();
         this.panel1 = new System.Windows.Forms.Panel();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.grid = new System.Windows.Forms.DataGridView();
         this.contextMenuStrip1 = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.miMarkAsread = new System.Windows.Forms.ToolStripMenuItem();
         this.поToolStripMenuItem = new System.Windows.Forms.ToolStripSeparator();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.tbRemark = new System.Windows.Forms.TextBox();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.cbBrigade = new System.Windows.Forms.ComboBox();
         this.cbUnread = new System.Windows.Forms.CheckBox();
         this.dgvOrderBrigade = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrderCreated = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrderNumber = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrderAddress = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         this.panel1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).BeginInit();
         this.contextMenuStrip1.SuspendLayout();
         this.groupBox1.SuspendLayout();
         this.SuspendLayout();
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 496);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(921, 22);
         this.statusStrip1.TabIndex = 0;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.toolStripSeparator1,
            this.btnRange,
            this.toolStripLabel1,
            this.toolStripLabel2,
            this.toolStripSeparator2,
            this.toolStripLabel3,
            this.btnSearchBack,
            this.btnSearchForward,
            this.btnReport});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(921, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
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
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Margin = new System.Windows.Forms.Padding(10, 0, 0, 0);
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // btnRange
         // 
         this.btnRange.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRange.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miToday,
            this.miRange});
         this.btnRange.Image = ((System.Drawing.Image)(resources.GetObject("btnRange.Image")));
         this.btnRange.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRange.Name = "btnRange";
         this.btnRange.Size = new System.Drawing.Size(32, 22);
         this.btnRange.Text = "toolStripSplitButton1";
         this.btnRange.ToolTipText = "За сегодня";
         this.btnRange.ButtonClick += new System.EventHandler(this.btnRange_ButtonClick);
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
         this.miRange.Text = "За интервал";
         this.miRange.Click += new System.EventHandler(this.miRange_Click);
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
         // toolStripLabel3
         // 
         this.toolStripLabel3.Name = "toolStripLabel3";
         this.toolStripLabel3.Size = new System.Drawing.Size(51, 22);
         this.toolStripLabel3.Text = "Бригада";
         this.toolStripLabel3.Visible = false;
         // 
         // btnSearchBack
         // 
         this.btnSearchBack.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSearchBack.Image = ((System.Drawing.Image)(resources.GetObject("btnSearchBack.Image")));
         this.btnSearchBack.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSearchBack.Name = "btnSearchBack";
         this.btnSearchBack.Size = new System.Drawing.Size(23, 22);
         this.btnSearchBack.Text = "Искать назад";
         this.btnSearchBack.Visible = false;
         // 
         // btnSearchForward
         // 
         this.btnSearchForward.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSearchForward.Image = ((System.Drawing.Image)(resources.GetObject("btnSearchForward.Image")));
         this.btnSearchForward.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSearchForward.Name = "btnSearchForward";
         this.btnSearchForward.Size = new System.Drawing.Size(23, 22);
         this.btnSearchForward.Text = " Искать вперед";
         this.btnSearchForward.Visible = false;
         // 
         // btnReport
         // 
         this.btnReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnReport.Image = ((System.Drawing.Image)(resources.GetObject("btnReport.Image")));
         this.btnReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnReport.Margin = new System.Windows.Forms.Padding(175, 1, 0, 2);
         this.btnReport.Name = "btnReport";
         this.btnReport.Size = new System.Drawing.Size(23, 22);
         this.btnReport.Text = "Отчет по заказам";
         this.btnReport.Visible = false;
         this.btnReport.Click += new System.EventHandler(this.btnReport_Click);
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.splitContainer1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7, 8, 7, 8);
         this.panel1.Size = new System.Drawing.Size(921, 471);
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
         this.splitContainer1.Panel2.Controls.Add(this.groupBox1);
         this.splitContainer1.Size = new System.Drawing.Size(907, 455);
         this.splitContainer1.SplitterDistance = 302;
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
            this.dgvOrderNumber,
            this.dgvOrderAddress});
         this.grid.ContextMenuStrip = this.contextMenuStrip1;
         this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid.Location = new System.Drawing.Point(0, 0);
         this.grid.Name = "grid";
         this.grid.RowHeadersVisible = false;
         this.grid.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.grid.Size = new System.Drawing.Size(907, 302);
         this.grid.TabIndex = 1;
         this.grid.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvOrder_CellFormatting);
         this.grid.ColumnHeaderMouseClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.dgvOrder_ColumnHeaderMouseClick);
         this.grid.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.grid_RowEnter);
         this.grid.SelectionChanged += new System.EventHandler(this.dgvOrder_SelectionChanged);
         this.grid.MouseDoubleClick += new System.Windows.Forms.MouseEventHandler(this.dgvOrder_MouseDoubleClick);
         this.grid.MouseDown += new System.Windows.Forms.MouseEventHandler(this.dgvOrder_MouseDown);
         // 
         // contextMenuStrip1
         // 
         this.contextMenuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miMarkAsread,
            this.поToolStripMenuItem});
         this.contextMenuStrip1.Name = "contextMenuStrip1";
         this.contextMenuStrip1.Size = new System.Drawing.Size(226, 32);
         // 
         // miMarkAsread
         // 
         this.miMarkAsread.Name = "miMarkAsread";
         this.miMarkAsread.Size = new System.Drawing.Size(225, 22);
         this.miMarkAsread.Text = "Пометить как прочитанное";
         this.miMarkAsread.Click += new System.EventHandler(this.miMarkAsread_Click);
         // 
         // поToolStripMenuItem
         // 
         this.поToolStripMenuItem.Name = "поToolStripMenuItem";
         this.поToolStripMenuItem.Size = new System.Drawing.Size(222, 6);
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.tbRemark);
         this.groupBox1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.groupBox1.Location = new System.Drawing.Point(0, 0);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(907, 149);
         this.groupBox1.TabIndex = 2;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Содержание";
         // 
         // tbRemark
         // 
         this.tbRemark.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tbRemark.Location = new System.Drawing.Point(3, 16);
         this.tbRemark.Multiline = true;
         this.tbRemark.Name = "tbRemark";
         this.tbRemark.Size = new System.Drawing.Size(901, 130);
         this.tbRemark.TabIndex = 1;
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(96, 2);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(132, 20);
         this.dtpBegin.TabIndex = 3;
         this.dtpBegin.LocationChanged += new System.EventHandler(this.dtpBegin_LocationChanged);
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(263, 2);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(132, 20);
         this.dtpEnd.TabIndex = 4;
         this.dtpEnd.ValueChanged += new System.EventHandler(this.dtpEnd_ValueChanged);
         // 
         // cbBrigade
         // 
         this.cbBrigade.FormattingEnabled = true;
         this.cbBrigade.Location = new System.Drawing.Point(459, 1);
         this.cbBrigade.Name = "cbBrigade";
         this.cbBrigade.Size = new System.Drawing.Size(141, 22);
         this.cbBrigade.TabIndex = 5;
         this.cbBrigade.Visible = false;
         this.cbBrigade.SelectionChangeCommitted += new System.EventHandler(this.cbBrigade_SelectionChangeCommitted);
         // 
         // cbUnread
         // 
         this.cbUnread.AutoSize = true;
         this.cbUnread.Checked = true;
         this.cbUnread.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbUnread.Location = new System.Drawing.Point(607, 5);
         this.cbUnread.Name = "cbUnread";
         this.cbUnread.Size = new System.Drawing.Size(60, 18);
         this.cbUnread.TabIndex = 6;
         this.cbUnread.Text = "Новые";
         this.cbUnread.UseVisualStyleBackColor = true;
         this.cbUnread.Visible = false;
         // 
         // dgvOrderBrigade
         // 
         this.dgvOrderBrigade.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrderBrigade.DataPropertyName = "AgentName";
         this.dgvOrderBrigade.HeaderText = "Бригада";
         this.dgvOrderBrigade.Name = "dgvOrderBrigade";
         // 
         // dgvOrderCreated
         // 
         this.dgvOrderCreated.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         this.dgvOrderCreated.DataPropertyName = "Created";
         this.dgvOrderCreated.HeaderText = "Создана";
         this.dgvOrderCreated.Name = "dgvOrderCreated";
         // 
         // dgvOrderNumber
         // 
         this.dgvOrderNumber.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrderNumber.DataPropertyName = "Client";
         this.dgvOrderNumber.HeaderText = "Клиент";
         this.dgvOrderNumber.Name = "dgvOrderNumber";
         // 
         // dgvOrderAddress
         // 
         this.dgvOrderAddress.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrderAddress.DataPropertyName = "Address";
         this.dgvOrderAddress.HeaderText = "Адрес";
         this.dgvOrderAddress.Name = "dgvOrderAddress";
         // 
         // FmUserOrder
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(921, 518);
         this.Controls.Add(this.cbUnread);
         this.Controls.Add(this.cbBrigade);
         this.Controls.Add(this.dtpEnd);
         this.Controls.Add(this.dtpBegin);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.statusStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmUserOrder";
         this.Text = "Заявки на дополнительные работы";
         this.Move += new System.EventHandler(this.FmOrder_Move);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.grid)).EndInit();
         this.contextMenuStrip1.ResumeLayout(false);
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripSplitButton btnRange;
      private System.Windows.Forms.ToolStripMenuItem miToday;
      private System.Windows.Forms.ToolStripMenuItem miRange;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.DateTimePicker dtpBegin;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.ToolStripLabel toolStripLabel3;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ComboBox cbBrigade;
      private System.Windows.Forms.ToolStripButton btnSearchBack;
      private System.Windows.Forms.ToolStripButton btnSearchForward;
      private System.Windows.Forms.SplitContainer splitContainer1;
      protected System.Windows.Forms.DataGridView grid;
      private System.Windows.Forms.CheckBox cbUnread;
      private System.Windows.Forms.ContextMenuStrip contextMenuStrip1;
      private System.Windows.Forms.ToolStripMenuItem miMarkAsread;
      private System.Windows.Forms.ToolStripSeparator поToolStripMenuItem;
      private System.Windows.Forms.ToolStripButton btnReport;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.TextBox tbRemark;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderBrigade;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderCreated;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderNumber;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderAddress;
   }
}