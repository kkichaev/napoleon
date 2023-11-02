namespace GRSoft.NapoleonManager
{
   partial class FmDocsReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmDocsReport));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnExcel = new System.Windows.Forms.ToolStripButton();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.cbDivision = new System.Windows.Forms.ToolStripComboBox();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.grid = new System.Windows.Forms.DataGridView();
         this.Column1 = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.btnSel = new System.Windows.Forms.ToolStripButton();
         this.btnUnsel = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.tbClear = new System.Windows.Forms.ToolStripButton();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.period = new GRSoft.NapoleonManager.DatePeriodView();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).BeginInit();
         this.toolStrip2.SuspendLayout();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.btnExcel,
            this.toolStripLabel1,
            this.cbDivision,
            this.toolStripSeparator1,
            this.toolStripLabel2});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(746, 25);
         this.toolStrip1.TabIndex = 0;
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Получить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // btnExcel
         // 
         this.btnExcel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnExcel.Image = global::GRSoft.NapoleonManager.Properties.Resources.excel;
         this.btnExcel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnExcel.Name = "btnExcel";
         this.btnExcel.Size = new System.Drawing.Size(23, 22);
         this.btnExcel.Text = "Excel";
         this.btnExcel.Click += new System.EventHandler(this.btnExcel_Click);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(92, 22);
         this.toolStripLabel1.Text = "Подразделение";
         // 
         // cbDivision
         // 
         this.cbDivision.Name = "cbDivision";
         this.cbDivision.Size = new System.Drawing.Size(165, 25);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(49, 22);
         this.toolStripLabel2.Text = "Период";
         // 
         // grid
         // 
         this.grid.AllowUserToAddRows = false;
         this.grid.AllowUserToDeleteRows = false;
         this.grid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.grid.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1,
            this.Column2});
         this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid.Location = new System.Drawing.Point(0, 50);
         this.grid.Name = "grid";
         this.grid.RowHeadersVisible = false;
         this.grid.Size = new System.Drawing.Size(746, 396);
         this.grid.TabIndex = 2;
         this.grid.CurrentCellDirtyStateChanged += new System.EventHandler(this.grid_CurrentCellDirtyStateChanged);
         // 
         // Column1
         // 
         this.Column1.DataPropertyName = "Sel";
         this.Column1.HeaderText = "";
         this.Column1.Name = "Column1";
         this.Column1.Width = 30;
         // 
         // Column2
         // 
         this.Column2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column2.DataPropertyName = "Name";
         this.Column2.HeaderText = "Наименование";
         this.Column2.Name = "Column2";
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnSel,
            this.btnUnsel,
            this.toolStripSeparator2,
            this.tbFind,
            this.tbClear});
         this.toolStrip2.Location = new System.Drawing.Point(0, 25);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(746, 25);
         this.toolStrip2.TabIndex = 3;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // btnSel
         // 
         this.btnSel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSel.Image = global::GRSoft.NapoleonManager.Properties.Resources.checkbox;
         this.btnSel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSel.Name = "btnSel";
         this.btnSel.Size = new System.Drawing.Size(23, 22);
         this.btnSel.Text = "Выбрать все";
         this.btnSel.Click += new System.EventHandler(this.btnSel_Click);
         // 
         // btnUnsel
         // 
         this.btnUnsel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnUnsel.Image = global::GRSoft.NapoleonManager.Properties.Resources.uncheckbox;
         this.btnUnsel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnUnsel.Name = "btnUnsel";
         this.btnUnsel.Size = new System.Drawing.Size(23, 22);
         this.btnUnsel.Text = "Сбросить все";
         this.btnUnsel.Click += new System.EventHandler(this.btnUnsel_Click);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // tbFind
         // 
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(150, 25);
         this.tbFind.TextChanged += new System.EventHandler(this.tbFind_TextChanged);
         // 
         // tbClear
         // 
         this.tbClear.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbClear.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.tbClear.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbClear.Name = "tbClear";
         this.tbClear.Size = new System.Drawing.Size(23, 22);
         this.tbClear.Text = "Очистить";
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Наименование";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // period
         // 
         this.period.Finish = new System.DateTime(2015, 5, 14, 0, 0, 0, 0);
         this.period.Location = new System.Drawing.Point(373, -2);
         this.period.Name = "period";
         this.period.Size = new System.Drawing.Size(367, 27);
         this.period.Start = new System.DateTime(2015, 5, 14, 0, 0, 0, 0);
         this.period.TabIndex = 1;
         // 
         // FmDocsReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(746, 446);
         this.Controls.Add(this.grid);
         this.Controls.Add(this.toolStrip2);
         this.Controls.Add(this.period);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmDocsReport";
         this.Text = "Отчет по документам";
         this.Load += new System.EventHandler(this.FmDocsReport_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).EndInit();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripComboBox cbDivision;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private DatePeriodView period;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnExcel;
      private System.Windows.Forms.DataGridView grid;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripButton btnSel;
      private System.Windows.Forms.ToolStripButton btnUnsel;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton tbClear;
      private System.Windows.Forms.DataGridViewCheckBoxColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
   }
}