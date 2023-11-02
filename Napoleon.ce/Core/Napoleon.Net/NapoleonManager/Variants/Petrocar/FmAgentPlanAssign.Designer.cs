namespace GRSoft.NapoleonManager
{
   partial class FmAgentPlanAssign
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmAgentPlanAssign));
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle2 = new System.Windows.Forms.DataGridViewCellStyle();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.dtpDate = new System.Windows.Forms.DateTimePicker();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnAgent = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnPlanRub = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnPDZ = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.tsbSave});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(637, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnAgent,
            this.clmnPlanRub,
            this.clmnPDZ});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 25);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(637, 519);
         this.dgvItems.TabIndex = 1;
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Margin = new System.Windows.Forms.Padding(140, 1, 0, 2);
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "toolStripButton1";
         this.btnRefresh.ToolTipText = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // tsbSave
         // 
         this.tsbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSave.Enabled = false;
         this.tsbSave.Image = ((System.Drawing.Image)(resources.GetObject("tsbSave.Image")));
         this.tsbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSave.Name = "tsbSave";
         this.tsbSave.Size = new System.Drawing.Size(23, 22);
         this.tsbSave.Text = "Сохранить";
         this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click);
         // 
         // dtpDate
         // 
         this.dtpDate.CustomFormat = "MMMM yyyy";
         this.dtpDate.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dtpDate.Location = new System.Drawing.Point(13, 1);
         this.dtpDate.Name = "dtpDate";
         this.dtpDate.Size = new System.Drawing.Size(131, 20);
         this.dtpDate.TabIndex = 2;
         this.dtpDate.Value = new System.DateTime(2010, 4, 22, 0, 0, 0, 0);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Агент";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Plan";
         this.dataGridViewTextBoxColumn2.HeaderText = "Общий план";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.DataPropertyName = "PDZ";
         this.dataGridViewTextBoxColumn3.HeaderText = "ПДЗ";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         // 
         // clmnAgent
         // 
         this.clmnAgent.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnAgent.DataPropertyName = "Name";
         this.clmnAgent.HeaderText = "Агент";
         this.clmnAgent.Name = "clmnAgent";
         // 
         // clmnPlanRub
         // 
         this.clmnPlanRub.DataPropertyName = "Plan";
         dataGridViewCellStyle1.Format = "N2";
         this.clmnPlanRub.DefaultCellStyle = dataGridViewCellStyle1;
         this.clmnPlanRub.HeaderText = "Общий план, руб.";
         this.clmnPlanRub.Name = "clmnPlanRub";
         // 
         // clmnPDZ
         // 
         this.clmnPDZ.DataPropertyName = "PDZ";
         dataGridViewCellStyle2.Format = "N2";
         this.clmnPDZ.DefaultCellStyle = dataGridViewCellStyle2;
         this.clmnPDZ.HeaderText = "ПДЗ, %";
         this.clmnPDZ.Name = "clmnPDZ";
         // 
         // FmAgentPlanAssign
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(637, 544);
         this.Controls.Add(this.dtpDate);
         this.Controls.Add(this.dgvItems);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmAgentPlanAssign";
         this.Text = "План продаж и ПДЗ";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.DataGridView dgvItems;
      protected System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton tsbSave;
      protected System.Windows.Forms.DateTimePicker dtpDate;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAgent;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnPlanRub;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnPDZ;
   }
}