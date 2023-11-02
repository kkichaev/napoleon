namespace GRSoft.NapoleonManager
{
   partial class FmAgentPlans
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
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tbSave = new System.Windows.Forms.ToolStripButton();
         this.tbRefresh = new System.Windows.Forms.ToolStripButton();
         this.tbReport = new System.Windows.Forms.ToolStripButton();
         this.dtPlanDate = new System.Windows.Forms.DateTimePicker();
         this.dgvPlans = new System.Windows.Forms.DataGridView();
         this.clmnAgent = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnPlan1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnFact1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnPlan2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnFact2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvPlans)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tbSave,
            this.tbRefresh,
            this.tbReport});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(601, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tbSave
         // 
         this.tbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbSave.Enabled = false;
         this.tbSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.tbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbSave.Name = "tbSave";
         this.tbSave.Size = new System.Drawing.Size(23, 22);
         this.tbSave.Text = "Сохранить";
         this.tbSave.Click += new System.EventHandler(this.tbSave_Click);
         // 
         // tbRefresh
         // 
         this.tbRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.tbRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbRefresh.Name = "tbRefresh";
         this.tbRefresh.Size = new System.Drawing.Size(23, 22);
         this.tbRefresh.Text = "Обновить";
         this.tbRefresh.Click += new System.EventHandler(this.tbRefresh_Click);
         // 
         // tbReport
         // 
         this.tbReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbReport.Image = global::GRSoft.NapoleonManager.Properties.Resources.print;
         this.tbReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbReport.Name = "tbReport";
         this.tbReport.Size = new System.Drawing.Size(23, 22);
         this.tbReport.Text = "Печать отчета";
         this.tbReport.Click += new System.EventHandler(this.tbReport_Click);
         // 
         // dtPlanDate
         // 
         this.dtPlanDate.CustomFormat = "MMMM yyyy";
         this.dtPlanDate.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dtPlanDate.Location = new System.Drawing.Point(84, 2);
         this.dtPlanDate.Name = "dtPlanDate";
         this.dtPlanDate.ShowUpDown = true;
         this.dtPlanDate.Size = new System.Drawing.Size(129, 20);
         this.dtPlanDate.TabIndex = 1;
         // 
         // dgvPlans
         // 
         this.dgvPlans.AllowUserToAddRows = false;
         this.dgvPlans.AllowUserToDeleteRows = false;
         this.dgvPlans.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvPlans.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnAgent,
            this.clmnPlan1,
            this.clmnFact1,
            this.clmnPlan2,
            this.clmnFact2});
         this.dgvPlans.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvPlans.Location = new System.Drawing.Point(0, 25);
         this.dgvPlans.Name = "dgvPlans";
         this.dgvPlans.RowHeadersVisible = false;
         this.dgvPlans.Size = new System.Drawing.Size(601, 422);
         this.dgvPlans.TabIndex = 2;
         // 
         // clmnAgent
         // 
         this.clmnAgent.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnAgent.DataPropertyName = "Name";
         this.clmnAgent.HeaderText = "Агент";
         this.clmnAgent.Name = "clmnAgent";
         this.clmnAgent.ReadOnly = true;
         // 
         // clmnPlan1
         // 
         this.clmnPlan1.DataPropertyName = "Plan1";
         this.clmnPlan1.FillWeight = 50F;
         this.clmnPlan1.HeaderText = "План \"заморозка\"";
         this.clmnPlan1.Name = "clmnPlan1";
         this.clmnPlan1.ReadOnly = true;
         // 
         // clmnFact1
         // 
         this.clmnFact1.DataPropertyName = "Fact1";
         this.clmnFact1.FillWeight = 50F;
         this.clmnFact1.HeaderText = "Факт \"заморозка\"";
         this.clmnFact1.Name = "clmnFact1";
         this.clmnFact1.ReadOnly = true;
         // 
         // clmnPlan2
         // 
         this.clmnPlan2.DataPropertyName = "Plan2";
         this.clmnPlan2.FillWeight = 50F;
         this.clmnPlan2.HeaderText = "План \"Колбасы\"";
         this.clmnPlan2.Name = "clmnPlan2";
         this.clmnPlan2.ReadOnly = true;
         // 
         // clmnFact2
         // 
         this.clmnFact2.DataPropertyName = "Fact2";
         this.clmnFact2.FillWeight = 50F;
         this.clmnFact2.HeaderText = "Факт \"Колбасы\"";
         this.clmnFact2.Name = "clmnFact2";
         this.clmnFact2.ReadOnly = true;
         // 
         // FmAgentPlans
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(601, 447);
         this.Controls.Add(this.dgvPlans);
         this.Controls.Add(this.dtPlanDate);
         this.Controls.Add(this.toolStrip1);
         this.Name = "FmAgentPlans";
         this.Text = "Планы";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvPlans)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton tbRefresh;
      private System.Windows.Forms.ToolStripButton tbSave;
      private System.Windows.Forms.ToolStripButton tbReport;
      private System.Windows.Forms.DateTimePicker dtPlanDate;
      private System.Windows.Forms.DataGridView dgvPlans;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAgent;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnPlan1;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnFact1;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnPlan2;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnFact2;
   }
}