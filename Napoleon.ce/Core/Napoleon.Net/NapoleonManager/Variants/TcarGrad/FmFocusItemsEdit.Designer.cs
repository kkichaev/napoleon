namespace GRSoft.NapoleonManager
{
   partial class FmFocusItemsEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmFocusItemsEdit));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.bntRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.tbOrgTypes = new System.Windows.Forms.ToolStripComboBox();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.tvPrice = new System.Windows.Forms.TreeView();
         this.imageList1 = new System.Windows.Forms.ImageList(this.components);
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.dgvGoodItems = new System.Windows.Forms.DataGridView();
         this.goodName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.btnDelGood = new System.Windows.Forms.ToolStripButton();
         this.dgvBadItems = new System.Windows.Forms.DataGridView();
         this.badName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip3 = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.btnDelBad = new System.Windows.Forms.ToolStripButton();
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvGoodItems)).BeginInit();
         this.toolStrip2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvBadItems)).BeginInit();
         this.toolStrip3.SuspendLayout();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.bntRefresh,
            this.btnSave,
            this.tbOrgTypes});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(600, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // bntRefresh
         // 
         this.bntRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.bntRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.bntRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.bntRefresh.Name = "bntRefresh";
         this.bntRefresh.Size = new System.Drawing.Size(23, 22);
         this.bntRefresh.Text = "Обновить";
         this.bntRefresh.Click += new System.EventHandler(this.bntRefresh_Click);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Enabled = false;
         this.btnSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // tbOrgTypes
         // 
         this.tbOrgTypes.Name = "tbOrgTypes";
         this.tbOrgTypes.Size = new System.Drawing.Size(250, 25);
         this.tbOrgTypes.SelectedIndexChanged += new System.EventHandler(this.tbOrgTypes_SelectedIndexChanged);
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.tvPrice);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.splitContainer2);
         this.splitContainer1.Size = new System.Drawing.Size(600, 388);
         this.splitContainer1.SplitterDistance = 227;
         this.splitContainer1.TabIndex = 1;
         // 
         // tvPrice
         // 
         this.tvPrice.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvPrice.ImageIndex = 0;
         this.tvPrice.ImageList = this.imageList1;
         this.tvPrice.Location = new System.Drawing.Point(0, 0);
         this.tvPrice.Name = "tvPrice";
         this.tvPrice.SelectedImageIndex = 0;
         this.tvPrice.Size = new System.Drawing.Size(227, 388);
         this.tvPrice.TabIndex = 0;
         this.tvPrice.ItemDrag += new System.Windows.Forms.ItemDragEventHandler(this.tvPrice_ItemDrag);
         // 
         // imageList1
         // 
         this.imageList1.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("imageList1.ImageStream")));
         this.imageList1.TransparentColor = System.Drawing.Color.Transparent;
         this.imageList1.Images.SetKeyName(0, "Folder_Back.ico");
         this.imageList1.Images.SetKeyName(1, "Generic_Document.ico");
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
         this.splitContainer2.Panel1.Controls.Add(this.dgvGoodItems);
         this.splitContainer2.Panel1.Controls.Add(this.toolStrip2);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.dgvBadItems);
         this.splitContainer2.Panel2.Controls.Add(this.toolStrip3);
         this.splitContainer2.Size = new System.Drawing.Size(369, 388);
         this.splitContainer2.SplitterDistance = 179;
         this.splitContainer2.TabIndex = 0;
         // 
         // dgvGoodItems
         // 
         this.dgvGoodItems.AllowDrop = true;
         this.dgvGoodItems.AllowUserToAddRows = false;
         this.dgvGoodItems.AllowUserToDeleteRows = false;
         this.dgvGoodItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvGoodItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.goodName});
         this.dgvGoodItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvGoodItems.Location = new System.Drawing.Point(0, 25);
         this.dgvGoodItems.Name = "dgvGoodItems";
         this.dgvGoodItems.RowHeadersVisible = false;
         this.dgvGoodItems.Size = new System.Drawing.Size(369, 154);
         this.dgvGoodItems.TabIndex = 1;
         this.dgvGoodItems.DragEnter += new System.Windows.Forms.DragEventHandler(this.DoDragEnter);
         this.dgvGoodItems.DragDrop += new System.Windows.Forms.DragEventHandler(this.DoDragDrop);
         // 
         // goodName
         // 
         this.goodName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.goodName.DataPropertyName = "Name";
         this.goodName.HeaderText = "Название";
         this.goodName.Name = "goodName";
         this.goodName.ReadOnly = true;
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel1,
            this.btnDelGood});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(369, 25);
         this.toolStrip2.TabIndex = 2;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(184, 22);
         this.toolStripLabel1.Text = "Рекомендованный ассортимент";
         // 
         // btnDelGood
         // 
         this.btnDelGood.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelGood.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_cancel;
         this.btnDelGood.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelGood.Name = "btnDelGood";
         this.btnDelGood.Size = new System.Drawing.Size(23, 22);
         this.btnDelGood.Text = "Удалить товар из списка";
         this.btnDelGood.Click += new System.EventHandler(this.btnDelGood_Click);
         // 
         // dgvBadItems
         // 
         this.dgvBadItems.AllowDrop = true;
         this.dgvBadItems.AllowUserToAddRows = false;
         this.dgvBadItems.AllowUserToDeleteRows = false;
         this.dgvBadItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvBadItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.badName});
         this.dgvBadItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvBadItems.Location = new System.Drawing.Point(0, 25);
         this.dgvBadItems.Name = "dgvBadItems";
         this.dgvBadItems.RowHeadersVisible = false;
         this.dgvBadItems.Size = new System.Drawing.Size(369, 180);
         this.dgvBadItems.TabIndex = 1;
         this.dgvBadItems.DragEnter += new System.Windows.Forms.DragEventHandler(this.DoDragEnter);
         this.dgvBadItems.DragDrop += new System.Windows.Forms.DragEventHandler(this.DoDragDrop);
         // 
         // badName
         // 
         this.badName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.badName.DataPropertyName = "Name";
         this.badName.HeaderText = "Название";
         this.badName.Name = "badName";
         this.badName.ReadOnly = true;
         // 
         // toolStrip3
         // 
         this.toolStrip3.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel2,
            this.btnDelBad});
         this.toolStrip3.Location = new System.Drawing.Point(0, 0);
         this.toolStrip3.Name = "toolStrip3";
         this.toolStrip3.Size = new System.Drawing.Size(369, 25);
         this.toolStrip3.TabIndex = 2;
         this.toolStrip3.Text = "toolStrip3";
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(199, 22);
         this.toolStripLabel2.Text = "Нерекомендованный ассортимент";
         // 
         // btnDelBad
         // 
         this.btnDelBad.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelBad.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_cancel;
         this.btnDelBad.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelBad.Name = "btnDelBad";
         this.btnDelBad.Size = new System.Drawing.Size(23, 22);
         this.btnDelBad.Text = "Удалить товар из списка";
         this.btnDelBad.Click += new System.EventHandler(this.btnDelBad_Click);
         // 
         // FmFocusItemsEdit
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(600, 413);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmFocusItemsEdit";
         this.Text = "Фокусный товар";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel1.PerformLayout();
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.Panel2.PerformLayout();
         this.splitContainer2.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvGoodItems)).EndInit();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvBadItems)).EndInit();
         this.toolStrip3.ResumeLayout(false);
         this.toolStrip3.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private System.Windows.Forms.TreeView tvPrice;
      private System.Windows.Forms.DataGridView dgvGoodItems;
      private System.Windows.Forms.DataGridView dgvBadItems;
      private System.Windows.Forms.ToolStripButton bntRefresh;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ToolStripComboBox tbOrgTypes;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripButton btnDelGood;
      private System.Windows.Forms.ToolStrip toolStrip3;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.ToolStripButton btnDelBad;
      private System.Windows.Forms.ImageList imageList1;
      private System.Windows.Forms.DataGridViewTextBoxColumn goodName;
      private System.Windows.Forms.DataGridViewTextBoxColumn badName;
   }
}