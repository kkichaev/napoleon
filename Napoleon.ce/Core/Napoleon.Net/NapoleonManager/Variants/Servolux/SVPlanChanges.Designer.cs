namespace GRSoft.NapoleonManager
{
   partial class SVPlanChanges
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
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle4 = new System.Windows.Forms.DataGridViewCellStyle();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbRefresh = new System.Windows.Forms.ToolStripButton();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.tsFirms = new System.Windows.Forms.ToolStripComboBox();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.dgvPrice = new System.Windows.Forms.DataGridView();
         this.clmnPriceItem = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnState = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvAgents = new System.Windows.Forms.DataGridView();
         this.clmnAgent = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnPlan = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.tsDivisionFilter = new System.Windows.Forms.ToolStripComboBox();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnItem = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnSVState = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip3 = new System.Windows.Forms.ToolStrip();
         this.tsDivisions = new System.Windows.Forms.ToolStripComboBox();
         this.dtWorkDate = new System.Windows.Forms.DateTimePicker();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvPrice)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvAgents)).BeginInit();
         this.toolStrip2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.toolStrip3.SuspendLayout();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbRefresh,
            this.tsbSave,
            this.tsFirms});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(1377, 39);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsbRefresh
         // 
         this.tsbRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.tsbRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbRefresh.Name = "tsbRefresh";
         this.tsbRefresh.Size = new System.Drawing.Size(24, 25);
         this.tsbRefresh.Text = "Обновить";
         this.tsbRefresh.ToolTipText = "Обновить";
         this.tsbRefresh.Click += new System.EventHandler(this.tsbRefresh_Click);
         // 
         // tsbSave
         // 
         this.tsbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSave.Enabled = false;
         this.tsbSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.tsbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSave.Margin = new System.Windows.Forms.Padding(2, 1, 0, 2);
         this.tsbSave.Name = "tsbSave";
         this.tsbSave.Size = new System.Drawing.Size(24, 25);
         this.tsbSave.Text = "Сохранить";
         this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click);
         // 
         // tsFirms
         // 
         this.tsFirms.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsFirms.Name = "tsFirms";
         this.tsFirms.Size = new System.Drawing.Size(265, 39);
         this.tsFirms.ToolTipText = "Выбор фабрики";
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
         this.splitContainer1.Panel1.Controls.Add(this.splitContainer2);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip2);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvItems);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip3);
         this.splitContainer1.Size = new System.Drawing.Size(1377, 775);
         this.splitContainer1.SplitterDistance = 758;
         this.splitContainer1.SplitterWidth = 5;
         this.splitContainer1.TabIndex = 1;
         // 
         // splitContainer2
         // 
         this.splitContainer2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer2.Location = new System.Drawing.Point(0, 28);
         this.splitContainer2.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.splitContainer2.Name = "splitContainer2";
         this.splitContainer2.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer2.Panel1
         // 
         this.splitContainer2.Panel1.Controls.Add(this.dgvPrice);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.dgvAgents);
         this.splitContainer2.Size = new System.Drawing.Size(758, 747);
         this.splitContainer2.SplitterDistance = 265;
         this.splitContainer2.SplitterWidth = 5;
         this.splitContainer2.TabIndex = 2;
         // 
         // dgvPrice
         // 
         this.dgvPrice.AllowUserToAddRows = false;
         this.dgvPrice.AllowUserToDeleteRows = false;
         this.dgvPrice.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvPrice.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnPriceItem,
            this.clmnState});
         this.dgvPrice.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvPrice.Location = new System.Drawing.Point(0, 0);
         this.dgvPrice.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.dgvPrice.Name = "dgvPrice";
         this.dgvPrice.ReadOnly = true;
         this.dgvPrice.RowHeadersVisible = false;
         this.dgvPrice.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvPrice.Size = new System.Drawing.Size(758, 265);
         this.dgvPrice.TabIndex = 0;
         this.dgvPrice.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvPrice_CellFormatting);
         this.dgvPrice.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvPrice_RowEnter);
         // 
         // clmnPriceItem
         // 
         this.clmnPriceItem.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnPriceItem.DataPropertyName = "Name";
         this.clmnPriceItem.HeaderText = "Товар";
         this.clmnPriceItem.Name = "clmnPriceItem";
         this.clmnPriceItem.ReadOnly = true;
         // 
         // clmnState
         // 
         this.clmnState.DataPropertyName = "State";
         this.clmnState.HeaderText = "Состояние";
         this.clmnState.Name = "clmnState";
         this.clmnState.ReadOnly = true;
         this.clmnState.Width = 200;
         // 
         // dgvAgents
         // 
         this.dgvAgents.AllowUserToAddRows = false;
         this.dgvAgents.AllowUserToDeleteRows = false;
         this.dgvAgents.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvAgents.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnAgent,
            this.clmnPlan});
         this.dgvAgents.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvAgents.Location = new System.Drawing.Point(0, 0);
         this.dgvAgents.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.dgvAgents.Name = "dgvAgents";
         this.dgvAgents.ReadOnly = true;
         this.dgvAgents.RowHeadersVisible = false;
         this.dgvAgents.Size = new System.Drawing.Size(758, 477);
         this.dgvAgents.TabIndex = 1;
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
         // clmnPlan
         // 
         this.clmnPlan.DataPropertyName = "Plan";
         this.clmnPlan.HeaderText = "Лимит/Отгрузка";
         this.clmnPlan.Name = "clmnPlan";
         this.clmnPlan.ReadOnly = true;
         this.clmnPlan.Width = 200;
         // 
         // toolStrip2
         // 
         this.toolStrip2.ImageScalingSize = new System.Drawing.Size(20, 20);
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsDivisionFilter});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(758, 28);
         this.toolStrip2.TabIndex = 0;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // tsDivisionFilter
         // 
         this.tsDivisionFilter.Name = "tsDivisionFilter";
         this.tsDivisionFilter.Size = new System.Drawing.Size(265, 28);
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnItem,
            this.clmnSVState,
            this.clmnQty});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 28);
         this.dgvItems.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.ReadOnly = true;
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvItems.Size = new System.Drawing.Size(614, 747);
         this.dgvItems.TabIndex = 0;
         this.dgvItems.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvItems_CellFormatting);
         // 
         // clmnItem
         // 
         this.clmnItem.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnItem.DataPropertyName = "Name";
         this.clmnItem.HeaderText = "Товар";
         this.clmnItem.Name = "clmnItem";
         this.clmnItem.ReadOnly = true;
         // 
         // clmnSVState
         // 
         this.clmnSVState.DataPropertyName = "State";
         this.clmnSVState.HeaderText = "Состояние";
         this.clmnSVState.Name = "clmnSVState";
         this.clmnSVState.ReadOnly = true;
         // 
         // clmnQty
         // 
         this.clmnQty.DataPropertyName = "Qty";
         dataGridViewCellStyle4.Format = "N0";
         dataGridViewCellStyle4.NullValue = null;
         this.clmnQty.DefaultCellStyle = dataGridViewCellStyle4;
         this.clmnQty.HeaderText = "Количество";
         this.clmnQty.Name = "clmnQty";
         this.clmnQty.ReadOnly = true;
         // 
         // toolStrip3
         // 
         this.toolStrip3.ImageScalingSize = new System.Drawing.Size(20, 20);
         this.toolStrip3.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsDivisions});
         this.toolStrip3.Location = new System.Drawing.Point(0, 0);
         this.toolStrip3.Name = "toolStrip3";
         this.toolStrip3.Size = new System.Drawing.Size(614, 28);
         this.toolStrip3.TabIndex = 1;
         this.toolStrip3.Text = "toolStrip3";
         // 
         // tsDivisions
         // 
         this.tsDivisions.Margin = new System.Windows.Forms.Padding(5, 0, 1, 0);
         this.tsDivisions.Name = "tsDivisions";
         this.tsDivisions.Size = new System.Drawing.Size(265, 28);
         this.tsDivisions.ToolTipText = "Выбор супервайзера";
         // 
         // dtWorkDate
         // 
         this.dtWorkDate.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.dtWorkDate.Format = System.Windows.Forms.DateTimePickerFormat.Short;
         this.dtWorkDate.Location = new System.Drawing.Point(356, 5);
         this.dtWorkDate.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.dtWorkDate.Name = "dtWorkDate";
         this.dtWorkDate.Size = new System.Drawing.Size(153, 30);
         this.dtWorkDate.TabIndex = 4;
         this.dtWorkDate.ValueChanged += new System.EventHandler(this.dtWorkDate_ValueChanged);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Agent";
         this.dataGridViewTextBoxColumn1.FillWeight = 200F;
         this.dataGridViewTextBoxColumn1.HeaderText = "Агент";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Plan";
         this.dataGridViewTextBoxColumn2.HeaderText = "Лимит/Отгрузка";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.DataPropertyName = "Item";
         this.dataGridViewTextBoxColumn3.FillWeight = 200F;
         this.dataGridViewTextBoxColumn3.HeaderText = "Товар";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         this.dataGridViewTextBoxColumn3.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.DataPropertyName = "Qty";
         this.dataGridViewTextBoxColumn4.HeaderText = "Количество";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         this.dataGridViewTextBoxColumn4.ReadOnly = true;
         // 
         // SVPlanChanges
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(1377, 814);
         this.Controls.Add(this.dtWorkDate);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.toolStrip1);
         this.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.Name = "SVPlanChanges";
         this.Text = "Передача планов супервайзеру";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvPrice)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvAgents)).EndInit();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.toolStrip3.ResumeLayout(false);
         this.toolStrip3.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton tsbRefresh;
      private System.Windows.Forms.ToolStripComboBox tsFirms;
      private System.Windows.Forms.ToolStripButton tsbSave;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.DataGridView dgvAgents;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripComboBox tsDivisionFilter;
      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.DateTimePicker dtWorkDate;
      private System.Windows.Forms.ToolStrip toolStrip3;
      private System.Windows.Forms.ToolStripComboBox tsDivisions;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private System.Windows.Forms.DataGridView dgvPrice;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAgent;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnPlan;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnPriceItem;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnState;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnItem;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnSVState;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnQty;
   }
}