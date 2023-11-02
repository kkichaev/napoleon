namespace GRSoft.NapoleonManager
{
   partial class OffTakeCoefEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(OffTakeCoefEdit));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvAgents = new System.Windows.Forms.DataGridView();
         this.clmnAgent = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnAgentCoef = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.dgvFolders = new System.Windows.Forms.DataGridView();
         this.clmnFolder = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnFolderCoef = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnNew = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvAgents)).BeginInit();
         this.toolStrip2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvFolders)).BeginInit();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 0);
         this.splitContainer1.Name = "splitContainer1";
         this.splitContainer1.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.dgvAgents);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip2);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvFolders);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip1);
         this.splitContainer1.Size = new System.Drawing.Size(440, 414);
         this.splitContainer1.SplitterDistance = 172;
         this.splitContainer1.TabIndex = 0;
         // 
         // dgvAgents
         // 
         this.dgvAgents.AllowUserToAddRows = false;
         this.dgvAgents.AllowUserToDeleteRows = false;
         this.dgvAgents.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvAgents.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnAgent,
            this.clmnAgentCoef});
         this.dgvAgents.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvAgents.Location = new System.Drawing.Point(0, 25);
         this.dgvAgents.Name = "dgvAgents";
         this.dgvAgents.RowHeadersVisible = false;
         this.dgvAgents.Size = new System.Drawing.Size(440, 147);
         this.dgvAgents.TabIndex = 0;
         // 
         // clmnAgent
         // 
         this.clmnAgent.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnAgent.DataPropertyName = "Agent";
         this.clmnAgent.HeaderText = "Агент";
         this.clmnAgent.Name = "clmnAgent";
         // 
         // clmnAgentCoef
         // 
         this.clmnAgentCoef.DataPropertyName = "Coef";
         this.clmnAgentCoef.HeaderText = "Коэфф.";
         this.clmnAgentCoef.Name = "clmnAgentCoef";
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnSave});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(440, 25);
         this.toolStrip2.TabIndex = 1;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // btnSave
         // 
         this.btnSave.Enabled = false;
         this.btnSave.Image = ((System.Drawing.Image)(resources.GetObject("btnSave.Image")));
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(148, 22);
         this.btnSave.Text = "Сохранить изменения";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // dgvFolders
         // 
         this.dgvFolders.AllowUserToAddRows = false;
         this.dgvFolders.AllowUserToDeleteRows = false;
         this.dgvFolders.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvFolders.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnFolder,
            this.clmnFolderCoef});
         this.dgvFolders.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvFolders.Location = new System.Drawing.Point(0, 25);
         this.dgvFolders.Name = "dgvFolders";
         this.dgvFolders.RowHeadersVisible = false;
         this.dgvFolders.Size = new System.Drawing.Size(440, 213);
         this.dgvFolders.TabIndex = 0;
         // 
         // clmnFolder
         // 
         this.clmnFolder.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnFolder.DataPropertyName = "Folder";
         this.clmnFolder.HeaderText = "Папка";
         this.clmnFolder.Name = "clmnFolder";
         // 
         // clmnFolderCoef
         // 
         this.clmnFolderCoef.DataPropertyName = "Coef";
         this.clmnFolderCoef.HeaderText = "Коэф.";
         this.clmnFolderCoef.Name = "clmnFolderCoef";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnNew,
            this.btnDel});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(440, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnNew
         // 
         this.btnNew.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnNew.Image = ((System.Drawing.Image)(resources.GetObject("btnNew.Image")));
         this.btnNew.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnNew.Name = "btnNew";
         this.btnNew.Size = new System.Drawing.Size(23, 22);
         this.btnNew.Text = "Добавить папку";
         this.btnNew.Click += new System.EventHandler(this.btnNew_Click);
         // 
         // btnDel
         // 
         this.btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDel.Image = ((System.Drawing.Image)(resources.GetObject("btnDel.Image")));
         this.btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(23, 22);
         this.btnDel.Text = "Удалить папаку";
         this.btnDel.Click += new System.EventHandler(this.btnDel_Click);
         // 
         // OffTakeCoefEdit
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(440, 414);
         this.Controls.Add(this.splitContainer1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "OffTakeCoefEdit";
         this.Text = "Коэффициент продаж";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.OffTakeCoefEdit_FormClosing);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvAgents)).EndInit();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvFolders)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.DataGridView dgvAgents;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAgent;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAgentCoef;
      private System.Windows.Forms.DataGridView dgvFolders;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnFolder;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnFolderCoef;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnNew;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripButton btnSave;

   }
}