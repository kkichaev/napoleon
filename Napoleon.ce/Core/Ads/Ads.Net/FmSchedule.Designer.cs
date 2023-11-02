namespace GRSoft.Ads
{
   partial class FmSchedule
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmSchedule));
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.cbBrigade = new System.Windows.Forms.ToolStripComboBox();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.bntRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.udYear = new System.Windows.Forms.NumericUpDown();
         this.dgvSchedule = new System.Windows.Forms.DataGridView();
         this.dgvScheduleData = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvScheduleStatus = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvScheduleAddress = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.menu = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.btnCreate = new System.Windows.Forms.ToolStripButton();
         this.cbMonth = new System.Windows.Forms.ToolStripComboBox();
         this.dgvDistrict = new System.Windows.Forms.DataGridView();
         this.dgvDistrictName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip3 = new System.Windows.Forms.ToolStrip();
         this.btnDistrict = new System.Windows.Forms.ToolStripButton();
         this.btnDelDistrict = new System.Windows.Forms.ToolStripButton();
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.udYear)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvSchedule)).BeginInit();
         this.toolStrip2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvDistrict)).BeginInit();
         this.toolStrip3.SuspendLayout();
         this.SuspendLayout();
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 320);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(624, 22);
         this.statusStrip1.TabIndex = 0;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.cbBrigade,
            this.toolStripSeparator1,
            this.bntRefresh,
            this.btnSave});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(624, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // cbBrigade
         // 
         this.cbBrigade.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbBrigade.Name = "cbBrigade";
         this.cbBrigade.Size = new System.Drawing.Size(121, 25);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // bntRefresh
         // 
         this.bntRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.bntRefresh.Image = ((System.Drawing.Image)(resources.GetObject("bntRefresh.Image")));
         this.bntRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.bntRefresh.Name = "bntRefresh";
         this.bntRefresh.Size = new System.Drawing.Size(23, 22);
         this.bntRefresh.Text = "Обновить";
         this.bntRefresh.Click += new System.EventHandler(this.bntRefresh_Click);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Image = ((System.Drawing.Image)(resources.GetObject("btnSave.Image")));
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.udYear);
         this.splitContainer1.Panel1.Controls.Add(this.dgvSchedule);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip2);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvDistrict);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip3);
         this.splitContainer1.Size = new System.Drawing.Size(624, 295);
         this.splitContainer1.SplitterDistance = 346;
         this.splitContainer1.TabIndex = 2;
         // 
         // udYear
         // 
         this.udYear.Location = new System.Drawing.Point(161, 3);
         this.udYear.Maximum = new decimal(new int[] {
            3000,
            0,
            0,
            0});
         this.udYear.Name = "udYear";
         this.udYear.Size = new System.Drawing.Size(65, 20);
         this.udYear.TabIndex = 2;
         // 
         // dgvSchedule
         // 
         this.dgvSchedule.AllowUserToAddRows = false;
         this.dgvSchedule.AllowUserToDeleteRows = false;
         this.dgvSchedule.AllowUserToResizeRows = false;
         this.dgvSchedule.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvSchedule.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvScheduleData,
            this.dgvScheduleStatus,
            this.dgvScheduleAddress});
         this.dgvSchedule.ContextMenuStrip = this.menu;
         this.dgvSchedule.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvSchedule.Location = new System.Drawing.Point(0, 25);
         this.dgvSchedule.MultiSelect = false;
         this.dgvSchedule.Name = "dgvSchedule";
         this.dgvSchedule.RowHeadersVisible = false;
         this.dgvSchedule.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvSchedule.Size = new System.Drawing.Size(346, 270);
         this.dgvSchedule.TabIndex = 1;
         this.dgvSchedule.MouseDown += new System.Windows.Forms.MouseEventHandler(this.dgvSchedule_MouseDown);
         this.dgvSchedule.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvSchedule_CellFormatting);
         this.dgvSchedule.EditingControlShowing += new System.Windows.Forms.DataGridViewEditingControlShowingEventHandler(this.dgvSchedule_EditingControlShowing);
         this.dgvSchedule.SelectionChanged += new System.EventHandler(this.dgvSchedule_SelectionChanged);
         // 
         // dgvScheduleData
         // 
         this.dgvScheduleData.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvScheduleData.DataPropertyName = "DateStr";
         this.dgvScheduleData.HeaderText = "Дата";
         this.dgvScheduleData.Name = "dgvScheduleData";
         // 
         // dgvScheduleStatus
         // 
         this.dgvScheduleStatus.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvScheduleStatus.DataPropertyName = "StatusStr";
         this.dgvScheduleStatus.HeaderText = "Статус";
         this.dgvScheduleStatus.Name = "dgvScheduleStatus";
         // 
         // dgvScheduleAddress
         // 
         this.dgvScheduleAddress.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvScheduleAddress.FillWeight = 300F;
         this.dgvScheduleAddress.HeaderText = "Адрес";
         this.dgvScheduleAddress.Name = "dgvScheduleAddress";
         this.dgvScheduleAddress.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.dgvScheduleAddress.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Automatic;
         // 
         // menu
         // 
         this.menu.Name = "menu";
         this.menu.Size = new System.Drawing.Size(61, 4);
         this.menu.ItemClicked += new System.Windows.Forms.ToolStripItemClickedEventHandler(this.menu_ItemClicked);
         this.menu.Opening += new System.ComponentModel.CancelEventHandler(this.menu_Opening);
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnCreate,
            this.cbMonth});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(346, 25);
         this.toolStrip2.TabIndex = 0;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // btnCreate
         // 
         this.btnCreate.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnCreate.Image = ((System.Drawing.Image)(resources.GetObject("btnCreate.Image")));
         this.btnCreate.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnCreate.Name = "btnCreate";
         this.btnCreate.Size = new System.Drawing.Size(23, 22);
         this.btnCreate.Text = "Создать расписание";
         this.btnCreate.Click += new System.EventHandler(this.btnCreate_Click);
         // 
         // cbMonth
         // 
         this.cbMonth.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbMonth.Name = "cbMonth";
         this.cbMonth.Size = new System.Drawing.Size(121, 25);
         // 
         // dgvDistrict
         // 
         this.dgvDistrict.AllowUserToAddRows = false;
         this.dgvDistrict.AllowUserToDeleteRows = false;
         this.dgvDistrict.AllowUserToResizeRows = false;
         this.dgvDistrict.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvDistrict.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvDistrictName});
         this.dgvDistrict.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvDistrict.Location = new System.Drawing.Point(0, 25);
         this.dgvDistrict.MultiSelect = false;
         this.dgvDistrict.Name = "dgvDistrict";
         this.dgvDistrict.RowHeadersVisible = false;
         this.dgvDistrict.Size = new System.Drawing.Size(274, 270);
         this.dgvDistrict.TabIndex = 1;
         // 
         // dgvDistrictName
         // 
         this.dgvDistrictName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvDistrictName.DataPropertyName = "District";
         this.dgvDistrictName.HeaderText = "Наименование";
         this.dgvDistrictName.Name = "dgvDistrictName";
         // 
         // toolStrip3
         // 
         this.toolStrip3.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnDistrict,
            this.btnDelDistrict});
         this.toolStrip3.Location = new System.Drawing.Point(0, 0);
         this.toolStrip3.Name = "toolStrip3";
         this.toolStrip3.Size = new System.Drawing.Size(274, 25);
         this.toolStrip3.TabIndex = 0;
         this.toolStrip3.Text = "toolStrip3";
         // 
         // btnDistrict
         // 
         this.btnDistrict.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDistrict.Image = ((System.Drawing.Image)(resources.GetObject("btnDistrict.Image")));
         this.btnDistrict.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDistrict.Name = "btnDistrict";
         this.btnDistrict.Size = new System.Drawing.Size(23, 22);
         this.btnDistrict.Text = "Район";
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
         // FmSchedule
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(624, 342);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.statusStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmSchedule";
         this.Text = "Графики работ";
         this.Load += new System.EventHandler(this.FmSchedule_Load);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmSchedule_FormClosing);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.udYear)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvSchedule)).EndInit();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvDistrict)).EndInit();
         this.toolStrip3.ResumeLayout(false);
         this.toolStrip3.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStrip toolStrip3;
      private System.Windows.Forms.DataGridView dgvSchedule;
      private System.Windows.Forms.DataGridView dgvDistrict;
      private System.Windows.Forms.ToolStripButton btnCreate;
      private System.Windows.Forms.ToolStripButton btnDistrict;
      private System.Windows.Forms.ToolStripButton btnDelDistrict;
      private System.Windows.Forms.ToolStripComboBox cbBrigade;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripButton bntRefresh;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvDistrictName;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ToolStripComboBox cbMonth;
      private System.Windows.Forms.NumericUpDown udYear;
      private System.Windows.Forms.ContextMenuStrip menu;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvScheduleData;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvScheduleStatus;
      private System.Windows.Forms.DataGridViewComboBoxColumn dgvScheduleAddress;
   }
}