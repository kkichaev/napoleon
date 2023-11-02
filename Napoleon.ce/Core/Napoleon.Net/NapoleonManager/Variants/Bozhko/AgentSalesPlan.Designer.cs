namespace GRSoft.NapoleonManager
{
   partial class AgentSalesPlan
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(AgentSalesPlan));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.dgvAgents = new System.Windows.Forms.DataGridView();
         this.clmnAgent = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.label2 = new System.Windows.Forms.Label();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.label1 = new System.Windows.Forms.Label();
         this.dgvPlans = new System.Windows.Forms.DataGridView();
         this.clmnPlan = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnDateStart = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnDateEnd = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnDelPlan = new System.Windows.Forms.ToolStripButton();
         this.tbAddPlan = new System.Windows.Forms.ToolStripButton();
         this.btnEdit = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnItemName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnPlanQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.btnDelItem = new System.Windows.Forms.ToolStripButton();
         this.btnAddItem = new System.Windows.Forms.ToolStripButton();
         this.btnSavePlan = new System.Windows.Forms.ToolStripButton();
         this.btnReport = new System.Windows.Forms.ToolStripButton();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn6 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvAgents)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvPlans)).BeginInit();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.toolStrip2.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 0);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.splitContainer2);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvItems);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer1.Size = new System.Drawing.Size(778, 402);
         this.splitContainer1.SplitterDistance = 457;
         this.splitContainer1.TabIndex = 0;
         // 
         // splitContainer2
         // 
         this.splitContainer2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer2.Location = new System.Drawing.Point(0, 0);
         this.splitContainer2.Name = "splitContainer2";
         this.splitContainer2.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer2.Panel1
         // 
         this.splitContainer2.Panel1.Controls.Add(this.dgvAgents);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.dtpEnd);
         this.splitContainer2.Panel2.Controls.Add(this.label2);
         this.splitContainer2.Panel2.Controls.Add(this.dtpStart);
         this.splitContainer2.Panel2.Controls.Add(this.label1);
         this.splitContainer2.Panel2.Controls.Add(this.dgvPlans);
         this.splitContainer2.Panel2.Controls.Add(this.toolStrip1);
         this.splitContainer2.Size = new System.Drawing.Size(457, 402);
         this.splitContainer2.SplitterDistance = 167;
         this.splitContainer2.TabIndex = 0;
         // 
         // dgvAgents
         // 
         this.dgvAgents.AllowUserToAddRows = false;
         this.dgvAgents.AllowUserToDeleteRows = false;
         this.dgvAgents.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvAgents.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnAgent});
         this.dgvAgents.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvAgents.Location = new System.Drawing.Point(0, 0);
         this.dgvAgents.Name = "dgvAgents";
         this.dgvAgents.ReadOnly = true;
         this.dgvAgents.RowHeadersVisible = false;
         this.dgvAgents.Size = new System.Drawing.Size(457, 167);
         this.dgvAgents.TabIndex = 0;
         this.dgvAgents.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvAgents_RowEnter);
         // 
         // clmnAgent
         // 
         this.clmnAgent.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnAgent.DataPropertyName = "Name";
         this.clmnAgent.HeaderText = "Агент";
         this.clmnAgent.Name = "clmnAgent";
         this.clmnAgent.ReadOnly = true;
         // 
         // dtpEnd
         // 
         this.dtpEnd.Format = System.Windows.Forms.DateTimePickerFormat.Short;
         this.dtpEnd.Location = new System.Drawing.Point(226, 2);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(97, 20);
         this.dtpEnd.TabIndex = 7;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(203, 5);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(19, 13);
         this.label2.TabIndex = 6;
         this.label2.Text = "по";
         // 
         // dtpStart
         // 
         this.dtpStart.Format = System.Windows.Forms.DateTimePickerFormat.Short;
         this.dtpStart.Location = new System.Drawing.Point(101, 2);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(97, 20);
         this.dtpStart.TabIndex = 5;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(83, 5);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(13, 13);
         this.label1.TabIndex = 4;
         this.label1.Text = "с";
         // 
         // dgvPlans
         // 
         this.dgvPlans.AllowUserToAddRows = false;
         this.dgvPlans.AllowUserToDeleteRows = false;
         this.dgvPlans.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvPlans.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnPlan,
            this.clmnDateStart,
            this.clmnDateEnd});
         this.dgvPlans.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvPlans.Location = new System.Drawing.Point(0, 25);
         this.dgvPlans.Name = "dgvPlans";
         this.dgvPlans.ReadOnly = true;
         this.dgvPlans.RowHeadersVisible = false;
         this.dgvPlans.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvPlans.Size = new System.Drawing.Size(457, 206);
         this.dgvPlans.TabIndex = 0;
         this.dgvPlans.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvPlans_RowEnter);
         this.dgvPlans.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvPlans_CellFormatting);
         this.dgvPlans.CellMouseDoubleClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.dgvPlans_CellMouseDoubleClick);
         // 
         // clmnPlan
         // 
         this.clmnPlan.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnPlan.DataPropertyName = "Name";
         this.clmnPlan.HeaderText = "План";
         this.clmnPlan.Name = "clmnPlan";
         this.clmnPlan.ReadOnly = true;
         // 
         // clmnDateStart
         // 
         this.clmnDateStart.DataPropertyName = "Start";
         this.clmnDateStart.FillWeight = 50F;
         this.clmnDateStart.HeaderText = "С";
         this.clmnDateStart.Name = "clmnDateStart";
         this.clmnDateStart.ReadOnly = true;
         this.clmnDateStart.Width = 80;
         // 
         // clmnDateEnd
         // 
         this.clmnDateEnd.DataPropertyName = "End";
         this.clmnDateEnd.FillWeight = 50F;
         this.clmnDateEnd.HeaderText = "По";
         this.clmnDateEnd.Name = "clmnDateEnd";
         this.clmnDateEnd.ReadOnly = true;
         this.clmnDateEnd.Width = 80;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnDelPlan,
            this.tbAddPlan,
            this.btnEdit,
            this.btnRefresh});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(457, 25);
         this.toolStrip1.TabIndex = 3;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnDelPlan
         // 
         this.btnDelPlan.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelPlan.Image = global::GRSoft.NapoleonManager.Properties.Resources.del;
         this.btnDelPlan.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelPlan.Name = "btnDelPlan";
         this.btnDelPlan.Size = new System.Drawing.Size(23, 22);
         this.btnDelPlan.Text = "Удалить план";
         this.btnDelPlan.Click += new System.EventHandler(this.btnDelPlan_Click);
         // 
         // tbAddPlan
         // 
         this.tbAddPlan.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbAddPlan.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.tbAddPlan.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbAddPlan.Name = "tbAddPlan";
         this.tbAddPlan.Size = new System.Drawing.Size(23, 22);
         this.tbAddPlan.Text = "Добавить план";
         this.tbAddPlan.Click += new System.EventHandler(this.tbAddPlan_Click);
         // 
         // btnEdit
         // 
         this.btnEdit.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEdit.Image = global::GRSoft.NapoleonManager.Properties.Resources.document_sign;
         this.btnEdit.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEdit.Name = "btnEdit";
         this.btnEdit.Size = new System.Drawing.Size(23, 22);
         this.btnEdit.Text = "Изменить";
         this.btnEdit.Click += new System.EventHandler(this.btnEdit_Click);
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Margin = new System.Windows.Forms.Padding(250, 1, 0, 2);
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnItemName,
            this.clmnPlanQty});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.dgvItems.Location = new System.Drawing.Point(0, 25);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.CellSelect;
         this.dgvItems.Size = new System.Drawing.Size(317, 377);
         this.dgvItems.TabIndex = 0;
         this.dgvItems.CellValueChanged += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvItems_CellValueChanged);
         // 
         // clmnItemName
         // 
         this.clmnItemName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnItemName.DataPropertyName = "Name";
         this.clmnItemName.HeaderText = "Товар";
         this.clmnItemName.Name = "clmnItemName";
         this.clmnItemName.ReadOnly = true;
         // 
         // clmnPlanQty
         // 
         this.clmnPlanQty.DataPropertyName = "Qty";
         this.clmnPlanQty.HeaderText = "Кол-во";
         this.clmnPlanQty.Name = "clmnPlanQty";
         this.clmnPlanQty.Width = 50;
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnDelItem,
            this.btnAddItem,
            this.btnSavePlan,
            this.btnReport});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(317, 25);
         this.toolStrip2.TabIndex = 4;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // btnDelItem
         // 
         this.btnDelItem.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelItem.Image = global::GRSoft.NapoleonManager.Properties.Resources.del;
         this.btnDelItem.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelItem.Name = "btnDelItem";
         this.btnDelItem.Size = new System.Drawing.Size(23, 22);
         this.btnDelItem.Text = "Удалить товар";
         this.btnDelItem.Click += new System.EventHandler(this.btnDelItem_Click);
         // 
         // btnAddItem
         // 
         this.btnAddItem.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddItem.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAddItem.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddItem.Name = "btnAddItem";
         this.btnAddItem.Size = new System.Drawing.Size(23, 22);
         this.btnAddItem.Text = "Добавить товар";
         this.btnAddItem.Click += new System.EventHandler(this.btnAddItem_Click);
         // 
         // btnSavePlan
         // 
         this.btnSavePlan.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSavePlan.Enabled = false;
         this.btnSavePlan.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.btnSavePlan.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSavePlan.Name = "btnSavePlan";
         this.btnSavePlan.Size = new System.Drawing.Size(23, 22);
         this.btnSavePlan.Text = "Сохранить";
         this.btnSavePlan.ToolTipText = "Сохранить план";
         this.btnSavePlan.Click += new System.EventHandler(this.btnSavePlan_Click);
         // 
         // btnReport
         // 
         this.btnReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnReport.Image = global::GRSoft.NapoleonManager.Properties.Resources.excel;
         this.btnReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnReport.Name = "btnReport";
         this.btnReport.Size = new System.Drawing.Size(23, 22);
         this.btnReport.Text = "Отчет по планам";
         this.btnReport.Click += new System.EventHandler(this.btnReport_Click);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Агент";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn2.HeaderText = "План";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.DataPropertyName = "Start";
         this.dataGridViewTextBoxColumn3.FillWeight = 50F;
         this.dataGridViewTextBoxColumn3.HeaderText = "С";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         this.dataGridViewTextBoxColumn3.ReadOnly = true;
         this.dataGridViewTextBoxColumn3.Width = 50;
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.DataPropertyName = "End";
         this.dataGridViewTextBoxColumn4.FillWeight = 50F;
         this.dataGridViewTextBoxColumn4.HeaderText = "По";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         this.dataGridViewTextBoxColumn4.ReadOnly = true;
         this.dataGridViewTextBoxColumn4.Width = 50;
         // 
         // dataGridViewTextBoxColumn5
         // 
         this.dataGridViewTextBoxColumn5.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn5.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn5.HeaderText = "Товар";
         this.dataGridViewTextBoxColumn5.Name = "dataGridViewTextBoxColumn5";
         this.dataGridViewTextBoxColumn5.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn6
         // 
         this.dataGridViewTextBoxColumn6.DataPropertyName = "Qty";
         this.dataGridViewTextBoxColumn6.HeaderText = "Кол-во";
         this.dataGridViewTextBoxColumn6.Name = "dataGridViewTextBoxColumn6";
         this.dataGridViewTextBoxColumn6.ReadOnly = true;
         this.dataGridViewTextBoxColumn6.Width = 50;
         // 
         // AgentSalesPlan
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(778, 402);
         this.Controls.Add(this.splitContainer1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "AgentSalesPlan";
         this.Text = "Планы";
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.Panel2.PerformLayout();
         this.splitContainer2.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvAgents)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvPlans)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private System.Windows.Forms.DataGridView dgvAgents;
      private System.Windows.Forms.DataGridView dgvPlans;
      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnDelPlan;
      protected System.Windows.Forms.ToolStripButton tbAddPlan;
      protected System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripButton btnDelItem;
      protected System.Windows.Forms.ToolStripButton btnAddItem;
      private System.Windows.Forms.ToolStripButton btnSavePlan;
      private System.Windows.Forms.ToolStripButton btnReport;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn5;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn6;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnPlan;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnDateStart;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnDateEnd;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.DateTimePicker dtpStart;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAgent;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnItemName;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnPlanQty;
   }
}