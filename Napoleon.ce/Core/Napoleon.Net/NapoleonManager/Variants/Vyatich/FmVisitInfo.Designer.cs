namespace GRSoft.NapoleonManager
{
   partial class FmVisitInfo
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmVisitInfo));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.cbAgents = new System.Windows.Forms.ToolStripComboBox();
         this.toolStripSeparator3 = new System.Windows.Forms.ToolStripSeparator();
         this.tbOrg = new System.Windows.Forms.ToolStripTextBox();
         this.btnClear = new System.Windows.Forms.ToolStripButton();
         this.cbAction = new System.Windows.Forms.CheckBox();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.grid = new System.Windows.Forms.DataGridView();
         this.list = new System.Windows.Forms.ListView();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.period = new GRSoft.NapoleonManager.DatePeriodView();
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.toolStripSeparator1,
            this.toolStripSeparator2,
            this.cbAgents,
            this.toolStripSeparator3,
            this.tbOrg,
            this.btnClear});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(844, 25);
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
         this.btnRefresh.Text = "btnRefresh";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Margin = new System.Windows.Forms.Padding(380, 0, 0, 0);
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Margin = new System.Windows.Forms.Padding(70, 0, 0, 0);
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // cbAgents
         // 
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(141, 25);
         this.cbAgents.SelectedIndexChanged += new System.EventHandler(this.cbAgents_SelectedIndexChanged);
         // 
         // toolStripSeparator3
         // 
         this.toolStripSeparator3.Name = "toolStripSeparator3";
         this.toolStripSeparator3.Size = new System.Drawing.Size(6, 25);
         // 
         // tbOrg
         // 
         this.tbOrg.Name = "tbOrg";
         this.tbOrg.Size = new System.Drawing.Size(150, 25);
         this.tbOrg.TextChanged += new System.EventHandler(this.tbOrg_TextChanged);
         // 
         // btnClear
         // 
         this.btnClear.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnClear.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.btnClear.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnClear.Name = "btnClear";
         this.btnClear.Size = new System.Drawing.Size(23, 22);
         this.btnClear.Text = "Очистить";
         this.btnClear.Click += new System.EventHandler(this.btnClear_Click);
         // 
         // cbAction
         // 
         this.cbAction.AutoSize = true;
         this.cbAction.Location = new System.Drawing.Point(424, 3);
         this.cbAction.Name = "cbAction";
         this.cbAction.Size = new System.Drawing.Size(57, 18);
         this.cbAction.TabIndex = 2;
         this.cbAction.Text = "Акция";
         this.cbAction.UseVisualStyleBackColor = true;
         this.cbAction.CheckedChanged += new System.EventHandler(this.cbAction_CheckedChanged);
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.grid);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.list);
         this.splitContainer1.Size = new System.Drawing.Size(844, 472);
         this.splitContainer1.SplitterDistance = 382;
         this.splitContainer1.TabIndex = 3;
         // 
         // grid
         // 
         this.grid.AllowUserToAddRows = false;
         this.grid.AllowUserToDeleteRows = false;
         this.grid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.grid.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1,
            this.Column2,
            this.Column3,
            this.Column4});
         this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid.Location = new System.Drawing.Point(0, 0);
         this.grid.Name = "grid";
         this.grid.ReadOnly = true;
         this.grid.RowHeadersVisible = false;
         this.grid.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.grid.Size = new System.Drawing.Size(382, 472);
         this.grid.TabIndex = 0;
         this.grid.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.grid_CellFormatting);
         this.grid.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.grid_RowEnter);
         // 
         // list
         // 
         this.list.Dock = System.Windows.Forms.DockStyle.Fill;
         this.list.Location = new System.Drawing.Point(0, 0);
         this.list.Name = "list";
         this.list.Size = new System.Drawing.Size(458, 472);
         this.list.TabIndex = 0;
         this.list.UseCompatibleStateImageBehavior = false;
         this.list.MouseDoubleClick += new System.Windows.Forms.MouseEventHandler(this.list_MouseDoubleClick);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "AgentName";
         this.dataGridViewTextBoxColumn1.HeaderText = "Агент";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "OrgName";
         this.dataGridViewTextBoxColumn2.HeaderText = "Клиент";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.DataPropertyName = "Created";
         this.dataGridViewTextBoxColumn3.HeaderText = "Дата";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         this.dataGridViewTextBoxColumn3.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn4.DataPropertyName = "Sended";
         this.dataGridViewTextBoxColumn4.HeaderText = "Отправлен";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         this.dataGridViewTextBoxColumn4.ReadOnly = true;
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DataPropertyName = "AgentName";
         this.Column1.HeaderText = "Агент";
         this.Column1.Name = "Column1";
         this.Column1.ReadOnly = true;
         // 
         // Column2
         // 
         this.Column2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column2.DataPropertyName = "OrgName";
         this.Column2.HeaderText = "Клиент";
         this.Column2.Name = "Column2";
         this.Column2.ReadOnly = true;
         // 
         // Column3
         // 
         this.Column3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column3.DataPropertyName = "Created";
         this.Column3.HeaderText = "Дата";
         this.Column3.Name = "Column3";
         this.Column3.ReadOnly = true;
         // 
         // Column4
         // 
         this.Column4.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column4.DataPropertyName = "Sended";
         this.Column4.HeaderText = "Отправлен";
         this.Column4.Name = "Column4";
         this.Column4.ReadOnly = true;
         // 
         // period
         // 
         this.period.Finish = new System.DateTime(2015, 4, 13, 0, 0, 0, 0);
         this.period.Location = new System.Drawing.Point(38, -3);
         this.period.Name = "period";
         this.period.Size = new System.Drawing.Size(367, 27);
         this.period.Start = new System.DateTime(2015, 4, 13, 0, 0, 0, 0);
         this.period.TabIndex = 1;
         // 
         // FmVisitInfo
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(844, 497);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.cbAction);
         this.Controls.Add(this.period);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmVisitInfo";
         this.Text = "Посещения";
         this.Load += new System.EventHandler(this.FmVisitInfo_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.grid)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private GRSoft.NapoleonManager.DatePeriodView period;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.CheckBox cbAction;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripComboBox cbAgents;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
      private System.Windows.Forms.ToolStripTextBox tbOrg;
      private System.Windows.Forms.ToolStripButton btnClear;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.DataGridView grid;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      private System.Windows.Forms.ListView list;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column3;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column4;
   }
}