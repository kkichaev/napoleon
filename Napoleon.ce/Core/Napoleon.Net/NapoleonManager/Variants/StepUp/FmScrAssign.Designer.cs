namespace GRSoft.NapoleonManager
{
   partial class FmScrAssign
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmScrAssign));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvScript = new System.Windows.Forms.DataGridView();
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
         this.lbScriptSlsOrgs = new System.Windows.Forms.ListBox();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.cbSls = new System.Windows.Forms.ToolStripComboBox();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvScript)).BeginInit();
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
         this.splitContainer1.Panel1.Controls.Add(this.dgvScript);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.lbScriptSlsOrgs);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer1.Size = new System.Drawing.Size(766, 412);
         this.splitContainer1.SplitterDistance = 379;
         this.splitContainer1.TabIndex = 0;
         // 
         // dgvScript
         // 
         this.dgvScript.AllowUserToAddRows = false;
         this.dgvScript.AllowUserToDeleteRows = false;
         this.dgvScript.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvScript.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1});
         this.dgvScript.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvScript.Location = new System.Drawing.Point(0, 25);
         this.dgvScript.Name = "dgvScript";
         this.dgvScript.RowHeadersVisible = false;
         this.dgvScript.Size = new System.Drawing.Size(379, 387);
         this.dgvScript.TabIndex = 1;
         this.dgvScript.CellDoubleClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvScript_CellDoubleClick);
         this.dgvScript.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvOrg_CellFormatting);
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DataPropertyName = "Name";
         this.Column1.HeaderText = "Сценарий";
         this.Column1.Name = "Column1";
         // 
         // toolStrip1
         // 
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
         this.toolStrip1.Size = new System.Drawing.Size(379, 25);
         this.toolStrip1.TabIndex = 0;
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
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(48, 22);
         this.toolStripLabel1.Text = "Фильтр";
         // 
         // edFilter
         // 
         this.edFilter.Name = "edFilter";
         this.edFilter.Size = new System.Drawing.Size(200, 25);
         this.edFilter.TextChanged += new System.EventHandler(this.edFilter_TextChanged);
         // 
         // btnClearFilter
         // 
         this.btnClearFilter.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnClearFilter.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.btnClearFilter.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnClearFilter.Name = "btnClearFilter";
         this.btnClearFilter.Size = new System.Drawing.Size(23, 22);
         this.btnClearFilter.Text = "Очистить фильтр";
         this.btnClearFilter.Click += new System.EventHandler(this.btnClearFilter_Click);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // btnOrg
         // 
         this.btnOrg.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnOrg.Image = global::GRSoft.NapoleonManager.Properties.Resources.actchk;
         this.btnOrg.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnOrg.Name = "btnOrg";
         this.btnOrg.Size = new System.Drawing.Size(23, 22);
         this.btnOrg.Text = "Организации ";
         this.btnOrg.Click += new System.EventHandler(this.btnOrg_Click);
         // 
         // lbScriptSlsOrgs
         // 
         this.lbScriptSlsOrgs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lbScriptSlsOrgs.FormattingEnabled = true;
         this.lbScriptSlsOrgs.ItemHeight = 14;
         this.lbScriptSlsOrgs.Location = new System.Drawing.Point(0, 25);
         this.lbScriptSlsOrgs.Name = "lbScriptSlsOrgs";
         this.lbScriptSlsOrgs.Size = new System.Drawing.Size(383, 387);
         this.lbScriptSlsOrgs.TabIndex = 1;
         this.lbScriptSlsOrgs.DoubleClick += new System.EventHandler(this.lbScriptSlsOrgs_DoubleClick);
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.cbSls});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(383, 25);
         this.toolStrip2.TabIndex = 0;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // cbSls
         // 
         this.cbSls.Name = "cbSls";
         this.cbSls.Size = new System.Drawing.Size(200, 25);
         this.cbSls.SelectedIndexChanged += new System.EventHandler(this.cbSls_SelectedIndexChanged);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Организация";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // FmScrAssign
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(766, 412);
         this.Controls.Add(this.splitContainer1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmScrAssign";
         this.Text = "Назначение сценариев";
         this.Load += new System.EventHandler(this.FmScrAssign_Load);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvScript)).EndInit();
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
      private System.Windows.Forms.ToolStripComboBox cbSls;
      private System.Windows.Forms.DataGridView dgvScript;
      private System.Windows.Forms.ListBox lbScriptSlsOrgs;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.ToolStripButton btnClearFilter;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripButton btnOrg;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
   }
}