namespace GRSoft.NapoleonManager
{
   partial class FmVisitReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmVisitReport));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.btnReport = new System.Windows.Forms.ToolStripButton();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.toolStripButton1 = new System.Windows.Forms.ToolStripButton();
         this.dgvOrg = new System.Windows.Forms.DataGridView();
         this.dgvTaskOrg = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dtpFinish = new System.Windows.Forms.DateTimePicker();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.timer1 = new System.Windows.Forms.Timer(this.components);
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrg)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.toolStripLabel1,
            this.toolStripLabel2,
            this.btnReport,
            this.tbFind,
            this.toolStripButton1});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(826, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(13, 22);
         this.toolStripLabel1.Text = "с";
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Margin = new System.Windows.Forms.Padding(130, 1, 0, 2);
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(21, 22);
         this.toolStripLabel2.Text = "по";
         // 
         // btnReport
         // 
         this.btnReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnReport.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_calendar_timeline;
         this.btnReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnReport.Margin = new System.Windows.Forms.Padding(130, 1, 0, 2);
         this.btnReport.Name = "btnReport";
         this.btnReport.Size = new System.Drawing.Size(23, 22);
         this.btnReport.Text = "Построить отчет";
         this.btnReport.Click += new System.EventHandler(this.btnReport_Click);
         // 
         // tbFind
         // 
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(150, 25);
         this.tbFind.TextChanged += new System.EventHandler(this.tbFind_TextChanged);
         // 
         // toolStripButton1
         // 
         this.toolStripButton1.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButton1.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.toolStripButton1.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButton1.Name = "toolStripButton1";
         this.toolStripButton1.Size = new System.Drawing.Size(23, 22);
         this.toolStripButton1.Text = "Очистить";
         this.toolStripButton1.Click += new System.EventHandler(this.btnClearFind_Click);
         // 
         // dgvOrg
         // 
         this.dgvOrg.AllowUserToAddRows = false;
         this.dgvOrg.AllowUserToDeleteRows = false;
         this.dgvOrg.AllowUserToResizeRows = false;
         this.dgvOrg.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrg.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvTaskOrg});
         this.dgvOrg.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrg.Location = new System.Drawing.Point(0, 25);
         this.dgvOrg.Name = "dgvOrg";
         this.dgvOrg.RowHeadersVisible = false;
         this.dgvOrg.Size = new System.Drawing.Size(826, 559);
         this.dgvOrg.TabIndex = 3;
         // 
         // dgvTaskOrg
         // 
         this.dgvTaskOrg.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvTaskOrg.DataPropertyName = "Name";
         this.dgvTaskOrg.HeaderText = "Организация";
         this.dgvTaskOrg.Name = "dgvTaskOrg";
         // 
         // dtpFinish
         // 
         this.dtpFinish.Location = new System.Drawing.Point(196, 3);
         this.dtpFinish.Name = "dtpFinish";
         this.dtpFinish.Size = new System.Drawing.Size(128, 20);
         this.dtpFinish.TabIndex = 4;
         // 
         // dtpStart
         // 
         this.dtpStart.Location = new System.Drawing.Point(47, 3);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(128, 20);
         this.dtpStart.TabIndex = 5;
         // 
         // timer1
         // 
         this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
         // 
         // FmVisitReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(826, 584);
         this.Controls.Add(this.dtpFinish);
         this.Controls.Add(this.dtpStart);
         this.Controls.Add(this.dgvOrg);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmVisitReport";
         this.Text = "Отчет о визитах";
         this.Load += new System.EventHandler(this.FmVisitReport_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrg)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      protected System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.ToolStripButton btnReport;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton toolStripButton1;
      protected System.Windows.Forms.DataGridView dgvOrg;
      protected System.Windows.Forms.DateTimePicker dtpFinish;
      protected System.Windows.Forms.DateTimePicker dtpStart;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvTaskOrg;
      private System.Windows.Forms.Timer timer1;
   }
}