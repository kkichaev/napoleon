namespace GRSoft.NapoleonManager
{
   partial class OrgMatrix
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(OrgMatrix));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tbAgents = new System.Windows.Forms.ToolStripComboBox();
         this.tbSave = new System.Windows.Forms.ToolStripButton();
         this.tbAdd = new System.Windows.Forms.ToolStripButton();
         this.tbDel = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnCopy = new System.Windows.Forms.ToolStripButton();
         this.btnPast = new System.Windows.Forms.ToolStripButton();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvOrgs = new System.Windows.Forms.DataGridView();
         this.clmnOrg = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvMatrix = new System.Windows.Forms.DataGridView();
         this.clmnPrice = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvMatrix)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tbAgents,
            this.tbSave,
            this.tbAdd,
            this.tbDel,
            this.toolStripSeparator1,
            this.btnCopy,
            this.btnPast});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(526, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tbAgents
         // 
         this.tbAgents.Name = "tbAgents";
         this.tbAgents.Size = new System.Drawing.Size(121, 25);
         this.tbAgents.Sorted = true;
         this.tbAgents.ToolTipText = "Агенты";
         this.tbAgents.SelectedIndexChanged += new System.EventHandler(this.tbAgents_SelectedIndexChanged);
         // 
         // tbSave
         // 
         this.tbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbSave.Enabled = false;
         this.tbSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.tbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbSave.Name = "tbSave";
         this.tbSave.Size = new System.Drawing.Size(23, 22);
         this.tbSave.Text = "Сохранить";
         this.tbSave.Click += new System.EventHandler(this.tbSave_Click);
         // 
         // tbAdd
         // 
         this.tbAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbAdd.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.tbAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbAdd.Name = "tbAdd";
         this.tbAdd.Size = new System.Drawing.Size(23, 22);
         this.tbAdd.Text = "Добавить товар";
         this.tbAdd.Click += new System.EventHandler(this.tbAdd_Click);
         // 
         // tbDel
         // 
         this.tbDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbDel.Image = ((System.Drawing.Image)(resources.GetObject("tbDel.Image")));
         this.tbDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbDel.Name = "tbDel";
         this.tbDel.Size = new System.Drawing.Size(23, 22);
         this.tbDel.Text = "Удалить товар";
         this.tbDel.Click += new System.EventHandler(this.tbDel_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // btnCopy
         // 
         this.btnCopy.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnCopy.Image = global::GRSoft.NapoleonManager.Properties.Resources.copy;
         this.btnCopy.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnCopy.Name = "btnCopy";
         this.btnCopy.Size = new System.Drawing.Size(23, 22);
         this.btnCopy.Text = "Копировать";
         this.btnCopy.Click += new System.EventHandler(this.btnCopy_Click);
         // 
         // btnPast
         // 
         this.btnPast.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnPast.Image = global::GRSoft.NapoleonManager.Properties.Resources.paste;
         this.btnPast.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnPast.Name = "btnPast";
         this.btnPast.Size = new System.Drawing.Size(23, 22);
         this.btnPast.Text = "Вставить";
         this.btnPast.Click += new System.EventHandler(this.btnPast_Click);
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
         this.splitContainer1.Panel2.Controls.Add(this.dgvMatrix);
         this.splitContainer1.Size = new System.Drawing.Size(526, 379);
         this.splitContainer1.SplitterDistance = 175;
         this.splitContainer1.TabIndex = 1;
         // 
         // dgvOrgs
         // 
         this.dgvOrgs.AllowUserToAddRows = false;
         this.dgvOrgs.AllowUserToDeleteRows = false;
         this.dgvOrgs.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrgs.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnOrg});
         this.dgvOrgs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrgs.Location = new System.Drawing.Point(0, 0);
         this.dgvOrgs.Name = "dgvOrgs";
         this.dgvOrgs.RowHeadersVisible = false;
         this.dgvOrgs.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvOrgs.Size = new System.Drawing.Size(175, 379);
         this.dgvOrgs.TabIndex = 0;
         this.dgvOrgs.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvOrgs_RowEnter);
         // 
         // clmnOrg
         // 
         this.clmnOrg.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnOrg.DataPropertyName = "Name";
         this.clmnOrg.HeaderText = "Контрагент";
         this.clmnOrg.Name = "clmnOrg";
         // 
         // dgvMatrix
         // 
         this.dgvMatrix.AllowDrop = true;
         this.dgvMatrix.AllowUserToAddRows = false;
         this.dgvMatrix.AllowUserToDeleteRows = false;
         this.dgvMatrix.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvMatrix.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnPrice});
         this.dgvMatrix.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvMatrix.Location = new System.Drawing.Point(0, 0);
         this.dgvMatrix.Name = "dgvMatrix";
         this.dgvMatrix.RowHeadersVisible = false;
         this.dgvMatrix.Size = new System.Drawing.Size(347, 379);
         this.dgvMatrix.TabIndex = 0;
         this.dgvMatrix.DragDrop += new System.Windows.Forms.DragEventHandler(this.dgvMatrix_DragDrop);
         this.dgvMatrix.DragEnter += new System.Windows.Forms.DragEventHandler(this.dgvMatrix_DragEnter);
         // 
         // clmnPrice
         // 
         this.clmnPrice.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnPrice.DataPropertyName = "Name";
         this.clmnPrice.HeaderText = "Товар";
         this.clmnPrice.Name = "clmnPrice";
         // 
         // OrgMatrix
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(526, 404);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "OrgMatrix";
         this.Text = "Матрицы контрагентов";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvMatrix)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripComboBox tbAgents;
      private System.Windows.Forms.ToolStripButton tbSave;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.DataGridView dgvOrgs;
      private System.Windows.Forms.DataGridView dgvMatrix;
      private System.Windows.Forms.ToolStripButton tbAdd;
      private System.Windows.Forms.ToolStripButton tbDel;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnOrg;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnPrice;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripButton btnCopy;
      private System.Windows.Forms.ToolStripButton btnPast;
   }
}