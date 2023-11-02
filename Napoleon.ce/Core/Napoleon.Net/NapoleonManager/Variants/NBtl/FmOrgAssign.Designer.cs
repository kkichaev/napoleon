namespace GRSoft.NapoleonManager
{
   partial class FmOrgAssign
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmOrgAssign));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvOrg = new System.Windows.Forms.DataGridView();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.edFilter = new System.Windows.Forms.ToolStripTextBox();
         this.btnClearFilter = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.btnOrg = new System.Windows.Forms.ToolStripButton();
         this.lbAgentOrgs = new System.Windows.Forms.ListBox();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.cbAgents = new System.Windows.Forms.ToolStripComboBox();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).BeginInit();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrg)).BeginInit();
         this.toolStrip1.SuspendLayout();
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
         this.splitContainer1.Panel1.Controls.Add(this.dgvOrg);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.lbAgentOrgs);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer1.Size = new System.Drawing.Size(1033, 565);
         this.splitContainer1.SplitterDistance = 511;
         this.splitContainer1.TabIndex = 0;
         // 
         // dgvOrg
         // 
         this.dgvOrg.AllowUserToAddRows = false;
         this.dgvOrg.AllowUserToDeleteRows = false;
         this.dgvOrg.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrg.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1});
         this.dgvOrg.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrg.Location = new System.Drawing.Point(0, 39);
         this.dgvOrg.Name = "dgvOrg";
         this.dgvOrg.RowHeadersVisible = false;
         this.dgvOrg.Size = new System.Drawing.Size(511, 526);
         this.dgvOrg.TabIndex = 1;
         this.dgvOrg.CellDoubleClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvOrg_CellDoubleClick);
         this.dgvOrg.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvOrg_CellFormatting);
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DataPropertyName = "Name";
         this.Column1.HeaderText = "Организация";
         this.Column1.Name = "Column1";
         // 
         // toolStrip1
         // 
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.btnSave,
            this.toolStripSeparator1,
            this.toolStripLabel1,
            this.edFilter,
            this.btnClearFilter,
            this.toolStripSeparator2,
            this.btnOrg});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(511, 39);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(36, 36);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(36, 36);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 39);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(63, 36);
         this.toolStripLabel1.Text = "Фильтр";
         // 
         // edFilter
         // 
         this.edFilter.Name = "edFilter";
         this.edFilter.Size = new System.Drawing.Size(200, 39);
         this.edFilter.TextChanged += new System.EventHandler(this.edFilter_TextChanged);
         // 
         // btnClearFilter
         // 
         this.btnClearFilter.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnClearFilter.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.btnClearFilter.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnClearFilter.Name = "btnClearFilter";
         this.btnClearFilter.Size = new System.Drawing.Size(36, 36);
         this.btnClearFilter.Text = "Очистить фильтр";
         this.btnClearFilter.Click += new System.EventHandler(this.btnClearFilter_Click);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 39);
         // 
         // btnOrg
         // 
         this.btnOrg.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnOrg.Image = global::GRSoft.NapoleonManager.Properties.Resources.ic_grid_on;
         this.btnOrg.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnOrg.Name = "btnOrg";
         this.btnOrg.Size = new System.Drawing.Size(36, 36);
         this.btnOrg.Text = "Организации";
         this.btnOrg.Click += new System.EventHandler(this.btnOrg_Click);
         // 
         // lbAgentOrgs
         // 
         this.lbAgentOrgs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lbAgentOrgs.FormattingEnabled = true;
         this.lbAgentOrgs.ItemHeight = 14;
         this.lbAgentOrgs.Location = new System.Drawing.Point(0, 29);
         this.lbAgentOrgs.Name = "lbAgentOrgs";
         this.lbAgentOrgs.Size = new System.Drawing.Size(518, 536);
         this.lbAgentOrgs.TabIndex = 1;
         this.lbAgentOrgs.DoubleClick += new System.EventHandler(this.lbAgentOrgs_DoubleClick);
         // 
         // toolStrip2
         // 
         this.toolStrip2.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.cbAgents});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(518, 29);
         this.toolStrip2.TabIndex = 0;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // cbAgents
         // 
         this.cbAgents.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(200, 29);
         this.cbAgents.SelectedIndexChanged += new System.EventHandler(this.cbAgents_SelectedIndexChanged);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Организация";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // FmOrgAssign
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(1033, 565);
         this.Controls.Add(this.splitContainer1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmOrgAssign";
         this.Text = "Назначение точек";
         this.Load += new System.EventHandler(this.FmOrgAssign_Load);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).EndInit();
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrg)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripTextBox edFilter;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripComboBox cbAgents;
      private System.Windows.Forms.DataGridView dgvOrg;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.ListBox lbAgentOrgs;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.ToolStripButton btnClearFilter;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripButton btnOrg;
   }
}