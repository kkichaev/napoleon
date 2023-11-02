namespace GRSoft.Ads
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
         this.dgvOrder = new System.Windows.Forms.DataGridView();
         this.dgvOrderBrigade = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrderCreated = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrderWorkTimeBegin = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrderNumber = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrderAddress = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.contextMenuStrip1 = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.miMarkAsread = new System.Windows.Forms.ToolStripMenuItem();
         this.поToolStripMenuItem = new System.Windows.Forms.ToolStripSeparator();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.dgvOrderItems = new System.Windows.Forms.DataGridView();
         this.dgvOrderItemsName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrderItemsQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrderItemsCost = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.tbRemark = new System.Windows.Forms.TextBox();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.cbBrigade = new System.Windows.Forms.ComboBox();
         this.cbUnread = new System.Windows.Forms.CheckBox();
         this.toolStrip1.SuspendLayout();
         this.panel1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrder)).BeginInit();
         this.contextMenuStrip1.SuspendLayout();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrderItems)).BeginInit();
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
         this.miToday.Size = new System.Drawing.Size(148, 22);
         this.miToday.Text = "За сегодня";
         this.miToday.Click += new System.EventHandler(this.miToday_Click);
         // 
         // miRange
         // 
         this.miRange.Image = ((System.Drawing.Image)(resources.GetObject("miRange.Image")));
         this.miRange.Name = "miRange";
         this.miRange.Size = new System.Drawing.Size(148, 22);
         this.miRange.Text = "За интервал";
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
         this.toolStripLabel2.Margin = new System.Windows.Forms.Padding(140, 1, 0, 2);
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(19, 22);
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
         this.toolStripLabel3.Size = new System.Drawing.Size(49, 22);
         this.toolStripLabel3.Text = "Бригада";
         // 
         // btnSearchBack
         // 
         this.btnSearchBack.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSearchBack.Image = ((System.Drawing.Image)(resources.GetObject("btnSearchBack.Image")));
         this.btnSearchBack.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSearchBack.Name = "btnSearchBack";
         this.btnSearchBack.Size = new System.Drawing.Size(23, 22);
         this.btnSearchBack.Text = "Искать назад";
         // 
         // btnSearchForward
         // 
         this.btnSearchForward.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSearchForward.Image = ((System.Drawing.Image)(resources.GetObject("btnSearchForward.Image")));
         this.btnSearchForward.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSearchForward.Name = "btnSearchForward";
         this.btnSearchForward.Size = new System.Drawing.Size(23, 22);
         this.btnSearchForward.Text = " Искать вперед";
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
         this.splitContainer1.Panel1.Controls.Add(this.dgvOrder);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.splitContainer2);
         this.splitContainer1.Size = new System.Drawing.Size(907, 455);
         this.splitContainer1.SplitterDistance = 302;
         this.splitContainer1.TabIndex = 0;
         // 
         // dgvOrder
         // 
         this.dgvOrder.AllowUserToAddRows = false;
         this.dgvOrder.AllowUserToDeleteRows = false;
         this.dgvOrder.AllowUserToResizeRows = false;
         this.dgvOrder.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrder.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvOrderBrigade,
            this.dgvOrderCreated,
            this.dgvOrderWorkTimeBegin,
            this.dgvOrderNumber,
            this.dgvOrderAddress});
         this.dgvOrder.ContextMenuStrip = this.contextMenuStrip1;
         this.dgvOrder.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrder.Location = new System.Drawing.Point(0, 0);
         this.dgvOrder.Name = "dgvOrder";
         this.dgvOrder.RowHeadersVisible = false;
         this.dgvOrder.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvOrder.Size = new System.Drawing.Size(907, 302);
         this.dgvOrder.TabIndex = 1;
         this.dgvOrder.MouseDown += new System.Windows.Forms.MouseEventHandler(this.dgvOrder_MouseDown);
         this.dgvOrder.ColumnHeaderMouseClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.dgvOrder_ColumnHeaderMouseClick);
         this.dgvOrder.MouseDoubleClick += new System.Windows.Forms.MouseEventHandler(this.dgvOrder_MouseDoubleClick);
         this.dgvOrder.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvOrder_CellFormatting);
         this.dgvOrder.SelectionChanged += new System.EventHandler(this.dgvOrder_SelectionChanged);
         // 
         // dgvOrderBrigade
         // 
         this.dgvOrderBrigade.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrderBrigade.DataPropertyName = "BrigadeName";
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
         // dgvOrderWorkTimeBegin
         // 
         this.dgvOrderWorkTimeBegin.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         this.dgvOrderWorkTimeBegin.DataPropertyName = "Date";
         this.dgvOrderWorkTimeBegin.HeaderText = "Дата";
         this.dgvOrderWorkTimeBegin.Name = "dgvOrderWorkTimeBegin";
         // 
         // dgvOrderNumber
         // 
         this.dgvOrderNumber.DataPropertyName = "Number";
         this.dgvOrderNumber.HeaderText = "Номер";
         this.dgvOrderNumber.Name = "dgvOrderNumber";
         // 
         // dgvOrderAddress
         // 
         this.dgvOrderAddress.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrderAddress.DataPropertyName = "Address";
         this.dgvOrderAddress.HeaderText = "Адрес";
         this.dgvOrderAddress.Name = "dgvOrderAddress";
         // 
         // contextMenuStrip1
         // 
         this.contextMenuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miMarkAsread,
            this.поToolStripMenuItem});
         this.contextMenuStrip1.Name = "contextMenuStrip1";
         this.contextMenuStrip1.Size = new System.Drawing.Size(225, 32);
         // 
         // miMarkAsread
         // 
         this.miMarkAsread.Name = "miMarkAsread";
         this.miMarkAsread.Size = new System.Drawing.Size(224, 22);
         this.miMarkAsread.Text = "Пометить как прочитанное";
         this.miMarkAsread.Click += new System.EventHandler(this.miMarkAsread_Click);
         // 
         // поToolStripMenuItem
         // 
         this.поToolStripMenuItem.Name = "поToolStripMenuItem";
         this.поToolStripMenuItem.Size = new System.Drawing.Size(221, 6);
         // 
         // splitContainer2
         // 
         this.splitContainer2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer2.Location = new System.Drawing.Point(0, 0);
         this.splitContainer2.Name = "splitContainer2";
         // 
         // splitContainer2.Panel1
         // 
         this.splitContainer2.Panel1.Controls.Add(this.dgvOrderItems);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.groupBox1);
         this.splitContainer2.Size = new System.Drawing.Size(907, 149);
         this.splitContainer2.SplitterDistance = 449;
         this.splitContainer2.TabIndex = 1;
         // 
         // dgvOrderItems
         // 
         this.dgvOrderItems.AllowUserToAddRows = false;
         this.dgvOrderItems.AllowUserToDeleteRows = false;
         this.dgvOrderItems.AllowUserToResizeRows = false;
         this.dgvOrderItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrderItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvOrderItemsName,
            this.dgvOrderItemsQty,
            this.dgvOrderItemsCost});
         this.dgvOrderItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrderItems.Location = new System.Drawing.Point(0, 0);
         this.dgvOrderItems.Name = "dgvOrderItems";
         this.dgvOrderItems.RowHeadersVisible = false;
         this.dgvOrderItems.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvOrderItems.Size = new System.Drawing.Size(449, 149);
         this.dgvOrderItems.TabIndex = 0;
         // 
         // dgvOrderItemsName
         // 
         this.dgvOrderItemsName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrderItemsName.DataPropertyName = "Name";
         this.dgvOrderItemsName.FillWeight = 400F;
         this.dgvOrderItemsName.HeaderText = "Наименование";
         this.dgvOrderItemsName.Name = "dgvOrderItemsName";
         // 
         // dgvOrderItemsQty
         // 
         this.dgvOrderItemsQty.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrderItemsQty.DataPropertyName = "Qty";
         this.dgvOrderItemsQty.HeaderText = "Кол-во";
         this.dgvOrderItemsQty.Name = "dgvOrderItemsQty";
         // 
         // dgvOrderItemsCost
         // 
         this.dgvOrderItemsCost.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrderItemsCost.DataPropertyName = "Cost";
         this.dgvOrderItemsCost.HeaderText = "Цена";
         this.dgvOrderItemsCost.Name = "dgvOrderItemsCost";
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.tbRemark);
         this.groupBox1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.groupBox1.Location = new System.Drawing.Point(0, 0);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(454, 149);
         this.groupBox1.TabIndex = 1;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Содержание";
         // 
         // tbRemark
         // 
         this.tbRemark.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tbRemark.Location = new System.Drawing.Point(3, 16);
         this.tbRemark.Multiline = true;
         this.tbRemark.Name = "tbRemark";
         this.tbRemark.Size = new System.Drawing.Size(448, 130);
         this.tbRemark.TabIndex = 0;
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
         this.Text = "Заказы пользователя";
         this.Load += new System.EventHandler(this.FmOrder_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmOrder_FormClosed);
         this.Move += new System.EventHandler(this.FmOrder_Move);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrder)).EndInit();
         this.contextMenuStrip1.ResumeLayout(false);
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrderItems)).EndInit();
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
      protected System.Windows.Forms.DataGridView dgvOrder;
      private System.Windows.Forms.DataGridView dgvOrderItems;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.TextBox tbRemark;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderItemsName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderItemsQty;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderItemsCost;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderBrigade;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderCreated;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderWorkTimeBegin;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderNumber;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderAddress;
      private System.Windows.Forms.CheckBox cbUnread;
      private System.Windows.Forms.ContextMenuStrip contextMenuStrip1;
      private System.Windows.Forms.ToolStripMenuItem miMarkAsread;
      private System.Windows.Forms.ToolStripSeparator поToolStripMenuItem;
      private System.Windows.Forms.ToolStripButton btnReport;
   }
}