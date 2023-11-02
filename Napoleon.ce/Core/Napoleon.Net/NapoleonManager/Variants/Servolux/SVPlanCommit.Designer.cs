namespace GRSoft.NapoleonManager
{
   partial class SVPlanCommit
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
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle3 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle4 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle5 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle6 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle7 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle8 = new System.Windows.Forms.DataGridViewCellStyle();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(SVPlanCommit));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvPlans = new System.Windows.Forms.DataGridView();
         this.clmnItem = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnState = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnFactory = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnDate = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnPlanQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvAgents = new System.Windows.Forms.DataGridView();
         this.clmnAgent = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnAgentQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn6 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn7 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvPlans)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvAgents)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbSave});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(1209, 39);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsbSave
         // 
         this.tsbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSave.Enabled = false;
         this.tsbSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.tsbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSave.Margin = new System.Windows.Forms.Padding(2, 1, 0, 2);
         this.tsbSave.Name = "tsbSave";
         this.tsbSave.Size = new System.Drawing.Size(36, 36);
         this.tsbSave.Text = "Сохранить";
         this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click);
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 39);
         this.splitContainer1.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.dgvPlans);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvAgents);
         this.splitContainer1.Size = new System.Drawing.Size(1209, 607);
         this.splitContainer1.SplitterDistance = 786;
         this.splitContainer1.SplitterWidth = 5;
         this.splitContainer1.TabIndex = 1;
         // 
         // dgvPlans
         // 
         this.dgvPlans.AllowUserToAddRows = false;
         this.dgvPlans.AllowUserToDeleteRows = false;
         this.dgvPlans.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvPlans.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnItem,
            this.clmnState,
            this.clmnFactory,
            this.clmnDate,
            this.clmnPlanQty});
         this.dgvPlans.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvPlans.Location = new System.Drawing.Point(0, 0);
         this.dgvPlans.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.dgvPlans.Name = "dgvPlans";
         this.dgvPlans.ReadOnly = true;
         this.dgvPlans.RowHeadersVisible = false;
         this.dgvPlans.Size = new System.Drawing.Size(786, 607);
         this.dgvPlans.TabIndex = 0;
         this.dgvPlans.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvPlans_CellFormatting);
         this.dgvPlans.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvPlans_RowEnter);
         // 
         // clmnItem
         // 
         this.clmnItem.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnItem.DataPropertyName = "Name";
         this.clmnItem.HeaderText = "Товар";
         this.clmnItem.Name = "clmnItem";
         this.clmnItem.ReadOnly = true;
         // 
         // clmnState
         // 
         this.clmnState.DataPropertyName = "State";
         this.clmnState.HeaderText = "Состояние";
         this.clmnState.Name = "clmnState";
         this.clmnState.ReadOnly = true;
         // 
         // clmnFactory
         // 
         this.clmnFactory.DataPropertyName = "Factory";
         this.clmnFactory.HeaderText = "Фабрика";
         this.clmnFactory.Name = "clmnFactory";
         this.clmnFactory.ReadOnly = true;
         // 
         // clmnDate
         // 
         this.clmnDate.DataPropertyName = "Date";
         dataGridViewCellStyle1.Format = "d";
         dataGridViewCellStyle1.NullValue = null;
         this.clmnDate.DefaultCellStyle = dataGridViewCellStyle1;
         this.clmnDate.HeaderText = "Дата";
         this.clmnDate.Name = "clmnDate";
         this.clmnDate.ReadOnly = true;
         // 
         // clmnPlanQty
         // 
         this.clmnPlanQty.DataPropertyName = "Qty";
         dataGridViewCellStyle2.Format = "N0";
         dataGridViewCellStyle2.NullValue = null;
         this.clmnPlanQty.DefaultCellStyle = dataGridViewCellStyle2;
         this.clmnPlanQty.HeaderText = "Количество";
         this.clmnPlanQty.Name = "clmnPlanQty";
         this.clmnPlanQty.ReadOnly = true;
         // 
         // dgvAgents
         // 
         this.dgvAgents.AllowUserToAddRows = false;
         this.dgvAgents.AllowUserToDeleteRows = false;
         this.dgvAgents.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvAgents.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnAgent,
            this.clmnAgentQty});
         this.dgvAgents.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvAgents.Location = new System.Drawing.Point(0, 0);
         this.dgvAgents.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.dgvAgents.Name = "dgvAgents";
         this.dgvAgents.ReadOnly = true;
         this.dgvAgents.RowHeadersVisible = false;
         this.dgvAgents.Size = new System.Drawing.Size(418, 607);
         this.dgvAgents.TabIndex = 0;
         this.dgvAgents.CellEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvAgents_CellEnter);
         // 
         // clmnAgent
         // 
         this.clmnAgent.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnAgent.DataPropertyName = "Agent";
         this.clmnAgent.HeaderText = "Агент";
         this.clmnAgent.Name = "clmnAgent";
         this.clmnAgent.ReadOnly = true;
         // 
         // clmnAgentQty
         // 
         this.clmnAgentQty.DataPropertyName = "Qty";
         dataGridViewCellStyle3.Format = "N0";
         dataGridViewCellStyle3.NullValue = null;
         this.clmnAgentQty.DefaultCellStyle = dataGridViewCellStyle3;
         this.clmnAgentQty.HeaderText = "Количесто";
         this.clmnAgentQty.Name = "clmnAgentQty";
         this.clmnAgentQty.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Товар";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.DataPropertyName = "State";
         this.dataGridViewTextBoxColumn2.HeaderText = "Состояние";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.DataPropertyName = "Date";
         dataGridViewCellStyle4.Format = "d";
         dataGridViewCellStyle4.NullValue = null;
         this.dataGridViewTextBoxColumn3.DefaultCellStyle = dataGridViewCellStyle4;
         this.dataGridViewTextBoxColumn3.HeaderText = "Дата";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         this.dataGridViewTextBoxColumn3.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.DataPropertyName = "Qty";
         dataGridViewCellStyle5.Format = "N0";
         dataGridViewCellStyle5.NullValue = null;
         this.dataGridViewTextBoxColumn4.DefaultCellStyle = dataGridViewCellStyle5;
         this.dataGridViewTextBoxColumn4.HeaderText = "Количество";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         this.dataGridViewTextBoxColumn4.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn5
         // 
         this.dataGridViewTextBoxColumn5.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn5.DataPropertyName = "Agent";
         dataGridViewCellStyle6.Format = "N0";
         dataGridViewCellStyle6.NullValue = null;
         this.dataGridViewTextBoxColumn5.DefaultCellStyle = dataGridViewCellStyle6;
         this.dataGridViewTextBoxColumn5.HeaderText = "Агент";
         this.dataGridViewTextBoxColumn5.Name = "dataGridViewTextBoxColumn5";
         this.dataGridViewTextBoxColumn5.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn6
         // 
         this.dataGridViewTextBoxColumn6.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn6.DataPropertyName = "Qty";
         dataGridViewCellStyle7.Format = "N0";
         dataGridViewCellStyle7.NullValue = null;
         this.dataGridViewTextBoxColumn6.DefaultCellStyle = dataGridViewCellStyle7;
         this.dataGridViewTextBoxColumn6.HeaderText = "Количесто";
         this.dataGridViewTextBoxColumn6.Name = "dataGridViewTextBoxColumn6";
         this.dataGridViewTextBoxColumn6.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn7
         // 
         this.dataGridViewTextBoxColumn7.DataPropertyName = "Qty";
         dataGridViewCellStyle8.Format = "N0";
         dataGridViewCellStyle8.NullValue = null;
         this.dataGridViewTextBoxColumn7.DefaultCellStyle = dataGridViewCellStyle8;
         this.dataGridViewTextBoxColumn7.HeaderText = "Количесто";
         this.dataGridViewTextBoxColumn7.Name = "dataGridViewTextBoxColumn7";
         this.dataGridViewTextBoxColumn7.ReadOnly = true;
         // 
         // SVPlanCommit
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(1209, 646);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.Name = "SVPlanCommit";
         this.Text = "SVPlanCommit";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvPlans)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvAgents)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ToolStripButton tsbSave;
      private System.Windows.Forms.DataGridView dgvPlans;
      private System.Windows.Forms.DataGridView dgvAgents;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAgent;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAgentQty;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnItem;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnState;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnFactory;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnDate;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnPlanQty;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn5;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn6;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn7;
   }
}