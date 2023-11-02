namespace GRSoft.NapoleonManager
{
   partial class FocusedGroupEditor
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FocusedGroupEditor));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.tbSave = new System.Windows.Forms.ToolStripButton();
         this.cbAgents = new System.Windows.Forms.ComboBox();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvOrgs = new System.Windows.Forms.DataGridView();
         this.OrgName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tvFolders = new System.Windows.Forms.TreeView();
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel1,
            this.toolStripSeparator1,
            this.tbSave});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(583, 25);
         this.toolStrip1.TabIndex = 16;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(38, 22);
         this.toolStripLabel1.Text = "Агент";
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Margin = new System.Windows.Forms.Padding(195, 0, 0, 0);
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // tbSave
         // 
         this.tbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.tbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbSave.Name = "tbSave";
         this.tbSave.Size = new System.Drawing.Size(23, 22);
         this.tbSave.Text = "Сохранить";
         // 
         // cbAgents
         // 
         this.cbAgents.FormattingEnabled = true;
         this.cbAgents.Location = new System.Drawing.Point(48, 1);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(189, 21);
         this.cbAgents.TabIndex = 17;
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.dgvOrgs);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.tvFolders);
         this.splitContainer1.Size = new System.Drawing.Size(583, 365);
         this.splitContainer1.SplitterDistance = 194;
         this.splitContainer1.TabIndex = 18;
         // 
         // dgvOrgs
         // 
         this.dgvOrgs.AllowUserToAddRows = false;
         this.dgvOrgs.AllowUserToDeleteRows = false;
         this.dgvOrgs.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrgs.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.OrgName});
         this.dgvOrgs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrgs.Location = new System.Drawing.Point(0, 0);
         this.dgvOrgs.Name = "dgvOrgs";
         this.dgvOrgs.RowHeadersVisible = false;
         this.dgvOrgs.Size = new System.Drawing.Size(194, 365);
         this.dgvOrgs.TabIndex = 0;
         // 
         // OrgName
         // 
         this.OrgName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.OrgName.DataPropertyName = "OrgName";
         this.OrgName.HeaderText = "Контрагенты";
         this.OrgName.Name = "OrgName";
         // 
         // tvFolders
         // 
         this.tvFolders.CheckBoxes = true;
         this.tvFolders.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvFolders.Location = new System.Drawing.Point(0, 0);
         this.tvFolders.Name = "tvFolders";
         this.tvFolders.Size = new System.Drawing.Size(385, 365);
         this.tvFolders.TabIndex = 0;
         // 
         // FocusedGroupEditor
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(583, 390);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.cbAgents);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FocusedGroupEditor";
         this.Text = "Обязательные группы товара";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FocusedGroupEditor_FormClosing);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ComboBox cbAgents;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.DataGridView dgvOrgs;
      private System.Windows.Forms.TreeView tvFolders;
      private System.Windows.Forms.DataGridViewTextBoxColumn OrgName;
      private System.Windows.Forms.ToolStripButton tbSave;
   }
}