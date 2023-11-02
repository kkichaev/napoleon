namespace GRSoft.NapoleonManager
{
   partial class DistribMatrix
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
         this.components = new System.ComponentModel.Container();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(DistribMatrix));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.cbAgents = new System.Windows.Forms.ToolStripComboBox();
         this.tsbCopy = new System.Windows.Forms.ToolStripButton();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.dgvOrgs = new System.Windows.Forms.DataGridView();
         this.contextMenuStrip1 = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.tsmCopyTo = new System.Windows.Forms.ToolStripMenuItem();
         this.tvOrgMatrix = new GRSoft.UILib.TreeViewMS();
         this.imageList1 = new System.Windows.Forms.ImageList(this.components);
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.tsbRemove = new System.Windows.Forms.ToolStripButton();
         this.tvPrice = new GRSoft.UILib.TreeViewMS();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnOrg = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).BeginInit();
         this.contextMenuStrip1.SuspendLayout();
         this.toolStrip2.SuspendLayout();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel1,
            this.cbAgents,
            this.tsbCopy,
            this.tsbSave});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(809, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(47, 22);
         this.toolStripLabel1.Text = "Агенты";
         // 
         // cbAgents
         // 
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(300, 25);
         this.cbAgents.SelectedIndexChanged += new System.EventHandler(this.cbAgents_SelectedIndexChanged);
         // 
         // tsbCopy
         // 
         this.tsbCopy.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbCopy.Image = global::GRSoft.NapoleonManager.Properties.Resources.copy;
         this.tsbCopy.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbCopy.Name = "tsbCopy";
         this.tsbCopy.Size = new System.Drawing.Size(23, 22);
         this.tsbCopy.Text = "Копировать матрицу";
         this.tsbCopy.Click += new System.EventHandler(this.tsmCopyTo_Click);
         // 
         // tsbSave
         // 
         this.tsbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSave.Enabled = false;
         this.tsbSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.tsbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSave.Name = "tsbSave";
         this.tsbSave.Size = new System.Drawing.Size(23, 22);
         this.tsbSave.Text = "СОхранить изменения";
         this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click);
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.splitContainer2);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.tvPrice);
         this.splitContainer1.Size = new System.Drawing.Size(809, 616);
         this.splitContainer1.SplitterDistance = 445;
         this.splitContainer1.TabIndex = 1;
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
         this.splitContainer2.Panel1.Controls.Add(this.dgvOrgs);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.tvOrgMatrix);
         this.splitContainer2.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer2.Size = new System.Drawing.Size(445, 616);
         this.splitContainer2.SplitterDistance = 226;
         this.splitContainer2.TabIndex = 0;
         // 
         // dgvOrgs
         // 
         this.dgvOrgs.AllowUserToAddRows = false;
         this.dgvOrgs.AllowUserToDeleteRows = false;
         this.dgvOrgs.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrgs.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnOrg});
         this.dgvOrgs.ContextMenuStrip = this.contextMenuStrip1;
         this.dgvOrgs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrgs.Location = new System.Drawing.Point(0, 0);
         this.dgvOrgs.Name = "dgvOrgs";
         this.dgvOrgs.ReadOnly = true;
         this.dgvOrgs.RowHeadersVisible = false;
         this.dgvOrgs.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvOrgs.Size = new System.Drawing.Size(445, 226);
         this.dgvOrgs.TabIndex = 0;
         this.dgvOrgs.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvOrgs_RowEnter);
         this.dgvOrgs.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvOrgs_CellFormatting);
         // 
         // contextMenuStrip1
         // 
         this.contextMenuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsmCopyTo});
         this.contextMenuStrip1.Name = "contextMenuStrip1";
         this.contextMenuStrip1.Size = new System.Drawing.Size(158, 26);
         this.contextMenuStrip1.Opening += new System.ComponentModel.CancelEventHandler(this.contextMenuStrip1_Opening);
         // 
         // tsmCopyTo
         // 
         this.tsmCopyTo.Image = global::GRSoft.NapoleonManager.Properties.Resources.copy;
         this.tsmCopyTo.Name = "tsmCopyTo";
         this.tsmCopyTo.Size = new System.Drawing.Size(157, 22);
         this.tsmCopyTo.Text = "Копировать в...";
         this.tsmCopyTo.Click += new System.EventHandler(this.tsmCopyTo_Click);
         // 
         // tvOrgMatrix
         // 
         this.tvOrgMatrix.AllowDrop = true;
         this.tvOrgMatrix.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvOrgMatrix.ImageIndex = 0;
         this.tvOrgMatrix.ImageList = this.imageList1;
         this.tvOrgMatrix.Location = new System.Drawing.Point(0, 25);
         this.tvOrgMatrix.Name = "tvOrgMatrix";
         this.tvOrgMatrix.SelectedImageIndex = 0;
         this.tvOrgMatrix.SelectedNodes = ((System.Collections.Generic.List<System.Windows.Forms.TreeNode>)(resources.GetObject("tvOrgMatrix.SelectedNodes")));
         this.tvOrgMatrix.Size = new System.Drawing.Size(445, 361);
         this.tvOrgMatrix.TabIndex = 0;
         this.tvOrgMatrix.DragDrop += new System.Windows.Forms.DragEventHandler(this.tvOrgMatrix_DragDrop);
         this.tvOrgMatrix.DragEnter += new System.Windows.Forms.DragEventHandler(this.tvOrgMatrix_DragEnter);
         // 
         // imageList1
         // 
         this.imageList1.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("imageList1.ImageStream")));
         this.imageList1.TransparentColor = System.Drawing.Color.Transparent;
         this.imageList1.Images.SetKeyName(0, "Folder_Back.ico");
         this.imageList1.Images.SetKeyName(1, "Generic_Document.ico");
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbRemove});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(445, 25);
         this.toolStrip2.TabIndex = 1;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // tsbRemove
         // 
         this.tsbRemove.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbRemove.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_cancel;
         this.tsbRemove.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbRemove.Name = "tsbRemove";
         this.tsbRemove.Size = new System.Drawing.Size(23, 22);
         this.tsbRemove.Text = "Удалить";
         this.tsbRemove.Click += new System.EventHandler(this.tsbRemove_Click);
         // 
         // tvPrice
         // 
         this.tvPrice.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvPrice.ImageIndex = 0;
         this.tvPrice.ImageList = this.imageList1;
         this.tvPrice.Location = new System.Drawing.Point(0, 0);
         this.tvPrice.Name = "tvPrice";
         this.tvPrice.SelectedImageIndex = 0;
         this.tvPrice.SelectedNodes = ((System.Collections.Generic.List<System.Windows.Forms.TreeNode>)(resources.GetObject("tvPrice.SelectedNodes")));
         this.tvPrice.Size = new System.Drawing.Size(360, 616);
         this.tvPrice.TabIndex = 0;
         this.tvPrice.NodeMouseDoubleClick += new System.Windows.Forms.TreeNodeMouseClickEventHandler(this.tvPrice_NodeMouseDoubleClick);
         this.tvPrice.ItemDrag += new System.Windows.Forms.ItemDragEventHandler(this.tvPrice_ItemDrag);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Контрагент";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         // 
         // clmnOrg
         // 
         this.clmnOrg.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnOrg.DataPropertyName = "Name";
         this.clmnOrg.HeaderText = "Контрагент";
         this.clmnOrg.Name = "clmnOrg";
         this.clmnOrg.ReadOnly = true;
         // 
         // DistribMatrix
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(809, 641);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "DistribMatrix";
         this.Text = "Матрица дистрибуции";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.Panel2.PerformLayout();
         this.splitContainer2.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).EndInit();
         this.contextMenuStrip1.ResumeLayout(false);
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripComboBox cbAgents;
      private System.Windows.Forms.ToolStripButton tsbSave;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private GRSoft.UILib.TreeViewMS tvOrgMatrix;
      private System.Windows.Forms.DataGridView dgvOrgs;
      private GRSoft.UILib.TreeViewMS tvPrice;
      private System.Windows.Forms.ImageList imageList1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripButton tsbRemove;
      private System.Windows.Forms.ContextMenuStrip contextMenuStrip1;
      private System.Windows.Forms.ToolStripMenuItem tsmCopyTo;
      private System.Windows.Forms.ToolStripButton tsbCopy;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnOrg;
   }
}