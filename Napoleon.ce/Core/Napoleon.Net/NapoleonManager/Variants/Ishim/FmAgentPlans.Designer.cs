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
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle2 = new System.Windows.Forms.DataGridViewCellStyle();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmAgentPlans));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tbRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.cbDivisions = new System.Windows.Forms.ToolStripComboBox();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.cbAgents = new System.Windows.Forms.ToolStripComboBox();
         this.tbSave = new System.Windows.Forms.ToolStripButton();
         this.tsbEditPlan = new System.Windows.Forms.ToolStripButton();
         this.dtPlanDate = new System.Windows.Forms.DateTimePicker();
         this.dgvPlans = new System.Windows.Forms.DataGridView();
         this.clmnAgent = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnPlan1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnPlan2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvPlans)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tbRefresh,
            this.toolStripLabel1,
            this.cbDivisions,
            this.toolStripLabel2,
            this.cbAgents,
            this.tbSave,
            this.tsbEditPlan});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(852, 39);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tbRefresh
         // 
         this.tbRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.tbRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbRefresh.Name = "tbRefresh";
         this.tbRefresh.Size = new System.Drawing.Size(36, 36);
         this.tbRefresh.Text = "Обновить";
         this.tbRefresh.Click += new System.EventHandler(this.tbRefresh_Click);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(133, 36);
         this.toolStripLabel1.Text = "Подразделение";
         // 
         // cbDivisions
         // 
         this.cbDivisions.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.cbDivisions.Name = "cbDivisions";
         this.cbDivisions.Size = new System.Drawing.Size(121, 39);
         this.cbDivisions.ToolTipText = "Позразделения";
         this.cbDivisions.SelectedIndexChanged += new System.EventHandler(this.cbDivisions_SelectedIndexChanged);
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(54, 36);
         this.toolStripLabel2.Text = "Агент";
         // 
         // cbAgents
         // 
         this.cbAgents.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(200, 39);
         this.cbAgents.ToolTipText = "Агенты";
         this.cbAgents.SelectedIndexChanged += new System.EventHandler(this.cbAgents_SelectedIndexChanged);
         // 
         // tbSave
         // 
         this.tbSave.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.tbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbSave.Enabled = false;
         this.tbSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.tbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbSave.Name = "tbSave";
         this.tbSave.Size = new System.Drawing.Size(36, 36);
         this.tbSave.Text = "Сохранить";
         this.tbSave.Click += new System.EventHandler(this.tbSave_Click);
         // 
         // tsbEditPlan
         // 
         this.tsbEditPlan.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbEditPlan.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.tsbEditPlan.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbEditPlan.Name = "tsbEditPlan";
         this.tsbEditPlan.Size = new System.Drawing.Size(36, 36);
         this.tsbEditPlan.ToolTipText = "Редактировать план";
         this.tsbEditPlan.Click += new System.EventHandler(this.tsbEditPlan_Click);
         // 
         // dtPlanDate
         // 
         this.dtPlanDate.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.dtPlanDate.CustomFormat = "MMMM yyyy";
         this.dtPlanDate.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.dtPlanDate.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dtPlanDate.Location = new System.Drawing.Point(628, 7);
         this.dtPlanDate.Name = "dtPlanDate";
         this.dtPlanDate.Size = new System.Drawing.Size(173, 26);
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
            this.clmnPlan2});
         this.dgvPlans.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvPlans.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.dgvPlans.Location = new System.Drawing.Point(0, 39);
         this.dgvPlans.Name = "dgvPlans";
         this.dgvPlans.RowHeadersVisible = false;
         this.dgvPlans.RowHeadersWidth = 51;
         this.dgvPlans.Size = new System.Drawing.Size(852, 427);
         this.dgvPlans.TabIndex = 2;
         this.dgvPlans.DataError += new System.Windows.Forms.DataGridViewDataErrorEventHandler(this.dgvPlans_DataError);
         // 
         // clmnAgent
         // 
         this.clmnAgent.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnAgent.DataPropertyName = "Name";
         this.clmnAgent.HeaderText = "План";
         this.clmnAgent.MinimumWidth = 6;
         this.clmnAgent.Name = "clmnAgent";
         this.clmnAgent.ReadOnly = true;
         // 
         // clmnPlan1
         // 
         this.clmnPlan1.DataPropertyName = "Weight";
         dataGridViewCellStyle1.Format = "#,#;#,#;";
         this.clmnPlan1.DefaultCellStyle = dataGridViewCellStyle1;
         this.clmnPlan1.FillWeight = 50F;
         this.clmnPlan1.HeaderText = "План заказов";
         this.clmnPlan1.MinimumWidth = 6;
         this.clmnPlan1.Name = "clmnPlan1";
         this.clmnPlan1.Width = 125;
         // 
         // clmnPlan2
         // 
         this.clmnPlan2.DataPropertyName = "AKB";
         dataGridViewCellStyle2.Format = "#,#;#,#;";
         this.clmnPlan2.DefaultCellStyle = dataGridViewCellStyle2;
         this.clmnPlan2.FillWeight = 50F;
         this.clmnPlan2.HeaderText = "План АКБ";
         this.clmnPlan2.MinimumWidth = 6;
         this.clmnPlan2.Name = "clmnPlan2";
         this.clmnPlan2.Width = 125;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Папка";
         this.dataGridViewTextBoxColumn1.MinimumWidth = 6;
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Weight";
         this.dataGridViewTextBoxColumn2.FillWeight = 50F;
         this.dataGridViewTextBoxColumn2.HeaderText = "План заказов";
         this.dataGridViewTextBoxColumn2.MinimumWidth = 6;
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.ReadOnly = true;
         this.dataGridViewTextBoxColumn2.Width = 125;
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.DataPropertyName = "AKB";
         this.dataGridViewTextBoxColumn3.FillWeight = 50F;
         this.dataGridViewTextBoxColumn3.HeaderText = "План АКБ";
         this.dataGridViewTextBoxColumn3.MinimumWidth = 6;
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         this.dataGridViewTextBoxColumn3.ReadOnly = true;
         this.dataGridViewTextBoxColumn3.Width = 125;
         // 
         // FmAgentPlans
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(852, 466);
         this.Controls.Add(this.dgvPlans);
         this.Controls.Add(this.dtPlanDate);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
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
      private System.Windows.Forms.DateTimePicker dtPlanDate;
      private System.Windows.Forms.DataGridView dgvPlans;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripComboBox cbDivisions;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.ToolStripComboBox cbAgents;
      private System.Windows.Forms.ToolStripButton tsbEditPlan;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAgent;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnPlan1;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnPlan2;
   }
}